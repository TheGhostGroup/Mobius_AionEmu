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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpellAttackEffect")
public class SpellAttackEffect extends AbstractOverTimeEffect
{
	@Override
	public void onPeriodicAction(Effect effect)
	{
		final Creature effected = effect.getEffected();
		final Creature effector = effect.getEffector();
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final int critAddDmg = critAddDmg2 + (critAddDmg1 * effect.getSkillLevel());
		final int damage = AttackUtil.calculateMagicalOverTimeSkillResult(effect, valueWithDelta, element, position, true, critProbMod2, critAddDmg);
		effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, damage, false, LOG.SPELLATK);
		effected.getObserveController().notifyDotAttackedObservers(effector, effect);
	}
}
