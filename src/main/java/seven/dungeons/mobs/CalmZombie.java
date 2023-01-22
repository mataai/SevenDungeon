package seven.dungeons.mobs;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CalmZombie extends SevenMob {

    private boolean isAngry;

	public CalmZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Calm Zombie";
        this.hasMagic = true;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25);
        this.mob.setHealth(25);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
        this.mob.getEquipment().setItemInMainHand(sword);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.fromRGB(146,154,222));
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
        this.isAngry = false;
        this.mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3600, 2, false, false));
        this.mob.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 1, false, false));
    }

    @Override
    public void spell() {
        if(!this.isAngry && this.mob.getHealth() < 25){
            this.isAngry = true;
            this.mob.removePotionEffect(PotionEffectType.SLOW);
            this.mob.removePotionEffect(PotionEffectType.WEAKNESS);
            this.mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 3, false, false));
            this.mob.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3600, 0, false, false));
            this.mobName = "Angry Zombie";
            ItemStack sword = this.mob.getEquipment().getItemInMainHand();
            sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
            this.mob.getEquipment().setItemInMainHand(sword);
            LeatherArmorMeta meta = (LeatherArmorMeta) this.mob.getEquipment().getHelmet().getItemMeta();
            meta.setColor(Color.fromRGB(184,6,0));
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
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
        }
    }
}
