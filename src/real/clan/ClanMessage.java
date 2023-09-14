package real.clan;
import real.player.Player;
import server.Service;
import server.Util;
import server.io.Message;

public class ClanMessage {

    public int clan_id;

    public byte type;
    public int id;
    public int player_id;
    public String player_name;
    public byte role;
    public int time;

    public String text;
    public byte color;

    public byte recieve;
    public byte maxCap;
    public String clan_name;
        
    public ClanMessage(Player pl, Clan cl) {
        clan_id = cl.id;
        id = cl.idms;
        player_id = (int) pl.id;
        player_name = pl.name;
        role = pl.clan == null ? 2 : pl.clan.getMember(pl.id).role;
        time = (int) (System.currentTimeMillis() / 1000 - 1000000000);
        clan_name = cl.name;
        cl.idms++;
        cl.messages.add(this);
        if(cl.messages.size() >= 30){
            cl.messages.remove(0);
        }
    }

    public void send_message() {
        Message msg = null;
        try {
            msg = new Message(-51);
            msg.writer().writeByte(type);
            msg.writer().writeInt(id);
            msg.writer().writeInt(player_id);
            msg.writer().writeUTF(player_name);
            msg.writer().writeByte(role);
            msg.writer().writeInt(time);
            switch (type) {
                case 0: // chat
                    msg.writer().writeUTF(text);
                    msg.writer().writeByte(color);
                    break;

                case 1: // xin dau
                    text = "Xin đậu " + player_name;
                    msg.writer().writeByte(recieve);
                    msg.writer().writeByte(maxCap);
                    msg.writer().writeByte(0);
                    break;

                case 2: // xin vao bang
                    text = player_name + " xin vào bang";
                    break;
                default:
                    return;
            }
            Service.gI().sendMessAllPlayerInClan(msg, clan_id);
        } catch (Exception e) {
            Util.debug("ClanMessage.send_message");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }

    }
}
