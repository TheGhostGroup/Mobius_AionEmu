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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class SkillAttackManager
{
	public static void performAttack(NpcAI2 npcAI, int delay)
	{
		if (npcAI.getOwner().getObjectTemplate().getAttackRange() == 0)
		{
			if ((npcAI.getOwner().getTarget() != null) && !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), npcAI.getOwner().getAggroRange()))
			{
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				npcAI.getOwner().getController().abortCast();
				return;
			}
		}
		if (npcAI.setSubStateIfNot(AISubState.CAST))
		{
			if (delay > 0)
			{
				ThreadPoolManager.getInstance().schedule(new SkillAction(npcAI), delay + DataManager.SKILL_DATA.getSkillTemplate(npcAI.getSkillId()).getDuration());
			}
			else
			{
				skillAction(npcAI);
			}
		}
	}
	
	protected static void skillAction(NpcAI2 npcAI)
	{
		final Creature target = (Creature) npcAI.getOwner().getTarget();
		if (npcAI.getOwner().getObjectTemplate().getAttackRange() == 0)
		{
			if ((npcAI.getOwner().getTarget() != null) && !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), npcAI.getOwner().getAggroRange()))
			{
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				npcAI.getOwner().getController().abortCast();
				return;
			}
		}
		if ((target != null) && !target.getLifeStats().isAlreadyDead())
		{
			final int skillId = npcAI.getSkillId();
			final int skillLevel = npcAI.getSkillLevel();
			final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			final int duration = template.getDuration();
			if (npcAI.isLogging())
			{
				AI2Logger.info(npcAI, "Using skill " + skillId + " level: " + skillLevel + " duration: " + duration);
			}
			switch (template.getSubType())
			{
				case BUFF:
				{
					switch (template.getProperties().getFirstTarget())
					{
						case ME:
						{
							if (npcAI.getOwner().getEffectController().isAbnormalPresentBySkillId(skillId))
							{
								afterUseSkill(npcAI);
								return;
							}
							break;
						}
						default:
						{
							if (target.getEffectController().isAbnormalPresentBySkillId(skillId))
							{
								afterUseSkill(npcAI);
								return;
							}
						}
					}
					break;
				}
				default:
				{
					break;
				}
			}
			final boolean success = npcAI.getOwner().getController().useSkill(skillId, skillLevel);
			if (!success)
			{
				afterUseSkill(npcAI);
			}
		}
		else
		{
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}
	}
	
	public static void afterUseSkill(NpcAI2 npcAI)
	{
		npcAI.setSubStateIfNot(AISubState.NONE);
		npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
	}
	
	public static NpcSkillEntry chooseNextSkill(NpcAI2 npcAI)
	{
		if (npcAI.isInSubState(AISubState.CAST))
		{
			return null;
		}
		final Npc owner = npcAI.getOwner();
		final NpcSkillList skillList = owner.getSkillList();
		if ((skillList == null) || (skillList.size() == 0))
		{
			return null;
		}
		if (owner.getGameStats().canUseNextSkill())
		{
			final NpcSkillEntry npcSkill = skillList.getRandomSkill();
			if (npcSkill != null)
			{
				final int currentHpPercent = owner.getLifeStats().getHpPercentage();
				if (npcSkill.isReady(currentHpPercent, System.currentTimeMillis() - owner.getGameStats().getFightStartingTime()))
				{
					final SkillTemplate template = npcSkill.getSkillTemplate();
					if (((template.getType() == SkillType.MAGICAL) && owner.getEffectController().isAbnormalSet(AbnormalState.SILENCE)) || ((template.getType() == SkillType.PHYSICAL) && owner.getEffectController().isAbnormalSet(AbnormalState.BIND)) || (owner.getEffectController().isUnderFear()))
					{
						return null;
					}
					npcSkill.setLastTimeUsed();
					return npcSkill;
				}
			}
		}
		return null;
	}
	
	private static final class SkillAction implements Runnable
	{
		private NpcAI2 npcAI;
		
		SkillAction(NpcAI2 npcAI)
		{
			this.npcAI = npcAI;
		}
		
		@Override
		public void run()
		{
			skillAction(npcAI);
			npcAI = null;
		}
	}
}