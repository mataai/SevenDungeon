package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Nald extends SevenNPC {

	public Nald(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SKELETON, "§b§lNald");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
	        player.sendMessage("§b§lNald §r> Welcome to the Water Plant ! Sorry... the doors have been locked from inside for... a while. Maybe they meant to fire me ? I'll wait a bit longuer.");
	        break;
	      case 1:
			  player.sendMessage("§b§lNald §r> Looks like someone managed to open the doors. Go ahead !");
	        break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {
		if (id == 141) {
			this.state = 1;
		}
	}

}
