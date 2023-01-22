package seven.dungeons.mobs;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class NormalSlime extends SevenMob {

    private double lastHealth;
    private SpawnMobSign sms;
    private ArrayList<SevenMob> babies = new ArrayList<SevenMob>();

    public NormalSlime(SpawnMobSign sms) {
        this.entityType = EntityType.SLIME;
        this.mobName = "Slime";
        this.hasMagic = true;
        this.sms = sms;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        ((Slime)this.mob).setSize(2);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        this.mob.setHealth(20);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.lastHealth = 20;
    }

    @Override
    public void death() {
        for(SevenMob sm : this.babies) {
            sm.mob.remove();
        }
        this.babies.clear();
        this.mob.remove();
    }

    @Override
    public void kill() {
        for(SevenMob sm : this.babies) {
            sm.mob.remove();
        }
        this.babies.clear();
        this.mob.remove();
    }

    @Override
    public void spell() {
        for(int i = this.babies.size()-1; i >= 0; i--) {
            if(this.babies.get(i).mob != null && !this.babies.get(i).isDead()) {
                this.babies.get(i).updateVisibleName();
                this.babies.get(i).spell();
            }
            else {
                this.babies.remove(i);
            }
        }

        if(this.mob.getHealth() < this.lastHealth){
            this.lastHealth = this.mob.getHealth();
            if(this.babies.size() > 2){
                return;
            }
            SevenMob bb1 = new BabySlime();
            new SpawnTask(bb1, this.mob.getEyeLocation()).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            this.babies.add(bb1);

            //bb1.getMob().setVelocity(new Vector(Math.random() * 0.3, Math.random() * 0.3, Math.random() * 0.3));
            this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1, 0.75f);
            for(Player p : this.sms.getGame().getPlayers()){
                if(p.getLocation().distance(this.mob.getLocation()) < 3){
                    if(p.hasPotionEffect(PotionEffectType.SLOW)){
                        p.removePotionEffect(PotionEffectType.SLOW);
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8 * 20, 2, false, false));
                }
            }
        }
    }
}
