package real.func;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import real.clan.Clan;
import real.clan.ClanDAO;
import real.clan.ClanManager;
import real.clan.Member;
import real.player.Player;
import service.DAOS.PlayerDAO;
import real.player.PlayerManger;
import real.task.TaskData;
import server.SQLManager;
import server.ServerManager;
import server.Service;
import server.Util;
import service.Chat;
import service.Setting;

public class DHVT {
    private static DHVT instance;
    public long currGC = System.currentTimeMillis();
    public long currLoad = System.currentTimeMillis();
    public long currNEXT = System.currentTimeMillis();
    public boolean onload;
    public static DHVT gI() {
        if (instance == null) {
            instance = new DHVT();
        }
        return instance;
    }
    public ArrayList<Player> listReg;
    public ArrayList<String> listThongBao;

    public String NameCup;
    public String[] Time;
    public int gem;
    public int gold;
    public int min_start;
    public int min_limit;
    public ArrayList<DHVT> listCup;
    public int round = 1;

    public int Year;
    public int Month;
    public int Day;
    public int Hour;
    public int Minutes;
    public int Second;

    public DHVT() {
        listReg = new ArrayList<>();
        listCup = new ArrayList<>();
        listThongBao = new ArrayList<>();
    }
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public void close() {
        try {
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
                    DHVT.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }

    public void loadCup() {
        try {
            listCup.clear();
            ResultSet rs = SQLManager.executeQueryDATA("SELECT * FROM dhvt_template");
            if (rs.next()) {
                DHVT dhvt = new DHVT();
                dhvt.NameCup = rs.getString(2);
                dhvt.Time = rs.getString(3).split("\n");
                dhvt.gem = rs.getInt(4);
                dhvt.gold = rs.getInt(5);
                dhvt.min_start = rs.getInt(6);
                dhvt.min_limit = rs.getInt(7);
                listCup.add(dhvt);
            }
            rs.close();
            rs = null;
            Util.warning("Finish load cup! [" + listCup.size() + "]\n");
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public String Giai(Player pl) {
        for (DHVT dh : listCup) {
            if (PlayerExits(pl.id)) {
                return "Đại hội võ thuật sẽ bắt đầu sau " + (dh.min_start - Minutes) + " phút nữa";
            } else if (Util.contains(dh.Time, String.valueOf(Hour)) && Minutes <= dh.min_limit) {
                return "Chào mừng bạn đến với đại hội võ thuật\nGiải " + dh.NameCup + " đang có " + listReg.size() + " người đăng ký thi đấu";
            }
        }
        return "Đã hết thời gian đăng ký vui lòng đợi đến giải đấu sau";
    }

    public boolean CanReg(Player pl) {
        return listCup.stream().anyMatch(dh -> Util.contains(dh.Time, String.valueOf(Hour)) && Minutes <= dh.min_limit && !PlayerExits(pl.id));
    }

    public boolean PlayerExits(int id) {
        return listReg.stream().anyMatch(pl -> pl.id == id);
    }

    public String Info() {
        for (DHVT dh : listCup) {
            if (dh.gold > 0) {
                return "Lịch thi đấu trong ngày\bGiải " + dh.NameCup + ": " + Arrays.toString(dh.Time).replace("[", "").replace("]", "") + "h\nLệ phí đăng ký thi đấu\bGiải " + dh.NameCup + ": " + Util.powerToString(dh.gold) + " vàng\b";
            } else if (dh.gem > 0) {
                return "Lịch thi đấu trong ngày\bGiải " + dh.NameCup + ": " + Arrays.toString(dh.Time).replace("[", "").replace("]", "") + "h\nLệ phí đăng ký thi đấu\bGiải " + dh.NameCup + ": " + Util.powerToString(dh.gem) + " ngọc\b";
            }
        }
        return "Không có giải đấu nào được tổ chức\b";
    }

    public void Reg(Player player) {
        boolean isReg = false;
        for (DHVT dh : listCup) {
            if (dh.gem > 0) {
                if (player.inventory.ruby >= dh.gem) {
                    player.inventory.ruby -= dh.gem;
                    isReg = true;
                } else if (player.inventory.gem >= dh.gem) {
                    player.inventory.gem -= dh.gem;
                    isReg = true;
                } else {
                    Service.gI().sendThongBao(player, "Bạn Không Đủ Ngọc Để Đăng Ký");
                    isReg = false;
                }
            } else if (dh.gold > 0) {
                if (player.inventory.gold >= dh.gold) {
                    player.inventory.gold -= dh.gold;
                    isReg = true;
                } else {
                    Service.gI().sendThongBao(player, "Bạn Không Đủ Vàng Để Đăng Ký");
                    isReg = false;
                }
            } else {
                Service.gI().sendThongBao(player, "Bạn Không Thể Đăng Ký Giải Đấu Này");
            }
        }
        if (isReg) {
            listReg.add(player);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Bạn đã đăng ký thành công!Vui lòng không rời khỏi đại hội võ thuật để tránh bị tước quyền thi đấu!!");
        }
    }

    public void Update(Player me) {
        for (DHVT dh : listCup)
        {
            if (Second == 0 && Minutes < dh.min_start)
            {
                Service.gI().sendThongBao(me, "Vòng " + round + " sẽ bắt đầu sau " + (dh.min_start - Minutes) + " phút nữa");
            }
            else if(Minutes >= dh.min_start && !Util.canDoWithTime(dh.currNEXT, 90000)){
                Service.gI().sendThongBao(me, "Vòng " + round + " sẽ bắt đầu sau " + (90 - (System.currentTimeMillis() - dh.currNEXT) / 1000) + " giây nữa");
            }
            else if (Minutes >= dh.min_start && Util.canDoWithTime(dh.currNEXT, 90000) && (me.zone.map.id == 52 || me.zone.map.id == 51))
            {
                if (listReg.size() > 1)
                {
                    List<Player> listPlayer = FindPlayer(me);
                    Player PlayerPK = listPlayer.get(Util.nextInt(0, listPlayer.size() - 1));
                    if(DHVT_Template.gI().findPK_DHVT(PlayerPK) == null && DHVT_Template.gI().findPK_DHVT(me) == null)
                    {
                        DHVT_Template dhvt_tem = new DHVT_Template(me, PlayerPK);
                        DHVT_Template.gI().PK_DHVT.add(dhvt_tem);
                        listReg.remove(me);
                        listReg.remove(PlayerPK);
                        dhvt_tem.DHVT_START.active(1000);
                    }
                }
                else if(listReg.size() == 1 && DHVT_Template.gI().PK_DHVT.size() <= 0)
                {
                    Player playerPK = listReg.get(0);
                    Service.gI().sendThongBaoAll(playerPK.name + " đã vô địch giải " + dh.NameCup);
                    listReg.remove(playerPK);
                    round = 1;
                }
                else if(listReg.size() == 1 && DHVT_Template.gI().PK_DHVT.size() > 0){
                    Service.gI().sendThongBao(me, "Bạn méo có đối thủ lên trực tiếp vào vòng trong :D");
                }
            }
            else if (Minutes >= dh.min_start && (me.zone.map.id != 52 || me.zone.map.id != 51)) {
                Service.gI().sendThongBao(me, "Bạn đã bị tước quyền thi đấu do không có mặt kịp giờ");
                listReg.remove(me);
            }
            if(Minutes >= dh.min_start && Util.canDoWithTime(dh.currNEXT, 90000)){
                dh.currNEXT = System.currentTimeMillis();
                round++;
            }
        }
    }
    
    public List<Player> FindPlayer(Player player)
    {
        return listReg.stream().filter(pl -> pl != null && pl != player).toList();
    }

    public void update()
    {
        if (true)
        {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            try {
                Year = calendar.get(Calendar.YEAR);
                Month = calendar.get(Calendar.MONTH) + 1;
                Day = calendar.get(Calendar.DAY_OF_MONTH);
                Hour = calendar.get(Calendar.HOUR_OF_DAY);
                Minutes = calendar.get(Calendar.MINUTE);
                Second = calendar.get(Calendar.SECOND);
                if (Util.canDoWithTime(currGC, 18000000))
                {
                    int numPlayer = PlayerManger.gI().getPlayers2().size();
                    Util.success("PLAYER ONLINE HIỆN TẠI: " + numPlayer + "\n");
                    System.gc();
                    currGC = System.currentTimeMillis();
                }
                if(Hour >= 17 && Hour <= 19)
                {
                    Setting.HO_TRO_TDST = true;
                }
                else
                {
                    Setting.HO_TRO_TDST = false;
                }
                if(Hour == Setting.TIME_BAOTRI && Minutes == 0 && !Chat.isBaoTri)
                {
                    Chat.isReset = true;
                    Chat.isBaoTri = true;
                    Chat.minuteBaoTri = Setting.TIME_TICK;
                }
                if (Chat.isBaoTri && Second == 0 && Chat.minuteBaoTri > 0) {
                    Chat.minuteBaoTri--;
                    Util.error("Hệ thống sẽ bảo trì sau " + Chat.minuteBaoTri + " phút\n");
                    Service.gI().sendThongBaoAll("Hệ thống sẽ bảo trì sau " + Chat.minuteBaoTri + " phút");
                }
                int size = listThongBao.size();
                if(size > 0){
                    Service.gI().sendThongBaoBenDuoi(listThongBao.get(0));
                    listThongBao.remove(0);
                }
                if(Chat.isBaoTri && Chat.minuteBaoTri <= 0 || Util.existsFile("BaoTri.txt"))
                {
                    for (Player player : PlayerManger.gI().getPlayers2())
                    {
                        if (player != null)
                        {
                            player.session.disconnect();
                        }
                    }
                }
                if(PlayerManger.gI().getPlayers2().isEmpty() && Chat.isBaoTri && Chat.minuteBaoTri <= 0 || Util.existsFile("BaoTri.txt") && PlayerManger.gI().getPlayers2().isEmpty())
                {
                    if(Util.existsFile("BaoTri.txt"))
                    {
                        File myObj = new File("BaoTri.txt"); 
                        myObj.delete();
                    }
                    BaoTri();
                }
                TopInfo();
                ClanInfo();
            } catch (Exception e) {
                Util.logException(DHVT.class, e);
            }
        }
    }
    
    public void TopInfo(){
        try{
            if(Util.canDoWithTime(currLoad, 600000) || !onload){
                SQLManager.createFROM();
                currLoad = System.currentTimeMillis();
                List<TopInfo> TOP = new ArrayList();
                TOP.clear();
                ResultSet rs = null;
                rs = SQLManager.executeQueryFROM("SELECT `total_money`,`id` FROM `account` WHERE `total_money` > '0' AND `active` > '0' ORDER BY total_money DESC LIMIT 100");
               // rs = SQLManager.executeQueryFROM("SELECT `total_money`,`id` FROM `account` WHERE `total_money` > '0' AND `active` > '0' ORDER BY total_money DESC LIMIT 100");
                if(rs != null){
                    while(rs.next()){
                        int id = rs.getInt("id");
                        int money = rs.getInt("total_money");
                        Player pl = PlayerDAO.getPlayerbyUserID(id);
                        if(pl != null){
                            TopInfo top = new TopInfo();
                            top.headID = pl.getHead();
                            top.body = pl.getBody();
                            top.leg = pl.getLeg();
                            top.name = pl.name;
                            top.pId = pl.id;
                            top.money = money;
                            top.info = "Tổng: " + Util.getMoneys((long)money) +" COIN ";
                            top.curr = Util.currentTimeSec();
                            if(!TOP.stream().anyMatch(p -> p != null && p.name.equals(pl.name))){
                                TOP.add(top);
                            }
                        }
                    }
                }
                rs.close();
                rs = null;
                TopInfo.topNap.clear();
                TOP.stream().sorted(Comparator.comparing(s -> s.money,Comparator.reverseOrder())).forEach(s -> {
                    if(!TopInfo.topNap.stream().limit(100).anyMatch(p -> p != null && p.name.equals(s.name))){
                        TopInfo.topNap.add(s);
                    }
                });
                TOP.clear();
                rs = SQLManager.executeQueryFROM("SELECT `player_id`,JSON_EXTRACT(info, '$.power') AS CharPower  FROM `player` WHERE `role` = 0  ORDER BY CAST(CharPower as Int) DESC LIMIT 100");  
               // rs = SQLManager.executeQueryFROM("SELECT `player_id` FROM `player` WHERE `role` = 0  ORDER BY JSON_EXTRACT(info, '$.power') DESC LIMIT 100");                   
               // rs = SQLManager.executeQueryFROM("SELECT `player_id` FROM `player` WHERE `role` = 0 AND `server_name`='" + Setting.SERVER_NAME + "' ORDER BY info->'$.power' DESC LIMIT 100");                   

                if(rs != null){
                    while(rs.next()){
                        int id = rs.getInt("player_id");
                        if(PlayerDAO.getInfobyID(id) != null){
                            Player pl = PlayerDAO.getPlayerbyID(id);
                            TopInfo top = new TopInfo();
                            top.headID = pl.getHead();
                            top.body = pl.getBody();
                            top.leg = pl.getLeg();
                            top.name = pl.name;
                            top.pId = pl.id;
                            top.power = pl.point.power;
                            top.info = "Sức Mạnh: " + Util.powerToString(top.power);
                            top.curr = Util.currentTimeSec();
                            if(!TOP.stream().anyMatch(p -> p != null && p.name.equals(pl.name)))
                            {
                                TOP.add(top);
                            }
                        }
                    }
                }
                rs.close();
                rs = null;
                TopInfo.topSM.clear();
                TOP.stream().sorted(Comparator.comparing(s -> s.power,Comparator.reverseOrder())).forEach(s -> {
                    if(!TopInfo.topSM.stream().limit(100).anyMatch(p -> p != null && p.name.equals(s.name))){
                        TopInfo.topSM.add(s);
                    }
                });
                TOP.clear();
                rs = SQLManager.executeQueryFROM("SELECT `player_id`, JSON_EXTRACT(info, '$.taskID') AS taskID FROM `player` WHERE `role` = 0  ORDER BY CAST(taskID as Int) DESC LIMIT 100");

               // rs = SQLManager.executeQueryFROM("SELECT `player_id` FROM `player` WHERE `role` = 0  ORDER BY JSON_EXTRACT(info, '$.taskID') DESC LIMIT 100");
               // rs = SQLManager.executeQueryFROM("SELECT `player_id` FROM `player` WHERE `role` = 0 AND `server_name`='" + Setting.SERVER_NAME + "' ORDER BY info->'$.taskID' DESC LIMIT 100");                   
                if(rs != null){
                    while(rs.next()){
                        int id = rs.getInt("player_id");
                        if(PlayerDAO.getInfobyID(id) != null){
                            Player pl = PlayerDAO.getPlayerbyID(id);
                            TopInfo top = new TopInfo();
                            top.headID = pl.getHead();
                            top.body = pl.getBody();
                            top.leg = pl.getLeg();
                            top.name = pl.name;
                            top.pId = pl.id;
                            top.power = pl.point.power;
                            top.info = "Nhiệm vụ: " + Service.gI().NpcTraTask(pl, TaskData.getTask(pl.taskId).subNames[pl.taskIndex]);
                            top.curr = Util.currentTimeSec();
                            if(!TOP.stream().anyMatch(p -> p != null && p.name.equals(pl.name)))
                            {
                                TOP.add(top);
                            }
                        }
                    }
                }
                rs.close();
                rs = null;
                TopInfo.topNV.clear();
                TOP.stream().sorted(Comparator.comparing(s -> s.power,Comparator.reverseOrder())).forEach(s -> {
                    if(!TopInfo.topNV.stream().limit(100).anyMatch(p -> p != null && p.name.equals(s.name))){
                        TopInfo.topNV.add(s);
                    }
                });
                TOP.clear();
                
              //  rs = SQLManager.executeQueryFROM("SELECT `player_id` FROM `player` WHERE `role` = 0 AND `server_name`='" + Setting.SERVER_NAME + "' ORDER BY info->'$.pointTET' DESC LIMIT 100 ");
                rs = SQLManager.executeQueryFROM("SELECT `player_id` FROM `player` WHERE `role` = 0  ORDER BY JSON_EXTRACT(info, '$.pointTET') DESC LIMIT 100");

                if(rs != null){
                    while(rs.next()){
                        int id = rs.getInt("player_id");
                        if(PlayerDAO.getInfobyID(id) != null){
                            Player pl = PlayerDAO.getPlayerbyID(id);
                            TopInfo top = new TopInfo();
                            top.headID = pl.getHead();
                            top.body = pl.getBody();
                            top.leg = pl.getLeg();
                            top.name = pl.name;
                            top.pId = pl.id;
                            top.power = pl.point.power;
                            top.eventPoint = pl.pointTET;
                            top.info = "Điểm: " + pl.pointTET;
                            top.curr = Util.currentTimeSec();
                            if(!TOP.stream().anyMatch(p -> p != null && p.name.equals(pl.name))){
                                TOP.add(top);
                            }
                        }
                    }
                }
                rs.close();
                rs = null;
                TopInfo.topSuKien.clear();
                TOP.stream().sorted(Comparator.comparing(s -> s.eventPoint,Comparator.reverseOrder())).forEach(s -> {
                    if(!TopInfo.topSuKien.stream().limit(100).anyMatch(p -> p != null && p.name.equals(s.name))){
                        TopInfo.topSuKien.add(s);
                    }
                });
                TOP.clear();
                onload = true;
                SQLManager.closeFROM();
            }
        }
        catch(Exception e){
            SQLManager.closeFROM();
            Util.logException(DHVT.class, e);
        }
    }
    
    public void ClanInfo(){
        try{
            List<Clan> clans = ClanManager.gI().getClans();
            if(clans != null)
            {
                for(Clan cl : clans){
                    if(cl.DoanhTrai != null){
                        if (Util.findDayDifference(System.currentTimeMillis(), cl.DoanhTrai.timeStart) >= 1){
                            cl.DoanhTrai = null;
                            continue;
                        }
                        if(cl.DoanhTrai.time > 0){
                            cl.DoanhTrai.time--;
                            for(Member mb : ClanManager.gI().getMemberByIdClan(cl.id)){
                                Player pl = PlayerManger.gI().getPlayerByID(mb.id);
                                if(pl != null){
                                    Service.gI().sendTextTime(pl, cl.DoanhTrai.id, "Trại độc nhãn:", cl.DoanhTrai.time);
                                }
                            }
                        }
                    }
                    if(cl.KhiGasHuyDiet != null){
                        if (Util.findDayDifference(System.currentTimeMillis(), cl.KhiGasHuyDiet.timeStart) >= 1){
                            cl.KhiGasHuyDiet = null;
                            continue;
                        }
                        if(cl.KhiGasHuyDiet.time > 0){
                            cl.KhiGasHuyDiet.time--;
                            for(Member mb : ClanManager.gI().getMemberByIdClan(cl.id)){
                                Player pl = PlayerManger.gI().getPlayerByID(mb.id);
                                if(pl != null){
                                    Service.gI().sendTextTime(pl, cl.KhiGasHuyDiet.id, "Khí gas hủy diệt:", cl.KhiGasHuyDiet.time);
                                }
                            }
                        }
                    }
                    if(cl.KhoBauDuoiBien != null){
                        if (Util.findDayDifference(System.currentTimeMillis(), cl.KhoBauDuoiBien.timeStart) >= 1){
                            cl.KhoBauDuoiBien = null;
                            continue;
                        }
                        if(cl.KhoBauDuoiBien.time > 0){
                            cl.KhoBauDuoiBien.time--;
                            for(Member mb : ClanManager.gI().getMemberByIdClan(cl.id)){
                                Player pl = PlayerManger.gI().getPlayerByID(mb.id);
                                if(pl != null){
                                    Service.gI().sendTextTime(pl, cl.KhoBauDuoiBien.id, "Hang kho báu:", cl.KhoBauDuoiBien.time);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e){
            
        }
    }
    
    public void BaoTri(){
        try
        {
            List<Clan> clans = ClanManager.gI().getClans();
            if(clans != null)
            {
                for(Clan cl : clans){
                    ClanDAO.updateDB(cl);
                }
                if(!Chat.isReset)
                {
                    Util.error("--------------Hoàn tất bảo trì--------------\n");
                    System.exit(0);
                }
                else
                {
                    ServerManager.gI().RE_LOAD();
                }
            }
        }
        catch (Exception e)
        {
            Util.logException(DHVT.class, e);
        }
    }
}
