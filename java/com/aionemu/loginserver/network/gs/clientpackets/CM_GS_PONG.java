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
package com.aionemu.loginserver.network.gs.clientpackets;

import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.gs.GsClientPacket;

/**
 * @author KID
 */
public class CM_GS_PONG extends GsClientPacket
{
	private byte serverId;
	private int pid;
	
	@Override
	protected void readImpl()
	{
		serverId = (byte) readC();
		pid = readD();
	}
	
	@Override
	protected void runImpl()
	{
		GameServerTable.pong(serverId, pid);
	}
}
