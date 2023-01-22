package seven.dungeons.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import seven.dungeons.Game;

public class RedstoneGeneratorSign extends ActivableSign {

    public RedstoneGeneratorSign(Sign sign, Game game) {
        super(sign, game);
    }

    @Override
    public void on() {
        this.location.getBlock().setType(Material.REDSTONE_BLOCK);
    }
    
    @Override
    public void off() {
        this.location.getBlock().setType(Material.AIR);
    }
}
