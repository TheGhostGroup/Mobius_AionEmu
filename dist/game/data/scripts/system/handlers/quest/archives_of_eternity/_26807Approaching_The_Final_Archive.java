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
package system.handlers.quest.archives_of_eternity;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rinzler (Encom)
 */
public class _26807Approaching_The_Final_Archive extends QuestHandler
{
	private static final int questId = 26807;
	private static final int[] IDEternity01Mobs =
	{
		220307,
		220310,
		220313,
		220316,
		220319,
		220325,
		220328,
		220331
	};
	
	public _26807Approaching_The_Final_Archive()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerOnEnterWorld(questId);
		qe.registerQuestNpc(806149).addOnTalkEvent(questId);
		for (int mob : IDEternity01Mobs)
		{
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int targetId = env.getTargetId();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 806149)
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 1011);
					}
					case ACCEPT_QUEST:
					case ACCEPT_QUEST_SIMPLE:
					{
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 806149:
				{
					switch (env.getDialog())
					{
						case START_DIALOG:
						{
							return sendQuestDialog(env, 2375);
						}
						case SELECT_REWARD:
						{
							changeQuestStep(env, 0, 0, true);
							return sendQuestEndDialog(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806149)
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 10002);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() == 301540000)
		{
			if ((qs == null) || qs.canRepeat())
			{
				env.setQuestId(questId);
				if (QuestService.startQuest(env))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			switch (env.getTargetId())
			{
				case 220307:
				case 220309:
				case 220313:
				case 220316:
				case 220319:
				case 220325:
				case 220328:
				case 220331:
				{
					if (qs.getQuestVarById(1) < 30)
					{
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
					}
					if (qs.getQuestVarById(1) >= 30)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
				}
			}
		}
		return false;
	}
}