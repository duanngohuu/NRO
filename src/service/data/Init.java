package service.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import real.map.MapManager;
import real.map.MobTemplate;
import real.map.MobTemplateData;
import real.npc.NpcTemplate;
import real.npc.NpcTemplateData;
import real.skill.Skill;
import server.FileIO;
import server.Util;
import service.Setting;

public class Init {

    public static void init() {
        try {
            HEAD.clear();
            createDataMap();
            File files = new File("data/head/");
            for (File file : files.listFiles()) {
                String name = file.getName();
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                HEAD.put(Integer.valueOf(name), Integer.valueOf(content));
            }
            Util.warning("Finish load head! [" + HEAD.size() + "]\n");
            // -------------------------- load image source -----------------------------------
            files = new File("data/res/x1/");
            IMAGE_SOURCE[0] = new HashMap();
            for (File file : files.listFiles()) {
                IMAGE_SOURCE[0].put(file.getName(), FileIO.readFile(file.getAbsolutePath()));
            }
            Util.warning("Finish load image source x1! [" + IMAGE_SOURCE[0].size() + "]\n");
            files = new File("data/res/x2/");
            IMAGE_SOURCE[1] = new HashMap();
            for (File file : files.listFiles()) {
                IMAGE_SOURCE[1].put(file.getName(), FileIO.readFile(file.getAbsolutePath()));
            }
            Util.warning("Finish load image source x2! [" + IMAGE_SOURCE[1].size() + "]\n");
            files = new File("data/res/x3/");
            IMAGE_SOURCE[2] = new HashMap();
            for (File file : files.listFiles()) {
                IMAGE_SOURCE[2].put(file.getName(), FileIO.readFile(file.getAbsolutePath()));
            }
            Util.warning("Finish load image source x3! [" + IMAGE_SOURCE[2].size() + "]\n");
            files = new File("data/res/x4/");
            IMAGE_SOURCE[3] = new HashMap();
            for (File file : files.listFiles()) {
                IMAGE_SOURCE[3].put(file.getName(), FileIO.readFile(file.getAbsolutePath()));
            }
            Util.warning("Finish load image source x4! [" + IMAGE_SOURCE[3].size() + "]\n");

            files = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static final HashMap[] IMAGE_SOURCE = new HashMap[4];
    public static final HashMap<Integer,Integer> HEAD = new HashMap<Integer,Integer>();
    
    public static final long[] SM_TIEU_CHUAN = {100010000000L, 80010000000L, 70010000000L, 60010000000L, 50010000000L, 40000000000L, 10000000000L, 5000000000L, 1500000000L, 150000000L, 15000000L, 1500000L, 700000L, 340000L, 170000L, 90000L, 40000L, 15000L, 3000L, 1000L};
    
    public static final short[] HEADMONKEY = {192, 195, 196, 199, 197, 200, 198};
    public static final String[] DEFAULT_ITEM_START = new String[]{
        "{\"bag\":[null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],\"box\":[{\"temp_id\":\"12\",\"quantity\":\"1\",\"option\":[{\"id\":\"14\",\"param\":\"1\"}]},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],\"body\":[{\"temp_id\":\"0\",\"quantity\":\"1\",\"option\":[{\"id\":\"47\",\"param\":\"2\"}]},{\"temp_id\":\"6\",\"quantity\":\"1\",\"option\":[{\"id\":\"6\",\"param\":\"30\"}]},null,null,null,null,null,null,null],\"ship\":[null,null,null,null,null,null,null,null,null,null],\"gift\":[]}",
        "{\"bag\":[null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],\"box\":[{\"temp_id\":\"12\",\"quantity\":\"1\",\"option\":[{\"id\":\"14\",\"param\":\"1\"}]},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],\"body\":[{\"temp_id\":\"1\",\"quantity\":\"1\",\"option\":[{\"id\":\"47\",\"param\":\"2\"}]},{\"temp_id\":\"7\",\"quantity\":\"1\",\"option\":[{\"id\":\"6\",\"param\":\"20\"}]},null,null,null,null,null,null,null],\"ship\":[null,null,null,null,null,null,null,null,null,null],\"gift\":[]}",
        "{\"bag\":[null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],\"box\":[{\"temp_id\":\"12\",\"quantity\":\"1\",\"option\":[{\"id\":\"14\",\"param\":\"1\"}]},null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],\"body\":[{\"temp_id\":\"2\",\"quantity\":\"1\",\"option\":[{\"id\":\"47\",\"param\":\"3\"}]},{\"temp_id\":\"8\",\"quantity\":\"1\",\"option\":[{\"id\":\"6\",\"param\":\"20\"}]},null,null,null,null,null,null,null],\"ship\":[null,null,null,null,null,null,null,null,null,null],\"gift\":[]}"
    };
    public static final String[] DEFAULT_POINT_START = new String[]{
        "{\"mp\":100,\"def\":0,\"crit\":0,\"stamina\":10000,\"hp\":200,\"ghsm\":0,\"dame\":12,\"exp\":1200}",
        "{\"mp\":200,\"def\":0,\"crit\":0,\"stamina\":10000,\"hp\":100,\"ghsm\":0,\"dame\":12,\"exp\":1200}",
        "{\"mp\":100,\"def\":0,\"crit\":0,\"stamina\":10000,\"hp\":100,\"ghsm\":0,\"dame\":15,\"exp\":1200}"
    };
    public static final String[] DEFAULT_SKILL_START = new String[]{
        "{\"skill_shortcut\":{\"0\":0,\"1\":-1,\"2\":-1,\"3\":-1,\"4\":-1,\"5\":-1,\"6\":-1,\"7\":-1,\"8\":-1,\"9\":-1},\"skill\":{\"0\":{\"level\":1,\"id\":0,\"lastTime\":0}},\"skill_special\":{\"param\":-1,\"count\":1,\"id\":-1}}",
        "{\"skill_shortcut\":{\"0\":2,\"1\":-1,\"2\":-1,\"3\":-1,\"4\":-1,\"5\":-1,\"6\":-1,\"7\":-1,\"8\":-1,\"9\":-1},\"skill\":{\"0\":{\"level\":1,\"id\":2,\"lastTime\":0}},\"skill_special\":{\"param\":-1,\"count\":1,\"id\":-1}}",
        "{\"skill_shortcut\":{\"0\":4,\"1\":-1,\"2\":-1,\"3\":-1,\"4\":-1,\"5\":-1,\"6\":-1,\"7\":-1,\"8\":-1,\"9\":-1},\"skill\":{\"0\":{\"level\":1,\"id\":4,\"lastTime\":0}},\"skill_special\":{\"param\":-1,\"count\":1,\"id\":-1}}"
    };
    public static final String DEFAULT_MAGIC_TREE_START = "{\"quantity\":0,\"level\":10,\"update\":0,\"time\":0}";

    public static final int[] SKILL_1_PET = new int[]{
        Skill.DRAGON, Skill.DEMON, Skill.GALIK
    };
    public static final int[] SKILL_2_PET = new int[]{
        Skill.KAMEJOKO, Skill.MASENKO, Skill.ANTOMIC
    };
    public static final int[] SKILL_3_PET = new int[]{
        Skill.THAI_DUONG_HA_SAN, Skill.KAIOKEN, Skill.TAI_TAO_NANG_LUONG
    };
    public static final int[] SKILL_4_PET = new int[]{
        Skill.BIEN_KHI, Skill.DE_TRUNG, Skill.KHIEN_NANG_LUONG
    };
    public static void createDataMap(){
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeByte(6);
            dos.writeByte(Setting.vsMap);
            dos.writeByte(MapManager.gI().maps.size());
            for (short i = 0; i < MapManager.gI().maps.size(); ++i) {
                dos.writeUTF(MapManager.gI().maps.get(i).name);
            }
            //npc
            dos.writeByte(NpcTemplateData.gI().temps.size());
            for(int i = 0 ; i < NpcTemplateData.gI().temps.size() ; i++){
                NpcTemplate npc = NpcTemplateData.gI().temps.get(i);
                dos.writeUTF(npc.name);
                dos.writeShort(npc.headId);
                dos.writeShort(npc.bodyId);
                dos.writeShort(npc.legId);
                dos.writeByte(1);
                dos.writeByte(1);
                dos.writeUTF("Nói chuyện");
            }
            //mob
            dos.writeByte(MobTemplateData.gI().temps.size());
            for(int i = 0 ; i < MobTemplateData.gI().temps.size() ; i++){
                MobTemplate mob = MobTemplateData.gI().temps.get(i);
                dos.writeByte(mob.type);  
                dos.writeUTF(mob.name);
                dos.writeInt(mob.hp);
                dos.writeByte(mob.rangeMove);
                dos.writeByte(mob.speed);
                dos.writeByte(mob.dartType);
            }
            byte[] ab = bas.toByteArray();
            FileIO.writeFile("data/NRmap", ab);
            Util.warning("Finish load data map! [" + ab.length + "]\n");
            dos.close();
            bas.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
