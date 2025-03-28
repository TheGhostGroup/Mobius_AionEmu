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
package system.database.mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.RewardServiceDAO;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;

import javolution.util.FastList;

/**
 * @author KID
 */
public class MySQL5RewardServiceDAO extends RewardServiceDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5RewardServiceDAO.class);
	public static final String UPDATE_QUERY = "UPDATE `web_reward` SET `rewarded`=?, received=NOW() WHERE `unique`=?";
	public static final String SELECT_QUERY = "SELECT * FROM `web_reward` WHERE `item_owner`=? AND `rewarded`=?";
	
	@Override
	public boolean supports(String arg0, int arg1, int arg2)
	{
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
	
	@Override
	public FastList<RewardEntryItem> getAvailable(int playerId)
	{
		final FastList<RewardEntryItem> list = FastList.newInstance();
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, 0);
			
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int unique = rset.getInt("unique");
				final int item_id = rset.getInt("item_id");
				final long count = rset.getLong("item_count");
				list.add(new RewardEntryItem(unique, item_id, count));
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.warn("getAvailable() for " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return list;
	}
	
	@Override
	public void uncheckAvailable(FastList<Integer> ids)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			for (int uniqid : ids)
			{
				stmt = con.prepareStatement(UPDATE_QUERY);
				stmt.setInt(1, 1);
				stmt.setInt(2, uniqid);
				stmt.execute();
				stmt.close();
			}
		}
		catch (Exception e)
		{
			log.error("uncheckAvailable", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
}
