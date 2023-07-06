package tn.amin.mpro2.util;

public class Range {
    public Number start;
    public Number end;

    public Range(Number start, Number end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(Number value) {
        return start.doubleValue() <= value.doubleValue() && value.doubleValue() < end.doubleValue();
    }

    public Number clamp(Number value) {
        if (value.doubleValue() < start.doubleValue()) return start;
        if (value.doubleValue() >= end.doubleValue()) return end;
        return value;
    }

    public Number transform(Number value, Range other) {
        if (value.doubleValue() < start.doubleValue() || value.doubleValue() > end.doubleValue()) {
            throw new IllegalArgumentException("Value is outside the original range.");
        }

        // Calculate the ratio of the original range
        double originalRange = end.doubleValue() - start.doubleValue();
        double newRange = other.end.doubleValue() - other.start.doubleValue();
        double ratio = (value.doubleValue() - start.doubleValue()) / originalRange;

        // Map the value to the new range
        return ratio * newRange + other.start.doubleValue();
    }

}
