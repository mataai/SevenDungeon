package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class WeakGuardian extends SevenMob {

    public WeakGuardian() {
        this.entityType = EntityType.GUARDIAN;
        this.mobName = "Weak Guardian";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);
        this.mob.setHealth(4);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
