package seven.dungeons;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import seven.dungeons.managers.PartyManager;

public class Party {
    
    private ArrayList<Player> players = new ArrayList<Player>();
    private Player leader;
    private int nbPlayer;
    
    public Party (Player player1, Player player2)
    {
        this.players.add(player1);
        this.leader = player1;
        this.players.add(player2);
        this.nbPlayer = 2;
    }
    
    public ArrayList<Player> getPlayers()
    {
        return this.players;
    }
    
    /*
     * Removes a player from the party
     */
    public void removePlayer(Player player)
    {
        this.players.remove(player);
        this.nbPlayer--;
        // Delete the party if only 1 player remains
        if(this.getNbPlayers() < 2)
        {
            Player p = this.players.get(0);
            players.remove(p);
            p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                          "> You were the only one remaining in your party, so it was automatically deleted.");
            PartyManager.clearInvitations(this);
            PartyManager.parties.remove(this);
            return;
        }
        // Change leader if the removed player was the leader
        else {
            if(this.leader == player) {
                this.leader = players.get(0);
                players.get(0).sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                        "> " + "You are now the leader of yout party.");
            }
        }
    }
    
    public void addPlayer(Player player)
    {
        this.players.add(player);
        this.nbPlayer++;
    }
    
    public Player getPlayer(String name)
    {
        for(Player p : players)
        {
            if(p.getName().equalsIgnoreCase(name))
            {
                return p;
            }
        }
        return null;
    }
    
    public Player getLeader()
    {
        return this.leader;
    }
    
    public void setLeader(Player player)
    {
        this.leader = player;
    }
    
    public int getNbPlayers()
    {
        return this.nbPlayer;
    }
}
