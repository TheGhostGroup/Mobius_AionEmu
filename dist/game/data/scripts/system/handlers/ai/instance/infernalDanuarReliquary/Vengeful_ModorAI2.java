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
package system.handlers.ai.instance.infernalDanuarReliquary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("vengeful_modor")
public class Vengeful_ModorAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private final boolean canThink = true;
	private final AtomicBoolean isHome = new AtomicBoolean(true);
	private final List<Integer> percents = new ArrayList<>();
	private final AtomicBoolean startedEvent = new AtomicBoolean(false);
	
	@Override
	public boolean canThink()
	{
		return canThink;
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 25)
			{
				if (startedEvent.compareAndSet(false, true))
				{
					SkillEngine.getInstance().getSkill(getOwner(), 19246, 60, getOwner()).useNoAnimationSkill();
					// It's been quite a while since someone tried to steal the fruits of my research...
					// Could be interesting. I'll give you 15 minutes. Go!
					sendMsg(1500737, getObjectId(), false, 3000);
					// My only son is safe at last.... My son? Where is he?
					sendMsg(1500738, getObjectId(), false, 6000);
					// You will not hinder my craft!
					sendMsg(1500740, getObjectId(), false, 9000);
				}
			}
		}
	}
	
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		if (isHome.compareAndSet(true, false))
		{
			// Be gone!
			sendMsg(1500739, getObjectId(), false, 0);
			startSkillTask();
		}
	}
	
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			75,
			70,
			65,
			60,
			50
		});
	}
	
	private void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch (percent)
				{
					case 75:
					{
						Teleport();
						// Modor has disappeared into another dimension.
						announceAnotherDimension();
						break;
					}
					case 70:
					{
						Teleport2();
						// Modor has disappeared into another dimension.
						announceAnotherDimension();
						break;
					}
					case 65:
					{
						Teleport3();
						startSkillTask();
						// Modor has disappeared into another dimension.
						announceAnotherDimension();
						break;
					}
					case 60:
					{
						Teleport4();
						// Modor has disappeared into another dimension.
						announceAnotherDimension();
						break;
					}
					case 50:
					{
						Teleport5();
						// Modor has disappeared into another dimension.
						announceAnotherDimension();
						break;
					}
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void startSkillTask()
	{
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable) () ->
		{
			if (isAlreadyDead())
			{
				cancelTask();
			}
			else
			{
				chooseRandomEvent();
			}
		}, 5000, 30000);
	}
	
	void cancelTask()
	{
		if ((skillTask != null) && !skillTask.isCancelled())
		{
			skillTask.cancel(true);
		}
	}
	
	private void chooseRandomEvent()
	{
		switch (Rnd.get(1, 2))
		{
			case 1:
			{
				AI2Actions.targetSelf(Vengeful_ModorAI2.this);
				SkillEngine.getInstance().getSkill(getOwner(), 21171, 60, getOwner()).useNoAnimationSkill();
				break;
			}
			case 2:
			{
				AI2Actions.targetSelf(Vengeful_ModorAI2.this);
				SkillEngine.getInstance().getSkill(getOwner(), 21229, 60, getOwner()).useNoAnimationSkill();
				break;
			}
		}
	}
	
	private void Teleport()
	{
		if (!isAlreadyDead())
		{
			// Rise, my children, rise!
			sendMsg(1500749, getObjectId(), false, 2000);
			SkillEngine.getInstance().getSkill(getOwner(), 21165, 60, getOwner()).useNoAnimationSkill();
			ThreadPoolManager.getInstance().schedule((Runnable) () ->
			{
				if (!isAlreadyDead())
				{
					spawn(284380, 244.12497f, 276.17401f, 242.625f, (byte) 0); // Modor's Bodyguard.
					spawn(284381, 263.12497f, 276.17401f, 242.625f, (byte) 0); // Vengeful Reaper.
					spawn(284382, 253.12497f, 277.17401f, 242.625f, (byte) 0); // Hoarfrost Acheron Drake.
					World.getInstance().updatePosition(getOwner(), 284.34036f, 262.9162f, 248.851f, (byte) 63);
					PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
				}
			}, 2000);
		}
	}
	
	private void Teleport2()
	{
		AI2Actions.targetSelf(Vengeful_ModorAI2.this);
		SkillEngine.getInstance().getSkill(getOwner(), 21165, 60, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			final float pos1[][] =
			{
				{
					232.426f,
					263.818f,
					248.6419f,
					115
				},
				{
					271.426f,
					230.243f,
					250.9022f,
					38
				},
				{
					240.130f,
					235.219f,
					251.1553f,
					17
				}
			};
			final float pos[] = pos1[Rnd.get(0, 2)];
			World.getInstance().updatePosition(getOwner(), pos[0], pos[1], pos[2], (byte) pos[3]);
			PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
		}, 2000);
	}
	
	private void Teleport3()
	{
		SkillEngine.getInstance().getSkill(getOwner(), 21165, 60, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			final float pos1[][] =
			{
				{
					245.426f,
					261.818f,
					242.1f,
					114
				},
				{
					251.426f,
					247.243f,
					242.1f,
					20
				},
				{
					261.130f,
					247.219f,
					242.1f,
					40
				},
				{
					267.426f,
					260.243f,
					242.1f,
					65
				},
				{
					256.426f,
					269.243f,
					242.1f,
					90
				}
			};
			final float pos[] = pos1[Rnd.get(0, 4)];
			World.getInstance().updatePosition(getOwner(), pos[0], pos[1], pos[2], (byte) pos[3]);
			PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
		}, 2000);
	}
	
	private void Teleport4()
	{
		AI2Actions.targetSelf(Vengeful_ModorAI2.this);
		SkillEngine.getInstance().getSkill(getOwner(), 21165, 60, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			World.getInstance().updatePosition(getOwner(), 256.4457f, 257.6867f, 242.30f, (byte) 115);
			PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
		}, 2000);
	}
	
	private void Teleport5()
	{
		AI2Actions.targetSelf(Vengeful_ModorAI2.this);
		// Which one, which one...
		sendMsg(1500743, getObjectId(), false, 0);
		// Let's see how you handle this!
		sendMsg(1500744, getObjectId(), false, 2000);
		SkillEngine.getInstance().getSkill(getOwner(), 21165, 60, getOwner()).useNoAnimationSkill();
		EmoteManager.emoteStopAttacking(getOwner());
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			despawnNpcs(234690); // Vengeful Modor.
			spawn(855244, 255.12497f, 293.17401f, 257.625f, (byte) 22);
			spawn(855244, 284.12497f, 262.17401f, 249.625f, (byte) 0);
			spawn(855244, 271.12497f, 230.17401f, 251.625f, (byte) 0);
			spawn(855244, 240.12497f, 235.17401f, 252.625f, (byte) 0);
			spawn(855244, 232.12497f, 263.17401f, 249.625f, (byte) 0);
		}, 2000);
	}
	
	@Override
	protected void handleCreatureAggro(Creature creature)
	{
		super.handleCreatureAggro(creature);
	}
	
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		addPercent();
	}
	
	@Override
	protected void handleDespawned()
	{
		super.handleDespawned();
		percents.clear();
	}
	
	@Override
	protected void handleBackHome()
	{
		addPercent();
		super.handleBackHome();
		isHome.set(true);
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time)
	{
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
	
	private void announceAnotherDimension()
	{
		getPosition().getWorldMapInstance().doOnAllPlayers(player ->
		{
			if (player.isOnline())
			{
				// Modor has disappeared into another dimension.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDLDF5_Under_Rune_User_Kill);
			}
		});
	}
	
	private void despawnNpcs(int npcId)
	{
		final List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
		for (Npc npc : npcs)
		{
			if (npc != null)
			{
				npc.getController().onDelete();
			}
		}
	}
}