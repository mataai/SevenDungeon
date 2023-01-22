package seven.dungeons.managers;

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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import seven.dungeons.Dungeon;
import seven.dungeons.SevenDungeons;

public class DungeonManager {
    
    private SevenDungeons plugin;
    
    public static Material[] gems = {Material.QUARTZ, Material.EMERALD};
    public static ArrayList<Dungeon> dungeons = new ArrayList<Dungeon>();
    
    public FileConfiguration dungeonConfig;
    public File dungeonFile;
    
    public DungeonManager (SevenDungeons plugin)
    {
        this.plugin = plugin;
        
        //Load dungeons from database
        loadDungeons();
        
        
    }
    
    public Dungeon getDungeon(String id)
    {
        for(Dungeon d : DungeonManager.dungeons)
        {
            if(d.getId().equals(id))
            {
                return d;
            }
        }
        return null;
    }
    
    public Dungeon getDungeonByWorld(String world) {
        for(Dungeon d : DungeonManager.dungeons)
        {
            if(d.getWorldName().equals(world))
            {
                return d;
            }
        }
        return null;
    }
    
    public boolean add(String worldName, String id)
    {
        // A dungeon with that id already exists
        if(this.getDungeon(id) != null)
        {
            return false;
        }
        
        // The world doesn't exist
        if(!this.plugin.worldManager.isInFolder(worldName))
        {
            return false;
        }
        
        Dungeon dungeon = new Dungeon(this.plugin, worldName , id);
        /*File worldfile = dungeon.getWorldFile();*/
        
        dungeon.saveDungeon();
        return true;
    }
    
    // Remove a dungeon from the config file
    public boolean removeDungeon(String id)
    {
        Dungeon dg = this.getDungeon(id);
        
        if(dg == null)
        {
            return false;
        }
        
        dg.remove();
        return true;
    }
    
    /*
     * Modify a dungeon property
     */
    public boolean set(String id, String property, String value, Player p)
    {
        Dungeon dungeon = this.getDungeon(id);
        
        if(dungeon == null)
        {
            return false;
        }
        
        switch(property)
        {
        case "timeStop": case "timestop":
            if(value.equals("true"))
            {
                dungeon.setTimeStop(true);break;
            }
            if(value.equals("false")){
                dungeon.setTimeStop(false);break;
            }
            return false;
        case "godmode": case "godMode":
            if(value.equals("true")){
                dungeon.setGodmode(true);break;
            }
            if(value.equals("false")){
                dungeon.setGodmode(false);break;
            }
            return false;
        case "hunger":
            if(value.equals("true")){
                dungeon.setHunger(true);break;
            }
            if(value.equals("false")){
                dungeon.setHunger(false);break;
            }
            return false;
        case "magic":
            if(value.equals("true")){
                dungeon.setMagic(true);break;
            }
            if(value.equals("false")){
                dungeon.setMagic(false);break;
            }
            return false;
        case "nbplayer": case "nbPlayer":
            if(CommandManager.tryParse(value) != null){
                dungeon.setNbPlayers(Integer.parseInt(value));
            }
            else {
                return false;
            }
            break;
        case "timeLimit": case "timelimit":
            if(CommandManager.tryParse(value) != null){
                dungeon.setTimeLimit(Integer.parseInt(value));
            }
            else {
                return false;
            }
            break;
        case "time":
            if(CommandManager.tryParse(value) != null){
                dungeon.setTime(Integer.parseInt(value));
            }
            else {
                return false;
            }
            break;
        case "life":
            if(CommandManager.tryParse(value) != null){
                dungeon.setLife(Integer.parseInt(value));
            }
            else {
                return false;
            }
            break;
        case "weather":
            if(value.equalsIgnoreCase("clear") || value.equalsIgnoreCase("rain") || value.equalsIgnoreCase("thunder")){
                dungeon.setWeather(value);
            }
            else{
                return false;
            }
            break;
        case "gem":
            if(CommandManager.tryParse(value) != null){
                dungeon.setGem(Integer.parseInt(value));
            }
            else {
                return false;
            }
            break;
        case "name":
            if(value.length() < 21) {
                value = value.replace('_', ' ');
                dungeon.setName(value);
            }
            else {
                return false;
            }
            break;
        default : return false;
        }
        dungeon.saveDungeon();
        return true;
    }
    
    /*
     * Teleport to a dungeon map to build
     */
    public boolean edit(Player p, String id)
    {
        Dungeon dungeon = this.getDungeon(id);
        
        if(dungeon == null)
        {
            return false;
        }
        String worldName = dungeon.getWorldName();
        this.plugin.worldManager.load(worldName);
        World dgWorld = Bukkit.getWorld(worldName);
        p.teleport(dgWorld.getSpawnLocation());
        return true;
        
    }
    
    /*
     * Save any change made in a dungeon (map files wise)
     */
    public boolean save(String id)
    {
        Dungeon d = this.getDungeon(id);
        
        if(d == null)
        {
            return false;
        }
        
        Bukkit.getWorld(d.getWorldName()).save();
        return true;
    }
    
    /*
     * Prints the dungeon properties
     */
    public boolean info(CommandSender sender, String dungeon)
    {
        Dungeon d = this.getDungeon(dungeon);
        if(d == null)
        {
            return false;
        }
        
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "SevenDungeons "
                + ChatColor.RESET + ": " + ChatColor.YELLOW + d.getId() + ChatColor.RESET +
                " info :\n World : " + d.getWorldName() + "\n Name : " + d.getName() + "\n Players : " + d.getNbPlayers() +
                "\n Time : " + d.getTime() + "\n Life : " + d.getLife() + "\n Time limit : " + d.getTimeLimit() +
                "\n Hunger : " + d.isHunger() + "\n Magic : " + d.isMagic() + "\n Godmode : " + d.isGodmode() +
                "\n Time stop : " + d.isTimeStop() + "\n Weather : " + d.getWeather() + "\n Gem : " + gems[d.getGem()].toString());
        return true;
    }
    
    public void loadDungeons() {
        String id, world, weather, name;
        int life, nbPlayers, time, timeLimit, gem;
        boolean timeStop, hunger, magic, godmode;
        try {
            PreparedStatement query = this.plugin.getConnection().prepareStatement("SELECT * FROM dungeons");
            ResultSet rs = query.executeQuery();
            while(rs.next()) {
                id = rs.getString("id");
                world = rs.getString("world");
                weather = rs.getString("weather");
                life = rs.getInt("life");
                nbPlayers = rs.getInt("nbPlayers");
                time = rs.getInt("time");
                timeLimit = rs.getInt("timeLimit");
                timeStop = rs.getBoolean("timeStop");
                hunger = rs.getBoolean("hunger");
                magic = rs.getBoolean("magic");
                godmode = rs.getBoolean("godmode");
                gem = rs.getInt("gem");
                name = rs.getString("name");
                
                Dungeon dungeon = new Dungeon(this.plugin, world, id);
                dungeon.setLife(life);
                dungeon.setGodmode(godmode);
                dungeon.setHunger(hunger);
                dungeon.setMagic(magic);
                dungeon.setNbPlayers(nbPlayers);
                dungeon.setTime(time);
                dungeon.setTimeLimit(timeLimit);
                dungeon.setWeather(weather);
                dungeon.setTimeStop(timeStop);
                dungeon.setGem(gem);
                dungeon.setName(name);
            }
            SevenDungeons.log("Dungeons loaded.", "SevenDungeons", ChatColor.GREEN);
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveSigns(Dungeon dungeon)
    {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = plugin.getConnection();
                    PreparedStatement delete = connection.prepareStatement("DELETE FROM sign WHERE id=?");
                    delete.setString(1, dungeon.getId());
                    delete.execute();
                    
                    for(Location l : dungeon.getSigns()) {
                        PreparedStatement insert = connection.prepareStatement("INSERT INTO sign (id, x, y, z) VALUES (?,?,?,?)");
                        insert.setString(1, dungeon.getId());
                        insert.setInt(2, l.getBlockX());
                        insert.setInt(3, l.getBlockY());
                        insert.setInt(4, l.getBlockZ());
                        insert.execute();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public Material getGem(int i) {
        return gems[i];
    }
}
