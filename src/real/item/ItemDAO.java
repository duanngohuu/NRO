package real.item;

import java.sql.ResultSet;
import server.SQLManager;
import server.Util;
public class ItemDAO {

    public static void load_default_option() {
        ItemData.gI().default_option_item = new ItemOption[ItemData.gI().itemTemplates.size()][];
        ResultSet rs = null;
        for (int i = 0; i < ItemData.gI().itemTemplates.size(); i++) {
            try {
                rs = SQLManager.executeQueryDATA("SELECT DISTINCT * FROM `default_option_item` WHERE `temp_id`='" + i + "'");
                rs.last();
                int size = rs.getRow();
                if (size > 0) {
                    rs.first();
                    int z = 0;
                    ItemData.gI().default_option_item[i] = new ItemOption[size];
                    do {
                        ItemData.gI().default_option_item[i][z] = new ItemOption(rs.getInt(2), (short) rs.getInt(3));
                        z++;
                    }while (rs.next());
                }else{
                    ItemData.gI().default_option_item[i] = new ItemOption[1];
                    ItemData.gI().default_option_item[i][0] = new ItemOption(73, (short) 0);
                }
                rs.close();
            }catch(Exception e){
                Util.log("---load_default_option---");
                e.printStackTrace();
            }
        }
        rs = null;
    }
}
