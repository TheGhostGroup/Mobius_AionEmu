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
package system.handlers.quest.event_quests;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.rewards.BonusType;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.events.EventsService;

/**
 * @author Rolandas
 */
public class _80018EventSockItToEm extends QuestHandler
{
	private static final int questId = 80018;
	
	public _80018EventSockItToEm()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(799778).addOnQuestStart(questId);
		qe.registerQuestNpc(799778).addOnTalkEvent(questId);
		qe.registerOnBonusApply(questId, BonusType.MOVIE);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (((qs == null) || (qs.getStatus() == QuestStatus.NONE)) && !onLvlUpEvent(env))
		{
			return false;
		}
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || ((qs.getStatus() == QuestStatus.COMPLETE) && (qs.getCompleteCount() < 10)))
		{
			if (env.getTargetId() == 799778)
			{
				switch (env.getDialog())
				{
					case USE_OBJECT:
					{
						return sendQuestDialog(env, 1011);
					}
					case ACCEPT_QUEST:
					{
						QuestService.startEventQuest(env, QuestStatus.START);
						return sendQuestDialog(env, 1003);
					}
					default:
					{
						return sendQuestStartDialog(env);
					}
				}
			}
			return false;
		}
		
		final int var = qs.getQuestVarById(0);
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if (env.getTargetId() == 799778)
			{
				switch (env.getDialog())
				{
					case USE_OBJECT:
					case START_DIALOG:
					{
						if (var == 0)
						{
							return sendQuestDialog(env, 2375);
						}
					}
					case CHECK_COLLECTED_ITEMS:
					{
						return checkQuestItems(env, 0, 1, true, 5, 2716);
					}
				}
			}
		}
		
		return sendQuestRewardDialog(env, 799778, 0);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (EventsService.getInstance().checkQuestIsActive(questId))
		{
			if (!QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel()))
			{
				return false;
			}
			
			// Start once
			if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
			{
				return QuestService.startEventQuest(env, QuestStatus.START);
			}
		}
		else if (qs != null)
		{
			// Set as expired
			QuestService.abandonQuest(player, questId);
		}
		return false;
	}
	
	@Override
	public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems)
	{
		if ((bonusType != BonusType.MOVIE) || (env.getQuestId() != questId))
		{
			return HandlerResult.UNKNOWN;
		}
		
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.REWARD))
		{
			if (qs.getCompleteCount() == 9) // [Event] Hat Box
			{
				rewardItems.add(new QuestItems(188051106, 1));
			}
			// randomize movie
			if ((Rnd.get() * 100) < 50)
			{
				playQuestMovie(env, 135);
			}
			else
			{
				playQuestMovie(env, 136);
			}
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.FAILED;
	}
	
}
