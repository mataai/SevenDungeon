package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class SpawnItemSign extends ActivableSign {
    
    private int chestId;
    private ItemStack item;

    public SpawnItemSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
        try {
            String line3 = this.sign.getLine(2).trim();
            this.chestId = Integer.parseInt(line3);
        }catch(Exception e) {
            SevenDungeons.log("SpawnItem's third line is wrong or empty.");
        }
    }
    
    @Override
    public void on() {
        ArrayList<ChestStockSign> chests = this.game.getChestStock(this.chestId);
        int total = 0;
        for(ChestStockSign css : chests) {
            total += css.getRate();
        }
        int random = (int) Math.ceil(total * Math.random());
        for(ChestStockSign css : chests) {
            random -= css.getRate();
            if(random <= 0) {
                this.item = css.getInventory().clone()[0];
                if(this.item != null) {
                    Item item = this.game.getWorld().dropItem(this.location, this.item);
                    item.setVelocity(new Vector(0,0,0));
                }
                return;
            }
        }
    }
    
}
