package real.skill;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.SQLManager;
import server.Util;

public class SpeacialSkillData {
    private static SpeacialSkillData instance;
    public static SpeacialSkillData gI() {
        if (instance == null) {
            instance = new SpeacialSkillData();
        }
        return instance;
    }
    
    public void load_speacialSkill() {
        SpeacialSkill.gI().SpeacialSkills.clear();
        ResultSet rs;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM `noi_tai` ORDER BY id ASC");
            while (rs.next()) {
                SpeacialSkillTemplate Skill = new SpeacialSkillTemplate();
                Skill.gender = rs.getInt("gender");
                Skill.imgID = rs.getInt("img");
                Skill.info1 = rs.getString("info");
                Skill.info2 = rs.getString("info2");
                Skill.minParam = rs.getInt("min");
                Skill.maxParam = rs.getInt("max");
                SpeacialSkill.gI().SpeacialSkills.add(Skill);
            }
            rs.close();
            rs = null;
            Util.warning("finish load speacialSkill! [" + SpeacialSkill.gI().SpeacialSkills.size() + "]\n");
        } catch (Exception e) {
        }
       
    }
}
