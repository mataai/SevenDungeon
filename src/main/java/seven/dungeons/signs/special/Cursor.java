package seven.dungeons.signs.special;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.ActivableSign;

public class Cursor extends ActivableSign {
    
    private int west;
    private int north;
    private int south;
    private int east;
    private int signal;
    private Location cursor;

    public Cursor(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            String line4 = this.sign.getLine(3).trim();
            this.west = Integer.parseInt(line3[0]);
            this.north = Integer.parseInt(line3[1]);
            this.south = Integer.parseInt(line3[2]);
            this.east = Integer.parseInt(line3[3]);
            this.signal = Integer.parseInt(line4);
        }catch(Exception e) {
            SevenDungeons.log("Cursor's third and/or fourth line is wrong.");
        }
        this.cursor = this.location.clone();
    }

    @Override
    public void on() {
        this.location.getBlock().setType(Material.LIGHT_BLUE_CONCRETE);
        this.game.getSpecialSigns().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSpecialSigns().remove(this);
        this.cursor.getBlock().setType(Material.YELLOW_CONCRETE);
    }
    
    @Override
    public void signal(int id) {
        this.cursor.getBlock().setType(Material.LIGHT_GRAY_CONCRETE);
        if(id == this.west) this.cursor.add(-1,0,0);
        if(id == this.north) this.cursor.add(0,0,-1);
        if(id == this.south) this.cursor.add(0,0,1);
        if(id == this.east) this.cursor.add(1,0,0);
        Block block = this.cursor.getBlock();
        switch(block.getType()) {
        case LIGHT_GRAY_CONCRETE :
            block.setType(Material.LIGHT_BLUE_CONCRETE);
            break;
        case GRAY_CONCRETE :
            this.cursor = this.location.clone();
            this.cursor.getBlock().setType(Material.LIGHT_BLUE_CONCRETE);
            break;
        case LIME_CONCRETE :
            this.game.activate(this.signal, null);
            this.off();
            break;
        default: break;
        }
            
    }
}
