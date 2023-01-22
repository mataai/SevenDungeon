package seven.dungeons.signs.special;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

import java.util.ArrayList;

public class FlipPuzzle extends CaptorSign {

    private int signalOn;

    public FlipPuzzle(Sign sign, Game game) {
        super(sign, game);
        try{
            String[] line2 = this.sign.getLine(1).trim().split(" ");
            this.signalOn = Integer.parseInt(line2[1]);
        }
        catch(Exception e){
            SevenDungeons.log("FlipPuzzle's third and/or fourth line is wrong.");
        }
    }

    @Override
    public void on(){
        Location l = this.location.clone().add(-1,-1,-1);
        for(int x = 0; x < 3; x++){
            for(int z = 0; z < 3; z++){
                l.clone().add(x, 0, z).getBlock().setType(Math.random() < 0.5 ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE);
            }
        }
        this.game.getSigns5ticks().add(this);
    }

    @Override
    public void off(){
        this.game.getSigns5ticks().remove(this);
    }

    @Override
    public void isTriggered(){

        Location l = this.location.clone().add(-1,-1,-1);
        for(int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Location l2 = l.clone().add(x, 0, z);
                Block b = l2.getBlock();
                if(b.hasMetadata("xgp")){
                    l.getWorld().playSound(l, Sound.ENTITY_ITEM_PICKUP, 1, 0.5f);
                    b.removeMetadata("xgp", this.game.getPlugin());
                    b.setType(b.getType() == Material.WHITE_CONCRETE ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE);
                    if(x > 0){
                        Block b2 = l2.clone().add(-1,0,0).getBlock();
                        b2.setType(b2.getType() == Material.WHITE_CONCRETE ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE);
                    }
                    if(x < 2){
                        Block b2 = l2.clone().add(1,0,0).getBlock();
                        b2.setType(b2.getType() == Material.WHITE_CONCRETE ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE);
                    }
                    if(z > 0){
                        Block b2 = l2.clone().add(0,0,-1).getBlock();
                        b2.setType(b2.getType() == Material.WHITE_CONCRETE ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE);
                    }
                    if(z < 2){
                        Block b2 = l2.clone().add(0,0,1).getBlock();
                        b2.setType(b2.getType() == Material.WHITE_CONCRETE ? Material.BLACK_CONCRETE : Material.WHITE_CONCRETE);
                    }

                    if(this.isDone()){
                        this.game.activate(this.signalOn, null);
                        this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 2, 0.5f);
                        this.off();
                    }
                }
            }
        }

    }

    private boolean isDone(){
        Location l = this.location.clone().add(-1,-1,-1);
        Material m = l.getBlock().getType();
        for(int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Material m2 = l.clone().add(x,0,z).getBlock().getType();
                if(m != m2){
                    return false;
                }
            }
        }
        return true;
    }

}
