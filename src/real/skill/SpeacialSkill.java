package real.skill;

import java.util.ArrayList;
import java.util.List;
import real.player.Player;
import server.Util;
import service.Setting;

public class SpeacialSkill {
    public List<SpeacialSkillTemplate> SpeacialSkills = new ArrayList<>();
    private static SpeacialSkill instance;
    public static SpeacialSkill gI() {
        if (instance == null) {
            instance = new SpeacialSkill();
        }
        return instance;
    }
    
    public List<SpeacialSkillTemplate> getSpeacialSkills(int gender){
        return SpeacialSkills.stream().filter(s -> s.gender == gender || s.gender == 3).toList();
    }
    
    public String[] getSpeacialSkill(Player pl){
        String[] Skill = new String[]{"5223", "Chưa kích hoạt nội tại\nBấm vào để xem chi tiết"};
        for(int i = 0; i < getSpeacialSkills(pl.gender).size(); i++){
            SpeacialSkillTemplate skill = getSpeacialSkills(pl.gender).get(i);
            if(pl.speacial[0] == i){
                Skill = new String[]{skill.imgID + "", skill.info2.replaceAll("#", pl.speacial[1] + "") + " [" + skill.minParam + " đến " + skill.maxParam + "]"};
                break;
            }
        }
        return Skill;
    }
    
    public short[] SpeacialSkill(Player pl){
        List<SpeacialSkillTemplate> skills = getSpeacialSkills(pl.gender);
        pl.speacial[0] = (short)Util.nextInt(0, skills.size() - 1);
        for(int i = 0; i < skills.size(); i++){
            SpeacialSkillTemplate skill = skills.get(i);
            if(pl.speacial[0] == i){
                pl.speacial[1] = (short)Util.nextInt(skill.minParam, skill.maxParam);
                break;
            }
        }
        return pl.speacial;
    }
    
    public long get_gold_open(Player pl) {
        long g = Setting.GOLD_OPEN_SPEACIAL;
        for (int i = 0; i < pl.speacial[2]; i++) {
            g *= 2;
        }
        return g;
    }
}
