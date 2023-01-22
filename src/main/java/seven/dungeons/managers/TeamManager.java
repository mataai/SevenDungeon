package seven.dungeons.managers;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import seven.dungeons.Dungeon;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.DungeonTeam;
import seven.dungeons.Game;
import seven.dungeons.Party;
import seven.dungeons.SevenDungeons;

public class TeamManager {
    
    public static ArrayList<DungeonTeam> teams = new ArrayList<DungeonTeam>();
    private Plugin plugin;
    private SevenDungeons sevenDungeons;
    
    public TeamManager(Plugin plugin, SevenDungeons plugin2)
    {
        this.plugin = plugin;
        this.sevenDungeons = plugin2;
        this.scoreTimer();
    }
    
    /*
     * Find a team (or create one) for a single player
     */
    public void findTeamFor(Player player, Dungeon dungeon)
    {
        if(this.findDungeonPlayer(player) != null) {
            return;
        }
        DungeonPlayer dp = new DungeonPlayer(player);
        // Looking for an uncompleted team
        
        for(DungeonTeam dt : teams)
        {
            if(dt.getDungeon() == dungeon && !dt.isPlaying() &&
               dt.getNbPlayers() < dungeon.getNbPlayers())
            {
                addDungeonPlayer(dt, dp);
                return;
            }
        }
        // Creating a new team for the player
        DungeonTeam newTeam = new DungeonTeam(dungeon);
        addDungeonPlayer(newTeam, dp);
        teams.add(newTeam);
    }
    
    /*
     * Find a team for a party
     */
    public void findTeamFor(Party party, Dungeon dungeon)
    {
        if(this.findDungeonPlayer(party.getLeader()) != null) {
            return;
        }
        int capacity = dungeon.getNbPlayers();
        for(DungeonTeam dt : teams)
        {
            // Is it the right dungeon, is there enough place in the team ?
            if(dt.getDungeon() == dungeon && !dt.isPlaying() &&
               dt.getNbPlayers() <= (capacity - party.getNbPlayers()))
            {
                for(Player p : party.getPlayers())
                {
                    DungeonPlayer dp = new DungeonPlayer(p);
                    addDungeonPlayer(dt, dp);
                }
                return;
            }
        }
        // Creating  a new team
        DungeonTeam newTeam = new DungeonTeam(dungeon);
        for(Player p : party.getPlayers())
        {
            DungeonPlayer dp = new DungeonPlayer(p);
            addDungeonPlayer(newTeam, dp);
        }
        teams.add(newTeam);
    }
    
    // Update the waiting delay for all ready teams.
    public void scoreTimer()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(DungeonTeam dt : TeamManager.teams)
                {
                    if(dt.isReady() && !dt.isPlaying()) {
                        dt.updateDelay();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }
    
    // Remove a player from a team
    public void removeFromDungeonTeam(Player player)
    {
        DungeonPlayer dp;
        DungeonTeam dt;
        // Is 
        dp = this.findDungeonPlayer(player);
        if(dp != null) {
            dt = dp.getTeam();
            dt.disconnect(dp);
            return;
        }
    }

    public void leaveTeam(Player player){
        DungeonPlayer dp;
        DungeonTeam dt;
        // Is
        dp = this.findDungeonPlayer(player);
        if(dp != null) {
            dt = dp.getTeam();
            dt.removePlayer(dp);
            return;
        }
    }

    
    /*
     * Get the DungeonPlayer object of a player.
     */
    public DungeonPlayer findDungeonPlayer(Player player) {
        for(DungeonTeam dt : TeamManager.teams)
        {
            for(DungeonPlayer dp : dt.getPlayers())
            {
                if(dp.getPlayer().getName().equals(player.getName()))
                {
                    dp.setPlayer(player);
                    return dp;
                }
            }
        }
        return null;
    }
    
    public void addDungeonPlayer(DungeonTeam team, DungeonPlayer player) {
        team.addPlayer(player);
        if(team.isReady()) {
            Game game = this.sevenDungeons.gameManager.addGame(team);
            team.setGame(game);
        }
    }
}
