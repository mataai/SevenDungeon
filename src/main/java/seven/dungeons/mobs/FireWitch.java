package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class FireWitch extends SevenMob {
	
	private SpawnMobSign sms;
    private ArrayList<Location> fires = new ArrayList<Location>();
    private int cooldown;

    public FireWitch(SpawnMobSign sms) {
        this.entityType = EntityType.WITCH;
        this.mobName = "Fire Witch";
        this.hasMagic = true;
        this.sms = sms;
        this.cooldown = 60;
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
        this.cooldown--;

        switch(this.cooldown){
            // Warning particles + Slowness
            case 40 :
                DustOptions du = new DustOptions(Color.ORANGE, 3);
                Location loc1 = this.mob.getLocation().clone();
                for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 24) {
                    loc1.getWorld().spawnParticle(Particle.REDSTONE, loc1.clone().add(Math.cos(i)*2.5,1,Math.sin(i)*2.5), 1, 0, 0, 0, 1, du);
                }
                PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 120, 10, false, false);
                if(this.mob.hasPotionEffect(PotionEffectType.SLOW)){
                    this.mob.removePotionEffect(PotionEffectType.SLOW);
                }
                this.mob.addPotionEffect(pe);
                break;
            // Fire
            case 36 :
                this.makeFire();
                break;
            // Extinguish + Remove Slowness
            case 16 :
                this.extinguish();
                break;

        }

        if(this.cooldown == 0){
            this.cooldown = 60;
        }
    }

    public void makeFire(){
        Location middle = this.mob.getLocation();
        float y = middle.getBlockY();
        World w = middle.getWorld();
        int x1 = middle.getBlockX() - 2;
        int x2 = x1 + 4;
        int z1 = middle.getBlockZ() - 2;
        int z2 = z1 + 4;
        Location l1 = new Location(w, x1, y, z1);
        Location l2 = new Location(w, x1, y, z2);
        for(int i = 0; i <= x2 - x1; i++){
            Location loc1 = l1.clone().add(i, 0, 0);
            if(loc1.getBlock().getType() == Material.AIR){
                this.fires.add(loc1);
                loc1.getBlock().setType(Material.FIRE);
            }
            Location loc2 = l2.clone().add(i, 0, 0);
            if(loc2.getBlock().getType() == Material.AIR){
                this.fires.add(loc2);
                loc2.getBlock().setType(Material.FIRE);
            }
        }
        Location l3 = new Location(w, x1, y, z1);
        Location l4 = new Location(w, x2, y, z1);
        for(int i = 0; i <= z2 - z1; i ++){
            Location loc1 = l3.clone().add(0, 0, i);
            if(loc1.getBlock().getType() == Material.AIR){
                this.fires.add(loc1);
                loc1.getBlock().setType(Material.FIRE);
            }
            Location loc2 = l4.clone().add(0, 0, i);
            if(loc2.getBlock().getType() == Material.AIR){
                this.fires.add(loc2);
                loc2.getBlock().setType(Material.FIRE);
            }
        }
    }

    public void extinguish(){
        for (Location l : this.fires){
            l.getBlock().setType(Material.AIR);
        }
        this.fires.clear();
    }
            
}
