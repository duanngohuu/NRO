package real.npc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Vector;
import real.map.Map;
import real.map.MapManager;
import real.player.Player;
import server.SQLManager;
import server.io.Message;
import service.Setting;

public abstract class Npc implements IAtionNpc {

    private static Vector<Npc> NPCS = new Vector<>();

    public int mapId;

    public int status;

    public int cx;

    public int cy;

    public int tempId;

    public int avartar;

    public Menu mainMenu;

    public Menu[] subMenu;

    protected Npc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        this.mapId = mapId;
        this.status = status;
        this.cx = cx;
        this.cy = cy;
        this.tempId = tempId;
        this.avartar = avartar;
        Npc.NPCS.add(this);
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay ,String... menuSelect) {
        Message msg = null;
        try {
            player.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                if(menu.isEmpty()){
                    continue;
                }
                msg.writer().writeUTF(menu);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public static void createMenuRongThieng(Player player, int indexMenu, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, NpcFactory.RONG_THIENG, -1, npcSay, menuSelect);
    }

    public static void createMenuConMeo(Player player, int indexMenu, int avatar, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, NpcFactory.CON_MEO, avatar, npcSay, menuSelect);
    }

    private static void createMenu(Player player, int indexMenu, byte npcTempId, int avatar, String npcSay, String... menuSelect) {
        Message msg = null;
        try {
            player.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(npcTempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                if(menu.isEmpty()){
                    continue;
                }
                msg.writer().writeUTF(menu);
            }
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public static void createTutorial(Player player, int avatar, String npcSay) {
        Message msg = null;
        try {
            msg = new Message(38);
            msg.writer().writeShort(NpcFactory.CON_MEO);
            msg.writer().writeUTF(npcSay);
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    @Override
    public void openMenu(Player player) {
        Message msg = null;
        try {
            if (mainMenu != null) {
                mainMenu.openMenu(player);
            } else {
                msg = new Message(32);
                msg.writer().writeShort(tempId);
                msg.writer().writeUTF("Xin chào " + player.name + " bạn có việc gì?");
                msg.writer().writeByte(1);
                msg.writer().writeUTF(this.tempId + " - " + this.mapId);
                player.sendMessage(msg);
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

    public static Npc getByIdAndMap(int id, int mapId) {
        for (Npc npc : NPCS) {
            if (npc.tempId == id && npc.mapId == mapId || npc.tempId == id && id == 54) {
                return npc;
            }
        }
        return null;
    }

    public static Npc getNpc(byte tempId) {
        for (Npc npc : NPCS) {
            if (npc.tempId == tempId) {
                return npc;
            }
        }
        return null;
    }

    public static Npc[] getByMap(int map) {
        Npc[] list = null;
        int i = 0;
        for (Npc npc : NPCS) {
            if (npc.mapId == map && npc.status == 1) {
                i++;
            }
        }
        list = new Npc[i];
        i = 0;
        for (Npc npc : NPCS) {
            if (npc.mapId == map && npc.status == 1) {
                list[i] = npc;
                i++;
            }
        }
        return list;
    }
    
    public static Npc[] getByMap(Player pl) {
        Npc[] list = null;
        int i = 0;
        for (Npc npc : NPCS) {
            if (!Setting.EVENT_TRUNG_THU && npc.mapId == pl.zone.map.id && npc.tempId != 41) {
                i++;
            } else if (Setting.EVENT_TRUNG_THU && npc.mapId == pl.zone.map.id) {
                i++;
            }
        }
        list = new Npc[i];
        i = 0;
        for (Npc npc : NPCS) {
            if (!Setting.EVENT_TRUNG_THU && npc.mapId == pl.zone.map.id && npc.tempId != 41) {
                list[i] = npc;
                i++;
            } else if (Setting.EVENT_TRUNG_THU && npc.mapId == pl.zone.map.id) {
                list[i] = npc;
                i++;
            }
        }
        return list;
    }

    public static void loadNPC() {
        try {
            NPCS.clear();
            ResultSet rs = SQLManager.executeQueryDATA("SELECT * FROM map_npc");
            while (rs.next()) {
                NpcFactory.createNPC(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6));
            }
            rs.close();
            rs = null;
            NpcFactory.createNpcConMeo();
            NpcFactory.createNpcRongThieng();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

}
