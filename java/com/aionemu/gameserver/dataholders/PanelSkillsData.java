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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.panels.SkillPanel;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "polymorph_panels")
public class PanelSkillsData
{
	@XmlElement(name = "panel")
	protected List<SkillPanel> templates;
	private final TIntObjectHashMap<SkillPanel> skillPanels = new TIntObjectHashMap<>();
	
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		for (SkillPanel panel : templates)
		{
			skillPanels.put(panel.getPanelId(), panel);
		}
		templates.clear();
		templates = null;
	}
	
	public SkillPanel getSkillPanel(int id)
	{
		return skillPanels.get(id);
	}
	
	public int size()
	{
		return skillPanels.size();
	}
}
