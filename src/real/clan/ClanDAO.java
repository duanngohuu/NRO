package real.clan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import real.player.Player;
import server.SQLManager;
import server.Util;
import server.io.JSON;
import service.Setting;

public class ClanDAO {

//    public static void Update_Clan(Player pl) {
//        Clan clan = pl.clan;
//        try {
//            JSONArray arr = new JSONArray();
//            for (Member mem : clan.members) {
//                arr.put(JSON.MemberToJson(mem));
//            }
//            JSONArray arr2 = new JSONArray();
//            if(pl.clan.DoanhTrai != null){
//                JSONObject obj = new JSONObject();
//                obj.put("id", clan.DoanhTrai.id);
//                obj.put("time", clan.DoanhTrai.time);
//                obj.put("timeStart", clan.DoanhTrai.timeStart);
//                obj.put("timeJoin", clan.DoanhTrai.timeJoin);
//                obj.put("zone", clan.DoanhTrai.zonePhuBan);
//                obj.put("hp", clan.DoanhTrai.hpMobPlus);
//                obj.put("level", clan.DoanhTrai.level);
//                arr2.put(obj);
//            }
//            JSONArray arr3 = new JSONArray();
//            if(pl.clan.KhiGasHuyDiet != null){
//                JSONObject obj = new JSONObject();
//                obj.put("id", clan.KhiGasHuyDiet.id);
//                obj.put("time", clan.KhiGasHuyDiet.time);
//                obj.put("timeStart", clan.KhiGasHuyDiet.timeStart);
//                obj.put("timeJoin", clan.KhiGasHuyDiet.timeJoin);
//                obj.put("zone", clan.KhiGasHuyDiet.zonePhuBan);
//                obj.put("hp", clan.KhiGasHuyDiet.hpMobPlus);
//                obj.put("level", clan.KhiGasHuyDiet.level);
//                arr3.put(obj);
//            }
//            JSONArray arr4 = new JSONArray();
//            if(pl.clan.KhoBauDuoiBien != null){
//                JSONObject obj = new JSONObject();
//                obj.put("id", clan.KhoBauDuoiBien.id);
//                obj.put("time", clan.KhoBauDuoiBien.time);
//                obj.put("timeStart", clan.KhoBauDuoiBien.timeStart);
//                obj.put("timeJoin", clan.KhoBauDuoiBien.timeJoin);
//                obj.put("zone", clan.KhoBauDuoiBien.zonePhuBan);
//                obj.put("hp", clan.KhoBauDuoiBien.hpMobPlus);
//                obj.put("level", clan.KhoBauDuoiBien.level);
//                arr4.put(obj);
//            }
//            JSONArray arr5 = new JSONArray();
//            if(pl.clan.ConDuongRanDoc != null){
//                JSONObject obj = new JSONObject();
//                obj.put("id", clan.ConDuongRanDoc.id);
//                obj.put("time", clan.ConDuongRanDoc.time);
//                obj.put("timeStart", clan.ConDuongRanDoc.timeStart);
//                obj.put("timeJoin", clan.ConDuongRanDoc.timeJoin);
//                obj.put("zone", clan.ConDuongRanDoc.zonePhuBan);
//                obj.put("hp", clan.ConDuongRanDoc.hpMobPlus);
//                obj.put("level", clan.ConDuongRanDoc.level);
//                arr5.put(obj);
//            }
//            SQLManager.execute(String.format("UPDATE `clan` SET slogan='%s',leader_id='%s',img_id='%s',power_point='%s',max_member='%s',clan_point='%s',level='%s',member='%s',doanhtrai='%s',khigas='%s',khobau='%s',conduong='%s' WHERE id=" + clan.id,
//                    clan.slogan, clan.leaderID, clan.imgID, clan.powerPoint, clan.maxMember, clan.clanPoint, clan.level, arr.toString(),arr2.toString(), arr3.toString(), arr4.toString(), arr5.toString()));
//        } catch (Exception e) {
//            Util.debug("Update_Clan");
//            e.printStackTrace();
//        }
//    }
    
    public static void updateDB(Clan clan) {
        try {
            JSONArray arr = new JSONArray();
            for (Member mem : clan.members) {
                arr.put(JSON.MemberToJson(mem));
            }
            JSONArray arr2 = new JSONArray();
            if(clan.DoanhTrai != null){
                JSONObject obj = new JSONObject();
                obj.put("id", clan.DoanhTrai.id);
                obj.put("time", clan.DoanhTrai.time);
                obj.put("timeStart", clan.DoanhTrai.timeStart);
                obj.put("timeJoin", clan.DoanhTrai.timeJoin);
                obj.put("zone", clan.DoanhTrai.zonePhuBan);
                obj.put("hp", clan.DoanhTrai.hpMobPlus);
                obj.put("level", clan.DoanhTrai.level);
                arr2.put(obj);
            }
            JSONArray arr3 = new JSONArray();
            if(clan.KhiGasHuyDiet != null){
                JSONObject obj = new JSONObject();
                obj.put("id", clan.KhiGasHuyDiet.id);
                obj.put("time", clan.KhiGasHuyDiet.time);
                obj.put("timeStart", clan.KhiGasHuyDiet.timeStart);
                obj.put("timeJoin", clan.KhiGasHuyDiet.timeJoin);
                obj.put("zone", clan.KhiGasHuyDiet.zonePhuBan);
                obj.put("hp", clan.KhiGasHuyDiet.hpMobPlus);
                obj.put("level", clan.KhiGasHuyDiet.level);
                arr3.put(obj);
            }
            JSONArray arr4 = new JSONArray();
            if(clan.KhoBauDuoiBien != null){
                JSONObject obj = new JSONObject();
                obj.put("id", clan.KhoBauDuoiBien.id);
                obj.put("time", clan.KhoBauDuoiBien.time);
                obj.put("timeStart", clan.KhoBauDuoiBien.timeStart);
                obj.put("timeJoin", clan.KhoBauDuoiBien.timeJoin);
                obj.put("zone", clan.KhoBauDuoiBien.zonePhuBan);
                obj.put("hp", clan.KhoBauDuoiBien.hpMobPlus);
                obj.put("level", clan.KhoBauDuoiBien.level);
                arr4.put(obj);
            }
            JSONArray arr5 = new JSONArray();
            if(clan.ConDuongRanDoc != null){
                JSONObject obj = new JSONObject();
                obj.put("id", clan.ConDuongRanDoc.id);
                obj.put("time", clan.ConDuongRanDoc.time);
                obj.put("timeStart", clan.ConDuongRanDoc.timeStart);
                obj.put("timeJoin", clan.ConDuongRanDoc.timeJoin);
                obj.put("zone", clan.ConDuongRanDoc.zonePhuBan);
                obj.put("hp", clan.ConDuongRanDoc.hpMobPlus);
                obj.put("level", clan.ConDuongRanDoc.level);
                arr5.put(obj);
            }
            SQLManager.execute(String.format("UPDATE `clan` SET slogan='%s',leader_id='%s',img_id='%s',power_point='%s',max_member='%s',clan_point='%s',level='%s',member='%s',doanhtrai='%s',khigas='%s',khobau='%s',conduong='%s' WHERE id=" + clan.id,
                    clan.slogan, clan.leaderID, clan.imgID, clan.powerPoint, clan.maxMember, clan.clanPoint, clan.level, arr.toString(),arr2.toString(), arr3.toString(), arr4.toString(), arr5.toString()));
        } catch (Exception e) {
            Util.logException(ClanDAO.class, e);
        }
    }

    public static ArrayList<ClanImage> loadClanImage() {
        ArrayList<ClanImage> clanImages = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM clan_image");
            while (rs.next()) {
                short frame1 = rs.getShort(7);
                short frame2 = rs.getShort(8);
                short frame3 = rs.getShort(9);
                short frame4 = rs.getShort(10);
                short frame5 = rs.getShort(11);
                short frame6 = rs.getShort(12);
                short frame7 = rs.getShort(13);
                short frame8 = rs.getShort(14);
                short frame9 = rs.getShort(15);
                short frame10 = rs.getShort(16);
                short frame11 = rs.getShort(17);
                short frame12 = rs.getShort(18);
                short[] f = {frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8, frame9, frame10, frame11, frame12};
                clanImages.add(new ClanImage(rs.getInt(1), rs.getInt(2), rs.getNString(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), f));
            }
            rs.close();
            rs = null;
            Util.warning("Finish load clan image! [" + clanImages.size() + "]\n");
        } catch (Exception e) {
            Util.logException(ClanDAO.class, e);
        }
        return clanImages;
    }

    public static ArrayList<Clan> load() {
        ArrayList<Clan> clans = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = SQLManager.executeQuery("SELECT * FROM clan WHERE server_name='"+Setting.SERVER_NAME+"'");
            while (rs.next()) {
                Clan clan = new Clan();
                clan.id = rs.getInt("id");
                clan.name = rs.getString("name");// name
                clan.slogan = rs.getString("slogan");// slogan
                clan.imgID = rs.getByte("img_id");// img id
                clan.level = rs.getByte("level");// level
                clan.powerPoint = rs.getLong("power_point");// power point
                clan.leaderID = rs.getInt("leader_id");// leader id
                clan.clanPoint = rs.getInt("clan_point");// clan point
                clan.currMember = 1;// curr mem
                clan.maxMember = rs.getByte("max_member");// max mem
                clan.time = rs.getTimestamp("create_time").getTime() / 1000;// time
                clan.members = JSON.ToListMember(new JSONArray(rs.getString("member")));
                String json = rs.getString("doanhtrai");
                if(json != null){
                    JSONArray arr = new JSONArray(json);
                    if(arr.length() > 0){
                        JSONObject obj = arr.getJSONObject(0);
                        clan.DoanhTrai = new PhuBan(clan.id, obj.getInt("id"), obj.getInt("time"), obj.getLong("timeJoin"), obj.getLong("timeStart"), obj.getInt("zone"), obj.getInt("hp"), obj.getInt("level"));
                    }
                }
                json = rs.getString("khigas");
                if(json != null){
                    JSONArray arr2 = new JSONArray(json);
                    if(arr2.length() > 0){
                        JSONObject obj = arr2.getJSONObject(0);
                        clan.KhiGasHuyDiet = new PhuBan(clan.id, obj.getInt("id"), obj.getInt("time"), obj.getLong("timeJoin"), obj.getLong("timeStart"), obj.getInt("zone"), obj.getInt("hp"), obj.getInt("level"));
                    }
                }
                json = rs.getString("khobau");
                if(json != null){
                    JSONArray arr3 = new JSONArray(json);
                    if(arr3.length() > 0){
                        JSONObject obj = arr3.getJSONObject(0);
                        clan.KhoBauDuoiBien = new PhuBan(clan.id, obj.getInt("id"), obj.getInt("time"), obj.getLong("timeJoin"), obj.getLong("timeStart"), obj.getInt("zone"), obj.getInt("hp"), obj.getInt("level"));
                    }
                }
                json = rs.getString("conduong");
                if(json != null){
                    JSONArray arr4 = new JSONArray(json);
                    if(arr4.length() > 0){
                        JSONObject obj = arr4.getJSONObject(0);
                        clan.ConDuongRanDoc = new PhuBan(clan.id, obj.getInt("id"), obj.getInt("time"), obj.getLong("timeJoin"), obj.getLong("timeStart"), obj.getInt("zone"), obj.getInt("hp"), obj.getInt("level"));
                    }
                }
                clans.add(clan);
            };
            rs.close();
            rs = null;
            Util.warning("Finish load clan! [" + clans.size() + "]\n");
        } catch (Exception e) {
            Util.logException(ClanDAO.class, e);
        }
        return clans;
    }
}
