package real.map;

import real.npc.Npc;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import real.player.BossManager;
import server.SQLManager;

public class MapData {

    static MapData instance;

    public static MapData gI() {
        return instance == null ? instance = new MapData() : instance;
    }

    public ArrayList<Map> loadMap() {
        ArrayList<Map> maps = new ArrayList<>();
        try {
            ResultSet rs = SQLManager.executeQueryDATA("SELECT * FROM map");
            while (rs.next()) {
                int id = rs.getInt("id");
                byte so_luong_khu = rs.getByte("zones");
                byte[][] tileMap = readTileMap(id);
                Npc[] npcs = Npc.getByMap(id);
                WayPoint[] wps = loadListWayPoint(id);
                Map map = new Map(id, tileMap);
                map.map_zone = new Zone[so_luong_khu];
                map.name = rs.getString("name");
                map.type = rs.getByte("type");
                map.setPlanetId(rs.getByte("planet_id"));
                map.tileId = rs.getByte("tile_id");
                map.bgId = rs.getByte("bg_id");
                map.bgType = rs.getByte("bg_type");
                map.maxPlayer = rs.getInt("max_player");
                for (int i = 0; i < map.map_zone.length; i++) {
                    map.map_zone[i] = new Zone(i, map);
                    map.map_zone[i].npcs = npcs;
                    map.map_zone[i].mobs = loadListMob(map, i);
                    map.map_zone[i].wayPoints = wps;
                    if(map.map_zone[i].isYardart()){
                        BossManager.UPDATE(map.map_zone[i]);
                    }
                }
                maps.add(map);
                if (map.map_zone[0].mobs.length > 0) {
                    map.active(500);
                }
                map.loadCurr();
            }
            rs.close();
            rs = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return maps;
    }

    public Mob[] loadListMob(Map map, int z) {
        Mob[] mobs = null;
        try {
            ResultSet rs = SQLManager.executeQueryDATA("SELECT * FROM map_mob WHERE map_id=" + map.id);
            rs.last();
            mobs = new Mob[rs.getRow()];
            rs.first();
            for (int i = 0; i < mobs.length; i++) {
                mobs[i] = new Mob();
                mobs[i].id = i;
                mobs[i].map = map.map_zone[z];
                mobs[i].template = MobTemplateData.gI().getTemp(rs.getInt("temp_id"));
                mobs[i].pointX = rs.getShort("point_x");
                mobs[i].pointY = rs.getShort("point_y");
                mobs[i].cx = rs.getShort("point_x");
                mobs[i].cy = rs.getShort("point_y");
                int hpTemp = mobs[i].template.hp * (map.isPorata(map.id) ? 10 : 1);
                mobs[i].setDame(hpTemp / 100 * 6);
                mobs[i].sethp(hpTemp);
                mobs[i].setHpFull(hpTemp);
                mobs[i].status = 5;
                Mob.temps.add(mobs[i]);
                rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mobs;
    }

    public WayPoint[] loadListWayPoint(int mapId) {
        WayPoint[] wps = null;
        try {
            ResultSet rs = SQLManager.executeQueryDATA("SELECT * FROM map_waypoint WHERE map_id='" + mapId + "'");
            rs.last();
            wps = new WayPoint[rs.getRow()];
            rs.first();
            for (int i = 0; i < wps.length; i++) {
                wps[i] = new WayPoint();
                wps[i].minX = rs.getShort("min_x");
                wps[i].minY = rs.getShort("min_y");
                wps[i].maxX = rs.getShort("max_x");
                wps[i].maxY = rs.getShort("max_y");
                wps[i].name = rs.getString("name");
                wps[i].isEnter = rs.getBoolean("is_enter");
                wps[i].isOffline = rs.getBoolean("is_offline");
                wps[i].goMap = rs.getInt("go_map");
                wps[i].goX = rs.getShort("go_x");
                wps[i].goY = rs.getShort("go_y");
                rs.next();
            }
            rs.close();
            rs = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wps;
    }

    private byte[][] readTileMap(int mapId) {
        byte[][] tileMap = null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/temp/" + mapId));
            dis.readByte();
            int w = dis.readByte();
            int h = dis.readByte();
            tileMap = new byte[h][w];
            for (int i = 0; i < tileMap.length; i++) {
                for (int j = 0; j < tileMap[i].length; j++) {
                    tileMap[i][j] = dis.readByte();
                }
            }
            dis.close();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return tileMap;
    }
}
