package seven.dungeons.mobs;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import seven.dungeons.signs.SpawnMobSign;

public class GeminiZombie extends SevenMob {
    
    private float lastHealth;
    private SpawnMobSign sms;
    
    public GeminiZombie(SpawnMobSign sms) {
        this.sms = sms;
        this.entityType = EntityType.ZOMBIE;
        this.mobName = "Gemini Zombie";
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
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(Color.WHITE);
        helmet.setItemMeta(meta);
        this.mob.getEquipment().setHelmet(helmet);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
        meta2.setColor(Color.WHITE);
        chestplate.setItemMeta(meta2);
        this.mob.getEquipment().setChestplate(chestplate);
        this.lastHealth = 40;
    }
    
    @Override
    public void spell() {
        if(this.lastHealth > this.mob.getHealth()) {
            this.lastHealth = (int)this.mob.getHealth();
            SevenMob bb1 = new CadetZombie();
            new SpawnTask(bb1, this.mob.getLocation()).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
            SevenMob bb2 = new CadetZombie();
            new SpawnTask(bb2, this.mob.getLocation()).runTaskLater(this.sms.getGame().getPlugin(), (long) 0);
        }
    }
}
