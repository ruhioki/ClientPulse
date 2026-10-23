package com.ruhioki.clientpulse.core.setting;

public final class NumberSetting extends Setting<Double> {
    private final double minimum;
    private final double maximum;
    private final double step;

    public NumberSetting(String id, String name, String description,
            double minimum, double maximum, double step, double defaultValue) {
        super(id, name, description, Double.valueOf(defaultValue));
        if (!finite(minimum) || !finite(maximum) || !finite(step)
                || maximum <= minimum || step <= 0.0) {
            throw new IllegalArgumentException("Number setting bounds and step must be finite and positive.");
        }
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
        setValue(Double.valueOf(defaultValue));
    }

    public double minimum() {
        return minimum;
    }

    public double maximum() {
        return maximum;
    }

    public double step() {
        return step;
    }

    public double doubleValue() {
        return value().doubleValue();
    }

    @Override
    public String serialize() {
        return Double.toString(doubleValue());
    }

    @Override
    protected Double parse(String serialized) {
        try {
            double parsed = Double.parseDouble(serialized);
            return finite(parsed) ? Double.valueOf(parsed) : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    protected Double validate(Double value) {
        double raw = value.doubleValue();
        if (!finite(raw)) {
            throw new IllegalArgumentException("Number setting value must be finite.");
        }
        if (!finite(minimum) || !finite(maximum) || !finite(step)
                || maximum <= minimum || step <= 0.0) {
            return value;
        }
        double clamped = Math.max(minimum, Math.min(maximum, raw));
        double steps = Math.round((clamped - minimum) / step);
        double stepped = minimum + steps * step;
        return Double.valueOf(Math.max(minimum, Math.min(maximum, stepped)));
    }

    private static boolean finite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
}
