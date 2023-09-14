package real.item;

public class ItemOptionShop {

    public int npcId;
    public int itemTemplateId;
    public int optionId;
    public int param;

    public ItemOptionShop() {
    }

    public ItemOptionShop(int npcId, int itemTemplateId, int optionId, int param) {
        this.npcId = npcId;
        this.itemTemplateId = itemTemplateId;
        this.optionId = optionId;
        this.param = param;
    }

    public ItemOptionShop(int itemTemplateId, int optionId, int param) {
        this.itemTemplateId = itemTemplateId;
        this.optionId = optionId;
        this.param = param;
    }
}
