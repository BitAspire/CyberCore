package net.zerotoil.dev.cybercore.utilities;

import lombok.Setter;
import me.croabeast.beanslib.Beans;

/**
 * Basic time utilities for any server.
 *
 * @author Kihsomray
 * @version 1.0
 */
public class TimeUtils {

    // formatting strings
    @Setter private static String secondFormat = "{time} Second(s)";
    @Setter private static String minuteFormat = "{time} Minute(s)";
    @Setter private static String hourFormat = "{time} Hour(s)";
    @Setter private static String dayFormat = "{time} Day(s)";
    @Setter private static String splitterFormat = ", ";

    // fields needed for time formatter
    private static String pluralRegex = "\\s*\\([^)]*\\)\\s*";
    @Setter private static char startDelimiter = '(';
    @Setter private static char endDelimiter = ')';

    private static String colorize(String input) {
        return Beans.colorize(input);
    }

    /**
     * Takes in seconds and returns a very nicely formatted string
     * that can contain seconds, minutes, hours and days.
     *
     * @param seconds Amount of seconds to format
     * @return Formatted string with seconds, minutes, hours and days
     */
    public static String formatTime(long seconds) {

        // if time 0, return right away
        if (seconds <= 0) return colorize(checkPluralFormat(0, secondFormat, ""));

        String formattedTime = "";
        long daysTotal, hoursTotal, minutesTotal;

        // gets day time
        daysTotal = getFixedTime(seconds, 86400);
        seconds = seconds - (daysTotal * 86400);
        if (daysTotal > 0) formattedTime += (checkPluralFormat(daysTotal, dayFormat, splitterFormat));

        // gets hour time
        hoursTotal = getFixedTime(seconds, 3600);
        seconds = seconds - (hoursTotal * 3600);
        if (hoursTotal > 0) formattedTime += checkPluralFormat(hoursTotal, hourFormat, splitterFormat);

        // gets minute time
        minutesTotal = getFixedTime(seconds, 60);
        seconds = seconds - (minutesTotal * 60);
        if (minutesTotal > 0) formattedTime += checkPluralFormat(minutesTotal, minuteFormat, splitterFormat);

        // gets second time
        if (seconds > 0) formattedTime += checkPluralFormat(seconds, secondFormat, splitterFormat);

        // returns final string
        return colorize(formattedTime.substring(0, formattedTime.length() - splitterFormat.length()));

    }

    // gets proper time for a time format
    private static long getFixedTime(long seconds, long formatter) {
        long tempSeconds = seconds % formatter;
        return (seconds - tempSeconds) / formatter;
    }

    // checks plural formatting and applies it
    private static String checkPluralFormat(long value, String string, String splitterFormat) {
        if (string.equalsIgnoreCase("")) return "";
        string = string.replace("{time}", value + "") + splitterFormat;
        if (value == 1) return string.replaceAll(pluralRegex, "");
        else return string.replace(startDelimiter + "", "").replace(endDelimiter + "", "");
    }


    /**
     * When there is only one item, what should be replaced?
     *
     * @param regex a custom regex
     */
    public static void pluralRegexFormat(String regex) {
        pluralRegex = regex;
    }

}
