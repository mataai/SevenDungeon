package seven.dungeons.signs;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class SoulEaterCaptorSign extends CaptorSign {

    private double radius;

    public SoulEaterCaptorSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
        try {
            String line3 = this.sign.getLine(2).trim();
            this.radius = Integer.parseInt(line3);

        }catch(Exception e) {
            SevenDungeons.log("SoulEaterCaptor's third and/or fourth line is wrong or empty.");
        }
        
    }
    
    @Override
    public void on() {
        this.game.getSigns2ticks().add(this);
        this.location.getBlock().setType(Material.SOUL_LANTERN);
    }
    
    @Override
    public void off() {
        this.game.getSigns2ticks().remove(this);
        this.location.getBlock().setType(Material.LANTERN);
    }
    
    @Override
    public void isTriggered() {
        for(Player p : this.game.getPlayers()) {
            Location l = p.getLocation();
            if(this.location.distance(l) <= this.radius && (Math.abs(l.getBlockY() - this.location.getBlockY()) < 2)) {
                p.damage(3);
                this.location.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_HURT, 0.5f, 0.8f);
                Vector direction = l.clone().add(this.location.clone().multiply(-1)).toVector().normalize().multiply(0.2);
                Particle.DustOptions du = new Particle.DustOptions(Color.TEAL, 1);
                Location point = this.location.clone().add(0, 0.3, 0);
                point.getWorld().spawnParticle(Particle.SMOKE_NORMAL, point, 40, 0.5, 0.5, 0.5, 0);
                point.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, l.clone().add(0, 2, 0), 1, 0, 0, 0);
                for(double i = 0; i < this.location.distance(l); i+=0.2){
                    l.getWorld().spawnParticle(Particle.REDSTONE, point, 1, 0, 0, 0, 1, du);
                    point.add(direction);
                }
            }
        }
        
    }

}
