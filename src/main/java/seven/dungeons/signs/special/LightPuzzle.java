package seven.dungeons.signs.special;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

public class LightPuzzle extends CaptorSign {

    private int signal;
    private boolean polarity;
    private Block powerBlock;
    private boolean busy;
    private String initialFacing;
    private int facing;
    private Location currentLocation;
    private Location corner1;
    private Location corner2;
    
    public LightPuzzle(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] signalLine = this.sign.getLine(2).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
            this.initialFacing = this.sign.getLine(3).trim();
            this.powerBlock = new Location(this.location.getWorld(), this.location.getBlockX(), this.location.getBlockY() + 1, this.location.getBlockZ()).getBlock();
            this.busy = false;
            switch(this.initialFacing) {
            case "w": 
                this.corner1 = new Location(this.location.getWorld(), this.location.getX() - 30, this.location.getY(), this.location.getZ()-15);
                this.corner2 = new Location(this.location.getWorld(), this.location.getX() - 1, this.location.getY()+5, this.location.getZ()+15);
                break;
            case "e": 
                this.corner1 = new Location(this.location.getWorld(), this.location.getX() + 1, this.location.getY(), this.location.getZ()-15);
                this.corner2 = new Location(this.location.getWorld(), this.location.getX() + 30, this.location.getY()+5, this.location.getZ()+15);
                break;
            case "n":
                this.corner1 = new Location(this.location.getWorld(), this.location.getX() - 15, this.location.getY(), this.location.getZ()-30);
                this.corner2 = new Location(this.location.getWorld(), this.location.getX() + 15, this.location.getY()+5, this.location.getZ()-1);
                break;
            case "s":
                this.corner1 = new Location(this.location.getWorld(), this.location.getX() - 15, this.location.getY(), this.location.getZ()+1);
                this.corner2 = new Location(this.location.getWorld(), this.location.getX() + 15, this.location.getY()+5, this.location.getZ()+30);
                break;
            }
            this.reset();
        }catch(Exception e) {
            SevenDungeons.log("LightPuzzle's third and / or fourth line is wrong.");
        }
    }

    @Override
    public void on() {
        this.location.getBlock().setType(Material.SEA_LANTERN);
        for(int x = this.corner1.getBlockX(); x < this.corner2.getBlockX(); x++) {
            for(int y = this.corner1.getBlockY(); y < this.corner2.getBlockY(); y++) {
                for(int z = this.corner1.getBlockZ(); z < this.corner2.getBlockZ(); z++) {
                    Location location = new Location(this.location.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if(block.getType() == Material.EMERALD_BLOCK) {
                        block.setType(Material.POLISHED_DIORITE_STAIRS);
                        Directional bd = (Directional) block.getBlockData();
                        bd.setFacing(BlockFace.NORTH);
                        block.setBlockData(bd);
                    }
                    else if(block.getType() == Material.DIAMOND_BLOCK) {
                        block.setType(Material.POLISHED_DIORITE_STAIRS);
                        Directional bd = (Directional) block.getBlockData();
                        bd.setFacing(BlockFace.EAST);
                        block.setBlockData(bd);
                    }
                    else if(block.getType() == Material.GOLD_BLOCK) {
                        block.setType(Material.POLISHED_DIORITE_SLAB);
                        Slab bd = (Slab) block.getBlockData();
                        bd.setType(Type.BOTTOM);
                        block.setBlockData(bd);
                    }
                }
            }
            
        }
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        for(int x = this.corner1.getBlockX(); x <= this.corner2.getBlockX(); x++) {
            for(int y = this.corner1.getBlockY(); y < this.corner2.getBlockY(); y++) {
                for(int z = this.corner1.getBlockZ(); z <= this.corner2.getBlockZ(); z++) {
                    Location location = new Location(this.location.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if(block.getType() == Material.POLISHED_DIORITE_STAIRS || block.getType() == Material.POLISHED_DIORITE_SLAB) {
                        block.setType(Material.AIR);
                    }
                }
            }
            
        }
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        for(int x = this.corner1.getBlockX(); x < this.corner2.getBlockX(); x++) {
            for(int y = this.corner1.getBlockY(); y < this.corner2.getBlockY(); y++) {
                for(int z = this.corner1.getBlockZ(); z < this.corner2.getBlockZ(); z++) {
                    Location location = new Location(this.location.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if(block.getType() == Material.POLISHED_DIORITE_STAIRS) {
                        if(block.hasMetadata("xlp")) {
                            block.removeMetadata("xlp", this.game.getPlugin());
                            Directional bd = (Directional)block.getBlockData();
                            switch(bd.getFacing()) {
                            case WEST : bd.setFacing(BlockFace.EAST);break;
                            case EAST : bd.setFacing(BlockFace.WEST);break;
                            case NORTH : bd.setFacing(BlockFace.SOUTH);break;
                            default: bd.setFacing(BlockFace.NORTH);break;
                            }
                            block.setBlockData(bd);
                        }
                    }
                    else if(block.getType() == Material.POLISHED_DIORITE_SLAB) {
                        if(block.hasMetadata("xlp")) {
                            block.removeMetadata("xlp", this.game.getPlugin());
                            Slab bd = (Slab)block.getBlockData();
                            switch(bd.getType()) {
                            case BOTTOM : bd.setType(Type.TOP);break;
                            default: bd.setType(Type.BOTTOM);break;
                            }
                            block.setBlockData(bd);
                        }
                    }
                }
            }      
        }
        
        if(!this.busy && this.powerBlock.isBlockPowered()) {
            this.busy = true;
            this.game.getWorld().playSound(this.currentLocation, Sound.BLOCK_BEACON_ACTIVATE, 15, 1.2f);
        }
        else if (this.busy){
            switch(this.facing) {
            case 0: this.currentLocation.add(-1, 0, 0); break;
            case 1: this.currentLocation.add(1, 0, 0); break;
            case 2: this.currentLocation.add(0, 0, -1); break;
            case 3: this.currentLocation.add(0, 0, 1); break;
            case 4: this.currentLocation.add(0, 1, 0); break;
            case 5: this.currentLocation.add(0, -1, 0); break;
            }
            if(this.currentLocation.getBlockX() < this.corner1.getBlockX() || this.currentLocation.getBlockX() > this.corner2.getBlockX() ||
               this.currentLocation.getBlockY() < this.corner1.getBlockY() || this.currentLocation.getBlockY() > this.corner2.getBlockY() ||
               this.currentLocation.getBlockZ() < this.corner1.getBlockZ() || this.currentLocation.getBlockZ() > this.corner2.getBlockZ()) {
                this.game.getWorld().playSound(this.currentLocation, Sound.BLOCK_BEACON_DEACTIVATE, 15, 1.2f);
                this.reset();
            }
            Block block = this.currentLocation.getBlock();
            if(block.getType() == Material.POLISHED_DIORITE_STAIRS) {
                Directional direction = (Directional)block.getBlockData();
                switch(direction.getFacing()) {
                case WEST : this.facing = 1;break;
                case EAST : this.facing = 0;break;
                case NORTH : this.facing = 3;break;
                case SOUTH : this.facing = 2;break;
                default: break;
                }
            }
            else if(block.getType() == Material.POLISHED_DIORITE_SLAB) {
                Slab slab = (Slab)block.getBlockData();
                switch(slab.getType()) {
                case BOTTOM : this.facing = 4; break;
                default : this.facing = 5; break;
                }
            }
            else if(block.getType() == Material.OBSERVER) {
                this.reset();
                this.game.getWorld().playSound(this.currentLocation, Sound.ENTITY_PLAYER_LEVELUP, 15, 1.2f);
                this.trigger(null);
                return;
            }
            else if(block.getType() == Material.AIR || block.getType() == Material.WHITE_STAINED_GLASS) {
                this.currentLocation.getBlock().setType(Material.WHITE_STAINED_GLASS);
            }
            else {
                this.game.getWorld().playSound(this.currentLocation, Sound.BLOCK_BEACON_DEACTIVATE, 15, 1.2f);
                this.reset();
            }
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.off();
        if(this.polarity) {
            this.game.activate(signal, null);
            return;
        }
        this.game.deactivate(signal, null);
    }
    
    public void reset() {
        this.busy = false;
        for(int x = this.corner1.getBlockX(); x <= this.corner2.getBlockX(); x++) {
            for(int y = this.corner1.getBlockY(); y <= this.corner2.getBlockY(); y++) {
                for(int z = this.corner1.getBlockZ(); z <= this.corner2.getBlockZ(); z++) {
                    Location location = new Location(this.location.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if(block.getType() == Material.WHITE_STAINED_GLASS) {
                        block.setType(Material.AIR);
                    }
                }
            }     
        }
        switch(this.initialFacing) {
        case "w": this.facing = 0; break;
        case "e": this.facing = 1; break;
        case "n": this.facing = 2; break;
        case "s": this.facing = 3; break;
        }
        this.currentLocation = this.location.clone();
    }
}
