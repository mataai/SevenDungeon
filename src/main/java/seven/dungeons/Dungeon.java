package seven.dungeons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import seven.dungeons.bundle.AnchorStore;
import seven.dungeons.bundle.DungeonBundle;
import seven.dungeons.managers.DungeonManager;

/**
 * A dungeon: a template world plus the settings, command sign positions and
 * messages stored in its bundle folder (see {@link DungeonBundle}).
 */
public class Dungeon {

    public static final String DEFAULT_VERSION = "1.0.0";

    private SevenDungeons plugin;

    private String id;
    private String worldName;

    // Self-contained folder holding everything below except the world itself.
    private DungeonBundle bundle;
    private AnchorStore anchors;
    private Map<Integer, String> messages;

    private int life;
    private int nbPlayers;
    private int time;
    private boolean timeStop;
    private String weather;
    private boolean magic;
    private boolean hunger;
    private boolean godmode;
    private int timeLimit;
    private boolean buildmode;
    private int gem;
    private String name;
    // Author-facing version of the dungeon content, e.g. "1.2.0". Independent of the bundle format.
    private String version;

    /**
     * Creates the dungeon object and registers it. If a bundle for this id already
     * exists on disk its settings, anchors and messages are loaded; otherwise the
     * dungeon starts with defaults and nothing is written until {@link #saveDungeon()}.
     */
    public Dungeon (SevenDungeons plugin, String worldName, String id)
    {
        this.plugin = plugin;
        this.worldName = worldName;
        this.id = id;

        //Initialization with default values, overridden by dungeon.yml if it exists.
        this.life = 0;
        this.nbPlayers = 1;
        this.time = 6000;
        this.timeStop = true;
        this.weather = "clear";
        this.magic = false;
        this.hunger = true;
        this.godmode = false;
        this.timeLimit = -1;
        this.buildmode = false;
        this.gem = 0;
        this.name = "NoName";
        this.version = DEFAULT_VERSION;

        DungeonManager.dungeons.add(this);

        this.bundle = new DungeonBundle(plugin, id);
        this.bundle.loadSettings(this);
        this.anchors = this.bundle.openAnchors(worldName);
        this.messages = this.bundle.loadMessages();
    }

    //Get the World File of this dungeon.
    public File getWorldFile()
    {
        File file = new File(this.plugin.multiverse.getServerFolder().getPath(), this.worldName);
        return file;
    }

    //Write the settings to the bundle.
    public void saveDungeon()
    {
        this.bundle.saveSettings(this);
    }

    //Remove this dungeon from disk and the current server session. The template world is kept.
    public void remove()
    {
        DungeonManager.dungeons.remove(this);
        this.bundle.delete();
    }

    // ---------------------------------------------------------------- bundle contents

    public DungeonBundle getBundle() {
        return this.bundle;
    }

    public AnchorStore getAnchors() {
        return this.anchors;
    }

    /** Command sign positions in the template world, read from the anchor file. */
    public List<Location> getSigns() {
        return this.anchors.getLocations();
    }

    public boolean addSign(Location location) {
        return this.anchors.add(location);
    }

    public boolean removeSign(Location location) {
        return this.anchors.remove(location);
    }

    /** Message id to text, as used by message signs. */
    public Map<Integer, String> getMessages() {
        return this.messages;
    }

    /** Replaces the messages and writes messages.yml. */
    public void setMessages(Map<Integer, String> messages) {
        this.messages = messages;
        this.bundle.saveMessages(messages);
    }

    /**
     * Migration helper: scans the template world from (0, minHeight, 0) up to the given
     * corner for command signs and replaces the anchor file with what was found.
     * Prefer placing signs normally; they are registered automatically.
     *
     * @return number of anchors found, or -1 if the template world is not loaded
     */
    public int importSignsFromWorld(Location corner) {
        World world = Bukkit.getWorld(this.worldName);
        if(world == null) {
            return -1;
        }
        SevenDungeons.log("Scanning dungeon \"" + this.id + "\" for command signs.");
        List<Location> found = new ArrayList<Location>();
        int maxX = corner.getBlockX();
        int maxY = corner.getBlockY();
        int maxZ = corner.getBlockZ();
        for(int x = 0; x < maxX; x++) {
            for(int y = world.getMinHeight(); y < maxY; y++) {
                for(int z = 0; z < maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if(AnchorStore.isCommandSign(block)) {
                        found.add(block.getLocation());
                    }
                }
            }
        }
        this.anchors.replaceAll(found);
        world.save();
        return found.size();
    }

    // ---------------------------------------------------------------- settings

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        if(life < -1 || life > 100)
        {
            return;
        }
        this.life = life;

    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        if(nbPlayers < 0 || nbPlayers > 8)
        {
            return;
        }
        this.nbPlayers = nbPlayers;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        if(time < 0 || time > 23999)
        {
            return;
        }
        this.time = time;
    }

    public boolean isTimeStop() {
        return timeStop;
    }

    public void setTimeStop(boolean timeStop) {
        this.timeStop = timeStop;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        switch(weather.toLowerCase())
        {
            case "rain" : this.weather = "rain"; break;
            case "thunder" : this.weather = "thunder"; break;
            default : this.weather = "clear";
        }
    }

    public boolean isMagic() {
        return magic;
    }

    public void setMagic(boolean magic) {
        this.magic = magic;
    }

    public boolean isHunger() {
        return hunger;
    }

    public void setHunger(boolean hunger) {
        this.hunger = hunger;
    }

    public boolean isGodmode() {
        return godmode;
    }

    public void setGodmode(boolean godmode) {
        this.godmode = godmode;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        if(timeLimit < -1 || timeLimit > 7200)
        {
            return;
        }
        this.timeLimit = timeLimit;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public String getId() {
        return this.id;
    }

    public boolean isBuildmode() {
        return this.buildmode;
    }

    public void setBuildmode(boolean mode) {
        this.buildmode = mode;
    }

    public int getGem() {
        return this.gem;
    }

    public void setGem(int gem) {
        this.gem = gem;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the content version. Accepts 1 to 20 characters of letters, digits, dots,
     * dashes and underscores. Returns false and leaves the version untouched otherwise.
     */
    public boolean setVersion(String version) {
        if(version == null || !version.matches("[A-Za-z0-9._-]{1,20}")) {
            return false;
        }
        this.version = version;
        return true;
    }

    /**
     * Increments the last numeric component of the version, so "1.0.2" becomes "1.0.3"
     * and "beta-4" becomes "beta-5". Returns false if the version holds no number.
     */
    public boolean bumpVersion() {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)(?!.*\\d)").matcher(this.version);
        if(!m.find()) {
            return false;
        }
        long next = Long.parseLong(m.group(1)) + 1;
        return this.setVersion(this.version.substring(0, m.start()) + next + this.version.substring(m.end()));
    }
}
