package real.clan;

import real.map.Mob;
import real.map.Zone;
import server.Util;

public class PhuBan {
    public int clanID;
    public int id;
    public int time;
    public long timeStart;
    public long timeJoin;
    public int zonePhuBan;
    public int hpMobPlus;
    public int level = -1;
    public boolean isDropItem = false;
    
    public PhuBan(int clanID, int id, int time, long timeJoin, long timeStart, int zonePhuBan, int hpMobPlus, int level){
        this.clanID = clanID;
        this.id = id;
        this.time = time;
        this.timeJoin = timeJoin;
        this.zonePhuBan = zonePhuBan;
        this.hpMobPlus = hpMobPlus;
        this.timeStart = timeStart;
        this.level = level;
    }
    
    public void setHPMob(Zone zone){
        try{
        int size = zone.mobs.length;
        for(int i = 0 ; i < size;i++){
            Mob mob = zone.mobs[i];
            if(mob != null && mob.map.zoneId == zonePhuBan){
                long hpPlus = mob.template.hp + hpMobPlus + mob.template.hp * (Util.nextInt(0, 1) == 1 ? 10 : -10) / 100;
                if(hpPlus > Integer.MAX_VALUE){
                    hpPlus = Integer.MAX_VALUE;
                }
                hpPlus = Math.abs(hpPlus);
                mob.dame = (int)hpPlus / 10;
                mob.hp = (int)hpPlus;
                mob.setHpFull((int)hpPlus);
                mob.sethp((int)hpPlus);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setHPMob_DoanhTrai(Zone zone){
        try{
        int size = zone.mobs.length;
        for(int i = 0 ; i < size;i++){
            Mob mob = zone.mobs[i];
            if(mob != null && !mob.isDie() && mob.map.zoneId == zonePhuBan){
                long hpPlus = mob.template.hp + hpMobPlus + mob.template.hp * (Util.nextInt(0, 1) == 1 ? 10 : -10) / 100;
                if(hpPlus > Integer.MAX_VALUE){
                    hpPlus = Integer.MAX_VALUE;
                }
                hpPlus = Math.abs(hpPlus);
                mob.setDame((int)hpPlus / 10);
                mob.setHpFull((int)hpPlus);
                mob.sethp((int)hpPlus);
            }
        }
        } catch (Exception e) {
            Util.debug("setHPMob_DoanhTrai");
            e.printStackTrace();
        }
    }
    
    public void setHPMob_KhiGas(Zone zone){
        try{
        int size = zone.mobs.length;
        int mob1 = size - 1;
        int mob2 = -1;
        if(size / 5 > 1){
            mob2 = size - 3;
        }
        for(int i = 0; i < size; i++){
            Mob mob = zone.mobs[i];
            if(mob != null && mob.map.zoneId == zonePhuBan){
                long HPPlus = mob.template.hp * level * (hpMobPlus > 0 ? hpMobPlus : 1);
                if(mob1 == i){
                    HPPlus *= hpMobPlus;
                    HPPlus *= (hpMobPlus >= 10 ? 2 : 1);
                }
                else if(mob2 == i){
                    HPPlus *= hpMobPlus;
                    HPPlus *= (hpMobPlus >= 10 ? 2 : 1);
                }
                mob.setDame((int)HPPlus / 10);
                mob.setHpFull((int)HPPlus);
                mob.sethp((int)HPPlus);
            }
        }
        } catch (Exception e) {
            Util.debug("setHPMob_KhiGas");
            e.printStackTrace();
        }
    }
    
    public void setHPMob_KhoBau(Zone zone){
        try{
        int size = zone.mobs.length;
        int mob1 = size - 1;
        int mob2 = -1;
        if(size / 5 > 1){
            mob2 = size - 3;
        }
        for(int i = 0; i < size; i++){
            Mob mob = zone.mobs[i];
            if(mob != null && mob.map.zoneId == zonePhuBan){
                long HPPlus = mob.template.hp * level * (hpMobPlus > 0 ? hpMobPlus : 1);
                if(mob1 == i){
                    HPPlus *= hpMobPlus;
                    HPPlus *= (hpMobPlus >= 10 ? 2 : 1);
                }
                else if(mob2 == i){
                    HPPlus *= hpMobPlus;
                    HPPlus *= (hpMobPlus >= 10 ? 2 : 1);
                }
                mob.cx = mob.pointX;
                mob.setDame((int)HPPlus / 10);
                mob.setHpFull((int)HPPlus);
                mob.sethp((int)HPPlus);
            }
        }
        } catch (Exception e) {
            Util.debug("setHPMob_KhoBau");
            e.printStackTrace();
        }
    }
}
