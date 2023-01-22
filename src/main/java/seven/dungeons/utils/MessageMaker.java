package seven.dungeons.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageMaker {

    public static void actionBarMessage(String message, Player player){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static void titleMessage(String message, String subMessage, Player player){
        player.sendTitle(message, subMessage, 10, 80, 10);
    }
}
