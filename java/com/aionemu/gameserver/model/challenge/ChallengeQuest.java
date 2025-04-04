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
package com.aionemu.gameserver.model.challenge;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;

/**
 * @author ViAl
 */
public class ChallengeQuest
{
	private final ChallengeQuestTemplate template;
	private int completeCount;
	private PersistentState persistentState;
	
	public ChallengeQuest(ChallengeQuestTemplate template, int completeCount)
	{
		this.template = template;
		this.completeCount = completeCount;
	}
	
	public int getQuestId()
	{
		return template.getId();
	}
	
	public ChallengeQuestTemplate getQuestTemplate()
	{
		return template;
	}
	
	public int getMaxRepeats()
	{
		return template.getRepeatCount();
	}
	
	public int getScorePerQuest()
	{
		return template.getScore();
	}
	
	public int getCompleteCount()
	{
		return completeCount;
	}
	
	public synchronized void increaseCompleteCount()
	{
		completeCount++;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	
	public void setPersistentState(PersistentState persistentState)
	{
		if ((this.persistentState == PersistentState.NEW) && (persistentState == PersistentState.UPDATE_REQUIRED))
		{
			return;
		}
		this.persistentState = persistentState;
	}
}