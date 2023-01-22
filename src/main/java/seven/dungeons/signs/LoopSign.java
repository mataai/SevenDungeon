package seven.dungeons.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class LoopSign extends CaptorSign {
    
    private Map<Integer, Float> start = new HashMap<>();
    private Map<Integer, Float> end = new HashMap<>();
    private float duration;
    private float alternate;
    private float timer;
    private boolean polarity;

    public LoopSign(Sign sign, Game game) {
        super(sign, game);
        try {
          String[] line4 = this.sign.getLine(3).trim().split(" ");
          this.duration = Float.parseFloat(line4[1]);
          this.alternate = Float.parseFloat(line4[0]);
          this.polarity = true;
          if (line4[2].equalsIgnoreCase("off"))
            this.polarity = false; 
          String[] line3 = this.sign.getLine(2).trim().split(" ");
          float m = line3.length * this.alternate;
          for (int i = 0; i < line3.length; i++) {
            float s = (i + 1) * this.alternate % m;
            float e = ((i + 1) * this.alternate + this.duration) % m;
            this.start.put(Integer.valueOf(Integer.parseInt(line3[i])), Float.valueOf(s));
            this.end.put(Integer.valueOf(Integer.parseInt(line3[i])), Float.valueOf(e));
          } 
        } catch (Exception e) {
          SevenDungeons.log("Loop's third and/or fourth line is wrong.");
        } 
        this.timer = 0.0F;
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
      }
      
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        for (Map.Entry<Integer, Float> entry : this.end.entrySet()) {
          if (this.polarity) {
            this.game.activate(((Integer)entry.getKey()).intValue(), null);
            continue;
          } 
          this.game.deactivate(((Integer)entry.getKey()).intValue(), null);
        }
    }
      
    @Override
    public void isTriggered() {
        for (Map.Entry<Integer, Float> entry : this.end.entrySet()) {
          if (((Float)entry.getValue()).floatValue() == this.timer)
            this.game.deactivate(((Integer)entry.getKey()).intValue(), null); 
        }
        for (Map.Entry<Integer, Float> entry : this.start.entrySet()) {
            if (((Float)entry.getValue()).floatValue() == this.timer)
              this.game.activate(((Integer)entry.getKey()).intValue(), null); 
          }

        incrementTimer();
    }
    
    public void incrementTimer() {
        this.timer = (float)((this.timer + 0.25D) % (this.start.size() * this.alternate));
    }

}
