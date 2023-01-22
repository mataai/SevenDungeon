package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.signs.SpawnMobSign;

public class ShiveringZombie extends SevenMob {

    private SpawnMobSign sms;
    
    public ShiveringZombie(SpawnMobSign sms) {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Shivering Zombie";
        this.sms = sms;
        this.hasMagic = true;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
        this.mob.setHealth(10);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.AQUA);
        helmet.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(Color.AQUA);
        chestplate.setItemMeta(meta2);
        this.mob.getEquipment().setChestplate(chestplate);
    }
    
    @Override
    public void spell() {
        Particle.DustOptions du = new Particle.DustOptions(Color.AQUA, 1);
        this.mob.getWorld().spawnParticle(Particle.REDSTONE, this.mob.getEyeLocation(), 2, 1, 1, 1, 1, du);
        for(Player p : this.sms.getGame().getPlayers()) {
            if(p.getLocation().distance(this.mob.getLocation()) < 4) {
                p.removePotionEffect(PotionEffectType.SLOW);
                PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 60, 2, true);
                p.addPotionEffect(potion);
            }
        }
    	
    }
}
