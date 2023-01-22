package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class RandomSign extends ActivableSign {
    
    private ArrayList<Integer> randomSignals = new ArrayList<>();
    
    private boolean polarity;

    public RandomSign(Sign sign, Game game) {
        super(sign, game);
        try {
          String[] line3 = this.sign.getLine(2).trim().split(" ");
          for (int i = 0; i < line3.length; i++) {
            int s = Integer.parseInt(line3[i]);
            this.randomSignals.add(Integer.valueOf(s));
          } 
          this.polarity = true;
          String line4 = this.sign.getLine(3).trim();
          if (line4 != null && !line4.isEmpty() && line4.equalsIgnoreCase("off"))
            this.polarity = false; 
        } catch (Exception e) {
          SevenDungeons.log("Random's third and/or fourth line is wrong.");
        } 
    }
    
    @Override
    public void on() {
        int s = ((Integer)this.randomSignals.get((int)Math.floor(Math.random() * this.randomSignals.size()))).intValue();
        if (this.polarity) {
          this.game.activate(s, null);
          return;
        } 
        this.game.deactivate(s, null);
      }

}
