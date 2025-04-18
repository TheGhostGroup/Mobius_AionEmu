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

import static ch.lambdaj.Lambda.maxFrom;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.instance.playerreward.IronWallWarfrontPlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Rinzler (Encom)
 */
public class IronWallWarfrontReward extends InstanceReward<IronWallWarfrontPlayerReward>
{
	private final int capPoints;
	private final MutableInt asmodiansPoints = new MutableInt(0);
	private final MutableInt elyosPoins = new MutableInt(0);
	private final MutableInt asmodiansPvpKills = new MutableInt(0);
	private final MutableInt elyosPvpKills = new MutableInt(0);
	private Race race;
	private Point3D asmodiansStartPosition;
	private Point3D elyosStartPosition;
	protected WorldMapInstance instance;
	private long instanceTime;
	private final int bonusTime;
	private final byte buffId;
	
	public IronWallWarfrontReward(Integer mapId, int instanceId, WorldMapInstance instance)
	{
		super(mapId, instanceId);
		this.instance = instance;
		// Commander Pashid give 200.000 Pts
		capPoints = 500000;
		bonusTime = 12000;
		buffId = 12;
		setStartPositions();
	}
	
	public int AbyssReward(boolean isWin, boolean isCommanderKilled)
	{
		final int CommanderKilled = 1993;
		final int Win = 3163;
		final int Loss = 1031;
		if (isCommanderKilled)
		{
			return isWin ? (Win + CommanderKilled) : (Loss + CommanderKilled);
		}
		return isWin ? Win : Loss;
	}
	
	public int GloryReward(boolean isWin, boolean isCommanderKilled)
	{
		final int CommanderKilled = 50;
		final int Win = 150;
		final int Loss = 75;
		if (isCommanderKilled)
		{
			return isWin ? (Win + CommanderKilled) : (Loss + CommanderKilled);
		}
		return isWin ? Win : Loss;
	}
	
	public int ExpReward(boolean isWin, boolean isCommanderKilled)
	{
		final int CommanderKilled = 20000;
		final int Win = 10000;
		final int Loss = 5000;
		if (isCommanderKilled)
		{
			return isWin ? (Win + CommanderKilled) : (Loss + CommanderKilled);
		}
		return isWin ? Win : Loss;
	}
	
	public List<IronWallWarfrontPlayerReward> sortPoints()
	{
		return sort(getInstanceRewards(), on(PvPArenaPlayerReward.class).getScorePoints(), (o1, o2) -> o2 != null ? o2.compareTo(o1) : -o1.compareTo(o2));
	}
	
	private void setStartPositions()
	{
		final Point3D a = new Point3D(282.16364f, 390.80576f, 238.60538f);
		final Point3D e = new Point3D(711.2616f, 630.29364f, 211.9369f);
		asmodiansStartPosition = a;
		elyosStartPosition = e;
	}
	
	public void portToPosition(Player player)
	{
		if (player.getRace() == Race.ASMODIANS)
		{
			TeleportService2.teleportTo(player, mapId, instanceId, asmodiansStartPosition.getX(), asmodiansStartPosition.getY(), asmodiansStartPosition.getZ());
		}
		else
		{
			TeleportService2.teleportTo(player, mapId, instanceId, elyosStartPosition.getX(), elyosStartPosition.getY(), elyosStartPosition.getZ());
		}
	}
	
	public MutableInt getPointsByRace(Race race)
	{
		return (race == Race.ELYOS) ? elyosPoins : (race == Race.ASMODIANS) ? asmodiansPoints : null;
	}
	
	public void addPointsByRace(Race race, int points)
	{
		final MutableInt racePoints = getPointsByRace(race);
		racePoints.add(points);
		if (racePoints.intValue() < 0)
		{
			racePoints.setValue(0);
		}
	}
	
	public MutableInt getPvpKillsByRace(Race race)
	{
		return (race == Race.ELYOS) ? elyosPvpKills : (race == Race.ASMODIANS) ? asmodiansPvpKills : null;
	}
	
	public void addPvpKillsByRace(Race race, int points)
	{
		final MutableInt racePoints = getPvpKillsByRace(race);
		racePoints.add(points);
		if (racePoints.intValue() < 0)
		{
			racePoints.setValue(0);
		}
	}
	
	public void setWinnerRace(Race race)
	{
		this.race = race;
	}
	
	public Race getWinnerRace()
	{
		return race;
	}
	
	public Race getWinnerRaceByScore()
	{
		return asmodiansPoints.compareTo(elyosPoins) > 0 ? Race.ASMODIANS : Race.ELYOS;
	}
	
	@Override
	public void clear()
	{
		super.clear();
	}
	
	public void regPlayerReward(Player player)
	{
		if (!containPlayer(player.getObjectId()))
		{
			addPlayerReward(new IronWallWarfrontPlayerReward(player.getObjectId(), bonusTime, buffId, player.getRace()));
		}
	}
	
	@Override
	public void addPlayerReward(IronWallWarfrontPlayerReward reward)
	{
		super.addPlayerReward(reward);
	}
	
	@Override
	public IronWallWarfrontPlayerReward getPlayerReward(Integer object)
	{
		return (IronWallWarfrontPlayerReward) super.getPlayerReward(object);
	}
	
	public void sendPacket(int type, Integer object)
	{
		instance.doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(type, getTime(), getInstanceReward(), object)));
	}
	
	public int getTime()
	{
		final long result = System.currentTimeMillis() - instanceTime;
		if (result < 90000)
		{
			return (int) (90000 - result);
		}
		else if (result < 2400000) // 40-Mins
		{
			return (int) (2400000 - (result - 90000));
		}
		return 0;
	}
	
	public byte getBuffId()
	{
		return buffId;
	}
	
	@Override
	public void setInstanceStartTime()
	{
		instanceTime = System.currentTimeMillis();
	}
	
	public int getCapPoints()
	{
		return capPoints;
	}
	
	public boolean hasCapPoints()
	{
		return maxFrom(getInstanceRewards()).getPoints() >= capPoints;
	}
}