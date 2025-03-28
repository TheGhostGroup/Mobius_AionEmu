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
package com.aionemu.gameserver.dao;

import java.util.ArrayList;

import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;

/**
 * Class that is responsible for storing/loading legion data
 * @author Simple
 */
public abstract class LegionMemberDAO implements IDFactoryAwareDAO
{
	/**
	 * Returns true if name is used, false in other case
	 * @param playerObjId
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isIdUsed(int playerObjId);
	
	/**
	 * Creates legion member in DB
	 * @param legionMember
	 * @return
	 */
	public abstract boolean saveNewLegionMember(LegionMember legionMember);
	
	/**
	 * Stores legion member to DB
	 * @param playerObjId
	 * @param legionMember
	 */
	public abstract void storeLegionMember(int playerObjId, LegionMember legionMember);
	
	/**
	 * Loads a legion member
	 * @param playerObjId
	 * @return LegionMember
	 */
	public abstract LegionMember loadLegionMember(int playerObjId);
	
	/**
	 * Loads an off line legion member by id
	 * @param playerObjId
	 * @return LegionMemberEx
	 */
	public abstract LegionMemberEx loadLegionMemberEx(int playerObjId);
	
	/**
	 * Loads an off line legion member by name
	 * @param playerName
	 * @return LegionMemberEx
	 */
	public abstract LegionMemberEx loadLegionMemberEx(String playerName);
	
	/**
	 * Loads all legion members of a legion
	 * @param legionId
	 * @return ArrayList<Integer>
	 */
	public abstract ArrayList<Integer> loadLegionMembers(int legionId);
	
	/**
	 * Removes legion member and all related data (Done by CASCADE DELETION)
	 * @param playerObjId
	 */
	public abstract void deleteLegionMember(int playerObjId);
	
	/**
	 * Identifier name for all LegionDAO classes
	 * @return LegionDAO.class.getName()
	 */
	@Override
	public final String getClassName()
	{
		return LegionMemberDAO.class.getName();
	}
	
}
