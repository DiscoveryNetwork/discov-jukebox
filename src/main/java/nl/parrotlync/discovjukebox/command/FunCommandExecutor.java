package nl.parrotlync.discovjukebox.command;

import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import nl.parrotlync.discovjukebox.DiscovJukebox;
import nl.parrotlync.discovjukebox.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FunCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            toggle(player);
            return true;
        }

        if (sender.hasPermission("discovjukebox.fun")) {
            if (args[0].equalsIgnoreCase("music") || args[0].equalsIgnoreCase("sound")) {
                if (args.length == 2) {
                    ResourceType type = args[0].equalsIgnoreCase("music") ? ResourceType.MUSIC : ResourceType.SOUND_EFFECT;
                    Media media = new Media(type, args[1]);
                    media.setLooping(false);
                    JukeboxAPI.getShowManager().getShow("fun").play(media);
                } else {
                    ChatUtil.sendMessage(sender, "§cPlease specify an URL!", true);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("connected")) {
                if (DiscovJukebox.getInstance().getPlayerManager().getPlayers().isEmpty()) {
                    ChatUtil.sendMessage(sender, "§7There are currently no players connected with Fun Connect.", true);
                } else {
                    StringBuilder players = new StringBuilder();
                    for (Player player : DiscovJukebox.getInstance().getPlayerManager().getPlayers()) {
                        players.append(player.getName()).append(", ");
                    }
                    ChatUtil.sendMessage(sender, "§7Connected players: §a" + players, true);
                }
            }

            if (args[0].equalsIgnoreCase("stop")) {
                JukeboxAPI.getShowManager().getShow("fun").stopAll();
                ChatUtil.sendMessage(sender, "§7You stopped all active Fun Connect playback.", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("close")) {
                JukeboxAPI.getShowManager().getShow("fun").stopAll();
                for (Player player : DiscovJukebox.getInstance().getPlayerManager().getPlayers()) {
                    JukeboxAPI.getShowManager().getShow("fun").removeMember(player);
                    ChatUtil.sendMessage(player, "§7All Fun Connections have been §cclosed §7by a staff member!", true);
                }
                DiscovJukebox.getInstance().getPlayerManager().getPlayers().clear();
                return true;
            }
        }
        return help(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("help");
            suggestions.add("connected");
            suggestions.add("music");
            suggestions.add("sound");
            suggestions.add("stop");
            suggestions.add("close");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return suggestions;
    }

    private void toggle(Player player) {
        if (DiscovJukebox.getInstance().getPlayerManager().hasPlayer(player)) {
            DiscovJukebox.getInstance().getPlayerManager().removePlayer(player);
            JukeboxAPI.getShowManager().getShow("fun").removeMember(player);
            ChatUtil.sendMessage(player, "§7Your Fun Connect is now §cdisabled§7!", true);
        } else {
            DiscovJukebox.getInstance().getPlayerManager().addPlayer(player);
            JukeboxAPI.getShowManager().getShow("fun").addMember(player, false);
            ChatUtil.sendMessage(player, "§7Your Fun Connect is now §aenabled§7!", true);
        }
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("discovjukebox.fun")) {
            ChatUtil.sendMessage(sender, "§f+---+ §9DiscovJukebox - Fun §f+---+", false);
            ChatUtil.sendMessage(sender, "§3/fun connected §7View all connected players", false);
            ChatUtil.sendMessage(sender, "§3/fun music <url> §7Play some music", false);
            ChatUtil.sendMessage(sender, "§3/fun sound <url> §7Play a sound", false);
            ChatUtil.sendMessage(sender, "§3/fun stop §7Stop all music and sounds that are playing", false);
            ChatUtil.sendMessage(sender, "§3/fun close §7Stop and close all connection with Fun Connect", false);
        } else {
            ChatUtil.sendMessage(sender, "§7use §a/fun §7to toggle your Fun Connect!", true);
        }
        return true;
    }
}
