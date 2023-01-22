package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class ActivableSign extends DungeonSign {
    
    protected int activationId;
    protected boolean singleTarget;
    
    public ActivableSign(Sign sign, Game game) {
        super(sign, game);
        this.singleTarget = false;
        try {
            this.activationId = Integer.parseInt(sign.getLine(1).trim().split(" ")[0]);
        }catch(Exception e) {
            SevenDungeons.log("ActivableSign empty on line 2.");
        }
    }
    
    public void on() {
    }
    
    public void on(DungeonPlayer dp) {
    }
    
    public void off() {
    }
    
    public int getActivationId() {
        return this.activationId;
    }
    
    public boolean isSingleTarget() {
        return this.singleTarget;
    }
    
    public void signal(int id) {
        return;
    }
}
