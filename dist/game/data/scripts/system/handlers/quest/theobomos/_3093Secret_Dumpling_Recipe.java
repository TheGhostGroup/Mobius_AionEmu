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
package system.handlers.quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler (Encom)
 */
public class _3093Secret_Dumpling_Recipe extends QuestHandler
{
	private static final int questId = 3093;
	
	public _3093Secret_Dumpling_Recipe()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(798185).addOnQuestStart(questId); // Bororinerk.
		qe.registerQuestNpc(798185).addOnTalkEvent(questId); // Bororinerk.
		qe.registerQuestNpc(798177).addOnTalkEvent(questId); // Gastak.
		qe.registerQuestNpc(798179).addOnTalkEvent(questId); // Jabala.
		qe.registerQuestNpc(203784).addOnTalkEvent(questId); // Hestia.
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 798185) // Bororinerk.
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 1011);
				}
				else if (env.getDialogId() == 1002)
				{
					if (giveQuestItem(env, 182206062, 1))
					{
						return sendQuestStartDialog(env);
					}
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if ((qs.getStatus() == QuestStatus.REWARD))
		{
			if (env.getDialog() == QuestDialog.START_DIALOG)
			{
				return sendQuestDialog(env, 2375);
			}
			else if (env.getDialogId() == 1009)
			{
				removeQuestItem(env, 182208052, 1);
				return sendQuestEndDialog(env);
			}
			else
			{
				return sendQuestEndDialog(env);
			}
		}
		else if (targetId == 798177) // Gastak.
		{
			if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 1))
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_1)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (targetId == 798179) // Jabala.
		{
			if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 2))
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 1693);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_2)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (targetId == 203784) // Hestia.
		{
			if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 3))
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 2034);
				}
				else if (env.getDialog() == QuestDialog.STEP_TO_3)
				{
					giveQuestItem(env, 182208052, 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		return false;
	}
}