package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class VeteranSkeleton extends SevenMob {

    public VeteranSkeleton() {
        this.entityType = EntityType.SKELETON;
        this.mobName = "Skeleton Cadet";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        this.mob.getEquipment().setItemInMainHand(bow);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        this.mob.setHealth(20);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.getEquipment().setChestplate(chestplate);
    }
}
