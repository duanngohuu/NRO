package real.clan;

import java.util.ArrayList;
import java.util.List;
import real.item.Item;
import real.player.Player;
import service.DAOS.PlayerDAO;
import real.player.PlayerManger;
import server.SQLManager;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;

public class ClanService {

    private static ClanService instance;

    public static ClanService gI() {
        if (instance == null) {
            instance = new ClanService();
        }
        return instance;
    }
    
    public void DelClan(Player pl){
        try{
            if(pl.clan != null && pl.clan.DoanhTrai == null || pl.clan != null && pl.clan.DoanhTrai != null && pl.clan.DoanhTrai.time <= 0 && Util.findDayDifference(System.currentTimeMillis(),pl.timeJoinClan) >= 1){
                if (pl.clan == null || pl.clan.members.stream().anyMatch(m -> m != null && m.id == pl.id && m.role != 0)) {
                    Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    return;
                }
                for(int i = 0;  i < pl.clan.members.size();i++){
                    Member m = pl.clan.members.get(i);
                    if(m != null && m.id != pl.id){
                        Player pls = PlayerDAO.getInfobyID(m.id);
                        pls.clan = null;
                        ClanManager.gI().removeMember(m.id, pl.clan);
                        if (PlayerManger.gI().getPlayerByID(pls.id) != null) {
                            Service.gI().sendBag(pl);
                            Service.gI().sendThongBao(pls, "Bang chủ đã giải tán bang hội");
                            clanInfo(pls, -1);
                        } else {
                            PlayerDAO.UpdateInfoPlayer(pls);
                        }
                    }
                }
                SQLManager.execute("DELETE FROM `clan` WHERE `id`=" + pl.clan.id);
                ClanManager.gI().removeMember(pl.id, pl.clan);
                ClanManager.gI().removeClan(pl.clan.id);
                clanInfo(pl, -1);
                pl.clan = null;
                Service.gI().sendBag(pl);
                Service.gI().sendThongBao(pl, "Giải tán bang hội thành công");
            }else{
                Service.gI().sendThongBao(pl, "Không thể thực hiện lúc này");
            }
        }catch(Exception e){
            Util.log("--------DelClan---------");
            e.printStackTrace();
        }
    }
    
    public void loadBagImage(Player pl, int id) {
        if (id == -1) {
            return;
        }
        Message msg = null;
        try {
            for (ClanImage cl : ClanManager.gI().clanImages) {
                if (cl != null && cl.id == id) {
                    msg = new Message(-63);
                    msg.writer().writeByte(cl.id);
                    int size = cl.frame.length;
                    int len = 0;
                    for(int i = 0; i < size; i++){
                        if(cl.frame[i] != -1){
                            len++;
                        }
                    }
                    msg.writer().writeByte(len);
                    for (int l = 0; l < len; l++) {
                        msg.writer().writeShort(cl.frame[l]);
                    }
                    pl.sendMessage(msg);
                    break;
                }
            }
        } catch (Exception e) {
            Util.log("ClanService.loadBagImage \n------------");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void loadImgClan(Player pl, int id) {
        if (id == -1) {
            return;
        }
        Message msg = null;
        try {
            for (ClanImage cl : ClanManager.gI().clanImages) {
                if (cl.id == id && cl.type != 1) {
                    msg = new Message(-62);
                    msg.writer().writeByte(cl.id);
                    int len = cl.frame[1] == -1 ? 1 : 2;
                    msg.writer().writeByte(len + 1);
                    msg.writer().writeShort((short) cl.img);
                    for (int l = 0; l < len; l++) {
                        msg.writer().writeShort(cl.frame[l]);
                    }
                    pl.sendMessage(msg);
                    break;
                }
            }
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void sendInviteClan(Player pl , int playerID , byte action){
        if(pl.clan == null){
            return;
        }
        if (pl.clan.members.size() >= pl.clan.maxMember) {
            Service.gI().sendThongBao(pl, "Bang hội đã đầy người");
            return;
        }
        Message msg = null;
        try {
            if(action == 0){
                msg = new Message(-57);
                Player invitepl = PlayerDAO.getInfobyID(playerID);
                if(invitepl != null){
                    msg.writer().writeUTF(pl.name + " muốn mời bạn vào bang hội " + pl.clan.name);
                    msg.writer().writeInt(pl.clan.id);
                    msg.writer().writeInt(invitepl.id);
                    invitepl.session.sendMessage(msg);
                    Service.gI().sendThongBao(pl, "Đã gửi lời mời tới " + invitepl.name);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {
        }
    }

    public void sendJoinClan(int clanId , int idInv){
        Message msg = null;
        try {
            Clan clan = ClanManager.gI().getClanById(clanId);
            Player mem = PlayerDAO.getInfobyID(idInv);
            if (clan.members.size() >= clan.maxMember) {
                Service.gI().sendThongBao(mem, "Bang hội đã đầy người");
                return;
            }
            if (mem.clan == null) {   
                Member member = new Member(mem, 2);
                clan.members.add(member);
                mem.clan = clan;
                ClanManager.gI().addClan(clan);
                mem.timeJoinClan = System.currentTimeMillis();
                if (PlayerManger.gI().getPlayerByID(idInv) != null) {
                    Service.gI().sendJoinClan(mem);
                    updateInfo(0, member, clan);
                    clan.members.stream().forEach((mems) -> {
                        clanInfo(PlayerDAO.getInfobyID(mems.id), clanId);
                    });
                    clanMessage(mem, (byte)0, mem.name + " đã gia nhập bang hội", idInv);
                } else {
                    PlayerDAO.UpdateInfoPlayer(mem);
                }
            }
            else if(mem.clan != null){
                Service.gI().sendThongBao(mem, "Bạn đã trong clan");
            }
        }catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void joinClan(Player pl, int id, byte action) {
        Member plmem = pl.clan.getMember(pl.id);
        if(plmem != null && plmem.role != 0){
            return;
        }
        Message msg = null;
        try {
            ClanMessage m = pl.clan.getMessage(id);
            if(m != null) {
                Player mem = PlayerDAO.getInfobyID(m.player_id);
                if(mem == null){
                    return;
                }
                m.type = 0;
                switch (action) {
                    case 1: //tu choi
                        m.player_id = pl.id;
                        m.player_name = pl.name;
                        m.role = 0;
                        m.text = "Từ chối " + mem.name;
                        m.color = 1;
                        if (PlayerManger.gI().getPlayerByID(mem.id) != null) {
                            Service.gI().sendThongBao(mem, "Bạn bị từ chối gia nhập bang hội " + pl.clan.name);
                        }
                        break;
                    case 0:// chap nhan
                        Clan clan = ClanManager.gI().getClanById(pl.clan.id);
                        if (clan.members.size() >= clan.maxMember) {
                            Service.gI().sendThongBao(pl, "Bang hội đã đầy người");
                            return;
                        }
                        if(mem.clan != null){
                            Service.gI().sendThongBao(pl, "Người chơi " + mem.name + " đã có bang hội rồi");
                            return;
                        }
                        m.player_id = pl.id;
                        m.player_name = pl.name;
                        m.role = 0;
                        m.text = "Đã chấp nhận " + mem.name;
                        Member member = new Member(mem, 2);
                        clan.members.add(member);
                        mem.clan = clan;
                        ClanManager.gI().addClan(clan);
                        mem.timeJoinClan = System.currentTimeMillis();
                        if (PlayerManger.gI().getPlayerByID(mem.id) != null) {
                            Service.gI().sendThongBao(mem, "Bạn đã gia nhập bang hội " + pl.clan.name);
                        } else {
                            PlayerDAO.UpdateInfoPlayer(mem);
                        }
                        Service.gI().sendJoinClan(pl);
                        updateInfo(0, member, clan);
                        clan.members.stream().forEach((mems) -> {
                            Player plMember = PlayerManger.gI().getPlayerByID(mems.id);
                            if(plMember != null){
                                clanInfo(plMember, plMember.clan.id);
                            }
                        });
                        break;
                }
                clanInfo(pl, pl.clan.id);
            }
        } catch (Exception e) {
            Util.debug("ClanService.joinClan");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public void clanDonate(Player pl, int id) {
        try {
            ClanMessage m = pl.clan.getMessage(id);
            Member mb = pl.clan.getMember(m.player_id);
            Player pln = PlayerManger.gI().getPlayerByID(m.player_id);
            Item pean = pl.get_Pean_In_Box();

            if (pean == null) {
                Service.gI().sendThongBao(pl, "Trong rương không có đậu thần");
                return;
            }

            mb.recieve_pean++;
            m.recieve = mb.recieve_pean;
            m.send_message();
            if (mb.recieve_pean >= m.maxCap) {
                mb.recieve_pean = 0;
            }

            pl.inventory.subQuantityItemsBox(pean, 1);
            pl.inventory.sendItemBox();

            Item it = new Item();
//            it.id = pean.id;
            it.info = pean.info;
            it.itemOptions = pean.itemOptions;
            it.template = pean.template;
            it.quantity = 1;

            pln.inventory.addItemBag(it);
            pln.inventory.sortItemBag();
            pln.inventory.sendItemBags();

            Service.gI().sendThongBao(pln, pl.name + " đã cho bạn " + it.template.name);

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void LeaveClan(Player plLeave) {
        Member mem = plLeave.clan.getMember(plLeave.id);
        if (mem != null && mem.role == 0) {
            Service.gI().sendThongBao(plLeave, "Bạn không thể rời bang lúc này");
        }
        else {
            Clan cl = plLeave.clan;
            clanMessage(plLeave, (byte) 0, plLeave.name + " đã rời khỏi bang", cl.id);
            updateInfo(1, mem, cl);
            ClanManager.gI().removeMember(mem.id, cl);
            plLeave.clan = null;
            clanInfo(plLeave, -1);
            PlayerDAO.UpdateInfoPlayer(plLeave);
            cl.members.stream().filter(m -> m != null).forEach(m -> {
                clanInfo(PlayerDAO.getInfobyID(m.id), cl.id);
            });
        }
    }

    public void clanRemote(Player pl, int id, byte role) {
        Clan cl = pl.clan;
        Member m1 = cl.getMember(pl.id);
        Player pl2 = PlayerDAO.getInfobyID(id);
        Member memTT = cl.getMember(id);
        if (m1 == null || memTT == null || m1.role >= memTT.role) {
            return;
        }
        try {
            switch (role) {
                case -1: // kick member
                    ClanManager.gI().removeMember(id, cl);
                    pl2.clan = null;
                    clanInfo(pl2, -1);
                    PlayerDAO.UpdateInfoPlayer(pl2);
                    cl.members.stream().filter(mem -> mem != null).forEach(mem -> {
                        Player plMem = PlayerManger.gI().getPlayerByID(mem.id);
                        if(plMem != null){
                            clanInfo(plMem, cl.id);
                        }
                    });
                    clanMessage(pl, (byte) 0, pl.name + " đã loại " + pl2.name + " khỏi bang!", pl.clan.id);
                    break;
                case 0: // phong chu bang
                    if (m1.role != 0) {
                        return;
                    }
                    m1.role = 2;
                    memTT.role = 0;
                    cl.leaderID = memTT.id;
                    clanMessage(pl, (byte)0, "Đã nhường chủ bang cho " + pl2.name, pl.clan.id);
                    clanInfo(pl, cl.id);
                    cl.members.stream().forEach((mems) -> {
                        Player plMem = PlayerManger.gI().getPlayerByID(mems.id);
                        if(plMem != null){
                            clanInfo(plMem, cl.id);
                        }
                    });
                    break;
                case 1: // phong pho bang
                    memTT.role = 1;
                    clanMessage(pl, (byte)0, "Ðã phong phó bang cho " + pl2.name, pl.clan.id);
                    break;
                case 2: // cat chuc
                    memTT.role = 2;
                    clanMessage(pl, (byte) 0, "Ðã cắt chức của " + pl2.name, pl.clan.id);
                    break;
            }
            updateInfo(2, memTT, cl);
        } catch (Exception e) {
            Util.debug("ClanService.clanRemote");
            e.printStackTrace();
        }
    }

    public void clanMessage(Player pl, byte type, String text, int id) {
        try {
            if (type == 1 && !Util.canDoWithTime(pl.lastTimeXinDau, 300000)) {
                int timeLeft = (int) ((300000 - (System.currentTimeMillis() - pl.lastTimeXinDau)) / 1000);
                Service.gI().sendThongBao(pl, "Vui lòng đợi " + (timeLeft < 60 ? timeLeft + " giây" : timeLeft / 60 + " phút") + " nữa");
                return;
            }
            Clan cl = pl.clan == null ? ClanManager.gI().getClanById(id) : pl.clan;
            ClanMessage m = new ClanMessage(pl, cl);
            m.type = type;
            if(type == 2 && cl.members.size() >= cl.maxMember){
                Service.gI().sendThongBao(pl, "Bang hội đã đầy người");
                return;
            }
            switch (type) {
                case 0: // chat
                    m.text = text;
                    m.color = 0;
                    break;
                case 1: // xin dau
                    m.text = "Xin đậu " + pl.name;
                    m.recieve = cl.getMember(pl.id).recieve_pean;
                    m.maxCap = 5;
                    pl.lastTimeXinDau = System.currentTimeMillis();
                    break;
                case 2: // xin vao bang
//                    m.text = pl.name+"-"+cl.name;
                    break;
            }
            m.send_message();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void clanAction(Player pl, byte action, int id, String text) {
        Message msg = null;
        try {
            switch (action) {
                case 2: //tao clan
                    if(ClanManager.gI().getClanByName(text) != null){
                        Service.gI().sendThongBao(pl, "Tên bang hội đã tồn tại");
                        return;
                    }
                    Clan clan = new Clan();
                    clan.name = text;
                    clan.slogan = "";
                    clan.imgID = (byte) id;
                    clan.powerPoint = 0;
                    clan.leaderID = (int) pl.id;
                    clan.maxMember = 10;
                    clan.level = 0;
                    clan.clanPoint = 0;
                    clan.currMember = 1;

                    Member member = new Member(pl, 0);
                    clan.members.add(member);
                    clan = PlayerDAO.createClan(clan);
                    pl.clan = clan;
                    pl.timeJoinClan = System.currentTimeMillis();
                    ClanManager.gI().addClan(clan);
                    clanInfo(pl, pl.clan.id);

                    msg = new Message(-61);
                    msg.writer().writeInt((int) pl.id); // id leader
                    msg.writer().writeInt(clan.id);// id clan
                    pl.sendMessage(msg);
                    break;
                case 1:
                case 3:
                    msg = new Message(-46);
                    msg.writer().writeByte(action);
                    msg.writer().writeByte(ClanManager.getClanImgs().size());
                    for (ClanImage cl : ClanManager.getClanImgs()) {
                        if (cl.id != -1 && cl.type != 1) {
                            msg.writer().writeByte(cl.id);
                            msg.writer().writeUTF(cl.name);
                            msg.writer().writeInt(cl.xu);
                            msg.writer().writeInt(cl.luong);
                        }
                    }
                    pl.sendMessage(msg);
                    break;
                case 4:  // doi bieu tuong & slogan
                    Member mem = pl.clan.getMember(pl.id);
                    if(mem != null){
                        if (mem.role != 0) {
                            return;
                        }
                        msg = new Message(-46);
                        msg.writer().writeByte(4);
                        pl.clan.imgID = (byte) id;
                        msg.writer().writeByte(id);
                        if (!Util.isNullOrEmpty(text)) {
                            msg.writer().writeUTF(text);
                            pl.clan.slogan = text;
                        } else {
                            msg.writer().writeUTF(pl.clan.slogan);
                        }
                        pl.sendMessage(msg);
                        Service.gI().sendBag(pl);
                    }else{
                        ClanManager.gI().removeMember(id, pl.clan);
                        pl.clan = null;
                        clanInfo(pl, -1);
                        PlayerDAO.UpdateInfoPlayer(pl);
                    }
                    break;

            }

        } catch (Exception e) {
            Util.debug("ClanService.clanAction");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }

    }

    public void updateInfo(int action, Member m, Clan clan) {
        Message msg = null;
        try{
            for (Player pl : PlayerManger.gI().getPlayers()) {
                if (pl.session != null && pl != null && pl.clan != null && pl.clan.id == clan.id) {
                    msg = new Message(-52);
                    msg.writer().writeByte(action);
                    if (action == 0 || action == 2) {
                        Player mem = PlayerDAO.getInfobyID(m.id);
                        msg.writer().writeInt((int) m.id);
                        msg.writer().writeShort(mem.getHead());
                        if (pl.session.get_version() > 214) {
                            msg.writer().writeShort(-1);
                        }
                        msg.writer().writeShort(mem.getLeg());
                        msg.writer().writeShort(mem.getBody());
                        msg.writer().writeUTF(mem.name);
                        msg.writer().writeByte(m.role);
                        msg.writer().writeUTF(Util.powerToString(mem.point.power));
                        msg.writer().writeInt((int) m.donate);
                        msg.writer().writeInt((int) m.receiveDonate);
                        msg.writer().writeInt((int) m.clanPoint);
                        msg.writer().writeInt((int) m.joinTime);
                    } else {
                        for (int i = 0; i < clan.members.size(); i++) {
                            if (clan.members.get(i).id == m.id) {
                                msg.writer().writeByte(i);
                                break;
                            }
                        }
                    }
                    pl.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {
            Util.debug("ClanService.updateInfo");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void clanInfo(Player pl, int id) {
        Message msg = null;
        try {
            msg = new Message(-53);
            Clan clan = pl.clan;
            if (id == -1 || clan == null) {
                msg.writer().writeInt(-1);
            }
            else
            {
                List<Member> members = clan.members;
                msg.writer().writeInt(clan.id);
                msg.writer().writeUTF(clan.name);
                msg.writer().writeUTF(clan.slogan);
                msg.writer().writeByte(clan.imgID);
                msg.writer().writeUTF(Util.powerToString(clan.powerPoint));
                msg.writer().writeUTF(PlayerDAO.getInfobyID(clan.leaderID).name);//leaderName
                msg.writer().writeByte(members.size());
                msg.writer().writeByte(clan.maxMember);
                Member memb = ClanManager.gI().getMember(clan, (int)pl.id);
                msg.writer().writeByte(memb == null ? 1 : memb.role);
                msg.writer().writeInt(clan.clanPoint);
                msg.writer().writeByte(clan.level);
                for (Member member : members) {
                    Player mem = PlayerDAO.getInfobyID(member.id);
                    if(mem != null){
                        msg.writer().writeInt((int) member.id);
                        msg.writer().writeShort(mem.getHead());
                        if (pl.session == null || pl.session.get_version() > 214) {
                            msg.writer().writeShort(-1);
                        }
                        msg.writer().writeShort(mem.getLeg());
                        msg.writer().writeShort(mem.getBody());
                        msg.writer().writeUTF(mem.name); // playerName
                        msg.writer().writeByte(member.role);
                        msg.writer().writeUTF(Util.powerToString(mem.point.power));
                        msg.writer().writeInt((int)member.donate);
                        msg.writer().writeInt((int)member.receiveDonate);
                        msg.writer().writeInt((int)member.clanPoint);
                        msg.writer().writeInt(0);
                        msg.writer().writeInt((int)member.joinTime);
                    }
                }
                msg.writer().writeByte(clan.messages.size());
                for (int i = clan.messages.size() - 1; i >= 0; i--)
                {
                    ClanMessage m = clan.messages.get(i);
                    if(m != null && !Util.isNullOrEmpty(m.text)){
                        msg.writer().writeByte(m.type);
                        msg.writer().writeInt(m.id);
                        msg.writer().writeInt(m.player_id);
                        msg.writer().writeUTF(m.player_name);
                        msg.writer().writeByte(m.role);
                        msg.writer().writeInt(m.time);

                        switch (m.type) {
                            case 0: // chat
                                msg.writer().writeUTF(m.text);
                                msg.writer().writeByte(m.color);
                                break;
                            case 1: // xin dau
                                msg.writer().writeByte(m.recieve);
                                msg.writer().writeByte(m.maxCap);
                                msg.writer().writeByte(0);
                                break;
                            case 2: // xin vao bang
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.logException(ClanService.class, e);
        } finally {
            Service.gI().sendBag(pl);
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }

    }

    public void clanMember(Session session, int clanId) {
        Message msg;
        List<Member> members = ClanManager.gI().getMemberByIdClan(clanId);
        try {
            msg = new Message(-50);
            msg.writer().writeByte(members.size());
            for (Member member : members) {
                Player mem = PlayerDAO.getInfobyID(member.id);
                msg.writer().writeInt(member.id);
                msg.writer().writeShort(mem.getHead());
                if (session.version > 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(mem.getLeg());
                msg.writer().writeShort(mem.getBody());
                msg.writer().writeUTF(mem.name);//playerName
                msg.writer().writeByte(member.role);
                msg.writer().writeUTF(Util.powerToString(mem.point.power));
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt((int) member.joinTime);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void searchClan(Player pl, String text) {
        Message msg = null;
        try {
            if (pl.clan != null) {
                ClanService.gI().clanInfo(pl, pl.clan.id);
                return;
            }

            List<Clan> clans = ClanManager.gI().search(text);
            msg = new Message(-47);
            if(clans == null){
                msg.writer().writeByte(0);
            }
            else{
                msg.writer().writeByte(clans.size());
                for (Clan clan : clans) {
                    msg.writer().writeInt(clan.id);
                    msg.writer().writeUTF(clan.name);
                    msg.writer().writeUTF(clan.slogan);
                    msg.writer().writeByte(clan.imgID);
                    String powerPoint = Util.powerToString(clan.powerPoint);
                    msg.writer().writeUTF(powerPoint);
                    Player memb = PlayerDAO.getInfobyID(clan.leaderID);
                    msg.writer().writeUTF(memb == null ? "(KHÔNG XÁC ĐỊNH)" : memb.name);//leaderName
                    int currMember = clan.members.size();
                    msg.writer().writeByte(currMember);
                    msg.writer().writeByte(clan.maxMember);
                    msg.writer().writeInt((int) clan.time);
                }
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            Util.log("--------------searchClan-------------");
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

}
