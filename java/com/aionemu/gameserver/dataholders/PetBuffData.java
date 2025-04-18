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

import com.aionemu.gameserver.model.templates.pet.PetBonusAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"petBonusattr"
})
@XmlRootElement(name = "pet_bonusattrs")
public class PetBuffData
{
	@XmlElement(name = "pet_bonusattr")
	protected List<PetBonusAttr> petBonusattr;
	
	@XmlTransient
	private final TIntObjectHashMap<PetBonusAttr> templates = new TIntObjectHashMap<>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PetBonusAttr template : petBonusattr)
		{
			templates.put(template.getBuffId(), template);
			templates.put(template.getFoodCount(), template);
		}
		petBonusattr.clear();
		petBonusattr = null;
	}
	
	public int size()
	{
		return templates.size();
	}
	
	public PetBonusAttr getPetBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
	
	public PetBonusAttr getFoodCount(int count)
	{
		return templates.get(count);
	}
}
