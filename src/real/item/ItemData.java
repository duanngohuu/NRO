package real.item;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import real.func.Shop;
import server.Util;
import server.io.Message;
import server.io.Session;
import service.Setting;

public class ItemData {

    static ItemData instance;

    public static ItemData gI() {
        return instance == null ? instance = new ItemData() : instance;
    }

    public List<ItemOptionTemplate> itemOptionTemplates = new ArrayList<>();
    public List<ItemTemplate> itemTemplates = new ArrayList<>();

    public ItemOption[][] default_option_item;

    ItemOption[][][] list_skh = null;

    public List<ItemOption> get_skh(int g, int i) {

        if (list_skh == null) {
            list_skh = new ItemOption[3][4][3];

            list_skh[0][0][0] = new ItemOption(127, 0);
            list_skh[0][0][1] = new ItemOption(139, 0);
            list_skh[0][0][2] = new ItemOption(30, 0);
            list_skh[0][1][0] = new ItemOption(128, 0);
            list_skh[0][1][1] = new ItemOption(140, 0);
            list_skh[0][1][2] = new ItemOption(30, 0);
            list_skh[0][2][0] = new ItemOption(129, 0);
            list_skh[0][2][1] = new ItemOption(141, 0);
            list_skh[0][2][2] = new ItemOption(30, 0);

            list_skh[1][0][0] = new ItemOption(130, 0);
            list_skh[1][0][1] = new ItemOption(142, 0);
            list_skh[1][0][2] = new ItemOption(30, 0);
            list_skh[1][1][0] = new ItemOption(131, 0);
            list_skh[1][1][1] = new ItemOption(143, 0);
            list_skh[1][1][2] = new ItemOption(30, 0);
            list_skh[1][2][0] = new ItemOption(132, 0);
            list_skh[1][2][1] = new ItemOption(144, 0);
            list_skh[1][2][2] = new ItemOption(30, 0);

            list_skh[2][0][0] = new ItemOption(133, 0);
            list_skh[2][0][1] = new ItemOption(136, 0);
            list_skh[2][0][2] = new ItemOption(30, 0);
            list_skh[2][1][0] = new ItemOption(134, 0);
            list_skh[2][1][1] = new ItemOption(137, 0);
            list_skh[2][1][2] = new ItemOption(30, 0);
            list_skh[2][2][0] = new ItemOption(135, 0);
            list_skh[2][2][1] = new ItemOption(138, 0);
            list_skh[2][2][2] = new ItemOption(30, 0);
        }
        if (g == 3) {
            int gg = Util.nextInt(2);
            return new ArrayList<ItemOption>() {
                {
                    add(list_skh[gg][i][0]);
                    add(list_skh[gg][i][1]);
                    add(list_skh[gg][i][2]);
                }
            };
        }
        return new ArrayList<ItemOption>() {
            {
                add(list_skh[g][i][0]);
                add(list_skh[g][i][1]);
                add(list_skh[g][i][2]);
            }
        };
    }

    public List<ItemOption> get_op_do_than(int type) {
        List<ItemOption> l = new ArrayList<>();
        switch (type) {
            case 0: // ao
                l.add(new ItemOption(47, Util.nextInt(800, 1000)));
                l.add(new ItemOption(21, Util.nextInt(15, 17)));
                break;
            case 1: // w
                l.add(new ItemOption(22, Util.nextInt(40, 55)));
                l.add(new ItemOption(27, Util.nextInt(8000, 12000)));
                l.add(new ItemOption(21, Util.nextInt(15, 17)));
                break;
            case 2:// g
                l.add(new ItemOption(0, Util.nextInt(4000, 5500)));
                l.add(new ItemOption(21, Util.nextInt(15, 17)));
                break;
            case 3: // j
                l.add(new ItemOption(23, Util.nextInt(40, 70)));
                l.add(new ItemOption(21, Util.nextInt(15, 17)));
                break;
            case 4: // rd
                l.add(new ItemOption(14, Util.nextInt(14, 18)));
                l.add(new ItemOption(21, Util.nextInt(15, 17)));
                break;
        }
        return l;
    }
    
    public List<ItemOption> get_max_op_do_than(int type) {
        List<ItemOption> l = new ArrayList<>();
        switch (type) {
            case 0: // ao
                l.add(new ItemOption(47, 900));
                l.add(new ItemOption(21, 17));
                break;
            case 1: // w
                l.add(new ItemOption(22, 70));
                l.add(new ItemOption(27, 12000));
                l.add(new ItemOption(21, 17));
                break;
            case 2:// g
                l.add(new ItemOption(0, 5000));
                l.add(new ItemOption(21, 17));
                break;
            case 3: // j
                l.add(new ItemOption(23, 70));
                l.add(new ItemOption(21, 17));
                break;
            case 4: // rd
                l.add(new ItemOption(14, 15));
                l.add(new ItemOption(21, 20));
                break;
        }
        return l;
    }
    
    public List<ItemOption> get_op_do_huy_diet(int type) {
        List<ItemOption> l = new ArrayList<>();
        switch (type) {
            case 0: // ao
                l.add(new ItemOption(47, 2070));
                l.add(new ItemOption(21, 42));
                l.add(new ItemOption(30, 0));
                break;
            case 1: // w
                l.add(new ItemOption(22, 120));
                l.add(new ItemOption(27, 18400));
                l.add(new ItemOption(21, 46));
                l.add(new ItemOption(30, 0));
                break;
            case 2:// g
                l.add(new ItemOption(0, 10350));
                l.add(new ItemOption(21, 50));
                l.add(new ItemOption(30, 0));
                break;
            case 3: // j
                l.add(new ItemOption(23, 115));
                l.add(new ItemOption(28, 13800));
                l.add(new ItemOption(21, 44));
                l.add(new ItemOption(30, 0));
                break;
            case 4: // rd
                l.add(new ItemOption(14, 31));
                l.add(new ItemOption(21, 48));
                l.add(new ItemOption(30, 0));
                break;
        }
        return l;
    }
    
    public List<ItemOption> get_op_do_thien_su(int type) {
        List<ItemOption> l = new ArrayList<>();
        switch (type) {
            case 0: // ao
                l.add(new ItemOption(47, 2484));
                l.add(new ItemOption(21, 60));
                l.add(new ItemOption(30, 0));
                break;
            case 1: // w
                l.add(new ItemOption(22, 144));
                l.add(new ItemOption(27, 22080));
                l.add(new ItemOption(21, 60));
                l.add(new ItemOption(30, 0));
                break;
            case 2:// g
                l.add(new ItemOption(0, 12420));
                l.add(new ItemOption(21, 60));
                l.add(new ItemOption(30, 0));
                break;
            case 3: // j
                l.add(new ItemOption(23, 138));
                l.add(new ItemOption(28, 16560));
                l.add(new ItemOption(21, 60));
                l.add(new ItemOption(30, 0));
                break;
            case 4: // rd
                l.add(new ItemOption(14, 23));
                l.add(new ItemOption(21, 60));
                l.add(new ItemOption(30, 0));
                break;
        }
        return l;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        for (int i = 0; i < itemOptionTemplates.size(); i++) {
            ItemOptionTemplate item = itemOptionTemplates.get(i);
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public void add(ItemTemplate it) {
        itemTemplates.add(it.id, it);
    }

    public int getItemIdByIcon(short IconID) {
        for (int i = 0; i < itemTemplates.size(); i++) {
            if (itemTemplates.get(i).iconID == IconID) {
                return itemTemplates.get(i).id;
            }
        }
        return -1;
    }

    public Item get_item(int id) {
        Item item = new Item();
        item.template = getTemplate((short) id);
        item.content = item.getContent();
        item.quantity = 1;
        if (item.template.level == 14) {
            item.itemOptions.addAll(get_op_do_huy_diet(item.template.type));
        } else if (item.template.level == 13) {
            item.itemOptions.addAll(get_max_op_do_than(item.template.type));
        }else if (item.template.level == 15) {
            item.itemOptions.addAll(get_op_do_thien_su(item.template.type));
        }else{
            item.itemOptions.addAll(get_option_default(id));
        }
        if(item.itemOptions.isEmpty()){
            item.itemOptions.add(new ItemOption(30,0));
        }
        Timestamp st = new Timestamp(System.currentTimeMillis());
        item.buyTime = st.getTime();
        return item;
    }

    public ItemTemplate getTemplate(short id) {
        try {
            return itemTemplates.get(id);
        } catch (Exception e) {
            return null;
        }
    }
    
    public int get_param_default(int idItem , int optionId){
        for(ItemOption op : this.get_item(idItem).itemOptions){
            if(op != null && op.optionTemplate.id == optionId){
                return op.param;
            }
        }
        return 0;
    }
    
    public List<ItemOption> get_option_default(int id) {
        List<ItemOption> ops = new ArrayList<>();
        ops.addAll(Arrays.asList(default_option_item[id]));
        return ops;
    }

    public short getPart(short itemTemplateID) {
        return getTemplate(itemTemplateID).part;
    }

    public short getIcon(short itemTemplateID) {
        return getTemplate(itemTemplateID).iconID;
    }

    public void loadDataItems() {
        itemTemplates = ItemTemplateDAO.getAll();
        Util.warning("Finish load item template! [" + itemTemplates.size() + "]\n");
        itemOptionTemplates = ItemOptionTemplateDAO.getAll();
        Util.warning("Finish load option template! [" + itemOptionTemplates.size() + "]\n");
        Shop.gI().itemShops = ItemShopDAO.loadItemShop();
        Util.warning("Finish load shop! [" + Shop.gI().itemShops.size() + "]\n");
        ItemDAO.load_default_option();
        Util.warning("Finish load default option! [" + default_option_item.length + "]\n");
    }

    public void updateItem(Session session) {
        int count = 800;
        updateItemOptionItemplate(session);
        updateItemTemplate(session, count);
        updateItemTemplate(session, count, itemTemplates.size());
    }
    
    public void reloadItem(Session session) {
//        int tab = itemTemplates.size() / 500;
//        if(tab <= 1){
//            updateItemTemplate(session, 500);
//            updateItemTemplate(session, 500, itemTemplates.size());
//        }
//        else if(tab <= 2){
//            updateItemTemplate(session, 500);
//            updateItemTemplate(session, 500, itemTemplates.size());
//        }
//        else if(tab <= 3){
//            updateItemTemplate(session, 500);
//            updateItemTemplate(session, 500, 1000);
//            updateItemTemplate(session, 1000, itemTemplates.size());
//        }
//        else if(tab <= 4){
//            updateItemTemplate(session, 500);
//            updateItemTemplate(session, 500, 1000);
//            updateItemTemplate(session, 1000, 1500);
//            updateItemTemplate(session, 1500, itemTemplates.size());
//        }
//        else if(tab <= 5){
//            updateItemTemplate(session, 500);
//            updateItemTemplate(session, 500, 1000);
//            updateItemTemplate(session, 1000, 1500);
//            updateItemTemplate(session, 1500, 2000);
//            updateItemTemplate(session, 2000, itemTemplates.size());
//        }
    }

    private void updateItemOptionItemplate(Session session) {
        Message msg = null;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);
            msg.writer().writeByte(Setting.vsItem); //vcitem
            msg.writer().writeByte(0); //update option
            msg.writer().writeByte(itemOptionTemplates.size());
            for (ItemOptionTemplate io : itemOptionTemplates) {
                msg.writer().writeUTF(io.name);
                msg.writer().writeByte(io.type);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        }
        catch (Exception e)
        {
            Util.logException(ItemData.class, e);
        }
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void updateItemTemplate(Session session , int count) {
        Message msg = null;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);
            msg.writer().writeByte(-1); //vcitem
            msg.writer().writeByte(1); //reload itemtemplate
            msg.writer().writeShort(count);
            for (int i = 0; i < count; i++) {
                msg.writer().writeByte(itemTemplates.get(i).type);
                msg.writer().writeByte(itemTemplates.get(i).gender);
                msg.writer().writeUTF(itemTemplates.get(i).name);
                msg.writer().writeUTF(itemTemplates.get(i).description);
                msg.writer().writeByte(itemTemplates.get(i).level);
                msg.writer().writeInt(itemTemplates.get(i).strRequire);
                msg.writer().writeShort(itemTemplates.get(i).iconID);
                msg.writer().writeShort(itemTemplates.get(i).part);
                msg.writer().writeBoolean(itemTemplates.get(i).isUpToUp);
            }
            session.doSendMessage(msg);
        } catch (Exception e) {
            Util.logException(ItemData.class, e);
        }
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    private void updateItemTemplate(Session session, int start, int end) {
        Message msg = null;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);
            msg.writer().writeByte(Setting.vsItem); //vcitem
            msg.writer().writeByte(2); //add itemtemplate
            msg.writer().writeShort(start);
            msg.writer().writeShort(end);
            for (int i = start; i < end; i++) {
                msg.writer().writeByte(itemTemplates.get(i).type);
                msg.writer().writeByte(itemTemplates.get(i).gender);
                msg.writer().writeUTF(itemTemplates.get(i).name);
                msg.writer().writeUTF(itemTemplates.get(i).description);
                msg.writer().writeByte(itemTemplates.get(i).level);
                msg.writer().writeInt(itemTemplates.get(i).strRequire);
                msg.writer().writeShort(itemTemplates.get(i).iconID);
                msg.writer().writeShort(itemTemplates.get(i).part);
                msg.writer().writeBoolean(itemTemplates.get(i).isUpToUp);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        }
        catch (Exception e)
        {
            Util.logException(ItemData.class, e);
        }
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
                msg = null;
            }
        }
    }
}
