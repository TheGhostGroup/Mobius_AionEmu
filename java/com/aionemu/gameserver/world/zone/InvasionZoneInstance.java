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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastMap;

/**
 * @author Source
 */
public class InvasionZoneInstance extends ZoneInstance
{
	private static final Logger log = LoggerFactory.getLogger(InvasionZoneInstance.class);
	private final FastMap<Integer, Player> players = new FastMap<>();
	
	/**
	 * @param mapId
	 * @param template
	 */
	public InvasionZoneInstance(int mapId, ZoneInfo template)
	{
		super(mapId, template);
	}
	
	@Override
	public boolean onEnter(Creature creature)
	{
		if (super.onEnter(creature))
		{
			if (creature instanceof Player)
			{
				players.put(creature.getObjectId(), (Player) creature);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized boolean onLeave(Creature creature)
	{
		if (super.onLeave(creature))
		{
			if (creature instanceof Player)
			{
				players.remove(creature.getObjectId());
			}
			return true;
		}
		return false;
	}
	
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		try
		{
			for (FastMap.Entry<Integer, Player> e = players.head(), mapEnd = players.tail(); (e = e.getNext()) != mapEnd;)
			{
				final Player player = e.getValue();
				if (player != null)
				{
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("Exception when running visitor on all players" + ex);
		}
	}
	
}