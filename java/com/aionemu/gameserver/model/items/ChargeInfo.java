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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class ChargeInfo extends ActionObserver
{
	public static final int LEVEL2 = 1000000;
	public static final int LEVEL1 = 500000;
	
	private int chargePoints;
	private final int attackBurn;
	private final int defendBurn;
	private final Item item;
	private Player player;
	
	public ChargeInfo(int chargePoints, Item item)
	{
		super(ObserverType.ATTACK_DEFEND);
		this.chargePoints = chargePoints;
		this.item = item;
		if (item.getImprovement() != null)
		{
			attackBurn = item.getImprovement().getBurnAttack();
			defendBurn = item.getImprovement().getBurnDefend();
		}
		else
		{
			attackBurn = 0;
			defendBurn = 0;
		}
	}
	
	public int getChargePoints()
	{
		return chargePoints;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public int updateChargePoints(int addPoints)
	{
		int newChargePoints = chargePoints + addPoints;
		if (newChargePoints > LEVEL2)
		{
			newChargePoints = LEVEL2;
		}
		else if (newChargePoints < 0)
		{
			newChargePoints = 0;
		}
		if (item.isEquipped() && (player != null))
		{
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
		chargePoints = newChargePoints;
		return newChargePoints;
	}
	
	@Override
	public void attacked(Creature creature)
	{
		updateChargePoints(-defendBurn);
		final Player player = this.player;
		if (player != null)
		{
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}
	
	@Override
	public void attack(Creature creature)
	{
		updateChargePoints(-attackBurn);
		final Player player = this.player;
		if (player != null)
		{
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}
}