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
package com.aionemu.gameserver.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author ATracer
 */
public abstract class AbyssRankDAO implements DAO
{
	@Override
	public final String getClassName()
	{
		return AbyssRankDAO.class.getName();
	}
	
	public abstract void loadAbyssRank(Player player);
	
	public abstract AbyssRank loadAbyssRank(int playerId);
	
	public abstract boolean storeAbyssRank(Player player);
	
	public abstract ArrayList<AbyssRankingResult> getAbyssRankingPlayers(Race race);
	
	public abstract ArrayList<AbyssRankingResult> getAbyssRankingLegions(Race race);
	
	public abstract Map<Integer, Integer> loadPlayersAp(Race race, int lowerApLimit, int maxOfflineDays);
	
	public abstract Map<Integer, Integer> loadPlayersGp(Race race, int lowerGpLimit, int maxOfflineDays);
	
	public abstract void updateAbyssRank(int playerId, AbyssRankEnum rankEnum);
	
	public abstract void updateRankList();
	
	public abstract void removePlayer(List<Player> listP);
}
