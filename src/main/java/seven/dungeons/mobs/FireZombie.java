package seven.dungeons.mobs;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class FireZombie extends SevenMob {

    private ArrayList<Location> fires = new ArrayList<Location>();
    
    public FireZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Fire Zombie";
        this.hasMagic = true;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        this.mob.setHealth(20);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.RED);
        helmet.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(Color.RED);
        chestplate.setItemMeta(meta2);
        this.mob.getEquipment().setChestplate(chestplate);
        PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, 36000, 2, false, false);
        this.mob.addPotionEffect(potion);
        this.mob.setFireTicks(36000);
    }
    
    @Override
    public void spell() {
    	Location l = this.mob.getLocation().clone();
    	if(l.getBlock().getType() == Material.AIR) {
    		l.getBlock().setType(Material.FIRE);
            this.fires.add(l);
    	}
        if(this.fires.size() > 6){
            this.fires.get(0).getBlock().setType(Material.AIR);
            this.fires.remove(0);
        }
    }

    public ArrayList<Location> getFires(){
        return this.fires;
    }
}
