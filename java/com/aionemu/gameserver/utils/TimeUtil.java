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
package com.aionemu.gameserver.utils;

import java.util.Date;

/**
 * @author ATracer
 */
public class TimeUtil
{
	/**
	 * Check whether supplied time in ms is expired
	 * @param time
	 * @return
	 */
	public static boolean isExpired(long time)
	{
		return time < System.currentTimeMillis();
	}
	
	@SuppressWarnings("deprecation")
	public static String getTimeData(long time)
	{
		final Date d = new Date(time * 1000);
		final String localDate = d.toLocaleString();
		return localDate;
	}
}
