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
import real.player.Inventory;


public class RuongBiAn {
    private static RuongBiAn instance;
    private int menuRuongBiAn;
    private byte select;
    public Item item;
    private int tg;
    private RuongBiAn() {
        
    }
    
    public static RuongBiAn gI() {
        if (instance == null) {
            instance = new RuongBiAn();
        }
        return instance;
    }
    public void openMenuRuong_1(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }
    public void openMenuRuong_2(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }  
    public void openMenuRuong_3(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }
    public void openMenuRuong_4(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }  
    public void openMenuRuong_5(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }
    public void openMenuRuong_6(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }  
    public void openMenuRuong_7(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }  
    public void openMenuRuong_8(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }
    public void openMenuRuong_9(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }public void openMenuRuong_10(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }  
    public void openMenuRuong_11(Player pl) {
        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN, -1, "Bạn muốn sử dụng Rương Bí Ẩn ?",
                "Sử dụng", "Đóng");
    }
    

    

    public void confirmRuongBiAn(Player pl) {
                int[] trangbilv_3 = new int[13];
                int[] trangbilv_4 = new int[13];
                int[] trangbilv_5 = new int[13];
                int[] trangbilv_6 = new int[13];
                int[] trangbilv_7 = new int[13];
                int[] trangbilv_8 = new int[13];
                int[] trangbilv_9 = new int[13];
                int[] trangbilv_10 = new int[13];
                int[] trangbilv_11 = new int[13];
//                
                int x_3 = 0;
                int x_4 = 0;
                int x_5 = 0;
                int x_6 = 0;
                int x_7 = 0;
                int x_8 = 0;
                int x_9 = 0;
                int x_10 = 0;
                int x_11 = 0;
                this.tg = Util.nextInt(-10,10);  
//                
                for (int i = 1; i <= 281; i++){
                        Item it = ItemData.gI().get_item(i);
                        if (it.template.type == 0 || it.template.type == 1 || it.template.type == 2 || it.template.type == 3|| it.template.type == 4){
                            switch(it.template.level){
                                case 3:
                                    trangbilv_3[x_3] = i;
                                    x_3++;
                                    break;
                                case 4:
                                    trangbilv_4[x_4] = i;
                                    x_4++;
                                    break;
                                case 5:
                                    trangbilv_5[x_5] = i;
                                    x_5++;
                                    break;  
                                case 6:
                                    trangbilv_6[x_6] = i;
                                    x_6++;
                                    break;
                                case 7:
                                    trangbilv_7[x_7] = i;
                                    x_7++;
                                    break;
                                case 8:
                                    trangbilv_8[x_8] = i;
                                    x_8++;
                                    break;
                                case 9:
                                    trangbilv_9[x_9] = i;
                                    x_9++;
                                    break;
                                case 10:
                                    trangbilv_10[x_10] = i;
                                    x_10++;
                                    break;
                                case 11:
                                    trangbilv_11[x_11] = i;
                                    x_11++;
                                    break; 
                                default:
                                    break;
                            }
                        }
                }
        int tb3 = trangbilv_3[Util.nextInt(0,trangbilv_3.length - 1)];
        int tb4 = trangbilv_4[Util.nextInt(0,trangbilv_4.length - 1)];
        int tb5 = trangbilv_5[Util.nextInt(0,trangbilv_5.length - 1)];
        int tb6 = trangbilv_6[Util.nextInt(0,trangbilv_6.length - 1)];
        int tb7 = trangbilv_7[Util.nextInt(0,trangbilv_7.length - 1)];
        int tb8 = trangbilv_8[Util.nextInt(0,trangbilv_8.length - 1)];
        int tb9 = trangbilv_9[Util.nextInt(0,trangbilv_9.length - 1)];
        int tb10 = trangbilv_10[Util.nextInt(0,trangbilv_10.length - 1)];
        int tn11 = trangbilv_11[Util.nextInt(0,trangbilv_11.length - 1)];
        switch (pl.DHVT_23.step){
            case 1:
                int i1 = Util.nextInt(1, 5);
                int a1 = i1*1000000; 
                pl.inventory.gold += a1;
                Service.gI().sendMoney(pl);
                switch (pl.getIndexMenu()){
                    case NpcFactory.RUONG_BI_AN:
                        Npc.createMenuConMeo(pl, -1, -1, "|6|Bạn nhận được \n|1|" + "+ " +i1+ "tr " + " Vàng","OK");
                        break;
                }
                pl.DHVT_23.isDrop = false;
                break;
            case 2:
                int i2 = Util.nextInt(6, 10);
                int a2 = i2*1000000; 
                pl.inventory.gold += a2;
                Service.gI().sendMoney(pl);
                switch (pl.getIndexMenu()){
                    case NpcFactory.RUONG_BI_AN:
                        Npc.createMenuConMeo(pl, -1, -1, "|6|Bạn nhận được \n|1|" + "+ " +i2+ "tr" + " Vàng","OK");
                        break;
                }
                break;
            case 3:
 
                switch (pl.getIndexMenu()){
                    case NpcFactory.RUONG_BI_AN:
                        int i3 = Util.nextInt(11, 20);
                        int a3 = i3*1000000; 
                        pl.inventory.gold += a3;
                        Service.gI().sendMoney(pl);
                        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN_2, -1, "|6|Bạn nhận được \n|1|" + "+ " +i3+ "tr" + " Vàng","OK[1]");
                        break;
                    case NpcFactory.RUONG_BI_AN_2:
                        Item itc3 = ItemData.gI().get_item(tb3);
                        itc3.quantity = 1;
                        pl.inventory.addItemBag(itc3);
                        pl.inventory.sendItemBags();
                        String npcSay = "|6|Bạn đã nhận được\n|1|" + itc3.template.name+"\n|0|";
                        for (ItemOption io : itc3.itemOptions) {
                                if (OptionCanUpgrade(io)){
                                    npcSay += io.optionTemplate.name.replaceAll("#", io.param + "")+"\n";        
                                }
                            }
                        npcSay += "|2|"+ itc3.template.description;
                        Npc.createMenuConMeo(pl, -1, -1,npcSay,"OK");
                        break;    
                }
            case 4:
                switch (pl.getIndexMenu()){
                    case NpcFactory.RUONG_BI_AN:
                        int i4 = Util.nextInt(21, 26);
                        int a4 = i4*1000000; 
                        pl.inventory.gold += a4;
                        Service.gI().sendMoney(pl);
                        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN_2, -1, "|6|Bạn nhận được \n|1|" + "+ " +i4+ "tr" + " Vàng","OK[2]");
                        break;
                    case NpcFactory.RUONG_BI_AN_2:
                        int j1 = Util.nextInt(500, 1000);
                        pl.inventory.gem += j1;
                        Service.gI().sendMoney(pl);
                        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN_3, -1, "|6|Bạn nhận được \n|1|" + "+" +j1+" " + "Ngọc xanh","OK[1]");
                        break; 
                    case NpcFactory.RUONG_BI_AN_3:
                        Item itc4 = ItemData.gI().get_item(tb4);
                        itc4.quantity = 1;
                        pl.inventory.addItemBag(itc4);
                        pl.inventory.sendItemBags();
                        String npcSay = "|6|Bạn đã nhận được\n|1|" + itc4.template.name+"\n|0|";
                        for (ItemOption io : itc4.itemOptions) {
                                if (OptionCanUpgrade(io)){
                                    npcSay += io.optionTemplate.name.replaceAll("#", io.param + "")+"\n";        
                                }
                            }
                        npcSay += "|2|"+ itc4.template.description;
                        Npc.createMenuConMeo(pl, -1, -1,npcSay,"OK");
                        break;     
                }
                break;
            case 5:
                switch (pl.getIndexMenu()){
                    case NpcFactory.RUONG_BI_AN: 
                        int i5 = Util.nextInt(30, 35);
                        int a5 = i5*1000000; 
                        pl.inventory.gold += a5;
                        Service.gI().sendMoney(pl);
                        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN_2, -1, "|6|Bạn nhận được \n|1|" + "+ " +i5+ "tr" + " Vàng","OK[2]");
                        break;
                    case NpcFactory.RUONG_BI_AN_2: 
                        int j2 = Util.nextInt(1500, 2000);
                        pl.inventory.gem += j2;
                        Service.gI().sendMoney(pl);
                        Npc.createMenuConMeo(pl, NpcFactory.RUONG_BI_AN_3, -1, "|6|Bạn nhận được \n|1|" + "+" +j2+" " + "Ngọc xanh","OK[1]");
                        break; 
                    case NpcFactory.RUONG_BI_AN_3:
                        Item itc5 = ItemData.gI().get_item(tb5);
                        for (ItemOption io : itc5.itemOptions) {
                            if (OptionCanUpgrade(io)) {
                                addParam(itc5, io.optionTemplate.id, paramRandom(itc5, io));
                            }
                        }
                        String npcSay = "|6|Bạn đã nhận được\n|1|" + itc5.template.name+"\n|0|";
                        for (ItemOption io : itc5.itemOptions) {
                                if (OptionCanUpgrade(io)){
                                    npcSay += io.optionTemplate.name.replaceAll("#", io.param  + "")+"\n";        
                                }
                            }
                        
                        npcSay += "|2|"+ itc5.template.description;
                        itc5.quantity = 1;
                        pl.inventory.addItemBag(itc5);
                        pl.inventory.sendItemBags();
                        Npc.createMenuConMeo(pl, -1, -1,npcSay,"OK");
                        break;     
                }
                break;
            case 6:
                
                break;
            case 7:
                
                break;
            case 8:
                
                break;
            case 9:
                
                break;
            case 10:
                
                break;
            case 11:
                
                break;
            
            
            
        }
    } 
public boolean OptionCanUpgrade(ItemOption io) {
        return io.optionTemplate.id == 23 || io.optionTemplate.id == 22 || io.optionTemplate.id == 47 || io.optionTemplate.id == 6 || io.optionTemplate.id == 27 || io.optionTemplate.id == 14 || io.optionTemplate.id == 0 || io.optionTemplate.id == 7 || io.optionTemplate.id == 28;
    }
public int paramRandom(Item item, ItemOption io) {
    if (OptionCanUpgrade(io)) {
            int i = Util.nextInt(-5,15);
            int param = (int)(io.param * i / 100);
            return param;
        }
        return 0;
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
}
