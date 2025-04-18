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
package system.handlers.quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

public class _3326The_Shugo_Menace extends QuestHandler
{
	private static final int questId = 3326;
	
	public _3326The_Shugo_Menace()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(798053).addOnQuestStart(questId); // Gyabrunerk.
		qe.registerQuestNpc(798053).addOnTalkEvent(questId); // Gyabrunerk.
		qe.registerQuestNpc(210897).addOnKillEvent(questId); // Desert Caltrops.
		qe.registerQuestNpc(210936).addOnKillEvent(questId); // Desert Caltrops.
		qe.registerQuestNpc(210939).addOnKillEvent(questId); // Ancient Caltrops.
		qe.registerQuestNpc(210873).addOnKillEvent(questId); // Dune Ampha.
		qe.registerQuestNpc(210900).addOnKillEvent(questId); // Dune Ampha.
		qe.registerQuestNpc(210919).addOnKillEvent(questId); // Wasteland Ampha.
		qe.registerQuestNpc(211754).addOnKillEvent(questId); // Rotting Gnarl.
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || (qs.getStatus() == QuestStatus.COMPLETE))
		{
			if (targetId == 798053) // Gyabrunerk.
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 4);
				}
				return sendQuestStartDialog(env);
			}
		}
		if (qs == null)
		{
			return false;
		}
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 798053) // Gyabrunerk.
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 10002);
					}
					case SELECT_REWARD:
					{
						if (qs.getQuestVarById(0) != 20)
						{
							return false;
						}
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 798053) // Gyabrunerk.
			{
				if (env.getDialog() == QuestDialog.SELECT_REWARD)
				{
					return sendQuestDialog(env, 5);
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
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		if ((targetId == 210897) || // Desert Caltrops.
			(targetId == 210936) || // Desert Caltrops.
			(targetId == 210939) || // Ancient Caltrops.
			(targetId == 210873) || // Dune Ampha.
			(targetId == 210900) || // Dune Ampha.
			(targetId == 210919) || // Wasteland Ampha.
			(targetId == 211754)) // Rotting Gnarl.
		{
			if ((var >= 0) && (var < 20))
			{
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}