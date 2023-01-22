package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.Game;

public class LightningSign extends ActivableSign {

    public LightningSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
    }
    
    @Override
    public void on() {
        this.game.getWorld().strikeLightning(this.location);
    }

}
