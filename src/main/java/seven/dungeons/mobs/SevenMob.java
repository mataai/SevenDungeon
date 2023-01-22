package seven.dungeons.mobs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public abstract class SevenMob {
    protected LivingEntity mob;
    protected EntityType entityType;
    protected String mobName;
    protected boolean hasMagic;
    
    public boolean isDead() {
        if(this.mob == null) {
            return false;
        }
        return this.mob.isDead();
    }
    
    public void kill() {
        this.mob.remove();
    }
    
    public EntityType getEntityType() {
        return this.entityType;
    }
    
    public void costumize(LivingEntity entity) {
    }
    
    public void updateVisibleName() {
        if(this.mob == null) {
            return;
        }
        this.mob.setCustomName(this.mobName + ChatColor.RED + " ❤ " + (int)Math.ceil(this.mob.getHealth()));
        this.mob.setCustomNameVisible(true);
    }
    
    public String getMobName() {
        return this.mobName;
    }
    
    public boolean hasMagic() {
        return this.hasMagic;
    }
    
    public void spell() {
        return;
    }

    public void death(){return;}
    
    public void emptyInventory() {
    	this.mob.getEquipment().setItemInMainHand(null);
    	this.mob.getEquipment().setHelmet(null);
    	this.mob.getEquipment().setBoots(null);
    	this.mob.getEquipment().setChestplate(null);
    	this.mob.getEquipment().setLeggings(null);
    	this.mob.getEquipment().setItemInOffHand(null);
        this.mob.getEquipment().clear();
    }
    
    public LivingEntity getMob() {
        return this.mob;
    }

    public void setMobName(String name){
        this.mobName = name;
    }
}
