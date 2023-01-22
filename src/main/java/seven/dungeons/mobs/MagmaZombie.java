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

public class MagmaZombie extends SevenMob {

	public MagmaZombie() {
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Magma Zombie";
        this.hasMagic = true;
    }

    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.emptyInventory();
        ((Zombie)this.mob).setBaby(false);
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        this.mob.setHealth(40);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD, 1);
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        this.mob.getEquipment().setItemInMainHand(sword);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.fromRGB(128,0,0));
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

    @Override
    public void spell() {
        if(this.mob.isInWater()){
            this.mob.setInvulnerable(false);
        }
        else{
            this.mob.setInvulnerable(true);
        }
    }
}
