package real.radar;

import java.util.ArrayList;
import java.util.List;
import real.item.ItemOption;

public class RadarTemplate {
    public int id;
    public int icon;
    public int rank;
    public int max;
    public int type;
    public int template = -1;
    public String name;
    public String info;
    
    public int aura;
    public List<ItemOption> itemOptions = new ArrayList<>();
    // THUOC VE PLAYER
    public int count = 0;
    public int user = 0;
    public int level = 0;
}
