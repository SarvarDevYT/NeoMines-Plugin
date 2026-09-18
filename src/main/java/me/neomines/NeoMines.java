package me.neomines;

import me.neomines.commands.NeoMinesHelpCommand;
import me.neomines.commands.commandhandler.CommandHandler;
import me.neomines.commands.commandhandler.FlagCommandsHandler;
import me.neomines.commands.subcommands.*;
import me.neomines.gui.MenuListener;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.listeners.BlockListeners;
import me.neomines.listeners.PlayerJoinListener;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.placeholders.NeoMinePlaceHolders;
import me.neomines.schedulers.MineManager;
import me.neomines.tabcompleters.NeoMinesTabCompleter;
import me.neomines.utils.UpdateChecker;
import me.neomines.utils.configuration.FileManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class NeoMines extends JavaPlugin {

    public static String PREFIX = "§6[§bNeo§aMines§6] §7";
    private static NeoMines plugin;
    private static HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    public boolean updateAvailable = false;
    public String availableVersion = "";
    public boolean placeholderAPI = false;
    private FileManager fileManager;

    public static NeoMines getInstance() {
        return plugin;
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        if (playerMenuUtilityMap.containsKey(player)) {
            return playerMenuUtilityMap.get(player);
        }
        PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
        playerMenuUtilityMap.put(player, playerMenuUtility);
        return playerMenuUtility;
    }

    public static void removePlayerMenuUtility(Player player) {
        playerMenuUtilityMap.remove(player);
    }

    public static HashMap<Player, PlayerMenuUtility> getPlayerMenuUtilityMap() {
        return playerMenuUtilityMap;
    }

    @Override
    public void onEnable() {
        plugin = this;
        ConfigurationSerialization.registerClass(CuboidNeoMine.class);
        ConfigurationSerialization.registerClass(NeoMineBlock.class);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.fileManager = new FileManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            new MineManager();
            registerCommands();
            registerListeners();
        }, 1L);

        if (getConfig().getBoolean("announceUpdate", true)) {
            new UpdateChecker(getInstance(), 96457).getVersion(version -> {
                if (!getInstance().getPluginVersion().equalsIgnoreCase(version)) {
                    updateAvailable = true;
                    availableVersion = version;
                    getLogger().info("There is a new version of NeoMines available: " + version);
                }
            });
        }

        try {
            int pluginId = 12889;
            new Metrics(this, pluginId);
        } catch (Throwable ignored) {}

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NeoMinePlaceHolders(this).register();
            placeholderAPI = true;
            getLogger().info("PlaceholderAPI found! Registered %neomines_...% placeholders.");
        }
    }

    @Override
    public void onDisable() {
        if (MineManager.getInstance() != null && MineManager.getInstance().getMines() != null) {
            MineManager.getInstance().getMines().forEach(CuboidNeoMine::save);
        }

        plugin = null;
        if (playerMenuUtilityMap != null) {
            playerMenuUtilityMap.clear();
        }
        playerMenuUtilityMap = null;
        fileManager = null;
    }

    private void registerCommands() {
        getLogger().info("Starting mines and registering commands. Running version " + getPluginVersion());

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.register("help", new NeoMinesHelpCommand());
        commandHandler.register("create", new CreateCommand());
        commandHandler.register("delete", new DeleteCommand());
        commandHandler.register("redefine", new RedefineCommand());
        commandHandler.register("info", new InfoCommand());
        commandHandler.register("list", new ListCommand());
        commandHandler.register("resetmode", new ResetModeCommand());
        commandHandler.register("setdelay", new SetDelayCommand());
        commandHandler.register("resetpercentage", new ResetPercentageCommand());
        commandHandler.register("set", new SetCommand());
        commandHandler.register("unset", new UnsetCommand());
        commandHandler.register("reset", new ResetCommand());
        commandHandler.register("flag", new FlagCommandsHandler());
        commandHandler.register("start", new StartCommand());
        commandHandler.register("stop", new StopCommand());
        commandHandler.register("starttasks", new StartTasksCommand());
        commandHandler.register("stoptasks", new StopTasksCommand());
        commandHandler.register("tp", new TeleportCommand());
        commandHandler.register("settp", new SetTeleportCommand());
        commandHandler.register("setresettp", new SetResetTeleportCommand());
        commandHandler.register("reload", new ReloadCommand());
        commandHandler.register("sync", new SyncCommand());
        commandHandler.register("gui", new GuiCommand());

        if (getCommand("neomines") != null) {
            getCommand("neomines").setExecutor(commandHandler);
            getCommand("neomines").setTabCompleter(new NeoMinesTabCompleter());
        }
    }

    private void registerListeners() {
        getLogger().info("Registering listeners");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListeners(), this);
        pm.registerEvents(new MenuListener(), this);
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public String getDefaultString(String str) {
        return fileManager.getDefaultString(str);
    }

    public String getLangString(String str) {
        return fileManager.getLangString(str);
    }

    public List<String> getLangStringList(String str) {
        return fileManager.getLangStringList(str);
    }

    public void reloadLanguages() {
        reloadConfig();
        fileManager.setupLanguageFiles();
    }

    @SuppressWarnings("deprecation")
    public String getPluginVersion() {
        try {
            return getPluginMeta().getVersion();
        } catch (Throwable ignored) {
            return getDescription().getVersion();
        }
    }
}
