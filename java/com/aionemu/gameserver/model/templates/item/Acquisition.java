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
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Acquisition")
public class Acquisition
{
	@XmlAttribute(name = "ap", required = false)
	private int ap;
	
	@XmlAttribute(name = "count", required = false)
	private int itemCount;
	
	@XmlAttribute(name = "item", required = false)
	private int itemId;
	
	@XmlAttribute(name = "type", required = true)
	private AcquisitionType type;
	
	public AcquisitionType getType()
	{
		return type;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public int getItemCount()
	{
		return itemCount;
	}
	
	public int getRequiredAp()
	{
		return ap;
	}
}
