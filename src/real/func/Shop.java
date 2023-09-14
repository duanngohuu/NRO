package real.func;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import real.item.CaiTrangData;
import real.item.Item;
import real.item.ItemBua;
import real.item.ItemDAO;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemOptionShop;
import real.item.ItemShop;
import real.item.ItemShopDAO;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Inventory;
import real.player.Player;
import real.player.PlayerManger;
import real.task.Task;
import real.task.TaskData;
import server.DBService;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;
import service.DAOS.PlayerDAO;
import service.Setting;

public class Shop {

    public Map itemShops = null;
    private static Shop instance;
    public Vector<ItemShop> its;

    private Shop() {

    }

    public static Shop gI() {
        if (instance == null) {
            instance = new Shop();
        }
        return instance;
    }

    public void XoaDo(Player pl, int index) {
        if(index > pl.inventory.itemsBoxSecond.size() - 1){
            return;
        }
        pl.inventory.itemsBoxSecond.remove(index);
        openBoxSecond(pl);
    }

    public void NhanDo(Player pl, int index) {
        if (pl.getBagNull() <= 0)
        {
            Service.gI().sendThongBao(pl, "Hành Trang Không Đủ Ô Trống");
            return;
        }
        if(index > pl.inventory.itemsBoxSecond.size() - 1)
        {
            return;
        }
        Item item = pl.inventory.itemsBoxSecond.get(index);
        if(item.template.id == 190)
        {
            if(pl.inventory.gold + item.quantity > pl.inventory.LIMIT_GOLD)
            {
                pl.inventory.gold = pl.inventory.LIMIT_GOLD;
            }
            else
            {
                pl.inventory.gold += item.quantity;
            }
            Service.gI().sendMoney(pl);
        }
        else
        {
            pl.inventory.addItemBag(item);
            pl.inventory.sendItemBags();
            pl.inventory.sortItemBag();
        }
        pl.inventory.itemsBoxSecond.remove(index);
        openBoxSecond(pl);
        Service.gI().sendThongBao(pl, "Chúc mừng bạn nhận được " + item.template.name);
    }
    
    public void NhanAllDo(Player pl, int index) {
        List<Item> itemN = new ArrayList();
        Item item = pl.inventory.itemsBoxSecond.get(index);
        for(int i = 0 ; i < pl.inventory.itemsBoxSecond.size() ;i++){
            Item it = pl.inventory.itemsBoxSecond.get(i);
            if(it.template.id == item.template.id)
            {
                itemN.add(it);
            }
        }
        for(Item it : itemN)
        {
            if(it.template.id == item.template.id)
            {
                if(it.template.id == 190)
                {
                    if(pl.inventory.gold + it.quantity > pl.inventory.LIMIT_GOLD)
                    {
                        pl.inventory.gold = pl.inventory.LIMIT_GOLD;
                    }
                    else
                    {
                        pl.inventory.gold += it.quantity;
                    }
                    Service.gI().sendMoney(pl);
                }
                else
                {
                    if(pl.getBagNull() <= 0){
                        Service.gI().sendThongBao(pl, "Hành Trang Không Đủ Ô Trống");
                        continue;
                    }
                    pl.inventory.sortItemBag();
                    pl.inventory.addItemBag(it);
                    pl.inventory.sendItemBags();
                    Service.gI().sendThongBao(pl, "Bạn nhận được " + it.template.name);
                }
                pl.inventory.itemsBoxSecond.remove(it);
                openBoxSecond(pl);
            }
        }
    }

    public void openBoxSecond(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            for (int i = 0; i < 1; i++) {
                int items = pl.inventory.itemsBoxSecond.size();
                msg.writer().writeUTF(items + " Vật\nPhẩm");
                msg.writer().writeByte(items);
                for (int j = 0; j < items; j++) {
                    Item it = pl.inventory.itemsBoxSecond.get(j);
                    msg.writer().writeShort(it.template.id);
                    msg.writer().writeUTF("LUCKY DRAGON BALL");
                    msg.writer().writeByte(it.itemOptions.size());
                    for (ItemOption op : it.itemOptions) {
                        msg.writer().writeByte(op.optionTemplate.id);
                        msg.writer().writeShort(op.param);
                    }
                    msg.writer().writeByte(0);
                    int isCaiTrang = it.template.type == 0 || it.template.type == 1 || it.template.type == 5 || it.template.type == 11 ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1)
                    {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.template.id);
                        int head = it.template.type == 5 ? it.template.part : -1;
                        int body = it.template.type == 0 ? it.template.part : -1;
                        int leg = it.template.type == 1 ? it.template.part : -1;
                        int bag = it.template.type == 11 ? it.template.part : -1;
                        if(ct != null && (ct.getID()[0] != -1 || ct.getID()[1] != -1 || ct.getID()[2] != -1 || ct.getID()[3] != -1)){
                            head = ct.getID()[0];
                            body = ct.getID()[1];
                            leg = ct.getID()[2];
                            bag = ct.getID()[3];
                        }
                        msg.writer().writeShort(head == -1 ? (int)pl.getHead() : head);
                        msg.writer().writeShort(body == -1 ? (int)pl.getBody() : body);
                        msg.writer().writeShort(leg == -1 ? (int)pl.getLeg() : leg);
                        msg.writer().writeShort(bag == -1 ? (int)pl.get_bag() : bag);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Shop openBoxSecond\n------------");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private int getCountTab(List<ItemShop> list) {
        int count = 0;
        int curr = -1;
        for (ItemShop it : list) {
            if (it.tab != curr) {
                count++;
                curr = it.tab;
            }
        }
        return count;
    }
    
    public ItemShop getItemShopSuKien(Player pl, int id)
    {
        for(int i = 0; i < getListShopSuKien().size(); i++)
        {
            ItemShop its = getListShopSuKien().get(i);
            if(its.itemTemplate.id == id)
            {
                return its;
            }
        }
        for(int i = 0; i < getListShopQua(pl).size(); i++)
        {
            ItemShop its = getListShopQua(pl).get(i);
            if(its.itemTemplate.id == id)
            {
                return its;
            }
        }
        return null;
    }
    
    private List<ItemShop> getListShopSuKien() {
        List<ItemShop> list = new ArrayList<>();
        int[] items = new int[]{758, 752, 753, 1206, 1207};
        int[] points = new int[]{1000, 300, 500, 500, 500};
        for(int i = 0; i < items.length; i++)
        {
            Item item = ItemData.gI().get_item(items[i]);
            ItemShop it = new ItemShop();
            it.itemTemplate = item.template;
            it.itemOptions = new ArrayList<>();
            it.itemNew = true;
            if(i == 1)
            {
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 50, 15));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 14, 15));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 30, 0));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 93, 30));
            }
            else if(i == 2)
            {
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 50, 25));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 14, 25));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 30, 0));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 93, 30));
            }
            else
            {
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 174, 2023));
                it.itemOptions.add(new ItemOptionShop(0, item.template.id, 93, 30));
            }
            it.itemOptions.add(new ItemOptionShop(0, item.template.id, 164, points[i]));
            it.isCaiTrang = false;
            it.quantity = 1;
            it.tabName = "Chọn\nQuà";
            it.point = points[i];
            list.add(it);
        }
        return list;
    }
    
    public List<ItemShop> getListShopQua(Player pl) {
        List<ItemShop> list = new ArrayList<>();
        int index = TopInfo.IndexTOP(pl.name, TopInfo.topSuKien);
        if(GiftCode.checkGift(pl, GiftCode.QUA_1) && GiftCode.checkGift(pl, GiftCode.QUA_2) || index < 0 || pl.pointTET <= 0)
        {
            return list;
        }
        int[] items = new int[]{1199, 1184};
        int param = index > 3 && index <= 10 ? 90 : 60;
        int quantity = index == 1 ? 1000 : index == 2 ? 500 : index == 3 ? 300 : index > 3 && index <= 10 ? 100 : 50;
        for(int i = 0; i < items.length; i++)
        {
            if(i == 0 && GiftCode.checkGift(pl, GiftCode.QUA_1))
            {
                continue;
            }
            else if(i == 1 && GiftCode.checkGift(pl, GiftCode.QUA_2)){
                continue;
            }
            Item item = ItemData.gI().get_item(items[i]);
            ItemShop it = new ItemShop();
            it.npcId = pl.getNPCMenu();
            it.itemTemplate = item.template;
            it.itemOptions = new ArrayList<>();
            it.itemNew = true;
            if(i == 0)
            {
                it.itemOptions.add(new ItemOptionShop(pl.getNPCMenu(), item.template.id, 77, 15));
                it.itemOptions.add(new ItemOptionShop(pl.getNPCMenu(), item.template.id, 103, 15));
                it.itemOptions.add(new ItemOptionShop(pl.getNPCMenu(), item.template.id, 50, 15));
                it.itemOptions.add(new ItemOptionShop(pl.getNPCMenu(), item.template.id, 14, 7));
                if(index > 3)
                {
                    it.itemOptions.add(new ItemOptionShop(pl.getNPCMenu(), item.template.id, 93, param));
                }
            }
            else if(i == 1)
            {
                it.itemOptions.add(new ItemOptionShop(pl.getNPCMenu(), item.template.id, 30, 0));
            }
            it.isCaiTrang = false;
            it.quantity = i == 0 ? 1 : quantity;
            it.tabName = "Quà đua\ntop";
            list.add(it);
        }
        return list;
    }

    private List<ItemShop> getListShopTab(Player pl, int npc, int tab) {
        List<ItemShop> list = new ArrayList<>();
        for (ItemShop it : getItemsShop(npc)) {
            if (list.size() < 75 && it.npcId == npc && it.tab == tab && (it.itemTemplate.gender == pl.gender || it.itemTemplate.gender > 2 || it.itemTemplate.level >= 14) && it.sell) {
                if(it.itemTemplate.id == 453 && pl.typeShip == 3){
                        continue;
                }
                Item porata1 = pl.inventory.findItem(pl.inventory.itemsBag, 454);
                Item porata2 = pl.inventory.findItem(pl.inventory.itemsBox, 454);
                Item porata3 = pl.inventory.findItem(pl.inventory.itemsBag, 921);
                Item porata4 = pl.inventory.findItem(pl.inventory.itemsBox, 921);
                if(it.itemTemplate.id == 454 && (porata1 != null || porata2 != null || porata3 != null || porata4 != null)){
                    continue;
                }
                if(it.itemTemplate.id == 522 && pl.pet == null){
                    continue;
                }
                list.add(it);
            }
        }
        return list;
    }

    private List<ItemShop> getListShop(int npc, int gender) {
        List<ItemShop> list = new ArrayList<>();
        for (ItemShop it : getItemsShop(npc)) {
            if (it != null && it.npcId == npc && (it.itemTemplate.gender == gender || it.itemTemplate.gender > 2 || it.itemTemplate.level >= 14)) {
                list.add(it);
            }
        }
        return list;
    }

    private List<ItemShop> getItemsShop(int npc) {
        List<ItemShop> list = new ArrayList<>();
        list = (List<ItemShop>)itemShops.get(npc);
        return list;
    }

    public void openShop(Player pl) {
        Message msg = null;
        try {
            List<ItemShop> list = getListShop(21, pl.gender);
            if (list.size() < 1) {
                return;
            }
            msg = new Message(-44);
            msg.writer().writeByte(list.get(0).typeShop);
            int tabs = getCountTab(list);
            msg.writer().writeByte(tabs);
            for (int i = 0; i < tabs; i++) {
                list = getListShopTab(pl, 21, i);
                msg.writer().writeUTF(list.get(0).tabName);
                int items = list.size();
                msg.writer().writeByte(items);
                for (int j = 0; j < items; j++) {
                    ItemShop it = list.get(j);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem * pl.tabBua);
                    it.itemOptions.clear();
                    for (int g = 0 ; g < pl.inventory.itemsBua.size() ; g++) {
                        ItemOptionShop op = pl.inventory.itemsBua.get(g);
                        if (it.itemTemplate.id == op.itemTemplateId) {
                            if (op.param >= 1440) {
                                it.itemOptions.add(new ItemOptionShop(it.itemTemplate.id , 63 , op.param / 60 / 24));
                            }else if (op.param >= 120 && op.param < 1440) {
                                it.itemOptions.add(new ItemOptionShop(it.itemTemplate.id , 64 , op.param / 60));
                            }else if(op.param <= 60){
                                it.itemOptions.add(new ItemOptionShop(it.itemTemplate.id , 65 , op.param));
                            }
                        }
                    }
                    if(it.itemOptions.size() <= 0){
                        it.itemOptions.add(new ItemOptionShop(it.itemTemplate.id , 66 , 0));
                    }
                    msg.writer().writeByte(it.itemOptions.size());
                    for (ItemOptionShop op : it.itemOptions) {
                        msg.writer().writeByte(op.optionId);
                        msg.writer().writeShort(op.param);
                    }
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(0);
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void openShop(Player pl, int npcId) {
        Npc n = Npc.getByIdAndMap(npcId, pl.zone.map.id);
        if (n == null) {
            return;
        }
        Message msg = null;
        try {
            List<ItemShop> list = getListShop(npcId, pl.gender);
            if (list.size() < 1) {
                return;
            }

            msg = new Message(-44);
            msg.writer().writeByte(list.get(0).typeShop);

            int tabs = getCountTab(list);
            msg.writer().writeByte(tabs);

            for (int i = 0; i < tabs; i++) {
                list = getListShopTab(pl, npcId, i);
                msg.writer().writeUTF(list.get(0).tabName);
                int items = list.size();
                msg.writer().writeByte(items);
                for (int j = 0; j < items; j++) {
                    ItemShop it = list.get(j);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem);
                    int options = it.itemOptions.size();
                    msg.writer().writeByte(options);
                    if (options != 0) {
                        for (int k = 0; k < options; k++) {
                            msg.writer().writeByte(it.itemOptions.get(k).optionId);
                            msg.writer().writeShort(it.itemOptions.get(k).param);
                        }
                    }
                    msg.writer().writeByte(it.itemNew ? 1 : 0);
                    int isCaiTrang = it.itemTemplate.type == 0 || it.itemTemplate.type == 1 || it.itemTemplate.type == 5 || it.itemTemplate.type == 11 ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1)
                    {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.itemTemplate.id);
                        int head = it.itemTemplate.type == 5 ? it.itemTemplate.part : -1;
                        int body = it.itemTemplate.type == 0 ? it.itemTemplate.part : -1;
                        int leg = it.itemTemplate.type == 1 ? it.itemTemplate.part : -1;
                        int bag = it.itemTemplate.type == 11 ? it.itemTemplate.part : -1;
                        if(ct != null && (ct.getID()[0] != -1 || ct.getID()[1] != -1 || ct.getID()[2] != -1 || ct.getID()[3] != -1)){
                            head = ct.getID()[0];
                            body = ct.getID()[1];
                            leg = ct.getID()[2];
                            bag = ct.getID()[3];
                        }
                        msg.writer().writeShort(head == -1 ? (int)pl.getHead() : head);
                        msg.writer().writeShort(body == -1 ? (int)pl.getBody() : body);
                        msg.writer().writeShort(leg == -1 ? (int)pl.getLeg() : leg);
                        msg.writer().writeShort(bag == -1 ? (int)pl.get_bag() : bag);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Shop.openShop");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void openShop(Player pl, int npcId, List<ItemShop> list) {
        Npc n = Npc.getByIdAndMap(npcId, pl.zone.map.id);
        if (n == null) {
            return;
        }
        Message msg = null;
        try {
            if (list.size() < 1) {
                return;
            }

            msg = new Message(-44);
            msg.writer().writeByte(list.get(0).typeShop);

            int tabs = getCountTab(list);
            msg.writer().writeByte(tabs);

            for (int i = 0; i < tabs; i++) {
                msg.writer().writeUTF(list.get(0).tabName);
                int items = list.size();
                msg.writer().writeByte(items);
                for (int j = 0; j < items; j++) {
                    ItemShop it = list.get(j);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem);
                    int options = it.itemOptions.size();
                    msg.writer().writeByte(options);
                    if (options != 0) {
                        for (int k = 0; k < options; k++) {
                            msg.writer().writeByte(it.itemOptions.get(k).optionId);
                            msg.writer().writeShort(it.itemOptions.get(k).param);
                        }
                    }
                    msg.writer().writeByte(it.itemNew ? 1 : 0);
                    int isCaiTrang = it.isCaiTrang ? 1 : it.itemTemplate.type == 11 ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1) {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.itemTemplate.id);
                        int head = it.itemTemplate.type == 5 ? it.itemTemplate.part : -1;
                        int body = it.itemTemplate.type == 0 ? it.itemTemplate.part : -1;
                        int leg = it.itemTemplate.type == 1 ? it.itemTemplate.part : -1;
                        int bag = it.itemTemplate.type == 11 ? it.itemTemplate.part : -1;
                        if(ct != null && (ct.getID()[0] != -1 || ct.getID()[1] != -1 || ct.getID()[2] != -1 || ct.getID()[3] != -1)){
                            head = ct.getID()[0];
                            body = ct.getID()[1];
                            leg = ct.getID()[2];
                            bag = ct.getID()[3];
                        }
                        msg.writer().writeShort(head == -1 ? (int)pl.getHead() : head);
                        msg.writer().writeShort(body == -1 ? (int)pl.getBody() : body);
                        msg.writer().writeShort(leg == -1 ? (int)pl.getLeg() : leg);
                        msg.writer().writeShort(bag == -1 ? (int)pl.get_bag() : bag);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Shop.openShop");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void openSukien(Player pl, int npcId) {
        Npc n = Npc.getByIdAndMap(npcId, pl.zone.map.id);
        if (n == null) {
            return;
        }
        Message msg = null;
        try {
            List<ItemShop> list = getListShopSuKien();
            if (list.size() < 1) {
                return;
            }

            msg = new Message(-44);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            for (int i = 0; i < 1; i++) {
                list = getListShopSuKien();
                msg.writer().writeUTF(list.get(0).tabName);
                int items = list.size();
                msg.writer().writeByte(items);
                for (int j = 0; j < items; j++) {
                    ItemShop it = list.get(j);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem);
                    int options = it.itemOptions.size();
                    msg.writer().writeByte(options);
                    if (options != 0) {
                        for (int k = 0; k < options; k++) {
                            msg.writer().writeByte(it.itemOptions.get(k).optionId);
                            msg.writer().writeShort(it.itemOptions.get(k).param);
                        }
                    }
                    msg.writer().writeByte(it.itemNew ? 1 : 0);
                    int isCaiTrang = it.isCaiTrang ? 1 : it.itemTemplate.type == 11 ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1) {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.itemTemplate.id);
                        int head = it.itemTemplate.type == 5 ? it.itemTemplate.part : -1;
                        int body = it.itemTemplate.type == 0 ? it.itemTemplate.part : -1;
                        int leg = it.itemTemplate.type == 1 ? it.itemTemplate.part : -1;
                        int bag = it.itemTemplate.type == 11 ? it.itemTemplate.part : -1;
                        if(ct != null && (ct.getID()[0] != -1 || ct.getID()[1] != -1 || ct.getID()[2] != -1 || ct.getID()[3] != -1)){
                            head = ct.getID()[0];
                            body = ct.getID()[1];
                            leg = ct.getID()[2];
                            bag = ct.getID()[3];
                        }
                        msg.writer().writeShort(head == -1 ? (int)pl.getHead() : head);
                        msg.writer().writeShort(body == -1 ? (int)pl.getBody() : body);
                        msg.writer().writeShort(leg == -1 ? (int)pl.getLeg() : leg);
                        msg.writer().writeShort(bag == -1 ? (int)pl.get_bag() : bag);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Shop.openShop");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public boolean CheckDoAn(Player pl){
        return pl.inventory.itemsBag.stream().anyMatch(it -> it != null && it.quantity >=99 && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 ||it.template.id == 667));
    }
    
    public void buyItem(Player pl, int itemID, int type, int quantity) {
        try {
            ItemShop itemShop = ItemShopDAO.getItemShop(itemID, pl.getNPCMenu());
            if(itemShop == null)
            {
                itemShop = getItemShopSuKien(pl, itemID);
            }
            if (itemShop == null) {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            if (itemShop.itemTemplate.id == 517 && pl.inventory.itemsBag.size() >= 45) {
                Service.gI().sendThongBao(pl, "Đã đạt giới hạn");
                return;
            }
            if (itemShop.itemTemplate.id == 518 && pl.inventory.itemsBox.size() >= 45) {
                Service.gI().sendThongBao(pl, "Đã đạt giới hạn");
                return;
            }
            else if(itemShop.itemTemplate.level == 14 && !CheckDoAn(pl)){
                Service.gI().sendThongBao(pl, "Bạn không có đủ 99 đồ ăn!");
                return;
            }
            if(itemShop.itemTemplate.id == 379 && pl.taskId < 26){
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            boolean isBuy = false;
            if (itemShop.gold > 0)
            {
                if (pl.inventory.gold >= itemShop.gold) {
                    pl.inventory.gold -= itemShop.gold;
                    isBuy = true;
                }
                else
                {
                    Service.gI().sendThongBao(pl, "Bạn không đủ vàng để mua vật phẩm");
                    isBuy = false;
                }
            }
            else if (itemShop.gem > 0) {
                if (pl.inventory.getGemAndRuby() >= itemShop.gem * (itemShop.itemTemplate.name.contains("Bùa") ? pl.tabBua : 1)) {
                    pl.inventory.subGemAndRuby(itemShop.gem * (itemShop.itemTemplate.name.contains("Bùa") ? pl.tabBua : 1));
                    isBuy = true;
                } else {
                    Service.gI().sendThongBaoOK(pl, "Bạn không đủ ngọc để mua vật phẩm này!");
                    isBuy = false;
                }
            }
            else if(itemShop.point > 0){
                if(pl.session.get_point() < itemShop.point){
                    Service.gI().sendThongBao(pl, "Bạn không đủ điểm để đổi vật phẩm này.");
                    isBuy = false;
                }
                else if(pl.session.get_point() >= itemShop.point)
                {
                    pl.session.remove_point(itemShop.point);
                    isBuy = true;
                }
            }
            else if(pl.getNPCMenu() == 13 && (!GiftCode.checkGift(pl, GiftCode.QUA_1) || !GiftCode.checkGift(pl, GiftCode.QUA_2)))
            {
                if(TopInfo.IndexTOP(pl.name, TopInfo.topSuKien) > 0){
                    pl.inventory.giftCode.add(itemShop.itemTemplate.id == 1184 ? GiftCode.QUA_2 : GiftCode.QUA_1);
                    Shop.gI().openShop(pl, pl.getNPCMenu(), Shop.gI().getListShopQua(pl));
                    isBuy = true;
                }
            }
            boolean isShow = GiftCode.checkGift(pl, GiftCode.QUA_1) && GiftCode.checkGift(pl, GiftCode.QUA_2);
            if (pl.getBagNull() <= 0 && !itemShop.itemTemplate.name.contains("Bùa"))
            {
                Service.gI().sendThongBaoOK(pl, "Hành trang không đủ ô trống!");
                isBuy = false;
            }
            if (isBuy)
            {
                Service.gI().sendMoney(pl);
                if (pl.taskId == 12) {
                    Task task = TaskData.getTask(pl.taskId);
                    if (task.counts[pl.taskIndex] != -1) {
                        pl.taskCount += 1;
                        if (pl.taskCount < task.counts[pl.taskIndex]) {
                            Service.gI().send_task(pl, task);
                        } else {
                            Service.gI().send_task_next(pl);
                        }
                    }
                }
                if(itemShop.itemTemplate.level == 14)
                {
                    Item doAn = pl.inventory.itemsBag.stream().filter(it -> it != null && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 ||it.template.id == 667) && it.quantity >= 99).findFirst().orElse(null);
                    if(doAn != null){
                        pl.inventory.subQuantityItemsBag(doAn, 99);
                    }
                }
                if (itemShop.itemTemplate.name.contains("Bùa"))
                {
                    List<ItemOptionShop> items = new ArrayList<>();
                    Service.gI().sendMoney(pl);
                    switch (NpcFactory.typeBua) {
                        case 0:
                            items.add(new ItemOptionShop((int) itemShop.itemTemplate.id, 65, 60));
                            ItemBua.createBua(pl, items);
                            Shop.gI().openShop(pl);
                            break;
                        case 1:
                            items.add(new ItemOptionShop((int) itemShop.itemTemplate.id, 65, 480));
                            ItemBua.createBua(pl, items);
                            Shop.gI().openShop(pl);
                            break;
                        case 2:
                            items.add(new ItemOptionShop((int) itemShop.itemTemplate.id, 65, 43200));
                            ItemBua.createBua(pl, items);
                            Shop.gI().openShop(pl);
                            break;
                    }
                }
                else if (itemShop.itemTemplate.id == 453) {
                    pl.typeShip = 3;
                }
                else if (itemShop.itemTemplate.id == 517) {
                    pl.ExtendBag();
                }
                else if (itemShop.itemTemplate.id == 518) {
                    pl.ExtendBox();
                }
                else {
                    Item item = new Item();
                    item.template = ItemData.gI().getTemplate((short) itemID);
                    item.content = item.getContent();
                    int param = 0;
                    if(item.template.level == 14){
                        param = Util.nextInt(0, 15);
                    }
                    if (!itemShop.itemOptions.isEmpty())
                    {
                        for (ItemOptionShop ios : itemShop.itemOptions)
                        {
                            if(ios.optionId == 22)
                            {
                                ios.optionId = 6;
                                ios.param = ios.param * 1000;
                            }
                            else if(ios.optionId == 23)
                            {
                                ios.optionId = 7;
                                ios.param = ios.param * 1000;
                            }
                            if(item.template.level == 14 && OptionCanUpgrade(ios) && param > 0)
                            {
                                item.itemOptions.add(new ItemOption(ios.optionId, ios.param + (ios.param / 100 * param)));
                            }
                            else if(ios.optionId != 164)
                            {
                                item.itemOptions.add(new ItemOption(ios.optionId, ios.param));
                            }
                        }
                    }
                    else {
                        item.itemOptions.add(new ItemOption(73, (short) 0));
                    }
                    if(item.template.id == 193 || item.template.id == 361)
                    {
                       item.quantity = 10;
                    }
                    else
                    {
                        item.quantity = itemShop.quantity; 
                    }
                    Timestamp timenow = new Timestamp(System.currentTimeMillis());
                    item.buyTime = timenow.getTime();
                    pl.inventory.addItemBag(item);
                }
                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + itemShop.itemTemplate.name);
            }
            pl.inventory.sendItemBags();
            pl.inventory.sendItemBody();
            Service.gI().sendMoney(pl);
        }
        catch (Exception e) {
            Util.logException(Shop.class, e);
        }
    }
    
    public boolean OptionCanUpgrade(ItemOptionShop io) {
        return io.optionId == 23 || io.optionId == 22 || io.optionId == 47 || io.optionId == 6 || io.optionId == 27 || io.optionId == 14 || io.optionId == 0 || io.optionId == 7 || io.optionId == 28;
    }
    
    // ---------------------------- KI GUI O DUOI ---------------------------- \\
    public void openKiGui(Player pl) {
        if (pl.zone.map.id != 84)
        {
            return;
        }
        Message msg = null;
        try {
            GetItems(pl); // Lấy toàn bộ item Shop ký gửi
            pl.setPageMenu(0); // Set page SHOP
            msg = new Message(-44); // -44 Show shop
            msg.writer().writeByte(2); // type shop Ký gửi
            String[] names = {
                "Trang bị",
                "Phụ kiện",
                "Hỗ trợ",
                "Linh tinh",
                "Item"
            };
            msg.writer().writeByte(names.length); // tab count
            for (int i = 0; i < names.length; i++) {
                msg.writer().writeUTF(names[i]); // Name tab
                List<Item> its = countPage(pl, i);
                int pages = 1;
                int maxItem = 20;
                if(i < 4 && its.size() > maxItem * pl.getPageMenu()){
                    pages = its.size() / maxItem + 1;
                    int fromIndex = maxItem * pl.getPageMenu();
                    int maxIndex = (maxItem * pl.getPageMenu()) + maxItem;
                    int toIndex = maxIndex > its.size() ? its.size() : maxIndex;
                    its = its.subList(fromIndex, toIndex);
                }
                msg.writer().writeByte(pages);// count page (số trang có trong tab)
                msg.writer().writeByte(its.size());// count item (số lượng item có trong tab)
                for (int j = 0; j < its.size(); j++) {
                    Item it = its.get(j);
                    msg.writer().writeShort(it.template.id);
                    msg.writer().writeShort(it.itemID);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem);
                    msg.writer().writeByte(it.Sell); // 0: chưa bán - 1: đang bán - 2: đã bán
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeByte(it.player_id == pl.id ? 1 : 0); // it.playerID == pl.id ? 1 : 0
                    int options = it.itemOptions.size();
                    msg.writer().writeByte(options);
                    if (options != 0) {
                        for (int k = 0; k < options; k++) {
                            msg.writer().writeByte(it.itemOptions.get(k).optionTemplate.id);
                            msg.writer().writeShort(it.itemOptions.get(k).param);
                        }
                    }
                    msg.writer().writeByte(0);
                    int isCaiTrang = it.template.type == 0 || it.template.type == 1 || it.template.type == 5 || it.template.type == 11 ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1)
                    {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.template.id);
                        int head = it.template.type == 5 ? it.template.part : -1;
                        int body = it.template.type == 0 ? it.template.part : -1;
                        int leg = it.template.type == 1 ? it.template.part : -1;
                        int bag = it.template.type == 11 ? it.template.part : -1;
                        if(ct != null && (ct.getID()[0] != -1 || ct.getID()[1] != -1 || ct.getID()[2] != -1 || ct.getID()[3] != -1)){
                            head = ct.getID()[0];
                            body = ct.getID()[1];
                            leg = ct.getID()[2];
                            bag = ct.getID()[3];
                        }
                        msg.writer().writeShort(head == -1 ? (int)pl.getHead() : head);
                        msg.writer().writeShort(body == -1 ? (int)pl.getBody() : body);
                        msg.writer().writeShort(leg == -1 ? (int)pl.getLeg() : leg);
                        msg.writer().writeShort(bag == -1 ? (int)pl.get_bag() : bag);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Shop.openShopKIGUI");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public List<Item> countPage(Player player, int tab){
        List<Item> items = new ArrayList();
        if(player.inventory.itemsShop != null){
            if(tab == 4){
                for(Item item : player.inventory.itemsShip)
                {
                    if(item != null){
                        items.add(item);
                    }
                }
                for(Item item : player.inventory.itemsBag)
                {
                    if(item != null){
                        boolean isSell_1 = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 86) || item.template.type == 14 || item.template.type == 15 || item.template.type == 6 || item.template.id >= 14 && item.template.id <= 20; // ký vàng
                        boolean isSell_2 = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 87); // ký ngọc
                        if(isSell_1 || isSell_2){
                            items.add(item);
                        }
                    }
                }
                return items;
            }
            for(Item item : player.inventory.itemsShop)
            {
                if(item == null){
                    continue;
                }
                else if(item.Sell == 1 && (item.template.gender == player.gender || item.template.gender == 3))
                {
                    if(tab == 0 && item.template.type >= 0 && item.template.type <= 4){
                        items.add(item);
                    }
                    else if(tab == 1 && (item.template.type == 12 || item.template.type == 27 || item.template.type == 33)){
                        items.add(item);
                    }
                    else if(tab == 2 && (item.template.type == 14 || item.template.type == 15 || item.template.type == 29)){
                        items.add(item);
                    }
                    else if(tab == 3 && !(item.template.type >= 0 && item.template.type <= 4 || item.template.type == 12 || item.template.type == 14 || item.template.type == 15 || item.template.type == 29 || item.template.type == 27 || item.template.type == 33)){
                        items.add(item);
                    }
                }
            }
        }
        if(tab < 4){
            items = items.stream().sorted(Comparator.comparing(s -> s.buyTime,Comparator.reverseOrder())).toList();
        }
        return items;
    }
    
    public void Kigui_onMessage(Message _msg, Player player){
        try
        {
            Service.gI().hideInfoDlg(player);
            byte active = _msg.reader().readByte();
            short itemID = -1;
            byte moneyType = -1;
            int money = -1;
            int quantity = -1;
            Item item = null;
            switch (active) {
                case 0:
                    itemID = _msg.reader().readShort();
                    moneyType = _msg.reader().readByte();
                    money = _msg.reader().readInt();
                    quantity = _msg.reader().readInt();
                    quantity = quantity <= 0 ? 1 : quantity;
                    item = player.inventory.getItemID(player.inventory.itemsBag, itemID);
                    if(item != null){
                        boolean isSell_1 = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 86) || item.template.type == 14 || item.template.type == 15 || item.template.type == 6 || item.template.id >= 14 && item.template.id <= 20; // ký vàng
                        boolean isSell_2 = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 87); // ký ngọc
                        if(isSell_1 && moneyType == 0)
                        {
                            if(money < Setting.GOLD_MIN_SELL_KI_GUI || money > Setting.GOLD_MAX_SELL_KI_GUI){
                                Service.gI().sendThongBao(player, "Nhập sai số tiền (Enter the wrong amount)");
                                break;
                            }
                            if(quantity <= 0 || quantity > item.quantity){
                                Service.gI().sendThongBao(player, "Nhập sai số lượng (Enter wrong quantity)");
                                break;
                            }
                            if(player.inventory.getGemAndRuby() < 1){
                                Service.gI().sendThongBao(player, "Không đủ 1 ngọc để ký gửi (Not enough 1 gem to deposit)");
                                break;
                            }
                            Item itemSell = new Item(item, quantity, money, -1, 1);
                            ModelKiGui(active, item, itemSell, player);
                        }
                        else if(isSell_2 && moneyType == 1){
                            if(money < Setting.NGOC_MIN_SELL_KI_GUI || money > Setting.NGOC_MAX_SELL_KI_GUI){
                                Service.gI().sendThongBao(player, "Nhập sai số tiền (Enter the wrong amount)");
                                break;
                            }
                            if(quantity <= 0 || quantity > item.quantity){
                                Service.gI().sendThongBao(player, "Nhập sai số lượng (Enter wrong quantity)");
                                break;
                            }
                            if(player.inventory.getGemAndRuby() < 1){
                                Service.gI().sendThongBao(player, "Không đủ 1 ngọc để ký gửi (Not enough 1 gem to deposit)");
                                break;
                            }
                            Item itemSell = new Item(item, quantity, -1, money, 1);
                            ModelKiGui(active, item, itemSell, player);
                        }
                    }
                    else
                    {
                        Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm này (This item could not be found)");
                    }
                    break;
                case 1: // HUY KI GUI
                    itemID = _msg.reader().readShort();
                    item = player.inventory.getItemID(player.inventory.itemsShip, itemID);
                    if(item != null && item.Sell == 1)
                    {
                        ModelKiGui(active, item, null, player);
                    }
                    else
                    {
                        Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm này (This item could not be found)");
                    }
                    break;
                case 2: // NHAN TIEN
                    itemID = _msg.reader().readShort();
                    item = player.inventory.findItem(itemID);
                    if(item != null && item.Sell == 2)
                    {
                        if(item.gold > 0)
                        {
                            money = item.gold - (item.gold / 100 * 5);
                            if(player.inventory.gold + money <= player.inventory.LIMIT_GOLD){
                                player.inventory.gold += money;
                                ModelKiGui(active, item, null, player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + Util.powerToString(money) + " vàng");
                            }
                            else
                            {
                                player.setPageMenu(0);
                                reloadTab(player, 4);
                                Service.gI().sendThongBao(player, "Không thể nhận quá " + Util.powerToString(player.inventory.LIMIT_GOLD) + " vàng");
                            }
                        }
                        else if (item.gem > 0){
                            money = item.gem - (item.gem  / 100 * 5);
                            player.inventory.gem += money;
                            ModelKiGui(active, item, null, player);
                            Service.gI().sendThongBao(player, "Bạn nhận được " + Util.powerToString(money) + " ngọc xanh");
                        }
                    }
                    else
                    {
                        Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm này (This item could not be found)");
                    }
                    break;
                case 3: // MUA ITEM SHOP KI GUI
                    itemID = _msg.reader().readShort();
                    moneyType = _msg.reader().readByte();
                    money = _msg.reader().readInt();
                    item = player.inventory.getItemID(player.inventory.itemsShop, itemID);
                    if(item != null && item.Sell == 1 && player.id != item.player_id)
                    {
                        boolean isSell_1 = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 86) || item.template.type == 14 || item.template.type == 15 || item.template.type == 6 || item.template.id >= 14 && item.template.id <= 20; // ký vàng
                        boolean isSell_2 = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 87); // ký ngọc
                        if(isSell_1 && moneyType == 0)
                        {
                            if(money > player.inventory.gold)
                            {
                                Service.gI().sendThongBao(player, "Bạn không đủ vàng để mua vật phẩm này");
                                break;
                            }
                            player.inventory.gold -= money;
                            ModelKiGui(active, item, null, player);
                        }
                        else if(isSell_2 && moneyType == 1)
                        {
                            if(money > player.inventory.gem)
                            {
                                Service.gI().sendThongBao(player, "Bạn không đủ ngọc xanh để mua vật phẩm này");
                                break;
                            }
                            ModelKiGui(active, item, null, player);
                            player.inventory.gem -= money;
                        }
                    }
                    else
                    {
                        Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm này hoặc đã có sự thay đổi");
                    }
                    break;
                case 4: // NEXT PAGE TAB - UPDATE
                    moneyType = _msg.reader().readByte();
                    money = _msg.reader().readByte();
                    player.setPageMenu(money);
                    reloadTab(player, moneyType);
                    break;
                case 5: // UP ITEM LEN TOP 1
                    itemID = _msg.reader().readShort();
                    item = player.inventory.findItem(itemID);
                    if(item != null && item.Sell == 1)
                    {
                        if(player.inventory.getGemAndRuby() < 1){
                            Service.gI().sendThongBao(player, "Không đủ 1 ngọc");
                            break;
                        }
                        ModelKiGui(active, item, null, player);
                    }
                    else
                    {
                        Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm này");
                    }
                    break;
                default:
                    Service.gI().sendThongBao(player, "Chưa có chức năng này");
                    Util.debug("KI GUI: " + active);
                    break;
            }
        } catch (Exception e) {
            Util.debug("Shop.Kigui_onMessage");
            e.printStackTrace();
        } finally {
            if (_msg != null) {
                _msg.cleanup();
                _msg = null;
            }
        }
    }
    
    public void ModelKiGui(byte active, Item item, Item itemSell, Player player){
        try{
            switch (active) {
                case 0: // KI GUI ITEM
                    player.inventory.subQuantityItemsBag(item, itemSell.quantity);
                    player.combine.addParam(itemSell, 37, 1);
                    if(itemSell.itemOptions != null){
                        for(ItemOption op : itemSell.itemOptions)
                        {
                            if(op != null && op.optionTemplate.id == 73)
                            {
                                itemSell.itemOptions.remove(op);
                            }
                        }
                    }
                    if(player.inventory.addItemShip(itemSell)){
                        Service.gI().sendThongBao(player, "Chúc mừng bạn đã ký gửi thành công (Congratulations on your successful deposit)");
                        player.inventory.subGemAndRuby(1);
                    }
                    break;
                case 1: // HUY KI GUI
                    if(item != null){
                        item.Sell = 0;
                        if(player.inventory.addItemBag(item)){
                            player.inventory.itemsShip.remove(item);
                            Service.gI().sendThongBao(player, "Đã hủy thành công");
                        }
                    }
                    break;
                case 2: // NHAN TIEN
                    if(player.inventory.itemsShip.remove(item)){
                        reloadTab(player, 4);
                    }
                    break;
                case 3: // MUA ITEM SHOP KI GUI
                    Player playerSell = PlayerDAO.LoadPlayer(item.player_id);
                    boolean isPlayer = PlayerManger.gI().getPlayers().stream().allMatch(pl -> pl.id != playerSell.id);
                    for(Item it : playerSell.inventory.itemsShip)
                    {
                        if(it != null && it.Sell == 1 && it.contains(item)){
                            it.Sell = 2;
                            break;
                        }
                    }
                    Item it = new Item(item);
                    if(player.inventory.addItemBag(it))
                    {
                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                    }
                    if(isPlayer){
                        PlayerDAO.UpdateItem(playerSell);
                        break;
                    }
                    reload(playerSell);
                    break;
                case 4: // NEXT PAGE TAB...
                    break;
                case 5: // UP ITEM LEN TOP
                    if(item != null){
                        player.inventory.subGemAndRuby(1);
                        Timestamp timenow = new Timestamp(System.currentTimeMillis());
                        item.buyTime = timenow.getTime();
                    }
                    break;
                default:
                    break;
            }
            reload(player);
        }
        catch (Exception e)
        {
            Util.debug("Shop.ModelKiGui");
            e.printStackTrace();
        }
    }
    
    public void reload(Player player){
        try{
            GetItems(player);
            reloadTab(player, 0);
            reloadTab(player, 1);
            reloadTab(player, 2);
            reloadTab(player, 3);
            reloadTab(player, 4);
            player.inventory.sendItemBags();
            Service.gI().sendMoney(player);
        }
        catch (Exception e)
        {
            Util.debug("Shop.reload");
            e.printStackTrace();
        }
        
    }
    
    public void reloadTab(Player pl, int tab) {
        if (pl.zone.map.id != 84) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-100); // -100 reloadShopKi
            msg.writer().writeByte(tab); // page
            List<Item> its = countPage(pl, tab);
            int pages = 1;
            int maxItem = 20;
            if(tab < 4 && its.size() > maxItem * pl.getPageMenu()){
                pages = its.size() / maxItem + 1;
                int fromIndex = maxItem * pl.getPageMenu();
                int maxIndex = (maxItem * pl.getPageMenu()) + maxItem;
                int toIndex = maxIndex > its.size() ? its.size() : maxIndex;
                its = its.subList(fromIndex, toIndex);
            }
            msg.writer().writeByte(pages); // count Page
            msg.writer().writeByte(pl.getPageMenu()); // curr page
            msg.writer().writeByte(its.size()); // count Item
            if(its.size() > 0){
                for (int j = 0; j < its.size(); j++) {
                    Item it = its.get(j);
                    msg.writer().writeShort(it.template.id);
                    msg.writer().writeShort(it.itemID);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem);
                    msg.writer().writeByte(it.Sell); // 0: chưa bán - 1: đang bán - 2: đã bán
                    msg.writer().writeInt(it.quantity);
                    msg.writer().writeByte(it.player_id == pl.id ? 1 : 0); // it.playerID == pl.id ? 1 : 0
                    int options = it.itemOptions.size();
                    msg.writer().writeByte(options);
                    if (options != 0) {
                        for (int k = 0; k < options; k++) {
                            msg.writer().writeByte(it.itemOptions.get(k).optionTemplate.id);
                            msg.writer().writeShort(it.itemOptions.get(k).param);
                        }
                    }
                    int isCaiTrang = it.template.type == 0 || it.template.type == 1 || it.template.type == 5 || it.template.type == 11 ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1)
                    {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.template.id);
                        int head = it.template.type == 5 ? it.template.part : -1;
                        int body = it.template.type == 0 ? it.template.part : -1;
                        int leg = it.template.type == 1 ? it.template.part : -1;
                        int bag = it.template.type == 11 ? it.template.part : -1;
                        if(ct != null && (ct.getID()[0] != -1 || ct.getID()[1] != -1 || ct.getID()[2] != -1 || ct.getID()[3] != -1)){
                            head = ct.getID()[0];
                            body = ct.getID()[1];
                            leg = ct.getID()[2];
                            bag = ct.getID()[3];
                        }
                        msg.writer().writeShort(head == -1 ? (int)pl.getHead() : head);
                        msg.writer().writeShort(body == -1 ? (int)pl.getBody() : body);
                        msg.writer().writeShort(leg == -1 ? (int)pl.getLeg() : leg);
                        msg.writer().writeShort(bag == -1 ? (int)pl.get_bag() : bag);
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Shop.reloadTab");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void GetItems(Player player)
    {
        player.inventory.itemsShop.clear();
        List<Player> players = PlayerDAO.getPlayers();
        players.forEach((pl) -> {
            for(Item item : pl.inventory.itemsShip)
            {
                if(item != null && item.Sell == 1 && !player.inventory.itemsShop.stream().anyMatch(it -> it.equals(item)))
                {
                    item.player_id = pl.id;
                    player.inventory.itemsShop.add(item);
                }
            }
        });
    }
    // ---------------------------- HET KI GUI ---------------------------- \\
}
