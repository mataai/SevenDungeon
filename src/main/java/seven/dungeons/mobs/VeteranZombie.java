package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class VeteranZombie extends SevenMob {

	public VeteranZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Veteran Zombie";
        this.hasMagic = false;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.setFireTicks(0);
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        this.mob.setHealth(40);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.getEquipment().setChestplate(chestplate);
        ItemStack axe = new ItemStack(Material.IRON_AXE, 1);
        this.mob.getEquipment().setItemInMainHand(axe);
    }
	
}
