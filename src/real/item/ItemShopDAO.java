package real.item;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import real.func.Shop;
import server.SQLManager;
import server.Util;

public class ItemShopDAO {

    public static Map loadItemShop() {
        Map map = new HashMap();
        Shop.gI().its = new Vector();
        try {
            ResultSet rs = SQLManager.executeQueryDATA("select * from item_shop order by npc_id asc, tab asc, create_time desc");
            while (rs.next()) {
                if(rs.getInt(7) == 1){
                    ItemShop it = new ItemShop();
                    it.npcId = rs.getInt(1);
                    it.itemTemplate = ItemData.gI().getTemplate(rs.getShort(2));
                    it.typeShop = rs.getInt(3);
                    it.tab = rs.getInt(4);
                    it.tabName = rs.getString(5);
                    it.itemNew = rs.getInt(6) == 1;
                    it.isCaiTrang = CaiTrangData.getCaiTrangByTempID(rs.getShort(2)) != null;
                    it.sell = rs.getInt(7) == 1;
                    it.gold = rs.getInt(8);
                    it.gem = rs.getInt(9);
                    it.quantity = rs.getInt(10);
                    it.itemOptions = ItemOptionShopDAO.getOption(it.npcId, it.itemTemplate.id);
                    List<ItemShop> list = (List<ItemShop>) map.get(it.npcId);
                    if (list == null) {
                        list = new ArrayList<ItemShop>();
                    }
                    list.add(it);
                    Shop.gI().its.add(it);
                    map.put(it.npcId, list);
                }
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            Util.log("ItemShopDAO.loadItemShop\n-------------");
        }
        return map;
    }

    public static ItemShop getByTemp(int tempId) {
        for (ItemShop it : Shop.gI().its) {
            if (it.itemTemplate.id == tempId) {
                return it;
            }
        }
        return null;
    }
    
    public static ItemShop getItemShop(int tempId, int npcID) {
        for (ItemShop it : Shop.gI().its) {
            if (it.npcId == npcID && it.itemTemplate.id == tempId) {
                return it;
            }
        }
        return null;
    }

}
