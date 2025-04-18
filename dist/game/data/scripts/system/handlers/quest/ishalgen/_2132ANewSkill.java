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
package system.handlers.quest.ishalgen;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author MrPoke
 */
public class _2132ANewSkill extends QuestHandler
{
	private static final int questId = 2132;
	
	public _2132ANewSkill()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203527).addOnTalkEvent(questId); // Warrior.
		qe.registerQuestNpc(203528).addOnTalkEvent(questId); // Scout.
		qe.registerQuestNpc(203529).addOnTalkEvent(questId); // Mage.
		qe.registerQuestNpc(203530).addOnTalkEvent(questId); // Priest.
		qe.registerQuestNpc(801218).addOnTalkEvent(questId); // Technist.
		qe.registerQuestNpc(801219).addOnTalkEvent(questId); // Muse.
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		final boolean lvlCheck = QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel());
		if (!lvlCheck)
		{
			return false;
		}
		if (qs != null)
		{
			return false;
		}
		env.setQuestId(questId);
		if (QuestService.startQuest(env))
		{
			qs = player.getQuestStateList().getQuestState(questId);
			qs.setStatus(QuestStatus.REWARD);
			PlayerClass playerClass = player.getPlayerClass();
			if (!playerClass.isStartingClass())
			{
				playerClass = PlayerClass.getStartingClassFor(playerClass);
			}
			switch (playerClass)
			{
				case WARRIOR:
				{
					qs.setQuestVar(1);
					break;
				}
				case SCOUT:
				{
					qs.setQuestVar(2);
					break;
				}
				case MAGE:
				{
					qs.setQuestVar(3);
					break;
				}
				case PRIEST:
				{
					qs.setQuestVar(4);
					break;
				}
				case TECHNIST:
				{
					qs.setQuestVar(5);
					break;
				}
				case MUSE:
				{
					qs.setQuestVar(6);
					break;
				}
			}
			updateQuestStatus(env);
		}
		return true;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getStatus() != QuestStatus.REWARD))
		{
			return false;
		}
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
		{
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		}
		final PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
		switch (targetId)
		{
			case 203527:
			{
				if (playerClass == PlayerClass.WARRIOR)
				{
					if (env.getDialog() == QuestDialog.USE_OBJECT)
					{
						return sendQuestDialog(env, 1011);
					}
					else if (env.getDialogId() == 1009)
					{
						return sendQuestDialog(env, 5);
					}
					else
					{
						return sendQuestEndDialog(env);
					}
				}
				return false;
			}
			case 203528:
			{
				if (playerClass == PlayerClass.SCOUT)
				{
					if (env.getDialog() == QuestDialog.USE_OBJECT)
					{
						return sendQuestDialog(env, 1352);
					}
					else if (env.getDialogId() == 1009)
					{
						return sendQuestDialog(env, 6);
					}
					else
					{
						return sendQuestEndDialog(env);
					}
				}
				return false;
			}
			case 203529:
			{
				if (playerClass == PlayerClass.MAGE)
				{
					if (env.getDialog() == QuestDialog.USE_OBJECT)
					{
						return sendQuestDialog(env, 1693);
					}
					else if (env.getDialogId() == 1009)
					{
						return sendQuestDialog(env, 7);
					}
					else
					{
						return sendQuestEndDialog(env);
					}
				}
				return false;
			}
			case 203530:
			{
				if (playerClass == PlayerClass.PRIEST)
				{
					if (env.getDialog() == QuestDialog.USE_OBJECT)
					{
						return sendQuestDialog(env, 2034);
					}
					else if (env.getDialogId() == 1009)
					{
						return sendQuestDialog(env, 8);
					}
					else
					{
						return sendQuestEndDialog(env);
					}
				}
				return false;
			}
			case 801218:
			{
				if (playerClass == PlayerClass.TECHNIST)
				{
					if (env.getDialog() == QuestDialog.USE_OBJECT)
					{
						return sendQuestDialog(env, 2375);
					}
					else if (env.getDialogId() == 1009)
					{
						return sendQuestDialog(env, 45);
					}
					else
					{
						return sendQuestEndDialog(env);
					}
				}
				return false;
			}
			case 801219:
			{
				if (playerClass == PlayerClass.MUSE)
				{
					if (env.getDialog() == QuestDialog.USE_OBJECT)
					{
						return sendQuestDialog(env, 2716);
					}
					else if (env.getDialogId() == 1009)
					{
						return sendQuestDialog(env, 46);
					}
					else
					{
						return sendQuestEndDialog(env);
					}
				}
				return false;
			}
		}
		return false;
	}
}