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
package system.handlers.ai.worlds.heiron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldMapInstance;

import system.handlers.ai.AggressiveFirstSkillAI2;

/**
 * @author Ritsu
 */
@AIName("bollvig")
public class BollvigAI2 extends AggressiveFirstSkillAI2
{
	protected List<Integer> percents = new ArrayList<>();
	private Future<?> firstTask;
	private Future<?> secondTask;
	private Future<?> thirdTask;
	private Future<?> lastTask;
	
	@Override
	protected void handleSpawned()
	{
		addPercent();
		super.handleSpawned();
		final Npc npc = getPosition().getWorldMapInstance().getNpc(204655);
		if (npc != null)
		{
			npc.getController().onDelete();
		}
	}
	
	@Override
	protected void handleRespawned()
	{
		addPercent();
		super.handleRespawned();
		final Npc npc = getPosition().getWorldMapInstance().getNpc(204655);
		if (npc != null)
		{
			npc.getController().onDelete();
		}
	}
	
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private synchronized void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch (percent)
				{
					case 75:
					case 50:
					{
						cancelTask();
						useFirstSkillTree();
						break;
					}
					case 25:
					{
						cancelTask();
						firstSkill();
						break;
					}
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void useFirstSkillTree()
	{
		useSkill(17861);
		rndSpawnInRange(280802);
		rndSpawnInRange(280802);
		rndSpawnInRange(280803);
		rndSpawnInRange(280803);
		firstSkill();
	}
	
	private void firstSkill()
	{
		final int hpPercent = getLifeStats().getHpPercentage();
		if ((50 >= hpPercent) && (hpPercent > 25))
		{
			firstTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
			{
				useSkill(18034);
				rndSpawnInRange(280804);
			}, 10000);
		}
		else if (hpPercent <= 25)
		{
			useSkill(18037);
		}
		secondTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> skillThree(), 31000);
	}
	
	private void skillThree()
	{
		useSkill(17899);
		thirdTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			final int hpPercent = getLifeStats().getHpPercentage();
			if ((75 >= hpPercent) && (hpPercent > 50))
			{
				useSkill(18025);
				firstSkill();
			}
			else if (50 >= hpPercent)
			{
				useSkill(18025);
				firstSkill();
			}
			else if (25 >= hpPercent)
			{
				useSkill(18027);
				lastTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> skillThree(), 11000);
			}
		}, 5000);
	}
	
	private void cancelTask()
	{
		if ((firstTask != null) && !firstTask.isDone())
		{
			firstTask.cancel(true);
		}
		else if ((secondTask != null) && !secondTask.isDone())
		{
			secondTask.cancel(true);
		}
		else if ((thirdTask != null) && !thirdTask.isDone())
		{
			thirdTask.cancel(true);
		}
		else if ((lastTask != null) && !lastTask.isDone())
		{
			lastTask.cancel(true);
		}
	}
	
	private void rndSpawnInRange(int npcId)
	{
		final float direction = Rnd.get(0, 199) / 100f;
		final float x = (float) (Math.cos(Math.PI * direction) * 10);
		final float y = (float) (Math.sin(Math.PI * direction) * 10);
		spawn(npcId, 1001 + x, 2828 + y, 235.66f, (byte) 0);
	}
	
	private void useSkill(int skillId)
	{
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}
	
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			75,
			50,
			25
		});
	}
	
	@Override
	protected void handleBackHome()
	{
		addPercent();
		cancelTask();
		super.handleBackHome();
	}
	
	@Override
	protected void handleDespawned()
	{
		percents.clear();
		cancelTask();
		deleteSummons(280802);
		deleteSummons(280803);
		deleteSummons(280804);
		super.handleDespawned();
		if (checkNpc())
		{
			spawn(204655, 1001f, 2828f, 235.66f, (byte) 0);
		}
	}
	
	@Override
	protected void handleDied()
	{
		percents.clear();
		cancelTask();
		deleteSummons(280802);
		deleteSummons(280803);
		deleteSummons(280804);
		super.handleDied();
		if (checkNpc())
		{
			spawn(204655, 1001f, 2828f, 235.66f, (byte) 0);
		}
	}
	
	private void deleteSummons(int npcId)
	{
		if (getPosition().getWorldMapInstance().getNpcs(npcId) != null)
		{
			final List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
			for (Npc npc : npcs)
			{
				npc.getController().onDelete();
			}
		}
	}
	
	private boolean checkNpc()
	{
		final WorldMapInstance map = getPosition().getWorldMapInstance();
		if ((map.getNpc(204655) == null) && ((map.getNpc(212314) == null) || map.getNpc(212314).getLifeStats().isAlreadyDead()))
		{
			return true;
		}
		return false;
	}
}