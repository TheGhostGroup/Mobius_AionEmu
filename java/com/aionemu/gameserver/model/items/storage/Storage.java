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
package com.aionemu.gameserver.model.items.storage;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

import javolution.util.FastList;

/**
 * @author KID, ATracer
 */
public abstract class Storage implements IStorage
{
	private final ItemStorage itemStorage;
	private Item kinahItem;
	private final StorageType storageType;
	private Queue<Item> deletedItems;
	/**
	 * Can be of 2 types: UPDATED and UPDATE_REQUIRED
	 */
	private PersistentState persistentState = PersistentState.UPDATED;
	
	public Storage(StorageType storageType)
	{
		this(storageType, true);
	}
	
	public Storage(StorageType storageType, boolean withDeletedItems)
	{
		itemStorage = new ItemStorage(storageType);
		this.storageType = storageType;
		if (withDeletedItems)
		{
			deletedItems = new ConcurrentLinkedQueue<>();
		}
	}
	
	@Override
	public long getKinah()
	{
		return kinahItem == null ? 0 : kinahItem.getItemCount();
	}
	
	@Override
	public Item getKinahItem()
	{
		return kinahItem;
	}
	
	@Override
	public StorageType getStorageType()
	{
		return storageType;
	}
	
	void increaseKinah(long amount, Player actor)
	{
		increaseKinah(amount, ItemUpdateType.INC_KINAH_COLLECT, actor);
	}
	
	void increaseKinah(long amount, ItemUpdateType updateType, Player actor)
	{
		if (kinahItem == null)
		{
			add(ItemFactory.newItem(ItemId.KINAH.value(), 0), actor);
		}
		if (amount > 0)
		{
			increaseItemCount(kinahItem, amount, updateType, actor);
		}
	}
	
	/**
	 * Decrease kinah by {@code amount} but check first that its enough in storage
	 * @param amount
	 * @param actor
	 * @return true if decrease was successful
	 */
	boolean tryDecreaseKinah(long amount, Player actor)
	{
		if (getKinah() >= amount)
		{
			decreaseKinah(amount, actor);
			return true;
		}
		return false;
	}
	
	/**
	 * just decrease kinah without any checks
	 * @param amount
	 * @param actor
	 */
	void decreaseKinah(long amount, Player actor)
	{
		decreaseKinah(amount, ItemUpdateType.DEC_KINAH_BUY, actor);
	}
	
	void decreaseKinah(long amount, ItemUpdateType updateType, Player actor)
	{
		if (amount > 0)
		{
			decreaseItemCount(kinahItem, amount, updateType, actor);
		}
	}
	
	long increaseItemCount(Item item, long count, Player actor)
	{
		return increaseItemCount(item, count, ItemUpdateType.DEC_ITEM_USE, actor);
	}
	
	/**
	 * increase item count and return left count
	 * @param item
	 * @param count
	 * @param updateType
	 * @param actor
	 * @return
	 */
	long increaseItemCount(Item item, long count, ItemUpdateType updateType, Player actor)
	{
		final long leftCount = item.increaseItemCount(count);
		ItemPacketService.sendItemPacket(actor, storageType, item, updateType);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}
	
	long decreaseItemCount(Item item, long count, Player actor)
	{
		return decreaseItemCount(item, count, ItemUpdateType.DEC_ITEM_USE, actor);
	}
	
	/**
	 * decrease item count and return left count
	 * @param item
	 * @param count
	 * @param updateType
	 * @param actor
	 * @return
	 */
	long decreaseItemCount(Item item, long count, ItemUpdateType updateType, Player actor)
	{
		if (item == null)
		{
			return 0;
		}
		final long leftCount = item.decreaseItemCount(count);
		if ((item.getItemCount() <= 0) && !item.getItemTemplate().isKinah())
		{
			delete(item, ItemDeleteType.fromUpdateType(updateType), actor);
		}
		else
		{
			ItemPacketService.sendItemPacket(actor, storageType, item, updateType);
		}
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}
	
	/**
	 * This method should be called only for new items added to inventory (loading from DB) If item is equiped - will be put to equipment if item is unequiped - will be put to default bag for now Kinah is stored separately as it will be used frequently
	 * @param item
	 */
	@Override
	public void onLoadHandler(Item item)
	{
		if (item.getItemTemplate().isKinah())
		{
			kinahItem = item;
		}
		else
		{
			itemStorage.putItem(item);
		}
	}
	
	Item add(Item item, Player actor)
	{
		if (item.getItemTemplate().isKinah())
		{
			kinahItem = item;
		}
		else if (!itemStorage.putItem(item))
		{
			return null;
		}
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.sendStorageUpdatePacket(actor, storageType, item);
		// TODO: move to ItemService
		QuestEngine.getInstance().onItemGet(new QuestEnv(null, actor, 0, 0), item.getItemTemplate().getTemplateId());
		if (item.getItemTemplate().isQuestUpdateItem())
		{
			actor.getController().updateNearbyQuests();
		}
		return item;
	}
	
	// a bit misleading name - but looks like its used only for equipment
	Item put(Item item, Player actor)
	{
		if (!itemStorage.putItem(item))
		{
			return null;
		}
		item.setItemLocation(storageType.getId());
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		ItemPacketService.sendItemUpdatePacket(actor, storageType, item, ItemUpdateType.EQUIP_UNEQUIP);
		return item;
	}
	
	/**
	 * Remove item from storage without changing its state
	 */
	@Override
	public Item remove(Item item)
	{
		return itemStorage.removeItem(item.getObjectId());
	}
	
	/**
	 * Delete item from storage and mark for DB update. QUEST_REWARD delete type
	 * @param item
	 * @param actor
	 * @return
	 */
	Item delete(Item item, Player actor)
	{
		return delete(item, ItemDeleteType.QUEST_REWARD, actor);
	}
	
	/**
	 * Delete item from storage and mark for DB update
	 * @param item
	 * @param deleteType
	 * @param actor
	 * @return
	 */
	Item delete(Item item, ItemDeleteType deleteType, Player actor)
	{
		if (remove(item) != null)
		{
			item.setPersistentState(PersistentState.DELETED);
			deletedItems.add(item);
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			ItemPacketService.sendItemDeletePacket(actor, StorageType.getStorageTypeById(item.getItemLocation()), item, deleteType);
			if (item.getItemTemplate().isQuestUpdateItem())
			{
				actor.getController().updateNearbyQuests();
			}
			return item;
		}
		return null;
	}
	
	boolean decreaseByItemId(int itemId, long count, Player actor)
	{
		final FastList<Item> items = itemStorage.getItemsById(itemId);
		if (items.size() == 0)
		{
			return false;
		}
		
		for (Item item : items)
		{
			if (count == 0)
			{
				break;
			}
			count = decreaseItemCount(item, count, actor);
		}
		
		FastList.recycle(items);
		return count == 0;
	}
	
	boolean decreaseByObjectId(int itemObjId, long count, Player actor)
	{
		return decreaseByObjectId(itemObjId, count, ItemUpdateType.DEC_ITEM_USE, actor);
	}
	
	boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType, Player actor)
	{
		final Item item = itemStorage.getItemByObjId(itemObjId);
		if ((item == null) || (item.getItemCount() < count))
		{
			return false;
		}
		
		return decreaseItemCount(item, count, updateType, actor) == 0;
	}
	
	@Override
	public Item getFirstItemByItemId(int itemId)
	{
		return itemStorage.getFirstItemById(itemId);
	}
	
	@Override
	public FastList<Item> getItemsWithKinah()
	{
		final FastList<Item> items = itemStorage.getItems();
		if (kinahItem != null)
		{
			items.add(kinahItem);
		}
		return items;
	}
	
	@Override
	public List<Item> getItems()
	{
		return itemStorage.getItems();
	}
	
	@Override
	public List<Item> getItemsByItemId(int itemId)
	{
		return itemStorage.getItemsById(itemId);
	}
	
	@Override
	public Queue<Item> getDeletedItems()
	{
		return deletedItems;
	}
	
	@Override
	public Item getItemByObjId(int itemObjId)
	{
		return itemStorage.getItemByObjId(itemObjId);
	}
	
	@Override
	public long getItemCountByItemId(int itemId)
	{
		final FastList<Item> temp = itemStorage.getItemsById(itemId);
		if (temp.size() == 0)
		{
			return 0;
		}
		
		long cnt = 0;
		for (Item item : temp)
		{
			cnt += item.getItemCount();
		}
		
		return cnt;
	}
	
	@Override
	public boolean isFull()
	{
		return itemStorage.isFull();
	}
	
	public boolean isFullSpecialCube()
	{
		return itemStorage.isFullSpecialCube();
	}
	
	public boolean isFull(int inventory)
	{
		if (inventory > 0)
		{
			return isFullSpecialCube();
		}
		return isFull();
	}
	
	public int getFreeSlots(int inventory)
	{
		if (inventory > 0)
		{
			return getSpecialCubeFreeSlots();
		}
		return getFreeSlots();
	}
	
	public int getSpecialCubeFreeSlots()
	{
		return itemStorage.getSpecialCubeFreeSlots();
	}
	
	@Override
	public int getFreeSlots()
	{
		return itemStorage.getFreeSlots();
	}
	
	public boolean setLimit(int limit)
	{
		return itemStorage.setLimit(limit);
	}
	
	@Override
	public int getLimit()
	{
		return itemStorage.getLimit();
	}
	
	@Override
	public final PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	@Override
	public final void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	@Override
	public int size()
	{
		return itemStorage.size();
	}
	
	public void clear()
	{
		for (Item i : itemStorage.getItems())
		{
			remove(i);
		}
	}
	
}