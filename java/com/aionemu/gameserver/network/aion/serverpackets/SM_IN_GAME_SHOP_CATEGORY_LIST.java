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

import com.aionemu.gameserver.configs.ingameshop.InGameShopProperty;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.templates.ingameshop.IGCategory;
import com.aionemu.gameserver.model.templates.ingameshop.IGSubCategory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author xTz
 */
public class SM_IN_GAME_SHOP_CATEGORY_LIST extends AionServerPacket
{
	private final int type;
	private final int categoryId;
	private final InGameShopProperty ing;
	
	public SM_IN_GAME_SHOP_CATEGORY_LIST(int type, int category)
	{
		this.type = type;
		categoryId = category;
		ing = InGameShopEn.getInstance().getIGSProperty();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(type);
		switch (type)
		{
			case 0:
			{
				writeH(ing.size());
				for (IGCategory category : ing.getCategories())
				{
					writeD(category.getId());
					writeS(category.getName());
				}
				break;
			}
			case 2:
			{
				if (categoryId < ing.size())
				{
					final IGCategory iGCategory = ing.getCategories().get(categoryId);
					writeH(iGCategory.getSubCategories().size());
					for (IGSubCategory subCategory : iGCategory.getSubCategories())
					{
						writeD(subCategory.getId());
						writeS(subCategory.getName());
					}
				}
				break;
			}
		}
	}
}
