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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ItemEnchantBouns")
public class ItemEnchantBonus
{
	@XmlElement(name = "modifiers", required = false)
	private ModifiersTemplate modifiers;
	@XmlAttribute(name = "level")
	private int level;
	
	public ItemEnchantBonus()
	{
	}
	
	public ModifiersTemplate getModifiers()
	{
		return modifiers;
	}
	
	public int getLevel()
	{
		return level;
	}
}