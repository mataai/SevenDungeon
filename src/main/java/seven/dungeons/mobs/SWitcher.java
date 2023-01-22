package seven.dungeons.mobs;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import seven.dungeons.signs.SpawnMobSign;

import java.util.ArrayList;

public class SWitcher extends SevenMob {

	private SpawnMobSign sms;
    private int cooldown;

    public SWitcher(SpawnMobSign sms) {
        this.entityType = EntityType.WITCH;
        this.mobName = "S. Witch";
        this.hasMagic = true;
        this.sms = sms;
        this.cooldown = 10;
    }
    
    @Override
    public void costumize(LivingEntity entity) {
        this.mob = entity;
        this.mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        this.mob.setHealth(20);
        this.updateVisibleName();
        this.mob.setRemoveWhenFarAway(false);
    }
    
    @Override
    public void spell() {
        this.cooldown--;
        if(this.cooldown == 0){
            this.cooldown = 40;

            this.mob.getLocation().getWorld().spawnParticle(Particle.SPELL_WITCH, this.mob.getEyeLocation(), 5, 1, 1, 1, 1);
            this.mob.getWorld().playSound(this.mob.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 3, 1.5f);

            ArrayList<Location> playerLocations = new ArrayList<Location>();
            ArrayList<Player> players = this.sms.getGame().getPlayers();
            for(Player p : players){
                playerLocations.add(p.getLocation());
            }


            for(int i = playerLocations.size() - 1, j = 0; i >= 0 && i < players.size() ; i--, j++){
                int finalJ = j;
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(this.sms.getGame().getPlugin(), () -> players.get(finalJ).teleport(playerLocations.get(finalI), PlayerTeleportEvent.TeleportCause.PLUGIN), 1l);
            }
        }

    }
            
}
