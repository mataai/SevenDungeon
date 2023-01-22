package seven.dungeons.signs;

import org.bukkit.Sound;
import org.bukkit.block.Sign;

import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class SoundSign extends ActivableSign {
    
    private Sound sound;
    
    //Sound static library
    public static Sound[] sounds = {Sound.BLOCK_BEEHIVE_EXIT,  // 0
    		Sound.ITEM_TRIDENT_RETURN, // 1
            Sound.ENTITY_PLAYER_LEVELUP, // 2
            Sound.BLOCK_WOODEN_DOOR_OPEN, // 3
            Sound.ENTITY_ENDERMAN_TELEPORT, // 4
            Sound.UI_TOAST_CHALLENGE_COMPLETE}; // 5

    public SoundSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String line3 = this.sign.getLine(2).trim();
            this.sound = sounds[Integer.parseInt(line3)];
        }catch(Exception e) {
            SevenDungeons.log("Sound's third line is wrong or empty.");
        }
    }

    @Override
    public void on() {
        this.game.getWorld().playSound(this.location, this.sound, 3, 1);
    }
}
