package seven.dungeons.managers;

import java.util.ArrayList;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Party;
import seven.dungeons.SevenDungeons;

public class PartyManager {

    private SevenDungeons plugin;
    public static ArrayList<Party> parties = new ArrayList<Party>();
    public static ArrayList<Player> inviterPlayer = new ArrayList<Player>(), invitedPlayer = new ArrayList<Player>();
    public static ArrayList<Integer> timer = new ArrayList<Integer>();
    
    
    public PartyManager (SevenDungeons plugin)
    {
        this.plugin = plugin;
        this.timerInvitations();
    }
    
    /*
     * Party commands management
     */
    public void partyCommand(CommandSender sender, String[] args)
    {
        if(args.length < 1 || args[0].equalsIgnoreCase("help"))
        {
            sender.sendMessage(ChatColor.BOLD + "The party command must be used as below." + ChatColor.RESET + "\n" +
                               ChatColor.YELLOW + "/party invite <player>" + ChatColor.RESET + " : Invite a player to a party.\n" +
                               ChatColor.YELLOW + "/party leave" + ChatColor.RESET + " : Leave a party.\n" +
                               ChatColor.YELLOW + "/party kick <player>" + ChatColor.RESET + " : Kick a player from a party.\n" +
                               ChatColor.YELLOW + "/party leader <player>" + ChatColor.RESET + " : Make another player leader of a party.");
            return;
        }
        if(!(sender instanceof Player))
        {
            sender.sendMessage("Console can't deal with parties.");
            return;
        }
        Player player = (Player) sender;
        if(args[0].equalsIgnoreCase("leave")) {
            this.leaveParty(player);
        }
        else {
            if(args.length < 2)
            {
                this.invitePlayer(player, args[0]);
                return;
            }
            switch(args[0].toUpperCase())
            {
                case "KICK" : this.kickPlayer(player, args[1]); break;
                case "LEADER" : this.makeLeader(player, args[1]); break;
                case "ACCEPT" : this.acceptParty(args[1], player); break;
                case "DENY" : this.denyParty(player, args[1]); break;
                case "INVITE" : this.invitePlayer(player, args[1]); break;
                default: this.invitePlayer(player, args[0]); break;
            }
        }
    }
    
    /*
     * Leave a party
     */
    public void leaveParty(Player player)
    {
        Party party = this.getParty(player);
        if(party == null)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "You are not in a party.");
            return; 
        }
        party.removePlayer(player);
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + "You left the party.");
    }
    
    /*
     * Kick a player from the party
     */
    public void kickPlayer(Player player, String kicked)
    {
        Party party = this.getParty(player);
        if(party == null)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "You are not in a party.");
            return;
        }
        if(party.getLeader() != player) {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "You are not the leader of your party.");
            return;
        }
        Player p = Bukkit.getPlayer(kicked);
        if(p == null || party != this.getParty(p))
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + kicked + ChatColor.RESET + " is not in your party.");
            return;
        }
        party.removePlayer(p);
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + ChatColor.AQUA + kicked + ChatColor.RESET + " was removed from the party.");
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + "You were kicked from the party.");
    }
    
    /*
     * Command to make another player the leader of your party
     */
    public void makeLeader(Player player, String nextLeader)
    {
        Party party = this.getParty(player);
        //Is the leader in a party ?
        if(party == null)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "You are not in a party.");
            return;
        }
        // Are you the leader ?
        if(party.getLeader() == player)
        {
            Player newLeader = party.getPlayer(nextLeader);
            // Is the next leader in YOUR party ?
            if(newLeader == null)
            {
                player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                        "> " + ChatColor.AQUA + nextLeader + ChatColor.RESET + " is not in your party.");
                return;
            }
            // Changing the leadership
            party.setLeader(newLeader);
            newLeader.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "You are now the leader of the party.");
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + nextLeader + ChatColor.RESET + " is now the leader of the party.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + "You are not the leader of the party so you can't lend the leadership.");
    }
    
    /*
     * Inviting a player via command
     */
    public void invitePlayer(Player player, String invited)
    {
        Player p = Bukkit.getPlayer(invited);
        //Player isn't online : not interesting
        if(p == null || !(p.isOnline()))
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + invited + ChatColor.RESET + " could not be found.");
            return;
        }
        DungeonPlayer dp = this.plugin.teamManager.findDungeonPlayer(player);
        if(dp != null) {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "Can't invite players while in a game lobby.");
            return;
        }
        dp = this.plugin.teamManager.findDungeonPlayer(p);
        if(dp != null) {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "Can't invite players that are in a game lobby.");
            return;
        }
        Party party = this.getParty(p);
        //Player already in a party
        if(party != null)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + invited + ChatColor.RESET + " is already in a party.");
            return;
        }
        //Player already invited
        if(getInvitationIndex(player, p) >= 0)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + invited + ChatColor.RESET + " was already invited.");
            return;
        }
        //Creating a timer for the player
        inviterPlayer.add(player);
        invitedPlayer.add(p);
        timer.add(32);
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + ChatColor.AQUA + invited + ChatColor.RESET + " was invited to your party.");
        
        /*IChatBaseComponent comp = ChatSerializer.a("{\"text\": \"> §b" + player.getName() + "§r invited you to a party. \",\"extra\":[{\"text\": \"§a§lAccept §r• \",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to accept\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party accept " + player.getName() + "\"}},{\"text\": \"§c§lDeny\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to deny\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party deny " + player.getName() + "\"}}]}");
        
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
        ((CraftPlayer) p).getHandle().b.sendPacket(packet);*/

        TextComponent text = new TextComponent("> §b" + player.getName() + "§r invited you to a party. ");

        TextComponent yes = new TextComponent("§a§lAccept §r•");
        yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to accept" )));
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()));

        TextComponent no = new TextComponent("§c§lDeny");
        no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to deny" )));
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + player.getName()));

        text.addExtra(yes);
        text.addExtra(no);
        player.spigot().sendMessage(text);
    }
    
    /*
     * When a player accepts an invitation to a party
     */
    public void acceptParty(String partyPlayer, Player player)
    {
        Player p = Bukkit.getPlayer(partyPlayer);
        if(!(p == null || p.isOnline()))
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + partyPlayer + ChatColor.RESET + " could not be found.");
            return;
        }
        DungeonPlayer dp = this.plugin.teamManager.findDungeonPlayer(player);
        if(dp != null) {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "Can't join a party in a game lobby.");
            return;
        }
        dp = this.plugin.teamManager.findDungeonPlayer(p);
        if(dp != null) {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "Can't join a party in a game lobby.");
            return;
        }
        
        int index = getInvitationIndex(p, player);
        // The invitation doesn't exist
        if(index < 0)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "The party invitation is expired.");
            return;
        }
        Party party = this.getParty(player);
        //Player already in a party
        if(party != null)
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + "You are already in a party.");
            return;
        }
        invitedPlayer.remove(index);
        inviterPlayer.remove(index);
        timer.remove(index);
        Party inviterParty = this.getParty(p);
        if(inviterParty == null) {
            inviterParty = new Party(p, player);
            PartyManager.parties.add(inviterParty);
        }
        else {
            inviterParty.addPlayer(player);
        }
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + ChatColor.AQUA + player.getName() + ChatColor.RESET + " joined your party.");
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + "You joined " + partyPlayer + "\'s party.");
    }
    
    /*
     * When a player denies a party invitation
     */
    public void denyParty(Player player, String partyPlayer)
    {
        Player p = Bukkit.getPlayer(partyPlayer);
        if(p == null || !(p.isOnline()))
        {
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                    "> " + ChatColor.AQUA + partyPlayer + ChatColor.RESET + " could not be found.");
            return;
        }
        int index = getInvitationIndex(p, player);
        if(index >= 0)
        {
            invitedPlayer.remove(index);
            inviterPlayer.remove(index);
            timer.remove(index);
        }
        p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + "> " + ChatColor.AQUA + player.getName() + ChatColor.RESET + " denied your party invitation.");
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Party " + ChatColor.RESET + 
                "> " + "You denied " + ChatColor.AQUA + partyPlayer + ChatColor.RESET + "\'s invitation.");
    }
    
    public Party getParty (Player player)
    {
        for(Party p : parties)
        {
            if(p.getPlayers().contains(player))
            {
                return p;
            }
        }
        return null;
    }
    
    /*
     * Get the index of a certain invitation 
     */
    public int getInvitationIndex(Player inviter, Player invited)
    {
        for(int i = 0; i < inviterPlayer.size(); i++)
        {
            if(inviterPlayer.get(i) == inviter)
            {
                if(PartyManager.invitedPlayer.get(i) == invited)
                {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /*
     * Removes 5 seconds of every timer. If timer is below 5 seconds,
     * removes it and removes the invitation. Looping every 5
     * seconds.
     */
    private void timerInvitations()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(int i = 0; i < timer.size(); i++)
                {
                    int second = timer.get(i);
                    if(second < 5)
                    {
                        inviterPlayer.remove(i);
                        invitedPlayer.remove(i);
                        timer.remove(i);
                    }
                    else
                    {
                        timer.set(i, second - 5);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0, 100);
    }
    
    
    /*
     * Remove a player from a party.
     */
    public void removeFromParty(Player player)
    {
        for(Party party : PartyManager.parties)
        {
            for(Player p : party.getPlayers())
            {
                if(p == player)
                {
                    party.removePlayer(player);
                    return;
                }
            }
        }
    }
    
    /*
     * Clears invitations for a deleted party
     */
    static public void clearInvitations(Party party) {
        for(Player p : party.getPlayers()) {
            for(int i = 0; i < inviterPlayer.size(); i++)
            {
                if(inviterPlayer.get(i) == p)
                {
                    inviterPlayer.remove(i);
                    invitedPlayer.remove(i);
                    timer.remove(i);
                }
            }
        }
    }
}
