package strategy;

import core.Device;
import report.SensorData;

import java.util.function.Supplier;

public class PresenceLightStrategy implements ActionStrategy {
    private final Supplier<Device> lightSupplier;
    private final Supplier<Double> latestLuxSupplier;
    private final double luxThreshold;

    public PresenceLightStrategy(Supplier<Device> lightSupplier, Supplier<Double> latestLuxSupplier, double luxThreshold) {
        this.lightSupplier = lightSupplier;
        this.latestLuxSupplier = latestLuxSupplier;
        this.luxThreshold = luxThreshold;
    }

    @Override
    public void apply(SensorData data) {
        if (!"presence".equals(data.getType())) return;
        Device light = lightSupplier.get();
        if (data.getValue() >= 1.0 && latestLuxSupplier.get() < luxThreshold) {
            light.on();
        } else {
            light.off();
        }
    }
}