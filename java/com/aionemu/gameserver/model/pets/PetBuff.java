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
package com.aionemu.gameserver.model.pets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.pet.PetBonusAttr;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.model.templates.pet.PetPenaltyAttr;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class PetBuff implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	private final PetBonusAttr petBonusAttr;
	private long startTime;
	ScheduledFuture<?> task = null;
	
	public PetBuff(int buffId)
	{
		petBonusAttr = DataManager.PET_BUFF_DATA.getPetBonusattr(buffId);
	}
	
	public void applyEffect(Player player, int time)
	{
		if (hasPetBuff() || (petBonusAttr == null))
		{
			return;
		}
		if (time != 0)
		{
			task = ThreadPoolManager.getInstance().schedule(new PetBuffTask(player), time);
		}
		startTime = System.currentTimeMillis();
		for (PetPenaltyAttr petPenaltyAttr : petBonusAttr.getPenaltyAttr())
		{
			final StatEnum stat = petPenaltyAttr.getStat();
			final int statToModified = player.getGameStats().getStat(stat, 0).getBase();
			final int value = petPenaltyAttr.getValue();
			final int valueModified = petPenaltyAttr.getFunc().equals(Func.PERCENT) ? ((statToModified * value) / 100) : (value);
			functions.add(new StatAddFunction(stat, valueModified, true));
		}
		player.getGameStats().addEffect(this, functions);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_BUFF_PET_USE_START_MESSAGE);
		PacketSendUtility.sendPacket(player, new SM_PET(true, 0, 0));// start cheering
	}
	
	void loopEffect(Player player, int time)
	{
		if (time != 0)
		{
			task = ThreadPoolManager.getInstance().schedule(new PetBuffTask(player), time);
		}
		PacketSendUtility.sendPacket(player, new SM_PET(true, 0, 0));// start cheering
	}
	
	public void endEffect(Player player)
	{
		functions.clear();
		if (task != null)
		{
			task.cancel(false);
			task = null;
		}
		player.getGameStats().endEffect(this);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_BUFF_PET_USE_STOP_MESSAGE);
		PacketSendUtility.sendPacket(player, new SM_PET(false, 0, 0));// stop cheering
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}
	
	public int getBuffRemaningTime()
	{
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	private class PetBuffTask implements Runnable
	{
		private final Player player;
		
		public PetBuffTask(Player player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			final Pet pet = player.getPet();
			final PetTemplate petTemp = DataManager.PET_DATA.getPetTemplate(pet.getPetId());
			final PetBonusAttr petBuff = DataManager.PET_BUFF_DATA.getPetBonusattr(petTemp.getPetFunction(PetFunctionType.CHEER).getId());
			
			if ((task != null) && (player.getInventory().getItemCountByItemId(182007162) >= petBuff.getFoodCount()))
			{
				player.getInventory().decreaseByItemId(182007162, petBuff.getFoodCount());
				loopEffect(player, 300000);
			}
			else
			{
				endEffect(player);
			}
		}
	}
	
	public boolean hasPetBuff()
	{
		return (task != null) && !task.isDone();
	}
	
}
