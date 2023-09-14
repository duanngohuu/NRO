package real.func;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.ItemMap;
import real.map.MapManager;
import real.map.Zone;
import real.player.Player;
import server.Service;
import server.Util;
import server.io.Message;
import service.Setting;

public class NRSD {
    public static int[][] mapNRSD = {{85,372},{86,373},{87,374},{88,375},{89,376},{90,377},{91,378}};
    public static int DropItem = 0;
    private static NRSD instance;
    
    public static NRSD gI() {
        if (instance == null) {
            instance = new NRSD();
        }
        return instance;
    }
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

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
                    NRSD.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
    
    public void update(){
        try{
            if(DHVT.gI().Hour == Setting.TIME_START){
                for(int k = 0 ; k < mapNRSD.length ; k++)
                {
                    for(int i = 0 ; i < MapManager.gI().getMapById(mapNRSD[k][0]).map_zone.length ; i++)
                    {
                        Zone zone = MapManager.gI().getMapById(mapNRSD[k][0]).map_zone[i];
                        if(zone != null && !zone.zoneDoneNRSD){
                            int itemId = mapNRSD[k][1];
                            if(!zone.items.stream().anyMatch(it -> it.itemTemplate.id == itemId)){
                                if(zone.players.stream().allMatch(pl -> pl != null && pl.idNRSD == -1) || zone.players.isEmpty()){
                                    int x = Util.nextInt(100, zone.map.mapWidth - 100);
                                    Service.gI().roiItem(ItemData.gI().get_item(itemId), zone, x, zone.LastY(x, 50), -1);
                                }
                            }
                            else if(!zone.players.isEmpty()) {
                                for(Player pl : zone.players){
                                    if(pl.isPl()){
                                        if(Util.canDoWithTime(zone.currMOVE_NRSD, 10000)){
                                            zone.currMOVE_NRSD = System.currentTimeMillis();
                                            int x = pl.x;
                                            int y = pl.y;
                                            ItemMap itemMap = zone.getItemInMap(itemId);
                                            long createTime = itemMap.createTime;
                                            ItemMap.removeItemMap(itemMap);
                                            Item item = ItemData.gI().get_item(itemId);
                                            item.buyTime = createTime;
                                            Service.gI().roiItem(item, zone, x, zone.LastY(x, y), -1);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }
   
    public static void OpenTabMap(Player pl){
        pl.mapCapsule = MapManager.gI().getMapNRSD(pl);
        Message msg = null;
        try {
            msg = new Message(-91);
            msg.writer().writeByte(pl.mapCapsule.size());
            for (int i = 0; i < pl.mapCapsule.size(); i++) {
                Zone map = pl.mapCapsule.get(i);
                msg.writer().writeUTF(map.map.name);
                msg.writer().writeUTF("Hành Tinh");
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public static boolean isNRSD(short id){
        return id == 372 || id == 373 || id == 374 || id == 375 || id == 376 || id == 377 || id == 378;
    }
    
    public static int getIdItemByMapID(int id){
        int iditem = -1;
        switch(id){
            case 85:
                iditem = 372;
                break;
            case 86:
                iditem = 373;
                break;
            case 87:
                iditem = 374;
                break;
            case 88:
                iditem = 375;
                break;
            case 89:
                iditem = 376;
                break;
            case 90:
                iditem = 377;
                break;
            case 91:
                iditem = 378;
                break;
        }
        return iditem;
    }
    
    public static int getMapIdByItemID(int id){
        int mapid = -1;
        switch(id){
            case 372:
                mapid = 85;
                break;
            case 373:
                mapid = 86;
                break;
            case 374:
                mapid = 87;
                break;
            case 375:
                mapid = 88;
                break;
            case 376:
                mapid = 89;
                break;
            case 377:
                mapid = 90;
                break;
            case 378:
                mapid = 91;
                break;
        }
        return mapid;
    }
    
    public static List<ItemOption> addOption(Item item){
        List<ItemOption> l = new ArrayList<>();
        switch(item.template.id){
            case 372:
                l.add(new ItemOption(50,21));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
            case 373:
                l.add(new ItemOption(77,35));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
            case 374:
                l.add(new ItemOption(95,35));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
            case 375:
                l.add(new ItemOption(97,35));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
            case 376:
                l.add(new ItemOption(5,35));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
            case 377:
                l.add(new ItemOption(103,40));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
            case 378:
                l.add(new ItemOption(108,14));
                l.add(new ItemOption(93,1));
                l.add(new ItemOption(30,1));
                break;
        }
        return l;
    }
    
    public static Item getNRSD(Player pl){
        Item item = ItemData.gI().get_item(NRSD.getIdItemByMapID(pl.zone.map.id));
        item.itemOptions.clear();
        item.itemOptions = addOption(item);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 20:00:00");
        Date date = new Date();    
        String strDate = formatter.format(date);
        Timestamp timenow = Timestamp.valueOf(strDate);
        item.buyTime = timenow.getTime();
        return item;
    }
}
