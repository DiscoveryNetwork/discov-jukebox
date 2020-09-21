package nl.parrotlync.discovjukebox.listener;

import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.events.ClientConnectEvent;
import nl.parrotlync.discovjukebox.DiscovJukebox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

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
        Media media = new Media(ResourceType.SOUND_EFFECT, DiscovJukebox.getInstance().getConfig().getString("startup-sound"));
        JukeboxAPI.play(player, media);
    }
}
