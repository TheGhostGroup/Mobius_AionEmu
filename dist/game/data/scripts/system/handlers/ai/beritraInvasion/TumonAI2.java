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
package system.handlers.ai.beritraInvasion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AbyssLandingService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("tumon")
public class TumonAI2 extends AggressiveNpcAI2
{
	private Future<?> phaseTask;
	private boolean canThink = true;
	private final AtomicBoolean isAggred = new AtomicBoolean(false);
	private final AtomicBoolean isStartedEvent = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true))
		{
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void checkPercentage(int hpPercentage)
	{
		if (hpPercentage <= 95)
		{
			if (isStartedEvent.compareAndSet(false, true))
			{
				startPhaseTask();
			}
		}
		if (hpPercentage <= 70)
		{
			if (isStartedEvent.compareAndSet(false, true))
			{
				startPhaseTask();
			}
		}
		if (hpPercentage <= 50)
		{
			if (isStartedEvent.compareAndSet(false, true))
			{
				startPhaseTask();
			}
		}
		if (hpPercentage <= 30)
		{
			if (isStartedEvent.compareAndSet(false, true))
			{
				startPhaseTask();
			}
		}
		if (hpPercentage <= 10)
		{
			if (isStartedEvent.compareAndSet(false, true))
			{
				startPhaseTask();
			}
		}
	}
	
	private void startPhaseTask()
	{
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable) () ->
		{
			if (isAlreadyDead())
			{
				cancelPhaseTask();
			}
			else
			{
				final List<Player> players = getLifedPlayers();
				if (!players.isEmpty())
				{
					final int size = players.size();
					if (players.size() < 6)
					{
						for (Player p : players)
						{
							spawnCannonBall(p);
						}
					}
					else
					{
						final int count = Rnd.get(6, size);
						for (int i = 0; i < count; i++)
						{
							if (players.isEmpty())
							{
								break;
							}
							spawnCannonBall(players.get(Rnd.get(players.size())));
						}
					}
				}
			}
		}, 20000, 40000);
	}
	
	private void spawnCannonBall(Player player)
	{
		final float x = player.getX();
		final float y = player.getY();
		final float z = player.getZ();
		if ((x > 0) && (y > 0) && (z > 0))
		{
			ThreadPoolManager.getInstance().schedule((Runnable) () ->
			{
				if (!isAlreadyDead())
				{
					spawn(287261, x, y, z, (byte) 0); // Cannon Ball.
				}
			}, 3000);
		}
	}
	
	@Override
	public boolean canThink()
	{
		return canThink;
	}
	
	private List<Player> getLifedPlayers()
	{
		final List<Player> players = new ArrayList<>();
		for (Player player : getKnownList().getKnownPlayers().values())
		{
			if (!CreatureActions.isAlreadyDead(player))
			{
				players.add(player);
			}
		}
		return players;
	}
	
	void cancelPhaseTask()
	{
		if ((phaseTask != null) && !phaseTask.isDone())
		{
			phaseTask.cancel(true);
		}
	}
	
	private void deleteHelpers()
	{
		final WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null)
		{
			deleteNpcs(instance.getNpcs(287261)); // Cannon Ball.
		}
	}
	
	@Override
	protected void handleDied()
	{
		cancelPhaseTask();
		final WorldPosition p = getPosition();
		if (p != null)
		{
			deleteNpcs(p.getWorldMapInstance().getNpcs(287261)); // Cannon Ball.
		}
		switch (getNpcId())
		{
			// Tumon & Prime Tumon & Elite Tumon.
			case 236722:
			case 236726:
			{
				announceTumonDie();
				break;
			}
			// Radeon & Prime Radeon.
			case 234589:
			case 234594:
			{
				announceRadeonDie();
				break;
			}
			case 234590:
			{
				addGpPlayer();
				announceRadeonDie();
				updateTumonLanding1();
				break;
			}
			case 234609:
			{
				addGpPlayer();
				announceRadeonDie();
				break;
			}
			case 234591:
			{
				addGpPlayer();
				announceTumonDie();
				updateTumonLanding2();
				break;
			}
			case 234592:
			case 234615:
			{
				addGpPlayer();
				announceTumonDie();
				break;
			}
		}
		super.handleDied();
	}
	
	private void addGpPlayer()
	{
		World.getInstance().doOnAllPlayers(player ->
		{
			if (MathUtil.isIn3dRange(player, getOwner(), 15))
			{
				AbyssPointsService.addGp(player, 500);
			}
		});
	}
	
	private void updateTumonLanding1()
	{
		World.getInstance().doOnAllPlayers(player ->
		{
			if (MathUtil.isIn3dRange(getOwner().getAggroList().getMostHated(), getOwner(), 20))
			{
				if (getOwner().getAggroList().getPlayerWinnerRace() == Race.ASMODIANS)
				{
					AbyssLandingService.getInstance().onRewardMonuments(Race.ASMODIANS, 21, 10000);
				}
				else if (getOwner().getAggroList().getPlayerWinnerRace() == Race.ELYOS)
				{
					AbyssLandingService.getInstance().onRewardMonuments(Race.ELYOS, 9, 10000);
				}
			}
		});
	}
	
	private void updateTumonLanding2()
	{
		World.getInstance().doOnAllPlayers(player ->
		{
			if (MathUtil.isIn3dRange(getOwner().getAggroList().getMostHated(), getOwner(), 20))
			{
				if (getOwner().getAggroList().getPlayerWinnerRace() == Race.ASMODIANS)
				{
					AbyssLandingService.getInstance().onRewardMonuments(Race.ASMODIANS, 22, 10000);
				}
				else if (getOwner().getAggroList().getPlayerWinnerRace() == Race.ELYOS)
				{
					AbyssLandingService.getInstance().onRewardMonuments(Race.ELYOS, 10, 10000);
				}
			}
		});
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
	
	@Override
	protected void handleDespawned()
	{
		cancelPhaseTask();
		super.handleDespawned();
	}
	
	@Override
	protected void handleBackHome()
	{
		canThink = true;
		cancelPhaseTask();
		deleteHelpers();
		isAggred.set(false);
		isStartedEvent.set(false);
		super.handleBackHome();
	}
	
	private void announceTumonDie()
	{
		World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_WORLDRAID_MESSAGE_DIE_04));
	}
	
	private void announceRadeonDie()
	{
		World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_WORLDRAID_MESSAGE_DIE_05));
	}
}