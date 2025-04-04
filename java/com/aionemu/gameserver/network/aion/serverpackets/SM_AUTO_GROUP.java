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
package com.aionemu.gameserver.network.aion.serverpackets;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author SheppeR, Guapo, nrg
 */
public class SM_AUTO_GROUP extends AionServerPacket
{
	private byte windowId;
	private int instanceMaskId;
	private int mapId;
	private int messageId;
	private int titleId;
	private int waitTime;
	private boolean close;
	String name = StringUtils.EMPTY;
	public static final byte wnd_EntryIcon = 6;
	
	public SM_AUTO_GROUP(int instanceMaskId)
	{
		isBG = false;
		final AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		this.instanceMaskId = instanceMaskId;
		messageId = agt.getNameId();
		titleId = agt.getTitleId();
		mapId = agt.getInstanceMapId();
	}
	
	public SM_AUTO_GROUP(int instanceMaskId, Number windowId)
	{
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		isBG = false;
	}
	
	public SM_AUTO_GROUP(int instanceMaskId, Number windowId, boolean close)
	{
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.close = close;
		isBG = false;
	}
	
	public SM_AUTO_GROUP(int instanceMaskId, Number windowId, int waitTime, String name)
	{
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.waitTime = waitTime;
		this.name = name;
		isBG = false;
	}
	
	// For BG System
	private boolean isBG;
	private int option = 0;
	private int extraOption = 0;
	private int worldId = 0;
	private int specialOption = 0;
	
	public SM_AUTO_GROUP(int worldId, boolean show)
	{
		isBG = true;
		option = 6;
		extraOption = 1;
		this.worldId = worldId;
		specialOption = show ? 1 : 0;
	}
	
	public SM_AUTO_GROUP(int worldId, boolean teamChoice, int messageId)
	{
		isBG = true;
		option = 0;
		extraOption = teamChoice ? 2 : 0;
		this.worldId = worldId;
		this.messageId = messageId;
	}
	
	public SM_AUTO_GROUP(int option, int worldId, int specialOption)
	{
		isBG = true;
		this.option = option;
		extraOption = 0;
		this.worldId = worldId;
		this.specialOption = specialOption;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if (!isBG)
		{
			writeD(instanceMaskId);
			writeC(windowId);
			writeD(mapId);
			switch (windowId)
			{
				case 0: // Request Entry
				{
					writeD(messageId);
					writeD(titleId);
					writeD(0);
					break;
				}
				case 1: // Waiting Window
				{
					writeD(0);
					writeD(0);
					writeD(waitTime);
					break;
				}
				case 2: // Cancel Looking
				{
					writeD(0);
					writeD(0);
					writeD(0);
					break;
				}
				case 3: // Pass Window
				{
					writeD(0);
					writeD(0);
					writeD(waitTime);
					break;
				}
				case 4: // Enter Window
				{
					writeD(0);
					writeD(0);
					writeD(0);
					break;
				}
				case 5: // After You Click Enter
				{
					writeD(0);
					writeD(0);
					writeD(0);
					break;
				}
				case wnd_EntryIcon:
				{
					writeD(messageId);
					writeD(titleId);
					writeD(close ? 0 : 1);
					break;
				}
				case 7: // Failed Window
				{
					writeD(messageId);
					writeD(titleId);
					writeD(0);
					break;
				}
				case 8:
				{
					writeD(0);
					writeD(0);
					writeD(waitTime);
					break;
				}
			}
			writeC(0);
			writeS(name);
		}
		else
		{
			writeD(extraOption);
			writeC(option);
			writeD(worldId);
			writeD(titleId);
			writeD(messageId);
			writeD(specialOption);
			writeH(0);
			writeC(0);
		}
	}
}