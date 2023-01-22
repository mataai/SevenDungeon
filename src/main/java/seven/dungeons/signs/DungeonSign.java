package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import seven.dungeons.Game;
public class DungeonSign {
    
    protected Sign sign;
    protected Location location;
    protected Game game;
    
    public DungeonSign(Sign sign, Game game) {
        this.sign = sign;
        this.game = game;
        this.location = sign.getLocation();
    }
    
    public Location getLocation() {
        return this.location;
    }

}
