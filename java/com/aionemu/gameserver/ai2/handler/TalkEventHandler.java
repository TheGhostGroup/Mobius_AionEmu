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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.TownService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class TalkEventHandler
{
	public static void onTalk(NpcAI2 npcAI, Creature creature)
	{
		onSimpleTalk(npcAI, creature);
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if (QuestEngine.getInstance().onDialog(new QuestEnv(npcAI.getOwner(), player, 0, -1)))
			{
				return;
			}
			switch (npcAI.getOwner().getObjectTemplate().getTitleId())
			{
				case 462877: // Village Trade Broker.
				case 462878: // Village Guestbloom.
				{
					// case 462881: //Village Quest Board.
					final int playerTownId = TownService.getInstance().getTownResidence(player);
					final int currentTownId = TownService.getInstance().getTownIdByPosition(player);
					if (playerTownId != currentTownId)
					{
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 44));
						return;
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
					return;
				}
				default:
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcAI.getOwner().getObjectId(), 10));
					break;
				}
			}
		}
	}
	
	public static void onSimpleTalk(NpcAI2 npcAI, Creature creature)
	{
		npcAI.setSubStateIfNot(AISubState.TALK);
		npcAI.getOwner().setTarget(creature);
	}
	
	public static void onFinishTalk(NpcAI2 npcAI, Creature creature)
	{
		final Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId()))
		{
			if (npcAI.getState() != AIState.FOLLOWING)
			{
				owner.setTarget(null);
			}
			npcAI.think();
		}
	}
	
	public static void onSimpleFinishTalk(NpcAI2 npcAI, Creature creature)
	{
		final Npc owner = npcAI.getOwner();
		if (owner.isTargeting(creature.getObjectId()) && npcAI.setSubStateIfNot(AISubState.NONE))
		{
			owner.setTarget(null);
		}
	}
}