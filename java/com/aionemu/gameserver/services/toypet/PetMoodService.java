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
package com.aionemu.gameserver.services.toypet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 */
public class PetMoodService
{
	private static final Logger log = LoggerFactory.getLogger(PetMoodService.class);
	
	public static void checkMood(Pet pet, int type, int shuggleEmotion)
	{
		switch (type)
		{
			case 0:
			{
				startCheckingMood(pet);
				break;
			}
			case 1:
			{
				interactWithPet(pet, shuggleEmotion);
				break;
			}
			case 3:
			{
				requestPresent(pet);
				break;
			}
		}
	}
	
	private static void requestPresent(Pet pet)
	{
		if (pet.getCommonData().getMoodPoints(false) < 9000)
		{
			log.warn("Requested present before mood fill up: {}", pet.getMaster().getName());
			return;
		}
		if (pet.getCommonData().getGiftRemainingTime() > 0)
		{
			AuditLogger.info(pet.getMaster(), "Trying to get gift during CD for pet " + pet.getPetId());
			return;
		}
		if (pet.getMaster().getInventory().isFull())
		{
			// Your cube is full. Wait before asking for a gift.
			PacketSendUtility.sendPacket(pet.getMaster(), SM_SYSTEM_MESSAGE.STR_PET_CONDITION_REWARD_FULL_INVEN);
			return;
		}
		pet.getCommonData().clearMoodStatistics();
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 4, 0));
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 3, 0));
		final int itemId = pet.getPetTemplate().getConditionReward();
		if (itemId != 0)
		{
			ItemService.addItem(pet.getMaster(), pet.getPetTemplate().getConditionReward(), 1);
		}
	}
	
	private static void interactWithPet(Pet pet, int shuggleEmotion)
	{
		if (pet.getCommonData() != null)
		{
			if (pet.getCommonData().increaseShuggleCounter())
			{
				PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 2, shuggleEmotion));
				PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 4, 0));
			}
		}
	}
	
	private static void startCheckingMood(Pet pet)
	{
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 0, 0));
	}
}