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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuardRankRestriction", propOrder =
{
	"guardpenaltyAttr"
})
public class GuardRankRestriction
{
	@XmlElement(name = "guard_penalty_attr")
	protected List<GuardRankPenaltyAttr> guardpenaltyAttr;
	
	@XmlAttribute(name = "rank_num", required = true)
	protected int rankNum;
	
	public List<GuardRankPenaltyAttr> getGuardPenaltyAttr()
	{
		if (guardpenaltyAttr == null)
		{
			guardpenaltyAttr = new ArrayList<>();
		}
		return guardpenaltyAttr;
	}
	
	public int getRankNum()
	{
		return rankNum;
	}
	
	public void setRankNum(int value)
	{
		rankNum = value;
	}
}