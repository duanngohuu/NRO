package real.radar;

import java.util.ArrayList;
import java.util.List;
import real.player.Player;

public class Radar {
    private static Radar instance;
    public List<RadarTemplate> radars = new ArrayList();
    public Player player;
    
    public Radar(Player pl){
        player = pl;
        radars.addAll(RadarTemplateData.gI().radars);
    }
    
    public List<RadarTemplate> getRadars(){
        return radars;
    }
    
    public RadarTemplate getRadar(int ID){
        RadarTemplate rd = null;
        for(int i = 0; i < radars.size(); i++){
            rd = radars.get(i); 
            if(rd.id == ID){
                break;
            }
        }
        return rd;
    }
    
    public void SetRadar(int ID, int user, int count, int level){
        radars.stream().filter(rd -> rd != null && rd.id == ID).forEach((rd) -> {
            rd.user = user;
            rd.count = count;
            rd.level = level;
        });
    }
}
