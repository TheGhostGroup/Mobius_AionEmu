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
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.bonus_service.BonusServiceAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Ranastic (Encom)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"serviceBonusattr"
})
@XmlRootElement(name = "service_bonusattrs")
public class ServiceBuffData
{
	@XmlElement(name = "service_bonusattr")
	protected List<BonusServiceAttr> serviceBonusattr;
	
	@XmlTransient
	private final TIntObjectHashMap<BonusServiceAttr> templates = new TIntObjectHashMap<>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (BonusServiceAttr template : serviceBonusattr)
		{
			templates.put(template.getBuffId(), template);
		}
		serviceBonusattr.clear();
		serviceBonusattr = null;
	}
	
	public int size()
	{
		return templates.size();
	}
	
	public BonusServiceAttr getInstanceBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
}