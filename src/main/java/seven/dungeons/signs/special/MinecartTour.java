package seven.dungeons.signs.special;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class MinecartTour extends CaptorSign {
    
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private int score;
    private int points;
    private int four;
    private int firstId, lastId;
    private int direction;
    private Location exit;
    private boolean started;
    private FixedMetadataValue fmd = new FixedMetadataValue(this.game.getPlugin(),"xmt");
    private FixedMetadataValue noDrop = new FixedMetadataValue(this.game.getPlugin(),"noDrop");
    private ArrayList<Vehicle> minecarts = new ArrayList<Vehicle>();
    private ArrayList<Player> noDropPlayers = new ArrayList<Player>();

    public MinecartTour(Sign sign, Game game) {
        super(sign, game);
        try {
            switch(this.sign.getLine(1).trim().split(" ")[1]) {
            case "n" : this.direction = 0; break;
            case "s" : this.direction = 1; break;
            case "w" : this.direction = 2; break;
            default : this.direction = 3;
            }
            
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.firstId = Integer.parseInt(line3[0]);
            this.lastId = Integer.parseInt(line3[1]);
            this.score = Integer.parseInt(line3[2]);
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
        this.game.getSpecialSigns().add(this);
        this.started = false;
        this.cooldown = 10;
        this.four = 0;
        this.points = 0;
        //this.exit = this.game.getPlayers().get(0).getLocation();
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta im = bow.getItemMeta();
        im.setUnbreakable(true);
        bow.setItemMeta(im);
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        for(Player p : this.game.getPlayers()) {
            p.getInventory().addItem(bow.clone());
            p.getInventory().addItem(arrow.clone());
            p.setMetadata("noDrop", noDrop);
        	p.setMetadata("xmt", this.fmd);
            this.noDropPlayers.add(p);
        }
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        this.game.getSpecialSigns().remove(this);
        for(Entity e : this.location.getWorld().getEntities()) {
            if(e.getType() == EntityType.ARROW){
                e.remove();
            }
        }
        for(Player p : this.game.getPlayers()) {
            p.getInventory().remove(Material.BOW);
            p.getInventory().remove(Material.ARROW);
        }
        for(Player p :this.noDropPlayers) {
            p.removeMetadata("noDrop", this.game.getPlugin());
        	p.removeMetadata("xmt", this.game.getPlugin());
        }
    }
    
    @Override
    public void isTriggered() {
        if(this.started) {
            for(Player p : this.game.getPlayers()) {
                if(p.isInsideVehicle()) {
                    Vehicle v = (Vehicle)p.getVehicle();
                    Block b = v.getLocation().getBlock();
                    if(b.getType().equals(Material.RAIL)) {
                        v.setVelocity(v.getVelocity().multiply(2));
                    }
                    else if(b.getType().equals(Material.DETECTOR_RAIL)) {
                    	this.location.getWorld().playSound(v.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1.2f);
                    	b.setType(Material.AIR);
                    	((Minecart)v).setMaxSpeed(0.5);
                    	Vector launch = v.getVelocity().clone().normalize().add(new Vector(0, 1.2, 0));
                    	v.setVelocity(launch);
                    	Bukkit.getScheduler().runTaskLater(this.game.getPlugin(), () -> b.setType(Material.DETECTOR_RAIL), 3);
                    	Bukkit.getScheduler().runTaskLater(this.game.getPlugin(), () -> ((Minecart)v).setMaxSpeed(0.13), 60);
                    }
                }
            }
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
                }
             // The game has ended
                if(this.cooldown == 0) {
                    // Deactivates the game
                    this.off();
                    
                    for(Vehicle v : this.minecarts) {
                        v.removeMetadata("xmt", this.game.getPlugin());
                        Entity e = null;
                        if(!v.getPassengers().isEmpty()) {
                            e = v.getPassengers().get(0);
                            v.removePassenger(e);
                        }
                        v.remove();
                    }
                    
                    for(Player p : this.game.getPlayers()) {
                            p.teleport(this.exit, TeleportCause.PLUGIN);
                    }
                    
                    // Activate the corresponding signal
                    String status;
                    if(this.points >= this.score) {
                        status = "§a§l--- You won ! ---";
                        this.game.activate(this.winSignal, null);
                        Firework fw = (Firework) this.game.getWorld().spawnEntity(this.exit, EntityType.FIREWORK);
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
                    message += "\n§rScore needed :§c§l " + this.score + "\n§rTotal score : §e§l" + this.points ;
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
                if(this.cooldown < 3) {
                    for(Vehicle v : this.minecarts) {
                        v.setVelocity(new Vector(0,0,0)); 
                    }
                }
                // Place players on minecarts
                if(this.cooldown == 4) {
                    this.exit = this.game.getPlayers().get(0).getLocation();
                }
                if(this.cooldown == 3) {
                    Location start = this.location.clone().add(0,-1,0);
                    Vector v = new Vector(0,0,0);
                    switch(this.direction) {
                    case 0 : v.add(new Vector(0,0,3)); break;
                    case 1 : v.add(new Vector(0,0,-3)); break;
                    case 2 : v.add(new Vector(3,0,0)); break;
                    default : v.add(new Vector(-3,0,0)); break;
                    }
                    for(Player p : this.game.getPlayers()) {
                        Vehicle minecart = (Vehicle)this.location.getWorld().spawnEntity(start, EntityType.MINECART);
                        this.minecarts.add(minecart);
                        minecart.setMetadata("xmt", this.fmd);
                        minecart.addPassenger(p);
                        start.add(v);
                        ((Minecart)minecart).setMaxSpeed(0.13);
                    }
                }
                if(this.cooldown > 0 && this.cooldown <= 3) {
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.7f);
                }
                if(this.cooldown == 0) {
                    // Start the game
                    this.cooldown = this.timer;
                    this.started = true;
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 3, 1);
                    
                    for(Vehicle v : this.minecarts) {
                        switch(this.direction) {
                        case 0 : v.setVelocity(new Vector(0,0,-2)); break;
                        case 1 : v.setVelocity(new Vector(0,0,2)); break;
                        case 2 : v.setVelocity(new Vector(-2,0,0)); break;
                        default : v.setVelocity(new Vector(2,0,0)); break;
                        }
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
    
    @Override
    public void signal(int id) {
        if(id >= this.firstId && id <= this.lastId) {
            this.points++;
        }
            
    }

}
