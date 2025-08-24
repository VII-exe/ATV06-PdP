package sensors;

import core.Sensor;
import core.SensorObserver;
import report.SensorData;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSensor implements Sensor {
    private final String id;
    private final List<SensorObserver> observers = new ArrayList<>();

    public BaseSensor(String id) {
        this.id = id;
    }

    @Override
    public String getId() { return id; }

    @Override
    public void addObserver(SensorObserver observer) { observers.add(observer); }

    @Override
    public void removeObserver(SensorObserver observer) { observers.remove(observer); }

    @Override
    public void notifyObservers(SensorData data) {
        for (SensorObserver o : observers) o.update(this, data);
    }
}