package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

import java.util.ArrayList;
import java.util.HashMap;

public class LaserGoRound extends CaptorSign {

    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private boolean started;
    private int ten;
    private int timer;
    private int radius;
    private int score;

    private Location back;
    private HashMap<Player,Integer> points = new HashMap<Player,Integer>();
    private ArrayList<Double> directions = new ArrayList<Double>();
    private ArrayList<Integer> graceTime = new ArrayList<Integer>();
    private HashMap<Player,Integer> playerGrace = new HashMap<Player,Integer>();
    private ArrayList<Item> ingots = new ArrayList<Item>();
    private static final Material[] materials = {Material.COPPER_INGOT, Material.COPPER_INGOT, Material.COPPER_INGOT, Material.IRON_INGOT, Material.IRON_INGOT, Material.GOLD_INGOT};
    private double rotation;
    private int count;

    public LaserGoRound(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.radius = Integer.parseInt(line3[0]);
            this.score = Integer.parseInt(line3[1]);
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
        }
        catch(Exception e){
            SevenDungeons.log("LaserGoRound's third and/or fourth line is wrong.");
        }
    }

    @Override
    public void on(){
        this.game.getSigns2ticks().add(this);
        this.started = false;
        this.cooldown = 10;
        this.ten = 0;
        this.points.clear();
        this.rotation = 0;
        for(Player p : this.game.getPlayers()){
            this.playerGrace.put(p, 0);
            this.points.put(p,0);
        }
        this.count = 0;
    }

    @Override
    public void off(){
        this.game.getSigns2ticks().remove(this);
        this.directions.clear();
        this.graceTime.clear();
        for(Item i : this.ingots){
            i.remove();
        }
        this.ingots.clear();
        for(Player p : this.game.getPlayers()){
            p.teleport(this.back);
        }
    }

    @Override
    public void isTriggered(){
        if(this.started){

            // Placing and removing ingots
            for(int i = this.ingots.size() - 1; i >= 0; i--){
                if(this.ingots.get(i).isDead()){
                    this.ingots.remove(i);
                }
            }

            World w = this.location.getWorld();
            Location middle = this.location.clone().add(0.5, 0, 0.5);

            // Remove an ingot if there are too much and add a new one
            if(this.ten % 3 == 0){
                if(this.ingots.size() <= 15){
                    int randomIngot = (int)Math.floor(Math.random() * 6);
                    ItemStack item = new ItemStack(LaserGoRound.materials[randomIngot], 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(this.count + "xlr");
                    this.count++;
                    item.setItemMeta(meta);
                    Item itemIngot = w.dropItem(this.getSafeLocation(), item);
                    itemIngot.setVelocity(new Vector(0,0,0));
                    this.ingots.add(itemIngot);
                    /*this.ingots.get(0).remove();
                    this.ingots.remove(0);*/
                }
            }


            // Player grace time
            for(Player p : this.game.getPlayers()){
                this.playerGrace.put(p, Math.max(0, this.playerGrace.get(p) - 1));
            }

            // Lasers
            double yOffset = 0;

            for(int i = 0; i < this.directions.size(); i++){
                Particle.DustOptions du;
                double laserTheta = this.directions.get(i);
                Vector direction;
                Location laser;

                // Collision for lower
                if(i % 2 == 0){
                    du = new Particle.DustOptions(Color.AQUA, 1);
                    yOffset = 0.2;
                    laser = middle.clone().add(0, yOffset, 0);
                    laserTheta += this.rotation;
                    direction = new Vector(Math.cos(laserTheta),0,Math.sin(laserTheta)).normalize();
                    for(Player p : this.game.getPlayers()){
                        if(this.playerGrace.get(p) > 0){
                            continue;
                        }
                        Vector v = laser.clone().toVector().subtract(p.getLocation().toVector());
                        double distance = v.crossProduct(direction).length();
                        if(distance < 0.6 && this.graceTime.get(i) == 0){
                            p.playSound(p.getLocation(), Sound.ENTITY_BAT_HURT, 1f, 1f);
                            this.playerGrace.put(p, 5);
                            this.points.put(p, this.points.get(p) - 10);
                        }
                    }
                }
                // Collision for upper
                else{
                    du = new Particle.DustOptions(Color.RED, 1);
                    yOffset = 1.85;
                    laser = middle.clone().add(0, yOffset, 0);
                    laserTheta -= this.rotation;
                    direction = new Vector(Math.cos(laserTheta),0,Math.sin(laserTheta)).normalize();
                    for(Player p : this.game.getPlayers()){
                        if((!p.isSneaking() || !p.isOnGround()) && this.playerGrace.get(p) == 0){
                            Vector v = laser.clone().toVector().subtract(p.getEyeLocation().toVector());
                            double distance = v.crossProduct(direction).length();
                            if(distance < 0.8 && this.graceTime.get(i) == 0){
                                p.playSound(p.getLocation(), Sound.ENTITY_BAT_HURT, 1f, 1f);
                                this.playerGrace.put(p, 3);
                                this.points.put(p, this.points.get(p) - 10);
                            }
                        }
                    }
                }

                // Drawing lasers
                for(double j = 0; j <= this.radius + 1; j += 0.2){
                    w.spawnParticle(Particle.REDSTONE, laser.clone().add(direction.clone().multiply(j)), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, laser.clone().add(direction.clone().multiply(-j)), 1, 0, 0, 0, 1, du);
                }
                this.graceTime.set(i, Math.max(0,this.graceTime.get(i) - 1));
            }

            this.rotation += Math.PI / 180;

            // Timer during game
            if(this.ten == 10){
                this.ten = 1;
                for(Player p : this.game.getPlayers()) {
                    MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
                }

                // First two lasers
                if(this.cooldown == this.timer){
                    this.directions.add(0.0);
                    this.directions.add(Math.PI / 6);
                    this.graceTime.add(12);
                    this.graceTime.add(12);
                }

                if(this.cooldown == this.timer - 10){
                    this.directions.add(Math.PI * 4 / 6);
                    this.graceTime.add(12);
                }

                if(this.cooldown == this.timer - 20){
                    this.directions.add(Math.PI * 5 / 6);
                    this.graceTime.add(12);
                }

                if(this.cooldown == this.timer - 25){
                    this.directions.add(Math.PI * 2 / 6);
                    this.graceTime.add(12);
                }

                if(this.cooldown == this.timer - 30){
                    this.directions.add(Math.PI * 3 / 6);
                    this.graceTime.add(12);
                }

                // Game's end
                if(this.cooldown == 0) {
                    this.off();

                    // Calculates the final score
                    int total = 0;
                    for(Player p : this.game.getPlayers()) {
                        ItemStack[] items = p.getInventory().getContents();
                        for (ItemStack is : items) {
                            if (is != null) {
                                if (is.getType() == Material.IRON_INGOT) {
                                    this.points.put(p, this.points.get(p) + 3 * is.getAmount());
                                    is.setAmount(0);
                                }
                                if (is.getType() == Material.COPPER_INGOT) {
                                    this.points.put(p, this.points.get(p) + is.getAmount());
                                    is.setAmount(0);
                                }
                                if (is.getType() == Material.GOLD_INGOT) {
                                    this.points.put(p, this.points.get(p) + 5 * is.getAmount());
                                    is.setAmount(0);
                                }
                            }
                        }
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
            else{
                this.ten++;
            }

        }
        else{
            if(this.ten == 10) {
                this.ten = 1;
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
                    this.back = this.game.getPlayers().get(0).getLocation();
                    for(Player p : this.game.getPlayers()) {
                        double randomRadius = 2 + Math.random()*(this.radius - 2);
                        double randomPi = Math.random() * (Math.PI * 2);
                        p.teleport(this.location.clone().add(Math.cos(randomPi) * randomRadius, 0, Math.sin(randomPi) * randomRadius));
                    }
                    return;
                }
                this.cooldown--;
            }
            else {
                this.ten++;
            }
        }

    }

    private Location getSafeLocation(){
        Location l = null;
        Location middle = this.location.clone().add(0.5, 0, 0.5);
        while(l == null || !l.getBlock().getType().equals(Material.AIR) || l.clone().add(0,-1,0).getBlock().getType().equals(Material.AIR)){
            double randomRadius = 2 + Math.random()*(this.radius - 2);
            double randomPi = Math.random() * (Math.PI * 2);
            l = middle.clone().add(Math.floor(Math.cos(randomPi) * randomRadius), 0, Math.floor(Math.sin(randomPi) * randomRadius));
        }
        return l;
    }

}
