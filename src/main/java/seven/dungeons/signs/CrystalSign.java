package seven.dungeons.signs;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;

public class CrystalSign extends CaptorSign {
    
    private Entity crystal;

    public CrystalSign(Sign sign, Game game) {
        super(sign, game);
        this.location.setX(this.location.getX() + 0.5);
        this.location.setZ(this.location.getZ() + 0.5);
        this.game.getGameScore().incrementCrystal();
    }
    
    @Override
    public void on() {
        this.crystal = this.game.getWorld().spawnEntity(this.location, EntityType.ENDER_CRYSTAL);
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void isTriggered() {
        if(this.crystal == null || !this.crystal.isDead())
        {
            return;
        }
        this.trigger(null);
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.game.getWorld().playSound(location, SoundSign.sounds[2], 10, 1.5f);
        Firework fw = (Firework) this.game.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.FUCHSIA).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
        this.game.crystalCounter(this.location);
        this.off();
    }

    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        if(!this.crystal.isDead()) {
            this.crystal.remove();
        }
    }
}
