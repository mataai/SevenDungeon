package seven.dungeons.signs.special;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;

import java.sql.Array;
import java.util.ArrayList;

public class GuessPuzzle extends CaptorSign {

    private static Material[] colors = {Material.LIGHT_BLUE_CONCRETE, Material.RED_CONCRETE, Material.YELLOW_CONCRETE, Material.PURPLE_CONCRETE};
    private int signalOn;
    private int bonusSignalOn;
    private Location buttonBlock, outputBoard;
    private int tries = 0;
    private int cooldown = 0;

    private ArrayList<Material> colorCode = new ArrayList<Material>();
    private ArrayList<Integer> colorNumbers = new ArrayList<Integer>();

    public GuessPuzzle(Sign sign, Game game) {
        super(sign, game);
        try{
            String[] line2 = this.sign.getLine(1).trim().split(" ");
            this.signalOn = Integer.parseInt(line2[1]);
            this.bonusSignalOn = Integer.parseInt(line2[2]);
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.buttonBlock = new Location(this.sign.getWorld(), Integer.parseInt(line3[0]), Integer.parseInt(line3[1]), Integer.parseInt(line3[2]));
            String[] line4 = this.sign.getLine(3).trim().split(" ");
            this.outputBoard = new Location(this.sign.getWorld(), Integer.parseInt(line4[0]), Integer.parseInt(line4[1]), Integer.parseInt(line4[2]));
        }
        catch(Exception e){
            SevenDungeons.log("GuessPuzzle's third and/or fourth line is wrong.");
        }
    }

    @Override
    public void on(){
        this.colorCode.clear();
        this.colorNumbers.clear();
        Location l = this.sign.getLocation().clone().add(0,-1,0);
        for(int i = 0; i < 4; i++){
            this.colorCode.add(GuessPuzzle.colors[(int)Math.floor(Math.random()*4)]);
            this.colorNumbers.add(0);
            l.getBlock().setType(GuessPuzzle.colors[(int)Math.floor(Math.random()*4)]);
            l.add(1,0,0);
        }
        for(int i = 0; i < 4; i++){
            int index = GuessPuzzle.getColorIndex(this.colorCode.get(i));
            this.colorNumbers.set(index, this.colorNumbers.get(index) + 1);
        }
        this.game.getSigns5ticks().add(this);
    }

    @Override
    public void off(){
        this.game.getSigns5ticks().remove(this);
    }

    @Override
    public void isTriggered(){
        this.cooldown = Math.max(0, this.cooldown - 1);
        Location l = this.sign.getLocation().clone().add(0, -1, 0);
        for(int i = 0; i < 4; i++){
            Block b = l.getBlock();
            if(b.hasMetadata("xgp")){
                l.getWorld().playSound(l, Sound.ENTITY_ITEM_PICKUP, 1, 0.5f);
                b.removeMetadata("xgp", this.game.getPlugin());
                b.setType(GuessPuzzle.colors[(GuessPuzzle.getColorIndex(b.getType()) + 1) % 4]);
            }
            l.add(1,0,0);
        }
        if(this.buttonBlock.getBlock().isBlockPowered() && this.cooldown == 0){
            this.cooldown = 12;
            Location input = this.sign.getLocation().clone().add(0,-1,0);
            Location output = this.outputBoard.clone();
            int outputIndex = 0;
            int valid = 0;
            this.tries++;

            ArrayList<Integer> placed = new ArrayList<Integer>();
            ArrayList<Integer> wellplaced = new ArrayList<Integer>();
            for(int i = 0; i < 4; i ++){
                placed.add(0);
                wellplaced.add(0);
            }

            // Inspect output
            for(int i = 0; i < 4; i++){
                Material mInput = input.getBlock().getType();
                int index = GuessPuzzle.getColorIndex(mInput);
                if(mInput == this.colorCode.get(i)){
                    /*output.clone().add(Math.floor(outputIndex/2.0), 0, outputIndex % 2).getBlock().setType(Material.BLACK_CONCRETE);
                    valid++;
                    outputIndex++;*/
                    wellplaced.set(index, wellplaced.get(index) + 1);
                    //placed.set(index, placed.get(index) + 1);
                }
                else if(this.colorCode.contains(mInput)){
                    /*output.clone().add(Math.floor(outputIndex/2.0), 0, outputIndex % 2).getBlock().setType(Material.WHITE_CONCRETE);
                    outputIndex++;*/
                    placed.set(index, placed.get(index) + 1);
                }
                input.add(1, 0, 0);
            }

            // Make output

            // Make black output
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < wellplaced.get(i); j++){
                    output.clone().add(Math.floor(outputIndex/2.0), 0, outputIndex % 2).getBlock().setType(Material.BLACK_CONCRETE);
                    valid++;
                    outputIndex++;
                }
            }

            // Make white output
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < placed.get(i); j++){
                    if(j + 1 <= this.colorNumbers.get(i) - wellplaced.get(i)){
                        output.clone().add(Math.floor(outputIndex/2.0), 0, outputIndex % 2).getBlock().setType(Material.WHITE_CONCRETE);
                        outputIndex++;
                    }
                }
            }

            // Make gray output
            for(int i = outputIndex; i < 4; i++){
                output.clone().add(Math.floor(i/2.0), 0, i % 2).getBlock().setType(Material.LIGHT_GRAY_CONCRETE);
            }

            // If code is correct
            if(valid == 4){
                this.location.getWorld().playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 2, 0.5f);
                this.game.activate(this.signalOn, null);
                if(this.tries < 8){
                    this.game.activate(this.bonusSignalOn, null);
                }
                this.off();
            }

            // If code is incorrect
            else{
                this.location.getWorld().playSound(this.location, Sound.ITEM_SHIELD_BREAK, 2, 0.5f);
            }
        }
    }

    private static int getColorIndex(Material m){
        for(int i = 0; i < GuessPuzzle.colors.length; i++){
            if(GuessPuzzle.colors[i] == m){
                return i;
            }
        }
        return 0;
    }

}
