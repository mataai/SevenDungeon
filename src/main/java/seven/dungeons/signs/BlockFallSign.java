package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.FallingBlock;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class BlockFallSign extends ActivableSign {
    
    int blockId;
    Location blockLocation;

    public BlockFallSign(Sign sign, Game game) {
        super(sign, game);
        try {
            this.blockId = Integer.parseInt(this.sign.getLine(2).trim());
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.equals("")) {
                this.blockLocation = this.location;
            }
            else
            {
                String[] splitLine4 = line4.trim().split(" ");
                this.blockLocation = new Location(this.location.getWorld(), Integer.parseInt(splitLine4[0]), Integer.parseInt(splitLine4[1]), Integer.parseInt(splitLine4[2]));
            }
            this.blockLocation.add(0.5,0,0.5);
        }catch(Exception e) {
            SevenDungeons.log("BlockSign's third line is wrong or empty.");
        }
        Block block = this.game.getBlockStock(this.blockId);
        this.blockLocation.getBlock().setType(block.getType());
        this.blockLocation.getBlock().setBlockData(block.getBlockData());
    }
    
    @Override
    public void on() {
        this.blockLocation.getBlock().setType(Material.AIR);
        Block block = this.game.getBlockStock(this.blockId);
        if(block == null) {
            return;
        }
        FallingBlock fb = this.game.getWorld().spawnFallingBlock(this.blockLocation, block.getBlockData());
        fb.setVelocity(fb.getVelocity().zero());
    }

}
