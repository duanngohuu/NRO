package real.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import server.Util;

public class TopInfo {
    public int headID;

    public short body;

    public short leg;

    public String name;

    public String info;

    public int pId;

    public long power;
    
    public int money;
    
    public int eventPoint;
    
    public long curr;
    
    public static List<TopInfo> topNap = new ArrayList<TopInfo>();
    public static List<TopInfo> topSM = new ArrayList<TopInfo>();
    public static List<TopInfo> topNV = new ArrayList<TopInfo>();
    public static List<TopInfo> topSuKien = new ArrayList<TopInfo>();
    
    public static int IndexTOP(String name, List<TopInfo> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            TopInfo top = list.get(i);
            if(top.name.equals(name))
            {
                return i + 1;
            }
        }
        return -1;
    }
}
