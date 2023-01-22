package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

import java.util.ArrayList;
import java.util.HashMap;

public class GravityBalloonBurst extends CaptorSign {

    private int radius;
    private int y1;
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private int nbPlayers;
    private boolean started;
    private int four;
    private int score;
    private boolean slimeReady;
    private Location tpBack;
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private HashMap<Player,Integer> colors = new HashMap<Player,Integer>();
    private ArrayList<Block> balloons = new ArrayList<Block>();

    public GravityBalloonBurst(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.radius = Integer.parseInt(line3[0]);
            this.y1 = Integer.parseInt(line3[1]);
            this.score = Integer.parseInt(line3[2]);
        }catch(Exception e) {
            SevenDungeons.log("GravityBalloonBurst's third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.started = false;
        this.cooldown = 10;
        this.slimeReady = true;
        this.four = 0;
        this.points.clear();
        this.tpBack = this.game.getPlayers().get(0).getLocation();
        this.nbPlayers = this.game.getPlayers().size();
        for(Player p : this.game.getPlayers()) {
            this.points.put(p,0);
        }
        World w = this.location.getWorld();
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
                        SevenDungeons.log("Player not found for Gravitational Balloons.");
                        break;
                    }
                    if(b.getType().equals(Material.TNT)){
                        for(int j = this.balloons.size() - 1; j >= 0; j--){
                            Block balloon = this.balloons.get(j);
                            if(balloon.getType().equals(this.getMaterialFromInteger(this.colors.get(p))) && balloon.getLocation().distance(b.getLocation()) < 7){
                                this.points.put(p, this.points.get(p) + 1);
                                this.removeBalloon(balloon);
                            }
                        }
                        b.getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3f);
                    }
                    else if(b.getType().equals(Material.SLIME_BLOCK)){
                        this.slimeReady = false;
                        Bukkit.getScheduler().runTaskLater(this.game.getPlugin(), () -> this.slimeReady = true, 180);
                        this.game.activate(11001, null);
                        for(int j = this.balloons.size() - 1; j >= 0; j--){
                            Block balloon = this.balloons.get(j);
                            if(balloon.getType().equals(Material.SLIME_BLOCK)){
                                this.removeBalloon(balloon);
                            }
                        }
                        for(Player player : this.game.getPlayers()){
                            player.sendMessage("§e§lGravitational Balloons §r> Fans deactivated for 8 seconds.");
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.75f);
                        }
                        b.getWorld().playSound(b.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 2, 1.3f);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
                    }
                    else if(b.getType().equals(this.getMaterialFromInteger(this.colors.get(p)))) {
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
                    this.balloons.remove(i);
                    i--;
                }
            }
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
                }

                // PLACE TNT OR SLIME
                if(this.cooldown % 5 == 0 && this.cooldown > 10) {
                    if(this.cooldown % 10 == 0){
                        // SPAWN TNT
                        this.placeOneBalloon(Material.TNT, this.getEmptyLocation());
                    }
                    else if(this.slimeReady){
                        // SPAWN SLIME
                        this.placeOneBalloon(Material.SLIME_BLOCK, this.getEmptyLocation());
                    }
                }

                // The game has ended
                if(this.cooldown == 0) {
                    // Deactivates the game
                    this.off();
                    
                    // Cleans up all balloons
                    this.removeAllBalloons();
                    
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
                        p.teleport(this.tpBack, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        if(p.hasPotionEffect(PotionEffectType.SLOW_FALLING)){
                            p.removePotionEffect(PotionEffectType.SLOW_FALLING);
                        }
                        if(p.hasPotionEffect(PotionEffectType.LEVITATION)){
                            p.removePotionEffect(PotionEffectType.LEVITATION);
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
        else {
            if(this.four == 4) {
                this.four = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lStarting in " + this.cooldown + "...", p);
                }
                if(this.cooldown == 5) {
                    ArrayList<Player> players = this.game.getPlayers();
                    this.tpBack = players.get(0).getLocation();
                    double angle = 0;
                    for(int i = 0; i < players.size(); i++) {
                        this.colors.put(players.get(i), i);
                        players.get(i).sendMessage("§e§lGravitational Balloons §r> Collect only " + this.getColorFromInteger(i) + " balloons ! (Left-click)");
                        players.get(i).teleport(this.sign.getLocation().add(new Vector(Math.cos(angle) * 2, 0, Math.sin(angle) * 2)), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        angle += Math.PI / 2;
                    }
                }
                if(this.cooldown > 0 && this.cooldown <= 3) {
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 2, 0.7f);
                }
                if(this.cooldown == 0) {
                    this.cooldown = this.timer;
                    this.started = true;
                    this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 3, 1);
                    PotionEffect potion = new PotionEffect(PotionEffectType.SLOW_FALLING, (this.timer+10)*20, 3, false, false);
                    for(Player p : this.game.getPlayers()) {
                        p.addPotionEffect(potion);
                    }
                    this.placeManyBalloons();
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
            Location l = b.getLocation().clone().add(0, -j, 0);
            l.getBlock().setType(Material.AIR);
        }
    }

    private void removeAllBalloons(){
        for(int i = this.balloons.size() - 1; i >= 0; i--){
            this.removeBalloon(this.balloons.get(i));
            this.balloons.remove(i);
        }
    }

    private void placeOneBalloon(Material m, Location l){
        l.getBlock().setType(m);
        this.balloons.add(l.getBlock());
        l.add(0,-1,0).getBlock().setType(Material.CHAIN);
        l.add(0,-1,0).getBlock().setType(Material.CHAIN);
    }

    private void placeManyBalloons(){
        ArrayList<Player> players = this.game.getPlayers();
        for(int i = 0; i < players.size(); i++){
            Material m = this.getMaterialFromInteger(i);
            for(int j = 0; j < (int) (this.radius * this.radius * Math.PI * (this.y1 - 5) / 200); j++){
                this.placeOneBalloon(m, this.getEmptyLocation());
            }
        }
    }

    private Location getEmptyLocation(){
        Location l = null;
        while(l == null || !isColumnEmpty(l)){
            double theta = Math.random() * Math.PI * 2;
            double r = Math.random() * (this.radius - 1);
            int y = (int) Math.floor(Math.random() * (this.y1 - 5) + 4);
            l = this.sign.getLocation().clone().add((Math.cos(theta)) * r, y, Math.sin(theta) * r);
        }
        return l;
    }

    private boolean isColumnEmpty(Location l){
        if(!l.getBlock().getType().equals(Material.AIR) || !l.clone().add(0,-1,0).getBlock().getType().equals(Material.AIR) || !l.clone().add(0,-2,0).getBlock().getType().equals(Material.AIR) || !l.clone().add(0,-3,0).getBlock().getType().equals(Material.AIR))
            return false;
        return true;
    }


}
