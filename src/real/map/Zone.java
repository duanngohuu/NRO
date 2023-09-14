package real.map;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.sql.Timestamp;
import real.npc.Npc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import real.boss.BossManager;
import real.func.DHVT;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.player.Player;
import server.Service;
import server.Util;
import service.Setting;

public class Zone {

    public List<Player> players;
    public List<ItemMap> items;
    public WayPoint[] wayPoints;
    public Npc[] npcs;
    public Mob[] mobs;
    public final int zoneId;
    public final Map map;
    public boolean zoneDoneNRSD;
    public boolean zoneDoneBOSS_1;
    public boolean zoneDoneBOSS_2;
    public boolean zoneDoneKGHD;
    public boolean zoneDoneDHVT;
    public long currMOVE_NRSD;
    
    public Zone(int z, Map m) {
        zoneId = z;
        this.players = new ArrayList<>();
        this.items = new ArrayList<>();
        map = m;
    }

    public boolean checkNpc(int id){
        for(int i = 0 ; i < this.npcs.length ;i++){
            Npc npc = this.npcs[i];
            if(npc != null && npc.tempId == id){
                return true;
            }
        }
        return false;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public int getNumPlayerInMap() {
        int count = 0;
        for (int i = 0 ; i < getPlayers().size() ;i++) {
            Player pl = getPlayers().get(i);
            if (pl != null && pl.isPl()) {
                count++;
            }
        }
        return count;
    }

    public synchronized void ChatAllPlayerInZone(Player me, String text) {
        for (Player pl : getPlayers()) {
            if (pl!=null && me !=null && pl != me && pl.zone.zoneId == me.zone.zoneId && !pl.isBoss&& !pl.isNewPet && !me.isPet && !pl.isDie() && !me.isDie()) {
                Service.gI().chat(pl, text);
            }
        }
    }
    
    public int dameZone(Player me) {
        int param = 0;
        List<Player> listPlayer = getPlayers().stream().filter(pl -> pl.inventory.OptionCt(117) && Util.getDistance(me, pl) <= 200).toList();
        for(Player pl : listPlayer){
            int zparam = pl.point.get_percent_option_ct(117);
            if(param < zparam){
                param = zparam;
            }
        }
        return param;
    }

    public synchronized void SetHPMPAllPlayerInZone(Player me, int type) {
        for (Player pl : getPlayers()) {
            if (pl != me && !pl.isBoss && !pl.isNewPet && !pl.isDie() && !me.isDie() && Util.getDistance(me, pl) <= 200) {
                switch (type) {
                    case 1:
                        if(me.isPet && me.id + pl.id == 0){
                            break; 
                        }
                        Service.gI().chat(pl, "Ewww!Mấy năm hông tắm zị");
                        int HOI_HP = pl.point.getHPFull() / 10;
                        if(pl.point.hp <= HOI_HP){
                            HOI_HP = pl.point.hp - 1; 
                            if(pl.point.hp <= 1){
                                HOI_HP = 0;
                            }
                        }
                        if(HOI_HP != 0){
                            pl.hoi_hp(-HOI_HP);
                        }
                        break;
                    case 2:
                        if(me.isPet && me.id + pl.id == 0){
                            break; 
                        }
                        Service.gI().chat(pl, "Tránh ra di ông xinbato");
                        pl.playerSkill.setXinbato(System.currentTimeMillis(), 2000);
                        break;
                    case 3:
                        int p = me.point.get_percent_option_ct(162);
                        if(Util.isTrue(40)){
                            Service.gI().chat(pl, "Cute quá :3");
                        }
                        pl.hoi_ki(pl.point.getMPFull() / 100 * p);
                        break;
                    case 4:
                        int pe = me.point.get_percent_option_ct(8);
                        int HUT_HP = pl.point.getHPFull() / 100 * pe;
                        int HUT_MP = pl.point.getMPFull() / 100 * pe;
                        if(HUT_HP < pl.point.hp){
                            me.hoi_hp(HUT_HP);
                            pl.hoi_hp(-HUT_HP);
                        }
                        if(HUT_MP < pl.point.mp){
                            me.hoi_ki(HUT_MP);
                            pl.hoi_ki(-HUT_MP);
                        }
                        break;
                    case 5:
                        if(me.isPet && me.id + pl.id == 0){
                            break; 
                        }
                        pl.playerSkill.setSocola(System.currentTimeMillis(), 5000);
                        pl.playerSkill.countPem1hp = 10;
                        Service.gI().sendItemTime(pl, 4134, 5000 / 1000);
                        Service.gI().Send_Caitrang(pl);
                        break;
                    case 6:
                        if(me.isPet && me.id + pl.id == 0){
                            break; 
                        }
                        pl.playerSkill.setHoaDa(System.currentTimeMillis(), 5000);
                        pl.playerSkill.AnHoaDa(pl);
                        Service.gI().sendItemTime(pl, 4392, 5000 / 1000);
                        Service.gI().Send_Caitrang(pl);
                        break;
                    case 7:
                        if(me.isPet && me.id + pl.id == 0 || pl.inventory.OptionCt(117)){
                            break; 
                        }
                        Service.gI().chat(pl, "Bắn tim...biu biu");
                        break;
                    case 8:
                        if(me.isPet && me.id + pl.id == 0){
                            break; 
                        }
                        if(!pl.inventory.OptionCt(106)){
                            Service.gI().chat(pl, "Hic hic... Lạnh quá");
                            int HP = pl.point.getHPFull() / 100 * 10;
                            pl.hoi_hp(-HP);
                        }
                        break;
                }
            }
        }
    }
    
    public synchronized void SetMPHPAllMobInZone(Player pl, int type){
        for(int i = 0; i < mobs.length; i++){
            Mob mob = mobs[i];
            if(!mob.isDie() && Util.getDistance(pl, mob) <= 200){
                switch (type) {
                    case 4:
                        int pe = pl.point.get_percent_option_ct(8);
                        int HUT_HP = mob.hpMax / 100 * pe;
                        int HUT_MP = mob.hpMax / 100 * pe;
                        if(HUT_HP < pl.point.hp){
                            pl.hoi_hp(HUT_HP);
                            mob.hoi_hp(-HUT_HP);
                        }
                        if(HUT_MP < pl.point.mp){
                            pl.hoi_ki(HUT_MP);
                        }
                        break;
                }
            }
        }
    }

    public boolean isMapTL() {
        return map.id == 92 || map.id == 93 || map.id == 94 || map.id == 96 || map.id == 97 || map.id == 98 || map.id == 99 || map.id == 100;
    }
    
    public boolean isCooler() {
        return map.id >= 105 && map.id <= 110 || map.id == 152;
    }
    
    public boolean isHTTV() {
        return map.id >= 160 && map.id <= 163;
    }
    
    public boolean isOSIN() {
        return map.id >= 114 && map.id <= 120;
    }
    
    public boolean isNRSD() {
        return map.id >= 85 && map.id <= 91;
    }

    public boolean isMapDTDN() {
        return map.id >= 53 && map.id <= 62;
    }
    
    public boolean isMapBDKB() {
        return map.id >= 135 && map.id <= 138;
    }
    
    public boolean isMapKGHD() {
        return map.id >= 147 && map.id <= 152;
    }
    
    public boolean isMapCDRD() {
        return map.id >= 141 && map.id <= 144;
    }
    
    public boolean isYardart() {
        return map.id >= 131 && map.id <= 133;
    }
    
    public boolean isBongTai() {
        return map.id >= 156 && map.id <= 159;
    }
    
    public boolean isPhuBan(){
        return isMapDTDN() || isMapBDKB() || isMapKGHD() || isMapCDRD() || isOSIN() || (DHVT.gI().Hour == Setting.TIME_START_HIRU_1 || DHVT.gI().Hour == Setting.TIME_START_HIRU_2) && map.id == 126;
    }
    
    public int getNumPlayerInZone(Player me) {
        byte count = 0;
        for (Player pl : getPlayers()) {
            if (pl != me && !me.isPet && !pl.isBoss&& !pl.isNewPet && pl.zone.zoneId == me.zone.zoneId && !pl.isDie()) {
                count++;
            }
        }
        return count;
    }

    public long currChat;

    public synchronized void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public int getNumItem() {
        return items.size();
    }

    short countItemAppeaerd = 0;

    public void addItem(ItemMap itemMap) {
        itemMap.itemMapId = countItemAppeaerd;
        countItemAppeaerd++;
        if (countItemAppeaerd >= Short.MAX_VALUE) {
            countItemAppeaerd = 0;
        }
        items.add(itemMap);
    }
    public void removeItemMap(ItemMap itemMap) {
        this.items.remove(itemMap);
    }

    public void setItems(ArrayList<ItemMap> items) {
        this.items = items;
    }
    
    public ItemMap getItemInMap(int idItem) {
        for (ItemMap it : items) {
            if (it != null && it.itemTemplate.id == idItem) {
                return it;
            }
        }
        return null;
    }

    public Player getPlayerInMap(int idPlayer) {
        for (Player pl : players) {
            if (pl!=null&&pl.id == idPlayer) {
                return pl;
            }
        }
        return null;
    }
    
    public int getBossInMap(int zone) {
        int num = 0;
        if(zone == -1){
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player != null && player.isBoss) {
                    num++;
                }
            }
        }
        return num;
    }
    
    public int getMobInMap(int idMob) {
        int num = 0;
        for(Mob mob : mobs){
            if(mob != null && mob.template.mobTemplateId == idMob && !mob.isDie()){
                num++;
            }
        }
        return num;
    }
    
    public boolean getMobDie() {
        if(map.id != 148){
            return false;
        }
        int num = 0;
        for(Mob mob : mobs){
            if(mob != null && !mob.isDie()){
                return false;
            }
        }
        Player Taget = null;
        if(!this.zoneDoneBOSS_1 && !this.zoneDoneBOSS_2 && !zoneDoneKGHD){
            for(Player player : players){
                if(player != null && player.clan != null && player.clan.KhiGasHuyDiet != null){
                    Taget = player;
                }
            }
            if(Taget == null){
                return false;
            }
            this.zoneDoneBOSS_1 = true;
            Item item = ItemData.gI().get_item(738);
            item.itemOptions.clear();
            int level = Taget.clan.KhiGasHuyDiet.level;
            int Param = (int)(0.3 * level);
            int hsd = Util.nextInt(15, 30);
            if(Param < 3){
                Param = 3;
            }
            item.itemOptions.add(new ItemOption(77, Param));
            item.itemOptions.add(new ItemOption(103, Param));
            item.itemOptions.add(new ItemOption(50, Param));
            item.itemOptions.add(new ItemOption(94, Param));
            item.itemOptions.add(new ItemOption(93, hsd));
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            item.buyTime = timenow.getTime();
            int HPmax = 10000000 * Taget.clan.KhiGasHuyDiet.level;
            BossManager.addBoss("Dr Lychee", 742, 743, 744, 148, this.zoneId, item, HPmax, HPmax / 100);
        }
        else if(this.zoneDoneBOSS_2 && this.zoneDoneBOSS_1 && !zoneDoneKGHD){
            for(Player player : players){
                if(player != null && player.clan != null && player.clan.KhiGasHuyDiet != null){
                    Taget = player;
                }
            }
            if(Taget == null){
                return false;
            }
            this.zoneDoneBOSS_1 = false;
            Item item = ItemData.gI().get_item(729);
            item.itemOptions.clear();
            int level = Taget.clan.KhiGasHuyDiet.level;
            int Param = (int)(0.33 * level);
            int hsd = Util.nextInt(15, 30);
            if(Param < 3){
                Param = 3;
            }
            item.itemOptions.add(new ItemOption(77, Param));
            item.itemOptions.add(new ItemOption(103, Param));
            item.itemOptions.add(new ItemOption(50, Param));
            item.itemOptions.add(new ItemOption(5, Param));
            item.itemOptions.add(new ItemOption(93, hsd));
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            item.buyTime = timenow.getTime();
            int HPmax = 10000000 * Taget.clan.KhiGasHuyDiet.level;
            BossManager.addBoss("Hatchiyack", 639, 640, 641, 148, this.zoneId, item, HPmax, HPmax / 100);
        }
        return true;
    }
    
    public int findY(int x, int y) {
        int rX = (int) x / Map.SIZE;
        int rY = -1;
        if (map.tileMap[y / Map.SIZE][rX] != 0) {
            return y;
        }
        for (int i = y / Map.SIZE; i < map.tileMap.length; i++) {
            int type = map.tileMap[i][rX];
            if (type != 0 && type != 3 &&  type != 7 && type != 8 && type != 10 && type != 28 && type != 29) {
                rY = i * Map.SIZE;
                break;
            }
        }
        return rY;
    }
    
    public int LastY(int cx, int cy){
        int num = 0;
        int ySd = 0;
        int xSd = cx;
        if (map.tileTypeAt(cx, cy, 2))
	{
            return cy;
        }
        while (num < 30)
	{
            num++;
            ySd += 24;
            if (map.tileTypeAt(xSd, ySd, 2))
            {
		if (ySd % 24 != 0)
		{
                    ySd -= ySd % 24;
		}
		break;
            }
	}
        return ySd;
    }
    
    public int[] MoveXY(Player player) {
        int xsd = player.x / 24;
        int ysd = player.y / 24;
        int p = this.map.id == 103 ? 4 : 3;
        if(map.tileMap[ysd][xsd] != 0){
            if(map.tileMap[ysd - p][xsd] != 0){
                if(map.tileMap[LastY(player.x, player.y - p * 24) / 24][xsd] != 0){
                    return new int[]{
                        player.xSend,
                        player.ySend
                    };
                }
                else{
                    return new int[]{
                        player.xSend,
                        LastY(player.x, 120)
                    };
                }
            }
            return new int[]{
                player.x,
                ysd
            };
        }
        if(LastY(player.x, player.y) >= map.pxh - 24){
            return new int[]{
                player.xSend,
                player.ySend
            };
        }
        return new int[]{
            player.x,
            ysd
        };
    }
    
    public boolean isWayPoint(Player player){
        for(int i = 0; i < wayPoints.length; i++){
            if(player.x > map.pxw - 35)
            {
                if(map.pxw - 35 < wayPoints[i].minX && wayPoints[i].minY < player.y){
                    return true;
                }
            }
            else if(player.x < 35)
            {
                if(35 > wayPoints[i].minX && wayPoints[i].minY < player.y){
                    return true;
                } 
            }
        }
        return false;
    }
}
