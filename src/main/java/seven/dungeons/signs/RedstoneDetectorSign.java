package seven.dungeons.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class RedstoneDetectorSign extends CaptorSign {
    
    private int signal;
    private boolean polarity;

    public RedstoneDetectorSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] signalLine = this.sign.getLine(2).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("RedstoneDetector's third line is wrong.");
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.off();
        if(this.polarity) {
            this.game.activate(signal, null);
            return;
        }
        this.game.deactivate(signal, null);
    }
    
    @Override
    public void on() {
        this.location.getBlock().setType(Material.REDSTONE_LAMP);
        this.game.getSigns2ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns2ticks().remove(this);
        this.location.getBlock().setType(Material.AIR);
    }
    
    @Override
    public void isTriggered() {
        if(this.location.getBlock().getBlockPower() > 0) {
            this.trigger(null);
        }
        return;
    }

}
