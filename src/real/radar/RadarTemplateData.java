package real.radar;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import server.SQLManager;
import server.Util;

public class RadarTemplateData {
    private static RadarTemplateData instance;
    
    public List<RadarTemplate> radars = new ArrayList();
    
    public static RadarTemplateData gI() {
        if (instance == null) {
            instance = new RadarTemplateData();
        }
        return instance;
    }
    
    public void load_radar() {
        radars.clear();
        ResultSet rs;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM `radar` ORDER BY id ASC");
            while (rs.next()) {
                RadarTemplate rd = new RadarTemplate();
                rd.id = rs.getInt("id");
                rd.icon = rs.getInt("icon");
                rd.rank = rs.getInt("rank");
                rd.max = rs.getInt("max");
                rd.type = rs.getInt("type");
                rd.template = rs.getInt("template");
                rd.name = rs.getString("name");
                rd.info = rs.getString("info");
                rd.aura = rs.getInt("aura");
                JSONArray js = new JSONArray(rs.getString("option"));
                int size = js.length();
                for(int i = 0 ; i < size;i++){
                    JSONObject ob = js.getJSONObject(i);
                    int idOptions = ob.getInt("id");
                    int param = ob.getInt("param");
                    int active = ob.getInt("activeCard");
                    ItemOption option = new ItemOption(idOptions, param, active);
                    rd.itemOptions.add(option);
                }
                radars.add(rd);
            }
            rs.close();
            rs = null;
            Util.warning("finish load radar! [" + radars.size() + "]\n");
        } catch (Exception e) {
        }
       
    }
}
