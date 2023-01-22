package seven.dungeons.npcs;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.NPCSign;

public class Sabrina extends SevenNPC{
    
    private int answer;
    
    public Sabrina(NPCSign sign) {
        this.sign = sign;
        this.npcId = SevenNPC.getNewId();
        this.state = 0;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.WITCH, "§5§lSabrina");
        LookClose trait1 = new LookClose();
        this.npc.addTrait((Trait)trait1);
        trait1.setRange(10);
        trait1.toggle();
        try {
          this.answer = Integer.parseInt(this.sign.getSign().getLine(3).trim());
        } catch (Exception e) {
            SevenDungeons.log("Sabrina's fourth line is wrong.");
        } 
    }

    @Override
    public void handleClick(Player player) {
        switch (this.state) {
          case 0:
            player.sendMessage("§5§lSabrina §r> I have a riddle for you. Answer right and earn gems. Answer wrong and be punished !");
            this.state = 1;
            break;
          case 1:
           /* comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§5§lSabrina §r> How many baby pumpkins are in that cave ?\n\", \"extra\":[{\"text\":\" §l[9] \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0\"}},{\"text\":\" §l[10] \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 1\"}},{\"text\":\" §l[11] \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 2\"}}]}");
            packet = new PacketPlayOutChat(comp, ChatMessageType.a, null);
            (((CraftPlayer)player).getHandle()).b.sendPacket(packet);*/

            player.sendMessage("§5§lSabrina §r> How many baby pumpkins are in that cave ?");

              TextComponent nine = new TextComponent("§a§l[9] ");
              nine.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 0"));

              TextComponent ten = new TextComponent("§a§l[10] ");
              ten.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 1"));

              TextComponent eleven = new TextComponent("§a§l[11]");
              eleven.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/7d npc " + this.sign.getGame().getInstanceName() + " " + getId() + " 1 2"));

              nine.addExtra(ten);
              nine.addExtra(eleven);
              player.spigot().sendMessage(nine);
            break;
          case 2:
            player.sendMessage("§5§lSabrina §r> Alright good job. Now leave my cave !");
            break;
          case 3:
            player.sendMessage("§5§lSabrina §r> Go away, you idiot !");
            break;
        } 
    }

    @Override
    public void handleFeedback(Player player, int state, int feedback) {
        PotionEffect pe;
        if (this.sign.getLocation().distance(player.getLocation()) > 5.0D) {
          player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "You're too far to answer this.");
          return;
        } 
        if (state != this.state) {
          player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> " + "It's too late to answer this.");
          return;
        } 
        switch (this.state) {
          case 1:
            if (feedback == this.answer) {
              this.sign.getGame().activate(54, null);
              player.sendMessage("§5§lSabrina §r> That is correct ! Have some gems... and leave !");
              this.state = 2;
              break;
            } 
            player.sendMessage("§5§lSabrina §r> That's not it. Enjoy the taste of defeat !");
            pe = new PotionEffect(PotionEffectType.POISON, 100, 1, true);
            if (player.hasPotionEffect(PotionEffectType.POISON))
              player.removePotionEffect(PotionEffectType.POISON); 
            player.addPotionEffect(pe);
            this.state = 3;
            break;
        } 
    }

    @Override
    public void signal(int id) {
    }

}
