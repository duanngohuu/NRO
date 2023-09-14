package service;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import real.func.DHVT;
import server.ServerManager;
import server.Util;


public class Setting {

    // ------------------------- server setting -------------------------
    public static int PORT = 14445;
    public static int MAX_IP = 5;
    public static final int PLAYER_ID = 1000000;
    public static final byte TIME_BAOTRI = 4;
    public static final byte TIME_TICK = 30;
    public static boolean SERVER_TEST = false;
    public static boolean IN_LOG = true;
                                            //127.0.0.1 - 14.161.0.85
    public static String LIST_SERVER = "MAD:127.0.0.1:14445:0,0,0";
    public static String SERVER_NAME = "MAD:127.0.0.1:14445:0,0,0";
    //-------------------------- sql setting ----------------------------
    public static String DB_HOST ;
    public static String DB_DATE ;
    public static String DB_NICK ;
    public static String DB_USER ;
    public static String DB_PASS ;
    
    // SOURCE
    public static final int VERSION_IMAGE_SOURCE = 5714090;
    public static byte vsData = 51;
    public static byte vsMap = 25;
    public static byte vsSkill = 6;
    public static byte vsItem = 100;

    // ------------------------- role -------------------------
    public static final byte ROLE_ADMIN_VIP = 100;
    public static final byte ROLE_ADMIN = 99;
    public static final byte ROLE_BAN_ACC = 1;
    public static final byte ROLE_BAN_CTG = 2;
    public static final byte ROLE_BAN_CHAT = 3;
    public static final byte ROLE_BAN_ALL_CHAT = 4;

    public static final int DONATE_EXP = 5; // X EXP
    public static final int GOLD_ROUND = 25000000;
    public static final int GOLD_DHVT23 = 50000;
    public static final int DAY_NEW = 30;

    public static final int MAX_STAMINA = 10000;
    public static final int MAX_STAMINA_FOR_PET = 1100;

    public static final int GOLD_OPEN_SPEACIAL = 10000000;
    public static final int GEM_OPEN_SPEACIAL = 100;

    public static final int DELAY_PLAYER = 1000;
    public static final int DELAY_PET = 500;

    public static final int DELAY_UPDATE_DATA_BASE = 600000;

    public static final int DELAY_UPDATE_MOB = 250;
    public static final int DELAY_MOB_ATTACK = 1000;
    
    // Player
    public static final int TOC_DO_CHAY = 9;
    public static final int SIZE_BODY_PLAYER = 10;
    public static final int SIZE_BODY_PET = 6;
    public static final int ITEM_START = 8;
    public static final int ITEM_LEVEL = 8;
    
    public static final int MAX_SMALL = 32767;
    public static final int GOLD_SELL_TV_1 = 500000000;
    public static final int GOLD_SELL_TV_2 = 50000000;
    // cong chi so
    public static final int HP_FROM_1000_TN = 20;
    public static final int MP_FROM_1000_TN = 20;
    public static final int SD_FROM_1000_TN = 1;
    public static final int EXP_FOR_ONE_ADD = 100;
    
    public static final int GOLD_OPEN_GHSM = 500000000;
    public static final long[] LIMIT_SUC_MANH = 
    {
        17999999999L,
        19999999999L,
        24999999999L,
        29999999999L,
        34999999999L,
        39999999999L,
        50010000000L,
        60010000000L,
        80010000000L,
        120010000000L,
    };

    // rong than trái đất
    public static final int TIME_RECALL_DRAGON = 600000;
    public static final int TIME_DRAGON_WAIT = 300000;
    public static final int COUNT_SUMMON_DRAGON = 50000;
    // rong than băng
    public static final int TIME_RECALL_DRAGON_BANG = 300000;
    public static final int TIME_DRAGON_WAIT_BANG = 300000;
    // Chat
    public static boolean LOG_CHAT_GLOBAL = false;
    // kí gửi
    public static int ITEM_ID = 1000;
    public static final int GOLD_MIN_SELL_KI_GUI = 10000;
    public static final int GOLD_MAX_SELL_KI_GUI = 500000000;
    public static final int NGOC_MIN_SELL_KI_GUI = 2;
    public static final int NGOC_MAX_SELL_KI_GUI = 20000;
    // Mini game
    public static final byte TIME_START_GAME = 8;
    public static final byte TIME_END_GAME = 22;
    public static final byte TIME_MINUTES_GAME = 5;
    // nrsd
    public static final byte TIME_START = 20;
    public static final byte TIME_END = 21;
    public static final byte TIME_PICK = 30;
    public static final int TIME_WIN_NRSD = 300000;
    // hirudegarn
    public static final byte TIME_START_HIRU_1 = 10;
    public static final byte TIME_START_HIRU_2 = 22;
    // osin mabu
    public static final byte TIME_START_OSIN_1 = 12;
    public static final byte TIME_START_OSIN_2 = 18;
    
    // open super tỉ lệ
    public static final byte TIME_START_SUPER = 17;
    // phụ bản
    public static final int ZONE_PHU_BAN = 10;
    // ho tro nv
    public static boolean HO_TRO_TDST = false;
    
    // Event
    public static final boolean EVENT_SPEACIAL = false;
    public static final boolean EVENT_HALLOWEEN = false;
    public static final boolean EVENT_TRUNG_THU = true;
    public static final boolean EVENT_GIANG_SINH = false;
    // Event 2023
    public static boolean isNEW_2023()
    {
        if(DHVT.gI().Year == 2020)
        {
            if(DHVT.gI().Month == 1 && DHVT.gI().Day >= 16)
            {
                return true;
            }
            else if(DHVT.gI().Month == 2 && DHVT.gI().Day <= 16)
            {
                return true;
            }
        }
        return false;
    }
    
    public static void loadConfigFile() {
        final byte[] ab = Util.getFile("mad.conf");
        if (ab == null) {
            System.out.println("Config file not found!");
            System.exit(0);
        }
        final String data = new String(ab);
        final HashMap<String, String> configMap = new HashMap<String, String>();
        final StringBuilder sbd = new StringBuilder();
        boolean bo = false;
        for (int i = 0; i <= data.length(); ++i) {
            final char es;
            if (i == data.length() || (es = data.charAt(i)) == '\n') {
                bo = false;
                final String sbf = sbd.toString().trim();
                if (!sbf.isEmpty() && sbf.charAt(0) != '#') {
                    final int j = sbf.indexOf(':');
                    if (j > 0) {
                        final String key = sbf.substring(0, j).trim();
                        final String value = sbf.substring(j + 1).trim();
                        configMap.put(key, value);
                        System.out.println(Util.BLUE+"Config: "+Util.RESET + key + "-" + value);
                    }
                }
                sbd.setLength(0);
            }
            else {
                if (es == '#') {
                    bo = true;
                }
                if (!bo) {
                    sbd.append(es);
                }
            }
        }
       
        SERVER_TEST = configMap.containsKey("server_test") && Util.checkTest(Boolean.parseBoolean(configMap.get("server_test")));
        PORT = configMap.containsKey("port") ? Short.parseShort(configMap.get("port")) : 14445;
        MAX_IP = configMap.containsKey("maxIp") ? Short.parseShort(configMap.get("maxIp")) : 5;
        
        DB_HOST = configMap.containsKey("mysql-host") ? configMap.get("mysql-host") : "localhost";
        DB_NICK = configMap.containsKey("sql_acc") ? configMap.get("sql_acc") : "nro_acc";
        DB_DATE = configMap.containsKey("sql_data") ? configMap.get("sql_data") : "nro_data";
        DB_USER = configMap.containsKey("mysql-user") ? configMap.get("mysql-user") : "root";
        DB_PASS = configMap.containsKey("mysql-password") ? configMap.get("mysql-password") : "";
        
        LIST_SERVER = configMap.containsKey("list_server") ? configMap.get("list_server") : "MAD:127.0.0.1:14445:0,0,0";
        SERVER_NAME = configMap.containsKey("server_name") ? configMap.get("server_name") : "MAD:127.0.0.1:14445:0,0,0";



    }
   
   

}

