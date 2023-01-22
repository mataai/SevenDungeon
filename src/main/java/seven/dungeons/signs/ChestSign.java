package seven.dungeons.signs;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class ChestSign extends ActivableSign {
    
    private String facing;
    private int chestId;

    public ChestSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.chestId = Integer.parseInt(line3[0]);
            if(line3.length > 1) {
                this.facing = line3[1];
            }
            else {
                this.facing = "w";
            }
            String line2 = this.sign.getLine(1).trim();
            if(line2 == null || line2.isEmpty()) {
                this.activationId = 0;
            }
            else {
                this.activationId = Integer.parseInt(line2);
            }
        }catch(Exception e) {
            SevenDungeons.log("ChestSign's line 2 and/or 3 is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest)this.location.getBlock().getState();
        Directional bs = (Directional)(chest.getBlock().getBlockData());
        switch(this.facing) {
            case "w": bs.setFacing(BlockFace.WEST); break;
            case "e": bs.setFacing(BlockFace.EAST); break;
            case "n": bs.setFacing(BlockFace.NORTH); break;
            case "s": bs.setFacing(BlockFace.SOUTH); break;
        }
        chest.getBlock().setBlockData(bs);
        ArrayList<ChestStockSign> chests = this.game.getChestStock(this.chestId);
        int total = 0;
        for(ChestStockSign css : chests) {
            total += css.getRate();
        }
        int random = (int) Math.ceil(total * Math.random());
        for(ChestStockSign css : chests) {
            random -= css.getRate();
            if(random <= 0) {
                ItemStack[] content = css.getInventory().clone();
                chest.getBlockInventory().setContents(content);
                return;
            }
        }
    }
    
    @Override
    public void off() {
        Chest chest = (Chest)this.location.getBlock().getState();
        chest.getBlockInventory().clear();
        this.location.getBlock().setType(Material.AIR);
    }

}
