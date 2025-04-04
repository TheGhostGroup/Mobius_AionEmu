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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author kecimis
 */
public class DelayedFpAtkInstantEffect extends EffectTemplate
{
	@XmlAttribute
	protected int delay;
	
	@XmlAttribute
	protected boolean percent;
	
	@Override
	public void calculate(Effect effect)
	{
		if ((effect.getEffected() instanceof Player))
		{
			super.calculate(effect, null, null);
		}
	}
	
	@Override
	public void applyEffect(Effect effect)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (effect.getEffector().isEnemy(effect.getEffected()))
			{
				calculateAndApplyDamage(effect);
			}
		}, delay);
	}
	
	void calculateAndApplyDamage(Effect effect)
	{
		if (!(effect.getEffected() instanceof Player))
		{
			return;
		}
		final int valueWithDelta = value + (delta * effect.getSkillLevel());
		final Player player = (Player) effect.getEffected();
		final int maxFP = player.getLifeStats().getMaxFp();
		
		int newValue = valueWithDelta;
		
		if (percent)
		{
			newValue = (maxFP * valueWithDelta) / 100;
		}
		player.getLifeStats().reduceFp(newValue);
	}
}
