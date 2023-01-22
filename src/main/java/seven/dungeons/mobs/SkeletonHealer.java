package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class SkeletonHealer extends SevenMob {

    private float cooldown;
    private SpawnMobSign sms;

    public SkeletonHealer(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.SKELETON;
        this.mobName = "Undead Healer";
        this.hasMagic = true;
        this.cooldown = 10;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ItemStack bow = new ItemStack(Material.BOW, 1);
        this.mob.getEquipment().setItemInMainHand(bow);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        this.mob.setHealth(50);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.WHITE);
        helmet.setItemMeta(meta);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        chestplate.setItemMeta(meta);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        leggings.setItemMeta(meta);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
        this.mob.getEquipment().setChestplate(chestplate);
        this.mob.getEquipment().setLeggings(leggings);
        this.mob.getEquipment().setBoots(boots);
        this.mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 36000, 1, false, false));
    }
    
    @Override
    public void spell() {
        this.cooldown -= 0.25;

        if(this.cooldown >= 7) {
            // Suck up
            Location l = this.mob.getLocation();
            for(LivingEntity le : this.mob.getWorld().getLivingEntities()){
                if(!(le instanceof Player) && le.getLocation().distance(this.mob.getLocation()) < 10){
                    le.setVelocity(l.clone().subtract(le.getLocation()).toVector().multiply(0.15));
                }
            }
        }
        else{
            if(this.cooldown <= 0){
                this.cooldown = 10;
            }
            // Heal
            for(LivingEntity le : this.mob.getWorld().getLivingEntities()){
                if(!(le instanceof Player) && le.getLocation().distance(this.mob.getLocation()) < 5){
                    le.setHealth(Math.min(le.getHealth() + 0.5, le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                    this.mob.getWorld().spawnParticle(Particle.WAX_OFF, le.getEyeLocation(), 1, 0.5, 0.5, 0.5, 0);
                }
            }
        }
    }
    

}
