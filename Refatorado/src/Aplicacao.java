import core.Sensor;
import devices.Fan;
import devices.Light;
import facade.SmartRoomFacade;
import sensors.LoggingSensorDecorator;
import sensors.SensorDecorator;
import sensors.SmoothingSensorDecorator;
import strategy.CoolingStrategy;
import strategy.PresenceLightStrategy;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Aplicacao {
    public static void main(String[] args) throws Exception {
        try (SmartRoomFacade facade = new SmartRoomFacade()) {
            // Devices
            Light light = (Light) facade.addDevice("light", "L1");
            Fan fan = (Fan) facade.addDevice("fan", "F1");

            // Sensors (Factory); wrap with Decorators (Decorator)
            Sensor temp = facade.addSensor("temperature", "T1");
            Sensor lum = facade.addSensor("luminosity", "LU1");
            Sensor pres = facade.addSensor("presence", "P1");

            // Decorate temperature with smoothing + logging
            SensorDecorator smoothTemp = new SmoothingSensorDecorator(temp, 3);
            SensorDecorator loggingTemp = new LoggingSensorDecorator(smoothTemp);

            // Replace map entry so scheduled reads use decorated chain
            // (simple approach for demo; in a larger app we'd register decorated sensors instead)
            // We'll just read the decorated sensor manually alongside.
            // Strategies (Strategy)
            facade.addStrategy(new PresenceLightStrategy(
                    facade.device("L1"),
                    facade.latestOf("luminosity"),
                    200.0
            ));
            facade.addStrategy(new CoolingStrategy(
                    facade.device("F1"),
                    27.0
            ));

            // Schedule periodic reads (Observer will capture values)
            facade.scheduleReadAll(500);

            // Additionally sample smoothed+logged temp in parallel
            for (int i = 0; i < 10; i++) {
                loggingTemp.read();
                TimeUnit.MILLISECONDS.sleep(300);
            }

            // Wait a bit to accumulate readings
            TimeUnit.SECONDS.sleep(3);

            // Export CSV report
            var out = facade.exportCsv(Paths.get("out", "report.csv"));
            System.out.println("Report exported to: " + out.toAbsolutePath());

            System.out.println("Light isOn: " + light.isOn());
            System.out.println("Fan isOn: " + fan.isOn());
        }
    }
}