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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer Effector: Player only
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DpUseAction")
public class DpUseAction extends Action
{
	@XmlAttribute(required = true)
	protected int value;
	
	@Override
	public void act(Skill skill)
	{
		final Player effector = (Player) skill.getEffector();
		final int currentDp = effector.getCommonData().getDp();
		
		if ((currentDp <= 0) || (currentDp < value))
		{
			return;
		}
		
		effector.getCommonData().setDp(currentDp - value);
	}
}
