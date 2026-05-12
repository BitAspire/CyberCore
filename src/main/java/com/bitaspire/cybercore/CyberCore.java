package com.bitaspire.cybercore;

import com.bitaspire.cybercore.file.FileManager;
import lombok.AccessLevel;
import lombok.Getter;
import me.croabeast.takion.TakionLib;
import me.croabeast.vnc.VNC;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BooleanSupplier;

@Getter
public final class CyberCore {

    private final JavaPlugin plugin;
    private final TextLibrary library;

    private final FileManager fileManager;
    private final CoreSettings settings;

    @Getter(AccessLevel.NONE)
    private final long bootStart;

    public CyberCore(JavaPlugin plugin) {
        this.plugin = plugin;

        library = new TextLibrary(this);
        fileManager = new FileManager(this);
        settings = new CoreSettings(this);

        bootStart = System.currentTimeMillis();
    }

    private void loadTPS() {
        library.getScheduler().runTaskTimer(Lag.initialize(), 100L, 1L);
    }

    public void loadFiles(boolean header, String... additionalFiles) {
        if (header) settings.sendBootHeader();
        fileManager.load(additionalFiles);
    }

    public void setColoredSupplier(BooleanSupplier supplier) {
        library.load(supplier);
    }

    public void loadStart(boolean loadFiles, String... additionalFiles) {
        settings.sendBootHeader();
        if (loadFiles)
            loadFiles(false, additionalFiles);
        setColoredSupplier(null);
        loadTPS();
    }

    public void loadStart(String... additionalFiles) {
        loadStart(true, additionalFiles);
    }

    public void loadFinish() {
        final PluginDescriptionFile desc = plugin.getDescription();

        library.getServerLogger().log(
                "&7Loaded " + settings.getBootColor() + desc.getPrefix() +
                        " v" + desc.getVersion() + "&7 in &a" +
                        (System.currentTimeMillis() - bootStart) + "ms&7."
        );
        library.getServerLogger().log(settings.getBootBar());
    }

    @NotNull
    public TakionLib getLibrary() {
        return library;
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    @Deprecated
    public void logger(String... lines) {
        library.getServerLogger().log(lines);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    @Deprecated
    public static double getMainVersion() {
        return VNC.SERVER_VERSION;
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    @Deprecated
    public static int getMajorVersion() {
        return (int) getMainVersion();
    }

    public static boolean restrictVersions(String minVersion, String maxVersion, String pluginPrefix, String pluginVersion) {
        int minCompare = VNC.compare(VNC.SERVER_MINECRAFT_VERSION, minVersion);
        int maxCompare = VNC.compare(VNC.SERVER_MINECRAFT_VERSION, maxVersion);

        if (minCompare >= 0 && maxCompare <= 0)
            return false;

        TakionLib.getLib().getServerLogger().log(
                pluginPrefix + " v" + pluginVersion +
                        " does not support " +
                        VNC.SERVER_CLASSIC_VERSION + " and " +
                        (minCompare < 0 ?
                                "older!" :
                                "newer. Please update!")
        );
        return true;
    }

    public static boolean restrictVersions(int minVersion, int maxVersion, String pluginPrefix, String pluginVersion) {
        return restrictVersions("1." + minVersion, "1." + maxVersion, pluginPrefix, pluginVersion);
    }

    public static boolean restrictVersions(int minVersion, int maxVersion, Plugin plugin) {
        String prefix = Objects.requireNonNull(plugin).getDescription().getPrefix();
        return restrictVersions(minVersion, maxVersion, prefix == null ? plugin.getName() : prefix, plugin.getDescription().getVersion());
    }
}
