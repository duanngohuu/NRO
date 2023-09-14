package real.pet;

import real.map.Zone;
import real.map.Mob;
import real.map.MobTemplate;
import real.player.Player;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;

public final class MobMe extends Mob {

    private final Player player;
    private final long lastTimeSpawn;
    private final int timeSurvive;

    public MobMe(Player player) {
        this.player = player;
        this.id = player.id;
        int level = player.playerSkill.getSkillbyId(12).point;
        template = new MobTemplate();
        template.mobTemplateId = SkillUtil.getTempMobMe(level);
        template.hp = SkillUtil.getHPMobMe(player.point.getHPFull(), level);
        this.dame = SkillUtil.getDameMobMe(player.point.getBaseDame(), level);
        this.hp = this.template.hp;
        this.map = player.zone;
        this.lastTimeSpawn = System.currentTimeMillis();
        this.timeSurvive = SkillUtil.getTimeSurviveMobMe(player.playerSkill.getSkillbyId(12));
        spawn();
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(this.lastTimeSpawn, this.timeSurvive)) {
            this.mobMeDie();
        }
    }

    public void attack(Player pl, Mob mob) {
        Message msg = null;
        try {
            if(pl != null && pl.isBoss && pl.typePk != 5){
                return;
            }
            int dameHit = this.dame;
            if (player.inventory.checkSKH(132)) {
                dameHit = player.point.getDameAttack();
            }
            if (pl != null) {
                if (pl.point.getHP() > dameHit) {
                    dameHit = pl.injured(null, this, dameHit, true);
                    msg = new Message(-95);
                    msg.writer().writeByte(2);
                    msg.writer().writeInt(this.id);
                    msg.writer().writeInt(pl.id);
                    msg.writer().writeInt(dameHit);
                    msg.writer().writeInt(pl.point.getHP());
                    Service.gI().sendMessAllPlayerInMap(map, msg);
                    msg.cleanup();
                }
            }

            if (mob != null) {
                if (mob.gethp() > dameHit) {
                    mob.sethp(mob.gethp() - dameHit);
                    
                    msg = new Message(-95);
                    msg.writer().writeByte(3);
                    msg.writer().writeInt(this.id);
                    msg.writer().writeInt(mob.id);
                    msg.writer().writeInt(mob.gethp());
                    msg.writer().writeInt(dameHit);
                    Service.gI().sendMessAllPlayerInMap(map, msg);
                    msg.cleanup();
                    Service.gI().congTiemNang(player, (byte) 2, 2000);
                }
            }
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void spawn() {
        Message msg = null;
        try {
            //System.out.println("38");
            msg = new Message(-95);
            msg.writer().writeByte(0);//type
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(template.mobTemplateId);
            msg.writer().writeInt(hp);// hp mob
            Service.gI().sendMessAllPlayerInMap(map, msg);
            msg.cleanup();
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void goToMap(Zone map) {
        if (map != null) {
            this.removeMobInMap();
            this.map = map;
        }
    }

    public void removeMobInMap() {
        Message msg = null;
        try {
            msg = new Message(-95);
            msg.writer().writeByte(7);//type
            msg.writer().writeInt((int) player.id);
            Service.gI().sendMessAllPlayerNotMeInMap(this.map,player, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void mobMeDie() {
        Message msg = null;
        try {
            msg = new Message(-95);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            Service.gI().sendMessAllPlayerInMap(this.map, msg);
            // this.removeMobInMap();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
        player.mobMe = null;
    }
}
