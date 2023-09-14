package real.item;

import java.util.ArrayList;
import java.util.List;
import real.player.Player;
import server.Service;
import server.Util;

public class ItemTimes {

    private final Player player;
    public List<ItemTime> vItemTime = new ArrayList<>();
    
    public ItemTimes(Player player) {
        this.player = player;
    }

    public void addItemTime(int iconID, int time) {
        Service.gI().sendItemTime(player, iconID, time);
        ItemTime it = GetItemTiem(iconID);
        if(it != null)
        {
            it.time = time;
            return;
        }
        vItemTime.add(new ItemTime(iconID, time));
    }
    
    public void removeItemTime(int iconID, int...del)
    {
        if((iconID == 7095 || iconID == 7096) && del.length == 0)
        {
            Service.gI().sendThongBao(player, "Bánh đã nấu xong, đến gặp Nồi Bánh để nhận nhé");
            return;
        }
        for (int i = 0; i < vItemTime.size(); i++)
        {
            ItemTime it = vItemTime.get(i);
            if (it.iconID == iconID)
            {
                Service.gI().sendItemTime(player, iconID, 0);
                vItemTime.remove(i);
                return;
            }
        }
        Service.gI().point(player);
    }

//    public void loadItemTime() {
//        for (int i = 0; i < vItemTime.size(); i++) {
//            ItemTime it = vItemTime.get(i);
//            if (it.iconID != -1 && it.time > 0) {
//                
//            }
//        }
//    }

    public boolean ExitsItemTiem(int iconID) {
        for (int i = 0; i < vItemTime.size(); i++) {
            ItemTime it = vItemTime.get(i);
            if (it.iconID == iconID) {
                return true;
            }
        }
        return false;
    }

    public ItemTime GetItemTiem(int iconID) {
        for (int i = 0; i < vItemTime.size(); i++) {
            ItemTime it = vItemTime.get(i);
            if (it.iconID == iconID) {
                return it;
            }
        }
        return null;
    }
    
    public boolean isHaveItemTime() {
        return vItemTime.size() > 0;
    }
    
    public void Update() {
        for (int i = 0; i < vItemTime.size(); i++) {
            ItemTime it = vItemTime.get(i);
            if (it.iconID != -1 && it.time > 0) {
                it.time--;
                Service.gI().sendItemTime(player, it.iconID, it.time);
                if (it.time <= 0) {
                    removeItemTime(it.iconID);
                }
            }
        }
    }
}
