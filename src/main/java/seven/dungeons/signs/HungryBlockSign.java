package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class HungryBlockSign extends CaptorSign {
    
    private int blockId;
    private int signal;
    private boolean polarity;

    public HungryBlockSign(Sign sign, Game game) {
        super(sign, game);
        try {
            this.blockId = Integer.parseInt(this.sign.getLine(2).trim());
            String[] signalLine = this.sign.getLine(3).trim().split(" ");
            this.signal = Integer.parseInt(signalLine[0]);
            if(signalLine[1].matches("on")) {
                this.polarity = true;
            }
            else {
                this.polarity = false;
            }
        }catch(Exception e) {
            SevenDungeons.log("HungryBlock's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        Block block = this.game.getBlockStock(this.blockId);
        if(block == null) {
            return ;
        }
        for(Player p : this.game.getPlayers()) {
            if(this.location.distance(p.getLocation()) > 2) {
                continue;
            }
            PlayerInventory pi = p.getInventory();
            if(pi.contains(block.getType())) {
                ItemStack[] items = pi.getContents();
                for (int i = 0; i < items.length; i++ ) {
                    ItemStack is = items[i];
                    if (is != null && is.getType() == block.getType()) {
                        is.setAmount(is.getAmount() - 1);
                        break;
                    }
                }
                this.trigger(null);
            }
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.off();
        Block block = this.game.getBlockStock(this.blockId);
        if (block == null) {
          this.location.getBlock().setType(Material.AIR);
        }
        else {
            this.location.getBlock().setType(block.getType());
            this.location.getBlock().setBlockData(block.getBlockData());
        }
        this.location.getWorld().playSound(this.location, Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 3, 1);
        if(this.polarity) {
            this.game.activate(signal, null);
            return;
        }
        this.game.deactivate(signal, null);
    }

}
