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

import com.aionemu.gameserver.model.templates.bonus_service.F2pBonusAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Created by wanke on 12/02/2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"f2pBonusattr"
})
@XmlRootElement(name = "f2p_bonus")
public class F2PBonusData
{
	@XmlElement(name = "f2p")
	protected List<F2pBonusAttr> f2pBonusattr;
	
	@XmlTransient
	private final TIntObjectHashMap<F2pBonusAttr> templates = new TIntObjectHashMap<>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (F2pBonusAttr template : f2pBonusattr)
		{
			templates.put(template.getBuffId(), template);
		}
		f2pBonusattr.clear();
		f2pBonusattr = null;
	}
	
	public int size()
	{
		return templates.size();
	}
	
	public F2pBonusAttr getInstanceBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
}
