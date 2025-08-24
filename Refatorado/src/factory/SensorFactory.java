package factory;

import core.Sensor;

public class SensorFactory {
    public Sensor create(String type, String id) {
        switch (type.toLowerCase()) {
            case "temperature": return new TemperatureSensor(id);
            case "presence": return new PresenceSensor(id);
            case "luminosity": return new LuminositySensor(id);
            default: throw new IllegalArgumentException("Unknown sensor type: " + type);
        }
    }
}