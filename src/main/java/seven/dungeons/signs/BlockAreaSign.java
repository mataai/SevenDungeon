package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class BlockAreaSign extends ActivableSign {
    
    private int x1, x2, y1, y2, z1, z2;
    private int blockId, replacedBlockId;
    private ArrayList<Block> toReplace = new ArrayList<Block>();

    public BlockAreaSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] distances = this.sign.getLine(2).trim().split(" ");
            if(distances[0].substring(distances[0].length() - 1).equals("s")) {
                this.z1 = this.sign.getZ();
                this.z2 = this.z1 + Integer.parseInt(distances[0].substring(0,distances[0].length() - 1));
            }
            else {
                this.z2 = this.sign.getZ();
                this.z1 = this.z2 - Integer.parseInt(distances[0].substring(0,distances[0].length() - 1));
            }
            if(distances[1].substring(distances[1].length() - 1).equals("e")) {
                this.x1 = this.sign.getX();
                this.x2 = this.x1 + Integer.parseInt(distances[1].substring(0,distances[1].length() - 1));
            }
            else {
                this.x2 = this.sign.getX();
                this.x1 = this.x2 - Integer.parseInt(distances[1].substring(0,distances[1].length() - 1));
            }
            if(distances[2].substring(distances[2].length() - 1).equals("u")) {
                this.y1 = this.sign.getY();
                this.y2 = this.y1 + Integer.parseInt(distances[2].substring(0,distances[2].length() - 1));
            }
            else {
                this.y2 = this.sign.getY();
                this.y1 = this.y2 - Integer.parseInt(distances[2].substring(0,distances[2].length() - 1));
            }
            String[] blocks = this.sign.getLine(3).trim().split(" ");
            this.blockId = Integer.parseInt(blocks[0]);
            this.replacedBlockId = -1;
            if(blocks.length > 1) {
                this.replacedBlockId = Integer.parseInt(blocks[1]);
            }
        }catch(Exception e) {
            SevenDungeons.log("BlockArea's third and/or fourth line is empty or wrong.");
        }
    }

    @Override
    public void on() {
        Block block = this.game.getBlockStock(this.blockId);
        if(block == null) {
            block = this.location.getBlock();
            block.setType(Material.AIR);
        }
        if(this.replacedBlockId == -1) {
            this.replaceBlocks(block);
            return;
        }
        Block replacedBlock = this.game.getBlockStock(this.replacedBlockId);
        if(replacedBlock == null) {
            replacedBlock = this.location.getBlock();
            replacedBlock.setType(Material.AIR);
        }
        this.replaceBlocks(block, replacedBlock);
    }
    
    @Override
    public void off() {
        if(this.replacedBlockId == -1) {
            return;
        }
        Block block = this.game.getBlockStock(this.replacedBlockId);
        if(block == null) {
            block = this.location.getBlock();
            block.setType(Material.AIR);
        }
        Block replacedBlock = this.game.getBlockStock(this.blockId);
        if(replacedBlock == null) {
            replacedBlock = this.location.getBlock();
            replacedBlock.setType(Material.AIR);
        }
        this.replaceBlocks(block, replacedBlock);
    }
    
    public void replaceBlocks(Block replacing, Block replaced) {
        World world = this.game.getWorld();
        Material type = replacing.getType();
        BlockData data = replacing.getBlockData();
        Material type2 = replaced.getType();
        BlockData data2 = replaced.getBlockData();
        for(int x = this.x1; x <= x2; x++) {
            for(int y = this.y1; y <= y2; y++) {
                for(int z = this.z1; z <= z2; z++) {
                    Block block = (new Location(world, x, y, z)).getBlock();
                    if(block.getType() == type2 && block.getBlockData().matches(data2)) {
                    	toReplace.add(block);
                    }
                }
            }
        }
        for(Block b : toReplace) {
        	b.setType(type);
            b.setBlockData(data);
        }
        toReplace.clear();
    }
    
    public void replaceBlocks(Block replacing) {
        World world = this.game.getWorld();
        Material type = replacing.getType();
        BlockData data = replacing.getBlockData();
        for(int x = this.x1; x <= x2; x++) {
            for(int y = this.y1; y <= y2; y++) {
                for(int z = this.z1; z <= z2; z++) {
                    Block block = (new Location(world, x, y, z)).getBlock();
                    block.setType(type);
                    block.setBlockData(data);
                }
            }
        }
    }
    
}
