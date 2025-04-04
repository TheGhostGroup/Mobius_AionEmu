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

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Luzien
 */
@AIName("spectral_warrior")
public class SpectralWarriorAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isDone = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void checkPercentage(int hpPercentage)
	{
		if ((hpPercentage <= 50) && isDone.compareAndSet(false, true))
		{
			getPosition().getWorldMapInstance().getInstanceHandler().onChangeStage(StageType.START_STAGE_6_ROUND_5);
			ThreadPoolManager.getInstance().schedule(() -> resurrectAllies(), 2000);
		}
	}
	
	void resurrectAllies()
	{
		for (VisibleObject obj : getKnownList().getKnownObjects().values())
		{
			if (obj instanceof Npc)
			{
				final Npc npc = (Npc) obj;
				if (CreatureActions.isAlreadyDead(npc))
				{
					continue;
				}
				switch (npc.getNpcId())
				{
					case 205413:
					{
						spawn(217576, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
						CreatureActions.delete(npc);
						break;
					}
					case 205414:
					{
						spawn(217577, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
						CreatureActions.delete(npc);
						break;
					}
				}
			}
		}
	}
}