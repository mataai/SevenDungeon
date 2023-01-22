package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;

public class CaptorSign extends ActivableSign {
    
    public CaptorSign(Sign sign, Game game) {
        super(sign, game);
    }
    
    public void isTriggered() {
    }
    
    public void trigger(DungeonPlayer dp) {
        return;
    }
}
