package real.task;

import real.player.Player;

public class Task {

    public int max;

    public short taskId;

    public String name;

    public String detail;

    public String[] subNames;

    public String[] contentInfo;

    public int[] killId;
    
    public String[] npc_quest;
    
    public int[][] maps;
    
    public int[] counts;

    public int type;
    
    public long getTNSM() {
        String[] gift = detail.split("\n");
        for (String str : gift) {
            if (str.startsWith("-")) {
                return Integer.parseInt(str.replaceAll("[\\D]", ""));
            }
        }
        return 0;
    }
    
    public boolean isTaskClan(Player pl){
        long countPlClan = pl.zone.players.stream().filter(p -> p!=null && p.isPl() && p.zone.zoneId == pl.zone.zoneId && p.clan != null && p.clan.id == pl.clan.id).count();
        if(countPlClan >= 3){
            return true;
        }
        return false;
    }
}
