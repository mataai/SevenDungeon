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
public class SpacePigRun extends SevenNPC {

    public SpacePigRun(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§6§lLeo");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(5);
        trait1.toggle();
    }


    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            this.msgAll("§6§lLeo §r> You must be the new pig trainers ! Right on time, the previous ones §odied§r. Huh... I meant they §oditched §rme, of course.");
            this.sign.getGame().activate(1003, null);
            this.state = 1;
            break;
        case 2 :

            TextComponent text = new TextComponent("§6§lLeo §r> Did you find a carrot to feed the pigs ? ");

            TextComponent yes = new TextComponent("§a§l[Give a carrot]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to play" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 2 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 5 :
            player.sendMessage("§6§lLeo §r> Very promising results... and you're still alive !");
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
            if (!playerInventory.contains(Material.CARROT)) {
              player.sendMessage("§6§lLeo §r> Ehm... are you sure that's a carrot ? It's more pink than orange.");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.CARROT) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§6§lLeo §r> Great. Now hang tight ! (-1 carrot)");
            }
            this.sign.getGame().activate(52, null);
            this.state = 3;
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 1004 :
            this.msgAll("§6§lLeo §r> Bring me a carrot and the pigs will be ready for you !");
            this.state = 2;
            break;
        case 53 :
            this.msgAll("§6§lLeo §r> Now now ! You're quite experienced with pigs aren't you ?");
            this.state = 5;
            break;
        case 54 :
            this.msgAll("§6§lLeo §r> Ahhh that's a shame... but we do have some pigs left.");
            this.state = 2;
            break;
        }
        
    }

}
