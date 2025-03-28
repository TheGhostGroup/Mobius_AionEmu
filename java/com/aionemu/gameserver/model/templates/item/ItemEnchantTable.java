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
package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEnchantTable")
public class ItemEnchantTable
{
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "type")
	private String type;
	@XmlAttribute(name = "part")
	private String part;
	@XmlElement(name = "item_enchant", required = false)
	private List<ItemEnchantBonus> item_enchant;
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	@XmlTransient
	private final TIntObjectHashMap<List<StatFunction>> enchants = new TIntObjectHashMap();
	
	@SuppressWarnings("unchecked")
	public List<StatFunction> getStats(int level)
	{
		for (ItemEnchantBonus ib : getItemEnchant())
		{
			if (ib.getLevel() != level)
			{
				continue;
			}
			
			return (List<StatFunction>) ib.getModifiers();
		}
		return null;
	}
	
	public List<ItemEnchantBonus> getItemEnchant()
	{
		return item_enchant;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getPart()
	{
		return part;
	}
	
}
