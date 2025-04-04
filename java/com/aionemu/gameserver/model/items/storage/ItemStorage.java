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

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author KID
 */
public class ItemStorage
{
	public static final long FIRST_AVAILABLE_SLOT = 65535L;
	
	private final FastMap<Integer, Item> items;
	private int limit;
	private final int specialLimit;
	
	public ItemStorage(StorageType storageType)
	{
		limit = storageType.getLimit();
		specialLimit = storageType.getSpecialLimit();
		items = FastMap.newInstance();
	}
	
	public FastList<Item> getItems()
	{
		final FastList<Item> temp = FastList.newInstance();
		temp.addAll(items.values());
		return temp;
	}
	
	public int getLimit()
	{
		return limit;
	}
	
	public boolean setLimit(int limit)
	{
		if (getCubeItems().size() > limit)
		{
			return false;
		}
		
		this.limit = limit;
		return true;
	}
	
	public Item getFirstItemById(int itemId)
	{
		for (Item item : items.values())
		{
			if (item.getItemTemplate().getTemplateId() == itemId)
			{
				return item;
			}
		}
		return null;
	}
	
	public FastList<Item> getItemsById(int itemId)
	{
		final FastList<Item> temp = FastList.newInstance();
		for (Item item : items.values())
		{
			if (item.getItemTemplate().getTemplateId() == itemId)
			{
				temp.add(item);
			}
		}
		return temp;
	}
	
	public Item getItemByObjId(int itemObjId)
	{
		return items.get(itemObjId);
	}
	
	public long getSlotIdByItemId(int itemId)
	{
		for (Item item : items.values())
		{
			if (item.getItemTemplate().getTemplateId() == itemId)
			{
				return item.getEquipmentSlot();
			}
		}
		return -1;
	}
	
	public Item getItemBySlotId(short slotId)
	{
		for (Item item : getCubeItems())
		{
			if (item.getEquipmentSlot() == slotId)
			{
				return item;
			}
		}
		return null;
	}
	
	public Item getSpecialItemBySlotId(short slotId)
	{
		for (Item item : getSpecialCubeItems())
		{
			if (item.getEquipmentSlot() == slotId)
			{
				return item;
			}
		}
		return null;
	}
	
	public long getSlotIdByObjId(int objId)
	{
		final Item item = getItemByObjId(objId);
		if (item != null)
		{
			return item.getEquipmentSlot();
		}
		return -1;
	}
	
	public long getNextAvailableSlot()
	{
		return FIRST_AVAILABLE_SLOT;
	}
	
	public boolean putItem(Item item)
	{
		if (items.containsKey(item.getObjectId()))
		{
			return false;
		}
		items.put(item.getObjectId(), item);
		return true;
	}
	
	public Item removeItem(int objId)
	{
		return items.remove(objId);
	}
	
	public boolean isFull()
	{
		return getCubeItems().size() >= limit;
	}
	
	public boolean isFullSpecialCube()
	{
		return getSpecialCubeItems().size() >= specialLimit;
	}
	
	public List<Item> getSpecialCubeItems()
	{
		return select(items.values(), having(on(Item.class).getItemTemplate().getExtraInventoryId(), greaterThan(0)));
	}
	
	public List<Item> getCubeItems()
	{
		return select(items.values(), having(on(Item.class).getItemTemplate().getExtraInventoryId(), lessThan(1)));
	}
	
	public int getFreeSlots()
	{
		return limit - getCubeItems().size();
	}
	
	public int getSpecialCubeFreeSlots()
	{
		return specialLimit - getSpecialCubeItems().size();
	}
	
	public int size()
	{
		return items.size();
	}
}