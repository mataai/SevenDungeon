package seven.dungeons.mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;
import java.util.HashMap;

public class BirdOfMinutes extends SevenMob {

    private SpawnMobSign sms;
    private ArrayList<SevenMob> babies = new ArrayList<SevenMob>();
    private ArrayList<FireZombie> fireZombies = new ArrayList<FireZombie>();
    private SevenMob soulEater = null;
    private SevenMob minute = null;
    private float currentSecond;
    private HashMap<Player,Location> playerLocations = new HashMap<Player,Location>();

    public BirdOfMinutes(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.PARROT;
        this.mobName = "Bird of Minutes";
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
        this.reset();
        for(Player p : this.sms.getGame().getPlayers()){
            this.playerLocations.put(p, p.getLocation());
        }
    }
    
    @Override
    public void kill() {
        this.clearSummons();
        this.mob.remove();
    }

    public void clearSummons(){
        for(SevenMob sm : this.babies) {
            sm.mob.remove();
        }
        for(SevenMob sm : this.fireZombies) {
            sm.mob.remove();
        }
        if(this.soulEater != null){
            this.soulEater.mob.remove();
            this.soulEater = null;
        }
        if(this.minute != null){
            this.minute.mob.remove();
            this.minute = null;
        }
        this.babies.clear();
        this.fireZombies.clear();
    }
    
    @Override
    public void spell() {
        if(this.currentSecond == 60){
            this.minute.getMob().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60);
            this.minute.getMob().setHealth(60);
            this.currentSecond -= 0.25f;
        }

        // Baisser la vie du master zombie
        else if(this.minute != null){
            this.minute.updateVisibleName();
            if(this.minute.mob.getHealth() < this.currentSecond){
                this.reset();
            }
            else{
                this.minute.mob.setHealth(this.currentSecond);
                this.currentSecond -= 0.25f;
            }

        }
        for(SevenMob sm : this.babies) {
            if (!sm.isDead()) {
                sm.updateVisibleName();
                sm.spell();
            }
        }

        for(FireZombie fz : this.fireZombies){
            if (!fz.isDead()) {
                fz.updateVisibleName();
                fz.spell();
            }
        }

        if(this.soulEater != null){
            if(!this.soulEater.isDead()){
                this.soulEater.updateVisibleName();
                this.soulEater.spell();
            }
            else{
                this.soulEater = null;
            }
        }

        if(this.currentSecond == 45){
            for(int i = 0; i < 12; i++){
                this.babies.add(new BabyBomber());
            }
            for(SevenMob sm : this.babies){
                new SpawnTask(sm, this.sms.getLocation().clone().add(0.5, -9, 0.5)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            }
        }
        else if(this.currentSecond == 30){
            for(int i = 0; i < 12; i++){
                this.fireZombies.add(new FireZombie());
            }
            for(SevenMob sm : this.fireZombies){
                new SpawnTask(sm, this.sms.getLocation().clone().add(0.5, -9, 0.5)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            }
        }
        else if(this.currentSecond == 15){
            this.soulEater = new SoulEater(this.sms);
            new SpawnTask(this.soulEater, this.sms.getLocation().clone().add(0.5, -8, 0.5)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
        }
        else if(this.currentSecond == 0){
            this.currentSecond = -1;
            this.mob.setInvulnerable(false);
            for(FireZombie fz : this.fireZombies){
                for(Location l : fz.getFires()){
                    l.getBlock().setType(Material.AIR);
                }
            }
            this.clearSummons();
            Location middle = this.sms.getLocation();
            for(int x = middle.getBlockX() - 1; x <= middle.getBlockX() + 1; x++){
                for(int z = middle.getBlockZ() - 1; z <= middle.getBlockZ() + 1; z++){
                    Location l = new Location(middle.getWorld(), x, middle.getBlockY() - 1, z);
                    l.getBlock().setType(Material.AIR);
                }
            }
            for(Player p : this.sms.getGame().getPlayers()){
                p.sendMessage("§aBird of Minutes §r: Look what you've done !");
            }
        }
    }

    public void reset(){
        for(Player p : this.playerLocations.keySet()){
            this.sms.getGame().healPlayer(p);
            p.teleport(this.playerLocations.get(p));
        }
        for(FireZombie fz : this.fireZombies){
            for(Location l : fz.getFires()){
                l.getBlock().setType(Material.AIR);
            }
        }
        this.clearSummons();
        this.currentSecond = 60;
        this.mob.getLocation().getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2, 2);

        this.minute = new MasterZombie();
        this.minute.setMobName("Longest Minute");
        new SpawnTask(this.minute, this.sms.getLocation().clone().add(0.5, -9, 0.5)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
    }
}
