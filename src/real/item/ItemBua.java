package real.item;

import java.util.List;
import real.player.Player;

public class ItemBua {
    
    public static boolean ItemBuaExits(Player pl, int id) {
        return pl.inventory.itemsBua.stream().anyMatch((op) -> (op.itemTemplateId == id && op.param > 0));
    }

    public static boolean CanPutTime(Player pl, List<ItemOptionShop> options) {
        for(ItemOptionShop ops : options){
            if(!pl.inventory.itemsBua.stream().anyMatch(op -> op!=null && op.itemTemplateId == ops.itemTemplateId && op.param >= 0)){
                return false;
            }
        }
        return true;
    }

    public static int TimeBua(Player pl, int itemid, int param) {
        for (ItemOptionShop op : pl.inventory.itemsBua) {
            if (op.itemTemplateId == itemid) {
                return op.param += param;
            }
        }
        return -1;
    }

    public static void createBua(Player pl, List<ItemOptionShop> itemOptions) {
        try {
            if (!CanPutTime(pl, itemOptions)) {
                for (int i = 0; i < itemOptions.size(); i++) {
                    pl.inventory.itemsBua.add(itemOptions.get(i));
                }
            } else {
                for (int i = 0; i < itemOptions.size(); i++) {
                    for(ItemOptionShop op : pl.inventory.itemsBua){
                        if(op.itemTemplateId == itemOptions.get(i).itemTemplateId){
                            op.param = TimeBua(pl, itemOptions.get(i).itemTemplateId, itemOptions.get(i).param);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public static void UpdateTimeBua(Player pl, List<ItemOptionShop> itemOptions) {
        try {
            for (int i = 0; i < itemOptions.size(); i++) {
                if (itemOptions.get(i).param > 0) {
                    for(ItemOptionShop op : pl.inventory.itemsBua){
                        if(op.itemTemplateId == itemOptions.get(i).itemTemplateId){
                            op.param = TimeBua(pl, itemOptions.get(i).itemTemplateId, -1);
                        }
                    }
                } else if (itemOptions.get(i).param <= 0) {
                    pl.inventory.itemsBua.remove(itemOptions.get(i));
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
