package real.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import real.boss.BossManager;
import real.func.ChangeMap;
import real.func.PVP;
import real.item.Item;
import real.npc.Npc;
import real.npc.NpcFactory;
import server.Service;
import server.Util;
import server.io.Message;
import service.DAOS.PlayerDAO;
import service.Setting;

public class Entities {

    private static final byte OPEN_LIST = 0;

    private static final byte MAKE_FRIEND = 1;
    private static final byte REMOVE_FRIEND = 2;

    private static final byte REVENGE = 1;
    private static final byte REMOVE_ENEMY = 2;

    private final Player player;
    public List<Integer> friends;
    public List<Integer> enemies;
    
    private int playerMakeFriend;

    public Entities(Player player) {
        this.player = player;
        this.friends = new ArrayList<>();
        this.enemies = new ArrayList<>();
    }

    public void controllerFriend(Message msg) {
        try {
            byte action = msg.reader().readByte();
            switch (action) {
                case OPEN_LIST:
                    openListFriend();
                    break;
                case MAKE_FRIEND:
                    makeFriend(msg.reader().readInt());
                    break;
                case REMOVE_FRIEND:
                    removeFriend(msg.reader().readInt());
                    break;
            }
        } catch (IOException ex) {

        }
    }

    public void controllerEnemy(Message msg) {
        try {
            byte action = msg.reader().readByte();
            //System.out.println("enemy action: " + action);
            switch (action) {
                case OPEN_LIST:
                    openListEnemy();
                    break;
                case REVENGE:
                    Player enemy = PlayerManger.gI().getPlayerByID(msg.reader().readInt());
                    if (enemy != null) {
                        PVP.gI().openSelectRevenge(player, enemy);
                    } else {
                        Service.gI().sendThongBao(player, "Đang offline");
                    }
                    break;
                case REMOVE_ENEMY:
                    removeEnemy(msg.reader().readInt());
                    Service.gI().sendThongBao(player, "Đã xóa thành công");
                    openListEnemy();
                    break;
            }
        } catch (Exception ex) {

        }
    }

    private void openListFriend() {
        Message msg = null;
        try {
            msg = new Message(-80);
            msg.writer().writeByte(OPEN_LIST);
            msg.writer().writeByte(friends.size());
            Player pl;
            for (int p : friends) {
                pl = PlayerDAO.LoadPlayer(p);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (this.player.session.get_version() > 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeByte(pl.get_bag());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeBoolean(PlayerManger.gI().getPlayerByID((int) pl.id) != null);
                msg.writer().writeUTF(Service.numberToMoney(pl.point.power));
                if (PlayerManger.gI().getPlayerByID(p) == null) {
                    pl.dispose();
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

    private void openListEnemy() {
        Message msg = null;
        try {
            msg = new Message(-99);
            msg.writer().writeByte(OPEN_LIST);
            msg.writer().writeByte(enemies.size());
            Player pl = null;
            for (int p : enemies) {
                pl = PlayerDAO.LoadPlayer(p);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (this.player.session.get_version() > 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeShort(pl.get_bag());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(Service.numberToMoney(pl.point.power));
                msg.writer().writeBoolean(PlayerManger.gI().getPlayerByID((int) pl.id) != null);
                if (PlayerManger.gI().getPlayerByID(p) == null) {
                    pl.dispose();
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

    private void makeFriend(int playerId) {
        if(player.id == playerId){
            return;
        }
        if (friends.contains(playerId)) {
            Service.gI().sendThongBao(player, "Đã có trong danh sách bạn bè");
            return;
        }
        this.playerMakeFriend = playerId;
        Player pl = PlayerManger.gI().getPlayerByID(playerId);
        String npcSay;
        if (friends.size() >= 5) {
            npcSay = "Bạn có muốn kết bạn với " + pl.name + " với phí là 5 ngọc ?";
        } else {
            npcSay = "Bạn có muốn kết bạn với " + pl.name + " ?";
        }
        Npc.createMenuConMeo(player, NpcFactory.MAKE_FRIEND, -1, npcSay, "Đồng ý", "Từ chối");

    }

    private void removeFriend(int playerId) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i) == playerId) {
                Service.gI().sendThongBao(player, "Đã xóa thành công");
                Message msg = null;
                try {
                    msg = new Message(-80);
                    msg.writer().writeByte(REMOVE_FRIEND);
                    msg.writer().writeInt((int) friends.get(i));
                    player.sendMessage(msg);
                } catch (Exception e) {
                } finally {
                    if (msg != null) {
                        msg.cleanup();
                        msg = null;
                    }
                }
                friends.remove(i);
                break;
            }
        }
    }
    
    public void removeEnemy(int playerId) {
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i) == playerId) {
                enemies.remove(i);
                break;
            }
        }
    }

    public void chatPrivate(Message msg) {
        try {
            int playerId = msg.reader().readInt();
            String text = msg.reader().readUTF();
            Player pl = PlayerManger.gI().getPlayerByID(playerId);
            if (pl != null) {
                Service.gI().chatPrivate(player, pl, text);
            }
        } catch (Exception e) {
        }
    }

    public void acceptMakeFriend() {
        Player pl = PlayerManger.gI().getPlayerByID(playerMakeFriend);
        if (pl != null) {
            friends.add((int) pl.id);
            Service.gI().sendThongBao(player, "Kết bạn thành công");
            Service.gI().chatPrivate(player, pl, player.name + " vừa mới kết bạn với " + pl.name);
        } else {
            Service.gI().sendThongBao(player, "Không tìm thấy hoặc đang Offline, vui lòng thử lại sau");
        }
    }

    public void goToPlayerWithYardrat(Message msg) {
        try {
            Player pl = PlayerManger.gI().getPlayerByID(msg.reader().readInt());
            if(pl == null){
                Service.gI().sendThongBao(player, "Đối phương đã offline!");
                return;
            }
            int[] toado = new int[] {-50, 50};
            if(player.role >= Setting.ROLE_ADMIN){
                ChangeMap.gI().changeMap(player, pl.zone.map.id, pl.zone.zoneId, pl.x + toado[Util.nextInt(0, 1)], pl.y, ChangeMap.TELEPORT_YARDRAT);
                return;
            }
            if(!Util.canDoWithTime(player.lastTimeYardrat, 10000)){
                long curr = (System.currentTimeMillis() - player.lastTimeYardrat) / 1000;
                Service.gI().sendThongBao(player, "Vui lòng đợi " + (10 - curr) + " giây nữa");
                return;
            }
            player.lastTimeYardrat = System.currentTimeMillis();
            if (pl.role != Setting.ROLE_ADMIN) {
                Item ct = player.inventory.itemsBody.get(5);
                if(ct != null && (ct.template.id == 592 || ct.template.id == 593 || ct.template.id == 594) && player.inventory.OptionCt(33)){
//                    if (pl.itemTime.ExitsItemTiem(2760) || pl.zone.isMapDTDN() || pl.zone.map.id == 21 || pl.zone.map.id == 22 ||pl.zone.map.id == 23 || pl.zone.isNRSD() || (Setting.HO_TRO_TDST && pl.taskId != 19 && BossManager.isTDST && pl.zone.map.id == BossManager.mapTDST && pl.zone.zoneId == BossManager.zoneTDST)) {
//                        Service.gI().sendThongBao(player, "Không thể dịch chuyển tới người chơi bây giờ");
//                    }
                    if (pl.itemTime.ExitsItemTiem(2760) || pl.zone.isMapDTDN() || pl.zone.map.id == 21 || pl.zone.map.id == 22 ||pl.zone.map.id == 23 || pl.zone.isNRSD()) {
                        Service.gI().sendThongBao(player, "Không thể dịch chuyển tới người chơi bây giờ");
                    }
                    else if(pl.zone.players.size() >= pl.zone.map.maxPlayer){
                        Service.gI().sendThongBao(player, "Khu vực đã đầy");
                    }
                    else{
                        ChangeMap.gI().changeMap(player, pl.zone.map.id, pl.zone.zoneId, pl.x + toado[Util.nextInt(0, 1)], pl.y, ChangeMap.TELEPORT_YARDRAT);
                    }
                }
                else{
                    Service.gI().sendThongBao(player, "Yêu cầu trang bị có khả năng dịch chuyển tức thời");
                }
            } else if (pl.role == Setting.ROLE_ADMIN && player.role != Setting.ROLE_ADMIN) {
                Service.gI().sendThongBao(player, "Không thế dịch chuyển đến ADMIN");
            } else {
                Item ct = player.inventory.itemsBody.get(5);
//                if (pl.itemTime.ExitsItemTiem(2760) || pl.zone.isMapDTDN() || pl.zone.map.id == 21 || pl.zone.map.id == 22 || pl.zone.map.id == 23 || pl.zone.isNRSD() || pl.zone.players.size() >= pl.zone.map.maxPlayer || (Setting.HO_TRO_TDST && pl.taskId != 19 && BossManager.isTDST && pl.zone.map.id == BossManager.mapTDST && pl.zone.zoneId == BossManager.zoneTDST)) {
//                    Service.gI().sendThongBao(player, "Không thể dịch chuyển tới người chơi bây giờ");
//                }
                if (pl.itemTime.ExitsItemTiem(2760) || pl.zone.isMapDTDN() || pl.zone.map.id == 21 || pl.zone.map.id == 22 || pl.zone.map.id == 23 || pl.zone.isNRSD() || pl.zone.players.size() >= pl.zone.map.maxPlayer) {
                    Service.gI().sendThongBao(player, "Không thể dịch chuyển tới người chơi bây giờ");
                }
                else if (ct != null && (ct.template.id == 592 || ct.template.id == 593 || ct.template.id == 594)) {
                    ChangeMap.gI().changeMap(player, pl.zone.map.id, pl.zone.zoneId, pl.x + toado[Util.nextInt(0, 1)], pl.y, ChangeMap.TELEPORT_YARDRAT);
                }
                else {
                    Service.gI().sendThongBao(player, "Yêu cầu trang bị có khả năng dịch chuyển tức thời");
                }
            }
        } catch (IOException ex) {

        }
    }

    public void addFriend(int player) {
        this.friends.add(player);
    }

    public void addEnemy(int player) {
        if (!enemies.contains(player)) {
            this.enemies.add(player);
        }
    }
}
