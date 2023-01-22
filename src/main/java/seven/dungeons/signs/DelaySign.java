package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class DelaySign extends CaptorSign {
    
    private int signal;
    private boolean polarity;
    private float time;
    private float initialTime;

    public DelaySign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.signal = Integer.parseInt(line3[0]);
            this.polarity = true;
            if(line3.length > 1 && line3[1].equalsIgnoreCase("off")) {
                this.polarity = false;
            }
            this.initialTime = Float.parseFloat(this.sign.getLine(3).trim());
        }catch(Exception e) {
        	SevenDungeons.log("Delay's sign is wrong at " + this.location.toString() + ".");
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
        this.time -= 0.25;
        if(this.time <= 0) {
            this.trigger(null);
        }
    }
    
    public void trigger(DungeonPlayer dp) {
        this.off();
        if(this.polarity) {
            this.game.activate(signal, null);
            return;
        }
        this.game.deactivate(signal, null);
    }
}
