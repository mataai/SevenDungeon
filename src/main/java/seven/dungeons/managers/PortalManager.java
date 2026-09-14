package seven.dungeons.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import seven.dungeons.Portal;
import seven.dungeons.SevenDungeons;

public class PortalManager {
    
    private SevenDungeons plugin;
    
    public static ArrayList<Portal> portals = new ArrayList<Portal>();
    
    public FileConfiguration portalConfig;
    public File portalFile;
    
    public PortalManager (SevenDungeons plugin)
    {
        this.plugin = plugin;
        this.loadPortals();
        
        
    }
    
    /*
     * Get a Portal based on its ID
     */
    public Portal getPortal(String id)
    {
        for(Portal p : PortalManager.portals)
        {
            if(p.getId().equals(id))
            {
                return p;
            }
        }
        return null;
    }
    
    /*
     * Adds a new portal with an ID
     */
    public boolean add(String worldName, String id)
    {
        if(this.getPortal(worldName) != null)
        {
            return false;
        }
        
        if(!this.plugin.worldManager.isInFolder(worldName))
        {
            return false;
        }
        
        Portal portal = new Portal(this.plugin, worldName , id);
        /*File worldfile = dungeon.getWorldFile();*/
        portal.savePortal();
        return true;
    }
    
    /*
     * Removes a portal (his config section will be removed as well)
     */
    public boolean removePortal(String portal)
    {
        Portal p = this.getPortal(portal);
        
        if(p == null)
        {
            return false;
        }
        
        p.remove();
        return true;
    }
    
    /*
     * Changes a portal's property
     */
    public boolean set(String id, String[] property, String value, Player p)
    {
        Portal portal = this.getPortal(id);
        
        if(portal == null)
        {
            return false;
        }
        
        switch(property[2])
        {
       
        case "dungeon":
            if(this.plugin.dungeonManager.getDungeon(value) == null)
            {
                return false;
            }
            portal.setDungeon(value);
            break;
        case "pos1":
            if (property.length > 5) {
                portal.setPos1(new Location(p.getWorld(), Integer.parseInt(property[3]), Integer.parseInt(property[4]), Integer.parseInt(property[5])));
            } 
            else {
                portal.setPos1(p.getLocation());
            }
            break;
        case "pos2":
            if (property.length > 5) {
                portal.setPos2(new Location(p.getWorld(), Integer.parseInt(property[3]), Integer.parseInt(property[4]), Integer.parseInt(property[5])));
            } else {
                portal.setPos2(p.getLocation());
            }
            break;
        case "lobby":
            portal.setLobby(p.getLocation());
            break;
        default : return false;
            
        }
        
        portal.savePortal();
        return true;
    }
    
    /*
     * Prints a portal's properties
     */
    public boolean info(CommandSender sender, String portal)
    {
        Portal p = this.getPortal(portal);
        if(p == null)
        {
            return false;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        Location pos1 = p.getPos1(), pos2 = p.getPos2(), lobby = p.getLobby();
        double x1 = pos1.getX(), y1 = pos1.getY(), z1 = pos1.getZ(),
               x2 = pos2.getX(), y2 = pos2.getY(), z2 = pos2.getZ(),
               x3 = lobby.getX(), y3 = lobby.getY(), z3 = lobby.getZ();
        
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "SevenDungeons "
                + ChatColor.RESET + ": " + ChatColor.YELLOW + p.getId() + ChatColor.RESET + " info :\n World : " 
                + p.getWorldName() + "\n Dungeon : " + p.getDungeon().getId() +
                "\n Pos 1 : X = " + df.format(x1) + ", Y = " + df.format(y1) + ", Z = " + df.format(z1) +
                "\n Pos 2 : X = " + df.format(x2) + ", Y = " + df.format(y2) + ", Z = " + df.format(z2) +
                "\n Lobby : X = " + df.format(x3) + ", Y = " + df.format(y3) + ", Z = " + df.format(z3));
        
        return true;
    }
    
    public boolean loadPortals() {
        String id, dungeon, world;
        int x1, y1, z1, x2, y2, z2;
        float lobby_x, lobby_y, lobby_z;
        if(!this.plugin.hasDatabase()) {
            SevenDungeons.log("Portals not loaded: database unavailable.", "SevenDungeons", ChatColor.YELLOW);
            return false;
        }
        try {
            PreparedStatement statement = this.plugin.getConnection().prepareStatement("SELECT * FROM portals");
            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                id = rs.getString("id");dungeon = rs.getString("dungeon_id");world = rs.getString("world");
                x1 = rs.getInt("pos1_x"); y1 = rs.getInt("pos1_y"); z1 = rs.getInt("pos1_z");
                x2 = rs.getInt("pos2_x"); y2 = rs.getInt("pos2_y"); z2 = rs.getInt("pos2_z");
                lobby_x = rs.getFloat("lobby_x"); lobby_y = rs.getFloat("lobby_y"); lobby_z = rs.getFloat("lobby_z");
                Portal portal = new Portal(this.plugin, world, id);
                
                portal.setDungeon(dungeon);
                portal.setPos1(new Location(Bukkit.getWorld(portal.getWorldName()), x1, y1, z1));
                portal.setPos2(new Location(Bukkit.getWorld(portal.getWorldName()), x2, y2, z2));
                portal.setLobby(new Location(Bukkit.getWorld(portal.getWorldName()), lobby_x, lobby_y, lobby_z));
            }
            SevenDungeons.log("Portals loaded.", "SevenDungeons", ChatColor.GREEN);
        }catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
