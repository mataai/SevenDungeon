package seven.dungeons;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import seven.dungeons.managers.DungeonManager;

public class Dungeon {
    
    private SevenDungeons plugin;
    
    private String id;
    private String worldName;
    
    private ArrayList<Location> signs = new ArrayList<Location>();
    
    private int life;
    private int nbPlayers;
    private int time;
    private boolean timeStop;
    private String weather;
    private boolean magic;
    private boolean hunger;
    private boolean godmode;
    private int timeLimit;
    private boolean buildmode;
    private int gem;
    private String name;
    
    public Dungeon (SevenDungeons plugin, String worldName, String id)
    {
        this.plugin = plugin;
        this.worldName = worldName;
        this.id = id;
        
        //Initialization with default values, will be changed if they exist in config.
        this.life = 0;
        this.nbPlayers = 1;
        this.time = 6000;
        this.timeStop = true;
        this.weather = "clear";
        this.magic = false;
        this.hunger = true;
        this.godmode = false;
        this.timeLimit = -1;
        this.buildmode = false;
        this.gem = 0;
        this.name = "NoName";
        
        DungeonManager.dungeons.add(this);
        this.loadSigns();
        
    }
    
    //Get the World File of this dungeon.
    public File getWorldFile()
    {
        File file = new File(this.plugin.multiverse.getServerFolder().getPath(), this.worldName);
        return file;
    }
    
    //Save this dungeon in the config file.
    public void saveDungeon()
    {
        
        try {
            Connection connection = this.plugin.getConnection();
            PreparedStatement query = connection.prepareStatement("SELECT * FROM dungeons where id=?");
            query.setString(1, this.id);
            ResultSet rs = query.executeQuery();
            if(rs.next()) {
                PreparedStatement update = connection.prepareStatement("UPDATE dungeons SET life=?, nbPlayers=?, time=?, "
                        + "timeStop=?, weather=?, magic=?, hunger=?, godmode=?, timeLimit=?, gem=?, name=? WHERE id=?");
                update.setInt(1,this.life);update.setInt(2,this.nbPlayers);update.setInt(3,this.time);
                update.setBoolean(4,this.timeStop);update.setString(5, this.weather);update.setBoolean(6, this.magic);
                update.setBoolean(7, this.hunger);update.setBoolean(8, this.godmode);update.setInt(9, this.timeLimit);
                update.setString(12, this.id); update.setInt(10, this.gem); update.setString(11, this.name);
                update.execute();
            }
            else {
                PreparedStatement insert = connection.prepareStatement("INSERT INTO dungeons (id, world, life, nbPlayers, time, "
                        + "timeStop, weather, magic, hunger, godmode, timeLimit, gem, name) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insert.setString(1, this.id);insert.setString(2, this.worldName);
                insert.setInt(3,this.life);insert.setInt(4,this.nbPlayers); insert.setInt(5,this.time);
                insert.setBoolean(6,this.timeStop); insert.setString(7, this.weather); insert.setBoolean(8, this.magic);
                insert.setBoolean(9, this.hunger); insert.setBoolean(10, this.godmode); insert.setInt(11, this.timeLimit);
                insert.setInt(12, this.gem); insert.setString(13, this.name);
                insert.execute();
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveSigns() {
        Bukkit.getWorld(this.getWorldName()).save();
        this.plugin.dungeonManager.saveSigns(this);
    }
    
    public void loadSigns() {
        try {
            Connection connection = this.plugin.getConnection();
            PreparedStatement query = connection.prepareStatement("SELECT * FROM sign WHERE id =?");
            query.setString(1, this.id);
            ResultSet rs = query.executeQuery();
            while(rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                this.signs.add(new Location(Bukkit.getWorld(this.worldName),x,y,z));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    //Remove this dungeon from the config file and the current server session.
    
    public void remove()
    {
        DungeonManager.dungeons.remove(this);
        this.removeDungeonConfig();
    }
    
    public void removeDungeonConfig()
    {
        try {
            PreparedStatement delete = this.plugin.getConnection().prepareStatement("DELETE FROM dungeons where id=?");
            delete.setString(1, this.id);
            delete.execute();
        }
        catch(SQLException e)
        {
            SevenDungeons.log("Dungeon \"" + this.id + "\" could not be removed properly." , "SevenDungeons", ChatColor.RED);
        }
    }
    
    //Getters and Setters.

    public ArrayList<Location> getSigns() {
        return this.signs;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        if(life < -1 || life > 100)
        {
            return;
        }
        this.life = life;
        
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        if(nbPlayers < 0 || nbPlayers > 8)
        {
            return;
        }
        this.nbPlayers = nbPlayers;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        if(time < 0 || time > 23999)
        {
            return;
        }
        this.time = time;
    }

    public boolean isTimeStop() {
        return timeStop;
    }

    public void setTimeStop(boolean timeStop) {
        this.timeStop = timeStop;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        switch(weather.toLowerCase())
        {
            case "rain" : this.weather = "rain"; break;
            case "thunder" : this.weather = "thunder"; break;
            default : this.weather = "clear";
        }
    }

    public boolean isMagic() {
        return magic;
    }

    public void setMagic(boolean magic) {
        this.magic = magic;
    }

    public boolean isHunger() {
        return hunger;
    }

    public void setHunger(boolean hunger) {
        this.hunger = hunger;
    }

    public boolean isGodmode() {
        return godmode;
    }

    public void setGodmode(boolean godmode) {
        this.godmode = godmode;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        if(timeLimit < -1 || timeLimit > 7200)
        {
            return;
        }
        this.timeLimit = timeLimit;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void addSign(Location location) {
        this.signs.add(location);
    }
    
    public void removeSign(Location location) {
        ArrayList<Location> toRemove = new ArrayList<Location>();
        for(Location l : this.signs) {
            if(l.equals(location)) {
                toRemove.add(l);
            }
        }
        signs.removeAll(toRemove);    
        }
    
    public boolean isBuildmode() {
        return this.buildmode;
    }
    
    public void setBuildmode(boolean mode) {
        this.buildmode = mode;
    }
    
    public void findSigns(Location location) {
        this.signs.clear();
        World world = Bukkit.getWorld(this.worldName);
        SevenDungeons.log("Signs are being scanned.");
        int maxX = location.getBlockX();
        int maxY = location.getBlockY();
        int maxZ = location.getBlockZ();
        for(int x = 0; x < maxX; x++) {
            for(int y = -63; y < maxY; y++) {
                for(int z = 0; z < maxZ; z++) {
                    if(x == 181 && y == 40 && z == 125){
                        SevenDungeons.log("Il est là");
                    }
                    Location l = (new Location(world, x, y, z));
                    Block block = l.getBlock();
                    if(block.getType().equals(Material.OAK_SIGN) || block.getType().equals(Material.OAK_WALL_SIGN)) {
                        Sign sign = (Sign)(block.getState());
                        String firstLine = sign.getLine(0);
                        if(firstLine == null || firstLine.equals("")) {
                            continue;
                        }       
                        if(firstLine.trim().substring(0, 1).equals("=")) {
                            this.signs.add(l);
                        }
                    }
                }
            }
        }
        this.saveSigns();
    }
    
    public int getGem() {
        return this.gem;
    }
    
    public void setGem(int gem) {
        this.gem = gem;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
