package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeakWarden extends SevenMob {

    private boolean isAngry = false;

    public WeakWarden() {
        this.entityType = EntityType.WARDEN;
        this.mobName = "Warden";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(350);
        this.mob.setHealth(350);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3600, 0, false, false));
        this.mob.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 0, false, false));
    }

    @Override
    public void spell(){
        if(this.mob.getHealth() > 100 && !this.mob.hasPotionEffect(PotionEffectType.SLOW)){
            this.mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3600, 0, false, false));
        }
        if(!this.isAngry && this.mob.getHealth() < 100){
            this.isAngry = true;
            this.mob.removePotionEffect(PotionEffectType.SLOW);
            this.mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0, false, false));
        }
        if(this.mob.getHealth() < 100){
            this.mob.setHealth(this.mob.getHealth() + 0.5);
        }
        if(!this.mob.hasPotionEffect(PotionEffectType.WEAKNESS)){
            this.mob.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 0, false, false));
        }
    }
}
