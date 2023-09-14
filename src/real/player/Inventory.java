package real.player;

import java.util.ArrayList;
import java.util.List;
import real.item.Item;
import real.item.ItemOption;
import real.item.ItemOptionShop;
import real.map.ItemMap;
import real.pet.Pet;
import server.Service;
import server.Util;
import server.io.Message;

public class Inventory {

    public int LIMIT_GOLD = 2000000000;

    private final Player player;

    public List<Item> itemsBody;
    public List<Item> itemsBag;
    public List<Item> itemsBox;
    public List<Item> itemsShip;
    public List<Item> itemsShop;
    public List<Item> itemsTrade;
    public List<Item> itemsGift;
    public List<Item> itemsBoxSecond;
    public List<String> giftCode;
    public List<ItemOptionShop> itemsBua;
    public int goldTrade;
    public long gold;
    public int gem;
    public int ruby;

    public Inventory(Player player) {
        this.player = player;
        itemsGift = new ArrayList<>();
        giftCode = new ArrayList<>();
        itemsBua = new ArrayList<>();
        itemsBody = new ArrayList<>();
        itemsBag = new ArrayList<>();
        itemsBox = new ArrayList<>();
        itemsShip = new ArrayList<>();
        itemsShop = new ArrayList<>();
        itemsTrade = new ArrayList<>();
        itemsBoxSecond = new ArrayList<>();
    }

    public boolean checkSKH(int idoption) {
        return itemsBody.stream().limit(5).allMatch(it -> (it != null && it.itemOptions.stream().anyMatch(op -> (op.optionTemplate.id == idoption))));
    }

    public Item eatMagicTree(Item item) {
        int index = 0;
        for (Item it : itemsBag) {
            if (it != null && it.template.type == 6 && it == item) {
                it.quantity -= 1;
                if (it.quantity <= 0) {
                    removeItemBag(index);
                    this.sortItemBag();
                }
                this.sendItemBags();
                return it;
            }
            index++;
        }
        return null;
    }
    
    public boolean checkItem(Item item) {
        return itemsBag.stream().anyMatch(it -> (it != null && it.equals(item)));
    }
    
    public boolean checkOption(List<Item> lst, int idoption) {
        return lst.stream().anyMatch(it -> (it != null && it.itemOptions.stream().anyMatch(op -> (op.optionTemplate.id == idoption))));
    }
    
    public boolean checkOption() {
        return !checkSKH(136) && !checkSKH(137) && !checkSKH(138) && !checkSKH(139) && !checkSKH(140) && !checkSKH(141) && !checkSKH(142) && !checkSKH(143) && !checkSKH(144) && !checkSKH(188);
    }
    
    public boolean OptionCt(int optionid) {
        if (this.itemsBody.size() < 6){
            return false;
        }
        Item it = this.itemsBody.get(5);
        if (it != null) {
            if (it.itemOptions.stream().anyMatch(iop -> iop.optionTemplate.id == optionid)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean OptionPhuKien(int optionid) {
        if (this.itemsBody.size() < 9){
            return false;
        }
        Item it = this.itemsBody.get(8);
        if (it != null) {
            if (it.itemOptions.stream().anyMatch(iop -> iop.optionTemplate.id == optionid)) {
                return true;
            }
        }
        return false;
    }

    public int getGemAndRuby() {
        return this.gem + this.ruby;
    }

    public void subGemAndRuby(int num) {
        this.ruby -= num;
        if (this.ruby < 0) {
            this.gem += this.ruby;
            this.ruby = 0;
        }
    }

    public List<Item> copyItemsBag() {
        return copyList(itemsBag);
    }

    private List<Item> copyList(List<Item> items) {
        List<Item> list = new ArrayList<>();
        for (Item item : items) {
            if (item != null) {
                list.add(new Item(item));
            } else {
                list.add(null);
            }
        }
        return list;
    }

    public void removeNgocRong() {
        for (int i = 0; i < itemsBag.size(); i++) {
            if (itemsBag.get(i) != null && itemsBag.get(i).template.id >= 14 && itemsBag.get(i).template.id <= 20) {
                if (itemsBag.get(i).quantity == 1) {
                    itemsBag.set(i, null);
                } else {
                    itemsBag.get(i).quantity -= 1;
                }
            }
        }
        sortItemBag();
        sendItemBags();
    }

    public boolean existItemBag(int id) {
        return itemsBag.stream().anyMatch((it) -> (it != null && it.template.id == id));
    }

    public void setItemBag(Item item) {
        for (int i = 0; i < itemsBag.size(); i++) {
            if (itemsBag.get(i) == item) {
                itemsBag.set(i, item);
                break;
            }
        }
    }

    public boolean addItemBag(Item item) {
        return addItemList(itemsBag, item);
    }
    
    public boolean addItemBox(Item item) {
        return addItemList(itemsBox, item);
    }
    
    public boolean addItemShip(Item item) {
        return itemsShip.add(item);
    }

    public boolean addItemList(List<Item> items, Item item) {
        try {
            if (isItemIncremental(item)) {
                for (Item it : items) {
                    if (it != null && it.template.id == item.template.id) {
                        it.itemOptions.clear();
                        it.itemOptions.addAll(item.itemOptions);
                        it.quantity += item.quantity;
                        return true;
                    }
                }
            }
            int optionId = isItemIncrementalOption(item);
            if (optionId != -1) {
                int param = 0;
                for (ItemOption io : item.itemOptions) {
                    if (io != null && io.optionTemplate.id == optionId) {
                        param = io.param;
                    }
                }

                for (Item it : items) {
                    if (it != null && it.template.id == item.template.id) {
                        for (ItemOption io : it.itemOptions) {
                            if (io != null && io.optionTemplate.id == optionId) {
                                io.param += param;
                            }
                        }
                        return true;
                    }
                }
            }
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == null && item != null && item.itemOptions != null) {
                    items.set(i, item);
                    return true;
                }
            }
        } catch (Exception e) {
            Util.log("Inventory addItemList\n------------");
        }
        return false;
    }

    private boolean isItemIncremental(Item item) { //item cộng dồn số lượng
        if (item.template.id == 1213 || item.template.id == 1199 || item.template.id == 1055 ||item.template.id == 590 || item.template.id == 521 || item.template.id == 933 || item.template.id == 942 || item.template.id == 943 || item.template.id == 944) {
            return false;
        }
        if (item.template.type == 14  || item.template.type == 22 || item.template.type == 12 || item.template.type == 30 || item.template.type == 31 || item.template.type == 33 || item.template.type == 6 || item.template.type == 27 || item.template.type == 29) {
            return true;
        }
        int temp = item.template.id;
        if (temp == 372 || temp == 373 || temp == 374 || temp == 375 || temp == 376 || temp == 377 || temp == 378 ||temp == 193 || temp == 225 || temp == 402 || temp == 403 || temp == 404 || temp == 759 || temp == 361 || temp == 73) {
            return true;
        }
        return false;
    }

    private int isItemIncrementalOption(Item item) { //trả về optionid
        int temp = item.template.id;
        switch (temp) {
            case 521:
                return 1;
            case 933:
            case 590:
                return 31;
            default:
                return -1;
        }
    }

    public void throwItem(int where, int index) {
        Item itemThrow = null;
        if (where == 0) {
            itemThrow = itemsBody.get(index);
            removeItemBody(index);
            sendItemBody();
        } else if (where == 1) {
            itemThrow = itemsBag.get(index);
            removeItemBag(index);
            sendItemBags();
        }
        if (itemThrow == null) {
            return;
        }
        if (itemThrow.template.type >= 0 && itemThrow.template.type < 5 || itemThrow.template.type == 30 || itemThrow.template.type == 14) {
//            ItemMap itemMap = new ItemMap(player.zone, itemThrow.template, itemThrow.quantity, player.x, player.y, player.id);
//            itemMap.options = itemThrow.itemOptions;
//            Service.gI().roiItem(player, itemMap);
            Service.gI().Send_Caitrang(player);
        }
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public void arrangeItems(List<Item> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) == null) {
                int indexSwap = -1;
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(j) != null) {
                        indexSwap = j;
                        break;
                    }
                }
                if (indexSwap != -1) {
                    list.set(i, list.get(indexSwap));
                    list.set(indexSwap, null);
                } else {
                    break;
                }
            }
        }
    }

    public Item putItemBag(Item item) {
        for (int i = 0; i < itemsBag.size(); i++) {
            if (itemsBag.get(i) == null) {
                itemsBag.set(i, item);
                return null;
            }
        }
        return item;
    }

    public Item putItemBox(Item item) {
        for (int i = 0; i < itemsBox.size(); i++) {
            if (itemsBox.get(i) == null) {
                itemsBox.set(i, item);
                return null;
            }
        }
        return item;
    }

    public boolean PowerCanUse(Item item) {
        for (ItemOption op : item.itemOptions) {
            if (op != null && op.optionTemplate.id == 21)
            {
                if (op.param > (int) (player.point.getPower() / 1000000000)) {
                    return false;
                }
            }
            else if (op != null && op.optionTemplate.id != 21) {
                if (item.template.strRequire > player.point.getPower()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Item putItemBody(Item item) {
        Item sItem = item;
        if (item != null) {
            if (item.template.type >= 0 && item.template.type <= 5 || item.template.type == 32 || item.template.type == 27 || item.template.type == 11 || item.template.type == 23 || item.template.type == 24) {                
                if (item.template.gender == player.gender || item.template.gender == 3) {
                    if (PowerCanUse(item)) {
                        sItem = itemsBody.get(item.template.type == 32 ? 6 : item.template.type == 11 ? 8 : item.template.type == 27 ? 7 : item.template.type == 23 ? 9 : item.template.type == 24 ? 9 : item.template.type);
                        itemsBody.set(item.template.type == 32 ? 6 : item.template.type == 11 ? 8 : item.template.type == 27 ? 7 : item.template.type == 23 ? 9 : item.template.type == 24 ? 9 : item.template.type, item);
                    } else {
                        Service.gI().sendThongBao(player.isPet ? ((Pet) player).master : player, "Sức mạnh không đủ yêu cầu");
                    }
                } else {
                    Service.gI().sendThongBao(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp");
                }
            } else {
                Service.gI().sendThongBao(player.isPet ? ((Pet) player).master : player, "Trang bị không phù hợp");
            }
        } else {
            Service.gI().sendThongBao(player.isPet ? ((Pet) player).master : player, "Không tìm thấy trang bị");
        }
        return sItem;
    }

    public void itemBagToBody(int index) {
        Item item = itemsBag.get(index);
        if (item != null) {
            itemsBag.set(index, putItemBody(item));
            Service.gI().point(player);
            sendItemBags();
            sendItemBody();
            Service.gI().Send_Caitrang(player);
            Service.gI().sendBag(player);
        }
    }

    public void itemBodyToBag(int index) {
        if (player.getBagNull() <= 0) {
            Service.gI().sendThongBao(player, "Hành Trang Không Đủ Ô Trống");
            return;
        }
        Item item = itemsBody.get(index);
        if (item != null) {
            itemsBody.set(index, putItemBag(item));
            Service.gI().point(player);
            sendItemBags();
            sendItemBody();
            Service.gI().Send_Caitrang(player);
            Service.gI().sendBag(player);
        }
    }

    public void itemBagToPetBody(int index) {
        if (player.pet.point.power < 1500000) {
            Service.gI().sendThongBao(player, "Đệ Tử Phải Trên 1tr5 Sức Mạnh Mới Có Thể Mặc Đồ");
            return;
        }
        Item item = itemsBag.get(index);
        if (item != null) {
            Item itemSwap = player.pet.inventory.putItemBody(item);
            itemsBag.set(index, itemSwap);
            Service.gI().point(player);
            sendItemBags();
            sendItemBody();
            Service.gI().Send_Caitrang(player.pet);
            Service.gI().Send_Caitrang(player);
            if (itemSwap == null || !itemSwap.equals(item)) {
                Service.gI().showInfoPet(player);
            }
        }
    }

    public void itemPetBodyToBag(int index) {
        if (player.getBagNull() <= 0) {
            Service.gI().sendThongBao(player, "Hành Trang Không Đủ Ô Trống");
            return;
        }
        Item item = player.pet.inventory.itemsBody.get(index);
        if (item != null) {
            player.pet.inventory.itemsBody.set(index, putItemBag(item));
            sendItemBags();
            sendItemBody();
            Service.gI().Send_Caitrang(player.pet);
            Service.gI().Send_Caitrang(player);
            Service.gI().showInfoPet(player);
        }
    }

    public void itemBagToBox(int index) {
        if (this.player.getBoxNull() <= 0) {
            Service.gI().sendThongBao(player, "Rương Đồ Không Đủ Ô Trống");
            return;
        }
        Item item = itemsBag.get(index);
        if (item != null) {
            this.removeItemBag(index);
            this.addItemBox(item);
            Service.gI().point(player);
            sendItemBags();
            sendItemBox();
            Service.gI().Send_Caitrang(player);
        }
    }
    
    public byte getIndexBagid(int id) {
        for (byte i = 0; i < itemsBag.size(); ++i) {
            Item item = itemsBag.get(i);
            if (item != null && item.template.id == id) {
                return i;
            }
        }
        return -1;
    }
    
    public Item getItemGTL(int opID) {
        for (byte i = 0; i < itemsBag.size(); ++i) {
            Item item = itemsBag.get(i);
            if (item != null && !item.itemOptions.isEmpty() && item.itemOptions.get(0).optionTemplate.id == opID) {
                return item;
            }
        }
        return null;
    }

    public void itemBoxToBag(int index) {
        if (player.getBagNull() <= 0) {
            Service.gI().sendThongBao(player, "Hành Trang Không Đủ Ô Trống");
            return;
        }
        Item item = itemsBox.get(index);
        if (item != null) {
            this.removeItemBox(index);
            this.addItemBag(item);
            Service.gI().point(player);
            sendItemBags();
            sendItemBox();
            Service.gI().Send_Caitrang(player);
        }
    }

    public void itemBodyToBox() {

    }

    //--------------------------------------------------------------------------
    public void subQuantityItemsBag(Item item, int quantity) {
        subQuantityItem(itemsBag, item, quantity);
    }
    
    public void subSoLuongItemsBag(int itemID, int param) {
        Item itemRemove = null;
        int index = -1;
        for (Item it : itemsBag) {
            if (it != null && it.template.id == itemID) {
                for(ItemOption op : it.itemOptions){
                    if(op != null && op.optionTemplate.id == 31){
                        op.param -= param;
                        if (op.param <= 0) {
                            itemRemove = it;
                        }
                        break;
                    }
                }
                break;
            }
        }
        if(itemRemove != null){
            removeItem(itemsBag, itemRemove);
        }
    }

    public void subQuantityItemsBox(Item item, int quantity) {
        subQuantityItem(itemsBox, item, quantity);
    }

    public void subQuantityItem(List<Item> items, Item item, int quantity) {
        for (Item it : items) {
            if (it != null && it == item) {
                it.quantity -= quantity;
                break;
            }
        }
        removeItemQuantityItem(itemsBag);
    }
    
    public void removeItemQuantityItem(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (it == null || it.quantity <= 0) {
                items.set(i, null);
            }
        }
    }

    public void sortItemBag() {
        sortItem(itemsBag);
    }

    public void sortItem(List<Item> items) {
        int index = 0;
        for (Item item : items) {
            if (item != null && item.quantity > 0) {
                items.set(index, item);
                index++;
            }
        }
        for (int i = index; i < items.size(); i++) {
            items.set(i, null);
        }
    }

    //--------------------------------------------------------------------------
    public void removeItem(List<Item> items, int index) {
        items.set(index, null);
    }

    public void removeItem(List<Item> items, Item item) {
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (it == null || it.equals(item)) {
                items.set(i, null);
                break;
            }
        }
    }

    public void removeItemBag(int index) {
        removeItem(itemsBag, index);
    }
    
    public void removeItemShip(int index) {
        removeItem(itemsShip, index);
    }
    
    public void removeItemShip(Item item) {
        removeItem(itemsShip, item);
    }

    public void removeItemBag(Item item) {
        removeItem(itemsBag, item);
    }

    public void removeItemBody(int index) {
        removeItem(itemsBody, index);
    }

    public void removeItemPetBody(int index) {
        this.player.pet.inventory.removeItemBody(index);
    }

    public void removeItemBox(int index) {
        removeItem(itemsBox, index);
    }

    //--------------------------------------------------------------------------
    public Item findItem(List<Item> list, int tempId) {
        for (Item item : list) {
            if (item != null && item.template.id == tempId) {
                return item;
            }
        }
        return null;
    }
    
    public Item getItemID(List<Item> list, int itemID) {
        for (Item item : list) {
            if (item != null && item.itemID == itemID) {
                return item;
            }
        }
        return null;
    }
    
    public Item findItem(int itemID) {
        for (Item item : itemsShip) {
            if (item != null && item.itemID == itemID) {
                return item;
            }
        }
        return null;
    }

    public Item findItemBagByTemp(int tempId) {
        return findItem(itemsBag, tempId);
    }

    //--------------------------------------------------------------------------
    public void sendItemBags() {
        arrangeItems(itemsBag);
        List<Item> items = new ArrayList<>();
        Message msg = null;
        try {
            msg = new Message(-36);
            msg.writer().writeByte(0);
            msg.writer().writeByte(itemsBag.size());
            for (Item item : itemsBag) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                    continue;
                }
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeUTF(item.getInfo(player));
                msg.writer().writeUTF(item.getContent());
                if (!item.itemOptions.isEmpty())
                {
                    msg.writer().writeByte(item.itemOptions.size());
                    for (ItemOption op : item.itemOptions)
                    {
                        if (op != null)
                        {
                            if (op.optionTemplate != null)
                            {
                                int[] optionInfo = op.getItemOption(op.optionTemplate.id, op.param);
                                msg.writer().writeByte(optionInfo[0]);
                                msg.writer().writeShort(optionInfo[1]);
                            }
                        }
                        
                    }
                }
                else
                {
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(73);
                    msg.writer().writeShort(0);
                }
            }
            this.player.sendMessage(msg);
        } catch (Exception e) {
            Util.debug("Inventory.sendItemBags - NAME: " + this.player.name + "\n-------------");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendItemBody() {
        Message msg = null;
        try {
            msg = new Message(-37);
            msg.writer().writeByte(0);
            msg.writer().writeShort(this.player.getHead());
            msg.writer().writeByte(itemsBody.size());
            for (Item item : itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo(player));
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        int[] optionInfo = itemOption.getItemOption(itemOption.optionTemplate.id, itemOption.param);
                        msg.writer().writeByte(optionInfo[0]);
                        msg.writer().writeShort(optionInfo[1]);
                    }
                }
            }
            this.player.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendItemBox() {
        arrangeItems(itemsBox);
        Message msg = null;
        try {
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(itemsBox.size());
            for (int i = 0; i < itemsBox.size(); i++) {
                Item item = itemsBox.get(i);
                if (item == null) {
                    msg.writer().writeShort(-1);
                    continue;
                }
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeUTF(item.getInfo());
                msg.writer().writeUTF(item.getContent());
                msg.writer().writeByte(item.itemOptions.size()); //options
                for (ItemOption itemOption : item.itemOptions) {
                    int[] optionInfo = itemOption.getItemOption(itemOption.optionTemplate.id, itemOption.param);
                    msg.writer().writeByte(optionInfo[0]);
                    msg.writer().writeShort(optionInfo[1]);
                }
            }

            this.player.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
}
