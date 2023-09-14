package service.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import server.FileIO;
import server.SQLManager;
import server.Util;

public class PartData {
     public static Part[] parts;

    public static void loadPart() {
        try {
            parts = null;
            ResultSet rs = SQLManager.executeQueryDATA("SELECT * FROM part");
            if (rs.last()) {
                parts = new Part[rs.getRow()];
                rs.beforeFirst();
            }
            int i = 0;
            while (rs.next()) {
                byte type = rs.getByte("type");
                JSONArray jA = new JSONArray(rs.getString("py"));
                Part part = new Part(type);
                for (int k = 0; k < part.pi.length; k++) {
                    JSONObject o = (JSONObject) jA.get(k);
                    part.pi[k] = new PartImage();
                    part.pi[k].id = (short) o.getInt("id");
                    part.pi[k].dx = (byte) o.getInt("dx");
                    part.pi[k].dy = (byte) o.getInt("dy");
                }
                parts[i] = part;
                ++i;
            }
            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        createCachePart();
    }
    public static void createCachePart() {
        try
        {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeShort(parts.length);
            for (short i = 0; i < parts.length; ++i) {
                dos.writeByte(parts[i].type);
                for (short j = 0; j < parts[i].pi.length; ++j) {
                    dos.writeShort(parts[i].pi[j].id);
                    dos.writeByte(parts[i].pi[j].dx);
                    dos.writeByte(parts[i].pi[j].dy);
                }
            }
            byte[] ab = bas.toByteArray();
            FileIO.writeFile("data/NR_part", ab);
            Util.warning("Finish load part! [" + parts.length + "]\n");
            dos.close();
            bas.close();
        }
        catch (Exception e)
        {
            Util.logException(PartData.class, e);
        }
    }
}
