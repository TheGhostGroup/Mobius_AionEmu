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
package com.aionemu.gameserver.services.item;

import java.util.Collections;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_WAREHOUSE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_ADD_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_ADD_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_UPDATE_ITEM;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * TODO: <br>
 * 0x01 0000 0001 increase count by merge<br>
 * 0x06 0000 0110 decrease count after split, equip<br>
 * 0x16 0001 0110 decrease count by use item<br>
 * 0x19 0001 1001 increase count by looting<br>
 * 0x1A 0001 1010 increase kinah by loot<br>
 * 0x1D 0001 1101 decrease kinah<br>
 * 0x32 0011 0010 increase kinah by quest<br>
 * @author ATracer
 */
public class ItemPacketService
{
	public static enum ItemUpdateType
	{
		EQUIP_UNEQUIP(-1, false), // internal usage only
		CHARGE(-2, false), // internal usage only
		STATS_CHANGE(0, true), // soul healer pay, manastone socketing, armor/weapons/arrows
		INC_ITEM_MERGE(0x01, true),
		INC_KINAH_MERGE(0x05, true),
		DEC_ITEM_SPLIT(0x06, true),
		DEC_ITEM_SPLIT_MOVE(0x0A, true), // move to other storage with split
		DEC_ITEM_USE(0x16, true),
		INC_ITEM_COLLECT(0x19, true),
		INC_KINAH_COLLECT(0x1A, true),
		DEC_KINAH_BUY(0x1D, true),
		INC_KINAH_SELL(0x20, true),
		INC_KINAH_QUEST(0x32, true),
		DEC_KINAH_LEARN(0x49, true), // craft skill learn
		DEC_KINAH_FLY(0x4B, true), // teleport or fly
		INC_CASH_ITEM(0x50, true), // event items, for exchange
		INC_ITEM_REPURCHASE(0x51, true),
		DEC_KINAH_CUBE(0x5A, true), // expand cube
		DEC_PET_FOOD(0x5E, true),
		INC_PASSPORT_ADD(0x8A, true),
		PUT(0x13, true); // from other storage
		
		private final int mask;
		private final boolean sendable;
		
		private ItemUpdateType(int mask, boolean sendable)
		{
			this.mask = mask;
			this.sendable = sendable;
		}
		
		public int getMask()
		{
			return mask;
		}
		
		public boolean isSendable()
		{
			return sendable;
		}
		
		public static ItemUpdateType getKinahUpdateTypeFromAddType(ItemAddType itemAddType, boolean isIncrease)
		{
			if (!isIncrease)
			{
				return ItemUpdateType.DEC_KINAH_BUY;
			}
			switch (itemAddType)
			{
				case BUY:
				{
					return ItemUpdateType.INC_KINAH_SELL;
				}
				case ITEM_COLLECT:
				{
					return ItemUpdateType.INC_KINAH_COLLECT;
				}
				case QUEST_WORK_ITEM:
				{
					return ItemUpdateType.INC_KINAH_QUEST;
				}
				default:
				{
					return ItemUpdateType.INC_KINAH_MERGE;
				}
			}
		}
	}
	
	public static enum ItemAddType
	{
		PARTIAL_WITH_SLOT(0x07), // partial content of slot
		ALL_SLOT(0x13), // all content of slot
		BUY(0x1C),
		ITEM_COLLECT(0x19), // Item collect
		QUEST_WORK_ITEM(0x35),
		QUESTIONNAIRE(0x40),
		COALESCENCE(0xB0),
		AETHERFORGING(0xB2);
		
		private final int mask;
		
		private ItemAddType(int mask)
		{
			this.mask = mask;
		}
		
		public int getMask()
		{
			return mask;
		}
	}
	
	public static enum ItemDeleteType
	{
		QUEST_REWARD(0),
		SPLIT(0x04),
		MOVE(0x14),
		DISCARD(0x15),
		USE(0x17),
		SELL(0x1F),
		QUEST_COMPLETE(0x31),
		QUEST_START(0x34),
		DECOMPOSE(0x66),
		REGISTER(0x78),
		COALESCENCE(0xB1);
		
		private final int mask;
		
		private ItemDeleteType(int mask)
		{
			this.mask = mask;
		}
		
		public int getMask()
		{
			return mask;
		}
		
		public static ItemDeleteType fromUpdateType(ItemUpdateType updateType)
		{
			switch (updateType)
			{
				case DEC_ITEM_SPLIT:
				{
					return SPLIT;
				}
				case DEC_ITEM_USE:
				{
					return USE;
				}
				case DEC_ITEM_SPLIT_MOVE:
				{
					return MOVE;
				}
				default:
				{
					return QUEST_REWARD;
				}
			}
		}
		
		public static ItemDeleteType fromQuestStatus(QuestStatus questStatus)
		{
			switch (questStatus)
			{
				case START:
				{
					return QUEST_START;
				}
				case REWARD:
				{
					return QUEST_REWARD;
				}
				case COMPLETE:
				{
					return QUEST_COMPLETE;
				}
				default:
				{
					return QUEST_REWARD;
				}
			}
		}
	}
	
	public static void updateItemAfterInfoChange(Player player, Item item)
	{
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
	}
	
	public static void updateItemAfterInfoChange(Player player, Item item, ItemUpdateType updateType)
	{
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, updateType));
	}
	
	public static void updateItemAfterEquip(Player player, Item item)
	{
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, ItemUpdateType.EQUIP_UNEQUIP));
	}
	
	public static void sendItemPacket(Player player, StorageType storageType, Item item, ItemUpdateType updateType)
	{
		if ((item.getItemCount() <= 0) && !item.getItemTemplate().isKinah())
		{
			sendItemDeletePacket(player, storageType, item, ItemDeleteType.fromUpdateType(updateType));
		}
		else
		{
			sendItemUpdatePacket(player, storageType, item, updateType);
		}
	}
	
	/**
	 * Item will be deleted from UI slot
	 * @param player
	 * @param storageType
	 * @param item
	 * @param deleteType
	 */
	public static void sendItemDeletePacket(Player player, StorageType storageType, Item item, ItemDeleteType deleteType)
	{
		switch (storageType)
		{
			case CUBE:
			{
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(item.getObjectId(), deleteType));
				break;
			}
			default:
			{
				PacketSendUtility.sendPacket(player, new SM_DELETE_WAREHOUSE_ITEM(storageType.getId(), item.getObjectId(), deleteType));
			}
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(storageType, player));
	}
	
	/**
	 * Item will be updated in UI slot (stacked items)
	 * @param player
	 * @param storageType
	 * @param item
	 * @param updateType
	 */
	public static void sendItemUpdatePacket(Player player, StorageType storageType, Item item, ItemUpdateType updateType)
	{
		switch (storageType)
		{
			case CUBE:
			{
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, updateType));
				break;
			}
			case LEGION_WAREHOUSE:
			{
				if (item.getItemTemplate().isKinah())
				{
					PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion()));
					break;
				}
			}
			default:
			{
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_UPDATE_ITEM(player, item, storageType.getId(), updateType));
			}
		}
	}
	
	public static void sendStorageUpdatePacket(Player player, StorageType storageType, Item item)
	{
		sendStorageUpdatePacket(player, storageType, item, ItemAddType.ITEM_COLLECT);
	}
	
	/**
	 * New item will be displayed in storage
	 * @param player
	 * @param storageType
	 * @param item
	 * @param addType
	 */
	public static void sendStorageUpdatePacket(Player player, StorageType storageType, Item item, ItemAddType addType)
	{
		switch (storageType)
		{
			case CUBE:
			{
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_ADD_ITEM(Collections.singletonList(item), player, addType));
				break;
			}
			case LEGION_WAREHOUSE:
			{
				if (item.getItemTemplate().isKinah())
				{
					PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion()));
					break;
				}
			}
			default:
			{
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_ADD_ITEM(item, storageType.getId(), player, addType));
			}
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(storageType, player));
	}
	
}
