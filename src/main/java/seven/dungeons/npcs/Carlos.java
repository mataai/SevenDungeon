package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Carlos extends SevenNPC {

	public Carlos(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§e§lCarlos");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      	case 0:
	        	this.msgAll("§e§lCarlos §r> Hey you ! Help me !! I forgot to turn off the invisible mode on my spaceship... The front of the ship is still visible, but I don't know where to enter now... ");
	        	this.state = 1;
	        	break;
	      	case 1:
			    player.sendMessage("§e§lCarlos §r> I think you might be able to hop on a wing of the ship from here...");
	        	break;
			case 2 :
				this.msgAll("§e§lCarlos §r> Phew ! I almost lost 4 millions blollars... What ? Ice ? Oh boy I forgot to turn on the heating... Anyway, take some gems !");
				this.state = 3;
				this.sign.getGame().activate(134, null);
				break;
			case 3 :
				player.sendMessage("§e§lCarlos §r> My spaceship is quitte pretty... when I can see it.");
				break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {
		if (id == 28) {
			this.state = 2;
		}
	}

}
