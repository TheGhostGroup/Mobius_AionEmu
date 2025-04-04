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

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.HousingService;

/**
 * Sends a friend list to the client
 * @author Ben
 */
public class SM_FRIEND_LIST extends AionServerPacket
{
	@Override
	protected void writeImpl(AionConnection con)
	{
		final FriendList list = con.getActivePlayer().getFriendList();
		writeH((0 - list.getSize()));
		writeC(0);
		for (Friend friend : list)
		{
			writeD(friend.getOid());
			writeS(friend.getName());
			writeD(friend.getLevel());
			writeD(friend.getPlayerClass().getClassId());
			writeC(friend.isOnline() ? 1 : 0);
			writeD(friend.getMapId());
			writeD(friend.getLastOnlineTime());
			writeS(friend.getNote());
			writeC(friend.getStatus().getId());
			final int address = HousingService.getInstance().getPlayerAddress(friend.getOid());
			if (address > 0)
			{
				House house = HousingService.getInstance().getPlayerStudio(friend.getOid());
				if (house == null)
				{
					house = HousingService.getInstance().getHouseByAddress(address);
					writeD(house.getAddress().getId());
				}
				else
				{
					writeD(address);
				}
				writeC(house.getDoorState().getPacketValue());
			}
			else
			{
				writeD(0);
				writeC(0);
			}
			writeS("Encom Emu"); // Friend Note.
		}
	}
}