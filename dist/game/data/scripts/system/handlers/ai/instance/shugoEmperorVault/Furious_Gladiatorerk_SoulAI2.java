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
package system.handlers.ai.instance.shugoEmperorVault;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler (Encom)
 */
@AIName("gladiatorerk")
public class Furious_Gladiatorerk_SoulAI2 extends NpcAI2
{
	@Override
	protected void handleDialogStart(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		final PlayerEffectController effectController = player.getEffectController();
		if (dialogId == 10000)
		{
			switch (getNpcId())
			{
				case 833492: // Furious Gladiatorerk's Soul.
				{
					if (player.getCommonData().getRace() == Race.ELYOS)
					{
						effectController.removeEffect(21829);
						effectController.removeEffect(21831);
						SkillEngine.getInstance().applyEffectDirectly(21830, player, player, 1200000 * 1); // Furious Gladiatorerk's Soul.
					}
					break;
				}
				case 833495: // Furious Gladiatorerk's Soul.
				{
					if (player.getCommonData().getRace() == Race.ASMODIANS)
					{
						effectController.removeEffect(21832);
						effectController.removeEffect(21834);
						SkillEngine.getInstance().applyEffectDirectly(21833, player, player, 1200000 * 1); // Furious Gladiatorerk's Soul.
					}
					break;
				}
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}