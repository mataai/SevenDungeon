package seven.dungeons.signs;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class TargetSign extends CaptorSign {
    
    private int signal;
    private boolean polarity;
    private int blockId;

    public TargetSign(Sign sign, Game game) {
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
            this.blockId = Integer.parseInt(this.sign.getLine(3).trim());
        }catch(Exception e) {
            SevenDungeons.log("Target's sign is wrong.");
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
    	Location centeredLocation = this.location.clone().add(0.5, 0.5, 0.5);
    	this.game.getWorld().playSound(centeredLocation, Sound.BLOCK_BEEHIVE_EXIT, 1, 1.3f);
        Firework fw = (Firework) this.game.getWorld().spawnEntity(centeredLocation, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.RED).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
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
        if(this.location.getBlock().isBlockFacePowered(BlockFace.UP) && this.location.getBlock().isBlockFacePowered(BlockFace.DOWN)) {
        	for(Entity e : this.location.getWorld().getEntities()) {
        		if(e instanceof Arrow) {
        			if(this.location.clone().add(0.5, 0.5, 0.5).distance(e.getLocation()) < 1) {
        				e.remove();
        				break;
        			}
        		}
        	}
            this.trigger(null);
        }
        return;
    }

}
