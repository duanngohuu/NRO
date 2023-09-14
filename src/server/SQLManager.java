package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import service.Setting;

public class SQLManager {

    protected static Connection connDATA;
    protected static Connection connNICK;
    protected static Connection connFROM;

    public static synchronized void create()
    {
        final String url = "jdbc:mysql://" + Setting.DB_HOST + "/" + Setting.DB_NICK + "?useUnicode=true&characterEncoding=utf-8";
        try
        {
            SQLManager.connNICK = DriverManager.getConnection(url, Setting.DB_USER, Setting.DB_PASS);
        }
        catch (SQLException e)
        {
            Util.logException(SQLManager.class, e);
            System.exit(0);
        }
    }
    
    public static synchronized void createDATA()
    {
        final String url = "jdbc:mysql://" + Setting.DB_HOST + "/" + Setting.DB_DATE + "?useUnicode=true&characterEncoding=utf-8";
        try
        {
            SQLManager.connDATA = DriverManager.getConnection(url, Setting.DB_USER, Setting.DB_PASS);
        }
        catch (SQLException e) {
            Util.logException(SQLManager.class, e);
            System.exit(0);
        }
    }
    
    public static synchronized void createFROM()
    {
        final String url = "jdbc:mysql://" + Setting.DB_HOST + "/" + Setting.DB_NICK + "?useUnicode=true&characterEncoding=utf-8";
        try
        {
            SQLManager.connFROM = DriverManager.getConnection(url, Setting.DB_USER, Setting.DB_PASS);
        }
        catch (SQLException e)
        {
            Util.logException(SQLManager.class, e);
        }
    }
    
    // SQL NICK
    public static boolean execute(String sql) {
        try
        {
            return connNICK.createStatement().execute(sql);
        }
        catch (SQLException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet executeQuery(String sql) {
        try {
            return connNICK.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int executeUpdate(String sql) {
        try {
            return connNICK.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int execute(String sql, int id) {
        try {
            Statement s = connNICK.createStatement();
            s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = s.getGeneratedKeys();
            rs.first();
            final int r = rs.getInt(id);
            rs.close();
            s.close();
            s = null;
            rs = null;
            return r;
        } catch (SQLException e) {
//            e.printStackTrace();
            return -1;
        }
    }

    public static synchronized boolean close() {
        try
        {
            if (connNICK != null) {
                connNICK.close();
            }
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }
    
    // SQL DATA SERVER
    public static boolean executeDATA(String sql) {
        try {
            Statement s = connDATA.createStatement();
            boolean r = s.execute(sql);
            s.close();
            s = null;
            return r;
        } catch (SQLException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet executeQueryDATA(String sql) {
        try {
            return connDATA.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int executeUpdateDATA(String sql) {
        try {
            Statement s = connDATA.createStatement();
            final int r = s.executeUpdate(sql);
            s.close();
            s = null;
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int executeDATA(String sql, int id) {
        try
        {
            Statement s = connDATA.createStatement();
            s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = s.getGeneratedKeys();
            rs.first();
            final int r = rs.getInt(id);
            rs.close();
            s.close();
            s = null;
            rs = null;
            return r;
        }
        catch (SQLException e) {
//            e.printStackTrace();
            return -1;
        }
    }

    public static synchronized boolean closeDATA() {
        try {
            if (connDATA != null) {
                connDATA.close();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static boolean executeFROM(String sql) {
        try
        {
            boolean isRequest = connFROM.createStatement().execute(sql);
            return isRequest;
        }
        catch (SQLException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet executeQueryFROM(String sql) {
        try {
            ResultSet Request = connFROM.createStatement().executeQuery(sql);
            return Request;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int executeUpdateFROM(String sql) {
        try {
            int Request = connFROM.createStatement().executeUpdate(sql);
            return Request;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int executeFROM(String sql, int id) {
        try {
            Statement s = connFROM.createStatement();
            s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = s.getGeneratedKeys();
            rs.first();
            final int r = rs.getInt(id);
            rs.close();
            s.close();
            s = null;
            rs = null;
            return r;
        } catch (SQLException e) {
//            e.printStackTrace();
            return -1;
        }
    }
    
    public static synchronized boolean closeFROM() {
        try {
            if (connFROM != null) {
                connFROM.close();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
