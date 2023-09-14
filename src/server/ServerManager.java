package server;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.boss.BossManager;
import real.clan.ClanManager;
import real.func.DHVT;
import real.func.DHVT_Template;
import real.func.GiftCode;
import real.func.MiniGame;
import real.func.NRNM;
import real.func.NRSD;
import real.item.CaiTrangData;
import real.item.ItemData;
import real.map.MapManager;
import real.map.MobTemplateData;
import real.npc.Npc;
import real.npc.NpcTemplateData;
import real.player.BossDAO;
import real.radar.RadarTemplateData;
import real.skill.SkillData;
import real.skill.SpeacialSkillData;
import real.task.TaskBoMongManager;
import server.io.Session;
import real.task.TaskData;
import service.Chat;
import service.Setting;
import service.data.Init;
import service.data.PartData;

public class ServerManager {
    
    private ServerSocket listenSocket = null;

    private static ServerManager instance;

    public static List<Session> Sessions = new ArrayList();

    public static boolean serverStart;
    
    public void init() {
       Setting.loadConfigFile();
        SQLManager.create();
        SQLManager.createDATA();
        PartData.loadPart();
        SkillData.loadSkill();
        CaiTrangData.loadCaiTrang();
        Npc.loadNPC();
        TaskBoMongManager.gI().init();
        MobTemplateData.gI().init();
        NpcTemplateData.gI().init();
        BossDAO.loadBoss();
        MapManager.gI().init();
        ClanManager.gI().init();
        ItemData.gI().loadDataItems();
        TaskData.load_task();
        DHVT.gI().loadCup();
        GiftCode.loadGift();
        RadarTemplateData.gI().load_radar();
        SpeacialSkillData.gI().load_speacialSkill();
        Init.init();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args)
    {
        ServerManager.gI().run();
    }
    public static List<String> WhiteList = new ArrayList<>();

    public static void addIP(String ip) {
        for (String s : WhiteList) {
            if (!s.contains(ip)) {
                WhiteList.add(ip);
            }
        }
    }
    
    public static void removeIP(String ip) {
        for (String s : WhiteList) {
            if (s.contains(ip)) {
                WhiteList.add(ip);
            }
        }
    }
    
    public boolean checkIP(String ip) {
        for (String s : WhiteList) {
            if (s.contains(ip)) {
                return true;
            }
        }
        return false;
    }

    public void run() {
//        loadDataEff();
        loadModel();
        active(10);
    }
    
    public static void loadDataEff() {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/eff_data/x4/data/23_0"));
            byte count = dis.readByte();
            // System.out.print("ImageInfo: " + count + "\n");
            for(int i = 0; i < count; i++)
            {
                System.out.print("---|" + i + "|---" + "\n");
                System.out.print("ID: " + dis.readByte());
                System.out.print("x0: " + dis.readByte());
                System.out.print("y0: " + dis.readByte());
                System.out.print("w: " + dis.readByte());
                System.out.print("h: " + dis.readByte());
            }
            System.out.print("\n---|FRAME|---" + "\n");
            short Frames = dis.readShort();
            System.out.print("Frame: " + Frames + "\n");
            for(int i = 0; i < Frames; i++)
            {
                int a = dis.readByte();
                System.out.print("---|" + i + "|---" + "\n");
                System.out.print("count: " + a + "\n");
                for(int k = 0; k < a; k++)
                {
                    System.out.print("--|" + k + "|--" + "\n");
                    System.out.print("dx: " + dis.readShort() + "\n");
                    System.out.print("dy: " + dis.readShort() + "\n");
                    System.out.print("id: " + dis.readByte() + "\n");
                }
            }
            
            short arrFrames = dis.readShort();
            System.out.print("arrFrame: " + Frames + "\n");
            for(int i = 0; i < arrFrames; i++)
            {
                System.out.print("---|" + i + "|---" + "\n");
                System.out.print("arrFrame[i]: " + dis.readShort() + "\n");
            }
            
            dis.close();
            dis = null;
        } catch (Exception e) {
            System.exit(0);
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
                    ServerManager.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
    
    public void update()
    {
        try
        {
            Socket sc = listenSocket.accept();
            if (Util.existsFile("BaoTri.txt") || Chat.isBaoTri && Chat.minuteBaoTri <= 0)
            {
                sc.close();
                sc = null;
            }
            else
            {
                String IP = sc.getInetAddress().getHostAddress();
                if(Sessions.stream().filter(SS -> SS.ipAddress.equals(IP)).toList().size() < Setting.MAX_IP)
                {
                    Util.debug(Util.BLUE+"Socket: "+Util.RESET + IP + ":" + sc.getPort());
                    Session session = new Session();
                    session.add(sc, new Controller());
                    Sessions.add(session);
                }
                else
                {
                    sc.close();
                    sc = null;
                }
            }
        }
        catch (Exception e)
        {
            Util.logException(ServerManager.class, e);
        }
    }
    
    public void loadModel()
    {
        try
        {
            serverStart = true;
            SQLManager.execute("UPDATE `player` SET `online`='0' WHERE online='1'");
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String strDate = formatter.format(date);
            Util.success("START SERVER..."+Util.RESET + strDate + " \n");
            
            BossManager.gI().active(1000);
            DHVT_Template.gI().active(1000);
            DHVT.gI().active(1000);
            NRSD.gI().active(1000);
            NRNM.gI().active(1000);
            MiniGame.gI().MiniGame_S1.active(1000);
            listenSocket = new ServerSocket(Setting.PORT);
        }
        catch (Exception e)
        {
            
        }
    }
    
    public void RE_LOAD()
    {
        init();
        Chat.isReset = false;
        Chat.isBaoTri = false;
        Chat.minuteBaoTri = -1;
        close();
        ServerManager.gI().run();
    }
}
