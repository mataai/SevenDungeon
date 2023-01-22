package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class ExplosiveTargetSign extends CaptorSign {
    
    private int signal;
    private boolean polarity;
    private int blockId;
    private int radius;
    private int animation;

    public ExplosiveTargetSign(Sign sign, Game game) {
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
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.blockId = Integer.parseInt(line4[0]);
            this.radius = Integer.parseInt(line4[1]);
        }catch(Exception e) {
            SevenDungeons.log("ExplosiveTarget's sign is wrong.");
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
    	Location centeredLocation = this.location.clone().add(0.5, 0.5, 0.5);
    	this.location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, centeredLocation.add(0.5,0,0.5), 3);
        Location l = null;
        for(int x = this.location.getBlockX()-this.radius; x <= this.location.getBlockX()+this.radius; x++) {
        	for(int y = this.location.getBlockY()-this.radius; y <= this.location.getBlockY()+this.radius; y++) {
        		for(int z = this.location.getBlockZ()-this.radius; z <= this.location.getBlockZ()+this.radius; z++) {
                	l = new Location(this.location.getWorld(), x, y, z);
                	if(l.getBlock().getType().equals(Material.TARGET)){
                		l.getBlock().setType(Material.REDSTONE_BLOCK);
                	}
                }
            }
        }
        this.off();
        if(this.polarity) {
            this.game.activate(signal, null);
            return;
        }
        this.game.deactivate(signal, null);
    }
    
    @Override
    public void on() {
        this.location.getBlock().setType(Material.TARGET);
        this.game.getSigns2ticks().add(this);
        this.animation = 0;
    }
    
    @Override
    public void off() {
        this.game.getSigns2ticks().remove(this);
        Block block = this.game.getBlockStock(this.blockId);
        if (block == null) {
            this.location.getBlock().setType(Material.AIR);
            return;
        }
        this.location.getBlock().setType(block.getType());
        this.location.getBlock().setBlockData(block.getBlockData());
    }
    
    @Override
    public void isTriggered() {
    	ArrayList<Entity> nearbyEntities = new ArrayList<>(this.location.getWorld().getNearbyEntities(this.location.clone().add(0.5, 0.5, 0.5), 0.6, 0.6, 0.6));
    	for(Entity e : nearbyEntities) {
    		if(e instanceof Arrow) {
    			e.remove();
    			this.trigger(null);
    			return;
    		}
    	}
    	this.animation++;
    	if(this.animation == 5) {
    		this.location.getBlock().setType(Material.TARGET);
    	}
    	else if(this.animation == 10) {
    		this.location.getBlock().setType(Material.RED_CONCRETE);
    		this.animation = 0;
    	}
        return;
    }

}
