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
package com.aionemu.gameserver.model.stats.container;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.items.ItemSlot;

/**
 * @author xavier
 * @author ATracer
 */
@XmlType(name = "StatEnum")
@XmlEnum
public enum StatEnum
{
	MAXDP(22),
	MAXHP(18),
	MAXMP(20),
	AGILITY(9, true),
	BLOCK(33),
	EVASION(31),
	CONCENTRATION(41),
	WILL(11, true),
	HEALTH(7, true),
	ACCURACY(8, true),
	KNOWLEDGE(10, true),
	PARRY(32),
	POWER(6, true),
	SPEED(36, true),
	ALLSPEED,
	WEIGHT(39, true),
	HIT_COUNT(35, true),
	ATTACK_RANGE(38, true),
	ATTACK_SPEED(29, -1, true),
	PHYSICAL_ATTACK(25),
	PHYSICAL_ACCURACY(30),
	PHYSICAL_CRITICAL(34),
	PHYSICAL_DEFENSE(26),
	MAIN_HAND_HITS,
	MAIN_HAND_ACCURACY,
	MAIN_HAND_CRITICAL,
	MAIN_HAND_POWER,
	MAIN_HAND_ATTACK_SPEED,
	OFF_HAND_HITS,
	OFF_HAND_ACCURACY,
	OFF_HAND_CRITICAL,
	OFF_HAND_POWER,
	OFF_HAND_ATTACK_SPEED,
	MAGICAL_ATTACK(27),
	MAIN_HAND_MAGICAL_ATTACK,
	OFF_HAND_MAGICAL_ATTACK,
	MAGICAL_ACCURACY(105),
	MAIN_HAND_MAGICAL_ACCURACY,
	OFF_HAND_MAGICAL_ACCURACY,
	MAGICAL_CRITICAL(40),
	MAGICAL_RESIST(28),
	MAIN_HAND_MAGICAL_POWER,
	MAIN_HAND_MAGICAL_CRITICAL,
	OFF_HAND_MAGICAL_POWER,
	OFF_HAND_MAGICAL_CRITICAL,
	MAX_DAMAGES,
	MIN_DAMAGES,
	IS_MAGICAL_ATTACK(0, true),
	EARTH_RESISTANCE(14),
	FIRE_RESISTANCE(15),
	WIND_RESISTANCE(13),
	WATER_RESISTANCE(12),
	DARK_RESISTANCE(17),
	LIGHT_RESISTANCE(16),
	BOOST_MAGICAL_SKILL(104),
	BOOST_SPELL_ATTACK,
	BOOST_CASTING_TIME(108),
	BOOST_CASTING_TIME_HEAL,
	BOOST_CASTING_TIME_TRAP,
	BOOST_CASTING_TIME_ATTACK,
	BOOST_CASTING_TIME_SKILL,
	BOOST_CASTING_TIME_SUMMONHOMING,
	BOOST_CASTING_TIME_SUMMON,
	BOOST_HATE(109),
	FLY_TIME(23),
	FLY_SPEED(37),
	SOAR_SPEED, // No Idea :(
	DAMAGE_REDUCE,
	DAMAGE_REDUCE_MAX,
	BLEED_RESISTANCE(44),
	BLIND_RESISTANCE(48),
	BLOCK_PENETRATION,
	BIND_RESISTANCE,
	CHARM_RESISTANCE(49),
	CONFUSE_RESISTANCE(54),
	CURSE_RESISTANCE(53),
	DISEASE_RESISTANCE(50),
	DEFORM_RESISTANCE,
	FEAR_RESISTANCE(52),
	OPENAREIAL_RESISTANCE(59),
	PARALYZE_RESISTANCE(45),
	PERIFICATION_RESISTANCE(56),
	POISON_RESISTANCE(43),
	PULLED_RESISTANCE,
	ROOT_RESISTANCE(47),
	SILENCE_RESISTANCE(51),
	SLEEP_RESISTANCE(46),
	SLOW_RESISTANCE(60),
	SNARE_RESISTANCE(61),
	SPIN_RESISTANCE(62),
	STAGGER_RESISTANCE(58),
	STUMBLE_RESISTANCE(57),
	STUN_RESISTANCE(55),
	SILENCE_RESISTANCE_PENETRATION(77),
	PARALYZE_RESISTANCE_PENETRATION(71),
	POISON_RESISTANCE_PENETRATION(69),
	BLEED_RESISTANCE_PENETRATION(70),
	SLEEP_RESISTANCE_PENETRATION(72),
	ROOT_RESISTANCE_PENETRATION(73),
	BLIND_RESISTANCE_PENETRATION(74),
	CHARM_RESISTANCE_PENETRATION(75),
	DISEASE_RESISTANCE_PENETRATION(76),
	FEAR_RESISTANCE_PENETRATION(78),
	SPIN_RESISTANCE_PENETRATION(88),
	CURSE_RESISTANCE_PENETRATION(79),
	CONFUSE_RESISTANCE_PENETRATION(80),
	STUN_RESISTANCE_PENETRATION(81),
	PERIFICATION_RESISTANCE_PENETRATION(82),
	STUMBLE_RESISTANCE_PENETRATION(83),
	STAGGER_RESISTANCE_PENETRATION(84),
	OPENAREIAL_RESISTANCE_PENETRATION(85),
	SNARE_RESISTANCE_PENETRATION(87),
	SLOW_RESISTANCE_PENETRATION(86),
	REGEN_MP(21),
	REGEN_HP(19),
	REGEN_FP(24),
	HEAL_BOOST(110),
	HEAL_SKILL_BOOST,
	HEAL_SKILL_DEBOOST,
	ALLRESIST(2),
	STUNLIKE_RESISTANCE,
	ELEMENTAL_RESISTANCE_DARK,
	ELEMENTAL_RESISTANCE_LIGHT,
	MAGICAL_CRITICAL_RESIST(116),
	MAGICAL_CRITICAL_DAMAGE_REDUCE(118),
	PHYSICAL_CRITICAL_RESIST(115),
	PHYSICAL_CRITICAL_DAMAGE_REDUCE(117),
	ERFIRE,
	ERAIR,
	EREARTH,
	ERWATER,
	ABNORMAL_RESISTANCE_ALL(1),
	ALLPARA,
	KNOWIL(4),
	AGIDEX(5),
	STRVIT(3),
	MAGICAL_DEFEND(125),
	MAGIC_SKILL_BOOST_RESIST(126),
	BOOST_HUNTING_XP_RATE,
	BOOST_GROUP_HUNTING_XP_RATE,
	BOOST_QUEST_XP_RATE,
	BOOST_CRAFTING_XP_RATE,
	BOOST_COOKING_XP_RATE,
	BOOST_WEAPONSMITHING_XP_RATE,
	BOOST_ARMORSMITHING_XP_RATE,
	BOOST_TAILORING_XP_RATE,
	BOOST_ALCHEMY_XP_RATE,
	BOOST_HANDICRAFTING_XP_RATE,
	BOOST_MENUISIER_XP_RATE,
	BOOST_GATHERING_XP_RATE,
	BOOST_AETHERTAPPING_XP_RATE,
	BOOST_ESSENCETAPPING_XP_RATE,
	BOOST_DROP_RATE,
	BOOST_MANTRA_RANGE,
	BOOST_DURATION_BUFF,
	BOOST_RESIST_DEBUFF,
	ELEMENTAL_FIRE,
	PVP_PHYSICAL_ATTACK,
	PVP_PHYSICAL_DEFEND,
	PVP_MAGICAL_ATTACK,
	PVP_MAGICAL_DEFEND,
	PVP_ATTACK_RATIO(106),
	PVP_ATTACK_RATIO_MAGICAL(111),
	PVP_ATTACK_RATIO_PHYSICAL(113),
	PVP_DEFEND_RATIO(107),
	PVP_DEFEND_RATIO_PHYSICAL(112),
	PVP_DEFEND_RATIO_MAGICAL(114),
	PVE_ATTACK_RATIO,
	PVE_ATTACK_RATIO_MAGICAL,
	PVE_ATTACK_RATIO_PHYSICAL,
	PVE_DEFEND_RATIO,
	PVE_DEFEND_RATIO_PHYSICAL,
	PVE_DEFEND_RATIO_MAGICAL,
	AP_BOOST,
	DR_BOOST,
	BOOST_CHARGE_TIME,
	
	// 4.7
	PHYSICAL_DAMAGE,
	MAGICAL_DAMAGE,
	PHYSICAL_CRITICAL_REDUCE_RATE,
	MAGICAL_CRITICAL_REDUCE_RATE,
	PROC_REDUCE_RATE,
	PVP_DODGE,
	PVP_BLOCK,
	PVP_PARRY,
	PVP_HIT_ACCURACY,
	PVP_MAGICAL_RESIST,
	PVP_MAGICAL_HIT_ACCURACY,
	
	// 4.8
	ENCHANT_BOOST,
	AP_REDUCE_RATE,
	AUTHORIZE_BOOST,
	INDUN_DROP_BOOST,
	DEATH_PENALTY_REDUCE,
	ENCHANT_OPTION_BOOST,
	ORDALIE_REWARD;
	
	private boolean replace;
	private int sign;
	
	private int itemStoneMask;
	
	private StatEnum()
	{
		this(0);
	}
	
	private StatEnum(int stoneMask)
	{
		this(stoneMask, 1, false);
	}
	
	private StatEnum(int stoneMask, boolean replace)
	{
		this(stoneMask, 1, replace);
	}
	
	private StatEnum(int stoneMask, int sign)
	{
		this(stoneMask, sign, false);
	}
	
	private StatEnum(int stoneMask, int sign, boolean replace)
	{
		itemStoneMask = stoneMask;
		this.replace = replace;
		this.sign = sign;
	}
	
	public int getSign()
	{
		return sign;
	}
	
	public int getItemStoneMask()
	{
		return itemStoneMask;
	}
	
	public static StatEnum findByItemStoneMask(int mask)
	{
		for (StatEnum sEnum : values())
		{
			if (sEnum.getItemStoneMask() == mask)
			{
				return sEnum;
			}
		}
		throw new IllegalArgumentException("Cannot find StatEnum for stone mask: " + mask);
	}
	
	public StatEnum getHandStat(long itemSlot)
	{
		switch (this)
		{
			case MAGICAL_ATTACK:
			{
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_MAGICAL_ATTACK : OFF_HAND_MAGICAL_ATTACK;
			}
			case MAGICAL_ACCURACY:
			{
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_MAGICAL_ACCURACY : OFF_HAND_MAGICAL_ACCURACY;
			}
			case PHYSICAL_ATTACK:
			{
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_POWER : OFF_HAND_POWER;
			}
			case PHYSICAL_ACCURACY:
			{
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_ACCURACY : OFF_HAND_ACCURACY;
			}
			case PHYSICAL_CRITICAL:
			{
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_CRITICAL : OFF_HAND_CRITICAL;
			}
			default:
			{
				return this;
			}
		}
	}
	
	public boolean isMainOrSubHandStat()
	{
		switch (this)
		{
			case MAGICAL_ATTACK:
			case MAGICAL_ACCURACY:
			case PHYSICAL_ATTACK:
			case POWER:
			case PHYSICAL_ACCURACY:
			case PHYSICAL_CRITICAL:
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}
	
	public boolean isReplace()
	{
		return replace;
	}
	
	public static StatEnum getModifier(int skillId)
	{
		switch (skillId)
		{
			case 30001:
			case 30002:
			{
				return BOOST_ESSENCETAPPING_XP_RATE;
			}
			case 30003:
			{
				return BOOST_AETHERTAPPING_XP_RATE;
			}
			case 40001:
			{
				return BOOST_COOKING_XP_RATE;
			}
			case 40002:
			{
				return BOOST_WEAPONSMITHING_XP_RATE;
			}
			case 40003:
			{
				return BOOST_ARMORSMITHING_XP_RATE;
			}
			case 40004:
			{
				return BOOST_TAILORING_XP_RATE;
			}
			case 40007:
			{
				return BOOST_ALCHEMY_XP_RATE;
			}
			case 40008:
			{
				return BOOST_HANDICRAFTING_XP_RATE;
			}
			case 40010:
			{
				return BOOST_MENUISIER_XP_RATE;
			}
			default:
			{
				return null;
			}
		}
	}
}