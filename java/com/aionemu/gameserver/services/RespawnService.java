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
package com.aionemu.gameserver.services;

import java.util.Set;
import java.util.concurrent.Future;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer, Source, xTz
 */
public class RespawnService
{
	private static final int IMMEDIATE_DECAY = 5 * 1000;
	private static final int WITHOUT_DROP_DECAY = 0;
	private static final int WITH_DROP_DECAY = 5 * 60 * 1000;
	
	/**
	 * @param npc
	 * @return Future<?>
	 */
	public static Future<?> scheduleDecayTask(Npc npc)
	{
		int decayInterval;
		final Set<DropItem> drop = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		
		if (drop == null)
		{
			decayInterval = IMMEDIATE_DECAY;
		}
		else if (drop.isEmpty())
		{
			decayInterval = WITHOUT_DROP_DECAY;
		}
		else
		{
			decayInterval = WITH_DROP_DECAY;
		}
		
		return scheduleDecayTask(npc, decayInterval);
	}
	
	public static Future<?> scheduleDecayTask(Npc npc, long decayInterval)
	{
		return ThreadPoolManager.getInstance().schedule(new DecayTask(npc.getObjectId()), decayInterval);
	}
	
	/**
	 * @param visibleObject
	 * @return
	 */
	public static Future<?> scheduleRespawnTask(VisibleObject visibleObject)
	{
		final int interval = visibleObject.getSpawn().getRespawnTime();
		final SpawnTemplate spawnTemplate = visibleObject.getSpawn();
		final int instanceId = visibleObject.getInstanceId();
		return ThreadPoolManager.getInstance().schedule(new RespawnTask(spawnTemplate, instanceId), interval * 1000);
	}
	
	/**
	 * @param spawnTemplate
	 * @param instanceId
	 * @return
	 */
	static VisibleObject respawn(SpawnTemplate spawnTemplate, int instanceId)
	{
		if (spawnTemplate.isTemporarySpawn() && !spawnTemplate.getTemporarySpawn().canSpawn() && !spawnTemplate.getTemporarySpawn().isInSpawnTime())
		{
			return null;
		}
		
		final int worldId = spawnTemplate.getWorldId();
		final boolean instanceExists = InstanceService.isInstanceExist(worldId, instanceId);
		if (spawnTemplate.isNoRespawn() || !instanceExists)
		{
			return null;
		}
		
		if (spawnTemplate.hasPool())
		{
			spawnTemplate = spawnTemplate.changeTemplate(instanceId);
		}
		return SpawnEngine.spawnObject(spawnTemplate, instanceId);
	}
	
	private static class DecayTask implements Runnable
	{
		
		private final int npcId;
		
		DecayTask(int npcId)
		{
			this.npcId = npcId;
		}
		
		@Override
		public void run()
		{
			final VisibleObject visibleObject = World.getInstance().findVisibleObject(npcId);
			if (visibleObject != null)
			{
				visibleObject.getController().onDelete();
			}
		}
	}
	
	private static class RespawnTask implements Runnable
	{
		
		private final SpawnTemplate spawn;
		private final int instanceId;
		
		RespawnTask(SpawnTemplate spawn, int instanceId)
		{
			this.spawn = spawn;
			this.instanceId = instanceId;
		}
		
		@Override
		public void run()
		{
			final VisibleObject visibleObject = spawn.getVisibleObject();
			if ((visibleObject != null) && (visibleObject instanceof Npc))
			{
				((Npc) visibleObject).getController().cancelTask(TaskId.RESPAWN);
			}
			respawn(spawn, instanceId);
		}
	}
	
}
