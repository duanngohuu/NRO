package real.map;

public class WayPoint {

    public short minX;

    public short minY;

    public short maxX;

    public short maxY;

    public boolean isEnter;

    public boolean isOffline;

    public String name;

    public int goMap;

    public short goX;

    public short goY;
    
    public boolean isMapDTDN() {
        return goMap >= 53 && goMap <= 62;
    }
    
    public boolean isMapBDKB() {
        return goMap >= 135 && goMap <= 138;
    }
    
    public boolean isMapKGHD() {
        return goMap >= 147 && goMap <= 152;
    }
    
    public boolean isMapCDRD() {
        return goMap >= 141 && goMap <= 144;
    }
}
