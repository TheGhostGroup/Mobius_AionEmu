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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneType;

/**
 * @author MrPoke
 */
public class PvPZoneInstance extends SiegeZoneInstance
{
	public PvPZoneInstance(int mapId, ZoneInfo template)
	{
		super(mapId, template);
	}
	
	@Override
	public synchronized boolean onEnter(Creature creature)
	{
		if (super.onEnter(creature))
		{
			creature.setInsideZoneType(ZoneType.PVP);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized boolean onLeave(Creature creature)
	{
		if (super.onLeave(creature))
		{
			creature.unsetInsideZoneType(ZoneType.PVP);
			return true;
		}
		return false;
	}
}