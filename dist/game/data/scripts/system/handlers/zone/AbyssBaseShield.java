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
package system.handlers.zone;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * @author MrPoke
 */
@ZoneNameAnnotation("PRIMUM_FORTRESS TERMINON_LANDING")
public class AbyssBaseShield implements ZoneHandler
{
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone)
	{
		final Creature actingCreature = creature.getActingCreature();
		if ((actingCreature instanceof Player) && !((Player) actingCreature).isGM())
		{
			final ZoneName currZone = zone.getZoneTemplate().getName();
			if (currZone == ZoneName.get("PRIMUM_FORTRESS"))
			{
				if (((Player) actingCreature).getRace() == Race.ELYOS)
				{
					creature.getController().die();
				}
			}
			else if (currZone == ZoneName.get("TERMINON_LANDING"))
			{
				if (((Player) actingCreature).getRace() == Race.ASMODIANS)
				{
					creature.getController().die();
				}
			}
		}
	}
	
	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone)
	{
	}
}