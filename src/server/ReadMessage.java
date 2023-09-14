package server;
//share by chibikun

import real.func.Transaction;
import real.func.UseItem;
import real.item.Item;
import real.map.MapManager;
import real.player.Player;
import real.player.PlayerManger;
import server.io.Message;
import server.io.Session;

public class ReadMessage {

    private static ReadMessage instance;

    public static ReadMessage gI() {
        if (instance == null) {
            instance = new ReadMessage();
        }
        return instance;
    }

    public void getItem(Session session, Message msg) {
        if (Transaction.gI().findTran(session.player) != null) {
            Service.gI().sendThongBao(session.player, "Không thể thực hiện");
            return;
        }
        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
        if (player.isDie())
        {
            return;
        }
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();

            //   Util.log("type = " + type);
            switch (type) {
                case 0:
                    player.inventory.itemBoxToBag(index);
                    break;
                case 1:
                    player.inventory.itemBagToBox(index);
                    break;
                case 4:
                    player.inventory.itemBagToBody(index);
                    break;
                case 5:
                    player.inventory.itemBodyToBag(index);
                    break;
                case 6:
                    player.inventory.itemBagToPetBody(index);
                    break;
                case 7:
                    player.inventory.itemPetBodyToBag(index);
                    break;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void useItem(Player player, Message _msg) {
        if (Transaction.gI().findTran(player) != null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (player.isDie()) {
            return;
        }
        Message msg = null;
        try {
            int type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            int item_id = -1;
            if (index == -1)
            {
                item_id = _msg.reader().readShort();
            }
            else if (player.inventory.itemsBag.get(index) == null && where == 1)
            {
                return;
            }
            //System.out.println(type + " " + where + " " + index);
            switch (type) {
                case 0:
                    if (index == -1)
                    {
                        UseItem.gI().useItem(player, player.inventory.itemsBag.get(player.inventory.getIndexBagid(item_id)), player.inventory.getIndexBagid(item_id));
                    }
                    else if (player.inventory.itemsBag.get(index).template.type == 7 || player.inventory.itemsBag.get(index).template.type == 22) {
                        Service.gI().ItemRequest(player, type, where, index, "Bạn chắc chắn muốn sử dụng "+ player.inventory.itemsBag.get(index).template.name + "?");
                    }
                    else
                    {
                        UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    }
                    break;
                case 1:
                    if (MapManager.gI().isMapOffline(player.zone.map.id))
                    {
                        Service.gI().sendThongBao(player, "Bạn không thể vứt vật phẩm tại đây");
                        return;
                    }
                    if (where == 1)
                    {
                        Item item = player.inventory.itemsBag.get(index);
                        if(item.GetItemOption(102) != null || item.GetItemOption(72) != null)
                        {
                            Service.gI().sendThongBao(player, "Bạn không thể vứt vật phẩm này");
                            return;
                        }
                        Service.gI().ItemRequest(player, type, where, index, "Bạn chắc chắn muốn vứt " + item.template.name + "(Mất luôn)?");
                    }
                    else
                    {
                        Item item = player.inventory.itemsBody.get(index);
                        if(item.GetItemOption(102) != null || item.GetItemOption(72) != null)
                        {
                            Service.gI().sendThongBao(player, "Bạn không thể vứt vật phẩm này");
                            return;
                        }
                        Service.gI().ItemRequest(player, type, where, index, "Bạn chắc chắn muốn vứt " + item.template.name + "(Mất luôn)?");
                    }
                    break;
                case 2:
                    if (MapManager.gI().isMapOffline(player.zone.map.id))
                    {
                        Service.gI().sendThongBao(player, "Bạn không thể vứt vật phẩm tại đây");
                        return;
                    }
                    player.inventory.throwItem(where, index);
                    Service.gI().point(player);
                    player.inventory.sendItemBags();
                    break;
                case 3:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
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
