package real.item;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.SQLManager;
import server.Util;

public class CaiTrangData {

    private static List<CaiTrang> caiTrangs;

    public static CaiTrang getCaiTrangByTempID(int tempId) {
        for (CaiTrang ct : caiTrangs) {
            if (ct.tempId == tempId) {
                return ct;
            }
        }
        return null;
    }

    public static void loadCaiTrang()
    {
        caiTrangs = null;
        caiTrangs = new ArrayList<>();
        try
        {
            ResultSet rs = SQLManager.executeQueryDATA("select * from cai_trang");
            while (rs.next()) {
                caiTrangs.add(new CaiTrang(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
            }
            rs.close();
            rs = null;
            Util.warning("Finish load skin! [" + caiTrangs.size() + "]\n");
        }
        catch (Exception e)
        {
            Util.logException(CaiTrangData.class, e);
        }
    }

    public static class CaiTrang {

        private int tempId;
        private int[] id;

        private CaiTrang(int tempId, int... id) {
            this.tempId = tempId;
            this.id = id;
        }

        public int[] getID() {
            return id;
        }
    }

    public static void main(String[] args) {

    }

}
