package real.func;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.Mob;
import real.player.Player;
import server.SQLManager;
import server.Service;
import server.Util;
import server.io.JSON;
import service.DAOS.PetDAO;
import service.Setting;

public class GiftCode {
    public String code;
    public int gold;
    public int gem;
    public int dayexits;
    public Timestamp timecreate;
    public ArrayList<Item> listItem = new ArrayList<>();
    public static ArrayList<GiftCode> gifts = new ArrayList<>();
    
    // QUA TOP NGÀY TẾT
    public static String QUA_1 = "QUA_1";
    public static String QUA_2 = "QUA_2";
    
    public static void loadGift(){
        try {
            gifts.clear();
            ResultSet rs = SQLManager.executeQuery("SELECT * FROM giftcode");
            while (rs.next()) {
                GiftCode g = new GiftCode();
                g.code = rs.getString("code");
                g.gold = rs.getInt("gold");
                g.gem = rs.getInt("gem");
                g.dayexits = rs.getInt("dayexits");
                g.timecreate = rs.getTimestamp("createtime");
                JSONArray js = new JSONArray(rs.getString("item"));
                JSONObject ob = null;
                JSONArray jsa2 = null;
                int size = js.length();
                Item it = null;
                for(int i = 0 ; i < size;i++){
                    ob = js.getJSONObject(i);
                    it = new Item();
                    it.template = ItemData.gI().getTemplate((short) ob.getInt("temp_id"));
                    it.content = it.getContent();
                    it.quantity = ob.getInt("quantity");
                    it.buyTime = System.currentTimeMillis();
                    jsa2 = ob.getJSONArray("option");
                    for (int j = 0; j < jsa2.length(); j++) {
                        JSONObject jso2 = (JSONObject) jsa2.get(j);
                        int idOptions = jso2.getInt("id");
                        int param = jso2.getInt("param");
                        it.itemOptions.add(new ItemOption(idOptions, param));
                    }
                    if(it.itemOptions.isEmpty()){
                        it.itemOptions.add(new ItemOption(30,0));
                    }
                    g.listItem.add(it);
                }
                gifts.add(g);
            }
            Util.warning("Finish load giftcode! [" + gifts.size() + "]\n");
            rs.close();
            rs = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void claimGift(Player pl, String gif){
        if(Setting.SERVER_TEST){
            Service.gI().sendThongBao(pl, "Hiện server đang mở test vui lòng thử lại sau!");
            return;
        }
        GiftCode d = new GiftCode();
        String code = gif.replace(" ", "");
        boolean isCode = gifts.stream().anyMatch(g -> g != null && g.dayexits != -1 && Util.findDayDifference(System.currentTimeMillis(), g.timecreate.getTime()) <= g.dayexits && (g.code.equals(code)));
        // ---------------------2022---------------------- \\
        if(!isCode && !code.equals("tuanzin") && !code.equals("9000tv")){
            Service.gI().sendThongBao(pl, "Mã quà tặng hết hạn hoặc không tồn tại");
        }
        if(checkGift(pl, code))
        {
            Service.gI().sendThongBao(pl, "Mã quà tặng chỉ sử dụng được 1 lần");
        }
//        else if(!code.equals("tanthu") && !code.equals("napcard20kbatmondongiap")){
//            pl.inventory.giftCode.add(code);
//            int size = gifts.size();
//            for(int i = 0 ; i < size ;i++){
//                GiftCode c = gifts.get(i);
//                if(c != null && c.code.equals(code)){
//                    if(c.gold > 0){
//                        Item it = ItemData.gI().get_item(1184);
//                        it.itemOptions.add(new ItemOption(30, 0));
//                        it.quantity = c.gold;
//                        pl.inventory.addItemBag(it);
//                        pl.inventory.sendItemBags();
//                        pl.inventory.sortItemBag();
//                    }
//                    if(c.gem > 0){
//                        pl.inventory.gem+= c.gem;
//                        Service.gI().sendMoney(pl);
//                    }
//                    if(!c.listItem.isEmpty()){
//                        int sizeI = c.listItem.size();
//                        for(int j = 0 ; j < sizeI ; j++){
//                            Item item = c.listItem.get(j);
//                            if(item != null){
//                                pl.inventory.addItemBag(item);
//                                pl.inventory.sendItemBags();
//                                pl.inventory.sortItemBag();
//                            }
//                        }
//                    }
//                }
//            }
//            Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được quà");
//        }
        else if(code.equals("tuanzin")){
            if (pl.pet == null) {
                PetDAO.newPet(pl);
                pl.pet.changeStatus((byte)1);
                pl.sendMeHavePet();
                pl.pet.point.updateall();                              
            }
            pl.inventory.giftCode.add(code);
            Item tanthu = ItemData.gI().get_item(627);
            Item tanthu1 = ItemData.gI().get_item(457);
            Item tanthu2 = ItemData.gI().get_item(16);
            tanthu.quantity = 2;
            tanthu1.quantity = 100;
            tanthu2.quantity = 20;
            tanthu.itemOptions.clear();
            tanthu.itemOptions.add(new ItemOption(30, 0));
            tanthu.itemOptions.add(new ItemOption(93, 30));
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            tanthu.buyTime = timenow.getTime();
            tanthu1.buyTime = timenow.getTime();
            Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được quà");
            pl.inventory.addItemBag(tanthu);
            pl.inventory.addItemBag(tanthu1);
            pl.inventory.addItemBag(tanthu2);
            pl.inventory.sendItemBags();
        }
        else if(code.equals("napcard")){
            if(pl.session.get_money() >= 10000){
                pl.inventory.giftCode.add(code);
                Item tanthu = ItemData.gI().get_item(1199);
                tanthu.itemOptions.clear();
                tanthu.itemOptions.add(new ItemOption(50, 10));
                tanthu.itemOptions.add(new ItemOption(77, 10));
                tanthu.itemOptions.add(new ItemOption(103, 10));
                tanthu.itemOptions.add(new ItemOption(14, 5));
                tanthu.itemOptions.add(new ItemOption(93, 30));
                Timestamp timenow = new Timestamp(System.currentTimeMillis());
                tanthu.buyTime = timenow.getTime();
                Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được quà");
                pl.inventory.addItemBag(tanthu);
                pl.inventory.sendItemBags();
            }
        }
        else if(code.equals("skhnp")){
            pl.inventory.giftCode.add(code);
            int[] arrItem = Mob.get_do(2, Util.nextInt(1, 3));
            Item SKH = ItemData.gI().get_item(arrItem[Util.nextInt(0, arrItem.length - 1)]);
            List<ItemOption> ops = ItemData.gI().get_skh(2, 2);
            SKH.itemOptions.addAll(ops);
            
//            tanthu1.quantity = 100;      
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            SKH.buyTime = timenow.getTime();
            Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được quà");
            pl.inventory.addItemBag(SKH);
            pl.inventory.sendItemBags();        
        }
        else if(code.equals("9000tv")){
            pl.inventory.giftCode.add(code);
            Item tanthu1 = ItemData.gI().get_item(457);
            tanthu1.quantity = 4000;      
            Timestamp timenow = new Timestamp(System.currentTimeMillis());
            tanthu1.buyTime = timenow.getTime();
            
            pl.inventory.addItemBag(tanthu1);
            pl.inventory.sendItemBags();  
            Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được quà TuanZin");
        }
//        else if(code.equals("keybac")){
//            pl.inventory.giftCode.add(code);
//            
//            Item it1 = ItemData.gI().get_item(380);
//            it1.quantity = 99;  
//            
//            Item it2 = ItemData.gI().get_item(381);
//            it2.quantity = 99;    
//            
//            Item it3 = ItemData.gI().get_item(382);
//            it3.quantity = 99;    
//            
//            Item it4 = ItemData.gI().get_item(383);
//            it4.quantity = 99;    
//            
//            Item it5 = ItemData.gI().get_item(384);
//            it5.quantity = 99;    
//            
//
//            Timestamp timenow = new Timestamp(System.currentTimeMillis());
//            it1.buyTime = timenow.getTime();
//            it2.buyTime = timenow.getTime();
//            it3.buyTime = timenow.getTime();
//            it4.buyTime = timenow.getTime();
//            it5.buyTime = timenow.getTime();
//            Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được quà");
//            pl.inventory.addItemBag(it1);
//            pl.inventory.addItemBag(it2);
//            pl.inventory.addItemBag(it3);
//            pl.inventory.addItemBag(it4);
//            pl.inventory.addItemBag(it5);
//            pl.inventory.sendItemBags();        }
    
    }
        
    
    public static boolean checkGift(Player pl, String gif){
        String code = gif.replace(" ", "");
        return pl.inventory.giftCode.stream().anyMatch(g -> g != null && g.equals(code));
    }
}

/*if (playerKill.pet == null) {
                                    PetDAO.newPet(playerKill);
                                    playerKill.pet.changeStatus((byte)1);
                                    playerKill.sendMeHavePet();
                                    playerKill.pet.point.updateall();
                                }*/
