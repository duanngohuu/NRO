package real.clan;

import real.player.Player;

public class Member {

    public Member() {

    }

    public Member(Player pl, int r) {
        id = (int) pl.id;
        name = pl.name;
        powerPoint = pl.point.power;
        clanPoint = 0;
        joinTime = System.currentTimeMillis() / 1000;
        currPoint = 0;
        role = (byte)r;
    }

    public int id;

    public String name;

    public byte role;

    public long powerPoint;

    public long donate;

    public long receiveDonate;

    public long clanPoint;

    public long currPoint;

    public long joinTime;

    public byte recieve_pean = 0;

}
