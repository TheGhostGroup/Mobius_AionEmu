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
package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Alcapwnd
 */
public class CmdVisible extends AbstractGMHandler
{
	public CmdVisible(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	private void run()
	{
		admin.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
		admin.unsetVisualState(CreatureVisualState.HIDE20);
		PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
		PacketSendUtility.sendMessage(admin, "You are invisible.");
	}
	
}