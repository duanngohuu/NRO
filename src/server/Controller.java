package server;

import java.sql.Timestamp;
import server.io.Message;
import server.io.Session;
import java.io.IOException;
import java.sql.ResultSet;
import real.clan.ClanService;
import real.func.ChangeMap;
import real.func.DHVT;
import real.func.DHVT_Template;
import real.func.GiftCode;
import real.func.MiniGame;
import real.func.NRNM;
import real.func.PVP;
import real.func.Shop;
import real.func.Transaction;
import real.func.UseItem;
import real.item.ItemData;
import real.map.ItemMap;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Player;
import service.DAOS.PlayerDAO;
import real.player.PlayerManger;
import real.skill.Skill;
import real.task.TaskBoMongService;
import real.task.TaskData;
import service.Chat;
import service.Setting;
import server.io.IMessageHandler;

@SuppressWarnings("ALL")
public class Controller implements IMessageHandler {

    private static Controller instance;

    public static Controller gI() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    @Override
    public void onConnectOK() {
    }

    @Override
    public void onConnectionFail() {
    }

    @Override
    public void onDisconnected(Session ss) {
        logout(ss);
    }
    public long currCTG;

    public void onMessage(Session _session, Message _msg) {
        try {
            Player player = _session.player;//PlayerManger.gI().getPlayerByUserID(_session.userId);
            Transaction tran = null;
            byte cmd = _msg.getCommand();
            if(player != null){
                tran = Transaction.gI().findTran(player);
            }
//            if(cmd != -28 && cmd != -29 && cmd != -30){
//                Util.debug("CMD: " + cmd);
//            }
            switch (cmd) {
                case 127: // radar
                    int active = (int)_msg.reader().readByte();
                    int ID = -1;
                    if(active == 1){
                        ID = (int)_msg.reader().readShort();
                    }
                    Service.gI().SendRadar(player, active, ID);
                    break;
                case -125:
                    byte size = _msg.reader().readByte();
                    player.input = new String[size];
                    for(int i = 0 ; i < size ;i++){
                        player.input[i] = _msg.reader().readUTF();
                    }
                    if(player.typeInput == 0 && player.input.length == 1 && !Util.isNullOrEmpty(player.input[0])){
                        GiftCode.claimGift(player,player.input[0]);
                    }
                    if(player.typeInput == 10 && player.input.length == 1 && Util.isStringInt(player.input[0])){
                        int level = Integer.parseInt(player.input[0]);
                        if(level > 0 && level <= 110){
                            Npc.createMenuConMeo(player, NpcFactory.KHI_GAS_HUY_DIET, 2132, "Bạn có chắc chắn muốn vào ở level: " + level, "Ok", "Đóng");
                        }
                    }
                    if(player.typeInput == 11 && player.input.length == 1 && Util.isStringInt(player.input[0])){
                        int level = Integer.parseInt(player.input[0]);
                        if(level > 0 && level <= 110){
                            Npc.createMenuConMeo(player, NpcFactory.KHO_BAU_DUOI_BIEN, 564, "Bạn có chắc chắn muốn vào ở level: " + level, "Ok", "Đóng");
                        }
                    }
                    // typeInput == 20 - 1 số
                    if(player.typeInput == 20 && player.input.length == 1 && Util.isStringInt(player.input[0])){
                        int CSMM = Integer.parseInt(player.input[0]);
                        if(CSMM >= MiniGame.gI().MiniGame_S1.min && CSMM <= MiniGame.gI().MiniGame_S1.max && MiniGame.gI().MiniGame_S1.second > 10){
                            MiniGame.gI().MiniGame_S1.newData(player, CSMM);
                        }
                    }
                    if(player.input.length == 3 && player.role == Setting.ROLE_ADMIN_VIP){
                        Chat.giveIt(player, player.input[0],player.input[1],player.input[2]);
                    }
                    player.typeInput = -1;
                    break;
                case 66:
                    String ni = _msg.reader().readUTF();
                    Service.gI().getImgByName(_session, ni);
                    break;
                case 112:
                    Npc.createMenuConMeo(player, NpcFactory.SPECIAL_SKILL, -1, "Nội tại là một kỹ năng bị động hỗ trợ đặc biệt\nBạn có muốn mở hoặc thay đổi nội tại hay không?", "Xem\ntất cả\nNội Tại", "Mở\nNội Tại", "Mở VIP", "Từ Chối");
                    break;
                case -105:
                    if(player.type == 0 && player.maxTime == 30){// Tương lai
                        ChangeMap.gI().changeMap(player, 102, 100, 336); 
                    }
                    else if(player.type == 1 && player.maxTime == 5){// Hành tinh thực vật
                        ChangeMap.gI().changeMap(player, 160, -1, 5); 
                    }
                    else if(player.type == 2 && player.maxTime == 5){// Mảnh hồn bông tai
                        ChangeMap.gI().changeMap(player, 156, 836, 5); 
                    }
                    else if(player.type == 3 && player.maxTime == 5){// Khí gas hủy diệt
                        if(player.clan.KhiGasHuyDiet != null && player.clan.KhiGasHuyDiet.time > 0){
                            ChangeMap.gI().changeMap(player , 149, player.clan.KhiGasHuyDiet.zonePhuBan, 100, 300,ChangeMap.NON_SPACE_SHIP);
                        }
                    }
                    else if(player.type == 4 && player.maxTime == 5){// Khí gas hủy diệt
                        if(player.clan.KhiGasHuyDiet != null && player.clan.KhiGasHuyDiet.time > 0){
                            ChangeMap.gI().changeMapWaypoint(player);
                        }
                    }
                    else if(player.type == 5 && player.maxTime == 10){// Kho báu dưới biển
                        ChangeMap.gI().changeMap(player , 135, player.clan.KhoBauDuoiBien.zonePhuBan, 80, 5,ChangeMap.NON_SPACE_SHIP);
                    }
                    break;
                case -127:
                    byte typequay = _msg.reader().readByte();
                    byte soluong = 0;
                    try {
                        soluong = _msg.reader().readByte();
                    } catch (Exception e) {
                    }
                    if (soluong == 0) {
                        Service.gI().LuckyRound(player, 0, soluong);
                    }
                    if (soluong > 0) {
                        int ngoc = soluong * 4;
                        boolean isQuay;
                        if (player.inventory.ruby >= ngoc) {
                            player.inventory.ruby -= ngoc;
                            isQuay = true;
                        } else if (player.inventory.gem >= ngoc) {
                            player.inventory.gem -= ngoc;
                            isQuay = true;
                        } else {
                            Service.gI().sendThongBao(player, "Bạn Không Đủ Ngọc Để Quay");
                            isQuay = false;
                        }
                        if (isQuay) {
                            Service.gI().sendMoney(player);
                            Service.gI().LuckyRound(player, 1, soluong);
                        }
                    }
                    break;
                case -100:
                    Shop.gI().Kigui_onMessage(_msg, player);
                    break;
                case -99:
                    player.listPlayer.controllerEnemy(_msg);
                    break;
                case 18:
                    player.listPlayer.goToPlayerWithYardrat(_msg);
                    break;
                case -72:
                    player.listPlayer.chatPrivate(_msg);
                    break;
                case -80:
                    player.listPlayer.controllerFriend(_msg);
                    break;
                case -53:
                    short requestID = _msg.reader().readShort();
                    // Util.debug(requestID + "");
                    break;
                case -57:
                    byte actionInvite = _msg.reader().readByte();
                    int plInv = -1;
                    int clanInv = -1;
                    int codeInv = -1;
                    if(actionInvite == 0){
                        plInv = _msg.reader().readInt();
                        ClanService.gI().sendInviteClan(player, plInv ,actionInvite);
                    }else if(actionInvite == 1 || actionInvite == 2){
                        clanInv = _msg.reader().readInt();
                        codeInv = _msg.reader().readInt();
                        ClanService.gI().sendJoinClan(clanInv, codeInv);
                    }
                    break;
                case -59:
                    PVP.gI().controller(player, _msg);
                    break;
                case -86:
                    if (player != null) {
                        if(player.session.get_version() < 222){
                            Service.gI().sendThongBao(player, "Vui lòng tải phiên bản từ v2.2.2 trở lên để có thể giao dịch");
                        }
                        else if(Chat.isBaoTri)
                        {
                            Service.gI().sendThongBao(player, "Không thể thực hiện hệ thống đang trong quá trình bảo trì");
                        }
                        else if (player.session.get_active() == 0 && !Setting.SERVER_TEST){
                            Service.gI().sendThongBao(player, "Tài khoản của bạn chưa kích hoạt!");
                        }
                        else if (player.session.get_active() != 0 || Setting.SERVER_TEST){
                            Transaction.gI().controller(player, _msg);
                        }
                    }
                    break;
                case -107:
                    Service.gI().showInfoPet(player);
                    break;
                case -108:
                    if (player.pet != null) {
                        player.pet.changeStatus(_msg.reader().readByte());
                    }
                    break;
                case -76: // Nhận Ngọc Bò Mộng
                    int indexTask = _msg.reader().readByte();
                    TaskBoMongService.gI().getArchivemnt(player, indexTask);
                    break;
                case 7:
                    if(player != null){
                        if(tran == null){
                            int actionSale = _msg.reader().readByte();
                            int typeSale = _msg.reader().readByte();
                            int indexSale = _msg.reader().readShort();
                            Service.gI().SellItem(player, actionSale, typeSale, indexSale);
                        }
                        else{
                            Service.gI().sendThongBao(player, "Không thể thực hiện");
                        }
                    }
                    break;
                case 6: //buy item
                    if(tran == null){
                        int typeIem = _msg.reader().readByte();
                        int idItem = _msg.reader().readShort();
                        int quantity = 0;
                        try {
                            quantity = _msg.reader().readShort();
                        } catch (Exception e) {
                        }
                        if (player.getNPCMenu() == NpcFactory.THUONG_DE) {
                            switch (typeIem) {
                                case 0: // Nhận đồ
                                    Shop.gI().NhanDo(player, idItem);
                                    break;
                                case 1: // xóa đồ
                                    Shop.gI().XoaDo(player, idItem);
                                    break;
                                case 2: // nhận all
                                    Shop.gI().NhanAllDo(player, idItem);
                                    break;
                                default:
                                    break;
                            }
                        }
                        else
                        {
                            Shop.gI().buyItem(player, idItem, typeIem, quantity);
                        }
                    }
                    else{
                        Service.gI().sendThongBaoOK(player, "Không thể thực hiện");
                    }
                    break;
                case 29:
                    Service.gI().openZoneUI(player);
                    break;
                case 21:
                    int zoneId = _msg.reader().readByte();
                    Service.gI().requestChangeZone(player, zoneId);
                    break;
                case -71:
                    String ctg = _msg.reader().readUTF();
                    if(player.role < Setting.ROLE_ADMIN){
                        if (player.session.get_active() == 0){
                            Service.gI().sendThongBao(player, "Tài khoản của bạn chưa kích hoạt!");
                            break;
                        }
                        if (player.role == Setting.ROLE_BAN_CTG || player.role == Setting.ROLE_BAN_ALL_CHAT) {
                            Service.gI().sendThongBaoOK(player, "Bạn đã bị khóa moãm, có thể liên hệ admin để biết lí do :v");
                            break;
                        }
                        if (!Util.canDoWithTime(player.currCTG, 60000)) {
                            Service.gI().sendThongBao(player, "Chat thế giới đang bị quá tải vui lòng thử lại sau vài giây!");
                            break;
                        }
                        if (!Util.canDoWithTime(currCTG, 1000)) {
                            Service.gI().sendThongBao(player, "Chat thế giới đang bị quá tải vui lòng thử lại sau vài giây!");
                            break;
                        }
                        if(player.point.power < 50000000){
                            Service.gI().sendThongBao(player, "Không đủ 50tr sức mạnh để chat thế giới!");
                            break;
                        }
                        if (ctg.length() < 5 || ctg.length() > 150) {
                            Service.gI().sendThongBao(player, "Đoạn chat phải từ 5 đến 150 kí tự");
                            break;
                        }
                        if (player.inventory.getGemAndRuby() < 5) {
                            break;
                        }
                        Service.gI().chatGlobal(player, ctg);
                        player.inventory.subGemAndRuby(5);
                        Service.gI().sendMoney(player);
                        currCTG = System.currentTimeMillis();
                        player.currCTG = System.currentTimeMillis();
                        break;
                    }
                    Service.gI().chatGlobal(player, ctg);
                    break;
                case -79:
                    Service.gI().getPlayerMenu(player, _msg.reader().readInt());
                    break;
                case -113:
                    int sl = player.session.get_version() >= 217 ? 10 : 5;
                    int SkillNum = 0;
                    for (int i = 0; i < sl; i++) {
                        player.playerSkill.skillShortCut[i] = _msg.reader().readByte();
                        if(player.playerSkill.skillShortCut[i] != -1){
                            SkillNum++;
                        }
                    }
                    if(SkillNum <= 2){
                        player.playerSkill.sendSkillShortCut();
                    }
                    break;
                case -101:
                    login2(_session);
                    break;
                case -103:
                    byte act = _msg.reader().readByte();
                    if (act == 0) {
                        Service.gI().openFlagUI(player);
                    } else if (act == 1) {
                        Service.gI().chooseFlag(player, _msg.reader().readByte());
                    } else if (act == 2) {
                        Service.gI().sendFlagUI(player, _msg.reader().readByte());
                    }
                    break;
                case -7:
                    if(player == null)
                    {
                        return;
                    }
                    if (player.isDie()) {
                        Service.gI().charDie(player);
                        break;
                    }
                    if (player.playerSkill.isKame || player.playerSkill.isLienHoanChuong || player.playerSkill.isMafoba) {
                        break;
                    }
                    if(player != null && player.playerSkill.isHaveEffectSkill()){
                        break;
                    }
                    try {
                        byte b = _msg.reader().readByte();
                        player.xSend = player.x;
                        player.ySend = player.y;
                        player.x = _msg.reader().readShort();
                        player.y = _msg.reader().readShort();
                    } catch (Exception e) {
                    }
                    Service.gI().PlayerMove(player);
                    break;
                case -74:
                    byte v = _msg.reader().readByte();
                    Service.gI().sendResource(_session, (int)v);
                    break;
                case -81:
                    if (_msg.reader().readByte() == 1) {
                        int[] indexItem = new int[_msg.reader().readByte()];
                        for (int i = 0; i < indexItem.length; i++) {
                            indexItem[i] = _msg.reader().readByte();
                        }
                        player.combine.showInfoCombine(indexItem);
                    }
                    break;
                case -87:
                    Service.gI().updateData(_session);
                    break;
                case -67:
                    int id = _msg.reader().readInt();
                    Service.gI().requestIcon(_session, id);
                    break;
                case -66:
                    int effId = _msg.reader().readShort();
                    Service.gI().effData(_session, effId);
                    break;
                case -63:
                    if(player != null){
                        ClanService.gI().loadBagImage(player, _msg.reader().readByte());
                    }
                    break;
                case -62:
                    int b = _msg.reader().readByte();
                    if(player != null){
                        ClanService.gI().loadImgClan(player, b);
                    }
                    break;
                case -32:
                    int bgId = _msg.reader().readShort();
                    Service.gI().bgTemp(_session, bgId);
                    break;
                case -33:
                    ChangeMap.gI().changeMapWaypoint(player);
                    break;
                case -34:
                    byte MagicTree = _msg.reader().readByte();
                    if(player.zone.map.id == player.gender + 21){
                        if(MagicTree == 1){
                            MenuController.getInstance().openMenuNPC(4, player);
                        }
                        else if(MagicTree == 2){
                            player.magicTree.displayMagicTree(player);
                        }
                    }
                    break;
                case -23:
                    ChangeMap.gI().changeMapWaypoint(player);
                    break;
                case -45:
                    if(player != null){
                        int status = _msg.reader().readByte();
                        if(status == 20)
                        {
                            byte skillID = _msg.reader().readByte();
                            player.playerSkill.useSkill(_msg.reader().readShort(), _msg.reader().readShort(), _msg.reader().readByte(), _msg.reader().readShort(), _msg.reader().readShort());
                        }
                        else
                        {
                            Service.gI().useSkillNotFocus(player, status);
                        }
                    }
                    break;
                case -46:
                    byte action = _msg.reader().readByte();
                    ClanService.gI().clanAction(player, action, action == 2 || action == 4 ? _msg.reader().readByte() : -1, action == 2 || action == 4 ? _msg.reader().readUTF() : "");
                    break;
                case -50:
                    int clanId = _msg.reader().readInt();
                    ClanService.gI().clanMember(_session, clanId);
                    break;
                case -47:
                    String clanName = _msg.reader().readUTF();
                    ClanService.gI().searchClan(player, clanName);
                    break;
                case -55:
                    if(player.clan.DoanhTrai == null || player.clan.DoanhTrai.time <= 0){
                        ClanService.gI().LeaveClan(player);
                    }
                    else{
                        Service.gI().sendThongBao(player, "Bạn không thể rời bang lúc này");
                    }
                    break;
                case -40:
                    ReadMessage.gI().getItem(_session, _msg);
                    break;
                case -41:
                    //UPDATE_CAPTION
                    Service.gI().CaptionStr(_session);
                    break;
                case -43:
                    if(tran == null)
                    {
                        ReadMessage.gI().useItem(player, _msg);
                    }
                    else
                    {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case -91:
                    UseItem.gI().choseMapCapsule(player, _msg.reader().readByte());
                    break;
                case -38:
                    break;
                case -39:
                    //finishLoadMap
                    ChangeMap.gI().finishLoadMap(player);
                    break;
                case 11:
                    byte modId = _msg.reader().readByte();
                    Service.gI().requestMobTemplate(_session, modId);
                    break;
                case 44:
                    if(!Util.canDoWithTime(player.currOnchat, 1000)){
                        break;
                    }
                    player.currOnchat = System.currentTimeMillis();
                    if (player.role == Setting.ROLE_BAN_CHAT || player.role == Setting.ROLE_BAN_ALL_CHAT) {
                        Service.gI().sendThongBaoOK(player, "Bạn đã bị khóa moãm, có thể liên hệ admin để biết lí do :v");
                        return;
                    }
                    String text = _msg.reader().readUTF();
                    Chat.onChat(text, player);
                    break;
                case 22:
                    if(tran != null){
                        Service.gI().hideInfoDlg(player);
                        Service.gI().sendThongBao(player, "Có lỗi xảy ra khi thực hiện!");
                    }
                    else
                    {
                        int n = _msg.reader().readByte();
                        int m = _msg.reader().readByte();
                        MenuController.getInstance().doSelectMenu(player, n, m);
                    }
                    break;
                case 32:
                    if(tran != null){
                        Service.gI().hideInfoDlg(player);
                        Service.gI().sendThongBao(player, "Có lỗi xảy ra khi thực hiện!");
                    }
                    else{
                        int npcId = _msg.reader().readShort();
                        int select = _msg.reader().readByte();
                        MenuController.getInstance().doSelectMenu(player, npcId, select);
                    }
                    break;
                case 33:
                    int npcIds = _msg.reader().readShort();
                    MenuController.getInstance().openMenuNPC(npcIds, player);
                    break;
                case 34:
                    if(player == null)
                    {
                        break;
                    }
                    if(player.playerSkill.isKame || player.playerSkill.isLienHoanChuong || player.playerSkill.isMafoba)
                    {
                        break;
                    }
                    int selectSkill = _msg.reader().readShort();
                    try {
                        if(player != null){
                            for (Skill skill : player.playerSkill.skills) {
                                if (skill.template.id == selectSkill) {
                                    player.playerSkill.skillSelect = skill;
                                    if(player.playerSkill.count_ttnl != -1 && skill.template.id != 8){
                                        player.playerSkill.stopCharge();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        player.playerSkill.skillSelect = player.playerSkill.skills.get(0);
                    }
                    break;
                case 54:
                    Service.gI().attackMob(player, (int) (_msg.reader().readByte()));
                    break;
                case -60:
                    Service.gI().attackPlayer(player, _msg.reader().readInt());
                    break;
                case -20:
                    if(player != null){
                        int itemMapId = _msg.reader().readShort();
                        Service.gI().pickItem(player, itemMapId);
                    }
                    break;
                case 67:
                    Service.gI().attackMob(player, (int) (_msg.reader().readByte()));
                    Service.gI().attackPlayer(player, _msg.reader().readInt());
                    break;
                case -4:
                    Service.gI().attackMob(player, (int) (_msg.reader().readByte()));
                    Service.gI().attackPlayer(player, _msg.reader().readInt());
                    break;
                case -27:
                    _session.sendSessionKey();
                    Service.gI().sendResource(_session, 0);
                    break;
                case -28:
                    messageNotMap(_session, _msg);
                    break;
                case -29:
                    messageNotLogin(_session, _msg);
                    break;
                case -30:
                    messageSubCommand(_session, _msg);
                    break;
                case -15:
                    player.isGoHome = true;
                    int mapID = player.zone.isOSIN() ? 114 : player.gender + 21;
                    ChangeMap.gI().changeMapBySpaceShip(player, mapID, -1, -1, player.zone.isOSIN() ? 0 : player.typeShip);
                    if(player.zone.isOSIN()){
                        player.point.updateall();
                    }
                    else if(player.isDie()){
                        Service.gI().hsChar(player, 1, 1);
                    }
                    player.isGoHome = false;
                    break;
                case -16:
                    if(player.inventory.getGemAndRuby() > 0){
                        player.inventory.subGemAndRuby(1);
                        Service.gI().hsChar(player, player.point.getHPFull(), player.point.getMPFull());
                    }
                    else{
                        Service.gI().sendThongBao(player, "Bạn không đủ ngọc");
                    }
                    break;
                case -54:
                    ClanService.gI().clanDonate(player, _msg.reader().readInt());
                    break;
                case -51:
                    byte t = _msg.reader().readByte();
                    ClanService.gI().clanMessage(player, t, t == 0 ? _msg.reader().readUTF() : null, t == 2 ? _msg.reader().readInt() : 0);
                    break;
                case -49:
                    ClanService.gI().joinClan(player, _msg.reader().readInt(), _msg.reader().readByte());
                    break;
                case -56:
                    if(player.clan != null){
                        ClanService.gI().clanRemote(player, _msg.reader().readInt(), _msg.reader().readByte());
                    }
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            Util.logException(Controller.class, e);
        }
        finally {
            if (_msg != null) {
                _msg.cleanup();
            }
        }
    }

    public void messageNotMap(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
                byte cmd = _msg.reader().readByte();
//                Util.debug("-28 Type: " + cmd);
                switch (cmd) {
                    case 2:
                        createChar(_session, _msg);
                        break;
                    case 6:
                        Service.gI().updateMap(_session);
                        break;
                    case 7:
                        Service.gI().updateSkill(_session);
                        break;
                    case 8:
                        ItemData.gI().updateItem(_session);
                        break;
                    case 10:
                        if(player != null)
                        {
                            if(player.taskId > 2)
                            {
                                ItemMap.DUIGA(player);
                            }
                            if(player.taskId == 3 && player.taskIndex == 1)
                            {
                                ItemMap.DUABE(player);
                            }
                            Service.gI().mapTemp(player);
                        }
                        break;
                    case 13:
                        Util.debug("Client OK!");
                        if(player == null)
                        {
                            if(_session.isCreateChar){
                                _session.isCreateChar = false;
                                Service.gI().createChar(_session);
                            }
                            return;
                        }
                        player.point.updateall();
                        if(player.pet != null){
                            player.pet.point.updateall();
                        }
                        player.active(Setting.DELAY_PLAYER);
                        sendInfo(player);
                        boolean isTdlt = player.itemTime.ExitsItemTiem(4387);
                        if(isTdlt)
                        {
                            Message msg = new Message(-116);
                            msg.writer().writeByte(isTdlt ? 1 : 0);
                            player.sendMessage(msg);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Util.logException(Controller.class, e);
            }
        }
    }
    
    public void messageNotLogin(Session session, Message msg) {
        if (msg != null) {
            try {
                byte cmd = msg.reader().readByte();
//                Util.debug("-29 Type: " + cmd);
                switch (cmd) {
                    case 0:
                        login(session, msg);
                        break;
                    case 2:
                        session.setClientType(msg);
                        Service.gI().send_list_server(session);
                        break;
                    default:
//                        Util.debug("-29_" + cmd);
                        break;
                }
            } catch (Exception e) {
            }
        }
    }

    public void messageSubCommand(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
                byte cmd = _msg.reader().readByte();
//                Util.debug("-30 Type: " + cmd);
                switch (cmd) {
                    case 0:
                        Service.gI().player(player);
                        break;
                    case 16:
                        byte type = _msg.reader().readByte();
                        short point = _msg.reader().readShort();
                        player.point.upPotential(type, point);
                        break;
                    default:
                        // Service.gI().sendThongBaoOK(player, "[" + command + "] Chức năng này đang phát triển");
                        break;
                }
            } catch (IOException e) {
            }
        }
    }

    public void login(Session session, Message msg) {
        ResultSet rs = null;
        Timestamp lastTimeLogin;
        if (session.isLogin) {
            return;
        }
        try {
            final String user = msg.reader().readUTF().toLowerCase();
            final String pass = msg.reader().readUTF().toLowerCase();
            final String v = msg.reader().readUTF().toLowerCase();
            session.version = Short.valueOf(v.replace(".", ""));
            if(session.version < 199){
                Service.gI().sendThongBaoOK(session, "Vui lòng cập nhật phiên bản mới hơn");
                return;
            }
            msg.reader().readByte();
            msg.cleanup();
            if(Util.CheckString(user, "^[a-z0-9@]+$") && Util.CheckString(pass, "^[a-z0-9@]+$"))
            {
                rs = SQLManager.executeQuery("SELECT * FROM account WHERE username='" + user + "' and password='" + pass + "' limit 1");
                if (rs.first()) {
                    session.userId = rs.getInt("id");
                    session.active = rs.getInt("active");
                    session.account = user;
                    session.pass = pass;
                    if(rs != null){
                        rs.close();
                        rs = null;
                    }
                    rs = SQLManager.executeQuery("SELECT * FROM player WHERE account_id='" + session.userId + "' AND `server_name`='"+Setting.SERVER_NAME+"' LIMIT 1");
                    if (rs.first()) {
                        if (rs.getByte("role") == Setting.ROLE_BAN_ACC) {
                            Service.gI().sendThongBaoOK(session, "Tài khoản đã bị khóa");
                            session.close();
                            if(rs != null){
                                rs.close();
                                rs = null;
                            }
                            return;
                        }
                        else if (rs.getByte("online") == 1) {
                            Player p = PlayerManger.gI().getPlayerByUserID(session.userId);
                            if (p != null) {
                                p.session.disconnect();
                                Service.gI().sendThongBaoOK(session, "Máy chủ tắt hoặc mất sóng");
                                session.close();
                                if(rs != null){
                                    rs.close();
                                    rs = null;
                                }
                                return;
                            }
                            Service.gI().sendThongBaoOK(session, "Bạn đang đăng nhập ở 1 máy chủ khác rồi.");
                            return;
                        }
                        else
                        {
                            Thread.sleep(Util.nextInt(200,1000));
                            long t = rs.getTimestamp("last_logout_time").getTime();
                            if(t > System.currentTimeMillis()){
                                t = System.currentTimeMillis() - 10000;
                            }
                            if (!Util.canDoWithTime(t, 10000)) {
                                Service.gI().loginDe(session,(short)(10 - ((System.currentTimeMillis() - t) / 1000)));
                                session.close();
                                if(rs != null)
                                {
                                    rs.close();
                                    rs = null;
                                }
                                return;
                            }
                            lastTimeLogin = rs.getTimestamp("last_logout_time");
                            Timestamp timeCreate = rs.getTimestamp("create_time");
                            Player player = PlayerDAO.load(session.userId);
                            if(player == null){
                                session.disconnect();
                                if(rs != null){
                                    rs.close();
                                    rs = null;
                                }
                                return;
                            }
                            SQLManager.execute("UPDATE `player` SET `online`='1' WHERE player_id='" + player.id + "'");
                            session.player = player;
                            player.setSession(session);
                            PlayerManger.gI().getPlayers().add(player);
                            if (player.pet != null) {
                                PlayerManger.gI().getPlayers().add(player.pet);
                            }
                            player.zone.getPlayers().add(player);
                            Service.gI().updateVersionx(session);
                            Service.gI().sendMessage(session, -93, "1630679752231_-93_r");
                            Service.gI().updateVersion(session);
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            player.login_time = timestamp;
                            if(Util.findDayDifference(timestamp.getTime(), timeCreate.getTime()) <= Setting.DAY_NEW){
                                player.isNewMember = true;
                            }
                            long milliseconds = player.login_time.getTime() - lastTimeLogin.getTime();
                            int seconds = (int) milliseconds / 1000;
                            int stamina = Setting.MAX_STAMINA;
                            if(seconds >= 0 && seconds < 200000){
                                stamina = seconds / 20;
                            }
                            if (player.point.stamina + stamina > 10000) {
                                player.point.stamina = Setting.MAX_STAMINA;
                            } else {
                                player.point.stamina += stamina;
                            }
                            Service.gI().itemBg(session, 456);
                            session.isLogin = true;
                        }
                    }
                    else
                    {
                        Thread.sleep(Util.nextInt(200,1000));
                        if(session.isSession()){
                            session.disconnect();
                            return;
                        }
                        session.isCreateChar = true;
                        Service.gI().updateVersion(session);
                        Service.gI().itemBg(session, 456);
                        Service.gI().tileSet(session);
                        Util.debug("createChar!");
                        if(rs != null){
                            rs.close();
                            rs = null;
                        }
                        return;
                    }
                    if(rs != null)
                    {
                        rs.close();
                        rs = null;
                    }
                }
                else
                {
                    Service.gI().sendThongBaoOK(session, "Tài khoản mật khẩu không chính xác");
                    session.close();
                }
            }
            else
            {
                Service.gI().sendThongBaoOK(session, "Có lỗi xảy ra khi truy vấn dữ liệu vui lòng thử lại!");
                session.close();
            }
            if(rs != null){
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            Util.logException(Controller.class, e);
        }
    }

    public void createChar(Session session, Message msg)
    {
        try {
            if(session.isSession()){
                Service.gI().sendThongBaoOK(session, "Có lỗi xảy ra khi tạo player vui lòng thử lại sau.");
                return;
            }
            ResultSet rs  = null;
            rs = SQLManager.executeQuery("SELECT * FROM account WHERE username='" + session.account + "' and password='" + session.pass + "' limit 1");
            if (rs.first()) {
                rs.close();
                rs = null;
                String name = msg.reader().readUTF();
                int gender = msg.reader().readByte();
                int hair = msg.reader().readByte();
                msg.cleanup();
                if (name.contains("admin")) {
                    Service.gI().sendThongBaoOK(session, "Tên nhân vật không được đặt quá đặc biệt.");
                    return;
                }
                if (name.length() < 5 || name.length() > 10) {
                    Service.gI().sendThongBaoOK(session, "Tên nhân vật phải từ a-z, 0-9 và 5 - 10 ký tự");
                    return;
                }
                if (!Util.CheckString(name, "^[a-z0-9]+$")) {
                    Service.gI().sendThongBaoOK(session, "Tên nhân vật phải từ a-z, 0-9");
                    return;
                }
                rs = SQLManager.executeQuery("SELECT * FROM player WHERE account_id='" + session.userId + "' AND server_name='"+Setting.SERVER_NAME+"' LIMIT 1");
                if (!rs.first()) {
                    rs.close();
                    rs = null;
                    rs = SQLManager.executeQuery("SELECT * FROM player WHERE name='" + name + "'");
                    if (!rs.first()) {
                        if (PlayerDAO.create(session.userId, name, gender, hair)) {
                            Thread.sleep(1000);
                            Player player = PlayerDAO.load(session.userId);
                            if(player != null){
                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                player.login_time = timestamp;
                                Thread.sleep(Util.nextInt(200,1000));
                                SQLManager.execute("UPDATE `player` SET `online`='1' WHERE player_id='" + player.id + "'");
                                PlayerManger.gI().getPlayers().add(player);
                                session.player = player;
                                player.setSession(session);
                                player.isNewMember = true;
                                Service.gI().updateVersionx(session);
                                Service.gI().sendMessage(session, -93, "1630679752231_-93_r");
                                Service.gI().updateVersion(session);
                                session.isLogin = true;
                                if(Setting.SERVER_TEST){
                                    player.isTV = true;
                                    player.inventory.gem = 500000;
                                    player.inventory.ruby = 500000;
                                }
                            }
                        }
                    } else {
                        Service.gI().sendThongBaoOK(session, "Tên đã tồn tại");
                    }
                }
                else{
                    Service.gI().sendThongBaoOK(session, "Tài khoản đã tồn tại nhân vật");
                }
                rs.close();
                rs = null;
            }
            else
            {
                Service.gI().sendThongBaoOK(session, "Có lỗi xảy ra khi tạo nhân vật");
            }
        }
        catch (Exception e) {
            Util.logException(Controller.class, e);
        }

    }

    public void login2(Session session) {
        Service.gI().sendThongBaoOK(session, "Liên hệ Tuấn đẹp trai để đăng ký tài khoản free.");
    }

    public void Send_ThongBao(Session session) {
        Message msg = null;
        try {
            msg = new Message(-70);
            msg.writer().writeShort(1139);
            msg.writer().writeUTF("NRO TuanZin xin chào bạn!");
            msg.writer().writeByte(0);
            session.sendMessage(msg);
        }
        catch (Exception e) {
        }
        finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendInfo(Player player) {
        try {
            Service.gI().tileSet(player);
            Service.gI().player(player);
            Service.gI().Send_Caitrang(player);
            Service.gI().StaminaMax(player);
            Service.gI().Stamina(player);
            Service.gI().send_task(player, TaskData.getTask(player.taskId));
            Service.gI().send_special_skill(player);
            Service.gI().point(player);
            Service.gI().activePoint(player);
            Service.gI().Rank(player);
            Service.gI().send_tab_info(player);
            player.sendMeHavePet();
            player.sendItemTime();
            player.login_time = new Timestamp(System.currentTimeMillis());
            if (player.clan != null) {
                ClanService.gI().clanInfo(player, player.clan.id);
            }
            Service.gI().sendBag(player);
            if(player.playerSkill != null){
                player.playerSkill.sendSkillShortCut();
                if(player.isPl()){
                    for(int i = 0 ; i < player.playerSkill.skills.size() ; i++){
                        Skill skill = player.playerSkill.skills.get(i);
                        if(skill != null){
                            if(player.role >= 99)
                            {
                                skill.lastTimeUseThisSkill = 0;
                                Service.gI().HoiSkill(player, skill.skillId, 1000);
                                continue;
                            }
                            int c = (int) (System.currentTimeMillis() - skill.lastTimeUseThisSkill);
                            if(c > 0 && c < skill.coolDown){
                                Service.gI().HoiSkill(player, skill.skillId, c);
                            }
                        }
                    }
                }
            }
            if(player.zone == null){
                ChangeMap.gI().changeMap(player, player.gender + 21, -1, 5);
                return;
            }
            int mapID = player.zone.map.id;
            int cX = player.x;
            int cY = player.y;
            if(player.clan == null && player.zone.map.id == 153){
                mapID = 5;
                cX = 100;
                cY = 5;
            }
            if(player.zone.isNRSD() && (DHVT.gI().Hour < Setting.TIME_START || DHVT.gI().Hour >= Setting.TIME_END)){
                mapID = 24 + player.gender;
                cX = 400;
                cY = 5;
            }
            if(mapID == 126){
                mapID = 19;
                cX = -1;
                cY = 5;
            }
            if(mapID == 121){
                mapID = 21 + player.gender;
                cX = -1;
                cY = 5;
            }
            if(player.zone.map.id == player.gender + 39 || player.zone.isPhuBan()){
                mapID = 21 + player.gender;
                cX = 400;
                cY = 5;
            }
            ChangeMap.gI().changeMap(player, mapID, cX, cY);
        } catch (Exception e) {
            Util.logException(Controller.class, e, "Player Name: " + player.name);
            player.session.disconnect();
        }
    }

    public void logout(Session session) {
        Player player = session.player;
        if (player == null) {
            return;
        }
        try
        {
            player.DHVT_23.close();
            if(player.idNRNM != -1){
                NRNM.gI().SetPlayer(null, player.idNRNM, true);
                player.idNRNM = -1;
            }
            PVP.gI().finishPVP(player, PVP.TYPE_LEAVE_MAP);
            DHVT_Template.gI().finishPK_DHVT(player);
            Transaction.gI().StopTran(player);
            PlayerManger.gI().kick(player);
            if (player.playerSkill.useTroi) {
                player.playerSkill.removeUseTroi();
                player.playerSkill.removeAnTroi();
            }
            if (player.mobMe != null) {
                player.mobMe.mobMeDie();
            }
            if (player.pet != null) {
                if (player.pet.mobMe != null) {
                    player.pet.mobMe.mobMeDie();
                }
                player.pet.close();
            }
            if(player.newpet != null){
                player.newpet.close();
            }
            player.close();
            player.magicTree.close();
            PlayerDAO.updateDB(player);
            session.isLogin = false;
        } catch (Exception e) {
            Util.debug("Controller.logout");
            e.printStackTrace();
        }
        if (player.pet != null) {
            player.pet.dispose();
        }
        if (player.newpet != null) {
            player.newpet.dispose();
        }
        player.dispose();
        player = null;
    }
}
