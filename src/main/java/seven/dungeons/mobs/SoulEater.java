package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class SoulEater extends SevenMob {

	private SpawnMobSign sms;
    private int cooldown;
    private boolean lasersActive;
    private ArrayList<Location> laserLocations;
    private ArrayList<Vector> laserDirections;
    private int fireTicks;

    public SoulEater(SpawnMobSign sms) {
        this.entityType = EntityType.BLAZE;
        this.mobName = "Soul Eater";
        this.hasMagic = true;
        this.sms = sms;
        this.cooldown = 3;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
        this.mob.setHealth(50);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.fireTicks = 5;
        this.laserLocations = new ArrayList<Location>();
        this.laserDirections = new ArrayList<Vector>();
    }
    
    @Override
    public void spell() {

        if(this.lasersActive){
            this.laserProgress();
        }

        ArrayList<LivingEntity> mobs = new ArrayList<LivingEntity>();
        for(LivingEntity le : this.mob.getLocation().getWorld().getLivingEntities()){
            double distance = le.getLocation().distance(this.mob.getLocation());
            if(distance < 10 && distance > 1 && ! (le instanceof Player)){
                mobs.add(le);
            }
        }

        if(!mobs.isEmpty()){
            for(LivingEntity le : mobs){
                le.setHealth(Math.max(0.1, le.getHealth() - 0.26));
                this.mob.setHealth(Math.min(this.mob.getHealth() + 0.25, this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                this.fireTicks++;

                Particle.DustOptions du = new Particle.DustOptions(Color.TEAL, 1);

                Location l = this.mob.getEyeLocation().clone();
                l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, this.mob.getEyeLocation(), 2, 0.5, 0.5, 0.5);
                l.getWorld().spawnParticle(Particle.SMOKE_NORMAL, le.getEyeLocation(), 10, 0.5, 0.5, 0.5, 0);
                Vector direction = le.getLocation().clone().add(0, 1,0).add(l.clone().multiply(-1)).toVector().normalize().multiply(0.2);
                for(int j = 0; j < this.mob.getLocation().distance(le.getLocation()) * 4.8; j++){
                    this.mob.getWorld().spawnParticle(Particle.REDSTONE, l.add(direction), 1, 0, 0, 0, 1, du);
                }
            }
        }
        else if(this.cooldown <= 0){
            this.cooldown = 40;
            this.laserLocations.clear();
            this.laserDirections.clear();
            this.lasersActive = true;
            for(Player p : this.sms.getGame().getPlayers()){
                this.laserLocations.add(this.mob.getEyeLocation().clone());
                this.laserDirections.add(p.getEyeLocation().clone().add(this.mob.getEyeLocation().clone().multiply(-1)).toVector().normalize());
            }
            this.mob.getLocation().getWorld().playSound(this.mob.getEyeLocation(), Sound.BLOCK_CONDUIT_ATTACK_TARGET, 1.5f, 1.5f);

        }
        this.cooldown--;
    }

    public void laserProgress(){
        for(int i = this.laserLocations.size() - 1; i >= 0; i--){
            this.laserLocations.get(i).add(this.laserDirections.get(i).clone().multiply(3));
            for(Player p : this.sms.getGame().getPlayers()){
                if(p.getEyeLocation().distance(this.laserLocations.get(i)) < 2){
                    this.attackPlayer(p);
                    this.laserLocations.remove(i);
                    this.laserDirections.remove(i);
                    break;
                }
                else if(!this.laserLocations.get(i).getBlock().isPassable()){
                    this.laserLocations.remove(i);
                    this.laserDirections.remove(i);
                    break;
                }
            }
        }
        this.animateLasers();
        if(this.laserLocations.isEmpty()){
            this.lasersActive = false;
        }
    }

    public void attackPlayer(Player p){
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1, 2);
        DustOptions du = new DustOptions(Color.ORANGE, 1);
        p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation(), 15, 0.6, 0.6, 0.6, 1, du);
        p.setFireTicks(p.getFireTicks() + this.fireTicks);
    }

    public void animateLasers(){
        DustOptions du = new DustOptions(Color.ORANGE, 1);
        for(int i = 0; i < this.laserLocations.size(); i++){
            for(float j = -3f; j <= 0; j += 0.25) {
                this.laserLocations.get(i).getWorld().spawnParticle(Particle.REDSTONE, this.laserLocations.get(i).clone().add(this.laserDirections.get(i).clone().multiply(j)), 1, 0, 0, 0, 1, du);
            }
        }
    }
            
}
