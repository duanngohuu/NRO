package server.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import real.player.Player;
import server.SQLManager;
import server.ServerManager;
import server.Util;
import service.Setting;

public class Session implements ISession {

    public Player player;
    public boolean isLogin, isCreateChar, isResource, isRead;
    
    public int countDie;
    
    private boolean sendKeyComplete, connected;

    private static byte[] keys = "GENKAI".getBytes();
    private byte curR, curW;

    protected Socket socket;

    protected DataInputStream dis;
    protected DataOutputStream dos;
    public Sender sender;
    private MessageCollector collector;

    IMessageHandler messageHandler;

    public int userId = -1, active = -1;
    public String account;
    public String pass;
    public byte typeClient, zoom;
    public short version = 199;
    public String ipAddress;
    public int SocketID;
    
    public void add(Socket socket, IMessageHandler handler){
        try {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.sender = new Sender();
            this.collector = new MessageCollector();
            this.socket = socket;
            this.SocketID = socket.getPort();
            this.ipAddress = socket.getInetAddress().getHostAddress();
            this.messageHandler = handler;
            this.collector.active();
            connected = true;
        } catch (Exception e) {
            Util.debug("add(Socket socket, IMessageHandler handler)");
            e.printStackTrace();
        }
    }

    public void onRecieveMsg(Message message) {
        messageHandler.onMessage(this, message);
    }
    
    @Override
    public void remove_point(int quantity) {
        if(quantity <= 0 || userId == -1)
        {
            return;
        }
        SQLManager.executeUpdate("UPDATE account SET `point`=`point`-"+quantity+" WHERE id="+userId);
    }
    
    @Override
    public long get_point() {
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT point FROM account WHERE id="+userId);
            rs.first();
            int point = rs.getInt("point");
            rs.close();
            rs = null;
            return point;
        } catch (Exception ex) {
            return 0;
        }
    }
    
    @Override
    public int get_active() {
        return active;
    }
    
    @Override
    public void remove_gold(int quantity) {
        SQLManager.executeUpdate("UPDATE account SET `gold`=`gold`-"+quantity+" WHERE id="+userId);
    }
    public void update_active() {
        SQLManager.executeUpdate("UPDATE account SET `active`='1' WHERE id="+userId);
    }
    
    @Override
    public int get_gold() {
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT gold FROM account WHERE id="+userId);
            rs.first();
            int gold = rs.getInt("gold");
            rs.close();
            rs = null;
            return gold;
        } catch (Exception ex) {
            return 0;
        }
    }
    
    @Override
    public void remove_money(int quantity) {
        if(quantity <= 0){
            return;
        }
        SQLManager.executeUpdate("UPDATE account SET `money`=`money`-"+quantity+" WHERE id="+userId);
    }
    
    @Override
    public int get_money() {
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT total_money FROM account WHERE id="+userId);
            rs.first();
            int money = rs.getInt("total_money");
            rs.close();
            rs = null;
            return money;
        } catch (Exception ex) {
            return 0;
        }
    }
     public int get_act() {
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT active FROM account WHERE id="+userId);
            rs.first();
            int money = rs.getInt("active");
            rs.close();
            rs = null;
            return money;
        } catch (Exception ex) {
            return 0;
        }
    }
    public int get_vnd() {
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT money FROM account WHERE id="+userId);
            rs.first();
            int money = rs.getInt("money");
            rs.close();
            rs = null;
            return money;
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void sendMessage(Message msg) {
        sender.AddMessage(msg);
    }

    @Override
    public int get_zoom() {
        return zoom;
    }
    
    @Override
    public short get_version() {
        return version;
    }

    @Override
    public void set_version(short v) {
        version = v;
    }

    @Override
    public String get_client_account() {
        return this.account;
    }
    
    @Override
    public String get_client_pass() {
        return this.pass;
    }
    
    @Override
    public byte get_type_client() {
        return typeClient;
    }

    @Override
    public void set_type_client(byte t) {
        typeClient = t;
    }

    @Override
    public int get_user_id() {
        return userId;
    }

    @Override
    public void set_user_id(int u) {
        userId = u;
    }

    class Sender {
        Vector<Message> sendingMessage = new Vector<>();

        public void AddMessage(Message message) {
            sendingMessage.addElement(message);
        }

        public void removeAllMessage() {
            if (sendingMessage != null) {
                sendingMessage.removeAllElements();
            }
        }
        
        public Timer timer;
        public TimerTask task;
        public boolean actived = false;

        public void close() {
            try {
                actived = false;
                task.cancel();
                timer.cancel();
                task = null;
                timer = null;
            } catch (Exception e) {
                task = null;
                timer = null;
            }
        }

        public void active() {
            if (!actived) {
                actived = true;
                this.timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Sender.this.update();
                    }
                };
                this.timer.schedule(task, 5, 5);
            }
        }
        
        public void update(){
            try{
                if (sendingMessage.size() > 0 && connected) {
                    Message message = (Message)sendingMessage.elementAt(0);
                    sendingMessage.removeElementAt(0);
                    doSendMessage(message);
                    message.cleanup();
                }
            }
            catch (Exception e){
                Util.debug("ERROR");
            }
        }
    }

    class MessageCollector {
        public Timer timer;
        public TimerTask task;
        public boolean actived = false;

        public void close() {
            try {
                actived = false;
                task.cancel();
                timer.cancel();
                task = null;
                timer = null;
            } catch (Exception e) {
                task = null;
                timer = null;
            }
        }

        public void active() {
            if (!actived) {
                actived = true;
                this.timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        MessageCollector.this.update();
                    }
                };
                this.timer.schedule(task, 5, 5);
            }
        }
        
        public void update(){
            try
            {
                Message msg = readMessage();
                if (msg != null)
                {
                    onRecieveMsg(msg);
                    msg.cleanup();
                }
                else
                {
                    Session.this.disconnect();
                }
            }
            catch (Exception e){
                Session.this.disconnect();
            }
        }
        
        private Message readMessage()
        {
            try
            {
                if(dis != null)
                {
                    byte cmd;
                    cmd = dis.readByte();
                    if (sendKeyComplete) {
                        cmd = readKey(cmd);
                    }
                    int size;
                    if (sendKeyComplete) {
                        final byte b1 = dis.readByte();
                        final byte b2 = dis.readByte();
                        size = (readKey(b1) & 255) << 8 | readKey(b2) & 255;
                    } else {
                        size = dis.readUnsignedShort();
                    }
                    final byte data[] = new byte[size];
                    int len = 0;
                    int byteRead = 0;
                    while (len != -1 && byteRead < size)
                    {
                        len = dis.read(data, byteRead, size - byteRead);
                        if (len > 0) {
                            byteRead += len;
                        }
                    }
                    if (sendKeyComplete) {
                        for (int i = 0; i < data.length; i++) {
                            data[i] = readKey(data[i]);
                        }
                    }
                    return new Message(cmd, data);
                }
            }
            catch (IOException ex)
            {
//                Util.logException(Session.class, ex);
            }
            return null;
        }
    }
    
    @Override
    public void disconnect() {
        if (connected) {
            connected = false;
            try {
                this.curR = 0;
                this.curW = 0;
                if(player != null)
                {
                    SQLManager.execute("UPDATE `player` SET `online`='0' WHERE player_id='" + player.id + "'");
                }
                if (socket != null)
                {
                    socket.close();
                    socket = null;
                }
                if (this.dos != null)
                {
                    this.dos.close();
                    this.dos = null;
                }
                if (this.dis != null)
                {
                    this.dis.close();
                    this.dis = null;
                }
                this.close();
                sender.close();
                collector.close();
                sender = null;
                collector = null;
                if (messageHandler != null)
                {
                    messageHandler.onDisconnected(this);
                    messageHandler = null;
                }
                ServerManager.Sessions.remove(this);
            }
            catch (Exception e)
            {
                Util.logException(Session.class, e);
            }
        }
    }
    
    public boolean isSession(){
        List<Session> ss = new ArrayList();
        for(int i = 0; i < ServerManager.Sessions.size(); i++){
            Session session = ServerManager.Sessions.get(i);
            if(this.userId == session.userId){
                ss.add(session);
            }
        }
        if(ss.size() >= 2){
            for(int i = 0; i < ss.size(); i++){
                ss.get(i).disconnect();
            }
            return true;
        }
        return false;
    }
    
    public int numSession(){
        List<Session> ss = new ArrayList();
        for(int i = 0; i < ServerManager.Sessions.size(); i++){
            Session session = ServerManager.Sessions.get(i);
            if(session.ipAddress.equals(this.ipAddress)){
                ss.add(session);
            }
        }
        return ss.size();
    }
    
    public void close(){
        userId = -1;
        active = -1;
        account = null;
        pass = null;
    }

    public synchronized void doSendMessage(Message msg) {
        try {
            final byte[] data = msg.getData();
            if (sendKeyComplete) {
                byte b = writeKey(msg.getCommand());
                dos.writeByte(b);
            } else {
                dos.writeByte(msg.getCommand());
            }
            if (data != null) {
                int size = data.length;
                if (msg.getCommand() == -32 || msg.getCommand() == -66 || msg.getCommand() == -74 || msg.getCommand() == 11 || msg.getCommand() == -67 || msg.getCommand() == -87 || msg.getCommand() == 66) {
                    final byte b = writeKey((byte) (size));
                    dos.writeByte(b - 128);
                    final byte b2 = writeKey((byte) (size >> 8));
                    dos.writeByte(b2 - 128);
                    final byte b3 = writeKey((byte) (size >> 16));
                    dos.writeByte(b3 - 128);
                } else if (sendKeyComplete) {
                    final int byte1 = writeKey((byte) (size >> 8));
                    dos.writeByte(byte1);
                    final int byte2 = writeKey((byte) (size & 255));
                    dos.writeByte(byte2);
                } else {
                    dos.writeShort(size);
                }
                if (sendKeyComplete)
                {
                    for (int i = 0; i < data.length; i++)
                    {
                        data[i] = writeKey(data[i]);
                    }
                }
                dos.write(data);
            } else {
                dos.writeShort(0);
            }
            dos.flush();
        } catch (Exception e) {
            Util.logException(Session.class, e);
        }
    }

    private byte writeKey(byte b) {
        final byte i = (byte) ((keys[curW++] & 255) ^ (b & 255));
        if (curW >= keys.length) {
            curW %= keys.length;
        }
        return i;
    }

    private byte readKey(byte b) {
        final byte i = (byte) ((keys[curR++] & 255) ^ (b & 255));
        if (curR >= keys.length) {
            curR %= keys.length;
        }
        return i;
    }

    public void sendSessionKey() {
        Message msg = new Message(-27);
        try {
            msg.writer().writeByte(keys.length);
            msg.writer().writeByte(keys[0]);
            for (int i = 1; i < keys.length; i++)
            {
                msg.writer().writeByte(keys[i] ^ keys[i - 1]);
            }
//            String[] Server = Setting.SERVER_NAME.split(":");
//            msg.writer().writeUTF(Server[1]);//IP2
//            msg.writer().writeInt(Integer.valueOf(Server[2]));//Port
//            msg.writer().writeByte(1);//Connext (0 = false; 1 = true)
            doSendMessage(msg);
            isRead = true;
            sendKeyComplete = true;
            sender.active();
        }
        catch (Exception e) {
            Util.logException(Session.class, e);
        }
    }

    public void setClientType(Message msg) {
        try 
        {
           
            this.typeClient = msg.reader().readByte();//client_type
//            msg.reader().readByte();
//            msg.reader().readByte();
            zoom = msg.reader().readByte();//zoom_level
            msg.reader().readBoolean();//is_gprs
            msg.reader().readInt();//width
            msg.reader().readInt();//height
            msg.reader().readBoolean();//is_qwerty
            msg.reader().readBoolean();//is_touch
            String v = msg.reader().readUTF();//
            msg.cleanup();
            version = Short.valueOf(v.substring(v.indexOf("|") + 1).replace(".", ""));
            Util.debug("version: " + version);
        }
        catch (Exception e)
        {
            Util.logException(Session.class, e);
        }
    }
}
