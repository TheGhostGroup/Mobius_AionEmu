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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 */
public class StaticObject extends VisibleObject
{
	public StaticObject(int objectId, StaticObjectController controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate)
	{
		super(objectId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		controller.setOwner(this);
	}
	
	@Override
	public String getName()
	{
		return objectTemplate.getName();
	}
}
