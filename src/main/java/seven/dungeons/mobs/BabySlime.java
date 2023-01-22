package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

public class BabySlime extends SevenMob {

    public BabySlime() {
        this.entityType = EntityType.SLIME;
        this.mobName = "Baby Slime";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        ((Slime)this.mob).setSize(1);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);
        this.mob.setHealth(4);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
