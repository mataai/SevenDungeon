package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class NumberCaptorSign extends CaptorSign {

    private int x1, x2, y, z1, z2;
    private int signal;
    private int nb;
    private boolean polarity;

    public NumberCaptorSign(Sign sign, Game game) {
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
            this.y = this.sign.getBlock().getY();
            this.nb = Integer.parseInt(distances[2]);
            String[] signalLine = this.sign.getLine(3).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("NumberCaptor third and/or fourth line is empty or wrong at " + this.location);
        }
    }
    
    @Override
    public void isTriggered() {
        int nbPlayers = 0;
        this.drawBorders();
        for(Player p : this.game.getPlayers()) {
            Location l;
            if(p.isInsideVehicle()) {
                l = p.getVehicle().getLocation();
            }
            else {
                l = p.getLocation();
            }
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            if(y != this.y || x < this.x1 || x > this.x2 || z < this.z1 || z > this.z2) {
                continue;
            }
            nbPlayers++;
        }
        if(nbPlayers >= this.nb){
            this.trigger(null);
        }
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
    public void trigger(DungeonPlayer dp) {
        this.off();
        if(this.polarity) {
            this.game.activate(signal, dp);
            return;
        }
        this.game.deactivate(signal, dp);
    }

    public void drawBorders(){
        float y = this.location.getBlockY() + 0.2f;
        World w = this.location.getWorld();
        Location l1 = new Location(w, this.x1, y, this.z1);
        Location l2 = new Location(w, this.x1, y, this.z2+1);
        for(float i = 0; i <= this.x2 - this.x1 + 1; i += 0.25f){
            w.spawnParticle(Particle.VILLAGER_HAPPY, l1.clone().add(i,0,0), 1);
            w.spawnParticle(Particle.VILLAGER_HAPPY, l2.clone().add(i,0,0), 1);
        }
        Location l3 = new Location(w, this.x1, y, this.z1);
        Location l4 = new Location(w, this.x2+1, y, this.z1);
        for(float i = 0; i <= this.z2 - this.z1 + 1; i += 0.25f){
            w.spawnParticle(Particle.VILLAGER_HAPPY, l3.clone().add(0,0,i), 1);
            w.spawnParticle(Particle.VILLAGER_HAPPY, l4.clone().add(0,0,i), 1);
        }
    }
    
}
