package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Lenny extends SevenNPC {

	public Lenny(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§d§lLenny");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
	        player.sendMessage("§d§lLenny §r> Hey you can't use the teleporter sweetie, it might tear you to pieces !");
	        break;
	      case 1:
			  TextComponent text = new TextComponent("§d§lLenny §r> Oh no, the teleporter is only for non-valuable shipments sweetheart... and let me tell you that you ARE valuable. ");

			  TextComponent yes = new TextComponent("§c§l[But the ship is broken !]");
			  yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to interact" )));
			  yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

			  text.addExtra(yes);
			  player.spigot().sendMessage(text);
	        break;
			case 2 :
				player.sendMessage("§d§lLenny §r> Go ahead darling, teleport ! *fingers crossed*");
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

		if(this.state == 1){
			this.msgAll("§d§lLenny §r> Oh I see. Sorry, I don't know how to configure the teleporter honey. Right now it can only lead you to the Water Plant. You might find a mechanic there though.");
			this.sign.getGame().activate(136, null);
			this.state = 2;
		}
	}

	@Override
	public void signal(int id) {
		if (id == 135) {
			this.state = 1;
		}
	}

}
