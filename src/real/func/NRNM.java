package real.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.item.Item;
import real.item.ItemData;
import real.map.ItemMap;
import real.map.Map;
import real.map.MapManager;
import real.map.Zone;
import real.player.Player;
import server.Service;
import server.Util;
import service.Setting;

public class NRNM {
    private static NRNM instance;
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;
    public boolean isDrop = false;
    
    public Zone zone;
    public Player player;
    public Item item;
    
    public List<NRNM> NRNMS = new ArrayList();
    public List<Map> Maps = MapManager.gI().getListByPlanet(1).stream().filter(m -> m.id != 40 && m.id != 149 && m.id != 22).toList();;
    public int[] NRNM_ID = {353, 354, 355, 356, 357, 358, 359};
    
    public static NRNM gI() {
        if (instance == null) {
            instance = new NRNM(null, null, null);
        }
        return instance;
    }
    
    public NRNM(Zone zone, Player player, Item item){
        this.zone = zone;
        this.player = player;
        this.item = item;
    }
    
    public NRNM findNRNM(int itemID){
        try
        {
            for(NRNM nm : NRNMS){
                if(nm.item.template.id == itemID){
                    return nm;
                }
            }
        }
        catch (Exception e)
        {
        }
        return null;
    }
    
    public void SetPlayer(Player player, int itemID, boolean...exit){
        for(NRNM nm : NRNMS)
        {
            if(nm.item.template.id == itemID)
            {
                if(nm.player != null && player == null)
                {
                    Service.gI().changeTypePK(nm.player, 0);
                }
                if(player != null)
                {
                    player.idNRNM = (short)itemID;
                    Service.gI().sendBag(player);
                    Service.gI().changeTypePK(player, 5);
                }
                else if(nm.player != null && player == null)
                {
                    nm.zone = nm.player.zone;
                    Item item = ItemData.gI().get_item(itemID);
                    ItemMap itemMap = new ItemMap(nm.player.zone, item.template, item.quantity, nm.player.x, nm.player.zone.LastY(nm.player.x, nm.player.y), -1);
                    itemMap.options = item.itemOptions;
                    if(exit.length > 0){
                        Service.gI().roiItemNotMe(nm.player, itemMap);
                    }
                    else{
                        Service.gI().roiItem(nm.player, itemMap);
                    }
                }
//                else if(point.length > 0){
//                    nm.zone = nm.player.zone;
//                    Item item = ItemData.gI().get_item(itemID);
//                    ItemMap itemMap = new ItemMap(nm.zone, item.template, item.quantity, point[0], nm.zone.LastY(point[0], point[1]), -1);
//                    itemMap.options = item.itemOptions;
//                    Service.gI().roiItem(nm.player, itemMap);
//                }
                nm.player = player;
                break;
            }
        }
    }
    
    public void close() {
        try {
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
        } catch (Exception e) {
            task = null;
            timer = null;
        }
    }
    
    public void active(int delay) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    NRNM.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
    
    public void update(){
        try
        {
            if(!isDrop){
                NRNMS.clear();
                for(int i = 0; i < NRNM_ID.length; i++)
                {
                    int itemID = NRNM_ID[i];
                    Item item = ItemData.gI().get_item(itemID);
                    Map map = Maps.get(Util.nextInt(0, Maps.size() - 1));
                    zone = map.map_zone[Util.nextInt(0, map.map_zone.length - 1)];
                    NRNMS.add(new NRNM(zone, null, item));
                    int x = Util.nextInt(100, zone.map.pxh - 100);
                    int cy = zone.LastY(x, 24);
                    Service.gI().roiItem(item, zone, x, cy, -1);
                }
                isDrop = true;
            }
            else
            {
                NRNMS.forEach((nr)->{
                    if(nr.player != null)
                    {
                        nr.zone = nr.player.zone;
                    }
                });
            }
        }
        catch (Exception e)
        {
        }
    }
}
