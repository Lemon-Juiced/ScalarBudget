package site.scalarstudios.scalarbudget.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The Period enum represents various time periods with their display names and equivalent days.
 * It provides methods to retrieve all periods and to parse a period from a string.
 *
 * @author Lemon_Juiced
 */
public enum Period {
    DAY("day", 1),
    WEEK("week", 7),
    BIWEEK("bi-week", 14),
    MONTH("month", 30.4375),
    BIMONTH("bi-month", 60.875),
    YEAR("year", 365.25);

    private final String displayName;
    private final double days;

    Period(String displayName, double days) {
        this.displayName = displayName;
        this.days = days;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDays() {
        return days;
    }

    public static List<Period> getAll() {
        return Arrays.asList(values());
    }

    public static Optional<Period> fromString(String s) {
        if (s == null) return Optional.empty();
        String norm = s.trim().toLowerCase().replace("-", "");
        for (Period p : values()) {
            if (p.displayName.replace("-", "").equals(norm) || p.name().toLowerCase().equals(norm)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
}

