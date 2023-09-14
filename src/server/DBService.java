package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import service.Setting;

public class DBService {

    private static DBService instance;

    Connection conn = null;

    public static DBService gI() {
        if (instance == null) {
            instance = new DBService();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                String url = "jdbc:mysql://" + Setting.DB_HOST + "/" + Setting.DB_DATE + "?useUnicode=true&characterEncoding=utf-8";
                conn = DriverManager.getConnection(url, Setting.DB_USER, Setting.DB_PASS);
                conn.setAutoCommit(false);
            }
        } catch (Exception e) {
//           e.printStackTrace();
            return getConnection();
        }
        return conn;
    }

    @FunctionalInterface
    public interface MyConsumer {

        void accept(ResultSet r) throws Exception;
    }

    public static void executeQuery(String query, MyConsumer consumer) {
        try {
            consumer.accept(gI().getConnection().createStatement().executeQuery(query));
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection cn = DBService.gI().getConnection();
        if (cn != null) {
            System.out.println("ok");
        } else {
            System.out.println("lỗi");
        }
    }

    public static int resultSize(ResultSet rs) {
        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
            rs.beforeFirst();
        } catch (SQLException e) {
        }
        return count;
    }
}
