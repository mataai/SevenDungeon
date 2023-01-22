package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Simon extends SevenNPC {

	public Simon(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§e§lSimon");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      	case 0:
	        	this.msgAll("§e§lSimon §r> Welcome ! Go ahead and visit our laboratories. The green house is also near, but I would not go right now.");
	        	this.state = 1;
	        	break;
	      	case 1:
			    player.sendMessage("§e§lSimon §r> Yeah okay sure have fun.");
	        	break;
			case 2 :
				player.sendMessage("§e§lSimon §r> WHAT THE HECK ? I did not know this floor could disappear like that ! Did you do it ?");
				break;
			case 3 :
				player.sendMessage("§e§lSimon §r> Okay you solved that ... thing on the floor ? Well great work yeah sure.");
				break;
			case 4 :
				player.sendMessage("§e§lSimon §r> Whatever you do, don't go to the green house ! It's because ... its bushes are not properly trimmed right now. Yeah, that's why.");
				break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {
		if (id == 30) {
			this.state = 2;
		}
		if(id == 31){
			this.state = 3;
		}
		if(id == 65){
			this.state = 4;
		}
	}

}
