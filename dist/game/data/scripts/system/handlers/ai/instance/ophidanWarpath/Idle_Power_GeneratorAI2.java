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
package system.handlers.ai.instance.ophidanWarpath;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author Rinzler (Encom)
 */
@AIName("idle_power_generator")
public class Idle_Power_GeneratorAI2 extends NpcAI2
{
	@Override
	protected void handleSpawned()
	{
		switch (getNpcId())
		{
			case 806391: // North Power Generator.
			{
				ThreadPoolManager.getInstance().schedule(() ->
				{
					announceWarNeu01();
					spawn(833935, 589.974180f, 407.85278f, 610.20313f, (byte) 0, 3);
				}, 300000); // 5 Minutes.
				break;
			}
			case 806392: // South Power Generator.
			{
				ThreadPoolManager.getInstance().schedule(() ->
				{
					announceWarNeu01();
					spawn(833936, 605.049130f, 553.60150f, 591.49310f, (byte) 0, 42);
				}, 300000); // 5 Minutes.
				break;
			}
		}
		super.handleSpawned();
	}
	
	void announceWarNeu01()
	{
		World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDLDF5_Under_02_war_neu_01));
	}
}