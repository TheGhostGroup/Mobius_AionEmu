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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author kosyachok
 * @author -Nemesiss-
 */
public class SM_WAREHOUSE_UPDATE_ITEM extends AionServerPacket
{
	private final Player player;
	private final Item item;
	private final int warehouseType;
	private final ItemUpdateType updateType;
	
	public SM_WAREHOUSE_UPDATE_ITEM(Player player, Item item, int warehouseType, ItemUpdateType updateType)
	{
		this.player = player;
		this.item = item;
		this.warehouseType = warehouseType;
		this.updateType = updateType;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final ItemTemplate itemTemplate = item.getItemTemplate();
		
		writeD(item.getObjectId());
		writeC(warehouseType);
		writeNameId(itemTemplate.getNameId());
		
		final ItemInfoBlob itemInfoBlob = new ItemInfoBlob(player, item);
		itemInfoBlob.addBlobEntry(ItemBlobType.GENERAL_INFO);
		itemInfoBlob.writeMe(getBuf());
		
		if (updateType.isSendable())
		{
			writeH(updateType.getMask());
		}
	}
}
