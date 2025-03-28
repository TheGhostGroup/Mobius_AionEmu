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

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Divinity
 */
public class Say extends AdminCommand
{
	public Say()
	{
		super("say");
	}
	
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length < 1)
		{
			onFail(admin, null);
			return;
		}
		
		final VisibleObject target = admin.getTarget();
		
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "You must select a target !");
			return;
		}
		
		final StringBuilder sbMessage = new StringBuilder();
		
		for (String p : params)
		{
			sbMessage.append(p + " ");
		}
		
		final String sMessage = sbMessage.toString().trim();
		
		if (target instanceof Player)
		{
			PacketSendUtility.broadcastPacket(((Player) target), new SM_MESSAGE(((Player) target), sMessage, ChatType.NORMAL), true);
		}
		else if (target instanceof Npc)
		{
			// admin is not right, but works
			PacketSendUtility.broadcastPacket(admin, new SM_MESSAGE(((Npc) target).getObjectId(), ((Npc) target).getName(), sMessage, ChatType.NORMAL), true);
		}
	}
	
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "Syntax: //say <Text>");
	}
}
