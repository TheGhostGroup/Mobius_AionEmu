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

import org.apache.commons.lang.math.NumberUtils;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.ZorshivDredgionService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

public class ZorshivDredgion extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	
	public ZorshivDredgion()
	{
		super("zorshiv");
	}
	
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0]))
		{
			handleStartStop(player, params);
		}
	}
	
	protected void handleStartStop(Player player, String... params)
	{
		if ((params.length != 2) || !NumberUtils.isDigits(params[1]))
		{
			showHelp(player);
			return;
		}
		final int zorshivDredgionId = NumberUtils.toInt(params[1]);
		if (!isValidZorshivDredgionLocationId(player, zorshivDredgionId))
		{
			showHelp(player);
			return;
		}
		if (COMMAND_START.equalsIgnoreCase(params[0]))
		{
			if (ZorshivDredgionService.getInstance().isZorshivDredgionInProgress(zorshivDredgionId))
			{
				PacketSendUtility.sendMessage(player, "<Zorshiv Dredgion> " + zorshivDredgionId + " is already start");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "<Zorshiv Dredgion> " + zorshivDredgionId + " started!");
				World.getInstance().doOnAllPlayers(new Visitor<Player>()
				{
					@Override
					public void visit(Player player)
					{
						PacketSendUtility.sendSys3Message(player, "\uE050", "The <Zorshiv Dredgion> to lands at levinshor !!!");
					}
				});
				ZorshivDredgionService.getInstance().startZorshivDredgion(zorshivDredgionId);
			}
		}
		else if (COMMAND_STOP.equalsIgnoreCase(params[0]))
		{
			if (!ZorshivDredgionService.getInstance().isZorshivDredgionInProgress(zorshivDredgionId))
			{
				PacketSendUtility.sendMessage(player, "<Zorshiv Dredgion> " + zorshivDredgionId + " is not start!");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "<Zorshiv Dredgion> " + zorshivDredgionId + " stopped!");
				ZorshivDredgionService.getInstance().stopZorshivDredgion(zorshivDredgionId);
			}
		}
	}
	
	protected boolean isValidZorshivDredgionLocationId(Player player, int zorshivDredgionId)
	{
		if (!ZorshivDredgionService.getInstance().getZorshivDredgionLocations().keySet().contains(zorshivDredgionId))
		{
			PacketSendUtility.sendMessage(player, "Id " + zorshivDredgionId + " is invalid");
			return false;
		}
		return true;
	}
	
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //zorshiv start|stop <Id>");
	}
}