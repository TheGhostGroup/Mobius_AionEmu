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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * @author cura
 */
public class CM_LEGION_SEND_EMBLEM_INFO extends AionClientPacket
{
	private int legionId;
	
	public CM_LEGION_SEND_EMBLEM_INFO(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		legionId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Legion legion = LegionService.getInstance().getLegion(legionId);
		if (legion != null)
		{
			final LegionEmblem legionEmblem = legion.getLegionEmblem();
			if (legionEmblem.getCustomEmblemData() == null)
			{
				return;
			}
			LegionService.getInstance().sendEmblemData(getConnection().getActivePlayer(), legionEmblem, legionId, legion.getLegionName());
		}
	}
}
