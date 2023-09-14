package real.func;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import real.boss.BossManager;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Player;
import real.player.PlayerManger;
import server.Service;
import server.io.Message;
import service.Setting;

public class PVP {

    public static final byte TYPE_DIE = 0;
    public static final byte TYPE_LEAVE_MAP = 1;

    private static final byte OPEN_GOLD_SELECT = 0;
    private static final byte ACCEPT_PVP = 1;

    private static final List<PVP> PVPS = new LinkedList<>();
    private static final Map COUPLE_PVP = new HashMap();
    private static final Map PL_GOLD = new HashMap();

    private static PVP instance;

    private long pvpId;
    public Player player1;
    public Player player2;
    private int totalGold;

    private PVP() {

    }

    private PVP(Player pl1, Player pl2) {
        this.player1 = pl1;
        this.player2 = pl2;
        this.pvpId = System.currentTimeMillis();
        this.totalGold = (int) PL_GOLD.get(pl1) * 2 * 99 / 100;
    }

    public static PVP gI() {
        if (instance == null) {
            instance = new PVP();
        }
        return instance;
    }

    public void controller(Player pl, Message message) {
        try {
            byte action = message.reader().readByte();
            byte type = message.reader().readByte();
            int playerId = message.reader().readInt();
            Player plMap = pl.zone.getPlayerInMap(playerId);
            COUPLE_PVP.put(pl, plMap);
            COUPLE_PVP.put(plMap, pl);
            switch (action) {
                case OPEN_GOLD_SELECT:
                    openSelectGold(pl, plMap);
                    break;
                case ACCEPT_PVP:
                    acceptPVP(pl);
                    break;
            }
        } catch (IOException ex) {

        }
    }

    public PVP findPvp(Player player) {
        for (PVP pvp : PVPS) {
            if (pvp.player1.id == player.id || pvp.player2.id == player.id) {
                return pvp;
            }
        }
        return null;
    }

    public void finishPVP(Player plLose, byte typeWin) {
        if (plLose.typePk != 0) {
            Player plWin = (Player) COUPLE_PVP.get(plLose);
            COUPLE_PVP.put(plLose, null);
            if (plWin != null) {
                COUPLE_PVP.put(plWin, null);
            }
            PVP pvp = findPvp(plLose);
            if (pvp != null) {
                Service.gI().changeTypePK(pvp.player1, 0);
                Service.gI().changeTypePK(pvp.player2, 0);
                if (pvp.player1.equals(plLose)) {
                    pvp.sendResultMatch(pvp.player2, pvp.player1, typeWin);
                } else {
                    pvp.sendResultMatch(pvp.player1, pvp.player2, typeWin);
                }
                pvp.dispose();
                PVPS.remove(pvp);
                pvp = null;
            }
        }
    }

    private void acceptPVP(Player pl) {
        Player pl2 = (Player) COUPLE_PVP.get(pl);
        if (pl2 != null) {
            if (pl.zone.equals(pl2.zone)) {
                PVP pvp = new PVP(pl, pl2);
                PVPS.add(pvp);
                pvp.start();
            } else {
                Service.gI().sendThongBao(pl, "Đối thủ đã rời khỏi map");
            }
        }
    }

    private void start() {
        int gold = (int) PL_GOLD.get(this.player1);
        if(PlayerManger.gI().getPlayerByID(this.player1.id) != null){
            this.player1.inventory.gold -= gold;
            this.player1.sendInfo();
            Service.gI().changeTypePK(this.player1, 3);
        }
        if(PlayerManger.gI().getPlayerByID(this.player2.id) != null){
            this.player2.inventory.gold -= gold;
            this.player2.sendInfo();
            Service.gI().changeTypePK(this.player2, 3);
        }
    }

    private void openSelectGold(Player pl, Player plMap) {
        PVP pvp1 = findPvp(pl);
        PVP pvp2 = findPvp(plMap);
        if (pvp1 == null && pvp2 == null) {
            Npc.createMenuConMeo(pl, NpcFactory.MAKE_MATCH_PVP, -1, plMap.name + " (sức mạnh " + Service.numberToMoney(plMap.point.power) + ")\nBạn muốn cược bao nhiêu vàng?",
                    "1000\nvàng", "10000\nvàng", "100000\nvàng");
        }
        else {
            Service.gI().hideInfoDlg(pl);
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    public void openSelectRevenge(Player pl, Player enemy) {
        COUPLE_PVP.put(pl, enemy);
        COUPLE_PVP.put(enemy, pl);
        PL_GOLD.put(pl, 0);
        PL_GOLD.put(enemy, 0);
        PVP pvp1 = findPvp(pl);
        PVP pvp2 = findPvp(enemy);
        if (pvp1 == null && pvp2 == null) {
            Npc.createMenuConMeo(pl, NpcFactory.REVENGE, -1, "Bạn muốn đến ngay chỗ hắn, phí là 1 ngọc và được tìm thoải mái trong 5 phút nhé", "Ok", "Từ chối");
        } else {
            Service.gI().hideInfoDlg(pl);
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    public void acceptRevenge(Player pl) {
        if (pl.inventory.getGemAndRuby() > 0) {
            pl.inventory.subGemAndRuby(1);
            pl.sendInfo();
            Player enemy = (Player)COUPLE_PVP.get(pl);
            if (enemy != null && !enemy.zone.isPhuBan()) {
//                if (enemy.itemTime.ExitsItemTiem(2760) || enemy.zone.map.id == 21 || enemy.zone.map.id == 22 ||enemy.zone.map.id == 23 ||enemy.zone.players.size() >= enemy.zone.map.maxPlayer|| enemy.zone.isNRSD() || (Setting.HO_TRO_TDST && enemy.taskId != 19 && BossManager.isTDST && enemy.zone.map.id == BossManager.mapTDST && enemy.zone.zoneId == BossManager.zoneTDST)) {
//                    Service.gI().sendThongBao(pl, "Hiện tại không thể thực hiện vui lòng thử lại sau vài phút");
//                }
                if (enemy.itemTime.ExitsItemTiem(2760) || enemy.zone.map.id == 21 || enemy.zone.map.id == 22 ||enemy.zone.map.id == 23 || enemy.zone.players.size() >= enemy.zone.map.maxPlayer || enemy.zone.isNRSD() || (Setting.HO_TRO_TDST && enemy.taskId != 19)) {
                    Service.gI().sendThongBao(pl, "Hiện tại không thể thực hiện vui lòng thử lại sau vài phút");
                }
                else{
                    PVP pvp = new PVP(pl, enemy);
                    PVPS.add(pvp);
                    ChangeMap.gI().changeMap(pl, enemy.zone.map.id, enemy.zone.zoneId, enemy.x, enemy.y, ChangeMap.NON_SPACE_SHIP);
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                        }
                        Service.gI().chat(pl, "Mau đền tội");
                        pvp.start();
                    }).start();
                }
            }
            else if(enemy.zone.isPhuBan()){
                Service.gI().sendThongBao(pl, "Hiện tại không thể thực hiện vui lòng thử lại sau vài phút");
            }
        } else {
            Service.gI().sendThongBao(pl, "Bạn không đủ ngọc, còn thiếu 1 ngọc nữa");
        }
    }

    public void sendInvitePVP(Player pl, byte selectGold) {
        Message msg;
        try {
            Player plReceive = (Player) COUPLE_PVP.get(pl);
            if (plReceive != null) {
                int gold = selectGold == 0 ? 1000 : selectGold == 1 ? 10000 : 100000;
                if (pl.inventory.gold >= gold) {
                    if (plReceive.inventory.gold < gold) {
                        Service.gI().sendThongBao(pl, "Đối thủ chỉ có " + plReceive.inventory.gold + " vàng, không đủ tiền cược");
                    } else {
                        PL_GOLD.put(pl, gold);
                        PL_GOLD.put(plReceive, gold);
                        msg = new Message(-59);
                        msg.writer().writeByte(3);
                        msg.writer().writeInt(pl.id);
                        msg.writer().writeInt(gold);
                        msg.writer().writeUTF(pl.name + " (sức mạnh " + Service.numberToMoney(pl.point.tiemNangUse) + ") muốn thách đấu bạn với mức cược " + gold);
                        plReceive.sendMessage(msg);
                        msg.cleanup();
                    }
                } else {
                    Service.gI().sendThongBao(pl, "Bạn chỉ có " + pl.inventory.gold + " vàng, không đủ tiền cược");
                }
            }
        } catch (Exception e) {
        }
    }

    private void sendResultMatch(Player winer, Player loser, byte typeWin) {
        if(this.totalGold <= 0){
            return;
        }
        winer.inventory.gold += this.totalGold;
        winer.sendInfo();
        switch (typeWin) {
            case TYPE_DIE:
                Service.gI().sendThongBao(winer, "Đối thử đã kiệt sức, bạn thắng được " + this.totalGold + " vàng");
                if (PlayerManger.gI().getPlayerByID(loser.id) != null) {
                    Service.gI().sendThongBao(loser, "Bạn đã thua vì đã kiệt sức");
                }
                break;
            case TYPE_LEAVE_MAP:
                Service.gI().sendThongBao(winer, "Đối thủ sợ quá bỏ chạy, bạn thắng được " + this.totalGold + " vàng");
                if (PlayerManger.gI().getPlayerByID(loser.id) != null) {
                    Service.gI().sendThongBao(loser, "Đạn bị xử thua vì đã bỏ chạy");
                }
                break;
        }
    }

    private void dispose() {
        this.player1 = null;
        this.player2 = null;
    }
}
