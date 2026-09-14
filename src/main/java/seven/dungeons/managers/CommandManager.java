package seven.dungeons.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import seven.dungeons.Dungeon;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.bundle.AnchorStore;
import seven.dungeons.bundle.DungeonBundle;

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
            case "savesigns" : case "importsigns" : this.importSigns(player, args);
            break;
            case "importdb" : this.importDatabase(sender, args);
            break;
            case "anchors" : this.listAnchors(sender, args);
            break;
            case "addanchor" : this.addAnchor(player, args);
            break;
            case "delanchor" : case "removeanchor" : this.removeAnchor(player, args);
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

    /*
     * /7d importsigns <dungeon>
     * Migration helper. Rebuilds the anchor file by scanning the template world
     * from the origin up to the player's position (legacy "savesigns" behaviour).
     */
    public void importSigns(Player player, String[] args) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeon(args[1]);
        if(dungeon == null) {
            player.sendMessage(SevenDungeons.message("This dungeon doesn't exist."));
            return;
        }
        if(!dungeon.getWorldName().equals(player.getWorld().getName())) {
            player.sendMessage(SevenDungeons.message("You must stand in the dungeon's world to scan it."));
            return;
        }
        Location location = player.getLocation();
        if(location.getBlockX() + location.getBlockY() + location.getBlockZ() > 2000) {
            player.sendMessage(SevenDungeons.message("This area is too big to be scanned. Stand closer to the origin."));
            return;
        }
        int count = dungeon.importSignsFromWorld(location);
        player.sendMessage(SevenDungeons.message(count + " anchor(s) found and saved. Signs placed from now on are registered automatically."));
    }

    /*
     * /7d importdb <dungeon|all>
     * Migration helper. Rebuilds dungeon bundles (settings, messages, anchors)
     * from the legacy MySQL tables.
     */
    public void importDatabase(CommandSender sender, String[] args) {
        if(!this.plugin.hasDatabase()) {
            sender.sendMessage(SevenDungeons.message("No database connection. Check the console for the connection error."));
            return;
        }
        String id = args[1].equalsIgnoreCase("all") ? null : args[1];
        int count = this.plugin.dungeonManager.importFromDatabase(id);
        if(count < 0) {
            sender.sendMessage(SevenDungeons.message("Database import failed, see console."));
            return;
        }
        if(count == 0) {
            sender.sendMessage(SevenDungeons.message("No dungeon found in the database" + (id == null ? "." : " with id \"" + id + "\".")));
            return;
        }
        sender.sendMessage(SevenDungeons.message(count + " dungeon(s) imported into " + DungeonBundle.getRootFolder(this.plugin).getPath() + "."));
    }

    /*
     * /7d anchors <dungeon>
     * Lists the anchors of the file and flags the ones that no longer point at a command sign.
     */
    public void listAnchors(CommandSender sender, String[] args) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeon(args[1]);
        if(dungeon == null) {
            sender.sendMessage(SevenDungeons.message("This dungeon doesn't exist."));
            return;
        }
        AnchorStore anchors = dungeon.getAnchors();
        List<Location> stale = anchors.findStale();
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GREEN).append("").append(ChatColor.BOLD).append("SevenDungeons ").append(ChatColor.RESET)
          .append(": ").append(ChatColor.YELLOW).append(dungeon.getId()).append(ChatColor.RESET)
          .append(" has ").append(anchors.size()).append(" anchor(s) in ").append(anchors.getFile().getPath());
        if(stale == null) {
            sb.append("\n").append(ChatColor.GRAY).append("Template world not loaded, cannot check the signs.");
        }
        else if(!stale.isEmpty()) {
            sb.append("\n").append(ChatColor.RED).append(stale.size()).append(" anchor(s) have no command sign:");
            for(Location l : stale) {
                sb.append("\n").append(ChatColor.RED).append(" - ").append(AnchorStore.format(l));
            }
        }
        else {
            sb.append("\n").append(ChatColor.GREEN).append("All anchors point at a command sign.");
        }
        sender.sendMessage(sb.toString());
    }

    /*
     * /7d addanchor <dungeon>
     * Registers the command sign the player is looking at.
     */
    public void addAnchor(Player player, String[] args) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeon(args[1]);
        if(dungeon == null) {
            player.sendMessage(SevenDungeons.message("This dungeon doesn't exist."));
            return;
        }
        Block target = this.targetBlock(player, dungeon);
        if(target == null) {
            return;
        }
        if(!AnchorStore.isCommandSign(target)) {
            player.sendMessage(SevenDungeons.message("Look at a sign whose first line starts with \"" + AnchorStore.COMMAND_PREFIX + "\"."));
            return;
        }
        if(dungeon.addSign(target.getLocation())) {
            player.sendMessage(SevenDungeons.message("Anchor " + AnchorStore.format(target.getLocation()) + " registered."));
        }
        else {
            player.sendMessage(SevenDungeons.message("This sign is already registered."));
        }
    }

    /*
     * /7d delanchor <dungeon>
     * Unregisters the anchor the player is looking at (any block, so stale anchors can be cleaned).
     */
    public void removeAnchor(Player player, String[] args) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeon(args[1]);
        if(dungeon == null) {
            player.sendMessage(SevenDungeons.message("This dungeon doesn't exist."));
            return;
        }
        Block target = this.targetBlock(player, dungeon);
        if(target == null) {
            return;
        }
        if(dungeon.removeSign(target.getLocation())) {
            player.sendMessage(SevenDungeons.message("Anchor " + AnchorStore.format(target.getLocation()) + " removed."));
        }
        else {
            player.sendMessage(SevenDungeons.message("This block is not a registered anchor."));
        }
    }

    private Block targetBlock(Player player, Dungeon dungeon) {
        if(!dungeon.getWorldName().equals(player.getWorld().getName())) {
            player.sendMessage(SevenDungeons.message("You must stand in the dungeon's world."));
            return null;
        }
        Block target = player.getTargetBlockExact(6);
        if(target == null) {
            player.sendMessage(SevenDungeons.message("Look at a block within 6 blocks."));
            return null;
        }
        return target;
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
