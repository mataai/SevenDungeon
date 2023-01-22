package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

import java.util.ArrayList;
import java.util.HashMap;

public class ElectricTrap extends CaptorSign {

	private int xh;
    private int zh;
    private HashMap<Integer, Location> corners = new HashMap<Integer, Location>();
    private HashMap<Location,Boolean> powered = new HashMap<Location,Boolean>();

    public ElectricTrap(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.xh = Integer.parseInt(line3[0]);
            this.zh = Integer.parseInt(line3[1]);
            int x = this.location.getBlockX();
            int y = this.location.getBlockY() - 1;
            int z = this.location.getBlockZ();
            int i = 0;
            for(int indexX = x; indexX <= x + this.xh; indexX += this.xh){
                for(int indexZ = z; indexZ <= z + this.zh; indexZ += this.zh){
                    this.corners.put( Integer.parseInt(line3[2]) + i, new Location(this.location.getWorld(), indexX, y, indexZ));
                    this.powered.put(new Location(this.location.getWorld(), indexX, y, indexZ), true);
                    i++;
                }
            }

        }catch(Exception e) {
            SevenDungeons.log("ElectricTrap's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
        this.game.getSpecialSigns().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        this.game.getSpecialSigns().remove(this);
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.game.death(dp);
    }
    
    @Override
    public void isTriggered() {

        double yh = Math.random() + 1;
        Location l1 = this.location.clone().add(0.5, -1 - yh, 0.5);
        Location l2 = this.location.clone().add(0.5 + this.xh, -1 - yh, 0.5);
        Location l3 = this.location.clone().add(0.5, -1 - yh, 0.5 + this.zh);
        World w = this.location.getWorld();

        for(double x = 0; x < this.xh; x += 0.25){
            w.spawnParticle(Particle.CRIT_MAGIC, l1.clone().add(x,0,0), 1,0,0,0,0);
            w.spawnParticle(Particle.CRIT_MAGIC, l3.clone().add(x,0,0), 1,0,0,0,0);
        }

        for(double z = 0; z < this.zh; z += 0.25){
            w.spawnParticle(Particle.CRIT_MAGIC, l1.clone().add(0,0,z), 1, 0,0,0, 0);
            w.spawnParticle(Particle.CRIT_MAGIC, l2.clone().add(0,0,z), 1,0,0,0,0);
        }

        int x1 = l1.getBlockX();
        int x2 = x1 + xh + 1;
        int z1 = l1.getBlockZ();
        int z2 = z1 + zh + 1;
        int y2 = l1.getBlockY() + 1;
        int y1 = y2 - 4;

        for(DungeonPlayer dp : this.game.getTeam().getPlayers()){
            Location l = dp.getPlayer().getLocation();
            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();
            if(x < x1 || x > x2 || y < y1 || y > y2 || z < z1 || z > z2){
                continue;
            }
            this.trigger(dp);
        }
    }

    @Override
    public void signal(int id){
        Location l = this.corners.get(id);
        if(l == null){
            return;
        }
        boolean b = !this.powered.get(l);
        this.powered.put(l, b);
        l.getWorld().playSound(l, Sound.ITEM_FLINTANDSTEEL_USE,0.5f, 1.5f);
        if(b){
            l.getBlock().setType(Material.SEA_LANTERN);
        }
        else{
            l.getBlock().setType(Material.WAXED_OXIDIZED_COPPER);
        }
        for(Boolean bool : this.powered.values()){
            if(bool){
                return;
            }
        }
        this.off();
        this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1.5f);
    }
    
	
}
