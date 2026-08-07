package com.bitaspire.cybercore;

import me.croabeast.file.ConfigurableFile;
import me.croabeast.takion.TakionLib;
import me.croabeast.takion.logger.TakionLogger;
import me.croabeast.takion.marker.Marker;
import me.croabeast.takion.token.Token;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class TextLibrary extends TakionLib {

    private static final String DEFAULT_PREFIX = "&8&lCCR &8» &r";
    private static final String DEFAULT_KEY = "{p}";

    final CyberCore core;

    private String langPrefix = DEFAULT_PREFIX;
    private String langPrefixKey = DEFAULT_KEY;
    private Pattern langPrefixPattern = Pattern.compile(Pattern.quote(DEFAULT_KEY));

    // Takion 2.0.0 turned the lang prefix into a marker and its setters are package-private there,
    // so the whole thing is owned here. It reads the fields on every resolve instead of capturing
    // them, which is what makes a prefix set at reload time apply without re-registering anything.
    private final Marker langPrefixMarker = new Marker() {
        @NotNull
        public String getId() {
            return "lang_prefix";
        }

        @NotNull
        public Pattern getPattern() {
            return langPrefixPattern;
        }

        @NotNull
        public String resolve(@NotNull Token.Context context, @NotNull MatchResult match) {
            return Boolean.TRUE.equals(context.getOption("remove")) ? "" : langPrefix;
        }
    };

    TextLibrary(CyberCore core) {
        super(core.getPlugin());
        this.core = core;

        applyLangPrefix();

        getChannelManager().identify("action_bar").addPrefix("actionbar");
        getChannelManager().identify("action_bar").addPrefix("action-bar");
    }

    @NotNull
    public String getLangPrefix() {
        return langPrefix;
    }

    public void setLangPrefix(String prefix) {
        langPrefix = prefix == null ? "" : prefix;
        applyLangPrefix();
    }

    @NotNull
    public String getLangPrefixKey() {
        return langPrefixKey;
    }

    public void setLangPrefixKey(String key) {
        langPrefixKey = key == null || key.trim().isEmpty() ? DEFAULT_KEY : key;
        langPrefixPattern = Pattern.compile(Pattern.quote(langPrefixKey));
        applyLangPrefix();
    }

    private void applyLangPrefix() {
        // edit keeps the registration order, which the pipeline depends on; load covers the case
        // where the built-in marker is gone.
        if (!getMarkerManager().edit("lang_prefix", langPrefixMarker))
            getMarkerManager().load(langPrefixMarker);
    }

    void load(BooleanSupplier supplier) {
        applyLangPrefix();

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
