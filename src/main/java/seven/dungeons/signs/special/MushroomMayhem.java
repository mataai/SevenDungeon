package seven.dungeons.signs.special;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class MushroomMayhem extends CaptorSign {
    
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int score;
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private ArrayList<Location> ground = new ArrayList<Location>();
    private ArrayList<Integer> lifespan = new ArrayList<Integer>();
    private ArrayList<Block> mushrooms = new ArrayList<Block>();
    private int four;
    private int x1, x2;
    private int y1, y2;
    private int z1, z2;
    private boolean started;
    private int timer;

    public MushroomMayhem(Sign sign, Game game) {
        super(sign, game);
            try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            int x = this.location.getBlockX();
            int y = this.location.getBlockY();
            int z = this.location.getBlockZ();
            this.x1 = x - Integer.parseInt(line3[1]);
            this.y1 = y - Integer.parseInt(line3[2]);
            this.z1 = z - Integer.parseInt(line3[0]);
            this.x2 = x + Integer.parseInt(line3[1]);
            this.z2 = z + Integer.parseInt(line3[0]);
            this.y2 = y + Integer.parseInt(line3[2]);
            this.score = Integer.parseInt(line3[3]);
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
        }catch(Exception e) {
            SevenDungeons.log("MinecartTour's third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
        this.started = false;
        this.cooldown = 10;
        this.four = 0;
        this.points.clear();
        for(Player p : this.game.getPlayers()) {
            this.points.put(p,0);
        }
        this.ground.clear();
        World w = this.location.getWorld();
        for(int i = this.x1; i <= this.x2; i++) {
            for(int j = this.y1; j <= this.y2; j++) {
                for(int k = this.z1; k <= this.z2; k++) {
                    Location l = new Location(w,i,j,k);
                    if(l.getBlock().getType() == Material.MYCELIUM) {
                        this.ground.add(l);
                    }
                }
            }
        }
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        this.lifespan.clear();
        for(Block b : this.mushrooms) {
            b.setType(Material.AIR);
            this.ground.add(b.getLocation());
        }
        this.mushrooms.clear();
    }
    
    @Override
    public void isTriggered() {
        if(this.started) {
            for(int i = 0; i < this.mushrooms.size(); i++) {
                Block b = this.mushrooms.get(i);
                if(b.hasMetadata("xmm")) {
                    Player p = Bukkit.getPlayer(b.getMetadata("xmm").get(0).asString());
                    b.removeMetadata("xmm", this.game.getPlugin());
                    if(p == null) {
                        SevenDungeons.log("Player not found for xmm.");
                        break;
                    }
                    b.setType(Material.AIR);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    int life = this.lifespan.get(i);
                    this.ground.add(b.getLocation().clone().add(0,-1,0));
                    this.mushrooms.remove(i);
                    this.lifespan.remove(i);
                    if(life >= 24) {
                        this.points.put(p,this.points.get(p) + 5);
                    }
                    else if(life >= 20) {
                        this.points.put(p,this.points.get(p) + 3);
                    }
                    else if(life >= 12) {
                        this.points.put(p,this.points.get(p) + 2);
                    }
                    else {
                        this.points.put(p,this.points.get(p) + 1);
                    }
                }
                else {
                    int life = this.lifespan.get(i) + 1;
                    this.lifespan.set(i, life);
                    if(life >= 24) {
                        DustOptions du = new DustOptions(Color.WHITE, 1);
                        b.getLocation().getWorld().spawnParticle(Particle.REDSTONE, b.getLocation().clone().add(0.5,0.5,0.5), 3, 0.2, 0.2, 0.2, 1, du);
                    }
                    else if(life >= 12 && life <= 20) {
                        DustOptions du = new DustOptions(Color.WHITE, 1);
                        b.getLocation().getWorld().spawnParticle(Particle.REDSTONE, b.getLocation().clone().add(0.5,0.5,0.5), 3, 0.2, 0.2, 0.2, 1, du);
                    }
                    switch(life) {
                    case 20: b.setType(Material.CRIMSON_FUNGUS); break;
                    case 26: this.lifespan.remove(i); 
                    b.setType(Material.AIR);
                    b.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, b.getLocation().clone().add(0.5,0,0.5), 3);
                    b.getLocation().getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3f);
                    this.ground.add(b.getLocation().clone().add(0,-1,0));
                    for(Player p : this.game.getPlayers()) {
                        if(p.getLocation().distance(b.getLocation().clone().add(0.5,0,0.5)) < 3) {
                            this.poison(p, b.getLocation());
                        }
                    }
                    this.mushrooms.remove(i); break;
                    }
                }
            }
            
            //Timer during the game
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
                }
                
                //Spawn mushrooms
                int random = (int)Math.floor(this.ground.size() * Math.random());
                Location l = this.ground.get(random).clone().add(0,1,0);
                this.ground.remove(random);
                l.getBlock().setType(Material.RED_MUSHROOM);
                this.mushrooms.add(l.getBlock());
                this.lifespan.add(0);
                // The game has ended
                if(this.cooldown == 0) {
                    // Deactivates the game
                    this.off();
                    
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
        }
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
    
    public void poison(Player p, Location l) {
        Vector v = p.getLocation().toVector().subtract(l.toVector());
        v.normalize().multiply(1.6);
        p.setVelocity(p.getVelocity().add(v.add(new Vector(0,0,0))));
        PotionEffect pe = new PotionEffect(PotionEffectType.CONFUSION, 160, 0, false, false);
        if(p.hasPotionEffect(PotionEffectType.CONFUSION)) {
            p.removePotionEffect(PotionEffectType.CONFUSION);
        }
        p.addPotionEffect(pe);
    }

}
