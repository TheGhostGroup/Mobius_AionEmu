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
package system.handlers.quest.raksang_ruins;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Rinzler (Encom)
 */
public class _18745Steal_From_The_Stealers extends QuestHandler
{
	private static final int questId = 18745;
	
	public _18745Steal_From_The_Stealers()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(804707).addOnQuestStart(questId);
		qe.registerQuestNpc(804707).addOnTalkEvent(questId);
		qe.registerQuestNpc(702958).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("RAKSANG_ENTRANCE_300610000"), questId);
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
			if (targetId == 804707)
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
		}
		else if (targetId == 702958)
		{
			if (dialog == QuestDialog.USE_OBJECT)
			{
				closeDialogWindow(env);
				return true;
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 804707)
			{
				switch (dialog)
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 1011);
					}
					case CHECK_COLLECTED_ITEMS:
					{
						return checkQuestItems(env, 0, 0, true, 5, 2716);
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
			if (targetId == 804707)
			{
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (zoneName == ZoneName.get("RAKSANG_ENTRANCE_300610000"))
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
}