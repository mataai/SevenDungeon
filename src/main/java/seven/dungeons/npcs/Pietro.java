package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import seven.dungeons.signs.NPCSign;

public class Pietro extends SevenNPC {

	public Pietro(NPCSign sign) {
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
	        	this.msgAll("§a§lPietro §r> Oh god OH GOD. We will run out of food AND oxygen ! ... Hey you ! You look fierce, I'm sure you want to help ! Don't you ?");
	        	this.state = 1;
	        	break;
	      	case 1:
			    this.msgAll("§a§lPietro §r> Okay so here's the thing : lately the green house has become ... infested with many annoying aliens. Maybe you could get rid of them ?");
				this.sign.getGame().activate(1005, null);
				this.state = 5;
	        	break;
			case 2 :
				this.msgAll("§a§lPietro §r> What ? You want me to come too ... ? Oh I can't... I... I have the hiccups right now.");
				this.state = 3;
				break;
			case 3 :
				player.sendMessage("§a§lPietro §r> I still have the hiccups... Sorry. Go ahead I'll join you soon enough !");
				break;
	    }
	}

	@Override
	public void handleFeedback(Player player, int state, int feedback) {

	}

	@Override
	public void signal(int id) {
		if (id == 1006) {
			this.msgAll("§a§lPietro §r> Our gardeners left some notes here and there about those aliens. It might help you. Tell me if you need more help.");
			this.state = 2;
		}
	}

}
