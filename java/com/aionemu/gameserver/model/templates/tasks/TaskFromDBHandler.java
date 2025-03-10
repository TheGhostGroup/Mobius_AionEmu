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
package com.aionemu.gameserver.model.templates.tasks;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.TaskFromDBDAO;

/**
 * @author Divinity
 */
public abstract class TaskFromDBHandler implements Runnable
{
	protected int id;
	protected String params[];
	
	/**
	 * Task's id
	 * @param id
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * Task's param(s)
	 * @param params String[]
	 */
	public void setParam(String params[])
	{
		this.params = params;
	}
	
	/**
	 * The task's name This allow to check with the table column "task"
	 * @return
	 */
	public abstract String getTaskName();
	
	/**
	 * Check if the task's parameters are valid
	 * @return true if valid, false otherwise
	 */
	public abstract boolean isValid();
	
	/**
	 * Retuns {@link com.aionemu.gameserver.dao.TaskFromDBDAO} , just a shortcut
	 */
	protected void setLastActivation()
	{
		DAOManager.getDAO(TaskFromDBDAO.class).setLastActivation(id);
	}
}
