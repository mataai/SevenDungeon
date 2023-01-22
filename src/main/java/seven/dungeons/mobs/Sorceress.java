package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class Sorceress extends SevenMob {

	private SpawnMobSign sms;
    private int cooldown;
    private Player target;
    private boolean laserActive;
    private Location laserLocation;
    private Vector laserDirection;

    public Sorceress(SpawnMobSign sms) {
        this.entityType = EntityType.WITCH;
        this.mobName = "Sorceress";
        this.hasMagic = true;
        this.sms = sms;
        this.cooldown = 3;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        this.mob.setHealth(40);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
    
    @Override
    public void spell() {
        if(this.cooldown == 0){
            this.cooldown = 40;
            this.target = this.getClosestPlayer();
            this.laserActive = true;
            this.laserLocation = this.mob.getEyeLocation().clone();
            this.laserDirection = this.target.getEyeLocation().clone().subtract(this.laserLocation).toVector().normalize();
            this.mob.getLocation().getWorld().playSound(this.laserLocation, Sound.BLOCK_CONDUIT_ATTACK_TARGET, 1.5f, 1.5f);

        }
        if(this.laserActive){
            this.laserProgress();
        }
        this.cooldown--;
    }

    public Player getClosestPlayer(){
        double distance = Double.MAX_VALUE;
        Player player = null;
        for(Player p : this.sms.getGame().getPlayers()){
            double d = p.getLocation().distance(this.mob.getLocation());
            if(d < distance){
                player = p;
                distance = d;
            }
        }
        return player;
    }

    public void laserProgress(){
        this.laserDirection = this.laserDirection.clone().multiply(0.7).add(this.target.getLocation().toVector().add(new Vector(0,1,0)).subtract(this.laserLocation.clone().toVector()).normalize().multiply(0.3)).normalize();
        this.laserLocation.add(this.laserDirection.clone().multiply(2));
        if(target.getEyeLocation().distance(this.laserLocation) < 1.2) {
            this.attackPlayer();
            this.laserActive = false;
        }
        this.animateLaser();
        if(!this.laserLocation.getBlock().isPassable()){
            this.laserActive = false;
        }
    }

    public void attackPlayer(){
        this.target.playSound(this.laserLocation, Sound.ENTITY_ENDER_PEARL_THROW, 1, 2);
        DustOptions du = new DustOptions(Color.WHITE, 1);
        this.laserLocation.getWorld().spawnParticle(Particle.REDSTONE, target.getEyeLocation(), 15, 0.6, 0.6, 0.6, 1, du);
        PotionEffect pe;
        switch((int)Math.floor(Math.random()*7)){
            case 0 :
                this.target.damage(8);
                break;
            case 1 :
                this.target.setFireTicks(this.target.getFireTicks() + 140);
                break;
            case 2 :
                pe = new PotionEffect(PotionEffectType.POISON, 140, 1, false, false);
                this.givePotionEffectToTarget(pe);
                break;
            case 3 :
                pe = new PotionEffect(PotionEffectType.WITHER, 140, 0, false, false);
                this.givePotionEffectToTarget(pe);
                break;
            case 4 :
                pe = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false);
                this.givePotionEffectToTarget(pe);
                break;
            case 5 :
                pe = new PotionEffect(PotionEffectType.SLOW, 140, 3, false, false);
                this.givePotionEffectToTarget(pe);
                break;
            default :
                this.target.damage(4);
                this.target.setFireTicks(this.target.getFireTicks() + 100);
                pe = new PotionEffect(PotionEffectType.POISON, 120, 0, false, false);
                this.givePotionEffectToTarget(pe);
                pe = new PotionEffect(PotionEffectType.BLINDNESS, 80, 0, false, false);
                this.givePotionEffectToTarget(pe);
                pe = new PotionEffect(PotionEffectType.SLOW, 140, 1, false, false);
                this.givePotionEffectToTarget(pe);
        }
    }

    public void animateLaser(){
        for(float i = -2f; i <= 0; i += 0.25) {
            DustOptions du = new DustOptions(Color.WHITE, 1);
            this.laserLocation.getWorld().spawnParticle(Particle.REDSTONE, this.laserLocation.clone().add(this.laserDirection.clone().multiply(i)), 1, 0, 0, 0, 1, du);
        }
    }

    public void givePotionEffectToTarget(PotionEffect pe){
        if(this.target.hasPotionEffect(pe.getType())){
            this.target.removePotionEffect(pe.getType());
        }
        this.target.addPotionEffect(pe);
    }
            
}
