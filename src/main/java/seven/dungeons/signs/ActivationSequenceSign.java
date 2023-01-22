package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class ActivationSequenceSign extends CaptorSign {
    private int signalMin;
    private int signalMax;
    private float duration;
    private float delay;
    private float timer;

    public ActivationSequenceSign(Sign sign, Game game) {
        super(sign, game);
        try {
          String[] line4 = this.sign.getLine(3).trim().split(" ");
          if(line4.length < 2){
            this.duration = -1f;
          }
          else{
              this.duration = Float.parseFloat(line4[1]);
          }

          this.delay = Float.parseFloat(line4[0]);
          String[] line3 = this.sign.getLine(2).trim().split(" ");
          this.signalMin = Integer.parseInt(line3[0]);
          this.signalMax = Integer.parseInt(line3[1]); 
        } catch (Exception e) {
          SevenDungeons.log("ActivationSequence's third and/or fourth line is wrong.");
        } 
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
        this.timer = 0;
        SevenDungeons.log("Activation Sequence started.");
      }
      
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
      
    @Override
    public void isTriggered() {
        
        for(int i = this.signalMin; i <= this.signalMax; i++) {
            if(this.timer == ((i - this.signalMin) * this.delay)) {
                this.game.activate(i, null);
            }
            if(this.duration > 0 && this.timer == ((i - this.signalMin) * this.delay + this.duration)) {
                this.game.deactivate(i, null);
            }
        }
        this.timer += 0.25;
        if(this.timer > (this.signalMax - this.signalMin + 1) * this.delay + this.duration) {
            this.off();
        }
    }
    

}
