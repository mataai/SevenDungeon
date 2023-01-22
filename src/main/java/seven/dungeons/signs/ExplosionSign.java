package seven.dungeons.signs;

import org.bukkit.block.Sign;

import seven.dungeons.Game;

public class ExplosionSign extends ActivableSign {
    
    private float power;
    private boolean damage;

    public ExplosionSign(Sign sign, Game game) {
        super(sign, game);
        String line3 = this.sign.getLine(2);
        if(line3 == null || line3.isEmpty()) {
            this.power = 2;
        }
        else {
            this.power = Float.parseFloat(line3.trim());
        }
        String line4 = this.sign.getLine(3);
        if(line4 == null || line4.isEmpty()) {
            this.damage = false;
        }
        else {
            this.damage = false;
            if(line4.trim().equalsIgnoreCase("true"))
            {
                this.damage = true;
            }
        }
    }

    @Override
    public void on() {
        this.game.getWorld().createExplosion(this.location, this.power, false, this.damage);
    }
    
}
