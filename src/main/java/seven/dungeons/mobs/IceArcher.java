package seven.dungeons.mobs;

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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import seven.dungeons.signs.SpawnMobSign;

public class IceArcher extends SevenMob {
    private float cooldown;
    private SpawnMobSign sms;
    
    public IceArcher(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.SKELETON;
        this.mobName = "Ice Archer";
        this.hasMagic = true;
        this.cooldown = 8;
        
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack tippedArrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) tippedArrow.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.SLOWNESS));
        tippedArrow.setItemMeta(meta);
        this.mob.getEquipment().setItem(EquipmentSlot.OFF_HAND, tippedArrow);
        ItemStack bow = new ItemStack(Material.BOW, 1);
        this.mob.getEquipment().setItemInMainHand(bow);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(35);
        this.mob.setHealth(35);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) helmet.getItemMeta();
        meta2.setColor(Color.AQUA);
        helmet.setItemMeta(meta2);
        this.mob.getEquipment().setHelmet(helmet);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta3 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta3.setColor(Color.AQUA);
        chestplate.setItemMeta(meta3);
        this.mob.getEquipment().setChestplate(chestplate);
    }
    
    @Override
    public void kill() {
        this.mob.remove();
    }
    
    @Override
    public void spell() {
    	this.cooldown -= 0.25;
    	if(this.cooldown == 1.5) {
    		DustOptions du = new DustOptions(Color.AQUA, 1);
    		Location loc1 = this.mob.getLocation().clone().add(0,0,0);
    		for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 24) {
                loc1.getWorld().spawnParticle(Particle.REDSTONE, loc1.clone().add(Math.cos(i)*5,1,Math.sin(i)*5), 1, 0, 0, 0, 1, du);
            }
    	}
    
    	if(this.cooldown <= 0) {
    		this.cooldown = 8;
    		for(Player p : this.sms.getGame().getPlayers()) {
                if(p.getLocation().distance(this.mob.getLocation()) < 5) {
                    p.removePotionEffect(PotionEffectType.SLOW);
                    PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 100, 2, true);
                    p.addPotionEffect(potion);
                    this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 2);
                }
            }
    	}
    }
}
