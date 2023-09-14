package real.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.player.Player;
import real.player.PlayerManger;
import server.Service;
import server.Util;

public class DHVT_Template {
    public Player player1;
    public Player player2;
    public Player tagetWin;
    public DHVT_START DHVT_START;
    public int attack = -1;
    private static DHVT_Template instance;
    
    public List<DHVT_Template> PK_DHVT = new ArrayList();
    
    public DHVT_Template(Player pl1, Player pl2){
        player1 = pl1;
        player2 = pl2;
        DHVT_START = new DHVT_START();
    }
    
    public static DHVT_Template gI() {
        if (instance == null) {
            instance = new DHVT_Template(null, null);
        }
        return instance;
    }
    
    public DHVT_Template findPK_DHVT(Player pl){
        try{
            for(DHVT_Template dhvt : PK_DHVT)
            {
                if(dhvt.player1 == pl || dhvt.player2 == pl)
                {
                    return dhvt;
                }
            }
            return null;
        }catch (Exception e) {
            return null;
        }
    }
    
    public void finishPK_DHVT(Player pl){
        try{
            for(DHVT_Template dhvt : PK_DHVT)
            {
                if(dhvt.player1 == pl)
                {
                    dhvt.tagetWin = player2;
                    dhvt.attack = 1;
                    return;
                }
                if(dhvt.player2 == pl)
                {
                    dhvt.tagetWin = player1;
                    dhvt.attack = 1;
                    return;
                }
            }
        }
        catch (Exception e) {
            
        }
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
                    DHVT_Template.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }
    
    public void update(){
        try{
            DHVT.gI().listReg.stream().filter(pl -> pl != null && PlayerManger.gI().getPlayerByID(pl.id) != null).forEach((pl) ->{
                if(pl != null){
                    DHVT.gI().Update(pl);
                }
            });
        }
        catch (Exception e) {
        }
    }
    
    class DHVT_START{
        public long curr = -1;
        public int time = 45000;
        
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
                        DHVT_START.this.update();
                    }
                };
                this.timer.schedule(task, delay, delay);
            }
        }

        public void update()
        {
            if(curr == -1){
                curr = System.currentTimeMillis();
                ChangeMap.gI().changeMapDHVT(player1, player2, 51, 300, 200);
                return;
            }
            if(!Util.canDoWithTime(curr, 15000)){
                int mis = (int)(System.currentTimeMillis() - curr) / 1000;
                Service.gI().chat(player2, "Đếm ngược " + mis + "s");
                Service.gI().chat(player1, "Đếm ngược " + mis + "s");
                return;
            }
            if(attack == -1 && Util.canDoWithTime(curr, 15000)){
                attack = 0;
                Service.gI().sendPlayerVS(player1, player2, (byte)3);
                Service.gI().sendPlayerVS(player2, player1, (byte)3);
                return;
            }
            if(attack == 0 && player1.isDie()){
                DHVT.gI().listReg.add(player2);
                Service.gI().sendThongBao(player2, "Chúc mừng bạn đã vào vòng trong");
                Service.gI().sendThongBao(player1, "Rất tiếc bạn đã thua cuộc vui lòng quay lại sau!");
                tagetWin = player2;
                attack = 1;
                curr = System.currentTimeMillis();
                return;
            }
            if(attack == 0 && player2.isDie()){
                DHVT.gI().listReg.add(player1);
                Service.gI().sendThongBao(player1, "Chúc mừng bạn đã vào vòng trong");
                Service.gI().sendThongBao(player2, "Rất tiếc bạn đã thua cuộc vui lòng quay lại sau!");
                tagetWin = player1;
                attack = 1;
                curr = System.currentTimeMillis();
                return;
            }
            if(attack == 0 && Util.canDoWithTime(curr, time)){
                DHVT.gI().listReg.add(player1);
                Service.gI().sendThongBao(player1, "Chúc mừng bạn đã vào vòng trong");
                Service.gI().sendThongBao(player2, "Rất tiếc bạn đã thua cuộc vui lòng quay lại sau!");
                tagetWin = player1;
                attack = 1;
                curr = System.currentTimeMillis();
                return;
            }
            if(attack == 1 && Util.canDoWithTime(curr, 3000)){
                int mapID = 52;
                if(player1 != null){
                    if(tagetWin != null && tagetWin.id == player1.id){
                        ChangeMap.gI().changeMap(player1, mapID,-1, 5);
                    }
                    else{
                        ChangeMap.gI().changeMap(player1, 21 + player1.gender,-1, 5);
                    }
                }
                if(player2 != null){
                    if(tagetWin != null && tagetWin.id == player2.id){
                        ChangeMap.gI().changeMap(player2, mapID,-1, 5);
                    }
                    else{
                        ChangeMap.gI().changeMap(player1, 21 + player1.gender,-1, 5);
                    }
                }
                close();
                attack = 2;
                DHVT_Template temp = DHVT_Template.gI().findPK_DHVT(tagetWin);
                DHVT_Template.gI().PK_DHVT.remove(temp);
            }
        }
    }
}
