package seven.dungeons.signs.special;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class FishingGame extends CaptorSign {
    
    private int x1, xh;
    private int z1, zh;
    private float y;
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private int score;
    private int nbFish;
    private int four;
    private ArrayList<Entity> fishes = new ArrayList<Entity>();
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private ArrayList<Location> locations = new ArrayList<Location>();
    private boolean started;
    private ArrayList<Player> noDropPlayers = new ArrayList<Player>();
    private FixedMetadataValue noDrop = new FixedMetadataValue(this.game.getPlugin(),"noDrop");

    public FishingGame(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            int x = Integer.parseInt(line3[1]);
            this.x1 = this.location.getBlockX() - x;
            this.xh = this.location.getBlockX() + x + 1 - this.x1;
            int z = Integer.parseInt(line3[0]);
            this.z1 = this.location.getBlockZ() - z;
            this.zh = this.location.getBlockZ() + z + 1 - this.z1;
            this.y = (float) (this.location.getBlockY() - 0.5);
            this.score = Integer.parseInt(line3[2]);
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
            this.nbFish = (int)((this.xh + this.zh) / 1.5);
            this.started = false;
        }catch(Exception e) {
            SevenDungeons.log("FishingGame's third and/or fourth line is wrong.");
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
        for(int x = this.x1; x < this.x1 + 1 + this.xh; x++) {
            for(int z = this.z1; z < this.z1 + this.zh + 1; z++) {
                for(int tempY = (int) (this.y - 2); tempY < this.y + 3; tempY++) {
                    Location l = new Location(w, x, tempY, z);
                    Block b = l.getBlock();
                    if(b.getBlockData() instanceof Slab && b.getBlockData() instanceof Waterlogged && ((Waterlogged)b.getBlockData()).isWaterlogged()) {
                            this.locations.add(l);
                    }
                }
            }
        }
        ItemStack rod = new ItemStack(Material.FISHING_ROD, 1);
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
            p.getInventory().remove(Material.FISHING_ROD);
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
            for(int i = 0; i < this.fishes.size(); i++) {
                Entity e = this.fishes.get(i);
                if(e.hasMetadata("xlp")) {
                    Player p = Bukkit.getPlayer(e.getMetadata("xlp").get(0).asString());
                    if(p == null) {
                        SevenDungeons.log("Player not found for fishing.");
                        break;
                    }
                    this.points.put(p,this.points.get(p) + Integer.parseInt(e.getCustomName().substring(4)));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    e.remove();
                    this.fishes.remove(e);
                    continue;
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
            if(this.fishes.size() < this.nbFish) {
                Entity e;
                Location l = this.locations.get((int)Math.floor(this.locations.size() * Math.random())).clone().add(0.5,0.5,0.5);
                int rand = (int)Math.floor(Math.random() * 7);
                World w = this.location.getWorld();
                switch(rand) {
                case 0 : case 1 : case 2 :
                    e = w.spawnEntity(l, EntityType.SALMON);
                    e.setCustomName("§c§l1");
                    break;
                case 3 : case 4 :
                    e = w.spawnEntity(l, EntityType.COD);
                    e.setCustomName("§e§l2");
                    break;
                case 5 : 
                    e = w.spawnEntity(l, EntityType.TROPICAL_FISH);
                    e.setCustomName("§a§l3");
                    break;
                default : 
                    e = w.spawnEntity(l, EntityType.PUFFERFISH);
                    e.setCustomName("§4§l-3");
                    PufferFish pf = (PufferFish)e;
                    pf.setPuffState(2);
                }
                e.setCustomNameVisible(true);
                this.fishes.add(e);
            }
            //Remove a fish if there are too much
            else {
                Entity e = this.fishes.get(0);
                e.remove();
                this.fishes.remove(e);
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
