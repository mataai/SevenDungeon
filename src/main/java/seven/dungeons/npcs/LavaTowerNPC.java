package seven.dungeons.npcs;

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

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import seven.dungeons.signs.NPCSign;

// Lava Tower Game NPC - USES STANDARD SIGNALS 1040 to 1049
public class LavaTowerNPC extends SevenNPC{
    
    public LavaTowerNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.BLAZE, "§c§lBlazy");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§c§lBlazy §r> *cough* Hey there. Want to play Lava Tower when I catch up my breath ?");
            this.sign.getGame().activate(1040, null);
            this.state = 1;
            break;
        case 2 :
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§c§lBlazy §r> Ready to play ? The cost is 1 blaze powder.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 2 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§c§lBlazy §r> Ready to play ? The cost is 1 blaze powder. ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
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
            if (!playerInventory.contains(Material.BLAZE_POWDER)) {
              player.sendMessage("§c§lBlazy §r> The cost is 1 blaze powder and you don't have any.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.BLAZE_POWDER) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§c§lBlazy §r> Watch out ! The lava tower game begins in 10 seconds. (-1 blaze powder)");
            }
            this.sign.getGame().activate(1044, null);
            this.state = 3;
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 1041 : 
            this.msgAll("§c§lBlazy §r> Alright. This game is rather simple : climb up the tower before the lava catches you !"); 
            this.sign.getGame().activate(1042, null);
            break;
        case 1043 : 
            this.msgAll("§c§lBlazy §r> If enough of you can reach high, you'll win the game. Tell me when you are ready."); 
            this.state = 2;
            break;
        case 1045 :
            this.msgAll("§c§lBlazy §r> You did it. *cough some gems*");
            this.sign.getGame().activate(1047, null);
            this.state = 4;
            break;
        case 1046 :
            this.msgAll("§c§lBlazy §r> This game is too hot for you. You can try again.");
            this.state = 2;
            break;
        }
    }

}
