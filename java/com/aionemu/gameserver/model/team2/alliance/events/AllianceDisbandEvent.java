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
package com.aionemu.gameserver.model.team2.alliance.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class AllianceDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerAlliance alliance;
	
	public AllianceDisbandEvent(PlayerAlliance alliance)
	{
		this.alliance = alliance;
	}
	
	@Override
	public void handleEvent()
	{
		alliance.applyOnMembers(this);
	}
	
	@Override
	public boolean apply(Player player)
	{
		alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, player, LeaveReson.DISBAND));
		return true;
	}
}