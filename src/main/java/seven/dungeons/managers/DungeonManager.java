package seven.dungeons.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import seven.dungeons.Dungeon;
import seven.dungeons.SevenDungeons;
import seven.dungeons.bundle.DungeonBundle;

public class DungeonManager {
    
    private SevenDungeons plugin;
    
    public static Material[] gems = {Material.QUARTZ, Material.EMERALD};
    public static ArrayList<Dungeon> dungeons = new ArrayList<Dungeon>();
    
    public DungeonManager (SevenDungeons plugin)
    {
        this.plugin = plugin;
        
        //Load dungeons from their bundle folders
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
        
        // Creates the bundle folder with default settings
        Dungeon dungeon = new Dungeon(this.plugin, worldName , id);
        dungeon.saveDungeon();
        return true;
    }
    
    // Remove a dungeon and delete its bundle folder (the world is kept)
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
        case "version":
            if(value.equalsIgnoreCase("bump") || value.equals("+")) {
                if(!dungeon.bumpVersion()) {
                    return false;
                }
            }
            else if(!dungeon.setVersion(value)) {
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
                " info :\n World : " + d.getWorldName() + "\n Name : " + d.getName() + "\n Version : " + d.getVersion() + "\n Players : " + d.getNbPlayers() +
                "\n Time : " + d.getTime() + "\n Life : " + d.getLife() + "\n Time limit : " + d.getTimeLimit() +
                "\n Hunger : " + d.isHunger() + "\n Magic : " + d.isMagic() + "\n Godmode : " + d.isGodmode() +
                "\n Time stop : " + d.isTimeStop() + "\n Weather : " + d.getWeather() + "\n Gem : " + gems[d.getGem()].toString() +
                "\n Anchors : " + d.getAnchors().size() + "\n Messages : " + d.getMessages().size() +
                "\n Bundle : " + d.getBundle().getDirectory().getPath());
        return true;
    }
    
    /** Loads every bundle found under plugins/SevenDungeons/dungeons/. */
    public void loadDungeons() {
        int count = 0;
        for(String id : DungeonBundle.listIds(this.plugin)) {
            String world = DungeonBundle.readWorldName(this.plugin, id);
            if(world == null || world.isEmpty()) {
                SevenDungeons.log("Dungeon bundle \"" + id + "\" has no world name, skipped.", "SevenDungeons", ChatColor.RED);
                continue;
            }
            int format = DungeonBundle.readFormat(this.plugin, id);
            if(format > DungeonBundle.FORMAT_VERSION) {
                SevenDungeons.log("Dungeon bundle \"" + id + "\" uses format " + format + ", this plugin supports up to "
                        + DungeonBundle.FORMAT_VERSION + ". Skipped, update the plugin.", "SevenDungeons", ChatColor.RED);
                continue;
            }
            if(!this.plugin.worldManager.isInFolder(world)) {
                SevenDungeons.log("Dungeon \"" + id + "\": world folder \"" + world + "\" not found. Loaded anyway, games will fail until it exists.", "SevenDungeons", ChatColor.YELLOW);
            }
            new Dungeon(this.plugin, world, id); // constructor loads settings, anchors and messages
            count++;
        }
        SevenDungeons.log(count + " dungeon(s) loaded from " + DungeonBundle.getRootFolder(this.plugin).getPath() + ".", "SevenDungeons", ChatColor.GREEN);
    }
    
    /**
     * Migration helper: rebuilds bundles from the legacy MySQL tables (dungeons,
     * messages, sign). Pass null to import every dungeon of the database.
     * Existing bundles are overwritten with the database content.
     *
     * @return number of dungeons imported, or -1 if the database is unavailable or failed
     */
    public int importFromDatabase(String onlyId)
    {
        Connection connection = this.plugin.getConnection();
        if(connection == null) {
            return -1;
        }
        int count = 0;
        try {
            PreparedStatement query;
            if(onlyId == null) {
                query = connection.prepareStatement("SELECT * FROM dungeons");
            }
            else {
                query = connection.prepareStatement("SELECT * FROM dungeons WHERE id=?");
                query.setString(1, onlyId);
            }
            ResultSet rs = query.executeQuery();
            while(rs.next()) {
                String id = rs.getString("id");
                String world = rs.getString("world");
                Dungeon dungeon = this.getDungeon(id);
                if(dungeon == null) {
                    dungeon = new Dungeon(this.plugin, world, id);
                }
                else if(!dungeon.getWorldName().equals(world)) {
                    SevenDungeons.log("Dungeon \"" + id + "\": database world \"" + world + "\" differs from bundle world \"" + dungeon.getWorldName() + "\", bundle kept.", "SevenDungeons", ChatColor.YELLOW);
                }
                dungeon.setLife(rs.getInt("life"));
                dungeon.setGodmode(rs.getBoolean("godmode"));
                dungeon.setHunger(rs.getBoolean("hunger"));
                dungeon.setMagic(rs.getBoolean("magic"));
                dungeon.setNbPlayers(rs.getInt("nbPlayers"));
                dungeon.setTime(rs.getInt("time"));
                dungeon.setTimeLimit(rs.getInt("timeLimit"));
                dungeon.setWeather(rs.getString("weather"));
                dungeon.setTimeStop(rs.getBoolean("timeStop"));
                dungeon.setGem(rs.getInt("gem"));
                dungeon.setName(rs.getString("name"));
                dungeon.saveDungeon();
                
                // Messages
                PreparedStatement mq = connection.prepareStatement("SELECT id, message FROM messages WHERE dungeon_id=?");
                mq.setString(1, id);
                ResultSet mrs = mq.executeQuery();
                Map<Integer, String> messages = new LinkedHashMap<Integer, String>();
                while(mrs.next()) {
                    messages.put(mrs.getInt("id"), mrs.getString("message"));
                }
                dungeon.setMessages(messages);
                
                // Sign positions
                PreparedStatement sq = connection.prepareStatement("SELECT x, y, z FROM sign WHERE id=?");
                sq.setString(1, id);
                ResultSet srs = sq.executeQuery();
                ArrayList<Location> anchors = new ArrayList<Location>();
                while(srs.next()) {
                    anchors.add(new Location(Bukkit.getWorld(world), srs.getInt("x"), srs.getInt("y"), srs.getInt("z")));
                }
                dungeon.getAnchors().replaceAll(anchors);
                
                SevenDungeons.log("Imported dungeon \"" + id + "\" (" + messages.size() + " messages, " + anchors.size() + " anchors) into " + dungeon.getBundle().getDirectory().getPath());
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return count;
    }
    
    public Material getGem(int i) {
        return gems[i];
    }
}
