package seven.dungeons.mobs;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.SpawnMobSign;

public class BomberKing extends SevenMob {
    
    private float cooldown;
    private FixedMetadataValue no_knockback = new FixedMetadataValue (SevenDungeons.getPlugin(), "noknockback");
    private FixedMetadataValue no_explosion = new FixedMetadataValue (SevenDungeons.getPlugin(), "noExplosion");
    private SpawnMobSign sms;
    private ArrayList<SevenMob> babies = new ArrayList<SevenMob>();
    private float lastHealth;
    
    public BomberKing(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.WITHER_SKELETON;
        this.mobName = "Bomber King";
        this.hasMagic = true;
        this.cooldown = 25;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(250);
        this.mob.setHealth(250);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.TNT);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.setMetadata("noknockback", this.no_knockback);
        this.mob.setMetadata("noExplosion", this.no_explosion);
        this.lastHealth = 250;
        PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 36000, 2, true);
        this.mob.addPotionEffect(potion);
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
        this.cooldown -= 0.25;
        if(this.cooldown > 24) {
            return;
        }
        for(int i = 0; i < this.babies.size(); i++) {
            if(!this.babies.get(i).isDead()) {
                this.babies.get(i).updateVisibleName();
                this.babies.get(i).spell();
            }
            else {
                this.babies.remove(i);
                i--;
            }
        }
        if(this.babies.size() > 1) {
            if(this.lastHealth > this.mob.getHealth()) {
                this.mob.setHealth(this.lastHealth);
                this.mob.getLocation().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, this.mob.getLocation().clone().add(0, 1, 0), 10, 2, 2, 2);
                this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2, 2);
            }
        }
        else {
            this.lastHealth = (int)this.mob.getHealth();
        }
        if(this.cooldown <= 18) {
            DustOptions du = new DustOptions(Color.RED, 1);
            World w = this.mob.getWorld();
            PotionEffect potion;
            switch((int)(this.cooldown * 4)) {
            case 72 :
                this.mob.getEquipment().setItemInMainHand(new ItemStack(Material.TNT, 1));
                Location loc1 = this.mob.getLocation().clone().add(0,1,0);
                for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 24) {
                    w.spawnParticle(Particle.REDSTONE, loc1.clone().add(Math.cos(i)*3,0,Math.sin(i)*3), 1, 0, 0, 0, 1, du);
                }
                this.mob.removePotionEffect(PotionEffectType.SLOW);
                potion = new PotionEffect(PotionEffectType.SLOW, 80, 20, true);
                this.mob.addPotionEffect(potion);
                break;
            case 64 :
                this.mob.getEquipment().setItemInMainHand(null);
                Location loc2 = this.mob.getLocation().clone().add(0,3,0);
                for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 4) {
                    Entity tnt1 = w.spawnEntity(loc2.clone().add(Math.cos(i)*3,0,Math.sin(i)*3), EntityType.PRIMED_TNT);
                    tnt1.setMetadata("noExplosion", this.no_explosion);
                }
                this.mob.removePotionEffect(PotionEffectType.SLOW);
                potion = new PotionEffect(PotionEffectType.SLOW, 3600, 2, true);
                this.mob.addPotionEffect(potion);
                break;
            case 40 :
                this.mob.getEquipment().setItemInMainHand(new ItemStack(Material.TNT, 1));
                Location loc3 = this.mob.getLocation().clone().add(0,1,0);
                for(float i = 0; i < 8; i += 0.25) {
                    w.spawnParticle(Particle.REDSTONE, loc3.clone().add(i,0,0), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc3.clone().add(-i,0,0), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc3.clone().add(0,0,i), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc3.clone().add(0,0,-i), 1, 0, 0, 0, 1, du);
                }
                this.mob.removePotionEffect(PotionEffectType.SLOW);
                potion = new PotionEffect(PotionEffectType.SLOW, 80, 20, true);
                this.mob.addPotionEffect(potion);
                break;
            case 32 :
                this.mob.getEquipment().setItemInMainHand(null);
                Location loc4 = this.mob.getLocation().clone().add(0,3,0);
                for(int i = 2; i <= 8; i += 2) {
                    Entity tnt1 = w.spawnEntity(loc4.clone().add(i,0,0), EntityType.PRIMED_TNT);
                    Entity tnt2 = w.spawnEntity(loc4.clone().add(-i,0,0), EntityType.PRIMED_TNT);
                    Entity tnt3 = w.spawnEntity(loc4.clone().add(0,0,i), EntityType.PRIMED_TNT);
                    Entity tnt4 = w.spawnEntity(loc4.clone().add(0,0,-i), EntityType.PRIMED_TNT);
                    tnt1.setMetadata("noExplosion", this.no_explosion);
                    tnt2.setMetadata("noExplosion", this.no_explosion);
                    tnt3.setMetadata("noExplosion", this.no_explosion);
                    tnt4.setMetadata("noExplosion", this.no_explosion);
                }
                this.mob.removePotionEffect(PotionEffectType.SLOW);
                potion = new PotionEffect(PotionEffectType.SLOW, 3600, 2, true);
                this.mob.addPotionEffect(potion);
                break;
            case 8 :
                this.mob.getEquipment().setItemInMainHand(new ItemStack(Material.TNT, 1));
                Location loc5 = this.mob.getLocation().clone().add(0,3,0);
                for(float i = 0; i < 3; i += 0.25) {
                    w.spawnParticle(Particle.REDSTONE, loc5.clone().add(2,-i,0), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc5.clone().add(0,-i,2), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc5.clone().add(1.67,-i,-1.67), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc5.clone().add(-1.67,-i,1.67), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, loc5.clone().add(-1.67,-i,-1.67), 1, 0, 0, 0, 1, du);
                }
                this.mob.removePotionEffect(PotionEffectType.SLOW);
                potion = new PotionEffect(PotionEffectType.SLOW, 80, 20, true);
                this.mob.addPotionEffect(potion);
                break;
            case 0 :
                this.mob.getEquipment().setItemInMainHand(null);
                Location loc6 = this.mob.getLocation().clone().add(0,1,0);
                SevenMob bb1 = new BabyBomber();
                SevenMob bb2 = new BabyBomber();
                SevenMob bb3 = new BabyBomber();
                SevenMob bb4 = new BabyBomber();
                SevenMob bb5 = new BabyBomber();
                new SpawnTask(bb1, loc6.clone().add(2,0,0)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
                new SpawnTask(bb2, loc6.clone().add(0,0,2)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
                new SpawnTask(bb3, loc6.clone().add(1.67,0,-1.67)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
                new SpawnTask(bb4, loc6.clone().add(-1.67,0,-1.67)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
                new SpawnTask(bb5, loc6.clone().add(-1.67,0,-1.67)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
                this.babies.add(bb1);
                this.babies.add(bb2);
                this.babies.add(bb3);
                this.babies.add(bb4);
                this.babies.add(bb5);
                this.cooldown = 24;
                this.mob.removePotionEffect(PotionEffectType.SLOW);
                potion = new PotionEffect(PotionEffectType.SLOW, 3600, 2, true);
                this.mob.addPotionEffect(potion);
                break;
            }
        }
    }
}
