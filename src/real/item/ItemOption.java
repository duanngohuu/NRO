package real.item;
//share by chibikun

import server.Util;

public class ItemOption {

    public int param;
    public int activeCard;

    public ItemOptionTemplate optionTemplate;

    public ItemOption() {
    }

    public ItemOption(ItemOption io) {
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }

    public ItemOption(int tempId, int param) {
        this.optionTemplate = ItemData.gI().getItemOptionTemplate(tempId);
        this.param = param;
    }
    
    public ItemOption(int tempId, int param, int activeCard) {
        this.optionTemplate = ItemData.gI().getItemOptionTemplate(tempId);
        this.param = param;
        this.activeCard = activeCard;
    }

    public String getOptionString() {
        return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
    }
    
    public int[] getItemOption(int optionID, int param)
    {
        int op = optionID;
        int pr = param;
        if(optionID == 6 || optionID == 7){
            pr = param > Short.MAX_VALUE ? (int)Util.IntToLong(param) : param;
            op = param > Short.MAX_VALUE ? (optionID == 6 ? 22 : optionID == 7 ? 23 : optionID) : optionID;
        }
        return new int[]{
            op,
            pr
        };
    }

    @Override
    public String toString() {
        final String n = "\"";
        return "{"
                + n + "id" + n + ":" + n + optionTemplate.id + n + ","
                + n + "param" + n + ":" + n + param + n
                + "}";
    }
    @Override
    public boolean equals(Object it){
        ItemOption item = (ItemOption)it;
        if(this.optionTemplate.id != item.optionTemplate.id)
            return false;
        
        if(this.param != item.param)
            return false;
        
        return true;
    }
}
