package real.boss;

import real.func.ChangeMap;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.Zone;
import real.map.MapManager;
import real.player.Player;
import real.skill.Skill;
import real.skill.SkillUtil;
import real.task.Task;
import real.task.TaskData;
import server.Service;
import server.Util;
import service.Setting;

public class Boss_Template extends Boss {

    public int ZONE_JOIN = -1;
    public int[] MAP_JOIN = {5};

    public static int idb = -1000;
    public short body;
    public short leg;
    
    public boolean isExitBoss = false;
    public long currExit = System.currentTimeMillis();
    public long timeExit = 900000;
    
    public Item itemDrop = null;
    
    public long currAttack = System.currentTimeMillis();
    
    @Override
    protected void init() {
        int mapJoinId = MAP_JOIN[Util.nextInt(0,MAP_JOIN.length -1)];
        int zoneRandom = ZONE_JOIN > -1 ? ZONE_JOIN : Util.nextInt(1,MapManager.gI().getMapById(mapJoinId).map_zone.length - 1);
        if(MapManager.gI().getMap(mapJoinId,zoneRandom).getBossInMap(zoneRandom) < 1){
            this.isBoss = true;
            this.status = JUST_JOIN_MAP;
            this.id = idb;
            idb--;
            this.gender = 2;
            this.point.power = 1;
            this.point.mpGoc = 100000000;
            this.point.mp = 100000000;
            if (zone == null) {
                this.zone = MapManager.gI().getMap(mapJoinId,zoneRandom);
            }
        }else{
            this.isBoss = true;
            setStatus(DIE);
            this.zone = null;
        }
    }
    
    public void CheckTaskBoss(){
        if(playerKill != null && playerKill.isPl()){
            Task taskPl = TaskData.getTask(playerKill.taskId);
            if(taskPl.type == 1)
            {
                if(taskPl.taskId == 29 && playerKill.taskIndex == 4 && this.zone.map.id == 103 && taskPl.counts[playerKill.taskIndex] != -1 && taskPl.killId[playerKill.taskIndex] == -this.head){
                    playerKill.taskCount += 1;
                    if (playerKill.taskCount < taskPl.counts[playerKill.taskIndex])
                    {
                        Service.gI().send_task(playerKill, taskPl);
                    }
                    else
                    {
                        Service.gI().send_task_next(playerKill);
                    }
                }
                else if (taskPl.counts[playerKill.taskIndex] != -1 && taskPl.killId[playerKill.taskIndex] == -this.head) {
                    playerKill.taskCount += 1;
                    if (playerKill.taskCount < taskPl.counts[playerKill.taskIndex])
                    {
                        Service.gI().send_task(playerKill, taskPl);
                    }
                    else
                    {
                        Service.gI().send_task_next(playerKill);
                    }
                }
            }
        }
    }

    public Boss_Template() {
        super();
    }

    public Boss_Template(Zone m, String n, int hp, int dame, int h, int b, int l) {
        this.point.dameGoc = dame;
        this.point.hpGoc = hp;
        this.point.hp = hp;
        this.name = n;
        this.zone = m;
        this.head = (short) h;
        this.body = (short) b;
        this.leg = (short) l;
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

    public boolean BossTT(){
        return this.name.contains("Broly")|| this.name.contains("Pi láp") || this.name.contains("Mai") 
                || this.name.contains("Su") || this.name.contains("Chaien") || this.name.contains("Xuka") 
                || this.name.contains("Nobita") || this.name.contains("Xekô") || this.name.contains("Đôrêmon") 
                || this.name.contains("Lychee") || this.name.contains("Hatchiyack") || this.name.contains("Videl") 
                || this.name.contains("Robot") ||this.name.contains("Ninja") ||this.name.contains("Trung Úy") 
                ||this.name.contains("Chilled") ||this.name.contains("Tập sự") || this.name.contains("Fide") 
                || this.name.contains("Dơi Nhí")||this.name.contains("Ma Trơi")|| this.name.contains("Bộ Xương")
                ||this.name.contains("Cooler")||this.name.contains("Thần")||this.name.contains("Rambo") 
                ||this.name.contains("Mập Đầu Đinh") ||this.name.contains("KuKu") ||this.name.contains("Xên") 
                || this.name.contains("Super") || this.name.contains("Black Goku") || this.name.contains("Zamasu") 
                || this.name.contains("Thỏ")|| this.name.contains("Đại Ca")|| this.name.contains("Cumber");
    }

    @Override
    public synchronized void update() {
        try{
            if(Util.canDoWithTime(currExit, timeExit))
            {
                setStatus(EXIT_MAP);
            }
            if (this.isDie() && this.status < DIE) {
                setStatus(DIE);
            }
            this.playerSkill.update();
            switch (status) {
                case JUST_JOIN_MAP:
                    joinMap();
                    setStatus(TALK_BEFORE);
                    break;
                case TALK_BEFORE:
                    if (indexTalk > textTalkBefore.length - 1) {
                        if(BossManager.isTDST){
                            if(this.name.equals(BossManager.countTDST)){
                                Service.gI().changeTypePK(this, Player.PK_ALL);
                                setStatus(ATTACK);
                            }
                        }
                        if(BossManager.isAdr){
                            if(this.name.equals(BossManager.countAdr)){
                                Service.gI().changeTypePK(this, Player.PK_ALL);
                                setStatus(ATTACK);
                            }
                        }
                        if(BossManager.isAdr2){
                            if(this.name.equals(BossManager.countAdr2)){
                                Service.gI().changeTypePK(this, Player.PK_ALL);
                                setStatus(ATTACK);
                            }
                        }
                        if(BossManager.isAdr3){
                            if(this.name.equals(BossManager.countAdr3)){
                                Service.gI().changeTypePK(this, Player.PK_ALL);
                                setStatus(ATTACK);
                            }
                        }
                        if(BossManager.isXenBh && this.zone.players.stream().filter(pla -> pla != null && pla.name.toLowerCase().contains("xên con")).count() <= 0 && BossManager.isXenCon){
                            if(this.name.equals("Siêu bọ hung")){
                                Service.gI().changeTypePK(this, Player.PK_ALL);
                                setStatus(ATTACK);
                            }
                        }
                        if(BossTT()){
                            Service.gI().changeTypePK(this, Player.PK_ALL);
                            setStatus(ATTACK);
                        }
                    }
                    else
                    {
                        if (Util.canDoWithTime(lastTimeTalk, timeTalk)) {
                            Service.gI().chat(this, textTalkBefore[indexTalk]);
                            timeTalk = textTalkBefore[indexTalk].length() * 100;
                            lastTimeTalk = System.currentTimeMillis();
                            indexTalk++;
                        }
                    }
                    break;
                case ATTACK:
                    if(indexTalk >= 3){
                        indexTalk = 0;
                    }
                    Player pl = getPlayerAttack();
                    if (pl != null && pl != this) {
                        int dis = Util.getDistance(this, pl);
                        if (this.playerSkill.isHaveEffectSkill()) {
                            break;
                        } else if (dis > 450) {
                            move(pl.x - 24, pl.y);
                        } else if (dis > 100) {
                            int dir = (this.x - pl.x < 0 ? 1 : -1);
                            int move = Util.nextInt(50, 100);
                            move(this.x + (dir == 1 ? move : -move), pl.y);
                        } else {
                            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(this.playerSkill.skills.size() - 1));
                            this.playerSkill.useSkill(pl, null);
                            if (pl.isDie())
                            {
                                Service.gI().chat(this, this.name.contains("Broly") ? "Chừa nhé " + pl.name : "Thí sinh tiếp theo.");
                                indexTalk = 0;
                            }
                            else if (Util.canDoWithTime(lastTimeTalk, timeTalk)) {
                                Service.gI().chat(this, textTalkMidle[indexTalk]);
                                timeTalk = textTalkMidle[indexTalk].length() * 800;
                                lastTimeTalk = System.currentTimeMillis();
                                indexTalk++;
                            }
                            if (Util.isTrue(50)) {
                                int move = Util.nextInt(50);
                                move(pl.x + (Util.nextInt(0,1) == 1 ? move : -move), this.y);
                            }
                        }
                    }
                    else if(this.name.contains("Broly"))
                    {
                        for(Skill skill : this.playerSkill.skills){
                            if(skill.template.id == Skill.TAI_TAO_NANG_LUONG){
                                this.playerSkill.skillSelect = skill;
                                this.playerSkill.useSkill(this, null);
                            }
                        }
                    }
                    break;
                case DIE:
                    CheckTaskBoss();
                    setStatus(LEAVE_MAP);
                    break;
                case LEAVE_MAP:
                    if(playerKill.id < 0){
                        playerKill.id = Math.abs(playerKill.id);
                    }
                    if(this.name.contains("Lychee")){
                        this.zone.zoneDoneBOSS_2 = true;
                    }
                    if(this.name.contains("Hatchiyack")){
                        playerKill.clan.KhiGasHuyDiet.time = 60;
                        this.zone.zoneDoneKGHD = true;
                    }
//                    if(Setting.EVENT_GIANG_SINH){
//                        int[] NROB = new int[]{928, 929};
//                        int itemID = NROB[Util.nextInt(0, 1)];
//                        if(playerKill != null){
//                            if (Util.isTrue(20) && !isExitBoss) {
//                                Item it = ItemData.gI().get_item(itemID);
//                                it.itemOptions.clear();
//                                it.itemOptions.add(new ItemOption(174, 2022));
//                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
//                            }
//                        }
//                    }
//                    int trai = 0;
//                    int phai = 1;
//                    int next = 0;
//                    Item NGOC = ItemData.gI().get_item(77);
//                    for(int i = 0; i < 15; i++){
//                        int X = next == 0 ? -5 * trai : 5 * phai;
//                        if(next == 0){
//                            trai++;
//                        }
//                        else{
//                            phai++;
//                        }
//                        next = next == 0 ? 1 : 0;
//                        if(trai > 10){
//                            trai = 0;
//                        }
//                        if(phai > 10){
//                            phai = 1;
//                        }
//                        Service.gI().roiItem(NGOC, zone, x + X, y, -1);
//                    }
                    startDie();
                    ChangeMap.gI().spaceShipArrive(this, ChangeMap.TENNIS_SPACE_SHIP);
                    this.exitMap();
                    this.close();
                    this.dispose();
                    break;
                case EXIT_MAP:
                    playerKill = null;
                    isExitBoss = true;
                    startDie();
                    this.exitMap();
                    this.close();
                    this.dispose();
                    break;
            }
        }catch(Exception e){
//            if(status == ATTACK || status == EXIT_MAP || status == LEAVE_MAP){
//                Util.debug(name + " - MAP: " + zone.map.id + " - k" + zone.zoneId);
//            }
        }
    }
    
    @Override
    public void startDie() {
    }

    public void joinMap() {
        ChangeMap.gI().changeMapBySpaceShip(this, this.zone, ChangeMap.TENNIS_SPACE_SHIP);
    }

    @Override
    protected void initSkill() {
        byte[] skillTemp = {Skill.TAI_TAO_NANG_LUONG, Skill.MASENKO, Skill.ANTOMIC, Skill.KAMEJOKO, Skill.MASENKO, Skill.ANTOMIC, Skill.KAMEJOKO, Skill.DRAGON, Skill.GALIK, Skill.DEMON};
        if (!this.name.contains("Cỗ Máy Hủy Diệt") || !this.name.contains("Cooler") || !this.name.contains("Thần") || !this.name.contains("Super") || !this.name.contains("Black Goku") || !this.name.contains("Zamasu")) {
            skillTemp = Util.removeTheElement(skillTemp, 0);
        }
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i], Util.nextInt(1, 7),0);
            skill.coolDown = 20;
            skill.manaUse = 0;
            this.playerSkill.skills.add(skill);
        }
        if(this.name.contains("Trung Úy Xanh Lơ")){
            Skill skill = SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 7,0);
            skill.manaUse = 0;
            this.playerSkill.skills.add(skill);
        }
    }

    @Override
    protected void initTalk() {
        this.textTalkBefore = new String[]{
            "Hôm nay ta tới đây để tiêu diệt tất cả các ngươi...",
            "Hãy gọi những kẻ mạnh nhất ra đây!"
        };
        this.textTalkMidle = new String[] {
            "Lũ ngu ngốc...",
            "Những kẻ yếu đuối...",
            "Ngươi chỉ có thế thôi à..."
        };
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
