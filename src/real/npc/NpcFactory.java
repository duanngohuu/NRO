package real.npc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import real.boss.BossManager;
import real.clan.ClanManager;
import real.clan.ClanService;
import real.clan.PhuBan;
import real.func.ChangeMap;
import real.func.Combine;
import real.func.DHVT;
import real.func.GiftCode;
import real.func.MiniGame;
import real.func.NRSD;
import real.func.PVP;
import real.func.RuongBiAn;
import real.func.Shop;
import real.func.SummonDragon;
import static real.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static real.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import real.func.TopInfo;
import real.func.Transaction;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemTime;
import real.magictree.MagicTree;
import real.map.Map;
import real.map.MapManager;
import real.map.Zone;
import real.player.Player;
import real.skill.Skill;
import real.skill.SkillUtil;
import real.skill.SpeacialSkill;
import real.task.Task;
import real.task.TaskBoMongService;
import real.task.TaskData;
import real.task.TaskOrders;
import server.Service;
import server.Util;
import server.io.Message;
import service.DAOS.PetDAO;
import service.Setting;

public class NpcFactory {
    public static final byte ONG_GOHAN = 0;
    public static final byte ONG_PARAGUS = 1;
    public static final byte ONG_MOORI = 2;
    public static final byte RUONG_DO = 3;
    public static final byte DAU_THAN = 4;
    public static final byte CON_MEO = 5;
    public static final byte KHU_VUC = 6;
    public static final byte BUNMA = 7; //562
    public static final byte DENDE = 8; //350
    public static final byte APPULE = 9; //565
    public static final byte DR_DRIEF = 10;
    public static final byte CARGO = 11;
    public static final byte CUI = 12;
    public static final byte QUY_LAO_KAME = 13;
    public static final byte TRUONG_LAO_GURU = 14;
    public static final byte VUA_VEGETA = 15;
    public static final byte URON = 16;
    public static final byte BO_MONG = 17;
    public static final byte THAN_MEO_KARIN = 18;
    public static final byte THUONG_DE = 19;
    public static final byte THAN_VU_TRU = 20;
    public static final byte BA_HAT_MIT = 21; //1410
    public static final byte TRONG_TAI = 22;
    public static final byte GHI_DANH = 23;
    public static final byte RONG_THIENG = 24;
    public static final byte LINH_CANH = 25;
    public static final byte DOC_NHAN = 26;
    public static final byte RONG_THIENG_NAMEC = 27;
    public static final byte CUA_HANG_KY_GUI = 28;
    public static final byte RONG_OMEGA = 29;
    public static final byte RONG_2S = 30;
    public static final byte RONG_3S = 31;
    public static final byte RONG_4S = 32;
    public static final byte RONG_5S = 33;
    public static final byte RONG_6S = 34;
    public static final byte RONG_7S = 35;
    public static final byte RONG_1S = 36;
    public static final byte BUNMA_ = 37;
    public static final byte CALICK = 38;
    public static final byte SANTA = 39;
    public static final byte MABU_MAP = 40;
    public static final byte TRUNG_THU = 41;
    public static final byte QUOC_VUONG = 42;
    public static final byte TO_SU_KAIO = 43;
    public static final byte OSIN = 44;
    public static final byte KIBIT = 45;
    public static final byte BABIDAY = 46;
    public static final byte GIUMA_DAU_BO = 47;
    public static final byte NGO_KHONG = 48;
    public static final byte DUONG_TANG = 49;
    public static final byte QUA_TRUNG = 50;
    public static final byte DUA_HAU = 51;
    public static final byte HUNG_VUONG = 52;
    public static final byte TAPION = 53;
    public static final byte LY_TIEU_NUONG = 54;
    public static final byte BILL = 55;
    public static final byte WHIS = 56;
    public static final byte CHAMPA = 57;
    public static final byte VADOS = 58;
    public static final byte TRONG_TAI_ = 59;
    public static final byte GOKU_SSJ = 60;
    public static final byte GOKU_SSJ_ = 61;
    public static final byte POTAGE = 62;
    public static final byte JACO = 63;
    public static final byte DAI_THIEN_SU = 64;
    public static final byte YARIROBE = 65;
    public static final byte NOI_BANH = 66;
    public static final byte MR_POPO = 67;
    public static final byte PANCHY = 68;
    public static final byte THO_DAI_CA = 69;
    public static final byte BARDOCK = 70;
    public static final byte BERRY = 71;
    public static final byte TORI_BOT = 74;
    //index menu con meo
    public static final int IGNORE_MENU = 500;
    public static final int START_COMBINE = 501;
    public static final int MAKE_MATCH_PVP = 502;
    public static final int MAKE_FRIEND = 503;
    public static final int REVENGE = 504;
    public static final int TUTORIAL_SUMMON_DRAGON = 505;
    public static final int SUMMON_SHENRON = 506;
    public static final int SPECIAL_SKILL = 507;
    public static final int OPEN_SPECIAL_NORMAL = 508;
    public static final int OPEN_SPECIAL_VIP = 509;
    public static final int SUMMON_SHENRON_BANG = 510;
    public static final int KHI_GAS_HUY_DIET = 511;
    public static final int KHO_BAU_DUOI_BIEN = 512;
    public static final int CON_DUONG_RAN_DOC = 513;
    public static final int NGOC_RONG_NAMEC = 514;
   
    //index menu ruong bi an
    public static final int RUONG_BI_AN = 515;
    public static final int RUONG_BI_AN_2 = 516;
    public static final int RUONG_BI_AN_3 = 517;
    public static final int RUONG_BI_AN_4 = 518;
    public static final int RUONG_BI_AN_5 = 519;
    public static final int RUONG_BI_AN_6 = 520;
    public static final int RUONG_BI_AN_7 = 521;
    public static final int RUONG_BI_AN_8 = 522;
    public static final int RUONG_BI_AN_9 = 523;
    public static final int RUONG_BI_AN_10 = 524;
    public static final int RUONG_BI_AN_11 = 525;
    public static final int RUONG_BI_AN_CONFIRM = 526;
    //index menu rong thieng
    public static final int SHENRON_CONFIRM = 501;
    public static final int SHENRON_1_1 = 502;
    public static final int SHENRON_1_2 = 503;
    public static final int SHENRON_2 = 504;
    public static final int SHENRON_3 = 505;
    
    public static final int SHENRON_CONFIRM_DEN = 506;
    public static final int SHENRON_DEN = 507;
    
    public static final int SHENRON_CONFIRM_BANG = 508;
    public static final int SHENRON_BANG = 509;
    
    public static int typeBua;

    public static void createNPC(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        try {
            Npc npc = null;
            switch (tempId) {
                case TORI_BOT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Xin chào, ta có 1 đống đồ xịn cậu có muốn xem không?";
                            String[] menuSelect = new String[]{
                                "Cửa\nhàng"
                            };
                            createOtherMenu(pl, 0, npcSay, menuSelect);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    Shop.gI().openShop(player, this.tempId);
                                    break;
                                case 1:
                                    break;
                            }
                        }
                    };
                    break;
                    
                case BERRY:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (TaskData.getTask(pl.taskId) != null && TaskData.getTask(pl.taskId).subNames[pl.taskIndex].contains("Tìm Kiếm Berry đi lạc")) {
                                Service.gI().send_task_next(pl);
                            }
                            Service.gI().sendPopUpMultiLine(pl, 0, avartar, "Huhu. Chú ơi cứu cháu lũ quái vật này đang đánh cháu");
                        }
                        @Override
                        public void confirmMenu(Player pl, int select) {
                        }
                    };
                    break;
                case BARDOCK:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (TaskData.getTask(pl.taskId + 1) != null && TaskData.getTask(pl.taskId).subNames[pl.taskIndex].toLowerCase().contains("nói chuyện với bardock")) {
                                Service.gI().congTiemNang(pl, (byte) 2, TaskData.getTask(pl.taskId).getTNSM());
                                if(TaskData.getTask(pl.taskId).type == 1){
                                    Item it = ItemData.gI().get_item(1184);
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    it.quantity = 54;
                                    pl.inventory.addItemBag(it);
                                    pl.inventory.sendItemBags();
                                    pl.inventory.sortItemBag();
                                    Service.gI().sendThongBao(pl, "Bạn nhận được "+ it.quantity +" "+it.template.name);
                                }
                                pl.taskId += 1;
                                pl.taskIndex = 0;
                                pl.taskCount = 0;
                                Service.gI().ClearTask(pl);
                                Service.gI().send_task(pl, TaskData.getTask(pl.taskId));
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Quá ghê gớm :>");
                                return;
                            }
                            if (TaskData.getTask(pl.taskId) != null && (TaskData.getTask(pl.taskId).subNames[pl.taskIndex].contains("Tìm Người Xayda đang bị thương") || TaskData.getTask(pl.taskId).subNames[pl.taskIndex].contains("Mang Berry về hang cho Bardock"))) {
                                Service.gI().send_task_next(pl);
                            }
                            Service.gI().sendPopUpMultiLine(pl, tempId, avartar, "Ta đang bị thương nặng sau khi chiến đấu với lũ Chilled hãy giúp ta tìm kiếm thuốc mà bọn chúng đã ăn cắp của ta");                        }
                        @Override
                        public void confirmMenu(Player pl, int select) {
                        }
                    };
                    break;
                case MR_POPO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Thượng đế đã phát hiện 1 loại khí đang âm thầm\n";
                            npcSay += "hủy diệt mọi mầm sống trên Trái Đất,\n";
                            npcSay += "nó được gọi là Destron Gas.\n";
                            npcSay += "Ta sẽ đưa các cậu đến nơi ấy, các cậu sẵn sàng chưa?";
                            String[] menu = new String[]{
                                "Thông tin\nChi tiết",
                                "Top 100\nBang hội",
                                "Thành tích\nBang",
                                "Ok",
                                "Từ chối"
                            };
                            createOtherMenu(pl, 0, npcSay, menu);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                                switch(select){
                                    case 0:
                                        String Text = "Nice to meet you.";
                                        Service.gI().sendPopUpMultiLine(player, tempId, avartar, Text);
                                        break;
                                    case 1:
                                        Service.gI().sendTabTop(player, "Top 100", new ArrayList<>(), true);
                                        break;
                                    case 2:
                                        Service.gI().sendTabTop(player, "Thành tích Bang", new ArrayList<>(), true);
                                        break;
                                    case 3:
                                        if(player.clan == null){
                                            Service.gI().sendPopUpMultiLine(player, tempId, 0, "Chỉ tiếp bang hội không tiếp khách vãng lai");
                                        }
                                        else if(Util.findDayDifference(System.currentTimeMillis(), player.timeJoinClan) < 1){
                                            Timestamp timest = new Timestamp(player.timeJoinClan);
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(timest);
                                            cal.add(Calendar.DATE, 1);
                                            long time = cal.getTime().getTime();
                                            Service.gI().sendPopUpMultiLine(player, tempId, 0, "Vui lòng đợi " + (Util.findHoursDifference(time,System.currentTimeMillis()) <= 1 ? Util.findMinutesDifference(time,System.currentTimeMillis())+" phút" : Util.findHoursDifference(time,System.currentTimeMillis()) +" giờ") +" nữa để tiếp tục!");
                                        }
                                        else if(player.clan != null && player.clan.KhiGasHuyDiet == null && player.clan.members.stream().anyMatch(m -> m.id == player.id && m.role == 0)){
                                            player.typeInput = 10;
                                            Service.gI().sendInputText(player, "Hãy chọn cấp đồ từ 1-110", 1, new int[]{1}, new String[]{"Cấp độ"});
                                        }
                                        else if(player.clan.KhiGasHuyDiet != null && player.clan.KhiGasHuyDiet.time > 0){
                                            String npcSay = "Bang hội của cậu đang tham gia Destron Gas cấp độ " + player.clan.KhiGasHuyDiet.level;
                                            npcSay += "\ncậu có muốn đi cùng họ không ?(" + Util.timeAgo(Util.currentTimeSec(), player.clan.KhiGasHuyDiet.timeJoin / 1000) + ")";
                                            createOtherMenu(player, 1, npcSay, "Đồng ý", "Từ chối");
                                        }
                                        else if(player.clan.KhiGasHuyDiet != null && player.clan.KhiGasHuyDiet.time <= 0){
                                            Service.gI().sendPopUpMultiLine(player, tempId, 0, "Mỗi ngày chỉ đi được 1 lượt duy nhất");
                                        }
                                        else
                                        {
                                            Service.gI().sendThongBao(player, "Chức năng này chỉ dành cho bang chủ.");
                                        }
                                        break;
                                }
                            }
                            else if (player.getIndexMenu() == 1) {
                                switch(select){
                                    case 0:
                                        player.type = 3;
                                        player.maxTime = 5;
                                        Service.gI().Transport(player, 1);
                                        Service.gI().clearMap(player);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case DAI_THIEN_SU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Đua top dang dien ra!";
                            String[] menu = new String[]{
                                "Top 100\nNạp Thẻ",
                                "Top 100\nSức Mạnh",
                                "Top 100\nNhiem Vu"
                            };
                            createOtherMenu(pl, 0, npcSay, menu);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(select == 0){
                               // Service.gI().sendTabTop(player, "Top 100 Nạp Thẻ", TopInfo.topNap);
                                Service.gI().sendTabTop(player, "Top 100 Nạp Thẻ", TopInfo.topNap, false);
                            }else if(select == 1){
                              //  Service.gI().sendTabTop(player, "Top 100 Sức Mạnh", TopInfo.topSM);
                                Service.gI().sendTabTop(player, "Top 100 Sức Mạnh", TopInfo.topSM, false);
                            }
                            else if(select == 2){
                              //  Service.gI().sendTabTop(player, "Top 100 Sức Mạnh", TopInfo.topSM);
                                Service.gI().sendTabTop(player, "Top 100 Nhiem Vu", TopInfo.topNV, false);
                            }
                        }
                    };
                    break;
                case NOI_BANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Xin chào " + pl.name + "\n";
                            npcSay += "Tôi là nồi nấu bánh\n";
                            npcSay += "Tôi có thể giúp gì cho bạn ?\n";
                            boolean isNau = pl.itemTime.ExitsItemTiem(7096) && pl.itemTime.ExitsItemTiem(7095);
                            String[] menu = null;
                            ItemTime it = pl.itemTime.GetItemTiem(7095);
                            ItemTime it2 = pl.itemTime.GetItemTiem(7096);
                            if(it != null && it.time <= 0)
                            {
                                menu = new String[]{
                                    "Đổi điểm\nsự kiện\n[" + Util.getMoneys(pl.session.get_point()) + "]",
                                    "Nhận\nBánh tét",
                                    "Từ chối"
                                };
                            }
                            else if(it2 != null && it2.time <= 0)
                            {
                                menu = new String[]{
                                    "Đổi điểm\nsự kiện\n[" + Util.getMoneys(pl.session.get_point()) + "]",
                                    "Nhận\nBánh chưng",
                                    "Từ chối"
                                };
                            }
                            else if(isNau)
                            {
                                menu = new String[]{
                                    "Đổi điểm\nsự kiện\n[" + Util.getMoneys(pl.session.get_point()) + "]",
                                    "Từ chối"
                                }; 
                            }
                            else
                            {
                                menu = new String[]{
                                    "Đổi điểm\nsự kiện\n[" + Util.getMoneys(pl.session.get_point()) + "]",
                                    "Tự nấu\nbánh",
                                    "Từ chối"
                                };
                            }
                            createOtherMenu(pl, 0, npcSay, menu);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            // Bánh trưng: thịt heo x2, thúng nếp x2, thúng đậu x2, lá dong x2 - 5 phút. // 7096
                            // Bánh tét: thịt heo x1, thúng nếp x1, thúng đậu x1, lá dong x1 - 3 phút. // 7095
                            String npcSay = null;
                            String[] menu = null;
                            switch (player.getIndexMenu()) {
                                case 0:
                                    switch (select) {
                                        case 0:
                                            Shop.gI().openSukien(player, tempId);
                                            break;
                                        case 1:
                                            ItemTime it = player.itemTime.GetItemTiem(7095);
                                            ItemTime it2 = player.itemTime.GetItemTiem(7096);
                                            if(it != null && it.time <= 0)
                                            {
                                                if(player.getBagNull() < 1)
                                                {
                                                    Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống");
                                                    break;
                                                }
                                                Item item = ItemData.gI().get_item(752);
                                                item.itemOptions.clear();
                                                item.itemOptions.add(new ItemOption(50, 15));
                                                item.itemOptions.add(new ItemOption(14, 15));
                                                item.itemOptions.add(new ItemOption(30, 0));
                                                item.itemOptions.add(new ItemOption(93, 30));
                                                Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                                player.itemTime.removeItemTime(7095, 0);
                                                player.inventory.addItemBag(item);
                                                player.inventory.sendItemBags();
                                            }
                                            else if(it2 != null && it2.time <= 0)
                                            {
                                                if(player.getBagNull() < 1)
                                                {
                                                    Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống");
                                                    break;
                                                }
                                                Item item = ItemData.gI().get_item(753);
                                                item.itemOptions.clear();
                                                item.itemOptions.add(new ItemOption(50, 25));
                                                item.itemOptions.add(new ItemOption(14, 25));
                                                item.itemOptions.add(new ItemOption(30, 0));
                                                item.itemOptions.add(new ItemOption(93, 30));
                                                player.itemTime.removeItemTime(7096, 0);
                                                player.inventory.addItemBag(item);
                                                player.inventory.sendItemBags();
                                                Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                            }
                                            else
                                            {
                                                npcSay = "Hãy tìm đủ nguyên liệu và chọn loại bánh muốn nấu";
                                                if(player.itemTime.ExitsItemTiem(7096) && !player.itemTime.ExitsItemTiem(7095))
                                                {
                                                    menu = new String[]{
                                                        "Nấu\nBánh tét",
                                                        "Từ chối"
                                                    };
                                                }
                                                else if(!player.itemTime.ExitsItemTiem(7096) && player.itemTime.ExitsItemTiem(7095))
                                                {
                                                    menu = new String[]{
                                                        "Nấu\nBánh chưng",
                                                        "Từ chối"
                                                    };
                                                }
                                                else if(!player.itemTime.ExitsItemTiem(7095) && !player.itemTime.ExitsItemTiem(7096))
                                                {
                                                    menu = new String[]{
                                                        "Nấu\nBánh tét",
                                                        "Nấu\nBánh chưng",
                                                        "Từ chối"
                                                    };
                                                }
                                                createOtherMenu(player, 1, npcSay, menu);
                                            }
                                            break;
                                    }
                                    break;
                                case 1:
                                    switch (select) {
                                        case 0:
                                            if(!player.itemTime.ExitsItemTiem(7095))
                                            {
                                                npcSay = "Bạn muốn nấu bánh tét ?\n";
                                                npcSay += "Nguyên liệu bao gồm:\n";
                                                npcSay += "1 thịt heo, 1 thúng nếp, 1 thúng đậu xanh, 1 lá dong\n";
                                                npcSay += "Thời gian nấu: 3 phút";
                                                menu = new String[]{
                                                    "Đồng ý\n"+Util.getMoneys(5000000),
                                                    "Từ chối"
                                                };
                                                createOtherMenu(player, 2, npcSay, menu);
                                                break;
                                            }
                                        case 1:
                                            if(!player.itemTime.ExitsItemTiem(7096))
                                            {
                                                npcSay = "Bạn muốn nấu bánh chưng ?\n";
                                                npcSay += "Nguyên liệu bao gồm:\n";
                                                npcSay += "2 thịt heo, 2 thúng nếp, 2 thúng đậu xanh, 2 lá dong\n";
                                                npcSay += "Thời gian nấu: 5 phút";
                                                menu = new String[]{
                                                    "Đồng ý\n"+Util.getMoneys(10000000),
                                                    "Từ chối"
                                                };
                                                createOtherMenu(player, 3, npcSay, menu);
                                            }
                                            break;
                                    }
                                    break;
                                case 2:// bánh tét
                                    switch (select) {
                                        case 0:
                                            Item item1 = player.inventory.findItem(player.inventory.itemsBag, 748); // thịt
                                            Item item2 = player.inventory.findItem(player.inventory.itemsBag, 749); // thúng nếp
                                            Item item3 = player.inventory.findItem(player.inventory.itemsBag, 750); // thúng đậu
                                            Item item4 = player.inventory.findItem(player.inventory.itemsBag, 751); // lá dong
                                            if(item1 != null && item1.quantity > 0 && item2 != null && item2.quantity > 0 && item3 != null && item3.quantity > 0 && item4 != null && item4.quantity > 0)
                                            {
                                                if(player.inventory.gold < 5000000)
                                                {
                                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng");
                                                    return;
                                                }
                                                player.inventory.gold -= 5000000;
                                                if (!player.itemTime.ExitsItemTiem(7095))
                                                {
                                                    player.inventory.subQuantityItemsBag(item1, 1);
                                                    player.inventory.subQuantityItemsBag(item2, 1);
                                                    player.inventory.subQuantityItemsBag(item3, 1);
                                                    player.inventory.subQuantityItemsBag(item4, 1);
                                                    player.itemTime.addItemTime(7095, 180);
                                                    player.inventory.sendItemBags();
                                                }
                                            }
                                            else
                                            {
                                                Service.gI().sendThongBao(player, "Bạn không đủ nguyên liệu");
                                            }
                                            break;
                                    }
                                    break;
                                case 3://bánh chưng
                                    switch (select) {
                                        case 0:
                                            Item item1 = player.inventory.findItem(player.inventory.itemsBag, 748); // thịt
                                            Item item2 = player.inventory.findItem(player.inventory.itemsBag, 749); // thúng nếp
                                            Item item3 = player.inventory.findItem(player.inventory.itemsBag, 750); // thúng đậu
                                            Item item4 = player.inventory.findItem(player.inventory.itemsBag, 751); // lá dong
                                            if(item1 != null && item1.quantity > 1 && item2 != null && item2.quantity > 1 && item3 != null && item3.quantity > 1 && item4 != null && item4.quantity > 1)
                                            {
                                                if(player.inventory.gold < 10000000)
                                                {
                                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng");
                                                    return;
                                                }
                                                player.inventory.gold -= 10000000;
                                                if (!player.itemTime.ExitsItemTiem(7096))
                                                {
                                                    player.inventory.subQuantityItemsBag(item1, 2);
                                                    player.inventory.subQuantityItemsBag(item2, 2);
                                                    player.inventory.subQuantityItemsBag(item3, 2);
                                                    player.inventory.subQuantityItemsBag(item4, 2);
                                                    player.itemTime.addItemTime(7096, 300);
                                                    player.inventory.sendItemBags();
                                                }
                                            }
                                            else
                                            {
                                                Service.gI().sendThongBao(player, "Bạn không đủ nguyên liệu");
                                            }
                                            break;
                                    }
                                    break;
                            }
                        }
                    };
                    break;
                case QUA_TRUNG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (this.mapId == 21 + pl.gender && pl.mabuEgg != null && pl.mabuEgg.player.id == pl.id) {
                                if(pl.mabuEgg.timeHatches <= 0){
                                    createOtherMenu(pl, 0,"Bạn có chắc chắn thay thế đệ tử hiện tại thành đệ tử Mabư?", "Đồng ý","Đóng");
                                }else{
                                    createOtherMenu(pl, 0,"Bạn có chắc chắn thay thế đệ tử hiện tại thành đệ tử Mabư?", "Chờ","Nở ngay","Đóng");
                                }
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                                if (this.mapId == 21 + player.gender && player.mabuEgg != null && player.mabuEgg.player.id == player.id) {
                                    if(player.typeFusion != 0){
                                        Service.gI().sendThongBao(player, "Tách hợp thể để có thể thực hiện thao tác");
                                        return;
                                    }
                                    if(player.mabuEgg.timeHatches <= 0 && select == 0){
                                        createOtherMenu(player, 1,"Vui lòng chọn hành tinh cho đệ tử", "Trái Đất","Namec","Xayda");
                                    }else if(select == 1){
                                        createOtherMenu(player, 1,"Vui lòng chọn hành tinh cho đệ tử", "Trái Đất","Namec","Xayda");
                                    }
                                }
                            }else if (player.getIndexMenu() == 1) {
                                PetDAO.OpenMabuEgg(player, (byte)select);
                            }
                        }
                    };
                    break;
                case DOC_NHAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(ChangeMap.gI().getMobDie(pl.zone) >= pl.zone.mobs.length && pl.zone.players.stream().filter(p -> p.isBoss).count() <= 0 && !pl.clan.DoanhTrai.isDropItem){
                                for(;;){
                                    Zone zone = MapManager.gI().getMapById(53).map_zone[pl.clan.DoanhTrai.zonePhuBan];
                                    int itemId = 17;
                                    if(!zone.items.stream().anyMatch(it -> it.itemTemplate.id == itemId)){
                                        int x = Util.nextInt(100, zone.map.mapWidth - 100);
                                        int y = zone.map.pxh / 2;
                                        Service.gI().roiItem(ItemData.gI().get_item(itemId), zone, x, zone.LastY(x, y), -1);
                                        pl.clan.DoanhTrai.time = 300;
                                        pl.clan.DoanhTrai.isDropItem = true;
                                        break;
                                    }
                                }
                                Service.gI().sendPopUpMultiLine(pl, tempId, avartar, "Ta đã giấu ngọc rồng 3 sao ở trong doanh trại này ngươi chỉ có 5 phút đi tìm hahaha...");
                            }else{
                                Service.gI().sendPopUpMultiLine(pl, tempId, avartar, "Nhà người tuổi con ngan con");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                        }
                    };
                    break;
                case LINH_CANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.clan == null){
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Chỉ tiếp bang hội không tiếp khách vãng lai");
                            }
                            else if(Util.findDayDifference(System.currentTimeMillis(), pl.timeJoinClan) < 1){
                                Timestamp timest = new Timestamp(pl.timeJoinClan);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(timest);
                                cal.add(Calendar.DATE, 1);
                                long time = cal.getTime().getTime();
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Vui lòng đợi " + (Util.findHoursDifference(time,System.currentTimeMillis()) <= 1 ? Util.findMinutesDifference(time,System.currentTimeMillis())+" phút" : Util.findHoursDifference(time,System.currentTimeMillis()) +" giờ") +" nữa để tiếp tục!");
                            }
                            else if(pl.clan.DoanhTrai != null && pl.clan.DoanhTrai.time > 0){
                                createOtherMenu(pl, 0, "Bang hội bạn còn " + (int)(pl.clan.DoanhTrai.time / 60) +" phút để chiến đấu\nBạn có muốn tham gia không?", "Tham gia","Từ chối");
                            }
                            else if(ClanManager.gI().getZone() == -1){
                                createOtherMenu(pl, 0,"Doanh trại đang quá đông hãy quay lại sau", "Ok","Đóng");
                            }
                            else if(pl.clan.DoanhTrai == null && pl.zone.players.stream().filter(p -> p != null && p.isPl() && p.clan != null && p.clan.id == pl.clan.id && Util.getDistance(p, pl) <= 200).toList().size() >= 2 && pl.clan.members.size() >= 5){
                                createOtherMenu(pl, 0,"Hôm nay bang hội của ngươi chưa vào doanh trại lần nào. Ngươi có muốn vào không?", "Vào\n(Miễn phí)","Từ chối");
                            }
                            else if(pl.clan.DoanhTrai != null && pl.clan.DoanhTrai.time <= 0){
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Hôm nay bang của bạn đã đi doanh trại rồi");
                            }
                            else{
                                createOtherMenu(pl, 0,"Ngươi phải có ít nhất 1 đồng đội cùng bang đứng gần mới có thể vào\ntuy nhiên ta khuyên ngươi nên đi cùng 3-4 người để khởi chết.\nLưu ý bang hội của ngươi phải có trên 5 thành viên mới vào được", "Ok","Đóng");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    if(player.clan.DoanhTrai != null){
                                        if(player.clan.DoanhTrai != null && player.clan.DoanhTrai.time > 0 && Util.findDayDifference(System.currentTimeMillis(), player.timeJoinClan) > 0){
                                            player.clan.DoanhTrai.setHPMob_DoanhTrai(MapManager.gI().getMap(53, player.clan.DoanhTrai.zonePhuBan));
                                            ChangeMap.gI().changeMap(player, 53, player.clan.DoanhTrai.zonePhuBan , 115, 5, ChangeMap.NON_SPACE_SHIP);
                                        }
                                    }
                                    else if(player.clan != null && player.clan.members.size() >= 5){
                                        int zone = ClanManager.gI().getZone();
                                        if(zone != -1){
                                            if(player.clan.DoanhTrai == null && player.zone.players.stream().filter(p -> p != null && p.isPl() && p.clan != null && p.clan == player.clan && Util.getDistance(p, player) <= 200).toList().size() >= 2 && player.clan.members.size() >= 5){
                                                int[] mapdt ={53,58,59,60,61,62,55,56,54,57};
                                                int hp = (player.point.getHPFull() / 100 * 60) * 2;
                                                player.clan.DoanhTrai = new PhuBan(player.clan.id, tempId, 1800, System.currentTimeMillis(), Util.TimePhuBan(), zone, hp, -1);
                                                for(int i = 0; i < mapdt.length; i++){
                                                    Zone zones = MapManager.gI().getMap(mapdt[i], zone);
                                                    player.clan.DoanhTrai.setHPMob(zones);
                                                }
                                                addBoss(player);
                                                List<Player> clanDT = player.zone.getPlayers().stream().filter(pl -> pl.clan != null && pl.clan == player.clan && Util.getDistance(player, pl) <= 200 && Util.findDayDifference(System.currentTimeMillis(), pl.timeJoinClan) > 0).toList();
                                                for(Player pl : clanDT){
                                                    ChangeMap.gI().changeMap(pl, 53, zone, 115, 5, ChangeMap.NON_SPACE_SHIP);
                                                }
                                                Util.debug("DONE DOANH TRAI - ZONE: " + zone);
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    };
                    break;
                case RONG_1S:
                case RONG_2S:
                case RONG_3S:
                case RONG_4S:
                case RONG_5S:
                case RONG_6S:
                case RONG_7S:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.idNRSD == -1)
                            {
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Về nhà","Từ chối");
                            }
                            else
                            {
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Phù hộ","Từ chối");
                                // createOtherMenu(pl, 0, "Ta sẽ giúp ngươi tăng HP và KI lên mức kinh hoàng, ngươi hãy chọn đi", "x3 HP\n400Tr vàng","x5 HP\n700Tr vàng","x7 HP\n1tỉ vàng","Từ chối");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.getIndexMenu() == 0)
                            {
                                if(player.idNRSD == -1){
                                    switch (select) {
                                        case 0:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1, ChangeMap.getSpaceShip(player));
                                            break;
                                    }
                                }
                                else
                                {
                                    switch (select) {
                                        case 0:
                                            createOtherMenu(player, 1, "Ta sẽ giúp ngươi tăng HP và KI lên mức kinh hoàng, ngươi hãy chọn đi", "x3 HP\n400Tr vàng","x5 HP\n700Tr vàng","x7 HP\n1tỉ vàng","Từ chối");
                                            break;
                                    }
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                int money = 150000000 * (select == 0 ? 3 : select == 1 ? 5 : select == 2 ? 7 : 0);
                                money = money == 450000000 ? 400000000 : money == 750000000 ? 700000000 : money == 1050000000 ? 1000000000 : 0;
                                if(player.inventory.gold >= money){
                                    player.inventory.gold -= money;
                                    switch(select){
                                        case 0:
                                            player.percentHpPlus = 3;
                                            break;
                                        case 1:
                                            player.percentHpPlus = 5;
                                            break;
                                        case 2:
                                            player.percentHpPlus = 7;
                                            break;
                                    }
                                    int HPFull = player.point.getHPFull();
                                    player.hoiphuc(HPFull, HPFull);
                                    Service.gI().point(player);
                                    Service.gI().sendMoney(player);
                                }
                                else
                                {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng");
                                }
                            }
                        }
                    };
                    break;
                case RONG_OMEGA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if((DHVT.gI().Hour >= Setting.TIME_END || DHVT.gI().Hour < Setting.TIME_START)){
                                String[] MenuNRSD = new String[]
                                {
                                    "Hướng\ndẫn\nthêm",
                                    "Từ chối",
                                };
                                if(pl.inventory.itemsGift.size() > 0){
                                    MenuNRSD = new String[]
                                    {
                                        "Hướng\ndẫn\nthêm",
                                        "Nhận thưởng",
                                        "Từ chối",
                                    };
                                }
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", MenuNRSD);
                            }
                            else if(DHVT.gI().Hour >= Setting.TIME_START && DHVT.gI().Hour < Setting.TIME_END){
                                String[] MenuNRSD = new String[]
                                {
                                    "Hướng\ndẫn\nthêm",
                                    "Tham gia",
                                    "Từ chối",
                                };
                                if(pl.inventory.itemsGift.size() > 0){
                                    MenuNRSD = new String[]
                                    {
                                        "Hướng\ndẫn\nthêm",
                                        "Tham gia",
                                        "Nhận thưởng",
                                        "Từ chối",
                                    };
                                }
                                createOtherMenu(pl, 0, "Cuộc chiến đã bắt đầu ngươi có muốn tham gia không?", MenuNRSD);
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.getIndexMenu() == 0){
                                switch (select) {
                                    case 0:
                                        Service.gI().sendPopUpMultiLine(player, tempId, avartar, "Mỗi ngày từ 20h đến 21h các hành tinh có Ngọc Rồng Sao Đen sẽ xảy ra 1 cuộc đại chiến\bNgười nào tìm thấy và giữ được Ngọc Rồng Sao Đen sẽ mang phần thưởng về cho bang của mình trong vòng 1 ngày\nLưu ý mỗi bang có thể chiếm hữu nhiều viên khác nhau\bnhưng nếu cùng loại cũng chỉ nhận được 1 lần phần thưởng đó.\bGiữ ngọc sao đen trên người hơn 5 phút liên tục\nCác phần thưởng như sau\b1 sao đen: +21% sức đánh\b2 sao đen: +35% HP\b3 sao đen: Biến 35% tấn công thành HP\b4 sao đen: Phản 35% sát thương\b5 sao đen: 35% Sức đánh chí mạng\b6 sao đen: KI+40%\b7 sao đen: 14% Né đòn");
                                        break;
                                    case 1:
                                        if(DHVT.gI().Hour == Setting.TIME_START){
                                            NRSD.OpenTabMap(player);
                                        }
                                        else
                                        {
                                            if(player.inventory.itemsGift.size() > 0){
                                                String[] MenuNRSD = new String[player.inventory.itemsGift.size()];
                                                for(int i = 0; i < player.inventory.itemsGift.size(); i++)
                                                {
                                                    Item it = player.inventory.itemsGift.get(i);
                                                    MenuNRSD[i] =  "Nhận\n"+ it.template.name.replace("Ngọc rồng ", "").replace(" đen", "") + "\n";
                                                }
                                                createOtherMenu(player, 1, "Ngươi muốn nhận thưởng gì?", MenuNRSD);
                                            }
                                        }
                                        break;
                                    case 2:
                                        if(DHVT.gI().Hour == Setting.TIME_START){
                                            if(player.inventory.itemsGift.size() > 0){
                                                String[] MenuNRSD = new String[player.inventory.itemsGift.size()];
                                                for(int i = 0; i < player.inventory.itemsGift.size(); i++)
                                                {
                                                    Item it = player.inventory.itemsGift.get(i);
                                                    MenuNRSD[i] =  "Nhận\n"+ it.template.name.replace("Ngọc rồng ", "").replace(" đen", "") + "\n";
                                                }
                                                createOtherMenu(player, 1, "Ngươi muốn nhận thưởng gì?", MenuNRSD);
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                switch (select) {
                                    default:
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case GOKU_SSJ:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.zone.map.id == 80){
                                createOtherMenu(pl, 0, "Ta mới hạ Fide, nhưng nó đã kịp đào 1 cái lỗ\nHành tinh này sắp nổ tung rồi\nMau lượn thôi", "Chuẩn");
                            }else if(pl.zone.map.id == 131){
                                createOtherMenu(pl, 0, "Đây là đâu? Xong cmnr", "Bó tay" , "Về chỗ cũ");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (this.mapId) {
                                case 80:
                                    switch (select) {
                                        case 0:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 131, -1, 946, ChangeMap.getSpaceShip(player));
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 131:
                                    switch (select) {
                                        case 1:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 80, -1, 800, ChangeMap.getSpaceShip(player));
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    break;
                case GOKU_SSJ_:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.zone.map.id == 133){
                                createOtherMenu(pl, 0, "Hãy cố gắng tu luyện\nThu thập 9.999 bí kiếp để đổi trang phục Yardrat nhé", new String[]{"Đổi\nthưởng", "Đóng"});
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (this.mapId) {
                                case 133:
                                    switch (select) {
                                        case 0:
                                            int param = player.point.getParam(31, 590);
                                            if (param >= 9999) {
                                                player.inventory.subSoLuongItemsBag(590, 9999);
                                                int[] itemID = new int[]{592, 593, 594};
                                                Item yardrat = ItemData.gI().get_item(itemID[player.gender]);
                                                yardrat.itemOptions.clear();
                                                yardrat.itemOptions.add(new ItemOption(47, 350));
                                                yardrat.itemOptions.add(new ItemOption(108, 10));
                                                yardrat.itemOptions.add(new ItemOption(33, 0));
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                yardrat.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(yardrat);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + yardrat.template.name);
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (9999 - param) + " Bí kiếp");
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    break;
                case LY_TIEU_NUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String plWin = MiniGame.gI().MiniGame_S1.result_name;
                            String KQ = MiniGame.gI().MiniGame_S1.result + "";
                            String Money = MiniGame.gI().MiniGame_S1.money + "";
                            String count = MiniGame.gI().MiniGame_S1.players.size() + "";
                            String second = MiniGame.gI().MiniGame_S1.second + "";
                            String number = MiniGame.gI().MiniGame_S1.strNumber(pl.id);
                            String npcSay = ""
                                    + "Kết quả giải trước: " + KQ + "\n"
                                    + (plWin != null ? "Thắng giải trước: " + plWin + "\n" : "")
                                    + "Tham gia: " + count + " tổng giải thưởng: " + Money + " ngọc\n"
                                    + "<" + second + ">giây\n"
                                    + (number != "" ? "Các số bạn chọn: " + number : "");
                            String[] Menus = {
                                "1 Số\n5\nngọc xanh",
                                "Hướng\ndẫn\nthêm",
                                "Đóng"
                            };
                            createOtherMenu(pl, 0, npcSay, Menus);
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch(select){
                                case 0:
                                    if(player.getIndexMenu() == 0){
                                        player.typeInput = 20;
                                        Service.gI().sendInputText(player, "Hãy chọn 1 số từ " + MiniGame.gI().MiniGame_S1.min + "-" + MiniGame.gI().MiniGame_S1.max + ", giá 5 ngọc", 1, new int[]{1}, new String[]{"Số bạn chọn"});
                                    }
//                                    break;
                                     //Service.gI().sendThongBao(player, "Chức năng này đang được bảo trì và phát triển");
                                            break;
                                case 1:
                                    if(player.getIndexMenu() == 0){
                                        String npcSay = ""
                                                + "Thời gian từ " + Setting.TIME_START_GAME + "h đến hết " + (Setting.TIME_END_GAME - 1) + "h59 hàng ngày\n"
                                                + "Mỗi lượt được chọn 10 con số từ " + MiniGame.gI().MiniGame_S1.min + "-" + MiniGame.gI().MiniGame_S1.max + "\n"
                                                + "Thời gian mỗi lượt là " + Setting.TIME_MINUTES_GAME + " phút.";
                                        createOtherMenu(player, 1, npcSay, "Đồng ý");
                                    }
                                    break;
                                    // Service.gI().sendThongBao(player, "Chức năng này đang được bảo trì và phát triển");
                                            //break;
                            }
                        }
                    };
                    break;
                case BILL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(mapId == 48)
                            {
                                if(pl.inventory.itemsBody.stream().limit(5).allMatch(dtl -> dtl != null && dtl.template.level == 13)){
                                    createOtherMenu(pl, 0, "Đói bụng quá...ngươi mang cho ta 99 phần đồ ăn,\nta sẽ cho ngươi một món đồ Hủy Diệt.\nNếu tâm trạng ta vui ngươi có thể nhận được trang bị tăng đến 15%", "Ok", "Từ chối");
                                }
                                else{
                                    createOtherMenu(pl, 0, "Ngươi trang bị đủ 5 món trang bị Thần và mang cho ta 99 phần đồ ăn tới đây...\nrồi ta nói chuyện tiếp.", "Ok");
                                }
                            }
                            else if(mapId == 154)
                            {
                                this.createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi", "Trở về\nThánh địa Kaio", "Từ chối");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch(select){
                                case 0:
                                    if(mapId == 48){
                                        if(player.inventory.itemsBody.stream().limit(5).allMatch(dtl -> dtl != null && dtl.template.level == 13)){
                                            Shop.gI().openShop(player, tempId);
                                        }
                                    }
                                    else if (mapId == 154){
                                        ChangeMap.gI().changeMapBySpaceShip(player, 50, -1, 345, ChangeMap.getSpaceShip(player));
                                    }
                                    break;
                                case 1:
//                                    if(mapId == 154){
//                                        if(player.point.power < 60000000000L){
//                                            Service.gI().sendThongBao(player, "Yêu cầu phải 60 tỷ sức mạnh trở lên");
//                                            break;
//                                        }
//                                        ChangeMap.gI().changeMapBySpaceShip(player, 155, -1, 345, ChangeMap.getSpaceShip(player));
//                                    }
                                    break;
                            }
                        }
                    };
                    break;
                case WHIS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            
                            if(mapId == 154)
                            {
                                String npcSay = "Thử đánh với ta xem nào.\nNgươi còn 1 lượt cơ mà.";
                                String[]menuSelect = new String[]{
                                    "Nói chuyện",
                                    "Học\ntuyệt kỹ",
                                    "Top 100",
                                    "[LV:1]"
                                };
//                                this.createOtherMenu(pl, 0, "Thử đánh với ta xem nào.\nNgươi còn 1 lượt cơ mà.", "Nói chuyện","Học\ntuyệt kỹ", "Top 100", "[LV:1]");
                                createOtherMenu(pl, 0, npcSay, menuSelect);
                            }
                            else
                            {
                                super.openMenu(pl);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            
                            if(player.getIndexMenu() == 0)
                            {
                                switch(select){
                                    case 0:
                                        createOtherMenu(player, 1, "Ta sẽ giúp ngươi chế tạo trang bị Thiên sứ", "OK", "Đóng");
                                        break;
                                    case 1:
                                        if(mapId == 154)
                                        {
                                            if(player.point.power < 60000000000L)
                                            {
                                                Service.gI().sendThongBao(player, "Yêu cầu phải 60 tỷ sức mạnh trở lên");
                                                break;
                                            }
                                            Skill skill = SkillUtil.getSkillbyId(player, player.gender == 0 ? 24 : player.gender == 1 ? 26 : 25);
                                            Skill curSkill = SkillUtil.createSkill(player.gender == 0 ? 24 : player.gender == 1 ? 26 : 25, 1, 0, 0);
                                            if(skill == null)
                                            {
                                                this.createOtherMenu(player, 1, "|5|Ta sẽ dạy cho người tuyệt kỹ " + curSkill.template.name + "\n", "Đồng ý", "Từ chối");
                                            }
                                            else
                                            {
                                                this.createOtherMenu(player, 2, "|5|Ta nâng cấp tuyệt kỹ " + skill.template.name + "\n"
                                                        + "giá 60ty tiềm năng", "Đồng ý", "Hoc nhanh\n50TV","Từ chối");
                                            }
                                        }
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1)
                            {
                                switch(select){
                                    case 0: 
                                        player.combine.openTabCombine(Combine.NANG_CAP_TRANG_BI_THIENSU);
                                        break;
                                    case 1:
                                        if(mapId == 154)
                                        {
                                            Skill skill = SkillUtil.getSkillbyId(player, player.gender == 0 ? 24 : player.gender == 1 ? 26 : 25);
                                            Skill curSkill = SkillUtil.createSkill(player.gender == 0 ? 24 : player.gender == 1 ? 26 : 25, 1, 0, 0);
                                            if(skill == null)
                                            {
                                                player.playerSkill.skills.add(curSkill);
                                                Service.gI().sendSkillPlayer(player, curSkill);
                                                Service.gI().sendThongBao(player, "Bạn học thành công");
                                                // player.combine.doCombine(7, null, null, (short)curSkill.template.iconId, player.getNPCMenu());
                                            }
                                            else
                                            {
                                                
                                            }
                                        }
                                        break;
                                    case 2:
                                        if(player.inventory.findItemBagByTemp(457) == null || player.inventory.findItemBagByTemp(457).quantity < 50){
                                            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng");
                                            return;
                                        }                             
                                        if(mapId == 154)
                                        {
                                            Skill skill = SkillUtil.getSkillbyId(player, player.gender == 0 ? 24 : player.gender == 1 ? 26 : 25);
                                            Skill curSkill = SkillUtil.createSkill(player.gender == 0 ? 24 : player.gender == 1 ? 26 : 25, 1, 0, 0);
                                            if(skill == null)
                                            {
                                                player.inventory.subQuantityItemsBag(player.inventory.findItemBagByTemp(457), 50);
                                                player.playerSkill.skills.add(curSkill);
                                                Service.gI().sendSkillPlayer(player, curSkill);
                                                Service.gI().sendThongBao(player, "Bạn học thành công");
                                                // player.combine.doCombine(7, null, null, (short)curSkill.template.iconId, player.getNPCMenu());
                                            }
                                            else
                                            {
                                                
                                            }
                                        }
                                        break;
                                }
                            }
                            }
                        
                    };
                    break;
                case TRUNG_THU:
                    if(Setting.EVENT_TRUNG_THU){
                        npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                            @Override
                            public void openMenu(Player pl) {
                                createOtherMenu(pl, 0, "Chào cậu! Ở đây chúng tôi bán những loại bánh sang xịn mịn nhứt", "Cửa hàng", "Đổi quà", "Đóng");
                            }

                            @Override
                            public void confirmMenu(Player player, int select) {
                                if (player.getIndexMenu() == 0) {
                                    switch (select) {
                                        case 0:
                                            Shop.gI().openShop(player, 41);
                                            break;
                                        case 1:
                                            createOtherMenu(player, 1, "Chào cậu! Cậu muốn đổi x99 cà rốt lấy quà gì nào?", "Cải Trang\nBunma", "Cải Trang\nThỏ Đại Ca", "Đóng");
                                            break;
                                    }
                                } else if (player.getIndexMenu() == 1) {
                                    switch (select) {
                                        case 1:
                                            Item item = player.inventory.findItemBagByTemp(462);
                                            if (item != null && item.quantity >= 99) {
                                                player.inventory.subQuantityItemsBag(item, 99);
                                                Item thoDaiCa = ItemData.gI().get_item(463);
                                                thoDaiCa.itemOptions.add(new ItemOption(93, 15));
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                thoDaiCa.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(thoDaiCa);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được cải trang thành Thỏ Đại Ca");
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (99 - item.quantity) + " củ cà rốt nữa");
                                            }
                                            break;
                                        case 0:
                                            Item item2 = player.inventory.findItemBagByTemp(462);
                                            if (item2 != null && item2.quantity >= 99) {
                                                player.inventory.subQuantityItemsBag(item2, 99);
                                                Item thoBunma = ItemData.gI().get_item(464);
                                                thoBunma.itemOptions.add(new ItemOption(77, Util.nextInt(5, 35)));
                                                thoBunma.itemOptions.add(new ItemOption(103, Util.nextInt(5, 35)));
                                                thoBunma.itemOptions.add(new ItemOption(50, Util.nextInt(5, 35)));
                                                thoBunma.itemOptions.add(new ItemOption(93, 15));
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                thoBunma.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(thoBunma);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được cải trang thành Bunma");
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (99 - item2.quantity) + " củ cà rốt nữa");
                                            }
                                            break;
                                    }
                                }
                            }
                        };
                    }
                    break;
                case TRUONG_LAO_GURU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.gender != 1){
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Về hành tinh của ngươi mà thể hiện");
                                return;
                            }
//                            if (TaskData.getTask(pl.taskId + 1) != null && (TaskData.isBaoCao2(TaskData.getTask(pl.taskId).subNames[pl.taskIndex]) || (pl.taskId == 13 && pl.clan != null && pl.clan.members.size() >= 5))) {
//                                Service.gI().congTiemNang(pl, (byte) 2, TaskData.getTask(pl.taskId).getTNSM());
//                                if(TaskData.getTask(pl.taskId).type == 1){
//                                    Item it = ItemData.gI().get_item(1184);
//                                    it.itemOptions.add(new ItemOption(30, 0));
//                                    it.quantity = 54;
//                                    pl.inventory.addItemBag(it);
//                                    pl.inventory.sendItemBags();
//                                    pl.inventory.sortItemBag();
//                                    Service.gI().sendThongBao(pl, "Bạn nhận được "+ it.quantity +" "+it.template.name);
//                                }
//                                pl.taskId += 1;
//                                pl.taskIndex = 0;
//                                pl.taskCount = 0;
//                                Service.gI().ClearTask(pl);
//                                Service.gI().send_task(pl, TaskData.getTask(pl.taskId));
//                                return;
//                            }
                            String[] strMenu = null;
                            strMenu = new String[]{
                                "Nói\nchuyện"
                            };
                            createOtherMenu(pl, 0, "Con muốn hỏi gì nào?", strMenu);
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            String[] strMenu = null;
                            String Say = null;
                            if(player.getIndexMenu() == 0){
                                switch (select) {
                                    case 0:
                                        strMenu = new String[]{
                                            "Nhiệm vụ",
                                        };
                                        if(player.clan != null){
                                            strMenu = new String[]{
                                                "Nhiệm vụ",
                                                "Bang Hội"
                                            };
                                        }
                                        Say = "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào?";
                                        createOtherMenu(player, 1, Say, strMenu);
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                switch (select) {
                                    case 0:
                                        Task task = TaskData.getTask(player.taskId);
                                        Service.gI().sendPopUpMultiLine(player, tempId, 0, Service.gI().NpcTraTask(player, task.subNames[player.taskIndex]));
                                        break;
                                    case 1:
                                        if(player.clan != null && player.clan.members.stream().anyMatch(m -> m != null && m.id == player.id && m.role == 0)){
                                            strMenu = new String[]{
                                                "Về khu\nvực bang",
                                                "Giải tán\nbang hội",
                                                "Đóng"
                                            };
                                        }
                                        else {
                                            strMenu = new String[]{
                                                "Về khu\nvực bang",
                                                "Đóng"
                                            };
                                        }
                                        createOtherMenu(player, 2, "Con muốn ta giúp gì nào?\nCon muốn thực hiện một số chức năng bang hội à?\nĐể ta giúp con.", strMenu);
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 2){
                                boolean isMember = player.clan != null && player.clan.members.stream().anyMatch(m -> m != null && m.id == player.id && m.role == 0);
                                switch(select){
                                    case 0:
                                        ChangeMap.gI().changeMap(player, 153, 100, 432);
                                        break;
                                    case 1:
                                        if(isMember){
                                            ClanService.gI().DelClan(player);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case VUA_VEGETA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.gender != 2){
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Về hành tinh của ngươi mà thể hiện");
                                return;
                            }
                            String[] strMenu = null;
                            strMenu = new String[]{
                                "Nói\nchuyện"
                            };
                            createOtherMenu(pl, 0, "Con muốn hỏi gì nào?", strMenu);
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            String[] strMenu = null;
                            String Say = null;
                            if(player.getIndexMenu() == 0){
                                switch (select) {
                                    case 0:
                                        strMenu = new String[]{
                                            "Nhiệm vụ",
                                        };
                                        if(player.clan != null){
                                            strMenu = new String[]{
                                                "Nhiệm vụ",
                                                "Bang Hội"
                                            };
                                        }
                                        Say = "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào?";
                                        createOtherMenu(player, 1, Say, strMenu);
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                switch (select) {
                                    case 0:
                                        Task task = TaskData.getTask(player.taskId);
                                        Service.gI().sendPopUpMultiLine(player, tempId, 0, Service.gI().NpcTraTask(player, task.subNames[player.taskIndex]));
                                        break;
                                    case 1:
                                        if(player.clan != null && player.clan.members.stream().anyMatch(m -> m != null && m.id == player.id && m.role == 0)){
                                            strMenu = new String[]{
                                                "Về khu\nvực bang",
                                                "Giải tán\nbang hội",
                                                "Đóng"
                                            };
                                        }
                                        else {
                                            strMenu = new String[]{
                                                "Về khu\nvực bang",
                                                "Đóng"
                                            };
                                        }
                                        createOtherMenu(player, 2, "Con muốn ta giúp gì nào?\nCon muốn thực hiện một số chức năng bang hội à?\nĐể ta giúp con.", strMenu);
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 2){
                                boolean isMember = player.clan != null && player.clan.members.stream().anyMatch(m -> m != null && m.id == player.id && m.role == 0);
                                switch(select){
                                    case 0:
                                        ChangeMap.gI().changeMap(player, 153, 100, 432);
                                        break;
                                    case 1:
                                        if(isMember){
                                            ClanService.gI().DelClan(player);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case QUY_LAO_KAME:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String[] strMenu = null;
                            if(Setting.EVENT_HALLOWEEN){
                                strMenu = new String[]{
                                    "Nói chuyện",
                                    "Sự kiện\nHalloween\n2022",
                                    "Skip NV Trung Uý Trắng"
                                };
                            }else if(Setting.EVENT_SPEACIAL){
                                strMenu = new String[]{
                                    "Nói chuyện",
                                    "Sự kiện\nSpeacial\n2022",
                                    "Skip NV Trung Uý Trắng"
                                };
                            }
                            else if(Setting.EVENT_GIANG_SINH){
                                strMenu = new String[]{
                                    "Nói chuyện",
                                    "Sự kiện\nGiáng sinh\n2022",
                                    "Skip NV Trung Uý Trắng",
                                  /*  "Top\nNạp thẻ",
                                    "Top\nSức Mạnh",
                                    "Top\nNhiệm Vụ",*/
                                };
                            }
                            else if(Setting.isNEW_2023()){
                                boolean isShow = GiftCode.checkGift(pl, GiftCode.QUA_1) && GiftCode.checkGift(pl, GiftCode.QUA_2);
                                if(TopInfo.IndexTOP(pl.name, TopInfo.topSuKien) > 0 && !isShow){
                                    strMenu = new String[]{
                                        "Nói chuyện",
                                        "Sự kiện\nTết\n2023",
                                        "Quà đua\nTop"
                                    };
                                }
                                else
                                {
                                    strMenu = new String[]{
                                        "Nói chuyện",
                                        "Sự kiện\nTết\n2023",
                                        "Skip NV Trung Uý Trắng"
                                    };
                                }
                            }
                            else{
                                strMenu = new String[]{
                                    "Nói\nchuyện",
                                    "Top\nNạp thẻ",
                                    "Top\nSức Mạnh",
                                    "Top\nNhiệm Vụ",
                                    "Skip NV Trung Uý Trắng",
                                    
                                    
                                        
                                };
                            }
                            createOtherMenu(pl, 0, "Con muốn hỏi gì nào?", strMenu);
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            String[] strMenu = null;
                            String Say = null;
                            if(player.getIndexMenu() == 0){
                                switch (select) {
                                    case 0:
                                        strMenu = new String[]{
                                            "Nhiệm vụ",
                                            "Kho báu\ndưới biển"
                                        };
                                        Say = "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào?";
                                        if(player.clan != null){
                                            strMenu = new String[]{
                                                "Nhiệm vụ",
                                                "Bang Hội",
                                                "Kho báu\ndưới biển"
                                            };
                                        }
                                        createOtherMenu(player, 3, Say, strMenu);
                                        break;
                                    case 1:
                                        strMenu = new String[]{
                                            "Từ chối"
                                        };
                                        Say = "Xin chào!";
                                        if(Setting.EVENT_HALLOWEEN){
                                            strMenu = new String[]{
                                                "Tùy chọn\n1",
                                                "Tùy chọn\n2",
                                                "Tùy chọn\n3",
                                                "Từ chối"
                                            };
                                            Say = "|7|Bạn có muốn đổi x60 bí ngô lấy\n1 trong các vật phẩm sự kiện Halloween:\n|1|1) Capsule Halloween\n|1|2) Lưỡi hái Thần Chết\n|1|3) 12 vệ tinh trí tuệ và phòng thủ";
                                        }else if(Setting.EVENT_SPEACIAL){
                                            strMenu = new String[]{
                                                "Đổi Rương",
                                                "Từ chối"
                                            };
                                            Say = "|7|Bạn có thể đổi tối đa rương gỗ!\n Với mức giá: " +Util.powerToString(50000000)+" vàng / 1.";
                                        }
                                        else if(Setting.EVENT_GIANG_SINH){
                                            strMenu = new String[]{
                                                "Đổi x99\nKẹo\ngiáng sinh",
                                                "Đổi x1\nTất, vớ\ngiáng sinh",
                                                "Top 100\nSự kiện"
                                            };
                                            Say = "Sự kiện giáng sinh 2022 đang diễn ra\nChi tiết tại fanpage.";
                                        }
                                        else if(Setting.isNEW_2023()){
                                            strMenu = new String[]{
                                                "Tặng \nMâm ngũ quả",
                                                "Bày Mâm ngũ quả",
                                                "Top 100\nSự kiện"
                                            };
                                            Say = "Sự kiện tết 2023 đang diễn ra\nChi tiết tại fanpage.";
                                        }
                                        else{
                                            Service.gI().sendTabTop(player, "Top Nạp Thẻ", TopInfo.topNap, true);
                                            break;
                                        }
                                        createOtherMenu(player, 1, Say, strMenu);
                                        break;
                                        case 2:
                                            if(Setting.EVENT_GIANG_SINH ||Setting.EVENT_HALLOWEEN || Setting.isNEW_2023()  || Setting.EVENT_SPEACIAL ){
                                             if(
                                            player.taskId == 19 && player.taskIndex == 1){
                                            Service.gI().send_task_next(player);
                                            }
                                            else{
                                                Service.gI().sendThongBao(player, "Có phải NV TUT đâu mà Skip " );
                                                return;
                                            }                                     
                                        }
                                            Service.gI().sendTabTop(player, "Top Sức Mạnh", TopInfo.topSM, true);
                                            break;
                                        case 3:
                                            Service.gI().sendTabTop(player, "Top Nhiệm Vụ", TopInfo.topNV, true);
                                            break;  
                                        case 4:
                                            if(
                                            player.taskId == 19 && player.taskIndex == 1){
                                            Service.gI().send_task_next(player);
                                            }
                                            else{
                                                Service.gI().sendThongBao(player, "Có phải NV TUT đâu mà Skip " );
                                                return;
                                            }
                                        case 5:
                                           Item itc = ItemData.gI().get_item(457);
                                            itc.quantity = 1;
                                           player.inventory.addItemBag(itc);
                                           Service.gI().sendThongBao(player, "Bú vàng");
                                           player.inventory.sendItemBags();
                                            break;
                                        case 6:
                                            Item itc1 = ItemData.gI().get_item(16);
                                            itc1.quantity = 1;
                                           player.inventory.addItemBag(itc1);
                                           Service.gI().sendThongBao(player, "Bú 3s");  
                                           player.inventory.sendItemBags();
                                        break;
                                      
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                if (player.getBagNull() <= 0)
                                {
                                    Service.gI().sendThongBao(player, "Hành Trang Không Đủ Ô Trống");
                                    return;
                                }
                                if(Setting.isNEW_2023()){
                                    switch (select) {
                                        case 0:
                                            Item item = player.inventory.findItemBagByTemp(1193);
                                            if (item != null)
                                            {
                                                player.inventory.subQuantityItemsBag(item, 1);
                                                Item HopQua = ItemData.gI().get_item(1198);
                                                HopQua.itemOptions.clear();
                                                HopQua.itemOptions.add(new ItemOption(174, 2023));
                                                HopQua.itemOptions.add(new ItemOption(93, 30));
                                                player.inventory.addItemBag(HopQua);
                                                player.inventory.sendItemBags();
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + HopQua.template.name);
                                            }
                                            break;
                                        case 1:
                                            Item cau = player.inventory.findItemBagByTemp(1188);
                                            Item dua = player.inventory.findItemBagByTemp(1189);
                                            Item du = player.inventory.findItemBagByTemp(1190);
                                            Item xoai = player.inventory.findItemBagByTemp(1191);
                                            Item sung = player.inventory.findItemBagByTemp(1192);
                                            Item thiep1 = player.inventory.findItemBagByTemp(1202);
                                            Item thiep2 = player.inventory.findItemBagByTemp(1203);
                                            Item thiep3 = player.inventory.findItemBagByTemp(1204);
                                            if ((thiep1 != null || thiep2 != null || thiep3 != null) && cau != null && cau.quantity >= 20 && dua != null && dua.quantity >= 20 && du != null && du.quantity >= 20 && xoai != null && xoai.quantity >= 20 && sung != null && sung.quantity >= 20)
                                            {
                                                player.inventory.subQuantityItemsBag(cau, 20);
                                                player.inventory.subQuantityItemsBag(dua, 20);
                                                player.inventory.subQuantityItemsBag(du , 20);
                                                player.inventory.subQuantityItemsBag(xoai, 20);
                                                player.inventory.subQuantityItemsBag(sung, 20);
                                                player.inventory.subQuantityItemsBag(thiep1 == null ? (thiep2 == null ? thiep3 : thiep2) : thiep1, 1);
                                                Item MamNguQua = ItemData.gI().get_item(1193);
                                                MamNguQua.itemOptions.clear();
                                                MamNguQua.itemOptions.add(new ItemOption(174, 2023));
                                                MamNguQua.itemOptions.add(new ItemOption(93, 30));
                                                player.inventory.addItemBag(MamNguQua);
                                                player.inventory.sendItemBags();
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + MamNguQua.template.name);
                                            }
                                            break;
                                        case 2:
                                            if(
                                            player.taskId == 19 && player.taskIndex == 1){
                                            Service.gI().send_task_next(player);
                                            }
                                            else{
                                                Service.gI().sendThongBao(player, "Có phải NV TUT đâu mà Skip " );
                                                return;
                                            }
                                       /* case 2:
                                            Service.gI().sendTabTop(player, "Top sự kiện", TopInfo.topSuKien, false);
                                            break;*/
                                        /* case 2:
                                            Service.gI().sendTabTop(player, "Top sự kiện", TopInfo.topSuKien, false);
                                            break;*/
                                        /* case 2:
                                            Service.gI().sendTabTop(player, "Top sự kiện", TopInfo.topSuKien, false);
                                            break;*/    
                                    }
                                }
                                else if(Setting.EVENT_HALLOWEEN){
                                    switch (select) {
                                        case 0:
                                            Item item = player.inventory.findItemBagByTemp(585);
                                            if (item != null && item.quantity >= 60) {
                                                player.inventory.subQuantityItemsBag(item, 60);
                                                Item capsule = ItemData.gI().get_item(818);
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                capsule.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(capsule);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + capsule.template.name);
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (60 - item.quantity) +" " + item.template.name);
                                            }
                                            break;
                                        case 1:
                                            Item item2 = player.inventory.findItemBagByTemp(585);
                                            if (item2 != null && item2.quantity >= 60) {
                                                player.inventory.subQuantityItemsBag(item2, 60);
                                                Item LuoiHai = ItemData.gI().get_item(740);
                                                LuoiHai.itemOptions.add(new ItemOption(77, Util.nextInt(5, 30)));
                                                LuoiHai.itemOptions.add(new ItemOption(103, Util.nextInt(5, 30)));
                                                LuoiHai.itemOptions.add(new ItemOption(50, Util.nextInt(5, 30)));
                                                LuoiHai.itemOptions.add(new ItemOption(5, Util.nextInt(5, 30)));
                                                LuoiHai.itemOptions.add(new ItemOption(88, Util.nextInt(5, 20)));
                                                LuoiHai.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                LuoiHai.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(LuoiHai);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + LuoiHai.template.name);
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (60 - item2.quantity) +" " + item2.template.name);
                                            }
                                            break;
                                        case 2:
                                            Item item3 = player.inventory.findItemBagByTemp(585);
                                            if (item3 != null && item3.quantity >= 60) {
                                                player.inventory.subQuantityItemsBag(item3, 60);
                                                Item i1 = ItemData.gI().get_item(343);
                                                i1.quantity = 12;
                                                player.inventory.addItemBag(i1);
                                                i1 = ItemData.gI().get_item(344);
                                                i1.quantity = 12;
                                                player.inventory.addItemBag(i1);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được x12 vệ tinh trí tuệ và phòng thủ");
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + (60 - item3.quantity) +" " + item3.template.name);
                                            }
                                            break;
                                    }
                                }else if(Setting.EVENT_SPEACIAL){
                                    switch (select) {
                                        case 0:
                                            Service.gI().sendInputText(player, "Đổi rương quà", 1, new int[]{1}, new String[]{"Nhập số lượng rương muốn đổi"});
                                            break;
                                        case 2:
                                            if(
                                            player.taskId == 19 && player.taskIndex == 1){
                                            Service.gI().send_task_next(player);
                                            }
                                            else{
                                                Service.gI().sendThongBao(player, "Có phải NV TUT đâu mà Skip " );
                                                return;
                                            }    
                                    }
                                }else if(Setting.EVENT_GIANG_SINH){
                                    switch (select) {
                                        case 0:
                                            Item item = player.inventory.findItemBagByTemp(533);
                                            if (item != null && item.quantity >= 99) {
                                                player.inventory.subQuantityItemsBag(item, 99);
                                                Item nonNoel = ItemData.gI().get_item(player.gender == 0 ? 387 : player.gender == 1 ? 390 : 393);
                                                nonNoel.itemOptions.clear();
                                                nonNoel.itemOptions.add(new ItemOption(77, Util.nextInt(15, 50)));
                                                nonNoel.itemOptions.add(new ItemOption(103, Util.nextInt(15, 50)));
                                                nonNoel.itemOptions.add(new ItemOption(50, Util.nextInt(5, 30)));
                                                nonNoel.itemOptions.add(new ItemOption(80, Util.nextInt(15, 50)));
//                                                nonNoel.itemOptions.add(new ItemOption(167, 0));
                                                nonNoel.itemOptions.add(new ItemOption(106, 0));
                                                double rd = Util.nextdouble(100);
                                                if(rd > 0.01){
                                                    nonNoel.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
                                                }
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                nonNoel.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(nonNoel);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + nonNoel.template.name);
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                String str = "99 Kẹo giáng sinh";
                                                if(item != null){
                                                    str = (99 - item.quantity) + " " + item.template.name;
                                                }
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + str);
                                            }
                                            break;
                                        case 1:
                                            Item item2 = player.inventory.findItemBagByTemp(649);
                                            if (item2 != null && item2.quantity >= 1) {
                                                player.inventory.subQuantityItemsBag(item2, 1);
                                                Item quaNoel = ItemData.gI().get_item(648);
                                                quaNoel.itemOptions.clear();
                                                quaNoel.itemOptions.add(new ItemOption(174, 2022));
                                                quaNoel.itemOptions.add(new ItemOption(30, 0));
                                                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                                                quaNoel.buyTime = timenow.getTime();
                                                player.inventory.addItemBag(quaNoel);
                                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + quaNoel.template.name);
                                                player.inventory.sendItemBags();
                                                player.inventory.sortItem(player.inventory.itemsBag);
                                            } else {
                                                String str = "1 Tất,vớ giáng sinh";
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + str);
                                            }
                                            break;
                                        case 2:
                                            Service.gI().sendTabTop(player, "Top sự kiện", TopInfo.topSuKien, false);
                                            break;
                                            
                                    }
                                }
                            }
                            else if(player.getIndexMenu() == 2){
                                switch (select) {
                                    case 0:
                                        if(player.inventory.findItemBagByTemp(457) == null || player.inventory.findItemBagByTemp(457).quantity < 5){
                                            Service.gI().sendThongBao(player, "Bạn không đủ thỏi vàng");
                                            return;
                                        }
                                        player.inventory.subQuantityItemsBag(player.inventory.findItemBagByTemp(457), 5);
                                        player.inventory.addItemBag(ItemData.gI().get_item(1071));
                                        player.inventory.sendItemBags();
                                        player.inventory.sortItemBag();
                                        Service.gI().sendThongBao(player, "Chúc mừng bạn nhận được 1 chìa khóa");
                                        break;
                                    case 2:
                                            if(
                                            player.taskId == 19 && player.taskIndex == 1){
                                            Service.gI().send_task_next(player);
                                            }
                                            else{
                                                Service.gI().sendThongBao(player, "Có phải NV TUT đâu mà Skip " );
                                                return;
                                            }    
                                }
                            }
                            else if(player.getIndexMenu() == 3){
                                switch(select){
                                    case 0:
                                        if(player.taskId == 24 && player.taskIndex == 2){

                                            Service.gI().sendThongBao(player, "Bạn đã cứu sống quy lão với thuốc từ Calich");
                                            Service.gI().send_task_next(player);
                                        }
                                        Task task = TaskData.getTask(player.taskId);
                                        Service.gI().sendPopUpMultiLine(player, tempId, 0, Service.gI().NpcTraTask(player, task.subNames[player.taskIndex]));
                                        break;
                                    case 1:
                                        if(player.clan != null && player.clan.members.stream().anyMatch(m -> m != null && m.id == player.id && m.role == 0)){
                                            strMenu = new String[]{
                                                "Về khu\nvực bang",
                                                "Giải tán\nbang hội",
                                                "Đóng"
                                            };
                                        }
                                        else {
                                            strMenu = new String[]{
                                                "Về khu\nvực bang",
                                                "Đóng"
                                            };
                                        }
                                        if(player.clan != null){
                                            createOtherMenu(player, 4, "Con muốn ta giúp gì nào?\nCon muốn thực hiện một số chức năng bang hội à?\nĐể ta giúp con.", strMenu);
                                            break;
                                        }
                                    case 2: // Đóng - Bản đồ kho báu
                                        strMenu = new String[]{
                                            "Top\nBang hội",
                                            "Thành tích\nBang",
                                            player.clan != null && player.clan.KhoBauDuoiBien == null || player.clan != null && player.clan.KhoBauDuoiBien.time <= 0 ? "Chọn\ncấp độ" : "Đồng ý",
                                            "Tự chối"
                                        };
                                        String npcSay = null;
                                        npcSay = "Đây là bản đồ kho báu hải tặc tí hon\n";
                                        npcSay += "Các con cứ yên tâm lên đường\n";
                                        npcSay += "Ở đây có ta lo\n";
                                        npcSay += "Nhớ chọn cấp độ vừa sức mình nhé\n";
                                        if(player.clan != null && player.clan.KhoBauDuoiBien != null && player.clan.KhoBauDuoiBien.time > 0){
                                            npcSay = "Bang hội của cậu đang tham gia Bản đồ kho báu cấp độ " + player.clan.KhoBauDuoiBien.level;
                                            npcSay += "\ncậu có muốn đi cùng họ không ?(" + Util.timeAgo(Util.currentTimeSec(), player.clan.KhoBauDuoiBien.timeJoin / 1000) + ")";
                                        }
                                        createOtherMenu(player, 5, npcSay, strMenu);
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 4){
                                boolean isMember = player.clan != null && player.clan.members.stream().anyMatch(m -> m != null && m.id == player.id && m.role == 0);
                                switch(select){
                                    case 0:
                                        ChangeMap.gI().changeMap(player, 153, 100, 432);
                                        break;
                                    case 1:
                                        if(isMember){
                                            ClanService.gI().DelClan(player);
                                        }
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 5){
                                switch(select){
                                    case 0:
                                        Service.gI().sendTabTop(player, "Top bang hội", new ArrayList<>(), true);
                                        break;
                                    case 1:
                                        Service.gI().sendTabTop(player, "Thành tích bang", new ArrayList<>(), true);
                                        break;
                                    case 2:
//                                        if(player != null){
//                                            Service.gI().sendPopUpMultiLine(player, tempId, 0, "Kho báu dưới biến chưa hoàn thành!");
//                                            break;
//                                        }
                                        if(player.clan == null)
                                        {
                                            Service.gI().sendPopUpMultiLine(player, tempId, 0, "Chỉ tiếp bang hội không tiếp khách vãng lai");
                                        }
                                        else if(player.clan.KhoBauDuoiBien == null && player.clan.members.stream().anyMatch(m -> m.id == player.id && m.role == 0)){
                                            player.typeInput = 11;
                                            Service.gI().sendInputText(player, "Hãy chọn cấp đồ từ 1-110", 1, new int[]{1}, new String[]{"Cấp độ"});
                                        }
                                        else if(player.clan.KhoBauDuoiBien != null && player.clan.KhoBauDuoiBien.time > 0){
                                            player.type = 5;
                                            player.maxTime = 10;
                                            Service.gI().Transport(player, 1);
                                            Service.gI().clearMap(player);
                                        }
                                        else if(player.clan.KhoBauDuoiBien != null && player.clan.KhoBauDuoiBien.time <= 0){
                                            Service.gI().sendThongBao(player, "Tạm thời chỉ có 1 lượt");
                                        }
                                        else{
                                            Service.gI().sendThongBao(player, "Chức năng này chỉ dành cho bang chủ!");
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ONG_GOHAN:
                case ONG_PARAGUS:
                case ONG_MOORI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (mapId == 21 + pl.gender) {
                                if(Setting.SERVER_TEST){
//                                    if(pl.session.get_active() < 2){
//                                        Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Chưa đủ thẩm quyền để nhận");
//                                        return;
//                                    }
                                    String[] menu = new String[]
                                    {
                                        "Nhận\n1 Sao",
                                        "Nhận\n2 Sao",
                                        "Nhận\n3 Sao",
                                        "Nhận\n4 Sao",
                                        "Nhận\n5 Sao",
                                        "Nhận\n6 Sao",
                                        "Nhận\n7 Sao",
                                        "Nhận\nSao pha lê",
                                        "Nhận\nĐệ tử"
                                    };
                                    if(pl.pet != null)
                                    {
                                        menu = new String[]
                                        {
                                            "Nhận\n1 Sao",
                                            "Nhận\n2 Sao",
                                            "Nhận\n3 Sao",
                                            "Nhận\n4 Sao",
                                            "Nhận\n5 Sao",
                                            "Nhận\n6 Sao",
                                            "Nhận\n7 Sao",
                                            "Nhận\nSao pha lê"
                                        };
                                    }
                                    createOtherMenu(pl, 0, "Xin chào " + pl.name + " bạn muốn nhận gì?", menu);
                                }
                                else
                                {
                                    createOtherMenu(pl, 0, "Xin chào " + pl.name + " bạn muốn nhận gì?", "Nhận ngọc","GiftCode","Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.getIndexMenu() == 0){
                                if(Setting.SERVER_TEST){
                                    if(select == 0){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(14);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }else if(select == 1){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(15);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }
                                    else if(select == 2){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(16);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }
                                    else if(select == 3){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(17);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }
                                    else if(select == 4){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(18);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }
                                    else if(select == 5){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(19);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }
                                    else if(select == 6){
                                        if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                        }
                                        Item item = ItemData.gI().get_item(20);
                                        player.inventory.addItemBag(item);
                                        player.inventory.sendItemBags();
                                        Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                    }
                                    else if(select == 7){
                                        if(player.getBagNull() < 7){
                                            Service.gI().sendThongBao(player, "Bạn không đủ 7 ô hành trang để nhận");
                                            return;
                                        }
                                        for (int i = 0; i < 7; i++) {
                                            Item item = ItemData.gI().get_item(441 + i);
                                            player.inventory.addItemBag(item);
                                            Service.gI().sendThongBao(player, "Bạn nhận thành công " + item.template.name);
                                        }
                                        player.inventory.sendItemBags();
                                    }
                                    else if(select == 8){
                                        if(player.pet == null){
                                            PetDAO.newPet(player);
                                            player.pet.changeStatus((byte)1);
                                            player.sendMeHavePet();
                                            player.pet.point.updateall();
                                        }
                                    }
                                }
                                else
                                {
                                    if(select == 0)
                                    {
                                        if(player.inventory.ruby >= 50000000){
                                            Service.gI().sendThongBao(player, "Vui lòng sử dụng hết ngọc rồi tiếp tục thực hiện");
                                            return;
                                        }
                                        player.inventory.ruby += 5000;
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Nhận thành công 5.000 ngọc hồng");
                                         
                                    }
                                    if(select == 1)
                                    {
                                        player.typeInput = 0;
                                        Service.gI().sendInputText(player, "Nhập GiftCode được TuanZin cung cấp", 1, new int[]{1}, new String[]{"Ví dụ: tuanzin"});
                                        
                                    }
                                }
                            }
                        }
                    };
                    break;
                case QUOC_VUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1 || pl.pet != null && pl.pet.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1)
                            {
                                String[] menu = new String[]
                                {
                                    "Bản thân",
                                    "Đệ tử",
                                    "Từ chối"
                                };
                                if(pl.pet == null){
                                    menu = new String[]
                                    {
                                        "Bản thân",
                                        "Từ chối"
                                    };
                                }
                                createOtherMenu(pl, 0, "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?", menu);
                            }
                            else
                            {
                                createOtherMenu(pl, 0, "Sức mạnh của con đã đạt mức tối đa", "OK");
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.getIndexMenu() == 0)
                            {
                                switch (select) {
                                    case 0:
                                        if(player.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1)
                                        {
                                            createOtherMenu(player, 1, "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên " + Util.powerToString(Setting.LIMIT_SUC_MANH[player.point.limitPower + 1]) + ".\nLưu ý: 40 tỉ trở lên sức mạnh của con sẽ tăng chậm đáng kể", "Nâng ngay\n" + Util.powerToString(Setting.GOLD_OPEN_GHSM) + " vàng", "Từ chối");
                                        }
                                        else if(player.point.limitPower >= Setting.LIMIT_SUC_MANH.length - 1 && player.pet != null && player.pet.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1)
                                        {
                                            Service.gI().sendThongBao(player, "Giới hạn sức mạnh của con đã đạt tối đa.");
                                        }
                                        break;
                                    case 1:
                                        if(player.pet == null)
                                        {
                                            return;
                                        }
                                        if(player.pet.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1)
                                        {
                                            createOtherMenu(player, 2, "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên " + Util.powerToString(Setting.LIMIT_SUC_MANH[player.pet.point.limitPower + 1]) + ".\nLưu ý: 40 tỉ trở lên sức mạnh của đệ tử con sẽ tăng chậm đáng kể", "Nâng ngay\n" + Util.powerToString(Setting.GOLD_OPEN_GHSM) + " vàng", "Từ chối");
                                        }
                                        else if(player.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1 && player.pet != null && player.pet.point.limitPower >= Setting.LIMIT_SUC_MANH.length - 1)
                                        {
                                            Service.gI().sendThongBao(player, "Giới hạn sức mạnh của đệ tử đã đạt tối đa.");
                                        }
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1)
                            {
                                switch (select) {
                                    case 0:
                                        if (player.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1) {
                                            if(player.inventory.gold < Setting.GOLD_OPEN_GHSM){
                                                Service.gI().sendThongBao(player, "Cần " + Util.powerToString(Setting.GOLD_OPEN_GHSM) + " vàng");
                                                break;
                                            }
                                            player.inventory.gold -= Setting.GOLD_OPEN_GHSM;
                                            player.point.limitPower++;
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Con đã mở thành công giới hạn sức mạnh.");
                                        }
                                        else
                                        {
                                            Service.gI().sendThongBao(player, "Giới hạn sức mạnh của con đã đạt tối đa.");
                                        }
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 2)
                            {
                                switch (select) {
                                    case 0:
                                        if (player.pet.point.limitPower < Setting.LIMIT_SUC_MANH.length - 1)
                                        {
                                            if(player.inventory.gold < Setting.GOLD_OPEN_GHSM)
                                            {
                                                Service.gI().sendThongBao(player, "Cần " + Util.powerToString(Setting.GOLD_OPEN_GHSM) + " vàng");
                                                break;
                                            }
                                            player.inventory.gold -= Setting.GOLD_OPEN_GHSM;
                                            player.pet.point.limitPower++;
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Con đã mở thành công giới hạn sức mạnh.");
                                        }
                                        else
                                        {
                                            Service.gI().sendThongBao(player, "Giới hạn sức mạnh của đệ tử đã đạt tối đa.");
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case GHI_DANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(mapId == 52){
                                createOtherMenu(pl, 0, DHVT.gI().Giai(pl), "Thông tin\nChi tiết", DHVT.gI().CanReg(pl) ? "Đăng ký" : "OK", "Đại Hội\nVõ Thuật\nLần thứ\n23");
                            }
                            else if(mapId == 129){
                                String[] menus = {
                                    "Hướng\ndẫn\nthêm",
                                    pl.DHVT_23.isStart ? "Hủy\nđăng ký" : "Thi đấu\n" + Util.powerToString(pl.DHVT_23.die * Setting.GOLD_DHVT23) + "\nvàng",
                                    "Về\nĐại Hội\nVõ Thuật"
                                };
                                if(pl.DHVT_23.step > 0 && !pl.DHVT_23.isDrop)
                                {
                                    menus = new String[]{
                                        "Hướng\ndẫn\nthêm",
                                        pl.DHVT_23.isStart ? "Hủy\nđăng ký" : "Thi đấu\n" + Util.powerToString(pl.DHVT_23.die * Setting.GOLD_DHVT23) + "\nvàng",
                                        "Nhận\nthưởng\nRương cấp " + pl.DHVT_23.step,
                                        "Về\nĐại Hội\nVõ Thuật"
                                    };
                                }
                                createOtherMenu(pl, 0, ""
                                        + "Đại hội võ thuật lần thứ 23\n"
                                        + "Diễn ra bất kể ngày đêm, ngày nghỉ, ngày lễ\n"
                                        + "Phần thưởng vô cùng quý giá\n"
                                        + "Nhanh chóng tham gia nào", menus);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(mapId == 52){
                                switch (select) {
                                    case 0:
                                        Service.gI().sendPopUpMultiLine(player, tempId, avartar, DHVT.gI().Info());
                                        break;
                                    case 1:
                                        if (DHVT.gI().CanReg(player)) {
                                            DHVT.gI().Reg(player);
                                        }
                                        break;
                                    case 2:
                                        ChangeMap.gI().changeMap(player, 129, 300, 360);
                                        break;
                                }
                            }
                            else if(mapId == 129){
                                switch (select) {
                                    case 0:
                                        Service.gI().sendPopUpMultiLine(player, tempId, avartar, DHVT.gI().Info());
                                        break;
                                    case 1:
                                        if(player.DHVT_23.isDrop)
                                        {
                                            Service.gI().sendThongBao(player, "Vui lòng mở thưởng trước");
                                            break;
                                        }
                                        if(player.DHVT_23.step >= 11)
                                        {
                                            Service.gI().sendThongBao(player, "Bạn đã thắng người cuối cùng rồi");
                                            break;
                                        }
                                        if(player.DHVT_23.isStart)
                                        {
                                            player.DHVT_23.close();
                                            break;
                                        }
                                        int Gold = player.DHVT_23.die * Setting.GOLD_DHVT23;
                                        if(player.inventory.gold >= Gold)
                                        {
                                            player.inventory.gold -= Gold;
                                            Service.gI().sendMoney(player);
                                            if(player.DHVT_23.next == 0){
                                                player.DHVT_23.Start();
                                            }
                                        }
                                        else
                                        {
                                            Service.gI().sendThongBao(player, "Bạn còn thiếu " + Util.getMoneys(Gold - player.inventory.gold));
                                        }
                                        break;
                                    case 2:
                                        if(player.DHVT_23.step <= 0 || player.DHVT_23.isDrop)
                                        {
                                            ChangeMap.gI().changeMap(player, 52, 300, 336);
                                        }
                                        else
                                        {
                                            
                                            Item it = ItemData.gI().get_item(570);
                                            it.itemOptions.clear();
                                            it.itemOptions.add(new ItemOption(72, player.DHVT_23.step));
                                            it.itemOptions.add(new ItemOption(30, 0));
                                            it.quantity = 1;
                                           
                                            player.inventory.addItemBag(it);
                                            player.inventory.sendItemBags();
                                            Service.gI().sendThongBao(player, "Bạn nhận được "+ it.quantity+ " " +it.template.name+ " " +"cấp "+ player.DHVT_23.step);
                                            player.DHVT_23.isDrop = true;
                                        }
                                        break;
                                    case 3:
                                        if(player.DHVT_23.step > 0)
                                        {
                                            ChangeMap.gI().changeMap(player, 52, 300, 336);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case CUA_HANG_KY_GUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            createOtherMenu(pl, 0, "Cửa hàng chúng tôi luôn mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.", 
                                    "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    Service.gI().sendPopUpMultiLine(player, tempId, avartar, ""
                                            + "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\b"
                                            + "Chỉ với 1 ngọc và 5% phí ký gửi\b"
                                            + "Giá trị ký gửi " + Util.powerToString(Setting.GOLD_MIN_SELL_KI_GUI) + "-" + Util.powerToString(Setting.GOLD_MAX_SELL_KI_GUI) + " vàng hoặc "
                                            + Util.powerToString(Setting.NGOC_MIN_SELL_KI_GUI) + "-" + Util.powerToString(Setting.NGOC_MAX_SELL_KI_GUI) + " ngọc\b"
                                            + "Một người bán, vạn người mua, mại dô, mại dô\n"
                                            + "The store specializes in consignment buying and selling of items\b"
                                            + "With only 1 gem and 5% deposit fee\b"
                                            + "Deposit value " + Util.powerToString(Setting.GOLD_MIN_SELL_KI_GUI) + "-" + Util.powerToString(Setting.GOLD_MAX_SELL_KI_GUI) + " gold or "
                                            + Util.powerToString(Setting.NGOC_MIN_SELL_KI_GUI) + "-" + Util.powerToString(Setting.NGOC_MAX_SELL_KI_GUI) + " gem\b"
                                            + "One seller, ten thousand buyers, for sale, for sale");
                                    break;
                                case 1:
                                    if(player.session.get_version() < 222){
                                        Service.gI().sendPopUpMultiLine(player, tempId, avartar, "Bạn phải sử dụng phiên bản từ v2.2.2 trở lên mới có thể sử dụng tính năng này");
                                        break;
                                    }
                                    Shop.gI().openKiGui(player);
                                    break;
                            }
                        }
                    };
                    break;
                case CALICK:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (mapId >= 27 && mapId <= 29) {
                               // if(pl.taskId == 21 && pl.taskIndex == 0){
                                
                                createOtherMenu(pl, 0, "Chào chú, cháu có thể giúp gì?", "Nói\nchuyện", "Đến\nTương lai", "Từ chối");
                                return;
                            }
                            if (mapId == 102) {
                                createOtherMenu(pl, 0, "Chào chú, cháu có thể giúp gì?", "Quay về\nQuá khứ");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    if(mapId >= 27 && mapId <= 29){
                                        if(
                                            player.taskId == 24 && player.taskIndex == 1){
                                            Service.gI().send_task_next(player);
                                        }
                                        break;
                                    }
                                case 1:
                                    if (mapId == 102) {
                                        ChangeMap.gI().changeMapBySpaceShip(player, 27, -1, -1, ChangeMap.getSpaceShip(player));
                                    }
                                    else
                                    {
                                        if (!ChangeMap.gI().listMapCanChange(player).stream().anyMatch(id -> 102 == id)) {
                                            Service.gI().sendPopUpMultiLine(player,6, 0, "Không thể vào map");
                                            if(player.taskId == 24 && player.taskIndex == 3)
                                            {
                                                Service.gI().send_task_next(player);
                                            }
                                            return;
                                        }
                                        player.type = 0;
                                        player.maxTime = 30;
                                        Service.gI().Transport(player, 0);
                                        Service.gI().clearMap(player);
                                    }
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    };
                    break;
                case DAU_THAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (this.mapId == (21 + pl.gender)) {
                                MagicTree dauThan = pl.magicTree;
                                if (dauThan.isUpdate == true) {
                                    int gemUp = dauThan.gemUpPea;
                                    int phantram = ((dauThan.timeUpdate - ((int) (System.currentTimeMillis() / 1000))) * 100 / dauThan.timeUpPea);
                                    if (phantram <= 80) {
                                        gemUp = (dauThan.gemUpPea * phantram / 100);
                                    }
                                    createMenuMagicTree(pl, 0, new String[]{"Nâng cấp nhanh\n" + Util.powerToString(gemUp).replace(" ", "") + " ngọc", "Hủy nâng đậu\n" + Util.powerToString(dauThan.goldUpPea / 2).replace(" ", "") + " vàng"});
                                } else if (dauThan.currentPea == dauThan.maxPea && dauThan.level < 10) {
                                    createMenuMagicTree(pl, 0, new String[]{"Thu hoạch", "Nâng cấp\n" + Util.powerToString(dauThan.goldUpPea).replace(" ", "") + " vàng"});
                                } else if (dauThan.level == 10 && dauThan.currentPea == dauThan.maxPea) {
                                    createMenuMagicTree(pl, 0, new String[]{"Thu hoạch"});
                                } else if (dauThan.level == 10 && dauThan.currentPea != dauThan.maxPea) {
                                    createMenuMagicTree(pl, 0, new String[]{"Thu hoạch", "Kết hạt nhanh\n" + dauThan.gemOnPea + " ngọc"});
                                } else {
                                    createMenuMagicTree(pl, 0, new String[]{"Thu hoạch", "Nâng cấp\n" + Util.powerToString(dauThan.goldUpPea).replace(" ", "") + " vàng", "Kết hạt nhanh\n" + dauThan.gemOnPea + " ngọc"});
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            MagicTree dau_than = player.magicTree;
                            if (!dau_than.isUpdate) {
                                switch (select) {
                                    case 0:
                                        if (player.getBagNull() <= 0) {
                                            Service.gI().sendThongBao(player, "Hành trang không đủ ô trống");
                                            return;
                                        }
                                        if (dau_than.currentPea > 0) {
                                            Item it = ItemData.gI().get_item(dau_than.idDau);
                                            if (it != null) {
                                                it.quantity += (int)(player.magicTree.currentPea);
                                                player.inventory.addItemBag(it);
                                                player.inventory.sendItemBags();
                                                player.magicTree.currentPea = 0;
                                                player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeOnPea);
                                                player.magicTree.ThuHoach(player);
                                            }
                                        }
                                        break;
                                    case 1:
                                        if (dau_than.level < 10) {
                                            if (player.inventory.gold >= dau_than.goldUpPea) {
                                                player.inventory.gold -= dau_than.goldUpPea;
                                                Service.gI().sendMoney(player);
                                                player.magicTree.isUpdate = true;
                                                player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeUpPea);
                                                player.magicTree.displayMagicTree(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Bạn còn thiếu " + Util.powerToString((long) (player.magicTree.goldUpPea - player.inventory.gold)).replace(" ", "") + " vàng");
                                            }
                                        } else if (player.inventory.getGemAndRuby() >= dau_than.gemOnPea) {
                                            player.inventory.subGemAndRuby(dau_than.gemOnPea);
                                            Service.gI().sendMoney(player);
                                            player.magicTree.currentPea = player.magicTree.maxPea;
                                            player.magicTree.displayMagicTree(player);
                                        }
                                        else {
                                            Service.gI().sendThongBao(player, "Bạn còn thiếu " + Util.powerToString((long)(dau_than.gemOnPea - player.inventory.getGemAndRuby())).replace(" ", "") + " ngọc");
                                        }
                                        break;
                                    case 2://Kết hạt nhanh
                                        if (player.inventory.getGemAndRuby() >= dau_than.gemOnPea) {
                                            player.inventory.subGemAndRuby(dau_than.gemOnPea);
                                            Service.gI().sendMoney(player);
                                            player.magicTree.currentPea = player.magicTree.maxPea;
                                            player.magicTree.displayMagicTree(player);
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn còn thiếu " + Util.powerToString((long) (dau_than.gemOnPea - player.inventory.getGemAndRuby())).replace(" ", "") + " ngọc");
                                        }
                                        break;

                                }
                            } else {
                                switch (select) {
                                    case 0:
                                        int gemUp = dau_than.gemUpPea;
                                        int phantram = ((dau_than.timeUpdate - ((int) (System.currentTimeMillis() / 1000))) * 100 / dau_than.timeUpPea);
                                        if (phantram <= 80) {
                                            gemUp = (dau_than.gemUpPea * phantram / 100);
                                        }
                                        if (player.inventory.getGemAndRuby() >= gemUp) {
                                            player.inventory.subGemAndRuby(gemUp);
                                            Service.gI().sendMoney(player);
                                            player.magicTree.currentPea = 0;
                                            player.magicTree.isUpdate = false;
                                            player.magicTree.level += 1;
                                            player.magicTree.displayMagicTree(player);
                                            player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeOnPea);
                                            player.magicTree.displayMagicTree(player);
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn còn thiếu " + Util.powerToString((long) (gemUp - player.inventory.getGemAndRuby())).replace(" ", "") + " ngọc");
                                        }
                                        break;
                                    case 1:
                                        player.inventory.gold += ((player.magicTree.goldUpPea) / 2);
                                        Service.gI().sendMoney(player);
                                        player.magicTree.currentPea = 0;
                                        player.magicTree.isUpdate = false;
                                        player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeOnPea);
                                        player.magicTree.displayMagicTree(player);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case OSIN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(mapId == 155){
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Trở về\nHành tinh Bill", "Từ chối");
                            }
                            else if(mapId == 50)
                            {
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Đến\nKaio", "Đến\nHành tinh\nBill", "Từ chối");
                            }
                            else if(mapId == 154)
                            {
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Cửa\nhàng", "Đến\nHành tinh\nNgục tù", "Từ chối");
                            }
                            else if(mapId == 52)
                            {
                                createOtherMenu(pl, 0, ""
                                        + "Vào lúc 12h -18h tôi sẽ bí mật...\n"
                                        + "đuổi theo 2 tên đồ tể...\n"
                                        + "Quý vị nào muốn theo thì xin mời !", "OK", "Từ chối");
                            }
                            else if(pl.zone.isOSIN()){
                                if(pl.cFlag == 10){
                                    Service.gI().sendPopUpMultiLine(pl, tempId, avartar, "Về phe của người mà thể hiện");
                                    return;
                                }
                                String[] menus = {
                                    "Về nhà",
                                    "Từ chối"
                                };
                                if(pl.currPower >= pl.maxPower && pl.zone.map.id != 120){
                                    menus = new String[]{
                                        "Xuống\ntầng dưới",
                                        "Từ chối"
                                    };
                                }
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi", menus);
                            }
                            else
                            {
                                super.openMenu(pl);
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(mapId == 155)
                            {
                                switch (select) {
                                    case 0:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 154, -1, 345, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                            else if(mapId == 50)
                            {
                                switch (select) {
                                    case 0:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 48, -1, 345, ChangeMap.getSpaceShip(player));
                                        break;
                                    case 1:
                                         ChangeMap.gI().changeMapBySpaceShip(player, 154, -1, 345, ChangeMap.getSpaceShip(player));
                                     //   Service.gI().sendPopUpMultiLine(player, tempId, 0, "Hiện tại chưa hoàn thành!");
                                        break;
                                }
                            }
                            else if(mapId == 154){
                                switch(select){
                                    case 0:
                                        Shop.gI().openShop(player, this.tempId);
                                        break;
                                    case 1:
                                        if(player.point.power < 60000000000L){
                                            Service.gI().sendThongBao(player, "Yêu cầu phải 60 tỷ sức mạnh trở lên");
                                            break;
                                        }
                                        ChangeMap.gI().changeMapBySpaceShip(player, 155, -1, 97, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                            else if(mapId == 52){
                                switch (select) {
                                    case 0:
                                        if(DHVT.gI().Hour == Setting.TIME_START_OSIN_1 || DHVT.gI().Hour == Setting.TIME_START_OSIN_2 || player.role >= Setting.ROLE_ADMIN){
                                            ChangeMap.gI().changeMap(player, 114, -1, 5);
                                        }
                                        break;
                                }
                            }
                            else if(player.zone.isOSIN()){
                                switch (select) {
                                    case 0:
                                        if(player.currPower >= player.maxPower && player.zone.map.id != 120){
                                            int mapID_osin = player.zone.map.id;
                                            mapID_osin++;
                                            if(mapID_osin == 116){
                                                mapID_osin++;
                                            }
                                            player.currPower = 0;
                                            ChangeMap.gI().changeMap(player, mapID_osin, -1, 5);
                                        }
                                        else{
                                            ChangeMap.gI().changeMap(player, 21 + player.gender, 340, 5);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case KIBIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(mapId == 155){
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Đến\nKaio", "Từ chối");
                            }
                            else if(pl.zone.isOSIN()){
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                            }
                            else{
                                super.openMenu(pl);
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(mapId == 155){
                                ChangeMap.gI().changeMap(player, 21 + player.gender, 340, 5); 
                                switch (select) {
                                    case 0:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 48, -1, 345, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                            else if(player.zone.isOSIN()){
                                switch (select) {
                                    case 0:
                                        ChangeMap.gI().changeMap(player, 21 + player.gender, 340, 5);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case BABIDAY:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.zone.isOSIN()){
                                if(pl.cFlag == 9){
                                    Service.gI().sendPopUpMultiLine(pl, tempId, avartar, "Về phe của người mà thể hiện");
                                    return;
                                }
                                String[] menus = {
                                    "Về nhà",
                                    "Từ chối"
                                };
                                if(pl.currPower >= pl.maxPower && pl.zone.map.id != 120){
                                    menus = new String[]{
                                        "Xuống\ntầng dưới",
                                        "Từ chối"
                                    };
                                }
                                createOtherMenu(pl, 0, "Ta có thể giúp gì cho ngươi", menus);
                            }
                            else{
                                super.openMenu(pl);
                            }
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.zone.isOSIN()){
                                switch (select) {
                                    case 0:
                                        if(player.currPower >= player.maxPower && player.zone.map.id != 120){
                                            int mapID_osin = player.zone.map.id;
                                            mapID_osin++;
                                            if(mapID_osin == 116){
                                                mapID_osin++;
                                            }
                                            player.currPower = 0;
                                            ChangeMap.gI().changeMap(player, mapID_osin, -1, 5);
                                        }
                                        else{
                                            ChangeMap.gI().changeMap(player, 21 + player.gender, 340, 5);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case THAN_VU_TRU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            this.createOtherMenu(pl, 0, "Ta có thể giúp gì cho con", "Trở về\nThần Điện","Đến\nThánh địa Kaio", "Từ chối");
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    ChangeMap.gI().changeMapBySpaceShip(player, 45, -1, 345, ChangeMap.getSpaceShip(player));
                                    break;
                                case 1:
                                    ChangeMap.gI().changeMapBySpaceShip(player, 50, -1, 345, ChangeMap.getSpaceShip(player));
                                    break;
                            }
                        }
                    };
                    break;
                case THUONG_DE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            createOtherMenu(pl, 0, "Ta có thể giúp gì cho con", "Vòng Quay\nMay Mắn", "Đến Kaio", "Đóng");
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.getIndexMenu() == 0){
                                switch (select) {
                                    case 0:
                                        createOtherMenu(player, 1, "Con có thể chọn ngọc từ 1 đến 7 sao\nMỗi lần chọn sẽ tốn 4 ngọc\nChúc con may mắn.", "Vòng Quay\nĐặc biệt", "Rương Phụ\nĐang Có " + player.inventory.itemsBoxSecond.size() + " Món", "Từ chối");
                                        break;
                                    case 1:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 48, -1, 345, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                switch (select) {
                                    case 0:
//                                        Service.gI().sendThongBao(player, "Đang trong quá trình xây dựng");
                                        Service.gI().LuckyRound(player, 0, 0);
                                        break;
                                    case 1:
//                                        Service.gI().sendThongBao(player, "Đang trong quá trình xây dựng");
                                        Shop.gI().openBoxSecond(player);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case BO_MONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (TaskData.getTask(pl.taskId + 1) != null && pl.taskId == 7 && pl.taskIndex == 0) {
                               Service.gI().send_task_next(pl);
                            }
                            createOtherMenu(pl, 0, "Ngươi muốn có thêm ngọc, có nhiều cách, nạp thẻ cào là nhanh nhất, còn không thì chịu khó làm vài nhiệm vụ sẽ được ngọc thưởng", "Nhập\nGiftCode",pl.TaskOrder_Count + " Nhiệm vụ\nhàng ngày");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(player.getIndexMenu() == 0){
                                switch (select) {
                                    case 0:
                                        player.typeInput = 0;
                                        Service.gI().sendInputText(player, "Nhập GiftCode được LyLy cung cấp", 1, new int[]{1}, new String[]{"Ví dụ: tanthuLyLy"});
                                        break;
                                    case 1:
                                        if(player.TaskOrder_Count <= 0){
                                            Service.gI().sendThongBao(player, "Hôm nay bạn đã hoàn thành hết 10 nhiệm vụ rồi");
                                            break;
                                        }
                                        if(player.taskOrder == null || player.taskOrder.taskId == -1){
                                            TaskOrders task = Service.gI().createTask(player);
                                            if(task != null){
                                                player.TaskOrder_Count--;
                                                player.taskOrder = task;
                                                Service.gI().send_task_orders(player, task);
                                                Service.gI().sendPopUpMultiLine(player, tempId, 0, "Nhiệm vụ của bạn là: " + player.taskOrder.name+"\bTiến trình: " + Util.getMoneys(player.taskOrder.count)+" / " + Util.getMoneys(player.taskOrder.maxCount * (player.taskOrder.taskId == 2 ? player.taskOrder.killId : 1))+" ("+((player.taskOrder.count / player.taskOrder.maxCount * (player.taskOrder.taskId == 2 ? player.taskOrder.killId : 1)) * 100) +"%)\bChi tiết: " + player.taskOrder.description+"");
                                            }else{
                                                Service.gI().sendPopUpMultiLine(player, tempId, 0, "Nhiệm vụ lần này có chút trục trặc chắc con không làm được rồi ahihi");
                                            }
                                        }else{
                                            if(player.taskOrder.count < player.taskOrder.maxCount * (player.taskOrder.taskId == 2 ? player.taskOrder.killId : 1)){
                                                this.createOtherMenu(player, 1, "Nhiệm vụ của bạn là: " + player.taskOrder.name, "Chi tiết\nnhiệm vụ", "Hủy bỏ\nnhiệm vụ","Đóng");
                                            }else{
                                                this.createOtherMenu(player, 2, "Chúc mừng bạn đã hoàn thành nhiệm vụ " + player.taskOrder.name, "Trả\nnhiệm vụ","Đóng");
                                            }
                                        }
                                        break;
                                }
                            }
                            else if(player.getIndexMenu() == 1){
                                if(select == 0){
                                    Service.gI().sendPopUpMultiLine(player, tempId, 0, "Nhiệm vụ của bạn là: " + player.taskOrder.name+"\bTiến trình: " + Util.getMoneys(player.taskOrder.count)+" / " + Util.getMoneys(player.taskOrder.maxCount * (player.taskOrder.taskId == 2 ? player.taskOrder.killId : 1))+" ("+((player.taskOrder.count / player.taskOrder.maxCount * (player.taskOrder.taskId == 2 ? player.taskOrder.killId : 1)) * 100) +"%)\bChi tiết: " + player.taskOrder.description+"");
                                }else if(select == 1){
                                    player.taskOrder = new TaskOrders();
                                    Service.gI().sendThongBao(player, "Hủy nhiệm vụ thành công");
                                }
                            }
                            else if(player.getIndexMenu() == 2)
                            {
                                if(player.taskOrder.count >= player.taskOrder.maxCount * (player.taskOrder.taskId == 2 ? player.taskOrder.killId : 1))
                                {
                                    player.taskOrder = new TaskOrders();
                                    Item it = ItemData.gI().get_item(1184);
                                    it.itemOptions.clear();
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    it.quantity = Util.nextInt(1, 5);
                                    
                                    if(Setting.isNEW_2023() && Util.isTrue(40))
                                    {
                                        Item it2 = ItemData.gI().get_item(749);
                                        it2.itemOptions.clear();
                                        it2.itemOptions.add(new ItemOption(174, 2023));
                                        it2.itemOptions.add(new ItemOption(93, 30));
                                        player.inventory.addItemBag(it2);
                                    }
                                    player.inventory.addItemBag(it);
                                    player.inventory.sendItemBags();
                                    Service.gI().sendThongBao(player, "Bạn nhận được "+ it.quantity +" "+it.template.name);
                                }
                            }
                        }
                    };
                    break;
                case RUONG_DO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if(pl.zone.map.id == 21 + pl.gender){
                                pl.openBox();
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                        }
                    };
                    break;
                case BUNMA_:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl)
                        {
                            if(pl.taskId == 21 && pl.taskIndex == 1)
                            {
                                Service.gI().send_task_next(pl);
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Cảm ơn bạn đã đến đây giúp chúng tôi");
                            }
                            else if(pl.taskId >= 26)
                            {
                                createOtherMenu(pl, 0, "Chào cưng!Chị có thể giúp gì cho cưng", "Cửa hàng");
                            }
                            else
                            {
                                Service.gI().sendPopUpMultiLine(pl, tempId, 0, "Cảm ơn bạn đã đến đây giúp chúng tôi");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                                switch (select) {
                                    case 0://Shop
                                        if(player.taskId >= 26){
                                            Shop.gI().openShop(player, this.tempId);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case BUNMA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player player) {
                            if(player.taskId < 7)
                            {
                                return;
                            }
                            if (player.gender != 0)
                            {
                                Service.gI().sendPopUpMultiLine(player, tempId, 0, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất");         
                            }
                            else
                            {
                                createOtherMenu(player, 0, "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                            }
                        }
                        
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                                switch (select) {
                                    case 0://Shop
                                        Shop.gI().openShop(player, this.tempId);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case DENDE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player player) {
                            if(player.taskId < 7)
                            {
                                return;
                            }
                            if (player.gender != 1)
                            {
                                Service.gI().sendPopUpMultiLine(player, tempId, 0, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc");         
                            }
                            else
                            {
                                createOtherMenu(player, 0, "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                            }
                        }
                        
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                                switch (select) {
                                    case 0://Shop
                                        Shop.gI().openShop(player, this.tempId);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case APPULE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player player) {
                            if(player.taskId < 7)
                            {
                                return;
                            }
                            if (player.gender != 2)
                            {
                                Service.gI().sendPopUpMultiLine(player, tempId, 0, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi");         
                            }
                            else
                            {
                                createOtherMenu(player, 0, "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                            }
                        }
                        
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                                switch (select) {
                                    case 0://Shop
                                        Shop.gI().openShop(player, this.tempId);
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case GIUMA_DAU_BO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            createOtherMenu(pl, 0, "Ngươi có muốn đi tìm mảnh vỡ và mảnh hồn bông tai Porata trong truyền thuyết, ta sẽ đưa ngươi đến đó ?", "Khiêu chiến Boss", "Ok", "Đóng");
                        }
                        
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch(select){
                                case 0:
                                    Service.gI().sendThongBao(player, "Đang trong quá trình xây dựng");
                                    break;
                                case 1:
                                    if (!ChangeMap.gI().listMapCanChange(player).stream().anyMatch(id -> 156 == id)) {
                                        Service.gI().sendPopUpMultiLine(player,6, 0, "Bạn chưa thể đến khu vực này");
                                        return;
                                    }
                                    player.type = 2;
                                    player.maxTime = 5;
                                    Service.gI().Transport(player, 2);
                                    Service.gI().clearMap(player);
                                    break;
                            }
                            
                        }
                    };
                    break;
                case DR_DRIEF:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            if (this.mapId == 84) {
                                this.createOtherMenu(pl, 0, "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                        pl.gender == 0 ? "Đến\nTrái Đất" : pl.gender == 1 ? "Đến\nNamếc" : "Đến\nXayda");
                            }
                            else if(this.mapId == 153){
                                this.createOtherMenu(pl, 0, "Ta có thể giúp gì cho bang hội của bạn?", "Nhiệm vụ\nbang\n[5/5]", "Đảo Kame", "Từ chối");
                            }
                            else
                            {
                                String npcSay = "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?";
                                String[] menuSelect = new String[]{
                                    "Đến\nNamếc",
                                    "Đến\nXayda",
                                    "Siêu thị"
                                };
                                createOtherMenu(pl, 0, npcSay, menuSelect);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (this.mapId == 84) {
                                ChangeMap.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1, ChangeMap.getSpaceShip(player));
                            }
                            else if(this.mapId == 153){
                                switch(select){
                                    case 0:
                                        Service.gI().sendThongBao(player, "Đang trong quá trình xây dựng");
                                        break;
                                    case 1:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 5, -1, -1, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                            else {
                                switch (select) {
                                    case 0:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 25, -1, -1, ChangeMap.getSpaceShip(player));
                                        break;
                                    case 1:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 26, -1, -1, ChangeMap.getSpaceShip(player));

                                        break;
                                    case 2:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 84, -1, -1, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case CARGO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        public void openMenu(Player pl) {
                            String npcSay = "Tàu vũ trụ Namếc tuy cũ nhưng tốc độ không hề kém bất kỳ loại tàu nào khác. Cậu muốn đi đâu?";
                            String[] menuSelect = new String[]{
                                "Đến\nTrái Đất",
                                "Đến\nXayda",
                                "Siêu thị"
                            };
                            createOtherMenu(pl, 0, npcSay, menuSelect);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    ChangeMap.gI().changeMapBySpaceShip(player, 24, -1, -1, ChangeMap.getSpaceShip(player));
                                    break;
                                case 1:
                                    ChangeMap.gI().changeMapBySpaceShip(player, 26, -1, -1, ChangeMap.getSpaceShip(player));
                                    break;
                                case 2:
                                    ChangeMap.gI().changeMapBySpaceShip(player, 84, -1, -1, ChangeMap.getSpaceShip(player));
                                    break;
                            }
                        }
                    };
                    break;
                case CUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.";
                            String[] menuSelect = new String[]{
                                "Đến\nTrái Đất",
                                "Đến\nNamếc",
                                "Siêu thị"
                            };
                            if(mapId == 19)
                            {
                                npcSay = "Ây zô nhóc con muốn đi đâu.";
                                menuSelect = new String[]{
                                    "Đến\nCold",
                                    "Đến\nNappa",
                                    "Từ chối"
                                };
                            }
                            else if(mapId == 68)
                            {
                                npcSay = "Nhóc sợ rồi à. Ta sẽ đưa nhóc về thành phố Vegeta";
                                menuSelect = new String[]{
                                    "Đồng ý",
                                    "Từ chối"
                                };
                            }
                            createOtherMenu(pl, 0, npcSay, menuSelect);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (this.mapId) {
                                case 19:
                                    switch (select) {
                                        case 0:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 109, -1, -1, ChangeMap.getSpaceShip(player));
                                            break;
                                        case 1:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 68, -1, 177, ChangeMap.getSpaceShip(player));
                                            break;
                                    }
                                    break;
                                case 68:
                                    switch (select) {
                                        case 0:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 19, -1, 1100, ChangeMap.getSpaceShip(player));
                                            break;
                                        case 1:
                                            break;
                                    }
                                    break;
                                case 26:
                                    switch (select) {
                                        case 0:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 24, -1, -1, ChangeMap.getSpaceShip(player));
                                            break;
                                        case 1:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 25, -1, -1, ChangeMap.getSpaceShip(player));
                                            break;
                                        case 2:
                                            ChangeMap.gI().changeMapBySpaceShip(player, 84, -1, -1, ChangeMap.getSpaceShip(player));
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    break;
                case TAPION:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            createOtherMenu(pl, 0, "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó", "OK", "Từ chối");
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(select == 0){
                                if(player.zone.map.id == 19){
                                    if(DHVT.gI().Hour != Setting.TIME_START_HIRU_1 && DHVT.gI().Hour != Setting.TIME_START_HIRU_2){
                                        Service.gI().sendThongBao(player, "Hẹn gặp bạn lúc " + Setting.TIME_START_HIRU_1 + "h - " + Setting.TIME_START_HIRU_2 + "h mỗi ngày.");
                                    }
                                    else
                                    {
                                        ChangeMap.gI().changeMap(player, 126, 200, 330);
                                    }
                                }
                                else
                                {
                                    ChangeMap.gI().changeMap(player, 19, -1, -1, -1, player.getUseSpaceShip());
                                }
                            }
                        }
                    };
                    break;
                    case POTAGE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Xin chào, ta có 1 đống đồ xịn cậu có muốn xem không?";
                            String[] menuSelect = new String[]{
                                "Cửa\nhàng"
                            };
                            createOtherMenu(pl, 0, npcSay, menuSelect);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            switch (select) {
                                case 0:
                                    Shop.gI().openShop(player, this.tempId);
                                    break;
                                case 1:
                                    break;
                            }
                        }
                    };
                    break;
                    case SANTA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?";
                            String[] menuSelect = new String[]{
                                "Cửa\nhàng","Đổi Thỏi Vàng","Kích hoạt thành viên"
                            };
                            createOtherMenu(pl, 0, npcSay, menuSelect);
                            
                            
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.getIndexMenu() == 0) {
                            switch (select) {
                                case 0:
                                    Shop.gI().openShop(player, this.tempId);
                                    break;
                                case 1:                                  
                                    createOtherMenu(player, 1, "Con có muốn đổi thỏi vàng ?\n|7| Số Coin hiện có là:  "+ Util.getMoneys(player.session.get_vnd()) , "10 Coin\n50 TV", "20 Coin\n100 TV", "50 Coin\n250 TV", "100 Coin\n500 TV", "200 Coin\n1000 TV");
                                    break;
                                case 2:                                  
                                    createOtherMenu(player, 2, "Gia nhập NroLYLY, con sẽ được nhận thêm 50 TV \n|7| Số Coin hiện có là:  "+ Util.getMoneys(player.session.get_vnd()) , "Kích hoạt thành viên\n20 Coin");
                                    break;
                            }
                        }
                            else if (player.getIndexMenu() == 1) {
                                int x= 5;
                                    switch (select) {
                                        
                                        case 0:
                                        {
                                            int vnd = 10;
                                            if(player.session.get_vnd() < vnd*1000){
                                            Service.gI().sendThongBao(player, "Nạp Coin ủng hộ Sv tại Web Nronight.com, hoặc ib Key Vàng bạn nhé");
                                            return;
                                            }
                                            if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                            }
                                            Item item = ItemData.gI().get_item(457);
                                            item.quantity = vnd *x;
                                            player.inventory.addItemBag(item);
                                            player.inventory.sendItemBags();
                                            player.session.remove_money(vnd*1000);
                                            Service.gI().sendThongBao(player, "Bạn nhận thành công " +item.quantity+" " + item.template.name);
                                            break;
                                        }
                                        case 1:
                                        {
                                            int vnd = 20;
                                            if(player.session.get_vnd() < vnd*1000){
                                            Service.gI().sendThongBao(player, "Nạp Coin ủng hộ Sv tại Web Nronight.com, hoặc ib Key Vàng bạn nhé");
                                            return;
                                            }
                                            if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                            }
                                            Item item = ItemData.gI().get_item(457);
                                            item.quantity = vnd*x ;
                                            player.inventory.addItemBag(item);
                                            player.inventory.sendItemBags();
                                            player.session.remove_money(vnd*1000);
                                            Service.gI().sendThongBao(player, "Bạn nhận thành công " +item.quantity+" " + item.template.name);
                                            break;
                                        }
                                        case 2:
                                        {
                                            int vnd = 50;
                                            if(player.session.get_vnd() < vnd*1000){
                                            Service.gI().sendThongBao(player, "Nạp Coin ủng hộ Sv tại Web Nronight.com, hoặc ib Key Vàng bạn nhé");
                                            return;
                                            }
                                            if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                            }
                                            Item item = ItemData.gI().get_item(457);
                                            item.quantity = vnd*x ;
                                            player.inventory.addItemBag(item);
                                            player.inventory.sendItemBags();
                                            player.session.remove_money(vnd*1000);
                                            Service.gI().sendThongBao(player, "Bạn nhận thành công " +item.quantity+" " + item.template.name);
                                            break;
                                        }
                                        case 3:
                                        {
                                            int vnd = 100;
                                            if(player.session.get_vnd() < vnd*1000){
                                            Service.gI().sendThongBao(player, "Nạp Coin ủng hộ Sv tại Web Nronight.com, hoặc ib Key Vàng bạn nhé");
                                            return;
                                            }
                                            if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                            }
                                            Item item = ItemData.gI().get_item(457);
                                            item.quantity = vnd*x ;
                                            player.inventory.addItemBag(item);
                                            player.inventory.sendItemBags();
                                            player.session.remove_money(vnd*1000);
                                            Service.gI().sendThongBao(player, "Bạn nhận thành công " +item.quantity+" " + item.template.name);
                                            break;
                                        }
                                        case 4:
                                        {
                                            int vnd = 200;
                                            if(player.session.get_vnd() < vnd*1000){
                                            Service.gI().sendThongBao(player, "Nạp Coin ủng hộ Sv tại Web Nronight.com, hoặc ib Key Vàng bạn nhé");
                                            return;
                                            }
                                            if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                            }
                                            
                                            Item item = ItemData.gI().get_item(457);
                                            item.quantity = vnd*x ;
                                            player.inventory.addItemBag(item);
                                            player.inventory.sendItemBags();
                                            player.session.remove_money(vnd*1000);
                                            Service.gI().sendThongBao(player, "Bạn nhận thành công " +item.quantity+" " + item.template.name);
                                            break;
                                        }
                                    }
                                }
                                        else if (player.getIndexMenu() == 2) {
                                            int vnd = 20;
                                            if(player.session.get_vnd() < vnd*1000){
                                            Service.gI().sendThongBao(player, "Nạp Coin ủng hộ Sv tại Web Nronight.com, hoặc ib Key Vàng bạn nhé");
                                            return;
                                            }
                                            if (player.session.get_act() == 1){
                                            Service.gI().sendThongBao(player, "Con đã là thành viên chính thức rồi!");
                                            return;
                                            }
                                            if(player.getBagNull() < 0){
                                            Service.gI().sendThongBao(player, "Bạn không đủ ô hành trang để nhận");
                                            return;
                                            }
                                            Item item = ItemData.gI().get_item(457);
                                            item.quantity = 50;
                                            player.inventory.addItemBag(item);                                         
                                            player.inventory.sendItemBags();
                                            player.session.remove_money(vnd*1000);
                                            player.session.update_active();
                                            Service.gI().sendThongBao(player, "Chào mừng con đến với Nro LyLy");
                                            
                            }
                        }
                    };
                    break;
                case URON:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            Shop.gI().openShop(pl, this.tempId);
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                        }
                    };
                    break;
                case TRONG_TAI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl)
                        {
                            createOtherMenu(pl, 0, "Đại chiến bang hội toàn vũ trụ đang diễn ra ngươi có muôn tham gia?", "OK", "Từ chối");
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if(select == 0){
                                Service.gI().sendPopUpMultiLine(player, tempId, avartar, "Bây giờ chưa phải lúc.");
                            }
                        }
                    };
                    break;
                case BA_HAT_MIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openMenu(Player pl) {
                            String npcSay = "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.";
                            String[] menuSelect = new String[]{
                                "Đến\nTrái Đất",
                                "Đến\nNamếc",
                                "Siêu thị"
                            };
                            if(mapId == 5){
                                npcSay = "Ngươi tìm ta có việc gì ?";
                                menuSelect = new String[]{
                                    "Ép sao\ntrang bị",
                                    "Pha lê\nhóa\ntrang bị",
                                    "Chuyển hóa\ntrang bị",
                                    "Nâng cấp\ntrang bị"
                                };
                            }
                            else if(mapId == 42 || mapId == 43 || mapId == 44 || mapId == 84){
                                npcSay = "Ngươi tìm ta có việc gì ?";
                                Item btc1 = pl.inventory.findItemBagByTemp(454);
                                Item btc2 = pl.inventory.findItemBagByTemp(921);
                                if(btc1 != null && btc2 == null){
                                    menuSelect = new String[]{
                                        "Cửa hàng\nBùa",
                                        "Nâng cấp\nVật phẩm",
                                        "Nâng cấp\nBông tai\nPorata",
                                        "Nhập\nNgọc Rồng"
                                    };
                                }
                                else if(btc1 == null && btc2 != null){
                                    menuSelect = new String[]{
                                        "Cửa hàng\nBùa",
                                        "Nâng cấp\nVật phẩm",
                                        "Mở chỉ số\nBông tai\nPorata",
                                        "Nhập\nNgọc Rồng"
                                    };
                                }
                                else if(btc1 == null && btc2 == null){
                                    menuSelect = new String[]{
                                        "Cửa hàng\nBùa",
                                        "Nâng cấp\nVật phẩm",
                                        "Nhập\nNgọc Rồng"
                                    };
                                }
                            }
                            createOtherMenu(pl, 0, npcSay, menuSelect);
                        }
                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (this.mapId == 5) {
                                if (player.getIndexMenu() == 0) {
                                    switch (select) {
                                        case 0:
                                            player.combine.openTabCombine(Combine.EP_SAO_TRANG_BI);
                                            break;
                                        case 1:
                                            createOtherMenu(player, 1, "Ngươi muốn pha lê hóa trang bị bằng cách nào?", "Bằng ngọc", "Từ chối");
                                            break;
                                        case 2:
                                            player.combine.openTabCombine(Combine.CHUYEN_HOA_TRANG_BI);
                                            break;
                                        case 3:
                                            player.combine.openTabCombine(Combine.NANG_CAP_TRANG_BI_SKH);
                                            break;
//                                        case 3:
//                                            ChangeMap.gI().changeMap(player, 112, -1, 50, 408, ChangeMap.NON_SPACE_SHIP);
//                                            break;
                                    }
                                } else if (player.getIndexMenu() == 1) {
                                    switch (select) {
                                        case 0:
                                            player.combine.openTabCombine(Combine.PHA_LE_HOA_TRANG_BI);
                                            break;
                                        case 1:
                                            break;
                                    }
                                }
                            }
                            else if (this.mapId == 112) {
                                switch (select) {
                                    case 3:
                                        ChangeMap.gI().changeMapBySpaceShip(player, 5, -1, 1156, ChangeMap.getSpaceShip(player));
                                        break;
                                }
                            }
                            else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44 || this.mapId == 84) {
                                if (player.getIndexMenu() == 0) {
                                    Item btc1 = player.inventory.findItemBagByTemp(454);
                                    Item btc2 = player.inventory.findItemBagByTemp(921);
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, 1, "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để mạnh mẽ à, mua không ta bán cho xài rồi lại thích cho mà xem.", "Bùa\ndùng\n1 giờ", "Bùa\ndùng\n8 giờ", "Bùa\ndùng\n1 tháng");
                                            break;
                                        case 1:
                                            player.combine.openTabCombine(Combine.NANG_CAP_TRANG_BI);
                                            break;
                                        case 2:
                                            if(btc1 != null && btc2 == null)
                                            {
                                                player.combine.openTabCombine(Combine.NANG_CAP_BONG_TAI);
                                                break;
                                            }
                                            if(btc1 == null && btc2 != null)
                                            {
                                                player.combine.openTabCombine(Combine.NANG_CHI_SO_BONG_TAI);
                                                break;
                                            }
//                                        case 3:
//                                            if(btc1 == null && btc2 != null)
//                                            {
//                                                player.combine.openTabCombine(Combine.NANG_CHI_SO_BONG_TAI);
//                                                break;
//                                            }
                                        case 3:
                                            player.combine.openTabCombine(Combine.NHAP_NGOC_RONG);
                                            break;
                                        case 4:
                                            player.combine.openTabCombine(Combine.NHAP_NGOC_RONG);
                                            break;
                                    }
                                }
                                else if (player.getIndexMenu() == 1) {
                                    switch (select) {
                                        case 0:
                                            typeBua = 0;
                                            player.tabBua = 1;
                                            Shop.gI().openShop(player);
                                            break;
                                        case 1:
                                            typeBua = 1;
                                            player.tabBua = 4;
                                            Shop.gI().openShop(player);
                                            break;
                                        case 2:
                                            typeBua = 2;
                                            player.tabBua = 100;
                                            Shop.gI().openShop(player);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                default:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void confirmMenu(Player player, int select) {
                            //Shop.gI().openShop(player, this.tempId);
                        }
                    };
            }
        }
        catch (Exception e) {
            Util.debug("createNpc");
            e.printStackTrace();
        }
    }

    public static void createNpcRongThieng() {
        try{
        Npc npc = new Npc(-1, -1, -1, -1, RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                //System.out.println("= = =menu: " + player.getIndexMenu() + " select: " + select);
                switch (player.getIndexMenu()) {
                    case IGNORE_MENU:
                        break;
                    case SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case SHENRON_1_1:
                        if (player.getIndexMenu() == SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            Npc.createMenuRongThieng(player, NpcFactory.SHENRON_1_2, SummonDragon.gI().GET_SHENRON_SAY(player), SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case SHENRON_1_2:
                        if (player.getIndexMenu() == SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            Npc.createMenuRongThieng(player, NpcFactory.SHENRON_1_1, SummonDragon.gI().GET_SHENRON_SAY(player), SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    case SHENRON_2:
                    case SHENRON_3:
                        SummonDragon.gI().showConfirmShenron(player, player.getIndexMenu(), (byte) select);
                        break;
                    case SHENRON_CONFIRM_BANG:
                        if (select == 0) {
                            SummonDragon.gI().confirmWishBang(player);
                        } else if (select == 1) {
                            SummonDragon.gI().sendWhishesShenronBang(player);
                        }
                        break;
                    case SHENRON_BANG:
                        SummonDragon.gI().showConfirmShenronBang(player, player.getIndexMenu(), (byte)select);
                        break;
                    
                       
                    default:
                        break;
                }
            }
        };
        } catch (Exception e) {
            Util.debug("createNpcRongThieng");
            e.printStackTrace();
        }
    }

    public static void createNpcConMeo() {
        try{
        Npc npc = new Npc(-1, -1, -1, -1, CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                Transaction tran = Transaction.gI().findTran(player);
                if(tran != null){
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                player.setSelectMenu((short)select);
                switch (player.getIndexMenu()) {
                    case IGNORE_MENU:
                        break;
                    case NGOC_RONG_NAMEC:
                        break;
                    case OPEN_SPECIAL_NORMAL:
                        if (player.point.getPower() < 10000000000L) {
                            Service.gI().sendThongBao(player, "Cần 10 tỉ sức mạnh để mở");
                            break;
                        }
                        if (select == 0) {
                            long g = SpeacialSkill.gI().get_gold_open(player);
                            if (player.inventory.gold >= g) {
                                player.inventory.gold -= g;
                                Service.gI().sendMoney(player);
                                player.speacial = SpeacialSkill.gI().SpeacialSkill(player);
                                player.speacial[2]++;
                                Service.gI().send_special_skill(player);
                                String[] peacialSkill = SpeacialSkill.gI().getSpeacialSkill(player);
                                Service.gI().sendThongBao(player, peacialSkill[1]);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.powerToString(g - player.inventory.gold) + " nữa!");
                            }
                        }
                        break;
                    case OPEN_SPECIAL_VIP:
                        if (player.point.getPower() < 10000000000L) {
                            Service.gI().sendThongBao(player, "Cần 10 tỉ sức mạnh để mở");
                            break;
                        }
                        if (select == 0) {
                            int g = Setting.GEM_OPEN_SPEACIAL;
                            if (player.inventory.getGemAndRuby() >= g) {
                                player.inventory.subGemAndRuby(g);
                                Service.gI().sendMoney(player);
                                player.speacial = SpeacialSkill.gI().SpeacialSkill(player);
                                player.speacial[2] = 0;
                                Service.gI().send_special_skill(player);
                                String[] peacialSkill = SpeacialSkill.gI().getSpeacialSkill(player);
                                Service.gI().sendThongBao(player, peacialSkill[1]);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu " + Util.powerToString(g - player.inventory.getGemAndRuby()) + " nữa!");
                            }
                        }
                        break;
                    case SPECIAL_SKILL:
                        switch (select) {
                            case 0:
                                Service.gI().send_all_special_skill(player);
                                break;
                            case 1:
                                Npc.createMenuConMeo(player, NpcFactory.OPEN_SPECIAL_NORMAL, -1, "Bạn có muốn mở nội tại\nVới giá " + Util.powerToString(SpeacialSkill.gI().get_gold_open(player)).replace(" ", "") + " vàng ?", "Mở\nNội Tại", "Từ Chối");
                                break;
                            case 2:
                                Npc.createMenuConMeo(player, NpcFactory.OPEN_SPECIAL_VIP, -1, "Bạn có muốn mở nội tại\nVới giá  " + Setting.GEM_OPEN_SPEACIAL + " ngọc và\ntái lập giá vàng quay lại ban đầu\nkhông?", "Mở VIP", "Từ Chối");
                                break;
                            default:
                                break;
                        }
                        break;
                    case START_COMBINE:
                        if (select == 0) {
                            player.combine.startCombine();
                        }
                        else if(select == 1 && player.combine.getTypeCombine() == Combine.NANG_CAP_TRANG_BI && player.combine.useDBV(player)){
                            
                            player.combine.startCombine();
                        }
                        break;
                    case MAKE_MATCH_PVP:
                        PVP.gI().sendInvitePVP(player, (byte) select);
                        break;
                    case MAKE_FRIEND:
                        if (select == 0) {
                            player.listPlayer.acceptMakeFriend();
                        }
                        break;
                    case REVENGE:
                        PVP.gI().acceptRevenge(player);
                        break;
                    case TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            Npc.createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case SUMMON_SHENRON:
                        if (select == 0) {
                            Npc.createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case SUMMON_SHENRON_BANG:
                        if (select == 0) {
                            SummonDragon.gI().summonShenronBang(player);
                        }
                        break;
                    case RUONG_BI_AN_CONFIRM:
                        if (select == 0) {
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        }
                        break;
                    case RUONG_BI_AN:
                        if (select == 0) {
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        } else if (select == 1) {
                            RuongBiAn.gI().openMenuRuong_1(player);
                        }
                        break; 
                    case RUONG_BI_AN_2:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_2);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                            
                        }
                        break;  
                    case RUONG_BI_AN_3:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_3);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                            
                        }
                        break;
                    case RUONG_BI_AN_4:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_4);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        }
                        break; 
                    case RUONG_BI_AN_5:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_5);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        }
                        break;  
                    case RUONG_BI_AN_6:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_6);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                            
                        }
                        break;
                    case RUONG_BI_AN_7:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_7);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        }
                        break; 
                    case RUONG_BI_AN_8:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_8);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        }
                        break;  
                    case RUONG_BI_AN_9:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_9);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                            
                        }
                        break;
                    case RUONG_BI_AN_10:
                        if (select == 0) {
                            player.setIndexMenu(RUONG_BI_AN_10);
                            RuongBiAn.gI().confirmRuongBiAn(player);
                        }
                        break;  
                        
                    case KHI_GAS_HUY_DIET:
                        if(player.clan.members.stream().anyMatch(m -> m.id == player.id && m.role == 0) && player.clan.KhiGasHuyDiet == null){
                            int zone = ClanManager.gI().getZone_KhiGas();
                            if (select == 0) {
                                if(zone != -1){
                                    int level = Integer.parseInt(player.input[0]);
                                    player.clan.KhiGasHuyDiet = new PhuBan(player.clan.id, 67, 1800, System.currentTimeMillis(), Util.TimePhuBan(), zone, level / 10, level);
                                    for(int i = 147; i <= 152; i++){
                                        Zone map = MapManager.gI().getMap(i, zone);
                                        player.clan.KhiGasHuyDiet.setHPMob_KhiGas(map);
                                        map.zoneDoneBOSS_1 = false;
                                        map.zoneDoneBOSS_2 = false;
                                        map.zoneDoneKGHD = false;
                                    }
                                    player.type = 3;
                                    player.maxTime = 5;
                                    Service.gI().Transport(player, 1);
                                    Service.gI().clearMap(player);
                                }
                                else
                                {
                                    Service.gI().sendThongBao(player, "Khí gas hủy diệt đang quá tải vui lòng thử lại sau!");
                                }
                            }
                        }
                        break;
                    case KHO_BAU_DUOI_BIEN:
                        if(player.clan != null && player.clan.KhoBauDuoiBien == null){
                            int zone = ClanManager.gI().getZone_KHOBAU();
                            if (select == 0) {
                                if(zone != -1){
                                    int level = Integer.parseInt(player.input[0]);
                                    player.clan.KhoBauDuoiBien = new PhuBan(player.clan.id, 13, 1800, System.currentTimeMillis(), Util.TimePhuBan(), zone, level / 10, level);
                                    for(int i = 135; i <= 138; i++){
                                        Zone map = MapManager.gI().getMap(i, zone);
                                        player.clan.KhoBauDuoiBien.setHPMob_KhoBau(map);
                                    }
                                    int HPmax = 15000000 * player.clan.KhoBauDuoiBien.level;
                                    Item item = ItemData.gI().get_item(77);
                                    BossManager.addBoss_BDKB("Trung Úy Xanh Lơ", 135, 136, 137, 137, zone, item, HPmax, HPmax / 100);
                                    player.type = 5;
                                    player.maxTime = 10;
                                    Service.gI().Transport(player, 1);
                                    Service.gI().clearMap(player);
                                }
                                else{
                                    Service.gI().sendThongBao(player, "Kho báu dưới biển đang quá tải vui lòng thử lại sau!");
                                }
                            }
                        }
                        break;
                }
            }
        };
        } catch (Exception e) {
            Util.debug("createNpcConMeo");
            e.printStackTrace();
        }
    }

    public static void createMenuMagicTree(Player player, int indexMenu, String... menuSelect) {
        Message msg = null;
        try {
            msg = new Message(-34);
            msg.writer().writeByte(1);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            player.session.sendMessage(msg);

        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }
    
    public static Player getBoss(String name , int[] mapid){
        try{
            for(int i = 0 ;i<mapid.length ;i++){
                Map map = MapManager.gI().getMapById(mapid[i]);
                for(Zone zn : map.map_zone){
                    if(zn != null){
                        for(Player pl : zn.players){
                            if(pl.isBoss &&!pl.isDie()&& pl.name.contains(name)){
                                return pl;
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
        }
        return null;
    }
    
    public static void addBoss(Player pl){
        if(MapManager.gI().getMap(59, pl.clan.DoanhTrai.zonePhuBan).players.stream().filter(p -> p.isBoss).count() <= 0){
            int hpPlus = 50000 +  pl.clan.DoanhTrai.hpMobPlus + 50000;
            BossManager.addBossDT("Trung Úy Trắng", 141, 142, 143, 59, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus , hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
        }
        if(MapManager.gI().getMap(62, pl.clan.DoanhTrai.zonePhuBan).players.stream().filter(p -> p.isBoss).count() <= 0){
            int hpPlus = 80000 +  pl.clan.DoanhTrai.hpMobPlus + 80000;
            BossManager.addBossDT("Trung Úy Xanh Lơ", 135, 136, 137, 62, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus , hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
        }
        if(MapManager.gI().getMap(55, pl.clan.DoanhTrai.zonePhuBan).players.stream().filter(p -> p.isBoss).count() <= 0){
            int hpPlus = 100000 +  pl.clan.DoanhTrai.hpMobPlus + 100000;
            BossManager.addBossDT("Trung Úy Thép", 129, 130, 131, 55, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus , hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
        }
        if(MapManager.gI().getMap(54, pl.clan.DoanhTrai.zonePhuBan).players.stream().filter(p -> p.isBoss).count() <= 0){
            int hpPlus = 100000 +  pl.clan.DoanhTrai.hpMobPlus + 100000;
            Item item = ItemData.gI().get_item(650);
            item.itemOptions.add(new ItemOption(108, 70));
            BossManager.addBossDT("Ninja Áo Tím", 123, 124, 125, 54, pl.clan.DoanhTrai.zonePhuBan, item, hpPlus < 0 ? -hpPlus : hpPlus / 2, hpPlus < 0 ? -hpPlus : hpPlus  * 5 / 100);
        }
        if(MapManager.gI().getMap(54, pl.clan.DoanhTrai.zonePhuBan).players.stream().filter(p -> p.isBoss).count() <= 0){
            int hpPlus = 200000 +  pl.clan.DoanhTrai.hpMobPlus + 200000;
            BossManager.addBossDT("Robot Vệ Sĩ 1", 138, 139, 140, 57, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus, hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
            BossManager.addBossDT("Robot Vệ Sĩ 2", 138, 139, 140, 57, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus, hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
            BossManager.addBossDT("Robot Vệ Sĩ 3", 138, 139, 140, 57, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus, hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
            BossManager.addBossDT("Robot Vệ Sĩ 4", 138, 139, 140, 57, pl.clan.DoanhTrai.zonePhuBan, null, hpPlus < 0 ? -hpPlus : hpPlus, hpPlus < 0 ? -hpPlus : hpPlus  * 10 / 100);
        }
    }
}
