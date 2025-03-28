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
package com.aionemu.gameserver.services.abysslandingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.AbyssLandingConfig;
import com.aionemu.gameserver.model.landing.LandingLocation;
import com.aionemu.gameserver.services.AbyssLandingService;

public class LandingUpdateService
{
	private static final Logger log = LoggerFactory.getLogger(LandingUpdateService.class);
	
	final LandingLocation redemptionLanding = AbyssLandingService.getInstance().redemptionLanding();
	final LandingLocation harbingerLanding = AbyssLandingService.getInstance().harbingerLanding();
	
	// Quest Points.
	final int redemptionPts = redemptionLanding.getQuestPoints() - redemptionLanding.getQuestPoints();
	final int harbingerPts = harbingerLanding.getQuestPoints() - harbingerLanding.getQuestPoints();
	
	// Monument Points.
	final int redemptionPts1 = redemptionLanding.getMonumentsPoints() - redemptionLanding.getMonumentsPoints();
	final int harbingerPts1 = harbingerLanding.getMonumentsPoints() - harbingerLanding.getMonumentsPoints();
	
	// Facility Points.
	final int redemptionPts2 = redemptionLanding.getFacilityPoints() - redemptionLanding.getFacilityPoints();
	final int harbingerPts2 = harbingerLanding.getFacilityPoints() - harbingerLanding.getFacilityPoints();
	
	// Commander Points.
	final int redemptionPts3 = redemptionLanding.getCommanderPoints() - redemptionLanding.getCommanderPoints();
	final int harbingerPts3 = harbingerLanding.getCommanderPoints() - harbingerLanding.getCommanderPoints();
	
	LandingUpdateService()
	{
	}
	
	public void initResetQuestPoints()
	{
		if (AbyssLandingConfig.ABYSS_LANDING_QUEST_RESET_ENABLED)
		{
			CronService.getInstance().schedule(() -> resetQuestPoints(), AbyssLandingConfig.ABYSS_LANDING_QUEST_RESET_TIME);
		}
	}
	
	public void initResetAbyssLandingPoints()
	{
		if (AbyssLandingConfig.ABYSS_LANDING_POINTS_RESET_ENABLED)
		{
			CronService.getInstance().schedule(() ->
			{
				resetMonumentPoints();
				resetFacilityPoints();
				resetCommanderPoints();
			}, AbyssLandingConfig.ABYSS_LANDING_POINTS_RESET_TIME);
		}
	}
	
	public void resetQuestPoints()
	{
		log.debug("##### Abyss Landing Reset Quest Points #####");
		// final long startTime = System.currentTimeMillis();
		// Redemption's Landing.
		redemptionLanding.setPoints(redemptionPts);
		redemptionLanding.setQuestPoints(0);
		AbyssLandingService.getInstance().checkRedemptionLanding(redemptionLanding.getPoints(), false);
		// Harbinger's Landing.
		harbingerLanding.setPoints(harbingerPts);
		harbingerLanding.setQuestPoints(0);
		AbyssLandingService.getInstance().checkHarbingerLanding(harbingerLanding.getPoints(), false);
		// Update All Landing.
		AbyssLandingService.getInstance().onUpdate();
	}
	
	public void resetMonumentPoints()
	{
		log.debug("##### Abyss Landing Reset Monuments Points #####");
		// final long startTime = System.currentTimeMillis();
		// Redemption's Landing.
		redemptionLanding.setPoints(redemptionPts1);
		redemptionLanding.setMonumentsPoints(0);
		AbyssLandingService.getInstance().checkRedemptionLanding(redemptionLanding.getPoints(), false);
		// Harbinger's Landing.
		harbingerLanding.setPoints(harbingerPts1);
		harbingerLanding.setMonumentsPoints(0);
		AbyssLandingService.getInstance().checkHarbingerLanding(harbingerLanding.getPoints(), false);
		// Update All Landing.
		AbyssLandingService.getInstance().onUpdate();
	}
	
	public void resetFacilityPoints()
	{
		log.debug("##### Abyss Landing Reset Facility Points #####");
		// final long startTime = System.currentTimeMillis();
		// Redemption's Landing.
		redemptionLanding.setPoints(redemptionPts2);
		redemptionLanding.setFacilityPoints(0);
		AbyssLandingService.getInstance().checkRedemptionLanding(redemptionLanding.getPoints(), false);
		// Harbinger's Landing.
		harbingerLanding.setPoints(harbingerPts2);
		harbingerLanding.setFacilityPoints(0);
		AbyssLandingService.getInstance().checkHarbingerLanding(harbingerLanding.getPoints(), false);
		// Update All Landing.
		AbyssLandingService.getInstance().onUpdate();
	}
	
	public void resetCommanderPoints()
	{
		log.debug("##### Abyss Landing Reset Commander Points #####");
		// final long startTime = System.currentTimeMillis();
		// Redemption's Landing.
		redemptionLanding.setPoints(redemptionPts3);
		redemptionLanding.setCommanderPoints(0);
		AbyssLandingService.getInstance().checkRedemptionLanding(redemptionLanding.getPoints(), false);
		// Harbinger's Landing.
		harbingerLanding.setPoints(harbingerPts3);
		harbingerLanding.setCommanderPoints(0);
		AbyssLandingService.getInstance().checkHarbingerLanding(harbingerLanding.getPoints(), false);
		// Update All Landing.
		AbyssLandingService.getInstance().onUpdate();
	}
	
	public static LandingUpdateService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final LandingUpdateService instance = new LandingUpdateService();
	}
}