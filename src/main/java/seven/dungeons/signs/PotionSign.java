package seven.dungeons.signs;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class PotionSign extends ActivableSign{
    
    public static PotionEffectType[] effects =
            {
                    PotionEffectType.SPEED, // 0
                    PotionEffectType.LEVITATION // 1
            };
    private PotionEffectType effect;
    private boolean all;
    private int time;
    private int force;

    public PotionSign(Sign sign, Game game) {
        super(sign, game);
        this.singleTarget = true;
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.effect = effects[Integer.parseInt(line3[0])];
            this.time = Integer.parseInt(line3[1]);
            this.force = 0;
            if(line3.length > 2) {
                this.force = Integer.parseInt(line3[2]);
            }
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.isEmpty()) {
                this.all = false;
            }
            else {
                this.all = true;
            }
        }catch(Exception e) {
            SevenDungeons.log("Potion's third or fourth line is wrong or empty.");
        }
    }
    
    @Override
    public void on(DungeonPlayer dp) {
        if(dp == null) {
            this.all = true;
        }   
        PotionEffect pe = new PotionEffect(this.effect, this.time * 20, this.force, false, false);
        if(!this.all) {
            this.applyPotion(dp.getPlayer(), pe);
        }
        else {
            for(Player p : this.game.getPlayers()) {
                this.applyPotion(p, pe);
            }
        }
    }
    
    public void applyPotion(Player p, PotionEffect pe) {
        if(p.hasPotionEffect(this.effect)) {
            p.removePotionEffect(this.effect);
        }
        p.addPotionEffect(pe);
    }

}
