package seven.dungeons.npcs;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import seven.dungeons.signs.NPCSign;

public class Larry extends SevenNPC{

    private int food;
    private Slime slime;
    
    public Larry(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.food = 0;
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SLIME, "§a§lLarry");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait) trait1);
        trait1.setRange(10);
        trait1.toggle();
    }
    
    @Override
    public void handleClick(Player player) {
        switch(this.state) {
        case 0 :
            this.msgAll("§a§lLarry §r> I'm SOOO hungry ! Can you bring me some of this gingerbread house over there ?");
            this.state = 1;
            this.slime = (Slime)this.npc.getEntity();
            break;
        case 1 : 
            /*comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§a§lLarry §r> I'm still hungry !\", \"extra\":[{\"text\":\" §a§l[Give food]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to feed\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            TextComponent text = new TextComponent("§a§lLarry §r> I'm still hungry ! ");

            TextComponent yes = new TextComponent("§a§l[Give food]");
            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Click to feed" )));
            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

            text.addExtra(yes);
            player.spigot().sendMessage(text);
            break;
        }
        
    }

    @Override
    public void handleFeedback(Player player, int state, int feedback) {
        if (this.sign.getLocation().distance(player.getLocation()) > 10.0D) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "You're too far to answer this.");
            return;
        } 
        if (state != this.state) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "It's too late to answer this.");
            return;
        }
        PlayerInventory playerInventory;
        ItemStack[] items;
        switch(this.state) {
        case 1 :
            playerInventory = player.getInventory();
            if (!playerInventory.contains(Material.BROWN_CONCRETE_POWDER)) {
              player.sendMessage("§a§lLarry §r> What ?! You don't have what I asked!");
              break;
            }
            items = playerInventory.getContents();
            for (int i = 0; i < items.length; i++ ) {
              ItemStack is = items[i];
              if (is != null && is.getType() == Material.BROWN_CONCRETE_POWDER) {
                is.setAmount(is.getAmount() - 1);
                break;
              }
            }
            this.food++;
            this.slime.setSize(this.slime.getSize() + 1);
            if(this.food >= 10) {
                this.msgAll("§a§lLarry §r> Finally... I... don't feel so good...");
                this.state = 2;
                this.sign.getGame().activate(11, null);
                break;
            }
            for(Player p : this.sign.getGame().getPlayers()) {
                p.sendMessage("§a§lLarry §r> I need MORE.");
            }
            break;
        }
    }

    @Override
    public void signal(int id) {
        switch(id) {
        case 12 :
            this.slime.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.slime.getLocation().clone().add(0.5,4,0.5), 10, 4, 4, 4);
            this.slime.getWorld().playSound(this.slime.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 0.8f);
            this.npc.destroy();
        }
        
    }

}
