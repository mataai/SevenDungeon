package seven.dungeons;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameScore {
    
    // Bukkit Scoreboard
    private Scoreboard board;
    
    // Collectibles
    private int nbGems = 0;
    private int nbCrystals = 0;
    private int gemCount = 0;
    private int crystalCount = 0;
    
    // Time Counter
    private int timeLimit;
    private int timeLeft;
    
    // Dungeon Name
    private String name;
    
    // Game
    private Game game;
    
    // Entries (players)
    private ArrayList<Team> playerEntries = new ArrayList<Team>();
    
    public static ChatColor[] colors = {ChatColor.AQUA, ChatColor.BLACK, ChatColor.DARK_PURPLE, ChatColor.GRAY, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW, ChatColor.DARK_RED};
    
    // Constructor
    public GameScore(int timeLimit, String name, Game game) {
        this.timeLimit = timeLimit;
        this.timeLeft = timeLimit;
        this.name = name;
        this.game = game;

        // Scoreboard mess
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = this.board.registerNewObjective("game", "", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
       
        //Title
        objective.setDisplayName(ChatColor.GREEN + " " + ChatColor.BOLD + this.name + " ");
        
        Score space3 = objective.getScore("  ");
        space3.setScore(10);
        
        // "Teams"
        this.board.registerNewTeam("timeLimit");
        
        //Time limit
        Score time = objective.getScore(ChatColor.BOLD + "» Time left");
        time.setScore(9);
        Team left = board.getTeam("timeLimit");
        left.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
        if(this.timeLeft < 0){
            left.setPrefix("∞");
        }
        else{
            String warning = (this.timeLeft > 60) ? ChatColor.WHITE + "" : ChatColor.RED + "";

            String minutes = Integer.toString((int)Math.floor(this.timeLeft / 60));
            int sec = ((int)Math.ceil((this.timeLeft % 60)));
            String seconds = (sec < 10) ? "0" + sec : "" + sec;
            left.setPrefix(warning + minutes + " : " + seconds);
        }

        objective.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(8);
        //Score space1 = objective.getScore("");
        //space1.setScore(6);
        
        this.board.registerNewTeam("gemCount");
        
        //Gem Count
        Score gem = objective.getScore(ChatColor.BOLD + "» Gems");
        gem.setScore(5);
        Team gemC = board.getTeam("gemCount");
        gemC.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE);
        String done2 = this.gemCount == this.nbGems ? ChatColor.GREEN + "" : ChatColor.WHITE + "";
        gemC.setPrefix(done2 + this.gemCount + " / " + this.nbGems);
        objective.getScore(ChatColor.BLUE + "" + ChatColor.WHITE).setScore(4);
        
        this.board.registerNewTeam("crystalCount");
        
        //Crystal Count
        Score crystal = objective.getScore(ChatColor.BOLD + "» Crystal");
        crystal.setScore(7);
        Team crystalC = board.getTeam("crystalCount");
        crystalC.addEntry(ChatColor.GOLD + "" + ChatColor.WHITE);
        String done = this.crystalCount == this.nbCrystals ? ChatColor.GREEN + "" : ChatColor.WHITE + "";
        crystalC.setPrefix(done + this.crystalCount + " / " + this.nbCrystals);
        objective.getScore(ChatColor.GOLD + "" + ChatColor.WHITE).setScore(6);
        Score space2 = objective.getScore(" ");
        space2.setScore(3);
        
        this.board.registerNewTeam("players");
        
        //Player list
        Score players = objective.getScore(ChatColor.BOLD + "» Players");
        players.setScore(2);
        
        int i = 0;
        for(Player p : this.game.getPlayers()) {
            Team t = this.board.registerNewTeam(p.getName());
            this.playerEntries.add(t);
            t.addEntry(GameScore.colors[i] + "" + ChatColor.WHITE);
            objective.getScore(GameScore.colors[i] + "" + ChatColor.WHITE).setScore(1);
            i++;
        }
        
        this.updateScoreBoard();
    }
    
    public void incrementGemCounter() {
        this.gemCount++;
    }
    
    public void incrementCrystalCounter() {
        this.crystalCount++;
    }
    
    public int getNbGems() {
        return this.nbGems++;
    }
    
    public void incrementCrystal() {
        this.nbCrystals++;
    }
    
    public boolean countdown() {
        if(this.timeLeft >= 0){
            this.timeLeft -= 1;
        }
        this.updateScoreBoard();
        if(this.timeLeft == 0) {
            return true;
        }
        return false;
    }
    
    public Scoreboard getScore() {
        return this.board;
    }
    
    public void updateScoreBoard() {
        Team left = this.board.getTeam("timeLimit");
        if(this.timeLeft >= 0){
            String end = (this.timeLeft > 60) ? ChatColor.WHITE + "" : ChatColor.RED + "";
            left.setPrefix(end + timeString(this.timeLeft));
        }
        Team crystalC = this.board.getTeam("crystalCount");
        ChatColor chatColor2 = (this.crystalCount == this.nbCrystals) ? ChatColor.GREEN : ChatColor.WHITE;
        crystalC.setPrefix(String.valueOf(chatColor2) + this.crystalCount + " / " + this.nbCrystals);
        Team gemC = this.board.getTeam("gemCount");
        ChatColor chatColor3 = (this.gemCount == this.nbGems) ? ChatColor.GREEN : ChatColor.WHITE;
        gemC.setPrefix(String.valueOf(chatColor3) + this.gemCount + " / " + this.nbGems);
        
        
        for(int i = 0; i < this.game.getPlayers().size(); i++) {
            if(this.game.getPlayers().get(i).isOnline()){
                Team t = this.playerEntries.get(i);
                DungeonPlayer dp = this.game.getTeam().getPlayers().get(i);
                String health = "§r" + (int)Math.floor(dp.getPlayer().getHealth()/2) + " §c❤ ";
                String life = "§r" + ((dp.getLife() >= 0) ? dp.getLife() : "∞") + " §a✦";
                String name = (dp.getPlayer().getName().length() > 10) ? dp.getPlayer().getName().substring(0, 10) : dp.getPlayer().getName();
                String line = name + " " + health + life;
                t.setPrefix(line);
            }
        }
        
        /*for(DungeonPlayer dp : this.game.getTeam().getPlayers()) {
            String health = "§r" + (int)Math.floor(dp.getPlayer().getHealth()/2) + " §c❤ ";
            String life = "§r" + ((dp.getLife() >= 0) ? dp.getLife() : "∞") + " §a✦";
            String name = (dp.getPlayer().getName().length() > 10) ? dp.getPlayer().getName().substring(0, 10) : dp.getPlayer().getName();
            String line = name + " " + health + " " + life;
            this.playerEntries.add(line);
            playerList.addEntry(line);
            this.board.getObjective("game").getScore(line).setScore(1);
        }*/
    }
    
    public void endMessage(ArrayList<Player> players, String name) {
        for (Player p : players)
          p.sendMessage("§6§l" + name + " §r> Dungeon completed in §e" + timeString(this.timeLimit - this.timeLeft) + " \n§rwith§e " + this.gemCount + " / " + this.nbGems + 
                  " gems §rand§e " + this.crystalCount + " / " + this.nbCrystals + " crystals§r."); 
    }
    
    public String timeString(int seconds) {
        String time = "";
        time = time + ((seconds > 3599) ? ("0" + (int)Math.floor((seconds / 3600)) + " : ") : "00 : ");
        seconds %= 3600;
        int min = (int)Math.floor((seconds / 60));
        String min2 = (min < 10) ? ("0" + min) : min + "";
        int sec = (int)Math.ceil((seconds % 60));
        String sec2 = (sec < 10) ? ("0" + sec) : sec + "";
        return time + min2 + " : " + sec2;
    }
}
