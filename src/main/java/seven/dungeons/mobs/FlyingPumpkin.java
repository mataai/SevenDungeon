package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyingPumpkin extends SevenMob {

    public FlyingPumpkin() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Flying Pumpkin";
        this.hasMagic = false;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        PotionEffect potion2 = new PotionEffect(PotionEffectType.INVISIBILITY, 36000, 0, true);
        this.mob.addPotionEffect(potion2);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5);
        this.mob.setHealth(5);
        ((Zombie)this.mob).setBaby(false);
        ItemStack pumpkin = new ItemStack(Material.PUMPKIN, 1);
        this.mob.getEquipment().setHelmet(pumpkin);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
}
