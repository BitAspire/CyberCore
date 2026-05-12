package com.bitaspire.cybercore;

import lombok.Setter;
import me.croabeast.takion.logger.TakionLogger;
import me.croabeast.vnc.VNC;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class CoreSettings {

    private final CyberCore core;
    @Setter
    private char bootColor = '8';

    private String[] bootLogo = new String[]{
            "&8╭━━━╮&7╱╱╱&8╭╮&7╱╱╱╱╱╱&8╭━━━╮",
            "&8┃╭━╮┃&7╱╱╱&8┃┃&7╱╱╱╱╱╱&8┃╭━╮┃",
            "&8┃┃&7╱&8╰╋╮&7╱&8╭┫╰━┳━━┳━┫┃&7╱&8╰╋━━┳━┳━━╮",
            "&8┃┃&7╱&8╭┫┃&7╱&8┃┃╭╮┃┃━┫╭┫┃&7╱&8╭┫╭╮┃╭┫┃━┫",
            "&8┃╰━╯┃╰━╯┃╰╯┃┃━┫┃┃╰━╯┃╰╯┃┃┃┃━┫",
            "&8╰━━━┻━╮╭┻━━┻━━┻╯╰━━━┻━━┻╯╰━━╯",
            "&7╱╱╱╱&8╭━╯┃ &7Author: &fKihsomray",
            "&7╱╱╱╱&8╰━━╯ &7Version: &7"
    };

    private String[] legacyBootLogo = new String[]{
            "&8_________  _________  __________",
            "&8\\_   ___ \\ \\_   ___ \\ \\______   \\",
            "&8/    \\  \\/ /    \\  \\/  |       _/",
            "&8\\     \\____\\     \\____ |    |   \\",
            "&8 \\______  / \\______  / |____|_  /",
            "&8        \\/         \\/         \\/"
    };

    CoreSettings(CyberCore core) {
        this.core = core;
    }

    @NotNull
    public String getBootColor() {
        return "§" + bootColor;
    }

    public void setBootLogo(@NotNull String... array) {
        this.bootLogo = array;
    }

    public void setLegacyBootLogo(@NotNull String... array) {
        this.legacyBootLogo = array;
    }

    private boolean useLegacy() {
        return SystemUtils.OS_NAME.contains("Windows") && VNC.isBefore("1.12");
    }

    private String[] getBaseLogo() {
        return useLegacy() ? legacyBootLogo : bootLogo;
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    @Deprecated
    public String[] getArrayLogo(boolean applyColor) {
        return getLogo(applyColor);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
    @Deprecated
    public String getStringLogo(boolean applyColor) {
        return getLogoAsString(applyColor);
    }

    @NotNull
    public String[] getLogo(boolean applyColor) {
        final String[] logo = getBaseLogo();
        if (!applyColor) return logo;

        for (int i = 0; i < logo.length; i++)
            logo[i] = core.getLibrary().colorize(logo[i]);

        return logo;
    }

    @NotNull
    public String getLogoAsString(boolean applyColor) {
        StringBuilder builder = new StringBuilder();

        for (final String s : getLogo(applyColor))
            builder.append(s).append("\n");

        if (builder.length() > 2)
            builder = new StringBuilder(builder.substring(2));

        return builder.toString();
    }

    @NotNull
    public String getBootBar() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 48; i++) {
            if (i == 0 && !useLegacy()) builder.append(getBootColor());
            builder.append(useLegacy() ? "-" : "―");
        }

        return builder.toString();
    }

    public void sendBootHeader() {
        TakionLogger logger = core.getLibrary().getServerLogger();

        logger.log(getBootBar());
        logger.log(getLogo(true));
        logger.log(getBootBar(), "&7");
    }
}
