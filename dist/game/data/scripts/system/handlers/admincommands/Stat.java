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

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunctionProxy;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author MrPoke
 */
public class Stat extends AdminCommand
{
	private static final Logger log = LoggerFactory.getLogger(Stat.class);
	
	public Stat()
	{
		super("stat");
	}
	
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length >= 1)
		{
			final VisibleObject target = admin.getTarget();
			if (target == null)
			{
				PacketSendUtility.sendMessage(admin, "No target selected");
				return;
			}
			if (target instanceof Creature)
			{
				final Creature creature = (Creature) target;
				
				final TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
				
				if (params.length == 1)
				{
					for (IStatFunction stat : stats)
					{
						PacketSendUtility.sendMessage(admin, stat.toString());
					}
				}
				else if ("details".equals(params[1]))
				{
					for (IStatFunction stat : stats)
					{
						final String details = collectDetails(stat);
						PacketSendUtility.sendMessage(admin, details);
						log.info(details);
					}
				}
			}
		}
	}
	
	private String collectDetails(IStatFunction stat)
	{
		final StringBuffer sb = new StringBuffer();
		sb.append(stat.toString() + "\n");
		if (stat instanceof StatFunctionProxy)
		{
			final StatFunctionProxy proxy = (StatFunctionProxy) stat;
			sb.append(" -- " + proxy.getProxiedFunction().toString());
		}
		final StatOwner owner = stat.getOwner();
		if (owner instanceof Effect)
		{
			final Effect effect = (Effect) owner;
			sb.append("\n -- skillId: " + effect.getSkillId());
			sb.append("\n -- skillName: " + effect.getSkillName());
		}
		return sb.toString();
	}
	
	@Override
	public void onFail(Player player, String message)
	{
		// TODO Auto-generated method stub
	}
}
