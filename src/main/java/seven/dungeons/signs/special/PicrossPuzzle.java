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

public class PicrossPuzzle extends CaptorSign {

    private int signalOn;
    private int xh;
    private int zh;
    private ArrayList<ArrayList<Material>> solution = new ArrayList<ArrayList<Material>>();

    public PicrossPuzzle(Sign sign, Game game) {
        super(sign, game);
        try{
            String[] line2 = this.sign.getLine(1).trim().split(" ");
            this.signalOn = Integer.parseInt(line2[1]);
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.xh = Integer.parseInt(line3[0]);
            this.zh = Integer.parseInt(line3[1]);
        }
        catch(Exception e){
            SevenDungeons.log("PicrossPuzzle's third and/or fourth line is wrong.");
        }
    }

    @Override
    public void on(){
        this.game.getSigns5ticks().add(this);
        Location l = this.location.clone().add(0,-1,0);
        for(int x = 0; x <= this.xh; x++){
            this.solution.add(new ArrayList<Material>());
            for(int z = 0; z <= this.zh; z++){
                Block b = l.clone().add(x, 0, z).getBlock();
                this.solution.get(x).add(b.getType());
                b.setType(Material.LIGHT_GRAY_CONCRETE);
            }
        }
    }

    @Override
    public void off(){
        this.game.getSigns5ticks().remove(this);
    }

    @Override
    public void isTriggered(){

        boolean didSomethingChange = false;

        Location l = this.location.clone().add(0,-1,0);
        for(int x = 0; x <= this.xh; x++) {
            for (int z = 0; z <= this.zh; z++) {
                Block b = l.clone().add(x, 0, z).getBlock();
                if(b.hasMetadata("xgp")){
                    l.getWorld().playSound(l, Sound.ENTITY_ITEM_PICKUP, 1, 0.5f);
                    b.removeMetadata("xgp", this.game.getPlugin());
                    didSomethingChange = true;
                    b.setType(b.getType() == Material.WHITE_CONCRETE ? Material.BLACK_CONCRETE : (b.getType() ==  Material.BLACK_CONCRETE ? Material.LIGHT_GRAY_CONCRETE : Material.WHITE_CONCRETE));
                    if(this.isColCorrect(z)){
                        this.colorCol(Material.LIME_CONCRETE, z);
                    }
                    else{
                        this.colorCol(Material.RED_CONCRETE, z);
                    }
                    if(this.isRowCorrect(x)){
                        this.colorRow(Material.LIME_CONCRETE, x);
                    }
                    else{
                        this.colorRow(Material.RED_CONCRETE, x);
                    }
                }
            }
        }

        if(didSomethingChange){

            for(int x = 0; x <= this.xh; x++) {
                if(!this.isRowCorrect(x)){
                    return;
                }
                for (int z = 0; z <= this.zh; z++) {
                    if(!this.isColCorrect(z)){
                        return;
                    }
                }
            }

            this.game.activate(this.signalOn, null);
            this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 2, 0.5f);
            this.off();
        }

    }

    private void colorRow(Material m, int x){
        Location l = this.location.clone().add(x, -1, 0);
        l.clone().add(0, 0, -1).getBlock().setType(m);
        l.clone().add(0, 0, this.zh + 1).getBlock().setType(m);
    }

    private void colorCol(Material m, int z){
        Location l = this.location.clone().add(0, -1, z);
        l.clone().add(-1, 0, 0).getBlock().setType(m);
        l.clone().add(this.xh + 1, 0, 0).getBlock().setType(m);
    }

    private boolean isRowCorrect(int x){
        Location l = this.location.clone().add(x, -1, 0);
        for(int z = 0; z <= this.zh; z++){
            if(l.clone().add(0, 0, z).getBlock().getType() != this.solution.get(x).get(z)){
                return false;
            }
        }
        return true;
    }

    private boolean isColCorrect(int z){
        Location l = this.location.clone().add(0, -1, z);
        for(int x = 0; x <= this.xh; x++){
            if(l.clone().add(x, 0, 0).getBlock().getType() != this.solution.get(x).get(z)){
                return false;
            }
        }
        return true;
    }

}
