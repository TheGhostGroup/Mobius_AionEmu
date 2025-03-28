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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Ritsu
 */
public class _30057JotunJaunt extends QuestHandler
{
	private static final int questId = 30057;
	
	public _30057JotunJaunt()
	{
		super(questId);
	}
	
	@Override
	public void register()
	{
		qe.registerQuestNpc(799381).addOnQuestStart(questId);
		qe.registerQuestNpc(799381).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("SILENTERA_WESTGATE_600010000"), questId);
	}
	
	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		if (zoneName != ZoneName.get("SILENTERA_WESTGATE_600010000"))
		{
			return false;
		}
		final Player player = env.getPlayer();
		if (player == null)
		{
			return false;
		}
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if ((qs == null) || (qs.getQuestVars().getQuestVars() != 0))
		{
			return false;
		}
		if (qs.getStatus() != QuestStatus.START)
		{
			return false;
		}
		env.setQuestId(questId);
		qs.setStatus(QuestStatus.REWARD);
		updateQuestStatus(env);
		return true;
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final QuestDialog dialog = env.getDialog();
		
		if (targetId == 799381)
		{
			if ((qs == null) || (qs.getStatus() == QuestStatus.NONE))
			{
				if (dialog == QuestDialog.START_DIALOG)
				{
					return sendQuestDialog(env, 4762);
				}
				return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.REWARD)
			{
				if (dialog == QuestDialog.USE_OBJECT)
				{
					return sendQuestDialog(env, 10002);
				}
				else if (dialog == QuestDialog.SELECT_REWARD)
				{
					return sendQuestDialog(env, 5);
				}
				else
				{
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
