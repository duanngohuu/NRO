package real.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import real.player.Player;
import server.DBService;

public class TaskBoMong {

    public int id;
    public String info1;
    public String info2;
    public int money;

    public TaskBoMong() {
    }

    public TaskBoMong(int i, String i1, String i2, int m) {
        id = i;
        info1 = i1;
        info2 = i2;
        money = m;
    }
}
