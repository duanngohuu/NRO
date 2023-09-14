package real.boss;

import java.util.Timer;
import java.util.TimerTask;
import real.func.DHVT;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.MapManager;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.ServerManager;
import server.Service;
import server.Util;
import service.DAOS.PetDAO;
import service.Setting;

public class BossManager {

    static BossManager instance;

    public static BossManager gI() {
        if (instance == null) {
            instance = new BossManager();
        }
        return instance;
    }

    public static int zoneBlackGoku;
    public static int mapBlackGoku;
    public static int isSuperBlackGoku;
    public static boolean isBlackGoku;
    public static long currBlackGoku = System.currentTimeMillis();
    
    public static int zoneCumber;
    public static int mapCumber;
    public static int isCumber1;
    public static int isCumber2;
    
    
    public static boolean isXenBh;
    public static int ZoneXenCon;
    public static int countXenCon = 1;
    public static boolean isXenCon;
    public static long currXenCon = System.currentTimeMillis();
    
    public static boolean isCooler1;
    public static boolean isCooler2;
    public static int ZoneCooler;
    public static int MapCooler;
    public static long currCooler = System.currentTimeMillis();
    
    public static int ZoneSuper;
    public static int MapSuper;
    public static int MapDie;
    
    public static boolean isPILAP;
    public static int zonePILAP;
    public static int mapPILAP;
    public static long currPILAP = System.currentTimeMillis();
    
    public static boolean isMEOMAY;
    public static int zoneMEOMAY;
    public static int mapMEOMAY;
    public static long currMEOMAY = System.currentTimeMillis();
    
    public static byte numTapSu;
        
    public static boolean isZoro;
    public static long currZoro = System.currentTimeMillis();
    
    public static boolean isSuper;
    public static int Super_HP;
    public static int Super_ID;
    public static byte numSuper;
    public static long currSuper = System.currentTimeMillis();
    
    public static byte numSongohan;
    public static long currSongohan = System.currentTimeMillis();

    public static byte numThoDaiCa;
    public static long currThoDaiCa = System.currentTimeMillis();
    
    public static int intKuKu;
    public static long currKuKu = System.currentTimeMillis();
        
    public static boolean isTDST;
    public static int zoneTDST;
    public static int mapTDST;
    public static String countTDST;
    public static long currTDST = System.currentTimeMillis();
    
    public static boolean isFide;
    public static int countFide;
    public static int zoneFide;
    public static long currFide = System.currentTimeMillis();

    public static boolean isDrabula;
    public static int countDrabula;
    public static long currDrabula = System.currentTimeMillis();
    
    public static boolean isAdr;
    public static String countAdr;
    public static long currAdr = System.currentTimeMillis();
    
    public static boolean isAdr2;
    public static String countAdr2;
    public static long currAdr2 = System.currentTimeMillis();
    
    public static boolean isAdr3;
    public static String countAdr3;
    public static int zoneAdr3;
    public static long currAdr3 = System.currentTimeMillis();
    
    public static boolean isXenC1;
    public static boolean isXenC2;
    public static boolean isXenHt;
    public static int ZoneXen;
    public static long currXen = System.currentTimeMillis();
    
    public static boolean isCumber;
    public static long currCumber = System.currentTimeMillis();
    
    public static int numHalloween;
    public static int[] mapHalloween = {1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 27, 28, 29, 30, 31, 31, 32, 34, 35, 36, 37, 38, 39, 40, 41};
    public static long currHalloween = System.currentTimeMillis();
    
    public static boolean isRobot;
    public static long currRobot = System.currentTimeMillis();
    
    public static boolean isChilled;
    public static int countChilled;
    public static int zoneChilled;
    public static int mapChilled;
    public static long currChilled = System.currentTimeMillis();
    
    public static boolean isVidel;
    public static int countVidel;
    public static long currVidel = System.currentTimeMillis();
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;
    
    public int timeStart = 30;

    public void close() {
        try {
            task.cancel();
            timer.cancel();
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
                    BossManager.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
        
    public static void addBossDT(String name, int head, int body, int leg, int mapid, int zoneid, Item item, int hp, int dame){
        Boss_Template Super = new Boss_Template() {
            @Override
            public void startDie() {
                if (playerKill != null) {
                    int[] iditem = new int[]{17,18,19,20};
                    Item it = ItemData.gI().get_item(iditem[Util.nextInt(0,iditem.length - 1)]);
                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                }
            }
        };
        Super.head = (short) head;
        Super.body = (short) body;
        Super.leg = (short) leg;
        Super.name = name;
        Super.ZONE_JOIN = zoneid;
        Super.MAP_JOIN = new int[]{mapid};
        Super.inventory.itemsBody.add(0, item);
        Super.point.dameGoc = dame;
        Super.point.hpGoc = hp;
        Super.point.hp = hp;
        Super.point.defGoc = 30000;
        Super.timeExit = 1800000;
        Super.start();
        Super.active(1000);
    }
    
    public static void addBoss(String name, int head, int body, int leg, int mapid, int zoneid, Item item, int hp, int dame){
        Boss_Template Super = new Boss_Template() {
            @Override
            public void startDie() {
                if (playerKill != null) {
                    int trai = 0;
                    int phai = 1;
                    int next = 0;
                    for(int i = 0; i < playerKill.zone.getNumPlayerInMap(); i++){
                        int X = next == 0 ? -30 * trai : 30 * phai;
                        if(next == 0){
                            trai++;
                        }
                        else{
                            phai++;
                        }
                        next = next == 0 ? 1 : 0;
                        if(trai > 10){
                            trai = 0;
                        }
                        if(phai > 10){
                            phai = 1;
                        }
                        Service.gI().roiItem(itemDrop, zone, x + X, y, -1);
                    }
                }
            }
        };
        Super.head = (short)head;
        Super.body = (short)body;
        Super.leg = (short)leg;
        Super.name = name;
        Super.ZONE_JOIN = zoneid;
        Super.MAP_JOIN = new int[]{mapid};
        Super.point.dameGoc = dame;
        Super.point.hpGoc = hp;
        Super.point.hp = hp;
        Super.point.defGoc = 30000;
        Super.timeExit = 1800000;
        Super.itemDrop = item;
        Super.start();
        Super.active(500);
    }
    
    public static void addBoss_BDKB(String name, int head, int body, int leg, int mapid, int zoneid, Item item, int hp, int dame){
        Boss_Template Super = new Boss_Template() {
            @Override
            public void startDie() {
                if (playerKill != null) {
                    int trai = 0;
                    int phai = 1;
                    int next = 0;
                    for(int i = 0; i < playerKill.clan.KhoBauDuoiBien.level; i++){
                        int X = next == 0 ? -15 * trai : 15 * phai;
                        if(next == 0){
                            trai++;
                        }
                        else{
                            phai++;
                        }
                        next = next == 0 ? 1 : 0;
                        if(trai > 10){
                            trai = 0;
                        }
                        if(phai > 10){
                            phai = 1;
                        }
                        Item NGOC = itemDrop;
                        NGOC.quantity = 1;
                        Service.gI().roiItem(NGOC, zone, x + X, y, -1);
                    }
                }
            }
        };
        Super.head = (short)head;
        Super.body = (short)body;
        Super.leg = (short)leg;
        Super.name = name;
        Super.ZONE_JOIN = zoneid;
        Super.MAP_JOIN = new int[]{mapid};
        Super.point.dameGoc = dame;
        Super.point.hpGoc = hp;
        Super.point.hp = hp;
        Super.point.defGoc = 30000;
        Super.timeExit = 1800000;
        Super.itemDrop = item;
        Super.start();
        Super.active(500);
    }
    
    public void update() {
        if(timeStart > 0){
            timeStart--;
        }
        else
        {
            try
            {
                if (!isCooler1 && (Util.canDoWithTime(currCooler, 600000) || ServerManager.serverStart)) {
                    MapCooler = (byte) Util.nextInt(105,110);
                    ZoneCooler = Util.nextInt(0,MapManager.gI().getMapById(0).map_zone.length-1);
                    BossManager.currCooler = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currCooler = System.currentTimeMillis();
                            isCooler2 = true;
                            if(playerKill != null){
                                if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    it.itemOptions.add(new ItemOption(207 , 0));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = ZoneCooler;
                    Super.MAP_JOIN = new int[]{MapCooler};
                    Item item = ItemData.gI().get_item(650);
                    item.itemOptions.add(new ItemOption(108,40));
                    Super.inventory.itemsBody.add(0, item);
                    Super.name = "Cooler 1";
                    Super.head = 317;
                    Super.body = 318;
                    Super.leg = 319;
                    Super.point.dameGoc = 20000000;
                    Super.point.hpGoc = 1500000000;
                    Super.point.hp = 1500000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(600);
                    isCooler1 = true;
                }
                if (isCooler2 && Util.canDoWithTime(currCooler, 200)) {
                    BossManager.currCooler = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currCooler = System.currentTimeMillis();
                            isCooler2 = false;
                            isCooler1 = false;
                            if(playerKill != null){
                                if(!isExitBoss){
                                   if(Util.isTrue(40)){
                                        int[] itemId = new int[]{562,564,566,925};
                                        Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                        it.itemOptions.clear();
                                        it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                        it.itemOptions.add(new ItemOption(207 , 0));
                                        if(Util.isTrue(10)){
                                            it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                        }
                                        Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                    }
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = ZoneCooler;
                    Super.MAP_JOIN = new int[]{MapCooler};
                    Item item = ItemData.gI().get_item(650);
                    item.itemOptions.add(new ItemOption(108, 40));
                    Super.inventory.itemsBody.add(0, item);
                    Super.name = "Cooler 2";
                    Super.head = 320;
                    Super.body = 321;
                    Super.leg = 322;
                    Super.point.dameGoc = 20000000;
                    Super.point.hpGoc = 2000000000;
                    Super.point.hp = 2000000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(700);
                    isCooler2 = false;
                }
//                if(!isCumber && (Util.canDoWithTime(currCumber, 600000) || ServerManager.serverStart)){
//                    int[]map = new {
//                }
                
                if (!isBlackGoku && isSuperBlackGoku < 2 && (Util.canDoWithTime(currBlackGoku, 600000) || ServerManager.serverStart)) {
                    int[] map = new int[]{92,93,94,96,97,98,99,100};
                    mapBlackGoku = map[Util.nextInt(0,map.length-1)];
                    zoneBlackGoku = Util.nextInt(0,MapManager.gI().getMapById(mapBlackGoku).map_zone.length-1);;
                    Boss_Template Black = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currBlackGoku = System.currentTimeMillis();
                            BossManager.isBlackGoku = false;
                            if (playerKill != null) {
                                if(Util.isTrue(99)){
                                    int[] itemId = new int[]{561,561,561};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,2)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    it.itemOptions.add(new ItemOption(207 , 0));
                                    if(Util.isTrue(35)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,3)));
                                    }else if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(4,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                                isSuperBlackGoku += 1;
                            }
                        }
                    };
                    Black.ZONE_JOIN = zoneBlackGoku;
                    Black.MAP_JOIN = new int[]{mapBlackGoku};
                    Black.name = "Black Goku";
                    Black.head = 550;
                    Black.body = 551;
                    Black.leg = 552;
                    Black.point.dameGoc = 2000000;
                    Black.point.hpGoc = 1500000000;
                    Black.point.hp = 1500000000;
                    Black.point.defGoc = 30000;
                    Black.start();
                    Black.active(510);
                    
                    Black = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currBlackGoku = System.currentTimeMillis();
                            BossManager.isBlackGoku = false;
                            if (playerKill != null) {
                                if(Util.isTrue(40)){
                                    int[] itemId = new int[]{563,565,567};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,2)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    it.itemOptions.add(new ItemOption(207 , 0));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                                isSuperBlackGoku += 1;
                            }
                        }
                    };                    
                    Black.ZONE_JOIN = zoneBlackGoku;
                    Black.MAP_JOIN = new int[]{mapBlackGoku};
                    Black.name = "Zamasu";
                    Black.head = 903;
                    Black.body = 904;
                    Black.leg = 905;
                    Black.point.dameGoc = 2500000;
                    Black.point.hpGoc = 1500000000;
                    Black.point.hp = 1500000000;
                    Black.point.defGoc = 30000;
                    Black.start();
                    Black.active(700);
                    isSuperBlackGoku = 0;
                    isBlackGoku = true;
                }
                
                
                
                if(isSuperBlackGoku >= 2 && !isBlackGoku && Util.canDoWithTime(currBlackGoku, 20)){
                    Boss_Template SuperBlack = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currBlackGoku = System.currentTimeMillis();
                            if (playerKill != null) {
                                boolean isNhan = false;
                                boolean isCT = false;
                                if(!isExitBoss){
                                    if(Util.isTrue(20 + (Setting.SERVER_TEST ? 20 : 0))){
                                        isCT = true;
                                        Item it = ItemData.gI().get_item(957);
                                        it.itemOptions.clear();
                                        it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 35)));
                                        it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 35)));
                                        it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 35)));
                                        it.itemOptions.add(new ItemOption(116, 0));
                                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
                                        Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                    }
                                    else if(Util.isTrue(50) && playerKill.taskId == 31 && playerKill.taskIndex == 0){
                                        isNhan = true;
                                        Service.gI().roiItem(ItemData.gI().get_item(992), zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                    }
                                }
                                String txt = isNhan ? " và nhặt được nhẫn thời không " : isCT ? " và nhặt được cải trang Super Black Goku." : " mọi người đều ngưỡng mộ.";
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + ": đã tiêu diệt " + this.name + txt);
                                isSuperBlackGoku = 0;
                            }
                        }
                    };
                    SuperBlack.ZONE_JOIN = zoneBlackGoku;
                    SuperBlack.MAP_JOIN = new int[]{mapBlackGoku};
                    SuperBlack.name = "Super Black Goku";
                    SuperBlack.head = 553;
                    SuperBlack.body = 551;
                    SuperBlack.leg = 552;
                    SuperBlack.point.dameGoc = 10000000;
                    SuperBlack.point.hpGoc = 2000000000;
                    SuperBlack.point.hp = 2000000000;
                    SuperBlack.point.defGoc = 30000;
                    SuperBlack.start();
                    SuperBlack.active(650);
                    zoneBlackGoku = -1;
                    isSuperBlackGoku = 0;
                }
                // Cumber
                if (isCumber && (Util.canDoWithTime(currBlackGoku, 600000) || ServerManager.serverStart)) {
                    int[] map = new int[]{155};
                    mapCumber = map[0]; //Util.nextInt(0,map.length-1) 
                    zoneCumber = Util.nextInt(0,MapManager.gI().getMapById(mapBlackGoku).map_zone.length-1);
                    Boss_Template Black = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currCumber = System.currentTimeMillis();
                            BossManager.isCumber = false;
                            if (playerKill != null) {
                                if(Util.isTrue(99)){
                                    int[] itemId = new int[]{561,561,561};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,2)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    it.itemOptions.add(new ItemOption(207 , 0));
                                    if(Util.isTrue(35)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,3)));
                                    }else if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(4,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                                isSuperBlackGoku += 1;
                            }
                        }
                    };
                    Black.ZONE_JOIN = zoneCumber;
                    Black.MAP_JOIN = new int[]{mapCumber};
                    Black.name = "Cumber";
                    Black.head = 2020;
                    Black.body = 2021;
                    Black.leg = 2022;
                    Black.point.dameGoc = 2000000;
                    Black.point.hpGoc = 1500000000;
                    Black.point.hp = 1500000000;
                    Black.point.defGoc = 30000;
                    Black.start();
                    Black.active(510);
                    
//                    Black = new Boss_Template() {
//                        @Override
//                        public void startDie() {
//                            BossManager.currBlackGoku = System.currentTimeMillis();
//                            BossManager.isBlackGoku = false;
//                            if (playerKill != null) {
//                                if(Util.isTrue(40)){
//                                    int[] itemId = new int[]{563,565,567};
//                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,2)]);
//                                    it.itemOptions.clear();
//                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
//                                    it.itemOptions.add(new ItemOption(207 , 0));
//                                    if(Util.isTrue(10)){
//                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
//                                    }
//                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
//                                }
//                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
//                                isSuperBlackGoku += 1;
//                            }
//                        }
//                    };                    
//                    Black.ZONE_JOIN = zoneBlackGoku;
//                    Black.MAP_JOIN = new int[]{mapBlackGoku};
//                    Black.name = "Zamasu";
//                    Black.head = 903;
//                    Black.body = 904;
//                    Black.leg = 905;
//                    Black.point.dameGoc = 2500000;
//                    Black.point.hpGoc = 1500000000;
//                    Black.point.hp = 1500000000;
//                    Black.point.defGoc = 30000;
//                    Black.start();
//                    Black.active(700);
//                    isSuperBlackGoku = 0;
//                    isBlackGoku = true;
                }
                // end 
                if(!isXenBh && (Util.canDoWithTime(currXenCon, 600000) || ServerManager.serverStart)){
                    ZoneXenCon = Util.nextInt(0,MapManager.gI().getMapById(103).map_zone.length-1);
                    BossManager.currXenCon = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currXenCon = System.currentTimeMillis();
                            isXenBh = false;
                            isXenCon = false;
                            if (this.playerKill != null && !isExitBoss) {
                                if(Util.isTrue(30)){
                                    int[] itemId = {555,557,559};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,2)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    it.itemOptions.add(new ItemOption(207 , 0));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                            }
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = ZoneXenCon;
                    Super.MAP_JOIN = new int[]{103};
                    Super.name = "Siêu bọ hung";
                    Super.head = 234;
                    Super.body = 235;
                    Super.leg = 236;
                    Super.point.dameGoc = 250000;
                    Super.point.hpGoc = 500000000;
                    Super.point.hp = 500000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    isXenBh = true;
                }
                if(isXenBh && !isXenCon && countXenCon <= 7 && Util.canDoWithTime(currXenCon, 100)){
                    if(countXenCon >= 7){
                        isXenCon = true;
                    }
                    BossManager.currXenCon = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currXenCon = System.currentTimeMillis();
                            countXenCon--;
                            if (this.playerKill != null && Util.isTrue(10) && !isExitBoss) {
                                Item it = ItemData.gI().get_item(16);
                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                            }
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = ZoneXenCon;
                    Super.MAP_JOIN = new int[]{103};
                    Super.name = "Xên con " + countXenCon;
                    Super.head = 264;
                    Super.body = 265;
                    Super.leg = 266;
                    Super.point.dameGoc = 50000;
                    Super.point.hpGoc = 150000000;
                    Super.point.hp = 150000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(800);
                    countXenCon++;
                }
                if (!isXenC1 && (Util.canDoWithTime(currXen, 900500 + Util.nextInt(8000,12000)) || ServerManager.serverStart)) {
                    int zone = Util.nextInt(0,MapManager.gI().getMapById(104).map_zone.length-1);
                    BossManager.currXen = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currXen = System.currentTimeMillis();
                            isXenC2 = true;
                            if(playerKill != null){
                                if (Util.isTrue(15) && !isExitBoss) {
                                    Item it = ItemData.gI().get_item(16);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zone;
                    Super.MAP_JOIN = new int[]{100};
                    Super.name = "Xên bọ hung 1";
                    Super.head = 228;
                    Super.body = 229;
                    Super.leg = 230;
                    Super.point.dameGoc = 60000;
                    Super.point.hpGoc = 100000000;
                    Super.point.hp = 100000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    ZoneXen = zone;
                    isXenC1 = true;
                }
                if (isXenC2 && Util.canDoWithTime(currXen, 100)) {
                    BossManager.currXen = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currXen = System.currentTimeMillis();
                            isXenHt = true;
                            if(playerKill != null){
                                if (Util.isTrue(15) && !isExitBoss) {
                                    Item it = ItemData.gI().get_item(16);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = ZoneXen;
                    Super.MAP_JOIN = new int[]{100};
                    Super.name = "Xên bọ hung 2";
                    Super.head = 231;
                    Super.body = 232;
                    Super.leg = 233;
                    Super.point.dameGoc = 80000;
                    Super.point.hpGoc = 200000000;
                    Super.point.hp = 200000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    isXenC2 = false;
                }
                if (isXenHt && Util.canDoWithTime(currXen, 100)) {
                    BossManager.currXen = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currXen = System.currentTimeMillis();
                            isXenC1 = false;
                            isXenC2 = false;
                            isXenHt = false;
                            if(playerKill != null){
                                if (Util.isTrue(15) && !isExitBoss) {
                                    Item it = ItemData.gI().get_item(16);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = ZoneXen;
                    Super.MAP_JOIN = new int[]{100};
                    Super.name = "Xên hoàn thiện";
                    Super.head = 234;
                    Super.body = 235;
                    Super.leg = 236;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 500000000;
                    Super.point.hp = 500000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    isXenHt = false;
                }
                if (!isAdr2 && (Util.canDoWithTime(currAdr2, 900000 + Util.nextInt(12000,15000)) || ServerManager.serverStart)) {
                    int zone = Util.nextInt(0,MapManager.gI().getMapById(104).map_zone.length-1);
                    BossManager.currAdr2 = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currAdr2 = System.currentTimeMillis();
                            countAdr2 = "Android 14";
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zone;
                    Super.MAP_JOIN = new int[]{104};
                    Super.name = "Android 15";
                    Super.head = 261;
                    Super.body = 262;
                    Super.leg = 263;
                    Super.point.dameGoc = 50000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currAdr2 = System.currentTimeMillis();
                            countAdr2 = "Android 13";
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zone;
                    Super.MAP_JOIN = new int[]{104};
                    Super.name = "Android 14";
                    Super.head = 246;
                    Super.body = 247;
                    Super.leg = 248;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 30000000;
                    Super.point.hp = 30000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currAdr2 = System.currentTimeMillis();
                            isAdr2 = false;
                        }
                    };
                    Super.ZONE_JOIN = zone;
                    Super.MAP_JOIN = new int[]{104};
                    Super.name = "Android 13";
                    Super.head = 252;
                    Super.body = 253;
                    Super.leg = 254;
                    Super.point.dameGoc = 60000;
                    Super.point.hpGoc = 50000000;
                    Super.point.hp = 50000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countAdr2 = "Android 15";
                    isAdr2 = true;
                }
                
                if (!isAdr && (Util.canDoWithTime(currAdr, 900000 + Util.nextInt(15000,17000)) || ServerManager.serverStart)) {
                    int zone = Util.nextInt(0,MapManager.gI().getMapById(92).map_zone.length-1);
                    BossManager.currAdr = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currAdr = System.currentTimeMillis();
                            countAdr = "Dr.Kôrê";
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zone;
                    Super.MAP_JOIN = new int[]{93};
                    Super.name = "Android 19";
                    Super.head = 249;
                    Super.body = 250;
                    Super.leg = 251;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 50000000;
                    Super.point.hp = 50000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currAdr = System.currentTimeMillis();
                            isAdr = false;
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zone;
                    Super.MAP_JOIN = new int[]{93};
                    Super.name = "Dr.Kôrê";
                    Super.head = 255;
                    Super.body = 256;
                    Super.leg = 257;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 80000000;
                    Super.point.hp = 80000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countAdr = "Android 19";
                    isAdr = true;
                }
                
                if (!isFide && (Util.canDoWithTime(currFide, 900000 + Util.nextInt(17000,19000)) || ServerManager.serverStart)) {
                    zoneFide = Util.nextInt(0,MapManager.gI().getMapById(80).map_zone.length-1);
                    BossManager.currFide = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null) {
                                if(Util.isTrue(40)){
                                    Item it = ItemData.gI().get_item(16);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currFide = System.currentTimeMillis();
                            countFide = 2;
                        }
                    };
                    Super.ZONE_JOIN = zoneFide;
                    Super.MAP_JOIN = new int[]{80};
                    Super.name = "Fide Đại Ca 1";
                    Super.head = 183;
                    Super.body = 184;
                    Super.leg = 185;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countFide = 1;
                    isFide = true;
                }
                
                if (isFide && countFide == 2 && Util.canDoWithTime(currFide, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                if(Util.isTrue(40)){
                                    Item it = ItemData.gI().get_item(16);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currFide = System.currentTimeMillis();
                            countFide = 4;
                            if(isExitBoss){
                                countFide = -1;
                                isFide = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneFide;
                    Super.MAP_JOIN = new int[]{80};
                    Super.name = "Fide Đại Ca 2";
                    Super.head = 186;
                    Super.body = 187;
                    Super.leg = 188;
                    Super.point.dameGoc = 35000;
                    Super.point.hpGoc = 50000000;
                    Super.point.hp = 50000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countFide = 3;
                }
                
                if (isFide && countFide == 4 && Util.canDoWithTime(currFide, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                if(Util.isTrue(40)){
                                    Item it = ItemData.gI().get_item(16);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currFide = System.currentTimeMillis();
                            countFide = -1;
                            isFide = false;
                            if(isExitBoss){
                                countFide = -1;
                                isFide = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneFide;
                    Super.MAP_JOIN = new int[]{80};
                    Super.name = "Fide Đại Ca 3";
                    Super.head = 189;
                    Super.body = 190;
                    Super.leg = 191;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 80000000;
                    Super.point.hp = 80000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countFide = 5;
                }
                if (!isDrabula && (Util.canDoWithTime(currDrabula, 90000 + Util.nextInt(17000,19000)) || ServerManager.serverStart)) {
                 // if (ServerManager.serverStart) {               
                    BossManager.currDrabula = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null) {
                                if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                playerKill.currPower = 10;
                                Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 2;
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{114};
                    Super.name = "Drabula Đại Ca 1";
                    Super.head = 418;
                    Super.body = 419;
                    Super.leg = 420;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 1;
                    isDrabula = true;
                }
                
                if (isDrabula && countDrabula == 2 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                 if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                 playerKill.currPower = 10;
                                 Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 4;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{115};
                    Super.name = "Pui Pui Đại Ca 1";
                    Super.head = 451;
                    Super.body = 452;
                    Super.leg = 453;
                    Super.point.dameGoc = 35000;
                    Super.point.hpGoc = 50000000;
                    Super.point.hp = 50000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 3;
                }
                
                if (isDrabula && countDrabula == 4 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 4;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{114};
                    Super.name = "Drabula Đại Ca 1";
                    Super.head = 418;
                    Super.body = 419;
                    Super.leg = 420;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 4;
                 //   isDrabula = true;
                }
                
                if (isDrabula && countDrabula == 4 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                 if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                playerKill.currPower = 10;
                                Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 6;
                            isDrabula = false;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{117};
                    Super.name = "Drabula Đại Ca 2";
                    Super.head = 418;
                    Super.body = 419;
                    Super.leg = 420;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);                   
                    countDrabula = 5;
                }
                
                if (isDrabula && countDrabula == 5 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                 if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                playerKill.currPower = 10;
                                Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 8;
                            isDrabula = false;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{118};
                    Super.name = "Pui Pui Đại Ca 2";
                    Super.head = 451;
                    Super.body = 452;
                    Super.leg = 453;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 80000000;
                    Super.point.hp = 80000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 7;
                }
                
                /*if (isDrabula && countDrabula == 8 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                if(Util.isTrue(20)){
                                    Item it = ItemData.gI().get_item(17);
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                playerKill.currPower = 10;
                                Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 12;
                            isDrabula = false;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{114};
                    Super.name = "Drabula Đại Ca 2";
                    Super.head = 418;
                    Super.body = 419;
                    Super.leg = 420;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 11;
                   // isDrabula = true;
                }*/
                if (isDrabula && countDrabula == 7 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                 if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                playerKill.currPower = 10;
                                Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = 10;
                            isDrabula = false;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{119};
                    Super.name = "Yacon Đại Ca ";
                    Super.head = 415;
                    Super.body = 416;
                    Super.leg = 417;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 80000000;
                    Super.point.hp = 80000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 9;
                   // isDrabula = true;
                }
                if (isDrabula && countDrabula == 9 && Util.canDoWithTime(currDrabula, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null && !isExitBoss) {
                                 if(Util.isTrue(40)){
                                    int[] itemId = {556,558,560,925};
                                    Item it = ItemData.gI().get_item(itemId[Util.nextInt(0,3)]);
                                    it.itemOptions.clear();
                                    it.itemOptions.addAll(ItemData.gI().get_op_do_than(it.template.type));
                                    if(Util.isTrue(10)){
                                        it.itemOptions.add(new ItemOption(107 , Util.nextInt(1,8)));
                                    }
                                    Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                                playerKill.currPower = 10;
                                Service.gI().sendThongBao(playerKill, "Xuống tầng tiếp theo nào");
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currDrabula = System.currentTimeMillis();
                            countDrabula = -1;
                            isDrabula = false;
                            if(isExitBoss){
                                countDrabula = -1;
                                isDrabula = false;
                            }
                        }
                    };
                    Super.ZONE_JOIN = 0;
                    Super.MAP_JOIN = new int[]{120};
                    Super.name = "Mabư Đại Ca";
                    Super.head = 297;
                    Super.body = 298;
                    Super.leg = 299;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countDrabula = 15;
                   // isDrabula = true;
                }
                
                
                
                
                if (!isTDST && (Util.canDoWithTime(currTDST, 600000 + Util.nextInt(19000,22000)) || ServerManager.serverStart)) {
                    int[] maps = new int[]{82,83,79};
                    mapTDST = maps[Util.nextInt(0,maps.length-1)];
                    zoneTDST = Util.nextInt(0,MapManager.gI().getMapById(mapTDST).map_zone.length-1);
                    BossManager.currTDST = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currTDST = System.currentTimeMillis();
                            countTDST = "Số 3";
                            if(playerKill != null)
                            {
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneTDST;
                    Super.MAP_JOIN = new int[]{mapTDST};
                    Super.name = "Số 4";
                    Super.head = 168;
                    Super.body = 169;
                    Super.leg = 170;
                    Super.point.dameGoc = 40000;
                    Super.point.hpGoc = 10000000;
                    Super.point.hp = 10000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(700);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currTDST = System.currentTimeMillis();
                            countTDST = "Số 2";
                            if(playerKill != null)
                            {
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneTDST;
                    Super.MAP_JOIN = new int[]{mapTDST};
                    Super.name = "Số 3";
                    Super.head = 174;
                    Super.body = 175;
                    Super.leg = 176;
                    Super.point.dameGoc = 45000;
                    Super.point.hpGoc = 15000000;
                    Super.point.hp = 15000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(650);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currTDST = System.currentTimeMillis();
                            countTDST = "Số 1";
                            if(playerKill != null)
                            {
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneTDST;
                    Super.MAP_JOIN = new int[]{mapTDST};
                    Super.name = "Số 2";
                    Super.head = 171;
                    Super.body = 172;
                    Super.leg = 173;
                    Super.point.dameGoc = 47000;
                    Super.point.hpGoc = 20000000;
                    Super.point.hp = 20000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(600);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currTDST = System.currentTimeMillis();
                            countTDST = "Tiểu Đội Trưởng";
                            if(playerKill != null)
                            {
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneTDST;
                    Super.MAP_JOIN = new int[]{mapTDST};
                    Super.name = "Số 1";
                    Super.head = 177;
                    Super.body = 178;
                    Super.leg = 179;
                    Super.point.dameGoc = 48000;
                    Super.point.hpGoc = 25000000;
                    Super.point.hp = 25000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(550);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currTDST = System.currentTimeMillis();
                            mapTDST = -1;
                            zoneTDST = -1;
                            isTDST = false;
                            if(playerKill != null)
                            {
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneTDST;
                    Super.MAP_JOIN = new int[]{mapTDST};
                    Super.name = "Tiểu Đội Trưởng";
                    Super.head = 180;
                    Super.body = 181;
                    Super.leg = 182;
                    Super.point.dameGoc = 49000;
                    Super.point.hpGoc = 90000000;
                    Super.point.hp = 90000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countTDST = "Số 4";
                    isTDST = true;
                }
                
                if (intKuKu <= 50 && Util.canDoWithTime(currKuKu, 5000)) {
                    short[][] head = {{159,160,161},{165,166,167},{162,163,164}};
                    String[] Name = {"KuKu","Mập Đầu Đinh","Rambo"};
                    int[][] map = {{68 ,69, 70, 71, 72},{64 ,65 ,63 ,66 ,67},{73, 74, 75, 76 ,77}};
                    int[] hp = {5000000,10000000,15000000};
                    int[] dame = {5000,10000,15000};
                    int boss = 0;
                    if(Util.isTrue(50)){
                        boss = 0;
                    }else if(Util.isTrue(50)){
                        boss = 1;
                    }else{
                        boss = 2;
                    }
                    BossManager.currKuKu = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currKuKu = System.currentTimeMillis();
                            BossManager.intKuKu--;
                        }
                    };
                    Super.MAP_JOIN = map[boss];
                    Super.name = Name[boss];
                    Super.head = head[boss][0];
                    Super.body = head[boss][1];
                    Super.leg = head[boss][2];
                    Super.point.dameGoc = dame[boss];
                    Super.point.hpGoc = hp[boss];
                    Super.point.hp = hp[boss];
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    intKuKu++;
                }
                
                if (!isChilled && (Util.canDoWithTime(currChilled, 1200000 + Util.nextInt(17000,19000)) || ServerManager.serverStart)) {
                    int[] map = new int[]{160,161,162,163};
                    mapChilled = map[Util.nextInt(0,map.length - 1)];
                    zoneChilled = Util.nextInt(0,MapManager.gI().getMapById(mapChilled).map_zone.length-1);
                    BossManager.currChilled = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currChilled = System.currentTimeMillis();
                            countChilled = 2;
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneChilled;
                    Super.MAP_JOIN = new int[]{mapChilled};
                    Super.name = "Chilled cấp 1";
                    Super.head = 1024;
                    Super.body = 1025;
                    Super.leg = 1026;
                    Super.point.dameGoc = 700000;
                    Super.point.hpGoc = 1000000000;
                    Super.point.hp = 1000000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countChilled = 1;
                    isChilled = true;
                }

                if (isChilled && countChilled == 2 && Util.canDoWithTime(currChilled, 100)) {
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            BossManager.currChilled = System.currentTimeMillis();
                            countChilled = -1;
                            isChilled=false;
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                        }
                    };
                    Super.ZONE_JOIN = zoneChilled;
                    Super.MAP_JOIN = new int[]{mapChilled};
                    Super.name = "Chilled cấp 2";
                    Super.head = 1021;
                    Super.body = 1022;
                    Super.leg = 1023;
                    Super.point.dameGoc = 1200000;
                    Super.point.hpGoc = 2000000000;
                    Super.point.hp = 2000000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countChilled = 3;
                }
                
                if (!isAdr3 && (Util.canDoWithTime(currAdr3, 900000 + Util.nextInt(12000,15000)) || ServerManager.serverStart)) {
                    zoneAdr3 = Util.nextInt(0,MapManager.gI().getMapById(97).map_zone.length-1);
                    BossManager.currAdr3 = System.currentTimeMillis();
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null &&Util.isTrue(20) && !isExitBoss) {
                                Item it = ItemData.gI().get_item(16);
                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                            }
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currAdr3 = System.currentTimeMillis();
                            countAdr3 = "Pic";
                        }
                    };
                    Super.ZONE_JOIN = zoneAdr3;
                    Super.MAP_JOIN = new int[]{97};
                    Super.name = "Poc";
                    Super.head = 240;
                    Super.body = 241;
                    Super.leg = 242;
                    Super.point.dameGoc = 20000;
                    Super.point.hpGoc = 100000000;
                    Super.point.hp = 100000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (this.playerKill != null &&Util.isTrue(20) && !isExitBoss) {
                                Item it = ItemData.gI().get_item(16);
                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                            }
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currAdr3 = System.currentTimeMillis();
                            countAdr3 = "King Kong";
                        }
                    };
                    Super.ZONE_JOIN = zoneAdr3;
                    Super.MAP_JOIN = new int[]{97};
                    Super.name = "Pic";
                    Super.head = 237;
                    Super.body = 238;
                    Super.leg = 239;
                    Super.point.dameGoc = 22000;
                    Super.point.hpGoc = 120000000;
                    Super.point.hp = 120000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    
                    Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            if (this.playerKill != null &&Util.isTrue(20) && !isExitBoss) {
                                Item it = ItemData.gI().get_item(16);
                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                            }
                            if(playerKill != null){
                                Service.gI().sendThongBaoBenDuoi(playerKill.name + " đã tiêu diệt " + this.name + " mọi người đều ngưỡng mộ.");
                            }
                            BossManager.currAdr3 = System.currentTimeMillis();
                            isAdr3 = false;
                        }
                    };
                    Super.ZONE_JOIN = zoneAdr3;
                    Super.MAP_JOIN = new int[]{97};
                    Super.name = "King Kong";
                    Super.head = 243;
                    Super.body = 244;
                    Super.leg = 245;
                    Super.point.dameGoc = 30000;
                    Super.point.hpGoc = 140000000;
                    Super.point.hp = 140000000;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    countAdr3 = "Poc";
                    isAdr3 = true;
                }
                
                if (numSuper < 50) {
                    BossManager.currSuper = System.currentTimeMillis();
                    int[] map = new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 10, 5, 13, 19, 20};
                    MapSuper = map[Util.nextInt(0,map.length-1)];
                    ZoneSuper = Util.nextInt(0,MapManager.gI().getMapById(MapSuper).map_zone.length-1);
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if(this.point.hpGoc >= 1000 && !isExitBoss/*  && Util.isTrue(20 + (DHVT.gI().Hour >= Setting.TIME_START_SUPER ? 20 : 0))*/){
                                BossManager.Super_HP = this.point.hpGoc*2;
                                BossManager.Super_ID = this.id;
                                BossManager.isSuper = true;
                                
                                Item it = ItemData.gI().get_item(568);
                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                BossManager.MapDie = this.zone.map.id;
                            }
                            BossManager.currSuper = System.currentTimeMillis();
                            BossManager.numSuper--;
                        }
                    };
                    Super.playerSkill.skills.add(SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 5, 0, 0));
//                    Super.MAP_JOIN = new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 10, 5, 13, 19, 20};
                    Super.MAP_JOIN = new int[]{MapSuper};
                    Super.name = "Broly " + (-Boss_Template.idb);
                    Super.head = 291;
                    Super.body = 292;
                    Super.leg = 293;
                    // Super.point.dameGoc = 500000;
                    int HPGOC = Util.nextInt((DHVT.gI().Hour >= Setting.TIME_START_SUPER ? 100000 : 500), (DHVT.gI().Hour >= Setting.TIME_START_SUPER ? 200000 : 1000));
                    Super.point.dameGoc = 50000;
//                    int HPGOC = 1500000;
                    Super.point.hpGoc = HPGOC;
                    Super.point.hp = HPGOC;
                    Super.point.defGoc = 30000;
                    
                    Super.start();
                    Super.timeExit = 86400000;
                    Super.active(500);
                    numSuper++;
                }
                
                if (isSuper) {
                    isSuper = false;
                    Boss_Template Super = new Boss_Template() {
                        @Override
                        public void startDie() {
                            if (playerKill != null) {
                                if (playerKill.pet == null) {
                                    PetDAO.newPet(playerKill);
                                    playerKill.pet.changeStatus((byte)1);
                                    playerKill.sendMeHavePet();
                                    playerKill.pet.point.updateall();
//                                Item it = ItemData.gI().get_item(568);
//                                Service.gI().roiItem(it, zone, x, y, (playerKill == null ? -1 : playerKill.id));
                                }
                            }
                            BossManager.numSuper--;
                        }
                    };
                    Super.playerSkill.skills.add(SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 7, 0, 0));
                    Super.MAP_JOIN = new int[]{MapDie};
                    //Super.MAP_JOIN = new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 10, 5, 13, 19, 20};
                    Super.name = "Super Broly " + Math.abs(Super_ID);
                    Super.head = 294;
                    Super.body = 295; // Super
                    Super.leg = 296;
                    Super.point.dameGoc = 500000;
                    Super.point.hpGoc = Super_HP;
                    Super.point.hp = Super_HP;
                    Super.point.defGoc = 30000;
                    Super.start();
                    Super.active(500);
                    numSuper++;
                }
                if(ServerManager.serverStart){
                    ServerManager.serverStart = false;
                }
            }
            catch (Exception ex) {
            }
        }
    }
}
