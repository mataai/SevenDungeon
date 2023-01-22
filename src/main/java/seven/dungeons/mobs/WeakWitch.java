package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class WeakWitch extends SevenMob {

    public WeakWitch() {
        this.entityType = EntityType.WITCH;
        this.mobName = "Weak Witch";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
        this.mob.setHealth(10);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
