package me.neomines.utils.configuration;

import me.neomines.NeoMines;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

public class CustomConfigFile extends YamlConfiguration {

    private final File file;

    public CustomConfigFile(NeoMines plugin, File file, String resourceName) {
        this.file = file;

        try {
            if (!this.file.exists()) {
                if (this.file.getParentFile() != null) {
                    this.file.getParentFile().mkdirs();
                }
                this.file.createNewFile();
            }

            this.load(this.file);

            Map<String, Object> mappedConfig = this.getValues(true);
            try (InputStream in = plugin.getResource(resourceName)) {
                if (in != null) {
                    Files.copy(in, this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Could not copy default resource " + resourceName + ": " + e.getMessage());
            }

            this.load(this.file);

            for (Map.Entry<String, Object> entry : mappedConfig.entrySet()) {
                this.set(entry.getKey(), entry.getValue());
            }

            try (InputStream defaultIn = plugin.getResource(resourceName)) {
                if (defaultIn != null) {
                    Map<String, Object> defaultMap = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultIn, StandardCharsets.UTF_8)).getValues(true);
                    for (Map.Entry<String, Object> entry : defaultMap.entrySet()) {
                        if (!this.contains(entry.getKey())) {
                            this.set(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

            saveConfig();

        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Error handling custom config file " + file.getName() + ": " + e.getMessage());
        }
    }

    public CustomConfigFile(NeoMines plugin, File file, String resourceName, StandardCopyOption copyOption) {
        this.file = file;

        try {
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }

            InputStream is = plugin.getResource(resourceName);
            if (is != null) {
                load(new InputStreamReader(is, StandardCharsets.UTF_8));
            }

            saveConfig();

        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Error loading resource " + resourceName + ": " + e.getMessage());
        }
    }

    public void saveConfig() {
        try {
            save(file);
        } catch (IOException e) {
            NeoMines.getInstance().getLogger().warning("Could not save config " + file.getName() + ": " + e.getMessage());
        }
    }

    public void reloadConfig() {
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            NeoMines.getInstance().getLogger().warning("Could not reload config " + file.getName() + ": " + e.getMessage());
        }
    }

    public File getFile() {
        return file;
    }
}
