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

public class StardustNPC extends SevenNPC {
    
    // Uses standard signals 1083 to 1085 for Stardust Game
    
    public StardustNPC(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.BLAZE, "§c§lStarry");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch (this.state) {
        case 0:
            this.msgAll("§c§lStarry §r> Welcome to the Stardust Game. We don't have many guests... Click me again to play.");
            this.state = 1;
            break;
        case 1:
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§c§lStarry §r> Ready to play ? The cost is 1 gunpowder.\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to play\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§c§lStarry §r> Ready to play ? The cost is 1 gunpowder. ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
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
          if (!playerInventory.contains(Material.GUNPOWDER)) {
            player.sendMessage("§c§lStarry §r> The cost is 1 gunpowder and you don't have any.");
            break;
          } 
          for (i = (arrayOfItemStack = playerInventory.getContents()).length, b = 0; b < i; ) {
            ItemStack is = arrayOfItemStack[b];
            if (is != null && is.getType() == Material.GUNPOWDER) {
              is.setAmount(is.getAmount() - 1);
              break;
            } 
            b++;
          } 
          this.sign.getGame().activate(1083, null);
          player.sendMessage("§c§lStarry §r> Good luck ! (-1 gunpowder)");
          this.state = 2;
          break;
      } 
    }

    @Override
    public void signal(int id) {
        switch (id) {
        case 1084:
            this.msgAll("§c§lStarry §r> You did it ! Well done.");
            break;
        case 1085:
            this.msgAll("§c§lStarry §r> The stars aren't with you tonight. You can try again.");
            this.state = 1;
            break;
        } 
    }

}
