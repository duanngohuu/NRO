package real.func;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import real.item.Item;
import real.item.ItemOption;
import real.map.MapManager;
import real.player.Inventory;
import real.player.Player;
import real.player.PlayerManger;
import server.Service;
import server.Util;
import server.io.Message;
import service.DAOS.PlayerDAO;

public class Transaction {

    private static final List<Transaction> TRANSACTIONS = new LinkedList<>();

    private static Transaction instance;

    private static final byte SEND_INVITE_TRADE = 0;
    private static final byte ACCEPT_TRADE = 1;
    private static final byte ADD_ITEM_TRADE = 2;
    private static final byte CANCEL_TRADE = 3;
    private static final byte LOCK_TRADE = 5;
    private static final byte ACCEPT = 7;

    private Player player1;
    private Player player2;
    private byte accept;

    private Transaction(Player pl1, Player pl2) {
        this.player1 = pl1;
        this.player2 = pl2;
    }

    public static Transaction gI() {
        if (instance == null) {
            instance = new Transaction(null, null);
        }
        return instance;
    }

    public void StopTran(Player pl) {
        Transaction tran = findTran(pl);
        if (tran != null) {
            tran.cancelTrade();
            tran.dispose();
            tran = null;
        }
    }

    public Transaction findTran(Player pl) {
        for (Transaction tran : TRANSACTIONS) {
            if (tran.player1.id == pl.id || tran.player2.id == pl.id) {
                return tran;
            }
        }
        return null;
    }

    public void controller(Player pl, Message message) {
        try {
            if (MapManager.gI().isMapOffline(pl.zone.map.id)) {
                return;
            }
            byte action = message.reader().readByte();
            int playerId = -1;
            Player plMap = null;
            Transaction tran = null;
            if(pl == null || pl.session == null){
                Service.gI().sendThongBao(pl, "Có lỗi xảy ra vui lòng đăng nhập lại!");
                return;
            }
            PlayerManger.gI().checkPlayer(pl.session.get_user_id());
            switch (action) {
                case SEND_INVITE_TRADE:
                    playerId = message.reader().readInt();
                    plMap = pl.zone.getPlayerInMap(playerId);
                    if (plMap != null && pl != plMap && pl.zone == plMap.zone && pl.zone.zoneId == plMap.zone.zoneId && pl.playerTran == null) {
//                        if ((pl.role >= 99 && plMap.role < 99) || (pl.role < 99 && plMap.role >= 99)) {
//                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
//                            return;
//                        }
                        tran = findTran(pl);
                        if (tran == null) {
                            tran = findTran(plMap);
                        }
                        if(plMap.session == null){
                            Service.gI().sendThongBao(pl, "Có lỗi xảy ra!");
                            return;
                        }
                        if (plMap.session.get_active() == 0){
                            Service.gI().sendThongBao(pl, "Tài khoản của đối phương chưa kích hoạt!");
                            return;
                        }
                        if(!Util.canDoWithTime(pl.delayTran, 5000)){
                            Service.gI().sendThongBao(pl, "Vui lòng chờ 5s nữa để thực hiện giao dịch!");
                            return;
                        }
                        if (tran == null) {
                            pl.delayTran = System.currentTimeMillis();
                            pl.playerTran = plMap;
                            sendInviteTrade(pl, plMap);
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng chờ đối phương giao dich xong!");
                        }
                    } else {
                        Service.gI().sendThongBao(pl, "Vui lòng chờ một lúc nữa!");
                    }
                    break;
                case ACCEPT_TRADE:
                    playerId = message.reader().readInt();
                    plMap = pl.zone.getPlayerInMap(playerId);
                    if (plMap != null && pl != plMap && pl.zone == plMap.zone && pl.zone.zoneId == plMap.zone.zoneId && plMap.playerTran == pl) {
                        if ((pl.role >= 99 && plMap.role < 99) || (pl.role < 99 && plMap.role >= 99)) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            return;
                        }
                        if(!Util.canDoWithTime(pl.delayTran, 5000)){
                            Service.gI().sendThongBao(pl, "Vui lòng chờ 5s nữa để thực hiện giao dịch!");
                            return;
                        }
                        tran = findTran(pl);
                        if (tran == null) {
                            tran = findTran(plMap);
                        }
                        if (tran == null) {
                            pl.inventory.itemsTrade.clear();
                            plMap.inventory.itemsTrade.clear();
                            tran = new Transaction(pl, plMap);
                            TRANSACTIONS.add(tran);
                            tran.openTabTrade();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng chờ đối phương thực hiện giao dịch xong!");
                        }
                    } else {
                        Service.gI().sendThongBao(pl, "Có lỗi xảy ra khi thực hiện giao dịch");
                    }
                    break;
                case ADD_ITEM_TRADE:
                    byte index = message.reader().readByte();
                    int quantity = message.reader().readInt();
                    addItemTrade(pl, index, quantity);
                    break;
                case CANCEL_TRADE:
                    tran = findTran(pl);
                    if (tran != null) {
                        tran.cancelTrade();
                        tran.dispose();
                        tran = null;
                    }
                    break;
                case LOCK_TRADE:
                    tran = findTran(pl);
                    if (tran != null)
                    {
                        tran.lockTran(pl);
                    }
                    break;
                case ACCEPT:
                    tran = findTran(pl);
                    if (tran != null){
                        tran.acceptTrade();
                        if (tran.accept == 2)
                        {
                            tran.dispose();
                            tran = null;
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng chờ đổi phương đồng ý");
                        }
                    }
                    break;
                default:
                    Util.debug(action +  "");
                    break;
            }
        } catch (Exception e) {
            Util.debug("GIAO DICH");
            e.printStackTrace();
        }
    }

    public boolean CheckItemTrade(Item item) {
        return item.itemOptions.stream().noneMatch((option) -> (option.optionTemplate.id == 30));
    }

    private void addItemTrade(Player pl, byte index, int quantity) {
        Transaction tran = findTran(pl);
        if(tran == null)
        {
            Service.gI().sendThongBao(pl, "Không thể thực hiện!");
            return;
        }
        quantity = quantity <= 0 ? 1 : quantity;
        if (index == -1)
        {
            pl.inventory.goldTrade = quantity;
        }
        else
        {
            Item i = pl.inventory.itemsBag.get(index);
            if(i != null){
                Item item = new Item(i);
                item.itemID = i.itemID;
                if(pl.inventory.itemsTrade.stream().anyMatch(it -> it != null && it.template.id == item.template.id)){
                    removeItemTrade(pl, index);
                    Service.gI().sendThongBao(pl, "Vật phẩm đã tồn tại");
                }
                else if (item.quantity <= 0 || item.template.type == 5 || item.template.type == 22 || item.template.type == 11 || !CheckItemTrade(item) || item.template.id == 1056|| item.template.id == 1057|| item.template.id == 570|| item.template.id == 1055|| item.template.id == 942 || item.template.id == 943 || item.template.id == 944 || item.template.id == 590 || item.template.name.toLowerCase().contains("giáp tập luyện")) {
                    removeItemTrade(pl, index);
                    Service.gI().sendThongBao(pl, "Không thể giao dịch vật phẩm này");
                }
                else {
                    if (quantity <= 0 || quantity > i.quantity) {
                        removeItemTrade(pl, index);
                        Service.gI().sendThongBao(pl, "Số lượng nhập không hợp lệ!");
                    }
                    else {
                        item.quantity = quantity;
                        pl.inventory.itemsTrade.add(item);
                    }
                }
            }
        }
    }

    private void removeItemTrade(Player pl, byte index) {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(2);
            msg.writer().write(index);
            pl.sendMessage(msg);
        }
        catch (Exception e) {
        }
        finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void lockTran(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(6);
            if (pl.equals(player1))
            {
                msg.writer().writeInt(player1.inventory.goldTrade);
                msg.writer().writeByte(player1.inventory.itemsTrade.size());
                for (Item item : player1.inventory.itemsTrade) {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeByte(item.itemOptions.size());
                    for (ItemOption io : item.itemOptions) {
                        msg.writer().writeByte(io.optionTemplate.id);
                        msg.writer().writeShort(io.param);
                    }
                }
                player2.sendMessage(msg);
            }
            else
            {
                msg.writer().writeInt(player2.inventory.goldTrade);
                msg.writer().writeByte(player2.inventory.itemsTrade.size());
                for (Item item : player2.inventory.itemsTrade) {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeByte(item.itemOptions.size());
                    for (ItemOption io : item.itemOptions) {
                        msg.writer().writeByte(io.optionTemplate.id);
                        msg.writer().writeShort(io.param);
                    }
                }
                player1.sendMessage(msg);
            }
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void sendInviteTrade(Player plInvite, Player plReceive) {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(0);
            msg.writer().writeInt(plInvite.id);
            plReceive.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void openTabTrade() {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(1);
            msg.writer().writeInt(player1.id);
            player2.sendMessage(msg);
            msg.cleanup();

            msg = new Message(-86);
            msg.writer().writeByte(1);
            msg.writer().writeInt(player2.id);
            player1.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void cancelTrade() {
//        String notifiText = "Giao dịch " + this.tranId + " bị hủy bỏ";
        String notifiText = "Giao dịch bị hủy bỏ";
        if (player1 != null) {
            Service.gI().sendThongBao(player1, notifiText);
        }
        if (player2 != null) {
            Service.gI().sendThongBao(player2, notifiText);
        }
        closeTab();
    }

    private void closeTab() {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(7);
            player1.sendMessage(msg);
            player2.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void acceptTrade() {
        this.accept++;
        if (this.accept == 2 && PlayerManger.gI().getPlayerByID(player1.id) != null && PlayerManger.gI().getPlayerByID(player2.id) != null) {
            this.trade();
        }
    }

    private void trade() {
        try{
            byte tradeStatus = 0;
            int goldTrade1 = player1.inventory.goldTrade;
            int goldTrade2 = player2.inventory.goldTrade;
            if (player1.inventory.gold + goldTrade2 > player1.inventory.LIMIT_GOLD) {
                tradeStatus = 1;
            }
            else if (player2.inventory.gold + goldTrade1 > player2.inventory.LIMIT_GOLD) {
                tradeStatus = 2;
            }
            if (player1.inventory.gold < goldTrade1){
                tradeStatus = 7;
            }
            else if (player2.inventory.gold < goldTrade2){
                tradeStatus = 8;
            }
            if (tradeStatus != 0) {
                sendNotifyTrade(tradeStatus);
            }
            else {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
                String strDate = formatter.format(date);
                if (player2.getBagNull() < player1.inventory.itemsTrade.size()) {
                    player1.inventory.itemsTrade.clear();
                    tradeStatus = 4;
                }
                if (player1.getBagNull() < player2.inventory.itemsTrade.size()) {
                    player2.inventory.itemsTrade.clear();
                    tradeStatus = 3;
                }
                if (tradeStatus != 0) {
                    sendNotifyTrade(tradeStatus);
                }
                else {
                    if (tradeStatus == 0) {
                        for (Item itemTrade : player1.inventory.itemsTrade) {
                            for (Item itemBag : player1.inventory.itemsBag) {
                                if (itemBag != null && itemBag.equals(itemTrade) && itemBag.quantity >= itemTrade.quantity) {
                                    player2.inventory.addItemBag(itemTrade);
                                    player1.inventory.subQuantityItemsBag(itemBag, itemTrade.quantity);
                                    break;
                                }
                            }
                        }
                        for (Item itemTrade : player2.inventory.itemsTrade) {
                            for (Item itemBag : player2.inventory.itemsBag) {
                                if (itemBag != null && itemBag.equals(itemTrade) && itemBag.quantity >= itemTrade.quantity) {
                                    player1.inventory.addItemBag(itemTrade);
                                    player2.inventory.subQuantityItemsBag(itemBag, itemTrade.quantity);
                                    break;
                                }
                            }
                        }
                        player1.inventory.gold += goldTrade2;
                        player2.inventory.gold += goldTrade1;
                        
                        player1.inventory.gold -= goldTrade1;
                        player2.inventory.gold -= goldTrade2;
                        
                        player1.inventory.sortItemBag();
                        player1.inventory.sendItemBags();
                        
                        player2.inventory.sortItemBag();
                        player2.inventory.sendItemBags();
                        
                        player1.sendInfo();
                        player2.sendInfo();
                    }
                    sendNotifyTrade(tradeStatus);
                }
            }
        }catch(Exception e){
        }
    }

    private void sendNotifyTrade(byte status) {
        this.player2.inventory.itemsTrade.clear();
        this.player1.inventory.itemsTrade.clear();
        this.player1.inventory.goldTrade = 0;
        this.player2.inventory.goldTrade = 0;
        switch (status) {
            case 0:
                Service.gI().sendThongBao(player1, "Giao dịch thành công");
                Service.gI().sendThongBao(player2, "Giao dịch thành công");
                break;
            case 1:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng sau giao dịch vượt tối đa");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do số lượng vàng " + player1.name + " sau giao dịch vượt tối đa");
                break;
            case 2:
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do bạn đã lượng vàng đã tới giới hạn");
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng " + player2.name + " sau giao dịch vượt tối đa");
                break;
            case 3:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do không đủ ô trống hành trang");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do " + player1.name + " không đủ chỗ ô hành trang");
                break;
            case 4:
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do không đủ ô trống hành trang");
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do " + player2.name + " không đủ chỗ ô hành trang");
                break;
            case 5:
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do số lượng không thích hợp");
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do " + player2.name + " có số lượng vật phẩm không thích hợp");
                break;
            case 6:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do số lượng không thích hợp");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do " + player1.name + " có số lượng vật phẩm không thích hợp");
                break;
            case 7:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng không thích hợp");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do " + player1.name + " có số lượng vàng không thích hợp");
                break;
            case 8:
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do số lượng vàng không thích hợp");
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do " + player2.name + " có số lượng vàng không thích hợp");
                break;
        }
    }

    public void dispose() {
        player1.delayTran = System.currentTimeMillis();
        player2.delayTran = System.currentTimeMillis();
        this.player1.inventory.itemsTrade.clear();
        this.player2.inventory.itemsTrade.clear();
        this.player1.inventory.goldTrade = 0;
        this.player2.inventory.goldTrade = 0;
        this.player1 = null;
        this.player2 = null;
        TRANSACTIONS.remove(this);
    }
}
