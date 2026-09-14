package seven.dungeons.bundle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

import seven.dungeons.SevenDungeons;

/**
 * Source of truth for the positions of a dungeon's command signs ("anchors").
 *
 * The in-world sign is only a visual marker for builders. The authoritative list
 * lives in the dungeon bundle ({@link DungeonBundle}) as {@code anchors.yml} and is
 * kept in sync by the listener (sign placed / sign broken in the template world)
 * and by the {@code /7d} anchor commands. Compilation of a game reads this list
 * and never scans the world.
 */
public class AnchorStore {

    public static final String FILE_NAME = "anchors.yml";
    public static final char COMMAND_PREFIX = '=';

    private final File file;
    private final String dungeonId;
    private final String worldName;
    private final Set<BlockVector> anchors = new LinkedHashSet<BlockVector>();

    /**
     * @param bundleDirectory the dungeon bundle folder that owns this store
     */
    public AnchorStore(File bundleDirectory, String dungeonId, String worldName) {
        this.file = new File(bundleDirectory, FILE_NAME);
        this.dungeonId = dungeonId;
        this.worldName = worldName;
        this.load();
    }

    // ---------------------------------------------------------------- file I/O

    public File getFile() {
        return this.file;
    }

    public void load() {
        this.anchors.clear();
        File file = this.getFile();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String entry : config.getStringList("anchors")) {
            BlockVector v = parse(entry);
            if (v == null) {
                SevenDungeons.log("Ignoring malformed anchor \"" + entry + "\" in " + file.getPath(), "SevenDungeons", ChatColor.RED);
                continue;
            }
            this.anchors.add(v);
        }
    }

    public void save() {
        File file = this.getFile();
        file.getParentFile().mkdirs();
        YamlConfiguration config = new YamlConfiguration();
        config.set("dungeon", this.dungeonId);
        config.set("world", this.worldName);
        List<String> entries = new ArrayList<String>();
        for (BlockVector v : this.anchors) {
            entries.add(format(v));
        }
        config.set("anchors", entries);
        try {
            config.save(file);
        } catch (IOException e) {
            SevenDungeons.log("Could not save anchors of dungeon \"" + this.dungeonId + "\" to " + file.getPath(), "SevenDungeons", ChatColor.RED);
            e.printStackTrace();
        }
    }

    public void delete() {
        File file = this.getFile();
        if (file.exists() && !file.delete()) {
            SevenDungeons.log("Could not delete " + file.getPath(), "SevenDungeons", ChatColor.RED);
        }
        this.anchors.clear();
    }

    // ---------------------------------------------------------------- mutation

    /** Registers an anchor and persists the file. Returns false if it was already registered. */
    public boolean add(Location location) {
        boolean added = this.anchors.add(toVector(location));
        if (added) {
            this.save();
        }
        return added;
    }

    /** Unregisters an anchor and persists the file. Returns false if it was not registered. */
    public boolean remove(Location location) {
        boolean removed = this.anchors.remove(toVector(location));
        if (removed) {
            this.save();
        }
        return removed;
    }

    /** Replaces the whole list at once (used by imports) and persists the file. */
    public void replaceAll(Iterable<Location> locations) {
        this.anchors.clear();
        for (Location l : locations) {
            this.anchors.add(toVector(l));
        }
        this.save();
    }

    // ---------------------------------------------------------------- queries

    public boolean contains(Location location) {
        return this.anchors.contains(toVector(location));
    }

    public int size() {
        return this.anchors.size();
    }

    public Set<BlockVector> getVectors() {
        return Collections.unmodifiableSet(this.anchors);
    }

    /**
     * Anchors as block locations in the given world. The world may be a template
     * world or a copied instance world; only the coordinates are taken from the store.
     */
    public List<Location> getLocations(World world) {
        List<Location> result = new ArrayList<Location>(this.anchors.size());
        for (BlockVector v : this.anchors) {
            result.add(new Location(world, v.getBlockX(), v.getBlockY(), v.getBlockZ()));
        }
        return result;
    }

    /** Anchors located in this dungeon's template world (which may be unloaded, giving a null world). */
    public List<Location> getLocations() {
        return this.getLocations(Bukkit.getWorld(this.worldName));
    }

    /**
     * Compares the file against the loaded template world. Returns the anchors whose
     * block is not (or no longer) a command sign. Returns null if the world is not loaded.
     */
    public List<Location> findStale() {
        World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            return null;
        }
        List<Location> stale = new ArrayList<Location>();
        for (Location l : this.getLocations(world)) {
            if (!isCommandSign(l.getBlock())) {
                stale.add(l);
            }
        }
        return stale;
    }

    // ---------------------------------------------------------------- helpers

    /** True if the block is a sign whose first line starts with the command prefix. */
    public static boolean isCommandSign(Block block) {
        if (!(block.getState() instanceof Sign)) {
            return false;
        }
        return isCommandLine(((Sign) block.getState()).getLine(0));
    }

    /** True if a sign's first line marks it as a command sign. */
    public static boolean isCommandLine(String firstLine) {
        if (firstLine == null) {
            return false;
        }
        String trimmed = ChatColor.stripColor(firstLine).trim();
        return !trimmed.isEmpty() && trimmed.charAt(0) == COMMAND_PREFIX;
    }

    public static BlockVector toVector(Location location) {
        return new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String format(BlockVector v) {
        return v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ();
    }

    public static String format(Location l) {
        return format(toVector(l));
    }

    private static BlockVector parse(String entry) {
        if (entry == null) {
            return null;
        }
        String[] parts = entry.trim().split("[ ,]+");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new BlockVector(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
