package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

import java.util.ArrayList;
import java.util.HashMap;

public class LaserTrap extends CaptorSign {

    private static Color[] laserColors = {Color.RED, Color.AQUA, Color.PURPLE};
    private int color;
    private String direction;
    private HashMap<String, Vector> vectors = new HashMap<String, Vector>();

    public LaserTrap(Sign sign, Game game) {
        super(sign, game);
        try {
        String[] line3 = this.sign.getLine(2).trim().split(" ");
        this.color = Math.min(Integer.parseInt(line3[0]), LaserTrap.laserColors.length - 1);
        this.direction = line3[1];
        this.vectors.put("e", new Vector(1,0,0));
        this.vectors.put("w", new Vector(-1, 0, 0));
        this.vectors.put("n", new Vector(0, 0, -1));
        this.vectors.put("s", new Vector(0, 0, 1));
        }catch(Exception e) {
            SevenDungeons.log("LaserTrap's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        int x1 = this.location.getBlockX();
        int x2 = x1;
        double y1 = this.location.getBlockY() - 1;
        double y2 = this.location.getBlockY() + 1;
        int z1 = this.location.getBlockZ();
        int z2 = z1;
        Location l = this.location.clone().add(0.5, 0.5, 0.5);
        World w = l.getWorld();
        Particle.DustOptions du = new Particle.DustOptions(LaserTrap.laserColors[this.color], 1);
        l.getWorld().playSound(l, Sound.BLOCK_BEACON_POWER_SELECT, 0.5f, 2);
        int i = 0;
        do{
            w.spawnParticle(Particle.REDSTONE, l.clone().add(this.vectors.get(this.direction).clone().multiply(-0.5)), 1, 0, 0, 0, 1, du);
            w.spawnParticle(Particle.REDSTONE, l.clone().add(this.vectors.get(this.direction).clone().multiply(-0.17)), 1, 0, 0, 0, 1, du);
            w.spawnParticle(Particle.REDSTONE, l.clone().add(this.vectors.get(this.direction).clone().multiply(0.17)), 1, 0, 0, 0, 1, du);
            x2 = l.getBlockX();
            z2 = l.getBlockZ();
            l.add(this.vectors.get(this.direction));
        }while(l.getBlock().getType() == Material.AIR);

        switch(this.direction){
            case "e" : case "s" : x2 += 1; z2 += 1;
                break;
            case "w" : x1 += 1; z2 += 1;
                break;
            default : x2 += 1; z1 += 1;
        }

        for(DungeonPlayer dp : this.game.getTeam().getPlayers()){
            Player p = dp.getPlayer();
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();

            if(x < Math.min(x1,x2) || x > Math.max(x1, x2) || y < y1 || y > y2 || z < Math.min(z1, z2) || z > Math.max(z1, z2)){
                continue;
            }
            this.game.death(dp);
        }
    }
    
    @Override
    public void off() {

    }
    
    @Override
    public void isTriggered() {

    }

}
