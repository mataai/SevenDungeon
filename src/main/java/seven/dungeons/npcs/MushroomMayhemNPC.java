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

//Mushroom Mayhem Game NPC - USES STANDARD SIGNALS 1020 to 1032
public class MushroomMayhemNPC extends SevenNPC {
    
    public MushroomMayhemNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.WITCH, "§5§lHilda");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }


    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§5§lHilda §r> Right on time ! You'll help me to pick mushrooms. If you get enough, I'll reward you with gems !");
            this.sign.getGame().activate(1020, null);
            this.state = 1;
            break;
        case 2 :
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§5§lHilda §r> Ready to play ? The cost is 1 nether wart.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 2 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§5§lHilda §r> Ready to play ? The cost is 1 nether wart. ");

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
            player.sendMessage("§5§lHilda §r> I said leave me !");
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
                p.sendMessage("§5§lHilda §r> Watch out ! The Mushroom Mayhem game begins in 10 seconds. (-1 nether wart)");
            }
            this.sign.getGame().activate(1026, null);
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
        case 1021 :
            this.msgAll("§5§lHilda §r> Hear me well : §cRight-click §rmushrooms to pick them. Your team gets points for every mushroom picked.");
            this.sign.getGame().activate(1022, null);
            break;
        case 1023 :
            this.msgAll("§5§lHilda §r> If you wait long enough, mushrooms glister and grow. Then, they are worth more and more points !");
            this.sign.getGame().activate(1024, null);
            break;
        case 1025 :
            this.msgAll("§5§lHilda §r> But if you wait too long ... mushrooms will explode ! Tell me when you're ready... you'll see that it's hard to be a witch !");
            this.state = 2;
            break;
        case 1027 :
            this.msgAll("§5§lHilda §r> Not bad, does your back hurts too ? Take these gems.");
            this.sign.getGame().activate(1029, null);
            //this.state = 4;
            this.state = 5;
            break;
        case 1028 :
            this.msgAll("§5§lHilda §r> I knew you weren't cut out for it. You may try again.");
            this.state = 2;
            break;
        /*case 1031 :
            this.msgAll("§5§lHilda §r> Well well, you're almost as good as me. Pick these gems and leave me !");
            this.state = 5;
            break;*/
        /*case 1032 :
            this.msgAll("§5§lHilda §r> Not that good after all. You may try again.");
            this.state = 4;
            break;*/
        }
        
    }

}
