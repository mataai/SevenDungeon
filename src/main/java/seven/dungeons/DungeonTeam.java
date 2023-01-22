package seven.dungeons;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import seven.dungeons.managers.TeamManager;

public class DungeonTeam {
    
    private ArrayList<DungeonPlayer> players = new ArrayList<DungeonPlayer>();
    private Scoreboard score;
    private Dungeon dungeon;
    private int delay;
    private boolean isPlaying;
    private Game game;
    
    /*
     * Creating a Team. 
     */
    public DungeonTeam (Dungeon dungeon)
    {
        this.dungeon = dungeon;
        this.delay = 11;
        this.isPlaying = false;
        this.game = null;
        
        //Setting up the scoreboard
        this.score = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = score.registerNewObjective("lobby", "", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        score.registerNewTeam("playerCount");
        score.registerNewTeam("playerList");
        
        updateScoreboard();
    }
    
    /*
     * Add a DungeonPlayer to a DungeonTeam and update scoreboard accordingly
     */
    public void addPlayer (DungeonPlayer player) {
        this.players.add(player);
        player.setTeam(this);
        this.teamMessage(ChatColor.AQUA + player.getPlayer().getDisplayName() + ChatColor.RESET + " joined the team.");
        this.teamMessage(this.players.size() + " / " + this.dungeon.getNbPlayers() + " players.");
        Player p = player.getPlayer();
        p.setScoreboard(this.score);
        
        //Add the player to the list on scoreboard
        updateScoreboard();
        
        if(this.isReady())
        {
            Objective objective = this.score.getObjective("lobby");
            objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + this.delay + "...");
        }
    }

    public void disconnect(DungeonPlayer player){
        if (!(players.contains(player)))
        {
            return;
        }

        if(this.isPlaying) {
            //this.game.exitDungeon(player);
            this.game.goOffline(player);
        }
        else {
            player.getPlayer().teleport(player.getPlayer().getWorld().getSpawnLocation());
            //Remove DungeonPlayer from DungeonTeam
            players.remove(player);
            player.setTeam(null);
            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            updateScoreboard();
        }

        //Delete the DungeonTeam if it's empty
        if(this.getNbPlayers() < 1)
        {
            if(this.isPlaying) {
                this.game.endGame();
            }
            TeamManager.teams.remove(this);
            SevenDungeons.log("A dungeon team was deleted.");
            return;
        }
    }
    /*
     * Remove a player from a DungeonTeam and update scoreboard accordingly
     */
    public void removePlayer (DungeonPlayer player) {
        // Can't remove if it's not in the team
        if (!(players.contains(player)))
        {
            return;
        }
        
        if(this.isPlaying) {
            this.game.exitDungeon(player);
        }
        else {
            player.getPlayer().teleport(player.getPlayer().getWorld().getSpawnLocation());
            updateScoreboard();
        }
        // Remove DungeonPlayer from DungeonTeam
        players.remove(player);
        player.setTeam(null);
        player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        
        //Delete the DungeonTeam if it's empty
        if(this.getNbPlayers() < 1)
        {
            if(this.isPlaying) {
                this.game.endGame();
            }
            TeamManager.teams.remove(this);
            SevenDungeons.log("A dungeon team was deleted.");
            return;
        }
        
        // If it was a ready team, make it a waiting team and reset delay
        if(TeamManager.teams.contains(this) && this.isReady())
        {
            this.resetDelay(16);
            Objective objective = score.getObjective("lobby");
            objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Waiting for players...");
        }
        
        this.teamMessage(this.players.size() + " / " + this.dungeon.getNbPlayers() + " players." + 
                " (" + ChatColor.AQUA + player.getPlayer().getName() + ChatColor.RESET + " left)");
    }
    
    public void teamMessage(String message)
    {
        for(DungeonPlayer p : players)
        {
            p.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + 
                    "Team" + ChatColor.RESET + ": " + message);
        }
    }
    
    public boolean hasPlayer(DungeonPlayer player)
    {
        for(DungeonPlayer dp : players)
        {
            if(dp == player.getPlayer())
            {
                return true;
            }
        }
        return false;
    }
    
    public int getNbPlayers()
    {
        return this.players.size();
    }
    
    public ArrayList<DungeonPlayer> getPlayers ()
    {
        return this.players;
    }
    
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }
    
    public Dungeon getDungeon() {
        return this.dungeon;
    }
    
    // Update delay (-1) and update scoreboard
    public void updateDelay()
    {
        this.delay--;
        this.score.getObjective("lobby").setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + this.delay + "...");
        //If waiting is done, launch game and switch team to Playing teams.
        if(this.delay <= 0)
        {
            this.isPlaying = true;
            this.score.getObjective("lobby").setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Launching game...");
            this.game.start();
        }
    }
    
    public int getDelay()
    {
        return this.delay;
    }
    
    // Resets delay when a player leaves of the game isn't ready.
    public void resetDelay(int newDelay)
    {
        if(newDelay > 0 && newDelay < 30)
        {
            this.delay = newDelay;
        }
    }
    
    public boolean isReady() {
        return (this.players.size() >= this.dungeon.getNbPlayers());
    }
    
    public boolean isPlaying() {
        return this.isPlaying;
    }
    
    public void updateScoreboard() {
        this.score.getObjective("lobby").unregister();
        Objective objective = score.registerNewObjective("lobby", "", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        //Title
        objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Waiting for players...");
        
        //Number of players
        Score total = objective.getScore(ChatColor.BOLD + "» Players");
        total.setScore(5);
        //Team playerCount = score.registerNewTeam("playerCount");
        Team playerCount = score.getTeam("playerCount");
        playerCount.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
        playerCount.setPrefix(ChatColor.YELLOW + Integer.toString(this.players.size()) + ChatColor.RESET + " / " + ChatColor.YELLOW + this.dungeon.getNbPlayers());
        objective.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(4);
        Score space1 = objective.getScore("");
        space1.setScore(3);
        
        //Player list
        Score playerList = objective.getScore(ChatColor.BOLD + "» Team :");
        playerList.setScore(2);
        
        Team list = score.getTeam("playerList");
        for(DungeonPlayer dp : this.players) {
            String playerName = dp.getPlayer().getDisplayName();
            list.addEntry(playerName);
            objective.getScore(playerName).setScore(1);
        }
        
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public Game getGame() {
        return this.game;
    }
}
