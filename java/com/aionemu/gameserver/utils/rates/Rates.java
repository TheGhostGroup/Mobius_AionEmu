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
package com.aionemu.gameserver.utils.rates;

/**
 * @author ATracer
 */
public abstract class Rates
{
	public abstract float getGroupXpRate();
	
	public abstract float getXpRate();
	
	public abstract float getApNpcRate();
	
	public abstract float getApPlayerGainRate();
	
	public abstract float getGpPlayerGainRate();
	
	public abstract float getXpPlayerGainRate();
	
	public abstract float getApPlayerLossRate();
	
	public abstract float getGpPlayerLossRate();
	
	public abstract float getGatheringXPRate();
	
	public abstract int getGatheringCountRate();
	
	public abstract float getCraftingXPRate();
	
	public abstract float getDropRate();
	
	public abstract float getQuestXpRate();
	
	public abstract float getQuestKinahRate();
	
	public abstract float getQuestApRate();
	
	public abstract float getQuestGpRate();
	
	public abstract float getQuestAbyssOpRate();
	
	public abstract float getQuestExpBoostRate();
	
	public abstract float getDpNpcRate();
	
	public abstract float getDpPlayerRate();
	
	public abstract int getCraftCritRate();
	
	public abstract int getComboCritRate();
	
	public abstract float getDisciplineRewardRate();
	
	public abstract float getChaosRewardRate();
	
	public abstract float getHarmonyRewardRate();
	
	public abstract float getGloryRewardRate();
	
	public abstract float getTollRewardRate();
	
	public static Rates getRatesFor(byte membership)
	{
		switch (membership)
		{
			case 0:
			case 1:
			{
				return new RegularRates();
			}
			case 2:
			{
				return new PremiumRates();
			}
			case 3:
			{
				return new VipRates();
			}
			default:
			{
				return new VipRates();
			}
		}
	}
}