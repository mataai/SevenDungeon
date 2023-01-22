package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class ProximityCaptor extends CaptorSign {

    private double radius;
    private int signal;
    private boolean polarity;
    private boolean glowing;

    public ProximityCaptor(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.radius = Integer.parseInt(line3[0]);
            if(line3.length > 1) {
                this.glowing = true;
            }
            String[] signalLine = this.sign.getLine(3).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("ProxmityCaptor's third and/or fourth line is wrong or empty.");
        }
        
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        for(Player p1 : this.game.getPlayers()) {
            int nbNearPlayers = 0;
            for(Player p2 : this.game.getPlayers()){
                if(p2.getLocation().distance(p1.getLocation()) < this.radius){
                    nbNearPlayers++;
                }
            }
            if(nbNearPlayers == this.game.getPlayers().size()){
                this.trigger(null);
                return;
            }
            else if(this.glowing && nbNearPlayers > 1){
                PotionEffect pe = new PotionEffect(PotionEffectType.GLOWING, 40, 0, false, false);
                if(p1.hasPotionEffect(PotionEffectType.GLOWING)){
                    p1.removePotionEffect(PotionEffectType.GLOWING);
                }
                p1.addPotionEffect(pe);
            }
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

}
