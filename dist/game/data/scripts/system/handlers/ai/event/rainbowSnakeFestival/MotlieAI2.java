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
package system.handlers.ai.event.rainbowSnakeFestival;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.GeneralNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("motlie")
public class MotlieAI2 extends GeneralNpcAI2
{
	@Override
	protected void handleDialogStart(Player player)
	{
		switch (getNpcId())
		{
			case 832963: // Motlie E.
			case 832974: // Motlie A.
			{
				super.handleDialogStart(player);
				break;
			}
			default:
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				break;
			}
		}
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		final QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		final PlayerEffectController effectController = player.getEffectController();
		if (QuestEngine.getInstance().onDialog(env) && (dialogId != 1011))
		{
			return true;
		}
		if (dialogId == 10000)
		{
			int skillId = 0;
			switch (getNpcId())
			{
				case 832963: // Motlie E.
				case 832974: // Motlie A.
				{
					switch (Rnd.get(1, 2))
					{
						case 1:
						{
							skillId = 10976;
							effectController.removeEffect(10977);
							effectController.removeEffect(10978);
							effectController.removeEffect(10979);
							break;
						}
						case 2:
						{
							skillId = 10977;
							effectController.removeEffect(10976);
							effectController.removeEffect(10978);
							effectController.removeEffect(10979);
							break;
						}
					}
					break;
				}
			}
			SkillEngine.getInstance().getSkill(getOwner(), skillId, 1, player).useNoAnimationSkill();
		}
		else if ((dialogId == 1011) && (questId != 0))
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
		return true;
	}
}