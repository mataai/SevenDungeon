package seven.dungeons.mobs;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.SpawnMobSign;

public class PumpkinBomber extends SevenMob {

    private int cooldown = 12;
    private SpawnMobSign sms;
    private double lastHealth;

    public PumpkinBomber(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Pumpkin Bomb";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(true);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
        this.mob.setHealth(300);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.mob.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
        PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 36000, 0, false, false);
        this.mob.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 36000, 0, false, false));
        this.mob.addPotionEffect(potion);
    }
    
    @Override
    public void spell() {
        if(this.cooldown > 0) {
            this.cooldown -= 1;
            this.lastHealth = this.mob.getHealth();
            return;
        }
        if(this.cooldown == 0){
            this.mobName = "§cPumpkin Bomb";
        }
        if(this.mob.getHealth() < this.lastHealth){
            for(LivingEntity le : this.sms.getLocation().getWorld().getLivingEntities()) {
                if(le.getLocation().distance(this.mob.getLocation()) < 3) {
                    if(le instanceof Player){
                        le.damage(20);
                    }
                    else{
                        le.setHealth(Math.max(0, le.getHealth() - 20));
                    }
                }
            }
            this.mob.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.mob.getLocation().clone().add(0,0.5,0), 3);
            this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3f);
            this.mob.remove();
        }

    }
}
