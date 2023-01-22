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
public class SpaceBalloons extends SevenNPC {

    public SpaceBalloons(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VINDICATOR, "§c§lPol");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(5);
        trait1.toggle();
    }


    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§c§lPol §r> Wonder how there's gravity ? That's MY work ! Want to play a game to see how much we can control gravity ?");
            this.sign.getGame().activate(1007, null);
            this.state = 1;
            break;
        case 2 :

            TextComponent text = new TextComponent("§c§lPol §r> You got the phantom membrane ? ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 5 :
            player.sendMessage("§c§lPol §r> Woohoo look ! I'm staying here. I'm affected by gravity !");
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
            if (!playerInventory.contains(Material.PHANTOM_MEMBRANE)) {
              player.sendMessage("§c§lPol §r> No, that's not a phantom membrane.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.PHANTOM_MEMBRANE) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§c§lPol §r> Good luck ! Have fun ! (-1 phantom membrane)");
            }
            this.sign.getGame().activate(142, null);
            this.state = 3;
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 1008 :
            this.msgAll("§c§lPol §r> Bring me a phantom membrane and I'll entertain you !");
            this.state = 2;
            break;
        case 143 :
            this.msgAll("§c§lPol §r> Wow ! You mastered gravity !");
            this.state = 5;
            break;
        case 144 :
            this.msgAll("§c§lPol §r> Gravity isn't that simple ! Nice try !");
            this.state = 2;
            break;
        }
        
    }

}
