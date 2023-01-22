package seven.dungeons.signs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.metadata.FixedMetadataValue;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class BlockSign extends ActivableSign {
    
    private int blockId, defaultBlockId;
    private Location blockLocation;
    private boolean breakable;
    private FixedMetadataValue bmv = new FixedMetadataValue(this.game.getPlugin(), "bmv");

    public BlockSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] blockIds = this.sign.getLine(2).trim().split(" ");
            this.blockId = Integer.parseInt(blockIds[0]);
            this.defaultBlockId = Integer.parseInt(blockIds[1]);
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.equals("")) {
                this.blockLocation = this.location;
            }
            else
            {
                String[] splitLine4 = line4.trim().split(" ");
                this.blockLocation = new Location(this.location.getWorld(), Integer.parseInt(splitLine4[0]), Integer.parseInt(splitLine4[1]), Integer.parseInt(splitLine4[2]));
            }
            this.breakable = false;
            String[] line2 = this.sign.getLine(1).trim().split(" ");
            if(line2.length > 1) {
                this.breakable = true;
            }
        }catch(Exception e) {
            SevenDungeons.log("BlockSign's third line is wrong or empty at " + this.location);
        }
    }
    
    @Override
    public void on() {
        Block block = this.game.getBlockStock(this.blockId);
        if (block == null) {
          this.blockLocation.getBlock().setType(Material.AIR);
          return;
        } 
        replaceBlock(block, this.blockLocation, false);
    }
    
    @Override
    public void off() {
        Block block = this.game.getBlockStock(this.defaultBlockId);
        if (block == null) {
          this.blockLocation.getBlock().setType(Material.AIR);
          return;
        } 
        Block block2 = this.game.getBlockStock(this.blockId);
        boolean door = (block2 != null && block2.getType().toString().contains("_DOOR"));
        replaceBlock(block, this.blockLocation, door);
    }
    
    public void replaceBlock(Block b, Location loc, boolean door) {
        boolean isDoor = loc.getBlock().getType().toString().contains("_DOOR");
        loc.getBlock().setType(b.getType());
        loc.getBlock().setBlockData(b.getBlockData());
        if (isDoor || door) {
          Location l = loc.clone().add(0.0D, 1.0D, 0.0D);
          l.getBlock().setType(b.getType());
          l.getBlock().setBlockData(b.getBlockData());
        } 
        if (b.getType().toString().contains("SIGN")) {
          Sign s = (Sign)b.getState();
          String[] lines = s.getLines();
          for (int i = 0; i < 4; i++) {
            Sign s2 = (Sign)loc.getBlock().getState();
            s2.setLine(i, lines[i]);
            s2.update();
          } 
        }
        if(this.breakable) {
            loc.getBlock().setMetadata("bmv", this.bmv);
        }
        if (b.getType().toString().contains("_DOOR")) {
          Block doorUp = loc.getBlock().getRelative(BlockFace.UP);
          Block doorBottom = doorUp.getRelative(BlockFace.DOWN);
          BlockData blockData = Bukkit.getServer().createBlockData(b.getType());
          Bisected bisected = (Bisected)blockData;
          bisected.setHalf(Bisected.Half.TOP);
          doorUp.setBlockData((BlockData)bisected, false);
          doorUp.setBlockData(b.getBlockData());
          bisected.setHalf(Bisected.Half.BOTTOM);
          doorBottom.setBlockData((BlockData)bisected, false);
          BlockData bd = b.getBlockData().clone();
          ((Door)bd).setHalf(Bisected.Half.BOTTOM);
          doorBottom.setBlockData(bd);
        } 
      }

}
