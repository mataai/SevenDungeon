package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import org.bukkit.entity.Player;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class SphericCaptorSign extends CaptorSign {
    
    private double radius;
    private int signal;
    private boolean polarity;

    public SphericCaptorSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
        try {
            String line3 = this.sign.getLine(2).trim();
            this.radius = Integer.parseInt(line3);
            String[] signalLine = this.sign.getLine(3).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("SphericCaptor's third and/or fourth line is wrong or empty.");
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
    public void isTriggered() {
        for(Player p : this.game.getPlayers()) {
            Location l = p.getLocation();
            if(this.location.distance(l) <= this.radius) {
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
