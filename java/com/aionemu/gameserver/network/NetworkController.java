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
package com.aionemu.gameserver.network;

/**
 * @author KID
 */
public class NetworkController
{
	private static NetworkController instance = new NetworkController();
	
	public static NetworkController getInstance()
	{
		return instance;
	}
	
	private byte serverCount = 1;
	
	public final byte getServerCount()
	{
		return serverCount;
	}
	
	public final void setServerCount(byte count)
	{
		serverCount = count;
	}
}
