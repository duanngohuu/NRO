package real.func;

import java.util.ArrayList;
import java.util.List;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.Mob;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Player;
import server.Service;
import static server.Service.numberToMoney;
import server.Util;
import server.io.Message;
import service.Setting;

public final class Combine {

    public static final byte EP_SAO_TRANG_BI = 10;
    public static final byte PHA_LE_HOA_TRANG_BI = 11;
    public static final byte CHUYEN_HOA_TRANG_BI = 12;
    public static final byte NANG_CAP_TRANG_BI = 13;
    public static final byte NANG_CAP_BONG_TAI = 14;
    public static final byte NANG_CHI_SO_BONG_TAI = 15;
    public static final byte NHAP_NGOC_RONG = 16;
    public static final byte NANG_CAP_TRANG_BI_SKH = 17;
    public static final byte NANG_CAP_TRANG_BI_THIENSU = 18;

    public static final byte OPEN_TAB_COMBINE = 0;
    public static final byte READD_ITEM_TAB = 1;
    public static final byte COMBINE_SUCCESS = 2;
    public static final byte COMBINE_FAIL = 3;
    public static final byte COMBINE_UPGRADE = 4;
    public static final byte COMBINE_DRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;

    final Player player;

    private boolean isCombine;
    
    private long currCombine;
    
    private byte typeCombine;
    private byte tiLeCombine;
    private int giaGoldCombine;
    private int giaGemCombine;
    private final List<Item> itemCombine;

    public Combine(Player player) {
        this.player = player;
        itemCombine = new ArrayList<>();
        setUncombine();
    }

    public void setUncombine() {
        itemCombine.clear();
        typeCombine = -1;
        tiLeCombine = 0;
        giaGoldCombine = 0;
        giaGemCombine = 0;
    }

    public byte getTypeCombine() {
        return this.typeCombine;
    }

    public void setTypeCombine(byte type) {
        this.typeCombine = type;
    }

    public byte getTileCombine() {
        return this.tiLeCombine;
    }

    public void setTiLeCombine(byte tiLe) {
        this.tiLeCombine = tiLe;
    }

    public int getGoldCombine() {
        return this.giaGoldCombine;
    }

    public void setGoldCombine(int gia) {
        this.giaGoldCombine = gia;
    }

    public int getGemCombine() {
        return this.giaGemCombine;
    }

    public void setGemCombine(int gia) {
        this.giaGemCombine = gia;
    }

    public List<Item> getItemCombine() {
        return this.itemCombine;
    }

    public void openTabCombine(byte type) {
        setTypeCombine(type);
        switch (type) {
            case EP_SAO_TRANG_BI:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo, quần, găng, giày hoặc rada) có ô đặt sao pha lê\n"
                        + "Chọn loại sao pha lê\n"
                        + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
                        + "cho trang bị của ngươi\n"
                        + "trở nên mạnh mẽ");
                break;
            case PHA_LE_HOA_TRANG_BI:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo, quần, găng, giày hoặc rada)\n"
                        + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
                        + "cho trang bị của ngươi\n"
                        + "trở thành trang bị pha lê");
                break;
            case CHUYEN_HOA_TRANG_BI:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn trang bị gốc\n"
                        + "(Áo,quần,găng,giày hoặc rada)\n"
                        + "từ cấp [+4] trở lên\n"
                        + "chọn tiếp trang bị mới\n"
                        + "chưa nâng cấp cần nhập thể\n"
                        + "sau đó chọn 'Nâng cấp'", "Lưu ý trang bị mới\nphải hơn trang bị gốc\n1 bậc");
                break;
            case NANG_CAP_TRANG_BI:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo,quần,găng,giày hoặc rada)\n"
                        + "Chọn loại đá để nâng cấp\n"
                        + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
                        + "cho trang bị của ngươi\n"
                        + "trở nên mạnh mẽ hơn");
                break;
            case NANG_CAP_BONG_TAI:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn bông tai Porata\n"
                        + "Chọn mảnh bông tai để nâng cấp\n"
                        + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
                        + "cho bông tai Porata của ngươi\n"
                        + "trở nên mạnh mẽ hơn");
                break;
            case NANG_CHI_SO_BONG_TAI:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn bông tai Porata cấp 2\n"
                        + "Chọn mảnh hồn bông tai và đá xanh lam\nđể nâng cấp\n"
                        + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
                        + "cho bông tai Porata của ngươi\n"
                        + "trở nên mạnh mẽ hơn");
                break;
            case NHAP_NGOC_RONG:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn 7 viên ngọc rồng cùng sao\n"
                        + "Sau đó chọn làm phép", "Ta sẽ phù phép\n"
                        + "cho 7 viên ngọc rống\n"
                        + "thành 1 viên ngọc rồng cao cấp");
                break;
            case NANG_CAP_TRANG_BI_SKH:
                doCombine(OPEN_TAB_COMBINE, "Vào hành trang\n"
                        + "Chọn trang bị\n"
                        + "(Áo,quần,găng,giày hoặc rada)\n"
                        + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
                        + "cho trang bị của ngươi\n"
                        + "trở thành đồ kích hoạt");
                break;
            case NANG_CAP_TRANG_BI_THIENSU:
                doCombine(OPEN_TAB_COMBINE, "Cần 1 công thức\n"
                        + "Mảnh trang bị tương ứng\n"
                        + "1 đá nâng cấp (tùy chọn)\n"
                        + "1 đá may mắn (tùy chọn)", "Chế tạo\n"
                        + "trang bị thiên sứ");
                break;
        }
    }

    public void startCombine() {
        switch (typeCombine) {
            case EP_SAO_TRANG_BI:
                doCombine(isSuccessCombine(), null, null);
                break;
            case PHA_LE_HOA_TRANG_BI:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case NANG_CAP_TRANG_BI:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case NANG_CAP_BONG_TAI:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case NANG_CHI_SO_BONG_TAI:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case CHUYEN_HOA_TRANG_BI:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case NHAP_NGOC_RONG:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case NANG_CAP_TRANG_BI_SKH:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
            case NANG_CAP_TRANG_BI_THIENSU:
                doCombine(player.combine.isSuccessCombine(), null, null);
                break;
        }
        Service.gI().sendMoney(player);
        player.inventory.sendItemBags();
        doCombine(READD_ITEM_TAB, null, null); //load lại info item trong tab nâng cấp
        isCombine = false;
    }

    public void doCombine(int type, String info, String infoTop, short... iconId) {
        Message msg = null;
        try
        {
            msg = new Message(-81);
            msg.writer().writeByte(type);
            switch (type) {
                case -1:
                    msg.cleanup();
                    return;
                case OPEN_TAB_COMBINE:
                    msg.writer().writeUTF(info);
                    msg.writer().writeUTF(infoTop);
                    break;
                case READD_ITEM_TAB:
                    msg.writer().writeByte(getItemCombine().size());
                    for (Item it : getItemCombine()) {
                        for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                            if (it == player.inventory.itemsBag.get(j)) {
                                msg.writer().writeByte(j);
                            }
                        }
                    }
                    break;
                case COMBINE_SUCCESS:
                    //thành công
                    break;
                case COMBINE_FAIL:
                    //xịt
                    break;
                case COMBINE_UPGRADE:
                    msg.writer().writeShort(iconId[0]);
                    break;
                case COMBINE_DRAGON_BALL:
                    msg.writer().writeShort(iconId[0]);
                    break;
                case OPEN_ITEM:
                    msg.writer().writeShort(iconId[0]);
                    msg.writer().writeShort(iconId[1]);
                    msg.writer().writeShort(-1);
                    break;
                case 7:
                    msg.writer().writeShort(iconId[0]);
                    msg.writer().writeShort(iconId[1]);
                    break;
                default:
                    msg.writer().writeShort(iconId[0]);
                    msg.writer().writeShort(iconId[1]);
                    break;
            }
            player.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private byte isSuccessCombine() {
        byte success = COMBINE_FAIL;
        try{
            if(isCombine){
                return -1;
            }
            isCombine = true;
            boolean isHaveStar = false;
            Item trangBi = null, trangBi2 = null, trangBi3 = null, daPhaLe = null, daPhaLe2 = null, daPhaLe3 = null,
                    CT = null, CTvip = null, DaNC = null, DaMM = null;
            int tiLe = getTileCombine();
            switch (getTypeCombine()) {
                case EP_SAO_TRANG_BI:
                    if (player.inventory.getGemAndRuby() < 10) {
                        Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu " + Util.powerToString(10 - player.inventory.getGemAndRuby()) + " ngọc nữa!");
                        return -1;
                    }
                    for (Item item : getItemCombine()) {
                        if (isItemCanCombine(item)) {
                            trangBi = item;
                        }
                        if (isDaPhaLe(item)) {
                            daPhaLe = item;
                        }
                    }
                    if (starSlot(trangBi) >= maxStarSlot(trangBi)) {
                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.", "Đóng");
                        return -1;
                    }
                    if(addParam(trangBi, 102, 1) && addParam(trangBi, getOptionDaPhaLe(daPhaLe), getParamDaPhaLe(daPhaLe))){
                        player.inventory.subQuantityItemsBag(daPhaLe, 1);
                        if (daPhaLe.quantity <= 0) {
                            getItemCombine().remove(daPhaLe);
                        }
                        player.inventory.subGemAndRuby(10);
                        Service.gI().sendMoney(player);
                        success = COMBINE_SUCCESS;
                    }
                    break;
                case NHAP_NGOC_RONG:
                    if (getItemCombine().size() > 1) {
                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Chọn 1 loại ngọc rồng thôi anh pạn!", "Đóng");
                        return -1;
                    }
                    if (getItemCombine().get(0).template.id > 14 && getItemCombine().get(0).template.id <= 20) {
                        if (getItemCombine().get(0).quantity < 7) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 7 viên ngọc rồng để phù phép", "Đóng");
                            return -1;
                        }
                        player.inventory.addItemBag(ItemData.gI().get_item(getItemCombine().get(0).template.id - 1));
                        player.inventory.subQuantityItemsBag(getItemCombine().get(0), 7);
                        if (getItemCombine().get(0).quantity == 0) {
                            getItemCombine().remove(getItemCombine().get(0));
                        }
                        success = COMBINE_SUCCESS;
                    } else {
                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Vui lòng chọn ngọc rồng.", "Đóng");
                    }
                    break;
                case PHA_LE_HOA_TRANG_BI:
                    int giaDap = getGoldCombine();
                    if (player.inventory.gold < giaDap) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.powerToString(giaDap - player.inventory.gold) + " vàng nữa!");
                        return -1;
                    }
                    player.inventory.gold -= giaDap;
                    Service.gI().sendMoney(player);
                    int star = getParam(player.combine.getItemCombine().get(0), 107);
                    if (star >= Setting.ITEM_START) {
                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Vật phẩm đã tối đa sao pha lê", "Đóng");
                        return -1;
                    }
                    if (Util.isComTrue(tiLe) || player.role >= 99) {
                        Item item = player.combine.getItemCombine().get(0);
                        for (ItemOption itemOption : item.itemOptions) {
                            if (itemOption.optionTemplate.id == 107) {
                                itemOption.param++;
                                isHaveStar = true;
                                break;
                            }
                        }
                        if (!isHaveStar) {
                            item.itemOptions.add(new ItemOption(107, (short) 1));
                        }
                        if(star + 1 > 4){
                            Service.gI().sendThongBaoBenDuoi("Chúc mừng " + player.name + " đã pha lê hóa thành công " + item.template.name + " +" + (star + 1) + " ô Sao Pha Lê");
                        }
                        success = COMBINE_SUCCESS;
                    }
                    break;
                case CHUYEN_HOA_TRANG_BI:
                    if(getItemCombine().size() != 2){
                        return -1;
                    }
                    for (Item item : getItemCombine()) {
                        if (item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 72 && op.param >= 4)) {
                            trangBi = item;
                            continue;
                        }
                        boolean isSPL = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 72);
                        boolean isCAP = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 107);
                        if(!isSPL && !isCAP){
                            trangBi2 = item;
                        }
                    }
                    if(trangBi == null || trangBi2 == null){
                        return -1;
                    }
                    if(!player.inventory.checkItem(trangBi) || !player.inventory.checkItem(trangBi2)){
                        return -1;
                    }
                    int CAP = trangBi2.template.level - trangBi.template.level;
                    if(CAP < 1 || CAP > 1 || trangBi.template.type != trangBi2.template.type){
                        return -1;
                    }
                    if(trangBi.template.level != 12){
                        return -1;
                    }
                    int count = getParam(trangBi, 72);
                    if(player.inventory.gold < 500000000){
                        return -1;
                    }
                    if(Util.isComTrue(tiLeCombine)){
                        List<ItemOption> listOption = new ArrayList<>();
                        trangBi.itemOptions.stream().filter(
                            op -> op.optionTemplate.id != 0 && 
                            op.optionTemplate.id != 2 && 
                            op.optionTemplate.id != 6 && 
                            op.optionTemplate.id != 7 &&
                            op.optionTemplate.id != 14 && 
                            op.optionTemplate.id != 21 && 
                            op.optionTemplate.id != 22 && 
                            op.optionTemplate.id != 23 && 
                            op.optionTemplate.id != 27 && 
                            op.optionTemplate.id != 28 && 
                            op.optionTemplate.id != 47 &&
                            op.optionTemplate.id != 48)
                        .forEach(op -> listOption.add(op));
                        for (ItemOption io : trangBi2.itemOptions) {
                            if (OptionCanUpgrade(io)) {
                                addParam(trangBi2, io.optionTemplate.id, paramMutiNextLevel(trangBi2, io, count));
                            }
                        }
                        for (ItemOption io : listOption) {
                            trangBi2.itemOptions.add(io);
                        }
//                        addParam(trangBi2, 208, 1); Đã chuyển hóa
                        player.inventory.subQuantityItemsBag(trangBi, 1);
                        getItemCombine().remove(trangBi);
                        success = COMBINE_SUCCESS;
                    }
                    player.inventory.gold -= 500000000;
                    break;
                case NANG_CAP_TRANG_BI_SKH:
                    if(getItemCombine().size() != 3 || player.inventory.gold < getGoldCombine()){
                        return -1;
                    }
                    trangBi = getItemCombine().get(0);
                    trangBi2 = getItemCombine().get(1);
                    trangBi3 = getItemCombine().get(2);
                    if(trangBi.template.level != 13 || trangBi2.template.level != 13 || trangBi3.template.level != 13){
                        return -1;
                    }
                    if(trangBi.template.type > 4 || trangBi2.template.type > 4 || trangBi3.template.type > 4){
                        return -1;
                    }
                    int[] arrItem = Mob.get_do(trangBi.template.gender, Util.nextInt(1, 3));
                    Item SKH = ItemData.gI().get_item(arrItem[Util.nextInt(0, arrItem.length - 1)]);
                    List<ItemOption> ops = ItemData.gI().get_skh(trangBi.template.gender, Util.nextInt(0, 2));
                    SKH.itemOptions.addAll(ops);
                    player.inventory.addItemBag(SKH);
                    trangBi.quantity = 0;
                    trangBi2.quantity = 0;
                    trangBi3.quantity = 0;
                    if (trangBi.quantity == 0) {
                        getItemCombine().remove(trangBi);
                        player.inventory.subQuantityItemsBag(trangBi, 1);
                    }
                    if (trangBi2.quantity == 0) {
                        getItemCombine().remove(trangBi2);
                        player.inventory.subQuantityItemsBag(trangBi2, 1);
                    }
                    if (trangBi3.quantity == 0) {
                        getItemCombine().remove(trangBi3);
                        player.inventory.subQuantityItemsBag(trangBi3, 1);
                    }
                    getItemCombine().add(SKH);
                    player.inventory.gold -= getGoldCombine();
                    success = COMBINE_SUCCESS;
                    break;
                case NANG_CAP_TRANG_BI_THIENSU:
                    for (Item item : getItemCombine()) {
                        if (isCT(item)) {
                            CT = item;
                        }
                        if (isCTVIP(item)) {
                            CTvip = item;
                        }
                        if (isDaNC(item)) {
                            DaNC = item;
                        }
                        if (isDaMM(item)) {
                            DaMM = item;
                        }
                    }
                    break;
                case NANG_CAP_TRANG_BI:
                    if (player.inventory.getGemAndRuby() < getGemCombine()) {
                        Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu " + Util.powerToString(10 - player.inventory.getGemAndRuby()) + " ngọc nữa!");
                        return -1;
                    }
                    player.inventory.subGemAndRuby(getGemCombine());
                    Service.gI().sendMoney(player);
                    for (Item item : getItemCombine()) {
                        if (isItemCanNangCap(item)) {
                            trangBi = item;
                        }
                        if (isDaNangCap(item)) {
                            daPhaLe = item;
                        }
                    }
                    if(trangBi == null || daPhaLe == null)
                    {
                        Service.gI().sendThongBaoOK(player, "Cần một trang bị và đá nâng cấp phù hợp");
                        return -1;
                    }
                    if(!player.inventory.checkItem(trangBi) || !player.inventory.checkItem(daPhaLe)){
                        Service.gI().sendThongBaoOK(player, "Cần một trang bị và đá nâng cấp phù hợp");
                        return -1;
                    }
                    int level = getParam(trangBi, 72);
                    Item iDBV = player.inventory.findItemBagByTemp(987);
                    if(iDBV == null){
                        iDBV = player.inventory.findItemBagByTemp(1143);
                    }
                    if(player.getSelectMenu() == 1 && level != 2 && level != 4 && level != 6 && level != 7 && iDBV != null && iDBV.quantity > 0){
                        return -1;
                    }
                    if(player.getSelectMenu() == 1 && iDBV == null){
                        return -1;
                    }
                    if (level >= Setting.ITEM_LEVEL) {
                        Service.gI().sendThongBaoOK(player, "Trang bị đã đạt cấp tối đa");
                        return -1;
                    }
                    if (Util.isComTrue(tiLe) || player.role >= 99) {
                        success = COMBINE_SUCCESS;
                        addParam(trangBi, 72, 1);
                        for (ItemOption io : trangBi.itemOptions) {
                            if (OptionCanUpgrade(io)) {
                                addParam(trangBi, io.optionTemplate.id, paramNextLevel(trangBi, io));
                            }
                        }
                        if(level + 1 > 4){
                            Service.gI().sendThongBaoBenDuoi("Chúc mừng " + player.name + " đã nâng cấp thành công " + trangBi.template.name + " +" + (level + 1));
                        }
                    }
                    else
                    {
                        if(!useDBV(player) && removeParam(trangBi) && removeCap(trangBi) && addParam(trangBi, 209, 1))
                        {
                            success = COMBINE_FAIL;
                        }
                    }
                    if(useDBV(player)){
                        player.inventory.subQuantityItemsBag(iDBV, 1);
                    }
                    player.inventory.subQuantityItemsBag(daPhaLe, 5 + trangBi.template.level + level);
                    if (daPhaLe.quantity == 0) {
                        getItemCombine().remove(daPhaLe);
                    }
                    break;
                case NANG_CAP_BONG_TAI:
                    if (player.inventory.getGemAndRuby() < getGemCombine()) {
                        Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu " + Util.powerToString(getGemCombine() - player.inventory.getGemAndRuby()) + " ngọc nữa!");
                        return -1;
                    } else if (player.inventory.gold < getGoldCombine()) {
                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.powerToString(getGoldCombine() - player.inventory.gold) + " vàng nữa!");
                        return -1;
                    } else if (player.inventory.findItem(player.inventory.itemsBag, 921) != null || player.inventory.findItem(player.inventory.itemsBox, 921) != null) {
                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Bạn đã sở hữu bông tai Porata cấp 2 rồi.", "Đóng");
                        return -1;
                    }
                    player.inventory.subGemAndRuby(getGemCombine());
                    player.inventory.gold -= this.getGoldCombine();
                    Service.gI().sendMoney(player);
                    for (Item item : getItemCombine()) {
                        if (item.template.id == 454) {
                            trangBi = item;
                        }
                        if (item.template.id == 933) {
                            daPhaLe = item;
                        }
                    }
                    if (Util.isComTrue(tiLe)) {
                        player.inventory.subQuantityItemsBag(trangBi, 1);
                        Item item = new Item();
                        item = ItemData.gI().get_item((short) 921);
                        item.itemOptions.clear();
                        addParam(item, 72, 2);
                        player.inventory.addItemBag(item);
                        success = COMBINE_SUCCESS;
                        addParam(daPhaLe, 31, -9999);
                    } else {
                        success = COMBINE_FAIL;
                        addParam(daPhaLe, 31, -99);
                    }
                    player.inventory.sendItemBags();
                    if (getParam(daPhaLe, 31) == 0) {
                        getItemCombine().remove(daPhaLe);
                    }
                    break;
                case NANG_CHI_SO_BONG_TAI:
                    if (player.inventory.getGemAndRuby() < getGemCombine()) {
                        Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu " + Util.powerToString(getGemCombine() - player.inventory.getGemAndRuby()) + " ngọc nữa!");
                        return -1;
                    }
                    player.inventory.subGemAndRuby(getGemCombine());
                    Service.gI().sendMoney(player);
                    Item BongTai2 = null,
                     honBongTai = null,
                     daXanhLam = null;
                    for (Item item : getItemCombine()) {
                        if (item.template.id == 921) {
                            BongTai2 = item;
                        }
                        if (item.template.id == 934) {
                            honBongTai = item;
                        }
                        if (item.template.id == 935) {
                            daXanhLam = item;
                        }
                    }
                    if (Util.isComTrue(tiLe)) {
                        byte[] option = {77, 80, 81, 103, 50, 94, 5};
                        byte optionid = 0;
                        byte param = 0;
                        if (BongTai2.itemOptions.size() < 2) {
                            optionid = option[Util.nextInt(0, option.length - 1)];
                            param = (byte) Util.nextInt(5, 15);
                            BongTai2.itemOptions.add(new ItemOption(optionid, param));
                        } else {
                            BongTai2.itemOptions.remove(1);
                            optionid = option[Util.nextInt(0, option.length - 1)];
                            param = (byte) Util.nextInt(5, 15);
                            BongTai2.itemOptions.add(new ItemOption(optionid, param));
                        }
                        success = COMBINE_SUCCESS;
                    } else {
                        success = COMBINE_FAIL;
                    }
                    player.inventory.subQuantityItemsBag(honBongTai, 99);
                    player.inventory.subQuantityItemsBag(daXanhLam, 1);
                    break;
            }
        } catch (Exception e) {
        }
        return success;
    }

    public boolean isOptionBTC2(byte id) {
        return id == 77 || id == 80 || id == 81 || id == 103 || id == 50 || id == 94 || id == 5;
    }

    public void showInfoCombine(int... index) {
        itemCombine.clear();
        if (index.length > 0) {
            try {
                Item[] items = new Item[index.length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = player.inventory.itemsBag.get(index[i]);
                    int id = items[i].template.id;
                    if (Transaction.gI().findTran(player) != null) {
                        Service.gI().sendPopUpMultiLine(player, 0, 0, "Không thể thực hiện");
                        return;
                    }
                    if(!player.combine.getItemCombine().equals(items[i])){
                        player.combine.getItemCombine().add(items[i]);
                    }
                }
                switch (player.combine.getTypeCombine()) {
                    case EP_SAO_TRANG_BI:
                        if (items.length != 2) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.", "Đóng");
                        } else {
                            Item trangBi = null, daPhaLe = null;
                            for (Item item : items) {
                                if (isItemCanCombine(item)) {
                                    trangBi = item;
                                }
                                if (isDaPhaLe(item)) {
                                    daPhaLe = item;
                                }
                            }
                            if (trangBi == null || daPhaLe == null || starSlot(trangBi) == maxStarSlot(trangBi)) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.", "Đóng");
                            } else {
                                this.setGemCombine(10);
                                String npcSay = trangBi.template.name + "\n";
                                String spl = null;
                                String NEWIO = null;
                                for (ItemOption io : trangBi.itemOptions) {
                                    if (daPhaLe.template.type == 30) {
                                        for (ItemOption io2 : daPhaLe.itemOptions) {
                                            if (io2.optionTemplate.name.contains(io.optionTemplate.name)) {
                                                NEWIO = "HEHE";
                                                spl = "HEHE";
                                                npcSay += "|2|" + io.optionTemplate.name.replaceAll("#", (io.param + getParamDaPhaLe(daPhaLe)) + "") + "\n";
                                                break;
                                            }
                                        }
                                    } else {
                                        String TeleName = ItemData.gI().getItemOptionTemplate(getOptionDaPhaLe(daPhaLe)).name;
                                        if (io.optionTemplate.name.contains(TeleName)) {
                                            NEWIO = "HEHE";
                                            spl = "HEHE";
                                            npcSay += "|2|" + io.optionTemplate.name.replaceAll("#", (io.param + getParamDaPhaLe(daPhaLe)) + "") + "\n";
                                        }
                                    }
                                    if (io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && spl == null) {
                                        npcSay += "|0|" + io.optionTemplate.name.replaceAll("#", io.param + "") + "\n";
                                    }
                                    spl = null;
                                }
                                if (daPhaLe.template.type == 30 && NEWIO == null) {
                                    for (ItemOption io : daPhaLe.itemOptions) {
                                        npcSay += "|2|" + io.optionTemplate.name.replaceAll("#", io.param + "") + "\n";
                                    }
                                } else if (NEWIO == null) {
                                    npcSay += "|2|" + ItemData.gI().getItemOptionTemplate(getOptionDaPhaLe(daPhaLe)).name.replaceAll("#", (getParamDaPhaLe(daPhaLe)) + "") + "\n";
                                }
                                npcSay += "|2|Cần 10 ngọc";
                                if (player.inventory.getGemAndRuby() >= this.getGemCombine()) {
                                    Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Nâng cấp\n10 ngọc", "Từ chối");
                                } else {
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (getGemCombine() - player.inventory.getGemAndRuby()) + " ngọc", "Đóng");
                                }
                            }
                        }
                        break;
                    case NHAP_NGOC_RONG:
                        if (items.length > 1) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Chọn 1 loại ngọc rồng thôi anh pạn!", "Đóng");
                        }else if (items[0].template.id > 14 && items[0].template.id <= 20) {
                            if (items[0].quantity < 7) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 7 viên ngọc rồng để phù phép", "Đóng");
                            } else {
                                String npcSay = "|2|Con có muốn biến 7 " + items[0].template.name + " thành\n 1 viên " + ItemData.gI().get_item(items[0].template.id - 1).template.name;
                                if (items[0].quantity < 7) {
                                    npcSay += "\n|7|Cần " + (7 - items[0].quantity) + " " + items[0].template.name;
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (7 - items[0].quantity) + " " + items[0].template.name);
                                } else {
                                    npcSay += "\n|1|Cần 7 " + items[0].template.name;
                                    Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Làm phép");
                                }
                            }
                        } else {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Vui lòng chọn ngọc rồng.", "Đóng");
                        }
                        break;
                    case PHA_LE_HOA_TRANG_BI:
                        if (items.length > 1) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Chọn 1 vật phẩm thôi anh bạn!", "Đóng");
                        } else if (isItemCanCombine(items[0])) {
                            int star = getParam(items[0], 107);
                            if (star >= Setting.ITEM_START) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Vật phẩm đã tối đa sao pha lê", "Đóng");
                            } else {
                                setGoldCombine(getGiaDapDo(star));
                                setTiLeCombine((byte) getTiLeDapDo(star));
                                String npcSay = items[0].template.name + "\n|0|";
                                for (ItemOption io : items[0].itemOptions) {
                                    if (io.optionTemplate.id == 102 || io.optionTemplate.id == 107 || io.optionTemplate.id == 72) {
                                        break;
                                    }
                                    npcSay += io.optionTemplate.name.replaceAll("#", io.param + "") + "\n";
                                }
                                npcSay += star == 0 ? "|2|1 ô Sao Pha Lê\n" : "|2|" + (star + 1) + " ô Sao Pha Lê\n";
                                if (player.inventory.gold < this.giaGoldCombine) {
                                    npcSay += "|2|Tỉ lệ thành công: " + this.tiLeCombine + "%\n|7|Cần " + numberToMoney(this.giaGoldCombine) + " vàng";
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + numberToMoney(this.giaGoldCombine - player.inventory.gold) + "\nvàng");
                                } else {
                                    npcSay += "|2|Tỉ lệ thành công: " + this.tiLeCombine + "%\nCần " + numberToMoney(this.giaGoldCombine) + " vàng";
                                    Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Nâng cấp", "Đóng");
                                }
                            }
                        } else {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Vui lòng chọn đúng trang bị.", "Đóng");
                        }
                        break;
                    case CHUYEN_HOA_TRANG_BI:
                        if (items.length != 2) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Chưa đủ yêu cầu không thể chuyển hóa", "Đóng");
                        }
                        else
                        {
                            String npcSay = null;
                            Item trangBi = null, trangBi2 = null;
                            for (Item item : items) {
                                if (item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 72 && op.param >= 4)) {
                                    trangBi = item;
                                    continue;
                                }
                                boolean isSPL = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 72);
                                boolean isCAP = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id == 107);
                                boolean isSKH = item.itemOptions.stream().anyMatch(op -> op.optionTemplate.id >= 127 && op.optionTemplate.id <= 143);
                                if(!isSPL && !isCAP && !isSKH && item.template.id != 674){
                                    trangBi2 = item;
                                }
                            }
                            if(trangBi == null){
                                npcSay = "Cần 1 trang bị [+4] trở lên";
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Đóng");
                            }
                            else if(trangBi2 == null){
                                npcSay = "Cần 1 trang bị gốc chưa nâng cấp";
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Đóng");
                            }
                            else if(trangBi2.template.level - trangBi.template.level < 1 || trangBi2.template.level - trangBi.template.level > 1){
                                npcSay = "Cần 1 trang bị chưa nâng cấp hơn 1 bậc của trang bị cần nhập thể";
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Đóng");
                            }
                            else if(trangBi.template.gender != trangBi2.template.gender){
                                npcSay = "Cần trang bị cùng trủng tộc";
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Đóng");
                            }
                            else if(trangBi.template.level != 12){
                                npcSay = "Cần trang bị cấp jean, lưỡng long, vàng Zealot trở lên.";
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Đóng");
                            }
                            else if(trangBi2.template.type != trangBi.template.type){
                                npcSay = "Yêu cầu trang bị cùng loại";
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Đóng");
                            }
                            else{
                                setTiLeCombine((byte)(80 - trangBi2.template.level * 5));
                                int level = getParam(trangBi, 72);
                                npcSay = "|1|" + trangBi2.template.name + " [+" + level + "]" + "\n";
                                List<ItemOption> listOption = new ArrayList<>();
                                trangBi.itemOptions.stream().filter(
                                        op -> op.optionTemplate.id != 0 && 
                                        op.optionTemplate.id != 2 && 
                                        op.optionTemplate.id != 6 && 
                                        op.optionTemplate.id != 7 &&
                                        op.optionTemplate.id != 14 && 
                                        op.optionTemplate.id != 21 && 
                                        op.optionTemplate.id != 22 && 
                                        op.optionTemplate.id != 23 && 
                                        op.optionTemplate.id != 27 && 
                                        op.optionTemplate.id != 28 && 
                                        op.optionTemplate.id != 47 &&
                                        op.optionTemplate.id != 48)
                                .forEach(op -> listOption.add(op));
                                for (ItemOption io : trangBi2.itemOptions) {
                                    if (OptionCanUpgrade(io)) {
                                        int param = paramMutiNextLevel(trangBi2 , io, level);
                                        npcSay += "|0|" + io.optionTemplate.name.replaceAll("#", (io.param + param) + "") + "\n";
                                    }
                                }
                                for (ItemOption io : listOption) {
                                    if(io.optionTemplate.id == 72 || io.optionTemplate.id == 102 || io.optionTemplate.id == 107){
                                        continue;
                                    }
                                    npcSay += "|0|" + io.optionTemplate.name.replaceAll("#", io.param + "") + "\n";
                                }
                                int indexMenu = NpcFactory.START_COMBINE;
                                String strMenu = "Nâng cấp\n" + numberToMoney(500000000);
                                npcSay += "|2|Tỉ lệ thành công: " + this.tiLeCombine + "%\n";
                                if(player.inventory.gold < 500000000){
                                    npcSay += "|7|Cần " + numberToMoney(500000000) + " vàng";
                                    indexMenu = NpcFactory.IGNORE_MENU;
                                    strMenu = "Còn thiếu\n" + numberToMoney(500000000 - player.inventory.gold);
                                }
                                else{
                                    npcSay += "|2|Cần " + numberToMoney(500000000) + " vàng";
                                }
                                
                                Npc.createMenuConMeo(player, indexMenu, 1410, npcSay, strMenu, "Đóng");
                            }
                        }
                        break;
                    case NANG_CAP_TRANG_BI_SKH:
                        if (items.length != 3)
                        {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 3 trang bị thần linh để nâng cấp.", "Đóng");
                        }
                        else
                        {
                            setGoldCombine(2000000000);
                            Item trangBi = items[0], trangBi2 = items[1], trangBi3 = items[2];
                            if (trangBi == null || trangBi2 == null || trangBi3 == null)
                            {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 3 trang bị thần linh để nâng cấp.", "Đóng");
                            }
                            else
                            {
                                if(trangBi.template.level != 13 || trangBi2.template.level != 13 || trangBi3.template.level != 13)
                                {
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 3 trang bị thần linh để nâng cấp.", "Đóng");
                                }
                                else if(player.inventory.gold < getGoldCombine())
                                {
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "|2|1 trang bị bí ẩn\n|5|Cần 2Tỷ Vàng", "Còn thiếu\n" + Util.powerToString(getGoldCombine() - player.inventory.gold) + " Vàng");
                                }
                                else{
                                    Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, "|2|1 trang bị bí ẩn\n|2|Cần 2Tỷ Vàng", "Nâng cấp\n" + Util.powerToString(getGoldCombine()) + " Vàng", "Từ chối");
                                }
                            }
                        }
                        break;
                    case NANG_CAP_TRANG_BI:
                        if (items.length != 2)
                        {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 1 trang bị và 1 loại đá phù hợp với trang bị để nâng cấp.", "Đóng");
                        }
                        else
                        {
                            Item iDBV = player.inventory.findItemBagByTemp(987);
                            if(iDBV == null){
                                iDBV = player.inventory.findItemBagByTemp(1143);
                            }
                            Item trangBi = null, daNangCap = null;
                            for (Item item : items)
                            {
                                if (isItemCanNangCap(item)) {
                                    trangBi = item;
                                }
                                if (isDaNangCap(item)) {
                                    daNangCap = item;
                                }
                            }
                            if (trangBi == null || daNangCap == null || !CanNangCap(trangBi, daNangCap)) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần 1 loại đá phù hợp với trang bị để nâng cấp.", "Đóng");
                            }
                            else {
                                int level = getParam(trangBi, 72);
                                if (level >= Setting.ITEM_LEVEL)
                                {
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Vật phẩm đã đạt cấp tối đa", "Đóng");
                                }
                                else
                                {
                                    setGemCombine(getGiaNangCap(level));
                                    setTiLeCombine((byte) getTiLeNangCap(level));
                                    String npcSay = "|2|Hiện tại " + trangBi.template.name + " [+" + level + "]\n|0|";
                                    for (ItemOption io : trangBi.itemOptions) {
                                        if (OptionCanUpgrade(io)) {
                                            npcSay += io.optionTemplate.name.replaceAll("#", io.param + "") + "\n";
                                        }
                                    }
                                    npcSay += "|2|Sau khi nâng cấp [+" + (level + 1) + "]\n";
                                    for (ItemOption io : trangBi.itemOptions) {
                                        if (OptionCanUpgrade(io)) {
                                            npcSay += "|1|" + io.optionTemplate.name.replaceAll("#", (io.param + paramNextLevel(trangBi , io)) + "") + "\n";
                                        }
                                    }
                                    for (ItemOption io : trangBi.itemOptions) {
                                        if (OptionCanUpgrade(io)) {
                                            if (level == 2 || level == 4 || level == 6) {
                                                npcSay += "|7|Thất bại: " + io.optionTemplate.name.replaceAll("#", io.param - (io.param * 10 / 100) + "") + "\n";
                                            }
                                        }
                                    }
                                    npcSay += "|2|Tỉ lệ thành công: " + this.tiLeCombine + "%\n";
                                    if (daNangCap.quantity < 5 + trangBi.template.level + level) {
                                        npcSay += "|7|Cần " + (5 + trangBi.template.level + level) + " " + daNangCap.template.name;
                                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + ((getGemCombine() + trangBi.template.level + level) - daNangCap.quantity) + " " + daNangCap.template.name, "Đóng");
                                    }
                                    else if (player.inventory.getGemAndRuby() >= this.getGemCombine()) {
                                        npcSay += "|2|Cần " + (5 + trangBi.template.level + level) + " " + daNangCap.template.name;
                                        if((iDBV != null && iDBV.quantity > 0) && (level == 2 || level == 4 || level == 6 || level == 7)){
                                            npcSay += "\n|7|Sử dụng đá bảo vệ sẽ không bị rớt cấp\n";
                                            Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Nâng cấp\n"+getGemCombine()+" ngọc","Nâng cấp\ndùng\nĐá bảo vệ", "Từ chối");
                                        }
                                        else{
                                            Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Nâng cấp\n"+getGemCombine()+" ngọc", "Từ chối");
                                        }
                                    }
                                    else {
                                        npcSay += "|2|Cần " + (5 + trangBi.template.level + level) + " " + daNangCap.template.name;
                                        Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (getGemCombine() - player.inventory.getGemAndRuby()) + " ngọc", "Đóng");
                                    }
                                }
                            }
                        }
                        break;
                    case NANG_CAP_BONG_TAI:
                        if (items.length != 2) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần bông tai Porata và mảnh vỡ bông tai để nâng cấp.", "Đóng");
                        } else {
                            Item trangBi = null, daNangCap = null;
                            for (Item item : items) {
                                if (item.template.id == 454) {
                                    trangBi = item;
                                }
                                if (item.template.id == 933) {
                                    daNangCap = item;
                                }
                            }
                            if (trangBi == null || daNangCap == null) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần bông tai Porata và mảnh vỡ bông tai để nâng cấp.", "Đóng");
                            } else if (player.inventory.findItem(player.inventory.itemsBag, 921) != null || player.inventory.findItem(player.inventory.itemsBox, 921) != null) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Bạn đã sở hữu bông tai Porata cấp 2 rồi.", "Đóng");
                            } else {
                                setGemCombine(200);
                                setGoldCombine(2000000000);
                                setTiLeCombine((byte) 50);
                                String npcSay = "|2|" + trangBi.template.name + " [+2]\n\n";
                                npcSay += "|2|Tỉ lệ thành công: " + this.tiLeCombine + "%\n";
                                if (getParam(daNangCap, 31) < 9999) {
                                    npcSay += "|7|Cần 9999 " + daNangCap.template.name + "\n";
                                    npcSay += "|2|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|2|Cần: " + Util.powerToString(getGoldCombine()) + " vàng\n";
                                    npcSay += "|7|Thất bại -99 " + daNangCap.template.name;
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (9999 - getParam(daNangCap, 31)) + " " + daNangCap.template.name, "Đóng");
                                } else if (player.inventory.getGemAndRuby() >= this.getGemCombine()) {
                                    npcSay += "|2|Cần 9999 " + daNangCap.template.name + "\n";
                                    npcSay += "|2|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|2|Cần: " + Util.powerToString(getGoldCombine()) + " vàng\n";
                                    npcSay += "|7|Thất bại -99 " + daNangCap.template.name;
                                    Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Nâng cấp\n " + Util.powerToString(getGoldCombine()) + " vàng\n" + +getGemCombine() + " ngọc\n ", "Từ chối");
                                } else if (player.inventory.getGemAndRuby() < this.getGemCombine()) {
                                    npcSay += "|2|Cần 9999 " + daNangCap.template.name + "\n";
                                    npcSay += "|7|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|2|Cần: " + Util.powerToString(getGoldCombine()) + " vàng\n";
                                    npcSay += "|7|Thất bại -99 " + daNangCap.template.name;
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (getGemCombine() - player.inventory.getGemAndRuby()) + " ngọc", "Đóng");
                                } else if (player.inventory.gold < this.getGoldCombine()) {
                                    npcSay += "|2|Cần 9999 " + daNangCap.template.name + "\n";
                                    npcSay += "|2|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|7|Cần: " + Util.powerToString(getGoldCombine()) + " vàng\n";
                                    npcSay += "|7|Thất bại -99 " + daNangCap.template.name;
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + Util.powerToString(getGoldCombine() - player.inventory.gold) + " vàng", "Đóng");
                                }
                            }
                        }
                        break;
                    case NANG_CHI_SO_BONG_TAI:
                        if (items.length != 3) {
                            Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần bông tai Porata cấp 2, mảnh hồn bông tai và đá xanh lam để nâng cấp.", "Đóng");
                        } else {
                            Item trangBi = null, honBongTai = null, daXanhLam = null;
                            for (Item item : items) {
                                if (item.template.id == 921) {
                                    trangBi = item;
                                }
                                if (item.template.id == 934) {
                                    honBongTai = item;
                                }
                                if (item.template.id == 935) {
                                    daXanhLam = item;
                                }
                            }
                            if (trangBi == null || daXanhLam == null || honBongTai == null) {
                                Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, "Cần bông tai Porata cấp 2, mảnh hồn bông tai và đá xanh lam để nâng cấp.", "Đóng");
                            } else {
                                setGemCombine(250);
                                setTiLeCombine((byte) 50);
                                String npcSay = "|2|" + trangBi.template.name + "\n\n";
                                npcSay += "|2|Tỉ lệ thành công: " + this.tiLeCombine + "%\n";
                                if (daXanhLam.quantity < 1) {
                                    npcSay += "|2|Cần 99 " + honBongTai.template.name + "\n";
                                    npcSay += "|7|Cần 1 " + daXanhLam.template.name + "\n";
                                    npcSay += "|2|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|1|+1 Chỉ số ngẫu nhiên";
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (1 - daXanhLam.quantity) + " " + daXanhLam.template.name, "Đóng");
                                } else if (honBongTai.quantity < 99) {
                                    npcSay += "|7|Cần 99 " + honBongTai.template.name + "\n";
                                    npcSay += "|2|Cần 1 " + daXanhLam.template.name + "\n";
                                    npcSay += "|2|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|1|+1 Chỉ số ngẫu nhiên";
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (99 - honBongTai.quantity) + " " + honBongTai.template.name, "Đóng");
                                } else if (player.inventory.getGemAndRuby() >= this.getGemCombine()) {
                                    npcSay += "|2|Cần 99 " + honBongTai.template.name + "\n";
                                    npcSay += "|2|Cần 1 " + daXanhLam.template.name + "\n";
                                    npcSay += "|2|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|1|+1 Chỉ số ngẫu nhiên";
                                    Npc.createMenuConMeo(player, NpcFactory.START_COMBINE, 1410, npcSay, "Nâng cấp\n " + getGemCombine() + " ngọc", "Từ chối");
                                } else if (player.inventory.getGemAndRuby() < this.getGemCombine()) {
                                    npcSay += "|2|Cần 99 " + honBongTai.template.name + "\n";
                                    npcSay += "|2|Cần 1 " + daXanhLam.template.name + "\n";
                                    npcSay += "|7|Cần: " + getGemCombine() + " ngọc\n";
                                    npcSay += "|1|+1 Chỉ số ngẫu nhiên";
                                    Npc.createMenuConMeo(player, NpcFactory.IGNORE_MENU, 1410, npcSay, "Còn thiếu\n" + (getGemCombine() - player.inventory.getGemAndRuby()) + " ngọc", "Đóng");
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                
            }
        }
    }

    public int getParam(Item item, int optionid) {
        for (ItemOption op : item.itemOptions) {
            if (op != null && op.optionTemplate.id == optionid) {
                return op.param;
            }
        }
        return 0;
    }

    public boolean OptionCanUpgrade(ItemOption io) {
        return io.optionTemplate.id == 23 || io.optionTemplate.id == 22 || io.optionTemplate.id == 47 || io.optionTemplate.id == 6 || io.optionTemplate.id == 27 || io.optionTemplate.id == 14 || io.optionTemplate.id == 0 || io.optionTemplate.id == 7 || io.optionTemplate.id == 28;
    }

    public int paramNextLevel(Item item, ItemOption io) {
        if (OptionCanUpgrade(io)) {
            int param = (int)(io.param * 0.1);
            return param <= 0 ? 1 : param;
        }
        return 0;
    }
    
    public int paramMutiNextLevel(Item item, ItemOption io, int count) {
        if (OptionCanUpgrade(io)) {
            int param = io.param;
            for(int i = 0; i < count; i++){
                param += (int)(param * 0.1);
            }
            return param - io.param;
        }
        return 0;
    }
    
    private boolean CanNangCap(Item item, Item da) {
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 72 && io.param >= Setting.ITEM_LEVEL) {
                return false;
            }
        }
        if (item.template.type == 4 && da.template.id == 220) {
            return true;
        } else if (item.template.type == 3 && da.template.id == 221) {
            return true;
        } else if (item.template.type == 2 && da.template.id == 224) {
            return true;
        } else if (item.template.type == 1 && da.template.id == 222) {
            return true;
        } else if (item.template.type == 0 && da.template.id == 223) {
            return true;
        }
        return false;
    }

    private int getGiaNangCap(int level) {
        switch (level) {
            case 0:
                return 5;
            case 1:
                return 15;
            case 2:
                return 25;
            case 3:
                return 35;
            case 4:
                return 45;
            case 5:
                return 55;
            case 6:
                return 65;
            case 7:
                return 75;
        }
        return 0;
    }

    private int getGiaDapDo(int star) {
        switch (star) {
            case 0:
                return 5000000;
            case 1:
                return 10000000;
            case 2:
                return 20000000;
            case 3:
                return 40000000;
            case 4:
                return 60000000;
            case 5:
                return 90000000;
            case 6:
                return 120000000;
            default:
                return 200000000;
        }
    }

    private int getTiLeDapDo(int star) {
        switch (star) {
            case 0:
                return 85;
            case 1:
                return 55;
            case 2:
                return 25;
            case 3:
                return 15;
            case 4:
                return 7;
            case 5:
                return 4;
            case 6:
                return 3;
            default:
                return 2;
        }
    }

    private int getTiLeNangCap(int level) {
        switch (level) {
            case 0:
                return 85;
            case 1:
                return 55;
            case 2:
                return 25;
            case 3:
                return 15;
            case 4:
                return 7;
            case 5:
                return 4;
            case 6:
                return 3;
            default:
                return 2;
        }
    }

    public byte starSlot(Item item) {
        byte star = 0;
        for (ItemOption io : item.itemOptions) {
            if (io != null && io.optionTemplate.id == 102) {
                star = (byte) io.param;
            }
        }
        return star;
    }

    private byte maxStarSlot(Item item) {
        byte star = 0;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 107) {
                star = (byte) io.param;
            }
        }
        return star;
    }

    private boolean isItemCanCombine(Item item) {
        return item.template.type == 0 || item.template.type == 1 || item.template.type == 2
                || item.template.type == 3 || item.template.type == 4 || item.template.type == 32;
    }

    public boolean isItemCanNangCap(Item item) {
        return item.template.type == 0 || item.template.type == 1 || item.template.type == 2 || item.template.type == 3 || item.template.type == 4;
    }

    private boolean isDaNangCap(Item item) {
        return item.template.type == 14;
    }

    private boolean isDaPhaLe(Item item) {
        return item.template.type == 30 || (item.template.id >= 14 && item.template.id <= 20);
    }
    
    private boolean isCT(Item item) {
        return item.template.type == 1 || (item.template.id >= 1071 && item.template.id <= 1073);
    }
    
    private boolean isCTVIP(Item item) {
        return item.template.type == 1 || (item.template.id >= 1084 && item.template.id <= 1086);
    }
    
    private boolean isDaNC(Item item) {
        return item.template.id >= 1074 && item.template.id <= 1078;
    }
    
    private boolean isDaMM(Item item) {
        return item.template.id >= 1079 && item.template.id <= 1083;
    }

    public int getParamDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).param;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 5;
            case 19:
                return 5;
            case 18:
                return 5;
            case 17:
                return 5;
            case 16:
                return 3;
            case 15:
                return 2;
            case 14:
                return 2;
            default:
                return -1;
        }
    }

    public int getOptionDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).optionTemplate.id;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 77;
            case 19:
                return 103;
            case 18:
                return 80;
            case 17:
                return 81;
            case 16:
                return 50;
            case 15:
                return 94;
            case 14:
                return 108;
            default:
                return -1;
        }
    }
    
    public boolean addParam(Item item, int optionId, int param) {
        try{
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == optionId) {
                    io.param += param;
                    return true;
                }
            }
            item.itemOptions.add(new ItemOption(optionId, param));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean removeCap(Item item) {
        for (int i = 0; i < item.itemOptions.size(); i++) {
            if (item.itemOptions.get(i).optionTemplate.id == 72) {
                if (item.itemOptions.get(i).param == 2 || item.itemOptions.get(i).param == 4 || item.itemOptions.get(i).param == 6) {
                    if(addParam(item, item.itemOptions.get(i).optionTemplate.id, -1)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean removeParam(Item item){
        int index = 0;
        int level = getParam(item, 72);
        if (level == 2 || level == 4 || level == 6) {
            for (int i = 0; i < item.itemOptions.size(); i++) {
                if (OptionCanUpgrade(item.itemOptions.get(i))) {
                    if(addParam(item, item.itemOptions.get(i).optionTemplate.id, -(item.itemOptions.get(i).param * 10 / 100))){
                        index = 1;
                    }
                }
            }
        }
        return index == 1;
    }
    
    public boolean useDBV(Player pl)
    {
        Item iDBV = pl.inventory.findItemBagByTemp(987);
        if(iDBV == null){
            iDBV = pl.inventory.findItemBagByTemp(1143);
        }
        if(pl.getSelectMenu() == 1 && player.combine.getTypeCombine() == Combine.NANG_CAP_TRANG_BI && iDBV != null && iDBV.quantity > 0)
        {
            return true;
        }
        return false;
    }
}
