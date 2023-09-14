package real.func;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import real.item.Item;
import real.item.ItemData;
import real.map.ItemMap;
import real.map.Zone;
import real.map.MapManager;
import real.map.MapService;
import real.map.Map;
import real.map.Mob;
import real.map.WayPoint;
import real.player.Player;
import real.task.TaskData;
import server.Controller;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;

public class ChangeMap {

    public static final byte NON_SPACE_SHIP = 0;
    public static final byte TELEPORT_YARDRAT = 2;
    public static final byte TENNIS_SPACE_SHIP = 3;
    public static final byte DEFAUL_SPACE_SHIP = 1;
    private static ChangeMap instance;

    private ChangeMap() {

    }

    public static ChangeMap gI() {
        if (instance == null) {
            instance = new ChangeMap();
        }
        return instance;
    }
    
    public static byte getSpaceShip(Player pl){
        return (byte)pl.typeShip;
    }

    //capsule, tàu vũ trụ
    public void changeMapBySpaceShip(Player pl, int mapId, int zone, int x, byte typeSpace) {
        if (pl.isDie()) {
            mapId = 21 + pl.gender;
        }
        Map maps = MapManager.gI().getMapById(mapId);
        if (!this.listMapCanChange(pl).stream().anyMatch(id -> maps.id == id)) {
            Service.gI().sendPopUpMultiLine(pl,0, 7184, "Không thể vào map");
            return;
        }
        Zone map = null;
        if (MapManager.gI().isMapOffline(mapId)) {
            map = maps.map_zone[0];
        } else if (zone == -1) {
            for (Zone m : maps.map_zone) {
                if (m.getPlayers().size() <= 7) {
                    map = m;
                    break;
                }
            }
            if (map == null) {
                for (Zone m : maps.map_zone) {
                    if (m.getPlayers().size() < 15) {
                        map = m;
                        break;
                    }
                }
            }
        } else {
            map = MapManager.gI().getMap(mapId, zone);
        }
        if (map != null) {
            if (typeSpace == TENNIS_SPACE_SHIP) {
                pl.point.updateall();
            }
            boolean isDrop = false;
            for(Map m : NRNM.gI().Maps){
                if(m.id == map.map.id){
                    isDrop = true;
                }
            }
            if(pl.idNRNM != -1 && !isDrop){
                NRNM.gI().SetPlayer(null, pl.idNRNM);
                pl.idNRNM = -1;
                Service.gI().sendBag(pl);
            }
            pl.setUseSpaceShip(typeSpace);
            pl.x = x != -1 ? x : Util.nextInt(100, map.map.mapWidth - 100);
            pl.y = 5;
            spaceShipArrive(pl, typeSpace);
            pl.gotoMap(map);
            Service.gI().clearMap(pl);
            Service.gI().mapInfo(pl); //-24
            MapService.gI().joinMap(pl, map);
            MapService.gI().loadAnotherPlayers(pl, pl.zone);
            pl.setUseSpaceShip((byte) 0);
        }
    }

    public void changeMap(Player pl, int mapId, int zoneId, int x, int y, byte typeSpace) {
        Map maps = MapManager.gI().getMapById(mapId);
        if(maps == null){
            Service.gI().sendThongBao(pl, "Có lỗi xảy ra khi load map");
            return;
        }
        if (!this.listMapCanChange(pl).stream().anyMatch(id -> maps.id == id) && pl.role < 99) {
            Service.gI().sendPopUpMultiLine(pl,0, 7184, "Không thể vào map");
            return;
        }
        Zone map = null;
        if (zoneId == -1) {
            for (Zone m : maps.map_zone) {
                if (m.getPlayers().size() <= 7) {
                    map = m;
                    break;
                }
            }
        } else {
            map = MapManager.gI().getMap(mapId, zoneId);
        }
        if (map != null) {
            pl.setUseSpaceShip(typeSpace);
            PVP.gI().finishPVP(pl, PVP.TYPE_LEAVE_MAP);
            pl.x = x != -1 ? x : Util.nextInt(100, map.map.mapWidth - 100);
            pl.y = y;
            pl.gotoMap(map);
            boolean isDrop = false;
            for(Map m : NRNM.gI().Maps){
                if(m.id == map.map.id){
                    isDrop = true;
                }
            }
            if(pl.idNRNM != -1 && !isDrop){
                NRNM.gI().SetPlayer(null, pl.idNRNM);
                pl.idNRNM = -1;
                Service.gI().sendBag(pl);
            }
            Service.gI().clearMap(pl);
            Service.gI().mapInfo(pl);
            MapService.gI().joinMap(pl, map);
            MapService.gI().loadAnotherPlayers(pl, pl.zone);
            pl.setUseSpaceShip((byte) 0);
        }
    }

    public void changeMap(Player pl, Zone map, int x, int y) {
        if (map != null) {
            Service.gI().clearMap(pl);
            boolean isDrop = false;
            for(Map m : NRNM.gI().Maps){
                if(m.id == map.map.id){
                    isDrop = true;
                }
            }
            if(pl.idNRNM != -1 && !isDrop){
                NRNM.gI().SetPlayer(null, pl.idNRNM);
                pl.idNRNM = -1;
                Service.gI().sendBag(pl);
            }
            pl.gotoMap(map);
            pl.x = x != -1 ? x : Util.nextInt(100, map.map.mapWidth - 100);
            pl.y = y;
            Service.gI().mapInfo(pl);
            if(pl.role >= 99){
                Service.gI().sendEffPlayer(pl, pl, 58, 1, -1, 1);
            }
            if(map.isNRSD() && pl.isPl()){
                int flagID = Util.nextInt(1, 7);
                if(pl.clan != null){
                    for(Player player : pl.zone.players){
                        if(player.clan != null && pl != player && player.clan.id == pl.clan.id){
                            flagID = player.cFlag;
                            break;
                        }
                    }
                }
                Service.gI().chooseFlag(pl, flagID, true);
            }
        }
    }

    public void changeMap(Player pl, int mapID, int x, int y) {
        Map maps = MapManager.gI().getMapById(mapID);
        if(maps != null){
            if (!this.listMapCanChange(pl).stream().anyMatch(id -> maps.id == id) && pl.role < 99) {
                Map mapk = MapManager.gI().getMapById(21 + pl.gender);
                changeMap(pl, mapk.map_zone[0], mapk.pxw / 2, 5);
                return;
            }
            if (MapManager.gI().isMapOffline(mapID)) {
                changeMap(pl, maps.map_zone[0], x, y);
                return;
            }
            Zone map = null;
            for (Zone m : maps.map_zone) {
                if (m.getPlayers().size() <= 7) {
                    map = m;
                    break;
                }
            }
            if (map == null) {
                for (Zone m : maps.map_zone) {
                    if (m.getPlayers().size() < 15) {
                        map = m;
                        break;
                    }
                }
            }
            if (map == null) {
                map = maps.map_zone[Util.nextInt(0, maps.map_zone.length - 1)];
            }
            changeMap(pl, map, x, y);
        }
    }
    
    //chỉ dùng cho đại hội võ thuật
    public void changeMapDHVT(Player pl, Player pl2, int mapID, int x, int y) {
        Map maps = MapManager.gI().getMapById(mapID);
        if(maps != null){
            Zone map = null;
            if (map == null) {
                for (Zone m : maps.map_zone) {
                    if (m.getPlayers().size() == 0) {
                        map = m;
                        break;
                    }
                }
            }
            if (map == null) {
                map = maps.map_zone[Util.nextInt(0, maps.map_zone.length - 1)];
            }
            if (pl.idNRSD != -1) {
                pl.idNRSD = -1;
                Service.gI().sendBag(pl);
            }
            if (pl2.idNRSD != -1) {
                pl2.idNRSD = -1;
                Service.gI().sendBag(pl2);
            }
            changeMap(pl, map, x, y);
            changeMap(pl2, map, x + 100, y);
        }
    }
    
    //chỉ dùng cho boss
    public synchronized void changeMapBySpaceShip(Player pl, Zone mapJoin, byte typeSpace) {
        if (mapJoin != null) {
            pl.setUseSpaceShip(typeSpace);
            pl.x = Util.nextInt(100, mapJoin.map.mapWidth-100);
            pl.y = 5;
            spaceShipArrive(pl, typeSpace);
            pl.gotoMap(mapJoin);
            MapService.gI().joinMap(pl, mapJoin);
            pl.setUseSpaceShip((byte)0);
        }
    }
    
    //chỉ dùng cho boss
    public void changeMapDHVT23(Player pl, Zone zone, int x, int y) {
        if (zone != null) {
            pl.x = x;
            pl.y = y;
            pl.gotoMap(zone);
            MapService.gI().joinMap(pl, zone);
        }
    }
    
    public Integer[] listMapXd = {39,44,23,14,15,16,26,17,18,20,19,35,36,37,38,85,86,87,88,89,90,91,52,113,126};
    public Integer[] listMapNm = {40,43,22,7,8,9,25,11,12,13,10,31,32,33,34};
    public Integer[] listMapTd = {41,42,21,0,1,2,24,3,4,5,6,27,28,29,30,47,46,45,48,112,84,50};
    
    public Integer[] listMapTL = {102,92,93,94,96,97,98,99,100,103};
    public Integer[] listMapNappa = {68,69,70,71,72,64,65,63,66,67,73,74,75,76,77,81,82,83,79,80};
    public Integer[] listMapCold = {109,108,107,110,106,105};
    
    public Integer[] listMapDoanhTrai = {53,58,59,60,61,62,55,56,54,57,147,148,149,151,152,153};
    public Integer[] listMapBDKB = {135,136,137,138};
    public Integer[] listMapMABU = {127,128};
    public Integer[] listMapOSIN = {114,115,117,118,119,120};
    public Integer[] listMapYARDART = {131,132,133};
    public Integer[] listMapCDRD = {141,142,143,144};
    public Integer[] listMapBONGTAI = {156,157,158,159};
    public Integer[] listHTNT = {154,155};
    
    public Integer[] listHTTV = {160,161,162,163};
    
    public List<Integer> listMapCanChange(Player pl){
        List<Integer> listMapChange = new ArrayList<>();
        List<Integer> listMapXD = Arrays.asList(listMapXd);
        List<Integer> listMapTD = Arrays.asList(listMapTd);
        List<Integer> listMapNM = Arrays.asList(listMapNm);
        
        List<Integer> listMapTl = Arrays.asList(listMapTL);
        List<Integer> listMapNP = Arrays.asList(listMapNappa);
        List<Integer> listMapCD = Arrays.asList(listMapCold);
        
        List<Integer> listMapDT = Arrays.asList(listMapDoanhTrai);
        List<Integer> listMapBD = Arrays.asList(listMapBDKB);
        List<Integer> listMapMB = Arrays.asList(listMapMABU);
        List<Integer> listMapOS = Arrays.asList(listMapOSIN);
        List<Integer> listMapRD = Arrays.asList(listMapCDRD);
        List<Integer> listMapBT = Arrays.asList(listMapBONGTAI);
        List<Integer> listMapYD = Arrays.asList(listMapYARDART);
        
        
        List<Integer> listMapNT = Arrays.asList(listHTNT);
        List<Integer> listMapTV = Arrays.asList(listHTTV);
        
        switch (pl.taskId) {
            case 1:
                switch (pl.gender) {
                    case 0:
                        listMapChange.addAll(Util.getValue(listMapTD,0, 3));
                        break;
                    case 1:
                        listMapChange.addAll(Util.getValue(listMapNM,0, 3));
                        break;
                    case 2:
                        listMapChange.addAll(Util.getValue(listMapXD,0, 3));
                        break;
                }
                break;
            case 2:
            case 3:
                switch (pl.gender) {
                    case 0:
                        listMapChange.addAll(Util.getValue(listMapTD,0, 4));
                        break;
                    case 1:
                        listMapChange.addAll(Util.getValue(listMapNM,0, 4));
                        break;
                    case 2:
                        listMapChange.addAll(Util.getValue(listMapXD,0, 4));
                        break;
                }
                break;
            case 4:
                listMapChange.addAll(Util.getValue(listMapNM,0, 6));
                listMapChange.addAll(Util.getValue(listMapXD,0, 6));
                listMapChange.addAll(Util.getValue(listMapTD,0, 6));
                break;
            case 5:
                listMapChange.addAll(Util.getValue(listMapNM,0, 7));
                listMapChange.addAll(Util.getValue(listMapXD,0, 7));
                listMapChange.addAll(Util.getValue(listMapTD,0, 7));
                break;
            case 6:
                listMapChange.addAll(Util.getValue(listMapNM,0, 8));
                listMapChange.addAll(Util.getValue(listMapXD,0, 8));
                listMapChange.addAll(Util.getValue(listMapTD,0, 8));
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                break;
            case 17:
            case 18:
            case 19:
            case 20:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll( Util.getValue(listMapBT,0, 1));
                break;
            case 21:
            case 22:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(Util.getValue(listMapTl,0, 3));
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll( Util.getValue(listMapBT,0, 1));
                listMapChange.addAll(listMapYD);
                break;
            case 23:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(Util.getValue(listMapTl,0, 3));
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll( Util.getValue(listMapBT,0, 1));
                listMapChange.addAll(listMapYD);
                listMapChange.add(104);
                break;
            case 24:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(Util.getValue(listMapTl,0, 7));
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll( Util.getValue(listMapBT,0, 1));
                listMapChange.addAll(listMapYD);
                break;
            case 25:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(Util.getValue(listMapTl,0, 8));
                listMapChange.addAll(listMapCD);
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapNT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll( Util.getValue(listMapBT,0, 1));
                listMapChange.addAll(listMapYD);
                break;
            case 26:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(listMapTl);
                listMapChange.addAll(listMapCD);
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapNT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll(listMapBT);
                listMapChange.addAll(listMapYD);
                listMapChange.add(104);
                break;
            case 27:
                
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
                listMapChange.addAll(listMapNM);
                listMapChange.addAll(listMapXD);
                listMapChange.addAll(listMapTD);
                listMapChange.addAll(listMapNP);
                listMapChange.addAll(listMapTl);
                listMapChange.addAll(listMapCD);
                listMapChange.addAll(listMapDT);
                listMapChange.addAll(listMapTV);
                listMapChange.addAll(listMapNT);
                listMapChange.addAll(listMapBD);
                listMapChange.addAll(listMapMB);
                listMapChange.addAll(listMapOS);
                listMapChange.addAll(listMapRD);
                listMapChange.addAll(listMapBT);
                listMapChange.addAll(listMapYD);
                break;
        }
        return listMapChange;
    }
    
    public void changeMapWaypoint(Player player)
    {
        try
        {
            if (player != null && (player.isDie() || !Util.canDoWithTime(player.currNextMap, 200))) {
                Service.gI().resetPoint(player, player.x - 50, player.y);
                return;
            }
            player.currNextMap = System.currentTimeMillis();
            WayPoint wp = player.isInWaypoint();
            if (wp != null) {
                if (wp.goMap == 111) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    return;
                }
                else if (wp.goMap == 93) {
                    if(player.taskId == 25 && player.taskIndex == 0)
                    {
                        Service.gI().send_task_next(player);
                    }              
                }
                else if (wp.goMap == 104) {
                    if(player.taskId == 26 && player.taskIndex == 0)
                    {
                        Service.gI().send_task_next(player);
                    }                 
                }
                else if (wp.goMap == 97) {
                    if(player.taskId == 27 && player.taskIndex == 0)
                    {
                        Service.gI().send_task_next(player);
                    }                 
                }
                else if (wp.goMap == 100) {
                    if(player.taskId == 28 && player.taskIndex == 0)
                    {
                        Service.gI().send_task_next(player);
                    }                 
                }
                else if (wp.goMap == 103) {
                    if(player.taskId == 29 && player.taskIndex == 2)
                    {
                        Service.gI().send_task_next(player);
                    }                 
                }
                else if (wp.goMap ==109|| wp.goMap ==108|| wp.goMap ==107|| wp.goMap ==110|| wp.goMap ==106|| wp.goMap ==105)
                {
                
                if(player.taskId < 28 )
                    {
                     Service.gI().sendThongBao(player, "Không thể thực hiện");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    return;
                    }  
                }
                
                else if (!this.listMapCanChange(player).stream().anyMatch(id -> wp.goMap == id)) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    return;
                }
                else if ((wp.goMap == 21 ||wp.goMap == 22||wp.goMap == 23) && wp.goMap != (player.gender + 21)) {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    return;
                }
                else if (wp.isMapDTDN() && (getMobDie(player.zone) < player.zone.mobs.length || player.zone.players.stream().filter(p -> p.isBoss).count() > 0)) {
                    Service.gI().sendThongBao(player, "Chưa hạ hết đối thủ");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    return;
                }
                else if (wp.isMapKGHD()&& (getMobDie(player.zone) < player.zone.mobs.length || player.zone.players.stream().filter(p -> p.isBoss).count() > 0)) {
                    Service.gI().sendThongBao(player, "Chưa hạ hết đối thủ");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    return;
                }
                else if (wp.isMapBDKB()&& (getMobDie(player.zone) < player.zone.mobs.length || player.zone.players.stream().filter(p -> p.isBoss).count() > 0)) {
                    Service.gI().sendThongBao(player, "Chưa hạ hết đối thủ");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                    
                    return;
                }
                if(wp.isMapDTDN()){
                    if(player.clan.DoanhTrai == null){
                        changeMap(player , player.gender + 21, -1, -1, 5,ChangeMap.getSpaceShip(player));
                        return;
                    }
                    player.clan.DoanhTrai.setHPMob_DoanhTrai(MapManager.gI().getMap(wp.goMap, player.clan.DoanhTrai.zonePhuBan));
                    changeMap(player , (int)wp.goMap, player.clan.DoanhTrai.zonePhuBan, (int)wp.goX, (int)wp.goY,ChangeMap.NON_SPACE_SHIP);
                }
                else if(wp.isMapBDKB()){
                    if(player.clan.KhoBauDuoiBien == null){
                        changeMap(player , player.gender + 21, -1, -1, 5,ChangeMap.getSpaceShip(player));
                        return;
                    }
                    changeMap(player , (int)wp.goMap, player.clan.KhoBauDuoiBien.zonePhuBan, (int)wp.goX, (int)wp.goY,ChangeMap.NON_SPACE_SHIP);
                }
                else if(wp.isMapCDRD()){
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    Service.gI().resetPoint(player, player.x - (wp.maxX < 50 ? -50 : 50), player.y);
                }
                else if(wp.isMapKGHD()){
                    if(player.clan.KhiGasHuyDiet == null){
                        changeMap(player , player.gender + 21, -1, -1, 5,ChangeMap.getSpaceShip(player));
                        return;
                    }
                    else if(player.start == -1){
                        player.type = 4;
                        player.maxTime = 5;
                        player.start = 1;
                        Service.gI().Transport(player, 1);
                        Service.gI().clearMap(player);
                    }
                    else{
                        player.start = -1;
                        changeMap(player , (int)wp.goMap, player.clan.KhiGasHuyDiet.zonePhuBan, (int)wp.goX, (int)wp.goY,ChangeMap.NON_SPACE_SHIP);
                    }
                }
                else{
                    changeMap(player, wp.goMap,wp.goX, wp.goY);
                }
            }
        }catch(Exception e){
            Util.debug("changeMapWaypoint");
            e.printStackTrace();
        }
    }

    public int getMobDie(Zone zone){
        int num = 0;
        for(int i = 0 ; i < zone.mobs.length ;i++){
            if(zone.mobs[i] != null && zone.mobs[i].isDie()){
                num++;
            }
        }
        return num;
    }
    
    public void finishLoadMap(Player player) {
        if (player.zone.map.id == 21 + player.gender) {
            player.magicTree.displayMagicTree(player);
        }
        MapService.gI().joinMap(player, player.zone);
        MapService.gI().loadAnotherPlayers(player, player.zone);
        sendEffectMapToMe(player);
        sendEffectMeToMap(player);
        if(player.loader == 0){
            String content = "|7|CHÚC " + player.name + " CHƠI GAME VUI VẺ!";
            Service.gI().sendPopUpMultiLine(player, 0, 1139, content);
            Service.gI().sendThongBao(player, Service.gI().NpcTraTask(player, TaskData.getTask(player.taskId).subNames[player.taskIndex]));
        }
        SummonDragon.gI().sendWhishesShenron(player);
    }

    private void sendEffectMeToMap(Player player) {
        Message msg = null;
        try {
            for (Mob mob : player.zone.mobs) {
                if (mob.isDie()) {
                    continue;
                }
                if (mob.isThoiMien) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1); //b5
                    msg.writer().writeByte(1); //b6
                    msg.writer().writeByte(41); //num6
                    msg.writer().writeByte(mob.id); //b7
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.isSocola) {
                    msg = new Message(-112);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(mob.id); //b4
                    msg.writer().writeShort(4133);//b5
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.isBlindDCTT) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(40);
                    msg.writer().writeByte(mob.id);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.isStun) {
                    msg = new Message(-45);
                    msg.writer().writeByte(0);
                    msg.writer().writeInt(player.id);
                    msg.writer().writeShort(42);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(mob.id);
                    int time = (int)(mob.lastTimeStun - System.currentTimeMillis() + mob.timeStun) / 1000;
                    msg.writer().writeByte(time);
                    msg.writer().writeByte(0);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {

        }
        try {
            if (player.playerSkill.isShielding) {
                msg = new Message(-124);
                msg.writer().writeByte(1);
                msg.writer().writeByte(0);
                msg.writer().writeByte(33);
                msg.writer().writeInt(player.id);
                Service.gI().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }

            if (player.mobMe != null) {
                msg = new Message(-95);
                msg.writer().writeByte(0);//type
                msg.writer().writeInt(player.id);
                msg.writer().writeShort(player.mobMe.template.mobTemplateId);
                msg.writer().writeInt(player.mobMe.gethp());// hp mob
                Service.gI().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }
            if (player.pet != null && player.pet.mobMe != null) {
                msg = new Message(-95);
                msg.writer().writeByte(0);//type
                msg.writer().writeInt(player.pet.mobMe.id);
                msg.writer().writeShort(player.pet.mobMe.template.mobTemplateId);
                msg.writer().writeInt(player.pet.mobMe.gethp());// hp mob
                Service.gI().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
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

    private void sendEffectMapToMe(Player player) {
        Message msg = null;
        try {
            for (Player pl : player.zone.players) {
                if(player.equals(pl)){
                    continue;
                }
                if (pl.playerSkill.isShielding) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(33);
                    msg.writer().writeInt(pl.id);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (pl.playerSkill.isThoiMien) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1); //b5
                    msg.writer().writeByte(0); //b6
                    msg.writer().writeByte(41); //num3
                    msg.writer().writeInt(pl.id); //num4
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (pl.playerSkill.isBlindDCTT) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(40);
                    msg.writer().writeInt(pl.id);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(32);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                
                if (pl.playerSkill.isStun) {
                    msg = new Message(-45);
                    msg.writer().writeByte(0);
                    msg.writer().writeInt(player.id);
                    msg.writer().writeShort(42);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(1);
                    msg.writer().writeInt(pl.id);
                    int time = (int)(pl.playerSkill.lastTimeStartStun - System.currentTimeMillis() + pl.playerSkill.timeStun) / 1000;
                    msg.writer().writeByte(time);
                    player.sendMessage(msg);
                    msg.cleanup();
                }

                if (pl.playerSkill.useTroi) {
                    if (pl.playerSkill.plAnTroi != null) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(0);//b6
                        msg.writer().writeByte(32);//num3
                        msg.writer().writeInt(pl.playerSkill.plAnTroi.id);//num4
                        msg.writer().writeInt(pl.id);//num9
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                    if (pl.playerSkill.mobAnTroi != null) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b4
                        msg.writer().writeByte(1);//b5
                        msg.writer().writeByte(32);//num8
                        msg.writer().writeByte(pl.playerSkill.mobAnTroi.id);//b6
                        msg.writer().writeInt(pl.id);//num9
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                }

                if (pl.mobMe != null) {
                    msg = new Message(-95);
                    msg.writer().writeByte(0);//type
                    msg.writer().writeInt(pl.id);
                    msg.writer().writeShort(pl.mobMe.template.mobTemplateId);
                    msg.writer().writeInt(pl.mobMe.gethp());// hp mob
                    player.sendMessage(msg);
                    msg.cleanup();
                }
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

    public void spaceShipArrive(Player player, byte typeSpace) {
        Message msg = null;
        try {
            msg = new Message(-65);
            msg.writer().writeInt(player.id);
            msg.writer().writeByte(typeSpace);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
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
