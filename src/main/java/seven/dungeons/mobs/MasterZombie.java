package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class MasterZombie extends SevenMob {

	public MasterZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Master Zombie";
        this.hasMagic = false;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.setFireTicks(0);
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        this.mob.setHealth(50);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.getEquipment().setChestplate(chestplate);
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE, 1);
        this.mob.getEquipment().setItemInMainHand(axe);
    }
	
}
