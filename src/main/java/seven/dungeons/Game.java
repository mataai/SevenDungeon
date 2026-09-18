package seven.dungeons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;

import seven.dungeons.managers.GameManager;
import seven.dungeons.npcs.SevenNPC;
import seven.dungeons.signs.ActivableSign;
import seven.dungeons.signs.AnimatedSign;
import seven.dungeons.signs.BlockStockSign;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.signs.ChestStockSign;
import seven.dungeons.signs.NPCSign;
import seven.dungeons.signs.PlayerSpawnSign;
import seven.dungeons.signs.SoundSign;

public class Game {

    // Dungeon and team
    private DungeonTeam dungeonTeam;
    private ArrayList<Player> offlinePlayers = new ArrayList<Player>();
    private Dungeon dungeon;
    // Lobby world !
    private World exitWorld;
    // Current checkpoint level and checkpoints
    private int checkpointLevel;
    private HashMap<Player, Location> checkpoints = new HashMap<Player, Location>();
    // Gem Material
    private Material gem;
    
    // All the compiled signs
    private ArrayList<ActivableSign> signs = new ArrayList<ActivableSign>();
    
    // All the activated signs that are actively checked by the DungeonListener
    private ArrayList<AnimatedSign> asyncSigns2ticks = new ArrayList<AnimatedSign>();
    private ArrayList<CaptorSign> signs2ticks = new ArrayList<CaptorSign>();
    private ArrayList<CaptorSign> signs5ticks = new ArrayList<CaptorSign>();
    private ArrayList<NPCSign> NPCSigns = new ArrayList<NPCSign>();
    private ArrayList<ActivableSign> specialSigns = new ArrayList<ActivableSign>();
    
    // Plugin, to access Managers
    private SevenDungeons plugin;
    
    // World file name
    private String instanceName;

    // Game library
    private ArrayList<PlayerSpawnSign> spawns = new ArrayList<PlayerSpawnSign>();
    private ArrayList<BlockStockSign> blockLibrary = new ArrayList<BlockStockSign>();
    private ArrayList<ChestStockSign> chestLibrary = new ArrayList<ChestStockSign>();
    private HashMap<Integer, String> messages = new HashMap<Integer, String>();
    
    // Game Score
    private GameScore score;
    
    // Constructor
    public Game(DungeonTeam dungeonTeam, Dungeon dungeon, SevenDungeons plugin, World exitWorld) {
        this.plugin = plugin;
        this.dungeonTeam = dungeonTeam;
        this.dungeon = dungeon;
        this.instanceName = this.getNewInstanceName();
        this.exitWorld = exitWorld;
        this.checkpointLevel = 0;
        this.score = new GameScore(this.dungeon.getTimeLimit(), this.dungeon.getName(), this);
        
        // Creating a new instance world file for this game
        File sourceWorldFile = this.dungeon.getWorldFile();
        File instanceWorldFile = new File(sourceWorldFile.getParent(), this.instanceName);
        // TODO FIX
        // try {
        //     FileUtils.copyFolder(sourceWorldFile, instanceWorldFile);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        File uidFile = new File(instanceWorldFile, "uid.dat");
        uidFile.delete();
        this.plugin.worldManager.load(this.instanceName);
        
        // Compile signs based on the dungeon's locations ArrayList.
        this.plugin.gameManager.compile(this);
    }
    
    // Get a new instance name.
    public String getNewInstanceName() {
        return "instance" + GameManager.getCounter();
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    // Start the game
    public void start() {
        // Resetting scoreboards
        for(DungeonPlayer dp : this.dungeonTeam.getPlayers()) {
            dp.getPlayer().setScoreboard(this.score.getScore());
            dp.getPlayer().getInventory().clear();
        }
        if(this.spawns.size() < this.dungeonTeam.getNbPlayers()) {
            SevenDungeons.log("Some spawners are missing ... cancelling game.");
            this.endGame();
            return;
        }
        // Setting the first spawn
        this.setSpawn(this.spawns.get(0).getLocation(), null);
        // Teleporting to PlayerSpawns
        this.teleportPlayers();
        // Setting world properties according to dungeon properties
        this.plugin.gameManager.setDungeonProperties(this);
        // Activate all 0 signs.
        this.activate(0, null);
    }
    
    // End the game
    public void endGame() {
        this.score.endMessage(this.getPlayers(), this.dungeon.getName());
        while(this.NPCSigns.size() > 0) {
            this.NPCSigns.get(0).off();
        }
        Location spawn = this.exitWorld.getSpawnLocation();
        for(Player p : this.getPlayers())
        {
            if(p.hasMetadata("noDrop")) {
                p.removeMetadata("noDrop", this.plugin);
            }
            if(p.isOnline()){
                this.healPlayer(p);
                p.getInventory().clear();
                p.teleport(spawn, TeleportCause.PLUGIN);
                p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon" + ChatColor.RESET + " > The game is over ! Well played.");
            }
        }
        while(this.dungeonTeam.getNbPlayers() > 0) {
            this.dungeonTeam.removePlayer(this.dungeonTeam.getPlayers().get(this.dungeonTeam.getNbPlayers()-1));
        }
        this.asyncSigns2ticks.clear();
        this.signs2ticks.clear();
        this.signs5ticks.clear();
        this.NPCSigns.clear();
        this.specialSigns.clear();
        this.spawns.clear();
        this.blockLibrary.clear();
        this.chestLibrary.clear();
        this.messages.clear();
        this.plugin.gameManager.removeGame(this);
        this.plugin.worldManager.remove(this.instanceName);
        // try {
        //     File file = new File(this.plugin.multiverse.getServerFolder().getPath(), this.instanceName);
        //     FileUtils.deleteFolder(file);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
    
    // Activates a signal
    public void activate(int id, DungeonPlayer dp) {
        for(ActivableSign as : this.signs) {
            if(as.getActivationId() == id) {
                if(as.isSingleTarget()) {
                    as.on(dp);
                }
                else {
                    as.on();
                }
            }
        }
        for(NPCSign ns : this.NPCSigns) {
            ns.signalNPC(id);
        }
        for(int i = 0; i < this.specialSigns.size(); i++) {
            this.specialSigns.get(i).signal(id);
        }
    }
    
    // Deactivates a signal
    public void deactivate(int id, DungeonPlayer dp) {
        for(ActivableSign as : this.signs) {
            if(as.getActivationId() == id) {
                as.off();
            }
        }
    }
    
    // Handle death of a player.
    public void death(DungeonPlayer dp) {
        dp.setLife(dp.getLife() - 1);
        String life = "lives";
        Player p = dp.getPlayer();
        if(dp.getLife() <= 1) {
            life = "life";
        }
        if(dp.getLife() == 0) {
            p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon" + ChatColor.RESET + " > You lost. You are back to the lobby.");
            this.exitDungeon(dp);
            dp.setTeam(null);
            this.dungeonTeam.removePlayer(dp);
            if(this.dungeonTeam.getNbPlayers() == 0) {
                this.endGame();
            }
            return;
        }
        if(dp.getLife() > 0) {
            p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> You lost one life. " 
                    + ChatColor.AQUA + dp.getLife() + ChatColor.RESET + " " + life + " left.");
        }
        dp.getPlayer().teleport(this.checkpoints.get(dp.getPlayer()), TeleportCause.PLUGIN);
    }
    
    // Heal player upon death.
    public void healPlayer(Player player) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(1);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setFireTicks(0), 2);
        //player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

    public void goOffline(DungeonPlayer dp){
        this.offlinePlayers.add(dp.getPlayer());
        if(this.getPlayers().size() == 0){

            this.endGame();
        }
    }

    public void goOnline(DungeonPlayer dp){
        SevenDungeons.log(dp.toString());
        SevenDungeons.log(dp.getPlayer().toString());
        this.offlinePlayers.remove(dp.getPlayer());
        dp.getPlayer().setScoreboard(this.score.getScore());
    }
    
    // Makes a player leave the dungeon.
    public void exitDungeon(DungeonPlayer dp) {
        dp.getPlayer().teleport(this.exitWorld.getSpawnLocation(), TeleportCause.PLUGIN);
        dp.getPlayer().getInventory().clear();
        if(dp.getPlayer().hasMetadata("noDrop")) {
            dp.getPlayer().removeMetadata("noDrop", this.plugin);
        }
        if(this.getPlayers().size() == 0) {
            this.endGame();
        }
    }
    
    // Teleports players to the game at the beginning.
    public void teleportPlayers() {
        for(int i = 0; i < this.dungeonTeam.getNbPlayers(); i++) {
            Location location = null;
            for(PlayerSpawnSign ps : this.spawns) {
                if(ps.getPlayerNumber() == i+1) {
                    location = ps.getLocation();
                }
            }
            this.healPlayer(this.dungeonTeam.getPlayers().get(i).getPlayer());
            this.dungeonTeam.getPlayers().get(i).getPlayer().teleport(location, TeleportCause.PLUGIN);
            this.dungeonTeam.getPlayers().get(i).getPlayer().setGameMode(GameMode.ADVENTURE);
            this.checkpoints.put(this.dungeonTeam.getPlayers().get(i).getPlayer(), location);
        }
    }
    
    public void interactNPC(int id, int state, int feedback, Player player) {
        for(NPCSign ns : this.NPCSigns) {
            SevenNPC sn = ns.getSevenNPC();
            if(sn.getId() == id) {
                sn.handleFeedback(player, state, feedback);
                return;
            }
        }
    }
    
    // Getters and Setters
    
    public void setSpawn(Location location, Player p) {
        //this.getWorld().setSpawnLocation(location);
        if(p == null){
            for(Player player : this.getPlayers()){
                this.checkpoints.put(player, location);
            }
        }
        else{
            this.checkpoints.put(p, location);
        }

    }

    public ArrayList<Player> getPlayers(){
        ArrayList<Player> players = new ArrayList<Player>();
        for(DungeonPlayer dp : this.dungeonTeam.getPlayers()) {
            if(!this.offlinePlayers.contains(dp.getPlayer())){
                players.add(dp.getPlayer());
            }
        }
        return players;
    }
    
    public Block getBlockStock(int id) {
        for(BlockStockSign bs : this.blockLibrary) {
            if(bs.getId() == id) {
                return bs.getBlock();
            }
        }
        return null;
    }
    
    public ArrayList<ChestStockSign> getChestStock(int id){
        ArrayList<ChestStockSign> chests = new ArrayList<ChestStockSign>();
        for(ChestStockSign css : this.chestLibrary) {
            if(css.getId() == id) {
                chests.add(css);
            }
        }
        return chests;
    }
    
    public ArrayList<CaptorSign> getSigns2ticks() {
        return this.signs2ticks;
    }

    public ArrayList<CaptorSign> getSigns5ticks() {
        return this.signs5ticks;
    }
    
    public ArrayList<AnimatedSign> getAsyncSigns2ticks(){
        return this.asyncSigns2ticks;
    }
   
    public World getWorld() {
        return Bukkit.getWorld(this.instanceName);
    }
    
    public DungeonTeam getTeam() {

        return this.dungeonTeam;
    }
    
    public int getCheckpointLevel() {
        return this.checkpointLevel;
    }
    
    public void setCheckpointLevel(int level) {
        this.checkpointLevel = level;
    }
    
    public SevenDungeons getPlugin() {
        return this.plugin;
    }
    
    public Dungeon getDungeon() {
        return this.dungeon;
    }
    
    public void addMessage(int id, String message) {
        this.messages.put(id, message);
    }
    
    public String getMessage(int id) {
        return this.messages.get(id);
    }
    
    public ArrayList<ActivableSign> getSigns(){
        return this.signs;
    }

    public ArrayList<PlayerSpawnSign> getSpawns() {
        return spawns;
    }

    public ArrayList<BlockStockSign> getBlockLibrary() {
        return blockLibrary;
    }

    public ArrayList<ChestStockSign> getChestLibrary() {
        return chestLibrary;
    }
    
    public void setMaterial(Material material) {
        this.gem = material;
    }
    
    public Material getMaterial()
    {
        return this.gem;
    }
    
    public void gemCounter(Location location) {
        this.getWorld().playSound(location, SoundSign.sounds[1], 3, 1.3f);
        this.score.incrementGemCounter();
    }
    
    public GameScore getGameScore() {
        return this.score;
    }
    
    public void crystalCounter(Location location) {
        this.score.incrementCrystalCounter();
    }
    
    public ArrayList<NPCSign> getNPCs(){
        return this.NPCSigns;
    }
    
    public ArrayList<ActivableSign> getSpecialSigns(){
        return this.specialSigns;
    }
}