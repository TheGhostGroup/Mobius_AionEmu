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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.sql.Timestamp;
import java.util.Calendar;

import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * @author IlBuono, kosyachok
 * @author GiGatR00n
 */
public class SM_BROKER_SERVICE extends AionServerPacket
{
	private enum BrokerPacketType
	{
		SEARCHED_ITEMS(0),
		REGISTERED_ITEMS(1),
		REGISTER_ITEM(3),
		SHOW_SETTLED_ICON(5),
		SETTLED_ITEMS(5),
		REMOVE_SETTLED_ICON(6),
		AVE_LOW_HIGH_ITEM(7);
		
		private int id;
		
		private BrokerPacketType(int id)
		{
			this.id = id;
		}
		
		int getId()
		{
			return id;
		}
	}
	
	private final BrokerPacketType type;
	private BrokerItem[] brokerItems;
	private int itemsCount;
	private int startPage;
	private int message;
	private long settled_kinah;
	private int itemUniqueId;
	private long Ave7day;
	private long CurrentLow;
	private long CurrentHigh;
	private boolean IsLowHighSame;
	
	public SM_BROKER_SERVICE(BrokerItem brokerItem, int message, int itemsCount)
	{
		type = BrokerPacketType.REGISTER_ITEM;
		brokerItems = new BrokerItem[]
		{
			brokerItem
		};
		this.message = message;
		this.itemsCount = itemsCount;
	}
	
	public SM_BROKER_SERVICE(int message)
	{
		type = BrokerPacketType.REGISTER_ITEM;
		this.message = message;
	}
	
	public SM_BROKER_SERVICE(BrokerItem[] brokerItems)
	{
		type = BrokerPacketType.REGISTERED_ITEMS;
		this.brokerItems = brokerItems;
	}
	
	public SM_BROKER_SERVICE(BrokerItem[] brokerItems, long settled_kinah)
	{
		type = BrokerPacketType.SETTLED_ITEMS;
		this.brokerItems = brokerItems;
		this.settled_kinah = settled_kinah;
	}
	
	public SM_BROKER_SERVICE(BrokerItem[] brokerItems, int itemsCount, int startPage)
	{
		type = BrokerPacketType.SEARCHED_ITEMS;
		this.brokerItems = brokerItems;
		this.itemsCount = itemsCount;
		this.startPage = startPage;
	}
	
	public SM_BROKER_SERVICE(boolean showSettledIcon, long settled_kinah)
	{
		type = showSettledIcon ? BrokerPacketType.SHOW_SETTLED_ICON : BrokerPacketType.REMOVE_SETTLED_ICON;
		this.settled_kinah = settled_kinah;
	}
	
	public SM_BROKER_SERVICE(int itemUniqueId, long Ave7day, long CurrentLow, long CurrentHigh, boolean IsLowHighSame)
	{
		type = BrokerPacketType.AVE_LOW_HIGH_ITEM;
		this.itemUniqueId = itemUniqueId;
		this.Ave7day = Ave7day;
		this.CurrentLow = CurrentLow;
		this.CurrentHigh = CurrentHigh;
		this.IsLowHighSame = IsLowHighSame;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		switch (type)
		{
			case SEARCHED_ITEMS:
			{
				writeSearchedItems();
				break;
			}
			case REGISTERED_ITEMS:
			{
				writeRegisteredItems();
				break;
			}
			case REGISTER_ITEM:
			{
				writeRegisterItem();
				break;
			}
			case SHOW_SETTLED_ICON:
			{
				writeShowSettledIcon();
				break;
			}
			case REMOVE_SETTLED_ICON:
			{
				writeRemoveSettledIcon();
				break;
			}
			case SETTLED_ITEMS:
			{
				writeShowSettledItems();
				break;
			}
			case AVE_LOW_HIGH_ITEM:
			{
				writeItemAveLowHigh();
				break;
			}
		}
	}
	
	private void writeItemAveLowHigh()
	{
		writeC(type.getId());
		writeC(0x00);
		writeD(itemUniqueId);
		writeQ(Ave7day);
		writeC(IsLowHighSame ? 0x02 : 0x00);
		writeQ(CurrentLow);
		writeQ(CurrentHigh);
	}
	
	private void writeSearchedItems()
	{
		writeC(type.getId());
		writeD(itemsCount);
		writeC(0);
		writeH(startPage);
		writeH(brokerItems.length);
		for (BrokerItem item : brokerItems)
		{
			writeItemInfo(item);
		}
	}
	
	private void writeRegisteredItems()
	{
		writeC(type.getId());
		writeD(0x00);
		writeH(brokerItems.length);
		for (BrokerItem brokerItem : brokerItems)
		{
			writeRegisteredItemInfo(brokerItem);
		}
	}
	
	private void writeRegisterItem()
	{
		writeC(type.getId());
		writeC(message);
		if (message == 0)
		{
			writeC(itemsCount + 1);
			final BrokerItem itemForRegistration = brokerItems[0];
			writeRegisteredItemInfo(itemForRegistration);
		}
		else
		{
			writeB(new byte[107]);
		}
	}
	
	private void writeShowSettledIcon()
	{
		writeC(type.getId());
		writeQ(settled_kinah);
		writeD(0x00);
		writeH(0x00);
		writeH(0x01);
		writeC(0x00);
	}
	
	private void writeRemoveSettledIcon()
	{
		writeH(type.getId());
	}
	
	private void writeShowSettledItems()
	{
		writeC(type.getId());
		writeQ(settled_kinah);
		writeH(brokerItems.length);
		writeD(0x00);
		writeC(0x00);
		writeH(brokerItems.length);
		for (BrokerItem settledItem : brokerItems)
		{
			writeD(settledItem.getItemId());
			if (settledItem.isSold())
			{
				writeQ(settledItem.getPrice());
			}
			else
			{
				writeQ(0);
			}
			writeQ(settledItem.getItemCount());
			writeQ(settledItem.getItemCount());
			writeD((int) ((settledItem.getSettleTime().getTime() / 60000) & 0xffffffffl));
			final Item item = settledItem.getItem();
			if (item != null)
			{
				ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
			}
			else
			{
				writeB(new byte[138]);
			}
			writeS(settledItem.getItemCreator());
		}
	}
	
	private void writeRegisteredItemInfo(BrokerItem brokerItem)
	{
		final Item item = brokerItem.getItem();
		writeD(brokerItem.getItemUniqueId());
		writeD(brokerItem.getItemId());
		writeQ(brokerItem.getPrice());
		writeQ(item.getItemCount());
		writeQ(item.getItemCount());
		final Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		final int daysLeft = (int) ((brokerItem.getExpireTime().getTime() - currentTime.getTime()) / 86400000);
		writeC(daysLeft);
		ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
		writeS(brokerItem.getItemCreator());
		ItemInfoBlob.newBlobEntry(ItemBlobType.PREMIUM_OPTION, null, item).writeThisBlob(getBuf());
		ItemInfoBlob.newBlobEntry(ItemBlobType.IDIAN_INFO, null, item).writeThisBlob(getBuf());
		writeC(0x00);
		writeC(brokerItem.isSplitSell() ? 0x01 : 0x00);
	}
	
	private void writeItemInfo(BrokerItem brokerItem)
	{
		final Item item = brokerItem.getItem();
		writeD(item.getObjectId());
		writeD(item.getItemTemplate().getTemplateId());
		writeQ(brokerItem.getPrice());
		writeQ(brokerItem.getPrice());
		writeQ(item.getItemCount());
		ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
		writeS(brokerItem.getSeller());
		writeS(brokerItem.getItemCreator());
		ItemInfoBlob.newBlobEntry(ItemBlobType.PREMIUM_OPTION, null, item).writeThisBlob(getBuf());
		ItemInfoBlob.newBlobEntry(ItemBlobType.IDIAN_INFO, null, item).writeThisBlob(getBuf());
		writeC(0x00);
		writeC(brokerItem.isSplitSell() ? 0x01 : 0x00);
	}
}