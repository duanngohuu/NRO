package real.skill;

import java.io.DataInputStream;
import java.io.FileInputStream;
import server.Util;

public class SkillData {

    public static NClass[] nClasss;
    
    public static void loadSkill() {
        try {
            nClasss = null;
            DataInputStream dis = new DataInputStream(new FileInputStream("data/skill/Skill_v2"));
            nClasss = new NClass[dis.readByte()];
            for (int i = 0; i < nClasss.length; i++) {
                nClasss[i] = new NClass();
                nClasss[i].classId = i;
                nClasss[i].name = dis.readUTF();
                // TD = 24 - NM = 26 - XD = 25 \\
                nClasss[i].skillTemplates = new SkillTemplate[dis.readByte()];
                for (int j = 0; j < nClasss[i].skillTemplates.length; j++) {
                    nClasss[i].skillTemplates[j] = new SkillTemplate();
                    nClasss[i].skillTemplates[j].id = dis.readByte();
                    nClasss[i].skillTemplates[j].name = dis.readUTF();
                    nClasss[i].skillTemplates[j].maxPoint = dis.readByte();
                    nClasss[i].skillTemplates[j].manaUseType = dis.readByte();
                    nClasss[i].skillTemplates[j].type = dis.readByte();
                    nClasss[i].skillTemplates[j].iconId = dis.readShort();
                    nClasss[i].skillTemplates[j].damInfo = dis.readUTF();
                    nClasss[i].skillTemplates[j].description = dis.readUTF();
                    nClasss[i].skillTemplates[j].skills = new Skill[dis.readByte()];
                    for (int k = 0; k < nClasss[i].skillTemplates[j].skills.length; k++) {
                        nClasss[i].skillTemplates[j].skills[k] = new Skill();
                        nClasss[i].skillTemplates[j].skills[k].template = nClasss[i].skillTemplates[j];
                        nClasss[i].skillTemplates[j].skills[k].skillId = dis.readShort();
                        nClasss[i].skillTemplates[j].skills[k].point = dis.readByte();
                        nClasss[i].skillTemplates[j].skills[k].powRequire = dis.readLong();
                        nClasss[i].skillTemplates[j].skills[k].manaUse = dis.readShort();
                        nClasss[i].skillTemplates[j].skills[k].coolDown = dis.readInt();
                        nClasss[i].skillTemplates[j].skills[k].dx = dis.readShort();
                        nClasss[i].skillTemplates[j].skills[k].dy = dis.readShort();
                        nClasss[i].skillTemplates[j].skills[k].maxFight = dis.readByte();
                        nClasss[i].skillTemplates[j].skills[k].damage = dis.readShort();
                        nClasss[i].skillTemplates[j].skills[k].price = dis.readShort();
                        nClasss[i].skillTemplates[j].skills[k].moreInfo = dis.readUTF();
                    }
                }
            }
            dis.close();
            dis = null;
            Util.warning("Finish load skill! [" + nClasss.length + "]\n");
        } catch (Exception e) {
            Util.logException(SkillData.class, e);
            System.exit(0);
        }
    }
}
