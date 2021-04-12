package nl.parrotlync.discovjukebox.listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.events.ClientConnectEvent;
import nl.parrotlync.discovjukebox.DiscovJukebox;
import nl.parrotlync.discovjukebox.util.ChatUtil;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class JukeboxListener implements Listener {
    private final HashMap<UUID, Date> cooldownMap = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (DiscovJukebox.getInstance().getPlayerManager().hasPlayer(player)) {
            DiscovJukebox.getInstance().getPlayerManager().removePlayer(player);
            JukeboxAPI.getShowManager().getShow("fun").removeMember(player);
        }
    }

    @EventHandler
    public void onClientConnect(ClientConnectEvent event) {
        Player player = Bukkit.getPlayer(event.getUsername());
        if (player != null) {
            Media media = new Media(ResourceType.SOUND_EFFECT, DiscovJukebox.getInstance().getConfig().getString("startup-sound"));
            JukeboxAPI.play(player, media);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (Objects.requireNonNull(event.getLine(0)).equalsIgnoreCase("[showsync]")) {
            if (event.getPlayer().hasPermission("discovjukebox.signs")) {
                event.setLine(0, "[§9ShowSync§0]");
                event.setLine(2, "");
                event.setLine(3, "§oClick to sync!");
                ChatUtil.sendMessage(event.getPlayer(), "§aSign registered.", true);
            } else {
                ChatUtil.sendMessage(event.getPlayer(), "§cYou don't have permission to do that.", true);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            if (sign.getLine(0).equals("[§9ShowSync§0]")) {
                if (event.getPlayer().hasPermission("discovjukebox.signs")) {
                    ChatUtil.sendMessage(event.getPlayer(), "§cSign unregistered.", true);
                } else {
                    ChatUtil.sendMessage(event.getPlayer(), "§cYou don't have permission to do that.", true);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (Objects.requireNonNull(event.getClickedBlock()).getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (sign.getLine(0).equals("[§9ShowSync§0]")) {
                    WorldGuardPlugin plugin = (WorldGuardPlugin) DiscovJukebox.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
                    assert plugin != null;
                    RegionManager manager = plugin.getRegionManager(sign.getWorld());
                    if (manager != null) {
                        ApplicableRegionSet set = manager.getApplicableRegions(sign.getLocation());
                        int highestPriority = -1;
                        String highestRegion = null;
                        for (ProtectedRegion region : set) {
                            if (region.getPriority() > highestPriority) {
                                highestPriority = region.getPriority();
                                highestRegion = region.getId();
                            }
                        }

                        if (highestRegion != null && MCJukebox.getInstance().getRegionManager().hasRegion(highestRegion)) {
                            String show = MCJukebox.getInstance().getRegionManager().getURL(highestRegion);
                            if (JukeboxAPI.getShowManager().showExists(show)) {
                                Date now = new Date();
                                if (cooldownMap.get(event.getPlayer().getUniqueId()) == null || now.compareTo(cooldownMap.get(event.getPlayer().getUniqueId())) > 0) {
                                    cooldownMap.put(event.getPlayer().getUniqueId(), DateUtils.addSeconds(new Date(), 10));
                                    ChatUtil.sendMessage(event.getPlayer(), "§eRefreshing show music...", false);
                                    JukeboxAPI.getShowManager().getShow(show).removeMember(event.getPlayer());
                                    JukeboxAPI.getShowManager().getShow(show).addMember(event.getPlayer(), false);
                                } else {
                                    long secondsLeft = (cooldownMap.get(event.getPlayer().getUniqueId()).getTime() - now.getTime()) / 1000;
                                    ChatUtil.sendMessage(event.getPlayer(), "§cPlease wait " + secondsLeft + "s before using this again!", true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
