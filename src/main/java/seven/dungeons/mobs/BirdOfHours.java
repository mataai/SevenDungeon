package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;
import java.util.HashMap;

public class BirdOfHours extends SevenMob {

    private SpawnMobSign sms;
    private ArrayList<SevenMob> babies = new ArrayList<SevenMob>();
    private int currentHour;
    private HashMap<Player,Location> playerLocations = new HashMap<Player,Location>();

    public BirdOfHours(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.PARROT;
        this.mobName = "Bird of Hours";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        ((Parrot)this.mob).setVariant(Parrot.Variant.GRAY);
        this.emptyInventory();
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
        this.mob.setHealth(1);
        this.mob.setInvulnerable(true);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.currentHour = 0;
        this.reset();
        for(Player p : this.sms.getGame().getPlayers()){
            this.playerLocations.put(p, p.getLocation());
        }
    }
    
    @Override
    public void kill() {
        for(SevenMob sm : this.babies) {
            sm.mob.remove();
        }
        this.babies.clear();
        this.mob.remove();
    }
    
    @Override
    public void spell() {
        boolean deathCheck = false;
        for(int i = 0; i < this.babies.size(); i++){
            SevenMob sm = this.babies.get(i);
            if(deathCheck){
                if(sm.isDead()){
                    this.reset();
                    return;
                }
            }
            if(!sm.isDead()){
                sm.updateVisibleName();
                if(sm.hasMagic){
                    sm.spell();
                }
                deathCheck = true;
            }
            else if(this.currentHour == i){
                this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.BLOCK_BELL_USE, 3, 0.5f);
                this.currentHour++;
            }
        }
        if(this.currentHour == 12){
            this.currentHour++;
            this.mob.setInvulnerable(false);
            Location middle = this.sms.getLocation();
            for(int x = middle.getBlockX() - 1; x <= middle.getBlockX() + 1; x++){
                for(int z = middle.getBlockZ() - 1; z <= middle.getBlockZ() + 1; z++){
                    Location l = new Location(middle.getWorld(), x, middle.getBlockY() - 1, z);
                    l.getBlock().setType(Material.AIR);
                }
            }
            for(Player p : this.sms.getGame().getPlayers()){
                p.sendMessage("§aBird of Hours §r: Please don't hurt me !!");
            }
        }
    }

    public void reset(){
        for(Player p : this.playerLocations.keySet()){
            this.sms.getGame().healPlayer(p);
            p.teleport(this.playerLocations.get(p));
        }

        this.currentHour = 0;
        this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2, 2);
        for(SevenMob sm : this.babies){
            if(!sm.isDead()){
                sm.getMob().remove();
            }
        }
        this.babies.clear();

        // 1, 2, 3 : Cadet Skeletons
        SevenMob hour1 = new CadetSkeleton();
        hour1.setMobName("One o'clock");
        this.babies.add(hour1);

        SevenMob hour2 = new CadetSkeleton();
        hour2.setMobName("Two o'clock");
        this.babies.add(hour2);

        SevenMob hour3 = new CadetSkeleton();
        hour3.setMobName("Three o'clock");
        this.babies.add(hour3);

        // 4, 5, 6 : Grassy Zombies
        SevenMob hour4 = new GrassyZombie(this.sms);
        hour4.setMobName("Four o'clock");
        this.babies.add(hour4);

        SevenMob hour5 = new GrassyZombie(this.sms);
        hour5.setMobName("Five o'clock");
        this.babies.add(hour5);

        SevenMob hour6 = new GrassyZombie(this.sms);
        hour6.setMobName("Six o'clock");
        this.babies.add(hour6);

        // 7, 8, 9 : Witches
        SevenMob hour7 = new WeakWitch();
        hour7.setMobName("Seven o'clock");
        this.babies.add(hour7);

        SevenMob hour8 = new WeakWitch();
        hour8.setMobName("Eight o'clock");
        this.babies.add(hour8);

        SevenMob hour9 = new WeakWitch();
        hour9.setMobName("Nine o'clock");
        this.babies.add(hour9);

        // 10, 11, 12 : Veteran Zombies
        SevenMob hour10 = new VeteranZombie();
        hour10.setMobName("Ten o'clock");
        this.babies.add(hour10);

        SevenMob hour11 = new VeteranZombie();
        hour11.setMobName("Eleven o'clock");
        this.babies.add(hour11);

        SevenMob hour12 = new VeteranZombie();
        hour12.setMobName("Twelve o'clock");
        this.babies.add(hour12);

        double circle = 0.0;
        for(SevenMob sm : this.babies){
            new SpawnTask(sm, this.sms.getLocation().clone().add(0.5 + Math.cos(circle) * 4.5,-9,0.5 + Math.sin(circle) * 4.5)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            circle += Math.PI / 6;
        }
    }
}
