package seven.dungeons.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.trait.LookClose;
import seven.dungeons.signs.NPCSign;

public class SerCreighton extends SevenNPC {

    public SerCreighton(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.CREEPER, "§a§lSer Creighton");
        LookClose trait1 = new LookClose();
        this.npc.addTrait(trait1);
        trait1.setRange(10);
        trait1.toggle();
    }

    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : player.sendMessage("§7Let me finish my speech you silly...");break;
        case 1 : 
            player.sendMessage("§a§lSer Creighton§r > Let me spawn it ... hmmm ... §2§lMoron Zombie§r, Master of the rotten flesh, §oI SUMMON THEE §r!"); 
            this.sign.getGame().activate(6, null);
            this.state = 2;
            break;
        case 3 : player.sendMessage("§a§lSer Creighton§r > Come on ! Find all four §echests §r!");break;
        default:break;
        }
    }

    @Override
    public void handleFeedback(Player player, int state, int feedback) {
        if(this.sign.getLocation().distance(player.getLocation()) > 5) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "You're too far to answer this.");
            return;
        }
        if(state != this.state) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "It's too late to answer this.");
            return;
        }
        switch(this.state) {
        case 0 : 
        default : break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 5: this.state = 1; break;
        case 10: this.state = 3; this.npc.teleport(new Location(this.sign.getGame().getWorld(), 64.5, 53, 33.5, -180, 0), TeleportCause.PLUGIN); break;
        case 17: this.state = 4; this.npc.teleport(new Location(this.sign.getGame().getWorld(), 47.5, 49, 57.5, -60, -7), TeleportCause.PLUGIN); break;
        case 22: this.npc.teleport(new Location(this.sign.getGame().getWorld(), 17.5, 49, 38.5, -5, 9), TeleportCause.PLUGIN); break;
        case 27: this.npc.teleport(new Location(this.sign.getGame().getWorld(), 47.5, 36, 25.5, 108, -21), TeleportCause.PLUGIN); break;
        case 36: this.npc.teleport(new Location(this.sign.getGame().getWorld(), 57.5, 34, 45.5, -160, -13), TeleportCause.PLUGIN); break;
        }
        
    }
}
