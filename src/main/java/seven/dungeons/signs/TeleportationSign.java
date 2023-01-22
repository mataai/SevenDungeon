package seven.dungeons.signs;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class TeleportationSign extends ActivableSign {
    
    private boolean all;
    private boolean particles;
    private int yaw;

    public TeleportationSign(Sign sign, Game game) {
        super(sign, game);
        this.location.add(0.5,0,0.5);
        this.singleTarget = true;
        Rotatable signRotation = (Rotatable)this.sign.getBlock().getState().getBlockData();
        switch(signRotation.getRotation()) {
        case NORTH : this.yaw = 180; break;
        case NORTH_EAST : this.yaw = -135; break;
        case EAST : this.yaw = -90; break;
        case NORTH_WEST : this.yaw = 135; break;
        case WEST : this.yaw = 90; break;
        case SOUTH_WEST : this.yaw = 45; break;
        case SOUTH : this.yaw = 0; break;
        case SOUTH_EAST : this.yaw = -45;
            default : this.yaw = 0; break;
        }
        try {
            String line3 = this.sign.getLine(2);
            if(line3 == null || line3.isEmpty()) {
                this.all = false;
            }
            else {
                this.all = true;
            }
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.isEmpty()) {
                this.particles = false;
            }
            else {
                this.particles = true;
            }
        }catch(Exception e) {
            SevenDungeons.message("Teleportation's third line is wrong.");
        }
    }
    
    @Override
    public void on(DungeonPlayer dp) {
        if(dp == null) {
            this.all = true;
        }
        if(!this.all) {
            this.tp(dp.getPlayer());
        }
        else {
            for(Player p : this.game.getPlayers()) {
                this.tp(p);
            }
        }
    }
    
    public void tp(Player p) {
        Entity v = null;
        if(p.isInsideVehicle()) {
            v = p.getVehicle();
        }
        p.setVelocity(new Vector(0,0,0));
        Location l = this.location.clone();
        l.setYaw(this.yaw);
        p.setNoDamageTicks(20);
        if(this.particles){
            this.animation(p,l);
        }
        Bukkit.getScheduler().runTaskLater(this.game.getPlugin(), () -> p.teleport(l, TeleportCause.PLUGIN), 1l);
        if(v != null) {
            v.teleport(this.location.clone().add(0,1,0), TeleportCause.PLUGIN);
        }
    }

    public void animation(Player p, Location l){
        Particle.DustOptions du = new Particle.DustOptions(Color.WHITE, 3);
        Location loc1 = p.getLocation().clone();
        Location loc2 = l.clone();
        for(float i = 0; i < 2 * Math.PI; i+= Math.PI / 24) {
            for(float j = 1; j <= 2; j+= 0.5f){
                loc1.getWorld().spawnParticle(Particle.REDSTONE, loc1.clone().add(Math.cos(i)*2,j,Math.sin(i)*2), 1, 0, 0, 0, 1, du);
                loc2.getWorld().spawnParticle(Particle.REDSTONE, loc2.clone().add(Math.cos(i)*2,j,Math.sin(i)*2), 1, 0, 0, 0, 1, du);
            }

        }
    }

}
