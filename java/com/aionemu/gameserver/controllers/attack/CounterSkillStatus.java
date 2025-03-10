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
package com.aionemu.gameserver.controllers.attack;

/**
 * @author Ever'
 */
public enum CounterSkillStatus
{
	BLOCK(32),
	PARRY(64),
	DODGE(128),
	RESIST(256);
	
	private final int type;
	
	private CounterSkillStatus(int type)
	{
		this.type = type;
	}
	
	public final int getId()
	{
		return type;
	}
}