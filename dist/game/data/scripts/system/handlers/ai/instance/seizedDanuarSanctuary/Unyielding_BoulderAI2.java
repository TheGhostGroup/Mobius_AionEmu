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
package system.handlers.ai.instance.seizedDanuarSanctuary;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.skillengine.SkillEngine;

/**
 * @author Rinzler (Encom)
 */
@AIName("unyielding_boulder")
public class Unyielding_BoulderAI2 extends NpcAI2
{
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		SkillEngine.getInstance().getSkill(getOwner(), 22776, 1, getOwner()).useNoAnimationSkill();
		SkillEngine.getInstance().getSkill(getOwner(), 22783, 1, getOwner()).useNoAnimationSkill();
	}
	
	@Override
	public boolean isMoveSupported()
	{
		return false;
	}
}