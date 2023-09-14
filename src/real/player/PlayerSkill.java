package real.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import real.func.DHVT_Template;
import real.func.PVP;
import real.func.UseItem;
import real.item.Item;
import real.item.ItemBua;
import real.map.MapManager;
import real.map.Mob;
import real.pet.MobMe;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;

public class PlayerSkill {

    private Player player;
    public List<Skill> skills;
    public Skill skillSelect;
    public short don_ke = 0;

    public PlayerSkill(Player player) {
        this.player = player;
        skills = new ArrayList<>();
    }

    public Skill getSkillbyId(int id) {
        for (Skill skill : skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }

    public byte[] skillShortCut = new byte[10];

    public void AnHoaDa(Player plHoaDa) {
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(1); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(42); //num3
            msg.writer().writeInt((int) plHoaDa.id);//num4
            msg.writer().writeInt((int) player.id);//num9
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendSkillShortCut() {
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte) 61);
            msg.writer().writeUTF("KSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
            msg = Service.gI().messageSubCommand((byte) 61);
            msg.writer().writeUTF("OSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
            msg = Service.gI().messageSubCommand((byte) 61);
            msg.writer().writeUTF("CSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void AnTroi(Player plAnTroi) {
        Message msg = null;
        try {
            if(plAnTroi.playerSkill.useTroi){
                plAnTroi.playerSkill.removeUseTroi();
                plAnTroi.playerSkill.removeAnTroi();
            }
            msg = new Message(-124);
            msg.writer().writeByte(1); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(32); //num3
            msg.writer().writeInt((int) plAnTroi.id);//num4
            msg.writer().writeInt((int) player.id);//num9
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void update() {
        try{
            if (player.isPl() && Util.canDoWithTime(player.lastTimeGTL, 60000) && player.inventory.itemsBody.get(6) == null){
                player.lastTimeGTL = System.currentTimeMillis();
                if (player.inventory.getItemGTL(9) != null && player.combine.getParam(player.inventory.getItemGTL(9), 9) > 0 && player.combine.addParam(player.inventory.getItemGTL(9), 9, -1)){
                    player.inventory.sendItemBags();
                    Service.gI().point(player);
                }
            }
            if (isUpMonkey && (Util.canDoWithTime(lastUpMonkey, 1000) || !player.isPl())) {
                monkeyUp();
            }
            if (isMonkey && (Util.canDoWithTime(lastTimeMonkey, timeMonkey * (player.inventory.checkSKH(134) ? 5 : 1)) || player.isDie())) {
                monkeyDown();
            }
            if (isShielding && (Util.canDoWithTime(lastTimeShieldUp, timeShield) || player.isDie())) {
                shieldDown();
            }
            if (useTroi && (Util.canDoWithTime(lastTimeTroi, timeTroi) || player.isDie())) {
                removeUseTroi();
            }
            if (anTroi && (Util.canDoWithTime(lastTimeAnTroi, timeAnTroi) || player.isDie())) {
                removeAnTroi();
            }
            if (isStun && (Util.canDoWithTime(lastTimeStartStun, timeStun) || player.isDie())) {
                removeStun();
            }
            if (isBinh && (Util.canDoWithTime(lastTimeStartBinh, timeBinh) || player.isDie())) {
                removeBinh();
            }
            if (isThoiMien && (Util.canDoWithTime(lastTimeThoiMien, timeThoiMien) || player.isDie())) {
                removeThoiMien();
            }
            if (isBlindDCTT && (Util.canDoWithTime(lastTimeBlindDCTT, timeBlindDCTT) || player.isDie())) {
                removeBlindDCTT();
            }
            if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola))) {
                removeSocola();
            }
            if (isHoaDa && (Util.canDoWithTime(lastTimeHoaDa, timeHoaDa))) {
                removeHoaDa();
            }
            if (isXinbato && (Util.canDoWithTime(lastTimeXinbato, timeXinbato))) {
                removeXinbato();
            }
            if (isMaTroi && (Util.canDoWithTime(lastTimeMaTroi, timeMaTroi))) {
                removeCaRot();
            }
            if (tiLeHPHuytSao != 0 && Util.canDoWithTime(lastTimeHuytSao, 30000)) {
                removeHuytSao();
            }
        }
        catch (Exception e) {
            Util.debug("-------------PlayerSkill.Update-------------");
            e.printStackTrace();
        }
    }

    public void useSkill(Player plTarget, Mob mobTarget) {
        try
        {
            if (player.isDie() || MapManager.gI().isMapOffline(player.zone.map.id)) {
                return;
            }
            if (player.isPl() && mobTarget != null && (ItemBua.ItemBuaExits(player, 217)) && player.point.hp == 1) {
                Service.gI().sendThongBao(player, "Bạn đang được bùa bất tử bảo vệ không thể tấn công quái");
                return;
            }
            if(player.point.stamina <= 0){
                if(player.isPl()){
                    Service.gI().sendThongBao(player, "Thể lực đã cạn cần nghỉ ngơi để hồi phục thể lực");
                    return;
                }
            }
            if (player.playerSkill.skillSelect == null) {
                player.playerSkill.skillSelect = player.playerSkill.skills.get(0);
            }
            if (isHaveEffectSkill() && player.playerSkill.skillSelect.template.id != Skill.TU_SAT && player.playerSkill.skillSelect.template.id != Skill.QUA_CAU_KENH_KHI && player.playerSkill.skillSelect.template.id != Skill.MAKANKOSAPPO) {
                return;
            }
            if(plTarget != null && plTarget.isNewPet){
                return;
            }
            if (plTarget != null && plTarget.isDie() && player.playerSkill.skillSelect.template.id != Skill.TRI_THUONG || mobTarget != null && mobTarget.isDie()) {
                return;
            }
            if (plTarget != null && player.playerSkill.skillSelect.template.id != Skill.TU_SAT && player.playerSkill.skillSelect.template.id != Skill.TRI_THUONG)
            {
                PVP pvp1 = PVP.gI().findPvp(plTarget);
                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(plTarget);
                if(plTarget.zone.isNRSD() && player.zone.isNRSD() && player.cFlag == plTarget.cFlag)
                {
                    return;
                }
                else if (player.isPl() && !(plTarget.isBoss || (pvp1 != null && (pvp1.player1.equals(player) || pvp1.player2.equals(player))) || player.typePk == 5 || plTarget.typePk == 5 || (player.cFlag != 0 && plTarget.cFlag != 0 && (plTarget.cFlag != player.cFlag || player.cFlag == 8 || plTarget.cFlag == 8))))
                {
                    return;
                }
                else if(pvp1 != null && (pvp1.player1.equals(player) || pvp1.player2.equals(player)) && (player.typePk == 0 || plTarget.typePk == 0))
                {
                    return;
                }
                else if(player.zone.map.id == 51 && dhvt_tem != null && dhvt_tem.attack == -1)
                {
                    return;
                }
                else if(player.isPl() && player.zone.map.id == 129 && player.DHVT_23 != null && player.DHVT_23.next != 3)
                {
                    return;
                }
            }
            if (!player.playerSkill.nemQCKK && !player.playerSkill.tuSat && !player.playerSkill.laze && !Util.canDoWithTime(this.skillSelect.lastTimeUseThisSkill, this.getCoolDown())) {
                return;
            }
            
            if (!player.playerSkill.nemQCKK && !player.playerSkill.tuSat && !player.playerSkill.laze && !player.setMPUseSkill())
            {
                if (!player.isPet) {
                    Service.gI().sendThongBao(player, "Không đủ KI để sử dụng!");
                } else {
                    player.SkillUser = 1;
                }
                return;
            }
            if (player.playerSkill.useTroi && player.playerSkill.skillSelect.template.id != 8)
            {
                player.playerSkill.removeUseTroi();
                player.playerSkill.removeAnTroi();
            }
            if(player.playerSkill.skillSelect.template.id != 8 && player.playerSkill.count_ttnl != -1)
            {
                player.playerSkill.stopCharge();
            }
            if (plTarget != null && plTarget.isBoss && plTarget.typePk != 3 && plTarget.zone.map.id == 129)
            {
                return;
            }
            long timeskills = 0;
            int c = 0;
            Message msg = null;
            List<Mob> mobs;
            List<Player> players;
            boolean miss = false;
            if (player.id > 0) {
                if (plTarget != null) {
                    if (Util.getDistance(player, plTarget) > skillSelect.dx * 2) {
                        miss = true;
                    }
                }
                if (mobTarget != null) {
                    if (Util.getDistance(player, mobTarget) > skillSelect.dx * 2) {
                        miss = true;
                    }
                }
            }
            switch (player.playerSkill.skillSelect.template.id) {
                case Skill.KAIOKEN:
                    int hpUse = player.point.getHPFull() / 100 * (player.isPet ? 5 : 10);
                    if (player.point.getHP() <= hpUse) {
                        break;
                    }
                    if (plTarget != null) {
                        if(Util.canDoWithTime(player.lastTimeRevived, 3000) && Util.canDoWithTime(plTarget.lastTimeRevived, 3000)){
                            if(plTarget.playerSkill.anTroi){
                                player.point.setCrit100();
                            }
                            plTarget.attackPlayer(player, miss);
                        }
                    }
                    else if (mobTarget != null) {
                        if(Util.canDoWithTime(player.lastTimeRevived, 3000)){
                            if(mobTarget.isAnTroi){
                                player.point.setCrit100();
                            }
                            mobTarget.attackMob(player, miss);
                        }
                    }
                    if (player.mobMe != null) {
                        if(Util.canDoWithTime(player.lastTimeRevived, 3000)){
                            player.mobMe.attack(plTarget, mobTarget);
                        }
                    }
                    player.point.setHP(player.point.getHP() - hpUse);
                    Service.gI().sendInfoChar30c4(player);
                    Service.gI().Send_Info_NV(player);
                    break;
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALIK:
                case Skill.LIEN_HOAN:
                case Skill.ANTOMIC:
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                    if (plTarget != null) {
                        if(Util.canDoWithTime(player.lastTimeRevived, 3000) && Util.canDoWithTime(plTarget.lastTimeRevived, 3000)){
                            if(plTarget.playerSkill.anTroi){
                                player.point.setCrit100();
                            }
                            plTarget.attackPlayer(player, miss);
                        }
                    }
                    else if (mobTarget != null) {
                        if(Util.canDoWithTime(player.lastTimeRevived, 3000)){
                            if(mobTarget.isAnTroi){
                                player.point.setCrit100();
                            }
                            mobTarget.attackMob(player, miss);
                        }
                    }
                    if (player.mobMe != null) {
                        if(Util.canDoWithTime(player.lastTimeRevived, 3000)){
                            player.mobMe.attack(plTarget, mobTarget);
                        }
                    }
                    break;
                case Skill.THAI_DUONG_HA_SAN: //thái dương hạ san
                    mobs = new ArrayList<>();
                    players = new ArrayList<>();
                    if (player.isBoss) {
                        for (Player pl : player.zone.getPlayers()) {
                            if (pl != null && !pl.isDie() && pl != player && !pl.isBoss && !pl.inventory.OptionCt(116)) {
                                players.add(pl);
                            }
                        }
                    } else {
                        for (Mob mob : player.zone.mobs) {
                            if (Util.getDistance(player, mob) <= SkillUtil.getRangeStun(skillSelect) && player.mobMe != mob) {
                                mobs.add(mob);
                            }
                        }
                        for (Player pl : player.zone.getPlayers()) {
                            if (pl != null && !pl.isDie() && pl != player && ((!player.isPet && player.pet != pl) || (player.isPet && pl.id + player.id != 0))) {
                                PVP pvp = PVP.gI().findPvp(pl);
                                if (pvp != null && pl.isPl() && !pl.inventory.OptionCt(116) && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    players.add(pl);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(plTarget);
                                if (dhvt_tem != null && pl.isPl() && !pl.inventory.OptionCt(116) && (dhvt_tem.player1.equals(player) || dhvt_tem.player2.equals(player))) {
                                    players.add(pl);
                                    continue;
                                }
                                if (pl.isBoss && pl.typePk == 5) {
                                    players.add(pl);
                                    continue;
                                }
                                if (!pl.inventory.OptionCt(116) && (pl.typePk == 5 || (player.cFlag != 0 && pl.cFlag != 0 && (pl.cFlag != player.cFlag || player.cFlag == 8)))) {
                                    if (Util.getDistance(player, pl) <= SkillUtil.getRangeStun(skillSelect) && pl != player) {
                                        players.add(pl);
                                    }
                                }
                            }
                        }
                    }
                    try {
                        msg = new Message(-45);
                        msg.writer().writeByte(0);
                        msg.writer().writeInt((int) player.id);
                        msg.writer().writeShort(skillSelect.skillId);

                        int blindTime = SkillUtil.getTimeStun(skillSelect);
                        if (player.inventory.checkSKH(127)) {
                            blindTime *= 2;
                        }
                        msg.writer().writeByte(mobs.size());
                        for (Mob mob : mobs) {
                            msg.writer().writeByte(mob.id);
                            msg.writer().writeByte(blindTime / 1000);
                            mob.startStun(System.currentTimeMillis(), blindTime);
                        }

                        msg.writer().writeByte(players.size());
                        for (Player pl : players) {
                            msg.writer().writeInt((int) pl.id);
                            msg.writer().writeByte(blindTime / 1000);
                            pl.playerSkill.startStun(System.currentTimeMillis(), blindTime);
                        }
                        Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                        msg.cleanup();
                    } catch (Exception e) {
                    }
                    if (player.speacial[0] == 1) {
                        c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                        timeskills = System.currentTimeMillis();
                    }
                    break;
                case Skill.TRI_THUONG: //trị thương
                    Player plTriThuong = null;
                    for (Player pl : player.zone.getPlayers()) {
                        if (pl != null && pl != player && pl.zone.zoneId == player.zone.zoneId && !pl.isNewPet && !pl.isBoss && !plTarget.isBoss && !plTarget.isNewPet) {
                            if (skillSelect.point > 1) {
                                plTriThuong = pl;
                            } else if (skillSelect.point == 1) {
                                plTriThuong = plTarget;
                            }
                            int percentTriThuong = SkillUtil.getPercentTriThuong(skillSelect);
                            Service.gI().chat(plTriThuong, "Cảm ơn " + player.name + " đã cứu mình");
                            try {
                                msg = new Message(-60);
                                msg.writer().writeInt((int) player.id); //id pem
                                msg.writer().writeByte(skillSelect.skillId); //skill pem
                                msg.writer().writeByte(1); //số người pem
                                msg.writer().writeInt((int) plTriThuong.id); //id ăn pem
                                msg.writer().writeByte(0); //read continue
                                msg.writer().writeByte(skillSelect.template.type); //type skill
                                Service.gI().sendMessAllPlayerInMap(plTriThuong.zone, msg);
                                msg.cleanup();
                                boolean isDie = plTriThuong.isDie();
                                player.point.setHP(player.point.getHP() + (player.point.getHPFull() * percentTriThuong / 100));
                                plTriThuong.point.setHP(plTriThuong.point.getHP() + (plTriThuong.point.getHPFull() * percentTriThuong / 100));
                                plTriThuong.point.setMP(plTriThuong.point.getMP() + (plTriThuong.point.getMPFull() * percentTriThuong / 100));
                                if (isDie) {
                                    Service.gI().Send_Info_NV(player);
                                    Service.gI().hsChar(plTriThuong, plTriThuong.point.getHP(), plTriThuong.point.getMP());
                                    Service.gI().sendInfoChar30c4(plTriThuong);
                                    player.sendInfoHPMP();
                                } else {
                                    Service.gI().Send_Info_NV(player);
                                    plTriThuong.sendInfoHPMP();
                                    player.sendInfoHPMP();
                                }
                                Service.gI().Send_Info_NV(plTriThuong);
                            } catch (Exception e) {
    //                            e.printStackTrace();
                            }
                        }
                    }
                    if (player.speacial[0] == 3)
                    {
                        c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                        timeskills = System.currentTimeMillis();
                    }
                    break;
                case Skill.TAI_TAO_NANG_LUONG: //tái tạo năng lượng
                    charge();
                    return;
                case Skill.QUA_CAU_KENH_KHI: //quả cầu kênh khi
                    if (nemQCKK) {
                        nemQCKK = false;
                        if (plTarget != null) {
                            try {
                                msg = new Message(-60);
                                msg.writer().writeInt((int) player.id); //id pem
                                msg.writer().writeByte(skillSelect.skillId); //skill pem
                                msg.writer().writeByte(1); //số người pem
                                msg.writer().writeInt((int) plTarget.id); //id ăn pem
                                msg.writer().writeByte(1); //read continue
                                msg.writer().writeByte(0); //type skill
                                int dameHit = plTarget.injured(player, null, player.point.getDameAttack());
                                msg.writer().writeInt(dameHit); //dame ăn
                                msg.writer().writeBoolean(false); //is die
                                msg.writer().writeBoolean(player.point.isCrit); //crit
                                Service.gI().sendMessAllPlayerInMap(plTarget.zone, msg);
                                msg.cleanup();
                                plTarget.sendInfo();
                            } catch (Exception e) {
                            }
                        }
                        for (Mob mob : player.zone.mobs) {
                            if (Util.getDistance(player, mob) <= 1000 && mob.template.mobTemplateId != 0) {
                                mob.injured(player, player.point.getDameAttack(), true);
                            }
                        }
                        if (player.speacial[0] == 2) {
                            c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                            timeskills = System.currentTimeMillis();
                        }
                        break;
                    } else {
                        try {
                            nemQCKK = true;
                            msg = new Message(-45);
                            msg.writer().writeByte(4);
                            msg.writer().writeInt((int) player.id);
                            msg.writer().writeShort(skillSelect.skillId);
                            msg.writer().writeShort(3000);
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();
                            return;
                        } catch (Exception e) {
                        }
                    }
                    break;
                case Skill.MAKANKOSAPPO: //makankosappo
                    if (laze) {
                        laze = false;
                        if (plTarget != null) {
                            try {
                                msg = new Message(-60);
                                msg.writer().writeInt((int) player.id); //id pem
                                msg.writer().writeByte(skillSelect.skillId); //skill pem
                                msg.writer().writeByte(1); //số người pem
                                msg.writer().writeInt((int) plTarget.id); //id ăn pem

                                msg.writer().writeByte(1); //read continue
                                msg.writer().writeByte(0); //type skill

                                int dameHit = plTarget.injured(player, null, player.point.getDameAttack(true));
                                msg.writer().writeInt(dameHit); //dame ăn
                                msg.writer().writeBoolean(false); //is die
                                msg.writer().writeBoolean(player.point.isCrit); //crit
                                Service.gI().sendMessAllPlayerInMap(plTarget.zone, msg);
                                msg.cleanup();
                                plTarget.sendInfo();
                            } catch (Exception e) {
                            }
                        } else if (mobTarget != null) {
                            mobTarget.attackMob(player, false);
                        }
                        if (player.speacial[0] == 4) {
                            c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                            timeskills = System.currentTimeMillis();
                        }
                    } else {
                        try {
                            laze = true;
                            msg = new Message(-45);
                            msg.writer().writeByte(4);
                            msg.writer().writeInt((int) player.id);
                            msg.writer().writeShort(skillSelect.skillId);
                            msg.writer().writeShort(2500);
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();
                            return;
                        } catch (Exception e) {
                        }
                    }
                    break;
                case Skill.DE_TRUNG: //đẻ trứng
                    try {
                        sendEffectUseSkill();
                        if (player.mobMe != null) {
                            player.mobMe.mobMeDie();
                        }
                        player.mobMe = new MobMe(player);
                    } catch (Exception e) {
                    }
                    if (player.speacial[0] == 5) {
                        c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                        timeskills = System.currentTimeMillis();
                    }
                    break;
                case Skill.BIEN_KHI: //biến khỉ
                    try {
                        if (!isMonkey) {
                            msg = new Message(-45);
                            msg.writer().writeByte(6);
                            msg.writer().writeInt((int) player.id);
                            msg.writer().writeShort(skillSelect.skillId);
                            Service.gI().sendMessAllPlayerInMap(this.player.zone, msg);
                            msg.cleanup();
                            lastUpMonkey = lastTimeMonkey = System.currentTimeMillis();
                            isUpMonkey = true;
                        }
                    } catch (Exception e) {
    //                    e.printStackTrace();
                    }
                    break;
                case Skill.TU_SAT: //tự sát
                    try {
                        if (tuSat) {
                            tuSat = false;
                            player.point.setHP(0);
                            int hpbom = player.point.getHPFull();
                            int range = SkillUtil.getRangeBom(skillSelect);
                            for (Mob mob : player.zone.mobs) {
                                hpbom = player.point.getHPFull();
                                if (Util.getDistance(player, mob) <= range && mob.template.mobTemplateId != 0) {
                                    mob.injured(player, hpbom, true);
                                }
                            }
                            for (Player pl : player.zone.getPlayers()) {
                                PVP pvp = PVP.gI().findPvp(pl);
                                if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    hpbom = player.point.getHPFull();
                                    pl.injured(player, null, hpbom);
                                    pl.sendInfoHPMP();
                                    Service.gI().Send_Info_NV(pl);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(pl);
                                if (dhvt_tem != null && (dhvt_tem.player1.equals(player) || dhvt_tem.player2.equals(player))) {
                                    hpbom = player.point.getHPFull();
                                    pl.injured(player, null, hpbom);
                                    pl.sendInfoHPMP();
                                    Service.gI().Send_Info_NV(pl);
                                    continue;
                                }
                                if (pl.isBoss && pl.typePk == 5) {
                                    hpbom = player.point.getHPFull(false);
                                    int hpBom = hpbom / 2;
                                    pl.injured(player, null, hpBom);
                                    Service.gI().Send_Info_NV(pl);
                                    continue;
                                }
                                if (pl != player && player.cFlag != 0 && pl.cFlag != 0 && (pl.cFlag != player.cFlag || player.cFlag == 8)) {
                                    if (Util.getDistance(player, pl) <= range) {
                                        hpbom = player.point.getHPFull();
                                        pl.injured(player, null, hpbom);
                                        pl.sendInfoHPMP();
                                        Service.gI().Send_Info_NV(pl);
                                    }
                                }
                            }
                            if (player.speacial[0] == 3) {
                                c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                                timeskills = System.currentTimeMillis();
                            }
                        } else {
                            try {
                                tuSat = true;
                                msg = new Message(-45);
                                msg.writer().writeByte(7);
                                msg.writer().writeInt((int) player.id);
                                msg.writer().writeShort(skillSelect.skillId);
                                msg.writer().writeShort(2000);
                                Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                                msg.cleanup();
                                return;
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                    break;
                case Skill.SOCOLA: //socola
                    try {
                        sendEffectUseSkill();
                        int timeSocola = 5000;
                        if (plTarget != null) {
                            plTarget.playerSkill.setSocola(System.currentTimeMillis(), timeSocola);
                            Service.gI().sendItemTime(plTarget, 4134, timeSocola / 1000);
                            Service.gI().Send_Caitrang(plTarget);
                        }
                        else {
                            msg = new Message(-112);
                            msg.writer().writeByte(1);
                            msg.writer().writeByte(mobTarget.id); //b4
                            msg.writer().writeShort(4133);//b5
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();
                            mobTarget.setSocola(System.currentTimeMillis(), timeSocola);
                        }
                    } catch (Exception e) {
                    }
                    if (player.speacial[0] == 7) {
                        don_ke = player.speacial[1];
                    }
                    break;
                case Skill.KHIEN_NANG_LUONG: //khiên năng lượng
                    isShielding = true;
                    lastTimeShieldUp = System.currentTimeMillis();
                    timeShield = SkillUtil.getTimeShield(skillSelect);
                    try {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(0); //b6
                        msg.writer().writeByte(33); //num3
                        msg.writer().writeInt((int) player.id); //num4
                        Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                        msg.cleanup();

                        Service.gI().sendItemTime(player, 3784, timeShield / 1000);
                        if ((player.speacial[0] == 3 && player.gender == 0) || (player.speacial[0] == 6 && player.gender == 1) || (player.speacial[0] == 5 && player.gender == 2)) {
                            c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                            timeskills = System.currentTimeMillis();
                        }
                    } catch (Exception e) {
                    }
                    break;
                case Skill.DICH_CHUYEN_TUC_THOI: //dịch chuyển tức thời
                    int timeDCTT = SkillUtil.getTimeDCTT(skillSelect);
                    if (mobTarget != null) {
                        try {
                            mobTarget.setStartBlindDCTT(System.currentTimeMillis(), timeDCTT);
                            mobTarget.attackMob(player, false);
                            Service.gI().setPos(player, mobTarget.pointX, mobTarget.pointY, 1);
                            msg = new Message(-124);
                            msg.writer().writeByte(1);
                            msg.writer().writeByte(1);
                            msg.writer().writeByte(40);
                            msg.writer().writeByte(mobTarget.id);
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();

                        } catch (Exception e) {

                        }
                    } else if (plTarget != null) {
                        try {
                            plTarget.playerSkill.setBlindDCTT(System.currentTimeMillis(), timeDCTT, plTarget.id);
                            Service.gI().setPos(player, plTarget.x, plTarget.y, 1);
                            msg = new Message(-60);
                            msg.writer().writeInt((int) player.id); //id pem
                            msg.writer().writeByte(skillSelect.skillId); //skill pem
                            msg.writer().writeByte(1); //số người pem
                            msg.writer().writeInt((int) plTarget.id); //id ăn pem

                            msg.writer().writeByte(1); //read continue
                            msg.writer().writeByte(0); //type skill

                            int dameHit = plTarget.injured(player, null, player.point.getDameAttack(true)/10);
                            msg.writer().writeInt(dameHit); //dame ăn
                            msg.writer().writeBoolean(false); //is die
                            msg.writer().writeBoolean(player.point.isCrit); //crit
                            Service.gI().sendMessAllPlayerInMap(plTarget.zone, msg);
                            msg.cleanup();
                            plTarget.sendInfo();
                            Service.gI().point(plTarget);
                        } catch (Exception e) {

                        }
                    }
                    if (player.speacial[0] == 6) {
                        don_ke = player.speacial[1];
                    }
                    break;
                case Skill.HUYT_SAO: //huýt sáo
                    int HP = SkillUtil.getPercentHPHuytSao(skillSelect);
                    for (Player pl : player.zone.getPlayers()) {
                        if (pl != null && !pl.isBoss && !pl.isNewPet && pl.gender != 1 && pl.cFlag == player.cFlag) {
                            pl.playerSkill.setHuytSao(HP);
                            try {
                                msg = new Message(-124);
                                msg.writer().writeByte(1); //b5
                                msg.writer().writeByte(0); //b6
                                msg.writer().writeByte(39); //num3
                                msg.writer().writeInt((int) pl.id); //num4
                                Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                                msg.cleanup();

                                Service.gI().point(pl);
                                Service.gI().Send_Info_NV(pl);
                                Service.gI().sendItemTime(pl, 3781, 31);

                                msg = Service.gI().messageSubCommand((byte) 5);
                                msg.writer().writeInt(pl.point.getHP());
                                pl.sendMessage(msg);
                                msg.cleanup();
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (player.speacial[0] == 4) {
                        c = skillSelect.coolDown - (skillSelect.coolDown / 100) * player.speacial[1];
                        timeskills = System.currentTimeMillis();
                    }
                    break;
                case Skill.THOI_MIEN: //thôi miên
                    int timeThoiMien = SkillUtil.getTimeThoiMien(skillSelect);
                    if (plTarget != null) {
                        try {
                            msg = new Message(-124);
                            msg.writer().writeByte(1); //b5
                            msg.writer().writeByte(0); //b6
                            msg.writer().writeByte(41); //num3
                            msg.writer().writeInt((int) plTarget.id); //num4
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();
                            Player plAnThoiMien = player.zone.getPlayerInMap((int) plTarget.id);
                            plAnThoiMien.playerSkill.setThoiMien(System.currentTimeMillis(), timeThoiMien);
                            Service.gI().sendItemTime(plAnThoiMien, 3782, timeThoiMien / 1000);
                        } catch (Exception e) {
                        }
                    } else if (mobTarget != null) {
                        try {
                            msg = new Message(-124);
                            msg.writer().writeByte(1); //b5
                            msg.writer().writeByte(1); //b6
                            msg.writer().writeByte(41); //num6
                            msg.writer().writeByte(mobTarget.id); //b7
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();
                            mobTarget.setThoiMien(System.currentTimeMillis(), timeThoiMien);
                        } catch (Exception e) {
    //                        e.printStackTrace();
                        }
                    }
                    if (player.speacial[0] == 4) {
                        don_ke = player.speacial[0];
                    }
                    break;
                case Skill.TROI: //trói
                    int timeTroiz = SkillUtil.getTimeTroi(skillSelect);
                    setUseTroi(System.currentTimeMillis(), timeTroiz);
                    sendEffectUseSkill();
                    if (plTarget != null) {
                        try {
                            plAnTroi = plTarget;
                            AnTroi(plAnTroi);
                            plAnTroi.playerSkill.setAnTroi(player, System.currentTimeMillis(), timeTroiz);
                        } catch (Exception e) {
                        }
                    } else if (mobTarget != null) {
                        try {
                            mobAnTroi = mobTarget;
                            msg = new Message(-124);
                            msg.writer().writeByte(1); //b4
                            msg.writer().writeByte(1);//b5
                            msg.writer().writeByte(32);//num8
                            msg.writer().writeByte(mobTarget.id);//b6
                            msg.writer().writeInt((int) player.id);//num9
                            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                            msg.cleanup();
                            mobAnTroi.setTroi(System.currentTimeMillis(), timeTroiz);
                        } catch (Exception e) {
                        }
                    }
                    if (player.speacial[0] == 6) {
                        don_ke = player.speacial[1];
                    }
                    break;
            }
            if(c > 0 && timeskills > 0){
                this.skillSelect.lastTimeUseThisSkill = timeskills - (this.getCoolDown() - c);
                Service.gI().HoiSkill(player, skillSelect.skillId, c);
            }else{
                this.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
            }
            
            this.nemQCKK = false;
            this.tuSat = false;
            this.laze = false;
            if (player.isPl() && Util.canDoWithTime(player.lastTimeGTL, 60000) && player.inventory.itemsBody.get(6) != null){
                player.lastTimeGTL = System.currentTimeMillis();
                if(player.combine.addParam(player.inventory.itemsBody.get(6), 9, 1))
                {
                    player.inventory.sendItemBody();
                    Service.gI().Send_Caitrang(player);
                }
            }
        } 
        catch (Exception e)
        {
            this.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
        }
    }
    public int timeSkill = 3000;
    public int dir = -1;
    public int dx = -1;
    public int dy = -1;
    public int x = -1;
    public int y = -1;
    public List<Player> playerTaget = new ArrayList();
    public List<Mob> mobTaget = new ArrayList();
    // Trái đất
    public boolean isKame = false;
    public long lastTimeKame;
    public int stepKame;
    // namếc
    public boolean isMafoba = false;
    public long lastTimeMafoba;
    public int stepMafoba;
    // Xayda
    public boolean isLienHoanChuong = false;
    public long lastTimeLienHoanChuong;
    public int stepLienHoanChuong;
    
    public void useSkill(short cx, short cy, byte dir, short x, short y)
    {
        try
        {
            if(!this.skillSelect.template.isSkillSpec())
            {
                return;
            }
            if (player.isDie() || MapManager.gI().isMapOffline(player.zone.map.id))
            {
                return;
            }
            if (player.isPl() && (ItemBua.ItemBuaExits(player, 217)) && player.point.hp == 1)
            {
                Service.gI().sendThongBao(player, "Bạn đang được bùa bất tử bảo vệ không thể tấn công quái");
                return;
            }
            if(player.point.stamina <= 0)
            {
                if(player.isPl())
                {
                    Service.gI().sendThongBao(player, "Thể lực đã cạn cần nghỉ ngơi để hồi phục thể lực");
                    return;
                }
            }
            if (isHaveEffectSkill())
            {
                return;
            }
            if (!Util.canDoWithTime(this.skillSelect.lastTimeUseThisSkill, this.getCoolDown()))
            {
                return;
            }
            
            if (!player.setMPUseSkill())
            {
                Service.gI().sendThongBao(player, "Không đủ KI để sử dụng!");
                return;
            }
            this.dir = dir;
            this.x = x;
            this.y = y;
            this.dx = skillSelect.dx;
            this.dy = skillSelect.dy;
            Message msg = null;
            int rangeDame = skillSelect.dx;
            this.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
            if(this.skillSelect.curExp < 1000)
            {
                this.skillSelect.curExp += 1;
                Service.gI().sendUpdateSkillPlayer(player, skillSelect);
            }
            switch (player.playerSkill.skillSelect.template.id)
            {
                case Skill.SUPER_KAMEJOKO:
                    try
                    {
                        msg = new Message(-45);
                        msg.writer().writeByte(20);
                        msg.writer().writeInt(player.id);
                        msg.writer().writeShort(skillSelect.template.id);
                        msg.writer().writeByte(1); // typeFrame
                        msg.writer().writeByte(dir); // dir
                        msg.writer().writeShort(2000); // timeGong
                        msg.writer().writeByte(0); // isFly
                        msg.writer().writeByte(player.gender); // typePaint
                        msg.writer().writeByte(0); // typeItem
                        Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                        isKame = true;
                        lastTimeKame = System.currentTimeMillis();
                        active(300);
                    }
                    catch (Exception e)
                    {
//                        Util.logException(PlayerSkill.class, e);
                    }
                    finally
                    {
                        if (msg != null)
                        {
                            msg.cleanup();
                            msg = null;
                        }
                    }
                    break;
                case Skill.MA_PHONG_BA:
                    try
                    {
                        msg = new Message(-45);
                        msg.writer().writeByte(20);
                        msg.writer().writeInt((int) player.id);
                        msg.writer().writeShort(skillSelect.template.id);
                        msg.writer().writeByte(3); // typeFrame
                        msg.writer().writeByte(dir); // dir
                        msg.writer().writeShort(2000); // timeGong
                        msg.writer().writeByte(0); // isFly
                        msg.writer().writeByte(player.gender); // typePaint
                        msg.writer().writeByte(0); // typeItem
                        Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                        isMafoba = true;
                        lastTimeMafoba = System.currentTimeMillis();
                        active(300);
                    }
                    catch (Exception e)
                    {
                        Util.logException(PlayerSkill.class, e);
                    }
                    finally
                    {
                        if (msg != null) {
                            msg.cleanup();
                            msg = null;
                        }
                    }
                    break;
                case Skill.LIEN_HOAN_ANTOMIC:
                    try
                    {
                        msg = new Message(-45);
                        msg.writer().writeByte(20);
                        msg.writer().writeInt((int) player.id);
                        msg.writer().writeShort(skillSelect.template.id);
                        msg.writer().writeByte(2); // typeFrame
                        msg.writer().writeByte(dir); // dir
                        msg.writer().writeShort(2000); // timeGong
                        msg.writer().writeByte(0); // isFly
                        msg.writer().writeByte(0); // typePaint
                        msg.writer().writeByte(0); // typeItem
                        Service.gI().sendMessAllPlayerInMap(player.zone, msg);
                        int dx = SkillUtil.getRangeBom(skillSelect);
                        int ren = Math.abs(player.x - this.x);
                        if(ren <= dx)
                        {
                            this.x = player.x + (dir == -1 ? -ren : ren);
                            this.dx = ren;
                        }
                        else
                        {
                            this.x = player.x + (dir == -1 ? -dx : dx);
                            this.y = player.y;
                            this.dx = dx;
                        }
                        isLienHoanChuong = true;
                        lastTimeLienHoanChuong = System.currentTimeMillis();
                        active(300);
                    }
                    catch (Exception e)
                    {
                        Util.logException(PlayerSkill.class, e);
                    }
                    finally
                    {
                        if (msg != null)
                        {
                            msg.cleanup();
                            msg = null;
                        }
                    }
                    break;
            }
        }
        catch (Exception e)
        {
            Util.logException(PlayerSkill.class, e);
        }
        
    }
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;
    
    public void close() {
        try {
            isLienHoanChuong = false;
            isMafoba = false;
            isKame = false;
            actived = false;
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
        } catch (Exception e) {
            task = null;
            timer = null;
        }
    }
    
    public void active(int delay) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    PlayerSkill.this.updateSkill();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
    
    public void updateSkill()
    {
        if(isKame)
        {
            SkillKame();
        }
        else if(isMafoba)
        {
            SkillMafoba();
        }
        else if(isLienHoanChuong)
        {
            SkillLienHoanChuong();
        }
    }
    
    public void SkillKame()
    {
        if(stepKame == 0 && Util.canDoWithTime(lastTimeKame, 2000))
        {
            lastTimeKame = System.currentTimeMillis();
            stepKame = 1;
            SetSkillKame();
        }
        if(stepKame == 1 && !Util.canDoWithTime(lastTimeKame, timeSkill))
        {
            for(Player player : player.zone.players)
            {
                if(dir == -1 && player.x <= this.player.x - 20)
                {
                    if(Math.abs(this.player.x - player.x) <= dx && Math.abs(this.player.y - player.y) <= dy)
                    {
                        if(!this.player.isDie())
                        {
                            if(!player.isDie())
                            {
                                PVP pvp = PVP.gI().findPvp(this.player);
                                if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(player);
                                if (dhvt_tem != null && (dhvt_tem.player1.equals(this.player) || dhvt_tem.player2.equals(player))) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                if (player.isBoss && player.typePk == 5) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                if (player != this.player && this.player.cFlag != 0 && player.cFlag != 0 && (this.player.cFlag != player.cFlag || this.player.cFlag == 8)) {
                                    player.attackPlayer(this.player, false, 1);
                                }
                            }
                        }
                    }
                }
                if(dir == 1 && player.x >= this.player.x + 20)
                {
                    if(Math.abs(this.player.x - player.x) <= dx && Math.abs(this.player.y - player.y) <= dy)
                    {
                        if(!this.player.isDie())
                        {
                            if(!player.isDie())
                            {
                                PVP pvp = PVP.gI().findPvp(this.player);
                                if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(player);
                                if (dhvt_tem != null && (dhvt_tem.player1.equals(this.player) || dhvt_tem.player2.equals(player))) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                if (player.isBoss && player.typePk == 5) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                if (player != this.player && this.player.cFlag != 0 && player.cFlag != 0 && (this.player.cFlag != player.cFlag || this.player.cFlag == 8)) {
                                    player.attackPlayer(this.player, false, 1);
                                }
                            }
                        }
                    }
                }
            }
            for(Mob mob : player.zone.mobs)
            {
                if(dir == -1 && mob.cx <= this.player.x - 20)
                {
                    if(Math.abs(this.player.x - mob.cx) <= dx && Math.abs(this.player.y - mob.cy) <= dy)
                    {
                        mob.attackMob(player, false, 1);
                    }
                }
                if(dir == 1 && mob.cx >= this.player.x + 20)
                {
                    if(Math.abs(this.player.x - mob.cx) <= dx && Math.abs(this.player.y - mob.cy) <= dy)
                    {
                        mob.attackMob(player, false, 1);
                    }
                }
            }
        }
        if(stepKame == 1 && Util.canDoWithTime(lastTimeKame, timeSkill))
        {
            stepKame = 0;
            close();
        }
    }
    
    public void SkillMafoba()
    {
        if(stepMafoba == 0 && Util.canDoWithTime(lastTimeMafoba, 2000))
        {
            lastTimeMafoba = System.currentTimeMillis();
            stepMafoba = 1;
            playerTaget.clear();
            mobTaget.clear();
            for(Player player : player.zone.players)
            {
                if(dir == -1 && player.x <= this.player.x - 20)
                {
                    if(Math.abs((this.player.x - 20) - player.x) <= dx && Math.abs(this.player.y - player.y) <= dy)
                    {
                        if(!this.player.isDie())
                        {
                            if(!player.isDie())
                            {
                                PVP pvp = PVP.gI().findPvp(this.player);
                                if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(player);
                                if (dhvt_tem != null && (dhvt_tem.player1.equals(this.player) || dhvt_tem.player2.equals(player))) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                    continue;
                                }
                                if (player.isBoss && player.typePk == 5) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                    continue;
                                }
                                if (player != this.player && this.player.cFlag != 0 && player.cFlag != 0 && (this.player.cFlag != player.cFlag || this.player.cFlag == 8)) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                }
                            }
                        }
                    }
                }
                if(dir == 1 && player.x >= this.player.x + 20)
                {
                    if(Math.abs((this.player.x + 20) - player.x) <= dx && Math.abs(this.player.y - player.y) <= dy)
                    {
                        if(!this.player.isDie())
                        {
                            if(!player.isDie())
                            {
                                PVP pvp = PVP.gI().findPvp(this.player);
                                if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(player);
                                if (dhvt_tem != null && (dhvt_tem.player1.equals(this.player) || dhvt_tem.player2.equals(player))) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                    continue;
                                }
                                if (player.isBoss && player.typePk == 5) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                    continue;
                                }
                                if (player != this.player && this.player.cFlag != 0 && player.cFlag != 0 && (this.player.cFlag != player.cFlag || this.player.cFlag == 8)) {
                                    player.playerSkill.isMA_PHONG_BA = true;
                                    playerTaget.add(player);
                                }
                            }
                        }
                    }
                }
            }
            for(Mob mob : player.zone.mobs)
            {
                if(dir == -1 && mob.cx <= this.player.x - 20)
                {
                    if(Math.abs(this.player.x - mob.cx) <= dx && Math.abs(this.player.y - mob.cy) <= dy)
                    {
                        mobTaget.add(mob);
                    }
                }
                if(dir == 1 && mob.cx >= this.player.x + 20)
                {
                    if(Math.abs(this.player.x - mob.cx) <= dx && Math.abs(this.player.y - mob.cy) <= dy)
                    {
                        mobTaget.add(mob);
                    }
                }
            }
            SetSkillMafoba();
        }
        if(stepMafoba == 1 && Util.canDoWithTime(lastTimeMafoba, timeSkill))
        {
            for(Player player : playerTaget)
            {
                Service.gI().resetPoint(player, this.player.x + (dir == -1 ? -20 : 20), this.player.y);
                player.playerSkill.isMA_PHONG_BA = false;
                player.playerSkill.startBinh(System.currentTimeMillis(), 11000);
            }
            for(Mob mob : mobTaget)
            {
                mob.setBinh(System.currentTimeMillis(), 11000);
            }
            stepMafoba = 0;
            close();
        }
    }
    
    public void SkillLienHoanChuong()
    {
        if(stepLienHoanChuong == 0 && Util.canDoWithTime(lastTimeLienHoanChuong, 2000))
        {
            lastTimeLienHoanChuong = System.currentTimeMillis();
            stepLienHoanChuong = 1;
            SetSkillLienHoanChuong();
        }
        if(stepLienHoanChuong == 1 && !Util.canDoWithTime(lastTimeLienHoanChuong, timeSkill))
        {
            for(Player player : player.zone.players)
            {
                if(dir == -1 || dir == 1)
                {
                    if(Math.abs(x - player.x) <= dy && Math.abs(y - player.y) <= dy)
                    {
                        if(!this.player.isDie())
                        {
                            if(!player.isDie())
                            {
                                PVP pvp = PVP.gI().findPvp(this.player);
                                if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                DHVT_Template dhvt_tem = DHVT_Template.gI().findPK_DHVT(player);
                                if (dhvt_tem != null && (dhvt_tem.player1.equals(this.player) || dhvt_tem.player2.equals(player))) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                if (player.isBoss && player.typePk == 5) {
                                    player.attackPlayer(this.player, false, 1);
                                    continue;
                                }
                                if (player != this.player && this.player.cFlag != 0 && player.cFlag != 0 && (this.player.cFlag != player.cFlag || this.player.cFlag == 8)) {
                                    player.attackPlayer(this.player, false, 1);
                                }
                            }
                        }
                    }
                }
            }
            for(Mob mob : player.zone.mobs)
            {
                if(dir == -1 || dir == 1)
                {
                    if(Math.abs(x - mob.cx) <= dy && Math.abs(y - mob.cy) <= dy)
                    {
                        if(!this.player.isDie())
                        {
                            if(!player.isDie())
                            {
                                mob.attackMob(player, false, 1);
                            }
                        }
                    }
                }
            }
        }
        if(stepLienHoanChuong == 1 && Util.canDoWithTime(lastTimeLienHoanChuong, timeSkill))
        {
            stepLienHoanChuong = 0;
            close();
        }
    }
    
    public void SetSkillKame()
    {
        Message msg = null;
        try
        {
            msg = new Message(-45);
            msg.writer().writeByte(21);
            msg.writer().writeInt(player.id);
            msg.writer().writeShort(24);
            msg.writer().writeShort(player.x + (dir == -1 ? -dx : dx)); // X
            msg.writer().writeShort(player.y); // Y
            msg.writer().writeShort(3000); // time dame
            msg.writer().writeShort(dy); // range dame
            msg.writer().writeByte(player.gender);
            // Player ăn ma phong ba
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        }
        catch (Exception e)
        {
        } 
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
                msg = null;
            }
        }
        
                        
    }
    
    public void SetSkillMafoba()
    {
        Message msg = null;
        try
        {
            msg = new Message(-45);
            msg.writer().writeByte(21);
            msg.writer().writeInt(player.id);
            msg.writer().writeShort(26);
            msg.writer().writeShort(player.x + (dir == -1 ? -50 : 50)); // X
            msg.writer().writeShort(player.y); // Y
            msg.writer().writeShort(3000); // time dame
            msg.writer().writeShort(dy); // range dame
            msg.writer().writeByte(player.gender);
            // Player ăn ma phong ba
            int size = mobTaget.size() + playerTaget.size();
            
            msg.writer().writeByte(size);
            for(Player player : playerTaget)
            {
                msg.writer().writeByte(1);
                msg.writer().writeInt(player.id);
            }
            for(Mob mob : mobTaget)
            {
                msg.writer().writeByte(0);
                msg.writer().writeByte(mob.id);
            }
            msg.writer().writeByte(0);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        }
        catch (Exception e)
        {
        } 
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
                msg = null;
            }
        }
        
                        
    }
    
    public void SetSkillLienHoanChuong()
    {
        Message msg = null;
        try
        {
            msg = new Message(-45);
            msg.writer().writeByte(21);
            msg.writer().writeInt(player.id);
            msg.writer().writeShort(25);
            msg.writer().writeShort(player.x + (dir == -1 ? -dx : dx)); // X
            msg.writer().writeShort(player.y); // Y
            msg.writer().writeShort(3000); // time dame
            msg.writer().writeShort(25); // range dame
            msg.writer().writeByte(player.gender);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        }
        catch (Exception e)
        {
        } 
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
                msg = null;
            }
        }
        
                        
    }
    
    public boolean EffectSkillUse(){
        boolean isUse = true;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.QUA_CAU_KENH_KHI:
                isUse = false;
                break;
            case Skill.TU_SAT:
                isUse = false;
                break;
            case Skill.MAKANKOSAPPO:
                isUse = false;
                break;
            case Skill.BIEN_KHI:
                isUse = false;
                break;
            case Skill.TAI_TAO_NANG_LUONG:
                isUse = false;
                break;
        }
        return isUse;
    }

    public boolean isStun = false;
    public long lastTimeStartStun;
    public int timeStun;

    public void startStun(long lastTimeStartBlind, int timeBlind) {
        isStun = true;
        this.lastTimeStartStun = lastTimeStartBlind;
        this.timeStun = timeBlind;
        if(this.useTroi){
            this.removeUseTroi();
            this.removeAnTroi();
        }
    }

    public void removeStun() {
        isStun = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(40); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public boolean isBinh = false;
    public boolean isMA_PHONG_BA = false;
    public long lastTimeStartBinh;
    public int timeBinh;
    
    public void startBinh(long lastTimeStartBinh, int timeBinh) {
        isBinh = true;
        this.lastTimeStartBinh = lastTimeStartBinh;
        this.timeBinh = timeBinh;
        Service.gI().sendItemTime(player, 11175, this.timeBinh / 1000);
        Service.gI().Send_Caitrang(player);
    }
    
    public void removeBinh() {
        isBinh = false;
        Service.gI().Send_Caitrang(player);
    }

    public boolean isShielding;
    private long lastTimeShieldUp;
    public int timeShield;

    public void shieldDown() {
        isShielding = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(33); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean isUpMonkey;
    public boolean isMonkey;
    private byte levelMonkey;
     public long lastUpMonkey;
    public long lastTimeMonkey;
    public int timeMonkey;

    public void monkeyUp(){
        try {
            Message msg = null;
            isMonkey = true;
            isUpMonkey = false;
            timeMonkey = SkillUtil.getTimeMonkey(skillSelect.point);
            lastTimeMonkey = System.currentTimeMillis();
            levelMonkey = (byte) skillSelect.point;
            player.point.setHP(player.point.getHP() * 2);
            Service.gI().Send_Caitrang(player);
            Service.gI().sendSpeedPlayer(player, -1);
            if (!player.isPet) {
                msg = Service.gI().messageSubCommand((byte) 5);
                msg.writer().writeInt(player.point.getHPFull());
                player.sendMessage(msg);
                msg.cleanup();
            }
            Service.gI().point(player);
            Service.gI().Send_Info_NV(player);
        } catch (IOException ex) {
            
        }
    }
    
    public byte getLevelMonkey() {
        return levelMonkey;
    }

    public void monkeyDown() {
        isMonkey = false;
        levelMonkey = 0;
        Message msg = null;
        try {
            Skill monkeySkill = null;
            for (Skill skill : skills) {
                if (skill.template.id == 13) {
                    monkeySkill = skill;
                    break;
                }
            }
            msg = new Message(-45);
            msg.writer().writeByte(5);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(monkeySkill.skillId);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();

            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(monkeySkill.skillId);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();

            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();

            Service.gI().Send_Caitrang(player);
            Service.gI().sendSpeedPlayer(player, 0);

            Service.gI().point(player);

            msg = Service.gI().messageSubCommand((byte) 5);
            msg.writer().writeInt(player.point.getHP());
            player.sendMessage(msg);

        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void startCharge() {
        Skill ttnl = getSkillbyId(8);
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(ttnl.skillId);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
        } catch (Exception ex) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    public byte count_ttnl = -1;

    private void charge() {
        Skill ttnl = getSkillbyId(8);
        if (ttnl != null) {
            if (count_ttnl == -1) {
                startCharge();
                count_ttnl = 0;
            }
            int PTHP = (int)((float)((float)player.point.getHP() / (float)player.point.getHPFull()) * 100);
            int PTMP = (int)((float)((float)player.point.getMP() / (float)player.point.getMPFull()) * 100);
            if (player.isDie() || (player.isPet && PTHP >= 50 && PTMP >= 50) || count_ttnl >= 10 || (player.point.getHP() >= player.point.getHPFull() && player.point.getMP() >= player.point.getMPFull())) {
                stopCharge();
                return;
            }
            final int hoi = (int)(((float)(player.point.getHPFull() / 100)) * ttnl.damage);
            player.hoiphuc(hoi, hoi);
            if(count_ttnl == 0 || count_ttnl == 2 || count_ttnl == 4 || count_ttnl == 6 || count_ttnl == 8){
                Service.gI().chat(player, "Phục hồi năng lượng " + PTHP + "%");
            }
            count_ttnl++;
        }
    }

    public void stopCharge() {
        count_ttnl = -1;
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(-1);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            this.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public int tiLeHPHuytSao;
    public long lastTimeHuytSao;

    public void setHuytSao(int tiLeHP) {
        this.tiLeHPHuytSao = tiLeHP;
        player.point.setHP(player.point.getHP() + (player.point.getHP() / 100 * tiLeHP));
        player.playerSkill.lastTimeHuytSao = System.currentTimeMillis();
    }

    private void removeHuytSao() {
        this.tiLeHPHuytSao = 0;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(39); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            Service.gI().point(player);
            Service.gI().Send_Info_NV(player);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean isThoiMien;
    public long lastTimeThoiMien;
    public int timeThoiMien;

    public void setThoiMien(long lastTimeThoiMien, int timeThoiMien) {
        this.isThoiMien = true;
        this.lastTimeThoiMien = lastTimeThoiMien;
        this.timeThoiMien = timeThoiMien;
    }

    public void removeThoiMien() {
        this.isThoiMien = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(41); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean useTroi;
    public boolean anTroi;
    public long lastTimeTroi;
    public long lastTimeAnTroi;
    public int timeTroi;
    public int timeAnTroi;
    public Player plTroi;
    public Player plAnTroi;
    public Mob mobAnTroi;

    public void removeUseTroi() {
        try {
            if (mobAnTroi != null) {
                mobAnTroi.removeAnTroi();
            } else if (plAnTroi != null) {
                plAnTroi.playerSkill.removeAnTroi();
            }
        }
        catch (Exception e) {
            plAnTroi = null;
            mobAnTroi = null;
        }
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(32);
            msg.writer().writeInt((int) player.id);//b5
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);

        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
        this.useTroi = false;
        this.mobAnTroi = null;
        this.plAnTroi = null;
    }

    public void removeAnTroi() {
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(32);
            msg.writer().writeInt((int) player.id);//b5
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
        this.anTroi = false;
        this.plTroi = null;
    }

    public void setAnTroi(Player plTroi, long lastTimeAnTroi, int timeAnTroi) {
        this.anTroi = true;
        this.lastTimeAnTroi = lastTimeAnTroi;
        this.timeAnTroi = timeAnTroi;
        this.plTroi = plTroi;
    }

    public void setUseTroi(long lastTimeTroi, int timeTroi) {
        this.useTroi = true;
        this.lastTimeTroi = lastTimeTroi;
        this.timeTroi = timeTroi;
    }

    public boolean isBlindDCTT;
    public long lastTimeBlindDCTT;
    public int timeBlindDCTT;

    public void setBlindDCTT(long lastTimeDCTT, int timeBlindDCTT, int id) throws IOException {
        this.isBlindDCTT = true;
        this.lastTimeBlindDCTT = lastTimeDCTT;
        this.timeBlindDCTT = timeBlindDCTT;
        Service.gI().sendItemTime(player, 3779, timeBlindDCTT / 1000);
        Message msg = new Message(-124);
        msg.writer().writeByte(1);
        msg.writer().writeByte(0);
        msg.writer().writeByte(40);
        msg.writer().writeInt((int)id);
        msg.writer().writeByte(0);
        msg.writer().writeByte(32);
        Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        msg.cleanup();
        if(this.useTroi){
            this.removeUseTroi();
            this.removeAnTroi();
        }
    }

    public void removeBlindDCTT() {
        this.isBlindDCTT = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(40);
            msg.writer().writeInt((int) player.id);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private boolean nemQCKK;
    private boolean tuSat;
    private boolean bienkhi;
    private boolean laze;

    public boolean isSocola;
    private long lastTimeSocola;
    private int timeSocola;
    public int countPem1hp;
    
    public long lastTimeXinbato;
    public int timeXinbato;
    public boolean isXinbato;

    public long lastTimeHoaDa;
    public int timeHoaDa;
    public boolean isHoaDa;

    public long lastTimeMaTroi;
    public int timeMaTroi;
    public boolean isMaTroi;

    public void setHoaDa(long lastTimeHoaDa, int timeHoaDa) {
        this.lastTimeHoaDa = lastTimeHoaDa;
        this.timeHoaDa = timeHoaDa;
        this.isHoaDa = true;
        if(this.useTroi){
            this.removeUseTroi();
            this.removeAnTroi();
        }
    }

    public void setSocola(long lastTimeSocola, int timeSocola) {
        this.lastTimeSocola = lastTimeSocola;
        this.timeSocola = timeSocola;
        this.isSocola = true;
        countPem1hp = 0;
    }
    
    public void setXinbato(long lastTimeXinbato, int timeXinbato) {
        this.lastTimeXinbato = lastTimeXinbato;
        this.timeXinbato = timeXinbato;
        this.isXinbato = true;
    }

    public void setMaTroi(long lastTimeSocola, int timeSocola) {
        this.lastTimeMaTroi = lastTimeSocola;
        this.timeMaTroi = timeSocola;
        this.isMaTroi = true;
    }

    public void removeAnHoaDa() {
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(42); //num3
            msg.writer().writeInt((int) player.id);//num9
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    void removeCaRot() {
        this.isMaTroi = false;
        Service.gI().Send_Caitrang(player);
    }

    void removeHoaDa() {
        this.isHoaDa = false;
        removeAnHoaDa();
        Service.gI().Send_Caitrang(player);
    }
    
    void removeXinbato() {
        this.isXinbato = false;
    }

    void removeSocola() {
        this.isSocola = false;
        Service.gI().Send_Caitrang(player);
    }

    private void sendEffectUseSkill() {
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(8);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skillSelect.skillId);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean isHaveEffectSkill()
    {
        return isStun || isBlindDCTT || anTroi || isThoiMien || isHoaDa || isMA_PHONG_BA;
    }
    
    public int getCoolDown (){
        if(player.isPet){
            switch (skillSelect.template.id) {
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALIK:
                case Skill.KAIOKEN:
                    return 500;
                case Skill.KAMEJOKO:
                case Skill.ANTOMIC:
                case Skill.MASENKO:
                    return 700;
            }
        }
        if(player.isBoss){
            return 0;
        }
        return skillSelect.coolDown;
    }

    public byte getIndexSkillSelect() {
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALIK:
            case Skill.KAIOKEN:
            case Skill.LIEN_HOAN:
                return 1;
            case Skill.KAMEJOKO:
            case Skill.ANTOMIC:
            case Skill.MASENKO:
                return 2;
            default:
                return 3;
        }
    }
    
    public boolean getSelectSkill2() {
        switch (skillSelect.template.id) {
            case Skill.KAMEJOKO:
            case Skill.ANTOMIC:
            case Skill.MASENKO:
            case Skill.MAKANKOSAPPO:
            case Skill.QUA_CAU_KENH_KHI:
                return true;
            default:
                return false;
        }
    }
}
