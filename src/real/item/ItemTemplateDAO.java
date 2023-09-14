package real.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import server.DBService;
import server.SQLManager;
import server.Util;

public class ItemTemplateDAO {

    public static Vector getAll() {
        Vector<ItemTemplate> list = new Vector<>();
        try {
            ResultSet rs = SQLManager.executeQueryDATA("select * from item_template order by id");
            while (rs.next()) {
                list.add(new ItemTemplate(rs.getShort(1), rs.getByte(2),
                        rs.getByte(3), rs.getString(4), rs.getString(5), rs.getShort(6), rs.getShort(7), rs.getInt(8) == 1, rs.getInt(9), rs.getInt(10)));
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
//            e.printStackTrace();
            Util.log("Tải item template thất bại!");
//            e.printStackTrace();
            System.exit(0);
        }
        return list;
    }

    public static void insertList(ArrayList<ItemTemplate> list) {
        System.out.println(list.size());
        try {
            for (ItemTemplate it : list) {
                ResultSet rs = SQLManager.executeQuery("SELECT EXISTS(SELECT * FROM item_template WHERE id = " +it.id +")");
                rs.first();
                if(rs.getInt(1) == 0){
                    SQLManager.execute(String.format("INSERT INTO `item_template` (`id`, `TYPE`, `gender`, `NAME`, `description`, `iconid`, `part`, `isuptoup`, `strrequire`, `level`) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                        it.id, it.type, it.gender, it.name, it.description, it.iconID, it.part , it.isUpToUp ? 1: 0, it.strRequire, it.level));
                }
            }
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readFile() throws FileNotFoundException {
        ArrayList<ItemTemplate> list = new ArrayList<>();
        File folder = new File("E:\\Tool Nro By Me\\GetData\\Dragonboy_vn_v221\\Data Item");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String path = file.getPath();
                BufferedReader br = new BufferedReader(new FileReader(path));
                try {
                    while (true) {
                        ItemTemplate it = new ItemTemplate();
                        for (int i = 0; i < 11; i++) {
                            switch (i) {
                                case 0:
                                    it.id = (short) Integer.parseInt(br.readLine().replaceAll("id: ", ""));
                                    break;
                                case 1:
                                    it.type = (byte) Integer.parseInt(br.readLine().replaceAll("type: ", ""));
                                    break;
                                case 2:
                                    it.gender = (byte) Integer.parseInt(br.readLine().replaceAll("gender: ", ""));
                                    break;
                                case 3:
                                    it.name = br.readLine().replaceAll("name: ", "");
                                    break;
                                case 4:
                                    it.description = br.readLine().replaceAll("description: ", "");
                                    break;
                                case 5:
                                    it.level = Integer.parseInt(br.readLine().replaceAll("level: ", ""));
                                    break;
                                case 6:
                                    it.iconID = (short) Integer.parseInt(br.readLine().replaceAll("iconID: ", ""));
                                    break;
                                case 7:
                                    it.part = (short) Integer.parseInt(br.readLine().replaceAll("part: ", ""));
                                    break;
                                case 8:
                                    it.isUpToUp = Boolean.parseBoolean(br.readLine().replaceAll("isuptoup: ", ""));
                                    break;
                                case 9:
                                    it.strRequire = Integer.parseInt(br.readLine().replaceAll("strRequest: ", ""));
                                    break;
                            }
                        }
                        list.add(it);
                    }
                } catch (Exception e) {
                }
            }
        }
        insertList(list);
    }

    public static void main(String[] args) throws FileNotFoundException {
    }
}
