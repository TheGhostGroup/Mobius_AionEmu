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
package com.aionemu.gameserver.services.svsservice;

import com.aionemu.gameserver.model.svs.SvsLocation;
import com.aionemu.gameserver.model.svs.SvsStateType;

/**
 * @author Rinzler (Encom)
 */
public class Gate extends Panesterra<SvsLocation>
{
	public Gate(SvsLocation svs)
	{
		super(svs);
	}
	
	@Override
	public void startSvs()
	{
		getSvsLocation().setActiveSvs(this);
		despawn();
		spawn(SvsStateType.SVS);
	}
	
	@Override
	public void stopSvs()
	{
		getSvsLocation().setActiveSvs(null);
		despawn();
		spawn(SvsStateType.PEACE);
	}
}