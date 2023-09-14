package real.map;

import java.util.ArrayList;
import java.util.List;
import real.func.NRNM;
import real.func.NRSD;
import real.item.ItemOption;
import real.item.ItemTemplate;
import real.player.Player;
import real.task.Task;
import real.task.TaskData;
import server.Service;
import server.Util;
import server.io.Message;
import service.DAOS.PlayerDAO;

public class ItemMap {

    public Zone map;
    public short itemMapId;
    public ItemTemplate itemTemplate;
    public int quantity;

    public short x;
    public short y;
    public long playerId;
    public List<ItemOption> options;

    public long createTime;

    public ItemMap(Zone map, ItemTemplate itemTemp, int quantity, int x, int y, long playerId) {
        this.map = map;
        this.itemTemplate = itemTemp;
        this.quantity = quantity;
        this.x = (short) x;
        this.y = (short) y;
        this.playerId = playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
    }

    public synchronized void update() {
        if(NRSD.isNRSD(itemTemplate.id)){
            return;
        }
        if(NRNM.gI().findNRNM(itemTemplate.id) != null){
            return;
        }
        if ((System.currentTimeMillis() - createTime) >= 60000 * (itemTemplate.type == 22 ? 52 : 1)) {
            removeItemMap(this);
        }
        if ((System.currentTimeMillis() - createTime) >= 30000 && itemTemplate.type != 22) {
            playerId = -1;
        }
    }

    public static synchronized void removeItemMap(ItemMap itemMap) {
        Message msg = null;
        try {
            msg = new Message(-21);
            msg.writer().writeShort(itemMap.itemMapId);
            Service.gI().sendMessAllPlayerInMap(itemMap.map, msg);
            itemMap.map.items.remove(itemMap);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public static synchronized void removeItemMap(Player pl, int...itemid) {
        Message msg = null;
        try {
            msg = new Message(-21);
            msg.writer().writeShort(itemid.length > 0 ? itemid[0] : -1);
            pl.session.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public static void DUIGA(Player pl) {
        if (!MapManager.gI().isMapHome(pl)) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(-1);
            msg.writer().writeShort(74);
            msg.writer().writeShort(pl.gender == 0 ? 623 : (pl.gender == 1 ? 43 : 619));
            msg.writer().writeShort(pl.gender == 2 ? 325 : 320);
            msg.writer().writeInt(3);//
            pl.session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public static void DUABE(Player pl) {
        Task task = TaskData.getTask(pl.taskId);
        if (task.maps[pl.taskIndex][pl.gender] != pl.zone.map.id) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(-78);
            msg.writer().writeShort(78);
            msg.writer().writeShort(78);
            msg.writer().writeShort(pl.zone.map.id == 43 ? 264 : 288);
            msg.writer().writeInt(3);//
            pl.session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
}
