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
package system.handlers.ai.instance.aturamSkyFortress;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author xTz
 */
@AIName("alarm")
public class AlarmAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
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
			if (MathUtil.getDistance(getOwner(), player) <= 15)
			{
				if (startedEvent.compareAndSet(false, true))
				{
					canThink = false;
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1500379, getObjectId(), 0, 0);
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401350, 0);
					getSpawnTemplate().setWalkerId("3002400002");
					WalkManager.startWalking(this);
					getOwner().setState(1);
					PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
					getPosition().getWorldMapInstance().getDoors().get(128).setOpen(true);
					getPosition().getWorldMapInstance().getDoors().get(138).setOpen(true);
					ThreadPoolManager.getInstance().schedule((Runnable) () ->
					{
						if (!isAlreadyDead())
						{
							despawn();
						}
					}, 3000);
				}
			}
		}
	}
	
	void despawn()
	{
		AI2Actions.deleteOwner(this);
	}
}