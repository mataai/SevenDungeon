package seven.dungeons.signs;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.utils.MessageMaker;

public class BigMessageSign extends ActivableSign {

    int messageId;
    int subMessageId;
    private boolean all;

    public BigMessageSign(Sign sign, Game game) {
        super(sign, game);
        this.singleTarget = true;
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.messageId = Integer.parseInt(line3[0]);
            this.subMessageId = Integer.parseInt(line3[1]);
            String line4 = this.sign.getLine(3);
            if(line4 == null || line4.isEmpty()) {
                this.all = false;
            }
            else {
                this.all = true;
            }
        }catch(Exception e) {
            SevenDungeons.log("Big Message's third line is empty or wrong !");
        }
    }
    
    @Override
    public void on(DungeonPlayer dp) {
        if(dp != null && !this.all) {
            MessageMaker.titleMessage(this.game.getMessage(this.messageId), this.game.getMessage(this.subMessageId), dp.getPlayer());
        }
        else {
            for(Player p : this.game.getPlayers()) {
                MessageMaker.titleMessage(this.game.getMessage(this.messageId), this.game.getMessage(this.subMessageId), p);
            }
        }
    }

}
