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
package com.aionemu.gameserver.model.templates.bonus_service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by wanke on 12/02/2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "F2pBonusAttr", propOrder =
{
	"bonusAttr"
})
public class F2pBonusAttr
{
	@XmlElement(name = "bonus_attr")
	protected List<F2pPenalityAttr> bonusAttr;
	
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	public List<F2pPenalityAttr> getPenaltyAttr()
	{
		if (bonusAttr == null)
		{
			bonusAttr = new ArrayList<>();
		}
		return bonusAttr;
	}
	
	public int getBuffId()
	{
		return buffId;
	}
	
	public void setBuffId(int value)
	{
		buffId = value;
	}
}