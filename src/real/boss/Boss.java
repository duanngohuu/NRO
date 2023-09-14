package real.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import real.func.Combine;
import real.func.DHVT;
import real.item.ItemTimes;
import real.player.Inventory;
import real.player.Player;
import real.player.PlayerSkill;
import real.player.Point;
import server.ServerManager;
import server.Service;
import server.Util;

public abstract class Boss extends Player {

    protected static final byte JUST_JOIN_MAP = 0;
    protected static final byte TALK_BEFORE = 1;
    protected static final byte ATTACK = 2;
    protected static final byte DIE = 4;
    protected static final byte TALK_AFTER = 5;
    protected static final byte LEAVE_MAP = 6;
    protected static final byte EXIT_MAP = 7;

    protected String[] textTalkBefore;
    protected String[] textTalkMidle;
    protected String[] textTalkAfter;
    protected long lastTimeTalk;
    protected int timeTalk;
    protected byte indexTalk;

    protected byte status;

    protected void setStatus(byte status) {
        this.status = status;
        if (status == TALK_BEFORE || status == TALK_AFTER || status == ATTACK) {
            indexTalk = 0;
        }
    }

    public Boss() {
        super();
    }

    public synchronized void start() {
        this.init();
        this.initSkill();
        this.initTalk();
        if(this.zone != null && this.status != DIE){
            Util.debug("BOSS " + name + " vừa xuất hiện " + zone.map.name + "[" + zone.map.id + " - " + zone.zoneId + "]");
            if (!(this.name.startsWith("Broly") || this.name.contains("Hatchiyack") || this.name.contains("Lychee") || this.name.contains("Robot") || name.contains("Ninja") ||name.contains("Trung Úy") ||name.contains("Tập sự") || name.contains("Xên con") || name.equals("Thỏ Đầu Khấc") || this.name.contains("Dơi Nhí")||this.name.contains("Ma Trơi")|| name.contains("Bộ Xương") || name.equals("KuKu") || name.equals("Mập Đầu Đinh") || name.equals("Rambo"))) {
                String content = "BOSS " + name + " vừa xuất hiện tại " + zone.map.name;
                DHVT.gI().listThongBao.add(content);
                Service.gI().sendThongBaoBenDuoi(content);
            }
            
        }
    }

    protected abstract void init();

    protected abstract void initSkill();

    protected abstract void initTalk();

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
            if (pl != null && !pl.isDie() && pl != this && !pl.isBoss && !pl.isNewPet) {
                Players.add(pl);
            }
        }
        if (Players.size() > 0) {
            return Players.get(Util.nextInt(0, Players.size() - 1));
        }

        return null;
    }
}
