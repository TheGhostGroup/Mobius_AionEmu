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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Cura
 */
public class SM_CAPTCHA extends AionServerPacket
{
	private final int type;
	private int count;
	private int size;
	private byte[] data;
	private boolean isCorrect;
	private int banTime;
	
	/**
	 * @param count
	 * @param data
	 */
	public SM_CAPTCHA(int count, byte[] data)
	{
		type = 1;
		this.count = count;
		size = data.length;
		this.data = data;
	}
	
	/**
	 * @param isCorrect
	 * @param banTime
	 */
	public SM_CAPTCHA(boolean isCorrect, int banTime)
	{
		type = 3;
		this.isCorrect = isCorrect;
		this.banTime = banTime;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(type);
		
		switch (type)
		{
			case 0x01:
			{
				writeC(count);
				writeD(size);
				writeB(data);
				break;
			}
			case 0x03:
			{
				writeH(isCorrect ? 1 : 0);
				// time setting can't be extracted (retail server default value:3000 sec)
				writeD(banTime);
				break;
			}
		}
	}
}
