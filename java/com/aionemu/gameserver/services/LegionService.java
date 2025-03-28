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
package com.aionemu.gameserver.services;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.DeniedStatus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.model.team.legion.LegionHistory;
import com.aionemu.gameserver.model.team.legion.LegionHistoryType;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequest;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.team.legion.LegionRank;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ICON_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_ADD_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_LEAVE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_MEMBERLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_REQUEST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_REQUEST_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_REQUEST_PLAYER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_SEND_EMBLEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_SEND_EMBLEM_DATA;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_TABS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_EMBLEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_NICKNAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_SELF_INTRO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_TITLE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.collections.ListSplitter;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.container.LegionContainer;
import com.aionemu.gameserver.world.container.LegionMemberContainer;

import javolution.util.FastList;

/**
 * This class is designed to do all the work related with loading/storing legions and their members.<br>
 * @author Simple modified by cura, Source
 */
public class LegionService
{
	static final Logger log = LoggerFactory.getLogger(LegionService.class);
	private final LegionContainer allCachedLegions = new LegionContainer();
	private final LegionMemberContainer allCachedLegionMembers = new LegionMemberContainer();
	private final World world;
	public static final int LEGION_ACTION_KICK = 4;
	/**
	 * Legion Permission variables
	 */
	private static final int MAX_LEGION_LEVEL = 8;
	/**
	 * Legion ranking system
	 */
	private Map<Integer, Integer> legionRanking;
	/**
	 * Legion Restrictions
	 */
	private final LegionRestrictions legionRestrictions = new LegionRestrictions();
	
	public static LegionService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	public LegionService()
	{
		world = World.getInstance();
	}
	
	/**
	 * Checks if a name is valid. It should contain only english letters
	 * @param name legion name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidName(String name)
	{
		return LegionConfig.LEGION_NAME_PATTERN.matcher(name).matches();
	}
	
	/**
	 * Stores legion data into db
	 * @param legion
	 * @param newLegion
	 */
	private void storeLegion(Legion legion, boolean newLegion)
	{
		if (newLegion)
		{
			addCachedLegion(legion);
			DAOManager.getDAO(LegionDAO.class).saveNewLegion(legion);
		}
		else
		{
			DAOManager.getDAO(LegionDAO.class).storeLegion(legion);
			DAOManager.getDAO(LegionDAO.class).storeLegionEmblem(legion.getLegionId(), legion.getLegionEmblem());
		}
	}
	
	/**
	 * Stores newly created legion
	 * @param legion legion to store @
	 */
	private void storeLegion(Legion legion)
	{
		storeLegion(legion, false);
	}
	
	/**
	 * Stores legion member data into db or saves a new one
	 * @param legionMember
	 * @param newMember
	 */
	private void storeLegionMember(LegionMember legionMember, boolean newMember)
	{
		if (newMember)
		{
			addCachedLegionMember(legionMember);
			DAOManager.getDAO(LegionMemberDAO.class).saveNewLegionMember(legionMember);
		}
		else
		{
			DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
		}
	}
	
	/**
	 * Stores a legion member
	 * @param legionMember legion member to store
	 */
	private void storeLegionMember(LegionMember legionMember)
	{
		storeLegionMember(legionMember, false);
	}
	
	/**
	 * Stores legion member data into database
	 * @param player
	 */
	private void storeLegionMemberExInCache(Player player)
	{
		if (allCachedLegionMembers.containsEx(player.getObjectId()))
		{
			final LegionMemberEx legionMemberEx = allCachedLegionMembers.getMemberEx(player.getObjectId());
			legionMemberEx.setNickname(player.getLegionMember().getNickname());
			legionMemberEx.setSelfIntro(player.getLegionMember().getSelfIntro());
			legionMemberEx.setPlayerClass(player.getPlayerClass());
			legionMemberEx.setExp(player.getCommonData().getExp());
			legionMemberEx.setLastOnline(player.getCommonData().getLastOnline());
			legionMemberEx.setWorldId(player.getPosition().getMapId());
			legionMemberEx.setOnline(false);
		}
		else
		{
			final LegionMemberEx legionMemberEx = new LegionMemberEx(player, player.getLegionMember(), false);
			addCachedLegionMemberEx(legionMemberEx);
		}
	}
	
	/**
	 * Gets a legion ONLY if he is in the cache
	 * @param legionId
	 * @return Legion or null if not cached
	 */
	private Legion getCachedLegion(int legionId)
	{
		return allCachedLegions.get(legionId);
	}
	
	/**
	 * Gets a legion ONLY if he is in the cache
	 * @param legionName
	 * @return Legion or null if not cached
	 */
	private Legion getCachedLegion(String legionName)
	{
		return allCachedLegions.get(legionName);
	}
	
	/**
	 * Iterator for loaded legions
	 * @return
	 */
	public Iterator<Legion> getCachedLegionIterator()
	{
		return allCachedLegions.iterator();
	}
	
	/**
	 * This method will add a new legion to the cache
	 * @param legion
	 */
	private void addCachedLegion(Legion legion)
	{
		allCachedLegions.add(legion);
	}
	
	/**
	 * This method will add a new legion member to the cache
	 * @param legionMember
	 */
	private void addCachedLegionMember(LegionMember legionMember)
	{
		allCachedLegionMembers.addMember(legionMember);
	}
	
	/**
	 * This method will add a new legion member to the cache
	 * @param legionMemberEx
	 */
	private void addCachedLegionMemberEx(LegionMemberEx legionMemberEx)
	{
		allCachedLegionMembers.addMemberEx(legionMemberEx);
	}
	
	/**
	 * Completely removes legion from database and cache
	 * @param legion
	 */
	private void deleteLegionFromDB(Legion legion)
	{
		allCachedLegions.remove(legion);
		DAOManager.getDAO(LegionDAO.class).deleteLegion(legion.getLegionId());
	}
	
	/**
	 * This method will remove the legion member from cache and the database
	 * @param legionMember
	 */
	private void deleteLegionMemberFromDB(LegionMemberEx legionMember)
	{
		allCachedLegionMembers.remove(legionMember);
		DAOManager.getDAO(LegionMemberDAO.class).deleteLegionMember(legionMember.getObjectId());
		final Legion legion = legionMember.getLegion();
		legion.deleteLegionMember(legionMember.getObjectId());
		addHistory(legion, legionMember.getName(), LegionHistoryType.KICK);
	}
	
	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * @param legionName Legion Name
	 * @return Legion or null if doesn't exists
	 */
	public Legion getLegion(String legionName)
	{
		/**
		 * First check if our legion already exists in our Cache
		 */
		if (allCachedLegions.contains(legionName))
		{
			final Legion legion = getCachedLegion(legionName);
			return legion;
		}
		
		/**
		 * Else load the legion information from the database
		 */
		final Legion legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionName);
		
		/**
		 * This will handle the rest of the information that needs to be loaded
		 */
		loadLegionInfo(legion);
		
		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);
		
		/**
		 * Return the legion
		 */
		return legion;
	}
	
	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * @param legionId
	 * @return Legion
	 */
	public Legion getLegion(int legionId)
	{
		/**
		 * First check if our legion already exists in our Cache
		 */
		if (allCachedLegions.contains(legionId))
		{
			final Legion legion = getCachedLegion(legionId);
			return legion;
		}
		
		/**
		 * Else load the legion information from the database
		 */
		final Legion legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionId);
		
		/**
		 * This will handle the rest of the information that needs to be loaded
		 */
		loadLegionInfo(legion);
		
		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);
		
		/**
		 * Return the legion
		 */
		return legion;
	}
	
	/**
	 * This method will load the legion information
	 * @param legion
	 */
	private void loadLegionInfo(Legion legion)
	{
		/**
		 * Check if legion is not null
		 */
		if (legion == null)
		{
			return;
		}
		
		/**
		 * Load and add the legion members to legion
		 */
		legion.setLegionMembers(DAOManager.getDAO(LegionMemberDAO.class).loadLegionMembers(legion.getLegionId()));
		
		/**
		 * Load and set the announcement list
		 */
		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion.getLegionId()));
		
		/**
		 * Set legion emblem
		 */
		legion.setLegionEmblem(DAOManager.getDAO(LegionDAO.class).loadLegionEmblem(legion.getLegionId()));
		
		/**
		 * Load Legion Warehouse
		 */
		legion.setLegionWarehouse(DAOManager.getDAO(LegionDAO.class).loadLegionStorage(legion));
		
		if (legionRanking.containsKey(legion.getLegionId()))
		{
			legion.setLegionRank(legionRanking.get(legion.getLegionId()));
		}
		
		/**
		 * Load Legion History
		 */
		DAOManager.getDAO(LegionDAO.class).loadLegionHistory(legion);
	}
	
	/**
	 * Returns the legion Brigade general with given legionId (if such legion exists)
	 * @param legionId
	 * @return LegionMember (Brigade General)
	 */
	public int getLegionBGeneral(int legionId)
	{
		final Legion legion = LegionService.getInstance().getLegion(legionId);
		int legionBG = 0;
		
		for (int memberObjId : legion.getLegionMembers())
		{
			final LegionMember legionMember = LegionService.getInstance().getLegionMember(memberObjId);
			if (legionMember.getRank() == LegionRank.BRIGADE_GENERAL)
			{
				legionBG = memberObjId;
			}
		}
		
		return legionBG;
	}
	
	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * @param playerObjId
	 * @return LegionMember
	 */
	public LegionMember getLegionMember(int playerObjId)
	{
		LegionMember legionMember = null;
		if (allCachedLegionMembers.contains(playerObjId))
		{
			legionMember = allCachedLegionMembers.getMember(playerObjId);
		}
		else
		{
			legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(playerObjId);
			if (legionMember != null)
			{
				addCachedLegionMember(legionMember);
			}
		}
		
		if (legionMember != null)
		{
			if (checkDisband(legionMember.getLegion()))
			{
				return null;
			}
		}
		
		return legionMember;
	}
	
	/**
	 * Method that checks if a legion is disbanding
	 * @param legion
	 * @return true if it's time to be deleted
	 */
	private boolean checkDisband(Legion legion)
	{
		if (legion.isDisbanding())
		{
			if ((System.currentTimeMillis() / 1000) > legion.getDisbandTime())
			{
				disbandLegion(legion);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method will disband a legion and update all members
	 * @param legion
	 */
	public void disbandLegion(Legion legion)
	{
		for (Integer memberObjId : legion.getLegionMembers())
		{
			allCachedLegionMembers.remove(getLegionMemberEx(memberObjId));
		}
		SiegeService.getInstance().cleanLegionId(legion.getLegionId());
		updateAfterDisbandLegion(legion);
		deleteLegionFromDB(legion);
	}
	
	/**
	 * Returns the offline legion member with given playerId (if such member exists)
	 * @param playerObjId
	 * @return LegionMemberEx
	 */
	private LegionMemberEx getLegionMemberEx(int playerObjId)
	{
		if (allCachedLegionMembers.containsEx(playerObjId))
		{
			return allCachedLegionMembers.getMemberEx(playerObjId);
		}
		final LegionMemberEx legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerObjId);
		addCachedLegionMemberEx(legionMember);
		return legionMember;
	}
	
	/**
	 * Returns the offline legion member with given playerId (if such member exists)
	 * @param playerName
	 * @return LegionMemberEx
	 */
	LegionMemberEx getLegionMemberEx(String playerName)
	{
		if (allCachedLegionMembers.containsEx(playerName))
		{
			return allCachedLegionMembers.getMemberEx(playerName);
		}
		final LegionMemberEx legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerName);
		addCachedLegionMemberEx(legionMember);
		return legionMember;
	}
	
	/**
	 * This method will handle when disband request is called
	 * @param npc
	 * @param activePlayer
	 */
	public void requestDisbandLegion(Creature npc, Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if (legionRestrictions.canDisbandLegion(activePlayer, legion))
		{
			final RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					final int unixTime = (int) ((System.currentTimeMillis() / 1000) + LegionConfig.LEGION_DISBAND_TIME);
					legion.setDisbandTime(unixTime);
					updateMembersOfDisbandLegion(legion, unixTime);
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// no message
				}
			};
			
			final boolean disbandResult = activePlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE, disbandResponseHandler);
			if (disbandResult)
			{
				PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE, 0, 0));
			}
		}
	}
	
	/**
	 * This method will handle the creation of a legion
	 * @param activePlayer
	 * @param legionName
	 */
	public void createLegion(Player activePlayer, String legionName)
	{
		if (legionRestrictions.canCreateLegion(activePlayer, legionName))
		{
			/**
			 * Create new legion and put originator as first member
			 */
			final Legion legion = new Legion(IDFactory.getInstance().nextId(), legionName);
			legion.addLegionMember(activePlayer.getObjectId());
			
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_CREATE_REQUIRED_KINAH);
			
			/**
			 * Create a LegionMember, add it to the legion and bind it to a Player
			 */
			storeLegion(legion, true);
			final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			storeNewAnnouncement(legion.getLegionId(), currentTime, "");
			legion.addAnnouncementToList(currentTime, "");
			addLegionMember(legion, activePlayer, LegionRank.BRIGADE_GENERAL);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x05, (int) (System.currentTimeMillis() / 1000), ""));
			/**
			 * Add create and joined legion history and save it
			 */
			addHistory(legion, "", LegionHistoryType.CREATE);
			addHistory(legion, activePlayer.getName(), LegionHistoryType.JOIN);
			
			/**
			 * Send required packets
			 */
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATED(legion.getLegionName()));
		}
	}
	
	public boolean directAddPlayer(int legionId, Player player)
	{
		final Legion legion = getLegion(legionId);
		if (legion == null)
		{
			return false;
		}
		return directAddPlayer(legion, player);
	}
	
	public boolean directAddPlayer(Legion legion, Player player)
	{
		final int playerObjId = player.getObjectId();
		if (legion.addLegionMember(playerObjId))
		{
			// Bind LegionMember to Player
			addLegionMember(legion, player);
			
			// Display current announcement
			displayLegionMessage(player, legion.getCurrentAnnouncement());
			
			// Add to history of legion
			addHistory(legion, player.getName(), LegionHistoryType.JOIN);
			return true;
		}
		player.resetLegionMember();
		return false;
	}
	
	/**
	 * Method that will handle a invitation to a legion
	 * @param activePlayer
	 * @param targetPlayer
	 */
	private void invitePlayerToLegion(Player activePlayer, Player targetPlayer)
	{
		if (legionRestrictions.canInvitePlayer(activePlayer, targetPlayer))
		{
			final Legion legion = activePlayer.getLegion();
			final RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (!targetPlayer.getCommonData().isOnline())
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(targetPlayer.getName()));
					}
					else
					{
						final int playerObjId = targetPlayer.getObjectId();
						if (legion.addLegionMember(playerObjId))
						{
							addLegionMember(legion, targetPlayer);
							displayLegionMessage(targetPlayer, legion.getCurrentAnnouncement());
							addHistory(legion, targetPlayer.getName(), LegionHistoryType.JOIN);
						}
						else
						{
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_CAN_NOT_ADD_MEMBER_ANY_MORE);
							targetPlayer.resetLegionMember();
						}
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_HE_REJECTED_INVITATION(targetPlayer.getName()));
				}
			};
			final boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_INVITE_I_JOINED_MSGBOX, responseHandler);
			if (!requested)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_OTHER_IS_BUSY);
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_SENT_INVITE_MSG_TO_HIM(targetPlayer.getName()));
				PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_INVITE_I_JOINED_MSGBOX, 0, 0, legion.getLegionName(), legion.getLegionLevel() + "", activePlayer.getName()));
			}
		}
	}
	
	/**
	 * Displays current legion announcement
	 * @param targetPlayer
	 * @param currentAnnouncement
	 */
	void displayLegionMessage(Player targetPlayer, Entry<Timestamp, String> currentAnnouncement)
	{
		if (currentAnnouncement != null)
		{
			PacketSendUtility.sendPacket(targetPlayer, SM_SYSTEM_MESSAGE.STR_GUILD_NOTICE(currentAnnouncement.getValue(), (int) (currentAnnouncement.getKey().getTime() / 1000)));
		}
	}
	
	/**
	 * This method will handle a new appointed legion leader
	 * @param activePlayer
	 * @param targetPlayer
	 */
	private void appointBrigadeGeneral(Player activePlayer, Player targetPlayer)
	{
		if (legionRestrictions.canAppointBrigadeGeneral(activePlayer, targetPlayer))
		{
			final Legion legion = activePlayer.getLegion();
			final RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (!targetPlayer.getCommonData().isOnline())
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_NO_SUCH_USER);
					}
					else
					{
						final LegionMember legionMember = targetPlayer.getLegionMember();
						if (legionMember.getRank().getRankId() > LegionRank.BRIGADE_GENERAL.getRankId())
						{
							// Demote Brigade General to Centurion
							activePlayer.getLegionMember().setRank(LegionRank.CENTURION);
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(activePlayer, 0, ""));
							
							// Promote member to Brigade General
							legionMember.setRank(LegionRank.BRIGADE_GENERAL);
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(targetPlayer, 1300273, targetPlayer.getName()));
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x08));
							addHistory(legion, targetPlayer.getName(), LegionHistoryType.APPOINTED);
						}
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_HE_DECLINE_YOUR_OFFER(targetPlayer.getName()));
				}
			};
			
			final boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_CHANGE_MASTER_DO_YOU_ACCEPT_OFFER, responseHandler);
			// If the player is busy and could not be asked
			if (!requested)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_SENT_CANT_OFFER_WHEN_HE_IS_QUESTION_ASKED);
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_SENT_OFFER_MSG_TO_HIM(targetPlayer.getName()));
				
				// Send question packet to buddy
				// TODO: Add char name parameter? Doesn't work?
				PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_CHANGE_MASTER_DO_YOU_ACCEPT_OFFER, activePlayer.getObjectId(), 0, activePlayer.getName()));
			}
		}
	}
	
	/**
	 * This method will handle the process when a member is demoted or promoted while offline.
	 * @param activePlayer
	 * @param charName
	 * @param rankId
	 */
	private void appointRank(Player activePlayer, String charName, int rankId)
	{
		final LegionMemberEx LM = getLegionMemberEx(charName);
		if (LM == null)
		{
			log.error("Char name does not exist in legion member table: " + charName);
			return;
		}
		if (legionRestrictions.canAppointRank(activePlayer, LM.getObjectId()))
		{
			final Legion legion = activePlayer.getLegion();
			final LegionRank rank = LegionRank.values()[rankId];
			int msgId = 0;
			switch (rank)
			{
				case DEPUTY:
				{
					msgId = 1400902;
					break;
				}
				case LEGIONARY:
				{
					msgId = 1300268;
					break;
				}
				case CENTURION:
				{
					msgId = 1300267;
					break;
				}
				case VOLUNTEER:
				{
					msgId = 1400903;
				}
			}
			final LegionMember legionMember = getLegionMember(LM.getObjectId());
			legionMember.setRank(rank);
			DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
			LM.setRank(rank);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(LM, msgId, LM.getName()));
		}
	}
	
	/**
	 * This method will handle the process when a member is demoted or promoted.
	 * @param activePlayer
	 * @param targetPlayer
	 * @param rankId
	 */
	private void appointRank(Player activePlayer, Player targetPlayer, int rankId)
	{
		if (legionRestrictions.canAppointRank(activePlayer, targetPlayer.getObjectId()))
		{
			final Legion legion = activePlayer.getLegion();
			int msgId = 0;
			final LegionRank rank = LegionRank.values()[rankId];
			final LegionMember legionMember = targetPlayer.getLegionMember();
			switch (rank)
			{
				case DEPUTY:
				{
					msgId = 1400902;
					break;
				}
				case LEGIONARY:
				{
					msgId = 1300268;
					break;
				}
				case CENTURION:
				{
					msgId = 1300267;
					break;
				}
				case VOLUNTEER:
				{
					msgId = 1400903;
				}
			}
			legionMember.setRank(rank);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(targetPlayer, msgId, targetPlayer.getName()));
		}
	}
	
	/**
	 * This method will handle the changement of a self intro
	 * @param activePlayer
	 * @param newSelfIntro
	 */
	private void changeSelfIntro(Player activePlayer, String newSelfIntro)
	{
		if (legionRestrictions.canChangeSelfIntro(activePlayer, newSelfIntro))
		{
			final LegionMember legionMember = activePlayer.getLegionMember();
			legionMember.setSelfIntro(newSelfIntro);
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_UPDATE_SELF_INTRO(activePlayer.getObjectId(), newSelfIntro));
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WRITE_INTRO_DONE);
		}
	}
	
	/**
	 * This method will handle the changement of permissions
	 * @param legion
	 * @param deputyPermission
	 * @param centurionPermission
	 * @param legionarPermission
	 * @param volunteerPermission
	 */
	public void changePermissions(Legion legion, short deputyPermission, short centurionPermission, short legionarPermission, short volunteerPermission)
	{
		if (legion.setLegionPermissions(deputyPermission, centurionPermission, legionarPermission, volunteerPermission))
		{
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x02, legion));
		}
	}
	
	/**
	 * This method will handle the leveling up of a legion
	 * @param activePlayer
	 */
	private void requestChangeLevel(Player activePlayer)
	{
		if (legionRestrictions.canChangeLevel(activePlayer))
		{
			final Legion legion = activePlayer.getLegion();
			activePlayer.getInventory().decreaseKinah(legion.getKinahPrice());
			changeLevel(legion, legion.getLegionLevel() + 1, false);
			addHistory(legion, legion.getLegionLevel() + "", LegionHistoryType.LEVEL_UP);
		}
	}
	
	/**
	 * This method will change the legion level and send update to online members
	 * @param legion
	 * @param newLevel
	 * @param save
	 */
	public void changeLevel(Legion legion, int newLevel, boolean save)
	{
		legion.setLegionLevel(newLevel);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x00, legion));
		PacketSendUtility.broadcastPacketToLegion(legion, SM_SYSTEM_MESSAGE.STR_GUILD_EVENT_LEVELUP(newLevel));
		if (save)
		{
			storeLegion(legion);
		}
	}
	
	/**
	 * This method will handle the changement of a nickname
	 * @param activePlayer
	 * @param charName
	 * @param newNickname
	 */
	private void changeNickname(Player activePlayer, String charName, String newNickname)
	{
		final Legion legion = activePlayer.getLegion();
		LegionMember legionMember;
		Player targetPlayer = World.getInstance().findPlayer(charName);
		if (targetPlayer != null)
		{
			legionMember = targetPlayer.getLegionMember();
			if (targetPlayer.getLegion() != legion)
			{
				return;
			}
		}
		else
		{
			final LegionMemberEx LM = getLegionMemberEx(charName);
			if ((LM == null) || (LM.getLegion() != legion))
			{
				return;
			}
			legionMember = getLegionMember(LM.getObjectId());
		}
		if (legionRestrictions.canChangeNickname(legion, legionMember.getObjectId(), newNickname))
		{
			legionMember.setNickname(newNickname);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_NICKNAME(legionMember.getObjectId(), newNickname));
			if (targetPlayer == null)
			{
				DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
			}
		}
	}
	
	/**
	 * This method will remove legion from all legion members online after a legion has been disbanded
	 * @param legion
	 */
	private void updateAfterDisbandLegion(Legion legion)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_TITLE(onlineLegionMember.getObjectId(), 0, "", 0), true);
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_LEAVE_MEMBER(1300302, 0, legion.getLegionName()));
			onlineLegionMember.resetLegionMember();
		}
	}
	
	/**
	 * This method will send a packet to every legion member
	 * @param legion
	 * @param emblemType
	 */
	private void updateMembersEmblem(Legion legion, LegionEmblemType emblemType)
	{
		final LegionEmblem legionEmblem = legion.getLegionEmblem();
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(), legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b(), emblemType), true);
			if (legionEmblem.getEmblemType() == LegionEmblemType.CUSTOM)
			{
				sendEmblemData(onlineLegionMember, legionEmblem, legion.getLegionId(), legion.getLegionName());
			}
		}
	}
	
	/**
	 * This method will send a packet to every legion member and update them about the disband
	 * @param legion
	 * @param unixTime
	 */
	void updateMembersOfDisbandLegion(Legion legion, int unixTime)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_UPDATE_MEMBER(onlineLegionMember, 1300303, unixTime + ""));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x06, unixTime));
		}
	}
	
	/**
	 * This method will send a packet to every legion member and update them about the disband
	 * @param legion
	 */
	void updateMembersOfRecreateLegion(Legion legion)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_UPDATE_MEMBER(onlineLegionMember, 1300307, ""));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x07));
		}
	}
	
	/**
	 * @param activePlayer
	 * @param customEmblem
	 */
	public void storeLegionEmblem(Player activePlayer, LegionEmblem customEmblem)
	{
		addHistory(activePlayer.getLegion(), "", LegionHistoryType.EMBLEM_MODIFIED);
		activePlayer.getLegion().setLegionEmblem(customEmblem);
		updateMembersEmblem(activePlayer.getLegion(), customEmblem.getEmblemType());
		PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_EMBLEM);
	}
	
	/**
	 * Stores the new legion emblem
	 * @param activePlayer
	 * @param legionId
	 * @param emblemId
	 * @param color_r
	 * @param color_g
	 * @param color_b
	 * @param emblemType
	 */
	public void storeLegionEmblem(Player activePlayer, int legionId, int emblemId, int color_r, int color_g, int color_b, LegionEmblemType emblemType)
	{
		if (legionRestrictions.canStoreLegionEmblem(activePlayer, legionId, emblemId))
		{
			final Legion legion = activePlayer.getLegion();
			if (legion.getLegionEmblem().isDefaultEmblem())
			{
				addHistory(legion, "", LegionHistoryType.EMBLEM_REGISTER);
			}
			else
			{
				addHistory(legion, "", LegionHistoryType.EMBLEM_MODIFIED);
			}
			
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH);
			legion.getLegionEmblem().setEmblem(emblemId, color_r, color_g, color_b, emblemType, null);
			updateMembersEmblem(legion, emblemType);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_EMBLEM);
		}
	}
	
	/**
	 * @param legion
	 * @param objExcluded
	 * @return
	 */
	public ArrayList<LegionMemberEx> loadLegionMemberExList(Legion legion, Integer objExcluded)
	{
		final ArrayList<LegionMemberEx> legionMembers = new ArrayList<>();
		for (Integer memberObjId : legion.getLegionMembers())
		{
			LegionMemberEx legionMemberEx;
			if ((objExcluded != null) && objExcluded.equals(memberObjId))
			{
				continue;
			}
			final Player memberPlayer = world.findPlayer(memberObjId);
			if (memberPlayer != null)
			{
				legionMemberEx = new LegionMemberEx(memberPlayer, memberPlayer.getLegionMember(), true);
			}
			else
			{
				legionMemberEx = getLegionMemberEx(memberObjId);
			}
			legionMembers.add(legionMemberEx);
		}
		return legionMembers;
	}
	
	public String getBrigadeGeneralName(Legion legion)
	{
		for (LegionMemberEx member : loadLegionMemberExList(legion, null))
		{
			if (member.isBrigadeGeneral())
			{
				return member.getName();
			}
		}
		return "ERROR Name !!!";
	}
	
	public Player getBrigadeGeneral(Legion legion)
	{
		Player player = null;
		for (LegionMemberEx member : loadLegionMemberExList(legion, null))
		{
			if (member.isBrigadeGeneral())
			{
				player = World.getInstance().findPlayer(member.getObjectId());
			}
		}
		return player;
	}
	
	/**
	 * @param player
	 * @param npc
	 */
	public void openLegionWarehouse(Player player, Npc npc)
	{
		if (legionRestrictions.canOpenWarehouse(player))
		{
			LegionWhUpdate(player);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion())); // kinah
			final int whLvl = player.getLegion().getWarehouseLevel();
			final List<Item> items = player.getLegion().getLegionWarehouse().getItems();
			final int storageId = StorageType.LEGION_WAREHOUSE.getId();
			final boolean isEmpty = items.isEmpty();
			if (!isEmpty)
			{
				final ListSplitter<Item> splitter = new ListSplitter<>(items, 10);
				while (!splitter.isLast())
				{
					PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(splitter.getNext(), storageId, whLvl, splitter.isFirst(), player));
				}
			}
			PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, storageId, whLvl, isEmpty, player));
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 25));
		}
	}
	
	/**
	 * @param npc
	 * @param activePlayer
	 */
	public void recreateLegion(Npc npc, Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if (legionRestrictions.canRecreateLegion(activePlayer, legion))
		{
			final RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					legion.setDisbandTime(0);
					PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x07));
					updateMembersOfRecreateLegion(legion);
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// no message
				}
			};
			
			final boolean disbandResult = activePlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE_CANCEL, disbandResponseHandler);
			if (disbandResult)
			{
				PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE_CANCEL, 0, 0));
			}
		}
	}
	
	/**
	 * This method will set the legion ranking if needed
	 * @param legionRanking
	 */
	public void performRankingUpdate(Map<Integer, Integer> legionRanking)
	{
		log.info("Legion ranking update task started");
		final long startTime = System.currentTimeMillis();
		
		final Iterator<Legion> legionsIterator = allCachedLegions.iterator();
		int legionsUpdated = 0;
		
		this.legionRanking = legionRanking;
		
		while (legionsIterator.hasNext())
		{
			final Legion legion = legionsIterator.next();
			if (legionRanking.containsKey(legion.getLegionId()))
			{
				legion.setLegionRank(legionRanking.get(legion.getLegionId()));
				PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x01, legion));
			}
			legionsUpdated++;
		}
		
		final long workTime = System.currentTimeMillis() - startTime;
		log.info("Legion ranking update: " + workTime + " ms, legions: " + legionsUpdated);
	}
	
	public void LegionWhUpdate(Player player)
	{
		final Legion legion = player.getLegion();
		
		if (legion == null)
		{
			return;
		}
		
		final FastList<Item> allItems = legion.getLegionWarehouse().getItemsWithKinah();
		allItems.addAll(legion.getLegionWarehouse().getDeletedItems());
		try
		{
			/**
			 * 1. save items first
			 */
			DAOManager.getDAO(InventoryDAO.class).store(allItems, player.getObjectId(), player.getPlayerAccount().getId(), legion.getLegionId());
			
			/**
			 * 2. save item stones
			 */
			DAOManager.getDAO(ItemStoneListDAO.class).save(allItems);
		}
		catch (Exception ex)
		{
			log.error("Exception during periodic saving of legion WH", ex);
		}
	}
	
	/**
	 * This method will update all players about the level/class change
	 * @param player
	 */
	public void updateMemberInfo(Player player)
	{
		PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
	}
	
	/**
	 * This method will set the contribution points, specially for legion command
	 * @param legion
	 * @param newPoints
	 * @param save
	 */
	public void setContributionPoints(Legion legion, long newPoints, boolean save)
	{
		legion.setContributionPoints(newPoints);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x03, legion));
		if (save)
		{
			storeLegion(legion);
		}
	}
	
	/**
	 * @param activePlayer
	 * @param totalSize
	 * @param color_r
	 * @param color_g
	 * @param color_b
	 * @param emblemType
	 */
	public void uploadEmblemInfo(Player activePlayer, int totalSize, int color_r, int color_g, int color_b, LegionEmblemType emblemType)
	{
		if (legionRestrictions.canUploadEmblemInfo(activePlayer))
		{
			final LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			legionEmblem.resetUploadSettings();
			
			final int emblemId = legionEmblem.getEmblemId() + 1;
			legionEmblem.setEmblem(emblemId, color_r, color_g, color_b, emblemType, null);
			legionEmblem.setUploadSize(totalSize);
			legionEmblem.setUploading(true);
		}
	}
	
	/**
	 * @param activePlayer
	 * @param size
	 * @param data
	 */
	public void uploadEmblemData(Player activePlayer, int size, byte[] data)
	{
		if (legionRestrictions.canUploadEmblem(activePlayer))
		{
			final LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			legionEmblem.addUploadedSize(size);
			legionEmblem.addUploadData(data);
			
			if (legionEmblem.getUploadSize() == legionEmblem.getUploadedSize())
			{
				if ((legionEmblem.getUploadedSize() == 0) || (legionEmblem.getUploadSize() == 0))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WARN_CORRUPT_EMBLEM_FILE);
					return;
				}
				if (!activePlayer.getInventory().tryDecreaseKinah(1130000))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
					return;
				}
				// Finished
				legionEmblem.setCustomEmblemData(legionEmblem.getUploadData());
				DAOManager.getDAO(LegionDAO.class).storeLegionEmblem(activePlayer.getLegion().getLegionId(), legionEmblem);
				final LegionEmblem emblem = DAOManager.getDAO(LegionDAO.class).loadLegionEmblem(activePlayer.getLegion().getLegionId());
				LegionService.getInstance().storeLegionEmblem(activePlayer, emblem);
			}
		}
	}
	
	/**
	 * @param player
	 * @param legionEmblem
	 * @param legionId
	 * @param legionName
	 */
	public void sendEmblemData(Player player, LegionEmblem legionEmblem, int legionId, String legionName)
	{
		PacketSendUtility.sendPacket(player, new SM_LEGION_SEND_EMBLEM(legionId, legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b(), legionName, legionEmblem.getEmblemType(), legionEmblem.getCustomEmblemData().length));
		final ByteBuffer buf = ByteBuffer.allocate(legionEmblem.getCustomEmblemData().length);
		buf.put(legionEmblem.getCustomEmblemData()).position(0);
		log.debug("legionEmblem size: " + buf.capacity() + " bytes");
		final int maxSize = 7993;
		int currentSize;
		byte[] bytes;
		do
		{
			log.debug("legionEmblem data position: " + buf.position());
			currentSize = buf.capacity() - buf.position();
			log.debug("legionEmblem data remaining capacity: " + currentSize + " bytes");
			
			if (currentSize >= maxSize)
			{
				bytes = new byte[maxSize];
				for (int i = 0; i < maxSize; i++)
				{
					bytes[i] = buf.get();
				}
				log.debug("legionEmblem data send size: " + (bytes.length) + " bytes");
				PacketSendUtility.sendPacket(player, new SM_LEGION_SEND_EMBLEM_DATA(maxSize, bytes));
			}
			else
			{
				bytes = new byte[currentSize];
				for (int i = 0; i < currentSize; i++)
				{
					bytes[i] = buf.get();
				}
				log.debug("legionEmblem data send size: " + (bytes.length) + " bytes");
				PacketSendUtility.sendPacket(player, new SM_LEGION_SEND_EMBLEM_DATA(currentSize, bytes));
			}
		}
		while (buf.capacity() != buf.position());
	}
	
	/**
	 * @param legion
	 * @param newLegionName
	 * @param save
	 */
	public void setLegionName(Legion legion, String newLegionName, boolean save)
	{
		legion.setLegionName(newLegionName);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_INFO(legion));
		
		for (Player legionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.broadcastPacket(legionMember, new SM_LEGION_UPDATE_TITLE(legionMember.getObjectId(), legion.getLegionId(), legion.getLegionName(), legionMember.getLegionMember().getRank().getRankId()), true);
		}
		if (save)
		{
			storeLegion(legion);
		}
	}
	
	/**
	 * This will add a new announcement to the DB and change the current announcement
	 * @param activePlayer
	 * @param announcement
	 */
	private void changeAnnouncement(Player activePlayer, String announcement)
	{
		if (legionRestrictions.canChangeAnnouncement(activePlayer.getLegionMember(), announcement))
		{
			final Legion legion = activePlayer.getLegion();
			
			final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			storeNewAnnouncement(legion.getLegionId(), currentTime, announcement);
			legion.addAnnouncementToList(currentTime, announcement);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WRITE_NOTICE_DONE);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x05, (int) (System.currentTimeMillis() / 1000), announcement));
		}
	}
	
	/**
	 * This method stores all legion announcements
	 * @param legion
	 */
	private void storeLegionAnnouncements(Legion legion)
	{
		for (int i = 0; i < (legion.getAnnouncementList().size() - 7); i++)
		{
			removeAnnouncement(legion.getLegionId(), legion.getAnnouncementList().firstEntry().getKey());
			legion.removeFirstEntry();
		}
	}
	
	/**
	 * Stores newly created announcement
	 * @param legionId
	 * @param currentTime
	 * @param message
	 * @return true if announcement was successful saved.
	 */
	private boolean storeNewAnnouncement(int legionId, Timestamp currentTime, String message)
	{
		return DAOManager.getDAO(LegionDAO.class).saveNewAnnouncement(legionId, currentTime, message);
	}
	
	/**
	 * @param legionId
	 * @param key
	 */
	private void removeAnnouncement(int legionId, Timestamp key)
	{
		DAOManager.getDAO(LegionDAO.class).removeAnnouncement(legionId, key);
	}
	
	void addHistory(Legion legion, String text, LegionHistoryType legionHistoryType)
	{
		addHistory(legion, text, legionHistoryType, 0, StringUtils.EMPTY);
	}
	
	/**
	 * This method will add a new history for a legion
	 * @param legion
	 * @param text
	 * @param legionHistoryType
	 * @param tabId
	 * @param description
	 */
	public void addHistory(Legion legion, String text, LegionHistoryType legionHistoryType, int tabId, String description)
	{
		final LegionHistory legionHistory = new LegionHistory(legionHistoryType, text, new Timestamp(System.currentTimeMillis()), tabId, description);
		
		legion.addHistory(legionHistory);
		DAOManager.getDAO(LegionDAO.class).saveNewLegionHistory(legion.getLegionId(), legionHistory);
		
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_TABS(legion.getLegionHistoryByTabId(tabId), tabId));
	}
	
	/**
	 * This method will add a new legion member to a legion with VOLUNTEER rank
	 * @param legion
	 * @param player
	 */
	void addLegionMember(Legion legion, Player player)
	{
		addLegionMember(legion, player, LegionRank.VOLUNTEER);
	}
	
	/**
	 * This method will add a new legion member to a legion with input rank
	 * @param legion
	 * @param player
	 * @param rank
	 */
	private void addLegionMember(Legion legion, Player player, LegionRank rank)
	{
		// Set legion member of player and save in the database
		player.setLegionMember(new LegionMember(player.getObjectId(), legion, rank));
		storeLegionMember(player.getLegionMember(), true);
		
		// Send the new legion member the required legion packets
		PacketSendUtility.sendPacket(player, new SM_LEGION_INFO(legion));
		
		final ListSplitter<LegionMemberEx> splits = new ListSplitter<>(loadLegionMemberExList(legion, player.getObjectId()), 110);
		// Send the member list to the new legion member
		while (!splits.isLast())
		{
			PacketSendUtility.sendPacket(player, new SM_LEGION_MEMBERLIST(splits.getNext(), splits.isFirst()));
		}
		
		// Send legion member info to the members
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_ADD_MEMBER(player, false, 1300260, player.getName()), player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_LEGION_ADD_MEMBER(player, false, 0, ""));
		// Send legion emblem information
		final LegionEmblem legionEmblem = legion.getLegionEmblem();
		PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(), legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b(), legionEmblem.getEmblemType()), true);
		
		// Send legion edit
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x08));
		
		// Update legion member's appearance in game
		PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_TITLE(player.getObjectId(), legion.getLegionId(), legion.getLegionName(), player.getLegionMember().getRank().getRankId()), true);
		legion.addBonus();
	}
	
	/**
	 * This method will remove a legion member
	 * @param charName
	 * @param kick
	 * @param playerName
	 * @return true if successful
	 */
	private boolean removeLegionMember(String charName, boolean kick, String playerName)
	{
		/**
		 * Get LegionMemberEx from cache or database if offline
		 */
		final LegionMemberEx legionMember = getLegionMemberEx(charName);
		if (legionMember == null)
		{
			log.error("Char name does not exist in legion member table: " + charName);
			return false;
		}
		
		/**
		 * Delete legion member from database and cache
		 */
		deleteLegionMemberFromDB(legionMember);
		
		/**
		 * If player is online send packet and reset legion member
		 */
		final Player player = world.findPlayer(charName);
		if (player != null)
		{
			PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_TITLE(player.getObjectId(), 0, "", 2), true);
		}
		final Legion legion = legionMember.getLegion();
		/**
		 * Send packets to legion members
		 */
		if (kick)
		{
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_LEAVE_MEMBER(1300247, legionMember.getObjectId(), playerName, legionMember.getName()));
		}
		else
		{
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_LEAVE_MEMBER(900699, legionMember.getObjectId(), charName));
		}
		legion.removeBonus();
		return true;
	}
	
	/**
	 * This method will handle legion stuff
	 * @param exOpcode
	 * @param activePlayer
	 * @param charName
	 * @param newNickname
	 * @param rank
	 */
	public void handleCharNameRequest(int exOpcode, Player activePlayer, String charName, String newNickname, int rank)
	{
		final Legion legion = activePlayer.getLegion();
		
		charName = Util.convertName(charName);
		final Player targetPlayer = world.findPlayer(charName);
		
		switch (exOpcode)
		{
			/**
			 * Invite to legion *
			 */
			case 0x01:
			{
				if (targetPlayer != null)
				{
					if (targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.GUILD))
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_INVITE_GUILD(charName));
						return;
					}
					invitePlayerToLegion(activePlayer, targetPlayer);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_NO_USER_TO_INVITE);
				}
				break;
			}
			/**
			 * Kick member from legion *
			 */
			case LEGION_ACTION_KICK:
			{
				/**
				 * Check if player can be kicked
				 */
				if (legionRestrictions.canKickPlayer(activePlayer, charName))
				{
					if (removeLegionMember(charName, true, activePlayer.getName()))
					{
						// send packet to members?
						if (targetPlayer != null)
						{
							PacketSendUtility.sendPacket(targetPlayer, new SM_LEGION_LEAVE_MEMBER(1300246, 0, legion.getLegionName()));
							targetPlayer.resetLegionMember();
						}
					}
				}
				if (legion.hasBonus())
				{
					PacketSendUtility.sendPacket(activePlayer, new SM_ICON_INFO(1, false));
				}
				break;
			}
			/**
			 * Appoint a new Brigade General *
			 */
			case 0x05:
			{
				if (targetPlayer != null)
				{
					appointBrigadeGeneral(activePlayer, targetPlayer);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_NO_USER_TO_INVITE);
				}
				break;
			}
			/**
			 * Appoint Centurion/Legionairy *
			 */
			case 0x06:
			{
				if (targetPlayer != null)
				{
					appointRank(activePlayer, targetPlayer, rank);
				}
				else
				{
					appointRank(activePlayer, charName, rank);
				}
				break;
			}
			/**
			 * Set nickname *
			 */
			case 0x0F:
			{
				changeNickname(activePlayer, charName, newNickname);
				break;
			}
		}
	}
	
	/**
	 * This method will handle announcement and self intro changement
	 * @param exOpcode
	 * @param activePlayer
	 * @param text
	 */
	public void handleLegionRequest(int exOpcode, Player activePlayer, String text)
	{
		switch (exOpcode)
		{
			/**
			 * Edit announcements *
			 */
			case 0x09:
			{
				changeAnnouncement(activePlayer, text);
				break;
			}
			/**
			 * Change self introduction *
			 */
			case 0x0A:
			{
				changeSelfIntro(activePlayer, text);
				break;
			}
		}
	}
	
	/**
	 * @param exOpcode
	 * @param activePlayer
	 */
	public void handleLegionRequest(int exOpcode, Player activePlayer)
	{
		switch (exOpcode)
		{
			/**
			 * Leave legion *
			 */
			case 0x02:
			{
				if (legionRestrictions.canLeave(activePlayer))
				{
					if (removeLegionMember(activePlayer.getName(), false, ""))
					{
						final Legion legion = activePlayer.getLegion();
						PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_LEAVE_MEMBER(1300241, 0, legion.getLegionName()));
						activePlayer.resetLegionMember();
						if (legion.hasBonus())
						{
							PacketSendUtility.sendPacket(activePlayer, new SM_ICON_INFO(1, false));
						}
					}
				}
				break;
			}
			/**
			 * Level legion up *
			 */
			case 0x0E:
			{
				requestChangeLevel(activePlayer);
				break;
			}
		}
	}
	
	public boolean removePlayerFromLegionAsItself(Player player)
	{
		if (removeLegionMember(player.getName(), false, ""))
		{
			final Legion legion = player.getLegion();
			PacketSendUtility.sendPacket(player, new SM_LEGION_LEAVE_MEMBER(1300241, 0, legion.getLegionName()));
			player.resetLegionMember();
			if (legion.hasBonus())
			{
				PacketSendUtility.sendPacket(player, new SM_ICON_INFO(1, false));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * @param activePlayer
	 */
	public void onLogin(Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		
		// Tell all legion members player has come online
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(activePlayer, 0, ""), activePlayer.getObjectId());
		
		// Notify legion members player has logged in
		PacketSendUtility.broadcastPacketToLegion(legion, SM_SYSTEM_MESSAGE.STR_MSG_NOTIFY_LOGIN_GUILD(activePlayer.getName()), activePlayer.getObjectId());
		
		// Send member add to player
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_ADD_MEMBER(activePlayer, true, 0, ""));
		
		// Send legion info packets
		PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_INFO(legion));
		
		// Send member list to player
		final ListSplitter<LegionMemberEx> splits = new ListSplitter<>(loadLegionMemberExList(legion, null), 110);
		// Send the member list to the new legion member
		while (!splits.isLast())
		{
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_MEMBERLIST(splits.getNext(), splits.isFirst()));
		}
		
		// Send current announcement to player
		displayLegionMessage(activePlayer, legion.getCurrentAnnouncement());
		
		if (legion.isDisbanding())
		{
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_EDIT(0x06, legion.getDisbandTime()));
		}
		if (legion.hasBonus())
		{
			PacketSendUtility.sendPacket(activePlayer, new SM_ICON_INFO(1, true));
		}
		else
		{
			legion.addBonus();
		}
	}
	
	/**
	 * @param player
	 */
	public void onLogout(Player player)
	{
		final Legion legion = player.getLegion();
		final LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
		if (lwh.getWhUser() == player.getObjectId())
		{
			lwh.setWhUser(0);
		}
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(player));
		storeLegion(legion);
		storeLegionMember(player.getLegionMember());
		storeLegionMemberExInCache(player);
		storeLegionAnnouncements(legion);
		legion.removeBonus();
	}
	
	public void clearCaches()
	{
		allCachedLegions.clear();
		allCachedLegionMembers.clear();
	}
	
	/**
	 * This class contains all restrictions for legion features
	 * @author Simple
	 */
	private class LegionRestrictions
	{
		/**
		 * Static Emblem information *
		 */
		private static final int MIN_EMBLEM_ID = 0;
		private static final int MAX_EMBLEM_ID = 49;
		
		public LegionRestrictions()
		{
		}
		
		/**
		 * This method checks all restrictions for legion creation
		 * @param activePlayer
		 * @param legionName
		 * @return true if allow to create a legion
		 */
		boolean canCreateLegion(Player activePlayer, String legionName)
		{
			/* Some reasons why legions can' be created */
			if (!isValidName(legionName))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_INVALID_GUILD_NAME);
				return false;
			} // STR_GUILD_CREATE_TOO_FAR_FROM_CREATOR_NPC TODO
			else if (!isFreeName(legionName))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_SAME_GUILD_EXIST);
				return false;
			}
			else if (activePlayer.isLegionMember())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_ALREADY_BELONGS_TO_GUILD);
				return false;
			}
			else if (activePlayer.getInventory().getKinah() < LegionConfig.LEGION_CREATE_REQUIRED_KINAH)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_NOT_ENOUGH_MONEY);
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for invite player to legion
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can invite player
		 */
		boolean canInvitePlayer(Player activePlayer, Player targetPlayer)
		{
			final Legion legion = activePlayer.getLegion();
			if (activePlayer.getLifeStats().isAlreadyDead())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_CANT_INVITE_WHEN_DEAD);
				return false;
			}
			if (isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_CAN_NOT_INVITE_SELF);
				return false;
			}
			else if (targetPlayer.isLegionMember())
			{
				if (legion.isMember(targetPlayer.getObjectId()))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_HE_IS_MY_GUILD_MEMBER(targetPlayer.getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_HE_IS_OTHER_GUILD_MEMBER(targetPlayer.getName()));
				}
				return false;
			}
			else if (!activePlayer.getLegionMember().hasRights(LegionPermissionsMask.INVITE))
			{
				// No rights to invite
				return false;
			}
			else if ((activePlayer.getRace() != targetPlayer.getRace()) && !LegionConfig.LEGION_INVITEOTHERFACTION)
			{
				// Not Same Race
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for kicking a player from a legion
		 * @param activePlayer
		 * @param charName
		 * @return true if can kick player
		 */
		boolean canKickPlayer(Player activePlayer, String charName)
		{
			/**
			 * Get LegionMemberEx from cache or database if offline
			 */
			final LegionMemberEx legionMember = getLegionMemberEx(charName);
			if (legionMember == null)
			{
				log.error("Char name does not exist in legion member table: " + charName);
				return false;
			}
			
			// TODO: Can not kick during a war!!
			// STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH
			final Legion legion = activePlayer.getLegion();
			
			if (isSelf(activePlayer, legionMember.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_CANT_BANISH_SELF);
				return false;
			}
			else if (legionMember.isBrigadeGeneral())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_CAN_BANISH_MASTER);
				return false;
			}
			else if (legionMember.getRank() == activePlayer.getLegionMember().getRank())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH);
				return false;
			}
			else if (!legion.isMember(legionMember.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH);
				return false;
			}
			else if (!activePlayer.getLegionMember().hasRights(LegionPermissionsMask.KICK))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH);
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for appointing brigade general
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can appoint brigade general
		 */
		boolean canAppointBrigadeGeneral(Player activePlayer, Player targetPlayer)
		{
			final Legion legion = activePlayer.getLegion();
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT);
				return false;
			}
			if (isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_ERROR_SELF);
				return false;
			}
			else if (!legion.isMember(targetPlayer.getObjectId()))
			{
				// not in same legion
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for appointing rank
		 * @param activePlayer
		 * @param targetObjId
		 * @return true if can appoint rank
		 */
		boolean canAppointRank(Player activePlayer, int targetObjId)
		{
			final Legion legion = activePlayer.getLegion();
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT);
				return false;
			}
			if (isSelf(activePlayer, targetObjId))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_ERROR_SELF);
				return false;
			}
			else if (!legion.isMember(targetObjId))
			{
				// not in same legion
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for changing self intro
		 * @param activePlayer
		 * @param newSelfIntro
		 * @return true if allowed to change self intro
		 */
		boolean canChangeSelfIntro(Player activePlayer, String newSelfIntro)
		{
			if (!isValidSelfIntro(newSelfIntro))
			{
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for changing legion level
		 * @param activePlayer
		 * @return true if allowed to change legion level
		 */
		boolean canChangeLevel(Player activePlayer)
		{
			final Legion legion = activePlayer.getLegion();
			final int levelContributionPrice = legion.getContributionPrice();
			
			if (legion.getLegionLevel() == MAX_LEGION_LEVEL)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_CANT_LEVEL_UP);
				return false;
			}
			else if (LegionConfig.ENABLE_GUILD_TASK_REQ && (legion.getLegionLevel() >= 5))
			{
				if (!ChallengeTaskService.getInstance().canRaiseLegionLevel(legion.getLegionId(), legion.getLegionLevel()))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_LEVEL_UP_CHALLENGE_TASK(legion.getLegionLevel()));
					return false;
				}
			}
			else if (activePlayer.getInventory().getKinah() < legion.getKinahPrice())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_NOT_ENOUGH_MONEY);
				return false;
			}
			else if (!legion.hasRequiredMembers())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_NOT_ENOUGH_MEMBER);
				return false;
			}
			else if (legion.getContributionPoints() < levelContributionPrice)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_NOT_ENOUGH_POINT);
				return false;
			}
			return true;
		}
		
		/**
		 * This method will check all restrictions for changing nickname
		 * @param legion
		 * @param targetObjectId
		 * @param newNickname
		 * @return true if allowed to change nickname of target player
		 */
		boolean canChangeNickname(Legion legion, int targetObjectId, String newNickname)
		{
			if (!isValidNickname(newNickname))
			{
				// invalid nickname
				return false;
			}
			else if (!legion.isMember(targetObjectId))
			{
				// not in same legion
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for changing announcements
		 * @param legionMember
		 * @param announcement
		 * @return true if can change announcement
		 */
		boolean canChangeAnnouncement(LegionMember legionMember, String announcement)
		{
			return legionMember.hasRights(LegionPermissionsMask.EDIT) && (announcement.isEmpty() ? true : isValidAnnouncement(announcement));
		}
		
		/**
		 * This method checks all restrictions for disband legion
		 * @param activePlayer
		 * @param legion
		 * @return true if can disband legion
		 */
		boolean canDisbandLegion(Player activePlayer, Legion legion)
		{
			// TODO: Can't disband during a war!!
			// TODO: Can't disband legion with fortress or hideout!!
			if (legion == null)
			{
				return false;
			}
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_ONLY_MASTER_CAN_DISPERSE);
				return false;
			}
			else if (legion.getLegionWarehouse().size() > 0)
			{
				// TODO: Can't disband during using legion warehouse!!
				return false;
			}
			else if (legion.isDisbanding())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_ALREADY_REQUESTED);
				return false;
			}
			else if (legion.getLegionWarehouse().size() > 0)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_CANT_DISPERSE_GUILD_STORE_ITEM_IN_WAREHOUSE);
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for leaving
		 * @param activePlayer
		 * @return true if allowed to leave
		 */
		boolean canLeave(Player activePlayer)
		{
			if (isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_LEAVE_MASTER_CANT_LEAVE_BEFORE_CHANGE_MASTER);
				return false;
			}
			return true;
		}
		
		public boolean canChangeLegionJoinSetting(Player activePlayer)
		{
			if (!isBrigadeGeneral(activePlayer))
			{
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for recreate legion
		 * @param activePlayer
		 * @param legion
		 * @return true if allowed to recreate legion
		 */
		boolean canRecreateLegion(Player activePlayer, Legion legion)
		{
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_ONLY_MASTER_CAN_DISPERSE);
				return false;
			}
			else if (!legion.isDisbanding())
			{
				// Legion is not disbanding
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for upload emblem info
		 * @param activePlayer
		 * @return true if allowed to upload emblem info
		 */
		boolean canUploadEmblemInfo(Player activePlayer)
		{
			// TODO: System Messages
			if (!isBrigadeGeneral(activePlayer))
			{
				// Not legion leader
				return false;
			}
			else if (activePlayer.getLegion().getLegionLevel() < 3)
			{
				// Legion level isn't high enough
				return false;
			}
			else if (activePlayer.getLegion().getLegionEmblem().isUploading())
			{
				// Already uploading emblem, reset uploading
				activePlayer.getLegion().getLegionEmblem().setUploading(false);
				return false;
			}
			return true;
		}
		
		/**
		 * This method checks all restrictions for uploading emblem
		 * @param activePlayer
		 * @return true if allowed to upload emblem
		 */
		boolean canUploadEmblem(Player activePlayer)
		{
			if (!isBrigadeGeneral(activePlayer))
			{
				// Not legion leader
				return false;
			}
			else if (activePlayer.getLegion().getLegionLevel() < 3)
			{
				// Legion level isn't high enough
				return false;
			}
			else if (!activePlayer.getLegion().getLegionEmblem().isUploading())
			{
				// Not uploading emblem
				return false;
			}
			return true;
		}
		
		/**
		 * @param player
		 * @return
		 */
		public boolean canOpenWarehouse(Player player)
		{
			if (!player.isLegionMember())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NO_GUILD_TO_DEPOSIT);
				return false;
			}
			final Legion legion = player.getLegion();
			final LegionWarehouse legWh = legion.getLegionWarehouse();
			final int whUser = legWh.getWhUser();
			final int playerId = player.getObjectId();
			if (legion.isDisbanding())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GUILD_WAREHOUSE_CANT_USE_WHILE_DISPERSE);
				return false;
			}
			else if (!LegionConfig.LEGION_WAREHOUSE)
			{
				// Legion Warehouse not enabled
				return false;
			}
			else if ((whUser != playerId) && (legWh.getWhUser() != 0))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GUILD_WAREHOUSE_IN_USE);
				return false;
			}
			legWh.setWhUser(player.getObjectId());
			return true;
		}
		
		/**
		 * @param activePlayer
		 * @param legionId
		 * @param emblemId
		 * @return
		 */
		public boolean canStoreLegionEmblem(Player activePlayer, int legionId, int emblemId)
		{
			final Legion legion = activePlayer.getLegion();
			if ((emblemId < MIN_EMBLEM_ID) || (emblemId > MAX_EMBLEM_ID))
			{
				// Not a valid emblemId
				return false;
			}
			else if (legionId != legion.getLegionId())
			{
				// legion id not equal
				return false;
			}
			else if (legion.getLegionLevel() < 2)
			{
				// legion level not high enough
				return false;
			}
			else if (activePlayer.getInventory().getKinah() < LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH));
				return false;
			}
			return true;
		}
		
		/**
		 * Checks if player is brigade general and returns message if not
		 * @param player
		 * @return
		 */
		private boolean isBrigadeGeneral(Player player)
		{
			return player.getLegionMember().isBrigadeGeneral();
		}
		
		/**
		 * Checks if target is same as current player
		 * @param player
		 * @param targetObjId
		 * @return
		 */
		private boolean isSelf(Player player, int targetObjId)
		{
			return player.sameObjectId(targetObjId);
		}
		
		/**
		 * Checks if name is already taken or not
		 * @param name character name
		 * @return true if is free, false in other case
		 */
		private boolean isFreeName(String name)
		{
			return !DAOManager.getDAO(LegionDAO.class).isNameUsed(name);
		}
		
		/**
		 * Checks if a self intro is valid. It should contain only english letters
		 * @param name character name
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidSelfIntro(String name)
		{
			return LegionConfig.SELF_INTRO_PATTERN.matcher(name).matches();
		}
		
		/**
		 * Checks if a nickname is valid. It should contain only english letters
		 * @param name character name
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidNickname(String name)
		{
			return LegionConfig.NICKNAME_PATTERN.matcher(name).matches();
		}
		
		/**
		 * Checks if a announcement is valid. It should contain only english letters
		 * @param name announcement
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidAnnouncement(String name)
		{
			return LegionConfig.ANNOUNCEMENT_PATTERN.matcher(name.replaceAll("\\r\\n", "")).matches();
		}
	}
	
	public void addWHItemHistory(Player player, int itemId, long count, IStorage sourceStorage, IStorage destStorage)
	{
		final Legion legion = player.getLegion();
		if (legion != null)
		{
			final String description = Integer.toString(itemId) + ":" + Long.toString(count);
			if (sourceStorage.getStorageType() == StorageType.LEGION_WAREHOUSE)
			{
				LegionService.getInstance().addHistory(legion, player.getName(), LegionHistoryType.ITEM_WITHDRAW, 2, description);
			}
			else if (destStorage.getStorageType() == StorageType.LEGION_WAREHOUSE)
			{
				LegionService.getInstance().addHistory(legion, player.getName(), LegionHistoryType.ITEM_DEPOSIT, 2, description);
			}
		}
	}
	
	public void handleLegionSearch(Player player, int type, String legionName)
	{
		FastList<Legion> matchingLegions = new FastList<>();
		switch (type)
		{
			case 0:
			{
				matchingLegions = allCachedLegions.getAllLegions();
				break;
			}
			case 1:
			{
				for (Legion legion : allCachedLegions.getAllLegions())
				{
					if (legion.getLegionName().toLowerCase().contains(legionName.toLowerCase()))
					{
						matchingLegions.add(legion);
					}
				}
				break;
			}
		}
		if (player.isLegionMember())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_YOU_ARE_ALREADY_BELONGS_TO_GUILD);
		}
		else if (player.getLegion() == null)
		{
			// It is not ready yet. Please wait.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402395));
			// PacketSendUtility.sendPacket(player, new SM_LEGION_SEARCH(matchingLegions)); //To Do Fix...
		}
	}
	
	public void setJoinDescription(Player player, String description)
	{
		final Legion legion = player.getLegion();
		if (legion == null)
		{
			return;
		}
		if (legionRestrictions.canChangeLegionJoinSetting(player))
		{
			legion.setDescription(description);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x0C, legion));
			DAOManager.getDAO(LegionDAO.class).updateLegionDescription(legion);
		}
	}
	
	public void setJoinType(Player player, int joinType)
	{
		final Legion legion = player.getLegion();
		if (legion == null)
		{
			return;
		}
		if (legionRestrictions.canChangeLegionJoinSetting(player))
		{
			legion.setJoinType(joinType);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x0D, legion));
			DAOManager.getDAO(LegionDAO.class).updateLegionDescription(legion);
		}
	}
	
	public void setJoinMinLevel(Player player, int minLevel)
	{
		final Legion legion = player.getLegion();
		if (legion == null)
		{
			return;
		}
		if (legionRestrictions.canChangeLegionJoinSetting(player))
		{
			legion.setMinJoinLevel(minLevel);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x0E, legion));
			DAOManager.getDAO(LegionDAO.class).updateLegionDescription(legion);
		}
	}
	
	public void sendLegionJoinRequestInfo(Player player, int legionId)
	{
		if (legionId <= 0)
		{
			PacketSendUtility.sendPacket(player, new SM_LEGION_REQUEST_INFO(0, ""));
		}
		else
		{
			final Legion legion = getLegion(legionId);
			PacketSendUtility.sendPacket(player, new SM_LEGION_REQUEST_INFO(legion.getLegionId(), legion.getLegionName()));
		}
	}
	
	public void sendLegionJoinRequest(Player player)
	{
		final int legionId = player.getCommonData().getJoinRequestLegionId();
		if (legionId <= 0)
		{
			PacketSendUtility.sendPacket(player, new SM_LEGION_REQUEST_INFO(0, ""));
		}
		else
		{
			final Legion legion = getLegion(legionId);
			PacketSendUtility.sendPacket(player, new SM_LEGION_REQUEST_INFO(legion.getLegionId(), legion.getLegionName()));
		}
	}
	
	public void handleLegionJoinRequest(Player player, int legionId, int joinType, String joinRequestMsg)
	{
		final Legion legion = getLegion(legionId);
		if (legion == null)
		{
			return;
		}
		switch (joinType)
		{
			case 0:
			{
				player.getCommonData().setJoinRequestLegionId(legionId);
				sendLegionJoinRequestInfo(player, legionId);
				final LegionJoinRequest ljr = new LegionJoinRequest(legionId, player, joinRequestMsg);
				legion.addJoinRequest(ljr);
				final Player brigadeGeneral = getBrigadeGeneral(legion);
				if (brigadeGeneral != null)
				{
					PacketSendUtility.sendPacket(brigadeGeneral, new SM_LEGION_REQUEST_PLAYER(ljr));
				}
				break;
			}
			case 1:
			{
				directAddPlayer(legion, player);
				break;
			}
		}
	}
	
	public void handleJoinRequestCancel(Player player, int legionId)
	{
		final Legion legion = getLegion(legionId);
		player.clearJoinRequest();
		sendLegionJoinRequestInfo(player, 0);
		if (legion.getJoinRequestMap().containsKey(player.getObjectId()))
		{
			legion.getJoinRequestMap().remove(player.getObjectId());
		}
		final Player bg = getBrigadeGeneral(legion);
		if (bg != null)
		{
			PacketSendUtility.sendPacket(bg, new SM_LEGION_REQUEST(player.getObjectId(), false));
		}
	}
	
	public void handleJoinRequestGetAnswer(Player player)
	{
		final PlayerCommonData pcd = player.getCommonData();
		switch (pcd.getJoinRequestState())
		{
			case ACCEPTED:
			{
				directAddPlayer(pcd.getJoinRequestLegionId(), player);
				handleJoinRequestCancel(player, player.getCommonData().getJoinRequestLegionId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LEGION_APPLICATION_ACCEPTED);
				break;
			}
			case DENIED:
			{
				handleJoinRequestCancel(player, player.getCommonData().getJoinRequestLegionId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LEGION_APPLICATION_DENIED);
				break;
			}
		}
	}
	
	public void handleJoinRequestGiveAnswer(Player brigadeGeneral, int playerId, boolean accept)
	{
		boolean playerOnline = true;
		final LegionJoinRequestState state = accept ? LegionJoinRequestState.ACCEPTED : LegionJoinRequestState.DENIED;
		final Legion legion = brigadeGeneral.getLegion();
		if (legion == null)
		{
			return;
		}
		final Player player = World.getInstance().findPlayer(playerId);
		if (player == null)
		{
			playerOnline = false;
			DAOManager.getDAO(PlayerDAO.class).updateLegionJoinRequestState(playerId, state);
			if (legion.getJoinRequestMap().containsKey(playerId))
			{
				legion.getJoinRequestMap().remove(playerId);
			}
		}
		PacketSendUtility.sendPacket(brigadeGeneral, new SM_LEGION_REQUEST(playerId, accept));
		if (playerOnline && (player != null))
		{
			player.getCommonData().setJoinRequestState(state);
			handleJoinRequestGetAnswer(player);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final LegionService instance = new LegionService();
	}
}