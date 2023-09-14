package real.item;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import real.map.ItemMap;
import real.player.Player;
import server.Util;
import service.Setting;

public final class Item {

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public long buyTime;
    // Ký gửi
    public int Sell;
    public int gold;
    public int gem;
    public int itemID = -1;
    public int player_id = -1;
    // end
    public List<ItemOption> itemOptions = new ArrayList<>();
    
    public Item() {
        if(itemID == -1){
            itemID = Setting.ITEM_ID++;
        }
    }

    @Override
    public boolean equals(Object it){
        Item item = (Item)it;
        if(template.id != item.template.id)
            return false;
        
        if(itemID != item.itemID)
            return false;
        
        if(itemOptions.size() != item.itemOptions.size())
            return false;
        
        for(byte i = 0; i < itemOptions.size(); i++){
            if(itemOptions.get(i).optionTemplate.id != item.itemOptions.get(i).optionTemplate.id)
                return false;
            if(itemOptions.get(i).param !=  item.itemOptions.get(i).param)
                return false;
        }
        return true;
    }
    
    public boolean contains(Object it)
    {
        Item item = (Item)it;
        if(template.id != item.template.id)
            return false;
        
        if(this.gold != item.gold)
            return false;
        
        if(this.gem != item.gem)
            return false;
        
        if(this.Sell != item.Sell)
            return false;
        
        if(itemOptions.size() != item.itemOptions.size())
            return false;
        
        for(byte i = 0; i < itemOptions.size(); i++){
            if(itemOptions.get(i).optionTemplate.id != item.itemOptions.get(i).optionTemplate.id)
                return false;
            if(itemOptions.get(i).param !=  item.itemOptions.get(i).param)
                return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        final String n = "\"";
        return "{"
                + n + "temp_id" + n + ":" + n + template.id + n + ","
                + n + "quantity" + n + ":" + n + quantity + n + ","
                + n + "option" + n + ":" + itemOptions + n + ","
                + n + "Sell" + n + ":" + n + Sell + n + ","
                + n + "gold" + n + ":" + n + gold  + n + ","
                + n + "gem" + n + ":" + n + gem + n + ","
                + n + "buyTime" + n + ":" + buyTime
                + "}";
    }
    
    public Item(ItemMap item) {
        try{
            template = ItemData.gI().getTemplate(item.itemTemplate.id);
            this.info = getInfo();
            this.content = getContent();
            this.quantity = item.quantity;
            if (!item.options.isEmpty()) {
                for (ItemOption ios : item.options) {
                    itemOptions.add(new ItemOption(ios.optionTemplate.id, ios.param));
                }
            } else {
                itemOptions.add(new ItemOption(73, (short)0));
            }
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            this.buyTime = timenow.getTime();
            if(itemID == -1){
                itemID = Setting.ITEM_ID++;
            }
        }
        catch (Exception e) {
            Util.log("Item new ItemMap\n------------");
        }
    }

    public Item(Item item) {
        try{
            this.template = item.template;
            this.info = item.info;
            this.content = item.content;
            this.quantity = item.quantity;
            this.itemOptions.addAll(item.itemOptions);
            this.buyTime = item.buyTime;
            if(itemID == -1){
                itemID = Setting.ITEM_ID++;
            }
        }
        catch (Exception e) {
            Util.log("Item new\n------------");
        }
    }
    
    public Item(Item item, int quantity, int gold, int gem, int Sell) {
        try{
            this.template = item.template;
            this.info = item.info;
            this.content = item.content;
            this.itemOptions.addAll(item.itemOptions);
            this.quantity = quantity;
            this.gold = gold;
            this.gem = gem;
            this.Sell = Sell;
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            this.buyTime = timenow.getTime();
            if(itemID == -1){
                itemID = Setting.ITEM_ID++;
            }
        }
        catch (Exception e) {
            Util.log("Item new\n------------");
        }
    }
    
    public String getInfo() {
        try
        {
            String strInfo = "";
            for (ItemOption op : itemOptions) {
                if(op != null && op.optionTemplate != null){
                    strInfo += op.getOptionString();
                }
            }
            return strInfo;
        }
        catch (Exception e) {
            Util.log("Item getInfo\n------------");
        }
        return "";
    }

    public String getInfo(Player pl) {
        try
        {
            int[] opid = new int[]{ 136, 137, 138, 139, 140, 141, 142, 143, 144};
            for (ItemOption op : itemOptions)
            {
                for(int opSKH : opid)
                {
                    if(opSKH == op.optionTemplate.id && (pl.isPl() && !pl.inventory.checkOption() || (pl.pet != null && !pl.pet.inventory.checkOption())))
                    {
                        op.param = 1;
                    }
                    else if(opSKH == op.optionTemplate.id && (pl.isPl() && pl.inventory.checkOption() || (pl.pet != null && pl.pet.inventory.checkOption())))
                    {
                        op.param = 0;
                    }
                }
            }
            String strInfo = "";
            for (ItemOption op : itemOptions) {
                if(op != null && op.optionTemplate != null){
                    strInfo += op.getOptionString();
                }
            }
            return strInfo;
        }
        catch (Exception e) {
            Util.log("Item getInfo\n------------");
        }
        return "";
    }

    public ItemOption GetItemOption(int id) {
        for (ItemOption i : itemOptions) {
            if (i != null && i.optionTemplate.id == id) {
                return i;
            }
        }
        return null;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }
    
    public int getTime(int type){
        int time = 0;
        Date date=new Date(this.buyTime);  
        switch(type){
            case 0:
                time = date.getYear();
                break;
            case 1:
                time = date.getMonth();
                break;
            case 2:
                time = date.getDay();
                break;
            case 3:
                time = date.getHours();
                break;
            case 4:
                time = date.getMinutes();
                break;
        }
        return time;
    }
}
