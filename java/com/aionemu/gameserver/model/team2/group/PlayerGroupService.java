/*
 * This file is part of the Aion-Emu project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.team2.group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.aionemu.gameserver.model.team2.common.events.ShowBrandEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamKinahDistributionEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.callback.AddPlayerToGroupCallback;
import com.aionemu.gameserver.model.team2.group.callback.PlayerGroupCreateCallback;
import com.aionemu.gameserver.model.team2.group.callback.PlayerGroupDisbandCallback;
import com.aionemu.gameserver.model.team2.group.events.ChangeGroupLeaderEvent;
import com.aionemu.gameserver.model.team2.group.events.ChangeGroupLootRulesEvent;
import com.aionemu.gameserver.model.team2.group.events.GroupDisbandEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerConnectedEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerDisconnectedEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerEnteredEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupInvite;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupLeavedEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupStopMentoringEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerGroupUpdateEvent;
import com.aionemu.gameserver.model.team2.group.events.PlayerStartMentoringEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.TimeUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import javolution.util.FastMap;

/**
 * @author ATracer
 */
public class PlayerGroupService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerGroupService.class);
	static final Map<Integer, PlayerGroup> groups = new ConcurrentHashMap<>();
	private static final AtomicBoolean offlineCheckStarted = new AtomicBoolean();
	private static FastMap<Integer, PlayerGroup> groupMembers;
	
	public static void inviteToGroup(Player inviter, Player invited)
	{
		if (canInvite(inviter, invited))
		{
			final PlayerGroupInvite invite = new PlayerGroupInvite(inviter, invited);
			if (invited.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_PARTY_DO_YOU_ACCEPT_INVITATION, invite))
			{
				PacketSendUtility.sendPacket(invited, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_PARTY_DO_YOU_ACCEPT_INVITATION, 0, 0, inviter.getName()));
			}
		}
	}
	
	public static boolean canInvite(Player inviter, Player invited)
	{
		if (inviter.isInInstance())
		{
			if (AutoGroupService.getInstance().isAutoInstance(inviter.getInstanceId()))
			{
				// You cannot use invite, leave or kick commands related to your group or alliance in this region.
				PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_OPERATE_PARTY_COMMAND);
				return false;
			}
		}
		if (invited.isInInstance())
		{
			if (AutoGroupService.getInstance().isAutoInstance(invited.getInstanceId()))
			{
				// You cannot use invite, leave or kick commands related to your group or alliance in this region.
				PacketSendUtility.sendPacket(inviter, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_OPERATE_PARTY_COMMAND);
				return false;
			}
		}
		final PlayerGroup group = inviter.getPlayerGroup2();
		if (group != null)
		{
			if (invited.isInTeam())
			{
				for (Player pm : invited.getCurrentTeam().getMembers())
				{
					if (pm.isInInstance())
					{
						// You cannot invite the player to the force as the group leader of the player is in an Instanced Zone.
						PacketSendUtility.sendPacket(inviter, new SM_SYSTEM_MESSAGE(1400128));
						return false;
					}
				}
			}
		}
		return RestrictionsManager.canInviteToGroup(inviter, invited);
	}
	
	@GlobalCallback(PlayerGroupCreateCallback.class)
	public static PlayerGroup createGroup(Player leader, Player invited, TeamType type)
	{
		final PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader), type);
		groups.put(newGroup.getTeamId(), newGroup);
		addPlayer(newGroup, leader);
		addPlayer(newGroup, invited);
		if (offlineCheckStarted.compareAndSet(false, true))
		{
			initializeOfflineCheck();
		}
		return newGroup;
	}
	
	@GlobalCallback(PlayerGroupCreateCallback.class)
	public static PlayerGroup createGroup(Player leader)
	{
		final PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader), TeamType.GROUP);
		groups.put(newGroup.getTeamId(), newGroup);
		addPlayer(newGroup, leader);
		if (offlineCheckStarted.compareAndSet(false, true))
		{
			initializeOfflineCheck();
		}
		return newGroup;
	}
	
	private static void initializeOfflineCheck()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new OfflinePlayerChecker(), 1000, 30 * 1000);
	}
	
	@GlobalCallback(AddPlayerToGroupCallback.class)
	public static void addPlayerToGroup(PlayerGroup group, Player invited)
	{
		group.addMember(new PlayerGroupMember(invited));
	}
	
	public static void changeGroupRules(PlayerGroup group, LootGroupRules lootRules)
	{
		group.onEvent(new ChangeGroupLootRulesEvent(group, lootRules));
	}
	
	public static void onPlayerLogin(Player player)
	{
		for (PlayerGroup group : groups.values())
		{
			final PlayerGroupMember member = group.getMember(player.getObjectId());
			if (member != null)
			{
				group.onEvent(new PlayerConnectedEvent(group, player));
			}
		}
	}
	
	public static void onPlayerLogout(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			final PlayerGroupMember member = group.getMember(player.getObjectId());
			member.updateLastOnlineTime();
			group.onEvent(new PlayerDisconnectedEvent(group, player));
		}
	}
	
	public static void updateGroup(Player player, GroupEvent groupEvent)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerGroupUpdateEvent(group, player, groupEvent));
		}
	}
	
	public static void addPlayer(PlayerGroup group, Player player)
	{
		Preconditions.checkNotNull(group, "Group should not be null");
		group.onEvent(new PlayerEnteredEvent(group, player));
	}
	
	public static void removePlayer(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerGroupLeavedEvent(group, player));
		}
	}
	
	public static void banPlayer(Player bannedPlayer, Player banGiver)
	{
		Preconditions.checkNotNull(bannedPlayer, "Banned player should not be null");
		Preconditions.checkNotNull(banGiver, "Bangiver player should not be null");
		final PlayerGroup group = banGiver.getPlayerGroup2();
		if (group != null)
		{
			if (group.hasMember(bannedPlayer.getObjectId()))
			{
				group.onEvent(new PlayerGroupLeavedEvent(group, bannedPlayer, LeaveReson.BAN, banGiver.getName()));
			}
			else
			{
				log.warn("TEAM2: banning player not in group {}", group.onlineMembers());
			}
		}
	}
	
	@GlobalCallback(PlayerGroupDisbandCallback.class)
	public static void disband(PlayerGroup group)
	{
		Preconditions.checkState(group.onlineMembers() <= 1, "Can't disband group with more than one online member");
		groups.remove(group.getTeamId());
		group.onEvent(new GroupDisbandEvent(group));
	}
	
	public static void distributeKinah(Player player, long kinah)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new TeamKinahDistributionEvent<>(group, player, kinah));
		}
	}
	
	public static void showBrand(Player player, int targetObjId, int brandId)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new ShowBrandEvent<>(group, targetObjId, brandId));
		}
	}
	
	public static void changeLeader(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new ChangeGroupLeaderEvent(group, player));
		}
	}
	
	public static void startMentoring(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerStartMentoringEvent(group, player));
		}
	}
	
	public static void stopMentoring(Player player)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group != null)
		{
			group.onEvent(new PlayerGroupStopMentoringEvent(group, player));
		}
	}
	
	public static void cleanup()
	{
		log.info(getServiceStatus());
		groups.clear();
	}
	
	public static String getServiceStatus()
	{
		return "Number of groups: " + groups.size();
	}
	
	public static PlayerGroup searchGroup(Integer playerObjId)
	{
		for (PlayerGroup group : groups.values())
		{
			if (group.hasMember(playerObjId))
			{
				return group;
			}
		}
		return null;
	}
	
	public static class OfflinePlayerChecker implements Runnable, Predicate<PlayerGroupMember>
	{
		private PlayerGroup currentGroup;
		
		@Override
		public void run()
		{
			for (PlayerGroup group : groups.values())
			{
				currentGroup = group;
				group.apply(this);
			}
			currentGroup = null;
		}
		
		@Override
		public boolean apply(PlayerGroupMember member)
		{
			if (!member.isOnline() && TimeUtil.isExpired(member.getLastOnlineTime() + (GroupConfig.GROUP_REMOVE_TIME * 1000)))
			{
				currentGroup.onEvent(new PlayerGroupLeavedEvent(currentGroup, member.getObject()));
			}
			return true;
		}
	}
	
	public static void addGroupMemberToCache(Player player)
	{
		if (!groupMembers.containsKey(player.getObjectId()))
		{
			groupMembers.put(player.getObjectId(), player.getPlayerGroup2());
		}
	}
}