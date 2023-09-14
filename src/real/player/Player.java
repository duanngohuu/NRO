package real.player;

import service.DAOS.PlayerDAO;
import real.pet.Pet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.clan.Clan;
import real.func.ChangeMap;
import real.func.Combine;
import real.func.DHVT;
import real.func.DHVT23;
import real.func.NRSD;
import real.func.PVP;
import real.func.Transaction;
import real.item.CaiTrangData;
import real.item.Item;
import real.item.ItemTimes;
import real.magictree.MagicTree;
import real.map.Zone;
import real.map.MapService;
import real.map.WayPoint;
import real.pet.MobMe;
import real.skill.Skill;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;
import real.item.ItemBua;
import real.item.ItemData;
import real.item.ItemOption;
import real.magictree.MabuEgg;
import real.map.MapManager;
import real.map.Mob;
import real.pet.NewPet;
import static real.pet.Pet.FUSION;
import real.radar.Radar;
import real.task.Task;
import real.task.TaskData;
import real.task.TaskOrders;
import server.SQLManager;
import server.io.ISession;
import service.Chat;
import service.DAOS.PetDAO;
import service.Setting;
import service.data.Init;

public class Player {

    public Integer[] LIST_GIFT = {926,925,927,441, 442, 443, 444, 445, 446, 
        447, 740, 741, 860, 865, 954, 955, 967, 983, 984, 995, 996, 997, 998, 
        999, 1000, 1001, 1002, 1014, 1022, 1023, 1024, 1025, 1026, 1028, 1029, 
        1031, 1110, 1138, 1139, 1140, 1147, 1148, 1149, 1150, 1152, 1159, 1208, 
        942, 943, 944, 988};
    
    public boolean isTV = false;
    
    public int currPower = 0;
    public int maxPower = 10;
    
    public int currPage = -1;
    public int currTab = -1;
    
    public DHVT23 DHVT_23 = new DHVT23(this);
    
    public String[] input;
    public int typeInput = -1;
    public int answer;
    public int count_move;
    public int expDonate;
    public int tabBua = 1;
    public Player playerKill;
    public Player playerAtt;
    public Player playerTran;
    public byte role;
    public short speacial[] = new short[3];
    public long delayUsePeans;
    public long delayTran;
    
    public int maxTime = 30;
    public int type = 0;
    public int start = -1;
    
    public long currCTG = 0l;
    public long currOnchat = 0;
    public byte[] bo_mong_finish = null;
    public byte[] bo_mong_reviece = null;
    public long currenTimeUpdateBua = 0l;
    public long currHoi;

    private long lastTimeUpdate = System.currentTimeMillis();
    public long lastTimeXinDau;
    public long currNextMap;
    
    public long lastTimeGTL = System.currentTimeMillis();
    
    public long lastTimeYardrat = System.currentTimeMillis();

    public static final byte NON_PK = 0;
    public static final byte PK_PVP = 3;
    public static final byte PK_ALL = 5;

    public static final byte LUONG_LONG_NHAT_THE = 4;
    public static final byte HOP_THE_PORATA2 = 6;
    public static final byte HOP_THE_PORATA = 5;

    public ISession session;

    public Zone zone = null;
    public long currenTimeUpdate = 0l;

    public Zone zoneBeforeCapsule = null;
    public int cxBeforeCapsule = -1;
    
    public List<Zone> mapCapsule;

    public boolean isNewMember;
    public Player playerTrade;
    public MagicTree magicTree;
    public MabuEgg mabuEgg;
    
    public Pet pet = null;
    public NewPet newpet = null;
    public MobMe mobMe;
    public boolean isPet;
    public boolean isNewPet;
    public byte typeFusion = 0;
    public byte typeShip = 1;

    public boolean isBoss;
    
    public long LastTimeAutoItem;
    public long LastTimePickItem;
    public long LastTimeCheckBand;
    
    public static final int timeFusion = 600000;
    public long lastTimeFusion;

    public Entities listPlayer;
    public Point point;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public Combine combine;
    public Clan clan = null;
    public ItemTimes itemTime;
    public int SkillUser = 0;

    public int loader = 0;

    public int id;
    public String name;
    public byte gender;

    public short head;
    public short body;
    public short leg;

    public byte typePk;

    public byte cFlag;
    public long lastTimeChangeFlag;

    public short taskId;
    public byte taskIndex;
    public int taskCount;
    
    public long lastTimeEvent;
    public int eventPoint;
    public int pointTET;

    public int x;
    public int y;
    public int xSend;
    public int ySend;
    public int moveCount;
    public long lastTimeMove;

    private byte useSpaceShip;

    private short npcMenu;
    private short indexMenu;
    public short selectMenu;

    public boolean isGoHome;

    public boolean justRevived;
    public long lastTimeRevived = System.currentTimeMillis();
    public boolean immortal;
    
    public Timestamp login_time;
    public boolean isFly;
    public long LastTimeSummon = System.currentTimeMillis();
    public int countSummon;
    public long LastTimeZone = System.currentTimeMillis();
    
    public boolean isNRSD = false;
    public short idNRSD = -1;
    public short idNRNM = -1;
    public long currHaveNRSD;
    public long lastChatNRSD;
    public int percentHpPlus = -1;
    public int percentDamePlus = -1;
    
    public TaskOrders taskOrder = null;
    public int TaskOrder_Count = 10;
    public long currTimeTaskOrder;
    
    public Radar radar = null;
    
    public long timeJoinClan;
    
    public void dispose() {
        try {
            inventory.giftCode.clear();
            inventory.itemsBag.clear();
            inventory.itemsBody.clear();
            inventory.itemsBox.clear();
            inventory.itemsBoxSecond.clear();
            inventory.itemsBua.clear();
            playerSkill.skills.clear();
            mapCapsule.clear();
            listPlayer.friends.clear();
            listPlayer.enemies.clear();
        } catch (Exception e) {
        } finally {
            login_time = null;
            name = null;
            bo_mong_finish = null;
            bo_mong_reviece = null;
            listPlayer.friends = null;
            listPlayer.enemies = null;
            listPlayer = null;
            inventory.giftCode = null;
            inventory.itemsBag = null;
            inventory.itemsBody = null;
            inventory.itemsBox = null;
            inventory.itemsBoxSecond = null;
            inventory.itemsBua = null;
            inventory = null;
            playerSkill.skillShortCut = null;
            playerSkill.skills = null;
            playerSkill.skillSelect = null;
            playerSkill = null;
            point = null;
            combine = null;
            clan = null;
            itemTime = null;
            magicTree = null;
            playerTrade = null;
            mapCapsule = null;
            pet = null;
            newpet = null;
            zone = null;
            session = null;
            playerKill = null;
        }
    }
    
    public void claimGold(){
        int quantity = this.session.get_gold();
        if(isTV){
            if(this.getBagNull() < 1){
                Service.gI().sendThongBao(this, "Hành trang của bạn không đủ 1 ô trống");
                return;
            }
            Service.gI().sendThongBao(this, "Bạn đã nhận được 10k Thỏi vàng");
            Item it = ItemData.gI().get_item(457);
            it.quantity = 10000;
            this.inventory.addItemBag(it);
            this.inventory.sendItemBags();
            isTV = false;
        }
        if(quantity > 0){
            if(this.getBagNull() < 1){
                Service.gI().sendThongBao(this, "Hành trang của bạn không đủ 1 ô trống");
                return;
            }
            Service.gI().sendThongBao(this, "Bạn đã nhận được " + quantity +" Thỏi vàng");
            this.session.remove_gold(quantity);
            Item it = ItemData.gI().get_item(457);
            it.quantity = quantity;
            this.inventory.addItemBag(it);
            this.inventory.sendItemBags();
        }
    }
    
    public int getLevel(){
        int level = 0;
        if(this.point.power >= 1200 && this.point.power <= 17999999999L){
            level = 0;
        }else if(this.point.power >= 17999999999L && this.point.power <= 19999999999L){
            level = 1;
        }else if(this.point.power >= 19999999999L && this.point.power <= 24999999999L){
            level = 2;
        }else if(this.point.power >= 24999999999L && this.point.power <= 29999999999L){
            level = 3;
        }else if(this.point.power >= 29999999999L && this.point.power <= 34999999999L){
            level = 4;
        }else if(this.point.power >= 34999999999L && this.point.power <= 39999999999L){
            level = 5;
        }else if(this.point.power >= 39999999999L && this.point.power <= 50010000000L){
            level = 6;
        }else if(this.point.power >= 50010000000L && this.point.power <= 60010000000L){
            level = 7;
        }else if(this.point.power >= 60010000000L && this.point.power <= 80010000000L){
            level = 8;
        }else if(this.point.power >= 80010000000L && this.point.power <= 120010000000L){
            level = 9;
        }
        return level;
    }
    
    public void claimGem(int index){
        int gemClam = 0;
        int money =0;
        switch(index){
            case 0:
                gemClam = 50000;
                money = 10000;
                break;
            case 1:
                gemClam = 120000;
                money = 20000;
                break;
            case 2:
                gemClam = 320000;
                money = 50000;
                break;
            default:
                gemClam = 0;
                money =0;
                break;
        }
        if (this.session.get_money() <= 0 || this.session.get_money() < money || money<=0 || gemClam<=0) {
            return;
        }
        Service.gI().sendThongBao(this, "Bạn đã nhận được " + gemClam +" ngọc");
        this.session.remove_money(money);
        this.inventory.gem += gemClam;
        Service.gI().sendMoney(this);
    }
    
    public int get_bag() {
        int b = -1;
        if(this.isPl()){
            if (clan != null) {
                b = clan.imgID;
            }
            Item item = this.inventory.itemsBody.get(8);
            if (item != null) {
                b = item.template.part;
            }
            if(this.taskId == 3 && this.taskIndex >= 2){
                b = 28;
            }
            if(idNRNM != -1){
                b = 30;
            }
            if(idNRSD != -1){
                b = 31;
            }
        }
        return b;
    }

    public void ExtendBag() {
        try {
            this.inventory.itemsBag.add(this.inventory.itemsBag.size(), null);
            this.inventory.sendItemBags();
            this.inventory.sortItem(this.inventory.itemsBag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ExtendBox() {
        try {
            this.inventory.itemsBox.add(this.inventory.itemsBox.size(), null);
            this.inventory.sendItemBox();
            this.inventory.sortItem(this.inventory.itemsBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Item get_Pean_In_Box() {
        try {
            for (byte i = 0; i < this.inventory.itemsBox.size(); ++i) {
                if (inventory.itemsBox.get(i).template.type == 6) {
                    return inventory.itemsBox.get(i);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public byte getBagNull() {
        byte num = 0;
        for (byte i = 0; i < this.inventory.itemsBag.size(); ++i) {
            if (this.inventory.itemsBag.get(i) == null) {
                num++;
            }
        }
        return num;
    }

    public byte getBoxNull() {
        byte num = 0;
        for (byte i = 0; i < this.inventory.itemsBox.size(); ++i) {
            if (this.inventory.itemsBox.get(i) == null) {
                ++num;
            }
        }
        return num;
    }

    public void openBox() {
        Message m;
        try {
            m = new Message(-35);
            m.writer().writeByte(1);
            this.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player() {
        point = new Point(this);
        inventory = new Inventory(this);
        playerSkill = new PlayerSkill(this);
        combine = new Combine(this);
        itemTime = new ItemTimes(this);
        listPlayer = new Entities(this);
    }

    //--------------------------------------------------------------------------
    public void setUseSpaceShip(byte useSpaceShip) {
        // 0 - không dùng
        // 1 - tàu vũ trụ theo hành tinh
        // 2 - dịch chuyển tức thời
        // 3 - tàu tenis
        this.useSpaceShip = useSpaceShip;
    }

    public byte getUseSpaceShip() {
        return this.useSpaceShip;
    }

    public boolean isDie() {
        return this.point != null && this.point.getHP() <= 0;
    }
    
    public short getNPCMenu() {
        return npcMenu;
    }

    public void setNPCMenu(int NPC) {
        this.npcMenu = (short)NPC;
    }

    public short getIndexMenu() {
        return indexMenu;
    }

    public void setIndexMenu(int indexMenu) {
        this.indexMenu = (short) indexMenu;
    }
    
     public short getSelectMenu() {
        return selectMenu;
    }

    public void setSelectMenu(short indexMenu) {
        this.selectMenu = indexMenu;
    }
    
    public int getPageMenu() {
        return currPage;
    }

    public void setPageMenu(int currPage) {
        this.currPage = currPage;
    }

    //--------------------------------------------------------------------------
    public void move(int _toX, int _toY) {
        if (_toX != this.x) {
            this.x = _toX;
        }
        if (_toY != this.y) {
            this.y = _toY;
        }
        MapService.gI().playerMove(this);
    }

    public void gotoMap(Zone join) {
        this.DHVT_23.close();
        if (this.idNRSD != -1) {
            this.idNRSD = -1;
            Service.gI().sendBag(this);
        }
        if(join.isOSIN() && this.isPl()){
            int[] indexs = {9, 10};
            Service.gI().chooseFlag(this, indexs[Util.nextInt(0, 1)], true);
        }
        PVP.gI().finishPVP(this, PVP.TYPE_LEAVE_MAP);
        Transaction.gI().StopTran(this);
        if (this.zone != null) {
            if(!this.isBoss){
                this.percentDamePlus = -1;
                Service.gI().point(this);
            }
            MapService.gI().exitMap(this, this.zone);
        }
        if (join != null) {
            if (this.mobMe != null) {
                mobMe.goToMap(join);
            }
            this.zone = join;
            this.zone.getPlayers().add(this);
        }
    }

    public void exitMap() {
        if (this.zone != null) {
            this.zone.getPlayers().remove(this);
            if(!this.isBoss){
                this.percentDamePlus = -1;
                Service.gI().point(this);
            }
            MapService.gI().exitMap(this, this.zone);
            if(this.pet != null){
                MapService.gI().exitMap(this.pet, this.zone);
                this.pet.zone = null;
            }
            this.zone = null;
        }
    }

    public WayPoint isInWaypoint() {
        for (WayPoint wp : zone.wayPoints) {
            if (x + 100 >= wp.minX && x <= wp.maxX + 100 && y + 100 >= wp.minY && y <= wp.maxY + 100) {
                return wp;
            }
        }
        return null;
    }

    //--------------------------------------------------------------------------
    public void setSession(Session session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public void close() {
        try {
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
                    Player.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }

    public void sendInfo() {
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte) 4);

            if (this.session.get_version() >= 214) {
                msg.writer().writeLong(this.inventory.gold);//xu
            } else {
                msg.writer().writeInt((int) this.inventory.gold);//xu
            }
            msg.writer().writeInt(this.inventory.gem);//luong
            msg.writer().writeInt(this.point.hp);//chp
            msg.writer().writeInt(this.point.mp);//cmp
            msg.writer().writeInt(this.inventory.ruby);//ruby
            this.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public short getParamItem(int optionID) {
        switch (optionID) {
            case 97:
                return 5;
            case 77:
                return 5;
            case 103:
                return 5;
            case 80:
                return 5;
            case 81:
                return 5;
            case 50:
                return 3;
            case 108:
                return 2;
        }
        return 0;
    }
    
    public boolean opstar(short id) {
        return id == 77 || id == 103 || id == 80 || id == 81 || id == 50 || id == 108 || id == 97;
    }

    public boolean isBand() {
        if (inventory.itemsBody.stream().anyMatch((it) -> (it != null && it.template.id != 921 && it.itemOptions.stream().anyMatch((op) -> (op != null && opstar((short) op.optionTemplate.id) && combine.starSlot(it) > 0 && combine.starSlot(it) * getParamItem(op.optionTemplate.id) < combine.getParam(it, op.optionTemplate.id)))))) {
            return true;
        }
        if (inventory.itemsBag.stream().anyMatch((it) -> (it != null && it.template.id != 921 && it.itemOptions.stream().anyMatch((op) -> (op != null && opstar((short) op.optionTemplate.id) && combine.starSlot(it) > 0 && combine.starSlot(it) * getParamItem(op.optionTemplate.id) < combine.getParam(it, op.optionTemplate.id)))))) {
            return true;
        }
        if (inventory.itemsBox.stream().anyMatch((it) -> (it != null && it.template.id != 921 && it.itemOptions.stream().anyMatch((op) -> (op != null && opstar((short) op.optionTemplate.id) && combine.starSlot(it) > 0 && combine.starSlot(it) * getParamItem(op.optionTemplate.id) < combine.getParam(it, op.optionTemplate.id)))))) {
            return true;
        }
        if (pet != null) {
            if (pet.inventory.itemsBody.stream().anyMatch((it) -> (it != null && it.template.id != 921 && it.itemOptions.stream().anyMatch((op) -> (op != null && opstar((short) op.optionTemplate.id) && combine.starSlot(it) > 0 && combine.starSlot(it) * getParamItem(op.optionTemplate.id) < combine.getParam(it, op.optionTemplate.id)))))) {
                return true;
            }
        }
        return false;
    }
    
    public int getParamMax(Item item , int optionid){
        int paramMax = 0;
        Item it = ItemData.gI().get_item(item.template.id);
        if(it != null){
            for(ItemOption op : it.itemOptions){
                if(op != null && op.optionTemplate.id == optionid){          
                    for(int i = 0 ; i <= 7 ; i++){
                        if(i == 0){
                            paramMax = op.param;
                        }else{
                            paramMax += paramMax / 100 * 10;
                        }
                    }
                }
            }
        }
        return paramMax;
    }
    
    public void removeItem(){
        boolean isRemove = false;
        if(this.inventory.gold < 0){
            this.inventory.gold = 0;
            isRemove = true;
        }
        if(this.inventory.gem < 0){
            this.inventory.gem = 0;
            isRemove = true;
        }
        if(this.inventory.ruby < 0){
            this.inventory.ruby = 0;
            isRemove = true;
        }
        if(isRemove){
            this.inventory.sendItemBags();
            this.inventory.sendItemBox();
            Service.gI().Send_Caitrang(this);
            Service.gI().sendMoney(this);
        }
    }
    
    public void removeNRSD(){
        List<Item> gift = new ArrayList<>();
        for(Item item : this.inventory.itemsGift){
            if(item != null && ((System.currentTimeMillis() - item.buyTime) / 1000) / 3600 >= 24 && NRSD.isNRSD(item.template.id)){
                gift.add(item);
            }
            else if (item == null){
                gift.add(item);
            }
        }
        for(Item item : gift){
            this.inventory.itemsGift.remove(item);
        }
    }
    public int currTime = 300;
    public void update() {
        try {
            if (this.pet != null && this.pet.status != 3 && this.pet.status != 4) {
                this.pet.active(Setting.DELAY_PET);
            }
            
            if(this.point.power > Setting.LIMIT_SUC_MANH[this.point.limitPower]){
                this.point.power = Setting.LIMIT_SUC_MANH[this.point.limitPower];
            }
            if(loader == 2)
            {
                loader = 3;
                // claimGem();
            }
            if (loader == 1){
                loader = 2;
                claimGold();
            }
            if (loader == 0) {
                if(this.session != null){
                    loader = 1;
                    PlayerManger.gI().checkPlayer(this.session.get_user_id());
                    return;
                }
            }
            if(this.role < 99 && isBand() && Util.canDoWithTime(LastTimeCheckBand, 5000)){
                this.LastTimeCheckBand = System.currentTimeMillis();
                Player b = PlayerManger.gI().getPlayerByID(id);
                SQLManager.execute("UPDATE `player` SET role='1' WHERE `account_id`='" + b.session.get_user_id() + "'");
                if (b != null) {
                    b.session.disconnect();
                }
                return;
            }
            removeNRSD();
            removeItem();
            if(Chat.isBaoTri){
                Transaction tran = Transaction.gI().findTran(this);
                if(tran != null){
                   tran.cancelTrade();
                   tran.dispose();
                }
            }
            if(playerTran != null && Util.canDoWithTime(delayTran, 10000)) {
                playerTran = null;
            }
            playerSkill.update();
            if (mobMe != null) {
                mobMe.update();
            }
            if(this.zone.isMapDTDN() && (this.clan == null || this.clan != null && this.clan.DoanhTrai == null || this.clan != null && this.clan.DoanhTrai != null && this.clan.DoanhTrai.time <= 0)){
                ChangeMap.gI().changeMap(this, 21 + gender, 400, 5);
            }
            if(this.zone.isMapKGHD() && (this.clan == null || this.clan != null && this.clan.KhiGasHuyDiet == null || this.clan != null && this.clan.KhiGasHuyDiet != null && this.clan.KhiGasHuyDiet.time <= 0)){
                ChangeMap.gI().changeMap(this, 0, 500, 5);
            }
            if(this.zone.isMapBDKB() && (this.clan == null || this.clan != null && this.clan.KhoBauDuoiBien == null || this.clan != null && this.clan.KhoBauDuoiBien != null && this.clan.KhoBauDuoiBien.time <= 0)){
                ChangeMap.gI().changeMap(this, 5, 100, 5);
            }
            if(this.role < Setting.ROLE_ADMIN && !ChangeMap.gI().listMapCanChange(this).stream().anyMatch(id -> zone.map.id == id)){
                ChangeMap.gI().changeMap(this, 21 + gender, 400, 5);
            }
            if(this.role < Setting.ROLE_ADMIN && this.zone.isOSIN() && DHVT.gI().Hour != Setting.TIME_START_OSIN_1 && DHVT.gI().Hour != Setting.TIME_START_OSIN_2){
                ChangeMap.gI().changeMap(this, 21 + gender, 400, 5);
            }
            if(DHVT.gI().Hour != Setting.TIME_START_HIRU_1 && DHVT.gI().Hour != Setting.TIME_START_HIRU_2){
                if(this.zone == null || this.zone.map.id == 126){
                    ChangeMap.gI().changeMap(this, 19, -1, 5);
                }
            }
            if(isNRSD && zone.isNRSD() && (DHVT.gI().Hour > Setting.TIME_START || DHVT.gI().Hour < Setting.TIME_START)){
                this.isNRSD = false;
                Service.gI().chooseFlag(this, 0, true);
                ChangeMap.gI().changeMap(this, 24 + this.gender, -1, 5);
            }
            if(this.idNRSD != -1 && Util.canDoWithTime(lastChatNRSD, Util.nextInt(5,7) * 1000)){
                int timeLeft = (int) ((Setting.TIME_WIN_NRSD - (System.currentTimeMillis() - this.currHaveNRSD)) / 1000);
                if(timeLeft <= 0){
                    Item item = NRSD.getNRSD(this);
                    this.percentHpPlus = -1;
                    this.idNRSD = -1;
                    Service.gI().sendBag(this);
                    Service.gI().point(this);
                    Thread.sleep(Util.nextInt(500, 2000));
                    if(this.inventory.itemsGift.stream().anyMatch(it -> it != null && it.template.id == item.template.id))
                    {
                        int flagID = Util.nextInt(7);
                        this.zone.getPlayers().stream().filter(player -> this.clan != null && player.clan != null && player != this && player.clan.id == this.clan.id).forEach((pl) -> {
                            Service.gI().chooseFlag(pl, flagID, true);
                        });
                        Service.gI().chooseFlag(this, flagID, true);
                        return;
                    }
                    this.inventory.itemsGift.add(item);
                    this.zone.zoneDoneNRSD = true;
                    if(this.clan != null){
                        this.clan.members.stream().filter(m -> m != null && m.id != this.id).forEach((m) -> {
                            Player pl = PlayerManger.gI().getPlayerByID(m.id);
                            if(pl != null){
                                if(!pl.inventory.itemsGift.stream().anyMatch(it -> it != null && it.template.id == item.template.id)){
                                    pl.inventory.itemsGift.add(item);
                                }
                            }
                            else if(pl == null) {
                                pl = PlayerDAO.loadDB(m.id);
                                if(pl != null){
                                    if(!pl.inventory.itemsGift.stream().anyMatch(it -> it != null && it.template.id == item.template.id)){
                                        pl.inventory.itemsGift.add(item);
                                        PlayerDAO.updateDB2(pl);
                                    }
                                }
                            }
                        });
                    }
                    this.zone.getPlayers().stream().filter(pl -> pl != null && pl.isPl() && (pl.clan != null && pl.clan.id != this.clan.id || pl.clan == null && pl.id != this.id)).forEach((pl) -> {
                        Service.gI().sendThongBao(pl, "Thật đáng buồn bạn đã thua vui lòng chờ 20h - 21h ngày mai để tiếp tục");
                        pl.isNRSD = true;
                    });
                    this.zone.getPlayers().stream().filter(pl -> pl != null && pl.isPl() && (pl.id == this.id || this.clan != null && pl.clan != null && pl.clan.id == this.clan.id)).forEach((pl) -> {
                        Service.gI().sendThongBao(pl, "Chúc mừng bạn đã dành được chiến thắng tối ngày hôm nay!");
                        pl.isNRSD = true;
                    });
                }else{
                    Service.gI().sendThongBao(this, "Bạn sẽ giành chiến thắng sau " + timeLeft + " giây nữa");
                }
                lastChatNRSD = System.currentTimeMillis();
            }
            if (this.newpet == null && this.inventory.itemsBody.get(7) != null) {
                Item item = this.inventory.itemsBody.get(7);
                switch (item.template.id) {
                    case 942:
                        PetDAO.Pet2(this, 966, 967, 968);
                        break;
                    case 943:
                        PetDAO.Pet2(this, 969, 970, 971);
                        break;
                    case 944:
                        PetDAO.Pet2(this, 972, 973, 974);
                        break;
                    case 1117:
                    case 1055:
                        PetDAO.Pet2(this, 1155, 1156, 1157);
                        break;
                    case 1199:
                        PetDAO.Pet2(this, 1186, 1187, 1188);
                        break;
                    case 1213:
                        PetDAO.Pet2(this, 1204, 1205, 1206);
                        break;
                    default:
                        break;
                }
                Service.gI().point(this);
            }
            else if (this.newpet != null && this.inventory.itemsBody.get(7) == null) {
                PetDAO.Pet2Exit(this);
                Service.gI().point(this);
            }
            //Hồi Hp/30s
            if ((System.currentTimeMillis() - lastTimeHoiHp) > 30000 && !isDie() && (point.getHP() < point.getHPFull())) {
                int hpHoi = 0;
                for (Item it : inventory.itemsBody) {
                    if (it != null) {
                        for (ItemOption iop : it.itemOptions) {
                            switch (iop.optionTemplate.id) {
                                case 27:
                                    hpHoi += iop.param;
                                    break;
                                case 80:
                                    hpHoi += point.getHPFull() * iop.param / 100;
                                    break;    
                            }
                        }
                    }
                }    
                hoi_hp(hpHoi);
                lastTimeHoiHp = System.currentTimeMillis();
            }
            //Hồi Ki/30s
            if ((System.currentTimeMillis() - lastTimeHoiMp) > 30000 && !isDie() && (point.getMP() < point.getMPFull())) {
                int mpHoi = 0;
                for (Item it : inventory.itemsBody) {
                    if (it != null) {
                        for (ItemOption iop : it.itemOptions) {
                            switch (iop.optionTemplate.id) {
                                case 28:
                                    mpHoi += iop.param;
                                    break;
                                case 81:
                                    mpHoi += point.getMPFull() * iop.param /100;
                                    break;    
                            }
                        }
                    }
                }    
                hoi_ki(mpHoi);
                lastTimeHoiMp = System.currentTimeMillis();
            }
            if (zone != null && !MapManager.gI().isMapOffline(zone.map.id)) {
                DrPhuTung();
                if (!isPet) {
                    if (this != null && ItemBua.ItemBuaExits(this, 219) && Util.canDoWithTime(LastTimeAutoItem, 1000)) {
                        if (!this.isDie()) {
                            Service.gI().pickItem(this);
                        }
                        this.LastTimeAutoItem = System.currentTimeMillis();
                    }

                    if (System.currentTimeMillis() - currenTimeUpdate >= 1000) {
                        currenTimeUpdate = System.currentTimeMillis();
                        itemTime.Update();
                    }

                    if (typeFusion == LUONG_LONG_NHAT_THE && !this.itemTime.ExitsItemTiem(gender == 1 ? 3901 : 3790)) {
                        pet.unFusion();
                    }

                    if (System.currentTimeMillis() - currenTimeUpdateBua >= 60000) {
                        ItemBua.UpdateTimeBua(this, this.inventory.itemsBua);
                        currenTimeUpdateBua = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            if(this.role >= 99){
                Util.debug("Player.Update - Name: " + this.name + "\n---------");
                e.printStackTrace();
            }
        }
    }
    
    public void DrPhuTung(){
        try{
            if(this.role < 99){
                if(this.point.hpGoc > this.point.getHpMpLimit()){
                    this.point.hpGoc = this.point.getHpMpLimit();
                }
                if(this.point.mpGoc > this.point.getHpMpLimit()){
                    this.point.mpGoc = this.point.getHpMpLimit();
                }
                if(this.point.dameGoc > this.point.getDameLimit()){
                    this.point.dameGoc = this.point.getDameLimit();
                }
                if(this.point.defGoc > this.point.getDefLimit()){
                    this.point.defGoc = this.point.getDefLimit();
                }
                if(this.point.critGoc > this.point.getCritLimit()){
                    this.point.critGoc = this.point.getCritLimit();
                }
                int hp = this.point.getHP();
                int hpMax = this.point.getHPFull();
                if(hp > hpMax){
                    this.point.setHP(hpMax);
                }
                if(this.isPl()){
                    Service.gI().point(this);
                }
            }
            if (zone != null && !MapManager.gI().isMapOffline(zone.map.id)) {
                if (!isBoss && !isNewPet) {
                    if (this.zone.isCooler() && checkCooler && !this.inventory.OptionCt(106)) {
                        Service.gI().point(this);
                        Service.gI().sendThongBao(this, "Sức tấn công và HP của bạn sẽ bị giảm 50% vì lạnh");
                        checkCooler = false;
                    }
                    else if (!checkCooler && !this.zone.isCooler()) {
                        Service.gI().point(this);
                        Service.gI().sendThongBao(this, "Bạn đã rời khỏi hành tinh băng tấn công và HP được hồi phục");
                        checkCooler = true;
                    }
                    if (inventory.OptionCt(109) && System.currentTimeMillis() - zone.currChat >= 15000) {
                        zone.SetHPMPAllPlayerInZone(this, 1);
                        zone.currChat = System.currentTimeMillis();
                    }
                    else if (inventory.OptionCt(111) && System.currentTimeMillis() - zone.currChat >= 15000) {
                        zone.SetHPMPAllPlayerInZone(this, 2);
                        zone.currChat = System.currentTimeMillis();
                    }
                    else if (inventory.OptionCt(162) && System.currentTimeMillis() - zone.currChat >= 1000) {
                        zone.SetHPMPAllPlayerInZone(this, 3);
                        int p = this.point.get_percent_option_ct(162);
                        hoi_ki(point.getMPFull() / 100 * p);
                        zone.currChat = System.currentTimeMillis();
                    }
                    else if (point.getHP() < point.getHPFull() && inventory.OptionCt(8) && System.currentTimeMillis() - zone.currChat >= 5000) {
                        zone.SetHPMPAllPlayerInZone(this, 4);
                        zone.SetMPHPAllMobInZone(this, 4);
                        zone.currChat = System.currentTimeMillis();
                    }
                    else if (inventory.OptionCt(29) && System.currentTimeMillis() - zone.currChat >= 30000) {
                        Service.gI().chat(this, "Biến Socola");
                        zone.SetHPMPAllPlayerInZone(this, 5);
                        zone.currChat = System.currentTimeMillis();
                    }
                    else if (inventory.OptionCt(26) && System.currentTimeMillis() - zone.currChat >= 30000) {
                        Service.gI().chat(this, "Phẹt");
                        zone.SetHPMPAllPlayerInZone(this, 6);
                        zone.currChat = System.currentTimeMillis();
                    }
                    else if (inventory.OptionCt(117) && System.currentTimeMillis() - zone.currChat >= 15000) {
                        zone.SetHPMPAllPlayerInZone(this, 7);
                        zone.currChat = System.currentTimeMillis();
                    }
                    
                    if(inventory.OptionPhuKien(167) && System.currentTimeMillis() - zone.currChat >= 5000){
                        zone.SetHPMPAllPlayerInZone(this, 8);
                        zone.currChat = System.currentTimeMillis();
                    }
                }
            }
        }
        catch (Exception e) {
            
        }
    }
    
    public boolean checkCooler = true;

    public boolean isPl() {
        return !isPet && !isBoss && !isNewPet;
    }

    //--------------------------------------------------------------------------
    private static final short[][] idFusion = {{380, 381, 382}, {383, 384, 385}, {391, 392, 393}};
//    private static final short[][] idFusion2 = {{870, 871, 872}, {873, 874, 875}, {867, 868, 869}};
    private static final short[][] idFusion2 = {{1250, 1251, 1252}, {1253, 1254, 1255}, {1256, 1257, 1258}}; //td, nm, xd
    public final short[][] idHalloween ={{545,548,549},{651,652,653},{654,655,656}};
    public int indexHeadHalloween;
    public short getHead() {
        try {
            if(isBoss)
            {
                return head;
            }
            if (playerSkill.isMaTroi)
            {
                int plus = this.gender == 0 ? 0 : this.gender == 1 ? 2 : 1;
                return (short) (idHalloween[indexHeadHalloween][0] + plus);
            }
            else if (playerSkill.isMonkey) {
                return Init.HEADMONKEY[playerSkill.getLevelMonkey() - 1];
            }
            else if (playerSkill.isBinh) {
                return 1221;
            }
            else if (playerSkill.isHoaDa) {
                return 454;
            }
            else if (playerSkill.isSocola) {
                return 412;
            }
            else if (typeFusion != 0 && !inventory.OptionCt(38)) {
                if (typeFusion == LUONG_LONG_NHAT_THE) {
                    return idFusion[this.gender == 1 ? 2 : 0][0];
                } else if (typeFusion == HOP_THE_PORATA) {
                    return idFusion[this.gender == 1 ? 2 : 1][0];
                } else if (typeFusion == HOP_THE_PORATA2) {
                    switch (gender) {
                        case 0:
                            return idFusion2[0][0];
                        case 1:
                            return idFusion2[1][0];
                        case 2:
                            return idFusion2[2][0];
                        default:
                            break;
                    }
                }
            }
            else if (inventory.itemsBody.get(5) != null) {
                CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
                if (ct != null) {
                    return (short)(ct.getID()[0] != -1 ? ct.getID()[0] : head);
                }
            }
        } catch (Exception e) {
        }
        return head;
    }

    public short getBody() {
        try {
            if(isBoss)
            {
                return body;
            }
            body = (short)(gender == 1 ? 59 : 57);
            if (playerSkill.isMaTroi) {
                return idHalloween[indexHeadHalloween][1];
            }
            else if (playerSkill.isMonkey) {
                return 193;
            }
            else if (playerSkill.isHoaDa) {
                return 455;
            }
            else if (playerSkill.isBinh) {
                return 1222;
            }
            else if (playerSkill.isSocola) {
                return 413;
            }
            else if (typeFusion != 0 && !inventory.OptionCt(38)) {
                if (typeFusion == LUONG_LONG_NHAT_THE) {
                    return idFusion[this.gender == 1 ? 2 : 0][1];
                }
                else if (typeFusion == HOP_THE_PORATA) {
                    return idFusion[this.gender == 1 ? 2 : 1][1];
                }
                else if (typeFusion == HOP_THE_PORATA2) {
                    switch (gender) {
                        case 0:
                            return idFusion2[0][1];
                        case 1:
                            return idFusion2[1][1];
                        case 2:
                            return idFusion2[2][1];
                        default:
                            break;
                    }
                }
            }
            else if (inventory.itemsBody.get(5) != null) {
                CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
                if (ct != null && ct.getID()[1] != -1) {
                    return (short) ct.getID()[1];
                }
            }
            if (inventory.itemsBody.get(0) != null)
            {
                return inventory.itemsBody.get(0).template.part;
            }
        } catch (Exception e) {
        }
        return body;
    }

    public short getLeg() {
        try {
            if(isBoss)
            {
                return leg;
            }
            leg = (short)(gender == 1 ? 60 : 58);
            if (playerSkill.isMaTroi) {
                return idHalloween[indexHeadHalloween][2];
            }
            else if (playerSkill.isMonkey) {
                return 194;
            }
            else if (playerSkill.isHoaDa) {
                return 456;
            }
            else if (playerSkill.isBinh) {
                return 1223;
            }
            else if (playerSkill.isSocola) {
                return 414;
            }
            else if (typeFusion != 0 && !inventory.OptionCt(38)) {
                if (typeFusion == LUONG_LONG_NHAT_THE) {
                    return idFusion[this.gender == 1 ? 2 : 0][2];
                } else if (typeFusion == HOP_THE_PORATA) {
                    return idFusion[this.gender == 1 ? 2 : 1][2];
                } else if (typeFusion == HOP_THE_PORATA2) {
                    switch (gender) {
                        case 0:
                            return idFusion2[0][2];
                        case 1:
                            return idFusion2[1][2];
                        case 2:
                            return idFusion2[2][2];
                        default:
                            break;
                    }
                }
            }
            else if (inventory.itemsBody.get(5) != null) {
                CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
                if (ct != null && ct.getID()[2] != -1) {
                    return (short) ct.getID()[2];
                }
            }
            if (inventory.itemsBody.get(1) != null) {
                return inventory.itemsBody.get(1).template.part;
            }
        } catch (Exception e) {
        }
        return leg;
    }

    public boolean isMount(Item item) {
        return item.template.id == 346 || item.template.id == 347 || item.template.id == 348 
                || item.template.id == 349 || item.template.id == 350 || item.template.id == 351 
                || item.template.id == 396 || item.template.id == 532 || item.template.id == 1144;
    }

    public short getMount() {
        if(this.isPl()){
            if(this.session.get_version() >= 221){
                for (Item item : inventory.itemsBody) {
                    if (item != null) {
                        if (item.template.type == 23 || item.template.type == 24 && (item.template.gender == 3 || item.template.gender == this.gender)) {
                            if (isMount(item)) {
                                return item.template.id;
                            } else {
                                short[][] mount = new short[][]{
                                    {0, 4},
                                    {733, 3},
                                    {734, 5},
                                    {735, 4},
                                    {743, 2},
                                    {744, 3},
                                    {746, 3},
                                    {795, 4},
                                    {849, 3},
                                    {897, 4},
                                    {920, 3},
                                    {1102, 2},
                                    {1141, 4},
                                    {1154, 5}
                                };
                                for(short i = 0; i < mount.length; i++){
                                    short id = mount[i][0];
                                    if(id == item.template.id){
                                        return (short)(i + 30000);
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                for (Item item : inventory.itemsBag) {
                    if (item != null) {
                        if (item.template.type == 23 || item.template.type == 24 && (item.template.gender == 3 || item.template.gender == this.gender)) {
                            if (isMount(item)) {
                                return item.template.id;
                            } else {
                                short[][] mount = new short[][]{
                                    {0, 4},
                                    {733, 3},
                                    {734, 5},
                                    {735, 4},
                                    {743, 2},
                                    {744, 3},
                                    {746, 3},
                                    {795, 4},
                                    {849, 3},
                                    {897, 4},
                                    {920, 3},
                                    {1102, 3},
                                    {1141, 4},
                                    {1154, 4}
                                };
                                for(short i = 0; i < mount.length; i++){
                                    short id = mount[i][0];
                                    if(id == item.template.id){
                                        return (short)(i + 30000);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    //--------------------------------------------------------------------------
    public synchronized int injured(Player plAtt, Mob mobAtt, int hp, boolean... die)
    {
        try
        {
            if(this.name.equals("Trung Úy Trắng")){
                if(zone.getMobInMap(22) > 0)
                {
                    return 0;
                }
            }
            if(this.name.contains("Broly")){
                int PTHP = (int)((float)((float)this.point.getHP() / (float)this.point.getHPFull()) * 100);
                if(PTHP < 50 && Util.canDoWithTime(lastTimeHoiPhuc, 15000)){
                    lastTimeHoiPhuc = System.currentTimeMillis();
                    if(point.hpGoc < 160707777){
                        int HP_HOI = point.hpGoc / 100 * 10;
                        point.hpGoc += HP_HOI;
                        if(point.hpGoc > 160707777){
                            point.hpGoc = 160707777;
                        }
                        point.hp += HP_HOI;
                        Service.gI().Reload_HP_NV(this);
                    }
                }
                int HP_P = point.hpGoc / 100 * 10;
                if(hp > HP_P){
                    hp = HP_P;
                }
            }
            int nedon = this.point.get_percent_option(108);
            nedon = nedon > 90 ? 90 : nedon;
            if (this.isDie() || Util.isTrue(nedon)) {
                return 0;
            }
            if(plAtt != null){
                int pst = point.get_percent_option(97);
                if (pst > 0 && (!plAtt.isBoss || plAtt.name.contains("Broly"))) {
                    Service.gI().phan_sat_thuong(plAtt, null, hp / 100 * pst);
                }
                if (this.inventory.OptionCt(3) && plAtt.playerSkill.getSelectSkill2()) {
                    int hoi_ki = hp / 100 * this.point.get_percent_option(3);
                    this.hoi_ki(hoi_ki);
                    return 0;
                }
                if(this.isPl() && this.pet != null && this.pet.status > 0 && this.pet.status < 3 && !this.pet.findPlayer(plAtt)){
                    this.pet.AddPlayerAttack(plAtt);
                    Service.gI().chat(this.pet, plAtt.name + " mi làm ta nổi dận rồi đó");
                }
                else if(this.isPet && !((Pet)this).findPlayer(plAtt) && ((Pet)this).status > 0 && ((Pet)this).status < 3 && !((Pet)this).master.equals(plAtt)) {
                    ((Pet)this).AddPlayerAttack(plAtt);
                    Service.gI().chat(((Pet)this), plAtt.name + " mi làm ta nổi dận rồi đó");
                }
                boolean isDam = this.name.startsWith("Tập sự") || this.name.startsWith("Tân binh") || this.name.startsWith("Chiến binh") || this.name.startsWith("Đội trưởng");
                if(this.isBoss && isDam){
                    int PointHP = plAtt.point.dameGoc;
                    if(hp > PointHP){
                        hp = PointHP;
                    }
                    hp -= hp / 100 * Util.nextInt(20, 40);
                }
                
                boolean isYD = plAtt.name.startsWith("Tập sự") || plAtt.name.startsWith("Tân binh") || plAtt.name.startsWith("Chiến binh") || plAtt.name.startsWith("Đội trưởng");
                if(plAtt.isBoss && !isYD && this.zone.map.id != 129){
                    hp = this.point.getHPFull() / Util.nextInt(3, 10);
                }

                int giap = this.point.get_percent_option(94);
                giap = giap > 90 ? 90 : giap;
                hp = hp - (hp / 100 * giap);
            }
            if(this.isBoss && (this.name.startsWith("Videl") || this.name.startsWith("Pi láp") || this.name.equals("Mai") || this.name.equals("Su") || this.name.equals("Chaien") || this.name.equals("Xuka") || this.name.equals("Nobita") || this.name.equals("Xekô") || this.name.equals("Đôrêmon"))){
                int PointHP = point.hp / 100;
                if(hp < 10000){
                    hp = 0;
                }
                if(point.hp < 100){
                    PointHP = 1;
                }
                if(hp > PointHP){
                    hp = PointHP;
                }
            }
            
            int pst = point.get_percent_option(97);
            if (pst > 0 && mobAtt != null) {
                Service.gI().phan_sat_thuong(null, mobAtt, hp / 100 * pst);
            }
            
            if (this.playerSkill.isShielding) {
                if (plAtt != null && hp > this.point.getHPFull()) {
                    this.playerSkill.shieldDown();
                    Service.gI().sendThongBao(this, "Khiên năng lượng của bạn đã bị vỡ");
                    Service.gI().removeItemTime(this, 3784);
                }
                hp = 1;
            }
            
            if (isPl() && mobAtt != null && (ItemBua.ItemBuaExits(this, 217)) && this.point.hp == 1) {
                return 0;
            }
            
            point.hp -= hp;
            if (point.hp <= 0)
            {
                playerKill = plAtt;
                Service.gI().charDie(this);
                if(plAtt != null && plAtt.isPet && ((Pet)plAtt).findPlayer(this)){
                    Service.gI().chat(((Pet)plAtt), plAtt.name + " mi làm ta nổi dận rồi đó");
                }
                if(plAtt != null && isPl() && plAtt.listPlayer.enemies.contains(id)){
                    plAtt.listPlayer.removeEnemy(id);
                }
                else if (plAtt != null && isPl() && plAtt != this && plAtt.id > 0 && !plAtt.isPet && !plAtt.isNewPet && !plAtt.isBoss) {
                    this.listPlayer.addEnemy((int) plAtt.id);
                    Task t = TaskData.getTask(plAtt.taskId);
                    if (t.counts[plAtt.taskIndex] != -1 && t.killId[plAtt.taskIndex] == 999)
                    {
                        plAtt.taskCount += 1;
                        if (plAtt.taskCount < t.counts[plAtt.taskIndex])
                        {
                            Service.gI().send_task(plAtt, t);
                        }
                        else
                        {
                            Service.gI().send_task_next(plAtt);
                        }
                    }
                    if(plAtt.taskOrder != null && plAtt.taskOrder.taskId == 1)
                    {
                        if (plAtt.taskOrder.count < plAtt.taskOrder.maxCount)
                        {
                            plAtt.taskOrder.count++;
                            Service.gI().sendThongBao(plAtt, "Nhiệm vụ của bạn là " + plAtt.taskOrder.name + ". Tiến trình: " + plAtt.taskOrder.count +" / " + plAtt.taskOrder.maxCount);
                        }
                        else
                        {
                            plAtt.taskOrder.count = plAtt.taskOrder.maxCount;
                            Service.gI().sendThongBao(plAtt, "Đã hoàn thành nhiệm vụ!Đến với Bò mộng báo cáo ngay nào");
                        }
                        Service.gI().send_task_orders(plAtt, plAtt.taskOrder);
                    }
                }
                if (isPl()) {
                    PVP.gI().finishPVP(this, PVP.TYPE_DIE);
                    Transaction.gI().StopTran(this);
                }
            }
            if(plAtt != null && plAtt.isBoss){
                if(plAtt.name.contains("Dơi Nhí") || plAtt.name.contains("Ma Trơi")|| plAtt.name.equals("Bộ Xương")){
                    if (!this.playerSkill.isMaTroi) {
                        this.indexHeadHalloween = Util.nextInt(0,2);
                        this.playerSkill.setMaTroi(System.currentTimeMillis(), 600000);
                        Service.gI().Send_Caitrang(this);
                    }
                    hp += this.point.getHPFull() / 100;
                }else if(plAtt.name.equals("Thỏ Đầu Khấc")){
                    hp += this.point.getHPFull() / 100;
                }
            }
            return hp;
        }
        catch (Exception e){
            if(this.role == 99){
                Util.debug("Player.injured - Name: " + this.name);
                e.printStackTrace();
            }
            return 1;
        }
    }

    public boolean setMPUseSkill() {
        if (playerSkill.skillSelect == null) {
            return false;
        }
        if (this.isBoss) {
            return true;
        }
        boolean canUseSkill = false;
        int mana;
        switch (playerSkill.skillSelect.template.id) {
            case Skill.THAI_DUONG_HA_SAN:
                int manaFull = point.getMPFull();
                mana = (manaFull / 100) * playerSkill.skillSelect.manaUse;
                if (speacial[0] == 1) {
                    mana += (manaFull / 100) * speacial[1];
                }
                if (point.mp >= mana) {
                    point.mp -= mana;
                    canUseSkill = true;
                }
                break;
            default:
                switch (playerSkill.skillSelect.template.manaUseType) {
                    case 0:
                        if (this.point.mp >= playerSkill.skillSelect.manaUse) {
                            this.point.mp -= playerSkill.skillSelect.manaUse;
                            canUseSkill = true;
                        }
                        break;
                    case 1:
                        mana = (int) ((float) (this.point.getMPFull() / 100 * playerSkill.skillSelect.manaUse));
                        if (this.point.mp >= mana) {
                            this.point.mp -= mana;
                            canUseSkill = true;
                        }
                        break;
                    case 2:
                        this.point.mp = 0;
                        canUseSkill = true;
                        break;
                }
        }

        Service.gI().sendInfoChar30c4(this);
        return canUseSkill;
    }

    public long lastTimeHoiPhuc;
    public long lastTimeHoiHp;
    public long lastTimeHoiMp;

    public void hoi_hp(int hp) {
        if (point.getHP() == 0 || isDie()) {
            return;
        }
        int newHP = point.hp + hp;
        if (newHP >= point.getHPFull()) {
            point.hp = point.getHPFull();
        } else if(newHP > 0) {
            point.hp += hp;
        } else if(newHP <= 0){
            point.hp = 0;
        }
        Message msg = null;
        try {
            if (this.isPl()) {
                msg = Service.gI().messageSubCommand((byte) 5);
                msg.writer().writeInt(this.point.hp);
                this.sendMessage(msg);
            }
            Service.gI().Reload_HP_NV(this);
            msg.cleanup();
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void hoi_ki(int mp) {
        if (point.getHP() == 0 || isDie()) {
            return;
        }
        int newMP = point.mp + mp;
        if (newMP >= point.getMPFull()) {
            point.mp = point.getMPFull();
        } else if(newMP > 0) {
            point.mp += mp;
        } else if(newMP <= 0){
            point.mp = 0;
        }
        Message msg = null;
        try {
            if (this.isPl()) {
                msg = Service.gI().messageSubCommand((byte) 6);
                msg.writer().writeInt(this.point.mp);
                this.sendMessage(msg);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void hoiphuc(int hp, int mp) { //Hồi phục máu + mana
        if (point.hp == 0 || isDie()) {
            return;
        }
        final int hpfull = point.getHPFull();
        if (point.hp + hp >= hpfull || point.hp + hp < 0) {
            point.hp = point.hp + hp < 0 ? 1 : hpfull;
        } else {
            point.hp += hp;
        }
        final int mpfull = point.getMPFull();
        if (point.mp + mp >= point.getMPFull() || point.mp + mp < 0) {
            point.mp = point.mp + mp < 0 ? 1 : mpfull;
        } else {
            point.mp += mp;
        }
        try {
            Service.gI().Send_Info_NV(this);
            if (isPl()) {
                sendInfoHPMP();
            }
        } catch (Exception e) {

        }
    }

    public void sendInfoHPMP() {
        if (!isPl()) {
            return;
        }
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte) 5);
            msg.writer().writeInt(this.point.hp);
            this.sendMessage(msg);
            msg.cleanup();
            msg = Service.gI().messageSubCommand((byte) 6);
            msg.writer().writeInt(this.point.mp);
            this.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    //--------------------------------------------------------------------------
    public void sendItemTime() {
        if (this.typeFusion != 0) {
            pet.status = FUSION;
            point.updateall();
            Service.gI().Send_Caitrang(this);
            Service.gI().point(this);
        }
    }

    public void sendMeHavePet() {
        Message msg = null;
        try {
            msg = new Message(-107);
            msg.writer().writeByte(this.pet == null ? 0 : 1);
            this.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void setJustRevivaled() {
        this.justRevived = true;
        this.lastTimeRevived = System.currentTimeMillis();
        this.immortal = true;
    }

    public void attackPlayer(Player plAttack, boolean miss, int...dame)
    {
        int attackDame = plAttack.point.getDameAttack(true) / (dame.length > 0 ? dame[0] : 1);
        int dameHit = this.injured(plAttack, null, miss ? 0 : attackDame, false);
        if (this.itemTime.ExitsItemTiem(2757))
        {
            dameHit /= 2;
        }
        if (this.itemTime.ExitsItemTiem(10712))
        {
            dameHit /= 2.5;
        }
        if (dameHit > 0)
        {
            int hut_hp = plAttack.point.get_percent_hut_hp(false);
            int hut_ki = plAttack.point.get_percent_option(96);
            if (hut_hp > 0)
            {
                plAttack.hoi_hp(dameHit * hut_hp / 100);
            }
            if (hut_ki > 0)
            {
                plAttack.hoi_ki(dameHit * hut_ki / 100);
            }
        }
        Message msg = null;
        try
        {
            int hut_hp = plAttack.point.get_percent_hut_hp(false);
            int hut_ki = plAttack.point.get_percent_option(96);
            boolean isEff = hut_hp > 0 || hut_ki > 0;
            if (isEff)
            {
                Service.gI().sendEff(this, 37, x, y);
            }
            msg = new Message(-60);
            msg.writer().writeInt((int) plAttack.id); // id pem
            msg.writer().writeByte(dame.length == 0 ? plAttack.playerSkill.skillSelect.skillId : plAttack.playerSkill.skillSelect.template.id); // skill pem
            msg.writer().writeByte(1); // số người pem
            msg.writer().writeInt((int)this.id); //id ăn pem
            msg.writer().writeByte(1); // read continue
            msg.writer().writeByte(0); // type skill
            msg.writer().writeInt(dameHit); // dame ăn
            msg.writer().writeBoolean(this.isDie()); // is die
            msg.writer().writeBoolean(plAttack.point.isCrit); // crit
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            // Service.gI().Reload_HP_NV(this);
        }
        catch (Exception e)
        {
//            e.printStackTrace();
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
}
