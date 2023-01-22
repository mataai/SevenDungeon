package seven.dungeons.mobs;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import seven.dungeons.SevenDungeons;

public class SourSkeleton extends SevenMob {
    
    private float cooldown;
    
    public SourSkeleton() {
        this.entityType = EntityType.SKELETON;
        this.mobName = "Sour Skeleton";
        this.hasMagic = true;
        this.cooldown = 5;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack shovel = new ItemStack(Material.GOLDEN_SHOVEL, 1);
        ItemStack helmet = new ItemStack(Material.PINK_CONCRETE, 1);
        this.mob.getEquipment().setItemInMainHand(shovel);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(3);
        this.mob.setHealth(3);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
    
    @Override
    public void spell() {
        this.cooldown -= 0.25;
        if(this.cooldown < 1) {
            World w = this.mob.getWorld();
            Location l = this.mob.getLocation().clone().add(0,1,0);
            DustOptions du = new DustOptions(Color.FUCHSIA, 1);
            for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 12) {
                w.spawnParticle(Particle.REDSTONE, l.clone().add(Math.cos(i),0,Math.sin(i)), 1, 0, 0, 0, 1, du);
            }
            if(this.cooldown <= 0) {
                for(Player p : this.mob.getWorld().getPlayers()) {
                    Location pl = p.getLocation().clone().add(0,1,0);
                    float distance = (float) pl.distance(l);
                    if(distance <= 10) {
                        Vector v = pl.toVector().subtract(l.toVector());
                        for(float i = 0; i < 1; i+= 0.04f) {
                            Vector nv = v.clone().multiply(i);
                            w.spawnParticle(Particle.REDSTONE, l.clone().add(nv), 1, 0, 0, 0, 0, du);
                        }
                        for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 12) {
                            w.spawnParticle(Particle.REDSTONE, pl.clone().add(Math.cos(i),0,Math.sin(i)), 1, 0, 0, 0, 0, du);
                        }
                        w.playSound(l, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2, 1.3f);
                        if(p.hasPotionEffect(PotionEffectType.JUMP)) {
                            p.removePotionEffect(PotionEffectType.JUMP);
                        }
                        PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 200, 4, false, false);
                        p.addPotionEffect(jump);
                    }
                }
                this.cooldown = 5;
            }
        }
    }
}
