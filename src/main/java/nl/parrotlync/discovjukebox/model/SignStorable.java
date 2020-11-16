package nl.parrotlync.discovjukebox.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.io.Serializable;

public class SignStorable implements Serializable {
    private Integer x;
    private Integer y;
    private Integer z;
    private String world;
    private String show;

    public SignStorable(Sign sign, String show) {
        Location location = sign.getLocation();
        this.x = (int) location.getX();
        this.y = (int) location.getY();
        this.z = (int) location.getZ();
        this.world = location.getWorld().getName();
        this.show = show;
    }

    public Sign getSign() {
        World world = Bukkit.getWorld(this.world);
        if (world.getBlockAt(x, y, z).getState() instanceof Sign) {
            return (Sign) world.getBlockAt(x, y, z).getState();
        }
        return null;
    }

    public String getShow() {
        return show;
    }
}
