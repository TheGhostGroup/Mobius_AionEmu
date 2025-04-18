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
package system.handlers.ai.instance.rentusBase;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("broken_barrel")
public class Broken_BarrelAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
	public void think()
	{
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 15)
			{
				if (startedEvent.compareAndSet(false, true))
				{
					getPosition().getWorldMapInstance().getDoors().get(54).setOpen(true);
					ThreadPoolManager.getInstance().schedule(new Runnable()
					{
						@Override
						public void run()
						{
							spawn(282626, 167.56618f, 341.45828f, 207.60175f, (byte) 0, 229);
						}
					}, 5000);
					AI2Actions.deleteOwner(Broken_BarrelAI2.this);
				}
			}
		}
	}
	
	@Override
	public boolean isMoveSupported()
	{
		return false;
	}
}