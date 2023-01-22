package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MasterRavager extends SevenMob {

    public MasterRavager() {
        this.entityType = EntityType.RAVAGER;
        this.mobName = "Master Ravager";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
        this.mob.setHealth(500);
        this.updateVisibleName();
        PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 36000, 0, false, false);
        this.mob.addPotionEffect(potion);
        this.mob.setRemoveWhenFarAway(false);
    }
}
