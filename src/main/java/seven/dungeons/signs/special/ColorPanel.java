package seven.dungeons.signs.special;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.utils.MessageMaker;

public class ColorPanel extends CaptorSign {
    
    private int x1, x2;
    private int y1, y2;
    private int z1, z2;
    private int winSignal;
    private int loseSignal;
    private int cooldown;
    private int timer;
    private boolean started;
    private int ten;
    ArrayList<Material> materials = new ArrayList<Material>();
    ArrayList<Location> locations = new ArrayList<Location>();

    public ColorPanel(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.winSignal = Integer.parseInt(line4[0]);
            this.loseSignal = Integer.parseInt(line4[1]);
            this.timer = Integer.parseInt(line4[2]);
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            int x, y, z;
            x = Integer.parseInt(line3[1]);
            y = Integer.parseInt(line3[2]);
            z = Integer.parseInt(line3[0]);
            this.x1 = this.location.getBlockX() - x;
            this.x2 = this.location.getBlockX() + x;
            this.y1 = this.location.getBlockY() - y;
            this.y2 = this.location.getBlockY() + y;
            this.z1 = this.location.getBlockZ() - z;
            this.z2 = this.location.getBlockZ() + z;
        }catch(Exception e) {
            SevenDungeons.log("ColorPanel's third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        World w = this.location.getWorld();
        for(int x = this.x1; x <= this.x2; x++) {
            for(int y = this.y1; y <= this.y2; y++) {
                for(int z = this.z1; z <= this.z2; z++) {
                    Location l = new Location(w,x,y,z);
                    Material m = l.getBlock().getType();
                    if(m.toString().contains("_CONCRETE")) {
                        this.locations.add(l.add(0.5, 0.5, 0.5));
                        this.materials.add(m);
                    }
                }
            }
        }
        this.ten = 0;
        this.cooldown = 10;
        this.started = false;
        this.game.getSigns2ticks().add(this);
    }
    
    @Override
    public void off() {
        for(int i = 0; i < this.locations.size(); i++) {
            this.locations.get(i).getBlock().setType(this.materials.get(i));
        }
        this.started = false;
        this.locations.clear();
        this.materials.clear();
        this.game.getSigns2ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
       
        if(this.started) {
            
            if(this.cooldown == 0) {
                this.game.activate(this.loseSignal, null);
                this.location.getWorld().playSound(this.location, Sound.BLOCK_BEACON_DEACTIVATE, 5, 1);
                this.off();
                return;
            }
            if(this.locations.size() == 0) {
                Firework fw = (Firework) this.game.getWorld().spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(1);
                fwm.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());
                fw.setFireworkMeta(fwm);
                fw.detonate();
                this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
                this.off();
                this.game.activate(this.winSignal, null);
                return;
            }
            for(int i = 0; i < this.locations.size(); i++) {
                for(Player p : this.game.getPlayers()) {
                    if(p.getLocation().distance(this.locations.get(i)) <= 1) {
                        this.locations.get(i).getBlock().setType(this.materials.get(i));
                        this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BIT, 3, ColorPanel.getTonality(this.materials.get(i)));
                        this.locations.remove(i);
                        this.materials.remove(i);
                        i--;
                        break;
                    }
                }
            }
            this.ten = (ten + 1) % 10;
            if(this.ten != 0) {
                return;
            }
            for(Player p : this.game.getPlayers()) {
                MessageMaker.actionBarMessage("§e§lSeconds left : " + this.cooldown, p);
            }
        }
        else {
            if(this.cooldown == 0) {
                this.cooldown = this.timer;
                this.started = true;
                this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 5, 1);
                return;
            }
            if(this.cooldown == 3) {
                for(Location l : this.locations) {
                    l.getBlock().setType(Material.GRAY_CONCRETE);
                }
            }
            this.ten = (ten + 1) % 10;
            if(this.ten != 0) {
                return;
            }
            for(Player p : this.game.getPlayers()) {
                MessageMaker.actionBarMessage("§e§lStarting in " + this.cooldown + "...", p);
            }
            if(this.cooldown <= 4 && this.cooldown > 1) {
                this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 5, 0.7f);
            }
        }
        this.cooldown--;
        return;
    }
    
    public static float getTonality(Material m) {
        switch(m){
        case BLACK_CONCRETE : return 0.5f;
            case YELLOW_CONCRETE : return 1.4f;
            case PINK_CONCRETE: return 0.7f;
            case LIME_CONCRETE : return 0.9f;
        case RED_CONCRETE : return 1.3f;
        case GREEN_CONCRETE : return 1f;
        case WHITE_CONCRETE : return 1.5f;
        default: return 1f;
        }
    }
}
