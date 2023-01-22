package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class BlockStockSign extends DungeonSign{
    
    private int stockId;
    private Block block;

    public BlockStockSign(Sign sign, Game game) {
        super(sign, game);
        try {
            this.stockId = Integer.parseInt(this.sign.getLine(1).trim());
            this.block = (new Location(this.location.getWorld(), this.location.getBlockX(), this.location.getBlockY() - 1, this.location.getBlockZ())).getBlock();
        }catch(Exception e) {
            SevenDungeons.log("BlockStock's second line is wrong or empty.");
        }
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public int getId() {
        return this.stockId;
    }

}
