package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class DelayGroupSign extends CaptorSign {
    
    private int signalMin;
    private int signalMax;
    private boolean polarity;
    private float time;
    private float initialTime;

    public DelayGroupSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.signalMin = Integer.parseInt(line3[0]);
            this.signalMax = Integer.parseInt(line3[1]);
            this.polarity = true;
            if(line3.length > 2 && line3[2].equalsIgnoreCase("off")) {
                this.polarity = false;
            }
            this.initialTime = Float.parseFloat(this.sign.getLine(3).trim());
        }catch(Exception e) {
            SevenDungeons.log("DelayGroup's third and / or fourth line is wrong.");
        }
    }

    @Override
    public void on() {
        this.time = this.initialTime;
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    public void isTriggered() {
        if(this.time <= 0) {
            this.trigger(null);
        }
        this.time -= 0.25;
    }
    
    public void trigger(DungeonPlayer dp) {
        this.off();
        for(int i = this.signalMin; i <= this.signalMax; i++) {
            if(this.polarity) {
                this.game.activate(i, null);
            }
            else {
                this.game.deactivate(i, null);
            }
            
        }
        
    }
}
