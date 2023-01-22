package seven.dungeons.signs;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class MessageSign extends ActivableSign {

    int messageId;
    private boolean all;
    
    public MessageSign(Sign sign, Game game) {
        super(sign, game);
        this.singleTarget = true;
        try {
            this.messageId = Integer.parseInt(this.sign.getLine(2).trim());
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.isEmpty()) {
                this.all = false;
            }
            else {
                this.all = true;
            }
        }catch(Exception e) {
            SevenDungeons.log("Message's third line is empty or wrong !");
        }
    }
    
    @Override
    public void on(DungeonPlayer dp) {
        if(dp != null && !this.all) {
            dp.getPlayer().sendMessage(this.game.getMessage(this.messageId));
        }
        else {
            for(Player p : this.game.getPlayers()) {
                p.sendMessage(this.game.getMessage(this.messageId));
            }
        }
    }

}
