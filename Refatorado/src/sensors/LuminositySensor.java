package sensors;

import report.SensorData;

import java.time.Instant;
import java.util.Random;

public class LuminositySensor extends BaseSensor {
    private final Random rng = new Random();

    public LuminositySensor(String id) { super(id); }

    @Override
    public SensorData read() {
        double lux = 50 + rng.nextDouble() * 550; // 50-600 lux
        SensorData d = new SensorData(getId(), "luminosity", lux, Instant.now());
        notifyObservers(d);
        return d;
    }
}