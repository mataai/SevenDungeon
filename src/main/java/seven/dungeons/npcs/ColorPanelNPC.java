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
public class ColorPanelNPC extends SevenNPC {

    public ColorPanelNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§e§lBob");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }


    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§e§lBob §r> Would you like to try my Color Panel game ?");
            this.sign.getGame().activate(1100, null);
            this.state = 1;
            break;
        case 2 :
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§5§lHilda §r> Ready to play ? The cost is 1 nether wart.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 2 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§e§lBob §r> Ready to play ? The cost is 1 glistering melon. ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        /*case 4 :
            TextComponent text1 = new TextComponent("§5§lHilda §r> Ready to play for more gems ? The cost is 1 nether wart. ");

            TextComponent yes1 = new TextComponent("§a§l[Yes]");
            yes1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 4 0"));

            text1.addExtra(yes1);
            player.spigot().sendMessage(text1);
            break;*/
        case 5 :
            player.sendMessage("§e§lBob §r> Aren't these two fish pretty ? I wonder if Nintendo would sue me though.");
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
            if (!playerInventory.contains(Material.GLISTERING_MELON_SLICE)) {
              player.sendMessage("§e§lBob §r> The cost is 1 glistering melon and you don't have any.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.GLISTERING_MELON_SLICE) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBob §r> Watch out ! The Color Panel game begins in 10 seconds. (-1 glistering melon)");
            }
            this.sign.getGame().activate(1104, null);
            this.state = 3;
            break;
        /*case 4 :
            playerInventory = player.getInventory();
            if (!playerInventory.contains(Material.NETHER_WART)) {
              player.sendMessage("§5§lHilda §r> The cost is 1 nether wart and you don't have any.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.NETHER_WART) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§5§lHilda §r> Watch out ! The mushroom mayhem game begins in 10 seconds. (-1 nether wart)");
            }
            this.sign.getGame().activate(1030, null);
            this.state = 3;
            break;*/
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 1101 :
            this.msgAll("§e§lBob §r> Here's how it works : The paintings below will turn gray. All you have to do is walk over every block at least once.");
            this.sign.getGame().activate(1102, null);
            break;
        case 1103 :
            this.msgAll("§e§lBob §r> Walking over a block colors it. You have 32 seconds to color both paintings !");
            this.state = 2;
            break;
        case 1105 :
            this.msgAll("§e§lBob §r> You did it ! Take some gems.");
            //this.state = 4;
            this.state = 5;
            break;
        case 1106 :
            this.msgAll("§e§lBob §r> You could not make it in time !");
            this.state = 2;
            break;
        }
        
    }

}
