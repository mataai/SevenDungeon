package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PigRunGame extends CaptorSign {

    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private int score;
    private int four;
    private String direction;
    private Location teleportBack;
    private ArrayList<Location> startingLocations = new ArrayList<Location>();
    private ArrayList<Player> alivePlayers = new ArrayList<Player>();
    //static private HashMap<String, Vector> directions = new HashMap<String, Vector>();
    private HashMap<Player, ArrayList<Location>> points = new HashMap<Player, ArrayList<Location>>();
    private HashMap<Player, Pig> pigs = new HashMap<Player, Pig>();
    private boolean started;
    private ArrayList<Player> noDropPlayers = new ArrayList<Player>();
    private FixedMetadataValue noDrop = new FixedMetadataValue(this.game.getPlugin(),"noDrop");

    public PigRunGame(Sign sign, Game game) {
        super(sign, game);
        /*if(PigRunGame.directions.isEmpty()){
            PigRunGame.directions.put("e", new Vector(1,0,0));
            PigRunGame.directions.put("s", new Vector(0,0,1));
            PigRunGame.directions.put("w", new Vector(-1,0,0));
            PigRunGame.directions.put("n", new Vector(0,0,-1));
        }*/
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.direction = line3[0];
            this.startingLocations.add(this.location.clone().add(0,0,-5).setDirection(new Vector(1,0,0)));
            this.startingLocations.add(this.location.clone().add(0,0,2).setDirection(new Vector(1,0,0)));
            this.startingLocations.add(this.location.clone().add(6,1,-6).setDirection(new Vector(1,0,0)));
            this.startingLocations.add(this.location.clone().add(5,0,1).setDirection(new Vector(1,0,0)));

            this.score = Integer.parseInt(line3[1]);
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
            this.started = false;
        }catch(Exception e) {
            SevenDungeons.log("PigRunGame's third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
        this.started = false;
        this.cooldown = 10;
        this.four = 0;
        this.points.clear();

        ItemStack rod = new ItemStack(Material.CARROT_ON_A_STICK, 1);
        ItemMeta im = rod.getItemMeta();
        im.setUnbreakable(true);
        rod.setItemMeta(im);
        for(Player p : this.game.getPlayers()) {
            this.points.put(p, new ArrayList<Location>());
            this.alivePlayers.add(p);
        }
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        for(Player p : this.game.getPlayers()) {
            p.getInventory().remove(Material.CARROT_ON_A_STICK);
        }
        for(Player p :this.noDropPlayers) {
            p.removeMetadata("noDrop", this.game.getPlugin());
        }
        this.noDropPlayers.clear();
        this.alivePlayers.clear();
    }
    
    @Override
    public void isTriggered() {
        // Game is taking place
        if(this.started) {

            // Search for losers and check for emerald patches
            for(int i = this.alivePlayers.size() - 1; i >= 0; i--){
                Player p = this.alivePlayers.get(i);
                Pig pig = this.pigs.get(p);
                if(pig.isDead()){
                    p.sendMessage("§d§lPig Run §r > You killed your pig ! This is animal abuse !");
                    p.teleport(this.teleportBack, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    this.alivePlayers.remove(i);
                    this.pigs.remove(p);
                   p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20, 0, false, false));
                }
                else if(p.getInventory().getItemInMainHand().getType() != Material.CARROT_ON_A_STICK){
                    p.sendMessage("§d§lPig Run §r > You stopped holding your carrot stick.");
                    this.alivePlayers.remove(i);
                    pig.remove();
                    this.pigs.remove(p);
                    p.teleport(this.teleportBack, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
                else if(pig.getPassengers().size() == 0){
                    p.sendMessage("§d§lPig Run §r > You unmounted your pig !");
                    this.alivePlayers.remove(i);
                    pig.remove();
                    this.pigs.remove(p);
                    p.teleport(this.teleportBack, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }

                if(this.pigs.size() == 0){
                    this.cooldown = 0;
                    this.four = 4;
                }

                // Check for emerald patches
                int xPig = pig.getLocation().getBlockX();
                int yPig = pig.getLocation().getBlockY();
                int zPig = pig.getLocation().getBlockZ();
                for(int x = xPig - 2; x < xPig + 2; x++){
                    for(int y = yPig - 2; y < yPig + 2; y++){
                        for(int z = zPig - 2; z < zPig + 2; z++){
                            Location l = new Location(this.location.getWorld(), x, y, z);
                            if(l.getBlock().getType() == Material.EMERALD_BLOCK && !this.points.get(p).contains(l)){
                                this.points.get(p).add(l);
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
                                if(this.points.get(p).size() == 10){
                                    p.sendMessage("§d§lPig Run §r > You got all emerald patches !");
                                    this.alivePlayers.remove(i);
                                    pig.remove();
                                    this.pigs.remove(p);
                                    p.teleport(this.teleportBack, PlayerTeleportEvent.TeleportCause.PLUGIN);

                                    if(this.pigs.size() == 0){
                                        this.cooldown = 0;
                                        this.four = 4;
                                    }
                                }
                                else if(pig.hasPotionEffect(PotionEffectType.SPEED)){
                                    int level = pig.getPotionEffect(PotionEffectType.SPEED).getAmplifier();
                                    pig.removePotionEffect(PotionEffectType.SPEED);
                                    pig.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120 * 20, level + 1, false, false));
                                }

                            }
                        }
                    }
                }
            }

            
            //Timer during the game
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
                }
                // The game has ended
                if(this.cooldown == 0) {
                    // Deactivates the game
                    this.off();
                    
                    // Clean up all pigs
                    for(Entity e : this.pigs.values()) {
                        if(!e.isDead()){
                            Entity player = e.getPassengers().get(0);
                            e.removePassenger(player);
                            player.teleport(this.teleportBack, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            e.remove();
                        }
                    }
                    this.pigs.clear();
                    
                    // Calculates the final score
                    int total = 0;
                    for(Player p : this.game.getPlayers()) {
                        total += this.points.get(p).size();
                    }
                    
                    // Activate the corresponding signal
                    String status;
                    if(total >= this.score) {
                        status = "§a§l--- You won ! ---";
                        this.game.activate(this.winSignal, null);
                        Firework fw = (Firework) this.game.getWorld().spawnEntity(location, EntityType.FIREWORK);
                        FireworkMeta fwm = fw.getFireworkMeta();
                        fwm.setPower(1);
                        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
                        fw.setFireworkMeta(fwm);
                        fw.detonate();
                        this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 3, 1);
                    }
                    else {
                        status = "§c§l--- You lost. ---";
                        this.game.activate(this.loseSignal, null);
                        this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_DEACTIVATE, 3, 1);
                    }
                    
                    // Shows results in chat for players
                    String message = status;
                    message += "\n§rScore needed :§c§l " + this.score;
                    message += "\n§rTotal score : §a§l " + total;
                    for(Player p : this.game.getPlayers()) {
                        message += "\n§r" + p.getName() + " made §e§l" + this.points.get(p).size() + "§r points.";
                    }
                    for(Player p : this.game.getPlayers()) {
                        p.sendMessage(message);
                    }
                    return;
                }
                this.cooldown--;
            }
            else {
                this.four++;
            }
            
        }
        // Countdown until the game begins.
        else {
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lStarting in " + this.cooldown + "...", p);
                }
                if(this.cooldown > 0 && this.cooldown <= 3) {
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.7f);
                }
                if(this.cooldown == 3){
                    this.teleportBack = this.game.getPlayers().get(0).getLocation();
                    for(int i = 0; i < this.game.getPlayers().size(); i++){
                        Pig pig = (Pig)this.location.getWorld().spawnEntity(this.startingLocations.get(i), EntityType.PIG);
                        //pig.setAI(false);
                        pig.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 100, false, false));
                        pig.addPassenger(this.game.getPlayers().get(i));
                        pig.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
                        pig.setHealth(2);
                        pig.setSaddle(true);
                        this.pigs.put(this.game.getPlayers().get(i), pig);
                    }
                }
                if(this.cooldown == 2){
                    ItemStack rod = new ItemStack(Material.CARROT_ON_A_STICK, 1);
                    ItemMeta im = rod.getItemMeta();
                    im.setUnbreakable(true);
                    rod.setItemMeta(im);
                    for(Player p : this.game.getPlayers()) {
                        p.sendMessage("§d§lPig Run §r > Always keep your carrot stick in your main hand !");
                        ItemStack currentItem = p.getInventory().getItemInMainHand();
                        if(currentItem == null){
                            p.getInventory().setItemInMainHand(rod.clone());
                        }
                        else{
                            p.getInventory().setItemInMainHand(rod.clone());
                            p.getInventory().addItem(currentItem);
                        }
                        p.setMetadata("noDrop", noDrop);
                        this.noDropPlayers.add(p);
                    }
                }
                if(this.cooldown == 0) {
                    this.cooldown = this.timer;
                    this.started = true;
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 3, 1);
                    for(Pig pig : this.pigs.values()){
                        //pig.setAI(true);
                        pig.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120 * 20, 9, false, false));
                    }
                    return;
                }
                this.cooldown--;
            }
            else {
                this.four++;
            }
        }
    }

}
