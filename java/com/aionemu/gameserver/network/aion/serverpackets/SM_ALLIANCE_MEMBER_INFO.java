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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author Sarynth (Thx Rhys2002 for Packets)
 */
public class SM_ALLIANCE_MEMBER_INFO extends AionServerPacket
{
	private final Player player;
	private PlayerAllianceEvent event;
	private final int allianceId;
	private final int objectId;
	
	public SM_ALLIANCE_MEMBER_INFO(PlayerAllianceMember member, PlayerAllianceEvent event)
	{
		player = member.getObject();
		this.event = event;
		allianceId = member.getAllianceId();
		objectId = member.getObjectId();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final PlayerCommonData pcd = player.getCommonData();
		final WorldPosition wp = pcd.getPosition();
		if ((event == PlayerAllianceEvent.ENTER) && !player.isOnline())
		{
			event = PlayerAllianceEvent.ENTER_OFFLINE;
		}
		writeD(allianceId);
		writeD(objectId);
		if (player.isOnline())
		{
			final PlayerLifeStats pls = player.getLifeStats();
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp());
			writeD(pls.getCurrentFp());
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}
		writeD(0);
		writeD(wp.getMapId());
		writeD(wp.getMapId());
		writeF(wp.getX());
		writeF(wp.getY());
		writeF(wp.getZ());
		writeC(pcd.getPlayerClass().getClassId());
		writeC(pcd.getGender().getGenderId());
		writeC(pcd.getLevel());
		writeC(event.getId());
		writeH(0x00);
		writeC(0x0);
		writeC(0x00);// unk 5.1
		switch (event)
		{
			case LEAVE:
			case LEAVE_TIMEOUT:
			case BANNED:
			case MOVEMENT:
			case DISCONNECTED:
			{
				break;
			}
			case JOIN:
			case ENTER:
			case ENTER_OFFLINE:
			case UPDATE:
			case RECONNECT:
			case APPOINT_VICE_CAPTAIN:
			case DEMOTE_VICE_CAPTAIN:
			case APPOINT_CAPTAIN:
			{
				writeS(pcd.getName());
				writeD(0);
				writeD(0);
				if (player.isOnline())
				{
					final List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
					writeC(127);
					writeH(abnormalEffects.size());
					for (Effect effect : abnormalEffects)
					{
						writeD(effect.getEffectorId());
						writeH(effect.getSkillId());
						writeC(effect.getSkillLevel());
						writeC(effect.getTargetSlot());
						writeD(effect.getRemainingTime());
					}
					writeB(new byte[32]);
				}
				else
				{
					final List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
					writeD(0);
					writeD(0);
					writeC(0);
					writeH(abnormalEffects.size());
					for (Effect effect : abnormalEffects)
					{
						writeD(effect.getEffectorId());
						writeH(effect.getSkillId());
						writeC(effect.getSkillLevel());
						writeC(effect.getTargetSlot());
						writeD(effect.getRemainingTime());
					}
					writeB(new byte[32]);
				}
				break;
			}
			case MEMBER_GROUP_CHANGE:
			{
				writeS(pcd.getName());
				break;
			}
			default:
			{
				break;
			}
		}
	}
}