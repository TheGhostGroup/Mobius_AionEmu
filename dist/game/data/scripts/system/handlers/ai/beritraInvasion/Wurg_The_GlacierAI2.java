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
package system.handlers.ai.beritraInvasion;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AbyssLandingService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("wurg_the_glacier")
public class Wurg_The_GlacierAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleDied()
	{
		addGpPlayer();
		updateWurgLanding();
		announceEreshkigalDie();
		super.handleDied();
	}
	
	private void addGpPlayer()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (MathUtil.isIn3dRange(player, getOwner(), 15))
				{
					AbyssPointsService.addGp(player, 500);
				}
			}
		});
	}
	
	private void announceEreshkigalDie()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				// The Ereshkigal Legion's magic weapon has been destroyed.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_WORLDRAID_Ere_MESSAGE_DIE_01);
			}
		});
	}
	
	private void updateWurgLanding()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (MathUtil.isIn3dRange(getOwner().getAggroList().getMostHated(), getOwner(), 20))
				{
					if (getOwner().getAggroList().getPlayerWinnerRace() == Race.ASMODIANS)
					{
						AbyssLandingService.getInstance().onRewardMonuments(Race.ASMODIANS, 23, 10000);
					}
					else if (getOwner().getAggroList().getPlayerWinnerRace() == Race.ELYOS)
					{
						AbyssLandingService.getInstance().onRewardMonuments(Race.ELYOS, 11, 10000);
					}
				}
			}
		});
	}
}