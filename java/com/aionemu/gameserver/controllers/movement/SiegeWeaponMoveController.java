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
package com.aionemu.gameserver.controllers.movement;

import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.taskmanager.tasks.MoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * @author xTz
 */
public class SiegeWeaponMoveController extends SummonMoveController
{
	private float pointX;
	private float pointY;
	private float pointZ;
	private final float offset = 0.1f;
	public static final float MOVE_CHECK_OFFSET = 0.1f;
	
	public SiegeWeaponMoveController(Summon owner)
	{
		super(owner);
	}
	
	@Override
	public void moveToDestination()
	{
		if (!owner.canPerformMove() || (owner.getAi2().getSubState() == AISubState.CAST))
		{
			if (started.compareAndSet(true, false))
			{
				setAndSendStopMove(owner);
			}
			updateLastMove();
			return;
		}
		else if (started.compareAndSet(false, true))
		{
			movementMask = -32;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
		
		if (MathUtil.getDistance(owner.getTarget(), pointX, pointY, pointZ) > MOVE_CHECK_OFFSET)
		{
			pointX = owner.getTarget().getX();
			pointY = owner.getTarget().getY();
			pointZ = owner.getTarget().getZ();
		}
		moveToLocation(pointX, pointY, pointZ, offset);
		updateLastMove();
	}
	
	@Override
	public void moveToTargetObject()
	{
		updateLastMove();
		MoveTaskManager.getInstance().addCreature(owner);
	}
	
	protected void moveToLocation(float targetX, float targetY, float targetZ, float offset)
	{
		boolean directionChanged;
		final float ownerX = owner.getX();
		final float ownerY = owner.getY();
		final float ownerZ = owner.getZ();
		
		directionChanged = (targetX != targetDestX) || (targetY != targetDestY) || (targetZ != targetDestZ);
		
		if (directionChanged)
		{
			heading = (byte) (Math.toDegrees(Math.atan2(targetY - ownerY, targetX - ownerX)) / 3);
		}
		
		targetDestX = targetX;
		targetDestY = targetY;
		targetDestZ = targetZ;
		
		final float currentSpeed = owner.getGameStats().getMovementSpeedFloat();
		float futureDistPassed = (currentSpeed * (System.currentTimeMillis() - lastMoveUpdate)) / 1000f;
		
		final float dist = (float) MathUtil.getDistance(ownerX, ownerY, ownerZ, targetX, targetY, targetZ);
		
		if (dist == 0)
		{
			return;
		}
		
		if (futureDistPassed > dist)
		{
			futureDistPassed = dist;
		}
		
		final float distFraction = futureDistPassed / dist;
		final float newX = ((targetDestX - ownerX) * distFraction) + ownerX;
		final float newY = ((targetDestY - ownerY) * distFraction) + ownerY;
		final float newZ = ((targetDestZ - ownerZ) * distFraction) + ownerZ;
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		if (directionChanged)
		{
			movementMask = -32;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
	}
}
