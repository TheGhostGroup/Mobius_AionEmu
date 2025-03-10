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
package com.aionemu.gameserver.world;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author xavier
 * @param <T>
 */
public abstract class Executor<T extends AionObject>
{
	private static final Logger log = LoggerFactory.getLogger(Executor.class);
	
	public abstract boolean run(T object);
	
	final void runImpl(Collection<T> objects)
	{
		try
		{
			for (T o : objects)
			{
				if (o != null)
				{
					if (!Executor.this.run(o))
					{
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn(e.getMessage(), e);
		}
	}
	
	public final void execute(Collection<T> objects, boolean now)
	{
		if (now)
		{
			runImpl(objects);
		}
		else
		{
			ThreadPoolManager.getInstance().execute(() -> runImpl(objects));
		}
	}
	
	public final void execute(Collection<T> objects)
	{
		execute(objects, false);
	}
}
