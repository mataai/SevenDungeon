package seven.dungeons.signs.special;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

import java.util.ArrayList;

public class Painting extends CaptorSign {

    private static Material[] playerColors = {Material.LIGHT_BLUE_CONCRETE, Material.RED_CONCRETE, Material.YELLOW_CONCRETE, Material.PURPLE_CONCRETE};
    private ArrayList<Material> solution = new ArrayList<Material>();
    private int x1, x2, z1, z2;
    private int signalOn;

    public Painting(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line2 = this.sign.getLine(1).trim().split(" ");
            this.signalOn = Integer.parseInt(line2[1]);
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.x1 = this.location.getBlockX();
            this.z1 = this.location.getBlockZ();
            this.x2 = this.x1 + Integer.parseInt(line3[0]);
            this.z2 = this.z1 + Integer.parseInt(line3[1]);
        }catch(Exception e) {
            SevenDungeons.log("Painting's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.solution.clear();
        int y = this.location.getBlockY() - 1;
        World w = this.location.getWorld();
        for(int x = this.x1; x <= this.x2; x++){
            for(int z = this.z1; z <= this.z2; z++){
                Location l = new Location(w, x, y, z);
                this.solution.add(l.getBlock().getType());
                l.getBlock().setType(Material.LIGHT_GRAY_CONCRETE);
            }
        }
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        for(int i = 0; i < this.game.getPlayers().size(); i++){
            Player p = this.game.getPlayers().get(i);
            Location pLoc = p.getLocation();
            double x = pLoc.getX();
            int y = pLoc.getBlockY();
            double z = pLoc.getZ();
            if(!p.isOnGround() || y != this.location.getBlockY() || x < this.x1 || x > this.x2 + 1 || z < this.z1 || z > this.z2 + 1){
                continue;
            }
            Block b = pLoc.clone().add(0,-1,0).getBlock();
            if(b.getType() != Painting.playerColors[i]){
                b.setType(Painting.playerColors[i]);
                pLoc.getWorld().playSound(pLoc, Sound.ENTITY_ITEM_PICKUP, 0.5f, 0.5f);
            }
            if(this.isPaintingCorrect()){
                this.game.activate(this.signalOn, null);
                pLoc.getWorld().playSound(pLoc, Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                this.off();
            }
        }
    }

    private boolean isPaintingCorrect(){
        int y = this.location.getBlockY() - 1;
        int i = 0;
        World w = this.location.getWorld();
        for(int x = this.x1; x <= this.x2; x++) {
            for (int z = this.z1; z <= this.z2; z++) {
                Location l = new Location(w, x, y, z);
                if(l.getBlock().getType() != this.solution.get(i)){
                    return false;
                }
                i++;
            }
        }
        return true;
    }

}
