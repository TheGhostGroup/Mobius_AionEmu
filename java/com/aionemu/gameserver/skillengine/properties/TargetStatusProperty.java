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
package com.aionemu.gameserver.skillengine.properties;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetStatusProperty")
public class TargetStatusProperty
{
	/**
	 * @param skill
	 * @param properties
	 * @return
	 */
	public static boolean set(Skill skill, Properties properties)
	{
		if (skill.getEffectedList().size() != 1)
		{
			return false;
		}
		
		final List<String> targetStatus = properties.getTargetStatus();
		
		final Creature effected = skill.getFirstTarget();
		boolean result = false;
		
		for (String status : targetStatus)
		{
			if (effected.getEffectController().isAbnormalSet(AbnormalState.valueOf(status)))
			{
				result = true;
			}
		}
		
		return result;
	}
}
