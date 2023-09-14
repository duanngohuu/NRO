package real.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.SQLManager;
import server.Util;

public class BossDAO {
    public static void loadBoss(){
        BossService.gI().players.clear();
        ResultSet rs = null;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM map_boss");
            while (rs.next()) {
                BossTemplate temp = new BossTemplate();
                temp.name = rs.getString("name");
                temp.head = rs.getShort("head");
                temp.body = rs.getShort("body");
                temp.leg = rs.getShort("leg");
                
                temp.map = rs.getInt("map");
                temp.cx = rs.getInt("cx");
                temp.cy = rs.getInt("cy");
                
                temp.dame = rs.getInt("dame");
                temp.hp = rs.getInt("hp");
                temp.def = rs.getShort("def");
                
                temp.tile = rs.getInt("tile");
                
                temp.status = rs.getInt("status");
                BossService.gI().players.add(temp);
            }
            rs.close();
            rs = null;
            Util.warning("finish load boss! [" + BossService.gI().players.size() + "]\n");
        } catch (SQLException e) {
        }
    }
}
