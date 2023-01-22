package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Valda extends SevenNPC {

	public Valda(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.WITCH, "§d§lValda");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      	case 0:
	        	this.msgAll("§d§lValda §r> Let me guess : an incompetent mechanic led you here ? Yeah, nobody visits the gallery art willingly.");
				this.sign.getGame().activate(1010, null);
				this.state = 2;
	        	break;
	      	case 1:
			    player.sendMessage("§d§lValda §r> Was it your destiny to end up here ?");
	        	break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {
		if (id == 1011) {
			this.msgAll("§d§lValda §r> Anyway, why don't you take a look around while you are here ? Some accidents are by design !");
			this.state = 1;
		}
	}

}
