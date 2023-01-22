package seven.dungeons.mobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import seven.dungeons.signs.SpawnMobSign;

public class EarthZombie extends SevenMob {
    
    private SpawnMobSign sms;
    private float cooldown;
    private float launchTime;
    private boolean launchActive;
    
    public EarthZombie(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Earth Zombie";
        this.hasMagic = true;
        this.cooldown = 7;
        this.launchActive = false;
        this.launchTime = 0;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        this.mob.setHealth(50);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.GREEN);
        helmet.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(Color.GREEN);
        chestplate.setItemMeta(meta2);
        this.mob.getEquipment().setChestplate(chestplate);
    }
    
    @Override
    public void spell() {
    	this.cooldown -= 0.25;
    	if(this.launchActive) {
    		if(this.launchTime <= 0) {
    			if(this.mob.isOnGround()) {
    				this.launchActive = false;
    				for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 24) {
    		        	Location l = this.mob.getLocation().clone().add(Math.cos(i)*3,0.1,Math.sin(i)*3);
    		        	Material m = l.clone().add(0, -0.5, 0).getBlock().getType();
    		        	if(m.isSolid()) {
    		        		BlockData bd = m.createBlockData();
    		        		this.mob.getWorld().spawnParticle(Particle.BLOCK_DUST, l, 5, 0.2, 0, 0.2, 0, bd);
    		        		this.mob.getWorld().playSound(this.mob.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 0.5f, 0.8f);
    		        	}
    		        	 
    		        }
    		        this.mob.getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_GENERIC_BIG_FALL, 1.5f, 0.9f);
    		        
    		        for(Player p : this.mob.getWorld().getPlayers()) {
    		        	if(p.getLocation().distance(this.mob.getLocation()) < 3) {
    		        		p.damage(7);
    		        		Vector v = p.getLocation().toVector().add(this.mob.getLocation().clone().toVector().multiply(-1)).normalize().multiply(0.6);
    		        		v.add(new Vector(0, 0.3, 0));
    		        		p.setVelocity(v);
    		        	}
    		        }
    			}
    		}
    		else {
    			this.launchTime -= 0.25f;
    		}
    	}
    	if(this.cooldown <= 0) {
    		this.cooldown = 10;
    		
    		Vector launchVector = this.mob.getLocation().getDirection().normalize().multiply(0.5).setY(0.8);
            this.mob.setVelocity(launchVector);
            this.launchTime = 0.75f;
            this.launchActive = true;
    	}
    }
}
