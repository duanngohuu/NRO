package real.clan;

import java.util.ArrayList;
import java.util.List;
import service.Setting;

public class ClanManager {

    private static List<Clan> clans;
    public static List<ClanImage> clanImages;
    
    private static ClanManager instance;

    public static ClanManager gI() {
        if (instance == null) {
            instance = new ClanManager();
        }
        return instance;
    }

    public void init() {
        this.clans = ClanDAO.load();
        this.clanImages = ClanDAO.loadClanImage();
    }

    public void addClan(Clan clan) {
        if (!this.clans.contains(clan)) {
            this.clans.add(clan);
        }
    }
    
    public void removeClan(int id) {
        for (Clan c : this.clans) {
            if (c.id == id) {
                clans.remove(c);
                break;
            }
        }
    }
    
    public void removeMember(int id, Clan cl) {
        for (Member m : cl.members) {
            if (m.id == id) {
                cl.members.remove(m);
                break;
            }
        }
    }

    public Member getMember(Clan cl, int id) {
        for (Member m : cl.members) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }

    public Clan getClanById(int id) {
        for (Clan clan : this.clans) {
            if (clan.id == id) {
                return clan;
            }
        }
        return null;
    }
    
    public Clan getClanByName(String name) {
        for (Clan clan : this.clans) {
            if (clan.name.equals(name)) {
                return clan;
            }
        }
        return null;
    }

    public List<Clan> search(String text) {
        try {
            return clans.stream().limit(20).filter(c -> c!= null && c.name.contains(text)).toList();
        } catch (Exception e) {
        }
        return new ArrayList<Clan>();
    }
    
    public static List<ClanImage> getClanImgs() {
        List<ClanImage> listClan = new ArrayList<>();
        try {
            clanImages.stream().filter(c -> c != null && c.id != -1 && c.type != 1).forEach(cl -> listClan.add(cl));
        } catch (Exception e) {
        }
        return listClan;
    }

    public List<Member> getMemberByIdClan(int id) {
        for (Clan clan : this.clans) {
            if (clan.id == id) {
                return clan.members;
            }
        }
        return null;
    }
    
    public int getZone(){
        for(int i = 0; i < Setting.ZONE_PHU_BAN; i++){
            int zoneID = i;
            int num = clans.stream().filter(cl -> cl != null && cl.DoanhTrai != null && cl.DoanhTrai.time > 0 && cl.DoanhTrai.zonePhuBan == zoneID).toList().size();
            if(num <= 0){
                return i;
            }
        }
        return -1;
    }
    
    public int getZone_KhiGas(){
        for(int i = 0; i < Setting.ZONE_PHU_BAN; i++){
            int zoneID = i;
            int num = clans.stream().filter(cl -> cl != null && cl.KhiGasHuyDiet != null && cl.KhiGasHuyDiet.time > 0 && cl.KhiGasHuyDiet.zonePhuBan == zoneID).toList().size();
            if(num <= 0){
                return i;
            }
        }
        return -1;
    }
    
    public int getZone_KHOBAU(){
        for(int i = 0; i < Setting.ZONE_PHU_BAN; i++){
            int zoneID = i;
            int num = clans.stream().filter(cl -> cl != null && cl.KhoBauDuoiBien != null && cl.KhoBauDuoiBien.time > 0 && cl.KhoBauDuoiBien.zonePhuBan == zoneID).toList().size();
            if(num <= 0){
                return i;
            }
        }
        return -1;
    }
    
    public List<Clan> getClans(){
        return clans;
    }
}
