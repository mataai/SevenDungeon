package seven.dungeons.signs.special;

import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class BalloonBurst extends CaptorSign {
    
    private int x1, x2;
    private int balloonY;
    private int z1, z2;
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private int nbPlayers;
    private boolean started;
    private int four;
    private int score;
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private HashMap<Player,Integer> colors = new HashMap<Player,Integer>();
    private ArrayList<Block> balloons = new ArrayList<Block>();
    private ArrayList<Location> airSpots = new ArrayList<Location>();

    public BalloonBurst(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            int x = Integer.parseInt(line3[1]);
            int z = Integer.parseInt(line3[0]);
            this.x1 = this.location.getBlockX() - x;
            this.x2 = this.location.getBlockX() + x;
            this.balloonY = this.location.getBlockY() + 6;
            this.z1 = this.location.getBlockZ() - z;
            this.z2 = this.location.getBlockZ() + z;
            this.score = Integer.parseInt(line3[2]);
        }catch(Exception e) {
            SevenDungeons.log("BalloonBurst's third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.started = false;
        this.cooldown = 10;
        this.four = 0;
        this.points.clear();
        this.airSpots.clear();
        this.nbPlayers = this.game.getPlayers().size();
        for(Player p : this.game.getPlayers()) {
            this.points.put(p,0);
        }
        World w = this.location.getWorld();
        for(int x = this.x1; x <= this.x2; x++) {
                for(int z = this.z1; z <= this.z2; z++) {
                    this.airSpots.add(new Location(w,x,this.balloonY,z));
                }
        }
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        if(this.started) {
            //Check every balloon if they are popped
            for(int i = 0; i < this.balloons.size(); i++) {
                Block b = this.balloons.get(i);
                if(b.hasMetadata("xbb")) {
                    Player p = Bukkit.getPlayer(b.getMetadata("xbb").get(0).asString());
                    if(p == null) {
                        SevenDungeons.log("Player not found for Ballon Burst.");
                        break;
                    }
                    if(b.getType().equals(this.getMaterialFromInteger(this.colors.get(p)))) {
                        this.points.put(p, this.points.get(p) + 1);
                        b.getWorld().playSound(b.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 2, 1.3f);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
                    }
                    else {
                        this.points.put(p, this.points.get(p) - 3);
                        b.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 1.5f);
                    }

                    b.removeMetadata("xbb", this.game.getPlugin());
                    b.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, b.getLocation().clone().add(0.5,0.5,0.5), 2);
                    this.removeBalloon(b);
                    this.airSpots.add(new Location(b.getWorld(),b.getX(),this.balloonY,b.getZ()));
                    this.balloons.remove(i);
                    i--;
                }
            }
            if(this.four > 2) {
                // Moves players on arrows and spawns balloons
                
                if(this.balloons.size() < this.nbPlayers*3 + 3) {
                    Location l = this.airSpots.get((int)Math.floor(Math.random() * this.airSpots.size()));
                    this.airSpots.remove(l);
                    l.add(0,(int)Math.floor(Math.random()*3),0);
                    l.getBlock().setType(this.getMaterialFromInteger((int)Math.floor(Math.random()*this.nbPlayers)));
                    this.balloons.add(l.getBlock());
                    l.add(0,-1,0).getBlock().setType(Material.CHAIN);
                    l.add(0,-1,0).getBlock().setType(Material.CHAIN);
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
                    
                    // Cleans up all balloons
                    for(Block b : this.balloons) {
                        this.removeBalloon(b);
                    }
                    this.balloons.clear();
                    
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
                if(this.cooldown == 5) {
                    ArrayList<Player> players = this.game.getPlayers();
                    for(int i = 0; i < players.size(); i++) {
                        this.colors.put(players.get(i), i);
                        players.get(i).sendMessage("§e§lBalloon Burst §r> Collect only " + this.getColorFromInteger(i) + " balloons ! (Left-click)");
                    }
                }
                if(this.cooldown > 0 && this.cooldown <= 3) {
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.7f);
                }
                if(this.cooldown == 0) {
                    this.cooldown = this.timer;
                    this.started = true;
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 3, 1);
                    PotionEffect potion = new PotionEffect(PotionEffectType.JUMP, (this.timer+1)*20, 2, true);
                    for(Player p : this.game.getPlayers()) {
                        p.addPotionEffect(potion);
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
    
    private String getColorFromInteger(int i) {
        switch(i) {
        case 0 : return "§c§lRED§r";
        case 1 : return "§e§lYELLOW§r";
        case 2 : return "§b§lBLUE§r";
        case 3 : return "§a§lGREEN§r";
        case 4 : return "§4§lPINK§r";
        case 5 : return "§5§lPURPLE§r";
        case 6 : return "§6§lORANGE§r";
        case 7 : return "§f§lWHITE§r";
            default : return "§c§lRED§r"; 
        }
    }
    
    private Material getMaterialFromInteger(int i) {
        switch(i) {
        case 0 : return Material.RED_WOOL;
        case 1 : return Material.YELLOW_WOOL;
        case 2 : return Material.LIGHT_BLUE_WOOL;
        case 3 : return Material.LIME_WOOL;
        case 4 : return Material.PINK_WOOL;
        case 5 : return Material.PURPLE_WOOL;
        case 6 : return Material.ORANGE_WOOL;
        case 7 : return Material.WHITE_WOOL;
            default : return Material.RED_WOOL;
        }
    }
    
    private void removeBalloon(Block b) {
        for(int j = 0; j < 3; j++) {
            b.getLocation().clone().add(0,-j,0).getBlock().setType(Material.AIR);
        }
    }

}
