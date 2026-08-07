package com.bitaspire.cybercore;

import me.croabeast.takion.marker.Marker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the lang prefix, which Takion 2.0.0 moved into a marker whose setters are not reachable
 * from outside the library, so CyberCore owns it now.
 */
class TextLibraryTest {

    private TextLibrary library;

    @BeforeAll
    static void setUpServer() {
        TestServer.install();
    }

    @BeforeEach
    void setUp() {
        // A null plugin keeps TakionLib off the scheduler path, which needs a running server.
        library = new TextLibrary(new CyberCore(null));
    }

    private String apply(String text) {
        return library.applyMarker("lang_prefix", null, text);
    }

    @Test
    void replacesTheDefaultKeyWithTheDefaultPrefix() {
        assertEquals("{p}", library.getLangPrefixKey());
        assertEquals("&8&lCCR &8» &r", library.getLangPrefix());
        assertEquals("&8&lCCR &8» &r Hello", apply("{p} Hello"));
    }

    @Test
    void leavesTextWithoutTheKeyUntouched() {
        assertEquals("Hello", apply("Hello"));
        assertEquals("<P> Hello", apply("<P> Hello"));
    }

    @Test
    void replacesEveryOccurrence() {
        assertEquals("A A", apply("{p} {p}").replace("&8&lCCR &8» &r", "A"));
    }

    @Test
    void appliesAPrefixSetAfterConstruction() {
        library.setLangPrefix("&aNEW &r");

        assertEquals("&aNEW &r", library.getLangPrefix());
        assertEquals("&aNEW &r Hello", apply("{p} Hello"));
    }

    @Test
    void appliesAKeySetAfterConstruction() {
        library.setLangPrefixKey("<pre>");

        assertEquals("<pre>", library.getLangPrefixKey());
        assertEquals("&8&lCCR &8» &r Hello", apply("<pre> Hello"));
        assertEquals("{p} Hello", apply("{p} Hello"));
    }

    @Test
    void treatsTheKeyAsLiteralTextAndNotAsARegex() {
        library.setLangPrefixKey("[p]");

        assertEquals("&8&lCCR &8» &r", apply("[p]"));
        assertEquals("p", apply("p"));
    }

    @Test
    void nullPrefixBecomesEmptyAndBlankKeyFallsBackToTheDefault() {
        library.setLangPrefix(null);
        assertEquals("", library.getLangPrefix());
        assertEquals(" Hello", apply("{p} Hello"));

        library.setLangPrefixKey("  ");
        assertEquals("{p}", library.getLangPrefixKey());
    }

    @Test
    void reloadKeepsTheConfiguredPrefixInsteadOfResettingIt() {
        library.setLangPrefix("&aFROM-YML &r");
        library.setLangPrefixKey("<pre>");

        // load(...) is the path CyberCore.setColoredSupplier/loadStart run on a reload.
        library.load(() -> true);

        assertEquals("&aFROM-YML &r", library.getLangPrefix());
        assertEquals("<pre>", library.getLangPrefixKey());
        assertEquals("&aFROM-YML &r Hello", apply("<pre> Hello"));
    }

    @Test
    void reloadDoesNotLoseTheMarkerRegistration() {
        library.load(() -> true);

        Marker marker = library.getMarkerManager().getMarker("lang_prefix");
        assertNotNull(marker);
        assertEquals("lang_prefix", marker.getId());
        assertEquals("&8&lCCR &8» &r Hello", apply("{p} Hello"));
    }

    @Test
    void keepsASingleMarkerInstanceAcrossUpdates() {
        Marker first = library.getMarkerManager().getMarker("lang_prefix");

        library.setLangPrefix("&aNEW &r");
        library.setLangPrefixKey("<pre>");
        library.load(() -> true);

        assertSame(first, library.getMarkerManager().getMarker("lang_prefix"));
        assertEquals(1, library.getMarkerManager().getMarkers().stream()
                .filter(marker -> "lang_prefix".equals(marker.getId()))
                .count());
    }

    @Test
    void keepsTheOtherBuiltInMarkersRegistered() {
        assertNotNull(library.getMarkerManager().getMarker("line_separator"));
        assertNotNull(library.getMarkerManager().getMarker("center_prefix"));
    }

    @Test
    void registersTheActionBarChannelAliases() {
        assertNotNull(library.getChannelManager().identify("actionbar"));
        assertNotNull(library.getChannelManager().identify("action-bar"));
    }

    @Test
    void librarySurvivesWithoutFilesOnLoad() {
        // getFileManager().get("config") throws when no file was loaded; load() must swallow it.
        library.load(() -> false);
        assertTrue(library.getServerLogger() != null && library.getLogger() != null);
    }
}
