package seven.dungeons.signs.special;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

public class ArrowTrap extends CaptorSign {
    
    private int blockId;
    private boolean west = false, east = false, north = false, south = false;
    private float cooldown;
    private ArrayList<Arrow> arrows = new ArrayList<Arrow>();

    public ArrowTrap(Sign sign, Game game) {
        super(sign, game);
        try {
            blockId = Integer.parseInt(this.sign.getLine(2).trim());
        }catch(Exception e) {
            SevenDungeons.log("ArrowTrap's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.sign.getLocation().getBlock().setType(Material.LODESTONE);
        if(this.location.clone().add(-1,0,0).getBlock().getType().equals(Material.AIR)) {
            this.west = true;
        }
        if(this.location.clone().add(1,0,0).getBlock().getType().equals(Material.AIR)) {
            this.east = true;
        }
        if(this.location.clone().add(0,0,-1).getBlock().getType().equals(Material.AIR)) {
            this.north = true;
        }
        if(this.location.clone().add(0,0,1).getBlock().getType().equals(Material.AIR)) {
            this.south = true;
        }
        this.cooldown = 4;
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        Block block = this.game.getBlockStock(this.blockId);
        if (block == null) {
          this.sign.getLocation().getBlock().setType(Material.AIR);
          return;
        }
        else {
            this.sign.getLocation().getBlock().setType(block.getType());
            this.sign.getLocation().getBlock().setBlockData(block.getBlockData());
        }
    }
    
    @Override
    public void isTriggered() {
        this.cooldown -= 0.25;
        if(this.cooldown == 2) {
            for(Arrow a : this.arrows) {
                a.remove();
            }
            this.arrows.clear();
        }
        if(this.cooldown < 1) {
            Location l = this.sign.getLocation().clone().add(0.5,0.5,0.5);
            if(this.east) {
                Location l_e = l.clone().add(0.5,0,0);
                l_e.setYaw(-90);
                l_e.setPitch(0);
                this.arrows.add(l.getWorld().spawnArrow(l_e, l_e.getDirection(), 1.8f, 0));
            }
            if(this.west) {
                Location l_w = l.clone().add(-0.55,0,0);
                l_w.setYaw(90);
                l_w.setPitch(0);
                this.arrows.add(l.getWorld().spawnArrow(l_w, l_w.getDirection(), 1.8f, 0));
            }
            if(this.north) {
                Location l_n = l.clone().add(0,0,-0.55);
                l_n.setYaw(-180);
                l_n.setPitch(0);
                this.arrows.add(l.getWorld().spawnArrow(l_n, l_n.getDirection(), 1.8f, 0));
            }
            if(this.south) {
                Location l_s = l.clone().add(0,0,0.5);
                l_s.setYaw(0);
                l_s.setPitch(0);
                this.arrows.add(l.getWorld().spawnArrow(l_s, l_s.getDirection(), 1.8f, 0));
            }
            if(this.cooldown <= 0) {
                this.cooldown = 4;
            }
        }
    }

}
