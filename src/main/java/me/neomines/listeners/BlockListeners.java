package me.neomines.listeners;

import com.sk89q.worldedit.math.BlockVector3;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (MineManager.getInstance() == null || MineManager.getInstance().tasksStopped()) {
            return;
        }

        Location blockLocation = event.getBlock().getLocation();
        if (blockLocation.getWorld() == null) return;

        for (CuboidNeoMine mine : MineManager.getInstance().getMines()) {
            if (mine.isStopped() || mine.getRegion() == null || mine.getWorld() == null) {
                continue;
            }
            if (blockLocation.getWorld().getName().equals(mine.getWorld())
                    && mine.getRegion().contains(BlockVector3.at(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()))) {
                mine.handleBlockBreak(event);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (MineManager.getInstance() == null || MineManager.getInstance().tasksStopped()) {
            return;
        }

        Location blockLocation = event.getBlock().getLocation();
        if (blockLocation.getWorld() == null) return;

        for (CuboidNeoMine mine : MineManager.getInstance().getMines()) {
            if (mine.isStopped() || mine.getRegion() == null || mine.getWorld() == null) {
                continue;
            }
            if (blockLocation.getWorld().getName().equals(mine.getWorld())
                    && mine.getRegion().contains(BlockVector3.at(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()))) {
                mine.setBlockCount(mine.getBlockCount() + 1);
                break;
            }
        }
    }
}
