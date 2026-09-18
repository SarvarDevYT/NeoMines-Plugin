package me.neomines.listeners;

import me.neomines.NeoMines;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("neomines.*")
                || !NeoMines.getInstance().updateAvailable
                || !NeoMines.getInstance().getConfig().getBoolean("announceUpdate", false)) {
            return;
        }
        player.sendMessage(NeoMines.PREFIX + "An update for NeoMines is available: §a" + NeoMines.getInstance().availableVersion);
    }
}
