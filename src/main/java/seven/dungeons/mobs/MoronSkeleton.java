package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class MoronSkeleton extends SevenMob {
    
    public MoronSkeleton() {
        this.entityType = EntityType.SKELETON;
        this.mobName = "Moron Skeleton";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack pickaxe = new ItemStack(Material.WOODEN_PICKAXE, 1);
        this.mob.getEquipment().setItemInMainHand(pickaxe);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.5);
        this.mob.setHealth(0.5);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }

}
