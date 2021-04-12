package nl.parrotlync.discovjukebox.util;

import nl.parrotlync.discovjukebox.DiscovJukebox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

    public static void sendMessage(CommandSender sender, String msg, boolean withPrefix) {
        if (withPrefix) {
            String prefix = DiscovJukebox.getInstance().getConfig().getString("prefix");
            assert prefix != null;
            msg = ChatColor.translateAlternateColorCodes('&', prefix) + " " + msg;
        }
        sender.sendMessage(msg);
    }
}
