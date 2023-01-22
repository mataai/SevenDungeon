package seven.dungeons.mobs;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import seven.dungeons.SevenDungeons;
import seven.dungeons.signs.SpawnMobSign;

public class SpawnTask extends BukkitRunnable {

    private SevenMob mob;
    private SpawnMobSign sms;
    private Location location;
    
    public SpawnTask(SevenMob mob, SpawnMobSign sms) {
        this.mob = mob;
        this.sms = sms;
        this.location = null;
    }
    
    public SpawnTask(SevenMob mob, Location location) {
        this.mob = mob;
        this.sms = null;
        this.location = location;
    }
    
    @Override
    public void run() {
        Location spawnPosition = (this.sms == null) ? this.location : this.sms.getSpawnPosition();
        LivingEntity entity = (LivingEntity) spawnPosition.getWorld().spawnEntity(spawnPosition, this.mob.getEntityType());
        this.costumize(entity, this.mob);
    }
    
    public void costumize(LivingEntity entity, SevenMob mob) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                mob.costumize(entity);
            }
        }.runTaskLater(SevenDungeons.getPlugin(SevenDungeons.class), 1);
    }
}
