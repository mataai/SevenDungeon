package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import seven.dungeons.signs.NPCSign;

//Mushroom Mayhem Game NPC - USES STANDARD SIGNALS 1020 to 1032
public class SpaceLasers extends SevenNPC {

    public SpaceLasers(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.HORSE, "§6§lRonda");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(5);
        trait1.toggle();
    }


    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§6§lRonda §r> Yes, I'm a horse. Anyway, you look like you enjoy running around in circles ?");
            this.sign.getGame().activate(1013, null);
            this.state = 1;
            break;
        case 2 :

            TextComponent text = new TextComponent("§6§lRonda §r> Did you find a gold nugget ? ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 5 :
            player.sendMessage("§6§lRonda §r> You did well. Perhaps you were a horse in a previous life.");
            break;
        }
    }

    @Override
    public void handleFeedback(Player player, int state, int feedback) {
        if (this.sign.getLocation().distance(player.getLocation()) > 5.0D) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "You're too far to answer this.");
            return;
        } 
        if (state != this.state) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "It's too late to answer this.");
            return;
        }
        PlayerInventory playerInventory;
        ItemStack[] items;
        switch(this.state) {
        case 2 :
            playerInventory = player.getInventory();
            if (!playerInventory.contains(Material.GOLD_NUGGET)) {
              player.sendMessage("§6§lRonda §r> This is neither gold nor shaped like a nugget.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.GOLD_NUGGET) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§6§lRonda §r> Merry-Go ! (-1 gold nugget");
            }
            this.sign.getGame().activate(205, null);
            this.state = 3;
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 1014 :
            this.msgAll("§6§lRonda §r> Get me a gold nugget to play.");
            this.state = 2;
            break;
        case 206 :
            this.msgAll("§6§lRonda §r> Amazing !");
            this.state = 5;
            break;
        case 207 :
            this.msgAll("§6§lRonda §r> Looks like some of you are dead weight");
            this.state = 2;
            break;
        }
        
    }

}
