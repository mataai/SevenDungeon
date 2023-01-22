package seven.dungeons.signs;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class LauncherSign extends CaptorSign {
    
    private Block plate = null;
    private Vector velocity = null;

    public LauncherSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            float z = (line3[0].charAt(0) == 'n') ? (Float.parseFloat(line3[0].substring(1)) * -1) : Float.parseFloat(line3[0].substring(1));
            float x = (line3[1].charAt(0) == 'w') ? (Float.parseFloat(line3[1].substring(1)) * -1) : Float.parseFloat(line3[1].substring(1));
            float y = (line3[2].charAt(0) == 'd') ? (Float.parseFloat(line3[2].substring(1)) * -1) : Float.parseFloat(line3[2].substring(1));
            this.velocity = new Vector(x,y,z);
        }catch(Exception e) {
            SevenDungeons.log("Launcher's third line is wrong.");
        }
    }

    @Override
    public void on() {
        this.location.getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.plate = this.location.getBlock();
        Firework fw = (Firework) this.game.getWorld().spawnEntity(this.location.clone().add(0.5,0.5,0.5), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        this.location.getBlock().setType(Material.AIR);
        this.plate = null;
    }
    
    @Override
    public void isTriggered() {
        if(this.plate.getBlockPower() > 0) {
            for(Player p : this.game.getPlayers()) {
                if(p.getLocation().getBlock().equals(this.plate)) {
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 3, 1.3f);
                    p.setVelocity(this.velocity);
                    p.setInvulnerable(true);
                    this.makeVulnerable(p);
                }
            }
        }
    }
    
    public void makeVulnerable(Player p) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                p.setInvulnerable(false);
            }
        }.runTaskLater(this.game.getPlugin(), 100);
    }
}
