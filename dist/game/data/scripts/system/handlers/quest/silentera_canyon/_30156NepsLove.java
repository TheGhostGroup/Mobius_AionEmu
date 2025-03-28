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
package system.handlers.quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 */
public class _30156NepsLove extends QuestHandler
{
	private static final int questId = 30156;
	
	public _30156NepsLove()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(799234).addOnQuestStart(questId); // Nep
		qe.registerQuestNpc(799234).addOnTalkEvent(questId); // Nep
		qe.registerQuestNpc(204304).addOnTalkEvent(questId); // Vili
		qe.registerQuestNpc(700570).addOnTalkEvent(questId); // Statue Sinigalla
		qe.registerQuestNpc(799339).addOnTalkEvent(questId); // Sinigalla
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
			if (targetId == 799234)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 1011);
				}
				else if (dialog == QuestDialog.ACCEPT_QUEST)
				{
					if (!giveQuestItem(env, 182209253, 1))
					{
						return true;
					}
					return sendQuestStartDialog(env);
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 204304)
			{
				switch (dialog)
				{
					case USE_OBJECT:
					{
						return sendQuestDialog(env, 2375);
					}
					case SELECT_REWARD:
					{
						return sendQuestDialog(env, 5);
					}
					default:
					{
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			final int var = qs.getQuestVarById(0);
			if (targetId == 700570)
			{
				switch (dialog)
				{
					case USE_OBJECT:
					{
						if (var == 0)
						{
							QuestService.addNewSpawn(600010000, 1, 799339, (float) 545.308, (float) 1232.3855, (float) 304.35193, (byte) 73);
							return useQuestObject(env, 0, 0, false, 0, 0, 0, 182209223, 1);
						}
					}
				}
			}
			if (targetId == 799339)
			{
				switch (dialog)
				{
					case START_DIALOG:
					{
						if (var == 0)
						{
							return sendQuestDialog(env, 1352);
						}
					}
					case STEP_TO_1:
					{
						if (var == 0)
						{
							defaultCloseDialog(env, 0, 0, true, false);
							final Npc npc = (Npc) env.getVisibleObject();
							ThreadPoolManager.getInstance().schedule(new Runnable()
							{
								@Override
								public void run()
								{
									npc.getController().onDelete();
								}
							}, 400);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
