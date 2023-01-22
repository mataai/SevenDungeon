package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class KillPlayerSign extends ActivableSign {

    private boolean all;
    
    public KillPlayerSign(Sign sign, Game game) {
        super(sign, game);
        this.singleTarget = true;
        try {
            String line3 = this.sign.getLine(2);
            if(line3 == null || line3.isEmpty()) {
                this.all = false;
            }
            else {
                this.all = true;
            }
        }catch(Exception e) {
            SevenDungeons.message("Teleportation's third line is wrong.");
        }
    }

    @Override
    public void on(DungeonPlayer dp) {
        if(dp == null) {
            this.all = true;
        }
        if(!this.all) {
            this.game.death(dp);
        }
        else {
            for(DungeonPlayer dp2 : this.game.getTeam().getPlayers()) {
                this.game.death(dp2);
            }
        }
    }
    
}
