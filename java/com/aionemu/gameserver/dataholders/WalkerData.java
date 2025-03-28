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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;

import javolution.util.FastMap;

/**
 * @author KKnD, Rolandas
 */
@XmlRootElement(name = "npc_walker")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalkerData
{
	private static final Logger log = LoggerFactory.getLogger(WalkerData.class);
	
	@XmlElement(name = "walker_template")
	private List<WalkerTemplate> walkerlist;
	
	@XmlTransient
	private final FastMap<String, WalkerTemplate> walkerlistData = new FastMap<>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (WalkerTemplate route : walkerlist)
		{
			if (walkerlistData.containsKey(route.getRouteId()))
			{
				log.warn("Duplicate route ID: " + route.getRouteId());
				continue;
			}
			walkerlistData.put(route.getRouteId(), route);
		}
		walkerlist.clear();
		walkerlist = null;
	}
	
	public int size()
	{
		return walkerlistData.size();
	}
	
	public WalkerTemplate getWalkerTemplate(String routeId)
	{
		if (routeId == null)
		{
			return null;
		}
		return walkerlistData.get(routeId);
	}
	
	public void AddTemplate(WalkerTemplate newTemplate)
	{
		if (walkerlist == null)
		{
			walkerlist = new ArrayList<>();
		}
		walkerlist.add(newTemplate);
	}
	
	public void saveData(String routeId)
	{
		Schema schema = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		try
		{
			schema = sf.newSchema(new File("./data/static_data/npc_walker/npc_walker.xsd"));
		}
		catch (SAXException e1)
		{
			log.error("Error while saving data: " + e1.getMessage(), e1.getCause());
			return;
		}
		
		final File xml = new File("./data/static_data/npc_walker/generated_npc_walker_" + routeId + ".xml");
		JAXBContext jc;
		Marshaller marshaller;
		try
		{
			jc = JAXBContext.newInstance(WalkerData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, xml);
		}
		catch (JAXBException e)
		{
			log.error("Error while saving data: " + e.getMessage(), e.getCause());
			return;
		}
		finally
		{
			if (walkerlist != null)
			{
				walkerlist.clear();
				walkerlist = null;
			}
		}
	}
	
	public Collection<WalkerTemplate> getTemplates()
	{
		return walkerlistData.values();
	}
	
}
