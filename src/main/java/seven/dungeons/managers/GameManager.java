package seven.dungeons.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import seven.dungeons.Dungeon;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.DungeonTeam;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.*;
import seven.dungeons.signs.special.*;

public class GameManager {
    static public ArrayList<Game> games = new ArrayList<Game>();
    private SevenDungeons plugin;
    static private int counter;
    
    public GameManager(SevenDungeons plugin) {
        this.plugin = plugin;
        this.counter = 1;
    }
    
    public Game addGame(DungeonTeam team) {
        Game game = new Game(team, team.getDungeon(), this.plugin, team.getPlayers().get(0).getPlayer().getWorld());
        GameManager.games.add(game);
        return game;
    }
    
    static public int getCounter() {
        return GameManager.counter++;
    }
    
    public void removeGame(Game game) {
        GameManager.games.remove(game);
    }
    
    public void getMessages(Game game) {
        try {
            Connection connection = this.plugin.getConnection();
            PreparedStatement ps = connection.prepareStatement("select * from messages where dungeon_id=?");
            ps.setString(1, game.getDungeon().getId());
            ResultSet rs = ps.executeQuery();
            String message;
            int id;
            while(rs.next()) {
                id = rs.getInt("id");
                message = rs.getString("message");
                game.addMessage(id, message);
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void compile(Game game) {
        World world = game.getWorld();
        ArrayList<ActivableSign> gameSigns = game.getSigns();
        for(Location l : game.getDungeon().getSigns()) {
            Location location = new Location(world,l.getBlockX(),l.getBlockY(),l.getBlockZ());
            Block block = location.getBlock();
            if(block.getType().equals(Material.OAK_SIGN) || block.getType().equals(Material.OAK_WALL_SIGN)) {
                Sign sign = (Sign)(block.getState());
                String firstLine = sign.getLine(0);
                if(firstLine != null) {
                    switch(firstLine.trim()) {
                        // Basic signs
                        case "=as" : gameSigns.add(new ActivationSequenceSign(sign,game)); break;
                        case "=b" : gameSigns.add(new BlockSign(sign, game)); break;
                        case "=ba" : gameSigns.add(new BlockAreaSign(sign, game)); break;
                        case "=bi" : gameSigns.add(new BlockIllusionSign(sign, game)); break;
                        case "=bg" : gameSigns.add(new BlockGhost(sign, game)); break;
                        case "=bf" : gameSigns.add(new BlockFallSign(sign,game)); break;
                        case "=bm" : gameSigns.add(new BigMessageSign(sign, game)); break;
                        case "=bs" : game.getBlockLibrary().add(new BlockStockSign(sign, game)); break;
                        case "=c" : gameSigns.add(new SphericCaptorSign(sign, game)); break;
                        case "=cc": gameSigns.add(new CuboidCaptorSign(sign, game)); break;
                        case "=ch" : gameSigns.add(new CheckpointSign(sign, game)); break;
                        case "=cha" : gameSigns.add(new CheckpointAreaSign(sign, game)); break;
                        case "=clh" : gameSigns.add(new ClockHand(sign, game)); break;
                        case "=cr" : gameSigns.add(new CrystalSign(sign, game)); break;
                        case "=d" : gameSigns.add(new DelaySign(sign, game)); break;
                        case "=da" : gameSigns.add(new DamageAreaSign(sign,game)); break;
                        case "=gd" : gameSigns.add(new DelayGroupSign(sign, game)); break;
                        case "=e": gameSigns.add(new ExitSign(sign, game)); break;
                        case "=ex": gameSigns.add(new ExplosionSign(sign, game)); break;
                        case "=f" : gameSigns.add(new FanSign(sign,game)); break;
                        case "=g": gameSigns.add(new GemSign(sign, game)); break;
                        case "=hb": gameSigns.add(new HungryBlockSign(sign,game)); break;
                        case "=hc" : gameSigns.add(new HealthChange(sign, game)); break;
                        case "=kp" : gameSigns.add(new KillPlayerSign(sign, game)); break;
                        case "=la" : gameSigns.add(new LauncherSign(sign,game)); break;
                        case "=lc": gameSigns.add(new ChestSign(sign, game)); break;
                        case "=lcs": game.getChestLibrary().add(new ChestStockSign(sign, game)); break;
                        case "=li" : gameSigns.add(new LightningSign(sign, game)); break;
                        case "=lo" : gameSigns.add(new LoopSign(sign,game)); break;
                        case "=m" : gameSigns.add(new MessageSign(sign, game)); break;
                        case "=ms": gameSigns.add(new MushroomSign(sign,game)); break;
                        case "=nc": gameSigns.add(new NumberCaptorSign(sign,game)); break;
                        case "=pa": gameSigns.add(new PotionAreaSign(sign, game)); break;
                        case "=pc" : gameSigns.add(new ProximityCaptor(sign,game)); break;
                        case "=po" : gameSigns.add(new PotionSign(sign, game)); break;
                        case "=ps": game.getSpawns().add(new PlayerSpawnSign(sign, game)); break;
                        case "=ra": gameSigns.add(new RandomSign(sign,game)); break;
                        case "=rd" : gameSigns.add(new RedstoneDetectorSign(sign, game)); break;
                        case "=ret" : gameSigns.add(new ExplosiveTargetSign(sign, game)); break;
                        case "=rg" : gameSigns.add(new RedstoneGeneratorSign(sign, game)); break;
                        case "=rt" : gameSigns.add(new TargetSign(sign, game)); break;
                        case "=sec" : gameSigns.add(new SoulEaterCaptorSign(sign, game)); break;
                        case "=si" : gameSigns.add(new SpawnItemSign(sign, game)); break;
                        case "=sk" : gameSigns.add(new SnakeBlock(sign, game)); break;
                        case "=sm" : gameSigns.add(new SpawnMobSign(sign, game)); break;
                        case "=sn" : gameSigns.add(new NPCSign(sign, game)); break;
                        case "=so" : gameSigns.add(new SoundSign(sign, game)); break;
                        case "=tp" : gameSigns.add(new TeleportationSign(sign, game)); break;
                        
                        // Special signs
                        case "=xat" : gameSigns.add(new ArrowTrap(sign,game)); break;
                        case "=xbb" : gameSigns.add(new BalloonBurst(sign,game)); break;
                        case "=xc" : gameSigns.add(new Cursor(sign,game)); break;
                        case "=xcp" : gameSigns.add(new ColorPanel(sign,game)); break;
                        case "=xet" : gameSigns.add(new ElectricTrap(sign, game)); break;
                        case "=xf" : gameSigns.add(new FishingGame(sign,game)); break;
                        case "=xfp" : gameSigns.add(new FlipPuzzle(sign, game)); break;
                        case "=xgbb" : gameSigns.add(new GravityBalloonBurst(sign, game)); break;
                        case "=xgp" : gameSigns.add(new GuessPuzzle(sign, game)); break;
                        case "=xgt" : gameSigns.add(new LaserTrap(sign, game)); break;
                        case "=xlp" : gameSigns.add(new LightPuzzle(sign,game)); break;
                        case "=xlt" : gameSigns.add(new LavaTower(sign,game)); break;
                        case "=xlr" : gameSigns.add(new LaserGoRound(sign, game)); break;
                        case "=xmf" : gameSigns.add(new MakeshiftFishingGame(sign, game)); break;
                        case "=xmm" : gameSigns.add(new MushroomMayhem(sign,game)); break;
                        case "=xmt" : gameSigns.add(new MinecartTour(sign,game)); break;
                        case "=xp" : gameSigns.add(new Painting(sign, game)); break;
                        case "=xpic" : gameSigns.add(new PicrossPuzzle(sign, game)); break;
                        case "=xpr" : gameSigns.add(new PigRunGame(sign, game)); break;
                        case "=xsd" : gameSigns.add(new Stardust(sign,game)); break;
                        case "=xsm" : gameSigns.add(new SlotMachine(sign,game)); break;
                        case "=xsmt" : gameSigns.add(new SeaMineTrap(sign, game)); break;
                        default: break;
                    }
                }  
            }
        }
        for(DungeonSign ds : gameSigns) {
            if(ds instanceof BlockSign) {
                ((BlockSign) ds).off();
            }
        }
        for(Location l : game.getDungeon().getSigns()) {
            Location location = new Location(world,l.getBlockX(),l.getBlockY(),l.getBlockZ());
            Block block = location.getBlock();
            if(block.getType().equals(Material.OAK_SIGN) || block.getType().equals(Material.OAK_WALL_SIGN)) {
                location.getBlock().setType(Material.AIR);
            }
        }
        for (Entity e : world.getEntities()) {
            if (e instanceof Item)
                e.remove();
            if(e instanceof ArmorStand)
                e.setInvulnerable(true);
        }  
        
        this.getMessages(game);
    }
    
    public void setDungeonProperties(Game game) {
        World world = game.getWorld();
        Dungeon dungeon = game.getDungeon();
        if(!dungeon.isTimeStop()) {
            world.setGameRuleValue("doDaylightCycle", "true");
        }
        world.setTime(dungeon.getTime());
        switch(dungeon.getWeather()) {
            case "clear" : world.setStorm(false); world.setWeatherDuration(4000); break; 
            case "thunder" : world.setStorm(true); world.setThundering(true); world.setThunderDuration(4000);
            case "rain" :  world.setStorm(true); world.setWeatherDuration(4000);
                for(DungeonPlayer dp : game.getTeam().getPlayers()) {
                    dp.getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
                }
                world.setStorm(true);
            break;
            default : break;
        }
        for(DungeonPlayer dp : game.getTeam().getPlayers()) {
            dp.setLife(dungeon.getLife());
        }
        game.setMaterial(this.plugin.dungeonManager.getGem(game.getDungeon().getGem()));
    }
    
    public Game getGameByMap(String name) {
        for(Game g : GameManager.games) {
            if(g.getInstanceName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }

}
   