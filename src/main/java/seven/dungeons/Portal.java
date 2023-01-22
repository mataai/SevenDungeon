package seven.dungeons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import seven.dungeons.managers.PortalManager;

public class Portal {
    
    private SevenDungeons plugin;
    
    private String id;
    private String worldName;
    
    private Location pos1 = null;
    private Location pos2 = null;
    
    private Location lobby = null;
    private Dungeon dungeon = null;

    public Portal (SevenDungeons plugin, String worldName, String id)
    {
        this.plugin = plugin;
        this.worldName = worldName;
        this.id = id;
        
        World world = Bukkit.getWorld(worldName);
        Location location = new Location(world,0,0,0);
        this.pos1 = location;
        this.pos2 = location;
        this.lobby = location;
        this.dungeon = this.plugin.dungeonManager.getDungeon("test");
        
        PortalManager.portals.add(this);
    }
    
    public void setDungeon(String dungeon) {
        Dungeon d = plugin.dungeonManager.getDungeon(dungeon);
        if(d != null)
        {
            this.dungeon = d;
        }
    }
    
    /*
     * Saves a portal in the database
     */
    public void savePortal()
    {
        try {
            Connection connection = this.plugin.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * from portals where id=?");
            statement.setString(1, this.id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                PreparedStatement update = connection.prepareStatement("UPDATE portals set dungeon_id=?, world=?,"
                        + "pos1_x=?, pos1_y=?, pos1_z=?, pos2_x=?, pos2_y=?, pos2_z=?, lobby_x=?, lobby_y=?, lobby_z=?"
                        + "where id=?");
                update.setString(1, this.dungeon.getId());update.setString(2, this.worldName);
                update.setInt(3, (int)this.pos1.getX());update.setInt(4, (int)this.pos1.getY());update.setInt(5, (int)this.pos1.getZ());
                update.setInt(6, (int)this.pos2.getX());update.setInt(7, (int)this.pos2.getY());update.setInt(8, (int)this.pos2.getZ());
                update.setFloat(9, (float)this.lobby.getX());update.setFloat(10, (float)this.lobby.getY());update.setFloat(11, (float)this.lobby.getZ());
                update.setString(12, this.id);
                update.execute();
            }
            else {
                PreparedStatement insert = connection.prepareStatement("INSERT INTO portals (id, dungeon_id, world, pos1_x,"
                        + "pos1_y, pos1_z, pos2_x, pos2_y, pos2_z, lobby_x, lobby_y, lobby_z) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                insert.setString(1, this.id);insert.setString(2, this.dungeon.getId());insert.setString(3, this.worldName);
                insert.setInt(4, (int)this.pos1.getX());insert.setInt(5, (int)this.pos1.getY());insert.setInt(6, (int)this.pos1.getZ());
                insert.setInt(7, (int)this.pos2.getX());insert.setInt(8, (int)this.pos2.getY());insert.setInt(9, (int)this.pos2.getZ());
                insert.setFloat(10, (float)this.lobby.getX());insert.setFloat(11, (float)this.lobby.getY());insert.setFloat(12, (float)this.lobby.getZ());
                insert.execute();
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Deletes a portal and removes it from the database
    public void remove()
    {
        PortalManager.portals.remove(this);
        this.removePortalConfig();
    }
    
    // Removes a portal from the database
    public void removePortalConfig()
    {
        try {
            PreparedStatement delete = this.plugin.getConnection().prepareStatement("DELETE FROM portals where id=?");
            delete.setString(1, this.id);
            delete.execute();
        }
        catch(SQLException e)
        {
            SevenDungeons.log("Portal \"" + this.id + "\" could not be removed properly." , "SevenDungeons", ChatColor.RED);
        }
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }
    
    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Location getLobby() {
        return lobby;
    }

    public String getWorldName() {
        return worldName;
    }
    
    public String getId() {
        return this.id;
    }
}
