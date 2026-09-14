package seven.dungeons.bundle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import seven.dungeons.Dungeon;
import seven.dungeons.SevenDungeons;

/**
 * A dungeon is a self-contained folder under {@code plugins/SevenDungeons/dungeons/<id>/}:
 *
 * <pre>
 * dungeon.yml    settings (world name, lives, team size, time, weather, ...)
 * anchors.yml    positions of the command signs, see {@link AnchorStore}
 * messages.yml   texts shown by message signs, keyed by message id
 * </pre>
 *
 * The folder is the source of truth for everything the dungeon needs except the
 * template world itself, which stays a normal world folder named in dungeon.yml.
 * Copying the folder to another server (with its world) copies the dungeon.
 */
public class DungeonBundle {

    public static final String ROOT_FOLDER = "dungeons";
    public static final String SETTINGS_FILE = "dungeon.yml";
    public static final String MESSAGES_FILE = "messages.yml";

    /**
     * Layout version of the bundle files, written as {@code format} in dungeon.yml.
     * Bump it whenever a key changes meaning or a file is renamed, and add a step to
     * {@link #migrate}. Bundles without the key are treated as format 0.
     */
    public static final int FORMAT_VERSION = 1;

    private final File directory;
    private final String id;

    public DungeonBundle(Plugin plugin, String id) {
        this.id = id;
        this.directory = new File(new File(plugin.getDataFolder(), ROOT_FOLDER), id);
    }

    /** Root folder that holds every bundle. */
    public static File getRootFolder(Plugin plugin) {
        return new File(plugin.getDataFolder(), ROOT_FOLDER);
    }

    /** Ids of every bundle on disk that has a settings file. */
    public static List<String> listIds(Plugin plugin) {
        List<String> ids = new ArrayList<String>();
        File[] dirs = getRootFolder(plugin).listFiles();
        if (dirs == null) {
            return ids;
        }
        for (File dir : dirs) {
            if (dir.isDirectory() && new File(dir, SETTINGS_FILE).isFile()) {
                ids.add(dir.getName());
            }
        }
        return ids;
    }

    /** Reads only the world name of a bundle, without constructing a dungeon. Null if absent. */
    public static String readWorldName(Plugin plugin, String id) {
        File settings = new File(new DungeonBundle(plugin, id).directory, SETTINGS_FILE);
        if (!settings.isFile()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(settings).getString("world");
    }

    /**
     * Reads the bundle format of a dungeon without constructing it.
     *
     * @return the format, 0 if the key is missing, -1 if there is no settings file
     */
    public static int readFormat(Plugin plugin, String id) {
        File settings = new File(new DungeonBundle(plugin, id).directory, SETTINGS_FILE);
        if (!settings.isFile()) {
            return -1;
        }
        return YamlConfiguration.loadConfiguration(settings).getInt("format", 0);
    }

    // ---------------------------------------------------------------- layout

    public String getId() {
        return this.id;
    }

    public File getDirectory() {
        return this.directory;
    }

    public File getSettingsFile() {
        return new File(this.directory, SETTINGS_FILE);
    }

    public File getMessagesFile() {
        return new File(this.directory, MESSAGES_FILE);
    }

    public boolean exists() {
        return this.getSettingsFile().isFile();
    }

    public AnchorStore openAnchors(String worldName) {
        return new AnchorStore(this.directory, this.id, worldName);
    }

    // ---------------------------------------------------------------- settings

    /**
     * Applies dungeon.yml onto the dungeon. Does nothing if the file is missing.
     * A bundle written by an older plugin is migrated and rewritten in the current
     * format; a bundle newer than {@link #FORMAT_VERSION} is applied as-is with a warning,
     * since {@link seven.dungeons.managers.DungeonManager#loadDungeons} skips those.
     */
    public void loadSettings(Dungeon dungeon) {
        File file = this.getSettingsFile();
        if (!file.isFile()) {
            return;
        }
        YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
        int format = c.getInt("format", 0);
        if (format < FORMAT_VERSION) {
            this.migrate(c, format);
        } else if (format > FORMAT_VERSION) {
            SevenDungeons.log("Dungeon \"" + this.id + "\" uses bundle format " + format + " but this plugin only knows format "
                    + FORMAT_VERSION + ". Update the plugin.", "SevenDungeons", ChatColor.RED);
        }
        dungeon.setVersion(c.getString("version", dungeon.getVersion()));
        dungeon.setName(c.getString("name", dungeon.getName()));
        dungeon.setLife(c.getInt("life", dungeon.getLife()));
        dungeon.setNbPlayers(c.getInt("players", dungeon.getNbPlayers()));
        dungeon.setTime(c.getInt("time", dungeon.getTime()));
        dungeon.setTimeStop(c.getBoolean("timeStop", dungeon.isTimeStop()));
        dungeon.setWeather(c.getString("weather", dungeon.getWeather()));
        dungeon.setMagic(c.getBoolean("magic", dungeon.isMagic()));
        dungeon.setHunger(c.getBoolean("hunger", dungeon.isHunger()));
        dungeon.setGodmode(c.getBoolean("godmode", dungeon.isGodmode()));
        dungeon.setTimeLimit(c.getInt("timeLimit", dungeon.getTimeLimit()));
        dungeon.setGem(c.getInt("gem", dungeon.getGem()));
        if (format < FORMAT_VERSION) {
            this.saveSettings(dungeon);
            SevenDungeons.log("Dungeon \"" + this.id + "\": bundle migrated from format " + format + " to " + FORMAT_VERSION + ".");
        }
    }

    /**
     * Upgrades the loaded settings in memory from {@code from} to {@link #FORMAT_VERSION},
     * one step at a time. Each case rewrites keys so the load code below can read them.
     */
    private void migrate(YamlConfiguration c, int from) {
        switch (from) {
            case 0:
                // Format 0: bundles written before versioning existed. Same keys as format 1,
                // only "format" and "version" are missing and get their defaults on save.
                // fall through to the next step when format 2 is introduced
            default:
                break;
        }
    }

    /** Writes dungeon.yml from the dungeon's current settings, creating the folder if needed. */
    public void saveSettings(Dungeon dungeon) {
        YamlConfiguration c = new YamlConfiguration();
        c.options().header("SevenDungeons dungeon bundle. Edit with /7d editdungeon or by hand and reload.");
        c.set("format", FORMAT_VERSION);
        c.set("id", dungeon.getId());
        c.set("version", dungeon.getVersion());
        c.set("world", dungeon.getWorldName());
        c.set("name", dungeon.getName());
        c.set("players", dungeon.getNbPlayers());
        c.set("life", dungeon.getLife());
        c.set("timeLimit", dungeon.getTimeLimit());
        c.set("time", dungeon.getTime());
        c.set("timeStop", dungeon.isTimeStop());
        c.set("weather", dungeon.getWeather());
        c.set("hunger", dungeon.isHunger());
        c.set("godmode", dungeon.isGodmode());
        c.set("magic", dungeon.isMagic());
        c.set("gem", dungeon.getGem());
        this.write(c, this.getSettingsFile());
    }

    // ---------------------------------------------------------------- messages

    /** Message id to text, in file order. Empty if the file is missing. */
    public Map<Integer, String> loadMessages() {
        Map<Integer, String> messages = new LinkedHashMap<Integer, String>();
        File file = this.getMessagesFile();
        if (!file.isFile()) {
            return messages;
        }
        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection("messages");
        if (section == null) {
            return messages;
        }
        for (String key : section.getKeys(false)) {
            try {
                messages.put(Integer.parseInt(key), section.getString(key));
            } catch (NumberFormatException e) {
                SevenDungeons.log("Dungeon \"" + this.id + "\": message key \"" + key + "\" is not a number, ignored.", "SevenDungeons", ChatColor.RED);
            }
        }
        return messages;
    }

    public void saveMessages(Map<Integer, String> messages) {
        YamlConfiguration c = new YamlConfiguration();
        c.options().header("Texts used by message signs (=m, =bm). Keys are the message ids written on the signs.");
        for (Map.Entry<Integer, String> entry : messages.entrySet()) {
            c.set("messages." + entry.getKey(), entry.getValue());
        }
        this.write(c, this.getMessagesFile());
    }

    // ---------------------------------------------------------------- lifecycle

    /** Deletes the whole bundle folder. The template world is left untouched. */
    public void delete() {
        deleteRecursively(this.directory);
    }

    private void write(YamlConfiguration config, File file) {
        file.getParentFile().mkdirs();
        try {
            config.save(file);
        } catch (IOException e) {
            SevenDungeons.log("Could not write " + file.getPath(), "SevenDungeons", ChatColor.RED);
            e.printStackTrace();
        }
    }

    private static void deleteRecursively(File file) {
        if (!file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            SevenDungeons.log("Could not delete " + file.getPath(), "SevenDungeons", ChatColor.RED);
        }
    }
}
