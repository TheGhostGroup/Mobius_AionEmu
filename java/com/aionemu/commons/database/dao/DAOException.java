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
package com.aionemu.commons.database.dao;

/**
 * Generic DAO exception class
 * @author SoulKeeper
 */
public class DAOException extends RuntimeException
{
	public DAOException()
	{
	}
	
	/**
	 * @param message
	 */
	public DAOException(String message)
	{
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public DAOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public DAOException(Throwable cause)
	{
		super(cause);
	}
}
