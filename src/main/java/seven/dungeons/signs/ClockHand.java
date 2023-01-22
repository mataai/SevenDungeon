package seven.dungeons.signs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

import java.util.ArrayList;

public class ClockHand extends CaptorSign {

    private int radius;
    private float speed;
    private int blockId;
    private double rotation;
    private float cooldown;

    public ClockHand(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.radius = Integer.parseInt(line3[0]);
            this.speed = Float.parseFloat((line3[1]));
            this.blockId = Integer.parseInt(this.sign.getLine(3));
        }catch(Exception e) {
            SevenDungeons.log("Fan's third and/or fourth is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        this.clearBlocks();
    }
    
    @Override
    public void isTriggered() {
        this.cooldown += 0.25;
        if(this.cooldown >= this.speed){
            // remove all blocks
            this.clearBlocks();

            // change blocks
            Block block = this.game.getBlockStock(this.blockId);
            this.rotation = this.rotation + Math.PI / ((this.radius + 0.5) * 2 * Math.PI - 5) % (2 * Math.PI);
            Location middle = this.location.clone().add(0.5, 0, 0.5);
            Vector direction = new Vector(Math.cos(this.rotation), 0, Math.sin(this.rotation)).normalize();
            for(int i = 1; i <= this.radius; i++){
                Location l = middle.clone().add(direction.clone().multiply(i));
                Block b = l.getBlock();
                b.setType(block.getType());
                b.setBlockData(block.getBlockData());
            }

            this.cooldown = 0;
        }
    }

    public void clearBlocks(){
        int x1 = this.location.getBlockX() - this.radius;
        int x2 = this.location.getBlockX() + this.radius;
        int z1 = this.location.getBlockZ() - this.radius;
        int z2 = this.location.getBlockZ() + this.radius;
        int y = this.location.getBlockY();
        World w = this.location.getWorld();
        for(int x = x1; x <= x2; x++){
            for(int z = z1; z <= z2; z++){
                if(x == this.location.getBlockX() && z == this.location.getBlockZ()){
                    continue;
                }
                Location l = new Location(w,x,y,z);
                l.getBlock().setType(Material.AIR);
            }
        }
    }

}
