package real.map;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Util;

public class Map {

    public int id;
    public String name;

    public byte planetId;
    public String planetName;

    public byte tileId;
    public byte bgId;
    public byte bgType;
    public byte type;

    public byte[][] tileMap;
    public int mapWidth;
    public int mapHeight;
    static final int SIZE = 24;

    public Zone[] map_zone;
    public int maxPlayer;

    public volatile long lastTimeActive;
    public int tmw;
    public int tmh;
    public int pxh;
    public int pxw;
    public int[][] tileType;
    public int[][][] tileIndex;
    public int[] maps;
    public int[] types;
    public Map(int _id, byte[][] tileMap) {       
        this.id = _id;
        this.tileMap = tileMap;
        try {
            this.mapHeight = tileMap.length * SIZE;
            this.mapWidth = tileMap[0].length * SIZE;
        } catch (Exception e) {
        }
    }
    
    public boolean isNRSD(int id) {
        return id == 85 || id == 86 || id == 87||id == 88||id == 89||id == 90||id == 91;
    }
    
    public boolean isDT(int id) {
        return id == 53 || id == 58 || id == 59 || id == 60 || id == 61 || id == 62 || id == 55 || id == 56|| id == 54|| id == 57;
    }
    
    public boolean isPorata(int id) {
        return id == 156 || id == 157 || id == 158 || id == 159;
    }
    
    public final void getMaps(int mapID){
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/temp/" + mapID));
            dis.readByte();
            tmw = dis.readByte();
            tmh = dis.readByte();
            maps = new int[tmw * tmh];
            for (int j = 0; j < maps.length; j++){
                maps[j] = dis.readByte();
            }
            types = new int[maps.length];
            dis.close();
        } catch (Exception e) {
        }
    }
    
    public final void loadMap(int tileId){
        pxh = tmh * SIZE;
	pxw = tmw * SIZE;
        int num = tileId - 1;
        try{
            for (int i = 0; i < tmw * tmh; i++){
                for (int j = 0; j < tileType[num].length; j++){
                    setTile(i, tileIndex[num][j], tileType[num][j]);
                }
            }
        }catch(Exception e){
        }
    }
    
    public void setTile(int index, int[] mapsArr, int type){
        try{
            for (int i = 0; i < mapsArr.length; i++){
		if (maps[index] == mapsArr[i]){
                    types[index] |= type;
                    break;
                }
            }
        }catch(Exception e){
        }
    }
    
    public final void getTile(int id){
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_set/" + id));
            byte tileID = dis.readByte();
            tileIndex = new int[tileID][][];
            tileType = new int[tileID][];
            for (int num108 = 0; num108 < tileID; num108++){
                byte b45 = dis.readByte();
                tileType[num108] = new int[b45];
                tileIndex[num108] = new int[b45][];
                for (int num109 = 0; num109 < b45; num109++){
                    tileType[num108][num109] = dis.readInt();
                    byte b46 = dis.readByte();
                    tileIndex[num108][num109] = new int[b46];
                    for (int num110 = 0; num110 < b46; num110++){
                        tileIndex[num108][num109][num110] = dis.readByte();
                    }
                }
            }
        }catch (Exception e) {
            
        }
    }
     
    public void test(){
        for(int x = 0; x < tmw; x++){
            for(int y = 0; y < tmh; y++){
                int type = types[y * tmw + x];
                if(type == 2){
                    Util.log("MAP: " + id + " | X: " + x * SIZE + " | Y: " + y * SIZE);
                    Util.log("------------------");
                    continue;
                }
            }
        }
    }
    
    public void setPlanetId(byte planetId) {
        planetName = MapManager.gI().getPlanetName(planetId);
        this.planetId = planetId;
    }
    
    public void loadCurr(){
        getTile(999);
        getMaps(id);
        loadMap(tileId);
    }
    
    public boolean tileTypeAt(int x, int y, int type){
        try{
            return (types[y / 24 * tmw + x / 24] & type) == type;
        }catch(Exception e){
            return false;
        }
    }
    
    public int tileType(int x, int y){
        try{
            return (maps[y / 24 * tmw + x / 24]);
        }catch(Exception e){
            return 0;
        }
    }
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public void close() {
        try {
            actived = false;
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
        } catch (Exception e) {
            task = null;
            timer = null;
        }
    }

    public void active(int delay) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Map.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }

    
    public void update() {
        try {
            if (MapManager.gI().isMapOffline(id)) {
                return;
            }
            for (Zone m : map_zone) {
                m.getMobDie();
                for (Mob mob : m.mobs) {
                    mob.update();
                }
                m.items.forEach(itm -> {
                    if (itm != null) {
                        itm.update();
                    }
                });
            }
        } catch (Exception e) {
        }
    }
}
