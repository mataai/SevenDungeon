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

public class Bonnie extends SevenNPC {
	
	private int remainingBones;
	
	public Bonnie(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SKELETON, "§7§lBonnie");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(10);
	    trait1.toggle();
	    this.remainingBones = 3;
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
	        player.sendMessage("§7§lBonnie §r> Can you help me ? I'm missing 3 bones ... it's quite embarassing. Come back if you find some.");
	        this.state = 1;
	        break;
	      case 1:
	        /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§7§lBonnie §r> You found a bone ? I still need " + this.remainingBones + ".\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to give a bone\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0\"}}]}");
	        packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
	        (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

			  TextComponent text = new TextComponent("§7§lBonnie §r> You found a bone ? I still need " + this.remainingBones + ". ");

			  TextComponent yes = new TextComponent("§a§l[Yes]");
			  yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to give" )));
			  yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

			  text.addExtra(yes);
			  player.spigot().sendMessage(text);
	        break;
	      case 2:
	        player.sendMessage("§7§lBonnie §r> I'll try not to lose my bones again...");
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
	        if (!playerInventory.contains(Material.BONE)) {
	          player.sendMessage("§7§lBonnie §r> You don't have a bone...");
	          break;
	        } 
	        for (i = (arrayOfItemStack = playerInventory.getContents()).length, b = 0; b < i; ) {
	          ItemStack is = arrayOfItemStack[b];
	          if (is != null && is.getType() == Material.BONE) {
	            is.setAmount(is.getAmount() - 1);
	            break;
	          } 
	          b++;
	        } 
	        this.remainingBones--;
	        if (this.remainingBones == 0) {
	          player.sendMessage("§7§lBonnie §r> I'm whole again ! Take these gems.");
	          this.sign.getGame().activate(49, null);
	          this.state = 2;
	          break;
	        } 
	        player.sendMessage("§7§lBonnie §r> Oh thank you ! (-1 bone)");
	        break;
	    } 
	}

	@Override
	public void signal(int id) {
		if (id == 47) {
			for (Player p : this.sign.getGame().getPlayers()) {
				p.sendMessage("§7You see a bone fall in the water..."); 
			}
		}
		if (id == 117) {
			for (Player p : this.sign.getGame().getPlayers()) {
				p.sendMessage("§6§lPumpkin Mage §r> You fell right in my trap... Farewell !");  
			}
		}
		if (id == 119) {
			for (Player p : this.sign.getGame().getPlayers()) {
		        p.sendMessage("§6§lPumpkin Mage §r> Noooo !");
		        p.sendMessage("§7As the pumpkin mage dies, an exit appears near his throne.");
		    } 
		}
		      
	}

}
