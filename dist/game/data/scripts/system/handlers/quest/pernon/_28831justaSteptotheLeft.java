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
package system.handlers.quest.pernon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _28831justaSteptotheLeft extends QuestHandler
{
	private static final int questId = 28831;
	private static final Set<Integer> butlers;
	
	static
	{
		butlers = new HashSet<>();
		butlers.add(810022);
		butlers.add(810023);
		butlers.add(810024);
		butlers.add(810025);
		butlers.add(810026);
	}
	
	public _28831justaSteptotheLeft()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(830651).addOnQuestStart(questId);
		final Iterator<Integer> iter = butlers.iterator();
		while (iter.hasNext())
		{
			final int butlerId = iter.next();
			qe.registerQuestNpc(butlerId).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestDialog dialog = env.getDialog();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 830651)
			{
				switch (dialog)
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 1011);
					}
					case ACCEPT_QUEST_SIMPLE:
					{
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if ((qs.getStatus() == QuestStatus.START) && butlers.contains(targetId))
		{
			final House house = player.getActiveHouse();
			if (house.getButler().getNpcId() != targetId)
			{
				return false;
			}
			switch (dialog)
			{
				case USE_OBJECT:
				{
					return sendQuestDialog(env, 2375);
				}
				case SELECT_REWARD:
				{
					changeQuestStep(env, 0, 0, true);
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if ((qs.getStatus() == QuestStatus.REWARD) && butlers.contains(targetId))
		{
			return sendQuestEndDialog(env);
		}
		
		return false;
	}
	
}
