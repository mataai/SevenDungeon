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

public class GameHouse2 extends SevenNPC {
    
    private int version;
    
    public GameHouse2(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§e§lBarnaby");
        try {
            this.version = Integer.parseInt(this.sign.getSign().getLine(3).trim());
        } catch (Exception e) {
            this.version = 1;
        }
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            player.sendMessage("§e§lBarnaby §r> Welcome ! In the color panel game, you have 15 seconds to turn on " +
                    "all the color panels. Step on a panel to turn it on !"); 
            this.state = 1; break;
        case 1 : 
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§e§lBarnaby §r> Ready to play ? The cost is 1 glistering melon slice.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 1 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§e§lBarnaby §r> Ready to play ? The cost is 1 glistering melon slice. ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 2 : player.sendMessage("§e§lBarnaby §r> Hurry ! You'll run out of time !"); break;
        case 3 :
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§e§lBarnaby §r> Ready for a bigger challenge ? The cost is still 1 glistering melon slice.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 3 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text1 = new TextComponent("§e§lBarnaby §r> Ready for a bigger challenge ? The cost is still 1 glistering melon slice. ");

            TextComponent yes1 = new TextComponent("§a§l[Yes]");
            yes1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 3 0"));

            text1.addExtra(yes1);
            player.spigot().sendMessage(text1);
            break;
        case 4 :
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBarnaby §r> Leave the game house before we go bankrupt please !");
            }
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
        case 1 :
            playerInventory = player.getInventory();
            if (!playerInventory.contains(Material.GLISTERING_MELON_SLICE)) {
              player.sendMessage("§e§lBarnaby §r> The cost is 1 glistering melon slice and you don't have any.");
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
                p.sendMessage("§e§lBarnaby §r> Watch out ! The color panel game begins in 10 seconds. (-1 glistering melon slice)");
            }
            this.sign.getGame().activate(7, null);
            this.state = 2;
            break;
        case 3 :
            playerInventory = player.getInventory();
            if (!playerInventory.contains(Material.GLISTERING_MELON_SLICE)) {
              player.sendMessage("§e§lBarnaby §r> The cost is 1 glistering melon slice and you don't have any.");
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
                p.sendMessage("§e§lBarnaby §r> Watch out ! The color panel game begins in 10 seconds. (-1 melon slice)");
            }
            this.sign.getGame().activate(10, null);
            this.state = 2;
            break;
        
        }
              
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 8 :
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBarnaby §r> You won ! Have 2 gems. You may play again if you like challenges.");
            }
            this.state = 3;
            break;
        case 9 : 
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBarnaby §r> Too bad. You didn't make it in time.");
            }
            this.state = 1;
            break;
        case 11 :
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBarnaby §r> Nothing is right anymore ! Have 3 gems.");
            }
            this.state = 4;
            break;
        case 12 :
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBarnaby §r> Too bad. You didn't make it in time.");
            }
            this.state = 3;
            break;
        }
        
            
        
    }

}
