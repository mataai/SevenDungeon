package seven.dungeons.listeners;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import seven.dungeons.DungeonPlayer;
import seven.dungeons.DungeonTeam;
import seven.dungeons.Game;
import seven.dungeons.Dungeon;
import seven.dungeons.SevenDungeons;
import seven.dungeons.bundle.AnchorStore;
import seven.dungeons.managers.GameManager;
import seven.dungeons.signs.AnimatedSign;
import seven.dungeons.signs.CaptorSign;
import seven.dungeons.signs.NPCSign;

public class DungeonListener implements Listener {
    
    private SevenDungeons plugin;
    private ArrayList<Player> recentPlayers = new ArrayList<Player>();
    private ArrayList<Player> builders = new ArrayList<Player>();
    private FixedMetadataValue xlp;
    
    public DungeonListener (SevenDungeons plugin) {
        this.plugin = plugin;
        xlp = new FixedMetadataValue(this.plugin, "xlp");
        
        // Runnable timers for events
        this.timer2ticks();
        this.asyncTimer2ticks();
        this.timer5ticks();
        this.timer20ticks();
    }
    
    //TODO : Put in a more general plugin
    //Prevent grabing/putting stuff on invisible armorstand
    @EventHandler
    public void manipulate(PlayerArmorStandManipulateEvent e)
    {
            if(!e.getRightClicked().isVisible())
            {
                e.setCancelled(true);
            }
    }
    
    //TODO : Put in a more general plugin
    // Separate chat per world
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent e) {
        if(e.getMessage().matches("#.*")) {
            String message = e.getMessage().substring(1);
            e.setFormat("§c§lGlobal§r §e%s §r: %s");
            e.setMessage(message);
            return;
        }
        e.setFormat("§e%s §r: %s");
        /*Player sender = e.getPlayer();
        if(sender.getWorld().getName().matches("instance.*")) {
            Set<Player> r = e.getRecipients();
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!p.getWorld().getName().equals(sender.getWorld().getName())) {
                        r.remove(p);
                }
            }
        }*/
    }
    
    //Make sure ice / snow doesn't melt
    @EventHandler(priority = EventPriority.NORMAL)
    public void onMelt(BlockFadeEvent e) {
        Block b = e.getBlock();
        if(b.getType() == Material.ICE || b.getType() == Material.SNOW) {
            e.setCancelled(true);
        }
    }

    // Prevent water from freezing
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFreeze(BlockFormEvent e){
        e.setCancelled(true);
    }
    
    //Cancel dropping important items...
    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemDrop(PlayerDropItemEvent e) {
        if(e.getPlayer().hasMetadata("noDrop")) {
            e.setCancelled(true);
        }
    }
    
    // Make sure mobs drop nothing.
    @EventHandler(priority = EventPriority.NORMAL)
    public void onMobDeath(EntityDeathEvent e) {
        e.setDroppedExp(0);
        e.getDrops().clear();
    }
    
    //Make sure no block spreads
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }
    
    // Fishing game fish meta-marking
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFishing(PlayerFishEvent e) {
        if(e.getCaught() == null) {
            return;
        }
        Entity fish = e.getCaught();
        if(fish.getType() == EntityType.SALMON || fish.getType() == EntityType.TROPICAL_FISH 
                || fish.getType() == EntityType.COD || fish.getType() == EntityType.PUFFERFISH) {
            FixedMetadataValue fmv = new FixedMetadataValue(this.plugin, e.getPlayer().getName());
            fish.setMetadata("xlp", fmv);
        }
    }
    
    // Make sure players can't leave Minecart Tour Game
    @EventHandler(priority = EventPriority.NORMAL)
    public void onExitVehicle(VehicleExitEvent e) {
        if(e.getVehicle().hasMetadata("xmt")) {
            //Bukkit.getScheduler().runTaskLater(plugin, () -> e.getVehicle().addPassenger(e.getExited()), 2);
            this.backInMinecart(e.getExited(), e.getVehicle());
        }
    }
    
    /*
     * Put back player in Minecart
     */
    public void backInMinecart(LivingEntity le, Vehicle v) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(!v.isDead() && !le.isInsideVehicle()) {
                    v.addPassenger(le);
                }
            }
        }.runTaskLater(this.plugin, 2);
    }
    
    // Can't destroy Minecart in Minecart Tour Game
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDestroyVehicle(VehicleDestroyEvent e) {
        if(e.getVehicle().hasMetadata("xmt")) {
            e.setCancelled(true);
        }
    }
    
    // Make sure explosions leaves no blocks on the ground.
    @EventHandler(priority = EventPriority.NORMAL)
    public void onExplosion(BlockExplodeEvent e) {
        e.setYield(0);
    }
    
    // Prevent TNT from doing damage
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNT(EntityExplodeEvent e) {
        if(e.getEntity().hasMetadata("noExplosion")) {
            e.blockList().clear();
        }
    }
    
    // Can't break blocks in dungeons and lobbies.
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().hasMetadata("bmv")) {
            return;
        }
        String worldName = e.getBlock().getLocation().getWorld().getName();
        if(worldName.matches("(instance.*)|(lobby.*)") && !this.builders.contains(e.getPlayer())) {
            Player p = e.getPlayer();
            if(p.hasPotionEffect(PotionEffectType.JUMP)) {
                p.removePotionEffect(PotionEffectType.JUMP);
            }
            PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 20, -3, false, false);
            p.addPotionEffect(jump);
            e.setCancelled(true);
        }
    }
    
    // Can't place blocks in dungeons and lobbies.
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        if(worldName.matches("(instance.*)|(lobby.*)") && !this.builders.contains(e.getPlayer())) {
            Player p = e.getPlayer();
            if(p.hasPotionEffect(PotionEffectType.JUMP)) {
                p.removePotionEffect(PotionEffectType.JUMP);
            }
            PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 20, -3, false, false);
            p.addPotionEffect(jump);
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPutItem(PlayerInteractEntityEvent e) {
        if(e.getRightClicked() instanceof ItemFrame && !this.builders.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onHangingBreak(HangingBreakByEntityEvent e) {
        String worldName = e.getEntity().getLocation().getWorld().getName();
        if(e.getRemover() instanceof Player) {
            if(worldName.matches("(instance.*)|(lobby.*)") && !this.builders.contains((Player)e.getRemover())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onHangingPlace(HangingPlaceEvent e) {
        String worldName = e.getEntity().getLocation().getWorld().getName();
        if(worldName.matches("(instance.*)|(lobby.*)") && !this.builders.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlaceLiquid(PlayerBucketEmptyEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        if(worldName.matches("(instance.*)|(lobby.*)") && !this.builders.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onRemoveLiquid(PlayerBucketFillEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        if(worldName.matches("(instance.*)|(lobby.*)") && !this.builders.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    // Prevent gems from despawning
    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemDespawn(ItemDespawnEvent e) {
        if(!e.getLocation().getWorld().getName().matches("instance.*")) {
            return;
        }
        Item item = (Item)e.getEntity();
        if(item.getItemStack().getItemMeta().getDisplayName().matches("[0-9]+g")) {
            item.setTicksLived(1);
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemPickUp(EntityPickupItemEvent e) {
    	if(!(e.getEntity() instanceof Player) && !(e.getEntity() instanceof Allay)) {
    		e.setCancelled(true);
    		return;
    	}
        if(!e.getEntity().getLocation().getWorld().getName().matches("instance.*")) {
            return;
        }
        Item item = e.getItem();
        if (item.hasMetadata("xsm"))
            e.setCancelled(true);
        ItemMeta im = item.getItemStack().getItemMeta();
        if(im.getDisplayName().matches("[0-9]+g") && (e.getEntity() instanceof Player)) {
            
            Player p = (Player)e.getEntity();
            DungeonPlayer dp = this.plugin.teamManager.findDungeonPlayer(p);
            if(dp == null) {
                return;
            }
            DungeonTeam dt = dp.getTeam();
            if(!dt.isPlaying()) {
                return;
            }
            e.setCancelled(true);
            item.remove();
            dt.getGame().gemCounter(p.getLocation());
            
        }
        if(im.getDisplayName().matches("shard[0-9]+")) {
            e.setCancelled(true);
        }
        if(im.getDisplayName().matches("[0-9]+xlr")){
            im.setDisplayName("Ingot");
            item.getItemStack().setItemMeta(im);
        }
    }
    
    // Cancel food depletion except in dungeon with hunger.
    @EventHandler(priority = EventPriority.NORMAL)
    public void onHungerChange(FoodLevelChangeEvent e) {
        if(! (e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        DungeonPlayer dp = this.plugin.teamManager.findDungeonPlayer(p);
        if(dp == null) {
            p.setFoodLevel(20);
            p.setSaturation(10);
            e.setCancelled(true);
            return;
        }
        if(!dp.getTeam().getDungeon().isHunger() || !dp.getTeam().isPlaying()) {
            p.setFoodLevel(20);
            p.setSaturation(1);
            e.setCancelled(true);
        }
    }
    
    //Cancel Firework damage
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if(entity instanceof ItemFrame && e.getDamager() instanceof Player && !this.builders.contains(e.getDamager())) {
            e.setCancelled(true);
        }
        if (e.getDamager() instanceof Firework && entity instanceof Player) {
            e.setCancelled(true);
            return;
        }
        if (entity instanceof LivingEntity && entity.hasMetadata("noknockback")) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> entity.setVelocity(new Vector(0,0,0)), 1l);
            return;
        }
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
      String[] lines = e.getLines();
      for (int i = 0; i < 4; i++) {
        String line = lines[i];
        line = ChatColor.translateAlternateColorCodes('&', line);
        e.setLine(i, line);
      } 
    }

    // Keep the anchor file in sync: a command sign written in a dungeon's template
    // world is registered, and a registered sign rewritten without the prefix is dropped.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnchorSignChange(SignChangeEvent e) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeonByWorld(e.getBlock().getWorld().getName());
        if(dungeon == null) {
            return;
        }
        Location location = e.getBlock().getLocation();
        if(AnchorStore.isCommandLine(e.getLine(0))) {
            if(dungeon.addSign(location)) {
                e.getPlayer().sendMessage(SevenDungeons.message("Anchor " + AnchorStore.format(location) + " registered for dungeon " + dungeon.getId() + "."));
            }
        }
        else if(dungeon.removeSign(location)) {
            e.getPlayer().sendMessage(SevenDungeons.message("Anchor " + AnchorStore.format(location) + " removed from dungeon " + dungeon.getId() + "."));
        }
    }

    // Keep the anchor file in sync: breaking a registered sign in the template world unregisters it.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnchorBreak(BlockBreakEvent e) {
        Dungeon dungeon = this.plugin.dungeonManager.getDungeonByWorld(e.getBlock().getWorld().getName());
        if(dungeon == null) {
            return;
        }
        Location location = e.getBlock().getLocation();
        if(dungeon.removeSign(location)) {
            e.getPlayer().sendMessage(SevenDungeons.message("Anchor " + AnchorStore.format(location) + " removed from dungeon " + dungeon.getId() + "."));
        }
    }
    
    //If arrow hit a minecart from minecart tour, cancel collision
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent e) {
        if(e.getHitEntity() != null && e.getHitEntity().hasMetadata("xmt")) {
            Location l = e.getHitEntity().getLocation();
            Location launchLocation = l.clone().add(e.getEntity().getLocation().clone().getDirection().normalize().multiply(0.5));
            l.getWorld().spawnArrow(launchLocation, e.getEntity().getLocation().getDirection(), (float) e.getEntity().getVelocity().length(), 0);
        }
    }
    
    // Cancel player damage except in dungeons.
    // Cancel deaths
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent e) {
        if(e.getEntityType().equals(EntityType.ENDER_CRYSTAL)) {
            e.setCancelled(true);
            if(e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
                e.getEntity().remove();
            }
            return;
        }
        if(e.getCause().equals(DamageCause.ENTITY_EXPLOSION) && e.getEntity().hasMetadata("noExplosion")) {
            e.setCancelled(true);
            return;
        }
        if(! (e.getEntity() instanceof Player)) {
            return;
        }
        if(!e.getEntity().getWorld().getName().matches("instance.*")){
            e.setCancelled(true);
            return;
        }
        Player player = (Player) e.getEntity();
        DungeonPlayer dp = this.plugin.teamManager.findDungeonPlayer(player);
        if(dp == null) {
            e.setCancelled(true);
            return;
        }
        if(dp.getTeam().getDungeon().isGodmode()) {
            e.setCancelled(true);
            return;
        }

        double finalDamage;

        if(e.getCause() == DamageCause.CUSTOM || e.getCause() == DamageCause.VOID || e.getCause() == DamageCause.LAVA){
            finalDamage = e.getDamage();
        }
        else{
            // DAMAGE WITH ARMOR
            double damage = e.getDamage() / 2;
            double armorPoints = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
            double armorToughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
            PotionEffect res = player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

            // DAMAGE WITH RESISTANCE
            int resistanceLevel = res == null ? 0 : res.getAmplifier();

            double withArmorReduction = damage * (1 - Math.min(20, Math.max(armorPoints / 5, armorPoints - damage / (2 + armorToughness / 4))) / 25);
            double withResistanceReduction = withArmorReduction * (1 - (resistanceLevel * 0.2));
            finalDamage = withResistanceReduction;
            if(armorPoints < 10){
                finalDamage += 4;
            }
            else if(armorPoints < 15){
                finalDamage += 2;
            }
        }

        if(player.getHealth() - finalDamage < 1){
            e.setCancelled(true);
            dp.getTeam().getGame().healPlayer(dp.getPlayer());
            dp.getTeam().getGame().death(dp);
        }
    }
    
    /* Player interact event :
     * - left-clicking a cauldron in Stardust
     * - Left-clicking a balloon in Balloon Burst
     * - Right-clicking flowerpot (disabling picking items in it)
     * - Right-clicking polished_diorite_stairs in light puzzle
     * - Right-clicking mushrooms in mushroom mayhem
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void Click(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if(e.getAction() == Action.LEFT_CLICK_BLOCK && block.getType() == Material.CAULDRON && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.LAVA_BUCKET)) {
            FixedMetadataValue xsd = new FixedMetadataValue(this.plugin, e.getPlayer().getName());
            block.setMetadata("xsd", xsd);
            return;
        }
        if(e.getAction() == Action.LEFT_CLICK_BLOCK && (block.getType().toString().contains("WOOL") || block.getType().equals(Material.TNT) || block.getType().equals(Material.SLIME_BLOCK))) {
            FixedMetadataValue xbb = new FixedMetadataValue(this.plugin, e.getPlayer().getName());
            block.setMetadata("xbb", xbb);
            return;
        }
        if(! (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (block.getWorld().getName().matches("(instance.*)|(lobby.*)") && (block.getType().name().startsWith("POTTED_") || block.getType() == Material.FLOWER_POT)
                && !this.builders.contains(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        if(block.hasMetadata("xlp")) {
            return;
        }
        if(block.getType() == Material.POLISHED_DIORITE_STAIRS || block.getType() == Material.POLISHED_DIORITE_SLAB) {
            block.setMetadata("xlp", xlp);
            return;
        }
        if(block.getType() == Material.CRIMSON_FUNGUS || block.getType() == Material.RED_MUSHROOM) {
            FixedMetadataValue xmm = new FixedMetadataValue(this.plugin, e.getPlayer().getName());
           block.setMetadata("xmm", xmm);
        }
        if(block.getType().toString().matches(".*CONCRETE")){
            FixedMetadataValue xgp = new FixedMetadataValue(this.plugin, e.getPlayer().getName());
            block.setMetadata("xgp", xgp);
        }
    }
    
    // NPCS clicked 
    @EventHandler(priority = EventPriority.NORMAL)
    public void NPCclicked(NPCRightClickEvent e)
    {
        Player p = e.getClicker();
        if(this.recentPlayers.contains(p)) {
            p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Dungeon " + ChatColor.RESET + "> You must wait 2 seconds before interacting with a NPC again.");
            return;
        }
        for(Game g : GameManager.games) {
            for(NPCSign ns : g.getNPCs()) {
                if(ns.getSevenNPC().getNPC() == e.getNPC()) {
                    this.recentPlayers.add(p);
                    this.untreatPlayer(p);
                    ns.getSevenNPC().handleClick(p);
                    return;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void bowShoot(EntityShootBowEvent e) {
    	if(!e.getEntity().getWorld().getName().matches("(instance.*)|(lobby.*)")) {
    		return;
    	}
    	if(e.getEntity().hasMetadata("xmt") && e.getEntity().isInsideVehicle()) {
    		Vector v = e.getEntity().getVehicle().getVelocity().clone();
    		if(v.getX() == 0 && v.getY() == 0 && v.getZ() == 0) {
    			return;
    		}
    		v.normalize().multiply(-0.25);
    		Location l = e.getProjectile().getLocation().clone().add(v);
    		Bukkit.getScheduler().runTaskLater(plugin, () -> e.getProjectile().teleport(l), 1);
    	}
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void noSlimeSplit(SlimeSplitEvent e){
        e.setCancelled(true);
    }
    
    // Dungeons async events that take place every 2 ticks.
    public void asyncTimer2ticks() {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(int i = 0; i < GameManager.games.size(); i++) {
                    Game game = GameManager.games.get(i);
                    for(AnimatedSign as : game.getAsyncSigns2ticks())
                    {
                        as.animate(game.getPlayers());
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 0, 2);
    }
    
    // Dungeons events that take place every 2 ticks. (Player movement, redstone)
    public void timer2ticks() {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(int i = 0; i < GameManager.games.size(); i++) {
                    Game game = GameManager.games.get(i);
                    ArrayList<CaptorSign> cs = game.getSigns2ticks();
                    for(int j = 0; j < cs.size(); j++) cs.get(j).isTriggered();
                }
            }
        }.runTaskTimer(this.plugin, 0, 2);
    }
    
    // Dungeons events that take place every 5 ticks (Mobs and delays)
    public void timer5ticks() {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(int i = 0; i < GameManager.games.size(); i++) {
                    Game game = GameManager.games.get(i);
                    ArrayList<CaptorSign> signs5ticks = game.getSigns5ticks();
                    for(int j = 0; j < signs5ticks.size(); j++) {
                        signs5ticks.get(j).isTriggered();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0, 5);
    
    }
    
    public void timer20ticks() {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(int i = 0; i < GameManager.games.size(); i++) {
                    Game game = GameManager.games.get(i);
                    //Game time Management
                    if(game.getTeam().isPlaying() && game.getGameScore().countdown()) {
                        game.endGame();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0, 20);
    }
    
    /*
     * Removes the player from this object
     */
    public void untreatPlayer(Player p) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                recentPlayers.remove(p);
            }
        }.runTaskLater(this.plugin, 40);
    }
    
    public ArrayList<Player> getBuilders(){
        return this.builders;
    }
    
}
