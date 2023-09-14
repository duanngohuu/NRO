package real.map;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import server.SQLManager;
import server.Util;

public class MobTemplateData {

    public Vector<MobTemplate> temps;

    public MobTemplateData() {
        temps = new Vector<>();
    }
    private static MobTemplateData instance;

    public static MobTemplateData gI() {
        if (instance == null) {
            instance = new MobTemplateData();
        }
        return instance;
    }

    public void init() {
        temps.clear();
        load_mob();
        Util.warning("Finish load mob! [" + temps.size() + "]\n");
    }

    public MobTemplate getTempByLevel(int level) {
        for(int i = 0 ; i < temps.size(); i++){
            MobTemplate mob = temps.get(i);
            if(mob != null && mob.level == level){
                return mob;
            }
        }
        return null;
    }
    
    public MobTemplate getTemp(int id) {
        return temps.get(id);
    }

    public void load_mob() {
        ResultSet rs;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM `mob_template` ORDER BY id_mob ASC");
            while (rs.next()) {
                MobTemplate mob = new MobTemplate();
                mob.mobTemplateId = rs.getInt("id_mob");
                mob.hp = rs.getInt("hp");
                mob.rangeMove = rs.getInt("rangeMove");
                mob.speed = rs.getInt("speed");
                mob.type = rs.getInt("type");
                mob.name = rs.getString("name");
                mob.level = rs.getInt("level");
                mob.dartType = rs.getInt("dartType");
                temps.add(mob);
            }
            rs.close();
            rs = null;
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }
}
