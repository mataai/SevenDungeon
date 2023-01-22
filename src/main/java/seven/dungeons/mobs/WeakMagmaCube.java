package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

public class WeakMagmaCube extends SevenMob {

    public WeakMagmaCube() {
        this.entityType = EntityType.MAGMA_CUBE;
        this.mobName = "Weak Magma Cube";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
        this.mob.setHealth(6);
        ((Slime)this.mob).setSize(2);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}

