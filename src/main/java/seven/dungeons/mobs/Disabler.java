package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.signs.SpawnMobSign;

public class Disabler extends SevenMob {

    private SpawnMobSign sms;

    public Disabler(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.SPIDER;
        this.mobName = "Disabler";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25);
        this.mob.setHealth(25);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
    
    @Override
    public void spell() {
        for(Player p : this.sms.getGame().getPlayers()){
            if(p.getLocation().distance(this.mob.getLocation()) < 4){
                p.setExp(0.01f);
                this.mob.getLocation().getWorld().spawnParticle(Particle.SPELL_WITCH, this.mob.getEyeLocation(), 5, 1, 1, 1, 1);
            }

        }
    }
}
