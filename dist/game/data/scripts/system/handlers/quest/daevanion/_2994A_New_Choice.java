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
package system.handlers.quest.daevanion;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rinzler (Encom)
 */
public class _2994A_New_Choice extends QuestHandler
{
	private static final int questId = 2994;
	private static final int dialogs[] =
	{
		1013,
		1034,
		1055,
		1076,
		5103,
		1098,
		1119,
		1140,
		1161,
		1183,
		1204,
		1225,
		1246
	};
	private static final int daevanionWeapons[] =
	{
		100000723,
		100900554,
		101300538,
		100200673,
		101700594,
		100100568,
		101500566,
		100600608,
		100500572,
		115000826,
		101800569,
		101900562,
		102000592,
		102100517
	};
	private int choice = 0;
	private int item;
	
	public _2994A_New_Choice()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(204077).addOnQuestStart(questId);
		qe.registerQuestNpc(204077).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int dialogId = env.getDialogId();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE) || qs.canRepeat())
		{
			if (targetId == 204077)
			{
				if (dialogId == 59)
				{
					QuestService.startQuest(env);
					return sendQuestDialog(env, 1011);
				}
				return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 204077)
			{
				if (dialogId == 59)
				{
					return sendQuestDialog(env, 1011);
				}
				switch (env.getDialogId())
				{
					case 1012:
					case 1097:
					case 1182:
					case 1267:
					{
						return sendQuestDialog(env, dialogId);
					}
					case 1013:
					case 1034:
					case 1055:
					case 1076:
					case 5103:
					case 1098:
					case 1119:
					case 1140:
					case 1161:
					case 1183:
					case 1204:
					case 1225:
					case 1246:
					{
						item = getItem(dialogId);
						if (player.getInventory().getItemCountByItemId(item) > 0)
						{
							return sendQuestDialog(env, 1013);
						}
						return sendQuestDialog(env, 1352);
					}
					case 10000:
					case 10001:
					case 10002:
					case 10003:
					{
						if (player.getInventory().getItemCountByItemId(186000041) == 0)
						{
							return sendQuestDialog(env, 1009);
						}
						changeQuestStep(env, 0, 0, true);
						choice = dialogId - 10000;
						return sendQuestDialog(env, choice + 5);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 204077)
			{
				removeQuestItem(env, item, 1);
				removeQuestItem(env, 186000041, 1);
				return sendQuestEndDialog(env, choice);
			}
		}
		return false;
	}
	
	private int getItem(int dialogId)
	{
		int x = 0;
		for (int id : dialogs)
		{
			if (id == dialogId)
			{
				break;
			}
			x++;
		}
		return (daevanionWeapons[x]);
	}
}