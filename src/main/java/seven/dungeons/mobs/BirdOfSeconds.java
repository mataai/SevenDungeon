package seven.dungeons.mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;
import java.util.HashMap;

public class BirdOfSeconds extends SevenMob {

    private SpawnMobSign sms;
    private ArrayList<SevenMob> slimes = new ArrayList<SevenMob>();
    private ArrayList<SevenMob> disablers = new ArrayList<SevenMob>();
    private ArrayList<SevenMob> rewinders = new ArrayList<SevenMob>();
    private ArrayList<SevenMob> sorceresses = new ArrayList<SevenMob>();
    private SevenMob ravager = null;
    private Location hand;
    private int rotation;
    private boolean ended;
    private int four;

    public BirdOfSeconds(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.PARROT;
        this.mobName = "Bird of Seconds";
        this.hasMagic = true;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        ((Parrot)this.mob).setVariant(Parrot.Variant.GRAY);
        this.emptyInventory();
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
        this.mob.setHealth(1);
        this.mob.setInvulnerable(true);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        this.summonRavager();
        this.rotation = 0;
        this.ended = false;
        this.four = 3;
    }
    
    @Override
    public void kill() {
        this.clearSummons();
        this.mob.remove();
    }

    public void clearSummons(){
        this.clearBabies(this.slimes);
        this.clearBabies(this.disablers);
        this.clearBabies(this.rewinders);
        this.clearBabies(this.sorceresses);
    }

    public void clearBabies(ArrayList<SevenMob> babies){
        for(SevenMob sm : babies){
            if(!sm.isDead()){
                sm.mob.remove();
            }
        }
        babies.clear();
    }

    public void babySpells(ArrayList<SevenMob> babies){
        for(int i = babies.size() - 1; i >= 0; i--){
            SevenMob sm = babies.get(i);
            if (!sm.isDead()) {
                sm.updateVisibleName();
                if(sm.hasMagic){
                    sm.spell();
                }
            }
            else{
                babies.remove(i);
            }
        }

    }

    public void spawnAll(ArrayList<SevenMob> babies){
        for(SevenMob sm : babies){
            new SpawnTask(sm, this.sms.getLocation().clone().add(Math.random() * 2, -9, Math.random() * 2)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);

        }
    }
    
    @Override
    public void spell() {
        this.four++;
        if(this.four == 4){
            this.four = 0;
            if(this.hand != null){
                for(int i = 0; i < 5; i++){
                    this.hand.clone().add(0, i, 0).getBlock().setType(Material.AIR);
                }
            }
            double angle = (Math.PI * 2 / 104) * this.rotation;
            this.hand = new Location(this.mob.getWorld(), this.sms.getLocation().getBlockX() + Math.cos(angle) * 16,
                    this.sms.getLocation().getBlockY() - 8,
                    this.sms.getLocation().getBlockZ() + Math.sin(angle) * 16);
            for(int i = 0; i < 5; i++){
                this.hand.clone().add(0, i, 0).getBlock().setType(Material.GOLD_BLOCK);
            }
            this.rotation++;
        }

        if(!this.ravager.isDead()){
            this.ravager.mob.setInvulnerable(this.ravager.mob.getLocation().distance(this.hand) > 5);
            this.ravager.updateVisibleName();
            this.babySpells(this.disablers);
            this.babySpells(this.rewinders);
            this.babySpells(this.sorceresses);
            this.babySpells(this.slimes);

            switch(this.rotation){
                case 26 :
                        this.clearBabies(this.sorceresses);
                        for(int i = 0; i < 3; i++){
                            this.sorceresses.add(new Sorceress(this.sms));
                        }
                        this.spawnAll(this.sorceresses);

                    break;
                case 52 :
                        this.clearBabies(this.rewinders);
                        for(int i = 0; i < 6; i++){
                            this.rewinders.add(new Rewinder(this.sms));
                        }
                        this.spawnAll(this.rewinders);

                    break;
                case 78 :
                        this.clearBabies(this.disablers);
                        for(int i = 0; i < 9; i++){
                            this.disablers.add(new Disabler(this.sms));
                        }
                        this.spawnAll(this.disablers);

                    break;
                case 103 :
                        this.clearBabies(this.slimes);
                        for(int i = 0; i < 12; i++){
                            this.slimes.add(new BabySlime());
                        }
                        this.spawnAll(this.slimes);
                        this.rotation = 0;
                        break;
            }
        }
        else if(!this.ended){
            this.mob.setInvulnerable(false);
            this.ended = true;
            this.clearSummons();
            Location middle = this.sms.getLocation();
            for(int x = middle.getBlockX() - 1; x <= middle.getBlockX() + 1; x++){
                for(int z = middle.getBlockZ() - 1; z <= middle.getBlockZ() + 1; z++){
                    Location l = new Location(middle.getWorld(), x, middle.getBlockY() - 1, z);
                    l.getBlock().setType(Material.AIR);
                }
            }
        }

    }

    public void summonRavager(){
        this.ravager = new MasterRavager();
        new SpawnTask(this.ravager, this.sms.getLocation().clone().add(0.5, -9, 0.5)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
    }
}
