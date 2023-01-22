package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import seven.dungeons.Game;

public class ChestStockSign extends DungeonSign {
    
    private int stockId;
    private int rate;
    private ItemStack[] chestContent;

    public ChestStockSign(Sign sign, Game game) {
        super(sign, game);
        /*try {*/
            this.stockId = Integer.parseInt(this.sign.getLine(1).trim());
            String line3 = this.sign.getLine(2);
            if(line3 == null || line3.equals("")) {
                this.rate = 1;
            }
            else {
                this.rate = Integer.parseInt(this.sign.getLine(2).trim());
            }
            Block block = (new Location(this.location.getWorld(), this.location.getBlockX(), this.location.getBlockY() - 1, this.location.getBlockZ())).getBlock();
            this.chestContent = ((Chest)block.getState()).getBlockInventory().getContents();
        /*}catch(Exception e) {
            
        }*/
    }
    
    public int getRate() {
        return this.rate;
    }
    
    public int getId() {
        return this.stockId;
    }
    
    public ItemStack[] getInventory() {
        return this.chestContent;
    }

}
