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
package system.handlers.ai.instance.padmarashkaCave;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("padmarashka_eggs")
public class Padmarashka_EggsAI2 extends NpcAI2
{
	@Override
	protected void handleDied()
	{
		switch (Rnd.get(1, 5))
		{
			case 1:
			{
				spawn(282615, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading()); // Neonate Drakan.
				break;
			}
			case 2:
			{
				spawn(282616, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading()); // Neonate Drakan.
				break;
			}
			case 3:
			{
				spawn(282617, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading()); // Neonate Drakan.
				break;
			}
			case 4:
			{
				spawn(282618, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading()); // Neonate Drakan.
				break;
			}
			case 5:
			{
				spawn(282619, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading()); // Neonate Drakan.
				break;
			}
		}
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}
}