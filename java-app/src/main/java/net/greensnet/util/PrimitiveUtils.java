/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.util;

public final class PrimitiveUtils {

    private PrimitiveUtils() {
    }

    /**
     * Returns the value within the String as an positive integer, or -1 if the
     * String is not negative or invalid.
     */
    public static int parsePositiveIntSafe(String input) {
        if (input != null) {
            try {
                int num = Integer.parseInt(input);
                if (num < 0) {
                    return -1;
                }
                return num;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Returns the value within the String as an positive long, or -1 if the String
     * is not negative or invalid.
     */
    public static long parsePositiveLongSafe(String input) {
        if (input != null) {
            try {
                long num = Long.parseLong(input);
                if (num < 0) {
                    return -1;
                }
                return num;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Returns the value within the String as an positive float, or -1 if the String
     * is not negative or invalid.
     */
    public static float parsePositiveFloatSafe(String input) {
        if (input != null) {
            try {
                float num = Float.parseFloat(input);
                if (num < 0) {
                    return -1;
                }
                return num;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static boolean parseBooleanSafe(String input) {
        return "true".equalsIgnoreCase(input);
    }
}
