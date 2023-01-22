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


// Fish Frenzy Game NPC - USES STANDARD SIGNALS 1000 to 1009
public class FishFrenzyNPC extends SevenNPC{
    
    public FishFrenzyNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§b§lNemo");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }


    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§b§lNemo §r> Aye ! Ready for a §b§lFish Frenzy §rGame ?");
            this.sign.getGame().activate(1000, null);
            this.state = 1;
            break;
        case 2 : 
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§b§lNemo §r> Ready to play ? The cost is 1 prismarine shard.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 2 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("Nemo §r> Ready to play ? The cost is 1 prismarine shard. ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 4 :
            player.sendMessage("§b§lNemo §r> What a great fisher you are !");
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
            if (!playerInventory.contains(Material.PRISMARINE_SHARD)) {
              player.sendMessage("§b§lNemo §r> The cost is 1 prismarine shard and you don't have any.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.PRISMARINE_SHARD) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§b§lNemo §r> Watch out ! The fish frenzy game begins in 10 seconds. (-1 prismarine shard)");
            }
            this.sign.getGame().activate(1006, null);
            this.state = 3;
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
            case 1001 : 
                this.msgAll("§b§lNemo §r> The rules are quite simple : Catch as many fish as possible during the allowed time."); 
                this.sign.getGame().activate(1002, null);
                break;
            case 1003 : 
                this.msgAll("§b§lNemo §r> You must cooperate with your team to make the highest total score. Watch out for pufferfishes !"); 
                this.sign.getGame().activate(1004, null);
                break;
            case 1005 :
                this.msgAll("§b§lNemo §r> Tell me when you are all ready.");
                this.state = 2;
                break;
            case 1007 :
                this.msgAll("§b§lNemo §r> Well done. Reel in these gems !");
                this.sign.getGame().activate(1009, null);
                this.state = 4;
                break;
            case 1008 :
                this.msgAll("§b§lNemo §r> Too bad. You can try again.");
                this.state = 2;
                break;
        }
        
    }

}
