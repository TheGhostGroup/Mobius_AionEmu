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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.housing.HousePart;

/**
 * @author Rolandas
 */
public class HouseDecoration extends AionObject
{
	private final int templateId;
	private byte floor;
	private boolean isUsed;
	private PersistentState persistentState;
	
	public HouseDecoration(int objectId, int templateId)
	{
		this(objectId, templateId, -1);
	}
	
	public HouseDecoration(int objectId, int templateId, int floor)
	{
		super(objectId);
		this.templateId = templateId;
		this.floor = (byte) floor;
		persistentState = PersistentState.NEW;
	}
	
	public HousePart getTemplate()
	{
		return DataManager.HOUSE_PARTS_DATA.getPartById(templateId);
	}
	
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	public void setPersistentState(PersistentState persistentState)
	{
		this.persistentState = persistentState;
	}
	
	@Override
	public String getName()
	{
		return getTemplate().getName();
	}
	
	public byte getFloor()
	{
		return floor;
	}
	
	public void setFloor(int value)
	{
		if (value != floor)
		{
			floor = (byte) value;
			if ((persistentState != PersistentState.NEW) && (persistentState != PersistentState.NOACTION))
			{
				persistentState = PersistentState.UPDATE_REQUIRED;
			}
		}
	}
	
	public boolean isUsed()
	{
		return isUsed;
	}
	
	public void setUsed(boolean isUsed)
	{
		if ((this.isUsed != isUsed) && (persistentState != PersistentState.DELETED))
		{
			this.isUsed = isUsed;
			if ((persistentState != PersistentState.NEW) && (persistentState != PersistentState.NOACTION))
			{
				persistentState = PersistentState.UPDATE_REQUIRED;
			}
		}
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof HouseDecoration))
		{
			return false;
		}
		return ((HouseDecoration) object).getObjectId().equals(getObjectId());
	}
	
	@Override
	public int hashCode()
	{
		return getObjectId();
	}
}