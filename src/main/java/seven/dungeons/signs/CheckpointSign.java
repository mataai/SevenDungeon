package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class CheckpointSign extends ActivableSign {
    
    private int priority;

    public CheckpointSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
        String line3 = this.sign.getLine(2);
        if(line3 == null || line3.equals("")) {
            this.priority = 0;
        }
        else
        {
            try {
                this.priority = Integer.parseInt(line3.trim());
            }catch(Exception e) {
                this.priority = 0;
                SevenDungeons.log("Checkpoint's third line is wrong.");
            }
            
        }
    }
    
    @Override
    public void on() {
        if(this.game.getCheckpointLevel() <= this.priority) {
            this.game.setSpawn(this.location, null);
            this.game.setCheckpointLevel(this.priority);
        }
        
    }

}
