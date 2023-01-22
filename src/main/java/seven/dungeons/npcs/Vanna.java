package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Vanna extends SevenNPC {

	public Vanna(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SKELETON, "§b§lVanna");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
	        this.msgAll("§b§lVanna §r> What a mess ! Strange fish are contaminating the water in this comet ! I don't have time to talk.");
			this.state = 1;
	        break;
	      case 1:
			  player.sendMessage("§b§lVanna §r> Make yourself useful and kill some one-eyed fish if you find any !");
	        break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {

	}

}
