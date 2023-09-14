
package real.magictree;

import java.util.Timer;
import java.util.TimerTask;
import real.map.MapManager;
import real.player.Player;
import server.Service;
import service.DAOS.PlayerDAO;

public class MabuEgg {

    public Player player;
    public long createTime;
    public int timeHatches;
    
    public MabuEgg(Player pl , long CreateTime , int timeHatches){
        this.player = pl;
        this.createTime = CreateTime;
        this.timeHatches = timeHatches;
        this.active();
    }
    
    public void update() {
        try{
            if(player != null && player.mabuEgg != null){
                if(MapManager.gI().isMapHome(player) && !player.zone.checkNpc(50)){
                    PlayerDAO.createMabuEgg(player);
                }
                if((System.currentTimeMillis() - createTime) / 1000 < timeHatches && timeHatches > 0){
                    timeHatches--;
                    if(MapManager.gI().isMapHome(player)){
                        Service.gI().sendtMabuEff(player, 100);
                        Service.gI().sendImageMabuEgg(player);
                    }
                }
            }
        }catch(Exception e){
        }
    }
    
    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public void close() {
        task.cancel();
        timer.cancel();
        task = null;
        timer = null;
    }

    public void active() {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            };
            this.timer.schedule(task, 1000L, 1000L);

        }
    }
}
