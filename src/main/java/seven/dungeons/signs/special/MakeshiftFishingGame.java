package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

import java.util.ArrayList;
import java.util.HashMap;

public class MakeshiftFishingGame extends CaptorSign {

    private int x1, xh;
    private int z1, zh;
    private int y1, yh;
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private int score;
    private int four;
    private ArrayList<Entity> fishes = new ArrayList<Entity>();
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private ArrayList<Location> locations = new ArrayList<Location>();
    private boolean started;
    private ArrayList<Player> noDropPlayers = new ArrayList<Player>();
    private FixedMetadataValue noDrop = new FixedMetadataValue(this.game.getPlugin(),"noDrop");

    public MakeshiftFishingGame(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            int x = Integer.parseInt(line3[1]);
            this.x1 = this.location.getBlockX() - x;
            this.xh = this.location.getBlockX() + x + 1 - this.x1;
            int z = Integer.parseInt(line3[0]);
            this.z1 = this.location.getBlockZ() - z;
            this.zh = this.location.getBlockZ() + z + 1 - this.z1;
            int y = Integer.parseInt(line3[2]);
            this.y1 = this.location.getBlockY() - y;
            this.yh = this.location.getBlockY() + y + 1 - this.y1;
            this.score = Integer.parseInt(line3[3]);
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
            this.started = false;
        }catch(Exception e) {
            SevenDungeons.log("MakeshiftFishingGame's third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
        this.started = false;
        this.cooldown = 10;
        this.four = 0;
        this.points.clear();
        World w = this.sign.getWorld();
        for(int x = this.x1; x < this.x1 + this.xh; x++) {
            for(int z = this.z1; z < this.z1 + this.zh; z++) {
                for(int y = this.y1; y < this.y1 + this.yh; y++) {
                    Location l = new Location(w, x, y, z);
                    Block b = l.getBlock();
                    if(b.getType().equals(Material.WATER)) {
                            this.locations.add(l);
                    }
                }
            }
        }
        ItemStack rod = new ItemStack(Material.WATER_BUCKET, 1);
        ItemMeta im = rod.getItemMeta();
        im.setUnbreakable(true);
        rod.setItemMeta(im);
        for(Player p : this.game.getPlayers()) {
            this.points.put(p,0);
            p.getInventory().addItem(rod.clone());
            p.setMetadata("noDrop", noDrop);
            this.noDropPlayers.add(p);
        }
    }
    
    @Override
    public void off() {
        this.locations.clear();
        this.game.getSigns5ticks().remove(this);
        for(Player p : this.game.getPlayers()) {
            p.getInventory().remove(Material.WATER_BUCKET);
        }
        for(Player p :this.noDropPlayers) {
            p.removeMetadata("noDrop", this.game.getPlugin());
        }
    }
    
    @Override
    public void isTriggered() {
        // Game is taking place
        if(this.started) {
            // Check if any fish has been caught.
            for(int i = this.fishes.size() - 1; i >= 0; i--) {
                Entity e = this.fishes.get(i);
                if(e.isDead()) {
                    /*Player p = Bukkit.getPlayer(e.getMetadata("xlp").get(0).asString());
                    if(p == null) {
                        SevenDungeons.log("Player not found for fishing.");
                        break;
                    }
                    this.points.put(p,this.points.get(p) + Integer.parseInt(e.getCustomName().substring(4)));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    e.remove();*/
                    this.fishes.remove(e);
                }
            }

            for(Player p : this.game.getPlayers()){
                if(p.getInventory().contains(Material.TROPICAL_FISH_BUCKET)){
                    this.points.put(p,this.points.get(p) + 3);
                    p.getInventory().remove(Material.TROPICAL_FISH_BUCKET);
                    p.getInventory().setItemInMainHand(new ItemStack(Material.WATER_BUCKET, 1));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                else if(p.getInventory().contains(Material.PUFFERFISH_BUCKET)){
                    if(p.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)){
                        p.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 1, false, false));
                    p.getInventory().remove(Material.PUFFERFISH_BUCKET);
                    p.getInventory().setItemInMainHand(new ItemStack(Material.WATER_BUCKET, 1));
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.5f);
                }
                else if(p.getInventory().contains(Material.COD_BUCKET)){
                    this.points.put(p,this.points.get(p) + 2);
                    p.getInventory().remove(Material.COD_BUCKET);
                    p.getInventory().setItemInMainHand(new ItemStack(Material.WATER_BUCKET, 1));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                else if(p.getInventory().contains(Material.SALMON_BUCKET)){
                    this.points.put(p,this.points.get(p) + 1);
                    p.getInventory().remove(Material.SALMON_BUCKET);
                    p.getInventory().setItemInMainHand(new ItemStack(Material.WATER_BUCKET, 1));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
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
                    
                    // Cleans up all fishes
                    for(Entity e : this.fishes) {
                        e.remove();
                    }
                    this.fishes.clear();
                    
                    // Calculates the final score
                    int total = 0;
                    for(Player p : this.game.getPlayers()) {
                        total += this.points.get(p);
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
                        message += "\n§r" + p.getName() + " made §e§l" + this.points.get(p) + "§r points.";
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
            
            //Spawn or despawn fishes
            if(this.fishes.size() < 50) {
                Entity e;
                Location l = this.locations.get((int)Math.floor(this.locations.size() * Math.random())).clone().add(0.5,0.5,0.5);
                double rand = Math.random();
                World w = this.location.getWorld();
                if(rand < 0.6){
                    e = w.spawnEntity(l, EntityType.SALMON);
                    e.setCustomName("§c§l1");
                }
                else if(rand < 0.9) {
                    e = w.spawnEntity(l, EntityType.COD);
                    e.setCustomName("§e§l2");
                }
                else if(rand < 0.95) {
                    e = w.spawnEntity(l, EntityType.TROPICAL_FISH);
                    e.setCustomName("§a§l3");
                }
                else {
                    e = w.spawnEntity(l, EntityType.PUFFERFISH);
                    e.setCustomName("§b§lBonus");
                }
                e.setCustomNameVisible(true);
                this.fishes.add(e);
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
                    for(Player p : this.game.getPlayers()){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, this.timer * 20 + 60, 0, false, false));
                    }
                }
                if(this.cooldown == 0) {
                    this.cooldown = this.timer;
                    this.started = true;
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 3, 1);
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
