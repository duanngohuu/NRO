package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import real.map.Mob;
import real.player.Player;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import real.npc.Npc;
import service.Setting;
public class Util {

    private static final Random rand;
    private static final SimpleDateFormat dateFormat;
    
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE
    
    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE

    static NumberFormat numberFormat;
    static Locale locale;

    static {
        locale = new Locale("vi", "VN");
        rand = new Random();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        numberFormat = NumberFormat.getInstance(locale);
    }
    
    public static List<Integer> getValue(List<Integer> arr ,int startElement, int lastElement){
        List<Integer> listnew = new ArrayList<>();
        for(int i = startElement ; i <= lastElement ;i++){
            if(arr != null && arr.get(i) != null){
                listnew.add(arr.get(i));
            }
        }
        return listnew;
    }

    public static boolean existsFile(String path){
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) { 
            return true;
        }
        return false;
    }
    
    public Timestamp TimeNow() {
        return Timestamp.from(Instant.now());
    }
     
    public static long TimePhuBan(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 07:00:00");
        java.util.Date date = new java.util.Date();    
        String strDate = formatter.format(date);
        Timestamp timenow = Timestamp.valueOf(strDate);
        return timenow.getTime();
    }
    
    public static long TimeTask(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        java.util.Date date = new java.util.Date();    
        String strDate = formatter.format(date);
        Timestamp timenow = Timestamp.valueOf(strDate);
        return timenow.getTime();
    }
    
    public static long TimeFormat(String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        java.util.Date date = new java.util.Date();    
        String strDate = formatter.format(date);
        Timestamp timenow = Timestamp.valueOf(strDate);
        return timenow.getTime();
    }

    public static String getMoneys(long l) {
        return numberFormat.format(l);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static int[] parseIntArray(String[] arr) {
        return Stream.of(arr).mapToInt(Integer::parseInt).toArray();
    }
    
    public static byte[] removeTheElement(byte[] arr, int index) {
        if (arr == null || index < 0
                || index >= arr.length) {

            return arr;
        }
        byte[] anotherArray = new byte[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i == index) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }

    public static void writing(String path, String text) {
        PrintWriter printWriter = null;
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            printWriter = new PrintWriter(new FileOutputStream(path, true));
            printWriter.write(System.lineSeparator() + text);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
    }
 public static boolean checkTest(boolean condition) {
    // Kiểm tra điều kiện
    if (condition) {
        return true;
    } else {
        return false;
    }}
  public static byte[] getFile(final String url) {
        try {
            final FileInputStream openFileInput = new FileInputStream(url);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] bArr = new byte[1024];
            while (true) {
                final int read = openFileInput.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            openFileInput.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static <T> boolean checkForDuplicates(List<T> array)
    {
        for (int i = 0; i < array.size(); i++){
            for (int j = i + 1; j < array.size(); j++){
                if (array.get(i) != null && array.get(i).equals(array.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static int countDuplicate(Vector<String> arr, String key) {
        try {
            return Collections.frequency(arr, key);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean CheckString(String str, String c) {
        return Pattern.compile(c).matcher(str).find();
    }

    public static boolean contains(String[] arr, String key) {
        if (Arrays.toString(arr).contains(key)) {
            return true;
        }
        return false;
    }

    public static String powerToString(long power) {
        numberFormat.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return numberFormat.format((double) power / 1000000000) + "Tỷ";
        } else if (power >= 1000000) {
            return numberFormat.format((double) power / 1000000) + "Tr";
        } else if (power >= 1000) {
            return numberFormat.format((double) power / 1000) + "k";
        } else {
            return numberFormat.format(power);
        }
    }
    
    public static long IntToLong(long power) {
        numberFormat.setMaximumFractionDigits(1);
        if (power >= 1000) {
            return power / 1000;
        }
        else
        {
            return power;
        }
    }

    public static String convertSeconds(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                int days = hours / 24;
                return String.format("%dd%02dh%02d'", days, hours % 24, minutes);
            }
            return String.format("%02dh%02d'", hours, minutes);
        }
        return String.format("%02d'", minutes);
    }

    public static int timeToInt(int d, int h, int m) {
        int result = 0;
        try {
            if (d > 0) {
                result += (60 * 60 * 24 * d);
            }
            if (h > 0) {
                result += 60 * 60 * h;
            }
            if (m > 0) {
                result += 60 * m;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return result;
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        int num = 0;
        try
        {
            num = (int)Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        }
        catch(Exception e){
            Util.logException(Util.class, e, "getDistance(int x1, int y1, int x2, int y2)");
        }
        return num;
    }

    public static int getDistance(Player pl1, Player pl2) {
        return getDistance(pl1.x, pl1.y, pl2.x, pl2.y);
    }

    public static int getDistance(Player pl, Mob mob) {
        return getDistance(pl.x, pl.y, mob.cx, mob.cy);
    }

    public static int nextInt(int from, int to) {
        if(from > to){
            return from;
        }
        try {
            return from + rand.nextInt(to - from + 1);
        } catch (Exception e) {
            Util.debug("ERROR Util.nextInt("+from+", "+to+")");
        }
        return from;
    }

    public static double RandomNumber(double minimum, double maximum)
    { 
        return rand.nextDouble()* (maximum - minimum) + minimum;
    }
    
    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static double nextdouble(int max) {
        return rand.nextDouble() * max;
    }

    public static int getRandomElement(int[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000), i;
        for (i = 0; i < percen.length; i++) {
            if (next < percen[i]) {
                return i;
            }
            next -= percen[i];
        }
        return i;
    }

    public static long currentTimeSec() {
        return System.currentTimeMillis() / 1000;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static void log(String message) {
        if(!Setting.IN_LOG)
        {
            return;
        }
        System.out.println(message);
    }

    public static void debug(String message) {
        if(!Setting.IN_LOG)
        {
            return;
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String strDate = formatter.format(date);
        try {
            System.out.println(strDate + ": " + message);
        } catch (Exception e) {
            System.out.println(strDate + ": " + message);
        }
    }
    
    public static void log(String color, String text){
        System.out.print(color + text + RESET);
    }
    public static void success(String text) {
        System.out.print(GREEN_BOLD + text + RESET);
    }

    public static void warning(String text) {
        System.out.print(PURPLE + text + RESET);
    }

    public static void error(String text) {
        System.out.print(BLUE + text + RESET);
    }

    public static void logException(Class clazz, Exception ex, String... log) {
        try {
            if(log != null && log.length > 0){
                log(PURPLE, log[0] + "\n");
            }
            StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
            String nameMethod = stackTraceElements[1].getMethodName();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String detail = sw.toString();
            String[] arr = detail.split("\n");
            warning("Có lỗi tại class: ");
            error(clazz.getName());
            warning(" - tại phương thức: ");
            error(nameMethod + "\n");
            warning("Chi tiết lỗi:\n");
            for (String str : arr) {
                error(str + "\n");
            }
            log("--------------------------------------------------------\n");
        } catch (Exception e) {
        }
    }
    
    public static boolean isTrue(int ratio) {
        int num = Util.nextInt(100);
        if (num <= ratio) {
            return true;
        }
        return false;
    }
    
    public static boolean isComTrue(int ratio) {
        int num = Util.nextInt(180);
        if (num <= ratio) {
            return true;
        }
        return false;
    }

    public static boolean canDoWithTime(long lastTime, long timeTarget) {
        return System.currentTimeMillis() - lastTime >= timeTarget;
    }

    private static final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
        'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
        'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
        'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
        'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
        'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
        'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
        'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
        'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
        'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
        'ữ', 'Ự', 'ự',};

    private static final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
        'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
        'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
        'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
        'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
        'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
        'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
        'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
        'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
        'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
        'U', 'u', 'U', 'u',};

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }
    
    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }
    public static boolean isStringInt(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public static String readFile(String path){
        String data = "";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              data = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
        }
        return data;
    }
    
    public static long findDayDifference(long start_date,long end_date)
    {
        long difference_In_Time = start_date - end_date;
        long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
        return difference_In_Days;
    }
    
    public static long findMinutesDifference(long start_date,long end_date)
    {
        long difference_In_Time = start_date - end_date;
        long difference_In_Minutes = (difference_In_Time / 1000 * 60) % 60;
        return difference_In_Minutes;
    }
    
    public static long findHoursDifference(long start_date, long end_date)
    {
        long difference_In_Time = start_date - end_date;
        long difference_In_Hours= (difference_In_Time / (1000 * 60 * 60)) % 24;
        return difference_In_Hours;
    }
    
    public static int[][] ConvertArrInt(String arr, String regex2)
    {
        String[] arr1 = arr.split("\n");
        int[][] temp = new int[arr1.length][];
        for(int i = 0; i < arr1.length; i++){
            String[] arr2 = arr1[i].split(regex2);
            temp[i] = Stream.of(arr2).mapToInt(Integer::parseInt).toArray();
        }
        return temp;
    }
    
    public static int[] ConvertInt(String arr)
    {
        String[] arr1 = arr.split("\n");
        int[] temp = new int[arr1.length];
        for(int i = 0; i < arr1.length; i++){
            temp[i] = Integer.parseInt(arr1[i]);
        }
        return temp;
    }
    
    public static long[] ConvertLong(String arr)
    {
        String[] arr1 = arr.split("\n");
        long[] temp = new long[arr1.length];
        for(int i = 0; i < arr1.length; i++){
            temp[i] = Integer.parseInt(arr1[i]);
        }
        return temp;
    }
    
    public static String timeAgo(long start_date, long end_date)
    {
        long time_go = start_date - end_date;
        if(end_date == 0){
            return "-/-/-";
        }
        long seconds = time_go;
        long minutes = time_go / 60;
        long hours = time_go / 3600;
        long days = time_go / 86400;
        long weeks = time_go / 604800;
        long months = time_go / 2600640;
        long years = time_go / 31207680;
        if(seconds < 60){
            return seconds + " giây trước";
        }
        else if(minutes < 60){
            return minutes + " phút trước";
        }
        else if(hours < 24){
            return hours + " giờ trước";
        }
        else if(days < 7){
            return days + " ngày trước";
        }
        else if(weeks < 5){
            return weeks + " tuần trước";
        }
        else if(months < 12){
            return months + " tháng trước";
        }
        else if(years > 0){
            return years + " năm trước";
        }
        return "-/-/-";
    }
}
