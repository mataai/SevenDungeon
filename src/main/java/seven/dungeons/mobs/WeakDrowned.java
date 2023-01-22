package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class WeakDrowned extends SevenMob {

    public WeakDrowned() {
        this.entityType = EntityType.DROWNED;
        this.mobName = "Weak Drowned";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(7);
        this.mob.setHealth(7);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
