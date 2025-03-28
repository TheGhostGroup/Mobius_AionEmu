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
package system.handlers.quest.heiron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */
public class _1540BaittheHooks extends QuestHandler
{
	private static final int questId = 1540;
	
	public _1540BaittheHooks()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(204588).addOnQuestStart(questId);
		qe.registerQuestNpc(204588).addOnTalkEvent(questId);
		qe.registerQuestNpc(730189).addOnTalkEvent(questId);
		qe.registerQuestNpc(730190).addOnTalkEvent(questId);
		qe.registerQuestNpc(730191).addOnTalkEvent(questId);
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
			if (targetId == 204588)
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						return sendQuestDialog(env, 1011);
					}
					case ACCEPT_QUEST:
					{
						if (player.getInventory().getItemCountByItemId(182201822) == 0)
						{
							if (!giveQuestItem(env, 182201822, 1))
							{
								return true;
							}
						}
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
				case 730189:
				{
					switch (env.getDialog())
					{
						case USE_OBJECT:
						{
							if (player.getInventory().getItemCountByItemId(182201822) == 1)
							{
								return useQuestObject(env, 0, 1, false, 0); // 1
							}
						}
					}
				}
				case 730190:
				{
					switch (env.getDialog())
					{
						case USE_OBJECT:
						{
							if (player.getInventory().getItemCountByItemId(182201822) == 1)
							{
								return useQuestObject(env, 1, 2, false, 0); // 2
							}
						}
					}
				}
				case 730191:
				{
					switch (env.getDialog())
					{
						case USE_OBJECT:
						{
							if ((qs.getQuestVarById(0) == 2) && (player.getInventory().getItemCountByItemId(182201822) == 1))
							{
								qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								removeQuestItem(env, 182201822, 1);
								return true;
							}
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 204588)
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
}
