package real.map;

import real.player.Player;
import server.Util;
import server.io.Message;
import server.Service;

public class MapService {

    private static MapService instance;

    public static MapService gI() {
        if (instance == null) {
            instance = new MapService();
        }
        return instance;
    }

    public void joinMap(Player plJoin, Zone map) {
        try {
            if(map == null || MapManager.gI().isMapOffline(map.map.id)){
                return;
            }
            if (!map.getPlayers().isEmpty()) {
                for (Player pl : map.getPlayers()) {
                    if (pl != null && plJoin != null && pl.isPl() && plJoin != pl) {
                        infoPlayer(pl, plJoin);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    public void loadAnotherPlayers(Player plReceive, Zone map) { //load những player trong map và gửi cho player vào map
        try {
            if(map == null || MapManager.gI().isMapOffline(map.map.id)){
                return;
            }
            if(plReceive.isPl()){
                if(map != null){
                    for (Player pl : map.getPlayers()) {
                        if (pl != null && plReceive != null && plReceive != pl) {
                            infoPlayer(plReceive, pl);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Util.debug("loadAnotherPlayers");
//            ex.printStackTrace();
        }
    }

    private synchronized void infoPlayer(Player plReceive, Player plInfo) {
        synchronized(plInfo){
            Message msg = null;
            try {
                if(!plReceive.isPl()){
                    return;
                }
                msg = new Message(-5);
                msg.writer().writeInt(plInfo.id);
                if (plInfo.clan != null) {
                    msg.writer().writeInt(plInfo.clan.id);
                } else {
                    msg.writer().writeInt(-1);
                }
                msg.writer().writeByte(Service.gI().get_clevel(plInfo.point.getPower()));
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(plInfo.typePk);
                msg.writer().writeByte(plInfo.gender);
                msg.writer().writeByte(plInfo.gender);
                msg.writer().writeShort(plInfo.getHead());
                msg.writer().writeUTF(plInfo.name);
                msg.writer().writeInt(plInfo.point.getHP());
                msg.writer().writeInt(plInfo.point.getHPFull());
                msg.writer().writeShort(plInfo.getBody());
                msg.writer().writeShort(plInfo.getLeg());
                msg.writer().writeByte(plInfo.get_bag());
                msg.writer().writeByte(-1);
                msg.writer().writeShort(plInfo.x);
                msg.writer().writeShort(plInfo.zone.LastY(plInfo.x, plInfo.y));
                msg.writer().writeShort(0);
                msg.writer().writeShort(0);
                msg.writer().writeByte(0);
                msg.writer().writeByte(plInfo.getUseSpaceShip());
                msg.writer().writeByte(plInfo.playerSkill.isMonkey ? 1 : 0);
                msg.writer().writeShort(plInfo.getMount());
                msg.writer().writeByte(plInfo.cFlag);
                msg.writer().writeByte(0);
                //----aura----
                msg.writer().writeShort(plInfo.role >= 99 ? 0 : -1);
                //----set_----
                msg.writer().writeByte(plInfo.role >= 99 ? 8 : -1);
                //----hatid----
                msg.writer().writeShort(-1);
                plReceive.sendMessage(msg);
                if (plInfo.isDie()) {
                    msg = new Message(-8);
                    msg.writer().writeInt((int) plInfo.id);
                    msg.writer().writeByte(0);
                    msg.writer().writeShort(plInfo.x);
                    msg.writer().writeShort(plInfo.y);
                    plReceive.sendMessage(msg);
                }
                if(plInfo.role >= 99){
                    Service.gI().sendEffPlayer(plInfo, plReceive, 58, 1, -1, 1);
                }
            } catch (Exception e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                    msg = null;
                }
            }
        }
    }

    public void exitMap(Player playerExit, Zone map) {
        if (map != null) {
            Message msg = null;
            try {
                map.players.remove(playerExit);
                msg = new Message(-6);
                msg.writer().writeInt((int) playerExit.id);
                Service.gI().sendMessAnotherNotMeInMap(playerExit, msg);
            } catch (Exception e) {
//                e.printStackTrace();
            } finally {
                if (msg != null) {
                    msg.cleanup();
                    msg = null;
                }
            }
        }
    }

    public void playerMove(Player player) {
        if (player.playerSkill.count_ttnl != -1) {
            player.playerSkill.stopCharge();
        }
        if (player.playerSkill.useTroi) {
            player.playerSkill.removeUseTroi();
        }
        Message msg = null;
        try {
            msg = new Message(-7);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.x);
            msg.writer().writeShort(player.y);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
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
