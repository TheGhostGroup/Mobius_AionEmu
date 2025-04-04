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
package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Sarynth
 */
@XmlRootElement(name = "kisk_stats")
@XmlAccessorType(XmlAccessType.FIELD)
public class KiskStatsTemplate
{
	@XmlAttribute(name = "usemask")
	private final int useMask = 6;
	
	@XmlAttribute(name = "members")
	private final int maxMembers = 576;
	
	@XmlAttribute(name = "resurrects")
	private final int maxResurrects = 1728;
	
	public int getUseMask()
	{
		return useMask;
	}
	
	public int getMaxMembers()
	{
		return maxMembers;
	}
	
	public int getMaxResurrects()
	{
		return maxResurrects;
	}
}