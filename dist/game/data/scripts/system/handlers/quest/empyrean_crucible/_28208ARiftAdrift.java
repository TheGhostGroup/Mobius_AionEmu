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
package system.handlers.quest.empyrean_crucible;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _28208ARiftAdrift extends QuestHandler
{
	private static final int questId = 28208;
	
	public _28208ARiftAdrift()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(205320).addOnQuestStart(questId);
		qe.registerQuestNpc(205320).addOnTalkEvent(questId);
		qe.registerQuestNpc(205321).addOnTalkEvent(questId);
		qe.registerQuestNpc(217819).addOnKillEvent(questId);
		qe.registerQuestNpc(218185).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final QuestDialog dialog = env.getDialog();
		final int targetId = env.getTargetId();
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 205320) // Inggness
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 4762);
				}
				return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 205321) // Anja
			{
				if (dialog == QuestDialog.USE_OBJECT)
				{
					return sendQuestDialog(env, 10002);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int targetId = env.getTargetId();
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			final int var = qs.getQuestVarById(0);
			if (var == 0)
			{
				final int var1 = qs.getQuestVarById(1);
				if (var1 < 4)
				{
					return defaultOnKillEvent(env, 217819, 0, 4, 1); // 1: 0 - 4
				}
				else if ((var1 == 4) && (targetId == 217819))
				{
					qs.setQuestVar(1); // 1
					updateQuestStatus(env);
					return true;
				}
			}
			else if ((var == 1) && (targetId == 218185))
			{
				qs.setQuestVarById(2, 1); // 2: 1
				qs.setStatus(QuestStatus.REWARD); // reward
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}
