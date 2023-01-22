package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

public class WeakZombie extends SevenMob {
    
    public WeakZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Weak Zombie";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5);
        this.mob.setHealth(5);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
