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
package com.aionemu.gameserver.model;

/**
 * @author Rolandas
 */
public enum DialogAction
{
	ERROR(-1),
	NULL(1),
	BUY(2),
	SELL(3),
	OPEN_STIGMA_WINDOW(4),
	CREATE_LEGION(5),
	DISPERSE_LEGION(6),
	RECREATE_LEGION(7),
	RESURRECT_PET(19),
	RETRIEVE_CHAR_WAREHOUSE(25), // 4.3
	DEPOSIT_CHAR_WAREHOUSE(26), // 4.3
	RETRIEVE_ACCOUNT_WAREHOUSE(27), // 4.3
	DEPOSIT_ACCOUNT_WAREHOUSE(28), // 4.3
	OPEN_VENDOR(33), // 4.3
	RESURRECT_BIND(34), // 4.3
	RECOVERY(35), // 4.3
	ENTER_PVP(36), // 4.3
	LEAVE_PVP(37), // 4.3
	OPEN_POSTBOX(38), // 4.3
	DIC(40), // 4.3
	GIVE_ITEM_PROC(41), // 4.3
	REMOVE_MANASTONE(42), // 4.3
	CHANGE_ITEM_SKIN(43), // 4.3
	AIRLINE_SERVICE(44), // 4.3
	GATHER_SKILL_LEVELUP(45), // 4.3
	COMBINE_SKILL_LEVELUP(46), // 4.3
	EXTEND_INVENTORY(47), // 4.3
	EXTEND_CHAR_WAREHOUSE(48), // 4.3
	EXTEND_ACCOUNT_WAREHOUSE(49), // 4.3
	LEGION_LEVELUP(50), // 4.3
	LEGION_CREATE_EMBLEM(51), // 4.3
	LEGION_CHANGE_EMBLEM(52), // 4.3
	OPEN_LEGION_WAREHOUSE(53), // 4.3
	OPEN_PERSONAL_WAREHOUSE(54), // 4.3
	BUY_BY_AP(55), // 4.3
	CLOSE_LEGION_WAREHOUSE(56), // 4.3
	PASS_DOORMAN(57), // 4.3
	CRAFT(58), // 4.3
	EXCHANGE_COIN(59), // 4.3
	SHOW_MOVIE(60), // 4.3
	EDIT_CHARACTER(61), // 4.3
	EDIT_GENDER(62), // 4.3
	MATCH_MAKER(63), // 4.3
	MAKE_MERCENARY(64), // 4.3
	INSTANCE_ENTRY(65), // 4.3
	COMPOUND_WEAPON(66), // 4.3
	DECOMPOUND_WEAPON(67), // 4.3
	FACTION_JOIN(68), // 4.3
	FACTION_SEPARATE(69), // 4.3
	BUY_AGAIN(70), // 4.3
	PET_ADOPT(71), // 4.3
	PET_ABANDON(72), // 4.3
	HOUSING_BUILD(73), // 4.3
	HOUSING_DESTRUCT(74), // 4.3
	CHARGE_ITEM_SINGLE(75), // 4.3
	CHARGE_ITEM_MULTI(76), // 4.3
	INSTANCE_PARTY_MATCH(77), // 4.3
	TRADE_IN(78), // 4.3
	GIVEUP_CRAFT_EXPERT(79), // 4.3
	GIVEUP_CRAFT_MASTER(80), // 4.3
	HOUSING_FRIENDLIST(81), // 4.3
	HOUSING_RANDOM_TELEPORT(82), // 4.3
	HOUSING_PERSONAL_INS_TELEPORT(83), // 4.3
	HOUSING_PERSONAL_AUCTION(84), // 4.3
	HOUSING_PAY_RENT(85), // 4.3
	HOUSING_KICK(86), // 4.3
	HOUSING_CHANGE_BUILDING(87), // 4.3
	HOUSING_CONFIG(88), // 4.3
	HOUSING_GIVEUP(89), // 4.3
	HOUSING_CANCEL_GIVEUP(90), // 4.3
	HOUSING_CREATE_PERSONAL_INS(91), // 4.3
	SIDEKICK_ADOPT(92), // 4.3
	SIDEKICK_ABANDON(93), // 4.3
	AUGMENT_ITEM_SINGLE(94), // 4.3
	AUGMENT_ITEM_MULTI(95), // 4.3
	HOUSING_STUDIO(96), // 4.3
	HOUSING_LIKE(97), // 4.3
	HOUSING_SCRIPT(98), // 4.3
	HOUSING_GUESTBOOK(99), // 4.3
	QUEST_BOARD(100), // 4.3
	AP_SELLING(101), // 4.3
	PURCHASE_LIST(103), // 4.3
	TELEPORT_SIMPLE(104), // 4.3
	OPEN_INSTANCE_RECRUIT(105), // 4.3
	MOVE_ITEM_SKIN(106), // 4.7
	TRADE_IN_UPGRADE(107), // 4.7
	AUTO_REWARD(108), // 4.7
	ITEM_UPGRADE(109), // 4.7
	
	// Select Boss Level 4.7
	SELECT_BOSS_LEVEL_1(20006),
	SELECT_BOSS_LEVEL_2(20007),
	SELECT_BOSS_LEVEL_3(20008),
	SELECT_BOSS_LEVEL_4(20009),
	SELECT_BOSS_LEVEL_5(20010),
	
	// Quest Auto Reward 4.7
	QUEST_AUTO_REWARD_1(110),
	QUEST_AUTO_REWARD_2(111),
	QUEST_AUTO_REWARD_3(112),
	QUEST_AUTO_REWARD_4(113),
	QUEST_AUTO_REWARD_5(114),
	QUEST_AUTO_REWARD_6(115),
	QUEST_AUTO_REWARD_7(116),
	QUEST_AUTO_REWARD_8(117),
	QUEST_AUTO_REWARD_9(118),
	QUEST_AUTO_REWARD_10(119),
	QUEST_AUTO_REWARD_11(120),
	QUEST_AUTO_REWARD_12(121),
	QUEST_AUTO_REWARD_13(122),
	QUEST_AUTO_REWARD_14(123),
	QUEST_AUTO_REWARD_15(124),
	OPEN_STIGMA_ENCHANT(125); // 4.8
	
	private int id;
	
	private DialogAction(int id)
	{
		this.id = id;
	}
	
	public int id()
	{
		return id;
	}
}