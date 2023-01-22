package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeakRavager extends SevenMob {

    public WeakRavager() {
        this.entityType = EntityType.RAVAGER;
        this.mobName = "Weak Ravager";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        this.mob.setHealth(50);
        this.updateVisibleName();
        PotionEffect potion = new PotionEffect(PotionEffectType.WEAKNESS, 36000, 0, true);
        this.mob.addPotionEffect(potion);
        this.mob.setRemoveWhenFarAway(false);
    }
}
