package real.task;

public class TaskOrders {
    public int taskId = -1;

    public int count = -1;

    public short maxCount = -1;

    public String name = "";

    public String description  = "";

    public int killId = -1;

    public int mapId = -1;

    @Override
    public String toString() {
        final String n = "";
        return "{"
                + n + "id" + n + ":" + n + taskId + n + ","
                + n + "count" + n + ":" + n + count + n + ","
                + n + "maxCount" + n + ":" + n + maxCount + n + ","
                + n + "killId" + n + ":" + n + killId + n + ","
                + n + "mapId" + n + ":" + n + mapId + n
                + "}";
    }
    
    public TaskOrders(){
    }
    
    public TaskOrders(int id , int c , short mc , String n ,String des,int k,int m){
        taskId = id;
        count = c;
        maxCount = mc;
        name = n;
        description = des;
        killId = k;
        mapId = m;
    }
}
