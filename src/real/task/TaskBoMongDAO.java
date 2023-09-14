package real.task;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import real.player.Player;
import server.SQLManager;
import service.Setting;

public class TaskBoMongDAO {

    public static ArrayList<TaskBoMong> load() {
        ArrayList<TaskBoMong> tasks = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM `task_bomong_template`");
            while (rs.next()) {
                tasks.add(new TaskBoMong(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
            rs.close();
            rs = null;

        } catch (Exception e) {
        }
        return tasks;
    }

    public static void load_task_player(Player pl) {
        ResultSet rs = null;
        try {
            rs = SQLManager.executeQuery("SELECT * FROM `task_bomong` WHERE `player_id`=" + (pl.id));
            rs.last();
            int size = TaskBoMongManager.tasks.size();
            pl.bo_mong_finish = new byte[size];
            pl.bo_mong_reviece = new byte[size];

            if (rs.getRow() == 0) {
                SQLManager.execute("DELETE  FROM `task_bomong` WHERE `player_id`=" + (pl.id));
                int i = 0;
                while (i < size) {
                    SQLManager.execute("INSERT INTO task_bomong VALUES (" + i + "," + (pl.id) + ",0,0)");
                    i++;
                }
            } else {
                rs = SQLManager.executeQuery("SELECT * FROM `task_bomong` WHERE `player_id`=" + (pl.id));
                while (rs.next()) {
                    int id = rs.getInt(1);
                    pl.bo_mong_finish[id] = rs.getByte(3);
                    pl.bo_mong_reviece[id] = rs.getByte(4);
                }
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void update_task_process(Player pl, int task_id) {
        try {
            SQLManager.executeUpdate("UPDATE `task_bomong` SET isFinish=?, isRecieve=?  WHERE player_id`=" + (pl.id) + " AND `task_id`=" + task_id);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
