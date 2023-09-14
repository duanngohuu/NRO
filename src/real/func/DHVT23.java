package real.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.map.Map;
import real.map.MapManager;
import real.map.Zone;
import real.player.Player;
import real.skill.SkillUtil;
import server.Service;
import server.Util;

public class DHVT23 {
    private static DHVT23 instance;
    
    public List<DHVT23> DHVT23s = new ArrayList();
    
    public int next = 0;
    public int step = 0;
    public int die = 1;
    
    public boolean isDrop = false;
    public boolean isWin = false;
    public boolean isStart = false;
    
    public long curr;
    public long currATTACK;
    public long currTHDS;
    
    public Player player;
    public Player Boss;
    public int[][] players = 
    {
        {394, 395, 396},
        {400, 401, 402},
        {359, 360, 361},
        {362, 363, 364},
        {365, 366, 367},
        {371, 372, 373},
        {338, 339, 340},
        {374, 375, 376},
        {356, 357, 358},
        {368, 369, 370},
        {397, 398, 399}
    };
    
    public int[][] points = 
    {
        {10000, 1000},
        {25000 ,2000},
        {50000, 3000},
        {100000, 4000},
        {250000, 5000},
        {500000, 6000},
        {2000000, 7000},
        {5000000, 8000},
        {25000000, 9000},
        {75000000, 10000},
        {150000000, 11000}
    };
    
    public String[] names = {
        "Sói héc quyn",
        "Ở dơ",
        "Xinbatô",
        "Cha pa",
        "Pon put",
        "Chan xư",
        "Tàu Bảy Bảy",
        "Yamcha",
        "Jacky Chun",
        "Thiên xin hăng",
        "Liu Liu"
    };
    
    public static DHVT23 gI() {
        if (instance == null) {
            instance = new DHVT23(null);
        }
        return instance;
    }
    
    public DHVT23(Player player){
        this.player = player;
    }
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public void close()
    {
        try
        {
            DHVT23.gI().DHVT23s.remove(this);
            if(player.zone != null){
                player.zone.zoneDoneDHVT = false;
            }
            if(next != 0 && !isWin)
            {
                die++;
                Service.gI().sendPlayerVS(player, Boss, (byte)0);
            }
            next = 0;
            actived = false;
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
            if(Boss != null){
                Boss.exitMap();
                Boss.close();
                Boss.dispose();
                Boss = null;
            }
        }
        catch (Exception e)
        {
            if(player.zone != null){
                player.zone.zoneDoneDHVT = false;
            }
            task = null;
            timer = null;
            if(Boss != null){
                Boss.exitMap();
                Boss.close();
                Boss.dispose();
                Boss = null;
            }
        }
    }

    public void active(int delay) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    DHVT23.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
    
    public void update(){
        try
        {
            if(next == 0){
                if(System.currentTimeMillis() - curr >= 3000 || !isStart){
                    curr = System.currentTimeMillis();
                    player.x = 300;
                    player.y = 264;
                    Service.gI().setPos(player, 300, 264);
                    newBoss();
                    next++;
                    return;
                }
            }
            else if(next == 1)
            {
                if(System.currentTimeMillis() - curr >= 1000 || isStart)
                {
                    isStart = false;
                    curr = System.currentTimeMillis();
                    Service.gI().setPos(player, 300, 264);
                    player.playerSkill.setBlindDCTT(System.currentTimeMillis(), 10000, player.id);
                    Boss.playerSkill.setBlindDCTT(System.currentTimeMillis(), 10000, Boss.id);
                    player.point.updateall();
                    Service.gI().Send_Info_NV(player, 1);
                    Service.gI().Send_Info_NV(Boss, 1);
                    next++;
                    return;
                }
            }
            else if(next == 2)
            {
                if(System.currentTimeMillis() - curr >= 11000){
                    curr = System.currentTimeMillis();
                    Service.gI().sendPlayerVS(player, Boss, (byte)3);
                    Service.gI().changeTypePK(Boss, 3);
                    next++;
                    return;
                }
            }
            else if(next == 3)
            {
                if(Util.canDoWithTime(curr, 180000)){
                   next++;
                   return;
                }
                if(Boss != null){
                    if(Boss.isDie() && step < 10){
                        Service.gI().sendPlayerVS(player, Boss, (byte)0);
                        step++;
                        next = 0;
                        return;
                    }
                    else if(Boss.isDie() && step >= 10)
                    {
                        Service.gI().sendPlayerVS(player, Boss, (byte)0);
                        isWin = true;
                        next++;
                        step++;
                        return;
                    }
                    BossAttack();
                }
                if(player != null && player.isDie())
                {
                    next++;
                }
            }
            else if(next == 4)
            {
                if(isWin)
                {
                    ChangeMap.gI().changeMap(player, 129, -1, 360, 360, (byte)0);
                    Service.gI().sendThongBao(player, "Chúc mừng bạn đã vô dịch");
                }
                else
                {
                    Service.gI().setPos(player, 360, 360);
                    Service.gI().hsChar(player, 1, 1);
                    Service.gI().sendPlayerVS(player, Boss, (byte)0);
                    Service.gI().sendThongBao(player, "Rất tiếc bạn đã thua cuộc");
                }
                close();
            }
        }
        catch (Exception e)
        {
            Util.debug(e.toString());
            e.printStackTrace();
        }
    }
    
    public void newBoss()
    {
        if(step >= 11){
            return;
        }
        if(Boss != null){
            Boss.exitMap();
            Boss.close();
            Boss.dispose();
        }
        Boss = new Player();
        Boss.isBoss = true;
        Boss.name = names[step];
        Boss.id = -players[step][0];
        Boss.head = (short)players[step][0];
        Boss.body = (short)players[step][1];
        Boss.leg = (short)players[step][2];
        Boss.point.hpGoc = points[step][0];
        Boss.point.mpGoc = points[step][0];
        Boss.point.dameGoc = points[step][1];
        Boss.playerSkill.skills.add(SkillUtil.createSkill(0, step == 0 ? 1 : step > 7 ? 7 : step, 0, 0));
        Boss.playerSkill.skills.add(SkillUtil.createSkill(1, 7, 0, 0));
        Boss.playerSkill.skills.add(SkillUtil.createSkill(6, 7, 0, 0));
        curr = System.currentTimeMillis();
        Boss.point.updateall();
        Boss.zone = player.zone;
        joinMap();
    }
    
    public void BossAttack()
    {
        Boss.playerSkill.update();
        if(Boss.playerSkill.isHaveEffectSkill()){
            return;
        }
        if(step != 5 && Util.getDistance(Boss, player) >= 60){
            Boss.move(player.x + 40, player.y);
        }
        if(step == 5 && Util.getDistance(Boss, player) >= 60){
            Boss.playerSkill.skillSelect = Boss.playerSkill.skills.get(1);
        }
        else if(step == 9 && Util.canDoWithTime(currTHDS, 60000)){
            currTHDS = System.currentTimeMillis();
            Boss.playerSkill.skillSelect = Boss.playerSkill.skills.get(2);
        }
        else
        {
            Boss.playerSkill.skillSelect = Boss.playerSkill.skills.get(0);
        }
        if(Util.canDoWithTime(currATTACK, 500)){
            currATTACK = System.currentTimeMillis();
            Boss.playerSkill.useSkill(player, null);
        }
    }
    
    public void joinMap()
    {
        ChangeMap.gI().changeMapDHVT23(Boss, Boss.zone, 470, 264);
    }
    
    public void Start()
    {
        Zone zone = null;
        Map map = MapManager.gI().getMapById(129);
        for(Zone z : map.map_zone)
        {
            if(DHVT23.gI().DHVT23s.stream().allMatch(dh -> dh.player.zone != z))
            {
                zone = z;
                break;
            }
        }
        DHVT23.gI().DHVT23s.add(this);
        if(player.zone != zone)
        {
            ChangeMap.gI().changeMap(player, 129, zone.zoneId, 360, 360, (byte)0);
        }
        if(zone != null)
        {
            Service.gI().sendThongBao(player, "Bạn chuẩn bị đầu tiên");
            curr = System.currentTimeMillis();
            next = 0;
            active(5);
            isStart = true;
        }
    }
    
    @Override
    public String toString()
    {
        final String n = "";
        return "{"
                + n + "step" + n + ":" + n + step + n + ","
                + n + "die" + n + ":" + n + die + n + ","
                + n + "drop" + n + ":" + n + isDrop + n
                + "}";
    }
}
