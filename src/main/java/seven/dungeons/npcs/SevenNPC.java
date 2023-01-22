package seven.dungeons.npcs;

import org.bukkit.entity.Player;

import net.citizensnpcs.api.npc.NPC;
import seven.dungeons.signs.NPCSign;

public abstract class SevenNPC {

    static private int npcCount = 0;
    protected NPC npc;
    protected int state;
    protected int npcId;
    protected NPCSign sign;
    
    public NPC getNPC() {
        return this.npc;
    }
    
    public abstract void handleClick(Player player);
    
    public abstract void handleFeedback(Player player, int state, int feedback);
    
    public abstract void signal(int id);
    
    public static int getNewId() {
        return npcCount++;
    }
    
    public int getId() {
        return this.npcId;
    }
    
    public void msgAll(String msg) {
        for(Player p : this.sign.getGame().getPlayers()) {
            p.sendMessage(msg);
        }
    }
}
