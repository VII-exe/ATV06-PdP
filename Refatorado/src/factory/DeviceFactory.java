package factory;

import core.Device;
import devices.Fan;
import devices.Light;

public class DeviceFactory {
    public Device create(String type, String id) {
        switch (type.toLowerCase()) {
            case "light": return new Light(id);
            case "fan": return new Fan(id);
            default: throw new IllegalArgumentException("Unknown device type: " + type);
        }
    }
}