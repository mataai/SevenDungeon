package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;

public class ExitSign extends CaptorSign implements AnimatedSign {
    
    private int x1, x2, y1, z1, z2;
    
    public ExitSign(Sign sign, Game game) {
        super(sign, game);
        int x = this.location.getBlockX();
        int y = this.location.getBlockY();
        int z = this.location.getBlockZ();
        this.x1 = x-1; this.x2 = x+1;
        this.y1 = y;
        this.z1 = z-1; this.z2 = z+1;
    }
    
    @Override
    public void isTriggered() {
        for(Player p : this.game.getPlayers()) {
            Location l = p.getLocation();
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            if(y != this.y1 || x < this.x1 || x > this.x2 || z < this.z1 || z > this.z2) {
                return;
            }
        }
        this.trigger(null);
    }
    
    @Override
    public void animate(ArrayList<Player> players) {
        for(int i=0; i < 3; i++) {
           float x = (float) (this.location.getBlockX() - 1 + 3.0 * Math.random());
           float z = (float) (this.location.getBlockZ() - 1 + 3.0 * Math.random());
           this.location.getWorld().spawnParticle(Particle.SPELL_INSTANT, x, this.location.getBlockY(), z, 1);
        }
    }
    
    @Override
    public void on() {
        this.game.getAsyncSigns2ticks().add(this);
        this.game.getSigns2ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getAsyncSigns2ticks().remove(this);
        this.game.getSigns2ticks().remove(this);
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.off();
        this.game.endGame();
    }

}
