package seven.dungeons.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import seven.dungeons.*;
import seven.dungeons.managers.PortalManager;

public class LaunchListener implements Listener {
    
    private SevenDungeons plugin;
    private ArrayList<Player> players = new ArrayList<Player>();
    
    public LaunchListener(SevenDungeons plugin)
    {
        this.plugin = plugin;
    }
    
    /*
     * When a player enters a portal
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPortalEntered(EntityPortalEnterEvent e) {
        // Not a player ? Not interesting
        if(! (e.getEntity() instanceof Player))
        {
            return;
        }   
        Player p = (Player) e.getEntity();
        // If the player is actually treated by this class, not interesting
        if(players.contains(p))
        {
            return;
        }
        players.add(p);
        this.untreatPlayer(p);
        Location loc = e.getLocation();
        // Find which portal was entered
        for(Portal portal : PortalManager.portals)
        {
            Location pos1 = portal.getPos1();
            Location pos2 = portal.getPos2();
            double x = loc.getX(), y = loc.getY(), z = loc.getZ();
            // Verify if this very portal was the one entered
            if (x >= pos1.getX() && x <= pos2.getX() && y >= pos1.getY() 
               && y <= pos2.getY() && z >= pos1.getZ() && z <= pos2.getZ())
            {
                // Find if player is in a party
                Party possibleParty = this.plugin.partyManager.getParty(p);
                // Find which dungeon is linked to the portal
                Dungeon dungeon = portal.getDungeon();
                // Cancel teleportation if the dungeon is in build mode 
                if(dungeon.isBuildmode()) {
                    p.sendMessage(ChatColor.GOLD + "Sorry, this dungeon is currently being edited. Please come back later.");
                    return;
                }
                // No party ? Teleport the player and find a DungeonTeam for him
                if(possibleParty == null)
                {
                    this.plugin.teamManager.findTeamFor(p, dungeon);
                    teleportToLobby(portal.getLobby(), p);
                }
                // In a party :
                else
                {
                    int max = dungeon.getNbPlayers();
                    // Is the party too big for the dungeon ?
                    if(possibleParty.getNbPlayers() > max)
                    {
                        p.sendMessage(SevenDungeons.message("Your party is too big for this dungeon ! You must be at most " 
                        + max + " players."));
                        return;
                    }
                    //Is the player the leader of his party ? If not, no teleportation !
                    Player leader = possibleParty.getLeader().getPlayer();
                    if(leader != p) {
                        p.sendMessage(SevenDungeons.message("You must be the leader of your party to enter a portal. Only "
                        + leader.getDisplayName() + " may enter right now."));
                        return;
                    }
                    //Find a team for the party
                    this.plugin.teamManager.findTeamFor(possibleParty, dungeon);
                    //Teleport the party
                    for(Player partyPlayer : possibleParty.getPlayers())
                    {
                        teleportToLobby(portal.getLobby(), partyPlayer);
                    }
                }
                return;
            }
        }
    }
    
    /*
     * If a player disconnects, he is removes from his party and from his DungeonTeam.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        player.setCollidable(true);
        this.plugin.partyManager.removeFromParty(player);
        this.plugin.teamManager.removeFromDungeonTeam(player);
        players.remove(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        SevenDungeons.log("Trying to find DungeonPlayer...");
        DungeonPlayer dp = this.plugin.teamManager.findDungeonPlayer(player);
        if(dp != null && dp.getTeam().isPlaying()){
            SevenDungeons.log("Player found when reconnected");
            dp.getTeam().getGame().goOnline(dp);
        }
    }
    
    /*
     * Teleport a player (or players if the sender is the leader or
     * a party) out of a lobby. Doesn't teleport if the player is
     * in a party that he isn't the leader.
     */
    public void command(CommandSender sender)
    {
        // Is the sender a player ?
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            p.setCollidable(true);
            // Is the player in a lobby ?
            if(this.plugin.teamManager.findDungeonPlayer(p) == null) {
                p.sendMessage("> You are not in a lobby !");
                return;
            }
            
            if(p.isInsideVehicle()) {
                Vehicle v = (Vehicle) p.getVehicle();
                v.removeMetadata("xmt", this.plugin);
                v.removePassenger(p);
                v.remove();
            }
            
            // Is the player in a party ?
            Party party = this.plugin.partyManager.getParty(p);
            // In a party : Make sure it's its leader and teleport all the party back.
            
            if(party != null)
            {
                if(party.getLeader() == p)
                {
                    for(Player partyPlayer : party.getPlayers())
                    {
                        this.plugin.teamManager.leaveTeam(partyPlayer);
                    }
                }
                else
                {
                    p.sendMessage(SevenDungeons.message("You can't leave a dungeon if you are not the leader of your party."));
                    return;
                }
            }
            // No party : Remove from DungeonTeam and teleport back
            else
            {
                this.plugin.teamManager.leaveTeam(p);
            }
        }
    }
    
    
    /*
     * Teleport player to the given location. Removes the player from this object.
     */
    public void teleportToLobby(Location loc, Player p)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                p.teleport(loc, TeleportCause.PLUGIN);
            }
        }.runTaskLater(this.plugin, 1l);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                players.remove(p);
            }
        }.runTaskLater(this.plugin, 20);
    }
    
    /*
     * Removes the player from this object
     */
    public void untreatPlayer(Player p) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                players.remove(p);
            }
        }.runTaskLater(this.plugin, 100);
    }
    
}
