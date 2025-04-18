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
package com.aionemu.gameserver.model.nightmarecircus;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.nightmarecircus.NightmareCircusTemplate;
import com.aionemu.gameserver.services.nightmarecircusservice.CircusInstance;

import javolution.util.FastMap;

/**
 * @author Rinzler (Encom)
 */
public class NightmareCircusLocation
{
	protected int id;
	protected boolean isActive;
	protected NightmareCircusTemplate template;
	protected CircusInstance<NightmareCircusLocation> activeNightmareCircus;
	protected FastMap<Integer, Player> players = new FastMap<>();
	private final List<VisibleObject> spawned = new ArrayList<>();
	
	public NightmareCircusLocation()
	{
	}
	
	public NightmareCircusLocation(NightmareCircusTemplate template)
	{
		this.template = template;
		id = template.getId();
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	public void setActiveNightmareCircus(CircusInstance<NightmareCircusLocation> nightmareCircus)
	{
		isActive = nightmareCircus != null;
		activeNightmareCircus = nightmareCircus;
	}
	
	public CircusInstance<NightmareCircusLocation> getActiveNightmareCircus()
	{
		return activeNightmareCircus;
	}
	
	public final NightmareCircusTemplate getTemplate()
	{
		return template;
	}
	
	public int getId()
	{
		return id;
	}
	
	public List<VisibleObject> getSpawned()
	{
		return spawned;
	}
	
	public FastMap<Integer, Player> getPlayers()
	{
		return players;
	}
}