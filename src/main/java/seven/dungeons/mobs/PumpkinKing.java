package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class PumpkinKing extends SevenMob {

    private double cooldown;
    private FixedMetadataValue no_knockback = new FixedMetadataValue (SevenDungeons.getPlugin(), "noknockback");
    private SpawnMobSign sms;
    private ArrayList<SevenMob> babies = new ArrayList<SevenMob>();
    private ArrayList<Location> babiesLastLocation = new ArrayList<Location>();
    private ArrayList<SevenMob> healers = new ArrayList<SevenMob>();
    private ArrayList<Location> pumpkins = new ArrayList<Location>();

    public PumpkinKing(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.WITHER_SKELETON;
        this.mobName = "Pumpking King";
        this.hasMagic = true;
        this.cooldown = 30;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        this.mob.setHealth(100);
        this.mob.setInvulnerable(true);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.CARVED_PUMPKIN);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.setMetadata("noknockback", this.no_knockback);
        PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, 36000, 0, true);
        this.mob.addPotionEffect(potion);
        int x1 = this.mob.getLocation().getBlockX() - 17;
        int x2 = x1 + 35;
        int z1 = this.mob.getLocation().getBlockZ() - 17;
        int z2 = z1 + 35;
        int y1 = this.mob.getLocation().getBlockY();
        int y2 = y1 + 3;
        World w = this.mob.getWorld();
        for(int x = x1; x <= x2; x++){
            for(int y = y1; y <= y2; y++){
                for(int z = z1; z <= z2; z++){
                    Location l = new Location(w,x,y,z);
                    if(l.getBlock().getType() == Material.CARVED_PUMPKIN){
                        this.pumpkins.add(l);
                    }
                }
            }
        }
    }
    
    @Override
    public void kill() {
        for(SevenMob sm : this.babies) {
            sm.mob.remove();
        }
        this.babies.clear();
        this.babiesLastLocation.clear();
        for(SevenMob sm : this.healers) {
            sm.mob.remove();
        }
        this.healers.clear();
        this.mob.remove();
    }

    @Override
    public void death(){
        for(SevenMob sm : this.babies) {
            sm.mob.remove();
        }
        this.babies.clear();
        this.babiesLastLocation.clear();
        for(SevenMob sm : this.healers) {
            sm.mob.remove();
        }
        this.healers.clear();
        this.mob.remove();
    }

    @Override
    public void spell() {
        this.cooldown -= 0.25;

        for(int i = this.babies.size() - 1; i >= 0; i--) {
            if(this.babies.get(i).mob != null && !this.babies.get(i).isDead()) {
                this.babies.get(i).updateVisibleName();
                this.babies.get(i).spell();
            }
            else {
                this.babies.remove(i);
            }
        }
        for(int i = this.healers.size() - 1; i >= 0; i--){
            if(this.healers.get(i).mob != null && !this.healers.get(i).isDead()){
                this.healers.get(i).updateVisibleName();
                this.healers.get(i).spell();
            }
            else{
                this.healers.remove(i);
            }
        }

        if(this.cooldown >= 17){
            if(this.cooldown == 19.75){
                this.mob.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            }
            DustOptions du = new DustOptions(Color.ORANGE, 0);
            for(Location l : this.pumpkins){
                this.sms.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, l.clone().add(0.5, 1, 0.5), 3, 0.25, 0, 0.25, 0);
                for(Player p : this.sms.getGame().getPlayers()){
                    if(l.distance(p.getLocation()) < 5){
                        this.sms.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, p.getEyeLocation(), 1, 0.25, 0.25, 0.25, 0);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 0.5f, Math.max(0,(float)(1 + (20 - this.cooldown) / 3.0)));
                        Location middlePumpkin = l.clone().add(0.5, 0.5, 0.5);
                        Vector direction = p.getLocation().clone().add(0, 1,0).add(middlePumpkin.clone().multiply(-1)).toVector().normalize().multiply(0.33);
                        for(int j = 0; j < middlePumpkin.distance(p.getLocation()) * 3; j++){
                            this.sms.getLocation().getWorld().spawnParticle(Particle.REDSTONE, middlePumpkin.add(direction), 1, 0, 0, 0, 1, du);
                        }
                        continue;
                    }
                }
            }

        }
        if(this.cooldown == 17){
            this.mob.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
            for(Location l : this.pumpkins) {
                for (Player p : this.sms.getGame().getPlayers()) {
                    if (l.distance(p.getLocation()) < 5) {
                        if(p.hasPotionEffect(PotionEffectType.WITHER)){
                            p.removePotionEffect(PotionEffectType.WITHER);
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0, false, false));
                        if(p.hasPotionEffect(PotionEffectType.BLINDNESS)){
                            p.removePotionEffect(PotionEffectType.BLINDNESS);
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 8, 0, false, false));
                        continue;
                    }
                }
            }
        }

        if(this.cooldown == 12){
            SevenMob bb1 = new SkeletonHealer(this.sms);
            new SpawnTask(bb1, this.sms.getLocation()).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            this.healers.add(bb1);
        }

        if(this.cooldown == 3){
            SevenMob bb1 = new PumpkinBomber(this.sms);
            new SpawnTask(bb1, this.sms.getLocation().clone().add(18, 2,0)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            this.babies.add(bb1);
            SevenMob bb2 = new PumpkinBomber(this.sms);
            new SpawnTask(bb2, this.sms.getLocation().clone().add(-18, 2,0)).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            this.babies.add(bb2);
        }

        if(this.cooldown <= 0){
            this.cooldown = 20;
        }
    }
}
