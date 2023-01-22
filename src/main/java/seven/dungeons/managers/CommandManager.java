package seven.dungeons.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import seven.dungeons.Dungeon;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class CommandManager {
    
    private SevenDungeons plugin;
    
    public CommandManager (SevenDungeons plugin)
    {
        this.plugin = plugin;
    }
    
    /*
     * Command management
     */
    public void command(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(SevenDungeons.message("Console can't edit dungeons."));
            return;
        }
        Player player = (Player) sender;
        if(args[0].equals("npc")) {
            this.npcCommand(player, args);
            return;
        }
        if(args[0].equals("build") && sender.hasPermission("seven.builder")) {
            this.build(player);
            return;
        }
        if(args[0].equals("holo") && sender.hasPermission("seven.builder") && args.length > 1) {
            this.holo(player, args[1]);
            return;
        }
        if(args[0].equalsIgnoreCase("gamelist")){
            this.gamelist(sender);
            return;
        }
        if(args.length < 2)
        {
            notEnoughArgs(sender);
            return;
        }
        if(!sender.hasPermission("seven.dungeons"))
        {
            sender.sendMessage(SevenDungeons.message("This command doesn't exist ... or does it ?"));
            return;
        }
        
        
        switch(args[0])
        {
            case "createportal" : this.createPortal(sender, args, player);
            break;
            case "editportal" : this.editPortal(sender, args, player);
            break;
            case "delportal" : case "deleteportal" : this.deletePortal(sender, args);
            break;
            case "createdungeon" : this.createDungeon(sender, args, player);
            break;
            case "editdungeon" : this.editDungeon(sender, args, player);
            break;
            case "deldungeon" : case "deletedungeon" : this.deleteDungeon(sender, args);
            break;
            case "dungeoninfo" : this.dungeonInfo(sender, args);
            break;
            case "portalinfo" : this.portalInfo(sender,args);
            break;
            case "savesigns" : this.saveSigns(player, args);
            break;
            case "signal" : this.signal(sender, args);
            break;
            case "score" : this.getScore(sender, args, player);
            break;
            default: sender.sendMessage(SevenDungeons.message("This command doesn't exist, sorry boi."));
        }
    }

    public void getScore(CommandSender sender, String[] args, Player player){
        if(args[1].equalsIgnoreCase("reset")){
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        if(!args[1].matches("[0-9]+") || Integer.parseInt(args[1]) < 0){
            sender.sendMessage("The game id should be a positive integer.");
            return;
        }
        if(Integer.parseInt(args[1]) > GameManager.games.size() - 1){
            sender.sendMessage("Game id doesn't exist.");
            return;
        }
        Game g = GameManager.games.get(Integer.parseInt(args[1]));
        player.setScoreboard(g.getGameScore().getScore());
    }

    /*
        Turn on / off a signal in dungeon for debugging.
     */
    public void signal(CommandSender sender, String[] args){
        if(!args[3].matches("on|ON|OFF|off") || !args[1].matches("[0-9]+") || !args[2].matches("[0-9]+")){
            sender.sendMessage("The correct syntax is /7d signal game_id signal_id on_or_off");
            return;
        }
        if(Integer.parseInt(args[1]) < 0 || Integer.parseInt(args[2]) < 0){
            sender.sendMessage("Both the game id and the signal id should be positive integers.");
            return;
        }
        if(Integer.parseInt(args[1]) > GameManager.games.size() - 1){
            sender.sendMessage("Game id doesn't exist.");
            return;
        }
        Game g = GameManager.games.get(Integer.parseInt(args[1]));
        if(args[3].equalsIgnoreCase("on")){
            g.activate(Integer.parseInt(args[2]), null);
        }
        else{
            g.deactivate(Integer.parseInt(args[2]), null);
        }
    }

    public void gamelist(CommandSender sender){
        ArrayList<Game> games = GameManager.games;
        if(games.isEmpty()){
            sender.sendMessage("There are no active games currently.");
        }
        else{
            int i = 0;
            for(Game g : games){
                sender.sendMessage(i + " : " + g.getDungeon().getName());
                i++;
            }
        }
    }
    
    /*
     * Creates a portal
     */
    public void createPortal(CommandSender sender, String[] args, Player player)
    {
       if(this.plugin.portalManager.add(player.getWorld().getName(), args[1]))
       {
           sender.sendMessage(SevenDungeons.message("Portal was created."));
       }
       else
       {
           sender.sendMessage(SevenDungeons.message("Portal already exists. Delete it or edit it instead."));
       }
    }

    /*
     * Edits a portal's property
     */
    public void editPortal(CommandSender sender, String[] args, Player player) 
    {
        String value;
        if(args.length < 3)
        {
            notEnoughArgs(sender);
            return;
        }
        else if(args.length < 4)
        {
            value = "null";
        }
        else
        {
            value = args[3];
        }
        
        if(this.plugin.portalManager.set(args[1], args, value, player))
        {
            sender.sendMessage(SevenDungeons.message("Portal was edited."));
        }
        else
        {
            sender.sendMessage(SevenDungeons.message("This property doesn't exist or the entered value is unexpected."));
        } 
    }
    
    /*
     * Deletes a portal
     */
    public void deletePortal(CommandSender sender, String[] args) 
    {
        if(this.plugin.portalManager.removePortal(args[1]))
        {
            sender.sendMessage(SevenDungeons.message("Portal was deleted."));
        }
        else
        {
            sender.sendMessage(SevenDungeons.message("This portal doesn't exist."));
        }
    }
    
    /*
     * Deletes a dungeon
     */
    public void deleteDungeon(CommandSender sender, String[] args) 
    {
        if(this.plugin.dungeonManager.removeDungeon(args[1]))
        {
            sender.sendMessage(SevenDungeons.message("Dungeon was deleted."));
        }
        else
        {
            sender.sendMessage(SevenDungeons.message("This dungeon doesn't exist."));
        }
    }

    /*
     * Edits a dungeon's property
     */
    public void editDungeon(CommandSender sender, String[] args, Player player) 
    {
        String value;
        if(args.length < 3)
        {
            notEnoughArgs(sender);
            return;
        }
        else if(args.length < 4)
        {
            value = "null";
        }
        else
        {
            value = args[3];
        }
        if(this.plugin.dungeonManager.set(args[1], args[2], value, player))
        {
            sender.sendMessage(SevenDungeons.message("Dungeon was edited."));
        }
        else
        {
            sender.sendMessage(SevenDungeons.message("This property doesn't exist or the entered value is unexpected."));
        } 
    }

    /*
     * Creates a new dungeon with an ID in the world the player is
     */
    public void createDungeon(CommandSender sender, String[] args, Player player) 
    {
        if(this.plugin.dungeonManager.add(player.getWorld().getName(), args[1]))
        {
            sender.sendMessage(SevenDungeons.message("Dungeon was created."));
        }
        else
        {
            sender.sendMessage(SevenDungeons.message("Dungeon already exists. Delete it or edit it instead."));
        }
    }
    
    /*
     * Prints a dungeon's properties
     */
    public void dungeonInfo(CommandSender sender, String[] args)
    {
        if(args.length < 2)
        {
            notEnoughArgs(sender);
            return;
        }
        if(!this.plugin.dungeonManager.info(sender, args[1]))
        {
            sender.sendMessage(SevenDungeons.message("This dungeon doesn't exist."));
        }
    }
    
    /*
     * Prints a portal's properties
     */
    public void portalInfo(CommandSender sender, String[] args)
    {
        if(args.length < 2)
        {
            notEnoughArgs(sender);
            return;
        }
        if(!this.plugin.portalManager.info(sender, args[1]))
        {
            sender.sendMessage(SevenDungeons.message("This portal doesn't exist."));
        }
    }
    
    public void notEnoughArgs(CommandSender sender)
    {
        sender.sendMessage(SevenDungeons.message("Not enough arguments."));
    }
    
    public static Integer tryParse(String arg) {
        try {
          return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
          return null;
        }
      }
    
    public void build(Player player) {
        ArrayList<Player> builders = this.plugin.dungeonListener.getBuilders();
        if(builders.contains(player)) {
            builders.remove(player);
            player.sendMessage("§cYou were removed from the lobby builders.");
        }
        else {
            builders.add(player);
            player.sendMessage("§aYou were added to the lobby builders.");
        }
    }

    public void saveSigns(Player player, String[] args) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeon(args[1]);
        if(dungeon == null) {
            player.sendMessage(SevenDungeons.message("You are not in a dungeon !"));
            return;
        }
        if(!dungeon.getWorldName().equals(player.getWorld().getName())) {
            player.sendMessage(SevenDungeons.message("You are not in the right world to save this dungeon's signs."));
            return;
        }
        Location location = player.getLocation();
        if(location.getBlockX() + location.getBlockY() + location.getBlockY() > 2000) {
            player.sendMessage(SevenDungeons.message("This area is too big to be saved. Your dungeon must be smaller."));
            return;
        }
        dungeon.findSigns(location);
        player.sendMessage(SevenDungeons.message("All signs were saved."));
    }
    
    public void npcCommand(Player player, String[] args) {
        if(args.length < 5) {
            return;
        }
        Game game = this.plugin.gameManager.getGameByMap(args[1]);
        if(game == null) { return; }
        game.interactNPC(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), player);
    }
    
    public void holo(Player player, String string) {
        if(string.equalsIgnoreCase("remove")) {
            for(Entity e : player.getNearbyEntities(2, 2, 2)) {
                if(e instanceof ArmorStand && !((ArmorStand) e).isVisible()) {
                    e.remove();
                    player.sendMessage("§a» Hologram removed !");
                    return;
                }
            }
            player.sendMessage("§e» No hologram to remove nearby.");
        }
        else {
            Location l = player.getLocation();
            ArmorStand as = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND); //Spawn the ArmorStand
            String message = ChatColor.translateAlternateColorCodes('&', string);
            message = message.replace("_", " ");
            as.setGravity(false); //Make sure it doesn't fall
            as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
            as.setCustomName(message); //Set this to the text you want
            as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
            as.setVisible(false); //Makes the ArmorStand invisible
            player.sendMessage("§a» Hologram placed !");
        }
    }
}
