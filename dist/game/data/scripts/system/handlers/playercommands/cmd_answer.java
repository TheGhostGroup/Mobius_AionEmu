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
package system.handlers.playercommands;

import com.aionemu.gameserver.model.Wedding;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WeddingService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author synchro2
 */
public class cmd_answer extends PlayerCommand
{
	public cmd_answer()
	{
		super("answer");
	}
	
	@Override
	public void execute(Player player, String... params)
	{
		final Wedding wedding = WeddingService.getInstance().getWedding(player);
		
		if ((params == null) || (params.length != 1))
		{
			PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
			return;
		}
		
		if ((player.getWorldId() == 510010000) || (player.getWorldId() == 520010000))
		{
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}
		
		if (wedding == null)
		{
			PacketSendUtility.sendMessage(player, "Wedding not started.");
		}
		
		if (params[0].toLowerCase().equals("yes"))
		{
			PacketSendUtility.sendMessage(player, "You accept.");
			WeddingService.getInstance().acceptWedding(player);
		}
		
		if (params[0].toLowerCase().equals("no"))
		{
			PacketSendUtility.sendMessage(player, "You decide.");
			WeddingService.getInstance().cancelWedding(player);
		}
	}
	
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
	}
}
