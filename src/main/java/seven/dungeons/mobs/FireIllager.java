package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class FireIllager extends SevenMob {

	public FireIllager() {
        this.entityType = EntityType.VINDICATOR;
        this.mobName = "Fire Illager";
        this.hasMagic = false;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(35);
        this.mob.setHealth(35);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD, 1);
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        this.mob.getEquipment().setItemInMainHand(sword);
    }
	
}
