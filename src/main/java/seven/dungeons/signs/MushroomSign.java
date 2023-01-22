package seven.dungeons.signs;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class MushroomSign extends CaptorSign {
    
    private int signal;
    private HashMap<Player,Integer> players = new HashMap<Player,Integer>();

    public MushroomSign(Sign sign, Game game) {
        super(sign, game);
        try {
            this.signal = Integer.parseInt(this.sign.getLine(2));
        }catch(Exception e) {
            SevenDungeons.log("Mushroom's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        for(Player p : this.game.getPlayers()) {
            this.players.clear();
            this.players.put(p, 0);
        }
        this.game.getSigns5ticks().add(this);
        int x = this.location.getBlockX();
        int z = this.location.getBlockZ();
        for(int i = x; i <= x+1; i++) {
            for(int j = z; j <= z+1; j++) {
                Location l = new Location(this.location.getWorld(),i,this.location.getBlockY(),j);
                l.getBlock().setType(Material.BROWN_MUSHROOM);
            }
        }
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        int x = this.location.getBlockX();
        int z = this.location.getBlockZ();
        for(int i = x; i <= x+1; i++) {
            for(int j = z; j <= z+1; j++) {
                Location l = new Location(this.location.getWorld(),i,this.location.getBlockY(),j);
                l.getBlock().setType(Material.AIR);
            }
        }
    }
    
    @Override
    public void isTriggered() {
        int x = this.location.getBlockX();
        int y = this.location.getBlockY();
        int z = this.location.getBlockZ();
        for(Player p : this.game.getPlayers()) {
            Location l = p.getLocation();
            if(l.getBlockY() == y && l.getBlockX() >= x && l.getBlockX() <= x+1 && l.getBlockZ() >= z && l.getBlockZ() <= z+1) {
                this.players.put(p,this.players.get(p)+1);
                if(this.players.get(p) > 8) {
                    DungeonPlayer dp = null;
                    for(DungeonPlayer dpt : this.game.getTeam().getPlayers()) {
                        if(dpt.getPlayer().equals(p)) {
                            dp = dpt;
                        }
                    }
                    this.game.activate(this.signal, dp);
                }
                float x2 = (float) (this.location.getBlockX() + 2.0 * Math.random());
                float z2 = (float) (this.location.getBlockZ() + 2.0 * Math.random());
                this.location.getWorld().spawnParticle(Particle.SPELL_INSTANT, x2, this.location.getBlockY(), z2, 1);
            }
            else {
                this.players.put(p, 0);
            }
        }
        return;
    }
    

}
