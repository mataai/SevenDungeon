package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MoronZombie extends SevenMob {
    public MoronZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Moron Zombie";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.setFireTicks(0);
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.5);
        this.mob.setHealth(0.5);
        PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 36000, 0, true);
        this.mob.addPotionEffect(potion);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        this.mob.getEquipment().setHelmet(helmet);
    }
}
