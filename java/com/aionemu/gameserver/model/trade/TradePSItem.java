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
package com.aionemu.gameserver.model.trade;

/**
 * @author Simple
 */
public class TradePSItem extends TradeItem
{
	private int itemObjId;
	private long price;
	
	/**
	 * @param itemObjId
	 * @param itemId
	 * @param count
	 * @param price
	 */
	public TradePSItem(int itemObjId, int itemId, long count, long price)
	{
		super(itemId, count);
		setPrice(price);
		setItemObjId(itemObjId);
	}
	
	/**
	 * @param price the price to set
	 */
	public void setPrice(long price)
	{
		this.price = price;
	}
	
	/**
	 * @return the price
	 */
	public long getPrice()
	{
		return price;
	}
	
	/**
	 * @param itemObjId the itemObjId to set
	 */
	public void setItemObjId(int itemObjId)
	{
		this.itemObjId = itemObjId;
	}
	
	/**
	 * @return the itemObjId
	 */
	public int getItemObjId()
	{
		return itemObjId;
	}
	
}
