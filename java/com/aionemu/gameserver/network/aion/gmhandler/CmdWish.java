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
package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.configs.administration.PanelConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * @author Alcapwnd
 */
public class CmdWish extends AbstractGMHandler
{
	public CmdWish(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	public void run()
	{
		Player t = admin;
		
		if (admin.getClientConnection().getAccount().getAccessLevel() <= PanelConfig.WISHITEM_PANEL_LEVEL)
		{
			PacketSendUtility.sendMessage(admin, "You haven't access this panel commands");
			return;
		}
		
		if ((admin.getTarget() != null) && (admin.getTarget() instanceof Player))
		{
			t = World.getInstance().findPlayer(Util.convertName(admin.getTarget().getName()));
		}
		
		final String[] p = params.split(" ");
		if (p.length != 2)
		{
			PacketSendUtility.sendMessage(admin, "not enough parameters");
			return;
		}
		
		if (p[0].length() < 6)
		{
			final Integer qty = Integer.parseInt(p[0]);
			final Integer itemId = Integer.parseInt(p[1]);
			
			if ((qty > 0) && (itemId > 0))
			{
				if ((itemId >= 187100023) && (itemId <= 187100030))
				{// Those Items wont work correct on 4.9.
					PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1300493));
					return;
				}
				if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null)
				{
					PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + itemId);
				}
				else
				{
					final long count = ItemService.addItem(t, itemId, qty);
					if (count == 0)
					{
						PacketSendUtility.sendMessage(admin, "You successfully gave " + qty + " x [item:" + itemId + "] to " + t.getName() + ".");
					}
					else
					{
						PacketSendUtility.sendMessage(admin, "Item couldn't be added");
					}
				}
			}
		}
		
		if (p[0].length() > 6)
		{
			final String itemDesc = p[0];
			final Integer countitems = Integer.parseInt(p[1]);
			
			if ((itemDesc != null) && (countitems > 0))
			{
				for (ItemTemplate template : DataManager.ITEM_DATA.getItemData().valueCollection())
				{
					if ((template.getName() != null) && template.getName().equalsIgnoreCase(itemDesc))
					{
						if ((template.getTemplateId() >= 187100023) && (template.getTemplateId() <= 187100030))
						{// Those Items wont work correct on 4.9.
							PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1300493));
							return;
						}
						final long count = ItemService.addItem(t, template.getTemplateId(), countitems);
						if (count == 0)
						{
							PacketSendUtility.sendMessage(admin, "You successfully gave " + countitems + " x [item:" + template.getTemplateId() + "] ID: " + template.getTemplateId() + " to " + t.getName() + ".");
						}
						else
						{
							PacketSendUtility.sendMessage(admin, "Item couldn't be added");
						}
					}
				}
			}
		}
	}
}