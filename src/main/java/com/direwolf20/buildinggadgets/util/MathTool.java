package com.direwolf20.buildinggadgets.util;

public final class MathTool {

    public static int clamp(int value, int min, int max) {
        if (value > max) {
            return max;
        }

        if (value < min) {
            return min;
        }

        return value;
    }

    private MathTool() {}

    public static int floorMultiple(int i, int factor) {
        return i - (i % factor);
    }

    public static int ceilMultiple(int i, int factor) {
        return i + (i % factor);
    }

    public static boolean isEven(int i) {
        return (i & 1) == 0;
    }

    public static boolean isOdd(int i) {
        return i % 2 == 1;
    }

    private static int addForNonEven(int i, int c) {
        return isEven(i) ? i : i + c;
    }

    private static int addForNonOdd(int i, int c) {
        return isOdd(i) ? i : i + c;
    }

    public static int floorToEven(int i) {
        return addForNonEven(i, -1);
    }

    public static int floorToOdd(int i) {
        return addForNonOdd(i, -1);
    }

    public static int ceilToEven(int i) {
        return addForNonEven(i, 1);
    }

    public static int ceilToOdd(int i) {
        return addForNonOdd(i, 1);
    }

    public static int longToInt(long count) {
        try {
            return Math.toIntExact(count);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }
}
