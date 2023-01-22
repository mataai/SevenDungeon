package seven.dungeons;

import org.bukkit.entity.Player;

public class DungeonPlayer {
    private Player player;
    private int life = -1;
    private DungeonTeam team = null;
    
    public DungeonPlayer(Player player)
    {
        this.player = player;
    }
    
    public void setLife(int life)
    {
        if(life > 100 || life < -1)
        {
            return;
        }
        this.life = life;
    }
    
    public int getLife()
    {
        return this.life;
    }

    public Player getPlayer()
    {
        return this.player;
    }
    
    public void setTeam(DungeonTeam team) {
        this.team = team;
    }
    
    public DungeonTeam getTeam() {
        return this.team;
    }

    public void setPlayer(Player p){
        this.player = p;
    }
}

