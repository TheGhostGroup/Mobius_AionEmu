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
package system.handlers.ai.instance.steelRakeCabin;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("anikiki")
public class AnikikiAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isStartedWalkEvent = new AtomicBoolean(false);
	
	@Override
	public void think()
	{
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 8)
			{
				if (isStartedWalkEvent.compareAndSet(false, true))
				{
					getSpawnTemplate().setWalkerId("3004600001");
					WalkManager.startWalking(this);
					getOwner().setState(1);
					PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
					spawn(700553, 611f, 481f, 936f, (byte) 90);
					spawn(700553, 657f, 482f, 936f, (byte) 60);
					spawn(700553, 626f, 540f, 936f, (byte) 1);
					spawn(700553, 645f, 534f, 936f, (byte) 75);
					PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900));
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1400262, 3000);
				}
			}
		}
	}
	
	@Override
	protected void handleMoveArrived()
	{
		final int point = getOwner().getMoveController().getCurrentPoint();
		super.handleMoveArrived();
		if (getNpcId() == 219037) // Tamer Anikiki.
		{
			if (point == 8)
			{
				getOwner().setState(64);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
			}
			if (point == 12)
			{
				getSpawnTemplate().setWalkerId(null);
				WalkManager.stopWalking(this);
				AI2Actions.deleteOwner(this);
				spawn(219040, 736.2967f, 510.07104f, 941.4781f, (byte) 72); // Tamer Anikiki.
			}
		}
	}
	
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		if (getNpcId() != 219037) // Tamer Anikiki.
		{
			ThreadPoolManager.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					SkillEngine.getInstance().getSkill(getOwner(), 18189, 20, getOwner()).useNoAnimationSkill();
					getLifeStats().setCurrentHp(getLifeStats().getMaxHp());
				}
			}, 5000);
		}
	}
	
	@Override
	public int modifyOwnerDamage(int damage)
	{
		return 1;
	}
	
	@Override
	public int modifyDamage(int damage)
	{
		return 1;
	}
}