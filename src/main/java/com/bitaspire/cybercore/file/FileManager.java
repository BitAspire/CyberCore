package com.bitaspire.cybercore.file;

import com.bitaspire.cybercore.CyberCore;
import me.croabeast.file.ConfigurableFile;
import me.croabeast.takion.logger.LogLevel;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileManager {

    private final Map<String, ConfigurableFile> files = new HashMap<>();

    private final CyberCore core;

    public FileManager(CyberCore core) {
        this.core = core;
    }

    public void load(String name, boolean send) {
        ConfigurableFile file;
        try {
            (file = new ConfigurableFile(core.getPlugin(), name) {
                @Override
                public boolean isUpdatable() {
                    return FileManager.this.get("config").get("config.auto-update." + name, false);
                }
            }).saveDefaults();
            files.put(name, file);
            if (send)
                core.getLibrary().getServerLogger().log("&7Loaded file &e" + name + ".yml&7 file.");
        } catch (Exception e) {
            if (send)
                core.getLibrary().getServerLogger().log(LogLevel.ERROR,
                        "&cError while generating " + name + ".yml file",
                        e.getLocalizedMessage()
                );
        }
    }

    public void load(boolean send, boolean useDefaults, String... additions) {
        if (!files.isEmpty()) files.clear();

        if (send)
            core.getLibrary().getServerLogger().log(core.getSettings().getBootColor() + "Loading YAML files...");

        long startTime = System.currentTimeMillis();

        if (useDefaults) {
            load("config", send);
            load("lang", send);
        }

        for (String string : additions) load(string, send);

        if (send)
            core.getLibrary().getServerLogger().log(
                    "&7Loaded &e" + files.size() + "&7 files in &a" +
                            (System.currentTimeMillis() - startTime) + "ms&7.", "&7"
            );
    }

    public void load(String... additions) {
        load(true, true, additions);
    }

    @NotNull
    public ConfigurableFile get(String name) {
        return Objects.requireNonNull(files.get(name), "Name " + name + " wasn't found");
    }

    @NotNull
    public FileConfiguration getConfig(String name) {
        return get(name).getConfiguration();
    }
}
