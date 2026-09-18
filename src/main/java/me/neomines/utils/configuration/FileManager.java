package me.neomines.utils.configuration;

import me.neomines.NeoMines;
import me.neomines.utils.Utils;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    private final NeoMines plugin;
    private final File dataFolder;
    private FileConfig langCfg;
    private CustomConfigFile propertiesCfg;

    public FileManager(NeoMines plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        setupFiles();
    }

    public String getDefaultString(String str) {
        if (propertiesCfg == null || !propertiesCfg.contains(str)) {
            return "Could not find " + str;
        }
        return propertiesCfg.getString(str);
    }

    public String getLangString(String str) {
        if (langCfg == null || !langCfg.contains(str)) {
            return "Error loading: " + str + ", " + plugin.getConfig().getString("language");
        }
        return Utils.color(langCfg.getString(str));
    }

    public List<Integer> getDefaultIntegers(String str) {
        if (propertiesCfg == null || !propertiesCfg.contains(str)) {
            return Arrays.asList(600, 300, 60, 20, 5);
        }
        return propertiesCfg.getIntegerList(str);
    }

    public List<String> getLangStringList(String str) {
        if (langCfg == null || !langCfg.contains(str)) {
            return Arrays.asList("Could not load ", str, "language: " + plugin.getConfig().getString("language"));
        }
        List<String> translatedList = new ArrayList<>();
        langCfg.getStringList(str).forEach(s -> translatedList.add(Utils.color(s)));
        return translatedList;
    }

    public void setupFiles() {
        setupConfig();
        setupMinesFolder();
        setupCustomFiles();
        setupLanguageFiles();
    }

    public void setupConfig() {
        plugin.getLogger().info("Loading config file");
        new CustomConfigFile(plugin, new File(dataFolder, "config.yml"), "config.yml");
        plugin.reloadConfig();

        String prefix = plugin.getConfig().getString("prefix", "&6[&bNeo&aMines&6] &7");
        NeoMines.PREFIX = Utils.color(prefix);
    }

    public void setupMinesFolder() {
        File minesDirectory = new File(plugin.getDataFolder() + "/mines");
        if (!minesDirectory.exists() && !minesDirectory.mkdirs()) {
            plugin.getLogger().severe("Could not create mines folder, plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void setupCustomFiles() {
        this.propertiesCfg = new CustomConfigFile(plugin, new File(dataFolder, "default_properties.yml"), "default_properties.yml");
        this.propertiesCfg.saveConfig();
    }

    public void setupLanguageFiles() {
        new CustomConfigFile(plugin, new File(dataFolder + "/languages", "messages_uz.yml"), "languages/messages_uz.yml", StandardCopyOption.REPLACE_EXISTING);
        new CustomConfigFile(plugin, new File(dataFolder + "/languages", "messages_en.yml"), "languages/messages_en.yml", StandardCopyOption.REPLACE_EXISTING);
        new CustomConfigFile(plugin, new File(dataFolder + "/languages", "messages_es.yml"), "languages/messages_es.yml", StandardCopyOption.REPLACE_EXISTING);
        new CustomConfigFile(plugin, new File(dataFolder + "/languages", "messages_cn.yml"), "languages/messages_cn.yml", StandardCopyOption.REPLACE_EXISTING);
        new CustomConfigFile(plugin, new File(dataFolder + "/languages", "messages_custom.yml"), "languages/messages_custom.yml");

        String langName = plugin.getConfig().getString("language", "UZ").toLowerCase();
        File langFile = new File(dataFolder + "/languages", "messages_" + langName + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Tanlangan til topilmadi: " + langName + ", UZ tiliga o'tkazilmoqda");
            langCfg = new FileConfig(new File(dataFolder + "/languages", "messages_uz.yml"));
            return;
        }
        langCfg = new FileConfig(langFile);
    }
}
