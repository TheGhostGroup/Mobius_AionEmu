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
package system.handlers.quest.inggison;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Rinzler (Encom)
 */
public class _11147Cute_Beady_Eyes extends QuestHandler
{
	private static final int questId = 11147;
	
	public _11147Cute_Beady_Eyes()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(798997).addOnQuestStart(questId);
		qe.registerQuestNpc(798997).addOnTalkEvent(questId);
		qe.registerQuestNpc(799079).addOnTalkEvent(questId);
		qe.registerQuestNpc(799081).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LF4_SENSORYAREA_Q11147A_206158_9_210050000"), questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final QuestDialog dialog = env.getDialog();
		final int targetId = env.getTargetId();
		if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
		{
			if (targetId == 798997)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 4762);
				}
				return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			if (targetId == 798997)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					if (qs.getQuestVarById(0) == 0)
					{
						return sendQuestDialog(env, 1011);
					}
					else if (qs.getQuestVarById(0) == 1)
					{
						return sendQuestDialog(env, 1352);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_1)
				{
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 799079, player.getX(), player.getY(), player.getZ(), (byte) 0);
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 799081, player.getX() - 1, player.getY() + 2, player.getZ(), (byte) 0);
					return defaultCloseDialog(env, 0, 1);
				}
				else if (dialog == QuestDialog.STEP_TO_2)
				{
					return defaultCloseDialog(env, 1, 2);
				}
			}
			else if (targetId == 799079)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 1693);
				}
				else if (dialog == QuestDialog.STEP_TO_3)
				{
					final Npc npc = (Npc) env.getVisibleObject();
					npc.getController().onDelete();
					return defaultCloseDialog(env, 2, 3);
				}
			}
			else if (targetId == 799081)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 2034);
				}
				else if (dialog == QuestDialog.STEP_TO_4)
				{
					final Npc npc = (Npc) env.getVisibleObject();
					npc.getController().onDelete();
					return defaultCloseDialog(env, 3, 4);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 798997)
			{
				switch (dialog)
				{
					case USE_OBJECT:
					{
						return sendQuestDialog(env, 10002);
					}
					default:
					{
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		if (zoneName == ZoneName.get("LF4_SENSORYAREA_Q11147A_206158_9_210050000"))
		{
			final Player player = env.getPlayer();
			if (player == null)
			{
				return false;
			}
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			if ((qs != null) && (qs.getStatus() == QuestStatus.START))
			{
				final int var = qs.getQuestVarById(0);
				if (var == 4)
				{
					changeQuestStep(env, 4, 4, true);
					return true;
				}
			}
		}
		return false;
	}
}