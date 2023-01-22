package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class WeakWitherSkeleton extends SevenMob {
    
    public WeakWitherSkeleton() {
        this.entityType = EntityType.WITHER_SKELETON;
        this.mobName = "Weak Wither Skeleton";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
        this.mob.getEquipment().setItemInMainHand(sword);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(15);
        this.mob.setHealth(15);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
