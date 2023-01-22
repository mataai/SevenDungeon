package seven.dungeons.mobs;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class PumpkinMage extends SevenMob {
    
    private ArrayList<LivingEntity> babies = new ArrayList<LivingEntity>();
    private float cooldown;
    private float lastHealth;
    
    public PumpkinMage() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Pumpkin Mage";
        this.hasMagic = true;
        this.cooldown = 5;
        this.lastHealth = 40;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        ItemStack pumpkin = new ItemStack(Material.PUMPKIN, 1);
        this.mob.getEquipment().setHelmet(pumpkin);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        this.mob.setHealth(40);
        this.lastHealth = (float) this.mob.getHealth();
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }

    @Override
    public void spell() {
        this.cooldown -= 0.25;
        boolean abd = this.allBabiesDead();
        if(!abd) {
            if(this.lastHealth > this.mob.getHealth()) {
                this.mob.setHealth(this.lastHealth);
            }
        }
        else {
            this.lastHealth = (int)this.mob.getHealth();
        }
        if(this.cooldown <= 0 && abd) {
            this.cooldown = 12;
            Location l = this.mob.getLocation();
            this.babies.add((LivingEntity) l.getWorld().spawnEntity(l, EntityType.ZOMBIE));
            this.babies.add((LivingEntity) l.getWorld().spawnEntity(l, EntityType.ZOMBIE));
            
            
            for(int i=0; i < 5; i++) {
                l.getWorld().spawnParticle(Particle.SPELL_INSTANT,l, 10, 2, 2, 2);
            }
            for(LivingEntity le : this.babies) {
                ((Zombie)le).setBaby(true);
                ItemStack pumpkin = new ItemStack(Material.PUMPKIN, 1);
                le.getEquipment().setHelmet(pumpkin);
                le.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
                le.setHealth(2);
                le.setCustomName("Pumpkin Pawn");
                le.setCustomNameVisible(true);
            }
        }
    }
    
    public boolean allBabiesDead() {
        for(LivingEntity le : this.babies) {
            if(!le.isDead()) {
                return false;
            }
        }
        this.babies.clear();
        return true;
    }
}
