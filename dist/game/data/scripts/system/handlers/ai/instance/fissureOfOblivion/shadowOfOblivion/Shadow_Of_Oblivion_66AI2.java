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
package system.handlers.ai.instance.fissureOfOblivion.shadowOfOblivion;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * @author Rinzler (Encom)
 */
@AIName("Shadow_Of_Oblivion_66")
public class Shadow_Of_Oblivion_66AI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void checkPercentage(int hpPercentage)
	{
		if (hpPercentage <= 70)
		{
			ShadowOfOblivionType();
			announceShadowOfOblivion();
			AI2Actions.deleteOwner(this);
		}
	}
	
	private void announceShadowOfOblivion()
	{
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (player.isOnline())
				{
					// Shadow of Oblivion is transforming.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1403699));
				}
			}
		});
	}
	
	private void ShadowOfOblivionType()
	{
		SkillEngine.getInstance().getSkill(getOwner(), 18277, 60, getOwner()).useNoAnimationSkill(); // Oblivion.
		switch (Rnd.get(1, 4))
		{
			case 1:
			{
				spawn(244491, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading());
				break;
			}
			case 2:
			{
				spawn(244492, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading());
				break;
			}
			case 3:
			{
				spawn(244493, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading());
				break;
			}
			case 4:
			{
				spawn(244494, getOwner().getX(), getOwner().getY(), getOwner().getZ(), getOwner().getHeading());
				break;
			}
		}
	}
}