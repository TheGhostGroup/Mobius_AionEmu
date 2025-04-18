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
package system.handlers.ai.instance.tiamatStronghold;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.GeneralNpcAI2;

@AIName("murugan")
public class MuruganAI2 extends GeneralNpcAI2
{
	private boolean isMove;
	
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		if (getOwner().getNpcId() == 800438)
		{
			NpcShoutsService.getInstance().sendMsg(getOwner(), 390852, getOwner().getObjectId(), 0, 1000);
		}
	}
	
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		checkDistance(this, creature);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		checkDistance(this, creature);
	}
	
	private void checkDistance(NpcAI2 ai, Creature creature)
	{
		if (creature instanceof Player)
		{
			if (MathUtil.isIn3dRange(getOwner(), creature, 15) && !isMove)
			{
				isMove = true;
				openSuramaDoor();
				startWalk((Player) creature);
			}
		}
	}
	
	private void startWalk(Player player)
	{
		final int owner = getOwner().getNpcId();
		if ((owner == 800436) || (owner == 800438))
		{
			return;
		}
		switch (owner)
		{
			case 800435:
			{
				NpcShoutsService.getInstance().sendMsg(getOwner(), 390837, getOwner().getObjectId(), 0, 0);
				NpcShoutsService.getInstance().sendMsg(getOwner(), 390838, getOwner().getObjectId(), 0, 4000);
				break;
			}
		}
		setStateIfNot(AIState.WALKING);
		getOwner().setState(1);
		getMoveController().moveToPoint(838, 1317, 396);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			forQuest(player);
			AI2Actions.deleteOwner(MuruganAI2.this);
		}, 10000);
	}
	
	private void openSuramaDoor()
	{
		if (getOwner().getNpcId() == 800436)
		{
			NpcShoutsService.getInstance().sendMsg(getOwner(), 390835, getOwner().getObjectId(), 0, 0);
			getPosition().getWorldMapInstance().getDoors().get(56).setOpen(true);
			AI2Actions.deleteOwner(this);
		}
	}
	
	void forQuest(Player player)
	{
		final int quest = player.getRace().equals(Race.ELYOS) ? 30708 : 30758;
		final QuestState qs = player.getQuestStateList().getQuestState(quest);
		if ((qs != null) && (qs.getQuestVarById(0) != 5))
		{
			qs.setQuestVar(qs.getQuestVarById(0) + 1);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(quest, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
}