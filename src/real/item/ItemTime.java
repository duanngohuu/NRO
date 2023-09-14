package real.item;

public class ItemTime {

    public String text = null;
    public int time = 0;
    public int iconID = -1;

    public ItemTime(int iconID, int time) {
        this.iconID = iconID;
        this.time = time;
    }
    
    public ItemTime(int iconID, String text, int time) {
        this.iconID = iconID;
        this.time = time;
        this.text = text;
    }
}
