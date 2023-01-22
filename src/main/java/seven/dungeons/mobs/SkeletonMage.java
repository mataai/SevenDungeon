package seven.dungeons.mobs;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SkeletonMage extends SevenMob {
    
    private float cooldown;
    private Location spawnLocation;
    private ArrayList<Arrow> arrows = new ArrayList<Arrow>();
    public static int[] arrowYaw = {-135,-90,-45,0,45,90,135,-180};
    public static Vector[] arrowVectors = {
            new Vector(0.33,0,-0.33),
            new Vector(0.5,0,0),
            new Vector(0.33,0,0.33),
            new Vector(0,0,0.5),
            new Vector(-0.33,0,0.33),
            new Vector(-0.5,0,0),
            new Vector(-0.33,0,-0.33),
            new Vector(0,0,-0.5),
            };
    
    public SkeletonMage(Location l) {
        this.entityType = EntityType.SKELETON;
        this.mobName = "Skeleton Mage";
        this.hasMagic = true;
        this.cooldown = 8;
        this.spawnLocation = l;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack bow = new ItemStack(Material.BOW, 1);
        this.mob.getEquipment().setItemInMainHand(bow);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80);
        this.mob.setHealth(80);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.WHITE);
        helmet.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
    }
    
    @Override
    public void spell() {
        this.cooldown -= 0.25;
        if(this.cooldown < 7.5 && this.mob.getLocation().getBlockY() + 5 < this.spawnLocation.getBlockY()) {
            this.mob.teleport(this.spawnLocation, TeleportCause.PLUGIN);
            this.mob.setVelocity(new Vector(0,0,0));
        }
        if(this.cooldown <= 3) {
            if(this.cooldown == 3) {
                for(Arrow a : this.arrows) {
                    a.remove();
                }
                this.arrows.clear();
                PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 60, 20, true);
                this.mob.addPotionEffect(potion);
                ItemStack helmet = this.mob.getEquipment().getHelmet();
                LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
                meta.setColor(Color.RED);
                helmet.setItemMeta(meta);
                this.mob.getEquipment().setHelmet(helmet);
                DustOptions du = new DustOptions(Color.RED, 1);
                Location l = this.mob.getLocation().clone().add(0,0.5,0);
                World w = this.mob.getWorld();
                for(float i = 0; i <= 10; i += 0.25) {
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(i,0,0), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(0,0,i), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(-i,0,0), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(0,0,-i), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(i,0,i), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(-i,0,i), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(-i,0,-i), 1, 0, 0, 0, 1, du);
                    w.spawnParticle(Particle.REDSTONE, l.clone().add(i,0,-i), 1, 0, 0, 0, 1, du);
                }
            }
            else if(this.cooldown <= 2) {
                World w = this.mob.getWorld();
                for(int i = 0; i < SkeletonMage.arrowVectors.length; i++) {
                    Location l = this.mob.getEyeLocation().clone().add(SkeletonMage.arrowVectors[i]);
                    l.setYaw(SkeletonMage.arrowYaw[i]);
                    l.setPitch(0);
                    this.arrows.add(w.spawnArrow(l, l.getDirection(), 1.5f, 0));
                }
                
            }
            if(this.cooldown <= 0) {
                this.cooldown = 8;
                ItemStack helmet = this.mob.getEquipment().getHelmet();
                LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
                meta.setColor(Color.WHITE);
                helmet.setItemMeta(meta);
                this.mob.getEquipment().setHelmet(helmet);
            }
        }
    }
    

}
