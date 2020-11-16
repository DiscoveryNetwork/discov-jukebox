package nl.parrotlync.discovjukebox.manager;

import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import nl.parrotlync.discovjukebox.DiscovJukebox;
import nl.parrotlync.discovjukebox.util.DataUtil;

import java.util.HashMap;

public class ShowManager {
    private HashMap<String, String> shows;
    private String path = "plugins/DiscovJukebox/shows.data";
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        enabled = true;
        for (String show : shows.keySet()) {
            Media media = new Media(ResourceType.MUSIC, shows.get(show));
            JukeboxAPI.getShowManager().getShow(show).play(media);
        }
    }

    public void disable() {
        enabled = false;
        for (String show : shows.keySet()) {
            JukeboxAPI.getShowManager().getShow(show).stopAll();
        }
    }

    public void addShow(String show, String url) {
        shows.put(show, url);
    }

    public boolean removeShow(String show) {
        if (shows.containsKey(show)) {
            shows.remove(show);
            return true;
        }
        return false;
    }

    public HashMap<String, String> getShows() {
        return shows;
    }

    public void load() {
        shows = DataUtil.loadObjectFromPath(path);
        if (shows != null) {
            DiscovJukebox.getInstance().getLogger().info("Loaded " + shows.size() + " shows from file.");
        } else {
            shows = new HashMap<>();
        }
        enable();
    }

    public void save() {
        if (shows.size() != 0) {
            DataUtil.saveObjectToPath(shows, path);
            DiscovJukebox.getInstance().getLogger().info("Saved " + shows.size() + " shows to file.");
        }
        disable();
    }
}
