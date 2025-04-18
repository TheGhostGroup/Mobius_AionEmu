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
package com.aionemu.gameserver.services.vortexservice;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.model.vortex.VortexStateType;
import com.aionemu.gameserver.services.VortexService;

import javolution.util.FastMap;

/**
 * @author Source
 * @param <VL>
 */
public abstract class DimensionalVortex<VL extends VortexLocation>
{
	private final VL vortexLocation;
	private final GeneratorDestroyListener generatorDestroyListener = new GeneratorDestroyListener(this);
	private final AtomicBoolean finished = new AtomicBoolean();
	private boolean generatorDestroyed;
	private Npc generator;
	private boolean started;
	
	protected abstract void startInvasion();
	
	protected abstract void stopInvasion();
	
	public abstract void addPlayer(Player player, boolean isInvader);
	
	public abstract void kickPlayer(Player player, boolean isInvader);
	
	public abstract void updateDefenders(Player defender);
	
	public abstract void updateInvaders(Player invader);
	
	public abstract FastMap<Integer, Player> getDefenders();
	
	public abstract FastMap<Integer, Player> getInvaders();
	
	public DimensionalVortex(VL vortexLocation)
	{
		this.vortexLocation = vortexLocation;
	}
	
	public final void start()
	{
		boolean doubleStart = false;
		synchronized (this)
		{
			if (started)
			{
				doubleStart = true;
			}
			else
			{
				started = true;
			}
		}
		if (doubleStart)
		{
			return;
		}
		startInvasion();
	}
	
	public final void stop()
	{
		if (finished.compareAndSet(false, true))
		{
			stopInvasion();
		}
	}
	
	protected void initRiftGenerator()
	{
		Npc gen = null;
		for (VisibleObject obj : getVortexLocation().getSpawned())
		{
			final int npcId = ((Npc) obj).getNpcId();
			if ((npcId == 209486) || (npcId == 209487))
			{
				gen = (Npc) obj;
			}
		}
		if (gen == null)
		{
			throw new NullPointerException("No generator was found in loc:" + getVortexLocationId());
		}
		setGenerator(gen);
		// registerSiegeBossListeners(); // FIXME? init listeners - EnhancedObject cast
	}
	
	protected void spawn(VortexStateType type)
	{
		VortexService.getInstance().spawn(getVortexLocation(), type);
	}
	
	protected void despawn()
	{
		VortexService.getInstance().despawn(getVortexLocation());
	}
	
	// FIXME? init listeners - EnhancedObject cast
	// protected void registerSiegeBossListeners()
	// {
	// final AbstractAI ai = (AbstractAI) getGenerator().getAi2();
	// final EnhancedObject eo = (EnhancedObject) ai;
	// eo.addCallback(getGeneratorDestroyListener());
	// }
	
	// FIXME? init listeners - EnhancedObject cast
	// protected void unregisterSiegeBossListeners()
	// {
	// final AbstractAI ai = (AbstractAI) getGenerator().getAi2();
	// final EnhancedObject eo = (EnhancedObject) ai;
	// eo.removeCallback(getGeneratorDestroyListener());
	// }
	
	public boolean isGeneratorDestroyed()
	{
		return generatorDestroyed;
	}
	
	public void setGeneratorDestroyed(boolean state)
	{
		this.generatorDestroyed = state;
	}
	
	public Npc getGenerator()
	{
		return generator;
	}
	
	public void setGenerator(Npc generator)
	{
		this.generator = generator;
	}
	
	public GeneratorDestroyListener getGeneratorDestroyListener()
	{
		return generatorDestroyListener;
	}
	
	public boolean isFinished()
	{
		return finished.get();
	}
	
	public VL getVortexLocation()
	{
		return vortexLocation;
	}
	
	public int getVortexLocationId()
	{
		return vortexLocation.getId();
	}
}