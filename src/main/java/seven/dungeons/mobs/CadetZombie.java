package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class CadetZombie extends SevenMob {
    
    public CadetZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Zombie Cadet";
        this.hasMagic = false;
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
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.getEquipment().setChestplate(chestplate);
    }
}
