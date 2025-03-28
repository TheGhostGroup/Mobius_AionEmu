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
package com.aionemu.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;

/**
 * @author MrPoke
 */
public class QuestNpc
{
	private static final Logger log = LoggerFactory.getLogger(QuestNpc.class);
	private final List<Integer> onQuestStart;
	private final List<Integer> onKillEvent;
	private final List<Integer> onTalkEvent;
	private final List<Integer> onAttackEvent;
	private final List<Integer> onLostTargetEvent;
	private final List<Integer> onReachTargetEvent;
	private final List<Integer> onAddAggroListEvent;
	private final List<Integer> onAtDistanceEvent;
	private final int npcId;
	
	public QuestNpc(int npcId)
	{
		this.npcId = npcId;
		onQuestStart = new ArrayList<>();
		onKillEvent = new ArrayList<>();
		onTalkEvent = new ArrayList<>();
		onAttackEvent = new ArrayList<>();
		onLostTargetEvent = new ArrayList<>();
		onReachTargetEvent = new ArrayList<>();
		onAddAggroListEvent = new ArrayList<>();
		onAtDistanceEvent = new ArrayList<>();
	}
	
	private void registerCanAct(int questId, int npcId)
	{
		final NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		if (template == null)
		{
			log.warn("[QuestEngine] No such NPC template for " + npcId + " in Q" + questId);
			return;
		}
		final String aiName = DataManager.NPC_DATA.getNpcTemplate(npcId).getAi();
		if ("quest_use_item".equals(aiName))
		{
			QuestEngine.getInstance().registerCanAct(questId, npcId);
		}
	}
	
	public void addOnQuestStart(int questId)
	{
		if (!onQuestStart.contains(questId))
		{
			onQuestStart.add(questId);
		}
	}
	
	public List<Integer> getOnQuestStart()
	{
		return onQuestStart;
	}
	
	public void addOnAttackEvent(int questId)
	{
		if (!onAttackEvent.contains(questId))
		{
			onAttackEvent.add(questId);
		}
	}
	
	public List<Integer> getOnAttackEvent()
	{
		return onAttackEvent;
	}
	
	public void addOnKillEvent(int questId)
	{
		if (!onKillEvent.contains(questId))
		{
			onKillEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	public List<Integer> getOnKillEvent()
	{
		return onKillEvent;
	}
	
	public void addOnTalkEvent(int questId)
	{
		if (!onTalkEvent.contains(questId))
		{
			onTalkEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	public List<Integer> getOnTalkEvent()
	{
		return onTalkEvent;
	}
	
	public void addOnReachTargetEvent(int questId)
	{
		if (!onReachTargetEvent.contains(questId))
		{
			onReachTargetEvent.add(questId);
		}
	}
	
	public List<Integer> getOnReachTargetEvent()
	{
		return onReachTargetEvent;
	}
	
	public void addOnLostTargetEvent(int questId)
	{
		if (!onLostTargetEvent.contains(questId))
		{
			onLostTargetEvent.add(questId);
		}
	}
	
	public List<Integer> getOnLostTargetEvent()
	{
		return onLostTargetEvent;
	}
	
	public void addOnAddAggroListEvent(int questId)
	{
		if (!onAddAggroListEvent.contains(questId))
		{
			onAddAggroListEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	public List<Integer> getOnAddAggroListEvent()
	{
		return onAddAggroListEvent;
	}
	
	public void addOnAtDistanceEvent(int questId)
	{
		if (!onAtDistanceEvent.contains(questId))
		{
			onAtDistanceEvent.add(questId);
			registerCanAct(questId, npcId);
		}
	}
	
	public List<Integer> getOnDistanceEvent()
	{
		return onAtDistanceEvent;
	}
	
	public int getNpcId()
	{
		return npcId;
	}
}
