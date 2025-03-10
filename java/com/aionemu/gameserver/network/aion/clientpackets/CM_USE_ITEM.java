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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.ArrayList;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.HouseDyeAction;
import com.aionemu.gameserver.model.templates.item.actions.InstanceTimeClear;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.item.actions.MultiReturnAction;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Avol
 * @author GiGatR00n v4.7.5.x
 */
public class CM_USE_ITEM extends AionClientPacket
{
	public int uniqueItemId;
	public int type, targetItemId, syncId, returnId;
	
	public CM_USE_ITEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		uniqueItemId = readD();
		type = readC();
		if (type == 2)
		{
			targetItemId = readD();
		}
		else if (type == 5)
		{
			syncId = readD();
		}
		else if (type == 6)
		{
			returnId = readD();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		/**
		 * 5.0 ITEM_USE Cancel System
		 */
		if (type == 0)
		{
			if (player.getController().hasTask(TaskId.ITEM_USE))
			{
				player.getController().cancelUseItem();
				return;
			}
		}
		if (player.isProtectionActive())
		{
			player.getController().stopProtectionActiveTask();
		}
		final Item item = player.getInventory().getItemByObjId(uniqueItemId);
		Item targetItem = player.getInventory().getItemByObjId(targetItemId);
		HouseObject<?> targetHouseObject = null;
		if (item == null)
		{
			return;
		}
		if (targetItem == null)
		{
			targetItem = player.getEquipment().getEquippedItemByObjId(targetItemId);
		}
		if ((targetItem == null) && (player.getHouseRegistry() != null))
		{
			targetHouseObject = player.getHouseRegistry().getObjectByObjId(targetItemId);
		}
		if ((item.getItemTemplate().getTemplateId() == 165000001) && ((targetItem != null) && targetItem.getItemTemplate().canExtract()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return;
		}
		// check use item multicast delay exploit cast (spam)
		if (player.isCasting())
		{
			player.getController().cancelCurrentSkill();
		}
		if (!RestrictionsManager.canUseItem(player, item))
		{
			return;
		}
		if ((item.getItemTemplate().getRace() != Race.PC_ALL) && (item.getItemTemplate().getRace() != player.getRace()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_RACE);
			return;
		}
		final int requiredLevel = item.getItemTemplate().getRequiredLevel(player.getCommonData().getPlayerClass());
		if (requiredLevel == -1)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_CLASS);
			return;
		}
		if (requiredLevel > player.getLevel())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(item.getNameId(), requiredLevel));
			return;
		}
		final HandlerResult result = QuestEngine.getInstance().onItemUseEvent(new QuestEnv(null, player, 0, 0), item);
		if (result == HandlerResult.FAILED)
		{
			return;
		}
		final ItemActions itemActions = item.getItemTemplate().getActions();
		final ArrayList<AbstractItemAction> actions = new ArrayList<>();
		if (itemActions == null)
		{
			return;
		}
		for (AbstractItemAction itemAction : itemActions.getItemActions())
		{
			// check if the item can be used before placing it on the cooldown list.
			if ((targetHouseObject != null) && (itemAction instanceof HouseDyeAction))
			{
				final HouseDyeAction action = (HouseDyeAction) itemAction;
				if (action.canAct(player, item, targetHouseObject))
				{
					actions.add(itemAction);
				}
			}
			else if (itemAction.canAct(player, item, targetItem))
			{
				actions.add(itemAction);
			}
		}
		if (actions.size() == 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_IS_NOT_USABLE);
			return;
		}
		// Store Item CD in server Player variable.
		// Prevents potion spamming, and relogging to use kisks/aether jelly/long CD items.
		if (player.isItemUseDisabled(item.getItemTemplate().getUseLimits()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANT_USE_UNTIL_DELAY_TIME);
			return;
		}
		final int useDelay = player.getItemCooldown(item.getItemTemplate());
		if (useDelay > 0)
		{
			player.addItemCoolDown(item.getItemTemplate().getUseLimits().getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		}
		// notify item use observer
		player.getObserveController().notifyItemuseObservers(item);
		for (AbstractItemAction itemAction : actions)
		{
			if ((targetHouseObject != null) && (itemAction instanceof HouseDyeAction))
			{
				final HouseDyeAction action = (HouseDyeAction) itemAction;
				action.act(player, item, targetHouseObject);
			}
			else if (type == 5)
			{
				if (itemAction instanceof InstanceTimeClear)
				{
					final InstanceTimeClear action = (InstanceTimeClear) itemAction;
					final int SelectedSyncId = syncId;
					action.act(player, item, SelectedSyncId);
				}
			}
			else if (type == 6)
			{
				if (itemAction instanceof MultiReturnAction)
				{
					final MultiReturnAction action = (MultiReturnAction) itemAction;
					final int SelectedMapIndex = returnId;
					action.act(player, item, SelectedMapIndex);
				}
			}
			else
			{
				itemAction.act(player, item, targetItem);
			}
		}
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(item.getItemTemplate().getNameId())));
	}
}