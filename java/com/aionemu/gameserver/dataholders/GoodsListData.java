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

import com.aionemu.gameserver.model.templates.goods.GoodsList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author ATracer
 */
@XmlRootElement(name = "goodslists")
@XmlAccessorType(XmlAccessType.FIELD)
public class GoodsListData
{
	@XmlElement(required = true)
	protected List<GoodsList> list;
	
	@XmlElement(name = "in_list")
	protected List<GoodsList> inList;
	
	@XmlElement(name = "purchase_list")
	protected List<GoodsList> pList;
	
	private TIntObjectHashMap<GoodsList> goodsListData;
	private TIntObjectHashMap<GoodsList> goodsInListData;
	private TIntObjectHashMap<GoodsList> goodsPurchaseListData;
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		goodsListData = new TIntObjectHashMap<>();
		for (GoodsList it : list)
		{
			goodsListData.put(it.getId(), it);
		}
		goodsInListData = new TIntObjectHashMap<>();
		for (GoodsList it : inList)
		{
			goodsInListData.put(it.getId(), it);
		}
		goodsPurchaseListData = new TIntObjectHashMap<>();
		for (GoodsList it : pList)
		{
			goodsPurchaseListData.put(it.getId(), it);
		}
		list = null;
		inList = null;
		pList = null;
	}
	
	public GoodsList getGoodsListById(int id)
	{
		return goodsListData.get(id);
	}
	
	public GoodsList getGoodsInListById(int id)
	{
		return goodsInListData.get(id);
	}
	
	public GoodsList getGoodsPurchaseListById(int id)
	{
		return goodsPurchaseListData.get(id);
	}
	
	public int size()
	{
		return goodsListData.size() + goodsInListData.size() + goodsPurchaseListData.size();
	}
}