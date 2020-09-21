package nl.parrotlync.discovjukebox.manager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public List<Player> getPlayers() {
        return players;
    }
}

