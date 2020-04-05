package shared.model;

import java.io.Serializable;

/**
 * An enumeration to represent gender
 * Valid types: MALE, FEMALE
 * @author griffinbholt
 */
public enum Gender implements Serializable {
    MALE, FEMALE;

    /**
     * Generates an enum from the input gender abbreviation ("m"/"f")
     * @param abbreviation "m" or "f"; all other input is considered invalid
     * @return The enumerated case related to the input abbreviation (MALE for "m", FEMALE for "f");
     *         null, if input is invalid
     */
    public static Gender generate(String abbreviation) {
        switch (abbreviation) {
            case "m":
                return MALE;
            case "f":
                return FEMALE;
            default:
                return null;
        }
    }

    /**
     * Returns the abbreviation of the enumerated gender
     * @return "m" if MALE; "f" if FEMALE
     */
    public String toAbbreviation() {
        String abbreviation = null;

        switch (this) {
            case MALE:
                abbreviation = "m";
                break;
            case FEMALE:
                abbreviation = "f";
                break;
        }

        return abbreviation;
    }

    /**
     * Returns the full string of the enumerated gender, with the first letter capitalized
     * @return "Male" if MALE; "Female" if FEMALE
     */
    public String toFullString() {
        String fullString = null;

        switch (this) {
            case MALE:
                fullString = "Male";
                break;
            case FEMALE:
                fullString = "Female";
                break;
        }

        return fullString;
    }
}
