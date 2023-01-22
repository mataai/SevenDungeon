package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class ArmoredGuardian extends SevenMob {

    public ArmoredGuardian() {
        this.entityType = EntityType.GUARDIAN;
        this.mobName = "Armored Guardian";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(15);
        this.mob.setHealth(15);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);

        this.mob.setInvulnerable(true);
    }

    @Override
    public void spell() {
        if(this.mob.isInWater()){
            this.mob.setInvulnerable(true);
        }
        else{
            this.mob.setInvulnerable(false);
        }
    }
}
