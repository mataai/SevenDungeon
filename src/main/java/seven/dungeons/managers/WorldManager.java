package seven.dungeons.managers;

import org.bukkit.Difficulty;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.world.options.LoadWorldOptions;
import org.mvplugins.multiverse.core.world.options.RemoveWorldOptions;
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions;

import seven.dungeons.SevenDungeons;

public class WorldManager {

    private SevenDungeons plugin;

    public WorldManager(SevenDungeons plugin) {
        this.plugin = plugin;
    }

    public boolean isMVLoaded(String world) {
        for (MultiverseWorld MVw : this.plugin.multiverse.getWorldManager().getWorlds()) {
            if (MVw.getName().equals(world)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInFolder(String world) {
        // TODO fix
        return false;
        // File file = new File(this.plugin.multiverse.getServerFolder().getPath(), world);
        // return file.exists() && file.isDirectory();
    }

    public boolean isMVUnloaded(String world) {
        for (MultiverseWorld w : this.plugin.multiverse.getWorldManager().getUnloadedWorlds()) {
            if (w.getName().equals(world))
                return true;

        }
        return false;
    }

    public void load(String worldName) {
        if (this.isMVLoaded(worldName))
            return;

        if (this.isMVUnloaded(worldName)) {
            // LOAD
            this.plugin.multiverse.getWorldManager().getWorld(worldName).peek(world -> {
                this.plugin.multiverse.getWorldManager().loadWorld(LoadWorldOptions.world(world));
                world.setDifficulty(Difficulty.NORMAL);
            });
        }
        if (this.isInFolder(worldName)) {
            // ADD
            this.plugin.multiverse.getWorldManager().createWorld(
                    CreateWorldOptions.worldName(worldName)
                            .environment(Environment.NORMAL)
                            .worldType(WorldType.FLAT)
                    );
        }
    }

    public void unload(String worldName) {
        this.plugin.multiverse.getWorldManager().getWorld(worldName).peek(world -> {
            if (world.isLoaded()) {
                this.plugin.multiverse.getWorldManager()
                        .unloadWorld(UnloadWorldOptions.world((LoadedMultiverseWorld) world));
            }
        });
    }

    public void remove(String worldName) {
        this.plugin.multiverse.getWorldManager().getWorld(worldName).peek(world -> {
            if (world.isLoaded()) {
                this.plugin.multiverse.getWorldManager().removeWorld(RemoveWorldOptions.world(world));
            }
        });
    }
}
