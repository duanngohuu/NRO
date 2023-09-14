package service.DAOS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import real.func.ChangeMap;
import real.item.Item;
import real.npc.Npc;
import real.pet.NewPet;
import real.pet.Pet;
import real.player.Player;
import real.player.PlayerManger;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import service.Setting;
import service.data.Init;

public class PetDAO {

    public static void Pet2(Player pl, int h , int b , int l){
        if (pl.newpet != null) {
            pl.newpet.exitMap();
            pl.newpet.close();
            pl.newpet.dispose();
        }
        pl.newpet = new NewPet(pl,(short)h,(short)b,(short)l);
        pl.newpet.name =  "$";
        pl.newpet.gender = pl.gender;
        pl.newpet.point.tiemNang = 1;
        pl.newpet.point.power = 1;
        pl.newpet.point.limitPower = 1;
        pl.newpet.point.hpGoc = 500000000;
        pl.newpet.point.mpGoc = 500000000;
        pl.newpet.point.dameGoc = 1;
        pl.newpet.point.defGoc = 1;
        pl.newpet.point.critGoc = 1;
        pl.newpet.point.stamina = 1;
        PlayerManger.gI().getPlayers().add(pl.newpet);
        pl.newpet.point.updateall();
        pl.newpet.active(Setting.DELAY_PET);
    }
    
    public static void Pet2Exit(Player pl)
    {
        if (pl.newpet != null) {
            pl.newpet.exitMap();
            pl.newpet.close();
            pl.newpet.dispose();
            pl.newpet = null;
        }
    }
    
    public static void newPet(Player pl) {
        pl.pet = new Pet(pl);
        pl.pet.name = "$Đệ tử";
        pl.pet.gender = (byte) Util.nextInt(0, 2);

        pl.pet.point.power = 1200;
        pl.pet.point.limitPower = 0;
        int hpGoc = Util.nextInt(40, 200);
        int mpGoc = Util.nextInt(40, 200);
        int damGoc = Util.nextInt(30, 60);
        pl.pet.point.hpGoc = hpGoc * 20;
        pl.pet.point.mpGoc = mpGoc * 20;
        pl.pet.point.dameGoc = damGoc;
        pl.pet.point.defGoc = (short) Util.nextInt(12, 55);
        pl.pet.point.critGoc = (byte) Util.nextInt(1, 6);
        pl.pet.point.stamina = Setting.MAX_STAMINA_FOR_PET;

        List<Item> body = new ArrayList<>();
        for (int i = 0; i < Setting.SIZE_BODY_PET; i++) {
            body.add(null);
        }
        pl.pet.inventory.itemsBody = body;

        Skill skill = SkillUtil.createSkill(Init.SKILL_1_PET[Util.nextInt(0, 2)], 1 , 0, 0);
        pl.pet.playerSkill.skills.add(skill);
        pl.pet.isNew = true;
    }
    
    public static void OpenMabuEgg(Player pl , byte gender){
        try {
            Service.gI().sendtMabuEff(pl, 101);
            if(pl.pet == null){
                newPetMabu(pl, gender,3);
            }else{
                ChangePetMabu(pl, gender, 3);
            }
            pl.mabuEgg = null;
            pl.sendMeHavePet();
            pl.pet.isNew = true;
            pl.pet.point.updateall();
            Thread.sleep(3000);
            ChangeMap.gI().changeMap(pl, 7 * pl.gender, 500, 400);
            Thread.sleep(1000);
            Service.gI().chatJustForMe(pl, pl.pet, "Oa oa oa");
        } catch (Exception ex) {
        }
    }
    
    public static void ChangePetMabu(Player pl,byte gender , int b) {
        try {
            if (pl.pet != null) {
                pl.pet.exitMap();
                pl.pet.name = "$Mabư";
                pl.pet.gender = gender;
                pl.pet.isMabu = b;
                pl.pet.point.power = 1500000;
                int hpGoc = Util.nextInt(220, 500);
                int mpGoc = Util.nextInt(220, 500);
                int damGoc = Util.nextInt(80, 120);
                pl.pet.point.hpGoc = hpGoc * 20;
                pl.pet.point.mpGoc = mpGoc * 20;
                pl.pet.point.dameGoc = damGoc;
                pl.pet.point.defGoc = (short) Util.nextInt(12, 55);
                pl.pet.point.critGoc = (byte) Util.nextInt(1, 6);
                pl.pet.point.stamina = Setting.MAX_STAMINA_FOR_PET;
                pl.pet.inventory.itemsBody.clear();
                pl.pet.inventory.itemsBody = new ArrayList();
                for (int i = 0; i < Setting.SIZE_BODY_PET; i++) {
                    pl.pet.inventory.itemsBody.add(null);
                }
                Skill skill = SkillUtil.createSkill(Init.SKILL_1_PET[Util.nextInt(0, 2)], 1,0, 0);
                pl.pet.playerSkill.skills.clear();
                pl.pet.playerSkill.skills.add(skill);
                pl.pet.joinMapMaster();
            } else {
                Service.gI().sendThongBao(pl, "Bạn phải có đệ tử mới có thể sử dụng");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    public static void newPetMabu(Player pl,byte gender , int b) {
        pl.pet = new Pet(pl);
        pl.pet.name = "$Mabư";
        pl.pet.gender = gender;
        pl.pet.isMabu = b;
        pl.pet.point.power = 1500000;
        pl.pet.point.limitPower = 0;
        int hpGoc = Util.nextInt(220, 500);
        int mpGoc = Util.nextInt(220, 500);
        int damGoc = Util.nextInt(80, 120);
        pl.pet.point.hpGoc = hpGoc * 20;
        pl.pet.point.mpGoc = mpGoc * 20;
        pl.pet.point.dameGoc = damGoc;
        pl.pet.point.defGoc = (short) Util.nextInt(12, 55);
        pl.pet.point.critGoc = (byte) Util.nextInt(1, 6);
        pl.pet.point.stamina = Setting.MAX_STAMINA_FOR_PET;

        List<Item> body = new ArrayList<>();
        for (int i = 0; i < Setting.SIZE_BODY_PET; i++) {
            body.add(null);
        }
        pl.pet.inventory.itemsBody = body;

        Skill skill = SkillUtil.createSkill(Init.SKILL_1_PET[Util.nextInt(0, 2)], 1 , 0, 0);
        pl.pet.playerSkill.skills.add(skill);
    }

    public static boolean renamePet(Player pl, String name) {
        try {
            if (!pl.inventory.existItemBag(400) || pl.pet == null) {
                return false;
            }
            pl.inventory.subQuantityItemsBag(pl.inventory.findItemBagByTemp(400), 1);
            pl.pet.name = "$" + name;
            pl.inventory.sortItemBag();
            pl.inventory.sendItemBags();
            pl.pet.exitMap();
            pl.pet.joinMapMaster();
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return false;
    }
    
    public static void ChangePet(Player pl) {
        try {
            if (pl.pet != null) {
                if (!pl.inventory.existItemBag(401)) {
                    return;
                }
                pl.inventory.subQuantityItemsBag(pl.inventory.findItemBagByTemp(401), 1);
                int gender = 0;
                switch (pl.pet.gender) {
                    case 0:
                        gender = 1;
                        break;
                    case 1:
                        gender = 2;
                        break;
                    case 2:
                        gender = 0;
                        break;
                    default:
                        gender = 0;
                        break;
                }
                pl.pet.name = "$Đệ tử";
                pl.pet.gender = (byte) gender;
                pl.pet.isMabu = -1;
                pl.pet.point.power = 1200;
                int hpGoc = Util.nextInt(40, 200);
                int mpGoc = Util.nextInt(40, 200);
                int damGoc = Util.nextInt(30, 60);
                pl.pet.point.hpGoc = hpGoc * 20;
                pl.pet.point.mpGoc = mpGoc * 20;
                pl.pet.point.dameGoc = damGoc;
                pl.pet.point.defGoc = (short) Util.nextInt(12, 55);
                pl.pet.point.critGoc = (byte) Util.nextInt(1, 6);
                pl.pet.point.stamina = Setting.MAX_STAMINA_FOR_PET;
                pl.pet.inventory.itemsBody.clear();
                List<Item> body = new ArrayList();
                for (int i = 0; i < Setting.SIZE_BODY_PET; i++) {
                    body.add(null);
                }
                pl.pet.inventory.itemsBody = body;

                Skill skill = SkillUtil.createSkill(Init.SKILL_1_PET[Util.nextInt(0, 2)], 1, 0, 0);
                pl.pet.playerSkill.skills.clear();
                pl.pet.playerSkill.skills.add(skill);
                pl.inventory.sortItemBag();
                pl.inventory.sendItemBags();
                pl.pet.exitMap();
                pl.pet.joinMapMaster();
                Service.gI().sendThongBao(pl, "Đổi đệ tử thành công!");
            } else {
                Service.gI().sendThongBao(pl, "Bạn phải có đệ tử mới có thể sử dụng");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
