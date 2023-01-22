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

public class SlotMachineNPC extends SevenNPC {

    private int version;
    private Material material = null;

    //Uses standard signals 1080-1082
    public SlotMachineNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§e§lBarnaby");
        try {
            this.version = Integer.parseInt(this.sign.getSign().getLine(3).trim());
        } catch (Exception e) {
            this.version = 1;
        }
        switch(this.version) {
        case 1 : this.material = Material.POTATO; break;
        case 2 : this.material = Material.COOKIE; break;
        }
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch (this.state) {
        case 0:
            this.msgAll("§e§lBarnaby §r> This is the Slot Machine Game. Get identical pictures to win items ! Click me again to play.");
            this.state = 1;
            break;
        case 1:
            String m = this.material.toString().toLowerCase();
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§e§lBarnaby §r> Ready to play ? The cost is 1 " + m + ".\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§e§lBarnaby §r> Ready to play ? The cost is 1 " + m + ".");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 2:
            player.sendMessage("§e§lBarnaby §r> Quick ! Hit the pressure plates !");
            break;
        } 
    }

    @Override
    public void handleFeedback(Player player, int state, int feedback) {
        PlayerInventory playerInventory;
        byte b;
        int i;
        ItemStack[] arrayOfItemStack;
        if (this.sign.getLocation().distance(player.getLocation()) > 5.0D) {
          player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "You're too far to answer this.");
          return;
        } 
        if (state != this.state) {
          player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "It's too late to answer this.");
          return;
        } 
        switch (this.state) {
          case 1:
            playerInventory = player.getInventory();
            if (!playerInventory.contains(this.material)) {
              player.sendMessage("§e§lBarnaby §r> The cost is 1 " + this.material.toString().toLowerCase() + " and you don't have any.");
              break;
            } 
            for (i = (arrayOfItemStack = playerInventory.getContents()).length, b = 0; b < i; ) {
              ItemStack is = arrayOfItemStack[b];
              if (is != null && is.getType() == this.material) {
                is.setAmount(is.getAmount() - 1);
                break;
              } 
              b++;
            } 
            this.sign.getGame().activate(1080, null);
            player.sendMessage("§e§lBarnaby §r> Good luck ! (-1 " + this.material.toString().toLowerCase() + ")");
            this.state = 2;
            break;
        } 
    }

    @Override
    public void signal(int id) {
        switch (id) {
        case 1081:
            for (Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§e§lBarnaby §r> You got identical pictures ! Well done."); 
            }
            break;
        case 1082:
            this.state = 1;
            break;
        } 
    }

}
