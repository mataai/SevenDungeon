package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class PlayerSpawnSign extends DungeonSign {
    
    private int playerNumber;
    
    public PlayerSpawnSign(Sign sign, Game game) {
        super(sign, game);
        try {
            this.playerNumber = Integer.parseInt(sign.getLine(1).trim());
        }catch(Exception e) {
            SevenDungeons.log("PlayerSpawn empty on line 2.");
        }
    }
    
    public int getPlayerNumber() {
        return this.playerNumber;
    }
    
}
