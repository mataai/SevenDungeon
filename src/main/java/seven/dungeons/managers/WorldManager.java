package seven.dungeons.managers;

import java.io.File;

import org.bukkit.Difficulty;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import seven.dungeons.SevenDungeons;

public class WorldManager {

private SevenDungeons plugin;
    
    public WorldManager(SevenDungeons plugin)
    {
        this.plugin = plugin;
    }
    
    public boolean isMVLoaded(String world)
    {   
        for(MultiverseWorld MVw : this.plugin.multiverse.getMVWorldManager().getMVWorlds())
        {
            if(MVw.getName().equals(world))
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInFolder(String world)
    {
        File file = new File(this.plugin.multiverse.getServerFolder().getPath(), world);
        if(file.exists() && file.isDirectory())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean isMVUnloaded(String world)
    {
        for(String w : this.plugin.multiverse.getMVWorldManager().getUnloadedWorlds())
        {
            if(w.equals(world)) return true;
            
        }
        return false;
    }
    
    public void load(String world)
    {
        if(this.isMVLoaded(world)) return;
        
        if(this.isMVUnloaded(world))
        {
            //LOAD
            this.plugin.multiverse.getMVWorldManager().loadWorld(world);
            MultiverseWorld mw = this.plugin.multiverse.getMVWorldManager().getMVWorld(world);
            mw.setDifficulty(Difficulty.NORMAL);
            return;
        }
        if(this.isInFolder(world))
        {
            //ADD
            this.plugin.multiverse.getMVWorldManager().addWorld(world, Environment.NORMAL, null, WorldType.FLAT, null, null);
        }
    }
    
    public void unload(String world)
    {
        if(this.isMVLoaded(world))
        {
            this.plugin.multiverse.getMVWorldManager().unloadWorld(world);
        }
    }
    
    public void remove(String world)
    {
        if(this.isMVLoaded(world))
        {
            this.plugin.multiverse.getMVWorldManager().removeWorldFromConfig(world);
        }
    }
}
