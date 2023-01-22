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

public class Rewinder extends SevenMob {

	private SpawnMobSign sms;
    private int cooldown;
    private Player target;
    private boolean laserActive;
    private Location laserLocation;
    private Vector laserDirection;
    private Location previousLocation;

    public Rewinder(SpawnMobSign sms) {
        this.entityType = EntityType.WITHER_SKELETON;
        this.mobName = "Rewinder";
        this.hasMagic = true;
        this.sms = sms;
        this.cooldown = 3;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        this.mob.setHealth(30);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
    
    @Override
    public void spell() {
        if(this.cooldown == 0){
            this.cooldown = 40;
            this.target = this.getClosestPlayer();
            this.previousLocation = this.target.getLocation().clone();
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
        if(target.getEyeLocation().distance(this.laserLocation) < 1.3) {
            this.attackPlayer();
            this.laserActive = false;
        }
        this.animateLaser();
        if(!this.laserLocation.getBlock().isPassable()){
            this.laserActive = false;
        }
    }

    public void attackPlayer(){
        this.mob.getLocation().getWorld().spawnParticle(Particle.SPELL_WITCH, this.mob.getEyeLocation(), 5, 1, 1, 1, 1);
        this.mob.getWorld().playSound(this.target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.5f);
        this.laserLocation.getWorld().playSound(this.laserLocation, Sound.ENTITY_ENDER_PEARL_THROW, 1, 2);
        DustOptions du = new DustOptions(Color.BLACK, 1);
        this.laserLocation.getWorld().spawnParticle(Particle.REDSTONE, target.getEyeLocation(), 15, 0.6, 0.6, 0.6, 1, du);
        PotionEffect pe;
        pe = new PotionEffect(PotionEffectType.WITHER, 140, 0, false, false);
        this.givePotionEffectToTarget(pe);
        Bukkit.getScheduler().runTaskLater(this.sms.getGame().getPlugin(), () -> this.target.teleport(this.previousLocation, PlayerTeleportEvent.TeleportCause.PLUGIN), 10l);
    }

    public void animateLaser(){
        for(float i = -2f; i <= 0; i += 0.25) {
            DustOptions du = new DustOptions(Color.BLACK, 1);
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
