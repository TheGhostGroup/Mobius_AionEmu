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
package system.handlers.quest.mission;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler (Encom)
 */
public class _14052Restless_Souls extends QuestHandler
{
	private static final int questId = 14052;
	private static final int[] npc_ids =
	{
		204629,
		204625,
		204628,
		204627,
		204626,
		204622,
		700270
	};
	
	public _14052Restless_Souls()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids)
		{
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env)
	{
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env, 14051, true);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 204629)
			{
				return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() != QuestStatus.START)
		{
			return false;
		}
		if (targetId == 204629)
		{
			switch (env.getDialog())
			{
				case START_DIALOG:
				{
					if (var == 0)
					{
						return sendQuestDialog(env, 1011);
					}
					else if (var == 2)
					{
						return sendQuestDialog(env, 1693);
					}
				}
				case STEP_TO_1:
				{
					if (var == 0)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				}
				case STEP_TO_2:
				{
					if (var == 1)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
				}
			}
		}
		else if (targetId == 204625)
		{
			switch (env.getDialog())
			{
				case START_DIALOG:
				{
					if (var == 1)
					{
						return sendQuestDialog(env, 1352);
					}
					else if (var == 2)
					{
						return sendQuestDialog(env, 1693);
					}
					else if (var == 4)
					{
						return sendQuestDialog(env, 2375);
					}
				}
				case CHECK_COLLECTED_ITEMS:
				{
					if (QuestService.collectItemCheck(env, true))
					{
						if (!giveQuestItem(env, 182215344, 1))
						{
							return true;
						}
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return sendQuestDialog(env, 10000);
					}
					return sendQuestDialog(env, 10001);
				}
				case STEP_TO_2:
				{
					if (var == 1)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				}
				case SET_REWARD:
				{
					if (var == 4)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
				}
			}
		}
		else if (targetId == 204628)
		{
			switch (env.getDialog())
			{
				case START_DIALOG:
				{
					if (var == 2)
					{
						return sendQuestDialog(env, 1694);
					}
				}
				case STEP_TO_3:
				{
					if (var == 2)
					{
						if (player.getInventory().getItemCountByItemId(182215340) == 0)
						{
							if (!giveQuestItem(env, 182215340, 1))
							{
								return true;
							}
						}
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
				}
			}
		}
		else if (targetId == 204627)
		{
			switch (env.getDialog())
			{
				case START_DIALOG:
				{
					if (var == 2)
					{
						return sendQuestDialog(env, 1781);
					}
				}
				case STEP_TO_3:
				{
					if (var == 2)
					{
						if (player.getInventory().getItemCountByItemId(182215341) == 0)
						{
							if (!giveQuestItem(env, 182215341, 1))
							{
								return true;
							}
						}
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
				}
			}
		}
		else if (targetId == 204626)
		{
			switch (env.getDialog())
			{
				case START_DIALOG:
				{
					if (var == 2)
					{
						return sendQuestDialog(env, 1864);
					}
				}
				case STEP_TO_3:
				{
					if (var == 2)
					{
						if (player.getInventory().getItemCountByItemId(182215342) == 0)
						{
							if (!giveQuestItem(env, 182215342, 1))
							{
								return true;
							}
						}
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
				}
			}
		}
		else if (targetId == 204622)
		{
			switch (env.getDialog())
			{
				case START_DIALOG:
				{
					if (var == 2)
					{
						return sendQuestDialog(env, 1949);
					}
				}
				case STEP_TO_3:
				{
					if (var == 2)
					{
						if (player.getInventory().getItemCountByItemId(182215343) == 0)
						{
							if (!giveQuestItem(env, 182215343, 1))
							{
								return true;
							}
						}
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
				}
			}
		}
		else if (targetId == 700270)
		{
			if (env.getDialog() == QuestDialog.USE_OBJECT)
			{
				return useQuestObject(env, 3, 4, false, 0, 0, 1, 182215344, 1);
			}
		}
		return false;
	}
}