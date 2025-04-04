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
package system.handlers.ai.instance.beshmundirTemple;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author xTz
 */
@AIName("monolithicambusher")
public class MonolithicAmbusherAI2 extends AggressiveNpcAI2
{
	private boolean hasHelped;
	
	@Override
	protected void handleBackHome()
	{
		super.handleBackHome();
		hasHelped = false;
	}
	
	@Override
	protected void handleCreatureAggro(Creature creature)
	{
		super.handleCreatureAggro(creature);
		if (!hasHelped)
		{
			hasHelped = true;
			help(creature);
		}
	}
	
	private void help(Creature creature)
	{
		for (VisibleObject object : getKnownList().getKnownObjects().values())
		{
			if ((object instanceof Npc) && isInRange(object, 60))
			{
				final Npc npc = (Npc) object;
				if (!npc.getLifeStats().isAlreadyDead() && (npc.getNpcId() == 216215) && ((int) npc.getSpawn().getY() == (int) getSpawnTemplate().getY()))
				{
					npc.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
				}
			}
		}
	}
}