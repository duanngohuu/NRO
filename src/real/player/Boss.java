package real.player;

import java.util.ArrayList;
import java.util.List;
import real.func.ChangeMap;
import server.ServerManager;
import server.Service;
import server.Util;

public abstract class Boss extends Player {
    protected static final byte ATTACK = 2;
    protected static final byte DIE = 4;
    
    protected int cx;
    protected int cy;
    
    public long currTime;

    public Boss() {
        super();
    }

    public synchronized void start(int cx, int cy, int id) {
        this.initSkill();
        this.id = id;
        this.isBoss = true;
        this.typePk = 5;
        joinMap();
        this.cx = cx;
        this.cy = cy;
        move(cx, cy);
    }

    protected abstract void initSkill();

    @Override
    public abstract short getHead();

    @Override
    public abstract short getBody();

    @Override
    public abstract short getLeg();

    @Override
    public abstract void update();

    public abstract void startDie();

    protected Player getPlayerAttack() {
        List<Player> Players = new ArrayList<Player>();
        for (Player pl : this.zone.players) {
            if (pl != null && !pl.isDie() && pl != this && !pl.isBoss && !pl.isNewPet && Util.getDistance(this.x, this.y, pl.x, pl.y) <= 100) {
                Players.add(pl);
            }
        }
        if (Players.size() > 0) {
            return Players.get(0);
        }
        return null;
    }
    
    public void joinMap() {
        ChangeMap.gI().changeMapBySpaceShip(this, this.zone, ChangeMap.TENNIS_SPACE_SHIP);
    }
}