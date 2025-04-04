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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.PlayerStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.services.item.ItemService;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import javolution.util.FastList;

/**
 * @author ATracer
 */
public class MySQL5InventoryDAO extends InventoryDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5InventoryDAO.class);
	public static final String SELECT_QUERY = "SELECT `item_unique_id`, `item_id`, `item_count`, `item_color`, `color_expires`, `item_creator`, `expire_time`, `activation_count`, `is_equiped`, `is_soul_bound`, `slot`, `enchant`, `enchant_bonus`, `item_skin`, `fusioned_item`, `optional_socket`, `optional_fusion_socket`, `charge`, `rnd_bonus`, `rnd_count`, `wrappable_count`, `is_packed`, `tempering_level`, `is_topped`, `strengthen_skill`, `skin_skill`, `luna_reskin`, `reduction_level`, `is_seal`  FROM `inventory` WHERE `item_owner`=? AND `item_location`=? AND `is_equiped`=?";
	public static final String INSERT_QUERY = "INSERT INTO `inventory` (`item_unique_id`, `item_id`, `item_count`, `item_color`, `color_expires`, `item_creator`, `expire_time`, `activation_count`, `item_owner`, `is_equiped`, is_soul_bound, `slot`, `item_location`, `enchant`, `enchant_bonus`, `item_skin`, `fusioned_item`, `optional_socket`, `optional_fusion_socket`, `charge`, `rnd_bonus`, `rnd_count`, `wrappable_count`, `is_packed`, `tempering_level`, `is_topped`, `strengthen_skill`, `skin_skill`, `luna_reskin`, `reduction_level`, `is_seal`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE inventory SET  item_count=?, item_color=?, color_expires=?, item_creator=?, expire_time=?, activation_count=?,item_owner=?, is_equiped=?, is_soul_bound=?, slot=?, item_location=?, enchant=?, enchant_bonus=?, item_skin=?, fusioned_item=?, optional_socket=?, optional_fusion_socket=?, charge=?, rnd_bonus=?, rnd_count=?, wrappable_count=?, is_packed=?, tempering_level=?, is_topped=?, strengthen_skill=?, skin_skill=?, luna_reskin=?, reduction_level=?, is_seal=? WHERE item_unique_id=?";
	public static final String DELETE_QUERY = "DELETE FROM inventory WHERE item_unique_id=?";
	public static final String DELETE_CLEAN_QUERY = "DELETE FROM inventory WHERE item_owner=? AND item_location != 2"; // legion warehouse needs not to be excluded, since players and legions are IDAwareDAOs
	public static final String SELECT_ACCOUNT_QUERY = "SELECT `account_id` FROM `players` WHERE `id`=?";
	public static final String SELECT_LEGION_QUERY = "SELECT `legion_id` FROM `legion_members` WHERE `player_id`=?";
	public static final String DELETE_ACCOUNT_WH = "DELETE FROM inventory WHERE item_owner=? AND item_location=2";
	public static final String SELECT_QUERY2 = "SELECT * FROM `inventory` WHERE `item_owner`=? AND `item_location`=?";
	
	private static final Predicate<Item> itemsToInsertPredicate = new Predicate<Item>()
	{
		
		@Override
		public boolean apply(@Nullable Item input)
		{
			return (input != null) && (PersistentState.NEW == input.getPersistentState());
		}
	};
	
	private static final Predicate<Item> itemsToUpdatePredicate = new Predicate<Item>()
	{
		
		@Override
		public boolean apply(@Nullable Item input)
		{
			return (input != null) && (PersistentState.UPDATE_REQUIRED == input.getPersistentState());
		}
	};
	
	private static final Predicate<Item> itemsToDeletePredicate = new Predicate<Item>()
	{
		
		@Override
		public boolean apply(@Nullable Item input)
		{
			return (input != null) && (PersistentState.DELETED == input.getPersistentState());
		}
	};
	
	@Override
	public Storage loadStorage(int playerId, StorageType storageType)
	{
		final Storage inventory = new PlayerStorage(storageType);
		final int storage = storageType.getId();
		final int equipped = 0;
		
		if (storageType == StorageType.ACCOUNT_WAREHOUSE)
		{
			playerId = loadPlayerAccountId(playerId);
		}
		
		final int owner = playerId;
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, owner);
			stmt.setInt(2, storage);
			stmt.setInt(3, equipped);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final Item item = constructItem(storage, rset);
				item.setPersistentState(PersistentState.UPDATED);
				if (item.getItemTemplate() == null)
				{
					log.error(playerId + "loaded error item, itemUniqueId is: " + item.getObjectId());
				}
				else
				{
					inventory.onLoadHandler(item);
				}
			}
			rset.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore storage data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
		return inventory;
	}
	
	@Override
	public List<Item> loadStorageDirect(int playerId, StorageType storageType)
	{
		final List<Item> list = FastList.newInstance();
		final int storage = storageType.getId();
		
		if (storageType == StorageType.ACCOUNT_WAREHOUSE)
		{
			playerId = loadPlayerAccountId(playerId);
		}
		
		final int owner = playerId;
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY2);
			stmt.setInt(1, owner);
			stmt.setInt(2, storageType.getId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				list.add(constructItem(storage, rset));
			}
			rset.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore loadStorageDirect data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
		return list;
	}
	
	@Override
	public Equipment loadEquipment(Player player)
	{
		final Equipment equipment = new Equipment(player);
		
		final int playerId = player.getObjectId();
		final int storage = 0;
		final int equipped = 1;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, storage);
			stmt.setInt(3, equipped);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final Item item = constructItem(storage, rset);
				item.setPersistentState(PersistentState.UPDATED);
				equipment.onLoadHandler(item);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore Equipment data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return equipment;
	}
	
	@Override
	public List<Item> loadEquipment(int playerId)
	{
		final List<Item> items = new ArrayList<>();
		final int storage = 0;
		final int equipped = 1;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, storage);
			stmt.setInt(3, equipped);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final Item item = constructItem(storage, rset);
				items.add(item);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore Equipment data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return items;
	}
	
	private Item constructItem(int storage, ResultSet rset) throws SQLException
	{
		final int itemUniqueId = rset.getInt("item_unique_id");
		final int itemId = rset.getInt("item_id");
		final long itemCount = rset.getLong("item_count");
		final int itemColor = rset.getInt("item_color");
		final int colorExpireTime = rset.getInt("color_expires");
		final String itemCreator = rset.getString("item_creator");
		final int expireTime = rset.getInt("expire_time");
		final int activationCount = rset.getInt("activation_count");
		final int isEquiped = rset.getInt("is_equiped");
		final int isSoulBound = rset.getInt("is_soul_bound");
		final long slot = rset.getLong("slot");
		final int enchant = rset.getInt("enchant");
		final int enchantBonus = rset.getInt("enchant_bonus");
		final int itemSkin = rset.getInt("item_skin");
		final int fusionedItem = rset.getInt("fusioned_item");
		final int optionalSocket = rset.getInt("optional_socket");
		final int optionalFusionSocket = rset.getInt("optional_fusion_socket");
		final int charge = rset.getInt("charge");
		final int randomBonus = rset.getInt("rnd_bonus");
		final int rndCount = rset.getInt("rnd_count");
		final int wrappingCount = rset.getInt("wrappable_count");
		final int isPacked = rset.getInt("is_packed");
		final int temperingLevel = rset.getInt("tempering_level");
		final int isTopped = rset.getInt("is_topped");
		final int strengthenSkill = rset.getInt("strengthen_skill");
		final int skinSkill = rset.getInt("skin_skill");
		final int isLunaReskin = rset.getInt("luna_reskin");
		final int reductionLevel = rset.getInt("reduction_level");
		final int unSeal = rset.getInt("is_seal");
		final Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, colorExpireTime, itemCreator, expireTime, activationCount, isEquiped == 1, isSoulBound == 1, slot, storage, enchant, enchantBonus, itemSkin, fusionedItem, optionalSocket, optionalFusionSocket, charge, randomBonus, rndCount, wrappingCount, isPacked == 1, temperingLevel, isTopped == 1, strengthenSkill, skinSkill, isLunaReskin == 1, reductionLevel, unSeal);
		return item;
	}
	
	private int loadPlayerAccountId(int playerId)
	{
		Connection con = null;
		int accountId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_ACCOUNT_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				accountId = rset.getInt("account_id");
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore accountId data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return accountId;
	}
	
	public int loadLegionId(int playerId)
	{
		Connection con = null;
		int legionId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_LEGION_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				legionId = rset.getInt("legion_id");
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Failed to load legion id for player id: " + playerId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return legionId;
	}
	
	@Override
	public boolean store(Player player)
	{
		final int playerId = player.getObjectId();
		final Integer accountId = player.getPlayerAccount() != null ? player.getPlayerAccount().getId() : null;
		final Integer legionId = player.getLegion() != null ? player.getLegion().getLegionId() : null;
		
		final List<Item> allPlayerItems = player.getDirtyItemsToUpdate();
		return store(allPlayerItems, playerId, accountId, legionId);
	}
	
	@Override
	public boolean store(Item item, Player player)
	{
		final int playerId = player.getObjectId();
		final int accountId = player.getPlayerAccount().getId();
		final Integer legionId = player.getLegion() != null ? player.getLegion().getLegionId() : null;
		
		return store(item, playerId, accountId, legionId);
	}
	
	@Override
	public boolean store(List<Item> items, int playerId)
	{
		
		Integer accountId = null;
		Integer legionId = null;
		
		for (Item item : items)
		{
			
			if ((accountId == null) && (item.getItemLocation() == StorageType.ACCOUNT_WAREHOUSE.getId()))
			{
				accountId = loadPlayerAccountId(playerId);
			}
			
			if ((legionId == null) && (item.getItemLocation() == StorageType.LEGION_WAREHOUSE.getId()))
			{
				final int localLegionId = loadLegionId(playerId);
				if (localLegionId > 0)
				{
					legionId = localLegionId;
				}
			}
		}
		
		return store(items, playerId, accountId, legionId);
	}
	
	@Override
	public boolean store(List<Item> items, Integer playerId, Integer accountId, Integer legionId)
	{
		final Collection<Item> itemsToUpdate = Collections2.filter(items, itemsToUpdatePredicate);
		final Collection<Item> itemsToInsert = Collections2.filter(items, itemsToInsertPredicate);
		final Collection<Item> itemsToDelete = Collections2.filter(items, itemsToDeletePredicate);
		
		boolean deleteResult = false;
		boolean insertResult = false;
		boolean updateResult = false;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			deleteResult = deleteItems(con, itemsToDelete);
			insertResult = insertItems(con, itemsToInsert, playerId, accountId, legionId);
			updateResult = updateItems(con, itemsToUpdate, playerId, accountId, legionId);
		}
		catch (SQLException e)
		{
			log.error("Can't open connection to save player inventory: " + playerId);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		for (Item item : items)
		{
			item.setPersistentState(PersistentState.UPDATED);
		}
		
		if (!itemsToDelete.isEmpty() && deleteResult)
		{
			ItemService.releaseItemIds(itemsToDelete);
		}
		
		return deleteResult && insertResult && updateResult;
	}
	
	private int getItemOwnerId(Item item, Integer playerId, Integer accountId, Integer legionId)
	{
		if (item.getItemLocation() == StorageType.ACCOUNT_WAREHOUSE.getId())
		{
			return accountId;
		}
		
		if (item.getItemLocation() == StorageType.LEGION_WAREHOUSE.getId())
		{
			return legionId != null ? legionId : playerId;
		}
		
		return playerId;
	}
	
	private boolean insertItems(Connection con, Collection<Item> items, Integer playerId, Integer accountId, Integer legionId)
	{
		
		if (GenericValidator.isBlankOrNull(items))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(INSERT_QUERY);
			
			for (Item item : items)
			{
				stmt.setInt(1, item.getObjectId());
				stmt.setInt(2, item.getItemTemplate().getTemplateId());
				stmt.setLong(3, item.getItemCount());
				stmt.setInt(4, item.getItemColor());
				stmt.setInt(5, item.getColorExpireTime());
				stmt.setString(6, item.getItemCreator());
				stmt.setInt(7, item.getExpireTime());
				stmt.setInt(8, item.getActivationCount());
				stmt.setInt(9, getItemOwnerId(item, playerId, accountId, legionId));
				stmt.setBoolean(10, item.isEquipped());
				stmt.setInt(11, item.isSoulBound() ? 1 : 0);
				stmt.setLong(12, item.getEquipmentSlot());
				stmt.setInt(13, item.getItemLocation());
				stmt.setInt(14, item.getEnchantLevel());
				stmt.setInt(15, item.getEnchantBonus());
				stmt.setInt(16, item.getItemSkinTemplate().getTemplateId());
				stmt.setInt(17, item.getFusionedItemId());
				stmt.setInt(18, item.getOptionalSocket());
				stmt.setInt(19, item.getOptionalFusionSocket());
				stmt.setInt(20, item.getChargePoints());
				stmt.setInt(21, item.getBonusNumber());
				stmt.setInt(22, item.getRandomCount());
				stmt.setInt(23, item.getWrappableCount());
				stmt.setBoolean(24, item.isPacked());
				stmt.setInt(25, item.getAuthorize());
				stmt.setBoolean(26, item.isAmplified());
				stmt.setInt(27, item.getAmplificationSkill());
				stmt.setInt(28, item.getItemSkinSkill());
				stmt.setBoolean(29, item.isLunaReskin());
				stmt.setInt(30, item.getReductionLevel());
				stmt.setInt(31, item.getUnSeal());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			// log.error("Failed to execute insert batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		return true;
	}
	
	private boolean updateItems(Connection con, Collection<Item> items, Integer playerId, Integer accountId, Integer legionId)
	{
		
		if (GenericValidator.isBlankOrNull(items))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(UPDATE_QUERY);
			
			for (Item item : items)
			{
				stmt.setLong(1, item.getItemCount());
				stmt.setInt(2, item.getItemColor());
				stmt.setInt(3, item.getColorExpireTime());
				stmt.setString(4, item.getItemCreator());
				stmt.setInt(5, item.getExpireTime());
				stmt.setInt(6, item.getActivationCount());
				stmt.setInt(7, getItemOwnerId(item, playerId, accountId, legionId));
				stmt.setBoolean(8, item.isEquipped());
				stmt.setInt(9, item.isSoulBound() ? 1 : 0);
				stmt.setLong(10, item.getEquipmentSlot());
				stmt.setInt(11, item.getItemLocation());
				stmt.setInt(12, item.getEnchantLevel());
				stmt.setInt(13, item.getEnchantBonus());
				stmt.setInt(14, item.getItemSkinTemplate().getTemplateId());
				stmt.setInt(15, item.getFusionedItemId());
				stmt.setInt(16, item.getOptionalSocket());
				stmt.setInt(17, item.getOptionalFusionSocket());
				stmt.setInt(18, item.getChargePoints());
				stmt.setInt(19, item.getBonusNumber());
				stmt.setInt(20, item.getRandomCount());
				stmt.setInt(21, item.getWrappableCount());
				stmt.setBoolean(22, item.isPacked());
				stmt.setInt(23, item.getAuthorize());
				stmt.setBoolean(24, item.isAmplified());
				stmt.setInt(25, item.getAmplificationSkill());
				stmt.setInt(26, item.getItemSkinSkill());
				stmt.setBoolean(27, item.isLunaReskin());
				stmt.setInt(28, item.getReductionLevel());
				stmt.setInt(29, item.getUnSeal());
				stmt.setInt(30, item.getObjectId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute update batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		return true;
	}
	
	private boolean deleteItems(Connection con, Collection<Item> items)
	{
		
		if (GenericValidator.isBlankOrNull(items))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(DELETE_QUERY);
			for (Item item : items)
			{
				stmt.setInt(1, item.getObjectId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute delete batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		return true;
	}
	
	/**
	 * Since inventory is not using FK - need to clean items
	 */
	@Override
	public boolean deletePlayerItems(int playerId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_CLEAN_QUERY);
			stmt.setInt(1, playerId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error Player all items. PlayerObjId: " + playerId, e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return true;
	}
	
	@Override
	public void deleteAccountWH(int accountId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_ACCOUNT_WH);
			stmt.setInt(1, accountId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error deleting all items from account WH. AccountId: " + accountId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT item_unique_id FROM inventory", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		try
		{
			final ResultSet rs = statement.executeQuery();
			rs.last();
			final int count = rs.getRow();
			rs.beforeFirst();
			final int[] ids = new int[count];
			for (int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("item_unique_id");
			}
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from inventory table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
