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
package com.aionemu.gameserver.utils.stats.enums;

/**
 * @author ATracer
 */
public enum ACCURACY
{
	WARRIOR(100),
	GLADIATOR(100),
	TEMPLAR(100),
	SCOUT(110),
	ASSASSIN(110),
	RANGER(100),
	MAGE(95),
	SORCERER(100),
	SPIRIT_MASTER(100),
	PRIEST(100),
	CLERIC(100),
	CHANTER(90),
	// News Class 4.3
	TECHNIST(110),
	GUNSLINGER(100),
	MUSE(100),
	SONGWEAVER(100),
	// News Class 4.5
	AETHERTECH(110);
	
	private int value;
	
	private ACCURACY(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}