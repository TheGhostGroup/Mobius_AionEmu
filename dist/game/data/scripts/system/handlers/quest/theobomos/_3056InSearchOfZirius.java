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
package system.handlers.quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Balthazar
 */
public class _3056InSearchOfZirius extends QuestHandler
{
	private static final int questId = 3056;
	
	public _3056InSearchOfZirius()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(730147).addOnQuestStart(questId);
		qe.registerQuestNpc(730147).addOnTalkEvent(questId);
		qe.registerQuestNpc(798213).addOnTalkEvent(questId);
		qe.registerQuestNpc(214578).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 730147)
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 4762);
					}
					case STEP_TO_1:
					{
						QuestService.startQuest(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
						return true;
					}
					default:
					{
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		
		if (qs == null)
		{
			return false;
		}
		
		if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 798213:
				{
					switch (env.getDialog())
					{
						case START_DIALOG:
						{
							if (qs.getQuestVarById(0) == 0)
							{
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_2:
						{
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 798213)
			{
				if (env.getDialogId() == 1009)
				{
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if ((qs == null) || (qs.getStatus() != QuestStatus.START))
		{
			return false;
		}
		
		int targetId = 0;
		
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		
		if (targetId == 214578)
		{
			if (qs.getQuestVarById(0) == 1)
			{
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}
