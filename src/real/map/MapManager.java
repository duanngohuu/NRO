package real.map;

import java.util.ArrayList;
import java.util.List;
import real.player.Player;
import server.Util;

public class MapManager {
    private static MapManager instance;

    public ArrayList<Map> maps;

    public MapManager() {
        this.maps = new ArrayList<>();
    }

    public static MapManager gI() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    public boolean isMapHome(Player pl){
        return pl.zone.map.id == 21 + pl.gender;
    }

    public void init() {
        maps = MapData.gI().loadMap();
        Util.warning("Finish load map! [" + maps.size() + "]\n");
    }

    public boolean isMapOffline(int id) {
        return id == 21 || id == 22 || id == 23 || id == 47 || id == 111 || id == 46 || id == 45 || id == 48 || id == 49 || id == 50;
    }
    
    public Map getMapById(int id) {
        try{
            for (Map map : maps) {
                if (map.id == id) {
                    return map;
                }
            }
        }
        catch(Exception e){
            Util.debug("getMapById");
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Map> getListByPlanet(int pid) {
        try
        {
            return maps.stream().filter(m -> m.planetId == pid).toList();
        }
        catch(Exception e)
        {
            Util.debug("getMapById");
            e.printStackTrace();
        }
        return null;
    }

    public Zone getMap(int mapId, int zoneId) {
        for (Map map : maps) {
            if (map.id == mapId) {
                return map.map_zone[zoneId];
            }
        }
        return null;
    }

    public String getPlanetName(byte planetId) {
        switch (planetId) {
            case 0:
                return "Trái đất";
            case 1:
                return "Namếc";
            case 2:
                return "Xayda";
            default:
                return "";
        }
    }

    public List<Zone> getMapCapsule(Player pl) {
        List<Zone> list = new ArrayList<>();
        if (pl.zoneBeforeCapsule != null) {
            addListMapCapsule(pl, list, getMap(pl.zoneBeforeCapsule.map.id, pl.zoneBeforeCapsule.zoneId));
        }
        addListMapCapsule(pl, list, getMap(21 + pl.gender, 0));
        addListMapCapsule(pl, list, getMap(47, 0));
        addListMapCapsule(pl, list, getMap(45, 0));
        addListMapCapsule(pl, list, getMap(0, 0));
        addListMapCapsule(pl, list, getMap(7, 0));
        addListMapCapsule(pl, list, getMap(14, 0));
        addListMapCapsule(pl, list, getMap(5, 0));
        addListMapCapsule(pl, list, getMap(13, 0));
        addListMapCapsule(pl, list, getMap(20, 0));
        addListMapCapsule(pl, list, getMap(24 + pl.gender, 0));
        addListMapCapsule(pl, list, getMap(27, 0));
        addListMapCapsule(pl, list, getMap(19, 0));
        addListMapCapsule(pl, list, getMap(79, 0));
        addListMapCapsule(pl, list, getMap(84, 0));
        return list;
    }
    
    public List<Zone> getMapNRSD(Player pl) {
        List<Zone> list = new ArrayList<>();
        addListMapCapsule(pl, list, getMap(85, 0));
        addListMapCapsule(pl, list, getMap(86, 0));
        addListMapCapsule(pl, list, getMap(87, 0));
        addListMapCapsule(pl, list, getMap(88, 0));
        addListMapCapsule(pl, list, getMap(89, 0));
        addListMapCapsule(pl, list, getMap(90, 0));
        addListMapCapsule(pl, list, getMap(91, 0));
        return list;
    }

    private void addListMapCapsule(Player pl, List<Zone> list, Zone map) {
        for (Zone m : list) {
            if (m.map.id == map.map.id) {
                return;
            }
        }
        if (pl.zone.map.id != map.map.id) {
            list.add(map);
        }
    }
}
