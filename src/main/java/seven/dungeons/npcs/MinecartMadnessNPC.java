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

// Minecart Madness NPC - USES STANDARD SIGNALS 1060 to 1069
public class MinecartMadnessNPC extends SevenNPC {
    
    public MinecartMadnessNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VINDICATOR, "§7§lHerbert");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§7§lHerbert §r> Ahem, hi. You wish to make a Minecart Tour ?");
            this.sign.getGame().activate(1060, null);
            this.state = 1;
            break;
        case 2 :
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§7§lHerbert §r> Ready to play ? The cost is 1 iron ingot.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 2 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§7§lHerbert §r> Ready to play ? The cost is 1 iron ingot. ");

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
            if (!playerInventory.contains(Material.IRON_INGOT)) {
              player.sendMessage("§7§lHerbert §r> The cost is 1 iron ingot and you don't have any.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.IRON_INGOT) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§7§lHerbert §r> Watch out ! The Minecart Tour begins in 10 seconds. (-1 iron ingot)");
            }
            this.sign.getGame().activate(1062, null);
            this.state = 3;
            break;
        case 4 :
            player.sendMessage("§7§lHerbert §r> Yeah yeah, you were great.");
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 1061 :
            this.msgAll("§7§lHerbert §r> Hit as many target as possible to make points during the minecart ride. Tell me when you are ready.");
            this.state = 2;
            break;
        case 1063 :
            this.msgAll("§7§lHerbert §r> You can use a bow ! I'll give you some gems.");
            this.state = 4;
            break;
        case 1064 :
            this.msgAll("§7§lHerbert §r> Not a surprise ! Try again if you want.");
            this.state = 2;
            break;
        }
    }

}
