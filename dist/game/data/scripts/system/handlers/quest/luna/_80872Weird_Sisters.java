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
package system.handlers.quest.luna;

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
public class _80872Weird_Sisters extends QuestHandler
{
	private static final int questId = 80872;
	
	public _80872Weird_Sisters()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(833825).addOnQuestStart(questId); // Jay.
		qe.registerQuestNpc(834167).addOnTalkEvent(questId); // Jay.
		qe.registerQuestNpc(703378).addOnTalkEvent(questId); // Remirunrunerk’s Footlocker.
		qe.registerQuestNpc(703379).addOnTalkEvent(questId); // Bomirunrunerk’s Footlocker.
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int targetId = env.getTargetId();
		final QuestDialog dialog = env.getDialog();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 833825) // Jay.
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 4762);
					}
					case ACCEPT_QUEST:
					case ACCEPT_QUEST_SIMPLE:
					{
						return sendQuestStartDialog(env);
					}
					case REFUSE_QUEST_SIMPLE:
					{
						return closeDialogWindow(env);
					}
				}
			}
		}
		else if (targetId == 703378) // Remirunrunerk’s Footlocker.
		{
			if (dialog == QuestDialog.USE_OBJECT)
			{
				closeDialogWindow(env);
				return true;
			}
		}
		else if (targetId == 703379) // Bomirunrunerk’s Footlocker.
		{
			if (dialog == QuestDialog.USE_OBJECT)
			{
				closeDialogWindow(env);
				return true;
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 834167) // Jay.
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 1011);
					}
					case CHECK_COLLECTED_ITEMS:
					{
						if (QuestService.collectItemCheck(env, true))
						{
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						}
						return sendQuestDialog(env, 10001);
					}
					case FINISH_DIALOG:
					{
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 834167) // Jay.
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 10002);
				}
				else if (env.getDialog() == QuestDialog.SELECT_REWARD)
				{
					return sendQuestDialog(env, 5);
				}
				else
				{
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}