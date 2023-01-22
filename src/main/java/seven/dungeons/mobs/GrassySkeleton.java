package seven.dungeons.mobs;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import seven.dungeons.signs.SpawnMobSign;

public class GrassySkeleton extends SevenMob {
    
    private SpawnMobSign sms;
    private float cooldown;
    
    public GrassySkeleton(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.SKELETON;
        this.mobName = "Grassy Skeleton";
        this.hasMagic = true;
        this.cooldown = 5;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack bow = new ItemStack(Material.BOW, 1);
        this.mob.getEquipment().setItemInMainHand(bow);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25);
        this.mob.setHealth(25);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.LIME);
        helmet.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(Color.LIME);
        chestplate.setItemMeta(meta2);
        this.mob.getEquipment().setChestplate(chestplate);
    }
    
    @Override
    public void spell() {
    	this.cooldown -= 0.25;
    	if(this.cooldown == 2) {
    		DustOptions du = new DustOptions(Color.PURPLE, 1);
    		Location loc1 = this.mob.getLocation().clone();
    		for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 24) {
                loc1.getWorld().spawnParticle(Particle.REDSTONE, loc1.clone().add(Math.cos(i)*4,1,Math.sin(i)*4), 1, 0, 0, 0, 1, du);
            }
    	}
    
    	if(this.cooldown <= 0) {
    		this.cooldown = 5;
    		for(Player p : this.sms.getGame().getPlayers()) {
                if(p.getLocation().distance(this.mob.getLocation()) < 4) {
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                    PotionEffect potion = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, true);
                    p.addPotionEffect(potion);
                    this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 2);
                }
            }
    	}
    }
}
