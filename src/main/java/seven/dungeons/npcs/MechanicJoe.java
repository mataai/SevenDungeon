package seven.dungeons.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import seven.dungeons.signs.NPCSign;

public class MechanicJoe extends SevenNPC {

	public MechanicJoe(NPCSign sign) {
	    this.sign = sign;
	    this.npcId = SevenNPC.getNewId();
	    this.state = 0;
	    this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VINDICATOR, "§7§lMechanic Joe");
	    LookClose trait1 = new LookClose();
	    this.npc.addTrait((Trait)trait1);
	    trait1.setRange(5);
	    trait1.toggle();
	  }

	@Override
	public void handleClick(Player player) {
	    switch (this.state) {
	      case 0:
	        this.msgAll("§7§lMechanic Joe §r> By keeping those fish busy, you bought me enough time to repair the teleporter ! See you later !");
			this.state = 1;
			this.npc.teleport(new Location(this.sign.getLocation().getWorld(), 210, 113, 311), PlayerTeleportEvent.TeleportCause.PLUGIN);
	        break;
	      case 1:
			  player.sendMessage("§7§lMechanic Joe §r> Sorry, I didn't repaired the ship yet... I'm a bit of a procrastinator. I did configure the teleporter though !");
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
