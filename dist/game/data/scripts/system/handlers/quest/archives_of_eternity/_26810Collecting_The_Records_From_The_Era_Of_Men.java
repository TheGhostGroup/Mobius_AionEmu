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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rinzler (Encom)
 */
public class _26810Collecting_The_Records_From_The_Era_Of_Men extends QuestHandler
{
	private static final int questId = 26810;
	
	public _26810Collecting_The_Records_From_The_Era_Of_Men()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(806079).addOnTalkEvent(questId);
		qe.registerQuestItem(182215990, questId);
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (QuestService.startQuest(env))
			{
				changeQuestStep(env, 0, 0, true);
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		return HandlerResult.UNKNOWN;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int targetId = env.getTargetId();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			return false;
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 806079)
			{
				removeQuestItem(env, 182215990, 1);
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}