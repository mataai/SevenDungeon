package seven.dungeons.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import seven.dungeons.Game;

public class GemSign extends ActivableSign{

    private String name;
    private Item gem;
    private boolean out;
    
    public GemSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX()+0.5);
        this.location.setZ(this.location.getZ()+0.5);
        this.name =  this.game.getGameScore().getNbGems() + "g";
    }
    
    @Override
    public void on() {
        if (this.out)
            return; 
        this.out = true;
        Material material = this.game.getMaterial();
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        this.gem = this.game.getWorld().dropItem(this.location, item);
        this.gem.setVelocity(new Vector(0,0,0));
    }
    
    @Override
    public void off() {
        if(this.gem != null && this.gem.isOnGround()) {
            this.gem.remove();
        }
    }

}
