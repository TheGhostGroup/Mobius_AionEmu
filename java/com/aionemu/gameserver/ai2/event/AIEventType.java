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
package com.aionemu.gameserver.ai2.event;

/**
 * @author ATracer
 */
public enum AIEventType
{
	ACTIVATE,
	DEACTIVATE,
	FREEZE,
	UNFREEZE,
	/**
	 * Creature is being attacked (internal)
	 */
	ATTACK,
	/**
	 * Creature's attack part is complete (internal)
	 */
	ATTACK_COMPLETE,
	/**
	 * Creature's stopping attack (internal)
	 */
	ATTACK_FINISH,
	/**
	 * Some neighbor creature is being attacked (broadcast)
	 */
	CREATURE_NEEDS_SUPPORT,
	
	/**
	 * Creature is attacking (broadcast)
	 */
	
	MOVE_VALIDATE,
	MOVE_ARRIVED,
	
	CREATURE_SEE,
	CREATURE_NOT_SEE,
	CREATURE_MOVED,
	CREATURE_AGGRO,
	SPAWNED,
	RESPAWNED,
	DESPAWNED,
	DIED,
	
	TARGET_REACHED,
	TARGET_TOOFAR,
	TARGET_GIVEUP,
	TARGET_CHANGED,
	FOLLOW_ME,
	STOP_FOLLOW_ME,
	
	NOT_AT_HOME,
	BACK_HOME,
	
	DIALOG_START,
	DIALOG_FINISH,
	
	DROP_REGISTERED
}
