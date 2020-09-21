package nl.parrotlync.discovjukebox.command;

import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import nl.parrotlync.discovjukebox.DiscovJukebox;
import nl.parrotlync.discovjukebox.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("discovjukebox.music")) {
            if (args.length == 0) {
                ChatUtil.sendMessage(sender, "§7Music enabled: §6" + DiscovJukebox.getInstance().getShowManager().isEnabled(), true);
                return true;
            }

            if (args[0].equalsIgnoreCase("enable")) {
                DiscovJukebox.getInstance().getShowManager().enable();
                ChatUtil.sendMessage(sender, "§7Music is now §aenabled§7.", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("disable")) {
                DiscovJukebox.getInstance().getShowManager().disable();
                ChatUtil.sendMessage(sender, "§7Music is now §cdisabled§7.", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("add") && args.length == 3) {
                DiscovJukebox.getInstance().getShowManager().addShow(args[1], args[2]);
                Media media = new Media(ResourceType.MUSIC, args[2]);
                JukeboxAPI.getShowManager().getShow(args[1]).play(media);
                ChatUtil.sendMessage(sender, "§aYou updated a show!", true);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
                if (DiscovJukebox.getInstance().getShowManager().removeShow(args[1])) {
                    JukeboxAPI.getShowManager().getShow(args[1]).stopAll();
                    ChatUtil.sendMessage(sender, "§cYou removed a show!", true);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                HashMap<String, String> shows = DiscovJukebox.getInstance().getShowManager().getShows();
                List<String> keys = new ArrayList<>(shows.keySet());
                int numberOfPages = (int) Math.ceil((shows.size() / 5) + 1);

                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                        if (page > numberOfPages) { return false; }
                    } catch (Exception e) {
                        return false;
                    }
                }

                ChatUtil.sendMessage(sender, "§f+---+ §9DiscovJukebox - Show list (page " + page + ") §f+---+", false);
                for (int i = (page - 1) * 5; i < page * 5 && i < shows.size(); i++) {
                    ChatUtil.sendMessage(sender, "§6" + keys.get(i) + "§7: " + shows.get(keys.get(i)), false);
                }
                if (page < numberOfPages) {
                    ChatUtil.sendMessage(sender, "§3Type '/music list " + (page + 1) + "' to see more.", false);
                }
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
            suggestions.add("enable");
            suggestions.add("disable");
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("list");
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                HashMap<String, Show> shows = JukeboxAPI.getShowManager().getShows();
                List<String> keys = new ArrayList<>(shows.keySet());
                for (String key : keys) {
                    suggestions.add("@" + key);
                }
                return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
            }

            if (args[0].equalsIgnoreCase("remove")) {
                HashMap<String, String> shows = DiscovJukebox.getInstance().getShowManager().getShows();
                List<String> keys = new ArrayList<>(shows.keySet());
                suggestions.addAll(keys);
                return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
            }
        }

        return suggestions;
    }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("discovjukebox.music")) {
            ChatUtil.sendMessage(sender, "§f+---+ §9DiscovJukebox - Music §f+---+", false);
            ChatUtil.sendMessage(sender, "§3/music §7View the music status", false);
            ChatUtil.sendMessage(sender, "§3/music <enable/disable> §7Enable or disable the music", false);
            ChatUtil.sendMessage(sender, "§3/music add <@show> <url> §7Add a show", false);
            ChatUtil.sendMessage(sender, "§3/music remove <@show> §7Remove a show", false);
            ChatUtil.sendMessage(sender, "§3/music list §7List all registered shows", false);
        } else {
            ChatUtil.sendMessage(sender, "§cYou don't have permission to do that!", true);
        }
        return true;
    }
}
