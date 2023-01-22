package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class NormalAllay extends SevenMob {

    public NormalAllay() {
        this.entityType = EntityType.ALLAY;
        this.mobName = "Allay";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
        this.mob.setHealth(2);
        this.mob.setInvulnerable(true);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
