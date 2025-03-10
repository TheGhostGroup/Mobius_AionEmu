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
package system.handlers.ai.instance.empyreanCrucible;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("priest_elyos_preceptor")
public class PriestElyosPreceptorAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean is75EventStarted = new AtomicBoolean(false);
	private final AtomicBoolean is25EventStarted = new AtomicBoolean(false);
	
	@Override
	public void handleSpawned()
	{
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule((Runnable) () -> SkillEngine.getInstance().getSkill(getOwner(), 19612, 46, getOwner()).useNoAnimationSkill(), 1000);
	}
	
	@Override
	public void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void checkPercentage(int percentage)
	{
		if (percentage <= 75)
		{
			if (is75EventStarted.compareAndSet(false, true))
			{
				SkillEngine.getInstance().getSkill(getOwner(), 19611, 46, getTargetPlayer()).useNoAnimationSkill();
			}
		}
		if (percentage <= 25)
		{
			if (is25EventStarted.compareAndSet(false, true))
			{
				startEvent();
			}
		}
	}
	
	private void startEvent()
	{
		SkillEngine.getInstance().getSkill(getOwner(), 19610, 46, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			SkillEngine.getInstance().getSkill(getOwner(), 19614, 46, getOwner()).useNoAnimationSkill();
			ThreadPoolManager.getInstance().schedule((Runnable) () ->
			{
				final WorldPosition p = getPosition();
				switch (Rnd.get(1, 3))
				{
					case 1:
					{
						applySoulSickness((Npc) spawn(282366, p.getX(), p.getY(), p.getZ(), p.getHeading())); // Boreas.
						break;
					}
					case 2:
					{
						applySoulSickness((Npc) spawn(282367, p.getX(), p.getY(), p.getZ(), p.getHeading())); // Jumentis.
						break;
					}
					case 3:
					{
						applySoulSickness((Npc) spawn(282368, p.getX(), p.getY(), p.getZ(), p.getHeading())); // Charna.
						break;
					}
				}
			}, 5000);
		}, 2000);
	}
	
	private Player getTargetPlayer()
	{
		final List<Player> players = new ArrayList<>();
		for (Player player : getKnownList().getKnownPlayers().values())
		{
			if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 25))
			{
				players.add(player);
			}
		}
		return players.get(Rnd.get(players.size()));
	}
	
	void applySoulSickness(Npc npc)
	{
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			npc.getLifeStats().setCurrentHpPercent(50);
			SkillEngine.getInstance().getSkill(npc, 19594, 4, npc).useNoAnimationSkill();
		}, 1000);
	}
	
	@Override
	protected void handleDied()
	{
		final WorldPosition p = getPosition();
		if (p != null)
		{
			deleteNpcs(p.getWorldMapInstance().getNpcs(282366)); // Boreas.
			deleteNpcs(p.getWorldMapInstance().getNpcs(282367)); // Jumentis.
			deleteNpcs(p.getWorldMapInstance().getNpcs(282368)); // Charna.
		}
		super.handleDied();
	}
	
	@Override
	protected void handleBackHome()
	{
		final WorldPosition p = getPosition();
		if (p != null)
		{
			deleteNpcs(p.getWorldMapInstance().getNpcs(282366)); // Boreas.
			deleteNpcs(p.getWorldMapInstance().getNpcs(282367)); // Jumentis.
			deleteNpcs(p.getWorldMapInstance().getNpcs(282368)); // Charna.
		}
		is75EventStarted.set(false);
		is25EventStarted.set(false);
		super.handleDied();
	}
	
	private void deleteNpcs(List<Npc> npcs)
	{
		for (Npc npc : npcs)
		{
			if (npc != null)
			{
				npc.getController().onDelete();
			}
		}
	}
}