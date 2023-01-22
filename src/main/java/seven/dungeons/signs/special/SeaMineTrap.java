package seven.dungeons.signs.special;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

public class SeaMineTrap extends CaptorSign {

	private int blockId;
    private int cooldown;
    private float tone;

    public SeaMineTrap(Sign sign, Game game) {
        super(sign, game);
        try {
            blockId = Integer.parseInt(this.sign.getLine(2).trim());
        }catch(Exception e) {
            SevenDungeons.log("SeaMineTrap's third line is wrong.");
        }
        this.location = this.location.add(0.5, 0.5, 0.5);
        this.tone = 0.5f + (float)Math.random() * 0.3f;
    }
    
    @Override
    public void on() {
        this.sign.getLocation().getBlock().setType(Material.LODESTONE);
        this.cooldown = 12;
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
    public void trigger(DungeonPlayer dp) {
    	this.off();
    	this.location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.location, 4);
    	this.location.getWorld().playSound(this.location, Sound.ENTITY_GENERIC_EXPLODE, 2, 1.2f);
    	for(Player p : this.game.getPlayers()) {
    		if(p.getLocation().distance(this.location) < 5) {
    			p.damage(14);
    		}
    	}
    	Location chain = this.location.clone();
    	while(chain.add(0, -1, 0).getBlock().getType().equals(Material.CHAIN)) {
    		chain.getBlock().setType(Material.AIR);
    	}
    }
    
    @Override
    public void isTriggered() {
        
        int closest = 8;
        for(Player p : this.game.getPlayers()) {
        	int distance = (int)Math.ceil(p.getLocation().distance(this.location));
        	closest = distance < closest ? distance : closest;
        }
        
        if(closest <= 3) {
        	this.trigger(null);
        	return;
        }
        
        if(closest < 8) {
        	if(this.cooldown % (closest == 4 ? 1 : closest - 4) == 0) {
        		this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BIT, 1, this.tone);
        		this.location.getBlock().setType(this.location.getBlock().getType() == Material.TNT ? Material.LODESTONE : Material.TNT);
        	}
        }
        else if(closest % 4 == 0 && this.location.getBlock().getType().equals(Material.TNT)) {
        	this.location.getBlock().setType(Material.LODESTONE);
        }
        
        this.cooldown -= 1;
        if(this.cooldown <= 0) {
            this.cooldown = 12;
        }
        
    }
    
	
}
