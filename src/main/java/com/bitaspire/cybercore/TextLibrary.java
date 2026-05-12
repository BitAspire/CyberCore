package com.bitaspire.cybercore;

import me.croabeast.file.ConfigurableFile;
import me.croabeast.takion.TakionLib;
import me.croabeast.takion.logger.TakionLogger;

import java.util.function.BooleanSupplier;

final class TextLibrary extends TakionLib {

    final CyberCore core;

    TextLibrary(CyberCore core) {
        super(core.getPlugin());
        this.core = core;

        setLangPrefix("&8&lCCR &8» &r");
        setLangPrefixKey("{p}");

        getChannelManager().identify("action_bar").addPrefix("actionbar");
        getChannelManager().identify("action_bar").addPrefix("action-bar");
    }

    void load(BooleanSupplier supplier) {
        BooleanSupplier colored;
        try {
            ConfigurableFile file = core.getFileManager().get("config");
            colored = () -> file.get("config.console-color", true);
        } catch (NullPointerException e) {
            colored = supplier != null ? supplier : () -> true;
        }

        BooleanSupplier finalSupplier = colored;
        setLogger(new TakionLogger(this) {
            @Override
            public boolean isColored() {
                return finalSupplier.getAsBoolean();
            }
        });
        setServerLogger(new TakionLogger(this, false) {
            @Override
            public boolean isColored() {
                return finalSupplier.getAsBoolean();
            }
        });
    }
}
