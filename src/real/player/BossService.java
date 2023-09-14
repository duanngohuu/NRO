package real.player;

import java.util.ArrayList;
import java.util.List;

public class BossService {
    public List<BossTemplate> players = new ArrayList<>();
    
    private static BossService instance;
    public static BossService gI() {
        if (instance == null) {
            instance = new BossService();
        }
        return instance;
    }
}
