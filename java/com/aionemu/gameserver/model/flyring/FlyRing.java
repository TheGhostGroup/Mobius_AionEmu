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
package com.aionemu.gameserver.model.flyring;

import com.aionemu.gameserver.controllers.FlyRingController;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Plane3D;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.SphereKnownList;

/**
 * @author xavier
 */
public class FlyRing extends VisibleObject
{
	private FlyRingTemplate template = null;
	private String name = null;
	private Plane3D plane = null;
	private Point3D center = null;
	private Point3D left = null;
	private Point3D right = null;
	
	public FlyRing(FlyRingTemplate template, int instanceId)
	{
		super(IDFactory.getInstance().nextId(), new FlyRingController(), null, null, World.getInstance().createPosition(template.getMap(), template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ(), (byte) 0, instanceId));
		((FlyRingController) getController()).setOwner(this);
		this.template = template;
		name = (template.getName() == null) ? "FLY_RING" : template.getName();
		center = new Point3D(template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ());
		left = new Point3D(template.getLeft().getX(), template.getLeft().getY(), template.getLeft().getZ());
		right = new Point3D(template.getRight().getX(), template.getRight().getY(), template.getRight().getZ());
		plane = new Plane3D(center, left, right);
		setKnownlist(new SphereKnownList(this, template.getRadius() * 2));
	}
	
	public Plane3D getPlane()
	{
		return plane;
	}
	
	public FlyRingTemplate getTemplate()
	{
		return template;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public void spawn()
	{
		World.getInstance().spawn(this);
	}
}