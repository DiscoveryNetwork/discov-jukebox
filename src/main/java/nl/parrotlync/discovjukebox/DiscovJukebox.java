package nl.parrotlync.discovjukebox;

import nl.parrotlync.discovjukebox.command.FunCommandExecutor;
import nl.parrotlync.discovjukebox.command.MusicCommandExecutor;
import nl.parrotlync.discovjukebox.listener.PlayerListener;
import nl.parrotlync.discovjukebox.manager.PlayerManager;
import nl.parrotlync.discovjukebox.manager.ShowManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DiscovJukebox extends JavaPlugin {
    private static DiscovJukebox instance;
    private PlayerManager playerManager;
    private ShowManager showManager;

    public DiscovJukebox() {
        instance = this;
        this.playerManager = new PlayerManager();
        this.showManager = new ShowManager();
    }

    @Override
    public void onEnable() {
        getConfig().addDefault("startup-sound", "https://drive.ipictserver.nl/scotchcraft/fun/CHIME.wav");
        getConfig().addDefault("prefix", "&8[&3Music&8]");
        getConfig().options().copyDefaults(true);
        saveConfig();
        showManager.load();
        this.getCommand("fun").setExecutor(new FunCommandExecutor());
        this.getCommand("music").setExecutor(new MusicCommandExecutor());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getLogger().info("DiscovJukebox is now enabled!");
    }

    @Override
    public void onDisable() {
        showManager.save();
        getLogger().info("DiscovJukebox is now disabled!");
    }

    public static DiscovJukebox getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() { return playerManager; }

    public ShowManager getShowManager() { return showManager; }
}
