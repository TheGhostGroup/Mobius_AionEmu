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
package com.aionemu.gameserver.model.instance.instancereward;

import com.aionemu.gameserver.model.instance.playerreward.EternalBastionPlayerReward;

/**
 * @author Romanz
 */
public class EternalBastionReward extends InstanceReward<EternalBastionPlayerReward>
{
	private int points;
	private int npcKills;
	private int rank = 7;
	
	public EternalBastionReward(Integer mapId, int instanceId)
	{
		super(mapId, instanceId);
	}
	
	public void addPoints(int points)
	{
		this.points += points;
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public void addNpcKill()
	{
		npcKills++;
	}
	
	public int getNpcKills()
	{
		return npcKills;
	}
	
	public void setRank(int rank)
	{
		this.rank = rank;
	}
	
	public int getRank()
	{
		return rank;
	}
}