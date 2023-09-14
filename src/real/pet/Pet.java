package real.pet;

import java.util.ArrayList;
import java.util.List;
import real.func.UseItem;
import real.item.CaiTrangData;
import real.item.Item;
import real.map.MapService;
import real.map.Mob;
import real.player.Player;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;
import service.data.Init;

public class Pet extends Player {

    public Player master;

    public int isMabu = -1;
    
    private static final short ARANGE_FIND_ATTACK = 200;

    public static final byte FOLLOW = 0;
    public static final byte PROTECT = 1;
    public static final byte ATTACK = 2;
    public static final byte GOHOME = 3;
    public static final byte FUSION = 4;
    public static final byte DELETE = 5;
    
    public byte status = 0;
    public boolean playSkill;
    public boolean isBien = true;
    public boolean isNew = false;

    private long lastTimeDie = -1;
    private long lastTimeUp;
    private long lastTimeHome;
    private long lastTimeAskPea;
    private boolean goingHome;

    private Mob mobAttack;
    private Player playerAttack;
    
    private List<Player> players;

    public byte getStatus() {
        return this.status;
    }

    public Pet(Player master) {
        this.master = master;
        this.isPet = true;
        this.id = -master.id;
        this.players = new ArrayList();
    }

    public void changeStatus(byte status) {
        if (goingHome || master.typeFusion != 0) {
            Service.gI().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        Service.gI().chatJustForMe(master, this, getTextStatus(status));
        if (status == GOHOME) {
            goHome();
        } else if (status == FUSION) {
            fusion(3);
        }
        this.status = status;
    }

    public void joinMapMaster() {
        if (!isDie()) {
            int[] move = new int[]{-30,30};
            this.x = master.x + move[Util.nextInt(0, 1)];
            this.y = master.y;
            this.gotoMap(master.zone);
            MapService.gI().joinMap(this, master.zone);
        }
    }

    public void goHome() {
        if (this.status == GOHOME) {
            return;
        }
        this.lastTimeHome = System.currentTimeMillis();
    }

    private String getTextStatus(byte status) {
        switch (status) {
            case FOLLOW:
                return "Ok con theo sư phụ";
            case PROTECT:
                return "Ok con sẽ bảo vệ sư phụ";
            case ATTACK:
                return "Ok sư phụ để con lo cho";
            case GOHOME:
                return "Ok con về, bibi sư phụ";
            default:
                return "";
        }
    }

    public void fusion(int porata) {
        if (this.isDie()) {
            Service.gI().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        switch (porata) {
            case 1:
                master.typeFusion = Player.HOP_THE_PORATA;
                break;
            case 2:
                master.typeFusion = Player.HOP_THE_PORATA2;
                break;
            case 3:
                master.typeFusion = Player.LUONG_LONG_NHAT_THE;
                master.itemTime.addItemTime(master.gender == 1 ? 3901 : 3790, Player.timeFusion / 1000);
                break;
        }
        this.percentDamePlus = -1;
        this.playerSkill.monkeyDown();
        this.playerSkill.tiLeHPHuytSao = 0;
        this.status = FUSION;
        master.point.updateall();
        this.exitMap();
        fusionEffect(master.typeFusion == 4 ? 4 : 6);
        Service.gI().Send_Caitrang(master);
        Service.gI().point(master);
        this.close();
    }

    public void unFusion() {
        master.typeFusion = 0;
        this.status = PROTECT;
        Service.gI().point(master);
        fusionEffect(master.typeFusion);
        Service.gI().Send_Caitrang(master);
        Service.gI().point(master);
        joinMapMaster();
    }

    public void fusionEffect(int type) {
        Message msg;
        try {
            msg = new Message(125);
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) master.id);
            Service.gI().sendMessAllPlayerInMap(master.zone, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    private long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (status == GOHOME || status == FUSION || this.playerSkill.isHoaDa) {
            return;
        }
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int[] arr = {status == ATTACK ? -70 : -30, status == ATTACK ? 70 : 30};
            move(master.x + arr[Util.nextInt(0, 1)], master.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }

    @Override
    public void update() {
        try {
            if(isDie() && lastTimeDie == -1){
                lastTimeDie = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeUp, 0)) {
                increasePoint(); //cộng chỉ số
                lastTimeUp = System.currentTimeMillis() + Util.nextInt(1000, 2000);
            }
            updatePower(); //check mở skill...
            if(!isDie() && Util.canDoWithTime(lastTimeHome, 2000) && this.status == Pet.GOHOME){
                this.exitMap();
                this.close();
                return;
            }
            if (isDie() && Util.canDoWithTime(lastTimeDie, 60000)) {
                Service.gI().hsChar(this, point.getHPFull(), point.getMPFull());
                lastTimeDie = -1;
                return;
            }
            if (justRevived && this.zone == master.zone) {
                Service.gI().chatJustForMe(master, this, "Sư phụ ơi, con đây nè!");
                justRevived = false;
            }
            
            if(isNew && this.zone == master.zone){
                String mess = isMabu >= 3 ? "Oa Oa Oa" : "Sư phụ hãy thu nhận con làm đệ tử.";
                Service.gI().chatJustForMe(master, this, mess);
                isNew = false;
            }

            if (this.zone == null || this.zone != master.zone) {
                joinMapMaster();
            }
            playerSkill.update();
            DrPhuTung();
            if(this.mobMe != null){
                this.mobMe.update();
            }
            if (master.isDie() || this.isDie() || playerSkill.isHaveEffectSkill()) {
                return;
            }
            if (status > 0 && status < 3 && (useSkill3() || useSkill4())) {
                return;
            }
            if (this.point.stamina <= 0 || this.SkillUser == 1) {
                this.askPea();
                return;
            }
            int dis = -1;
            int[] arr = null;
            moveIdle();
            switch (status) {
                case FOLLOW:
                    break;
                case PROTECT:
                    playerAttack = findPlayerAttack(ARANGE_FIND_ATTACK);
                    if (playerAttack == null) {
                        mobAttack = this.findMobAttack(ARANGE_FIND_ATTACK);
                    }
                    if(playerAttack != null){
                        dis = Util.getDistance(this, playerAttack);
                        if (dis > 65) {
                            playerSkill.skillSelect = getSkill(2);
                            if (playerSkill.skillSelect == null) {
                                playerSkill.skillSelect = getSkill(1);
                            }
                        } else {
                            playerSkill.skillSelect = getSkill(1);
                        }
                        arr = new int[]{playerAttack.x + 10, playerAttack.y};
                    }
                    else if (mobAttack != null) {
                        dis = Util.getDistance(this, mobAttack);
                        if (dis > 65) {
                            playerSkill.skillSelect = getSkill(2);
                            if (playerSkill.skillSelect == null) {
                                playerSkill.skillSelect = getSkill(1);
                            }
                        } else {
                            playerSkill.skillSelect = getSkill(1);
                        }
                        int r = Util.nextInt(0, 1);
                        int point = Util.nextInt(1, 10);
                        arr = new int[]{mobAttack.pointX + (r == 0 ? -point : point), mobAttack.pointY};
                    }
                    switch (playerSkill.getIndexSkillSelect()) {
                        case 1: //skill đấm
                            if (dis <= 60) {
                                move(arr[0], arr[1]);
                                playerSkill.useSkill(playerAttack, mobAttack);
                            }
                            break;
                        case 2: //skill chưởng
                            if (dis <= 60) {
                                playerSkill.skillSelect = playerSkill.skills.get(0);
                            }
                            playerSkill.useSkill(playerAttack, mobAttack);
                            break;
                    }
                    break;
                case GOHOME:
                case ATTACK:
                    playerAttack = findPlayerAttack(ARANGE_FIND_ATTACK + 50);
                    if (playerAttack == null) {
                        mobAttack = this.findMobAttack(ARANGE_FIND_ATTACK + 50);
                    }
                    if(playerAttack != null){
                        dis = Util.getDistance(this, playerAttack);
                        if (dis > 65) {
                            playerSkill.skillSelect = getSkill(2);
                            if (playerSkill.skillSelect == null) {
                                playerSkill.skillSelect = getSkill(1);
                            }
                        } else {
                            playerSkill.skillSelect = getSkill(1);
                        }
                        int r = Util.nextInt(0, 1);
                        int point = Util.nextInt(1, 10);
                        arr = new int[]{playerAttack.x + (r == 0 ? -point : point), playerAttack.y};
                    }
                    else if (mobAttack != null) {
                        dis = Util.getDistance(this, mobAttack);
                        if (dis > 65) {
                            playerSkill.skillSelect = getSkill(2);
                            if (playerSkill.skillSelect == null) {
                                playerSkill.skillSelect = getSkill(1);
                            }
                        } else {
                            playerSkill.skillSelect = getSkill(1);
                        }
                        int r = Util.nextInt(0, 1);
                        int point = Util.nextInt(1, 10);
                        arr = new int[]{mobAttack.pointX + (r == 0 ? -point : point), mobAttack.pointY};
                    }
                    switch (playerSkill.getIndexSkillSelect()) {
                        case 1: //skill đấm
                            if (dis < 50 && dis > 80) {
                                move(arr[0], arr[1]);
                            }
                            else if (this.canUseSkill()) {
                                move(arr[0], arr[1]);
                            }
                            playerSkill.useSkill(playerAttack, mobAttack);
                            break;
                        case 2: //skill chưởng
                            if (dis <= 60) {
                                playerSkill.skillSelect = playerSkill.skills.get(0);
                            }
                            playerSkill.useSkill(playerAttack, mobAttack);
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
        }

        if (mobAttack == null || (mobAttack != null && mobAttack.isDie()) || playerAttack == null && (playerAttack != null && playerAttack.isDie())) {
            idle = true;
        }
    }

    public void askPea() {
        if (Util.canDoWithTime(lastTimeAskPea, 10000)) {
            Service.gI().chatJustForMe(master, this, "Sư phụ ơi cho con đậu thần");
            lastTimeAskPea = System.currentTimeMillis();
            for(int i = 0; i < master.inventory.itemsBag.size(); i++){
                Item item = master.inventory.itemsBag.get(i);
                if(item.template.type == 6){
                    UseItem.gI().Eat_Pean(master, item);
                    break;
                }
            }
        }
        this.SkillUser = 0;
    }

    private boolean useSkill3() {
        try {
            playerSkill.skillSelect = getSkill(3);
            if (playerSkill.skillSelect == null) {
                return false;
            }
            switch (this.playerSkill.skillSelect.template.id) {
                case Skill.THAI_DUONG_HA_SAN:
                    if (canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        Service.gI().chat(this, "Thái dương hạ san");
                        return true;
                    }
                    return false;
                case Skill.TAI_TAO_NANG_LUONG:
                    int PT_HP = (int)((float)((float)this.point.getHP() / (float)this.point.getHPFull()) * 100);
                    int PT_MP = (int)((float)((float)this.point.getMP() / (float)this.point.getMPFull()) * 100);
                    int POINT = 50;
                    if(this.playerSkill.count_ttnl != -1){
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    if(canUseSkill() && (PT_HP < POINT || PT_MP < POINT)){
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                case Skill.KAIOKEN:
                    if (canUseSkill()) {
                        mobAttack = this.findMobAttack(ARANGE_FIND_ATTACK);
                        if (mobAttack == null) {
                            return false;
                        }
                        int dis = Util.getDistance(this, mobAttack);
                        if (dis > 50) {
                            move(mobAttack.pointX, mobAttack.pointY);
                        } else if (canUseSkill()) {
                            int r = Util.nextInt(0, 1);
                            int point = Util.nextInt(1, 20);
                            move(mobAttack.pointX + (r == 0 ? -point : point), mobAttack.pointY);
                        }
                        playerSkill.useSkill(playerAttack, mobAttack);
                        getSkill(1).lastTimeUseThisSkill = System.currentTimeMillis();
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canUseSkill() {
        if (playerSkill.skillSelect == null || playerSkill.count_ttnl != -1) {
            return false;
        }
        if (playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
            int hpUse = point.getHPFull() / 100 * 10;
            if (point.getHP() <= hpUse) {
                return false;
            }
        }
        boolean canUseSkill = false;
        switch (playerSkill.skillSelect.template.manaUseType) {
            case 0:
                if (this.point.mp >= playerSkill.skillSelect.manaUse) {
                    canUseSkill = true;
                }
                break;
            case 1:
                int mpUse = (int) ((float) (this.point.getMPFull() / 100 * playerSkill.skillSelect.manaUse));
                if (this.point.mp >= mpUse) {
                    canUseSkill = true;
                }
                break;
            case 2:
                canUseSkill = true;
                break;
        }
        return canUseSkill && Util.canDoWithTime(playerSkill.skillSelect.lastTimeUseThisSkill, playerSkill.skillSelect.coolDown);
    }

    private boolean useSkill4() {
        try {
            this.playerSkill.skillSelect = getSkill(4);
            switch (this.playerSkill.skillSelect.template.id) {
                case Skill.BIEN_KHI:
                    if (!this.playerSkill.isMonkey && this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                case Skill.KHIEN_NANG_LUONG:
                    if (!this.playerSkill.isShielding && this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                case Skill.DE_TRUNG:
                    if (this.mobMe == null && this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void increasePoint() {
        for (int i = 0; i < 20; i++) {
            point.upPotential((byte) Util.nextInt(0, 4), (short) 1);
        }
    }

    public void followMaster() {
        if (this.isDie() || playerSkill.isHaveEffectSkill()) {
            return;
        }
        switch (this.status) {
            case ATTACK:
                if ((mobAttack != null && Util.getDistance(this, master) <= 500)) {
                    break;
                }
            case FOLLOW:
                followMaster(30);
                break;
            case PROTECT:
                followMaster(30);
                break;
        }
    }

    private void followMaster(int dis) {
        int mX = master.x;
        int mY = master.y;
        int disX = this.x - mX;
        double disChanger = Math.sqrt(Math.pow(mX - this.x, 2) + Math.pow(mY - this.y, 2));
        if (disChanger >= dis || disChanger <= dis) {
            if (disX < 0) {
                this.x = mX - dis;
            } else {
                this.x = mX + dis;
            }
            this.y = mY;
            this.move(this.x, this.y);
        }
    }

    private static final short[][] petID = {{285, 286, 287}, {288, 289, 290}, {282, 283, 284}, {304, 305, 303}};
    private static final short[][] petIDMabu = {{297, 298, 299}, {421, 422, 423}, {424, 425, 426}, {297, 421, 424}};
    public short getAvatar() {
        if(isMabu >= 3){
            return petIDMabu[3][this.isMabu-3];
        }else{
            return petID[3][this.gender];
        }
    }

    @Override
    public short getHead() {
        if (playerSkill.isMonkey) {
            return Init.HEADMONKEY[playerSkill.getLevelMonkey() - 1];
        }
        else if (playerSkill.isSocola) {
            return 412;
        }
        else if (playerSkill.isHoaDa) {
            return 454;
        }
        else if(this.isMabu >= 3 && isBien){
            return petIDMabu[this.isMabu-3][0];
        }
        else if (inventory.itemsBody.get(5) != null) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null) {
                return (short) ((short) ct.getID()[0] != -1 ? ct.getID()[0] : inventory.itemsBody.get(5).template.part);
            }
        }
        else if (playerSkill.isMaTroi) {
            int plus = this.gender == 0 ? 0 : this.gender == 1 ? 2 : 1;
            return (short) (idHalloween[indexHeadHalloween][0] + plus);
        }
        if (this.point.getPower() < 1500000) {
            return petID[this.gender][0];
        }else {
            return petID[3][this.gender];
        }
    }

    @Override
    public short getBody() {
        if (playerSkill.isMonkey) {
            return 193;
        }
        else if (playerSkill.isSocola) {
            return 413;
        }
        else if (playerSkill.isHoaDa) {
            return 455;
        }
        else if(this.isMabu >= 3 && isBien){
            return petIDMabu[this.isMabu-3][1];
        }
        else if (inventory.itemsBody.get(5) != null) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[1] != -1) {
                return (short) ct.getID()[1];
            }
        }
        else if (playerSkill.isMaTroi) {
            return master.idHalloween[indexHeadHalloween][1];
        }
        if (inventory.itemsBody.get(0) != null) {
            return inventory.itemsBody.get(0).template.part;
        }
        if (this.point.getPower() < 1500000) {
            return petID[this.gender][1];
        }else {
            return (short) (gender == 1 ? 59 : 57);
        }
    }

    @Override
    public short getLeg() {
        if (playerSkill.isMonkey) {
            return 194;
        }
        else if (playerSkill.isSocola) {
            return 414;
        }
        else if (playerSkill.isHoaDa) {
            return 456;
        }
        else if(this.isMabu >= 3 && isBien){
            return petIDMabu[this.isMabu-3][2];
        }
        else if (inventory.itemsBody.get(5) != null) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[2] != -1) {
                return (short) ct.getID()[2];
            }
        }
        else if (playerSkill.isMaTroi) {
            return master.idHalloween[indexHeadHalloween][2];
        }
        if (inventory.itemsBody.get(1) != null) {
            return inventory.itemsBody.get(1).template.part;
        }
        if (this.point.getPower() < 1500000) {
            return petID[this.gender][2];
        }
        else {
            return (short) (gender == 1 ? 60 : 58);
        }
    }

    @Override
    public void move(int _toX, int _toY) {
        if (zone == null) {
            zone = master.zone;
            gotoMap(zone);
            Service.gI().mapInfo(this);
            MapService.gI().joinMap(this, zone);
            MapService.gI().loadAnotherPlayers(this, this.zone);

        }
        super.move(_toX, _toY);
        idle = false;
    }

    private Mob findMobAttack(int distance) {
        Mob mobAtt = null;
        if(zone == null){
            return null;
        }
        for (Mob mob : zone.mobs) {
            int dis = Util.getDistance(this, mob);
            if (!mob.isDie() && dis <= distance) {
                distance = dis;
                mobAtt = mob;
            }
        }
        return mobAtt;
    }

    private void updatePower() {
        switch (this.playerSkill.skills.size()) {
            case 1:
                if (this.point.getPower() >= 150000000) {
                    openSkill2();
                }
                break;
            case 2:
                if (this.point.getPower() >= 1500000000) {
                    openSkill3();
                }
                break;
            case 3:
                if (this.point.getPower() >= 20000000000L) {
                    openSkill4();
                }
                break;
        }
    }

    public Skill openSkill2(int...open) {
        Skill skill = null;
        int tiLeKame = 30;
        int tiLeMasenko = 50;
        int tiLeAntomic = 20;

        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeKame) {
            skill = SkillUtil.createSkill(Skill.KAMEJOKO, 1,0);
        } else if (rd <= tiLeKame + tiLeMasenko) {
            skill = SkillUtil.createSkill(Skill.MASENKO, 1,0);
        } else if (rd <= tiLeKame + tiLeMasenko + tiLeAntomic) {
            skill = SkillUtil.createSkill(Skill.ANTOMIC, 1,0);
        }
        if(open.length <= 0){
            this.playerSkill.skills.add(skill);
        }
        return skill;
    }

    public Skill openSkill3(int...open) {
        Skill skill = null;
        int tiLeTDHS = 10;
        int tiLeTTNL = 70;
        int tiLeKOK = 20;

        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeTDHS) {
            skill = SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1,0);
        } else if (rd <= tiLeTDHS + tiLeTTNL) {
            skill = SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1,0);
        } else if (rd <= tiLeTDHS + tiLeTTNL + tiLeKOK) {
            skill = SkillUtil.createSkill(Skill.KAIOKEN, 1,0);
        }
        if(open.length <= 0){
            this.playerSkill.skills.add(skill);
        }
        return skill;
    }

    public Skill openSkill4(int...open) {
        Skill skill = null;
        int tiLeBienKhi = 30;
        int tiLeDeTrung = 50;
        int tiLeKNL = 20;

        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeBienKhi) {
            skill = SkillUtil.createSkill(Skill.BIEN_KHI, 1,0);
        } else if (rd <= tiLeBienKhi + tiLeDeTrung) {
            skill = SkillUtil.createSkill(Skill.DE_TRUNG, 1,0);
        } else if (rd <= tiLeBienKhi + tiLeDeTrung + tiLeKNL) {
            skill = SkillUtil.createSkill(Skill.KHIEN_NANG_LUONG, 1,0);
        }
        if(open.length <= 0){
            this.playerSkill.skills.add(skill);
        }
        return skill;
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
            case 3:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 6 || temp == 8 || temp == 9) {
                        return skill;
                    }
                }
                return null;
            case 4:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 12 || temp == 13 || temp == 19) {
                        return skill;
                    }
                }
                return null;
            default:
                return null;
        }
    }
    
    public List<Player> getPlayers(){
        return players;
    }
    
    public void AddPlayerAttack(Player pl){
        players.add(pl);
    }
    
    public boolean findPlayer(Player player) {
        for (Player pl : players) {
            if (pl.equals(player)) {
                return true;
            }
        }
        return false;
    }
    
    private Player findPlayerAttack(int distance) {
        Player player = null;
        for (Player plAtt : players) {
            for(Player pl : this.zone.getPlayers()){
                if(pl != this && pl == plAtt && !pl.isDie() && pl.zone == this.zone && ((pl.typePk == 5 || this.typePk == 5) || ((pl.cFlag != this.cFlag || this.cFlag == 8) && pl.cFlag != 0 && this.cFlag != 0))){
                    int dis = Util.getDistance(this, pl);
                    if (dis <= distance) {
                        distance = dis;
                        player = pl;
                    }
                }
            }
        }
        return player;
    }
}
