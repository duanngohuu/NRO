package real.player;

import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.Zone;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;

public class BossManager extends Boss {
    private static int idb = -1000000;
    public BossManager() {
        super();
    }
    
    public static void UPDATE(Zone zone){
        try{
            for(BossTemplate boss : BossService.gI().players){
                if(boss.status == 0 && boss.map == zone.map.id){
                    BossManager player = new BossManager(){
                        @Override
                        public void startDie() {
                            if (playerKill != null) {
                                if(Util.isTrue(boss.tile)){
                                    Item it = ItemData.gI().get_item(590);
                                    it.itemOptions.clear();
                                    it.itemOptions.add(new ItemOption(31, 1));
                                    Service.gI().roiItem(it, zone, this.x, this.y, (playerKill == null ? -1 : Math.abs(playerKill.id)));
                                }
                            }
                        }
                    };
                    player.zone = zone;
                    player.name = boss.name;
                    player.head = boss.head;
                    player.body = boss.body;
                    player.leg = boss.leg;
                    player.point.hp = boss.hp;
                    player.point.hpGoc = boss.hp;
                    player.point.mp = boss.hp;
                    player.point.mpGoc = boss.hp;
                    player.point.dameGoc = boss.dame;
                    player.point.defGoc = (short)boss.def;
                    player.start(boss.cx, boss.cy, idb--);
                    player.active(50);
                }
            }
        }
        catch(Exception e){
        }
    }
    
    @Override
    public void update() {
        try {
            if (isDie()) {
                Service.gI().hsChar(this, point.getHPFull(), point.getMPFull());
                Service.gI().changeTypePK(this, 5);
                startDie();
                return;
            }
            if(!Util.canDoWithTime(currTime, 400)){
                return;
            }
            currTime = System.currentTimeMillis();
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000) && !isDie() && point.getHP() < point.getHPFull())
            {
                lastTimeHoiPhuc = System.currentTimeMillis();
                hoiphuc((int)point.getHPFull() / 10, point.getMPFull());
            }
            playerSkill.update();
            if (playerSkill.isHaveEffectSkill()) {
                return;
            }
            Player playerAttack = this.getPlayerAttack();
            if(playerAttack == null){
                int r = Util.nextInt(0, 1);
                int point = Util.nextInt(1, 10);
                move(this.cx + (r == 0 ? -point : point), this.cy);
                return;
            }
            int dis2 = Util.getDistance(this, playerAttack);
            if(dis2 <= 50){
                playerSkill.skillSelect = playerSkill.skills.get(Util.nextInt(0, 2));
                int r = Util.nextInt(0, 1);
                int point = Util.nextInt(1, 10);
                move(playerAttack.x + (r == 0 ? -point : point), playerAttack.y);
            }
            if(dis2 > 50)
            {
                int[] cx = new int[]{-100, 100};
                playerSkill.skillSelect = playerSkill.skills.get(Util.nextInt(3, 5));
                move(playerAttack.x + cx[Util.nextInt(0, 1)], playerAttack.y);
            }
            if(Util.isTrue(1)){
                playerSkill.skillSelect = playerSkill.skills.get(6);
                move(playerAttack.x, playerAttack.y);
            }
            playerSkill.useSkill(playerAttack, null);
        } catch (Exception e) {
        }
    }

    @Override
    protected void initSkill() {
        byte[] skillTemp = {Skill.DRAGON, Skill.GALIK, Skill.DEMON, Skill.MASENKO, Skill.ANTOMIC, Skill.KAMEJOKO, Skill.DICH_CHUYEN_TUC_THOI};
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i], 1,0);
            skill.coolDown = 20;
            skill.manaUse = 0;
            this.playerSkill.skills.add(skill);
        }
    }

    @Override
    public short getHead() {
        return this.head;
    }

    @Override
    public short getBody() {
        return this.body;
    }

    @Override
    public short getLeg() {
        return this.leg;
    }
    
    @Override
    public void startDie() {
    }
    
    private Skill getSkill(int indexSkill) {
        switch (indexSkill) {
            case 1:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 0 || temp == 2 || temp == 4) {
                        return skill;
                    }
                }
                return null;
            case 2:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 1 || temp == 3 || temp == 5) {
                        return skill;
                    }
                }
                return null;
            default:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 20) {
                        return skill;
                    }
                }
                return null;
        }
    }
}
