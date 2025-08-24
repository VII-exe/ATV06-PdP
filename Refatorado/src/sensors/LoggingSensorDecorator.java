package sensors;

import core.Sensor;
import report.SensorData;

public class LoggingSensorDecorator extends SensorDecorator {
    public LoggingSensorDecorator(Sensor delegate) { super(delegate); }

    @Override
    public SensorData read() {
        SensorData d = delegate.read();
        System.out.println("[LOG] " + d);
        return d;
    }
}