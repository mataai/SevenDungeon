package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;

public class WeakHusk extends SevenMob {
    
    public WeakHusk() {
        this.entityType = EntityType.HUSK;
        this.mobName = "Weak Husk";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Husk)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(7);
        this.mob.setHealth(7);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}