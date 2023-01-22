package seven.dungeons.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class GuardianQueen extends SevenMob {

    private ArrayList<SevenMob> babies = new ArrayList<SevenMob>();
    private double cooldown;
    private SpawnMobSign sms;

    public GuardianQueen(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.ELDER_GUARDIAN;
        this.mobName = "Guardian Queen";
        this.hasMagic = true;
        this.cooldown = 15;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
        this.mob.setHealth(200);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
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
        for(int i = this.babies.size() - 1; i >= 0; i--) {
            if(!this.babies.get(i).isDead()) {
                this.babies.get(i).updateVisibleName();
                this.babies.get(i).spell();
            }
            else {
                this.babies.remove(i);
                i--;
            }
        }

        this.cooldown -= 0.25;
        if(this.mob.isInWater()){
            this.mob.setInvulnerable(true);
        }
        else{
            this.mob.setInvulnerable(false);
        }

        if(this.cooldown <= 0){
            this.cooldown = 15;
            for(int i = 0; i < 3; i++){
                SevenMob bb1 = new ArmoredGuardian();
                new SpawnTask(bb1, this.mob.getEyeLocation()).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
                this.babies.add(bb1);
            }
        }

    }
}
