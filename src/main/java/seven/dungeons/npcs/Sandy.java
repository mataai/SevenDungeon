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

public class Sandy extends SevenNPC {

	private int remainingSands;

	public Sandy(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SKELETON, "§6§lSandy");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(10);
	    trait1.toggle();
	    this.remainingSands = 5;
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
			  for(Player p : this.sign.getGame().getPlayers()){
				  p.sendMessage("§6§lSandy §r> Can you help me ? The sand pipe is clogged... again ! Yellow sand is heavier and might help.");
			  }
	        this.state = 1;
	        break;
	      case 1:
	        /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§7§lBonnie §r> You found a bone ? I still need " + this.remainingBones + ".\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to give a bone\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0\"}}]}");
	        packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
	        (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

			  TextComponent text = new TextComponent("§6§lSandy §r> You found some yellow sand ? I still need " + this.remainingSands + ". ");

			  TextComponent yes = new TextComponent("§a§l[Yes]");
			  yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to give" )));
			  yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

			  text.addExtra(yes);
			  player.spigot().sendMessage(text);
	        break;
	      case 2:
	        player.sendMessage("§6§lSandy §r> Perhaps that's why we stopped using hourglasses and went with clocks... Huh ! You're still here ?");
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
	        if (!playerInventory.contains(Material.YELLOW_CONCRETE_POWDER)) {
	          player.sendMessage("§6§lSandy §r> You don't even have yellow sand !");
	          break;
	        } 
	        for (i = (arrayOfItemStack = playerInventory.getContents()).length, b = 0; b < i; ) {
	          ItemStack is = arrayOfItemStack[b];
	          if (is != null && is.getType() == Material.YELLOW_CONCRETE_POWDER) {
	            is.setAmount(is.getAmount() - 1);
	            break;
	          } 
	          b++;
	        } 
	        this.remainingSands--;
	        if (this.remainingSands == 0) {
	          player.sendMessage("§6§lSandy §r> Phew... It's unclogged ! Thanks. Take those gems.");
	          this.sign.getGame().activate(13, null);
	          this.state = 2;
	          break;
	        } 
	        player.sendMessage("§7§lSandy §r> I'll take this. (-1 yellow concrete powder)");
	        break;
	    } 
	}

	@Override
	public void signal(int id) {

	}

}
