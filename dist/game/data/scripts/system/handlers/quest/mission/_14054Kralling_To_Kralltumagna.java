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

import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author Rinzler (Encom)
 */
public class _14054Kralling_To_Kralltumagna extends QuestHandler
{
	private static final int questId = 14054;
	
	public _14054Kralling_To_Kralltumagna()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerQuestNpc(204602).addOnTalkEvent(questId);
		qe.registerQuestNpc(800413).addOnTalkEvent(questId);
		qe.registerQuestNpc(802050).addOnTalkEvent(questId);
		// ****//
		final int[] mobs =
		{
			214010,
			214013,
			214014,
			214015,
			214016,
			214017,
			214018,
			214019,
			214020,
			214021,
			214022,
			214023,
			214081,
			214082,
			214083,
			214084,
			214085,
			214086,
			214087,
			214088,
			700220,
			233861
		};
		// ****//
		for (int mob : mobs)
		{
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
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
		return defaultOnLvlUpEvent(env, 14053, true);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 800413)
			{
				return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			final int var = qs.getQuestVarById(0);
			if (targetId == 204602)
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						if (var == 0)
						{
							return sendQuestDialog(env, 1011);
						}
					}
					case SELECT_ACTION_1012:
					{
						if (var == 0)
						{
							return sendQuestDialog(env, 1012);
						}
					}
					case STEP_TO_1:
					{
						if (var == 0)
						{
							changeQuestStep(env, 0, 1, false);
							return closeDialogWindow(env);
						}
					}
				}
			}
			else if (targetId == 800413)
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						if (var == 1)
						{
							return sendQuestDialog(env, 1352);
						}
					}
					case SELECT_ACTION_1353:
					{
						if (var == 1)
						{
							return sendQuestDialog(env, 1353);
						}
					}
					case STEP_TO_2:
					{
						if (var == 1)
						{
							TeleportService2.teleportTo(player, 210040000, 1886.9739f, 2572.614f, 139.97418f, (byte) 78, TeleportAnimation.BEAM_ANIMATION);
							changeQuestStep(env, 1, 2, false);
							return closeDialogWindow(env);
						}
					}
				}
			}
			else if (targetId == 802050)
			{
				switch (env.getDialog())
				{
					case START_DIALOG:
					{
						if (var == 2)
						{
							return sendQuestDialog(env, 2034);
						}
						else if (var == 4)
						{
							return sendQuestDialog(env, 2716);
						}
					}
					case SELECT_ACTION_2035:
					{
						if (var == 2)
						{
							return sendQuestDialog(env, 2035);
						}
					}
					case STEP_TO_4:
					{
						if (var == 2)
						{
							changeQuestStep(env, 2, 3, false);
							return closeDialogWindow(env);
						}
					}
					case STEP_TO_6:
					{
						TeleportService2.teleportTo(player, 210040000, 2772.3306f, 1876.4788f, 153.75f, (byte) 102, TeleportAnimation.BEAM_ANIMATION);
						return defaultCloseDialog(env, 4, 4, true, false);
					}
				}
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
		final int var = qs.getQuestVarById(0);
		if ((targetId == 214010) || (targetId == 214013) || (targetId == 214014) || (targetId == 214015) || (targetId == 214016) || (targetId == 214017) || (targetId == 214018) || (targetId == 214019) || (targetId == 214020) || (targetId == 214021) || (targetId == 214022) || (targetId == 214023) || (targetId == 214081) || (targetId == 214082) || (targetId == 214083) || (targetId == 214084) || (targetId == 214085) || (targetId == 214086) || (targetId == 214087) || (targetId == 214088))
		{
			switch (qs.getQuestVarById(1))
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				{
					qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		else if (targetId == 700220) // [702040 Baranath Crusher]
		{
			switch (qs.getQuestVarById(2))
			{
				case 0:
				case 1:
				case 2:
				{
					qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
					updateQuestStatus(env);
					if ((qs.getQuestVarById(1) == 6) && (qs.getQuestVarById(2) == 3))
					{
						updateQuestStatus(env);
						return true;
					}
					return true;
				}
			}
		}
		else if (targetId == 233861) // Officer Vitusa.
		{
			if (var == 3)
			{
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}