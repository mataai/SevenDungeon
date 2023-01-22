package seven.dungeons.signs;

import java.security.Guard;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

import seven.dungeons.DungeonPlayer;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.mobs.*;

public class SpawnMobSign extends CaptorSign {
    
    private int signal; 
    private String mobId;
    private int amount;
    private double radius;
    private double delay;
    private ArrayList<SevenMob> mobs = new ArrayList<SevenMob>();

    public SpawnMobSign(Sign sign, Game game) {
        super(sign, game);
        try {
            String[] line2 = this.sign.getLine(1).trim().split(" ");
            if(line2.length > 1) {
                this.signal = Integer.parseInt(line2[1]);
            }
            else {
                this.signal = -1;
            }
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.mobId = line3[0];
            if(line3.length > 1) {
                this.amount = Integer.parseInt(line3[1]);
            }
            else {
                this.amount = 1;
            }
            String line4 = this.sign.getLine(3);
            this.delay = 0;
            if(line4 == null || line4.isEmpty()) {
                this.radius = 0;
            }
            else {
                String[] line4tab = line4.trim().split(" ");  
                this.radius = Integer.parseInt(line4tab[0]);
                if(line4tab.length > 1) {
                    this.delay = Float.parseFloat(line4tab[1]);
                }
            }
            
        }catch(Exception e) {
            SevenDungeons.log("SpawnMob's sign is wrong at " + this.location.toString() + ".");
        }
    }
    
    public Location getSpawnPosition() {
        if(this.radius == 0) {
            return this.location.clone().add(0.5,0,0.5);
        }
        double a = Math.random() * 2 * Math.PI;
        double r = this.radius * Math.sqrt(Math.random());

        double x = Math.floor(r * Math.cos(a));
        double z = Math.floor(r * Math.sin(a));
        
        Location position = new Location(this.location.getWorld(), this.location.getX() + x + 0.5, this.location.getY(), this.location.getZ() + z + 0.5);
        while(position.getBlock().getType().isSolid()) {
            position.setY(position.getY() + 1);
        }
        return position;
    }
    
    @Override
    public void on() {
        for(int i = 0; i < this.amount; i++) {
            SevenMob mob = null;
            switch(this.mobId) {
                case "aguardian" : mob = new ArmoredGuardian(); break;
            	case "airs" : mob = new AirSkeleton(this); break; // TODO
            	case "airz" : mob = new AirZombie(this); break; // TODO
                case "allay" : mob = new NormalAllay(); break;
                case "babybomber" : mob = new BabyBomber(); break;
                case "babyslime" : mob = new BabySlime(); break;
                case "birdhours" : mob = new BirdOfHours(this); break;
                case "birdminutes" : mob = new BirdOfMinutes(this); break;
                case "birdseconds" : mob = new BirdOfSeconds(this); break;
                case "bomberking" : mob = new BomberKing(this); break;
                case "calmz" : mob = new CalmZombie(); break;
                case "cskeleton" : mob = new CadetSkeleton(); break;
                case "czombie" : mob = new CadetZombie(); break;
                case "disabler" : mob = new Disabler(this); break;
                case "ezombie" : mob = new EarthZombie(this); break; // TODO
                case "firei" : mob = new FireIllager(); break;
                case "firez" : mob = new FireZombie(); break; // TODO
                case "fpumpkin" : mob = new FlyingPumpkin(); break;
                case "grskeleton" : mob = new GrassySkeleton(this); break; // TODO
                case "grzombie" : mob = new GrassyZombie(this); break; // TODO
                case "guardianqueen" : mob = new GuardianQueen(this); break;
                case "gzombie" : mob = new GeminiZombie(this); break;
                case "hotwitch" : mob = new FireWitch(this); break; // TODO
                case "icearcher" : mob = new IceArcher(this); break; // TODO
                case "magmas" : mob = new MagmaSkeleton(); break;
                case "magmaz" : mob = new MagmaZombie(); break;
                case "masterr" : mob = new MasterRavager(); break;
                case "masters" : mob = new MasterSkeleton(); break;
                case "masterz" : mob = new MasterZombie(); break;
                case "megaslime" : mob = new MegaSlime(this); break;
                case "moron1" : mob = new MoronZombie(); break;
                case "moron2" : mob = new MoronSkeleton(); break;
                case "pumpkinbomb" : mob = new PumpkinBomber(this); break;
                case "pumpkinking" : mob = new PumpkinKing(this); break;
                case "pumpkinmage" : mob = new PumpkinMage(); break;
                case "rewinder" : mob = new Rewinder(this); break;
                case "rzombie" : mob = new RangerZombie(); break;
                case "shealer" : mob = new SkeletonHealer(this); break;
                case "skeletonmage" : mob = new SkeletonMage(this.sign.getLocation()); break;
                case "slime" : mob = new NormalSlime(this); break;
                case "sorceress" : mob = new Sorceress(this); break;
                case "souleater" : mob = new SoulEater(this); break;
                case "sour" : mob = new SourSkeleton(); break;
                case "soldiers" : mob = new SkeletonSoldier(); break;
                case "shiveringz" : mob = new ShiveringZombie(this); break; // TODO
                case "sweet" : mob = new SweetSkeleton(); break;
                case "switcher" : mob = new SWitcher(this); break;
                case "szombie" : mob = new SoldierZombie(); break;
                case "veterans" : mob = new VeteranSkeleton(); break;
                case "veteranz" : mob = new VeteranZombie(); break;
                case "wdrowned" : mob = new WeakDrowned(); break;
                case "wguardian" : mob = new WeakGuardian(); break;
                case "whusk" : mob = new WeakHusk(); break;
                case "wmagma" : mob = new WeakMagmaCube(); break;
                case "wzombie" : mob = new WeakZombie(); break;
                case "wskeleton" : mob = new WeakSkeleton(); break;
                case "wwitch" : mob = new WeakWitch(); break;
                case "wwithers" : mob = new WeakWitherSkeleton(); break;
                case "wravager" : mob = new WeakRavager(); break;
                case "wwarden" : mob = new WeakWarden(); break;
            }
            this.mobs.add(mob);
            if(mob != null) {
                new SpawnTask(mob, this).runTaskLater(this.game.getPlugin(), (long) (this.delay*20*i));
            }
        }
        this.game.getSigns5ticks().add(this);
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
        for(SevenMob sm : this.mobs) {
            sm.kill();
        }
        this.mobs.clear();
    }
    
    @Override
    public void isTriggered() {
        boolean allDead = true;
        for(SevenMob sm : this.mobs) {
            if(sm != null && !sm.isDead()) {
                sm.updateVisibleName();
                if(sm.hasMagic() && sm.getMob() != null) {
                    sm.spell();
                }
                allDead = false;
            }
            /*else if(sm.hasMagic()){
                sm.death();
            }*/
        }
        if(allDead) {
            this.trigger(null);
        }
    }
    
    @Override
    public void trigger(DungeonPlayer dp) {
        this.off();
        this.game.getSigns5ticks().remove(this);
        if(this.signal > 0) {
            this.game.activate(this.signal, null);
        }
    }
    
    public Game getGame() {
        return this.game;
    }

}
