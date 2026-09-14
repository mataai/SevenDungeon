package seven.dungeons;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import seven.dungeons.listeners.DungeonListener;
import seven.dungeons.listeners.LaunchListener;
import seven.dungeons.managers.CommandManager;
import seven.dungeons.managers.DungeonManager;
import seven.dungeons.managers.GameManager;
import seven.dungeons.managers.PartyManager;
import seven.dungeons.managers.PortalManager;
import seven.dungeons.managers.TeamManager;
import seven.dungeons.managers.WorldManager;

public class SevenDungeons extends JavaPlugin 
{
    
    public MultiverseCore multiverse = null;
    
    // Listeners
    private LaunchListener launchListener;
    public DungeonListener dungeonListener;
    
    public FileUtils fileU;
    
    // Managers
    public DungeonManager dungeonManager;
    public PortalManager portalManager;
    public WorldManager worldManager = new WorldManager(this);
    public PartyManager partyManager;
    public TeamManager teamManager;
    public GameManager gameManager;
    
    // Commands
    public CommandManager commandManager = new CommandManager(this);
    
    // Connection
    private Connection connection;

	@Override
	public void onEnable()
	{
	    super.onEnable();
	    // Dungeons live in bundle folders; the database is only needed for portals
	    // and for /7d importdb. A failed connection is not fatal.
	    try {
            this.openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            this.connection = null;
            log("Database unavailable (" + e.getMessage() + "). Portals will not load and /7d importdb is disabled.", "SevenDungeons", ChatColor.YELLOW);
        }
	    multiverse = this.getMultiverseCore();
	    dungeonManager = new DungeonManager(this);
	    portalManager = new PortalManager(this);
	    partyManager = new PartyManager(this);
	    teamManager = new TeamManager(this, this);
	    gameManager = new GameManager(this);
	    launchListener = new LaunchListener(this);
        dungeonListener = new DungeonListener(this);
        this.getServer().getPluginManager().registerEvents(this.launchListener, this);
        this.getServer().getPluginManager().registerEvents(this.dungeonListener, this);
	    
	    //Suppression des instanceX anciennes
        File file;
        for(MultiverseWorld w : this.multiverse.getMVWorldManager().getMVWorlds())
        {
            if(w.getName().matches("instance.*"))
            {
                file = new File(this.multiverse.getServerFolder().getPath(), w.getName());
                this.multiverse.getMVWorldManager().removeWorldFromConfig(w.getName());
                try {
                    FileUtils.deleteFolder(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	@Override
    public void onDisable()
    {
		super.onDisable();
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "npc remove all";
        Bukkit.dispatchCommand(console, command);
		log("Disabling ...", "SevenDungeons", ChatColor.GREEN);
    }
	
	public MultiverseCore getMultiverseCore() {
        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        log("Multiverse-Core loaded !", "SevenDungeons", ChatColor.GREEN);
        return core;
    }
	
	static public void log(String message, String title, ChatColor color)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        console.sendMessage(color +""+ ChatColor.BOLD + "["+title+"] "+ ChatColor.RESET + message);
        
    }
	
	static public void log(String message, String title)
    {
        ChatColor color = ChatColor.GREEN;
        log(message, title,color);
        
    }
	
	static public void log(String message)
    {
        ChatColor color = ChatColor.GREEN;
        log(message,"SevenDungeons",color);
        
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
       
	    if(cmd.getName().equalsIgnoreCase("party"))
	    {
	        this.partyManager.partyCommand(sender, args);
	        return true;
	    }
	    else if(cmd.getName().equalsIgnoreCase("7d"))
	    {
            this.commandManager.command(sender, args);
            return true;
	    }
	    else if(cmd.getName().equalsIgnoreCase("leave"))
	    {
	        this.launchListener.command(sender);
	        return true;
	    }
	    
    return false;
    }
	
	public void openConnection() throws SQLException, ClassNotFoundException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }
        
        synchronized (this) {
            if (this.connection != null && !this.connection.isClosed()) {
                return;
            }
			// TODO use the right database connection !
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + "localhost" + ":" + "3308" + "/" +
            "sevendungeons", "root", "");
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "SQL connection enabled");
        }
        
    }
	
	/** The legacy MySQL connection, or null if it could not be opened. */
	public Connection getConnection()
    {
        return this.connection;
    }
	
	public boolean hasDatabase()
    {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
	
	static public String message(String message)
    {
        return ChatColor.GREEN + "" + ChatColor.BOLD + "7Dungeons" + ChatColor.RESET + " : " + message;
    }
	
	public static Plugin getPlugin() {
	    return Bukkit.getPluginManager().getPlugin("SevenDungeons");
	}
	
}
