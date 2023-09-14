package real.pet;

import real.map.MapService;
import real.player.Player;
import server.Service;
import server.Util;

public class NewPet extends Player{
    public Player master;
    public short body;
    public short leg;
    public static int idb = -4000;
    public NewPet(Player master , short h,short b,short l) {
        this.master = master;
        this.isNewPet = true;
        this.id = idb;
        idb--;
        this.head = h;
        this.body = b;
        this.leg = l;
    }
    
    @Override
    public short getHead() {
        return head;
    }

    @Override
    public short getBody() {
        return body;
    }

    @Override
    public short getLeg() {
        return leg;
    }
    
    public void joinMapMaster() {
        int[] move = new int[]{-50,50};
        this.x = master.x + move[Util.nextInt(0, 1)];
        this.y = master.y;
        this.gotoMap(master.zone);
        MapService.gI().joinMap(this, master.zone);
        
    }
    
    public void exitMapFusion() {
        if (this.zone != null) {
            MapService.gI().exitMap(this, this.zone);
        }
    }
    
    private long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.x - master.x <= 0 ? -1 : 1;
            move(master.x + Util.nextInt(dir == -1 ? 0 : -50, dir == -1 ? 50 : 0), master.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }
    
    @Override
    public void update() {
        if (this.zone == null || this.zone != master.zone) {
            joinMapMaster();
        }
        if (master.isDie()) {
            return;
        }
        moveIdle();
    }
    
    @Override
    public void move(int _toX, int _toY) {
        if (zone == null) {
            zone = master.zone;
            gotoMap(zone);
            Service.gI().mapInfo(this);
            MapService.gI().joinMap(this, zone);
            MapService.gI().loadAnotherPlayers(this, this.zone);
        }
        super.move(_toX, _toY);
        idle = false;
    }
    
    public void followMaster() {
        followMaster(100);
    }

    private void followMaster(int dis) {
        int mX = master.x;
        int mY = master.y;
        int disX = this.x - mX;
        double disChanger = Math.sqrt(Math.pow(mX - this.x, 2) + Math.pow(mY - this.y, 2));
        if (disChanger >= dis) {
            if (disX < 0) {
                this.x = mX - dis;
            } else {
                this.x = mX + dis;
            }
            this.y = mY;
            this.move(this.x, this.y);
        }
    }
}
