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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.StateEvents;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.FollowEventHandler;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author ATracer
 */
@AIName("following")
public class FollowingNpcAI2 extends GeneralNpcAI2
{
	@Override
	protected void handleFollowMe(Creature creature)
	{
		FollowEventHandler.follow(this, creature);
	}
	
	@Override
	protected boolean canHandleEvent(AIEventType eventType)
	{
		switch (getState())
		{
			case DESPAWNED:
			{
				return StateEvents.DESPAWN_EVENTS.hasEvent(eventType);
			}
			case DIED:
			{
				return StateEvents.DEAD_EVENTS.hasEvent(eventType);
			}
			case CREATED:
			{
				return StateEvents.CREATED_EVENTS.hasEvent(eventType);
			}
		}
		if (eventType == AIEventType.CREATURE_MOVED)
		{
			return getState() == AIState.FOLLOWING;
		}
		return true;
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		if (creature == getOwner().getTarget())
		{
			FollowEventHandler.creatureMoved(this, creature);
		}
	}
	
	@Override
	protected void handleStopFollowMe(Creature creature)
	{
		FollowEventHandler.stopFollow(this, creature);
	}
}