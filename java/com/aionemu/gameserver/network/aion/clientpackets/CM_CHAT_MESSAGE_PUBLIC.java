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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.player.PlayerChatService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.chathandlers.ChatProcessor;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * Packet that reads normal chat messages.<br>
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_PUBLIC extends AionClientPacket
{
	private ChatType type;
	private String message;
	
	public CM_CHAT_MESSAGE_PUBLIC(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		type = ChatType.getChatTypeByInt(readC());
		message = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (ChatProcessor.getInstance().handleChatCommand(player, message))
		{
			return;
		}
		message = NameRestrictionService.filterMessage(message);
		if (LoggingConfig.LOG_CHAT)
		{
			PlayerChatService.chatLogging(player, type, message);
		}
		if (RestrictionsManager.canChat(player) && !PlayerChatService.isFlooding(player))
		{
			switch (type)
			{
				case GROUP:
				{
					if (!player.isInTeam())
					{
						return;
					}
					broadcastToGroupMembers(player);
					break;
				}
				case ALLIANCE:
				{
					if (!player.isInAlliance2())
					{
						return;
					}
					broadcastToAllianceMembers(player);
					break;
				}
				case GROUP_LEADER:
				{
					if (!player.isInTeam())
					{
						return;
					}
					if (player.isInGroup2())
					{
						broadcastToGroupMembers(player);
					}
					else
					{
						broadcastToAllianceMembers(player);
					}
					break;
				}
				case LEGION:
				{
					broadcastToLegionMembers(player);
					break;
				}
				case LEAGUE:
				case LEAGUE_ALERT:
				{
					if (!player.isInLeague())
					{
						return;
					}
					broadcastToLeagueMembers(player);
					break;
				}
				case NORMAL:
				case SHOUT:
				{
					if (player.isGM())
					{
						broadcastFromGm(player);
					}
					else
					{
						if (CustomConfig.SPEAKING_BETWEEN_FACTIONS)
						{
							broadcastToNonBlockedPlayers(player);
						}
						else
						{
							broadcastToNonBlockedRacePlayers(player);
						}
					}
					break;
				}
				case COMMAND:
				{
					if ((player.getAbyssRank().getRank() == AbyssRankEnum.COMMANDER) || (player.getAbyssRank().getRank() == AbyssRankEnum.SUPREME_COMMANDER))
					{
						broadcastFromCommander(player);
					}
					break;
				}
				default:
				{
					if (player.isGM())
					{
						broadcastFromGm(player);
					}
					else
					{
						AuditLogger.info(player, String.format("Send message type %s. Message: %s", type, message));
					}
					break;
				}
			}
		}
	}
	
	private void broadcastFromCommander(Player player)
	{
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return (senderRace == object.getRace().getRaceId());
			}
		});
	}
	
	private void broadcastFromGm(Player player)
	{
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true);
	}
	
	private void broadcastToNonBlockedPlayers(Player player)
	{
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return !object.getBlockList().contains(player.getObjectId());
			}
		});
	}
	
	private void broadcastToNonBlockedRacePlayers(Player player)
	{
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return (((senderRace == object.getRace().getRaceId()) && !object.getBlockList().contains(player.getObjectId())) || object.isGM());
			}
		});
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, "Unknow Message", type), false, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return (senderRace != object.getRace().getRaceId()) && !object.getBlockList().contains(player.getObjectId()) && !object.isGM();
			}
		});
	}
	
	private void broadcastToGroupMembers(Player player)
	{
		if (player.isInTeam())
		{
			player.getCurrentGroup().sendPacket(new SM_MESSAGE(player, message, type));
		}
		else
		{
			PacketSendUtility.sendMessage(player, "You are not in an alliance or group.");
		}
	}
	
	private void broadcastToAllianceMembers(Player player)
	{
		player.getPlayerAlliance2().sendPacket(new SM_MESSAGE(player, message, type));
	}
	
	private void broadcastToLeagueMembers(Player player)
	{
		player.getPlayerAlliance2().getLeague().sendPacket(new SM_MESSAGE(player, message, type));
	}
	
	private void broadcastToLegionMembers(Player player)
	{
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_MESSAGE(player, message, type));
		}
	}
}