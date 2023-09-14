package service.DAOS;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import real.clan.Clan;
import real.clan.ClanDAO;
import real.clan.Member;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Player;
import real.player.PlayerManger;
import server.Util;
import server.SQLManager;
import server.Service;
import server.io.JSON;
import service.Setting;
import service.data.Init;

public class PlayerDAO {
    
    public static void createMabuEgg(Player pl){
        if(!pl.isPl() && pl.mabuEgg == null){
            return;
        }
        int x=0, y=0;
        switch (pl.gender) {
            case 0:
                x = 698;
                y = 336;
                break;
            case 1:
                x = 697;
                y = 336;
                break;
            case 2:
                x = 695;
                y = 336;
                break;
        }
        NpcFactory.createNPC(21 + pl.gender, 1, x, y, 50, 0);
        Npc[] npcs = Npc.getByMap(pl);
        pl.zone.npcs = npcs;
        Service.gI().sendtMabuEff(pl, 100);
        Service.gI().sendImageMabuEgg(pl);
    }
    
    public static boolean create(int userId, String name, int gender, int hair) {
        try {
            SQLManager.execute(String.format("INSERT INTO player (account_id,server_name,name,info,point,item,skill,pet,magic_tree,item_time,entities,bua,mabu_egg,radar) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                    userId, Setting.SERVER_NAME, name, JSON.InfoToJson(gender, hair), Init.DEFAULT_POINT_START[gender], Init.DEFAULT_ITEM_START[gender], Init.DEFAULT_SKILL_START[gender], "{}", Init.DEFAULT_MAGIC_TREE_START, "[]", "{}", "[]", "{}", "{}"));
            SQLManager.execute("UPDATE `player` SET `player_id`=`id`+" + Setting.PLAYER_ID + " WHERE account_id='" + userId + "' AND server_name='"+Setting.SERVER_NAME+"' LIMIT 1");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Clan createClan(Clan clan) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            JSONArray arr = new JSONArray();
            for (Member mem : clan.members) {
                arr.put(JSON.MemberToJson(mem));
            }
            int result = SQLManager.execute(String.format("INSERT INTO clan (server_name,name,leader_id,img_id,create_time,member,doanhtrai,khigas,khobau,conduong) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                    Setting.SERVER_NAME,clan.name, clan.leaderID, clan.imgID, timestamp, arr, "[]", "[]", "[]", "[]"), 1);
            clan.id = result;
            return clan;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Player load(int userId) {
        Player player = null;
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT * FROM player WHERE account_id='" + userId + "' AND server_name='"+Setting.SERVER_NAME+"' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("player_id");
                player.role = rs.getByte("role");
                player.name = rs.getString("name");
                JSON.LoadInfoFromJson(player, rs.getString("info"));
                JSON.LoadPointFromJson(player, rs.getString("point"));
                JSON.LoadSkillFromJson(player, rs.getString("skill"));
                JSON.LoadItemFromJson(player, rs.getString("item"));
                JSON.LoadMagicTreeFromJson(player, rs.getString("magic_tree"));
                JSON.LoadPetFromJson(player, rs.getString("pet"));
                JSON.LoadItemTimeFromJson(player, rs.getString("item_time"));
                JSON.LoadEntitiesFromJson(player, rs.getString("entities"));
                JSON.LoadBuaFromJson(player, rs.getString("bua"));
                JSON.LoadMabuEggFromJson(player, rs.getString("mabu_egg"));
                JSON.LoadRadarFromJson(player, rs.getString("radar"));
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            Util.log("\n------------\n==>PlayerDAO.load");
            e.printStackTrace();
            return null;
        }
        return player;
    }
    
    public static Player loadDB(int PlayerID) {
        Player player = null;
        try {
            ResultSet rs = SQLManager.executeQuery("SELECT * FROM player WHERE player_id='" + PlayerID + "' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("player_id");
                player.role = rs.getByte("role");
                player.name = rs.getString("name");
                JSON.LoadInfoFromJson(player, rs.getString("info"));
                JSON.LoadPointFromJson(player, rs.getString("point"));
                JSON.LoadSkillFromJson(player, rs.getString("skill"));
                JSON.LoadItemFromJson(player, rs.getString("item"));
                JSON.LoadMagicTreeFromJson(player, rs.getString("magic_tree"));
                JSON.LoadPetFromJson(player, rs.getString("pet"));
                JSON.LoadItemTimeFromJson(player, rs.getString("item_time"));
                JSON.LoadEntitiesFromJson(player, rs.getString("entities"));
                JSON.LoadBuaFromJson(player, rs.getString("bua"));
                JSON.LoadMabuEggFromJson(player, rs.getString("mabu_egg"));
                JSON.LoadRadarFromJson(player, rs.getString("radar"));
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            Util.log("\n------------\n==>PlayerDAO.loadDB");
            e.printStackTrace();
        }
        return player;
    }
    
    public static Player LoadPlayer(int _playerID) {
        Player player = null;
        try {
            player = PlayerManger.gI().getPlayerByID(_playerID);
            if (player != null) {
                return player;
            }
            ResultSet rs = SQLManager.executeQuery("SELECT name,info,item FROM player WHERE player_id='" + _playerID + "' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = _playerID;
                player.name = rs.getString("name");
                JSON.LoadInfoFromJson(player, rs.getString("info"));
                JSON.LoadItemFromJson(player, rs.getString("item"));
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            return null;
        }
        return player;
    }
    
    public static Player getPlayerbyID(int _userId) {
        Player player = null;
        try {
            player = PlayerManger.gI().getPlayerByID(_userId);
            if (player != null) {
                return player;
            }
            ResultSet rs = SQLManager.executeQuery("SELECT * FROM player WHERE player_id='" + _userId + "' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("player_id");
                player.role = rs.getByte("role");
                player.name = rs.getString("name");
                JSON.LoadInfoFromJson(player, rs.getString("info"));
                JSON.LoadPointFromJson(player, rs.getString("point"));
                JSON.LoadSkillFromJson(player, rs.getString("skill"));
                JSON.LoadItemFromJson(player, rs.getString("item"));
                JSON.LoadMagicTreeFromJson(player, rs.getString("magic_tree"));
                JSON.LoadPetFromJson(player, rs.getString("pet"));
                JSON.LoadItemTimeFromJson(player, rs.getString("item_time"));
                JSON.LoadEntitiesFromJson(player, rs.getString("entities"));
                JSON.LoadBuaFromJson(player, rs.getString("bua"));
                JSON.LoadMabuEggFromJson(player, rs.getString("mabu_egg"));
            }
            rs.close();
        } catch (Exception e) {
            return null;
        }
        return player;
    }
    
    public static Player getPlayerbyUserID(int _userId) {
        Player player = null;
        try {
            player = PlayerManger.gI().getPlayerByID(_userId);
            if (player != null) {
                return player;
            }
            ResultSet rs = SQLManager.executeQuery("SELECT * FROM player WHERE account_id='" + _userId + "' AND server_name='"+Setting.SERVER_NAME+"' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("player_id");
                player.role = rs.getByte("role");
                player.name = rs.getString("name");
                JSON.LoadInfoFromJson(player, rs.getString("info"));
                JSON.LoadPointFromJson(player, rs.getString("point"));
                JSON.LoadSkillFromJson(player, rs.getString("skill"));
                JSON.LoadItemFromJson(player, rs.getString("item"));
                JSON.LoadMagicTreeFromJson(player, rs.getString("magic_tree"));
                JSON.LoadPetFromJson(player, rs.getString("pet"));
                JSON.LoadItemTimeFromJson(player, rs.getString("item_time"));
                JSON.LoadEntitiesFromJson(player, rs.getString("entities"));
                JSON.LoadBuaFromJson(player, rs.getString("bua"));
                JSON.LoadMabuEggFromJson(player, rs.getString("mabu_egg"));
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            return null;
        }
        return player;
    }
    
    public static Player load(String name) {
        Player player = null;
        try
        {
            player = PlayerManger.gI().getPlayerByName(name);
            if (player != null) {
                return player;
            }
            ResultSet rs = SQLManager.executeQuery("SELECT * FROM player WHERE name='" + name + "' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("player_id");
                player.role = rs.getByte("role");
                player.name = rs.getString("name");
                JSON.LoadInfoFromJson(player, rs.getString("info"));
                JSON.LoadPointFromJson(player, rs.getString("point"));
                JSON.LoadSkillFromJson(player, rs.getString("skill"));
                JSON.LoadItemFromJson(player, rs.getString("item"));
                JSON.LoadMagicTreeFromJson(player, rs.getString("magic_tree"));
                JSON.LoadPetFromJson(player, rs.getString("pet"));
                JSON.LoadItemTimeFromJson(player, rs.getString("item_time"));
                JSON.LoadEntitiesFromJson(player, rs.getString("entities"));
                JSON.LoadBuaFromJson(player, rs.getString("bua"));
                JSON.LoadMabuEggFromJson(player, rs.getString("mabu_egg"));
                JSON.LoadRadarFromJson(player, rs.getString("radar"));
            }
            rs.close();
            rs = null;
        }
        catch (Exception e) {
            Util.logException(PlayerDAO.class, e);
        }
        return player;
    }
    
    public static Player getInfobyID(int _userId) {
        Player player = null;
        try {
            player = PlayerManger.gI().getPlayerByID(_userId);
            if (player != null) {
                return player;
            }
            ResultSet rs = SQLManager.executeQuery("SELECT name,info FROM player WHERE player_id='" + _userId + "' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = _userId;
                player.name = rs.getString(1);
                JSON.LoadInfoFromJson(player, rs.getString(2));
            }
            rs.close();
        } catch (Exception e) {
            return null;
        }
        return player;
    }

    public static Player getInfobyName(String name) {
        Player player = null;
        try {
            if (PlayerManger.gI().getPlayerByName(name) != null) {
                return PlayerManger.gI().getPlayerByName(name);
            }
            ResultSet rs = SQLManager.executeQuery("SELECT player_id,info FROM player WHERE name='" + name + "' LIMIT 1");
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("player_id");
                player.name = name;
                JSON.LoadInfoFromJson(player, rs.getString(2));
            }
        } catch (Exception e) {
            return null;
        }
        return player;
    }
    
    public static void UpdateInfoPlayer(Player pl) {
        try {
            final String info = JSON.InfoToJson(pl);
            SQLManager.execute(String.format("UPDATE player SET info='%s' Where player_id='%s' ",
                    info, pl.id));
        } catch (Exception e) {
            Util.logException(PlayerDAO.class, e);
        }
    }

    public static void updateDB(Player player) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            final String info = JSON.InfoToJson(player);
            final String point = JSON.PointToJson(player);
            final String item = JSON.ItemToJson(player);
            final String skill = JSON.SkillToJson(player);
            final String pet = JSON.PetToJson(player.pet);
            final String magictree = JSON.MagicTreeToJson(player);
            final String item_time = JSON.ItemTimeToJson(player);
            final String entities = JSON.EntitiesToJson(player);
            final String bua = JSON.ItemBuaToJson(player);
            final String mabu = JSON.MabuEggToJson(player);
            final String radar = JSON.RadarToJson(player);
            if(Util.isNullOrEmpty(bua) || Util.isNullOrEmpty(radar) || Util.isNullOrEmpty(mabu) || Util.isNullOrEmpty(info) || Util.isNullOrEmpty(point) || Util.isNullOrEmpty(item) || Util.isNullOrEmpty(skill) || Util.isNullOrEmpty(pet) || Util.isNullOrEmpty(magictree) || Util.isNullOrEmpty(item_time) || Util.isNullOrEmpty(entities)){
                return;
            }
            SQLManager.executeUpdate(String.format("UPDATE player SET radar='%s',mabu_egg='%s',bua='%s',info='%s',point='%s',item='%s',skill='%s',pet='%s',magic_tree='%s',item_time='%s',entities='%s',last_logout_time='%s',last_login_time='%s' Where player_id='%s'",
                    radar,mabu,bua,info, point, item, skill, pet, magictree, item_time, entities, timestamp, player.login_time, player.id));
//            if (player.clan != null) {
//                ClanDAO.Update_Clan(player);
//            }
        } catch (Exception e) {
            Util.logException(PlayerDAO.class, e);
        }
    }
    
    public static void updateDB2(Player player) {
        try
        {
            final String info = JSON.InfoToJson(player);
            final String point = JSON.PointToJson(player);
            final String item = JSON.ItemToJson(player);
            final String skill = JSON.SkillToJson(player);
            final String pet = JSON.PetToJson(player.pet);
            final String magictree = JSON.MagicTreeToJson(player);
            final String item_time = JSON.ItemTimeToJson(player);
            final String entities = JSON.EntitiesToJson(player);
            final String bua = JSON.ItemBuaToJson(player);
            final String mabu = JSON.MabuEggToJson(player);
            final String radar = JSON.RadarToJson(player);
            SQLManager.executeUpdate(String.format("UPDATE player SET radar='%s',mabu_egg='%s',bua='%s',info='%s',point='%s',item='%s',skill='%s',pet='%s',magic_tree='%s',item_time='%s',entities='%s' Where player_id='%s'",
                    radar,mabu,bua,info, point, item, skill, pet, magictree, item_time, entities, player.id ));
//            if (player.clan != null) {
//                ClanDAO.Update_Clan(player);
//            }
        }
        catch (Exception e)
        {
            Util.logException(PlayerDAO.class, e);
        }
    }
    
    public static List<Player> getPlayers(){
        List<Player> players = new ArrayList();
        try {
            PlayerManger.gI().getPlayers().stream().filter(pl -> pl.isPl()).forEach((pl) -> {
                players.add(pl);
            });
            
            ResultSet rs = SQLManager.executeQuery("SELECT player_id,item FROM player WHERE `server_name` = '" + Setting.SERVER_NAME + "' ");
            while (rs.next()) {
                int playerID = rs.getInt("player_id");
                if(!players.stream().anyMatch(pl -> pl.id == playerID)){
                    Player player = new Player();
                    player.id = playerID;
                    JSON.LoadItemFromJson(player, rs.getString("item"));
                    players.add(player);
                }
            }
            rs.close();
        } catch (Exception e) {
            return null;
        }
        return players;
    }
    
    public static void UpdateItem(Player player){
        try {
            final String item = JSON.ItemToJson(player);
            SQLManager.execute(String.format("UPDATE player SET item='%s' Where player_id='%s' ",
                    item, player.id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
