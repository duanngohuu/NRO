package real.clan;

public class ClanImage {

    public ClanImage(int id, int type, String name, int img, int xu, int luong, short[] frame) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.img = img;
        this.xu = xu;
        this.luong = luong;
        this.frame = frame;
    }

    public int id = -1;
    
    public int type;

    public int img;

    public String name;

    public int xu;

    public int luong;
    
    public short[] frame;
}
