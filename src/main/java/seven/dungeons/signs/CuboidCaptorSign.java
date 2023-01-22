package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import org.bukkit.entity.Player;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class CuboidCaptorSign extends CaptorSign {
    
    private int x1, x2, y1, y2, z1, z2;
    private int signal;
    private boolean polarity;

    public CuboidCaptorSign(Sign sign, Game game) {
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
            String[] signalLine = this.sign.getLine(3).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("CuboidCaptor's third and/or fourth line is empty or wrong.");
        }
    }
    
    @Override
    public void isTriggered() {
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
            if(y < this.y1 || y > this.y2 || x < this.x1 || x > this.x2 || z < this.z1 || z > this.z2) {
                continue;
            }
            DungeonPlayer dp = null;
            for(DungeonPlayer dungeonPlayer : this.game.getTeam().getPlayers()){
                if(dungeonPlayer.getPlayer() == p){
                    dp = dungeonPlayer;
                }
            }
            this.trigger(dp);
            break;
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns2ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns2ticks().remove(this);
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

    
}
