package real.npc;

import java.sql.ResultSet;
import java.util.Vector;
import server.SQLManager;
import server.Util;

public class NpcTemplateData {
    public Vector<NpcTemplate> temps;

    public NpcTemplateData() {
        temps = new Vector<>();
    }
    private static NpcTemplateData instance;

    public static NpcTemplateData gI() {
        if (instance == null) {
            instance = new NpcTemplateData();
        }
        return instance;
    }

    public void init() {
        temps.clear();
        load_npc();
        Util.warning("Finish load npc! [" + temps.size() + "]\n");
    }

    public NpcTemplate getTemp(int id)
    {
        return temps.get(id);
    }

    public void load_npc() {
        ResultSet rs;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM `npc_template` ORDER BY id ASC");
            while (rs.next()) {
                NpcTemplate npc = new NpcTemplate();
                npc.npcTemplateId = rs.getInt("id");
                npc.name = rs.getString("name");
                npc.headId = rs.getInt("head");
                npc.bodyId = rs.getInt("body");
                npc.legId = rs.getInt("leg");
                temps.add(npc);
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
        }
    }
}
