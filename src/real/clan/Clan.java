package real.clan;

import java.util.ArrayList;
import real.item.Item;
import server.Util;

public class Clan {

    public int id;

    public String name;

    public String slogan;

    public byte imgID;

    public byte level;

    public int clanPoint;

    public long powerPoint;

    public int leaderID;

    public byte currMember;

    public byte maxMember;

    public long time;

    public PhuBan DoanhTrai = null;
    public PhuBan KhiGasHuyDiet = null;
    public PhuBan KhoBauDuoiBien = null;
    public PhuBan ConDuongRanDoc = null;
    public ArrayList<Member> members;
    public ArrayList<ClanMessage> messages;
    int idms;

    public Clan() {
        this.members = new ArrayList<>();
        messages = new ArrayList<>();
        time = System.currentTimeMillis() / 1000;
    }

    public ClanMessage getMessage(int id) {
        for (ClanMessage m : messages) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }

    public Member getMember(long id) {
        for (Member m : members) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }
}
