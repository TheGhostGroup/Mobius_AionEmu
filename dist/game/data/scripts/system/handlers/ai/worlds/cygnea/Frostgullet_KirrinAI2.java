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
package system.handlers.ai.worlds.cygnea;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("frostgullet_kirrin")
public class Frostgullet_KirrinAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleDied()
	{
		spawn(235918, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading()); // Frostshard Kirrin.
		super.handleDied();
		AI2Actions.deleteOwner(this);
		AI2Actions.scheduleRespawn(this);
	}
}