package sensors;

import core.Sensor;
import core.SensorObserver;
import report.SensorData;

public abstract class SensorDecorator implements Sensor {
    protected final Sensor delegate;

    public SensorDecorator(Sensor delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() { return delegate.getId(); }

    @Override
    public void addObserver(SensorObserver observer) { delegate.addObserver(observer); }

    @Override
    public void removeObserver(SensorObserver observer) { delegate.removeObserver(observer); }

    @Override
    public void notifyObservers(SensorData data) { delegate.notifyObservers(data); }
}