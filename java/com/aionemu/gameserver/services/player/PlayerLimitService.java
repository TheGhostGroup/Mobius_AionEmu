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
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.SellLimit;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastMap;

/**
 * @author Source
 */
public class PlayerLimitService
{
	static FastMap<Integer, Long> sellLimit = new FastMap<Integer, Long>().shared();
	
	public static boolean updateSellLimit(Player player, long reward)
	{
		if (!CustomConfig.LIMITS_ENABLED)
		{
			return true;
		}
		
		final int accoutnId = player.getPlayerAccount().getId();
		Long limit = sellLimit.get(accoutnId);
		if (limit == null)
		{
			limit = SellLimit.getSellLimit(player.getPlayerAccount().getMaxPlayerLevel()) * CustomConfig.LIMITS_RATE;
			sellLimit.put(accoutnId, limit);
		}
		
		if (limit < reward)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DAY_CANNOT_SELL_NPC(limit));
			return false;
		}
		limit -= reward;
		sellLimit.putEntry(accoutnId, limit);
		return true;
	}
	
	public void scheduleUpdate()
	{
		CronService.getInstance().schedule(() -> sellLimit.clear(), CustomConfig.LIMITS_UPDATE, true);
	}
	
	public static PlayerLimitService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerLimitService instance = new PlayerLimitService();
	}
}
