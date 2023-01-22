package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Pietro2 extends SevenNPC {

	public Pietro2(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "§a§lPietro");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      	case 0:
	        	this.msgAll("§a§lPietro §r> We meet again ! I tried to help and follow, but you were too fast hehe ! Anyway, I don't know how to thank you... So I guess I won't...");
	        	this.state = 1;
	        	break;
	      	case 1:
			    player.sendMessage("§a§lPietro §r> I should get to work and clean up the green house, but I think I still have the hiccups. Better wait.");
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
