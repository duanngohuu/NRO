package server.io;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import server.ServerManager;
import server.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;
import org.json.simple.JSONValue;
import real.clan.ClanManager;
import real.clan.Member;
import real.func.ChangeMap;
import real.item.ItemOptionShop;
import real.item.ItemTime;
import real.magictree.MabuEgg;
import real.magictree.MagicTree;
import real.map.Zone;
import real.map.MapManager;
import real.map.Map;
import real.map.Mob;
import real.map.MobTemplate;
import real.map.MobTemplateData;
import real.pet.Pet;
import real.player.Player;
import real.radar.Radar;
import real.radar.RadarTemplate;
import real.skill.Skill;
import real.skill.SkillUtil;
import real.task.TaskData;
import real.task.TaskOrders;
import service.Setting;
import service.data.Init;

public class JSON {

    public static void main(String[] args) throws JSONException {
    }

    public static void LoadPetFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);
            if (js.toString().equals("{}")) {
                js = null;
                return;
            }
            pl.pet = new Pet(pl);
            String name = js.getString("name");
            pl.pet.gender = (byte) js.getInt("gender");
            if (!js.isNull("status")) {
                pl.pet.status = (byte) js.getInt("status");
            }
            JSONObject obj = js.getJSONObject("point");
            pl.pet.isMabu = obj.isNull("isMabu") ? -1 : obj.getInt("isMabu");
            
            if(name.contains("�") || name.length() > 10 || name.length() < 5){
                name = pl.pet.isMabu != -1 ? "$Mabư" : "$Đệ tử";
            }
            pl.pet.name = name;
            
            pl.pet.point.tiemNang = obj.getLong("exp");
            pl.pet.point.power = obj.getLong("power");
            pl.pet.point.limitPower = (byte) obj.getInt("ghsm");
            pl.pet.point.hpGoc = obj.getInt("hp");
            pl.pet.point.mpGoc = obj.getInt("mp");
            pl.pet.point.dameGoc = obj.getInt("dame");
            pl.pet.point.defGoc = (short) obj.getInt("def");
            pl.pet.point.critGoc = (byte) obj.getInt("crit");
            pl.pet.point.stamina = obj.getInt("stamina");

            pl.pet.inventory.itemsBody = JSON.ToListItem(js.getJSONArray("body"));
            obj = js.getJSONObject("skill");
            JSONObject sk;
            Skill skill;
            for (int i = 0; i < obj.length(); i++) {
                sk = obj.getJSONObject(String.valueOf(i));
                skill = SkillUtil.createSkill(sk.getInt("id"), sk.getInt("level"), sk.isNull("lastTime") ? 0 : sk.getLong("lastTime"), sk.isNull("curExp") ? 0 : sk.getInt("curExp"));
                pl.pet.playerSkill.skills.add(skill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LoadItemFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);
            pl.inventory.itemsBody = ToListItem(js.getJSONArray("body"));
            pl.inventory.itemsBag = ToListItem(js.getJSONArray("bag"));
            pl.inventory.itemsBox = ToListItem(js.getJSONArray("box"));
            pl.inventory.itemsGift = ToListItem(js.getJSONArray("gift"));
            pl.inventory.itemsBoxSecond = ToListItem(js.getJSONArray("box2"));
            pl.inventory.itemsBoxSecond.removeIf(Objects::isNull);
            pl.inventory.itemsShip = ToListItem(js.getJSONArray("ship"));
            js = null;
        } catch (Exception e) {
        }
    }

    public static void LoadInfoFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);
            pl.gender = (byte) js.getInt("gender");
            pl.head = (short) js.getInt("hair");
            pl.point.power = js.getLong("power");
            if(!js.isNull("expUse")){
                pl.point.tiemNangUse = js.getLong("expUse");
            }
            
            if(!js.isNull("pointTET")){
                pl.pointTET = js.getInt("pointTET");
            }
            
            pl.eventPoint = js.getInt("eventPoint");
            pl.lastTimeEvent = js.getLong("lastTimeEvent");
            
            pl.inventory.gold = js.getLong("gold");
            pl.inventory.gem = js.getInt("gem");
            pl.inventory.ruby = js.getInt("ruby");
            
            if (js.isNull("timeSummon") || js.getLong("timeSummon") == 0) {
                pl.LastTimeSummon = 0;
            } 
            else if (js.isNull("countSummon") || js.getLong("countSummon") == 0) {
                pl.countSummon = 0;
            }
            else {
                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                Timestamp timelast = new Timestamp(js.getLong("timeSummon"));
                if (timenow.getDate() > timelast.getDate()) {
                    pl.LastTimeSummon = 0;
                    pl.countSummon = 0;
                } else {
                    pl.LastTimeSummon = js.getLong("timeSummon");
                    pl.countSummon = js.getInt("countSummon");
                }
            }
            pl.timeJoinClan = js.isNull("timeJoinClan") ? System.currentTimeMillis() : js.getLong("timeJoinClan");
            pl.x = js.getInt("x");
            pl.y = js.getInt("y");
            if (js.isNull("taskID")) {
                pl.taskId = TaskData.list_task.get(0).taskId;
            } else if (js.isNull("taskIndex")) {
                pl.taskIndex = 0;
            } else if (js.isNull("taskCount")) {
                pl.taskCount = 0;
            } else {
                pl.taskId = (short) js.getInt("taskID");
                if (pl.taskId > TaskData.list_task.size() || TaskData.list_task.get(pl.taskId - 1) == null) {
                    pl.taskId = TaskData.list_task.get(0).taskId;
                }
                pl.taskIndex = (byte) js.getInt("taskIndex");
                pl.taskCount = js.getInt("taskCount");
                if (pl.taskIndex > TaskData.getTask(pl.taskId).counts.length - 1) {
                    pl.taskIndex = (byte) (TaskData.getTask(pl.taskId).counts.length - 1);
                }
            }
            if(pl.taskIndex > TaskData.getTask(pl.taskId).counts.length - 1){
                pl.taskIndex = (byte) (TaskData.getTask(pl.taskId).counts.length - 1);
            }else if (pl.taskIndex < 0){
                pl.taskIndex = 0;
            }
            pl.zone = MapManager.gI().getMapById(js.getInt("map")).map_zone[0];

            int cl = js.getInt("clan");
            if (cl != -1) {
                pl.clan = ClanManager.gI().getClanById(cl);
            }
            
            pl.typeFusion = (byte) js.getInt("typeFusion");
            pl.typeShip = (byte)js.getInt("typeShip");
            if(!js.isNull("countOrders") && !js.isNull("TimeTaskOrder")){
                pl.TaskOrder_Count = js.getInt("countOrders");
                pl.currTimeTaskOrder = js.getLong("TimeTaskOrder");
            }
            else{
                pl.currTimeTaskOrder = Util.TimeTask();
                pl.TaskOrder_Count = 10;
            }
            boolean isLoadTask = true;
            long day = Util.findDayDifference(System.currentTimeMillis(), pl.currTimeTaskOrder);
            if(day > 0){
                pl.currTimeTaskOrder = Util.TimeTask();
                pl.TaskOrder_Count = 10;
                isLoadTask = false;
            }
            if(!js.isNull("taskOrders") && isLoadTask){
                JSONObject js2 = new JSONObject(js.getString("taskOrders"));
                String n = "";
                String des = "";
                if(js2.getInt("id") == 0){
                    MobTemplate mob = MobTemplateData.gI().getTemp(js2.getInt("killId"));
                    Zone mapMob = Mob.getMob(js2.getInt("killId")).map;
                    n = "Tiêu diệt " +js2.getInt("maxCount") +" " + mob.name;
                    des = "Hãy đến và tiêu diệt " + js2.getInt("maxCount") +" " + mob.name +" tại "+mapMob.map.name;
                }else if(js2.getInt("id") == 1){
                    n = "Hạ gục " + js2.getInt("maxCount") +" người chơi";
                    des = "Hạ gục " + js2.getInt("maxCount") +" người chơi";
                }else{
                    n = "Thu thập " + Util.powerToString(js2.getInt("maxCount") * js2.getInt("killId")) +" vàng";
                    des = "Hãy đi thu thập đủ " +Util.powerToString(js2.getInt("maxCount") * js2.getInt("killId")) +" vàng về đây cho ta";
                }
                pl.taskOrder = new TaskOrders(js2.getInt("id"),js2.getInt("count"), (short) js2.getInt("maxCount"),n,des,js2.getInt("killId"),js2.getInt("mapId"));
            }
            else{
                pl.taskOrder = new TaskOrders(-1,-1,(short)-1,"","",-1,-1);
            }
            if(!js.isNull("giftcode")){
                JSONArray arr = js.getJSONArray("giftcode");
                int size = arr.length();
                for(int i = 0 ; i < size;i++){
                    pl.inventory.giftCode.add(arr.getString(i));
                }
            }
            if(!js.isNull("dhvt23")){
                JSONObject js2 = new JSONObject(js.getString("dhvt23"));
                pl.DHVT_23.step = js2.getInt("step");
                pl.DHVT_23.die = js2.getInt("die");
                pl.DHVT_23.isDrop = js2.getBoolean("drop");
            }
            js = null;
        } catch (Exception e) {
            Util.debug("ERROR LOAD INFO");
            e.printStackTrace();
        }
    }

    public static void LoadPointFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);

            pl.point.tiemNang = js.getLong("exp");
            pl.point.limitPower = (byte) js.getInt("ghsm");

            pl.point.hpGoc = js.getInt("hp");
            pl.point.mpGoc = js.getInt("mp");
            pl.point.dameGoc = js.getInt("dame");
            pl.point.defGoc = (short)js.getInt("def");
            pl.point.critGoc = (byte)js.getInt("crit");
            if(!js.isNull("crit2")){
                pl.point.critSum = (byte)js.getInt("crit2");
            }
            pl.point.stamina = js.getInt("stamina");

            js = null;
        } catch (Exception e) {
            LoadPointFromJson(pl, Init.DEFAULT_POINT_START[pl.gender]);
        }
    }

    public static void LoadSkillFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);

            JSONObject skill = js.getJSONObject("skill");
            JSONObject sk = null;
            for (int i = 0; i < skill.length(); i++)
            {
                sk = skill.getJSONObject(String.valueOf(i));
                int id = sk.getInt("id");
                int level = sk.getInt("level");
                long lastTime = sk.isNull("lastTime") ? 0 : sk.getLong("lastTime");
                int curExp = sk.isNull("curExp") ? 0 : sk.getInt("curExp");
                pl.playerSkill.skills.add(SkillUtil.createSkill(id, level, lastTime, curExp));
                sk = null;
            }

            skill = js.getJSONObject("skill_shortcut");
            for (int i = 0; i < skill.length(); i++) {
                pl.playerSkill.skillShortCut[i] = (byte) skill.getInt(String.valueOf(i));
            }

            for (int i : pl.playerSkill.skillShortCut) {
                if (i != -1) {
                    pl.playerSkill.skillSelect = pl.playerSkill.getSkillbyId(i);
                    break;
                }
            }
            if (pl.playerSkill.skillSelect == null) {
                pl.playerSkill.skillSelect = pl.playerSkill.getSkillbyId(pl.gender == 0 ? 0 : (pl.gender == 1 ? 2 : 4));
            }

            skill = js.getJSONObject("skill_special");
            pl.speacial[0] = (short) skill.getInt("id");
            pl.speacial[1] = (short) skill.getInt("param");
            pl.speacial[2] = (short) skill.getInt("count");

            skill = null;
            sk = null;
            js = null;
        } catch (Exception e) {
//            e.printStackTrace();
            LoadSkillFromJson(pl, Init.DEFAULT_SKILL_START[pl.gender]);
        }
    }
    
    public static String MabuEggToJson(Player pl) {
        try {
            JSONObject js = new JSONObject();
            js.put("createTime", pl.mabuEgg.createTime);
            js.put("timeHatches", pl.mabuEgg.timeHatches);

            final String mgt = js.toString();
            js = null;
            return mgt;
        } catch (Exception e) {
        }
        return "{}";
    }
    
    public static String RadarToJson(Player pl){
        try {
            JSONArray js = new JSONArray();
            for(RadarTemplate rd : pl.radar.radars){
                JSONObject radar = new JSONObject();
                radar.put("id", rd.id);
                radar.put("user", rd.user);
                radar.put("count", rd.count);
                radar.put("level", rd.level);
                js.put(radar);
            }
            final String str = js.toString();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }
    
    public static void LoadMabuEggFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);
            pl.mabuEgg = new MabuEgg(pl, js.getLong("createTime"), js.getInt("timeHatches"));
            js = null;
        } catch (Exception e) {
            pl.mabuEgg = null;
        }
    }
    
    public static void LoadRadarFromJson(Player pl, String json) {
        try {
            pl.radar = new Radar(pl);
            if (Util.isNullOrEmpty(json) || json.equals("{}")) {
                return;
            }
            JSONArray js = new JSONArray(json);
            JSONObject obj;
            for(int i = 0; i < js.length(); i++){
                obj = js.getJSONObject(i);
                pl.radar.SetRadar(obj.getInt("id"), obj.getInt("user"), obj.getInt("count"), obj.getInt("level"));
            }
            obj = null;
            js = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LoadMagicTreeFromJson(Player pl, String json) {
        try {
            JSONObject js = new JSONObject(json);
            pl.magicTree = new MagicTree(pl.id, js.getInt("level"), js.getInt("quantity"), js.getInt("update") == 1, js.getInt("time"));
            js = null;
        } catch (Exception e) {
        }
    }

    public static void LoadItemTimeFromJson(Player pl, String json) {
        try {
            if (Util.isNullOrEmpty(json) || json.equals("[]")) {
                return;
            }
            JSONArray js = new JSONArray(json);
            JSONObject obj;
            for (int i = 0; i < js.length(); i++) {
                obj = js.getJSONObject(i);
                pl.itemTime.addItemTime(obj.getInt("icon"), obj.getInt("time"));
                obj = null;
            }
            js = null;
        } catch (Exception e) {
        }
    }
    
     public static void LoadBuaFromJson(Player pl, String json) {
        try {
            if (Util.isNullOrEmpty(json) || json.equals("[]")) {
                return;
            }
            JSONArray js = new JSONArray(json);
            JSONObject obj;
            for (int i = 0; i < js.length(); i++) {
                obj = js.getJSONObject(i);
                pl.inventory.itemsBua.add(new ItemOptionShop(obj.getInt("item_id"), obj.getInt("option_id"), obj.getInt("param")));
                obj = null;
            }
            js = null;
        } catch (Exception e) {
        }
    }

    public static void LoadEntitiesFromJson(Player pl, String json) {
        try {
            if (Util.isNullOrEmpty(json) || json.equals("{}")) {
                return;
            }
            JSONObject js = new JSONObject(json);
            JSONArray obj = js.getJSONArray("friend");
            for (int i = 0; i < obj.length(); i++) {
                pl.listPlayer.addFriend(obj.getInt(i));
            }
            obj = js.getJSONArray("enemy");
            for (int i = 0; i < obj.length(); i++) {
                pl.listPlayer.addEnemy(obj.getInt(i));
            }
            obj = null;
            js = null;
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static String EntitiesToJson(Player pl) {
        try {
            JSONObject js = new JSONObject();
            JSONArray jsa;

            jsa = new JSONArray();
            for (int f : pl.listPlayer.friends) {
                jsa.put(f);
            }
            js.put("friend", jsa);

            jsa = new JSONArray();
            for (int e : pl.listPlayer.enemies) {
                jsa.put(e);
            }
            js.put("enemy", jsa);
            jsa = null;

            final String str = js.toString();

            js = null;

            return str;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "{}";
    }

    public static String ItemTimeToJson(Player pl) {
        try {
            JSONArray js = new JSONArray();
            JSONObject obj;

            for (ItemTime itt : pl.itemTime.vItemTime) {
                obj = new JSONObject();
                obj.put("icon", itt.iconID);
                obj.put("time", itt.time);
                js.put(obj);
            }

            final String str = js.toString();

            js = null;
            obj = null;

            return str;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "[]";
    }
    
    public static String ItemBuaToJson(Player pl) {
        try {
            JSONArray js = new JSONArray();
            JSONObject obj;

            for (ItemOptionShop itt : pl.inventory.itemsBua) {
                obj = new JSONObject();
                obj.put("item_id", itt.itemTemplateId);
                obj.put("option_id", itt.optionId);
                obj.put("param", itt.param);
                js.put(obj);
            }

            final String str = js.toString();

            js = null;
            obj = null;

            return str;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "[]";
    }


    public static String PetToJson(Pet p) {
        if (p == null) {
            return "{}";
        }
        try {
            JSONObject pet = new JSONObject();
            pet.put("name", p.name);
            pet.put("gender", p.gender);
            pet.put("status", p.status);
            JSONObject point = new JSONObject();
            point.put("isMabu", p.isMabu);
            point.put("exp", p.point.tiemNang);
            point.put("power", p.point.power);
            point.put("ghsm", p.point.limitPower);
            point.put("stamina", p.point.stamina);
            point.put("hp", p.point.hpGoc);
            point.put("mp", p.point.mpGoc);
            point.put("dame", p.point.dameGoc);
            point.put("def", p.point.defGoc);
            point.put("crit", p.point.critGoc);
            pet.put("point", point);

            pet.put("body", JSON.ToJson(p.inventory.itemsBody));

            JSONObject skill = new JSONObject();
            JSONObject obj = null;
            int i = 0;
            for (Skill sk : p.playerSkill.skills) {
                obj = new JSONObject();
                obj.put("id", sk.template.id);
                obj.put("level", sk.point);
                obj.put("lastTime", sk.lastTimeUseThisSkill);
                skill.put(String.valueOf(i), obj);
                i++;
            }

            pet.put("skill", skill);
            final String str = pet.toString().replace("\\", "").replace("\"[", "[").replace("]\"", "]");

            obj = null;
            skill = null;
            point = null;
            pet = null;
            return str;
        } catch (Exception e) {
        }
        return "{}";
    }

    public static String MagicTreeToJson(Player pl) {
        try {
            JSONObject js = new JSONObject();
            js.put("level", pl.magicTree.level);
            js.put("quantity", pl.magicTree.currentPea);
            js.put("time", pl.magicTree.timeUpdate);
            js.put("update", pl.magicTree.isUpdate ? 1 : 0);

            final String mgt = js.toString();
            js = null;
            return mgt;
        } catch (Exception e) {
        }
        return null;
    }

    public static String SkillToJson(Player pl) {
        try {
            JSONObject js = new JSONObject();
            JSONObject skill = new JSONObject();
            JSONObject obj;
            JSONObject skill_shortcut = new JSONObject();
            JSONObject skill_special = new JSONObject();

            if (pl != null) {
                int i = 0;
                for (Skill sk : pl.playerSkill.skills) {
                    obj = new JSONObject();
                    obj.put("id", sk.template.id);
                    obj.put("level", sk.point);
                    obj.put("lastTime", sk.lastTimeUseThisSkill);
                    obj.put("curExp", sk.curExp);
                    skill.put(String.valueOf(i), obj);
                    i++;
                }
                for (int z = 0; z < pl.playerSkill.skillShortCut.length; z++) {
                    skill_shortcut.put(String.valueOf(z), pl.playerSkill.skillShortCut[z]);
                }
                skill_special.put("id", pl.speacial[0]);
                skill_special.put("param", pl.speacial[1]);
                skill_special.put("count", pl.speacial[2]);

            }

            js.put("skill", skill);
            js.put("skill_shortcut", skill_shortcut);
            js.put("skill_special", skill_special);

            final String str = js.toString();

            js = null;
            skill = null;
            obj = null;
            skill_shortcut = null;
            skill_special = null;

            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String PointToJson(Player pl) {
        try {
            JSONObject js = new JSONObject();

            if (pl != null) {
                js.put("exp", pl.point.tiemNang);
                js.put("ghsm", pl.point.limitPower);

                js.put("hp", pl.point.hpGoc);
                js.put("mp", pl.point.mpGoc);
                js.put("dame", pl.point.dameGoc);
                js.put("def", pl.point.defGoc);
                js.put("crit", pl.point.critGoc);
                js.put("crit2", pl.point.critSum);
                js.put("stamina", pl.point.stamina);
            }

            final String str = js.toString();
            js = null;
            return str;
        } catch (Exception e) {
        }
        return null;
    }

    public static String ItemToJson(Player pl) {
        try {

            JSONObject js = new JSONObject();
            js.put("body", ToJson(pl.inventory.itemsBody));
            js.put("bag", ToJson(pl.inventory.itemsBag));
            js.put("box", ToJson(pl.inventory.itemsBox));
            js.put("ship", ToJson(pl.inventory.itemsShip));
            js.put("gift", ToJson(pl.inventory.itemsGift));
            pl.inventory.itemsBoxSecond.removeIf(Objects::isNull);
            js.put("box2", ToJson(pl.inventory.itemsBoxSecond));
            final String str = js.toString().replace("\\", "").replace("\"[", "[").replace("]\"", "]");
            js = null;

            return str;

        } catch (Exception e) {
        }
        return null;
    }

    public static String InfoToJson(int gender, int hair) {
        try {
            JSONObject js = new JSONObject();
            js.put("gender", gender);
            js.put("hair", hair);
            js.put("power", 1200);
            js.put("expUse", 0);
            js.put("clan", -1);

            js.put("gold", 2000);
            js.put("gem", 0);
            js.put("ruby", 20);

            js.put("map", 21 + gender);
            js.put("x", 180);
            js.put("y", 284);
            
            int nvID = TaskData.list_task.get(0).taskId;
            if(Setting.SERVER_TEST){
                nvID = TaskData.list_task.get(TaskData.list_task.size() - 1).taskId;
            }

            js.put("taskID", nvID);
            js.put("taskIndex", 0);
            js.put("taskCount", 0);

            js.put("timeSummon", 0);
            js.put("countSummon", 0);
            
            js.put("eventPoint", 0);
            js.put("lastTimeEvent", System.currentTimeMillis());

            js.put("typeFusion", 0);
            js.put("typeShip", 1);
            final String str = js.toString();
            js = null;
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String InfoToJson(Player pl) {
        try {
            JSONObject js = new JSONObject();
            js.put("typeFusion", pl.typeFusion);
            js.put("gender", pl.gender);
            js.put("hair", pl.head);
            js.put("power", pl.point.power);
            js.put("expUse", pl.point.tiemNangUse);
            
            js.put("clan", pl.clan == null ? -1 : pl.clan.id);
            js.put("timeJoinClan", pl.timeJoinClan);
            
            js.put("pointTET", pl.pointTET);
            js.put("eventPoint", pl.eventPoint);
            js.put("lastTimeEvent", pl.lastTimeEvent);

            js.put("gold", pl.inventory.gold);
            js.put("gem", pl.inventory.gem);
            js.put("ruby", pl.inventory.ruby);

            js.put("map", pl.zone.map.id);
            js.put("x", pl.x);
            js.put("y", pl.y);

            js.put("taskID", pl.taskId);
            js.put("taskIndex", pl.taskIndex);
            js.put("taskCount", pl.taskCount);

            js.put("timeSummon", pl.LastTimeSummon);
            js.put("countSummon", pl.countSummon);
            
            js.put("taskOrders", ToJson(pl.taskOrder));
            js.put("countOrders", pl.TaskOrder_Count);
            js.put("TimeTaskOrder", pl.currTimeTaskOrder);
            
            js.put("giftcode", pl.inventory.giftCode);
            
            js.put("dhvt23", ToJson(pl.DHVT_23));
            
            js.put("typeShip", pl.typeShip);
            final String info = js.toString();
            js = null;
            return info;
        } catch (Exception e) {
            Util.debug("Json.InfoToJson");
            e.printStackTrace();
        }
        return null;
    }

    public static List<Item> ToListItem(JSONArray jsa) {
        List<Item> its = new ArrayList<Item>();
        try {
            JSONArray jsa2 = null;
            Item it = null;
            JSONObject jso = null;
            JSONObject jso2 = null;
            for (int i = 0; i < jsa.length(); i++) {
                try {
                    jso = jsa.getJSONObject(i);
                    it = new Item();
                    int itemID = jso.getInt("temp_id");
                    it.template = ItemData.gI().getTemplate((short)itemID);
                    it.content = it.getContent();
                    it.quantity = jso.getInt("quantity");
                    Timestamp timenow = new Timestamp(System.currentTimeMillis());
                    if (jso.isNull("buyTime") || jso.getLong("buyTime") == 0) {
                        it.buyTime = timenow.getTime();
                    } else {
                        it.buyTime = jso.getLong("buyTime");
                    }
                    if(!jso.isNull("Sell")){
                        it.Sell = jso.getInt("Sell");
                        it.gold = jso.getInt("gold");
                        it.gem = jso.getInt("gem");
                    }
                    jsa2 = jso.getJSONArray("option");
                    for (int j = 0; j < jsa2.length(); j++) {
                        jso2 = (JSONObject) jsa2.get(j);
                        int idOptions = jso2.getInt("id");
                        int param = jso2.getInt("param");
                        if(idOptions == 94 && param == 1 && itemID == 1054){
                            idOptions = 93;
                        }
                        if (idOptions == 93 && it.buyTime > 0) {
                            long day = Util.findDayDifference(timenow.getTime() , it.buyTime);
                            if(day >= 1){
                                param -= day;
                                it.buyTime = timenow.getTime();
                            }
                        }
                        it.itemOptions.add(new ItemOption(idOptions, param));
                    }
                    if(it.itemOptions.isEmpty()){
                        it.itemOptions.add(new ItemOption(30,0));
                    }
                    if (it.itemOptions.stream().allMatch(iop -> iop.optionTemplate.id != 93 || (iop.optionTemplate.id == 93 && iop.param > 0))) {
                        its.add(it);
                    }else{
                        its.add(null);
                    }
                    jso = null;
                } catch (Exception e) {
                    its.add(null);
                }
            }
            jsa = null;
            jsa2 = null;
            it = null;
            jso = null;
            jso2 = null;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return its;
    }

    public static String ToJson(Object obj) {
        return JSONValue.toJSONString(obj);
    }

    public static JSONObject MemberToJson(Member mem) {
        try {
            JSONObject job = new JSONObject();
            job.put("player_id", mem.id);
            job.put("role", mem.role);
            job.put("power_point", mem.powerPoint);
            job.put("donate", mem.donate);
            job.put("receive_donate", mem.receiveDonate);
            job.put("join_time", mem.joinTime);
            return job;
        } catch (Exception e) {
        }
        return null;
    }
    
    public static ArrayList<Member> ToListMember(JSONArray jsa) {
        ArrayList<Member> members = new ArrayList<Member>();
        try {
            Member mem = null;
            JSONObject jso = null;
            for (int i = 0; i < jsa.length(); i++) {
                jso = jsa.getJSONObject(i);
                    if (jso != null) {
                        mem = new Member();
                        mem.id = jso.getInt("player_id");
                        mem.role = (byte)jso.getInt("role");
                        mem.powerPoint = jso.getInt("power_point");
                        mem.donate = jso.getInt("donate");
                        mem.receiveDonate = jso.getInt("receive_donate");
                        mem.joinTime = jso.getLong("join_time");
                        members.add(mem);
                    }
            }
            jsa = null;
            jso = null;
            mem = null;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return members;
    }
}
