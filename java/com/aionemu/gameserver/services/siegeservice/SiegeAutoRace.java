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
package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.landing.LandingPointsEnum;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SIEGE_LOCATION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AbyssLandingService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

public class SiegeAutoRace
{
	private static String[] siegeIds = SiegeConfig.SIEGE_AUTO_LOCID.split(";");
	
	public static void AutoSiegeRace(int locid)
	{
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(locid);
		if (!loc.getRace().equals(SiegeRace.ASMODIANS) || !loc.getRace().equals(SiegeRace.ELYOS))
		{
			ThreadPoolManager.getInstance().schedule(() -> SiegeService.getInstance().startSiege(locid), 300000);
			SiegeService.getInstance().deSpawnNpcs(locid);
			final int oldOwnerRaceId = loc.getRace().getRaceId();
			final int legionId = loc.getLegionId();
			final String legionName = legionId != 0 ? LegionService.getInstance().getLegion(legionId).getLegionName() : "";
			final DescriptionId NameId = new DescriptionId(loc.getTemplate().getNameId());
			if (ElyosAutoSiege(locid))
			{
				loc.setRace(SiegeRace.ELYOS);
			}
			if (AsmoAutoSiege(locid))
			{
				loc.setRace(SiegeRace.ASMODIANS);
			}
			loc.setLegionId(0);
			World.getInstance().doOnAllPlayers(player ->
			{
				if ((legionId != 0) && (player.getRace().getRaceId() == oldOwnerRaceId))
				{
					// %0 has conquered %1.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301038, legionName, NameId));
				}
				// %0 succeeded in conquering %1.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301039, loc.getRace().getDescriptionId(), NameId));
				// %0 has occupied %0 and the Landing is now enhanced.
				AbyssLandingService.getInstance().AnnounceToPoints(player, loc.getRace().getDescriptionId(), NameId, 0, LandingPointsEnum.SIEGE);
				PacketSendUtility.sendPacket(player, new SM_SIEGE_LOCATION_INFO(loc));
			});
			if (ElyosAutoSiege(locid))
			{
				SiegeService.getInstance().spawnNpcs(locid, SiegeRace.ELYOS, SiegeModType.PEACE);
			}
			else if (AsmoAutoSiege(locid))
			{
				SiegeService.getInstance().spawnNpcs(locid, SiegeRace.ASMODIANS, SiegeModType.PEACE);
			}
			DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(loc);
			SiegeService.getInstance().updateOutpostStatusByFortress((FortressLocation) loc);
		}
		SiegeService.getInstance().broadcastUpdate(loc);
	}
	
	public static boolean isAutoSiege(int locId)
	{
		return ElyosAutoSiege(locId) || AsmoAutoSiege(locId);
	}
	
	public static boolean ElyosAutoSiege(int locId)
	{
		for (String id : siegeIds[0].split(","))
		{
			if (locId == Integer.parseInt(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean AsmoAutoSiege(int locId)
	{
		for (String id : siegeIds[1].split(","))
		{
			if (locId == Integer.parseInt(id))
			{
				return true;
			}
		}
		return false;
	}
}