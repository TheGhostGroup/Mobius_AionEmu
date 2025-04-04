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
package system.handlers.ai.instance.kromedesTrial;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler (Encom)
 */
@AIName("maga_potion_1")
public class Maga_Potion_Temple_VaultAI2 extends NpcAI2
{
	@Override
	protected void handleDialogStart(Player player)
	{
		if (player.getInventory().getFirstItemByItemId(185000109) != null) // Relic Key.
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
		}
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		final int instanceId = getPosition().getInstanceId();
		if ((dialogId == 10000) && player.getInventory().decreaseByItemId(185000109, 1)) // Relic Key.
		{
			switch (getNpcId())
			{
				case 730308: // Maga's Potion.
				{
					TeleportService2.teleportTo(player, 300230000, instanceId, 687.56116f, 681.68225f, 200.28648f, (byte) 30);
					break;
				}
			}
		}
		else if (dialogId == 1012)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1012));
		}
		return true;
	}
}