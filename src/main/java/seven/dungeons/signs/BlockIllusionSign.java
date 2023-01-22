package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class BlockIllusionSign extends CaptorSign {
    
    private int x1, x2, y1, y2, z1, z2;
    private double radius;
    private ArrayList<Material> blockMaterial = new ArrayList<Material>();
    private ArrayList<BlockData> blockData = new ArrayList<BlockData>();
    private ArrayList<Location> locations = new ArrayList<Location>();
    private Material material;

    public BlockIllusionSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] distances = this.sign.getLine(2).trim().split(" ");
            if (distances[0].substring(distances[0].length() - 1).equals("s")) {
                this.z1 = this.sign.getZ();
                this.z2 = this.z1 + Integer.parseInt(distances[0].substring(0, distances[0].length() - 1));
              } else {
                this.z2 = this.sign.getZ();
                this.z1 = this.z2 - Integer.parseInt(distances[0].substring(0, distances[0].length() - 1));
              } 
              if (distances[1].substring(distances[1].length() - 1).equals("e")) {
                this.x1 = this.sign.getX();
                this.x2 = this.x1 + Integer.parseInt(distances[1].substring(0, distances[1].length() - 1));
              } else {
                this.x2 = this.sign.getX();
                this.x1 = this.x2 - Integer.parseInt(distances[1].substring(0, distances[1].length() - 1));
              } 
              if (distances[2].substring(distances[2].length() - 1).equals("u")) {
                this.y1 = this.sign.getY();
                this.y2 = this.y1 + Integer.parseInt(distances[2].substring(0, distances[2].length() - 1));
              } else {
                this.y2 = this.sign.getY();
                this.y1 = this.y2 - Integer.parseInt(distances[2].substring(0, distances[2].length() - 1));
              }
            this.material = Material.AIR;
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            if(line4.length > 1) {
                this.material = Material.BARRIER;
            }
            this.radius = Double.parseDouble(line4[0]);
        }catch(Exception e) {
            SevenDungeons.log("BlockIllusion's third and/or fourth line is empty or wrong.");
        }
    }
    
    @Override
    public void isTriggered() {
        for(Player p : this.game.getPlayers()) {
            Location l = p.getLocation();
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            if(y < this.y1-radius || y > this.y2+radius || x < this.x1-radius || x > this.x2+radius || z < this.z1-radius || z > this.z2+radius) {
                continue;
            }
            this.trigger(null);
            return;
        }
        
    }
    
    @Override
    public void on() {
        this.game.getSigns2ticks().add(this);
        World world = this.game.getWorld();
        for (int x = this.x1; x <= this.x2; x++) {
          for (int y = this.y1; y <= this.y2; y++) {
            for (int z = this.z1; z <= this.z2; z++) {
              Block block = (new Location(world, x, y, z)).getBlock();
              if (block.getType() != Material.AIR) {
                this.locations.add(new Location(world, x, y, z));
                this.blockMaterial.add(block.getType());
                this.blockData.add(block.getBlockData());
              } 
            } 
          } 
        }
    }
    
    @Override
    public void off() {
        this.game.getSigns2ticks().remove(this);
        for (int i = 0; i < this.locations.size(); i++) {
          Material m = this.blockMaterial.get(i);
          Block block = ((Location)this.locations.get(i)).getBlock();
          block.setType(m);
          block.setBlockData(this.blockData.get(i));
        } 
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        for (int i = 0; i < this.locations.size(); i++) {
            float dist = Float.MAX_VALUE;
            for (Player p : this.game.getPlayers()) {
              float tempDist = (float)p.getLocation().distance(this.locations.get(i));
              if (tempDist < dist)
                dist = tempDist; 
            } 
            if (dist < this.radius + 1.75 && dist > this.radius - 1.75) {
              Block block = ((Location)this.locations.get(i)).getBlock();
              if (dist > this.radius && block.getType() != this.blockMaterial.get(i)) {
                Material m = this.blockMaterial.get(i);
                block.setType(m);
                block.setBlockData(this.blockData.get(i));
              } else if (dist <= this.radius && block.getType() == this.blockMaterial.get(i)){
                block.setType(this.material);
              } 
            } 
          }
    }
}
