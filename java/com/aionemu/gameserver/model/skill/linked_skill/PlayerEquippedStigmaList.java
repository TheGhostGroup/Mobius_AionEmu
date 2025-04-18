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
package com.aionemu.gameserver.model.skill.linked_skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerStigmasEquippedDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Ranastic
 */
public final class PlayerEquippedStigmaList implements StigmaList<Player>
{
	private final Map<Integer, EquippedStigmasEntry> itemList;
	private final List<EquippedStigmasEntry> deletedItems;
	
	public PlayerEquippedStigmaList()
	{
		itemList = new HashMap<>(0);
		deletedItems = new ArrayList<>(0);
	}
	
	public PlayerEquippedStigmaList(List<EquippedStigmasEntry> items)
	{
		this();
		for (EquippedStigmasEntry entry : items)
		{
			itemList.put(entry.getItemId(), entry);
		}
	}
	
	public EquippedStigmasEntry[] getAllItems()
	{
		final List<EquippedStigmasEntry> allItems = new ArrayList<>();
		allItems.addAll(itemList.values());
		return allItems.toArray(new EquippedStigmasEntry[allItems.size()]);
	}
	
	public List<Integer> getAllItemsAsInteger()
	{
		final HashSet<Integer> equippedIds = new HashSet<>();
		for (EquippedStigmasEntry i : itemList.values())
		{
			equippedIds.add(i.getItemId());
		}
		return Arrays.asList(equippedIds.toArray(new Integer[0]));
	}
	
	public EquippedStigmasEntry[] getDeletedItems()
	{
		return deletedItems.toArray(new EquippedStigmasEntry[deletedItems.size()]);
	}
	
	@Override
	public boolean addItem(Player player, int itemId, String itemName)
	{
		return addItem(player, itemId, itemName, PersistentState.NEW);
	}
	
	private synchronized boolean addItem(Player player, int itemId, String itemName, PersistentState state)
	{
		itemList.put(itemId, new EquippedStigmasEntry(itemId, itemName, state));
		DAOManager.getDAO(PlayerStigmasEquippedDAO.class).storeItems(player);
		return true;
	}
	
	@Override
	public boolean remove(Player player, int itemId)
	{
		final EquippedStigmasEntry entry = itemList.get(itemId);
		if (entry != null)
		{
			entry.setPersistentState(PersistentState.DELETED);
			deletedItems.add(entry);
			if (player != null)
			{
				DAOManager.getDAO(PlayerStigmasEquippedDAO.class).storeItems(player);
			}
			itemList.remove(itemId);
		}
		return entry != null;
	}
	
	@Override
	public boolean isItemPresent(int itemId)
	{
		return itemList.containsKey(itemId);
	}
	
	@Override
	public int size()
	{
		return itemList.size();
	}
	
}
