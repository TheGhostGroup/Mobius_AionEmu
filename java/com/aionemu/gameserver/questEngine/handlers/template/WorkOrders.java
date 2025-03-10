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
package com.aionemu.gameserver.questEngine.handlers.template;

import java.util.Iterator;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.WorkOrdersData;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.RecipeService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke reworked Bobobear
 */
public class WorkOrders extends QuestHandler
{
	private final WorkOrdersData workOrdersData;
	
	public WorkOrders(WorkOrdersData workOrdersData)
	{
		super(workOrdersData.getId());
		this.workOrdersData = workOrdersData;
	}
	
	@Override
	public void register()
	{
		final Iterator<Integer> iterator = workOrdersData.getStartNpcIds().iterator();
		while (iterator.hasNext())
		{
			final int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(workOrdersData.getId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(workOrdersData.getId());
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		if (workOrdersData.getStartNpcIds().contains(targetId))
		{
			final QuestState qs = player.getQuestStateList().getQuestState(workOrdersData.getId());
			if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 4);
					}
					case ACCEPT_QUEST:
					{
						if (RecipeService.validateNewRecipe(player, workOrdersData.getRecipeId()) != null)
						{
							if (QuestService.startQuest(env))
							{
								if (ItemService.addQuestItems(player, workOrdersData.getGiveComponent()))
								{
									RecipeService.addRecipe(player, workOrdersData.getRecipeId(), false);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
								}
								return true;
							}
						}
					}
				}
			}
			else if (qs.getStatus() == QuestStatus.START)
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
				{
					final int var = qs.getQuestVarById(0);
					if (QuestService.collectItemCheck(env, false))
					{
						changeQuestStep(env, var, var, true);
						final QuestWorkItems qwi = DataManager.QUEST_DATA.getQuestById(workOrdersData.getId()).getQuestWorkItems();
						if (qwi != null)
						{
							long count = 0;
							for (QuestItems qi : qwi.getQuestWorkItem())
							{
								if (qi != null)
								{
									count = player.getInventory().getItemCountByItemId(qi.getItemId());
									if (count > 0)
									{
										player.getInventory().decreaseByItemId(qi.getItemId(), count);
									}
								}
							}
						}
						return sendQuestDialog(env, 5);
					}
					return sendQuestSelectionDialog(env);
				}
			}
			else if (qs.getStatus() == QuestStatus.REWARD)
			{
				final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(workOrdersData.getId());
				final CollectItems collectItems = template.getCollectItems();
				long count = 0;
				for (CollectItem collectItem : collectItems.getCollectItem())
				{
					count = player.getInventory().getItemCountByItemId(collectItem.getItemId());
					if (count > 0)
					{
						player.getInventory().decreaseByItemId(collectItem.getItemId(), count);
					}
				}
				player.getRecipeList().deleteRecipe(player, workOrdersData.getRecipeId());
				if (env.getDialogId() == -1)
				{
					QuestService.finishQuest(env, 0);
					env.setQuestId(workOrdersData.getId());
					return sendQuestDialog(env, 1008);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}