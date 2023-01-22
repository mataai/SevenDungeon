package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Shawn extends SevenNPC {

	public Shawn(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VINDICATOR, "§b§lShawn");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
	        player.sendMessage("§b§lShawn §r> Hey. How's it going ?");
	        break;
	      case 1:
			  player.sendMessage("§b§lShawn §r> Yes that's right. Use the teleporter. Lenny will help you. Right Lenny ?");
			  player.sendMessage("§d§lLenny §r> ...");
	        break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {
		if (id == 135) {
			this.msgAll("§b§lShawn §r> Can't you see the ship's engines are clogged with grass ? Use the teleporter for now. No mechanics are available to fix the ship.");
			this.state = 1;
		}
	}

}
