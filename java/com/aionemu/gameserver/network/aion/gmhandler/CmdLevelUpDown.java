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

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Alcapwnd
 */
public class CmdLevelUpDown extends AbstractGMHandler
{
	public enum LevelUpDownState
	{
		UP,
		DOWN
	}
	
	private final LevelUpDownState state;
	
	public CmdLevelUpDown(Player admin, String params, LevelUpDownState state)
	{
		super(admin, params);
		this.state = state;
		run();
	}
	
	public void run()
	{
		final Player t = target != null ? target : admin;
		final Integer level = Integer.parseInt(params);
		
		if (state == LevelUpDownState.DOWN)
		{
			if ((t.getCommonData().getLevel() - level) >= 1)
			{
				final int newLevel = t.getCommonData().getLevel() - level;
				t.getCommonData().setLevel(newLevel);
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "The value of <level> will plus calculated to the current player level!");
			}
		}
		else if (state == LevelUpDownState.UP)
		{
			if ((t.getCommonData().getLevel() + level) <= GSConfig.PLAYER_MAX_LEVEL)
			{
				final int newLevel = t.getCommonData().getLevel() + level;
				t.getCommonData().setLevel(newLevel);
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "The value of <level> will plus calculated to the current player level!");
			}
		}
	}
}