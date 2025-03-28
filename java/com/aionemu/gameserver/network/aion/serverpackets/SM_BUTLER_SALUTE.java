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
 * @author Ranastic
 */
public class SM_BUTLER_SALUTE extends AionServerPacket
{
	private final int playerObjId;
	private final int isInside;
	private final int unk1;
	private final int unk2;
	private final int unk3;
	private final int unk4;
	
	public SM_BUTLER_SALUTE(int unk1, int unk2, int unk3, int unk4, int playerObjId, int isInside)
	{
		this.unk1 = unk1;
		this.unk2 = unk2;
		this.unk3 = unk3;
		this.unk4 = unk4;
		this.playerObjId = playerObjId;
		this.isInside = isInside;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(unk1);
		writeC(unk2);
		writeD(unk3);
		writeC(unk4);
		writeD(playerObjId);
		writeC(isInside);
	}
}