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

import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author Cheatkiller
 */
public class _11076ProofOfTalent extends QuestHandler
{
	private static final int questId = 11076;
	
	public _11076ProofOfTalent()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(799025).addOnQuestStart(questId);
		qe.registerQuestNpc(799084).addOnTalkEvent(questId);
		qe.registerQuestNpc(799025).addOnTalkEvent(questId);
		qe.registerOnEnterWindStream(questId);
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
			if (targetId == 799025)
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
			if (targetId == 799084)
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					if (qs.getQuestVarById(0) == 3)
					{
						return sendQuestDialog(env, 2034);
					}
				}
				else if (dialog == QuestDialog.STEP_TO_4)
				{
					TeleportService2.teleportTo(player, 210050000, 1338.6f, 279.6f, 590, (byte) 80, TeleportAnimation.BEAM_ANIMATION);
					return defaultCloseDialog(env, 3, 4, true, false);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 799025)
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
	
	@Override
	public boolean onEnterWindStreamEvent(QuestEnv env, int teleportId)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs != null) && (qs.getStatus() == QuestStatus.START))
		{
			if (player.getWorldId() == 210050000)
			{
				if (teleportId == 152001)
				{
					changeQuestStep(env, 0, 1, false);
				}
				else if (teleportId == 153001)
				{
					changeQuestStep(env, 1, 2, false);
				}
				else if (teleportId == 154001)
				{
					changeQuestStep(env, 2, 3, false);
				}
				return true;
			}
		}
		return false;
	}
}
