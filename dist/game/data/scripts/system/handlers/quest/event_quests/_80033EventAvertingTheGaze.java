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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.events.EventsService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Rolandas
 */
public class _80033EventAvertingTheGaze extends QuestHandler
{
	private static final int questId = 80033;
	
	public _80033EventAvertingTheGaze()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(799781).addOnQuestStart(questId);
		qe.registerQuestNpc(799781).addOnTalkEvent(questId);
		qe.registerQuestItem(188051133, questId); // [Event] Charm Card
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 799781)
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 1011);
				}
				else if (env.getDialog() == QuestDialog.ACCEPT_QUEST)
				{
					final Storage storage = player.getInventory();
					if (storage.getItemCountByItemId(164002015) > 0)
					{
						return sendQuestDialog(env, 2375);
					}
					return sendQuestDialog(env, 2716);
				}
				else if (env.getDialog() == QuestDialog.SELECT_REWARD)
				{
					if (qs.getQuestVarById(0) == 0)
					{
						defaultCloseDialog(env, 0, 1, true, true, 0, 0, 164002015, 1);
					}
					return sendQuestDialog(env, 5);
				}
				else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD)
				{
					return sendQuestRewardDialog(env, 799781, 5);
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		return sendQuestRewardDialog(env, 799781, 0);
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item)
	{
		// check if the parent quest is active (you get Charm Cards)
		if (!EventsService.getInstance().checkQuestIsActive(80032))
		{
			return HandlerResult.FAILED;
		}
		
		final Player player = env.getPlayer();
		
		// the same item registered for elyos quests, return UNKNOWN for them
		// to not start asmodians quests
		if ((item.getItemId() == 188051133) && player.getCommonData().getRace().equals(Race.ASMODIANS))
		{
			ThreadPoolManager.getInstance().schedule(() ->
			{
				final Storage storage = player.getInventory();
				QuestStatus status = QuestStatus.NONE;
				
				if (storage.getItemCountByItemId(164002015) > 0)
				{
					status = getQuestUpdateStatus(player, questId);
					// got a Beritra's Gaze, then start me
					QuestService.startEventQuest(new QuestEnv(null, player, questId, 0), status);
				}
				if (storage.getItemCountByItemId(164002016) > 9) // Israphel's Glory
				{
					status = getQuestUpdateStatus(player, 80037);
					QuestService.startEventQuest(new QuestEnv(null, player, 80037, 0), status);
				}
				if (storage.getItemCountByItemId(164002017) > 4) // Siel's Gift
				{
					status = getQuestUpdateStatus(player, 80038);
					QuestService.startEventQuest(new QuestEnv(null, player, 80038, 0), status);
				}
				if (storage.getItemCountByItemId(164002018) > 0) // Aion's Grace
				{
					status = getQuestUpdateStatus(player, 80039);
					QuestService.startEventQuest(new QuestEnv(null, player, 80039, 0), status);
				}
			}, 10000);
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.UNKNOWN;
	}
	
	final QuestStatus getQuestUpdateStatus(Player player, int questid)
	{
		final QuestState qs = player.getQuestStateList().getQuestState(questid);
		QuestStatus status = qs == null ? QuestStatus.START : qs.getStatus();
		
		if ((qs != null) && (questid != questId) && (status == QuestStatus.COMPLETE))
		{
			status = QuestStatus.START;
		}
		return status;
	}
	
}
