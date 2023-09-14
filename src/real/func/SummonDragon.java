package real.func;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.MapManager;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Inventory;
import real.player.Player;
import real.skill.SkillUtil;
import server.MenuController;
import server.Service;
import server.Util;
import server.io.Message;
import service.Setting;
import service.data.Init;

public class SummonDragon {

    public static final byte WISHED = 0;
    public static final byte TIME_UP = 1;
    
    private static long LastTimeSummon;
    private static long LastTimeSummonBang;

    public static final byte DRAGON_SHENRON = 0;
    public static final byte DRAGON_PORUNGA = 1;

    public static final short NGOC_RONG_1_SAO = 14;
    public static final short NGOC_RONG_2_SAO = 15;
    public static final short NGOC_RONG_3_SAO = 16;
    public static final short NGOC_RONG_4_SAO = 17;
    public static final short NGOC_RONG_5_SAO = 18;
    public static final short NGOC_RONG_6_SAO = 19;
    public static final short NGOC_RONG_7_SAO = 20;
    
    public static final short RONG_BANG_1_SAO = 925;
    public static final short RONG_BANG_2_SAO = 926;
    public static final short RONG_BANG_3_SAO = 927;
    public static final short RONG_BANG_4_SAO = 928;
    public static final short RONG_BANG_5_SAO = 929;
    public static final short RONG_BANG_6_SAO = 930;
    public static final short RONG_BANG_7_SAO = 931;

    public static final String SUMMON_SHENRON_TUTORIAL = null
            + "Có 3 cách gọi rồng thần. Gọi từ ngọc 1 sao, gọi từ ngọc 2 sao, hoặc gọi từ ngọc 3 sao\n"
            + "Các ngọc 4 sao đến 7 sao không thể gọi rồng thần được\n"
            + "Để gọi rồng 1 sao cần ngọc từ 1 sao đến 7 sao\n"
            + "Để gọi rồng 2 sao cần ngọc từ 2 sao đến 7 sao\n"
            + "Để gọi rồng 3 sao cần ngọc từ 3 sao đến 7sao\n"
            + "Điều ước rồng 3 sao: Capsule 3 sao, hoặc 2 triệu sức mạnh, hoặc 200k vàng\n"
            + "Điều ước rồng 2 sao: Capsule 2 sao, hoặc 20 triệu sức mạnh, hoặc 2 triệu vàng\n"
            + "Điều ước rồng 1 sao: Capsule 1 sao, hoặc 200 triệu sức mạnh, hoặc 20 triệu vàng, hoặc đẹp trai, hoặc....\n"
            + "Ngọc rồng sẽ mất ngay khi gọi rồng dù bạn có ước hay không\n"
            + "Quá 5 phút nếu không ước rồng thần sẽ bay mất";
    public static final String SHENRON_SAY
            = "Ta sẽ ban cho người 1 điều ước, ngươi có #, hãy suy nghĩ thật kỹ trước khi quyết định";

    public static final String[] SHENRON_BANG
            = new String[]{"Thay\nChiêu 3-4\nĐệ tử", "Tăng 20%\nSức đánh\n90 Phút", "Tăng 20%\nHP,MP\n90 Phút", "Cải trang\nVidel\n90 Ngày"};
    
    public static final String[] SHENRON_1_STAR_WISHES_1
            = new String[]{"Giàu có\n+20 Tr\nVàng", "Găng tay\nđang mang\nlên 1 cấp", "Chí mạng\nGốc +2%",
                "Thay\nChiêu 2-3\nĐệ tử", "Điều ước\nkhác"};
    public static final String[] SHENRON_1_STAR_WISHES_2
            = new String[]{"Đẹp trai\nnhất\nVũ trụ", "Giàu có\n+1,5 k\nNgọc", "+200 Tr\nSức mạnh\nvà tiềm\nnăng",
                "Điều ước\nkhác"};
    public static final String[] SHENRON_2_STARS_WHISHES
            = new String[]{"Giàu có\n+150\nNgọc", "+20 Tr\nSức mạnh\nvà tiềm năng", "Giàu có\n+2 Tr\nVàng"};
    public static final String[] SHENRON_3_STARS_WHISHES
            = new String[]{"Giàu có\n+15\nNgọc", "+2 Tr\nSức mạnh\nvà tiềm năng", "Giàu có\n+200 k\nVàng"};
    //--------------------------------------------------------------------------
    private static SummonDragon instance;
    private final Map pl_dragonStar;
    private long lastTimeShenronAppeared;
    private long lastTimeShenronWait;
    private boolean isShenronAppear;

    private final Thread update;
    private boolean active;

    private Player playerSummonShenron;
    private int menuShenron;
    private byte select;

    private SummonDragon() {
        this.pl_dragonStar = new HashMap<>();
        this.update = new Thread(() -> {
            while (active) {
                try {
                    if (isShenronAppear) {
                        if (Util.canDoWithTime(lastTimeShenronWait, Setting.TIME_DRAGON_WAIT)) {
                            shenronLeave(playerSummonShenron, TIME_UP);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        });
        this.active();
    }

    private void active() {
        if (!active) {
            active = true;
            this.update.start();
        }
    }

    public static SummonDragon gI() {
        if (instance == null) {
            instance = new SummonDragon();
        }
        return instance;
    }
    
    /// ------------------------------------------------------Rồng Thần BĂNG----------------------------------------------------------------\\\
    public void openMenuShenronBang(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.SUMMON_SHENRON_BANG, -1, "Bạn muốn gọi rồng băng ?",
                "Gọi\nrồng băng", "Từ chối");
    }
    
    public synchronized void summonShenronBang(Player pl) {
        if(playerSummonShenron != null){
            Service.gI().sendThongBao(pl, "Đang có rồng thần xuất hiện");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (pl.zone.map.id != 0 && pl.zone.map.id != 7 && pl.zone.map.id != 14 && !MapManager.gI().isMapOffline(pl.zone.map.id)) {
            if (checkShenronBangBall(pl)) {
                if (pl.role == Setting.ROLE_ADMIN || Util.canDoWithTime(pl.LastTimeSummon, Setting.TIME_RECALL_DRAGON_BANG)) {
                    pl.LastTimeSummon = timestamp.getTime();
                    for (int i = RONG_BANG_1_SAO; i <= RONG_BANG_7_SAO; i++) {
                        pl.inventory.subQuantityItemsBag(pl.inventory.findItemBagByTemp(i), 1);
                        pl.inventory.sendItemBags();
                        pl.inventory.sortItemBag();
                    }
                    activeShenron(pl, true, true);
                    sendWhishesShenronBang(pl);
                } else {
                    int timeLeft = (int) ((Setting.TIME_RECALL_DRAGON_BANG - (System.currentTimeMillis() - pl.LastTimeSummon)) / 1000);
                    Service.gI().sendThongBao(pl, "Vui lòng đợi " + (timeLeft < 60 ? timeLeft + " giây" : timeLeft / 60 + " phút") + " nữa");
                }
            }
        } else {
            Service.gI().sendThongBao(pl, "Không thể gọi rồng ở đây");
        }
    }
    
    private boolean checkShenronBangBall(Player pl) {
        if (!pl.inventory.existItemBag(RONG_BANG_1_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 1 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(RONG_BANG_2_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 2 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(RONG_BANG_3_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 3 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(RONG_BANG_4_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 4 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(RONG_BANG_5_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 5 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(RONG_BANG_6_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 6 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(RONG_BANG_7_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng băng 7 sao");
            return false;
        }
        return true;
    }
    
    public void sendWhishesShenronBang(Player pl) {
        Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_BANG, GET_SHENRON_SAY(pl), SHENRON_BANG);
    }
    
    public void confirmWishBang(Player pl) {
        switch (this.menuShenron) {
            case NpcFactory.SHENRON_BANG:
                switch (this.select) {
                    case 0: // Đổi Skill 3-4
                        if (pl.pet != null) {
                            try {
                                if (pl.pet.playerSkill.skills.size() > 2) {
                                    pl.pet.playerSkill.skills.set(2, SkillUtil.createSkill(Init.SKILL_3_PET[Util.nextInt(0, 2)], 1,0));
                                }
                                if (pl.pet.playerSkill.skills.size() > 3) {
                                    pl.pet.playerSkill.skills.set(3, SkillUtil.createSkill(Init.SKILL_4_PET[Util.nextInt(0, 2)], 1,0));
                                }
                            } catch (Exception r) {
                            }
                        }
                        break;
                    case 1: // 20% Sức đánh 90 phút
                        if (!pl.itemTime.ExitsItemTiem(ItemData.gI().getIcon((short)927))) {
                            pl.itemTime.addItemTime(ItemData.gI().getIcon((short)927), 5400);
                            Service.gI().point(pl);
                        }
                        break;
                    case 2: // 20% HP,MP 90 phút
                        if (!pl.itemTime.ExitsItemTiem(ItemData.gI().getIcon((short)931))) {
                            pl.itemTime.addItemTime(ItemData.gI().getIcon((short)931), 5400);
                            Service.gI().point(pl);
                        }
                        break;
                    case 3: // Nhận cải trang videl
                        Item nonNoel = ItemData.gI().get_item(827);
                        nonNoel.itemOptions.clear();
                        nonNoel.itemOptions.add(new ItemOption(50, 35));
                        nonNoel.itemOptions.add(new ItemOption(77, 35));
                        nonNoel.itemOptions.add(new ItemOption(103, 35));
                        nonNoel.itemOptions.add(new ItemOption(80, 50));
                        nonNoel.itemOptions.add(new ItemOption(106, 0));
                        nonNoel.itemOptions.add(new ItemOption(93, 90));
                        Timestamp timenow = new Timestamp(System.currentTimeMillis());
                        nonNoel.buyTime = timenow.getTime();
                        pl.inventory.addItemBag(nonNoel);
                        Service.gI().sendThongBao(pl, "Bạn đã nhận được " + nonNoel.template.name);
                        pl.inventory.sendItemBags();
                        pl.inventory.sortItem(pl.inventory.itemsBag);
                        break;
                    default:
                        break;
                }
                break;
        }
        shenronLeave(pl, WISHED);
    }

    public void showConfirmShenronBang(Player pl, int menu, byte select) {
        this.menuShenron = menu;
        this.select = select;
        String wish = SHENRON_BANG[select];
        Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_CONFIRM_BANG, "Ngươi có chắc muốn ước?", wish, "Từ chối");
    }
    
    /// ------------------------------------------------------Rồng Thần trái đất----------------------------------------------------------------\\\
    
    public void openMenuSummonShenron(Player pl, byte dragonBallStar) {
        this.pl_dragonStar.put(pl, dragonBallStar);
        Npc.createMenuConMeo(pl, NpcFactory.SUMMON_SHENRON, -1, "Bạn muốn gọi rồng thần ?",
                "Hướng\ndẫn thêm\n(mới)", "Gọi\nRồng Thần\n" + dragonBallStar + " Sao");
    }

    public synchronized void summonShenron(Player pl) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (pl.zone.map.id == 0 || pl.zone.map.id == 7 || pl.zone.map.id == 14) {
            if (checkShenronBall(pl)) {
                if (pl.role == Setting.ROLE_ADMIN || Util.canDoWithTime(lastTimeShenronAppeared, Setting.TIME_RECALL_DRAGON)) {
                    if (pl.role != Setting.ROLE_ADMIN && pl.countSummon >= Setting.COUNT_SUMMON_DRAGON) {
                        Service.gI().sendThongBao(pl, "Bạn đã hết lượt gọi rồng trong ngày");
                        return;
                    }
                    pl.countSummon++;
                    playerSummonShenron = pl;
                    playerSummonShenron.LastTimeSummon = timestamp.getTime();
                    for (int i = NGOC_RONG_1_SAO; i <= NGOC_RONG_7_SAO; i++) {
                        pl.inventory.subQuantityItemsBag(pl.inventory.findItemBagByTemp(i), 1);
                        pl.inventory.sendItemBags();
                        pl.inventory.sortItemBag();
                    }
                    activeShenron(pl, true);
                    sendWhishesShenron(pl);
                    lastTimeShenronAppeared = System.currentTimeMillis();
                } else {
                    int timeLeft = (int) ((Setting.TIME_RECALL_DRAGON - (System.currentTimeMillis() - lastTimeShenronAppeared)) / 1000);
                    Service.gI().sendThongBao(pl, "Vui lòng đợi " + (timeLeft < 60 ? timeLeft + " giây" : timeLeft / 60 + " phút") + " nữa");
                }
            }
        } else {
            Service.gI().sendThongBao(pl, "Chỉ được gọi rồng thần ở ngôi làng trước nhà");
        }
    }

    public void sendWhishesShenron(Player pl) {
        if(pl != playerSummonShenron){
            return;
        }
        byte dragonStar = (byte)pl_dragonStar.get(pl);
        switch (dragonStar) {
            case 1:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_1_1, GET_SHENRON_SAY(pl), SHENRON_1_STAR_WISHES_1);
                break;
            case 2:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_2, GET_SHENRON_SAY(pl), SHENRON_2_STARS_WHISHES);
                break;
            case 3:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_3, GET_SHENRON_SAY(pl), SHENRON_3_STARS_WHISHES);
                break;
        }
    }

    private boolean checkShenronBall(Player pl) {
        byte dragonStar = (byte) this.pl_dragonStar.get(pl);
        if (dragonStar == 1) {
            if (!pl.inventory.existItemBag(NGOC_RONG_2_SAO)) {
                Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 2 sao");
                return false;
            }
            if (!pl.inventory.existItemBag(NGOC_RONG_3_SAO)) {
                Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 3 sao");
                return false;
            }
        } else if (dragonStar == 2) {
            if (!pl.inventory.existItemBag(NGOC_RONG_3_SAO)) {
                Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 3 sao");
                return false;
            }
        }
        if (!pl.inventory.existItemBag(NGOC_RONG_4_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 4 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(NGOC_RONG_5_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 5 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(NGOC_RONG_6_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 6 sao");
            return false;
        }
        if (!pl.inventory.existItemBag(NGOC_RONG_7_SAO)) {
            Service.gI().sendThongBao(pl, "Bạn còn thiếu 1 viên ngọc rồng 7 sao");
            return false;
        }
        return true;
    }

    public void confirmWish() {
        if (this.playerSummonShenron == null) {
            return;
        }
        switch (this.menuShenron) {
            case NpcFactory.SHENRON_1_1:
                switch (this.select) {
                    case 0: //20 tr vàng
                        this.playerSummonShenron.inventory.gold += 20000000;
                        if (this.playerSummonShenron.inventory.gold > playerSummonShenron.inventory.LIMIT_GOLD) {
                            this.playerSummonShenron.inventory.gold = playerSummonShenron.inventory.LIMIT_GOLD;
                        }
                        this.playerSummonShenron.sendInfo();
                        break;
                    case 1: //găng tay đang đeo lên 1 cấp
                        Item item = playerSummonShenron.inventory.itemsBody.get(2);
                        if (item != null) {
                            boolean isHaveLevel = false;
                            int level = 0;
                            for (ItemOption itemOption : item.itemOptions) {
                                if (itemOption.optionTemplate.id == 72) {
                                    level += itemOption.param;
                                    break;
                                }
                            }
                            if (level < 7) {
                                for (ItemOption itemOption : item.itemOptions) {
                                    if (itemOption.optionTemplate.id == 72) {
                                        itemOption.param++;
                                        isHaveLevel = true;
                                        break;
                                    }
                                }
                                if (!isHaveLevel) {
                                    item.itemOptions.add(new ItemOption(72, (short) 1));
                                }
                                for (ItemOption io : item.itemOptions) {
                                    if (playerSummonShenron.combine.OptionCanUpgrade(io)) {
                                        playerSummonShenron.combine.addParam(item, io.optionTemplate.id, playerSummonShenron.combine.paramNextLevel(item, io));
                                    }
                                }
                            }
                            else{
                                Service.gI().sendThongBao(playerSummonShenron, "Chỉ ước được tối đa +7");
                                sendWhishesShenron(playerSummonShenron);
                                return;
                            }
                        }
                        else{
                            Service.gI().sendThongBao(playerSummonShenron, "Không tìm thấy gang tay");
                            sendWhishesShenron(playerSummonShenron);
                            return;
                        }
                        playerSummonShenron.inventory.sendItemBody();
                        Service.gI().Send_Caitrang(playerSummonShenron);
                        break;
                    case 2: //chí mạng +2%
                        if(playerSummonShenron.point.critSum < 20){
                            this.playerSummonShenron.point.critSum += 2;
                            Service.gI().point(playerSummonShenron);
                        }
                        else{
                            Service.gI().sendThongBao(playerSummonShenron, "Chí mạng đã đạt tối đa 10 lần ước");
                            sendWhishesShenron(playerSummonShenron);
                            return;
                        }
                        break;
                    case 3: //thay chiêu 2-3 đệ tử
                        if (playerSummonShenron.pet != null) {
                            if (playerSummonShenron.pet.playerSkill.skills.size() > 2)
                            {
                                playerSummonShenron.pet.playerSkill.skills.set(1, playerSummonShenron.pet.openSkill2(1));
                                playerSummonShenron.pet.playerSkill.skills.set(2, playerSummonShenron.pet.openSkill3(1));
                            }
                            else
                            {
                                Service.gI().sendThongBao(playerSummonShenron, "Đệ thử phải có skill 2 và 3 trước");
                                sendWhishesShenron(playerSummonShenron);
                                return;
                            }
                        }
                        else
                        {
                            Service.gI().sendThongBao(playerSummonShenron, "Bạn phải có đệ tử trước");
                            sendWhishesShenron(playerSummonShenron);
                            return;
                        }
                        break;
                }
                break;
            case NpcFactory.SHENRON_1_2:
                switch (this.select) {
                    case 0: //đẹp trai nhất vũ trụ
                        if (playerSummonShenron.getBagNull() <= 0) {
                            Service.gI().sendThongBao(playerSummonShenron, "Hành Trang Không Đủ Ô Trống");
                            sendWhishesShenron(playerSummonShenron);
                            return;
                        }
                        Item item = new Item();
                        item.quantity = 1;
                        item.template = ItemData.gI().getTemplate((short) (227 + playerSummonShenron.gender));
                        item.content = item.getContent();
                        item.itemOptions.add(new ItemOption(77, Util.nextInt(5, 20)));
                        item.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                        playerSummonShenron.inventory.addItemBag(item);
                        playerSummonShenron.inventory.sendItemBags();
                        break;
                    case 1: //+1,5 ngọc
                        this.playerSummonShenron.inventory.gem += 1500;
                        this.playerSummonShenron.sendInfo();
                        break;
                    case 2: //+200 tr smtn
                        Service.gI().congTiemNang(this.playerSummonShenron, (byte) 2, 200000000);
                        break;
                }
                break;
            case NpcFactory.SHENRON_2:
                switch (this.select) {
                    case 0: //+150 ngọc
                        this.playerSummonShenron.inventory.gem += 150;
                        this.playerSummonShenron.sendInfo();
                        break;
                    case 1: //+20 tr smtn
                        Service.gI().congTiemNang(this.playerSummonShenron, (byte) 2, 20000000);
                        break;
                    case 2: //2 tr vàng
                        this.playerSummonShenron.inventory.gold += 2000000;
                        if (this.playerSummonShenron.inventory.gold > playerSummonShenron.inventory.LIMIT_GOLD) {
                            this.playerSummonShenron.inventory.gold = playerSummonShenron.inventory.LIMIT_GOLD;
                        }
                        this.playerSummonShenron.sendInfo();
                        break;
                }
                break;
            case NpcFactory.SHENRON_3:
                switch (this.select) {
                    case 0: //+15 ngọc
                        this.playerSummonShenron.inventory.gem += 15;
                        this.playerSummonShenron.sendInfo();
                        break;
                    case 1: //+2 tr smtn
                        Service.gI().congTiemNang(this.playerSummonShenron, (byte) 2, 2000000);
                        break;
                    case 2: //200k vàng
                        this.playerSummonShenron.inventory.gold += 200000;
                        if (this.playerSummonShenron.inventory.gold > playerSummonShenron.inventory.LIMIT_GOLD) {
                            this.playerSummonShenron.inventory.gold = playerSummonShenron.inventory.LIMIT_GOLD;
                        }
                        this.playerSummonShenron.sendInfo();
                        break;
                }
                break;
        }
        shenronLeave(this.playerSummonShenron, WISHED);
    }

    public void showConfirmShenron(Player pl, int menu, byte select) {
        this.menuShenron = menu;
        this.select = select;
        String wish = null;
        switch (menu) {
            case NpcFactory.SHENRON_1_1:
                wish = SHENRON_1_STAR_WISHES_1[select];
                break;
            case NpcFactory.SHENRON_1_2:
                wish = SHENRON_1_STAR_WISHES_2[select];
                break;
            case NpcFactory.SHENRON_2:
                wish = SHENRON_2_STARS_WHISHES[select];
                break;
            case NpcFactory.SHENRON_3:
                wish = SHENRON_3_STARS_WHISHES[select];
                break;
        }
        Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_CONFIRM, "Ngươi có chắc muốn ước?", wish, "Từ chối");
    }

    public void reOpenShenronWishes(Player pl) {
        switch (menuShenron) {
            case NpcFactory.SHENRON_1_1:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_1_1, GET_SHENRON_SAY(pl), SHENRON_1_STAR_WISHES_1);
                break;
            case NpcFactory.SHENRON_1_2:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_1_2, GET_SHENRON_SAY(pl), SHENRON_1_STAR_WISHES_2);
                break;
            case NpcFactory.SHENRON_2:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_2, GET_SHENRON_SAY(pl), SHENRON_2_STARS_WHISHES);
                break;
            case NpcFactory.SHENRON_3:
                Npc.createMenuRongThieng(pl, NpcFactory.SHENRON_3, GET_SHENRON_SAY(pl), SHENRON_3_STARS_WHISHES);
                break;
        }
    }
    
    //--------------------------------------------------------------------------\\
    private void activeShenron(Player pl, boolean appear, boolean... isMess) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(appear ? 0 : (byte) 1);
            if (appear) {
                msg.writer().writeShort(pl.zone.map.id);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(pl.zone.zoneId);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.x);
                msg.writer().writeShort(pl.y);
                msg.writer().writeByte((isMess.length == 0 ? DRAGON_SHENRON : 1));
                if(isMess.length == 0){
                    sendNotifyShenronAppear(pl);
                }
                lastTimeShenronWait = System.currentTimeMillis();
                isShenronAppear = true;
            }
            Service.gI().sendMessAllPlayer(msg);
        } catch (Exception e) {
        }
    }
    
    private void sendNotifyShenronAppear(Player pl) {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(pl.name + " vừa gọi rồng thần tại " + pl.zone.map.name + " khu vực " + pl.zone.zoneId);
            Service.gI().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    
    public String GET_SHENRON_SAY(Player pl) {
        int timeLeft = (int) ((Setting.TIME_DRAGON_WAIT - (System.currentTimeMillis() - lastTimeShenronWait)) / 1000);
        String timeLefts = (timeLeft < 60 ? timeLeft + " giây" : timeLeft / 60 + " phút");
        return SHENRON_SAY.replace("#", timeLefts);
    }

    public void shenronLeave(Player pl, byte type) {
        if (type == WISHED) {
            Npc.createTutorial(pl, -1, "Điều ước của ngươi đã trở thành sự thật\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai");
        } else {
            Npc.createMenuRongThieng(pl, NpcFactory.IGNORE_MENU, "Ta buồn ngủ quá rồi\nHẹn gặp ngươi lần sau, ta đi đây, bái bai");
        }
        activeShenron(pl, false);
        this.playerSummonShenron = null;
        this.isShenronAppear = false;
        this.menuShenron = -1;
        this.select = -1;
    }
}
