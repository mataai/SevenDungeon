package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

public class CheckpointAreaSign extends CaptorSign {

    private int priority;
    private Location respawnLocation;
    private int x1, x2, y1, y2, z1, z2;

    public CheckpointAreaSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.priority = Integer.parseInt(line3[0]);
            this.respawnLocation = new Location(this.location.getWorld(), Integer.parseInt((line3[1])) + 0.5f, Integer.parseInt((line3[2])), Integer.parseInt((line3[3])) + 0.5f);
            String[] distances = this.sign.getLine(3).trim().split(" ");
            if(distances[0].substring(distances[0].length()-1).equals("s")) {
                this.z1 = this.sign.getZ();
                this.z2 = this.z1 + Integer.parseInt(distances[0].substring(0,distances[0].length()-1));
            }
            else {
                this.z2 = this.sign.getZ();
                this.z1 = this.z2 - Integer.parseInt(distances[0].substring(0,distances[0].length()-1));
            }
            if(distances[1].substring(distances[1].length()-1).equals("e")) {
                this.x1 = this.sign.getX();
                this.x2 = this.x1 + Integer.parseInt(distances[1].substring(0,distances[1].length()-1));
            }
            else {
                this.x2 = this.sign.getX();
                this.x1 = this.x2 - Integer.parseInt(distances[1].substring(0,distances[1].length()-1));
            }
            if(distances[2].substring(distances[2].length()-1).equals("u")) {
                this.y1 = this.sign.getY();
                this.y2 = this.y1 + Integer.parseInt(distances[2].substring(0,distances[2].length()-1));
            }
            else {
                this.y2 = this.sign.getY();
                this.y1 = this.y2 - Integer.parseInt(distances[2].substring(0,distances[2].length()-1));
            }
        }catch(Exception e) {
            SevenDungeons.log("CheckpointArea's third and/or fourth is wrong at " + this.sign.getLocation().toString());
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        if(dp != null && this.game.getCheckpointLevel() <= this.priority) {
            this.game.setSpawn(this.respawnLocation, dp.getPlayer());
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        for(Player p : this.game.getPlayers()) {
            Location l = p.getLocation();
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            if(y < this.y1 || y > this.y2 || x < this.x1 || x > this.x2 || z < this.z1 || z > this.z2) {
                continue;
            }
            DungeonPlayer dp = null;
            for(DungeonPlayer dungeonPlayer : this.game.getTeam().getPlayers()){
                if(dungeonPlayer.getPlayer() == p){
                    dp = dungeonPlayer;
                }
            }
            this.trigger(dp);
        }
    }


}
