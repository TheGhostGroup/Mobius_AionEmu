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
package system.handlers.ai.worlds.morheim;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("chieftain_muhamurru")
public class ChieftainMuhamurruAI2 extends AggressiveNpcAI2
{
	private Future<?> hideTask;
	final AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	public void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false))
		{
			sendMsg(1500397);
			startHideTask();
		}
	}
	
	void cancelPhaseTask()
	{
		if ((hideTask != null) && !hideTask.isDone())
		{
			hideTask.cancel(true);
		}
	}
	
	private void startHideTask()
	{
		hideTask = ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable) () ->
		{
			if (isAlreadyDead())
			{
				cancelPhaseTask();
			}
			else
			{
				SkillEngine.getInstance().getSkill(getOwner(), 19660, 60, getOwner()).useNoAnimationSkill();
				sendMsg(1500398);
				startEvent(2000, 1500399, 19661);
				startEvent(6000, 1500399, 19661);
				startEvent(8000, 1500400, 19662);
			}
		}, 14000, 14000);
	}
	
	void startEvent(int time, int msg, int skill)
	{
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			if (!isAlreadyDead() && !isHome.get())
			{
				Creature target = getOwner();
				if (skill == 19661)
				{
					final VisibleObject npcTarget = target.getTarget();
					if ((npcTarget != null) && (npcTarget instanceof Creature))
					{
						target = (Creature) npcTarget;
					}
				}
				if ((target != null) && isInRange(target, 5))
				{
					SkillEngine.getInstance().getSkill(getOwner(), skill, 60, target).useNoAnimationSkill();
				}
				getEffectController().removeEffect(19660);
				sendMsg(msg);
			}
		}, time);
	}
	
	void sendMsg(int msg)
	{
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
	}
	
	@Override
	protected void handleDied()
	{
		cancelPhaseTask();
		sendMsg(1500401);
		super.handleDied();
	}
	
	@Override
	protected void handleDespawned()
	{
		cancelPhaseTask();
		super.handleDespawned();
	}
	
	@Override
	protected void handleBackHome()
	{
		getEffectController().removeEffect(19660);
		cancelPhaseTask();
		isHome.set(true);
		super.handleBackHome();
	}
}