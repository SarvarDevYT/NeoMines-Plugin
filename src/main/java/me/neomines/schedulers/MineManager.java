package me.neomines.schedulers;

import me.neomines.NeoMines;
import me.neomines.mine.AbstractNeoMine;
import me.neomines.mine.CuboidNeoMine;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MineManager extends BukkitRunnable {

    private static MineManager INSTANCE;
    private boolean stopTasks;
    private List<CuboidNeoMine> mines;

    public MineManager() {
        INSTANCE = this;
        mines = loadMinesFromFiles();
        this.runTaskTimer(NeoMines.getInstance(), 0L, 20L);
    }

    public static MineManager getInstance() {
        return INSTANCE;
    }

    public static boolean mineExists(String name) {
        if (getInstance() == null) return false;
        return getInstance().getMineListNames().contains(name);
    }

    @Override
    public void run() {
        if (stopTasks) {
            return;
        }

        for (AbstractNeoMine mine : mines) {
            try {
                mine.run();
            } catch (Exception e) {
                NeoMines.getInstance().getLogger().warning("Error ticking mine " + mine.getName() + ": " + e.getMessage());
            }
        }
    }

    public boolean tasksStopped() {
        return stopTasks;
    }

    public void setStopTasks(boolean stopTasks) {
        this.stopTasks = stopTasks;
    }

    public List<CuboidNeoMine> getMines() {
        return mines;
    }

    public CuboidNeoMine getMine(String name) {
        for (CuboidNeoMine mine : mines) {
            if (mine.getName().equalsIgnoreCase(name)) {
                return mine;
            }
        }
        return null;
    }

    public List<String> getMineListNames() {
        List<String> mineNames = new ArrayList<>();
        if (mines != null) {
            mines.forEach(cuboidNeoMine -> mineNames.add(cuboidNeoMine.getName()));
        }
        return mineNames;
    }

    public List<CuboidNeoMine> loadMinesFromFiles() {
        NeoMines.getInstance().getLogger().info("Loading mines from files...");
        List<CuboidNeoMine> loadedMines = new ArrayList<>();

        File file = new File(NeoMines.getInstance().getDataFolder() + "/mines");
        if (!file.exists()) {
            file.mkdirs();
            return loadedMines;
        }

        File[] files = file.listFiles(File::isFile);
        if (files == null) {
            return loadedMines;
        }

        for (File value : files) {
            if (!value.getName().endsWith(".yml")) continue;
            try {
                YamlConfiguration configurationFile = YamlConfiguration.loadConfiguration(value);
                if (configurationFile.contains("Mine") && configurationFile.get("Mine") != null) {
                    Object obj = configurationFile.get("Mine");
                    if (obj instanceof CuboidNeoMine) {
                        loadedMines.add((CuboidNeoMine) obj);
                    }
                }
            } catch (Exception e) {
                NeoMines.getInstance().getLogger().severe("Could not load mine file " + value.getName() + ": " + e.getMessage());
            }
        }

        return loadedMines;
    }

    public void reloadMines() {
        this.mines = loadMinesFromFiles();
    }
}
