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
package com.aionemu.gameserver.model.templates.serial_guard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.change.Func;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuardRankPenaltyAttr")
public class GuardRankPenaltyAttr
{
	@XmlAttribute(required = true)
	protected StatEnum stat;
	
	@XmlAttribute(required = true)
	protected Func func;
	
	@XmlAttribute(required = true)
	protected int value;
	
	public StatEnum getStat()
	{
		return stat;
	}
	
	public void setStat(StatEnum value)
	{
		stat = value;
	}
	
	public Func getFunc()
	{
		return func;
	}
	
	public void setFunc(Func value)
	{
		func = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
}