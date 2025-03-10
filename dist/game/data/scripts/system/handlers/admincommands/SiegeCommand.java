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
package system.handlers.admincommands;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.OutpostLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.siegeservice.BalaurAssaultService;
import com.aionemu.gameserver.services.siegeservice.Siege;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

@SuppressWarnings("rawtypes")
public class SiegeCommand extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_LIST_LOCATIONS = "locations";
	private static final String COMMAND_LIST_SIEGES = "sieges";
	private static final String COMMAND_CAPTURE = "capture";
	private static final String COMMAND_ASSAULT = "assault";
	
	public SiegeCommand()
	{
		super("siege");
	}
	
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0]))
		{
			handleStartStopSiege(player, params);
		}
		else if (COMMAND_LIST.equalsIgnoreCase(params[0]))
		{
			handleList(player, params);
		}
		else if (COMMAND_LIST_SIEGES.equals(params[0]))
		{
			listLocations(player);
		}
		else if (COMMAND_CAPTURE.equals(params[0]))
		{
			capture(player, params);
		}
		else if (COMMAND_ASSAULT.equals(params[0]))
		{
			assault(player, params);
		}
	}
	
	protected void handleStartStopSiege(Player player, String... params)
	{
		if ((params.length != 2) || !NumberUtils.isDigits(params[1]))
		{
			showHelp(player);
			return;
		}
		final int siegeLocId = NumberUtils.toInt(params[1]);
		if (!isValidSiegeLocationId(player, siegeLocId))
		{
			showHelp(player);
			return;
		}
		if (COMMAND_START.equalsIgnoreCase(params[0]))
		{
			if (SiegeService.getInstance().isSiegeInProgress(siegeLocId))
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " is already under siege");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " - starting siege!");
				SiegeService.getInstance().startSiege(siegeLocId);
			}
		}
		else if (COMMAND_STOP.equalsIgnoreCase(params[0]))
		{
			if (!SiegeService.getInstance().isSiegeInProgress(siegeLocId))
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " is not under siege");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " - stopping siege!");
				SiegeService.getInstance().stopSiege(siegeLocId);
			}
		}
	}
	
	protected boolean isValidSiegeLocationId(Player player, int fortressId)
	{
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(fortressId))
		{
			PacketSendUtility.sendMessage(player, "Id " + fortressId + " is invalid");
			return false;
		}
		return true;
	}
	
	protected void handleList(Player player, String[] params)
	{
		if (params.length != 2)
		{
			showHelp(player);
			return;
		}
		if (COMMAND_LIST_LOCATIONS.equalsIgnoreCase(params[1]))
		{
			listLocations(player);
		}
		else if (COMMAND_LIST_SIEGES.equalsIgnoreCase(params[1]))
		{
			listSieges(player);
		}
		else
		{
			showHelp(player);
		}
	}
	
	protected void listLocations(Player player)
	{
		for (FortressLocation f : SiegeService.getInstance().getFortresses().values())
		{
			PacketSendUtility.sendMessage(player, "Fortress: " + f.getLocationId() + " belongs to " + f.getRace());
		}
		for (OutpostLocation o : SiegeService.getInstance().getOutposts().values())
		{
			PacketSendUtility.sendMessage(player, "Outpost: " + o.getLocationId() + " belongs to " + o.getRace());
		}
		for (ArtifactLocation a : SiegeService.getInstance().getStandaloneArtifacts().values())
		{
			PacketSendUtility.sendMessage(player, "Artifact: " + a.getLocationId() + " belongs to " + a.getRace());
		}
	}
	
	protected void listSieges(Player player)
	{
		for (Integer i : SiegeService.getInstance().getSiegeLocations().keySet())
		{
			final Siege s = SiegeService.getInstance().getSiege(i);
			if (s != null)
			{
				final int secondsLeft = SiegeService.getInstance().getRemainingSiegeTimeInSeconds(i);
				String minSec = (secondsLeft / 60) + "m ";
				minSec += (secondsLeft % 60) + "s";
				PacketSendUtility.sendMessage(player, "Location: " + i + ": " + minSec + " left.");
			}
		}
	}
	
	protected void capture(Player player, String[] params)
	{
		if ((params.length < 3) || !NumberUtils.isNumber(params[1]))
		{
			showHelp(player);
			return;
		}
		final int siegeLocationId = NumberUtils.toInt(params[1]);
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId))
		{
			PacketSendUtility.sendMessage(player, "Invalid Siege Location Id: " + siegeLocationId);
			return;
		}
		SiegeRace sr = null;
		try
		{
			sr = SiegeRace.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
		}
		Legion legion = null;
		if (sr == null)
		{
			try
			{
				final int legionId = Integer.valueOf(params[2]);
				legion = LegionService.getInstance().getLegion(legionId);
			}
			catch (NumberFormatException e)
			{
				String legionName = "";
				for (int i = 2; i < params.length; i++)
				{
					legionName += " " + params[i];
				}
				legion = LegionService.getInstance().getLegion(legionName.trim());
			}
			if (legion != null)
			{
				final int legionBGeneral = LegionService.getInstance().getLegionBGeneral(legion.getLegionId());
				if (legionBGeneral != 0)
				{
					final PlayerCommonData BGeneral = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
					sr = SiegeRace.getByRace(BGeneral.getRace());
				}
			}
		}
		if ((legion == null) && (sr == null))
		{
			PacketSendUtility.sendMessage(player, params[2] + " is not valid siege race or legion name");
			return;
		}
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(siegeLocationId);
		final Siege s = SiegeService.getInstance().getSiege(siegeLocationId);
		if (s != null)
		{
			s.getSiegeCounter().addRaceDamage(sr, s.getBoss().getLifeStats().getMaxHp() + 1);
			s.setBossKilled(true);
			SiegeService.getInstance().stopSiege(siegeLocationId);
			loc.setLegionId(legion != null ? legion.getLegionId() : 0);
		}
		else
		{
			SiegeService.getInstance().deSpawnNpcs(siegeLocationId);
			loc.setVulnerable(false);
			loc.setUnderShield(false);
			loc.setRace(sr);
			loc.setLegionId(legion != null ? legion.getLegionId() : 0);
			SiegeService.getInstance().spawnNpcs(siegeLocationId, sr, SiegeModType.PEACE);
			DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(loc);
			switch (siegeLocationId)
			{
				case 2011:
				case 2021:
				case 3011:
				case 3021:
				{
					SiegeService.getInstance().updateOutpostStatusByFortress((FortressLocation) loc);
					break;
				}
			}
		}
		SiegeService.getInstance().broadcastUpdate(loc);
	}
	
	protected void assault(Player player, String[] params)
	{
		if ((params.length < 2) || (!NumberUtils.isNumber(params[1]) && !NumberUtils.isNumber(params[2])))
		{
			showHelp(player);
			return;
		}
		final int siegeLocationId = NumberUtils.toInt(params[1]);
		final int delay = NumberUtils.toInt(params[2]);
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId))
		{
			PacketSendUtility.sendMessage(player, "Invalid Siege Location Id: " + siegeLocationId);
			return;
		}
		BalaurAssaultService.getInstance().startAssault(player, siegeLocationId, delay);
	}
	
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //siege Help\n" + "//siege start|stop <LocationId>\n" + "//siege list locations|sieges\n" + "//siege capture <LocationId> <siegeRaceName|legionName|legionId>\n" + "//siege assault <LocationId> <delaySec>");
		final java.util.Set<Integer> fortressIds = SiegeService.getInstance().getFortresses().keySet();
		final java.util.Set<Integer> artifactIds = SiegeService.getInstance().getStandaloneArtifacts().keySet();
		final java.util.Set<Integer> outpostIds = SiegeService.getInstance().getOutposts().keySet();
		PacketSendUtility.sendMessage(player, "Fortress: " + StringUtils.join(fortressIds, ", "));
		PacketSendUtility.sendMessage(player, "Artifacts: " + StringUtils.join(artifactIds, ", "));
		PacketSendUtility.sendMessage(player, "Outposts: " + StringUtils.join(outpostIds, ", "));
	}
}