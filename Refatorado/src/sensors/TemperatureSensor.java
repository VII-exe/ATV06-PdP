package sensors;

import report.SensorData;

import java.time.Instant;
import java.util.Random;

public class TemperatureSensor extends BaseSensor {
    private final Random rng = new Random();

    public TemperatureSensor(String id) { super(id); }

    @Override
    public SensorData read() {
        double temp = 20 + rng.nextDouble() * 15; // 20-35C
        SensorData d = new SensorData(getId(), "temperature", temp, Instant.now());
        notifyObservers(d);
        return d;
    }
}