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
package system.handlers.quest.harbinger_landing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Rinzler (Encom)
 */
public class _25476Boost_Base_Accessibility extends QuestHandler
{
	private static final int questId = 25476;
	private static final Set<Integer> ab1BLv4D01;
	
	public _25476Boost_Base_Accessibility()
	{
		super(questId);
	}
	
	static
	{
		ab1BLv4D01 = new HashSet<>();
		ab1BLv4D01.add(805826);
		ab1BLv4D01.add(805827);
		ab1BLv4D01.add(805828);
	}
	
	@Override
	public void register()
	{
		final Iterator<Integer> iter = ab1BLv4D01.iterator();
		while (iter.hasNext())
		{
			final int ab1Id = iter.next();
			qe.registerQuestNpc(ab1Id).addOnQuestStart(questId);
			qe.registerQuestNpc(ab1Id).addOnTalkEvent(questId);
			qe.registerQuestNpc(883279).addOnKillEvent(questId);
			qe.registerQuestNpc(883280).addOnKillEvent(questId);
			qe.registerQuestNpc(883281).addOnKillEvent(questId);
			qe.registerQuestNpc(883282).addOnKillEvent(questId);
			qe.registerQuestNpc(883283).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final int targetId = env.getTargetId();
		final Player player = env.getPlayer();
		if (!ab1BLv4D01.contains(targetId))
		{
			return false;
		}
		final QuestDialog dialog = env.getDialog();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			switch (dialog)
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
		else if (qs.getStatus() == QuestStatus.START)
		{
			switch (dialog)
			{
				case START_DIALOG:
				{
					return sendQuestDialog(env, 2375);
				}
				case SELECT_REWARD:
				{
					changeQuestStep(env, 8, 9, true);
					return sendQuestEndDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (env.getDialog() == QuestDialog.START_DIALOG)
			{
				return sendQuestDialog(env, 10002);
			}
			return sendQuestEndDialog(env);
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
				case 883279:
				case 883280:
				case 883281:
				case 883282:
				case 883283:
				{
					if (qs.getQuestVarById(1) < 8)
					{
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
					}
					if (qs.getQuestVarById(1) >= 8)
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