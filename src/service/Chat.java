package service;

import java.io.IOException;
import real.func.ChangeMap;
import real.func.MiniGame;
import real.item.Item;
import real.item.ItemData;
import real.magictree.MabuEgg;
import real.map.Mob;
import real.player.Player;
import real.player.PlayerManger;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Controller;
import server.SQLManager;
import server.ServerManager;
import server.Service;
import server.Util;
import server.io.Message;
import service.DAOS.PetDAO;
import service.DAOS.PlayerDAO;

public class Chat {

    public static boolean isBaoTri = false;
    public static boolean isReset = false;
    public static int minuteBaoTri = -1;
    
    public static void onChat(String text, Player pl) throws IOException{
        if (pl.role >= Setting.ROLE_ADMIN) {
           if(text.startsWith("eff")){
                String item = text.replace("eff", "");
               int x = pl.zone.map.pxw / 10;
               Service.gI().sendEff(pl, Integer.valueOf(item), x, pl.zone.LastY(x, 100));
               return;
          }
           else if (text.equals("test_map")){
                pl.zone.map.test();
                return;
            }
           if(text.equals("map")) {
               pl.zone.map.test();
               return;
            }
            if (text.equals("giveit")) {
                if(pl.role == Setting.ROLE_ADMIN_VIP){
                    Service.gI().sendInputText(pl, "Tặng quà", 3, new int[]{1,1,1}, new String[]{"Tên nhận vật","ID item","Số lượng"});
                    return;
                }
            }
            else if(text.equals("newskill")){
                Message msg = null;
                Skill curSkill = SkillUtil.createSkill(pl.gender == 0 ? 24 : pl.gender == 1 ? 26 : 25, 1, 0, 0);
                pl.playerSkill.skills.add(curSkill);
                Service.gI().sendSkillPlayer(pl, curSkill);
            }
            else if (text.equals("reset")) {
                if(pl.role == Setting.ROLE_ADMIN_VIP){
                    minuteBaoTri = Integer.valueOf(text.replace("reset", ""));
                    if (minuteBaoTri > 0) {
                        isBaoTri = !isBaoTri;
                        isReset = !isReset;
                        Service.gI().sendThongBao(pl, "Bảo trì hệ thống: " + (isBaoTri ? "Bật" : "Tắt"));
                        if(isBaoTri)
                        {
                            Service.gI().sendThongBaoAll("Hệ thống sẽ bảo trì sau " + Chat.minuteBaoTri + " phút");
                        }
                        return;
                    }
                }
            }
            else if (text.equals("sendpower"))
            {
                Service.gI().SendPowerInfo(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.equals("next_game")) {
                Service.gI().sendThongBao(pl, "Con số may mắn là: " + MiniGame.gI().MiniGame_S1.result_next);
                return;
            }
            else if (text.equals("remob")) {
                // pl.zone.mobs
                for(Mob mob : pl.zone.mobs){
                    mob.sethp(0);
                    mob.setDie();
                }
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.equals("sendmabu")) {
                Service.gI().sendtMabuEff(pl, 101);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.equals("nv")) {
                Service.gI().send_task_next(pl);
                return;
            }
            else if (text.equals("nr")) {
                for (int i = 0; i < 7; i++) {
                    pl.inventory.addItemBag(ItemData.gI().get_item(14 + i));
                }
                pl.inventory.sortItemBag();
                pl.inventory.sendItemBags();
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.equals("cpl")) {
                Service.gI().sendThongBao(pl, "Số player đang online: " + PlayerManger.gI().getPlayers2().size());
                Service.gI().sendThongBaoBenDuoi("Số player đang online: " + PlayerManger.gI().getPlayers2().size());
                return;
            }
            else if (text.equals("cip")) {
                Service.gI().sendThongBao(pl, "Số Session đang kết nối: " + ServerManager.Sessions.size());
                Service.gI().sendThongBaoOK(pl, "Số Session đang kết nối: " + ServerManager.Sessions.size());
                return;
            }
            else if (text.startsWith("getit")) {
                String[] item = text.replace("getit", "").split("_");
                if (item.length < 2) {
                    Service.gI().sendThongBaoOK(pl, "Lệnh get item: 'getitX_Y' (X là id item , Y là số lượng)");
                    return;
                }
                Item it = ItemData.gI().get_item(Integer.valueOf(item[0]));
                if(it != null){
                    it.quantity = !Util.isNullOrEmpty(item[1]) ? Integer.valueOf(item[1]) : 1;
                    pl.inventory.addItemBag(it);
                    pl.inventory.sortItemBag();
                    pl.inventory.sendItemBags();
                    Service.gI().sendThongBao(pl, "Đã nhận được " + it.template.name);
                }
                return;
            }
            else if (text.startsWith("m")) {
                String item = text.replace("m", "");
                if (item.isEmpty())
                {
                    Service.gI().sendThongBaoOK(pl, "Lệnh m: 'mX' (X là mapID)");
                    return;
                }
                ChangeMap.gI().changeMap(pl, Integer.valueOf(item),-1, 300);
                return;
            }
            else if (text.startsWith("sm_")) {
                pl.point.power = Long.valueOf(text.replace("sm_", ""));
                Controller.gI().sendInfo(pl);
                return;
            }
            else if (text.startsWith("tn_")) {
                pl.point.tiemNang = Long.valueOf(text.replace("tn_", ""));
                Service.gI().point(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.startsWith("hp_")) {
                pl.point.hpGoc = Integer.valueOf(text.replace("hp_", ""));
                Service.gI().point(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.startsWith("mp_")) {
                pl.point.mpGoc = Integer.valueOf(text.replace("mp_", ""));
                Service.gI().point(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.startsWith("sd_")) {
                pl.point.dameGoc = Integer.valueOf(text.replace("sd_", ""));
                Service.gI().point(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.startsWith("giap_")) {
                pl.point.defGoc = Short.valueOf(text.replace("giap_", ""));
                Service.gI().point(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.startsWith("cm_")) {
                pl.point.critGoc = Byte.valueOf(text.replace("cm_", ""));
                Service.gI().point(pl);
                Service.gI().sendThongBao(pl, "Thao tác hoàn tất!");
                return;
            }
            else if (text.startsWith("banctg_")) {
                String playerBan = text.replace("banctg_", "");
                Player b = (Util.isStringInt(playerBan) ? PlayerDAO.getInfobyID(Integer.valueOf(playerBan)) : PlayerDAO.getInfobyName(playerBan));
                SQLManager.execute("UPDATE `player` SET role='" + Setting.ROLE_BAN_CTG + "' WHERE `id`='" + b.id + "'");
                if (PlayerManger.gI().getPlayerByID(b.id) != null) {
                    b.role = Setting.ROLE_BAN_CTG;
                }
                Service.gI().sendThongBao(pl, "Đã ban chat thế giới " + b.name);
                return;
            }
            else if (text.startsWith("ban_")) {
                String playerBan = text.replace("ban_", "");
                Player b = (Util.isStringInt(playerBan) ? PlayerDAO.getInfobyID(Integer.valueOf(playerBan)) : PlayerDAO.getInfobyName(playerBan));
                SQLManager.execute("UPDATE `player` SET role='1' WHERE `name`='" + b.name + "'");
                Service.gI().sendThongBao(pl, "Đã ban " + b.name);
                if (PlayerManger.gI().getPlayerByID(b.id) != null) {
                    b.session.disconnect();
                }
                return;
            }
            else if (text.startsWith("banchat_")) {
                String playerBan = text.replace("banchat_", "");
                Player b = (Util.isStringInt(playerBan) ? PlayerDAO.getInfobyID(Integer.valueOf(playerBan)) : PlayerDAO.getInfobyName(playerBan));
                SQLManager.execute("UPDATE `player` SET role='" + Setting.ROLE_BAN_CHAT + "' WHERE `id`='" + b.id + "'");
                if (PlayerManger.gI().getPlayerByID(b.id) != null) {
                    b.role = Setting.ROLE_BAN_CHAT;
                }
                Service.gI().sendThongBao(pl, "Đã cấm chat " + b.name);
                return;
            }
            else if (text.startsWith("banall_")) {
                String playerBan = text.replace("banall_", "");
                Player b = (Util.isStringInt(playerBan) ? PlayerDAO.getInfobyID(Integer.valueOf(playerBan)) : PlayerDAO.getInfobyName(playerBan));
                SQLManager.execute("UPDATE `player` SET role='" + Setting.ROLE_BAN_ALL_CHAT + "' WHERE `id`='" + b.id + "'");
                if (PlayerManger.gI().getPlayerByID(b.id) != null) {
                    b.role = Setting.ROLE_BAN_ALL_CHAT;
                }
                Service.gI().sendThongBao(pl, "Đã chặn chat của " + b.name);
                return;
            }
            else if (text.startsWith("unban_")) {
                String playerBan = text.replace("unban_", "");
                Player b = (Util.isStringInt(playerBan) ? PlayerDAO.getInfobyID(Integer.valueOf(playerBan)) : PlayerDAO.getInfobyName(playerBan));
                SQLManager.execute("UPDATE `player` SET role='0' WHERE `id`='" + b.id + "'");
                if (b != null) {
                    b.role = 0;
                }
                Service.gI().sendThongBao(pl, "Đã gỡ tất cả lệnh ban " + b.name);
                return;
            }
            else if (text.startsWith("baotri_")) {
                minuteBaoTri = Integer.valueOf(text.replace("baotri_", ""));
                if(minuteBaoTri > 0)
                {
                    isBaoTri = !isBaoTri;
                    Service.gI().sendThongBao(pl, "Bảo trì hệ thống: " + (isBaoTri ? "Bật" : "Tắt"));
                    for (Player player : PlayerManger.gI().getPlayers2()) {
                        if (player != null) {
                            if(Chat.minuteBaoTri > 0){
                                Service.gI().sendThongBao(player, "Hệ thống sẽ bảo trì sau " + Chat.minuteBaoTri + " phút");
                            }
                        }
                    }
                    return;
                }
                Service.gI().sendThongBao(pl, "Số phút phải lớn hơn 0");
                return;
            }
            else if (text.equals("check")) {
                Service.gI().sendThongBao(pl, "Tọa độ: " + pl.x + "-" + pl.y);
                return;
            }
            else if (text.equals("ctg")) {
                Setting.LOG_CHAT_GLOBAL = !Setting.LOG_CHAT_GLOBAL;
                Service.gI().sendThongBao(pl, "Đóng hệ thống chat: " + (Setting.LOG_CHAT_GLOBAL ? "Bật" : "Tắt"));
                return;
            }
            else if(text.contains("m_")){
                int mapid = Integer.valueOf(text.replace("m_", ""));
                ChangeMap.gI().changeMap(pl, mapid, -1, 5);
                return;
            }
            else if(text.equals("mabu")){
               // if(pl.mabuEgg == null){
                    pl.mabuEgg = new MabuEgg(pl,System.currentTimeMillis(),86400);
                    Service.gI().sendThongBao(pl, "Thành công");
             //   }
                return;
            }
        }
        if (text.startsWith("ten con la ")) {
            String namePet = (text.replace("ten con la ", ""));
            if (!Util.CheckString(namePet, "^[a-z0-9]+$")) {
                Service.gI().sendThongBaoOK(pl, "Tên nhân vật không phù hợp");
                return;
            }
            else if(namePet.length() < 5 || namePet.length() > 10){
                Service.gI().sendThongBao(pl, "Tên đệ tử phải a-z, 0-9 và 5 - 10 ký tự");
                return;
            }
            if (PetDAO.renamePet(pl, namePet)) {
                Service.gI().sendThongBao(pl, "Bạn đã đổi tên đệ tử thành công!");
            }
        }
        if (text.equals("di theo") || text.equals("follow")) {
            if (pl.pet != null) {
                pl.pet.changeStatus((byte)0);
            }
        }
        if (text.equals("bao ve") || text.equals("protect")) {
            if (pl.pet != null) {
                pl.pet.changeStatus((byte)1);
            }
        }
        if (text.equals("tan cong") || text.equals("attack")) {
            if (pl.pet != null) {
                pl.pet.changeStatus((byte)2);
            }
        }
        if (text.equals("ve nha") || text.equals("gohome")) {
            if (pl.pet != null) {
                pl.pet.changeStatus((byte)3);
            }
        }
        
        if (text.equals("bien hinh") || text.equals("bienhinh")) {
            if (pl.pet != null && pl.pet.isMabu != -1) {
                pl.pet.isBien = !pl.pet.isBien;
                Service.gI().Send_Caitrang(pl.pet);
            }
        }
        
        Service.gI().chat(pl, text);
    }
    
    public static void giveIt(Player me ,String name , String itemId , String quantity){
        Player pl = PlayerDAO.load(name);
        Util.debug("BUFF: " + name);
        if(pl != null){
            String[] id = itemId.split("-");
            String[] q = quantity.split("-");
            if(id.length != q.length){
                Service.gI().sendThongBao(me, "Dữ liệu đầu vào không chính xác");
                return;
            }
            int size = id.length;
            for(int i = 0 ; i < size ;i++){
                Item it = ItemData.gI().get_item(Integer.parseInt(id[i]));
                if(it != null){
                    it.quantity = Integer.parseInt(q[i]);
                    pl.inventory.addItemBag(it);
                    if(PlayerManger.gI().getPlayerByName(name) != null){
                        pl.inventory.sortItemBag();
                        pl.inventory.sendItemBags();
                        Service.gI().sendThongBao(pl, "Bạn được tặng " + it.template.name);
                    }else{
                        PlayerDAO.updateDB2(pl);
                    }
                    Service.gI().sendThongBao(me, "Tặng thành công");
                }
            }
        }else{
            Service.gI().sendThongBao(me, "Không tìm được người chơi");
        }
    }
}
