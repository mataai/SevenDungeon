package seven.dungeons.signs.special;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class LavaTower extends CaptorSign{
    
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int score;
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private ArrayList<Location> platforms = new ArrayList<Location>();
    private ArrayList<Material> materials = new ArrayList<Material>();
    private ArrayList<Player> alive = new ArrayList<Player>();
    private int four;
    private int x1, x2;
    private int y1, y2;
    private int z1, z2;
    private int currentY;
    private Location exit;
    private Location spawn;
    private boolean started;
    private float currentTimer;
    private float timer;
    private float tonality;

    public LavaTower(Sign sign, Game game) {
        super(sign, game);
        try {
            
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.x1 = this.location.getBlockX();
            this.y1 = this.location.getBlockY();
            this.z1 = this.location.getBlockZ();
            this.x2 = this.x1 + Integer.parseInt(line3[1]);
            this.z2 = this.z1 + Integer.parseInt(line3[0]);
            this.y2 = this.y1 + Integer.parseInt(line3[2]);
            this.score = Integer.parseInt(line3[3]);
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
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
        this.currentY = this.y1 + 1;
        this.exit = this.game.getPlayers().get(0).getLocation();
        this.platforms.clear();
        this.materials.clear();
        this.spawn = this.location;
        this.timer = 0;
        this.tonality = 0.75f;
        this.currentTimer = 3;
        World w = this.location.getWorld();
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                for(int z = z1; z <= z2; z++) {
                    Location l = new Location(w,x,y,z);
                    if(l.getBlock().getType().toString().contains("CONCRETE")) {
                        this.materials.add(l.getBlock().getType());
                        this.platforms.add(l);
                        if(y != y1) {
                            l.getBlock().setType(Material.AIR);
                        }
                    }
                    else if(l.getBlock().getType() == Material.EMERALD_BLOCK) {
                        this.spawn = l.clone().add(0.5,1,0.5);
                        l.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        for(Player p : this.game.getPlayers()) {
            this.alive.add(p);
            this.points.put(p,0);
        }
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        World w = this.location.getWorld();
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                for(int z = z1; z <= z2; z++) {
                    Location l = new Location(w,x,y,z);
                    l.getBlock().setType(Material.AIR);
                }
            }
        }
        this.spawn.getBlock().setType(Material.EMERALD_BLOCK);
        for(int i = 0; i < this.platforms.size(); i++) {
            this.platforms.get(i).getBlock().setType(this.materials.get(i));
        }
    }
    
    @Override
    public void isTriggered() {
        if(this.started) {
            for(int i = 0; i < this.alive.size(); i++) {
                if(this.alive.get(i).getLocation().getBlockY() <= this.currentY - 3 || this.currentY == this.y2) {
                    Player p = this.alive.get(i);
                    this.points.put(p, (this.currentY - 3) - this.y1);
                    p.teleport(this.exit, TeleportCause.PLUGIN);
                    this.safeHeal(p);
                    p.setCollidable(true);
                    this.makeVisible(p);
                    this.alive.remove(i);
                }
            }
            if(this.currentY == this.y2 || this.alive.isEmpty()) {
                // Calculates the final score
                int total = 0;
                for(Player p : this.game.getPlayers()) {
                    if(p == null || this.alive.contains(p)) {
                        this.points.put(p, (this.currentY - 3) - this.y1);
                    }
                    total += this.points.get(p);
                }
                for(Player p : this.alive) {
                    p.teleport(this.exit, TeleportCause.PLUGIN);
                    p.setCollidable(true);
                    this.makeVisible(p);
                }
                // Activate the corresponding signal
                String status;
                if(total >= this.score) {
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
                message += "\n§rScore needed :§c§l " + this.score;
                message += "\n§rTotal score : §a§l " + total;
                for(Player p : this.game.getPlayers()) {
                    message += "\n§r" + p.getName() + " made §e§l" + this.points.get(p) + "§r points.";
                }
                for(Player p : this.game.getPlayers()) {
                    p.sendMessage(message);
                }

                // Deactives the game
                this.off();
                return;
            }
            if(this.timer >= this.currentTimer) {
                this.timer = 0;
                if(this.currentTimer >= 1.5) {
                    this.currentTimer = this.currentTimer - 0.125f;
                }
                else {
                    this.currentTimer = (float)Math.max(0.5, this.currentTimer - 0.03125);
                }
                this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 5, this.tonality);
                this.tonality = (float)Math.min(this.tonality + 0.02, 1.5);
                for(int i = 0; i < this.platforms.size(); i++) {
                    if(this.platforms.get(i).getBlockY() == this.currentY) {
                        this.platforms.get(i).getBlock().setType(this.materials.get(i));
                    }
                }
                World w = this.location.getWorld();
                for(int x = x1; x <= this.x2; x++) {
                    for(int z = z1; z <= this.z2; z++) {
                        Location l = new Location(w,x,this.currentY-3,z);
                        l.getBlock().setType(Material.LAVA);
                    }
                }
                this.currentY++;
            }
            else {
                this.timer += 0.25;
            }
        }
        else {
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lStarting in " + this.cooldown + "...", p);
                }
                // Teleports players in the lava tower
                if(this.cooldown == 3) {
                    
                    for(Player p : this.game.getPlayers()) {
                        p.setCollidable(false);
                        p.teleport(this.spawn, TeleportCause.PLUGIN);
                        this.makeInvisible(p);
                    }
                }
                if(this.cooldown > 0 && this.cooldown <= 3) {
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.7f);
                }
                if(this.cooldown == 0) {
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
    
    public void safeHeal(Player p) {
        new BukkitRunnable() {
        
            @Override
            public void run() {
                game.healPlayer(p);
            }
            
        }.runTaskLater(this.game.getPlugin(), 1);
    }
    
    public void makeVisible(Player player) {
        for(Player p : this.game.getPlayers()) {
            if(player != p) {
                player.showPlayer(this.game.getPlugin(), p);
            }
        }
    }
    
    public void makeInvisible(Player player) {
        for(Player p : this.game.getPlayers()) {
            if(player != p) {
                player.hidePlayer(this.game.getPlugin(), p);
            }
        }
    }
    
}
