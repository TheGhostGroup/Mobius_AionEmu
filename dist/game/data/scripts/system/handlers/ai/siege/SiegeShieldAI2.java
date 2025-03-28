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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHIELD_EFFECT;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Rinzler (Encom)
 */
@AIName("siege_shield")
public class SiegeShieldAI2 extends NpcAI2
{
	@Override
	protected void handleDespawned()
	{
		sendShieldPacket(false);
		super.handleDespawned();
	}
	
	@Override
	protected void handleSpawned()
	{
		sendShieldPacket(true);
		super.handleSpawned();
	}
	
	private void sendShieldPacket(boolean shieldStatus)
	{
		final int id = getSpawnTemplate().getSiegeId();
		SiegeService.getInstance().getFortress(id).setUnderShield(shieldStatus);
		final SM_SHIELD_EFFECT packet = new SM_SHIELD_EFFECT(id);
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				PacketSendUtility.sendPacket(player, packet);
			}
		});
	}
	
	@Override
	protected SiegeSpawnTemplate getSpawnTemplate()
	{
		return (SiegeSpawnTemplate) super.getSpawnTemplate();
	}
}