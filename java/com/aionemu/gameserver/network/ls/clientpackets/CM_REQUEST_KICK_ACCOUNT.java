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
package com.aionemu.gameserver.network.ls.clientpackets;

import com.aionemu.gameserver.network.ls.LoginServer;
import com.aionemu.gameserver.network.ls.LsClientPacket;

/**
 * This packet is request kicking player.
 * @author -Nemesiss-
 */
public class CM_REQUEST_KICK_ACCOUNT extends LsClientPacket
{
	public CM_REQUEST_KICK_ACCOUNT(int opCode)
	{
		super(opCode);
	}
	
	/**
	 * account id of account that login server request to kick.
	 */
	private int accountId;
	
	@Override
	public void readImpl()
	{
		accountId = readD();
	}
	
	@Override
	public void runImpl()
	{
		LoginServer.getInstance().kickAccount(accountId);
	}
}
