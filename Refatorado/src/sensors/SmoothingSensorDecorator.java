package sensors;

import core.Sensor;
import report.SensorData;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class SmoothingSensorDecorator extends SensorDecorator {
    private final int window;
    private final Deque<Double> values = new ArrayDeque<>();

    public SmoothingSensorDecorator(Sensor delegate, int window) {
        super(delegate);
        if (window < 1) throw new IllegalArgumentException("window >= 1");
        this.window = window;
    }

    @Override
    public SensorData read() {
        SensorData raw = delegate.read();
        values.addLast(raw.getValue());
        if (values.size() > window) values.removeFirst();
        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(raw.getValue());
        SensorData smooth = new SensorData(raw.getSensorId(), raw.getType(), avg, Instant.now());
        notifyObservers(smooth);
        return smooth;
    }
}