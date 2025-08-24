package strategy;

import core.Device;
import report.SensorData;

import java.util.function.Supplier;

public class CoolingStrategy implements ActionStrategy {
    private final Supplier<Device> fanSupplier;
    private final double tempThreshold;

    public CoolingStrategy(Supplier<Device> fanSupplier, double tempThreshold) {
        this.fanSupplier = fanSupplier;
        this.tempThreshold = tempThreshold;
    }

    @Override
    public void apply(SensorData data) {
        if (!"temperature".equals(data.getType())) return;
        Device fan = fanSupplier.get();
        if (data.getValue() >= tempThreshold) {
            fan.on();
        } else {
            fan.off();
        }
    }
}
