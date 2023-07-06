package tn.amin.mpro2.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

public class IntRange {
    public int start;
    public int end;

    public IntRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(int value) {
        return start <= value && value < end;
    }

    public double transform(double value, IntRange other) {
        if (value < start || value > end) {
            throw new IllegalArgumentException("Value is outside the original range.");
        }

        // Calculate the ratio of the original range
        double originalRange = end - start;
        double newRange = other.end - other.start;
        double ratio = (value - start) / originalRange;

        // Map the value to the new range
        double mappedValue = ratio * newRange + other.start;
        return mappedValue;
    }
}
