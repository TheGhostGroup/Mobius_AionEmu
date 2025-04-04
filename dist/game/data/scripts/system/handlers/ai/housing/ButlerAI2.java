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
package system.handlers.ai.housing;

import java.util.Map;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.PlayerScript;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_SCRIPTS;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.GeneralNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("butler")
public class ButlerAI2 extends GeneralNpcAI2
{
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		return kickDialog(player, DialogPage.getPageByAction(dialogId));
	}
	
	private boolean kickDialog(Player player, DialogPage page)
	{
		if (page == DialogPage.NULL)
		{
			return false;
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), page.id()));
		return true;
	}
	
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			final House house = (House) getCreator();
			if (player.getObjectId() == house.getOwnerId())
			{
			}
			final Map<Integer, PlayerScript> scriptMap = house.getPlayerScripts().getScripts();
			try
			{
				for (int position = 0; position < 8; position++)
				{
					scriptMap.get(position).writeLock();
				}
				int totalSize = 0;
				int position = 0;
				int from = 0;
				while (position != 7)
				{
					for (; position < 8; position++)
					{
						final PlayerScript script = scriptMap.get(position);
						final byte[] bytes = script.getCompressedBytes();
						if (bytes == null)
						{
							continue;
						}
						if (bytes.length > 8141)
						{
							return;
						}
						if ((totalSize + bytes.length) > 8141)
						{
							position--;
							PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(house.getAddress().getId(), house.getPlayerScripts(), from, position));
							from = position + 1;
							totalSize = 0;
							continue;
						}
						totalSize += bytes.length + 8;
					}
					position--;
					if ((totalSize > 0) || ((from == 0) && (position == 7)))
					{
						PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(house.getAddress().getId(), house.getPlayerScripts(), from, position));
					}
				}
			}
			finally
			{
				for (int position = 0; position < 8; position++)
				{
					scriptMap.get(position).writeUnlock();
				}
			}
		}
	}
}