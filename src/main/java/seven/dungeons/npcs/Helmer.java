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

public class Helmer extends SevenNPC {
    
    public Helmer(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§b§lHelmer");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait)trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 :
            player.sendMessage("§b§lHelmer §r> Hi there ! Would you mind helping me ? I lost my favourite leather hat ...");
            this.state = 1;
            break;
        case 1 :
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§b§lHelmer §r> Did you find my leather hat ??\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to give\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 1 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§b§lHelmer §r> Did you find my leather hat ?? ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to give" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 2 :
            player.sendMessage("§b§lHelmer §r> Thanks again for my lovely hat.");
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
        switch(this.state) {
        case 1 :
            PlayerInventory playerInventory = player.getInventory();
            if (!playerInventory.contains(Material.LEATHER_HELMET)) {
                player.sendMessage("§b§lHelmer §r> Huh... you don't have my hat.");
                break;
            }
            ItemStack[] items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
                ItemStack is = items[i];
                if (is != null && is.getType() == Material.LEATHER_HELMET) {
                    is.setAmount(is.getAmount() - 1);
                    break;
                }
            }
            player.sendMessage("§b§lHelmer §r> Wow thanks ! I found some gems while fishing... Take them !");
            this.sign.getGame().activate(17, null);
            this.state = 2;
            break;
        }
    }

    @Override
    public void signal(int id) {
        // TODO Auto-generated method stub
        
    }

}
