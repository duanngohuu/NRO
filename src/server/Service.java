package server;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import real.boss.BossManager;
import real.func.ChangeMap;
import real.func.DHVT;
import real.func.GameData;
import real.func.NRNM;
import real.func.NRSD;
import real.func.TopInfo;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemShop;
import real.item.ItemShopDAO;
import real.magictree.MabuEgg;
import real.map.ItemMap;
import real.map.Zone;
import real.map.MapManager;
import real.map.MapService;
import real.map.Map;
import real.map.Mob;
import real.map.MobTemplate;
import real.map.MobTemplateData;
import real.npc.Npc;
import real.map.WayPoint;
import real.pet.Pet;
import real.player.Inventory;
import real.player.Player;
import real.player.PlayerManger;
import real.radar.Radar;
import real.radar.RadarTemplate;
import server.io.Session;
import real.skill.Skill;
import real.skill.SpeacialSkill;
import real.skill.SpeacialSkillTemplate;
import real.task.Task;
import real.task.TaskData;
import static real.task.TaskData.getTask;
import real.task.TaskOrders;
import server.io.Message;
import service.Setting;
import server.io.ISession;
import service.data.Init;

public class Service {

    private static Service instance;

    public static Service gI() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
    
    public void showYourNumber(Player player, String Number, String result, String finish, int type){
        Message msg = null;
        try
        {
            msg = new Message(-126);
            msg.writer().writeByte(type); // 1 = RESET GAME | 0 = SHOW CON SỐ CỦA PLAYER
            if(type == 0)
            {
                msg.writer().writeUTF(Number);
            }
            else if(type == 1)
            {
                msg.writer().writeByte(type);
                msg.writer().writeUTF(result); // 
                msg.writer().writeUTF(finish);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void SendPowerInfo(Player player){
        Message msg = null;
        try {
            msg = new Message(-115);
            msg.writer().writeUTF("TL");
            msg.writer().writeShort(player.currPower);
            msg.writer().writeShort(10);
            msg.writer().writeShort(3);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void SendRadar(Player pl, int active, int ID){
        try{
            Message message = new Message(127);
            message.writer().writeByte(active);
            switch (active)
            {
                case 0: // danh sach
                    int size = pl.radar.radars.size();
                    message.writer().writeShort(size); // count Radar
                    for(int i = 0; i < size; i++){
                        RadarTemplate rd = pl.radar.radars.get(i);
                        message.writer().writeShort(rd.id); // id
                        message.writer().writeShort(rd.icon); // IconId
                        message.writer().writeByte(rd.rank); // rank d - c - a - s - ss - sss
                        message.writer().writeByte(rd.count); // Amount
                        message.writer().writeByte(rd.max); // max Amount
                        message.writer().writeByte(rd.type); // type 0: monster, 1: charpart
                        switch (rd.type)
                        {
                            case 0: // templateId
                                message.writer().writeShort(rd.template);
                                break;
                            case 1: // head - body - leg - bag
                                message.writer().writeShort(rd.template);
                                message.writer().writeShort(rd.template);
                                message.writer().writeShort(rd.template);
                                message.writer().writeShort(rd.template);
                                break;
                        }
                        message.writer().writeUTF(rd.name);
                        message.writer().writeUTF(rd.info);
                        message.writer().writeByte(rd.level); // level
                        message.writer().writeByte(rd.user); // use
                        message.writer().writeByte(rd.itemOptions.size()); // count option
                        for(ItemOption op : rd.itemOptions){
                            message.writer().writeByte(op.optionTemplate.id); // option id
                            message.writer().writeShort(op.param); // option param
                            message.writer().writeByte(op.activeCard); // option activeCard
                        }
                    }
                    pl.sendMessage(message);
                    break;
                case 1: // sử dụng card
                    RadarTemplate rd_card = pl.radar.getRadar(ID);
                    if(rd_card != null && rd_card.level > 0){
                        rd_card.user = rd_card.user == 0 ? 1 : 0;
                        message.writer().writeShort(rd_card.id); // id
                        message.writer().writeByte(rd_card.user);
                    }
                    pl.sendMessage(message);
                    break;
                case 2: // Set level
                    RadarTemplate rd_level = pl.radar.getRadar(ID);
                    if(rd_level != null){
                        message.writer().writeShort(rd_level.id); // id
                        message.writer().writeByte(rd_level.level);
                    }
                    pl.sendMessage(message);
                    break;
                case 3: // Set Amount
                    RadarTemplate rd_amount = pl.radar.getRadar(ID);
                    if(rd_amount != null){
                        message.writer().writeShort(rd_amount.id); // id
                        message.writer().writeByte(rd_amount.count);
                        message.writer().writeByte(rd_amount.max);
                    }
                    pl.sendMessage(message);
                    break;
                case 4: // Set AuraEff
                    RadarTemplate rd_aura = pl.radar.getRadar(ID);
                    if(rd_aura != null){
                        message.writer().writeInt(pl.id);
                        message.writer().writeShort(rd_aura.aura);
                    }
                    sendMessAllPlayerInMap(pl.zone, message);
                    break;
            }
            message.cleanup();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void NpcChat(Player pl ,short npcId, String text){
        try{
            var message = new Message(124);
            message.writer().writeShort(npcId);
            message.writer().writeUTF(text);
            pl.session.sendMessage(message);
        }
        catch (Exception e){
        }
    }
    
    public void sendTabTop(Player pl , String topname, List<TopInfo> tops, boolean isShow){
        Message msg = null;
        try{
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF(topname);
            msg.writer().writeByte(tops.size());
            for(int i = 0 ; i < tops.size() ; i++){
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(tops.get(i).pId);
                msg.writer().writeShort(tops.get(i).headID);
                if(pl.session.get_version() >= 214){
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(tops.get(i).body);
                msg.writer().writeShort(tops.get(i).leg);
                msg.writer().writeUTF(tops.get(i).name);
                msg.writer().writeUTF(Util.timeAgo(Util.currentTimeSec(), tops.get(i).curr));
                if(pl.id == tops.get(i).pId || isShow){
                    msg.writer().writeUTF(tops.get(i).info);
                }
                else
                {
                    msg.writer().writeUTF("...");
                }
            }
            pl.sendMessage(msg);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void sendInputText(Player pl, String title, int row, int[] inputtype, String[] text){
        Message msg = null;
        try{
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(row);
            for(int i = 0 ; i < row ; i++){
                msg.writer().writeUTF(text[i]);
                msg.writer().writeByte(inputtype[i]);
            }
            pl.sendMessage(msg);
        }catch(Exception e){
        }
    }
    
    public void sendEffPlayer(Player pl, Player plReceive, int idEff, int layer, int loop, int loopCount){
        Message msg = null;
        try{
            msg = new Message(-128);
            msg.writer().writeByte(0);
            msg.writer().writeInt(pl.id);
            msg.writer().writeShort(idEff);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(loop);
            msg.writer().writeShort(loopCount);
            msg.writer().writeByte(0);
            plReceive.sendMessage(msg);
            msg.cleanup();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void sendEff(Player pl, int idEff, int x, int y){
        Message msg = null;
        try{
            msg = new Message(113);
            msg.writer().writeByte(1);
            msg.writer().writeByte(3);
            msg.writer().writeByte(idEff);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeShort(-1);
            sendMessAllPlayerInMap(pl.zone ,msg);
            msg.cleanup();
        }catch(Exception e){
        }
    }
    
    public void sendtMabuEff(Player pl , int percent){
        Message msg = null;
        try{
            msg = new Message(-117);
            msg.writer().writeByte(percent);
            pl.sendMessage(msg);
            msg.cleanup();
        }catch(Exception e){
        }
    }
    
    public void sendImageMabuEgg(Player pl){
        Message msg = null;
        try{
            msg = new Message(-122);
            msg.writer().writeShort(50);
            msg.writer().writeByte(1);
            msg.writer().writeShort(4664);
            msg.writer().writeByte(0);
            msg.writer().writeInt(pl.mabuEgg.timeHatches);
            pl.sendMessage(msg);
            msg.cleanup();
        }
        catch(Exception e){
        }
    }
    
    public void sendBigBoss(Zone map, int action, int size, int id, int dame){
        Message msg = null;
        try {
            msg = new Message(102);
            msg.writer().writeByte(action);
            if(action != 6 && action != 7){
                msg.writer().writeByte(size); // SIZE PLAYER ATTACK
                msg.writer().writeInt(id); // PLAYER ID
                msg.writer().writeInt(dame); // DAME
            }
            sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendBigBoss2(Zone map, int action, Mob bigboss){
        Message msg = null;
        try {
            msg = new Message(101);
            msg.writer().writeByte(action);
            msg.writer().writeShort(bigboss.cx);
            msg.writer().writeShort(bigboss.cy);
            sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendPlayerVS(Player pVS1, Player pVS2, byte type){
        Message msg = null;
        try {
            pVS1.typePk = type;
            msg = new Message(-30);
            msg.writer().writeByte((byte)35);
            msg.writer().writeInt(pVS1.id); //ID PLAYER
            msg.writer().writeByte(type); //TYPE PK
            pVS1.sendMessage(msg);
            if(pVS2.isPl()){
                pVS2.sendMessage(msg);
            }
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendJoinClan(Player pl){
         Message msg = null;
        try{
            msg = new Message(-61);
            msg.writer().writeInt(pl.id);
            msg.writer().writeInt(pl.clan.id);
            sendMessAllPlayerInMap(pl.zone, msg);
        }catch(Exception e){
        }
    }
    
    public void ItemRequest(Player pl , int type , int where , int index , String text){
        Message msg = null;
        try{
            msg = new Message(-43);
            msg.writer().writeByte(type);
            msg.writer().writeByte(where);
            msg.writer().writeByte(index);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
        }catch(Exception e){
        }
    }
    
    public void send_tab_info(Player pl){
        Message msg = null;
        try {
            msg = new Message(50);
            msg.writer().writeByte(1);
            msg.writer().writeShort(1);
            
//            msg.writer().writeUTF("Hỗ Trợ Nhiệm Vụ");
//            msg.writer().writeUTF("Hỗ Trợ Nhiệm Vụ TDST\n-Khung giờ từ 17h đến 18h\n-Trong khoảng thời gian đó những người đã làm xong nhiệm vụ TDST sẽ không thể vào khu có boss");
//            pl.session.sendMessage(msg);
            
            msg.writer().writeUTF("NRO TuanZin THÔNG BÁO");
            msg.writer().writeUTF("-Ra mắt đồ 8 sao\n+TuanZin chính thức ra mắt đồ 8 sao\n-Ra mắt đồ [+8]\n+TuanZin chính thức ra mắt đồ [+8] (Cấp 8).\nBQT TuanZin Chúc bạn chơi game vui vẻ.");
            pl.session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void send_special_skill(Player pl) {
        Message msg = null;
        try {
            String[] peacialSkill = SpeacialSkill.gI().getSpeacialSkill(pl);
            msg = new Message(112);
            msg.writer().writeByte(0);
            msg.writer().writeShort(Integer.parseInt(peacialSkill[0]));
            msg.writer().writeUTF(peacialSkill[1]);
            pl.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendPopUpMultiLine(Player pl, int tempID, int avt, String text) {
        Message msg = null;
        try {
            msg = new Message(38);
            msg.writer().writeShort(tempID);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(avt);
            pl.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendBigMess(Player pl , int avt , String text){
        Message msg = null;
        try {
            msg = new Message(-70);
            msg.writer().writeShort(avt);
            msg.writer().writeUTF(text);
            msg.writer().writeByte(0);
            pl.session.sendMessage(msg);
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

    public void send_all_special_skill(Player pl) {
        Message msg = null;
        try {
            msg = new Message(112);
            msg.writer().writeByte(1);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Nội\nTại");
            msg.writer().writeByte(SpeacialSkill.gI().getSpeacialSkills(pl.gender).size());
            for (SpeacialSkillTemplate skill : SpeacialSkill.gI().getSpeacialSkills(pl.gender)) {
                msg.writer().writeShort(skill.imgID);
                msg.writer().writeUTF(skill.info1);
            }
            pl.session.sendMessage(msg);
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

    public int getNpcQuest(Player pl , String text){
        int id = -1;
        if(text.equals("home")){
            switch (pl.gender) {
                case 0:
                    id = 0;
                    break;
                case 1:
                    id = 2;
                    break;
                case 2:
                    id = 1;
                    break;
            }
        }
        else if(text.equals("dao"))
        {
            id = 13 + pl.gender;
        }
        else
        {
            id = Integer.valueOf(text) + (pl.taskId == 7 && pl.taskIndex == 2 ? pl.gender : 0);
        }
        return id;
    }
    
    public synchronized TaskOrders createTask(Player pl) {
        int type = Util.nextInt(0,2);
        if(type == 0){
            MobTemplate mob = MobTemplateData.gI().getTempByLevel(pl.getLevel() + Util.nextInt(2,5));
            if(mob != null){
                int mobId = mob.mobTemplateId;
                Zone mapMob = Mob.getMob(mobId).map;
                int c = Util.nextInt(150,500);
                String n = "Tiêu diệt " +c +" " + mob.name;
                String des = "Hãy đến " + mapMob.map.name + " và tiêu diệt " + c + " " + mob.name;
                return new TaskOrders(type,0,(short)c,n,des,mobId,mapMob.map.id);
            }
        }else if(type == 1){
            int countPem = Util.nextInt(10,40);
            String n = "Hạ gục " + countPem +" người chơi";
            String des = "Hạ gục " + countPem +" người chơi";
            return new TaskOrders(type,0,(short)countPem,n,des,-1,-1);
        }else{
            int countPick = Util.nextInt(20,500) * (pl.getLevel() == 0 ? 1 : pl.getLevel());
            int min = Util.nextInt(100,10000);
            String n = "Nhặt " + Util.getMoneys(countPick * min) +" vàng";
            String des = "Hãy nhặt đủ " +Util.getMoneys(countPick * min) +" vàng về đây cho ta";
            return new TaskOrders(type,0,(short)countPick,n,des,min,-1);
        }
        return null;
    }
    
    public void send_task_orders(Player pl,TaskOrders task){
        Message msg = null;
        try {
            msg = new Message(96);
            msg.writer().writeByte(task.taskId);
            msg.writer().writeShort(task.count);
            msg.writer().writeShort(task.maxCount);
            msg.writer().writeUTF(task.name);
            msg.writer().writeUTF(task.description);
            msg.writer().writeByte(task.killId);
            msg.writer().writeByte(task.mapId);
            pl.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void send_task(Player pl, Task t) {
        Message msg = null;
        try {
            msg = new Message(40);
            msg.writer().writeShort(t.taskId);
            msg.writer().writeByte(pl.taskIndex);
            msg.writer().writeUTF(NpcTraTask(pl, t.name));
            msg.writer().writeUTF(NpcTraTask(pl, t.detail));
            msg.writer().writeByte(t.subNames.length);
            for (int i = 0; i < t.subNames.length; i++)
            {
                msg.writer().writeUTF(NpcTraTask(pl, t.subNames[i]));
                msg.writer().writeByte(getNpcQuest(pl , t.npc_quest[i]));
                msg.writer().writeShort(t.maps[i][pl.gender]);
                msg.writer().writeUTF(NpcTraTask(pl, t.contentInfo[i]));
            }
            msg.writer().writeShort(pl.taskCount);
            for (int c : t.counts)
            {
                msg.writer().writeShort(c);
            }
            pl.session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void send_task_next(Player pl) {
        Message msg = null;
        try {
            msg = new Message(41);
            pl.taskCount = 0;
            if (pl.taskIndex > TaskData.getTask(pl.taskId).subNames.length - 1)
            {
                pl.taskIndex = (byte)(TaskData.getTask(pl.taskId).subNames.length - 1);
            }
            else
            {
                pl.taskIndex += 1;
            }
            pl.session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public String NpcTraTask(Player pl, String str)
    {
        if (!str.contains("#")) {
            return str;
        }
        String textreplace = "";
        String text = "";
        if (str.contains("home")) {
            switch (pl.gender) {
                case 0:
                    textreplace = "ông Gôhan";
                    break;
                case 1:
                    textreplace = "ông Moori";
                    break;
                case 2:
                    textreplace = "ông Paragus";
                    break;
            }
            text = str.replace("#home", textreplace);
        }
        else if (str.contains("dao")) {
            switch (pl.gender) {
                case 0:
                    textreplace = "Quy Lão";
                    break;
                case 1:
                    textreplace = "Trưởng lão";
                    break;
                case 2:
                    textreplace = "Vua Vegeta";
                    break;
            }
            text = str.replace("#dao", textreplace);
        }
        else if (str.contains("mob")) {
            if(pl.taskId == 7)
            {
                textreplace = MobTemplateData.gI().getTemp(7 + pl.gender).name;
            }
            else if(pl.taskId == 8)
            {
                textreplace = MobTemplateData.gI().getTemp(10 + (pl.gender == 0 ? 1 : pl.gender == 1 ? 2 : 0)).name;
            }
            else if(pl.taskId == 14){
                textreplace = MobTemplateData.gI().getTemp((13 + pl.gender)).name;
            }
            text = str.replace("#mob", textreplace);
        }
        else if (str.contains("npc")) {
            switch (pl.gender)
            {
                case 0:
                    textreplace = "Bunma";
                    break;
                case 1:
                    textreplace = "Dende";
                    break;
                case 2:
                    textreplace = "Appule";
                    break;
            }
            text = str.replace("#npc", textreplace);
        }
        if (text.contains("map")) {
            if(pl.taskId == 7)
            {
                switch (pl.gender)
                {
                    case 0:
                        textreplace = "Rừng nấm";
                        break;
                    case 1:
                        textreplace = "Thung lũng Maima";
                        break;
                    case 2:
                        textreplace = "Rừng nguyên sinh";
                        break;
                }
            }
            else if(pl.taskId == 9 || pl.taskId == 10 || pl.taskId == 11)
            {
                switch (pl.gender)
                {
                    case 0:
                        textreplace = "Đảo Kamê";
                        break;
                    case 1:
                        textreplace = "Đảo Guru";
                        break;
                    case 2:
                        textreplace = "Vách núi đen";
                        break;
                }
            }
            text = text.replace("#map", textreplace);
        }
        return text;
    }

    public void phan_sat_thuong(Player pl, Mob mob, int dame) {
        Message msg = null;
        try {
            if(pl != null){
                int hpNguocLai = 0;
                msg = new Message(56);
                msg.writer().writeInt((int) pl.id); // char bi phan
                if(dame >= pl.point.getHP()){
                    dame = pl.point.hp - 1;
                }
                if(pl.point.hp > 1)
                {
                   hpNguocLai = pl.injured(null, null, dame, true); 
                }
                msg.writer().writeInt(pl.point.getHP());
                msg.writer().writeInt(hpNguocLai);
                msg.writer().writeBoolean(false); //crit
                msg.writer().writeByte(36); //hiệu ứng pst
                Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            }
            if(mob != null){
                int hpNguocLai = 0;
                if(dame >= mob.hp){
                    dame = mob.hp - 1;
                }
                if(mob.hp > 1){
                   hpNguocLai = dame; 
                }
                mob.sethp(mob.hp -= hpNguocLai);
                msg = new Message(-9);
                msg.writer().writeByte(mob.id);
                msg.writer().writeInt(mob.hp);
                msg.writer().writeInt(hpNguocLai);
                msg.writer().writeBoolean(false); //crit
                msg.writer().writeByte(36); //hiệu ứng pst
                Service.gI().sendMessAllPlayerInMap(mob.map, msg);
            }
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void LuckyRound(Player p, int type, int soluong) {
        int typePrice = 1;
        if(p.inventory.findItemBagByTemp(821) == null){
            typePrice = 0;
        }
        if(typePrice == 0 && p.inventory.gold < Setting.GOLD_ROUND){
            Service.gI().sendThongBao(p, "Bạn không đủ vàng");
            return;
        }
        if(typePrice == 1 && p.inventory.findItemBagByTemp(821) == null){
            Service.gI().sendThongBao(p, "Bạn không có vé");
            return;
        }
        if( p.inventory.itemsBoxSecond.size() >= 56){
            Service.gI().sendThongBao(p, "Rương Phụ Đã Quá Tải");
            return;
        }
        if(type == 1 && p.inventory.itemsBoxSecond.size()+ soluong > 56){
            Service.gI().sendThongBao(p, "Rương Phụ Sẽ Quá Tải");
            Service.gI().LuckyRound(p, 0, 0);
            return;
        }
        Message m = null;
        try {
            if (type == 0) {
                m = new Message(-127);
                m.writer().writeByte(type);
                short[] arId = new short[]{419, 420, 421, 422, 423, 424, 425};
                m.writer().writeByte(7);
                for (short i = 0; i < arId.length; i++) {
                    m.writer().writeShort(arId[i]);
                }
                m.writer().writeByte(typePrice);
                m.writer().writeInt(typePrice == 0 ? Setting.GOLD_ROUND : 0);
                m.writer().writeShort(821);
                p.session.sendMessage(m);
                m.cleanup();
            } else if (type == 1) {
                if((typePrice == 1 && p.inventory.findItemBagByTemp(821).quantity < soluong) || (typePrice == 0 && p.inventory.gold < Setting.GOLD_ROUND * soluong)){
                    Service.gI().sendThongBao(p, "Không thể thực hiện");
                    Service.gI().LuckyRound(p, 0, 0);
                    return;
                }
                m = new Message(-127);
                m.writer().writeByte(type);
                m.writer().writeByte(soluong);
                List<Integer> list = new LinkedList<Integer>(Arrays.asList(p.LIST_GIFT));
                for (short i = 0; i < soluong; i++) {
                    int ID = list.get(Util.nextInt(0, list.size() - 1));
                    Item item = ItemData.gI().get_item(ID);
                    if(item.template.type == 11 && !Util.isTrue(10)){
                        ID = 190;
                    }
                    else if(item.template.id >= 942 && item.template.id <= 944 && !Util.isTrue(7)){
                        ID = 190;
                    }
                    else if(item.template.type == 5 && !Util.isTrue(6)){
                        ID = 190;
                    }
                    else if(item.template.id == 988 && !Util.isTrue(5)){
                        ID = 190;
                    }
                    item = ItemData.gI().get_item(ID);
                    m.writer().writeShort(item.template.iconID);
                    double randomRate = Util.RandomNumber(0.0, 100.0);
                    switch (item.template.type) {
                        case 11:
                            item.itemOptions.clear();
                            item.itemOptions.add(new ItemOption(77,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(103,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(50,Util.nextInt(2,12)));
                            if(randomRate >= 0.2){
                                item.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
                            }
                            break;
                        case 9:
                            int gold = Util.nextInt(5, 12);
                            item.itemOptions.clear();
                            item.itemOptions.add(new ItemOption(171,gold));
                            item.quantity = gold * 1000;
                            break;
                        case 5:
                            item.itemOptions.clear();
                            item.itemOptions.add(new ItemOption(50, 24));
                            item.itemOptions.add(new ItemOption(77, 24));
                            item.itemOptions.add(new ItemOption(117,20));
                            item.itemOptions.add(new ItemOption(154,0));
                            if(randomRate >= 0.5){
                                item.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
                            }
                            break;
                    }
                    switch (item.template.id) {
                        case 942:
                            item.itemOptions.clear();
                            item.itemOptions.add(new ItemOption(77,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(103,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(50,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(14,Util.nextInt(1, 5)));
                            if(randomRate >= 0.2){
                                item.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
                            }
                            break;
                        case 943:
                            item.itemOptions.clear();
                            item.itemOptions.add(new ItemOption(77, Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(103, Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(50, Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(14, Util.nextInt(1, 5)));
                            if(randomRate >= 0.2){
                                item.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
                            }
                            break;
                        case 944:
                            item.itemOptions.clear();
                            item.itemOptions.add(new ItemOption(77,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(103,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(50,Util.nextInt(2,12)));
                            item.itemOptions.add(new ItemOption(14,Util.nextInt(1, 5)));
                            if(randomRate >= 0.2){
                                item.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
                            }
                            break;
                    }
                    p.inventory.itemsBoxSecond.add(item); 
                }
                if(Util.isTrue(30) && p.getBagNull() > 0 && Setting.EVENT_GIANG_SINH){
                    int[] NROB = new int[]{926, 927};
                    int itemID = NROB[Util.nextInt(0, 1)];
                    Item quaNoel = ItemData.gI().get_item(itemID);
                    quaNoel.itemOptions.clear();
                    quaNoel.itemOptions.add(new ItemOption(174, 2022));
                    Timestamp timenow = new Timestamp(System.currentTimeMillis());
                    quaNoel.buyTime = timenow.getTime();
                    p.inventory.addItemBag(quaNoel);
                    Service.gI().sendThongBao(p, "Bạn đã nhận được " + quaNoel.template.name);
                }
                if(typePrice == 1)
                {
                    p.inventory.subQuantityItemsBag(p.inventory.findItemBagByTemp(821), soluong);
                }
                else if(typePrice == 0)
                {
                    p.inventory.gold -= Setting.GOLD_ROUND * soluong;
                }
                Service.gI().sendMoney(p);
                p.inventory.sendItemBags();
                p.session.sendMessage(m);
                m.cleanup();
            }
        }
        catch (Exception e) {
            Util.logException(Service.class, e);
        }
        finally{
            m.cleanup();
        }
    }

    public void SellItem(Player pl, int action, int type, int index) {
        Message m = null;
        try {
            m = new Message(7);
            if (action == 0) {
                if(pl.session.get_version() <= 219){
                    index -= 3;
                }
                switch (type) {
                    case 1:
                        if(index < 0){
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            return;
                        }
                        Item item = pl.inventory.itemsBag.get(index);
                        if (item == null) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            return;
                        }
                        ItemShop itemShop = ItemShopDAO.getByTemp(item.template.id);
                        if (item.template.id == 457) {
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x1 "+ item.template.name + " Với Giá " + Util.getMoneys(Setting.GOLD_SELL_TV_1) + " Vàng Không?", m);
                        }
                        else if(item.template.id == 1184){
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x1 "+ item.template.name + " Với Giá " + Util.getMoneys(Setting.GOLD_SELL_TV_2) + " Vàng Không?", m);
                        }
                        else if (itemShop != null && itemShop.itemTemplate.type >= 0 && itemShop.itemTemplate.type < 5) {
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x" + item.quantity + " " + item.template.name + " Với Giá " + itemShop.gold / 4 + " Vàng Không?", m);
                        }
                        else if(itemShop != null && itemShop.gold <= 0) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                        }
                        else {
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x" + item.quantity + " " + item.template.name + " Với Giá " + item.quantity + " Vàng Không?", m);
                        }
                        break;
                    case 0:
                        if(index < 0){
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            return;
                        }
                        Item itembody = pl.inventory.itemsBody.get(index);
                        if (itembody == null) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            return;
                        }
                        ItemShop itemShop2 = ItemShopDAO.getByTemp(itembody.template.id);
                        if (itembody.template.id == 457) {
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x1 "+ itembody.template.name + " Với Giá " + Util.getMoneys(Setting.GOLD_SELL_TV_1) + " Vàng Không?", m);
                        }
                        else if(itembody.template.id == 1184){
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x1 "+ itembody.template.name + " Với Giá " + Util.getMoneys(Setting.GOLD_SELL_TV_2) + " Vàng Không?", m);
                        }
                        else if (itemShop2 != null && itemShop2.gold > 0) {
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x" + itembody.quantity + " " + itembody.template.name + " Với Giá " + itemShop2.gold / 4 + " Vàng Không?", m);
                        }
                        else if(itemShop2 != null && itemShop2.gold <= 0) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                        }
                        else {
                            SaleRequest(pl, 1, index, "Bạn Có Muốn Bán x" + itembody.quantity + " " + itembody.template.name + " Với Giá " + itembody.quantity + " Vàng Không?", m);
                        }
                        break;
                }
            } else if (action == 1 && type == 1) {
                if(index < 0){
                    Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    return;
                }
                Item item = pl.inventory.itemsBag.get(index);
                if (item == null) {
                    Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    return;
                }
                ItemShop itemShop = ItemShopDAO.getByTemp(item.template.id);
                int goldPlus = 0;
                if (item.template.id == 457) {
                    goldPlus = Setting.GOLD_SELL_TV_1;
                }
                else if(item.template.id == 1184){
                    goldPlus = Setting.GOLD_SELL_TV_2;
                }
                else if (itemShop != null && itemShop.itemTemplate.type >= 0 && itemShop.itemTemplate.type < 5) {
                    goldPlus = itemShop.gold / 4;
                }
                else if(itemShop != null && itemShop.gold <= 0) {
                    return;
                }
                else {
                   goldPlus = item.quantity;
                }
                if(pl.inventory.gold + goldPlus > pl.inventory.LIMIT_GOLD){
                    goldPlus = (int) (pl.inventory.LIMIT_GOLD - pl.inventory.gold) ;
                }
                Service.gI().sendThongBao(pl, "Đã Bán Thành Công " + item.template.name);
                pl.inventory.gold += goldPlus;
                Service.gI().sendMoney(pl);
                if(item.template.id == 457){
                    pl.inventory.subQuantityItemsBag(item,1);
                }
                else if(item.template.id == 1184){
                    pl.inventory.subQuantityItemsBag(item,1);
                }
                else{
                    pl.inventory.removeItemBag(index);
                }
                pl.inventory.sortItemBag();
                pl.inventory.sendItemBags();
            }
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (m != null) {
                m.cleanup();
                m = null;
            }
        }
    }

    public void SaleRequest(Player pl, int type, int id, String text, Message msg) {
        try {
            msg.writer().writeByte(type);
            msg.writer().writeShort(id);
            msg.writer().writeUTF(text);
            pl.session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendMessAllPlayerInClan(Message msg, int id) {
        try {
            for (Player pl : PlayerManger.gI().getPlayers()) {
                if (pl.session != null && pl != null && pl.clan != null && pl.clan.id == id) {
                    pl.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void PlayerMove(Player player) {
        Zone map = player.zone;
        try {
            if(player.x >= 35 && player.x <= map.map.pxw - 35){
                int type = map.map.tileMap[player.y / 24][player.x / 24];
//                int[] Move = map.MoveXY(player);
//                if(Move[1] == player.ySend && Move[0] == player.xSend && !map.isPhuBan() && !MapManager.gI().isMapOffline(map.map.id)){
//                    Service.gI().resetPoint(player, Move[0], Move[1]);
//                    return;
//                }
                player.isFly = type == 0;
                if (player.isFly && player.getMount() == -1) {
                    int mp = player.point.mpGoc / 100;
                    player.hoi_ki(-mp);
                }
                if (player.pet != null) {
                    player.pet.followMaster();
                }
                if (player.newpet != null) {
                    player.newpet.followMaster();
                }
            }
            else{
                if(!map.isWayPoint(player)){
                    Service.gI().resetPoint(player, player.xSend, player.ySend);
                    return;
                }
            }
            MapService.gI().playerMove(player);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        }
    }
    
    public void sendMessClanAllPlayer(Message msg, Player player) {
        try {
            Message mes = msg;
            PlayerManger.gI().getPlayers().stream().filter(pl -> pl.session != null && pl != null && pl.clan != null && pl.clan.id == player.clan.id && pl.isPl()).forEach((pl) -> {
                pl.sendMessage(mes);
            });
            mes.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendMessAllPlayer(Message msg) {
        Message mes = msg;
        try {
            PlayerManger.gI().getPlayers().stream().filter(pl -> pl.session != null && pl != null && pl.isPl()).forEach((pl) -> {
                pl.sendMessage(mes);
            });
            mes.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendMessAllPlayerIgnoreMe(Player player, Message msg) {
        try {
            Message mes = msg;
            PlayerManger.gI().getPlayers().stream().filter(pl -> pl.session != null && pl != null && pl.isPl() && !player.equals(pl)).forEach((pl) -> {
                pl.sendMessage(mes);
            });
            mes.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendMessAllPlayerInMap(Zone map, Message msg) {
        try {
            Message mes = msg;
            map.getPlayers().stream().filter(pl -> pl != null && pl.session != null && pl.isPl()).forEach((pl) -> {
                pl.sendMessage(mes);
            });
            mes.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendMessAllPlayerNotMeInMap(Zone map, Player player, Message msg) {
        try {
            Message mes = msg;
            map.getPlayers().stream().filter(pl -> pl != null && pl.session != null && pl.isPl() && !player.equals(pl)).forEach((pl) -> {
                pl.sendMessage(mes);
            });
            mes.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendMessAnotherNotMeInMap(Player player, Message msg) {
        try {
            Message mes = msg;
            player.zone.getPlayers().stream().filter(pl -> pl != null && pl.session != null && pl.isPl() && !player.equals(pl)).forEach((pl) -> {
                pl.sendMessage(mes);
            });
            mes.cleanup();
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void Reload_HP_NV(Player pl) {
        Message msg = null;
        try {
            msg = messageSubCommand((byte)9);
            msg.writer().writeInt(pl.id);
            msg.writer().writeInt(pl.point.hp);
            msg.writer().writeInt(pl.point.getHPFull());
            sendMessAnotherNotMeInMap(pl, msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void Send_Info_NV(Player pl, int...type) {
        Message msg = null;
        try {
            msg = messageSubCommand((byte) 14);//Cập nhật máu
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeInt(pl.point.getHP());
            msg.writer().writeByte(type.length);//Hiệu ứng Ăn Đậu
            msg.writer().writeInt(pl.point.getHPFull());
            sendMessAnotherNotMeInMap(pl, msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void loginDe(Session session, short second) {
        Message msg = null;
        try {
            msg = new Message(122);
            msg.writer().writeShort(second);
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void getImgByName(Session session, String name) {
        Message msg = null;
        try {
            var nFrame = 3;
            if(name.contains("mount")){
                if(name.contains("3_1") || name.contains("7_1") || name.contains("9_0") || name.contains("12_1") || name.contains("13_0") || name.contains("13_1")) nFrame = 4;
                else if(name.contains("4_1")) nFrame = 2;
            }
            else if(name.contains("aura")){
                if(name.contains("0_0") || name.contains("0_1")) nFrame = 4;
                else if(name.contains("2_0")) nFrame = 5;
            }
            else if(name.contains("set_eff")){
                if(name.contains("4_0") || name.contains("5_0") || name.contains("6_0") || name.contains("7_0") || name.contains("8_0")) nFrame = 2;
                else if(name.contains("7_1") || name.contains("8_1")) nFrame = 6;
            }
            byte[] dt = FileIO.readFile("data/res/icon_by_name/x" +session.zoom+"/"+name+".png");
            if(dt == null)
            {
                return;
            }
            msg = new Message(66);
            msg.writer().writeUTF(name);
            msg.writer().writeByte(nFrame);
            msg.writer().writeInt(dt.length);
            msg.writer().write(dt);
            session.doSendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        }
        finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void requestIcon(Session session, int id) {
        Message msg = null;
        try {
            byte[] icon = FileIO.readFile("data/res/icon/x" +session.zoom+"/"+id+".png");
            if(icon == null)
            {
                return;
            }
            msg = new Message(-67);
            msg.writer().writeInt(id);
            msg.writer().writeInt(icon.length);
            msg.writer().write(icon);
            session.doSendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void createChar(Session session)
    {
        Message msg = null;
        try {
            msg = new Message(2);
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendResource(Session session, int type) {
        Message msg = null;
        try {
            switch(type){
                case 1:
                    boolean isSleep = session.isResource;
                    session.isResource = false;
                    if(isSleep){
                        Thread.sleep(1000);
                    }
                    msg = new Message(-74);
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(Init.IMAGE_SOURCE[session.zoom - 1].size());
                    session.sendMessage(msg);
                    break;
                case 2:
                    if(!session.isResource){
                        session.isResource = true;
                        Iterator i = Init.IMAGE_SOURCE[session.zoom - 1].keySet().iterator();
                        do
                        {
                            String original = (String)i.next();
                            byte[] res = (byte[]) Init.IMAGE_SOURCE[session.zoom - 1].get(original);
                            msg = new Message(-74);
                            msg.writer().writeByte(2);
                            msg.writer().writeUTF(original);
                            msg.writer().writeInt(res.length);
                            msg.writer().write(res);
                            session.sendMessage(msg);
                        }
                        while (i.hasNext() && session.isResource);
                        msg = new Message(-74);
                        msg.writer().writeByte(3);
                        msg.writer().writeInt(Setting.VERSION_IMAGE_SOURCE);
                        session.sendMessage(msg);
                    }
                    break;
                case 0:
                case 3:
                    msg = new Message(-74);
                    msg.writer().writeByte(0);
                    msg.writer().writeInt(Setting.VERSION_IMAGE_SOURCE);
                    session.sendMessage(msg);
                    break;
            }
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void itemBg(Session session, int id) {
        Message msg = null;
        try
        {
            byte[] item_bg = FileIO.readFile("data/res/map/item_bg/x" + session.zoom + "/" + id);
            if(item_bg == null)
            {
                Util.debug("item_bg: " + id);
                return;
            }
            msg = new Message(-31);
            msg.writer().write(item_bg);
            session.sendMessage(msg);
        }
        catch (Exception e)
        {
            Util.logException(Service.class, e);
        }
        finally
        {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void bgTemp(Session session, int id) {
        Message msg = null;
        try {
            byte[] bg_temp = FileIO.readFile("data/res/map/bg_temp/x" + session.zoom + "/" + id);
            if(bg_temp == null)
            {
                Util.debug("bg_temp: " + id);
                return;
            }
            msg = new Message(-32);
            msg.writer().write(bg_temp);
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void effData(Session session, int id) {
        Message msg = null;
        try {
            int idImg = id;
            if(id == 25 && session.player != null && session.player.zone.map.id != 0 && session.player.zone.map.id != 7 && session.player.zone.map.id != 14){
                idImg = Setting.EVENT_GIANG_SINH ? 59 : Setting.EVENT_HALLOWEEN ? 51 : 60;
            }
            byte[] eff_data = FileIO.readFile("data/eff_data/x" + session.zoom + "/data/" + idImg + (idImg == 60 ? "_2" : "_0"));
            byte[] img_eff = FileIO.readFile("data/eff_data/x" + session.zoom + "/ImgEffect_" + idImg + ".png");
            if(eff_data == null)
            {
                Util.debug("effData: " + id);
                return;
            }
            msg = new Message(-66);
            msg.writer().writeShort(id);
            msg.writer().writeInt(eff_data.length);
            msg.writer().write(eff_data);
            if(session.version > 216){
                msg.writer().write(idImg == 60 ? 2 : 0);
            }
            msg.writer().writeInt(img_eff.length);
            msg.writer().write(img_eff);
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void requestMobTemplate(Session session, int id) {
        Message msg = null;
        try {
            byte[] mob = FileIO.readFile("data/res/map/mob/x" + session.zoom + "/" + id);
            if(mob == null)
            {
                Util.debug("Mob: " + id);
                return;
            }
            msg = new Message(11);
            msg.writer().write(mob);
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void updateVersionx(Session session) {
        Message msg = null;
        try {
            msg = new Message(-77);
            msg.writer().writeShort(Setting.MAX_SMALL);
            for(int i = 0 ; i < Setting.MAX_SMALL ; i++){
                msg.writer().writeByte(44);
            }
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendMessage(ISession session, int cmd, String filename) {
        Message msg = null;
        try {
            msg = new Message(cmd);
            msg.writer().write(FileIO.readFile("data/msg/" + filename));
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void updateVersion(Session session) {
        Message msg = null;
        try {
            msg = messageNotMap((byte) 4);
            msg.writer().writeByte(Setting.vsData);
            msg.writer().writeByte(Setting.vsMap);
            msg.writer().writeByte(Setting.vsSkill);
            msg.writer().writeByte(Setting.vsItem);
            msg.writer().writeByte(0);
            long[] smtieuchuan = {1000L,3000L,15000L,40000L,90000L,170000L,340000L,700000L,1500000L,15000000L,150000000L,1500000000L,5000000000L,10000000000L,40000000000L,50010000000L,60010000000L,70010000000L,80010000000L,100010000000L,120010000000L};
            msg.writer().writeByte(smtieuchuan.length);
            for (int i = 0; i < smtieuchuan.length; i++) {
                msg.writer().writeLong(smtieuchuan[i]);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void ClearTask(Player pl) {
        Message msg = null;
        try {
            msg = messageNotMap((byte) 17);
            pl.session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void updateData(Session session) {
        Message msg = null;
        try {
            msg = new Message(-87);
            msg.writer().writeByte(Setting.vsData);
            byte[] ab = FileIO.readFile("data/NR_dart");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_arrow");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_effect");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_image");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_part");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_skill");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            session.doSendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void updateMap(Session session) {
        Message msg = null;
        try {
            msg = new Message(-28);
            msg.writer().write(FileIO.readFile("data/NRmap"));
            session.doSendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void updateSkill(Session session) {
        Message msg = null;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(7);
            msg.writer().write(FileIO.readFile("data/NRskill"));
            session.doSendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void tileSet(Player player) {
        Message msg = null;
        try {
            msg = new Message(-82);
            msg.writer().write(FileIO.readFile("data/map/tile_set/999"));
            player.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void tileSet(Session session) {
        Message msg = null;
        try {
            msg = new Message(-82);
            msg.writer().write(FileIO.readFile("data/map/tile_set/999"));
            session.sendMessage(msg);
        }
        catch (Exception e)
        {
            Util.logException(Service.class, e);
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

    public void mapInfo(Player pl) {
        Message msg = null;
        try {
            if(!pl.isPl()){
                return;
            }
            Zone map = pl.zone;
            msg = new Message(-24);
            msg.writer().writeByte(map.map.id);
            msg.writer().writeByte(map.map.planetId);
            msg.writer().writeByte(map.map.tileId);
            msg.writer().writeByte(map.map.bgId);
            msg.writer().writeByte(map.map.type);
            msg.writer().writeUTF(map.map.name);
            msg.writer().writeByte(map.zoneId);

            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);

            // waypoint
            msg.writer().writeByte(map.wayPoints.length);
            for (WayPoint wp : map.wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(wp.isOffline);
                msg.writer().writeUTF(wp.name);
            }
            
//            int size = 0;
//            for (Mob mob : map.mobs) {
//                if(mob.template.mobTemplateId == 70 && mob.level <= 2 && !mob.isDie() || mob.template.mobTemplateId >= 71 && mob.template.mobTemplateId <= 72 && !mob.isDie()){
//                    size++;
//                }
//                else if (mob.template.mobTemplateId < 70 && mob.template.mobTemplateId > 72){
//                    size++;
//                }
//            }
            Mob[] mobs = map.mobs;
//            Mob[] mobs = new Mob[size];
//            for (int i = 0; i < map.mobs.length; i++) {
//                Mob mob = map.mobs[i];
//                if(mob.template.mobTemplateId == 70 && mob.level <= 2 && !mob.isDie() || mob.template.mobTemplateId >= 71 && mob.template.mobTemplateId <= 72 && !mob.isDie()){
//                    mobs[i] = mob;
//                }
//                else if (mob.template.mobTemplateId < 70 && mob.template.mobTemplateId > 72){
//                    mobs[i] = mob;
//                }
//            }
            // mob
            msg.writer().writeByte(mobs.length);
            for (Mob mob : mobs) {
                msg.writer().writeBoolean(false); //is disable
                msg.writer().writeBoolean(false); //is dont move
                msg.writer().writeBoolean(false); //is fire
                msg.writer().writeBoolean(false); //is ice
                msg.writer().writeBoolean(false); //is wind
                msg.writer().writeByte(mob.template.mobTemplateId);
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt(mob.gethp());
                msg.writer().writeByte(mob.template.level);
                msg.writer().writeInt(mob.getHpFull());
                msg.writer().writeShort(mob.cx);
                msg.writer().writeShort(mob.cy);
                msg.writer().writeByte(mob.isDie() ? 0 : 5);
                msg.writer().writeByte(0);
                msg.writer().writeBoolean(false);
            }

            msg.writer().writeByte(0);

            // npc
            msg.writer().writeByte(map.npcs.length);
            for (Npc npc : map.npcs) {
                int status = npc.status;
                int cx = npc.cx;
                int cy = npc.cy;
                if(npc.tempId == 38 && pl.taskId < 23){
                    status = 15;
                    cx = -1;
                    cy = -1;
                }
                msg.writer().writeByte(status);
                msg.writer().writeShort(cx);
                msg.writer().writeShort(cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }

            msg.writer().writeByte(map.items.size());
            for (ItemMap it : map.items) {
                msg.writer().writeShort(it.itemMapId);
                msg.writer().writeShort(it.itemTemplate.id);
                msg.writer().writeShort(it.x);
                msg.writer().writeShort(it.y);
                msg.writer().writeInt((int) it.playerId);
            }

            // bg item
            try{
                byte[] bgItem = FileIO.readFile("data/map/bg/" + map.map.id);
                msg.writer().write(bgItem);
            }catch(Exception e){
                msg.writer().writeShort(0);
            }
            // eff item
            try
            {
                byte[] effItem = FileIO.readFile("data/map/eff/" + map.map.id);
                msg.writer().write(effItem);
            }
            catch(Exception e){
               msg.writer().writeShort(0);
            }
            msg.writer().writeByte(map.map.bgType);
            msg.writer().writeByte(pl.getUseSpaceShip());
            msg.writer().writeByte(0);
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void removePlayer(Session session, Player player) {
        Message msg = null;
        try {
            msg = new Message(-6);
            msg.writer().writeInt((int) player.id);
            session.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void resetPoint(Player player, int x, int y) {
        Message msg = null;
        try {
            player.x = x;
            player.y = y;
            msg = new Message(46);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            player.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(Service.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void ReturnPoint(Player player, int x, int y) {
        Message msg = null;
        try {
            player.x = x;
            player.y = y;
            msg = new Message(84);
            msg.writer().writeInt(player.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            sendMessAllPlayerInMap(player.zone, msg);
            Send_Info_NV(player);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void login2(Session session, String user) {
        Message msg = null;
        try {
            msg = new Message(-101);
            msg.writer().writeUTF(user);
            session.sendMessage(msg);

        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void mapTemp(Player pl)
    {
        Message msg = null;
        try
        {
            msg = new Message(-28);
            msg.writer().writeByte(10);
            msg.writer().writeByte(pl.zone.map.tmw);
            msg.writer().writeByte(pl.zone.map.tmh);
            int size = pl.zone.map.maps.length;
            for(int i = 0; i < size ; i++){
                msg.writer().writeByte(pl.zone.map.maps[i]);
            }
            pl.session.sendMessage(msg);
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

    public void clearMap(Player player) {
        Message msg = null;
        try {
            msg = new Message(-22);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void chat(Player pl, String text) {
        Message msg = null;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeUTF(text);
            sendMessAllPlayerInMap(pl.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void chatJustForMe(Player me, Player plChat, String text) {
        Message msg = null;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeUTF(text);
            me.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void playerMove(Session session, Player pl) {
        Message msg = null;
        try {
            msg = new Message(-7);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void Rank(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-119);
            msg.writer().writeInt(0);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void Transport(Player pl, int type) {
        Message msg = null;
        try {
            msg = new Message(-105);
            msg.writer().writeShort(pl.maxTime);
            msg.writer().writeByte(type);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void Stamina(Player pl) {
        if (!pl.isPl()) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-68);
            msg.writer().writeShort(pl.point.stamina);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void StaminaMax(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-69);
            msg.writer().writeShort(10000);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void activePoint(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-97);
            msg.writer().writeInt(1000);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public long exp_level1(long sucmanh) {
        if (sucmanh < 3000) {
            return 3000;
        } else if (sucmanh < 15000) {
            return 15000;
        } else if (sucmanh < 40000) {
            return 40000;
        } else if (sucmanh < 90000) {
            return 90000;
        } else if (sucmanh < 170000) {
            return 170000;
        } else if (sucmanh < 340000) {
            return 340000;
        } else if (sucmanh < 700000) {
            return 700000;
        } else if (sucmanh < 1500000) {
            return 1500000;
        } else if (sucmanh < 15000000) {
            return 15000000;
        } else if (sucmanh < 150000000) {
            return 150000000;
        } else if (sucmanh < 1500000000) {
            return 1500000000;
        } else if (sucmanh < 5000000000L) {
            return 5000000000L;
        } else if (sucmanh < 10000000000L) {
            return 10000000000L;
        } else if (sucmanh < 40000000000L) {
            return 40000000000L;
        } else if (sucmanh < 50010000000L) {
            return 50010000000L;
        } else if (sucmanh < 60010000000L) {
            return 60010000000L;
        } else if (sucmanh < 70010000000L) {
            return 70010000000L;
        } else if (sucmanh < 80010000000L) {
            return 80010000000L;
        } else if (sucmanh < 100010000000L) {
            return 100010000000L;
        }
        return 1000;
    }

    public void point(Player pl) {
        if (!pl.isPl()) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-42);
            msg.writer().writeInt(pl.point.hpGoc);
            msg.writer().writeInt(pl.point.mpGoc);
            msg.writer().writeInt(pl.point.dameGoc);
            msg.writer().writeInt(pl.point.getHPFull());// hp full
            msg.writer().writeInt(pl.point.getMPFull());// mp full
            msg.writer().writeInt(pl.point.getHP());// hp
            msg.writer().writeInt(pl.point.getMP());// mp
            msg.writer().writeByte(pl.point.getSpeed());// speed
            msg.writer().writeByte(Setting.HP_FROM_1000_TN);
            msg.writer().writeByte(Setting.MP_FROM_1000_TN);
            msg.writer().writeByte(Setting.SD_FROM_1000_TN);
            msg.writer().writeInt(pl.point.getBaseDame());// dam base
            msg.writer().writeInt(pl.point.getDefFull());// def full
            msg.writer().writeByte(pl.point.getCritFull());// crit full
            msg.writer().writeLong(pl.point.tiemNang);
            msg.writer().writeShort(Setting.EXP_FOR_ONE_ADD);
            msg.writer().writeShort(pl.point.defGoc);
            msg.writer().writeByte(pl.point.critGoc);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
   
    public void player(Player pl) {
        Message msg = null;
        try {
            msg = messageSubCommand((byte) 0);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(pl.taskId);
            msg.writer().writeByte(pl.gender);
            msg.writer().writeShort(pl.head);
            msg.writer().writeUTF(pl.name);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeLong(pl.point.power);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(pl.gender);
            //--------skill---------

            msg.writer().writeByte(pl.playerSkill.skills.size());

            for (Skill skill : pl.playerSkill.skills) {
                msg.writer().writeShort(skill.skillId);
            }

            //---vang---luong--luongKhoa
            if (pl.session.get_version() >= 214) {
                msg.writer().writeLong(pl.inventory.gold);//xu
            } else {
                msg.writer().writeInt((int) pl.inventory.gold);//xu
            }
            msg.writer().writeInt(pl.inventory.ruby);
            msg.writer().writeInt(pl.inventory.gem);

            //--------itemBody---------
            int size = pl.inventory.itemsBody.size();
            if(size < Setting.SIZE_BODY_PLAYER){
                for(int i = size; i < Setting.SIZE_BODY_PLAYER; i++){
                    pl.inventory.itemsBody.add(null);
                }
            }
            msg.writer().writeByte(pl.inventory.itemsBody.size());
            for (Item item : pl.inventory.itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                }
                else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo(pl));
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        int[] option = itemOption.getItemOption(itemOption.optionTemplate.id, itemOption.param);
                        msg.writer().writeByte(option[0]);
                        msg.writer().writeShort(option[1]);
                    }
                }

            }

            //--------itemBag---------
            msg.writer().writeByte(pl.inventory.itemsBag.size());
            for (int i = 0; i < pl.inventory.itemsBag.size(); i++) {
                Item item = pl.inventory.itemsBag.get(i);
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        int[] option = itemOption.getItemOption(itemOption.optionTemplate.id, itemOption.param);
                        msg.writer().writeByte(option[0]);
                        msg.writer().writeShort(option[1]);
                    }
                }

            }

            //--------itemBox---------
            msg.writer().writeByte(pl.inventory.itemsBox.size());
            for (int i = 0; i < pl.inventory.itemsBox.size(); i++) {
                Item item = pl.inventory.itemsBox.get(i);
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        int[] option = itemOption.getItemOption(itemOption.optionTemplate.id, itemOption.param);
                        msg.writer().writeByte(option[0]);
                        msg.writer().writeShort(option[1]);
                    }
                }
            }
            //-----------------
            Set<Integer> keySet = Init.HEAD.keySet();
            msg.writer().writeShort(keySet.size());
            for (Integer key : keySet) {
                msg.writer().writeShort(key);
                msg.writer().writeShort(Init.HEAD.get(key));
            }
            //---------- icon npc con mèo :v ----------
            msg.writer().writeShort(514);
            msg.writer().writeShort(515);
            msg.writer().writeShort(537);
            //------------------------------------------------
            msg.writer().writeByte(pl.typeFusion != 0 ? 1 : 0); // hợp thể
            msg.writer().writeInt(1632811835);
            msg.writer().writeByte(pl.isNewMember ? 1:0); // isnew member

            //----aura----
            msg.writer().writeShort(0);
            //----set_----
            msg.writer().writeByte(8);
            //----hatid----
            msg.writer().writeShort(0);
            pl.sendMessage(msg);
        } catch (Exception e) {
            // ChangeMap.gI().changeMap(pl, 21 + pl.gender, -1, 5);
            pl.session.disconnect();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void send_list_server(Session session) {
        Message msg = null;
        try {
            msg = messageNotLogin((byte)2);
            msg.writer().writeUTF(Setting.LIST_SERVER);
            session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private Message messageNotLogin(byte command) throws IOException {
        Message ms = new Message(-29);
        ms.writer().writeByte(command);
        return ms;
    }

    private Message messageNotMap(byte command) throws IOException {
        Message ms = new Message(-28);
        ms.writer().writeByte(command);
        return ms;
    }

    public Message messageSubCommand(byte command) throws IOException {
        Message ms = new Message(-30);
        ms.writer().writeByte(command);
        return ms;
    }

    public void congTiemNang(Player pl, byte type, long tiemnang, int...level)
    {
        if(level.length > 0)
        {
            int level1 = level[0];
            int level2 = level[1];
            int chia = Math.abs(level1 - level2);
            if(chia > 0)
            {
                for(int i = 0; i < chia; i++){
                    tiemnang /= 2;
                }
            }
        }
        int tnPl = pl.point.TiemNang(tiemnang);
        if(tiemnang <= 0){
            return;
        }
        if(!pl.point.checkPower(tnPl) && type == 2){
            return;
        }
        Message msg = null;
        try
        {
            Task task = getTask(pl.taskId);
            if(task != null && task.subNames[pl.taskIndex].toLowerCase().contains("đạt"))
            {
                long taskLimit = TaskData.getSucManh(pl);
                if(taskLimit != -1 && pl.point.power >= taskLimit)
                {
                    Service.gI().send_task_next(pl);
                }
            }
            Player playerSend = pl;
            msg = new Message(-3);
            if(pl.isPet)
            {
                playerSend = ((Pet)pl).master;
                int typeMaster = type;
                tnPl = playerSend.point.TiemNang(tiemnang);
                typeMaster = playerSend.point.checkPower(tnPl) ? type : 1;
                msg.writer().writeByte(typeMaster);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writer().writeInt(tnPl);// số tn cần cộng
                playerSend.sendMessage(msg);
                if (typeMaster == 2) {
                    playerSend.point.powerUp(tnPl);
                    playerSend.point.tiemNangUp(tnPl);
                }
                else if(typeMaster == 1){
                    playerSend.point.tiemNangUp(tnPl);
                }
                else if(typeMaster == 0) {
                    playerSend.point.powerUp(tnPl);
                }
            }
            else if(pl.isPl()){
                type = playerSend.point.checkPower(tnPl) ? type : 1;
                msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writer().writeInt(tnPl);// số tn cần cộng
                playerSend.sendMessage(msg);
            }
            tnPl = pl.point.TiemNang(tiemnang);
            switch (type) {
                case 0:
                    pl.point.powerUp(tnPl);
                    break;
                case 1:
                    pl.point.tiemNangUp(tnPl);
                    break;
                default:
                    pl.point.tiemNangUp(tnPl);
                    pl.point.powerUp(tnPl);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public String get_HanhTinh(int hanhtinh, int... sucmanh) {
        switch (hanhtinh) {
            case 0:
                return sucmanh.length > 0 ? "Trái đất" : "Nhân";
            case 1:
                return "Namếc";
            default:
                return "Xayda";
        }
    }

    public String exp_level(Player pl) {
        long sucmanh = pl.point.getPower();

        if (sucmanh < 3000) {
            return "Tân thủ";
        } else if (sucmanh < 15000) {
            return "Tập sự sơ cấp";
        } else if (sucmanh < 40000) {
            return "Tập sự trung cấp";
        } else if (sucmanh < 90000) {
            return "Tập sự cao cấp";
        } else if (sucmanh < 170000) {
            return "Tân binh";
        } else if (sucmanh < 340000) {
            return "Chiến binh";
        } else if (sucmanh < 700000) {
            return "Chiến binh cao cấp";
        } else if (sucmanh < 1500000) {
            return "Vệ binh";
        } else if (sucmanh < 15000000) {
            return "Vệ binh hoàng gia";
        } else if (sucmanh < 150000000) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 1";
        } else if (sucmanh < 1500000000) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 2";
        } else if (sucmanh < 5000000000L) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 3";
        } else if (sucmanh < 10000000000L) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 4";
        } else if (sucmanh < 40000000000L) {
            return "Thần " + get_HanhTinh(pl.gender, 1) + " cấp 1";
        } else if (sucmanh < 50010000000L) {
            return "Thần " + get_HanhTinh(pl.gender, 2) + " cấp 2";
        } else if (sucmanh < 60010000000L) {
            return "Thần " + get_HanhTinh(pl.gender, 3) + " cấp 3";
        } else if (sucmanh < 70010000000L) {
            return "Giới Vương Thần cấp 1";
        } else if (sucmanh < 80010000000L) {
            return "Giới Vương Thần cấp 2";
        } else if (sucmanh < 100010000000L) {
            return "Giới Vương Thần cấp 3";
        } else if (sucmanh < 120010000000L) {
            return "Thần Huỷ Diệt cấp 1";
        }
        return "Thần Huỷ Diệt cấp 2";
    }

    public int get_clevel(long sucmanh) {
        long[] smtieuchuan = {120010000000L, 100010000000L, 80010000000L, 70010000000L, 60010000000L, 50010000000L, 40000000000L, 10000000000L, 5000000000L, 1500000000L, 150000000L, 15000000L, 1500000L, 700000L, 340000L, 170000L, 90000L, 40000L, 15000L, 3000L, 1000L};
        int clevel = 0;
        for (int i = 0; i < smtieuchuan.length; i++) {
            if (sucmanh >= smtieuchuan[i]) {
                clevel = ((smtieuchuan.length) - 1) - i;
                break;
            }
        }
        return clevel;
    }

    public void hsChar(Player pl, int hp, int mp) {
        Message msg = null;
        try {
            if(!Util.canDoWithTime(pl.lastTimeRevived, 20) || pl.immortal){
                return;
            }
            pl.setJustRevivaled();
            pl.point.setHpMp(hp, mp);
            if (!pl.isPet) {
                msg = new Message(-16);
                pl.sendMessage(msg);
                msg.cleanup();
                pl.sendInfo();
            }
            msg = messageSubCommand((byte) 15);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeInt(hp);
            msg.writer().writeInt(mp);
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            sendMessAllPlayerInMap(pl.zone, msg);
            Send_Info_NV(pl);
            pl.sendInfoHPMP();
            sendMoney(pl);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void charDie(Player pl) {
        Message msg = null;
        try {
            if(pl.zone.isOSIN()){
               pl.playerKill.currPower += 5;
               SendPowerInfo(pl.playerKill);
            }
            pl.justRevived = false;
            pl.immortal = false;
            pl.percentDamePlus = -1;
            pl.percentHpPlus = -1;
            if(pl.playerSkill.useTroi){
                pl.playerSkill.removeUseTroi();
                pl.playerSkill.removeAnTroi();
            }
            if(pl.playerSkill.count_ttnl != -1){
                pl.playerSkill.stopCharge();
            }
            if (pl.mobMe != null) {
                pl.mobMe.mobMeDie();
            }
            if (pl.playerSkill.isMonkey) {
                pl.playerSkill.monkeyDown();
            }
            pl.point.hp = 0;
            pl.point.mp = 0;
            if(pl.idNRSD != -1){
                pl.idNRSD = -1;
                Item item = ItemData.gI().get_item(pl.idNRSD);
                Service.gI().point(pl);
                Service.gI().sendBag(pl);
                ItemMap itemMap = new ItemMap(pl.zone, item.template, item.quantity, pl.x, pl.zone.LastY(pl.x, pl.y), -1);
                itemMap.options = item.itemOptions;
                Service.gI().roiItem(pl, itemMap);
                int flagID = Util.nextInt(1,7);
                Service.gI().chooseFlag(pl, flagID, true);
                if(pl.clan != null){
                    for(Player player : pl.zone.players){
                        if(player.clan != null && player.clan.id == pl.clan.id){
                            Service.gI().chooseFlag(player, flagID, true);
                        }
                    }
                }
            }
            if(pl.idNRNM != -1){
                NRNM.gI().SetPlayer(null, pl.idNRNM);
                pl.idNRNM = -1;
                Service.gI().sendBag(pl);
            }
            if (!pl.isPet) {
                msg = new Message(-17);
                msg.writer().writeByte((int)pl.typePk);
                msg.writer().writeShort(pl.x);
                msg.writer().writeShort(pl.y);
                msg.writer().writeLong(pl.point.power);
                pl.sendMessage(msg);
                msg.cleanup();
            }
            msg = new Message(-8);
            msg.writer().writeShort((int) pl.id);
            msg.writer().writeByte(0); //cpk
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            sendMessAnotherNotMeInMap(pl, msg);
            Send_Info_NV(pl);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void attackMob(Player pl, int mobId) {
        try {
            if(pl != null){
                pl.playerSkill.useSkill(null, pl.zone.mobs[mobId]);
            }
        } catch (Exception e) {
        }
    }

    public void Send_Caitrang(Player player) {
        Message msg = null;
        try {
//            if (player.itemsBody.get(5).id != -1) {
            msg = new Message(-90);
            msg.writer().writeByte(1);// check type
            msg.writer().writeInt((int) player.id); //id player
            msg.writer().writeShort(player.getHead());//set head
            msg.writer().writeShort(player.getBody());//set body
            msg.writer().writeShort(player.getLeg());//set leg
            msg.writer().writeByte(player.playerSkill.isMonkey ? 1 : 0);//set khỉ
            sendMessAllPlayerInMap(player.zone, msg);
//            }
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void Send_Body_Mob(Mob mob, int type, int idIcon) {
        Message msg = null;
        try
        {
            msg = new Message(-112);
            msg.writer().writeByte(type);
            msg.writer().writeByte(mob.id);
            if(type == 1)
            {
                msg.writer().writeShort(idIcon);//set body
            }
            sendMessAllPlayerInMap(mob.map, msg);
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

    public void sendThongBaoOK(Player pl, String text) {
        if (pl.isPet) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendThongBaoOK(Session session, String text) {
        Message msg = null;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendThongBao(Player pl, String thongBao) {
        Message msg = null;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendThongBaoAll(String thongBao) {
        Message msg = null;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            sendMessAllPlayer(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendMoney(Player pl) {
        Message msg = null;
        try {
            msg = new Message(6);
            if (pl.session.get_version() >= 214) {
                msg.writer().writeLong(pl.inventory.gold);
            } else {
                msg.writer().writeInt((int) pl.inventory.gold);
            }
            msg.writer().writeInt(pl.inventory.gem);
            msg.writer().writeInt(pl.inventory.ruby);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public synchronized void pickItem(Player pl)
    {
        try {
            synchronized (pl.zone.items) {
                List<ItemMap> itMap = new ArrayList<>();
                for (ItemMap itm : pl.zone.items) {
                    if (itm != null && itm.itemTemplate.type != 22) {
                        if (itm.playerId == pl.id && !NRSD.isNRSD(itm.itemTemplate.id) && NRNM.gI().findNRNM(itm.itemTemplate.id) == null && itm.itemTemplate.id != 568) {
                            itMap.add(itm);
                        }
                    }
                }
                for (ItemMap itm : itMap) {
                    if (itm.itemTemplate.id == 74) {
                        pl.point.hp = pl.point.getHPFull();
                        pl.point.mp = pl.point.getMPFull();
                        Service.gI().point(pl);
                        pickItem(pl, "", itm, 0);
                        pl.zone.items.remove(itm);
                        continue;
                    }
                    Item item = new Item(itm);
                    if (isItemMoney(itm.itemTemplate.type)) {
                        int sl = itm.quantity;
                        switch (itm.itemTemplate.type) {
                            case 9:            
                                if(pl.inventory.gold + sl > pl.inventory.LIMIT_GOLD){            
                                    pl.inventory.gold = pl.inventory.LIMIT_GOLD;            
                                }else{
                                    pl.inventory.gold += sl;
                                }            
                                if (pl.isPl()) {
                                    if(pl.taskOrder != null && pl.taskOrder.taskId == 2){
                                        if(pl.taskOrder.count < pl.taskOrder.maxCount * pl.taskOrder.killId){
                                            pl.taskOrder.count += sl;
                                            Service.gI().sendThongBao(pl, "Nhiệm vụ của bạn là " + pl.taskOrder.name + ". Tiến trình: " + pl.taskOrder.count +" / " + pl.taskOrder.maxCount);
                                        }else{
                                            pl.taskOrder.count = pl.taskOrder.maxCount * pl.taskOrder.killId;
                                            Service.gI().sendThongBao(pl, "Đã hoàn thành nhiệm vụ!Đến với Bò mộng báo cáo ngay nào");
                                        }
                                        Service.gI().send_task_orders(pl, pl.taskOrder);
                                    }
                                }
                                break;
                            case 10:
                                pl.inventory.gem += sl;
                                break;
                            case 24:
                                pl.inventory.ruby += sl;
                                break;
                            default:
                                break;
                        }
                        pickItem(pl, "", itm, sl);
                        Service.gI().sendMoney(pl);
                        pl.zone.items.remove(itm);
                        continue;
                    }
                    if (pl.inventory.addItemBag(item))
                    {
                        if (pl.isPl())
                        {
                            Task task = TaskData.getTask(pl.taskId);
                            if(task != null && (
                                    (pl.taskId == 2 && pl.taskIndex == 0 && item.template.id == 73) || 
                                    (pl.taskId == 8 && pl.taskIndex == 1 && item.template.id == 20) || 
                                    (pl.taskId == 14 && pl.taskIndex == 1 && item.template.id == 85) || 
                                    (pl.taskId == 31 && pl.taskIndex == 0 && item.template.id == 992) || 
                                    (pl.taskId == 31 && pl.taskIndex == 7 && pl.zone.isHTTV() && item.template.id == 993) || 
                                    (pl.taskId == 29 && pl.taskIndex == 1 && item.template.id == 380)))
                            {
                                if (task.counts[pl.taskIndex] != -1)
                                {
                                    pl.taskCount += 1;
                                    if (pl.taskCount < task.counts[pl.taskIndex])
                                    {
                                        Service.gI().send_task(pl, task);
                                    }
                                    else
                                    {
                                        Service.gI().send_task_next(pl);
                                    }
                                }
                            }
                        }
                        pickItem(pl, "", itm, 0);
                        pl.zone.items.remove(itm);
                    } 
                }
            }
        } catch (Exception e) {
            if(pl.role >= 99){
                Util.log("Name: " + pl.name + "\n");
                e.printStackTrace();
                Util.log("\n------------------------\n");
            }
        }
    }

    public synchronized void pickItem(Player pl, int itemMapId) {
        try {
            if(pl.isDie() || !Util.canDoWithTime(pl.LastTimePickItem, 500)){
                return;
            }
            pl.LastTimePickItem = System.currentTimeMillis();
            synchronized (pl.zone.items)
            {
                if (MapManager.gI().isMapHome(pl) && pl.taskId > 2)
                {
                    pl.point.hp = pl.point.getHPFull();
                    pl.point.mp = pl.point.getMPFull();
                    Service.gI().point(pl);
                    Service.gI().sendThongBao(pl, "Bạn vừa ăn đùi gà nướng");
                    ItemMap.removeItemMap(pl);
                    return;
                }
                if (itemMapId == -78 && pl.taskId == 3 && pl.taskIndex == 1)
                {
                    ItemMap.removeItemMap(pl, -78);
                    Service.gI().send_task_next(pl);
                    Service.gI().sendBag(pl);
                    return;
                }
                for (int i = 0; i < pl.zone.items.size(); i++)
                {
                    ItemMap itm = pl.zone.items.get(i);
                    if (itm != null && itm.itemMapId == itemMapId && itm.itemTemplate.type != 22 && Util.getDistance(pl.x, pl.y, itm.x, itm.y) <= 50) {
                        if (itm.playerId == pl.id || itm.playerId == -1)
                        {
                            if (itm.itemTemplate.id == 74)
                            {
                                pl.point.hp = pl.point.getHPFull();
                                pl.point.mp = pl.point.getMPFull();
                                Service.gI().point(pl);
                                pickItem(pl, "", itm, 0);
                                pl.zone.items.remove(i);
                            }
                            else if(itm.itemTemplate.id == 568)
                            {
                                pl.mabuEgg = new MabuEgg(pl,System.currentTimeMillis(),172800);
                                pickItem(pl, "", itm, 0);
                                pl.zone.items.remove(i);
                            }
                            else if(NRNM.gI().findNRNM(itm.itemTemplate.id) != null)
                            {
                                if(pl.idNRNM != -1){
                                    sendThongBao(pl, "Chỉ có thể vác 1 viên");
                                    return;
                                }
                                NRNM.gI().SetPlayer(pl, itm.itemTemplate.id);
                                pickItem(pl, "", itm, 0);
                                pl.zone.items.remove(i);
                            }
                            else if(NRSD.isNRSD(itm.itemTemplate.id))
                            {
                                int timeLeft = (int)((Setting.TIME_PICK - DHVT.gI().Minutes) * 60) - DHVT.gI().Second;
                                boolean isPick = Util.canDoWithTime(itm.createTime, 3000);
                                if(timeLeft <= 0 && isPick){
                                    pl.idNRSD = itm.itemTemplate.id;
                                    Service.gI().sendBag(pl);
                                    pl.currHaveNRSD = System.currentTimeMillis();
                                    Service.gI().chooseFlag(pl, 8, true);
                                    if(pl.clan != null){
                                        for(Player player : pl.zone.players){
                                            if(player.clan != null && player.clan.id == pl.clan.id){
                                                Service.gI().chooseFlag(player, 8, true);
                                            }
                                        }
                                    }
                                    pickItem(pl, "", itm, 0);
                                    pl.zone.items.remove(i);
                                }
                                else if(timeLeft > 0){
                                    Service.gI().sendThongBao(pl, "Vui lòng đợi " + timeLeft + " giây nữa");
                                }
                                else if (!isPick) {
                                    Service.gI().sendThongBao(pl, "Vui lòng đợi " + (3 - (System.currentTimeMillis() - itm.createTime) / 1000) + " giây nữa");
                                }
                            }
                            else
                            {
                                if(pl.getBagNull() <= 0){
                                    Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ");
                                    return;
                                }
                                Item item = new Item(itm);
                                if (isItemMoney(itm.itemTemplate.type)) {
                                    int sl = itm.quantity;
                                    switch (itm.itemTemplate.type) {
                                        case 9:
                                            if(pl.inventory.gold + sl > pl.inventory.LIMIT_GOLD){
                                                pl.inventory.gold = pl.inventory.LIMIT_GOLD;
                                            }else{
                                                pl.inventory.gold += sl;
                                            }
                                            if (pl.isPl()) {
                                                if(pl.taskOrder != null && pl.taskOrder.taskId == 2){
                                                    if(pl.taskOrder.count < pl.taskOrder.maxCount * pl.taskOrder.killId){
                                                        pl.taskOrder.count += sl;
                                                        Service.gI().sendThongBao(pl, "Nhiệm vụ của bạn là " + pl.taskOrder.name + ". Tiến trình: " + pl.taskOrder.count +" / " + pl.taskOrder.maxCount);
                                                    }else{
                                                        pl.taskOrder.count = pl.taskOrder.maxCount * pl.taskOrder.killId;
                                                        Service.gI().sendThongBao(pl, "Đã hoàn thành nhiệm vụ!Đến với Bò mộng báo cáo ngay nào");
                                                    }
                                                    Service.gI().send_task_orders(pl, pl.taskOrder);
                                                }
                                            }
                                            break;
                                        case 10:
                                            pl.inventory.gem += sl;
                                            break;
                                        case 24:
                                            pl.inventory.ruby += sl;
                                            break;
                                        default:
                                            break;
                                    }
                                    Service.gI().sendMoney(pl);
                                    pickItem(pl, "", itm, sl);
                                    pl.zone.items.remove(i);
                                }
                                else if (pl.inventory.addItemBag(item))
                                {
                                    if (pl.isPl())
                                    {
                                        Task task = TaskData.getTask(pl.taskId);
                                        if(task != null && (
                                                (pl.taskId == 2 && pl.taskIndex == 0 && item.template.id == 73) || 
                                                (pl.taskId == 8 && pl.taskIndex == 1 && item.template.id == 20) || 
                                                (pl.taskId == 14 && pl.taskIndex == 1 && item.template.id == 85) || 
                                                (pl.taskId == 31 && pl.taskIndex == 0 && item.template.id == 992) || 
                                                (pl.taskId == 31 && pl.taskIndex == 7 && pl.zone.isHTTV() && item.template.id == 993) || 
                                                (pl.taskId == 29 && pl.taskIndex == 1 && item.template.id == 380)))
                                        {
                                            if (task.counts[pl.taskIndex] != -1) {
                                                pl.taskCount += 1;
                                                if (pl.taskCount < task.counts[pl.taskIndex])
                                                {
                                                    Service.gI().send_task(pl, task);
                                                }
                                                else
                                                {
                                                    Service.gI().send_task_next(pl);
                                                }
                                            }
                                        }
                                    }
                                    pickItem(pl, "", itm, 0);
                                    pl.zone.items.remove(i);
                                } 
                            }
                        }
                        else {
                            sendThongBao(pl, "Không thể nhặt vật phẩm của người khác");
                        }
                        return;
                    }
                }
            }
        } catch (Exception e) {
            if(pl.role == 99){
                Util.log("Name: " + pl.name + "\n");
                e.printStackTrace();
                Util.log("\n------------------------\n");
            }
        }
    }

    public void pickItem(Player pl, String text, ItemMap itm, int sl) {
        Message msg = null;
        try {
            pl.inventory.sendItemBags();
            msg = new Message(-20);
            msg.writer().writeShort(itm.itemMapId);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(sl);
            pl.sendMessage(msg);
            sendToAntherMePickItem(pl, itm.itemMapId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void pickItem(Player pl, String text, ItemMap itm){
        Message msg = null;
        try {
            msg = new Message(-20);
            msg.writer().writeShort(itm.itemMapId);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(1);
            sendMessAnotherNotMeInMap(pl,msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendToAntherMePickItem(Player player, int itemMapId) {
        Message msg = null;
        try {
            msg = new Message(-19);
            msg.writer().writeShort(itemMapId);
            msg.writer().writeInt((int) player.id);
            this.sendMessAnotherNotMeInMap(player, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public boolean isItemMoney(int type) {
        return type == 9 || type == 10 || type == 34;
    }

    public static String numberToMoney(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public void useSkillNotFocus(Player pl, int status) {
        if(pl != null && pl.isPl() && status != 20)
        {
            pl.playerSkill.useSkill(null, null);
        }
        else
        {
            pl.playerSkill.useSkill(null, null);
        }
    }

    public synchronized void chatGlobal(Player pl, String text) {
        if (pl.role < Setting.ROLE_ADMIN && Setting.LOG_CHAT_GLOBAL) {
            sendThongBao(pl, "Đã đóng");
            return;
        }
        Message msg = null;
        try {
            for (Player player : PlayerManger.gI().getPlayers()) {
                if (player.session != null && player != null && !player.isPet && !player.isBoss && !player.isNewPet) {
                    msg = new Message(92);
                    msg.writer().writeUTF(pl.name);
                    msg.writer().writeUTF("|5|" + text);
                    msg.writer().writeInt((int) pl.id);
                    msg.writer().writeShort(pl.getHead());
                    if(player.session.get_version() > 214){
                        msg.writer().writeShort(-1);
                    }
                    msg.writer().writeShort(pl.getBody());
                    msg.writer().writeShort(pl.get_bag());
                    msg.writer().writeShort(pl.getLeg());
                    msg.writer().writeByte(0);
                    player.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            Util.debug("chatGlobal");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public static final int[] flagTempId = {363, 364, 365, 366, 367, 368, 369, 370, 371, 519, 520, 747};
    public static final int[] flagIconId = {2761, 2330, 2323, 2327, 2326, 2324, 2329, 2328, 2331, 4386, 4385, 2325};
    
    public void sendFlagUI(Player pl, byte flag) {
        Message msg = null;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(2);
            msg.writer().writeByte(flag);
            msg.writer().writeShort(flagIconId[flag]);
            Service.gI().sendMessAllPlayer(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void openFlagUI(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(0);
            msg.writer().writeByte(flagTempId.length);
            for (int i = 0; i < flagTempId.length; i++) {
                msg.writer().writeShort(flagTempId[i]);
                msg.writer().writeByte(1);
                switch (flagTempId[i]) {
                    case 363:
                        msg.writer().writeByte(73);
                        msg.writer().writeShort(0);
                        break;
                    case 371:
                        msg.writer().writeByte(88);
                        msg.writer().writeShort(10);
                        break;
                    default:
                        msg.writer().writeByte(88);
                        msg.writer().writeShort(5);
                        break;
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void chooseFlag(Player pl, int index, boolean... auto)
    {
        if(pl.zone.isNRSD() && auto.length <= 0)
        {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if(pl.zone.isOSIN() && auto.length <= 0)
        {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if(index >= 9 && index <= 10)
        {
            return;
        }
        if (System.currentTimeMillis() - pl.lastTimeChangeFlag > 60000 || auto.length > 0)
        {
            Message msg = null;
            try
            {
                pl.cFlag = (byte) index;
                msg = new Message(-103);
                msg.writer().writeByte(1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeByte(index);
                Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
                msg.cleanup();

                msg = new Message(-103);
                msg.writer().writeByte(2);
                msg.writer().writeByte(index);
                msg.writer().writeShort(flagIconId[index]);
                Service.gI().sendMessAllPlayer(msg);
                msg.cleanup();

                if (pl.pet != null) {
                    pl.pet.cFlag = (byte) index;
                    msg = new Message(-103);
                    msg.writer().writeByte(1);
                    msg.writer().writeInt((int) pl.pet.id);
                    msg.writer().writeByte(index);
                    Service.gI().sendMessAllPlayerInMap(pl.pet.zone, msg);
                    msg.cleanup();

                    msg = new Message(-103);
                    msg.writer().writeByte(2);
                    msg.writer().writeByte(index);
                    msg.writer().writeShort(flagIconId[index]);
                    Service.gI().sendMessAllPlayerInMap(pl.pet.zone, msg);
                    msg.cleanup();
                }
                pl.lastTimeChangeFlag = System.currentTimeMillis();
            } catch (Exception e) {
//                e.printStackTrace();
            } finally {
                if (msg != null) {
                    msg.cleanup();
                    msg = null;
                }
            }
        }
        else
        {
            sendThongBao(pl, "Vui lòng đợi " + (60 - ((int) (System.currentTimeMillis() - pl.lastTimeChangeFlag) / 1000)) + " giây nữa");
        }
    }
    

    public void attackPlayer(Player pl, int idPlAnPem) {
        if(pl != null){
            pl.playerSkill.useSkill(pl.zone.getPlayerInMap(idPlAnPem), null);
        }
    }

    public void openZoneUI(Player pl) {
        if (pl.isDie()) {
            return;
        }
        if (MapManager.gI().isMapOffline(pl.zone.map.id) || pl.zone.isPhuBan()) {
            sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
            return;
        }
        Message msg = null;
        try {
            msg = new Message(29);
            Map maps = MapManager.gI().getMapById(pl.zone.map.id);
            msg.writer().writeByte(maps.map_zone.length);
            for (Zone map : maps.map_zone) {
                msg.writer().writeByte(map.zoneId);
                int numPlayers = map.getNumPlayerInMap();
                msg.writer().writeByte((numPlayers < 5 ? 0 : (numPlayers < 8 ? 1 : 2)));
                msg.writer().writeByte(numPlayers);
                msg.writer().writeByte(map.map.maxPlayer);
                msg.writer().writeByte(0);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void requestChangeZone(Player pl, int zoneId) {
        if (MapManager.gI().isMapOffline(pl.zone.map.id) || pl.zone.isPhuBan()) {
            Service.gI().sendPopUpMultiLine(pl, 6, 0, "Không thể thực hiện");
            return;
        }
        long zones = (System.currentTimeMillis() - pl.LastTimeZone) / 1000;
        if (!Util.canDoWithTime(pl.LastTimeZone, 10000) && pl.role < Setting.ROLE_ADMIN) {
            Service.gI().sendPopUpMultiLine(pl, 6, 0, "Chưa thể chuyển khu lúc này Vui lòng chờ " + (10 - zones) + " giây nữa");
            return;
        }
        pl.LastTimeZone = System.currentTimeMillis();
//        if(Setting.HO_TRO_TDST && pl.taskId != 19 && BossManager.isTDST && pl.zone.map.id == BossManager.mapTDST && zoneId == BossManager.zoneTDST && pl.role != Setting.ROLE_ADMIN){
//            sendThongBao(pl, "Không thể vào khu vì đang trong thời gian hỗ trợ");
//            return;
//        }
        Zone map = MapManager.gI().getMap(pl.zone.map.id, zoneId);
        if (map.getNumPlayerInMap() >= map.map.maxPlayer && pl.role != Setting.ROLE_ADMIN) {
            Service.gI().sendPopUpMultiLine(pl, 6, 0, "Khu vực đã đầy");
            return;
        }
        if (pl.playerSkill.count_ttnl != -1) {
            pl.playerSkill.stopCharge();
        }
        if (pl.playerSkill.useTroi) {
            pl.playerSkill.removeUseTroi();
        }
        ChangeMap.gI().changeMap(pl, map, pl.x, pl.y);
    }

    public void HoiSkill(Player pl, short id, int cooldown) {
        Message msg = null;
        try {
            msg = new Message(-94);
            msg.writer().writeShort(id);
            msg.writer().writeInt(cooldown);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    int lasttimeDrop;
    public void DropVeTinh(Player pl , Item item, Zone map, int x, int y,int time) {
        ItemMap itemMap = new ItemMap(map, item.template, item.quantity, x, y, pl.id);
        itemMap.options = item.itemOptions;
        map.addItem(itemMap);
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(itemMap.itemMapId);
            msg.writer().writeShort(itemMap.itemTemplate.id);
            msg.writer().writeShort(itemMap.x);
            msg.writer().writeShort(itemMap.y);
            msg.writer().writeInt(-2);//
            msg.writer().writeShort(200);
            sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
        if ((System.currentTimeMillis() - lasttimeDrop) > time){
           map.removeItemMap(itemMap); 
        }
    }
   

    public void roiItem(Item item, Zone map, int x, int y, int id) {
        ItemMap itemMap = new ItemMap(map, item.template, item.quantity, x, y, id);
        itemMap.options = item.itemOptions;
        map.addItem(itemMap);
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(itemMap.itemMapId);
            msg.writer().writeShort(itemMap.itemTemplate.id);
            msg.writer().writeShort(itemMap.x);
            msg.writer().writeShort(itemMap.y);
            msg.writer().writeInt(id);//
            if(id == -2){
                msg.writer().writeShort(200);
            }
            sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void roiItem(Player pl, ItemMap item) {
        pl.zone.addItem(item);
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt(pl.id);
            sendMessAllPlayerInMap(pl.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    } // sendMessAllPlayerIgnoreMe
    
    public void roiItemNotMe(Player pl, ItemMap item) {
        pl.zone.addItem(item);
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt(pl.id);
            sendMessAllPlayerNotMeInMap(pl.zone, pl, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    } // sendMessAllPlayerIgnoreMe

    public void showInfoPet(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-107);
            if (pl.pet == null) {
                msg.writer().writeByte(0);
                pl.sendMessage(msg);
                msg.cleanup();
                return;
            }

            msg.writer().writeByte(2);
            msg.writer().writeShort(pl.pet.getAvatar());
            msg.writer().writeByte(pl.pet.inventory.itemsBody.size());

            for (Item item : pl.pet.inventory.itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo(pl));
                    msg.writer().writeUTF(item.getContent());

                    int countOption = item.itemOptions.size();
                    msg.writer().writeByte(countOption);
                    for (ItemOption iop : item.itemOptions) {
                        msg.writer().writeByte(iop.optionTemplate.id);
                        msg.writer().writeShort(iop.param);
                    }
                }
            }

            msg.writer().writeInt(pl.pet.point.getHP()); //hp
            msg.writer().writeInt(pl.pet.point.getHPFull()); //hpfull
            msg.writer().writeInt(pl.pet.point.getMP()); //mp
            msg.writer().writeInt(pl.pet.point.getMPFull()); //mpfull
            msg.writer().writeInt(pl.pet.point.getBaseDame()); //damefull
            msg.writer().writeUTF(pl.pet.name); //name
            msg.writer().writeUTF(exp_level(pl.pet)); //curr level
            msg.writer().writeLong(pl.pet.point.getPower()); //power
            msg.writer().writeLong(pl.pet.point.getTiemNang()); //tiềm năng
            msg.writer().writeByte(pl.pet.getStatus()); //status
            msg.writer().writeShort(pl.pet.point.stamina); //stamina
            msg.writer().writeShort(Setting.MAX_STAMINA_FOR_PET); //stamina full
            msg.writer().writeByte(pl.pet.point.getCritFull()); //crit
            msg.writer().writeShort(pl.pet.point.getDefFull()); //def
            int sizeSkill = pl.pet.playerSkill.skills.size();
            msg.writer().writeByte(sizeSkill + (4 - sizeSkill)); //counnt pet skill
            for (Skill skill : pl.pet.playerSkill.skills) {
                msg.writer().writeShort(skill.skillId);
            }
            if (sizeSkill < 2) {
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Cần đạt sức mạnh 150tr để mở");
            }
            if (sizeSkill < 3) {
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Cần đạt sức mạnh 1tỷ5 để mở");
            }
            if (sizeSkill < 4) {
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Cần đạt sức mạnh 20tỷ để mở");
            }

            pl.sendMessage(msg);

        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendTextTime(Player pl, int itemId, String text, int time) {
        Message msg = null;
        try {
            msg = new Message(65);
            msg.writer().writeByte(itemId);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(time);
            this.sendMessClanAllPlayer(msg, pl);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void removeTextTime(Player pl, int itemId) {
        sendTextTime(pl, itemId, "", 0);
    }

    public void sendItemTime(Player pl, int itemId, int time) {
        Message msg = null;
        try {
            msg = new Message(-106);
            msg.writer().writeShort(itemId);
            msg.writer().writeShort(time);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void removeItemTime(Player pl, int itemTime) {
        sendItemTime(pl, itemTime, 0);
    }

    public void sendSpeedPlayer(Player pl, int speed) {
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte) 8);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(speed != -1 ? speed : pl.point.getSpeed());
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendUpdateSkillPlayer(Player pl, Skill skill) {
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte)62);
            msg.writer().writeShort(skill.skillId);
            msg.writer().writeByte(0);
            msg.writer().writeShort(skill.curExp);
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendSkillPlayer(Player pl, Skill skill) {
        Message msg = null;
        try {
            msg = Service.gI().messageSubCommand((byte)23);
            msg.writer().writeShort(skill.skillId);
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void setPos(Player player, int x, int y, int...type) {
        Message msg = null;
        try {
            msg = new Message(123);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeByte(type.length);
            sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendInfoChar30c4(Player player) {
        Message msg = null;
        try {
            msg = messageSubCommand((byte) 4);
            if (player.session.get_version() >= 214) {
                msg.writer().writeLong(player.inventory.gold);//xu
            } else {
                msg.writer().writeInt((int) player.inventory.gold);//xu
            }
            msg.writer().writeInt(player.inventory.gem);
            msg.writer().writeInt(player.point.getHP());
            msg.writer().writeInt(player.point.getMP());
            msg.writer().writeInt(player.inventory.ruby);
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void changeTypePK(Player player, int type) {
        player.typePk = (byte)type;
        Message msg = null;
        try {
            msg = messageSubCommand((byte) 35);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(type);
            sendMessAllPlayerInMap(player.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void getPlayerMenu(Player player, int playerId) {
        Message msg = null;
        try {
            msg = new Message(-79);
            Player pl = player.zone.getPlayerInMap(playerId);
            if (pl != null) {
                msg.writer().writeInt(playerId);
                msg.writer().writeLong(pl.point.getPower());
                msg.writer().writeUTF(Service.gI().exp_level(pl));
            }
            player.sendMessage(msg);

        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void CaptionStr(Session session) {
        Message msg = null;
        try {
            msg = new Message(-41);
            msg.writer().writeByte(Service.gI().ListCaption(session.player).size());
            for (int i = 0; i < Service.gI().ListCaption(session.player).size(); i++) {
                msg.writer().writeUTF(Service.gI().ListCaption(session.player).get(i));
            }
            session.player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public List<String> ListCaption(Player pl) {
        List<String> Captions = new ArrayList<>();
        Captions.add("Tân thủ");
        Captions.add("Tập sự sơ cấp");
        Captions.add("Tập sự trung cấp");
        Captions.add("Tập sự cao cấp");
        Captions.add("Tân binh");
        Captions.add("Chiến binh");
        Captions.add("Chiến binh cao cấp");
        Captions.add("Vệ binh");
        Captions.add("Vệ binh hoàng gia");
        Captions.add("Siêu " + get_HanhTinh(pl.gender) + " cấp 1");
        Captions.add("Siêu " + get_HanhTinh(pl.gender) + " cấp 2");
        Captions.add("Siêu " + get_HanhTinh(pl.gender) + " cấp 3");
        Captions.add("Siêu " + get_HanhTinh(pl.gender) + " cấp 4");
        Captions.add("Thần " + get_HanhTinh(pl.gender, 1) + " cấp 1");
        Captions.add("Thần " + get_HanhTinh(pl.gender, 2) + " cấp 2");
        Captions.add("Thần " + get_HanhTinh(pl.gender, 3) + " cấp 3");
        Captions.add("Giới Vương Thần cấp 1");
        Captions.add("Giới Vương Thần cấp 2");
        Captions.add("Giới Vương Thần cấp 3");
        Captions.add("Thần hủy diệt cấp 1");
        Captions.add("Thần hủy diệt cấp 2");
        return Captions;
    }

    public void sendBag(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-64);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(pl.get_bag());
            this.sendMessAllPlayerInMap(pl.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void hideInfoDlg(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-99);
            msg.writer().writeByte(-1);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendThongBaoBenDuoi(String text) {
        Message msg = null;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text.replace("$", ""));
            sendMessAllPlayer(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void chatPrivate(Player plChat, Player plReceive, String text) {
        Message msg = null;
        try {
            msg = new Message(92);
            msg.writer().writeUTF(plChat.name);
            msg.writer().writeUTF("|5|" + text);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeShort(plChat.getHead());
            if(plChat.session.get_version() > 214){
                msg.writer().writeShort(-1);
            }
            msg.writer().writeShort(plChat.getBody());
            msg.writer().writeShort(plChat.get_bag());
            msg.writer().writeShort(plChat.getLeg());
            msg.writer().writeByte(1);
            plChat.sendMessage(msg);
            // Receive
            msg = new Message(92);
            msg.writer().writeUTF(plChat.name);
            msg.writer().writeUTF("|5|" + text);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeShort(plChat.getHead());
            if(plReceive.session.get_version() > 214){
                msg.writer().writeShort(-1);
            }
            msg.writer().writeShort(plChat.getBody());
            msg.writer().writeShort(plChat.get_bag());
            msg.writer().writeShort(plChat.getLeg());
            msg.writer().writeByte(1);
            plReceive.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

}
