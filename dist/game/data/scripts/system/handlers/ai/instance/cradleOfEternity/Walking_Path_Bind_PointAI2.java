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
package system.handlers.ai.instance.cradleOfEternity;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Rinzler (Encom)
 */
@AIName("Walking_Path_Bind_Point")
public class Walking_Path_Bind_PointAI2 extends NpcAI2
{
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		checkDistance(this, creature);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		checkDistance(this, creature);
	}
	
	private void checkDistance(NpcAI2 ai, Creature creature)
	{
		if ((creature instanceof Player) && !creature.getLifeStats().isAlreadyDead())
		{
			if (MathUtil.isIn3dRange(getOwner(), creature, 5))
			{
				WalkingPathBindPoint();
			}
		}
	}
	
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		announceWalkingPath();
	}
	
	private void WalkingPathBindPoint()
	{
		AI2Actions.deleteOwner(Walking_Path_Bind_PointAI2.this);
		spawn(281446, 976.31232f, 774.7804f, 1043.3522f, (byte) 0);
		spawn(806037, 976.31232f, 774.7804f, 1043.3522f, (byte) 0); // Geodesis <To The Garden Temple>
	}
	
	private void announceWalkingPath()
	{
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (player.isOnline())
				{
					// The Walking Path's bind point device was activated.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_IDEternity_02_SYSTEM_MSG_11);
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported()
	{
		return false;
	}
}