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
package com.aionemu.gameserver.model.items;

import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class ManaStone extends ItemStone
{
	private List<StatFunction> modifiers;
	
	public ManaStone(int itemObjId, int itemId, int slot, PersistentState persistentState)
	{
		super(itemObjId, itemId, slot, persistentState);
		final ItemTemplate stoneTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if ((stoneTemplate != null) && (stoneTemplate.getModifiers() != null))
		{
			modifiers = stoneTemplate.getModifiers();
		}
	}
	
	public List<StatFunction> getModifiers()
	{
		return modifiers;
	}
	
	public StatFunction getFirstModifier()
	{
		return ((modifiers != null) && (modifiers.size() > 0)) ? modifiers.get(0) : null;
	}
	
	public boolean isBasic()
	{
		return !isAncient();
	}
	
	public boolean isAncient()
	{
		return (getItemId() >= 167020006) && // Ancient Manastone: HP +105
			(getItemId() <= 167020112); // [Event] Ancient Manastone: Healing Boost +5
	}
}