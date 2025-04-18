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
package system.handlers.quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rinzler (Encom)
 */
public class _21057Fundin_Old_Grudge extends QuestHandler
{
	private static final int questId = 21057;
	
	public _21057Fundin_Old_Grudge()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestItem(182207846, questId);
		qe.registerQuestNpc(799354).addOnTalkEvent(questId);
		qe.registerQuestNpc(296489).addOnKillEvent(questId);
		qe.registerQuestNpc(296490).addOnKillEvent(questId);
		qe.registerQuestNpc(296491).addOnKillEvent(questId);
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
			if (targetId == 0)
			{
				if (dialog == QuestDialog.ACCEPT_QUEST)
				{
					removeQuestItem(env, 182207846, 1);
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 799354)
			{
				if (dialog == QuestDialog.USE_OBJECT)
				{
					return sendQuestDialog(env, 10002);
				}
				else if (dialog == QuestDialog.SELECT_ACTION_2034)
				{
					return sendQuestDialog(env, 2034);
				}
				else if (dialog == QuestDialog.SELECT_REWARD)
				{
					if (player.getInventory().getKinah() >= 15000000)
					{
						player.getInventory().decreaseKinah(15000000);
						return sendQuestDialog(env, 5);
					}
					return sendQuestDialog(env, 3739);
				}
				else
				{
					return sendQuestEndDialog(env);
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
			final int var = qs.getQuestVarById(0);
			if (var == 0)
			{
				return defaultOnKillEvent(env, 296489, 0, 1);
			}
			else if (var == 1)
			{
				return defaultOnKillEvent(env, 296490, 1, 2);
			}
			else if (var == 2)
			{
				return defaultOnKillEvent(env, 296491, 2, true);
			}
		}
		return false;
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
		}
		return HandlerResult.FAILED;
	}
}