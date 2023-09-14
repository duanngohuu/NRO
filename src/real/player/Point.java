package real.player;

import java.sql.Timestamp;
import real.func.DHVT;
import real.func.NRSD;
import real.item.Item;
import real.item.ItemOption;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import service.Setting;
import real.item.ItemBua;
import real.pet.Pet;

public class Point {

    private Player player;

    public byte limitPower;
    public long power;
    public long tiemNangUse;
    public long tiemNang;

    public int hp;
    public int hpGoc;
    public int mp;
    public int mpGoc;
    public int dameGoc;
    public short defGoc;
    public byte critGoc;
    public byte critSum;
    
    public int hpHoi;
    public int addhp;
    public int mpHoi;

    public boolean isCrit;
    private boolean crit100;
    public int stamina;

    public int get_percent_hut_hp(boolean mob) {
        int p = 0;
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    switch (iop.optionTemplate.id) {
                        case 95: //Biến #% tấn công thành HP
                            p += iop.param;
                            break;
                        case 104://Biến #% tấn công quái thành HP
                            if (mob) {
                                p += iop.param;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        for(Item it : player.inventory.itemsGift){
            if(it != null && it.template.id == 374){
                for (ItemOption iop : it.itemOptions) {
                    if(iop != null && iop.optionTemplate.id == 95){
                        p += iop.param;
                    }
                }
            }
        }
        return p;
    }

    public int get_percent_option_ct(int id) {
        int p = 0;
        try {
            Item it = player.inventory.itemsBody.get(5);
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == id) {
                        p += iop.param;
                    }
                }
            }
        } catch (Exception e) {
        }
        return p;
    }

    public int get_percent_option(int id) {
        int p = 0;
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == id) {
                        p += iop.param;
                    }
                }
            }
        }
        for(Item it : player.inventory.itemsGift){
            if(it != null && NRSD.isNRSD(it.template.id)){
                for (ItemOption iop : it.itemOptions) {
                    if(iop != null && iop.optionTemplate.id == id){
                        p += iop.param;
                    }
                }
            }
        }
        return p;
    }

    private void setCrit() {
        if (crit100) {
            crit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(getCritFull());
        }
    }

    public Point(Player player) {
        this.player = player;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public long getPower() {
        return this.power;
    }

    public long getTiemNang() {
        return this.tiemNang;
    }

    public void setHP(int hp) {
        if (hp > 0) {
            this.hp = hp <= getHPFull() ? hp : getHPFull();
        } else {
            Service.gI().charDie(player);
        }
    }

    public int getHP() {
        return this.hp <= getHPFull() ? this.hp : getHPFull();
    }

    public void setCrit100() {
        this.crit100 = true;
    }

    public int getBaseHPFull() {
        if(player.isBoss || player.isNewPet){
            return this.hpGoc;
        }
        long hpBaseFull = this.hpGoc;
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    switch (iop.optionTemplate.id) {
                        case 2: //HP +#000
                            hpBaseFull += iop.param * 1000;
                            break;
                        case 6://HP+#
                            hpBaseFull += iop.param;
                            break;
                        case 22://HP+#K
                            hpBaseFull += iop.param * 1000;
                            break;
                            
                        case 48://HP +#
                            hpBaseFull += iop.param;
                            break;
                        case 77://HP+#%
                            hpBaseFull += hpBaseFull * iop.param / 100;
                            break;
                            
                        default:
                            break;
                    }
                }
            }
        }
        for(Item it : player.inventory.itemsGift){
            if(it != null && it.template.id == 373){
                for (ItemOption iop : it.itemOptions) {
                    if(iop != null && iop.optionTemplate.id == 77){
                        hpBaseFull += hpBaseFull * iop.param / 100;
                    }
                }
            }
        }
        if (player.typeFusion == Player.HOP_THE_PORATA2) {
            hpBaseFull += hpBaseFull * getParam(77, 921) / 100;
        }
        return (int)hpBaseFull;
    }

    public int getHPFull(boolean...isGet) {
        long hpFull = getBaseHPFull();
        this.hpHoi += this.addhp;
        
        if(player.isBoss || player.isNewPet){
            if(hpFull > Integer.MAX_VALUE){
                hpFull = Integer.MAX_VALUE;
            }
            return (int)hpFull;
        }
        if(player.percentHpPlus != -1 && player.zone.isNRSD()){
            hpFull *= player.percentHpPlus;
        }
        if (player.isPl() && player.typeFusion != 0) {
            if(player.pet.isMabu >= 3){
                int HP = player.pet.point.getHPFull();
                hpFull += HP + HP / 100 * 20;
            }else{
                hpFull += player.pet.point.getHPFull();
            }
        }
        if (!player.isBoss && player.inventory.checkSKH(135)) {
            hpFull += hpFull * 80 / 100;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(2755)) {
            hpFull *= 2;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(10714)) {
            hpFull *= 2.5;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(8062)) {
            hpFull += hpFull * 5 / 100;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(8585)) {
            hpFull += hpFull * 20 / 100;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(10904)) {
            hpFull += hpFull * 20 / 100;
        }
        if (player.playerSkill.isMonkey && isGet.length == 0) {
            int percent = SkillUtil.getPercentHpMonkey(player.playerSkill.getLevelMonkey());
            hpFull += (hpFull / 100) * percent;
        }
        hpFull += (hpFull / 100) * player.playerSkill.tiLeHPHuytSao;
        
        if(!player.isBoss && player.zone != null && player.zone.isCooler() && !player.inventory.OptionCt(106)){
            hpFull/=2;
        }
        if(hpFull > Integer.MAX_VALUE || hpFull <= 0){
            hpFull = Integer.MAX_VALUE;
        }
        return (int)hpFull;
    }

    public void setMP(int mp) {
        if (mp > 0) {
            this.mp = mp <= getMPFull() ? mp : getMPFull();
        } else {
            this.mp = 0;
        }
    }

    public int getMP() {
        return this.mp <= getMPFull() ? this.mp : getMPFull();
    }

    public int getBaseMPFull() {
        long mpBaseFull = this.mpGoc;
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    switch (iop.optionTemplate.id) {
                        case 2:
                            mpBaseFull += iop.param * 1000;
                            break;
                        case 7:
                            mpBaseFull += iop.param;
                            break;
                        case 48:
                            mpBaseFull += iop.param;
                            break;
                        case 23:
                            mpBaseFull += iop.param * 1000;
                            break;
                        case 103:
                            mpBaseFull += mpBaseFull * iop.param / 100;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        for(Item it : player.inventory.itemsGift){
            if(it != null && it.template.id == 377){
                ItemOption ops = it.itemOptions.stream().filter(op -> op != null && op.optionTemplate.id == 103).findFirst().orElse(null);
                if(ops != null){
                    mpBaseFull += mpBaseFull * ops.param / 100;
                }
            }
        }
        if (player.typeFusion == Player.HOP_THE_PORATA2) {
            mpBaseFull += mpBaseFull * getParam(103, 921) / 100;
        }
        return (int)mpBaseFull;
    }

    public int getMPFull() {
        long mpFull = getBaseMPFull();
        if(player.percentHpPlus != -1 && player.zone.isNRSD()){
            mpFull *= player.percentHpPlus;
        }
        if (player.typeFusion != 0) {
            if(player.pet.isMabu >= 3){
                int MP = player.pet.point.getMPFull();
                mpFull += MP + MP / 100 * 20;
            }else{
                mpFull += player.pet.point.getMPFull();
            }
        }
        if (player.itemTime.ExitsItemTiem(2756)) {
            mpFull *= 2;
        }
        if (player.itemTime.ExitsItemTiem(10715)) {
            mpFull *= 2.5;
        }
        if (player.itemTime.ExitsItemTiem(8061)) {
            mpFull += mpFull * 5 / 100;
        }
        if(mpFull > Integer.MAX_VALUE || mpFull <= 0){
            mpFull = Integer.MAX_VALUE;
        }
        return (int)mpFull;
    }

    public void powerUp(int power) {
        this.power += power;
    }

    public void tiemNangUp(int tiemNang) {
        this.tiemNang += tiemNang;
    }
    
    public int TiemNang(long TiemNang) {
        if(this.power >= 40000000000L && this.power < 50000000000L){
            TiemNang /= Util.nextInt(60, 70);
        }
        else if(this.power >= 50000000000L && this.power < 60000000000L){
            TiemNang /= Util.nextInt(70, 90);
        }
        else if(this.power >= 60000000000L && this.power < 70000000000L){
            TiemNang /= Util.nextInt(90,100);
        }
        else if(this.power >= 70000000000L && this.power < 80000000000L){
            TiemNang /= Util.nextInt(100,120);
        }
        else if(this.power >= 80000000000L && this.power < 100000000000L){
            TiemNang /= Util.nextInt(120,140);
        }
        else if(this.power >= 100000000000L && this.power < 120000000000L){
            TiemNang /= Util.nextInt(140,160);
        }
        else if(this.power >= 120000000000L && this.power < 140000000000L){
            TiemNang /= Util.nextInt(160,180);
        }
        if(TiemNang <= 0){
            TiemNang = 1;
        }
        return (int)TiemNang;
    }
    
    public boolean checkPower(long tnPl){
        return (long)(this.power + tnPl) <= Setting.LIMIT_SUC_MANH[this.limitPower];
    }

    public int getDefFull() {
        int defFull = this.defGoc * 4;
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == 47) {
                        defFull += iop.param;
                    }
                }
            }
        }
        if (player.typeFusion == Player.HOP_THE_PORATA2) {
            defFull += defFull * getParam(94, 921) / 100;
        }
        return defFull;
    }

    public byte getCritFull() {
        int critFull = this.critGoc + this.critSum;
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == 14) {
                        critFull += iop.param;
                    }
                }
            }
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(7079)) {
            critFull += 15;
        }
        else if (player.isPl() && player.itemTime.ExitsItemTiem(7080)) {
            critFull += 25;
        }
        if (player.playerSkill.isMonkey) {
            critFull = 110;
        }
        if(critFull > Byte.MAX_VALUE){
            critFull = Byte.MAX_VALUE;
        }
        return (byte)critFull;
    }

    public int getBaseDame() {
        long dameBase = (long)this.dameGoc;
        if (player.isPl() && player.itemTime.ExitsItemTiem(2754)) {
            dameBase += dameBase;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(10716)) {
            dameBase += dameBase + (dameBase * 20 / 100) ;
        }
        if (player.isPl() && (player.itemTime.ExitsItemTiem(6324) || player.itemTime.ExitsItemTiem(6325) || player.itemTime.ExitsItemTiem(6326) || player.itemTime.ExitsItemTiem(6327) || player.itemTime.ExitsItemTiem(6328))) {
            dameBase += dameBase * 10 / 100;
        }
        if (player.isPet && (((Pet)player).master.itemTime.ExitsItemTiem(6324) || ((Pet)player).master.itemTime.ExitsItemTiem(6325) || ((Pet)player).master.itemTime.ExitsItemTiem(6326) || ((Pet)player).master.itemTime.ExitsItemTiem(6327) || ((Pet)player).master.itemTime.ExitsItemTiem(6328))) {
            dameBase += dameBase * 10 / 100;
        }
        
        if (player.isPl() && (player.itemTime.ExitsItemTiem(8060))) {
            dameBase += dameBase * 5 / 100;
        }
        if (player.isPet && (((Pet)player).master.itemTime.ExitsItemTiem(8060) )) {
            dameBase += dameBase * 5 / 100;
        }
        
        if (player.isPl() && player.itemTime.ExitsItemTiem(8581)) {
            dameBase += dameBase * 20 / 100;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(7079)) {
            dameBase += dameBase * 15 / 100;
        }
        if (player.isPl() && player.itemTime.ExitsItemTiem(7080)) {
            dameBase += dameBase * 25 / 100;
        }
        if(player.isPl() && player.itemTime.ExitsItemTiem(10905)){
            dameBase += dameBase * 20 / 100;
        }
        for (Item it : player.inventory.itemsBody) {
            if (it != null) {
                for (ItemOption iop : it.itemOptions) {
                    switch(iop.optionTemplate.id){
                        case 0:
                            dameBase += iop.param;
                            break;
                        case 49:
                        case 50:
                        case 117:
                            dameBase += dameBase * iop.param / 100;
                            break;
                    }
                }
            }
        }
        for(Item it : player.inventory.itemsGift){
            if(it != null && it.template.id == 372){
                for (ItemOption iop : it.itemOptions) {
                    if(iop != null && iop.optionTemplate.id == 50){
                        dameBase += dameBase * iop.param / 100;
                    }
                }
            }
        }
        if (player.playerSkill.isMonkey) {
            dameBase += dameBase * 10 / 100;
        }
        if (player.isPl() && player.typeFusion != 0) {
            if(player.pet.isMabu >= 3){
                int dame = player.pet.point.getBaseDame();
                dameBase += dame + (dame * 20 / 100);
            }else{
                dameBase += player.pet.point.getBaseDame();
            }
        }
        if (player.typeFusion == Player.HOP_THE_PORATA2) {
            dameBase += dameBase * getParam(50, 921) / 100;
        }
        if(player.percentDamePlus != -1){
            dameBase += dameBase * player.percentDamePlus / 100;
        }
        if(!player.isPl()){
            if(dameBase <= 0 || dameBase > Integer.MAX_VALUE){
                dameBase = Integer.MAX_VALUE;
            }
            return (int)dameBase;
        }
        int[] itemID = new int[]{ 529, 530, 531, 534, 535, 536 }; // gtl
        int[] itemPT = new int[]{ 10, 20, 30, 10, 20, 30 }; // gtl
        if (player.inventory.itemsBody.get(6) != null){
            for (int i = 0; i < itemID.length; i++) {
                if(itemID[i] == player.inventory.itemsBody.get(6).template.id){
                    dameBase -= dameBase / 100 * itemPT[i];
                    break;
                }
            }
        } else {
            int phut = 0;
            int pt = 0;
            for (int i = 0; i < itemID.length; i++) {
                phut = getParam(9, itemID[i]);
                if(phut > 0 && pt < itemPT[i]){
                    pt = itemPT[i];
                }
            }
            if (pt > 0) {
                dameBase += dameBase / 100 * pt;
            }
        }
        if(player.zone != null &&player.zone.isCooler() && !player.inventory.OptionCt(106)){
            dameBase/=2;
        }
        if(dameBase <= 0 || dameBase > Integer.MAX_VALUE){
            dameBase = Integer.MAX_VALUE;
        }
        return (int)dameBase;
    }

    public int getParam(int idoption, int itemID) {
        for (Item it : player.inventory.itemsBag) {
            if (it != null && it.itemOptions != null && it.template.id == itemID) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == idoption) {
                        return iop.param;
                    }
                }
            }
        }
        return 0;
    }

    public int getDameAttack(boolean...isAttack) {
        try {
            if (player.playerSkill.isHoaDa) {
                return 0;
            }
            if(player.playerSkill.isBinh)
            {
                return 0;
            }
            if(player.playerSkill.isXinbato){
                return 0;
            }
            if (player.playerSkill.isSocola && player.playerSkill.countPem1hp < 10) {
                player.playerSkill.countPem1hp++;
                return 1;
            }
            if ((player.speacial[0] == 9 && player.gender != 1) || (player.speacial[0] == 10 && player.gender == 1)) {
                int PTHP = (int)((float)((float)this.getHP() / (float)this.getHPFull()) * 100);
                if (PTHP < player.speacial[1]) {
                    setCrit100();
                }
            }
            setCrit();
            Skill skillSelect = player.playerSkill.skillSelect;
            int damage = skillSelect.damage;
            int percentDameSkill = damage;
            switch (skillSelect.template.id) {
                case Skill.DRAGON:
                    if (player.speacial[0] == 1) {
                        percentDameSkill += player.speacial[1];
                    }
                case Skill.KAMEJOKO:
                    if (player.speacial[0] == 5) {
                        percentDameSkill += player.speacial[1];
                    }
                case Skill.GALIK:
                    if (player.speacial[0] == 0) {
                        percentDameSkill += player.speacial[1];
                    }
                case Skill.ANTOMIC:
                    if (player.speacial[0] == 1) {
                        percentDameSkill += player.speacial[1];
                    }
                case Skill.DEMON:
                    if (player.speacial[0] == 0) {
                        percentDameSkill += player.speacial[1];
                    }
                case Skill.MASENKO:
                    if (player.speacial[0] == 2) {
                        percentDameSkill += player.speacial[1];
                    }
                case Skill.KAIOKEN:
                case Skill.LIEN_HOAN:
                    if (player.speacial[0] == 1 && player.gender == 1) {
                        percentDameSkill += player.speacial[1];
                    }
                    break;
                case Skill.DICH_CHUYEN_TUC_THOI:
                    setCrit100();
                    break;
                case Skill.QUA_CAU_KENH_KHI:
                    isCrit = false;
                    break;
                case Skill.SUPER_KAMEJOKO:
                    isCrit = false;
                    break;
                case Skill.MA_PHONG_BA:
                    isCrit = false;
                    break;
                case Skill.MAKANKOSAPPO:
                    isCrit = false;
                    int dame = getMPFull() / 100 * skillSelect.damage;
                    if (player.inventory.checkSKH(130))
                    {
                        return (int) (dame * 2);
                    }
                    else
                    {
                        return (int) dame;
                    }
            }
            
            if(!player.isBoss && !player.inventory.OptionCt(117)){
                percentDameSkill += player.zone.dameZone(player);
            }
            long dameAttack = getBaseDame();
            if (percentDameSkill != 0)
            {
                dameAttack = dameAttack * percentDameSkill / 100;
            }
            if(!ItemBua.ItemBuaExits(player, 218))
            {
                if (player.point.stamina > 0)
                {
                    player.point.stamina--;
                }
                if(player.isPl())
                {
                    Service.gI().Stamina(player);
                }
            }
            if (skillSelect.template.id == Skill.GALIK && player.inventory.checkSKH(133))
            {
                dameAttack *= 2;
            }
            else if (skillSelect.template.id == Skill.QUA_CAU_KENH_KHI && player.inventory.checkSKH(128))
            {
                dameAttack *= 2;
            }
            else if (skillSelect.template.id == Skill.KAMEJOKO && player.inventory.checkSKH(129))
            {
                dameAttack *= 2;
            }
            else if (skillSelect.template.id == Skill.LIEN_HOAN && player.inventory.checkSKH(131))
            {
                dameAttack *= 2;
            }
            if (skillSelect.template.id != Skill.DICH_CHUYEN_TUC_THOI && skillSelect.template.id != Skill.TROI && skillSelect.template.id != Skill.SOCOLA && skillSelect.template.id != Skill.THOI_MIEN)
            {
                dameAttack += dameAttack * player.playerSkill.don_ke / 100;
                player.playerSkill.don_ke = 0;
            }
            if (player.playerSkill.isMonkey && player.speacial[0] == 2)
            {
                dameAttack += (dameAttack / 100) * player.speacial[1];
            }
            if (isCrit)
            {
                dameAttack += dameAttack / 100 * (100 + player.point.get_percent_option(5));
                if (player.typeFusion == Player.HOP_THE_PORATA2) {
                    dameAttack += dameAttack / 100 * getParam(5, 921);
                }
            }
            if (dameAttack >= 1000000) {
                int r = Util.nextInt(0, 1);
                int point = Util.nextInt(1, 100);
                dameAttack += (dameAttack / 1000) * (1000 + (r == 0 ? -point : point));
            }
            else {
                dameAttack = Util.nextInt((int) ((float) dameAttack * 0.9), (int) ((float) dameAttack * 1.1));
            }
//            if (dameAttack > 10000000) {
//                if (player.role != Setting.ROLE_ADMIN && player.isPl()) {
//                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                    Util.writing("log.txt", "---------------------------------------------------\nPlayer: " + player.name + "\nId: " + player.id + "\nSkill: " + player.playerSkill.skillSelect.template.name + "\nDame: " + Util.getMoneys(dameAttack) + "\nTime: " + timestamp);
//                }
//                if (player.isPl() && dameAttack >= 50000000 && player.role != Setting.ROLE_ADMIN && DHVT.gI().Hour >= 4 && DHVT.gI().Hour <= 5) {
//                    Service.gI().sendThongBaoBenDuoi(player.name + " đã đánh một chiêu " + player.playerSkill.skillSelect.template.name + " với sát thương " + Util.getMoneys(dameAttack));
//                }
//            }
//            if(dameAttack >= 100000000 && player.role != Setting.ROLE_ADMIN && player.isPl()){
//                Service.gI().sendThongBaoBenDuoi(player.name + " đã đánh một chiêu " + player.playerSkill.skillSelect.template.name + " với sát thương " + Util.getMoneys(dameAttack));
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                Util.writing("logBug.txt", "---------------------------------------------------\nPlayer: " + player.name + "\nId: " + player.id + "\nSkill: " + player.playerSkill.skillSelect.template.name + "\nDame: " + Util.getMoneys(dameAttack) + "\nTime: " + timestamp);
//            }
            return Math.abs((int)dameAttack);
        } catch (Exception e) {
//            e.printStackTrace();
            return 0;
        }
    }

    public byte getSpeed() {
        return Setting.TOC_DO_CHAY;
    }
    
    public boolean upPotential(byte type, short point) {
        boolean check = false;
        long tiemNangUse = getUseTiemNang(type, point);
        int points = getPoint(type, point);
        if(tiemNangUse <= 0 || points <= 0){
            return check;
        }
        if (type == 0)
        {
            if ((this.hpGoc + points) <= getHpMpLimit())
            {
                if (useTiemNang(tiemNangUse))
                {
                    hpGoc += points;
                    check = true;
                }
                else
                {
                    Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
                    check = false;
                }
            }
            else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                check = false;
            }
        }
        else if (type == 1) {
            if ((this.mpGoc + points) <= getHpMpLimit())
            {
                if (useTiemNang(tiemNangUse)) {
                    mpGoc += points;
                    check = true;
                }
                else
                {
                    Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
                    check = false;
                }
            }
            else
            {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                check = false;
            }
        }
        else if (type == 2)
        {
            if ((this.dameGoc + points) <= getDameLimit())
            {
                if (useTiemNang(tiemNangUse))
                {
                    dameGoc += points;
                    check = true;
                }
                else{
                    Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
                    check = false;
                }
            }
            else
            {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                check = false;
            }
        }
        else if (type == 3)
        {
            if ((this.defGoc + points) <= getDefLimit())
            {
                if (useTiemNang(tiemNangUse))
                {
                    defGoc += points;
                    check = true;
                }
                else
                {
                    Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
                    check = false;
                }
            }
            else
            {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                check = false;
            }
        }
        else if (type == 4)
        {
            if ((this.critGoc + points) <= getCritLimit())
            {
                if (useTiemNang(tiemNangUse))
                {
                    critGoc += points;
                    check = true;
                }
                else
                {
                    Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
                    check = false;
                }
            }
            else
            {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                check = false;
            }
        }
        if(check == true)
        {
            this.tiemNangUse += tiemNangUse;
            Service.gI().point(player);
        }
        return check;
    }
    
    public long getUseTiemNang(byte type, short point){
        long tiemNangUse = 0;
        int cHPGoc = this.hpGoc;
	int cMPGoc = this.mpGoc;
	int cDamGoc = this.dameGoc;
	int cDefGoc = this.defGoc;
	int cCriticalGoc = this.critGoc;
	int num2 = 1000;
        if(type == 0){ // HP
            if(point == 1 || point == 10 || point == 100){
                int points = point * 20 - 20;
                tiemNangUse = (long)(point * (2 * (cHPGoc + num2) + points) / 2);
            }
            else{
                
            }
        }
        else if(type == 1){ // MP
            if(point == 1 || point == 10 || point == 100){
                int points = point * 20 - 20;
                tiemNangUse = (long)(point * (2 * (cMPGoc + num2) + points) / 2);
            }
            else
            {
                
            }
        }
        else if(type == 2){ // SD
            if(point == 1 || point == 10 || point == 100){
                int points = point - 1;
                tiemNangUse = (long)(point * (2 * cDamGoc + points) / 2 * 100);
            }
            else{
                
            }
        }
        else if(type == 3){ // GIAP
            if(point == 1 || point == 10 || point == 100){
                int points = point - 1;
                tiemNangUse = point * (long)(2 * (cDefGoc + 5) + points) / 2 * 100000;
            }
            else{
                
            }
        }
        else if(type == 4)
        { // CHI MANG
            long[] t_tiemnang = new long[]
            {
                    50000000L,
                    250000000L,
                    1250000000L,
                    5000000000L,
                    15000000000L,
                    30000000000L,
                    45000000000L,
                    60000000000L,
                    75000000000L,
                    90000000000L,
                    110000000000L,
                    130000000000L,
                    150000000000L,
                    170000000000L
            };
            if (cCriticalGoc > t_tiemnang.length - 1)
            {
		cCriticalGoc = t_tiemnang.length - 1;
            }
            if(point == 1)
            {
                tiemNangUse = t_tiemnang[cCriticalGoc];
            }
            else
            {
                
            }
        }
        return tiemNangUse;
    }
    
    public int getPoint(byte type, short point){
        int points = 0;
        if(type == 0 || type == 1){
            if(point == 1 || point == 10 || point == 100){
                points = point * 20;
            }
            else{
                
            }
        }
        else if(type == 2){
            if(point == 1 || point == 10 || point == 100){
                points = point;
            }
            else{
                
            }
        }
        else if(type == 3){
            if(point == 1 || point == 10 || point == 100){
                points = point;
            }
            else{
                
            }
        }
        else if(type == 4){
            if(point == 1){
                points = point;
            }
            else{
                
            }
        }
        return points;
    }

    public boolean useTiemNang(long tiemNang) {
        if(this.tiemNang <= 0 || tiemNang <= 0){
            return false;
        }
        if (this.tiemNang < tiemNang) {
            return false;
        }else if (this.tiemNang >= tiemNang) {
            if (player.taskId == 3) {
                Service.gI().send_task_next(player);
            }
            this.tiemNang -= tiemNang;
            return true;
        }
        return false;
    }

    public void setHpMp(int hp, int mp) {
        setHP(hp);
        setMP(mp);
    }

    public void updateall() {
        this.mp = getMPFull();
        this.hp = getHPFull();
        if (player.isPl()) {
            Service.gI().point(player);
        }
    }

    //--------------------------------------------------------------------------
    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 220000;
        }
        if (limitPower == 1) {
            return 240000;
        }
        if (limitPower == 2) {
            return 300000;
        }
        if (limitPower == 3) {
            return 350000;
        }
        if (limitPower == 4) {
            return 400000;
        }
        if (limitPower == 5) {
            return 450000;
        }
        if (limitPower == 6) {
            return 500000;
        }
        if (limitPower == 7) {
            return 525000;
        }
        if (limitPower == 8) {
            return 550000;
        }
        if (limitPower == 9) {
            return 600000;
        }
        return 0;
    }

    public int getDameLimit() {
        if (limitPower == 0) {
            return 11000;
        }
        if (limitPower == 1) {
            return 12000;
        }
        if (limitPower == 2) {
            return 15000;
        }
        if (limitPower == 3) {
            return 18000;
        }
        if (limitPower == 4) {
            return 20000;
        }
        if (limitPower == 5) {
            return 22000;
        }
        if (limitPower == 6) {
            return 24000;
        }
        if (limitPower == 7) {
            return 24500;
        }
        if (limitPower == 8) {
            return 25000;
        }
        if (limitPower == 9) {
            return 25500;
        }
        return 0;
    }

    public short getDefLimit() {
        if (limitPower == 0) {
            return 550;
        }
        if (limitPower == 1) {
            return 600;
        }
        if (limitPower == 2) {
            return 700;
        }
        if (limitPower == 3) {
            return 800;
        }
        if (limitPower == 4) {
            return 1000;
        }
        if (limitPower == 5) {
            return 1200;
        }
        if (limitPower == 6) {
            return 1400;
        }
        if (limitPower == 7) {
            return 1500;
        }
        if (limitPower == 8) {
            return 1600;
        }
        if (limitPower == 9) {
            return 1700;
        }
        return 0;
    }

    public byte getCritLimit() {
        if (limitPower == 0) {
            return 5;
        }
        if (limitPower == 1) {
            return 6;
        }
        if (limitPower == 2) {
            return 7;
        }
        if (limitPower == 3) {
            return 8;
        }
        if (limitPower == 4) {
            return 9;
        }
        if (limitPower == 5) {
            return 10;
        }
        if (limitPower == 6) {
            return 11;
        }
        if (limitPower == 7) {
            return 12;
        }
        if (limitPower == 8) {
            return 12;
        }
        if (limitPower == 9) {
            return 13;
        }
        return 0;
    }
    
    
}
