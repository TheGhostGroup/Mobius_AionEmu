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
package system.handlers.admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Admin moveto command
 * @author KID
 */
public class MoveTo extends AdminCommand
{
	public MoveTo()
	{
		super("moveto");
	}
	
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 4))
		{
			PacketSendUtility.sendMessage(admin, "syntax //moveto worldId X Y Z");
			return;
		}
		
		int worldId;
		float x, y, z;
		
		try
		{
			worldId = Integer.parseInt(params[0]);
			x = Float.parseFloat(params[1]);
			y = Float.parseFloat(params[2]);
			z = Float.parseFloat(params[3]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "All the parameters should be numbers");
			return;
		}
		
		if (WorldMapType.getWorld(worldId) == null)
		{
			PacketSendUtility.sendMessage(admin, "Illegal WorldId %d " + worldId);
		}
		else
		{
			TeleportService2.teleportTo(admin, worldId, x, y, z);
			PacketSendUtility.sendMessage(admin, "Teleported to " + x + " " + y + " " + z + " [" + worldId + "]");
		}
	}
	
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "syntax //moveto worldId X Y Z");
	}
}
