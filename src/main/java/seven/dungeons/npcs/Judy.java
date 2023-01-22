package seven.dungeons.npcs;

import java.util.ArrayList;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Turtle;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.LookClose;
import seven.dungeons.signs.NPCSign;

public class Judy extends SevenNPC {
    
    private int babiesLeft;
    private ArrayList<NPC> babies = new ArrayList<NPC>();
    
    public Judy(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.TURTLE, "§a§lJudy");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait)trait1);
        trait1.setRange(10);
        trait1.toggle();
        this.babiesLeft = 3;
    }

    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 : 
            player.sendMessage("§a§lJudy §r> You have to help me ! I can't find my three babies ! They must be swimming close by... They love to wander underwater.");
            this.spawnBabies();
            this.state = 1;
            break;
        case 1 : 
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§a§lJudy §r> Did you find one of my babies ?\", \"extra\":[{\"text\":\" §a§l[Yes]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to give\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + this.getId() + " 1 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§a§lJudy §r> Did you find one of my babies ? ");

            TextComponent yes = new TextComponent("§a§l[Yes]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to give" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        case 2 :
            player.sendMessage("§a§lJudy §r> I'm such a good mother. Have a nice day.");
            break;
        }
        
    }

    @Override
    public void handleFeedback(Player player, int state, int feedback) {
        if (this.sign.getLocation().distance(player.getLocation()) > 5.0D) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "You're too far to answer this.");
            return;
        } 
        if (state != this.state) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "It's too late to answer this.");
            return;
        }
        switch(this.state) {
        case 1 : 
            int currentBabiesLeft = this.babiesLeft;
            for(NPC npc : this.babies) {
                if(npc.isSpawned() && npc.getEntity().getLocation().distance(this.sign.getLocation()) < 5) {
                    this.babiesLeft--;
                    npc.despawn();
                }
            }
            if(this.babiesLeft == 0) {
                player.sendMessage("§a§lJudy §r> I owe you a lot. Have 3 gems.");
                this.state = 2;
                this.sign.getGame().activate(140, null);
                break;
            }
            if(currentBabiesLeft > this.babiesLeft) {
                player.sendMessage("§a§lJudy §r> Wooow ! Thanks YOU ! " + "Still " + this.babiesLeft + " to go. Keep searching please !");
            }
            else {
                player.sendMessage("§a§lJudy §r> I don't see any of my babies close by... Are you sure ?");
            }
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 139 : this.checkPlayers();
        }
        
    }
    
    public void spawnBabies() {
        NPC baby1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.TURTLE, "§a§lBubby");
        NPC baby2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.TURTLE, "§a§lBubble");
        NPC baby3 = CitizensAPI.getNPCRegistry().createNPC(EntityType.TURTLE, "§a§lBubbly");
        this.babies.add(baby1);
        this.babies.add(baby2);
        this.babies.add(baby3);
        for(NPC npc : this.babies) {
            
            FollowTrait trait1 = new FollowTrait();
            npc.addTrait(trait1);
        }
        Location l1 = new Location(this.sign.getLocation().getWorld(), 79.5, 49.5, 124.5);
        Location l2 = new Location (this.sign.getLocation().getWorld(), 329.5, 49.5, 164.5);
        Location l3 = new Location (this.sign.getLocation().getWorld(), 159.5, 49.5, 156.5);
        l1.getChunk().load();
        l2.getChunk().load();
        l3.getChunk().load();
        baby1.spawn(l1);
        baby2.spawn(l2);
        baby3.spawn(l3);
        for(NPC npc : this.babies) {
            Turtle turtle = (Turtle) npc.getEntity();
            turtle.setBaby();
        }
    }
    
    public void checkPlayers() {
        for(NPC npc : this.babies) {
            for(Player p : this.sign.getGame().getPlayers()) {
                if(npc.isSpawned()) {
                    Turtle turtle = (Turtle) npc.getEntity();
                    turtle.setBaby();
                    if(npc.getEntity().getLocation().distance(p.getLocation()) < 5) {
                        npc.getTrait(FollowTrait.class).toggle(p, false);
                        break;
                    }
                }
            }
        }
    }
    

}
