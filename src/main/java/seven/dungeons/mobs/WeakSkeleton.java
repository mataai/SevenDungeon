package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeakSkeleton extends SevenMob {

    public WeakSkeleton() {
        this.entityType = EntityType.SKELETON;
        this.mobName = "Weak Skeleton";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack shovel = new ItemStack(Material.STONE_SHOVEL, 1);
        this.mob.getEquipment().setItemInMainHand(shovel);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5);
        this.mob.setHealth(5);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
