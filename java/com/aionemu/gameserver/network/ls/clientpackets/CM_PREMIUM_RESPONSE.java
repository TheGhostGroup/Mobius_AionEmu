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

import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.network.ls.LsClientPacket;

/**
 * @author KID
 */
public class CM_PREMIUM_RESPONSE extends LsClientPacket
{
	private int requestId;
	private int result;
	private long points;
	private long luna;
	
	public CM_PREMIUM_RESPONSE(int opCode)
	{
		super(opCode);
	}
	
	@Override
	protected void readImpl()
	{
		requestId = readD();
		result = readD();
		points = readQ();
		luna = readQ();
	}
	
	@Override
	protected void runImpl()
	{
		InGameShopEn.getInstance().finishRequest(requestId, result, points, luna);
	}
}
