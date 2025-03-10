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
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.npcshout.ShoutGroup;
import com.aionemu.gameserver.model.templates.npcshout.ShoutList;

import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;

/**
 * @author Rolandas
 */
/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shout_group" type="{}ShoutGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"shoutGroups"
})
@XmlRootElement(name = "npc_shouts")
public class NpcShoutData
{
	@XmlElement(name = "shout_group")
	protected List<ShoutGroup> shoutGroups;
	
	@XmlTransient
	private final TIntObjectHashMap<FastMap<Integer, List<NpcShout>>> shoutsByWorldNpcs = new TIntObjectHashMap<>();
	
	@XmlTransient
	private int count;
	
	public void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ShoutGroup group : shoutGroups)
		{
			for (int i = group.getShoutNpcs().size() - 1; i >= 0; i--)
			{
				final ShoutList shoutList = group.getShoutNpcs().get(i);
				final int worldId = shoutList.getRestrictWorld();
				
				FastMap<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(worldId);
				if (worldShouts == null)
				{
					worldShouts = FastMap.newInstance();
					shoutsByWorldNpcs.put(worldId, worldShouts);
				}
				
				count += shoutList.getNpcShouts().size();
				for (int j = shoutList.getNpcIds().size() - 1; j >= 0; j--)
				{
					final int npcId = shoutList.getNpcIds().get(j);
					final List<NpcShout> shouts = new ArrayList<>(shoutList.getNpcShouts());
					if (worldShouts.get(npcId) == null)
					{
						worldShouts.put(npcId, shouts);
					}
					else
					{
						worldShouts.get(npcId).addAll(shouts);
					}
					shoutList.getNpcIds().remove(j);
				}
				shoutList.getNpcShouts().clear();
				shoutList.makeNull();
				group.getShoutNpcs().remove(i);
			}
			group.makeNull();
		}
		shoutGroups.clear();
		shoutGroups = null;
	}
	
	public int size()
	{
		return count;
	}
	
	/**
	 * Get global npc shouts plus world specific shouts. Make sure to clean it after the use.
	 * @param worldId
	 * @param npcId
	 * @return null if not found
	 */
	public List<NpcShout> getNpcShouts(int worldId, int npcId)
	{
		FastMap<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(0);
		
		if ((worldShouts == null) || (worldShouts.get(npcId) == null))
		{
			worldShouts = shoutsByWorldNpcs.get(worldId);
			if ((worldShouts == null) || (worldShouts.get(npcId) == null))
			{
				return null;
			}
			return new ArrayList<>(worldShouts.get(npcId));
		}
		
		final List<NpcShout> npcShouts = new ArrayList<>(worldShouts.get(npcId));
		worldShouts = shoutsByWorldNpcs.get(worldId);
		if ((worldShouts == null) || (worldShouts.get(npcId) == null))
		{
			return npcShouts;
		}
		npcShouts.addAll(worldShouts.get(npcId));
		
		return npcShouts;
	}
	
	/**
	 * Lightweight check for shouts, doesn't use memory as {@link #getNpcShouts(int worldId, int npcId)})
	 * @param worldId
	 * @param npcId
	 * @return
	 */
	public boolean hasAnyShout(int worldId, int npcId)
	{
		FastMap<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(0);
		
		if ((worldShouts == null) || (worldShouts.get(npcId) == null))
		{
			worldShouts = shoutsByWorldNpcs.get(worldId);
			if ((worldShouts == null) || (worldShouts.get(npcId) == null))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Lightweight check for shouts, doesn't use memory as {@link #getNpcShouts(int worldId, int npcId, ShoutEventType type, String pattern, int skillNo)})
	 * @param worldId
	 * @param npcId
	 * @param type
	 * @return
	 */
	public boolean hasAnyShout(int worldId, int npcId, ShoutEventType type)
	{
		final List<NpcShout> shouts = getNpcShouts(worldId, npcId);
		if (shouts == null)
		{
			return false;
		}
		
		for (NpcShout s : shouts)
		{
			if (s.getWhen() == type)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets shouts for npc
	 * @param worldId - npc World Id
	 * @param npcId - npc Id
	 * @param type - shout event type
	 * @param pattern - specific pattern; if null, returns all
	 * @param skillNo - specific skill number; if 0, returns all
	 * @return
	 */
	public List<NpcShout> getNpcShouts(int worldId, int npcId, ShoutEventType type, String pattern, int skillNo)
	{
		final List<NpcShout> shouts = getNpcShouts(worldId, npcId);
		if (shouts == null)
		{
			return null;
		}
		
		final List<NpcShout> result = new ArrayList<>();
		for (NpcShout s : shouts)
		{
			if (s.getWhen() == type)
			{
				if ((pattern != null) && !pattern.equals(s.getPattern()))
				{
					continue;
				}
				if ((skillNo != 0) && (skillNo != s.getSkillNo()))
				{
					continue;
				}
				result.add(s);
			}
		}
		shouts.clear();
		return result.size() > 0 ? result : null;
	}
}
