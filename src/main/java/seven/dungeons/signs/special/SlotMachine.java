package seven.dungeons.signs.special;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.signs.ChestStockSign;

public class SlotMachine extends CaptorSign {
    
    private int stopId;
    
    private int startId;
    
    private int chestId;
    
    private int baseSignal;
    
    private boolean polarity;
    
    private ArrayList<Material> items = new ArrayList<>();
    
    private Item groundItem = null;
    
    private int currentIndex;
    
    private FixedMetadataValue xsm = new FixedMetadataValue((Plugin)this.game.getPlugin(), "xsm");
    
    private float pitch;
    
    private int speed;
    
    private int cooldown;

    //Uses standard signals 1080
    public SlotMachine(Sign sign, Game game) {
        super(sign, game);
        try {
          this.location.add(0.5D, 0.0D, 0.5D);
          this.startId = this.activationId;
          this.stopId = Integer.parseInt(this.sign.getLine(1).trim().split(" ")[1]);
          String[] line3 = this.sign.getLine(2).trim().split(" ");
          this.chestId = Integer.parseInt(line3[0]);
          this.speed = Integer.parseInt(line3[1]);
          this.cooldown = 0;
          String[] line4 = this.sign.getLine(3).trim().split(" ");
          this.baseSignal = Integer.parseInt(line4[0]);
          this.polarity = true;
          if (line4[1].equalsIgnoreCase("off"))
            this.polarity = false; 
          this.currentIndex = 0;
          this.pitch = (float)(Math.random() + 0.5D);
        } catch (Exception e) {
          SevenDungeons.log("SlotMachine's second, third and/or fourth line is wrong.");
        }
    }
    
    @Override
    public void on() {
        ChestStockSign chest = this.game.getChestStock(this.chestId).get(0);
        int i = 0;
        while (i < 8) {
          ItemStack item = chest.getInventory()[i];
          if (item == null)
            break; 
          this.items.add(item.getType());
          i++;
        } 
        this.activationId = this.stopId;
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        int signal = this.baseSignal + this.items.indexOf(this.groundItem.getItemStack().getType());
        if (this.activationId == this.stopId) {
          this.game.getSigns5ticks().remove(this);
          this.activationId = this.startId;
          if (this.polarity) {
            this.game.activate(signal, null);
            return;
          } 
          this.game.deactivate(signal, null);
        } else {
          if (!this.polarity) {
            this.game.activate(signal, null);
          } else {
            this.game.deactivate(signal, null);
          } 
          this.groundItem.remove();
          this.groundItem = null;
          this.items.clear();
        } 
    }
    
    @Override
    public void isTriggered() {
        if (this.cooldown <= 0) {
          if (this.groundItem != null && this.groundItem.isOnGround())
            this.groundItem.remove(); 
          ItemStack i = new ItemStack(this.items.get(this.currentIndex), 1);
          ItemMeta meta = i.getItemMeta();
          meta.setDisplayName(String.valueOf(this.stopId) + "a" + this.currentIndex);
          i.setItemMeta(meta);
          this.groundItem = this.location.getWorld().dropItem(this.location, i);
          this.groundItem.setVelocity(new Vector(0, 0, 0));
          this.groundItem.setMetadata("xsm", (MetadataValue)this.xsm);
          this.location.getWorld().playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BIT, 3, this.pitch);
          incrementCurrentIndex();
          this.cooldown = this.speed;
        } 
        this.cooldown--;
      }
    
    public void incrementCurrentIndex() {
        this.currentIndex++;
        if (this.currentIndex >= this.items.size())
          this.currentIndex = 0; 
      }

}
