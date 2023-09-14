package real.map;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import real.boss.BossManager;
import real.func.DHVT;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemTemplate;
import real.player.Player;
import server.Service;
import server.Util;
import server.io.Message;
import service.Setting;
import real.item.ItemBua;
import real.pet.Pet;
import real.task.Task;
import real.task.TaskData;

public class Mob {

    public boolean actived;
    public MobTemplate template;
    public int dame;
    public int hp;
    public int hpMax;
    public Zone map;
    public int id;
    public long timeDie;
    public int antidelay;
    public short pointX;
    public short pointY;
    public short cx;
    public short cy;
    public int sieuquai = 0;
    public int level = 0;
    public int action = 0;

    public long lastTimePhucHoi = System.currentTimeMillis();
    public long lastTimeRoiItem = System.currentTimeMillis();
    
    public long lastTimeDrop = -1;
    
    public boolean isAnTroi;
    public long lastTimeAnTroi;
    public int timeAnTroi;
    public static Vector<Mob> temps = new Vector<Mob>();
    
    public static Mob getMob(int mobId){
        for(Mob mob : temps){
            if(mob != null && mob.template.mobTemplateId == mobId){
                return mob;
            }
        }
        return null;
    }
    
    public void setTroi(long lastTimeAnTroi, int timeAnTroi) {
        this.lastTimeAnTroi = lastTimeAnTroi;
        this.timeAnTroi = timeAnTroi;
        this.isAnTroi = true;
    }

    public void removeAnTroi() {
        isAnTroi = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(1);//b5
            msg.writer().writeByte(32);//num8
            msg.writer().writeByte(id);//b6
            Service.gI().sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean isSocola;
    private long lastTimeSocola;
    private int timeSocola;

    public void removeSocola() {
        Message msg = null;
        this.isSocola = false;
        try {
            msg = new Message(-112);
            msg.writer().writeByte(0);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayer(msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void setSocola(long lastTimeSocola, int timeSocola)
    {
        this.lastTimeSocola = lastTimeSocola;
        this.timeSocola = timeSocola;
        this.isSocola = true;
    }
    
    public boolean isBinh;
    private long lastTimeBinh;
    private int timeBinh;

    public void removeBinh() {
        this.isBinh = false;
        Service.gI().Send_Body_Mob(this, 0, -1);
    }

    public void setBinh(long lastTimeBinh, int timeBinh)
    {
        this.lastTimeBinh = lastTimeBinh;
        this.timeBinh = timeBinh;
        this.isBinh = true;
        Service.gI().Send_Body_Mob(this, 1, 11175);
    }

    public byte status;

    private List<Player> playerAttack = new LinkedList<>();
    private long lastTimeAttackPlayer;

    public int getHpFull() {
        int hpMax = template.hp;
        if(map.isPhuBan()){
            hpMax = this.hpMax;
        }
        return hpMax;
    }

    public void setHpFull(int hp) {
        hpMax = hp;
    }
    
    public void setDame(int d) {
        dame = d;
    }

    public boolean isDie() {
        return this.gethp() <= 0;
    }

    public void attackMob(Player plAttack, boolean miss, int...dame) {
        if (isDie())
        {
            return;
        }

        Message msg = null;
        try {
            long dameAttack = (long)plAttack.point.getDameAttack() / (dame.length > 0 ? dame[0] : 1);
            if (plAttack.inventory.OptionCt(19))
            {
                dameAttack += dameAttack * plAttack.point.get_percent_option(19) / 100;
            }
            if (plAttack.isPl() && ItemBua.ItemBuaExits(plAttack, 214)) {
                dameAttack += dameAttack / 2;
            }
            if(plAttack.isPet && ItemBua.ItemBuaExits(((Pet)plAttack).master, 522)) {
                dameAttack *= 2;
            }
            if(this.template.mobTemplateId == 70){
                int pointHP = this.hp / 100;
                if(dameAttack < 10000)
                {
                    dameAttack = 0;
                }
                if(this.hp < 100)
                {
                    pointHP = 1;
                }
                if(dameAttack > pointHP)
                {
                    dameAttack = pointHP;
                }
            }
            msg = new Message(54);
            msg.writer().writeInt((int) plAttack.id);
            msg.writer().writeByte(plAttack.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayerInMap(plAttack.zone, msg);
            this.addPlayerAttack(plAttack);
            this.injured(plAttack, miss ? 0 : (int)dameAttack);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    int get_dame(Player pl) {
        int d = this.dame;
        int dame = d - pl.point.getDefFull();
        if(pl.isPet && ItemBua.ItemBuaExits(((Pet)pl).master, 522)){
            dame /= 2;
        }
        if (pl.isPl() && ItemBua.ItemBuaExits(pl, 215)) {
            dame /= 2;
        }
        if (pl.itemTime.ExitsItemTiem(2757)) {
            dame /= 2;
        }
        if (pl.itemTime.ExitsItemTiem(10712)) {
            dame /= 2;
        }        
        if (dame <= 0) {
            dame = 1;
        }
        if (pl.isPl() && (ItemBua.ItemBuaExits(pl, 217)) && pl.point.hp - dame <= 0) {
            dame = pl.point.hp - 1;
        }
        return (int)dame;
    }

    public void injured(Player pl, int hp, boolean... no1hp) {
        if (isDie()) {
            return;
        }
        int hut_hp = pl.point.get_percent_hut_hp(true);
        hut_hp = hut_hp > 50 ? 50 : hut_hp; 
        int hut_ki = pl.point.get_percent_option(96);
        if (hp > 0) {
            if (template.mobTemplateId == 0) {
                hp = 10;
            }
            if(this.template.mobTemplateId == 70){
                int pointHP = this.hp / 100;
                if(hp < 10000){
                    hp = 0;
                }
                if(this.hp < 100){
                    pointHP = 1;
                }
                if(hp > pointHP){
                    hp = pointHP;
                }
            }
            if (no1hp.length != 0)
            {
                if(this.hp < hp){
                    hp = this.hp;
                }
                this.sethp(this.hp -= hp);
            }
            else
            {
                if (this.hp == 1) {
                    hp = 1;
                }
                if (this.hp <= hp && this.hp == getHpFull())
                {
                    hp = this.hp - 1;
                    this.sethp(this.hp - hp);
                }
                else if (hp > this.hp && this.hp < getHpFull())
                {
                    hp = this.hp;
                    this.sethp(this.hp - hp);
                }
                else
                {
                    this.sethp(this.hp - hp);
                }
            }
            if (hut_hp > 0)
            {
                pl.hoi_hp(hp * hut_hp / 100);
            }
            if (hut_ki > 0)
            {
                pl.hoi_ki(hp * hut_ki / 100);
            }
        }

        Message msg = null;
        try
        {
            if (this.gethp() > 0) {
                msg = new Message(-9);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(this.gethp());
                msg.writer().writeInt(hp);
                msg.writer().writeBoolean(hp > 0 ? pl.point.isCrit : false);
                msg.writer().writeByte(hut_hp > 0 ? 37 : hut_ki > 0 ? 37 : -1);
            }
            else
            {
                msg = new Message(-12);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(hp);
                msg.writer().writeBoolean(pl.point.isCrit); // crit
                this.roiItem(pl, msg);
                this.setDie();
                if (pl.isPl())
                {
                    Task task = TaskData.getTask(pl.taskId);
                    if (task != null && task.counts[pl.taskIndex] != -1 && this.template.mobTemplateId == task.killId[pl.taskIndex] + (pl.taskId == 7 ? pl.gender : 0)) {
                        pl.taskCount += 1;
                        if((pl.taskId == 13 || pl.taskId == 15) && pl.clan != null && pl.zone.players.stream().filter(p -> p != null && p.isPl() && p.clan != null && p.clan.id == pl.clan.id).toList().size() >= 2)
                        {
                            pl.taskCount += 1;
                        }
                        if (pl.taskCount < task.counts[pl.taskIndex])
                        {
                            Service.gI().send_task(pl, task);
                        }
                        else
                        {
                            Service.gI().send_task_next(pl);
                        }
                    }
                    if(pl.taskOrder !=null && pl.taskOrder.taskId == 0 && this.template.mobTemplateId == pl.taskOrder.killId)
                    {
                        if(pl.taskOrder.count < pl.taskOrder.maxCount)
                        {
                            pl.taskOrder.count++;
                            Service.gI().sendThongBao(pl, "Tiến trình: " + pl.taskOrder.count +" / " + pl.taskOrder.maxCount);
                        }
                        else
                        {
                            pl.taskOrder.count = pl.taskOrder.maxCount;
                            Service.gI().sendThongBao(pl, "Đã hoàn thành nhiệm vụ!Đến với Bò mộng báo cáo ngay nào");
                        }
                        Service.gI().send_task_orders(pl, pl.taskOrder);
                    }
                }
            }
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
            if(no1hp.length > 0 && no1hp[0]){
                hp = hp / no1hp.length / pl.zone.mobs.length;
            }
            calculatePowerPlus(pl, hp);
        } catch (Exception e) {
            if(pl.taskIndex > TaskData.getTask(pl.taskId).counts.length - 1){
                pl.taskIndex = (byte) (TaskData.getTask(pl.taskId).counts.length - 1);
            }
            else if (pl.taskIndex < 0){
                pl.taskIndex = 0;
            }
            Service.gI().send_task(pl, TaskData.getTask(pl.taskId));
            if(pl.role >= 99){
                Util.debug("Mob.injured - Name: " + pl.name);
                e.printStackTrace();
            }
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void calculatePowerPlus(Player pl , int hp){
        if(pl.zone.isMapKGHD()){
            return;
        }
        if(pl.zone.isBongTai()){
            return;
        }
        if(template.level == 999){
            return;
        }
        int level = Service.gI().get_clevel(pl.point.getPower());
        level = level > 13 ? 13 : level == 0 ? 1 : level;
        if(pl.isPet){
            level = template.level;
        }
        int moblevel = level > template.level ? template.level : level;
        long tnPl = (hp * 10 / 100) * moblevel;
        tnPl = tnPl == 0 ? 1 : tnPl;
        pl.expDonate = pl.zone.isMapBDKB() ? 100 : 0;
        if (ItemBua.ItemBuaExits(pl, 213)) {
            pl.expDonate += 100;
        }
        if (ItemBua.ItemBuaExits(pl, 671)) {
            pl.expDonate += 200;
        }
        if (ItemBua.ItemBuaExits(pl, 672)) {
            pl.expDonate += 300;
        }
        pl.expDonate += pl.point.get_percent_option(101);
        pl.expDonate += pl.point.get_percent_option(88);
        if(pl.isPet){
            pl.expDonate += ((Pet)pl).master.point.get_percent_option(160);
        }
        if(pl.isPl()){
            switch (pl.cFlag)
            {
                case 0:
                    break;
                case 8:
                    pl.expDonate += 10;
                    break;
                default:
                    pl.expDonate += 5;
                    break;
            }
        }
        if(pl.isPl()){
            if ((pl.speacial[0] == 7 && pl.gender == 0) || (pl.speacial[0] == 8 && pl.gender == 1) || (pl.speacial[0] == 7 && pl.gender == 2)) {
                pl.expDonate += pl.speacial[1];
            }
        }
        if(pl.zone.items.stream().anyMatch(it -> it != null && (it.playerId == pl.id || isMemInMap(pl))&& it.itemTemplate.id == 343 && Util.getDistance(it.x, it.y, pl.x, pl.y) <= 200)){
            pl.expDonate += 20;
        }
        tnPl += tnPl / 100 * pl.expDonate;
        if(tnPl <= 0){
            tnPl = 1;
        }
        if(tnPl > 0){
            tnPl *= Setting.DONATE_EXP;
            Service.gI().congTiemNang(pl, (byte)2, tnPl);
            Player master = pl.isPet ? ((Pet)pl).master : pl;
            for(Player pt : pl.zone.players){
                if(pt != null && pt.isPl() && Util.getDistance(pt, pl) <= 500 && pt.clan != null && master.clan != null && pt.clan.id == master.clan.id && !pt.equals(master) && pt.zone == master.zone){
                    int level2 = Service.gI().get_clevel(pt.point.getPower());
                    Service.gI().congTiemNang(pt, (byte)1, tnPl, level, level2);
                }
            }
        }
    }
    
    public boolean isMemInMap(Player me){
        if(me.clan != null && me.zone.players.stream().allMatch(pl -> pl != null && !pl.isBoss && !pl.isNewPet && pl.zone.zoneId == me.zone.zoneId  && pl.clan != null && pl.clan.id == me.clan.id)){
            return true;
        }
        return false;
    }
    
    public class ItemDrop {

        public ItemDrop(int i, double p, int q, List<ItemOption> op) {
            ID = i;
            Percentage = p;
            quantity = q;
            if (op != null) {
                options = new ArrayList<>();
                options.addAll(op);
            }
        }

        public ItemDrop(int i, double p) {
            ID = i;
            Percentage = p;
            quantity = 1;
            options = ItemData.gI().get_option_default(ID);
        }
        public int ID;
        public double Percentage;
        public int quantity;
        public List<ItemOption> options;
    }
    static int[][][] list_do = null;

    public static int[] get_do(int gender, int level) {
        if (list_do == null) {
            list_do = new int[3][13][];
            int l = 0;
            while (l <= 12) {

                List<Integer> a = new ArrayList<>();
                List<Integer> b = new ArrayList<>();
                List<Integer> c = new ArrayList<>();

                for (ItemTemplate it : ItemData.gI().itemTemplates) {
                    if (it.type >= 0 && it.type <= 4 && it.id != 691 && it.id != 692 && it.id != 693 && it.level == l + 1) {
                        if (it.gender == 0) {
                            a.add((int) it.id);
                        } else if (it.gender == 1) {
                            b.add((int) it.id);
                        } else if (it.gender == 2) {
                            c.add((int) it.id);
                        } else {
                            a.add((int) it.id);
                            b.add((int) it.id);
                            c.add((int) it.id);
                        }
                    }
                }
                //Util.log("-----" + l);
                list_do[0][l] = new int[a.size()];
                //Util.log("-----" + a.size() + "------");
                for (int z = 0; z < a.size(); z++) {
                    list_do[0][l][z] = a.get(z);
                }

                list_do[1][l] = new int[b.size()];
                for (int z = 0; z < b.size(); z++) {
                    list_do[1][l][z] = b.get(z);
                }

                list_do[2][l] = new int[c.size()];
                for (int z = 0; z < c.size(); z++) {
                    list_do[2][l][z] = c.get(z);
                }
                l++;
            }
        }
        return list_do[gender][level - 1];
    }

    List<ItemDrop> get_item_drop(Player pl, int g)
    {
        List<ItemDrop> its = new ArrayList<>();
        if(template.mobTemplateId == 70){
            if(Util.isTrue(20))
            {
                its.add(new ItemDrop(568, 100, 1, null));
            }
            return its;
        }
        if (template.level == 0)
        {
            if(Setting.isNEW_2023())
            {
                List<ItemOption> new2023 = new ArrayList();
                new2023.add(new ItemOption(174, 2023));
                new2023.add(new ItemOption(93, 30));
                its.add(new ItemDrop(751, 0.32, 1, new2023));
                List<ItemDrop> itm = new ArrayList<>();
                double rd = 999;
                for (ItemDrop i : its) {
                    rd = Util.nextdouble(100);
                    if (rd <= i.Percentage) {
                        if (!itm.contains(i)) {
                            itm.add(i);
                        }
                    }
                }
                its.clear();
                its = null;
                return itm;
            }
            return null;
        }
        if(template.level <= 13){
            for (int i : get_do(g, template.level)) {              
                if (template.level == 13) {
                    if(lastTimeDrop != -1 && System.currentTimeMillis() - lastTimeDrop < 10000){
                        List<ItemOption> t = ItemData.gI().get_op_do_than(ItemData.gI().getTemplate((short) i).type);
                        its.add(new ItemDrop(i, 0.09, 1, t));
//                        if(pl.isNewMember && pl.isPl()){
//                            t.addAll(ItemData.gI().get_skh(g, Util.nextInt(0, 2)));
//                            its.add(new ItemDrop(i, (0.008 / template.level), 1, t));
//                        }
                    }
                } else {
                    List<ItemOption> t = ItemData.gI().get_option_default(i);
                    its.add(new ItemDrop(i, (5 / template.level),1,t));
//                    if(pl.isNewMember && pl.isPl()){
//                        t.addAll(ItemData.gI().get_skh(g, Util.nextInt(0, 2)));
//                        its.add(new ItemDrop(i, (0.008 / template.level), 1, t));
//                    }
                }
            }
        }

//        if(template.level <= 13){
//            for (int i : get_do(g, template.level)) {
//                if (template.level == 13) {
//                    
//                        List<ItemOption> t = ItemData.gI().get_op_do_than(ItemData.gI().getTemplate((short) i).type);
//                       // its.add(new ItemDrop(i, (0.004 / template.level), 1, t));
//                       its.add(new ItemDrop(i, 90, 1, t));
////                        if(pl.isNewMember && pl.isPl()){
////                            t.addAll(ItemData.gI().get_skh(g, Util.nextInt(0, 2)));
////                            its.add(new ItemDrop(i, (0.008 / template.level), 1, t));
////                        }
//            }
//        }
//        }
        if(Setting.isNEW_2023() && !pl.zone.isPhuBan())
        {
            List<ItemOption> new2023 = new ArrayList();
            new2023.add(new ItemOption(174, 2023));
            new2023.add(new ItemOption(93, 30));
            its.add(new ItemDrop(748, 0.22, 1, new2023));
        }
        else if(Setting.isNEW_2023() && (pl.zone.isMapDTDN() || pl.zone.isMapBDKB() || pl.zone.isMapKGHD()))
        {
            List<ItemOption> new2023 = new ArrayList();
            new2023.add(new ItemOption(174, 2023));
            new2023.add(new ItemOption(93, 30));
            its.add(new ItemDrop(750, 4.0, 1, new2023));
        }
        int vang = Util.nextInt(template.hp / 20, template.hp / 10);
        int PT_GOLD = pl.point.get_percent_option(100);
        vang += vang / 100 * (PT_GOLD == 0 ? 1 : PT_GOLD);
        if (vang > 32767)
        {
            vang = 32767;
        }
        int id_vang;
        if (vang < 200)
        {
            id_vang = 76;
        }
        else if (vang < 2000)
        {
            id_vang = 188;
        }
        else if (vang < 20000)
        {
            id_vang = 189;
        }
        else
        {
            id_vang = 190;
        }
        if(pl.zone.isMapDTDN())
        {
            if(Setting.EVENT_GIANG_SINH)
            {
                its.add(new ItemDrop(929, 2));
                its.add(new ItemDrop(928, 1));
            }
        }
        its.add(new ItemDrop(17, 0.3));
        its.add(new ItemDrop(18, 2));
        its.add(new ItemDrop(19, 3));
        its.add(new ItemDrop(20, 4));
        its.add(new ItemDrop(441, 1));
        its.add(new ItemDrop(442, 1));
        its.add(new ItemDrop(443, 1));
        its.add(new ItemDrop(444, 1));
        its.add(new ItemDrop(445, 1));
        its.add(new ItemDrop(446, 1));
        its.add(new ItemDrop(447, 1));

        
        
        
        Task task = TaskData.getTask(pl.taskId);
        if (task != null && task.taskId == 2 && pl.taskIndex == 0 && pl.taskCount < task.counts[pl.taskIndex] && (pl.zone.map.id == 1 || pl.zone.map.id == 15 || pl.zone.map.id == 8)) {
            its.add(new ItemDrop(73, 100));
        }
        if (task != null && task.taskId == 31 && pl.taskIndex == 7 && pl.taskCount < task.counts[pl.taskIndex] && pl.zone.isHTTV()) {
            its.add(new ItemDrop(993, 5));
        }
        if (task != null && task.taskId == 8 && pl.taskIndex == 1 && pl.taskCount < task.counts[pl.taskIndex] && this.template.mobTemplateId == 10 + (pl.gender == 0 ? 1 : pl.gender == 1 ? 2 : 0)) {
            its.add(new ItemDrop(20, 20));
        }
        if (task != null && task.taskId == 14 && pl.taskIndex == 1 && pl.taskCount < task.counts[pl.taskIndex] && this.template.mobTemplateId == 13 + pl.gender) {
            its.add(new ItemDrop(85, 10));
        }
        if (pl.itemTime.ExitsItemTiem(2758) && pl.zone.isMapTL())
        {
            its.add(new ItemDrop(380, 10));
        }
        if ((pl.zone.isCooler() || pl.zone.isHTTV() || pl.zone.isMapTL()) && pl.inventory.itemsBody.stream().limit(5).allMatch(dtl -> dtl != null && dtl.template.level == 13)) {
            its.add(new ItemDrop(663, 2));
            its.add(new ItemDrop(664, 2));
            its.add(new ItemDrop(665, 2));
            its.add(new ItemDrop(666, 2));
            its.add(new ItemDrop(667, 2));
        }
        if(Setting.EVENT_HALLOWEEN){
            its.add(new ItemDrop(585, 0.07));
        }
        if(Setting.EVENT_GIANG_SINH){
            List<ItemOption> opNoel = new ArrayList();
            opNoel.add(new ItemOption(30, 0));
            
            List<ItemOption> opGiangSinh = new ArrayList();
            opGiangSinh.add(new ItemOption(174, 2022));
            
            its.add(new ItemDrop(649, 0.12, 1, opNoel));
            
            its.add(new ItemDrop(533, 0.15, 1, opGiangSinh));
            its.add(new ItemDrop(931, 0.22, 1, opGiangSinh)); // nrb 7s
            its.add(new ItemDrop(930, 0.19, 1, opGiangSinh)); // nrb 6s
        }
        if(pl.zone.map.id == 80 || pl.zone.map.id == 79 || pl.zone.map.id == 82){
            its.add(new ItemDrop(821, 0.04));
        }
        its.add(new ItemDrop(id_vang, 5.0, vang, null));
        
//        for(int i = 0; i < 20; i++){
//            its.add(new ItemDrop(190, 100, 32767, null)); // test vị trí vàng rơi
//        }
        
//        its.add(new ItemDrop(225, 2)); // mdv
////        its.add(new ItemDrop(20, 5));
////        its.add(new ItemDrop(19, 4));
//        its.add(new ItemDrop(18, 3));
//        its.add(new ItemDrop(17, 2));
//        its.add(new ItemDrop(16, 1));
//        its.add(new ItemDrop(15, 0.1));
//        its.add(new ItemDrop(14, 0.01));
        its.add(new ItemDrop(220, 0.2)); // da lb
        its.add(new ItemDrop(221, 0.2)); // da sp
        its.add(new ItemDrop(222, 0.2)); // da rb
        its.add(new ItemDrop(223, 0.2)); // da tt
        its.add(new ItemDrop(224, 0.2)); // da tat
        its.add(new ItemDrop(20, 0.04));
        its.add(new ItemDrop(19, 0.04));
        its.add(new ItemDrop(18, 0.04));
        if(pl.inventory.OptionCt(110) || pl.inventory.checkOption(pl.inventory.itemsBody,110)){
            its.add(new ItemDrop(441, 0.2));
            its.add(new ItemDrop(442, 0.2));
            its.add(new ItemDrop(443, 0.2));
            its.add(new ItemDrop(444, 0.2));
            its.add(new ItemDrop(445, 0.2));
            its.add(new ItemDrop(446, 0.2));
            its.add(new ItemDrop(447, 0.2));
        }
        if(pl.zone.isBongTai()){
            its.add(new ItemDrop(934, 1));
        }
        its.add(new ItemDrop(74, 0.1)); // dui ga
//        its.add(new ItemDrop(861, 1)); // hong ngoc
//        its.add(new ItemDrop(225, 0.1)); // ngoc
//        its.add(new ItemDrop(211, 1)); // nho tim
//        its.add(new ItemDrop(212, 2)); // nho xanh
//        its.add(new ItemDrop(987, 100)); // da bao ve
        List<ItemDrop> itm = new ArrayList<>();
        double rd = 999;
        for (ItemDrop i : its) {
            rd = Util.nextdouble(100);
            // Util.log("ran: " + rd);
            if (rd <= i.Percentage) {
                if (!itm.contains(i)) {
                    itm.add(i);
                }
            }
        }
        its.clear();
        its = null;
        return itm;
    }

    private void roiItem(Player pl, Message msg) {
        try {
            boolean isShow = false;
            List<ItemDrop> items = get_item_drop(pl, (pl.isPet ? ((Pet) pl).master.gender : pl.gender));
            if (items != null)
            {
                int trai = 0;
                int phai = 1;
                int next = 0;

                msg.writer().writeByte(items.size()); //sl item roi
                for (int i = 0; i < items.size(); i++)
                {
                    int id = items.get(i).ID;
                    ItemTemplate temp = ItemData.gI().getTemplate((short) id);

                    int X = next == 0 ? -15 * trai : 15 * phai;
                    if(next == 0)
                    {
                        trai++;
                    }
                    else
                    {
                        phai++;
                    }
                    next = next == 0 ? 1 : 0;
                    if(trai > 10)
                    {
                        trai = 0;
                    }
                    if(phai > 10)
                    {
                        phai = 1;
                    }
                    if(pl.taskId == 8 && pl.taskIndex == 1 && temp.id == 20)
                    {
                        isShow = true;
                    }
                    else if(pl.taskId == 14 && pl.taskIndex == 1 && temp.id == 85)
                    {
                        isShow = true;
                    }
                    int x = this.cx + X;
                    int y = this.map.LastY(x, this.cy);
                    ItemMap itemMap = new ItemMap(pl.zone, temp, items.get(i).quantity, x, y, (int) pl.id);
                    if (items.get(i).options != null) {
                        itemMap.options.addAll(items.get(i).options);
                    }
                    itemMap.playerId = Math.abs(pl.id);
                    pl.zone.addItem(itemMap);
                    msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                    msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                    msg.writer().writeShort(itemMap.x); // xend item
                    msg.writer().writeShort(itemMap.y); // yend item
                    msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
                    if(temp.level >= 13 && temp.id < 663 && temp.id > 667){
                        Service.gI().sendThongBaoBenDuoi(pl.name + " đã nhặt được " + temp.name + " tại " + pl.zone.map.name + " khu vực " + pl.zone.zoneId);
                    }
                }
            }
            if(this.template.mobTemplateId == 10 + (pl.gender == 0 ? 1 : pl.gender == 1 ? 2 : 0) && pl.taskId == 8 && pl.taskIndex == 1){
                TaskData.autoChat(pl, this, isShow);
            }
            else if(this.template.mobTemplateId == (13 + pl.gender) && pl.taskId == 14 && pl.taskIndex == 1){
                TaskData.autoChat(pl, this, isShow);
            }
        } catch (Exception e) {
            Util.debug("Mob.roiItem\n------------");
            e.printStackTrace();
        }
    }
    
    public synchronized void update() {
        if(timeDie > System.currentTimeMillis()){
            timeDie = System.currentTimeMillis();
        }
        if (isDie() && !this.map.isPhuBan() && (System.currentTimeMillis() - timeDie) > 5000)
        {
            Message msg = null;
            try {
                level = 0;
                cx = pointX;
                cy = pointY;
                msg = new Message(-13);
                msg.writer().writeByte(this.id);
                msg.writer().writeByte(template.mobTemplateId);
                msg.writer().writeByte(0);
                msg.writer().writeInt(getHpFull());
                this.hp = getHpFull();
                Service.gI().sendMessAllPlayerInMap(map, msg);
            } catch (Exception e) {
//                e.printStackTrace();
            } finally {
                if (msg != null) {
                    msg.cleanup();
                    msg = null;
                }
            }
        }
        if(isDie() && (template.mobTemplateId == 71 || template.mobTemplateId == 72))
        {
            Service.gI().sendBigBoss(map, this.template.mobTemplateId == 71 ? 7 : 6, 0, -1, -1);
        }
        if(isDie() && template.mobTemplateId == 70 && (System.currentTimeMillis() - timeDie) > 5000 && level <= 2)
        {
            if(level == 0)
            {
                level = 1;
                action = 6;
                this.hp = getHpFull();
            }
            else if(level == 1)
            {
                level = 2;
                action = 5;
                this.hp = getHpFull();
            }
            else if(level == 2)
            {
                level = 3;
                action = 9;
            }
            int trai = 0;
            int phai = 1;
            int next = 0;
            Item GOLD = ItemData.gI().get_item(190);
            for(int i = 0; i < 30; i++){
                int X = next == 0 ? -5 * trai : 5 * phai;
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
                Service.gI().roiItem(GOLD, map, cx + X, cy, -1);
            }
            Service.gI().sendBigBoss2(map, action, this);
            if(level <= 2){
                Message msg = null;
                try {
                    msg = new Message(-9);
                    msg.writer().writeByte(this.id);
                    msg.writer().writeInt(this.gethp());
                    msg.writer().writeInt(1);
                    Service.gI().sendMessAllPlayerInMap(map, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (msg != null) {
                        msg.cleanup();
                        msg = null;
                    }
                }
            }
            else 
            {
                cx = -1000;
                cy = -1000;
            }
        }
        if(Util.canDoWithTime(lastTimePhucHoi, 30000) && !isDie()){
            lastTimePhucHoi = System.currentTimeMillis();
            int hpMax = this.getHpFull();
            if(this.hp < hpMax)
            {
                hoi_hp(hpMax / 10);
            }
        }
        if(System.currentTimeMillis() - lastTimeRoiItem >= 3300000)
        {
            if(lastTimeDrop == -1)
            {
                lastTimeDrop = System.currentTimeMillis();
            }
        }
        if(lastTimeDrop != -1 && System.currentTimeMillis() - lastTimeDrop >= 300000){
            lastTimeRoiItem = System.currentTimeMillis();
            lastTimeDrop = -1;
        }
        if (isStun && (Util.canDoWithTime(lastTimeStun, timeStun) || isDie())) {
            removeStun();
        }
        if (isThoiMien && (Util.canDoWithTime(lastTimeThoiMien, timeThoiMien) || isDie())) {
            removeThoiMien();
        }
        if (isBlindDCTT && (Util.canDoWithTime(lastTimeBlindDCTT, timeBlindDCTT)) || isDie()) {
            removeBlindDCTT();
        }
        if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola) || isDie())) {
            removeSocola();
        }
        if (isBinh && (Util.canDoWithTime(lastTimeBinh, timeBinh) || isDie())) {
            removeBinh();
        }
        if (isAnTroi && (Util.canDoWithTime(lastTimeAnTroi, timeAnTroi) || isDie())) {
            removeAnTroi();
        }
        if(template.mobTemplateId >= 70 && template.mobTemplateId <= 72)
        {
            BigbossAttack();
        }
        else
        {
            attackPlayer();
        }
    }

    private void attackPlayer() {
        if (!isDie() && !isHaveEffectSkill() && Util.canDoWithTime(lastTimeAttackPlayer, Setting.DELAY_MOB_ATTACK))
        { // 
            Message msg = null;
            Player plAttack = getPlayerCanAttack();
            try
            {
                if (template.type == 0) {
                    return;
                }
                if (plAttack != null)
                {
                    if(!Util.canDoWithTime(plAttack.lastTimeRevived, 3000))
                    {
                        return;
                    }
                    int dame = plAttack.injured(null, this, get_dame(plAttack));
                    if(template.mobTemplateId >= 71 && template.mobTemplateId <= 72)
                    {
                        int acticve = 0;
                        cx = (short)plAttack.x;
                        if(template.mobTemplateId == 71)
                        {
                            int dis = Util.getDistance(plAttack, this);
                            if (dis <= 100) {
                                acticve = 3;
                            }
                            else if(dis > 100 && dis <= 200){
                                acticve = 4;
                            }
                            else if(dis > 200){
                                acticve = 3;
                            }
                        }
                        if(template.mobTemplateId == 72)
                        {
                            int dis = Util.getDistance(plAttack, this);
                            if (dis <= 100) {
                                acticve = 0;
                            }
                            else if(dis > 100 && dis <= 200){
                                acticve = 1;
                            }
                            else if(dis > 200){
                                acticve = 2;
                            }
                        }
                        Service.gI().sendBigBoss(map, acticve, 1, plAttack.id, dame);
                    }
                    else
                    {
                        if (plAttack.isPl()) {
                            msg = new Message(-11);
                            msg.writer().writeByte(this.id);
                            msg.writer().writeInt(dame); //dame
                            plAttack.sendMessage(msg);
                        }
                        msg = new Message(-10);
                        msg.writer().writeByte(this.id);
                        msg.writer().writeInt((int)plAttack.id);
                        msg.writer().writeInt(plAttack.point.getHP());
                        Service.gI().sendMessAnotherNotMeInMap(plAttack, msg);
                    }
                    lastTimeAttackPlayer = System.currentTimeMillis();
                }
            }
            catch (Exception e)
            {
//                e.printStackTrace();
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
    
    private void BigbossAttack()
    {
        if (!isDie() && !isHaveEffectSkill() && Util.canDoWithTime(lastTimeAttackPlayer, 1000))
        {
            Message msg = null;
            try
            {
                switch (this.template.mobTemplateId)
                {
                    case 70: // Hirudegarn
                        if(!Util.canDoWithTime(lastTimeAttackPlayer, 3000))
                        {
                            return;
                        }
                        // 0: bắn - 1: Quật đuôi - 2: dậm chân - 3: Bay - 4: tấn công - 5: Biến hình - 6: Biến hình lên cấp
                        // 7: vận chiêu - 8: Di chuyển - 9: Die
                        int[] idAction = new int[]{1, 2, 3, 7};
                        if(this.level >= 2)
                        {
                            idAction = new int[]{1, 2};
                        }
                        action = action == 7 ? 0 : idAction[Util.nextInt(0, idAction.length - 1)];
                        int index = Util.nextInt(0, map.getPlayers().size() - 1);
                        Player player = map.getPlayers().get(index);
                        if(player == null || player.isDie())
                        {
                            return;
                        }
                        if(action == 1)
                        {
                           cx = (short)player.x;
                           Service.gI().sendBigBoss2(map, 8, this);
                        }
                        msg = new Message(101);
                        msg.writer().writeByte(action);
                        if(action >= 0 && action <= 4)
                        {
                            if(action == 1)
                            {
                                msg.writer().writeByte(1);
                                int dame = player.injured(null, this, get_dame(player));
                                msg.writer().writeInt(player.id);
                                msg.writer().writeInt(dame);
                            }
                            else if(action == 3)
                            {
                                cx = (short)player.x;
                                msg.writer().writeShort(cx);
                                msg.writer().writeShort(cy);
                            }
                            else
                            {
                                msg.writer().writeByte(map.players.size());
                                for(int i = 0; i < map.players.size(); i++)
                                {
                                    Player pl = map.players.get(i);
                                    int dame = pl.injured(null, this, get_dame(pl));
                                    msg.writer().writeInt(pl.id);
                                    msg.writer().writeInt(dame);
                                }
                            }
                        }
                        else
                        {
                            if(action == 6 || action == 8)
                            {
                                cx = (short)player.x;
                                msg.writer().writeShort(cx);
                                msg.writer().writeShort(cy);
                            }
                        }
                        Service.gI().sendMessAllPlayerInMap(map, msg);
                        lastTimeAttackPlayer = System.currentTimeMillis();
                        break;
                    case 71: // Vua Bạch Tuộc
                        int[] idAction2 = new int[]{3, 4, 5};
                        action = action == 7 ? 0 : idAction2[Util.nextInt(0, idAction2.length - 1)];
                        int index2 = Util.nextInt(0, map.getPlayers().size() - 1);
                        Player player2 = map.getPlayers().get(index2);
                        if(player2 == null || player2.isDie())
                        {
                            return;
                        }
                        msg = new Message(102);
                        msg.writer().writeByte(action);
                        if(action >= 0 && action <= 5)
                        {
                            if(action != 5)
                            {
                                msg.writer().writeByte(1);
                                int dame = player2.injured(null, this, get_dame(player2));
                                msg.writer().writeInt(player2.id);
                                msg.writer().writeInt(dame);
                            }
                            if(action == 5)
                            {
                                cx = (short)player2.x;
                                msg.writer().writeShort(cx);
                            }
                        }
                        else
                        {
                            
                        }
                        Service.gI().sendMessAllPlayerInMap(map, msg);
                        lastTimeAttackPlayer = System.currentTimeMillis();
                        break;
                    case 72: // Rôbốt bảo vệ
                        int[] idAction3 = new int[]{0, 1, 2};
                        action = action == 7 ? 0 : idAction3[Util.nextInt(0, idAction3.length - 1)];
                        int index3 = Util.nextInt(0, map.getPlayers().size() - 1);
                        Player player3 = map.getPlayers().get(index3);
                        if(player3 == null || player3.isDie())
                        {
                            return;
                        }
                        msg = new Message(102);
                        msg.writer().writeByte(action);
                        if(action >= 0 && action <= 2)
                        {
                            msg.writer().writeByte(1);
                            int dame = player3.injured(null, this, get_dame(player3));
                            msg.writer().writeInt(player3.id);
                            msg.writer().writeInt(dame);
                        }
                        Service.gI().sendMessAllPlayerInMap(map, msg);
                        lastTimeAttackPlayer = System.currentTimeMillis();
                        break;
                }
            }
            catch (Exception e)
            {
//                Util.debug("ERROR BIG BOSS");
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

    public Player getPlayerCanAttack() {
        int distance = 500;
        Player playerTaget = null;
        for (int i = playerAttack.size() - 1; i >= 0; i--) {
            try {
                boolean isRemove = true;
                for(Player player : this.map.getPlayers()){
                    Player pl = playerAttack.get(i);
                    if(player != pl){
                        continue;
                    }
                    if (player.zone == this.map && pl.point.getHP() > 0) {
                        if(Util.getDistance(pl, this) <= distance){
                            return pl;
                        }
                        isRemove = false;
                    }
                }
                if(isRemove){
                    playerAttack.remove(i);
                }
            } catch (Exception e) {
                playerAttack.remove(i);
            }
        }
        distance = template.mobTemplateId >= 70 && template.mobTemplateId <= 72 ? 500 : 100;
        Player plAttack = null;
        try {
            for (Player pl : map.getPlayers()) {
                if (pl.isDie() || pl.isNewPet || pl.isBoss || pl.inventory.OptionCt(105) || pl.zone.items.stream().anyMatch(it -> it != null && (it.playerId == pl.id || isMemInMap(pl)) && it.itemTemplate.id == 344 && Util.getDistance(it.x, it.y, pl.x, pl.y) <= 200)){
                    continue;
                }
                int dis = Util.getDistance(pl, this);
                if (dis <= distance && (this.template.type == 4 && this.template.level > 3 || this.template.level > 6)) {
                    plAttack = pl;
                    distance = dis;
                }
            }
        } catch (Exception e) {
        }
        return plAttack;
    }

    public void addPlayerAttack(Player pl) {
        for (Player player : playerAttack) {
            if (player == pl && !player.isNewPet) {
                return;
            }
        }
        if (playerAttack.size() > 10) {
            playerAttack.remove(0);
        }
        playerAttack.add(pl);
    }

    public int gethp() {
        return hp;
    }

    public void sethp(int hp) {
        if (this.hp < 0)
        {
            this.hp = 0;
        }
        else
        {
            this.hp = hp;
        }
        if(this.hp > this.getHpFull())
        {
            this.hp = this.getHpFull();
        }
    }

    public void setDie() {
        this.lastTimePhucHoi = System.currentTimeMillis();
        this.timeDie = System.currentTimeMillis();
        playerAttack.clear();
    }
    
    public long lastTimeStun;
    public int timeStun;
    public boolean isStun;

    public void startStun(long lastTimeStartBlind, int timeBlind) {
        this.lastTimeStun = lastTimeStartBlind;
        this.timeStun = timeBlind;
        isStun = true;
    }

    private void removeStun() {
        isStun = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            msg.writer().writeByte(40);
            msg.writer().writeByte(this.id);
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

    public boolean isThoiMien;
    public long lastTimeThoiMien;
    public int timeThoiMien;

    public void setThoiMien(long lastTimeThoiMien, int timeThoiMien) {
        this.isThoiMien = true;
        this.lastTimeThoiMien = lastTimeThoiMien;
        this.timeThoiMien = timeThoiMien;
        //System.out.println("set thoi mien");
    }

    public void removeThoiMien() {
        this.isThoiMien = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(1); //b6
            msg.writer().writeByte(41); //num6
            msg.writer().writeByte(this.id); //b7
            Service.gI().sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean isBlindDCTT;
    public long lastTimeBlindDCTT;
    public int timeBlindDCTT;

    public void setStartBlindDCTT(long lastTimeBlindDCTT, int timeBlindDCTT) {
        this.isBlindDCTT = true;
        this.lastTimeBlindDCTT = lastTimeBlindDCTT;
        this.timeBlindDCTT = timeBlindDCTT;
    }

    public void removeBlindDCTT() {
        this.isBlindDCTT = false;
        Message msg = null;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            msg.writer().writeByte(40);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void hoi_hp(int hp){
        Message msg = null;
        try {
            this.sethp(this.gethp() + hp);
            int HP = hp > 0 ? 1 : Math.abs(hp);
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.gethp());
            msg.writer().writeInt(HP);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(-1);
            Service.gI().sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private boolean isHaveEffectSkill() {
        return isAnTroi || isBlindDCTT || isStun || isThoiMien || isBinh;
    }
}
