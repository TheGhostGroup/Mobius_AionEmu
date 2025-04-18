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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Author Rinzler (Encom) Player receive "AP" everytime, if he uses any of these items: Abyss Armor 35% Extraction Tools. Abyss Accessory 35% Extraction Tools. Abyss Equipment 35% Extraction Tools. Abyss Weapon 35% Extraction Tools. Abyss Wing 35% Extraction Tools. Vindachinerk's Durable Abyss Armor
 * Extraction Tools. Vindachinerk's Durable Abyss Weapon Extraction Tools. Vindachinerk's Noble Abyss Armor Extraction Tools. Vindachinerk's Noble Abyss Weapon Extraction Tools.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtractAbyssAction")
public class ExtractAbyssAction extends AbstractItemAction
{
	@XmlAttribute(name = "apextractionrate")
	protected Integer apextractionrate;
	
	protected String itemCategory;
	
	public ExtractAbyssAction()
	{
	}
	
	public ExtractAbyssAction(Integer apextractionrate, String itemCategory)
	{
		this.apextractionrate = apextractionrate;
		this.itemCategory = itemCategory;
	}
	
	public Integer getRate()
	{
		return apextractionrate;
	}
	
	public void setRate(Integer apextractionrate)
	{
		this.apextractionrate = apextractionrate;
	}
	
	public String getItemCategory()
	{
		return itemCategory;
	}
	
	public void setItemCategory(String itemCategory)
	{
		this.itemCategory = itemCategory;
	}
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if (parentItem == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		return true;
	}
	
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		player.getController().cancelUseItem();
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 5000, 0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (targetItem.getItemTemplate().getAcquisition().getRequiredAp() != 0)
				{
					AbyssPointsService.addAp(player, (int) (targetItem.getItemTemplate().getAcquisition().getRequiredAp() * (getRate() / 1000f)));
					player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
					player.getInventory().decreaseItemCount(targetItem, 1);
				}
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 0));
			}
		}, 5000));
	}
}