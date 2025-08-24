package facade;

import core.Device;
import core.Sensor;
import factory.DeviceFactory;
import factory.SensorFactory;
import observer.MonitoringService;
import report.ReportGenerator;
import report.SensorData;
import strategy.ActionStrategy;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SmartRoomFacade implements AutoCloseable {
    private final SensorFactory sensorFactory = new SensorFactory();
    private final DeviceFactory deviceFactory = new DeviceFactory();
    private final Map<String, Sensor> sensors = new HashMap<>();
    private final Map<String, Device> devices = new HashMap<>();
    private final MonitoringService monitoring = new MonitoringService();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, Double> latestValues = new HashMap<>();

    public SmartRoomFacade() {}

    public Sensor addSensor(String type, String id) {
        Sensor s = sensorFactory.create(type, id);
        s.addObserver((source, data) -> latestValues.put(data.getType(), data.getValue()));
        s.addObserver(monitoring);
        sensors.put(id, s);
        return s;
    }

    public Device addDevice(String type, String id) {
        Device d = deviceFactory.create(type, id);
        devices.put(id, d);
        return d;
    }

    public void addStrategy(ActionStrategy strategy) {
        monitoring.registerStrategy(strategy);
    }

    public Supplier<Double> latestOf(String type) {
        return () -> latestValues.getOrDefault(type, 0.0);
    }

    public Supplier<Device> device(String id) {
        return () -> devices.get(id);
    }

    public void scheduleReadAll(long periodMillis) {
        scheduler.scheduleAtFixedRate(() -> sensors.values().forEach(Sensor::read),
                0, periodMillis, TimeUnit.MILLISECONDS);
    }

    public List<SensorData> history() {
        return monitoring.getHistory();
    }

    public Path exportCsv(Path out) throws Exception {
        return new ReportGenerator().toCsv(history(), out);
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}