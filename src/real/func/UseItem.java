package real.func;

import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemTime;
import real.map.ItemMap;
import real.map.Zone;
import real.map.MapManager;
import real.npc.IAtionNpc;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.pet.NewPet;
import real.player.Player;
import real.player.PlayerManger;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;
import service.DAOS.PetDAO;
import service.Setting;

public class UseItem {
    
    private static UseItem instance;

    private UseItem() {

    }
  
    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void useItem(Player pl, Item item, int indexBag) {
  
        if (pl.point.getHP() <= 0)
        {
            return;
        }
        if(!PowerCanUse(pl, item))
        {
            Service.gI().sendThongBao(pl, "Trang bị còn thiếu " + Util.getMoneys(item.template.strRequire - pl.point.getPower()) + " sức mạnh");
            return;
        }
        switch (item.template.id) {
            case 1194:
                if (pl.getBagNull() < 1) {
                    Service.gI().sendThongBao(pl, "Hành trang không đủ Ô trống");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                Item LIXI2023 = ItemData.gI().get_item(1184);
                pl.combine.doCombine(6, null, null, item.template.iconID, LIXI2023.template.iconID);
                LIXI2023.itemOptions.clear();
                LIXI2023.itemOptions.add(new ItemOption(30, 0));
                LIXI2023.quantity = Util.nextInt(100, 1000);
                pl.inventory.addItemBag(LIXI2023);
                break;
            case 1206:// xí muội hóa đào
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(item.template.iconID, 600);
                Service.gI().point(pl);
                break;
            case 1207:// xí muội hóa đào
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(item.template.iconID, 600);
                Service.gI().point(pl);
                break;
            case 1198:
                if (pl.getBagNull() < 1) {
                    Service.gI().sendThongBao(pl, "Hành trang không đủ ô trống");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                int[] QUA = {1000, 1150, 1154, 1199, 1208, 99999, 88888};
                int QUAClaim = QUA[Util.nextInt(0, QUA.length - 1)];
                if(QUAClaim == 99999){
                    int[] itNon = new int[]{
                        1209,
                        1210,
                        1211
                    };
                    QUAClaim = itNon[pl.gender];
                }
                else if(QUAClaim == 88888){
                    int[] Kimono = new int[]{
                        1185,
                        1186,
                        1187
                    };
                    QUAClaim = Kimono[pl.gender];
                }
                Item itemTET2 = ItemData.gI().get_item(QUAClaim);
                pl.combine.doCombine(6, null, null, item.template.iconID, itemTET2.template.iconID);
                if(QUAClaim == 1154) //ván
                {
                    itemTET2.itemOptions.clear();
                    itemTET2.itemOptions.add(new ItemOption(84, 0));
                    itemTET2.itemOptions.add(new ItemOption(148, 25));
                    int[] op = new int[]{ 50, 77, 103, 14, 19, 94};
                    itemTET2.itemOptions.add(new ItemOption(op[Util.nextInt(0, op.length - 1)], 5));
                }
                else if(QUAClaim == 1000 || QUAClaim == 1150 || QUAClaim == 1208) // đeo lưng
                {
                    itemTET2.itemOptions.clear();
                    itemTET2.itemOptions.add(new ItemOption(77, 12));
                    itemTET2.itemOptions.add(new ItemOption(103, 12));
                    itemTET2.itemOptions.add(new ItemOption(50, 12));
                    int[] op = new int[]{ 50, 77, 103, 14, 19, 94};
                    itemTET2.itemOptions.add(new ItemOption(op[Util.nextInt(0, op.length - 1)], 5));
                }
                else if(QUAClaim == 1185 || QUAClaim == 1186 || QUAClaim == 1187)
                {
                    itemTET2.itemOptions.clear();
                    itemTET2.itemOptions.add(new ItemOption(50, 24));
                    itemTET2.itemOptions.add(new ItemOption(77, 24));
                    itemTET2.itemOptions.add(new ItemOption(103, 24));
                    itemTET2.itemOptions.add(new ItemOption(94, 24));
                    itemTET2.itemOptions.add(new ItemOption(80, 24));
                }
                else if(QUAClaim == 1199)
                {
                    itemTET2.itemOptions.clear();
                    int[] op = new int[]{77, 103, 108, 14, 19, 94};
                    itemTET2.itemOptions.add(new ItemOption(50, 20));
                    itemTET2.itemOptions.add(new ItemOption(op[Util.nextInt(0, op.length - 1)], 5));
                }
                else
                {
                    itemTET2.itemOptions.clear();
                    itemTET2.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                    itemTET2.itemOptions.add(new ItemOption(80, Util.nextInt(16, 50)));
                    itemTET2.itemOptions.add(new ItemOption(50, 21));
                }
                int[] HSD2 = new int[]{1, 5, 7, 14};
                if(Util.isTrue(99))
                {
                    itemTET2.itemOptions.add(new ItemOption(93, HSD2[Util.nextInt(0, HSD2.length - 1)]));
                }
                pl.inventory.addItemBag(itemTET2);
                break;
            case 627:
                if (pl.getBagNull() < 1) {
                    Service.gI().sendThongBao(pl, "Hành trang không đủ ô trống");
                    break;
                }
                int[] itemTT = {739,742,730,731,732};
                int itemClaimTT = itemTT[Util.nextInt(0, 4)];
                Item tanthu = ItemData.gI().get_item(itemClaimTT);
                pl.combine.doCombine(6, null, null, (short)item.template.iconID, tanthu.template.iconID);
                tanthu.itemOptions.clear();
                tanthu.itemOptions.add(new ItemOption(77, 30));
                tanthu.itemOptions.add(new ItemOption(103, 30));
                tanthu.itemOptions.add(new ItemOption(50, 30));
                tanthu.itemOptions.add(new ItemOption(88, 30));
                if(tanthu.template.id == 730 || tanthu.template.id == 731 || tanthu.template.id == 732){
                    tanthu.itemOptions.add(new ItemOption(110, 0));
                }
                tanthu.itemOptions.add(new ItemOption(95, 20));
                tanthu.itemOptions.add(new ItemOption(93, 7));
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.inventory.addItemBag(tanthu);
                Service.gI().sendThongBao(pl, "Bạn đã nhận được " + tanthu.template.name);
                pl.inventory.sendItemBags();
                pl.inventory.sortItem(pl.inventory.itemsBag);
                break;
            case 648:
                if (pl.getBagNull() <= 2) {
                    Service.gI().sendThongBao(pl, "Hành trang không đủ ô trống");
                    break;
                }
                int itemID = 533;
                int Quantity = Util.nextInt(1, 2000);
                int[] itemS = new int[]{190, 77, 861};
                int[] itemNR = new int[]{14, 15, 16, 17, 18, 19, 20};
                int[] itemNoel = new int[]{745, 823, 822};
                if(Util.isTrue(5)){
                    itemID = itemNoel[Util.nextInt(0, itemNoel.length - 1)];
                }
                else if(Util.isTrue(7)){
                    itemID = itemNR[Util.nextInt(0, itemNR.length - 1)];
                }
                else{
                    itemID = itemS[Util.nextInt(0, itemS.length - 1)];
                }
                Item keo = ItemData.gI().get_item(533);
                keo.quantity = Util.nextInt(1, 5);
                keo.itemOptions.clear();
                keo.itemOptions.add(new ItemOption(174, 2022));
                Item noel = ItemData.gI().get_item(itemID);
                if(noel.template.id == 190 || noel.template.id == 77 || noel.template.id == 861){
                    if(noel.template.id == 77){
                        pl.inventory.gem += Util.nextInt(1, 200);
                    }
                    else if(noel.template.id == 861) {
                        pl.inventory.ruby += Util.nextInt(1, 1000);
                    }
                    else{
                        pl.inventory.gold += Util.nextInt(1000, 10000000);
                    }
                }
                else if(noel.template.id == 745 || noel.template.id == 822 || noel.template.id == 823){
                    double rd = Util.nextdouble(100);
                    noel.itemOptions.clear();
                    noel.itemOptions.add(new ItemOption(77,Util.nextInt(5,22)));
                    noel.itemOptions.add(new ItemOption(103,Util.nextInt(5,22)));
                    noel.itemOptions.add(new ItemOption(50,Util.nextInt(5,22)));
                    if(noel.template.id == 745){
                        noel.itemOptions.add(new ItemOption(167, 0));
                    }
                    if(rd > 0.4){
                        noel.itemOptions.add(new ItemOption(93, Util.nextInt(1,7)));
                    }
                    pl.inventory.addItemBag(noel);
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.inventory.addItemBag(keo);
                Service.gI().sendMoney(pl);
                break;
            case 570:
                         
////                if(Util.isTrue(5)){
////                    idc = 988;
////                }else if(Util.isTrue(7)){
////                    idc = 1055;
////                }else if(Util.isTrue(10)){
////                    idc = itemChest[Util.nextInt(0,itemChest.length - 1)];
////                }else if (Util.isTrue(99)){
////                    idc = trangbi[Util.nextInt(0,trangbi.length - 1)];
////                }else{
//                   int idc = trangbilv_3[Util.nextInt(0,trangbilv_3.length - 1)];
////                }

                RuongBiAn.gI().openMenuRuong_1(pl);
                pl.DHVT_23.isDrop = false;
                pl.inventory.subQuantityItemsBag(item,1);
                
                
                
//                Item itc = ItemData.gI().get_item(idc);
//                double randomRate = Util.RandomNumber(0.0, 100.0);
//                if(itc.template.id == 1055){
//                    itc.itemOptions.clear();
//                    itc.itemOptions.add(new ItemOption(77,Util.nextInt(2,22)));
//                    itc.itemOptions.add(new ItemOption(103,Util.nextInt(2,22)));
//                    itc.itemOptions.add(new ItemOption(50,Util.nextInt(2,22)));
//                    itc.itemOptions.add(new ItemOption(5,Util.nextInt(2,22)));
//                    itc.itemOptions.add(new ItemOption(14,Util.nextInt(5,12)));
//                    if(randomRate>=0.2){
//                        itc.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
//                    }   
//                }
//                else if(itc.template.id == 457 ||itc.template.id == 220 || itc.template.id == 221|| itc.template.id == 223|| itc.template.id == 224){
//                    itc.quantity = Util.nextInt(1,3);
//                }
//                pl.inventory.subQuantityItemsBag(item, 1);
//                pl.inventory.subQuantityItemsBag(pl.inventory.findItemBagByTemp(1071), 1);
//                pl.inventory.addItemBag(itc);
                break;
            case 992:
                if(pl.taskId < 31 && pl.taskIndex < 1)
                {
                    Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    return;
                }
                if(pl.taskId == 31 && pl.taskIndex == 1)
                {
                    Service.gI().send_task_next(pl);
                }
                pl.type = 1;
                pl.maxTime = 5;
                Service.gI().Transport(pl, 1);
                Service.gI().clearMap(pl);
                break;
            case 818:
                pl.inventory.subQuantityItemsBag(item, 1);
                int[] itemHl = {16,17,18,19,739,742,730,731,732,988};
                int itemClaimHl = itemHl[Util.nextInt(0, Util.isTrue(10) ? 9 : 3)];
                short iconitemHl = ItemData.gI().getIcon((short) itemClaimHl);
                pl.combine.doCombine(6, null, null, (short) 7356, iconitemHl);
                Item itHl = ItemData.gI().get_item(itemClaimHl);
                if(itHl.template.id == 739 || itHl.template.id == 742 || itHl.template.id == 730 || itHl.template.id == 731 || itHl.template.id == 732){
                    itHl.itemOptions.clear();
                    itHl.itemOptions.add(new ItemOption(77, Util.nextInt(5, 30)));
                    itHl.itemOptions.add(new ItemOption(103, Util.nextInt(5, 30)));
                    itHl.itemOptions.add(new ItemOption(50, Util.nextInt(5, 30)));
                    itHl.itemOptions.add(new ItemOption(88, Util.nextInt(5, 30)));
                    if(itHl.template.id == 730 || itHl.template.id == 731 || itHl.template.id == 732){
                        itHl.itemOptions.add(new ItemOption(110, 0));
                    }
                    if(Util.isTrue(5)){
                        itHl.itemOptions.add(new ItemOption(95, Util.nextInt(5, 116)));
                    }else if(Util.isTrue(10)){
                        itHl.itemOptions.add(new ItemOption(5, Util.nextInt(5, 30)));
                    }
                    if(Util.isTrue(98)){
                        itHl.itemOptions.add(new ItemOption(93, Util.nextInt(1,10)));
                    }
                }else{
                    itHl.quantity = Util.nextInt(1,3);
                }
                pl.inventory.addItemBag(itHl);
                break;
            case 880:    
            case 881:
            case 882:
                if (!pl.itemTime.ExitsItemTiem(8060) && !pl.itemTime.ExitsItemTiem(8061) && !pl.itemTime.ExitsItemTiem(8062) && !pl.itemTime.ExitsItemTiem(6327) && !pl.itemTime.ExitsItemTiem(6328)) {
                    pl.inventory.subQuantityItemsBag(item, 1);
                    pl.itemTime.addItemTime(ItemData.gI().getIcon(item.template.id), 600);
                    Service.gI().point(pl);
                }
                else
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                }
                break;    
            case 663:
            case 664:
            case 665:
            case 666:
            case 667:
                if (!pl.itemTime.ExitsItemTiem(6324) && !pl.itemTime.ExitsItemTiem(6325) && !pl.itemTime.ExitsItemTiem(6326) && !pl.itemTime.ExitsItemTiem(6327) && !pl.itemTime.ExitsItemTiem(6328)) {
                    pl.inventory.subQuantityItemsBag(item, 1);
                    pl.itemTime.addItemTime(ItemData.gI().getIcon(item.template.id), 600);
                    Service.gI().point(pl);
                }
                else
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                }
                break;
            case 361:
                pl.inventory.subQuantityItemsBag(item, 1);
                NRNM map1 = NRNM.gI().findNRNM(353);
                NRNM map2 = NRNM.gI().findNRNM(354);
                NRNM map3 = NRNM.gI().findNRNM(355);
                NRNM map4 = NRNM.gI().findNRNM(356);
                NRNM map5 = NRNM.gI().findNRNM(357);
                NRNM map6 = NRNM.gI().findNRNM(358);
                NRNM map7 = NRNM.gI().findNRNM(359);
                String npcSay = ""
                        + "1 Sao: " + (pl.zone.map.name != map1.zone.map.name ? map1.zone.map.name : "đây kv " + map1.zone.zoneId) + (map1.player != null ? " (" + map1.player.name + ")" : " (?)") + "\n"
                        + "2 Sao: " + (pl.zone.map.name != map2.zone.map.name ? map2.zone.map.name : "đây kv " + map2.zone.zoneId) + (map2.player != null ? " (" + map2.player.name + ")" : " (?)") + "\n"
                        + "3 Sao: " + (pl.zone.map.name != map3.zone.map.name ? map3.zone.map.name : "đây kv " + map3.zone.zoneId) + (map3.player != null ? " (" + map3.player.name + ")" : " (?)") + "\n"
                        + "4 Sao: " + (pl.zone.map.name != map4.zone.map.name ? map4.zone.map.name : "đây kv " + map4.zone.zoneId) + (map4.player != null ? " (" + map4.player.name + ")" : " (?)") + "\n"
                        + "5 Sao: " + (pl.zone.map.name != map5.zone.map.name ? map5.zone.map.name : "đây kv " + map5.zone.zoneId) + (map5.player != null ? " (" + map5.player.name + ")" : " (?)") + "\n"
                        + "6 Sao: " + (pl.zone.map.name != map6.zone.map.name ? map6.zone.map.name : "đây kv " + map6.zone.zoneId) + (map6.player != null ? " (" + map6.player.name + ")" : " (?)") + "\n"
                        + "7 Sao: " + (pl.zone.map.name != map7.zone.map.name ? map7.zone.map.name : "đây kv " + map7.zone.zoneId) + (map7.player != null ? " (" + map7.player.name + ")" : " (?)") + "\n";
                Npc.createMenuConMeo(pl, NpcFactory.NGOC_RONG_NAMEC, 2294, npcSay, "Kết thúc");
                break;
            case 379:
                if (!pl.itemTime.ExitsItemTiem(2758))
                {
                    pl.inventory.subQuantityItemsBag(item, 1);
                    pl.itemTime.addItemTime(2758, 1800);
                    Service.gI().sendItemTime(pl, 2758, 1800);
                }
                break;
            case 758:
                if (pl.getBagNull() < 1) {
                    Service.gI().sendThongBao(pl, "Hành trang không đủ ô trống");
                    break;
                }
//                pl.pointTET++;
                pl.lastTimeEvent = Util.currentTimeSec();
                pl.inventory.subQuantityItemsBag(item, 1);
                    int[] vatpham = {942, 849, 852, 1196, 1197, 1212, 806, 819, 862, 863, 864, 99999};
                int vatphamClaim = vatpham[Util.nextInt(0, vatpham.length - 1)];
                if(vatphamClaim == 99999){
                    int[] itNon = new int[]{
                        754,
                        755,
                        756
                    };
                    vatphamClaim = itNon[pl.gender];
                }
                short iconVatPham = ItemData.gI().getIcon((short) vatphamClaim);
                pl.combine.doCombine(6, null, null, item.template.iconID, iconVatPham);
                Item itemTET = ItemData.gI().get_item(vatphamClaim);
                if(vatphamClaim == 849) //ván
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(84, 0));
                    itemTET.itemOptions.add(new ItemOption(148, 25));
                    int[] op = new int[]{ 50, 77, 103, 14, 19, 94};
                    itemTET.itemOptions.add(new ItemOption(op[Util.nextInt(0, op.length - 1)], 5));
                }
                else if(vatphamClaim == 852 || vatphamClaim == 1196 || vatphamClaim == 1197) //cây trúc
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(77, 12));
                    itemTET.itemOptions.add(new ItemOption(103, 12));
                    itemTET.itemOptions.add(new ItemOption(50, 12));
                }
                else if(vatphamClaim == 806)// mèo dôre
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(77, 24));
                    itemTET.itemOptions.add(new ItemOption(94, 24));
                    itemTET.itemOptions.add(new ItemOption(176, 0));
                }
                else if(vatphamClaim == 819)//xuka
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(117, 10));
                    itemTET.itemOptions.add(new ItemOption(77, 24));
                    itemTET.itemOptions.add(new ItemOption(176, 0));
                }
                else if(vatphamClaim == 862)
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(77, 24));
                    itemTET.itemOptions.add(new ItemOption(108, 5));
                    itemTET.itemOptions.add(new ItemOption(176, 0));
                }
                else if(vatphamClaim == 863)
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(77, 24));
                    itemTET.itemOptions.add(new ItemOption(103, 24));
                    itemTET.itemOptions.add(new ItemOption(176, 0));
                }
                else if(vatphamClaim == 864)
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(50, 30));
                    itemTET.itemOptions.add(new ItemOption(77, 10));
                    itemTET.itemOptions.add(new ItemOption(176, 0));
                }
                else if(vatphamClaim == 942)
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(77, 12));
                    int[] op = new int[]{ 50, 103, 14, 19, 94};
                    itemTET.itemOptions.add(new ItemOption(op[Util.nextInt(0, op.length - 1)], 5));
                }
                else if(vatphamClaim == 1212)
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(50, 30));
                    itemTET.itemOptions.add(new ItemOption(77, 30));
                    itemTET.itemOptions.add(new ItemOption(103, 30));
                    itemTET.itemOptions.add(new ItemOption(94, 20));
                    itemTET.itemOptions.add(new ItemOption(108, 20));
                    itemTET.itemOptions.add(new ItemOption(80, 20));
                }
                else
                {
                    itemTET.itemOptions.clear();
                    itemTET.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                    itemTET.itemOptions.add(new ItemOption(80, Util.nextInt(16, 50)));
                    itemTET.itemOptions.add(new ItemOption(50, 21));
                }
                int[] HSD = new int[]{1, 5, 7, 14};
                if(Util.isTrue(97)){
                    itemTET.itemOptions.add(new ItemOption(93, HSD[Util.nextInt(0, HSD.length - 1)]));
                }
                pl.inventory.addItemBag(itemTET);
                break;
            case 380:
                pl.inventory.subQuantityItemsBag(item, 1);
                int[] itemid = {382, 383, 384, 381, 385, 190};
                int itemClaim = itemid[Util.nextInt(0, 5)];
                short iconitem = ItemData.gI().getIcon((short) itemClaim);
                pl.combine.doCombine(6, null, null, item.template.iconID, iconitem);
                if (itemClaim != 190) {
                    Item it = ItemData.gI().get_item(itemClaim);
                    pl.inventory.addItemBag(it);
                } else {
                    pl.inventory.gold += Util.nextInt(1000, 10000000);
                    Service.gI().sendMoney(pl);
                }
                break;
            case 752:
                if(pl.itemTime.ExitsItemTiem(7079) || pl.itemTime.ExitsItemTiem(7080))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(item.template.iconID, 3600);
                Service.gI().point(pl);
                break;
            case 753:
                if(pl.itemTime.ExitsItemTiem(7079) || pl.itemTime.ExitsItemTiem(7080))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(item.template.iconID, 3600);
                Service.gI().point(pl);
                break;
            case 382: // bổ huyết
                if(pl.itemTime.ExitsItemTiem(10714))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(2755, 600);
                Service.gI().point(pl);
                break;
            case 384: // giáp xên
                if(pl.itemTime.ExitsItemTiem(10712))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(2757, 600);
                Service.gI().point(pl);
                break;
            case 383: // bổ khí
                if(pl.itemTime.ExitsItemTiem(10715))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(2756, 600);
                Service.gI().point(pl);
                break;
            case 381: // cuồng nộ
                if(pl.itemTime.ExitsItemTiem(10716))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(2754, 600);
                Service.gI().point(pl);
                break;
             
            case 1150: // cuồng nộ 2
                if(pl.itemTime.ExitsItemTiem(2754))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(10716, 600);
                Service.gI().point(pl);
                break;   
            case 1151: // bổ khí 2
                if(pl.itemTime.ExitsItemTiem(2756))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(10715, 600);
                Service.gI().point(pl);
                break;
            case 1152: // bổ huyết 2
                if(pl.itemTime.ExitsItemTiem(2755))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(10714, 600);
                Service.gI().point(pl);
                break;
            case 1153: // giáp xên 2
                if(pl.itemTime.ExitsItemTiem(2757))
                {
                    Service.gI().sendThongBao(pl, "Chỉ dùng được 1 cái");
                    break;
                }
                pl.inventory.subQuantityItemsBag(item, 1);
                pl.itemTime.addItemTime(10712, 600);
                Service.gI().point(pl);
                break;      
                
                
            case 385: // ẩn danh
                if (!pl.itemTime.ExitsItemTiem(2760) || pl.itemTime.GetItemTiem(2760).time <= 0) {
                    pl.inventory.subQuantityItemsBag(item, 1);
                    pl.itemTime.addItemTime(2760, 600);
                } else if (pl.itemTime.GetItemTiem(2760).time + 600 > 1800) {
                    pl.inventory.subQuantityItemsBag(item, 1);
                    pl.itemTime.addItemTime(2760, 1800);
                } else if (pl.itemTime.GetItemTiem(2760).time < 1800) {
                    int time = pl.itemTime.GetItemTiem(2760).time + 600;
                    pl.inventory.subQuantityItemsBag(item, 1);
                    pl.itemTime.addItemTime(2760, time);
                }
                break;
            case 521://tdlt
                useTDLT(pl, item);
                break;
            case 921:// bông tai c2
                UseItem.gI().usePorata(pl, 2);
                break;
            case 454: //bông tai
                UseItem.gI().usePorata(pl, 1);
                break;
            case 193: //gói 10 viên capsule
                pl.inventory.subQuantityItemsBag(item, 1);
            case 194: //capsule đặc biệt
                openCapsuleUI(pl);
                break;
            case 402: //sách nâng chiêu 1 đệ tử
            case 403: //sách nâng chiêu 2 đệ tử
            case 404: //sách nâng chiêu 3 đệ tử
            case 759: //sách nâng chiêu 4 đệ tử
                upSkillPet(pl, item);
                break;
            case 400:
                Service.gI().sendThongBao(pl, "Bạn muốn đặt tên cho đệ tử!Hãy chat 'ten con la abc' để đặt tên đệ tử là 'abc'");
                break;
            case 401:
                PetDAO.ChangePet(pl);
                break;
            case 342:
            case 343:
            case 344:
            case 345:
                if(pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).toList().size() < 5)
                {
                    Service.gI().DropVeTinh(pl,item , pl.zone,pl.x,pl.y,30);
                    pl.inventory.subQuantityItemsBag(item, 1);
                    
                }
                else
                {
                    Service.gI().sendThongBao(pl, "Khu vực đã đặt tối đa 5 vệ tinh");
                }
                break;
            default:
                switch (item.template.type) {
                    case 27: //Các loại pet hỗ trợ
                        switch (item.template.id) {
                            case 942:
                                pl.inventory.itemBagToBody(indexBag);
                                PetDAO.Pet2(pl,966, 967, 968);
                                Service.gI().point(pl);
                                break;
                            case 943:
                                pl.inventory.itemBagToBody(indexBag);
                                PetDAO.Pet2(pl,969, 970, 971);
                                Service.gI().point(pl);
                                break;
                            case 944:
                                pl.inventory.itemBagToBody(indexBag);
                                PetDAO.Pet2(pl,972, 973, 974);
                                Service.gI().point(pl);
                                break;
                            case 1055:
                            case 1117:
                                pl.inventory.itemBagToBody(indexBag);
                                PetDAO.Pet2(pl,1155, 1156, 1157);
                                Service.gI().point(pl);
                                break;
                            case 1199:
                                pl.inventory.itemBagToBody(indexBag);
                                PetDAO.Pet2(pl,1186, 1187, 1188);
                                Service.gI().point(pl);
                                break;
                            case 1213:
                                pl.inventory.itemBagToBody(indexBag);
                                PetDAO.Pet2(pl, 1204, 1205, 1206);
                                Service.gI().point(pl);
                                break;
                            default:
                                break;
                        }
                        break;
                    case 24: // Các loại ván
                    case 23: // Các loại ván
                        if(pl.session.get_version() < 222)
                        {
                            Service.gI().sendThongBao(pl, "Vui lòng update lên phiên bản v2.2.2 trở lên!");
                        }
                        pl.inventory.itemBagToBody(indexBag);
                        break;
                    case 6:
                        Eat_Pean(pl, item);
                        break;
                    case 11: //lồng đen các loại
                        pl.inventory.itemBagToBody(indexBag);
                        break;
                    case 7: //sách học, nâng skill
                        learnSkill(pl, item);
                        break;
                    case 12: //ngọc rồng các loại
                        controllerCallRongThan(pl, item);
                        break;
                }
        }
        pl.inventory.sendItemBags();
    }

    int get_point_magic_tree(int id) {
        switch (id) {
            case 13:
                return 500;
            case 60:
                return 1000;
            case 61:
                return 2000;
            case 62:
                return 4000;
            case 63:
                return 8000;
            case 64:
                return 16000;
            case 65:
                return 32000;
            case 352:
                return 64000;
            case 523:
                return 128000;
            case 595:
                return 256000;
            default:
                return 0;
        }
    }

    public void Eat_Pean(Player pl, Item item) {
        if (!Util.canDoWithTime(pl.delayUsePeans, 9900)) {
            return;
        }
        if (pl.point.hp != pl.point.getHPFull() || pl.point.mp != pl.point.getMPFull() || pl.pet != null && (pl.pet.point.hp != pl.pet.point.getHPFull() || pl.pet.point.mp != pl.pet.point.getMPFull() || pl.pet.point.stamina != 1100)) {
            pl.inventory.subQuantityItemsBag(item, 1);
            int p = get_point_magic_tree(item.template.id);
            pl.hoiphuc(p, p);
            if (pl.pet != null) {
                pl.pet.hoiphuc(p, p);
                pl.pet.point.stamina += p;
                if (pl.pet.point.stamina > Setting.MAX_STAMINA_FOR_PET) {
                    pl.pet.point.stamina = Setting.MAX_STAMINA_FOR_PET;
                }
                Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
            }
            pl.delayUsePeans = System.currentTimeMillis();
            pl.inventory.sendItemBags();
        } else {
            Service.gI().sendThongBao(pl, "Không thể dùng đậu khi HP và KI đạt 100%");
        }
    }
    
//    private void controllerRuongBiAn(Player pl, Item item){
//        int tempId = item.template.id;
//        if (tempId == 570){
//            RuongBiAn.gI().openMenuRuongBiAn(pl);
//            
//        }
//    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    Npc.createMenuConMeo(pl, NpcFactory.TUTORIAL_SUMMON_DRAGON, -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
        if (tempId >= SummonDragon.RONG_BANG_1_SAO && tempId <= SummonDragon.RONG_BANG_7_SAO) {
            switch (tempId) {
                case SummonDragon.RONG_BANG_1_SAO:
                case SummonDragon.RONG_BANG_2_SAO:
                case SummonDragon.RONG_BANG_3_SAO:
                case SummonDragon.RONG_BANG_4_SAO:
                case SummonDragon.RONG_BANG_5_SAO:
                case SummonDragon.RONG_BANG_6_SAO:
                case SummonDragon.RONG_BANG_7_SAO:
                    SummonDragon.gI().openMenuShenronBang(pl);
                    break;
                default:
                    break;
            }
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg = null;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill == null) {
                    if (pl.point.getPower() < item.template.strRequire)
                    {
                        Service.gI().sendThongBao(pl, "Bạn mới " + pl.point.getPower() + " sức mạnh cần - " + item.template.strRequire);
                    }
                    else if (level == 1)
                    {
                        curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level,0);
                        pl.playerSkill.skills.add(curSkill);
                        pl.inventory.subQuantityItemsBag(item, 1);
                        msg = Service.gI().messageSubCommand((byte) 23);
                        msg.writer().writeShort(curSkill.skillId);
                        pl.sendMessage(msg);
                        msg.cleanup();
                    }
                    else
                    {
                        Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level,0);
                        Service.gI().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                    }
                }
                else if (curSkill.point + 1 == level)
                {
                    curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level,0);
                    SkillUtil.setSkill(pl, curSkill);
                    pl.inventory.subQuantityItemsBag(item, 1);
                    msg = Service.gI().messageSubCommand((byte) 62);
                    msg.writer().writeShort(curSkill.skillId);
                    pl.sendMessage(msg);
                    msg.cleanup();
                }
                else
                {
                    Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                }
                pl.inventory.sendItemBags();
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void useTDLT(Player pl, Item item) {
        Message msg = null;
        try {
            boolean isTdlt = pl.itemTime.ExitsItemTiem(item.template.iconID);
            int time;
            msg = new Message(-116);
            msg.writer().writeByte(isTdlt ? 0 : 1);
            pl.sendMessage(msg);

            if (!isTdlt) {
                if (item.GetItemOption(1).param < 533) {
                    time = 60 * item.GetItemOption(1).param;
                } else {
                    time = 60 * 533;
                }
                item.GetItemOption(1).param -= time / 60;
                pl.itemTime.addItemTime(4387, time);
            }
            else {
                ItemTime it = pl.itemTime.GetItemTiem(4387);
                item.GetItemOption(1).param += (short) (it.time / 60);
                pl.itemTime.removeItemTime(4387);
            }
            pl.inventory.sendItemBags();
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void usePorata(Player pl, int type) {
        if (pl.pet == null || pl.typeFusion == 4)
        {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
        else if (!Util.canDoWithTime(pl.lastTimeFusion , 10000) && pl.typeFusion == 0) {
            Service.gI().sendThongBao(pl, "Vui lòng đợi " + (10 - ((System.currentTimeMillis() - pl.lastTimeFusion) / 1000)) + " giây");
        }
        else if (pl.typeFusion == 0) {
            pl.pet.fusion(type);
        }
        else {
            pl.lastTimeFusion = System.currentTimeMillis();
            pl.pet.unFusion();
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.mapCapsule = MapManager.gI().getMapCapsule(pl);
        Message msg = null;
        try {
            msg = new Message(-91);
            msg.writer().writeByte(pl.mapCapsule.size());
            for (int i = 0; i < pl.mapCapsule.size(); i++) {
                Zone map = pl.mapCapsule.get(i);
                if (i == 0 && pl.zoneBeforeCapsule != null && map == pl.zoneBeforeCapsule) {
                    msg.writer().writeUTF("Về chỗ cũ: " + map.map.name);
                } else if (map.map.name.equals("Nhà Broly") || map.map.name.equals("Nhà Gôhan") || map.map.name.equals("Nhà Moori")) {
                    msg.writer().writeUTF("Về nhà");
                } else {
                    msg.writer().writeUTF(map.map.name);
                }
                msg.writer().writeUTF(map.map.planetName);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void choseMapCapsule(Player pl, int index) {
        int zoneID = -1;
        int cx = -1;
        Zone zone = pl.mapCapsule.get(index);
        if(zone == pl.zoneBeforeCapsule){
            zoneID = pl.zoneBeforeCapsule.zoneId;
            cx = pl.cxBeforeCapsule;
        }
        if (!MapManager.gI().isMapOffline(pl.zone.map.id) && !zone.isNRSD() && !pl.zone.isPhuBan() && zone != pl.zoneBeforeCapsule) {
            pl.zoneBeforeCapsule = pl.zone;
            pl.cxBeforeCapsule = pl.x;
        } else if (!zone.isNRSD() && !MapManager.gI().isMapOffline(pl.zone.map.id)) {
            pl.zoneBeforeCapsule = null;
            pl.cxBeforeCapsule = -1;
        }
        if(zone.isNRSD())
        {
            ChangeMap.gI().changeMap(pl, zone.map.id, zone.map.mapWidth / 10, zone.map.mapHeight / 2);
            return;
        }
        ChangeMap.gI().changeMapBySpaceShip(pl, zone.map.id, zoneID, cx, pl.typeShip);
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402:
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403:
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404:
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759:
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
            }
        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }
    
    public boolean PowerCanUse(Player player, Item item)
    {
        for (ItemOption op : item.itemOptions) {
            if (op != null && op.optionTemplate.id == 21)
            {
                if (op.param > (int) (player.point.getPower() / 1000000000)) {
                    return false;
                }
            }
            else if (op != null && op.optionTemplate.id != 21) {
                if (item.template.strRequire > player.point.getPower()) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean OptionCanUpgrade(ItemOption io) {
        return io.optionTemplate.id == 23 || io.optionTemplate.id == 22 || io.optionTemplate.id == 47 || io.optionTemplate.id == 6 || io.optionTemplate.id == 27 || io.optionTemplate.id == 14 || io.optionTemplate.id == 0 || io.optionTemplate.id == 7 || io.optionTemplate.id == 28;
    }
    

}
