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
package system.handlers.quest.fenris_fang;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rinzler (Encom)
 */
public class _4943Luck_And_Persistence extends QuestHandler
{
	private static final int questId = 4943;
	
	public _4943Luck_And_Persistence()
	{
		super(questId);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env, 4942, true);
	}
	
	@Override
	public void register()
	{
		qe.registerOnLevelUp(questId);
		final int[] npcs =
		{
			204096,
			204097,
			204075,
			204053,
			700538
		};
		qe.registerQuestNpc(204053).addOnQuestStart(questId);
		for (int npc : npcs)
		{
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final QuestDialog dialog = env.getDialog();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 204053)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 4762);
				}
				return sendQuestStartDialog(env);
			}
		}
		if (qs == null)
		{
			return false;
		}
		final int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 204096:
				{
					if (var == 0)
					{
						switch (dialog)
						{
							case START_DIALOG:
							{
								return sendQuestDialog(env, 1011);
							}
							case STEP_TO_1:
							{
								return defaultCloseDialog(env, 0, 1);
							}
						}
					}
					if (var == 2)
					{
						switch (dialog)
						{
							case START_DIALOG:
							{
								return sendQuestDialog(env, 1693);
							}
							case CHECK_COLLECTED_ITEMS:
							{
								if (QuestService.collectItemCheck(env, true))
								{
									removeQuestItem(env, 182207123, 1);
									changeQuestStep(env, 2, 3, false);
									return sendQuestDialog(env, 10000);
								}
								return sendQuestDialog(env, 10001);
							}
						}
					}
					break;
				}
				case 204097:
				{
					if (var == 1)
					{
						switch (dialog)
						{
							case START_DIALOG:
							{
								return sendQuestDialog(env, 1352);
							}
							case SELECT_ACTION_1354:
							{
								if (player.getInventory().tryDecreaseKinah(3400000))
								{
									if (player.getInventory().getItemCountByItemId(182207123) == 0)
									{
										if (!giveQuestItem(env, 182207123, 1))
										{
											return true;
										}
									}
									changeQuestStep(env, 1, 2, false);
									return sendQuestDialog(env, 1354);
								}
								return sendQuestDialog(env, 1438);
							}
						}
					}
					break;
				}
				case 700538:
				{
					if ((dialog == QuestDialog.USE_OBJECT) && (var == 2))
					{
						return useQuestObject(env, 2, 2, false, 0);
					}
					break;
				}
				case 204075:
				{
					switch (dialog)
					{
						case START_DIALOG:
						{
							if (var == 3)
							{
								return sendQuestDialog(env, 2034);
							}
						}
						case SET_REWARD:
						{
							if (player.getInventory().getItemCountByItemId(186000084) >= 1)
							{
								removeQuestItem(env, 186000084, 1);
								return defaultCloseDialog(env, 3, 3, true, false, 0);
							}
							return sendQuestDialog(env, 2120);
						}
						case FINISH_DIALOG:
						{
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				default:
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 204053)
			{
				if (dialog == QuestDialog.USE_OBJECT)
				{
					return sendQuestDialog(env, 10002);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}