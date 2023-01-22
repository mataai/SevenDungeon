package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class FanSign extends CaptorSign {
    
    private ArrayList<Location> locations = new ArrayList<Location>();
    private int x1, x2, y1, y2, z1, z2;
    private int xh, yh, zh;
    private int direction;
    private float force;
    private boolean particles = true;

    public FanSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] distances = this.sign.getLine(2).trim().split(" ");
            if(distances[0].substring(distances[0].length()-1).equals("s")) {
                this.z1 = this.sign.getZ();
                this.z2 = this.z1 + Integer.parseInt(distances[0].substring(0,distances[0].length()-1));
            }
            else {
                this.z2 = this.sign.getZ();
                this.z1 = this.z2 - Integer.parseInt(distances[0].substring(0,distances[0].length()-1));
            }
            if(distances[1].substring(distances[1].length()-1).equals("e")) {
                this.x1 = this.sign.getX();
                this.x2 = this.x1 + Integer.parseInt(distances[1].substring(0,distances[1].length()-1));
            }
            else {
                this.x2 = this.sign.getX();
                this.x1 = this.x2 - Integer.parseInt(distances[1].substring(0,distances[1].length()-1));
            }
            if(distances[2].substring(distances[2].length()-1).equals("u")) {
                this.y1 = this.sign.getY();
                this.y2 = this.y1 + Integer.parseInt(distances[2].substring(0,distances[2].length()-1));
            }
            else {
                this.y2 = this.sign.getY();
                this.y1 = this.y2 - Integer.parseInt(distances[2].substring(0,distances[2].length()-1));
            }
            this.xh = x2 - x1;
            this.yh = y2 - y1;
            this.zh = z2 - z1;
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            switch(line4[0]) {
            case "n": this.direction = 0; break;
            case "s": this.direction = 1; break;
            case "w": this.direction = 2; break;
            case "e": this.direction = 3; break;
            case "d": this.direction = 4; break;
            default : this.direction = 5; break;
            }
            this.force = Float.parseFloat(line4[1]);
            if(line4.length > 2) {
                this.particles = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("Fan's third and/or fourth is wrong.");
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        return;
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        Location playerLocation;
        if(this.locations.size() > 10) {
            this.locations.remove(0);
        }
        DustOptions du = new DustOptions(Color.WHITE, 1);
        this.locations.add(new Location(this.location.getWorld(), this.x1 + Math.floor(Math.random() * (this.xh + 2)),
                this.y1 + Math.floor(Math.random() * (this.yh + 2)), this.z1 + Math.floor(Math.random() * (this.zh + 2))));
        for(int i = 0; i < this.locations.size(); i++) {
            Location l = this.locations.get(i);
            switch(this.direction) {
            case 0 : l.add(0,0,-0.5); break;
            case 1 : l.add(0,0,0.5); break;
            case 2 : l.add(-0.5,0,0); break;
            case 3 : l.add(0.5,0,0); break;
            case 4 : l.add(0,-0.5,0); break;
            default : l.add(0,0.5,0); break;
            }
            if(this.particles) {
            	l.getWorld().spawnParticle(Particle.REDSTONE, l, 1, 0, 0, 0, 1, du);
            }
            
        }
        for(Player p : this.game.getPlayers()) {
            playerLocation = p.getLocation();
            int x = playerLocation.getBlockX();
            int y = playerLocation.getBlockY();
            int z = playerLocation.getBlockZ();
            if(y < this.y1 || y > this.y2 || x < this.x1 || x > this.x2 || z < this.z1 || z > this.z2) {
                continue;
            }
            else {
                switch(this.direction) {
                case 0 : p.setVelocity(p.getVelocity().add(new Vector(0,0,-(this.force)))); break;
                case 1 : p.setVelocity(p.getVelocity().add(new Vector(0,0,(this.force)))); break;
                case 2 : p.setVelocity(p.getVelocity().add(new Vector(-(this.force),0,0))); break;
                case 3 : p.setVelocity(p.getVelocity().add(new Vector((this.force),0,0))); break;
                case 4 : p.setVelocity(p.getVelocity().add(new Vector(0,-(this.force),0))); break;
                default : p.setVelocity(p.getVelocity().add(new Vector(0,(this.force),0))); break;
                }
                this.location.getWorld().playSound(this.location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2, 1);
            }
        }
    }

}
