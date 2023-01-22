package seven.dungeons.signs.special;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class Stardust extends CaptorSign {
    
    private int winSignal;
    private int loseSignal;
    private int score;
    private int points;
    private int x1, x2;
    private int y1, y2;
    private int z1, z2;
    private boolean started;
    private int timer;
    private int four;
    private int cooldown;
    private ArrayList<Location> ground = new ArrayList<Location>();
    private ArrayList<Location> stardusts = new ArrayList<Location>();
    private ArrayList<Integer> stardustColors = new ArrayList<Integer>();
    private ArrayList<Float> heights = new ArrayList<Float>();
    private ArrayList<Block> cauldrons = new ArrayList<Block>();
    private ArrayList<Integer> cauldronColors = new ArrayList<Integer>();
    private HashMap<Player,Integer> playerColors = new HashMap<Player,Integer>();
    public static Vector[] blocksAround = {new Vector(-1,0,0), new Vector(1,0,0), new Vector(0,0,-1), new Vector(0,0,1)};

    public Stardust(Sign sign, Game game) {
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
            SevenDungeons.log("Stardust's third and/or fourth line is wrong.");
        }
    }

    @Override
    public void on() {
        World w = this.location.getWorld();
        for(int x = this.x1; x <= this.x2; x++) {
            for(int y = this.y1; y <= this.y2; y++) {
                for(int z = this.z1; z <= this.z2; z++) {
                    Location l = new Location(w,x,y,z);
                    Material m = l.getBlock().getType();
                    if(m.toString().equalsIgnoreCase("BLUE_TERRACOTTA")) {
                        this.ground.add(l);
                    }
                    if(m.toString().equalsIgnoreCase("CAULDRON")) {
                        int color = this.getColor(l);
                        this.cauldrons.add(l.getBlock());
                        this.cauldronColors.add(color);
                    }
                }
            }
        }
        for(Player p : this.game.getPlayers()) {
            p.getInventory().addItem(new ItemStack(Material.BUCKET));
        }
        this.four = 0;
        this.points = 0;
        this.cooldown = 10;
        this.started = false;
        this.game.getSigns5ticks().add(this);
        
    }
    
    private int getColor(Location l) {
        for(int i = 0; i < Stardust.blocksAround.length; i++) {
            String color = l.clone().add(Stardust.blocksAround[i]).getBlock().getType().toString();
            if(color.contains("LIGHT_BLUE")) {
                return 1;
            }
            if(color.contains("RED")) {
                return 2;
            }
            if(color.contains("YELLOW")) {
                return 3;
            }
            if(color.contains("LIME")) {
                return 4;
            }
        }
        return 1;
    }

    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        for(Block b : this.cauldrons) {
            Levelled cauldronData = (Levelled) b.getBlockData();
            cauldronData.setLevel(0);
            b.setBlockData(cauldronData);
        }
        this.cauldrons.clear();
        this.ground.clear();
        this.heights.clear();
        this.stardusts.clear();
        this.stardustColors.clear();
        this.playerColors.clear();
        for(Player p : this.game.getPlayers()) {
            p.getInventory().remove(Material.BUCKET);
            p.getInventory().remove(Material.LAVA_BUCKET);
        }
    }
    
    @Override
    public void isTriggered() {
        if(this.started) {
            //Check every cauldron for filling
            for(int i = 0; i < this.cauldrons.size(); i++) {
                Block b = this.cauldrons.get(i);
                if(b.hasMetadata("xsd")) {
                    Player p = Bukkit.getPlayer(b.getMetadata("xsd").get(0).asString());
                    if(p == null) {
                        SevenDungeons.log("Player not found for stardust.");
                        break;
                    }
                    p.getInventory().remove(Material.LAVA_BUCKET);
                    p.getInventory().addItem(new ItemStack(Material.BUCKET));
                    Levelled cauldronData = (Levelled) b.getBlockData();
                    if(this.cauldronColors.get(i) == this.playerColors.get(p)) {
                        p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA, 1, 1.5f);
                        cauldronData.setLevel(cauldronData.getLevel() + 1);
                        
                        if(cauldronData.getLevel() >= 3) {
                            Firework fw = (Firework) this.game.getWorld().spawnEntity(b.getLocation().clone().add(0,1,0), EntityType.FIREWORK);
                            FireworkMeta fwm = fw.getFireworkMeta();
                            fwm.setPower(2);
                            Color color = this.getFireworkColor(this.cauldronColors.get(i));
                            fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(color, Color.WHITE).flicker(true).trail(true).build());
                            fw.setFireworkMeta(fwm);
                            cauldronData.setLevel(0);
                            this.points++;
                        }
                        b.setBlockData(cauldronData);
                    }
                    else {
                        cauldronData.setLevel(0);
                        b.setBlockData(cauldronData);
                        this.sign.getWorld().playSound(this.sign.getLocation(), Sound.ENTITY_ITEM_BREAK, 2, 0.5f);
                    }
                    b.removeMetadata("xsd", this.game.getPlugin());
                }
            }
            //Check for every stardust trail if a player is catching it.
            for(int i = 0; i < this.stardusts.size(); i++) {
                Location l = this.stardusts.get(i).clone().add(0,this.heights.get(i),0);
                for(Player p : this.game.getPlayers()) {
                    Block b = p.getLocation().getBlock(), b2 = p.getLocation().clone().add(0,1,0).getBlock();
                    if(b.equals(l.getBlock()) || b2.equals(l.getBlock())) {
                        if(p.getInventory().getItemInMainHand().getType().equals(Material.BUCKET)) {
                            this.playerColors.put(p,this.stardustColors.get(i));
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                            p.getInventory().remove(Material.BUCKET);
                            p.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
                            this.ground.add(l);
                            this.stardusts.remove(i);
                            this.stardustColors.remove(i);
                            this.heights.remove(i);
                            i--;
                            break;
                        }
                        if(p.getInventory().getItemInMainHand().getType().equals(Material.LAVA_BUCKET)) {
                            p.sendMessage("§e§lStardust §r> Your bucket is already full ! Empty it in the right cauldron.");
                        }
                        
                    }
                }
            }
            
            //Animate all stardust trails
            for(int i = 0; i < this.stardusts.size(); i++) {
                if(this.heights.get(i) < 0.5) {
                    this.ground.add(this.stardusts.get(i));
                    this.heights.remove(i);
                    this.stardusts.remove(i);
                    this.stardustColors.remove(i);
                    i--;
                    continue;
                }
                else {
                    Location l = this.stardusts.get(i);
                    DustOptions du = new DustOptions(this.getFireworkColor(this.stardustColors.get(i)), 1);
                    float height = this.heights.get(i);
                    l.getWorld().spawnParticle(Particle.REDSTONE, l.clone().add(0.5,height,0.5), (int)Math.ceil(6 - height), 0.15, 0.15, 0.15, 1, du);
                    this.heights.set(i, (float) (height - 0.25));
                }
            }
            
          //Timer during the game
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
                }

                //Generate new stardust trails
                if(this.stardusts.size() < (int)(this.x2 - this.x1 / 2)) {
                    Location l = this.ground.get((int)Math.floor(Math.random()*this.ground.size()));
                    this.ground.remove(l);
                    this.stardusts.add(l);
                    this.stardustColors.add((int)Math.ceil(Math.random()*4));
                    this.heights.add(6f);
                }
                
                // The game has ended
                if(this.cooldown == 0) {
                    // Deactivates the game
                    this.off();
                    
                    // Activate the corresponding signal
                    String status;
                    if(this.points >= this.score) {
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
                    message += "\n§rTotal score : §a§l " + this.points;
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
        // Not started
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

    private Color getFireworkColor(Integer integer) {
        switch(integer) {
        case 1 : return Color.AQUA;
        case 2 : return Color.RED;
        case 3 : return Color.YELLOW;
        case 4 : return Color.LIME;
        }
        return Color.WHITE;
    }
}
