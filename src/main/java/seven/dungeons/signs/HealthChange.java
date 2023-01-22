package seven.dungeons.signs;

import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class HealthChange extends ActivableSign{

    private int health;
    private boolean all = false;

    public HealthChange(Sign sign, Game game) {
        super(sign, game);
        this.singleTarget = true;
        try {
            this.health = Integer.parseInt(this.sign.getLine(2).trim());
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.isEmpty()) {
                this.all = false;
            }
            else {
                this.all = true;
            }
        }catch(Exception e) {
            SevenDungeons.log("Healthchange's third or fourth line is wrong or empty.");
        }
    }
    
    @Override
    public void on(DungeonPlayer dp) {
        if(dp == null) {
            this.all = true;
        }

        if(!this.all) {
            this.changeHealth(dp.getPlayer());
        }
        else {
            for(Player p : this.game.getPlayers()) {
                this.changeHealth(p);
            }
        }
    }

    private void changeHealth(Player p){
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.health);
        p.setHealth(this.health);
    }


}
