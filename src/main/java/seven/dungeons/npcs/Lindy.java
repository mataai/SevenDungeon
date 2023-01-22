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

public class Lindy extends SevenNPC {
    
    private int version;
    
    public Lindy(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.COW, "§c§lLady Lindy");
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
        switch (this.state) {
        case 0 :
            this.msgAll("§c§lLady Lindy §r> Hello ! I'm Lady Lindy, the first cow to travel the world. This is quite a strange place, don't you think ?");
            this.sign.getGame().activate(9, null);
            this.state = 1;
            break;
        case 2 : 
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§c§lLady Lindy §r> Did you find some magic sugar ?\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to give\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§c§lLady Lindy §r> Did you find some magic sugar ? ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to give" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 3 :
            player.sendMessage("§c§lLady Lindy §r> Did you know all this land is made of magic sugar ? Its pure form is the perfect souvenir.");
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
            if (!playerInventory.contains(Material.SUGAR)) {
              player.sendMessage("§c§lLady Lindy §r> I'm not a fool ! You didn't find any sugar.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.SUGAR) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§c§lLady Lindy §r> Wow ! You found it ! Thank you. Take some gems I found during my trips.");
                this.sign.getGame().activate(13, null);
            }
            this.state = 3;
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 10 :
            this.msgAll("§c§lLady Lindy §r> You see the giant ice cream cone close by ? Legend says you can find magic sugar at its top. Bring back some of it to me and I'll reward you !");
            this.state = 2;
            break;
        }
        
    }

}
