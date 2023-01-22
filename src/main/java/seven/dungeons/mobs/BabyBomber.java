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

public class BabyBomber extends SevenMob {
    
    int cooldown = 12;
    private FixedMetadataValue no_explosion = new FixedMetadataValue (SevenDungeons.getPlugin(), "noExplosion");
    
    public BabyBomber() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Baby Bomber";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(true);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
        this.mob.setHealth(2);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.mob.getEquipment().setHelmet(new ItemStack(Material.TNT));
        PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 36000, 1, true);
        this.mob.addPotionEffect(potion);
        this.mob.setMetadata("noExplosion", this.no_explosion);
    }
    
    @Override
    public void spell() {
        if(this.cooldown > 0) {
            this.cooldown -= 1;
            return;
        }
        if(this.cooldown == 0){
            this.mobName = "§cBaby Bomber";
        }
        for(Player p : this.mob.getWorld().getPlayers()) {
            if(p.getLocation().distance(this.mob.getLocation()) < 2) {
                this.mob.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.mob.getLocation().clone().add(0,0.5,0), 3);
                this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.3f);
                p.damage(8);
                this.mob.remove();
            }
        }
    }
}
