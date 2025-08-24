package sensors;

import report.SensorData;

import java.time.Instant;
import java.util.Random;

public class PresenceSensor extends BaseSensor {
    private final Random rng = new Random();

    public PresenceSensor(String id) { super(id); }

    @Override
    public SensorData read() {
        double present = rng.nextBoolean() ? 1.0 : 0.0;
        SensorData d = new SensorData(getId(), "presence", present, Instant.now());
        notifyObservers(d);
        return d;
    }
}