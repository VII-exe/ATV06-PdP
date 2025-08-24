package report;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReportGenerator {
    public Path toCsv(List<SensorData> data, Path out) throws IOException {
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out))) {
            pw.println("timestamp,sensorId,type,value");
            for (SensorData d : data) {
                pw.printf("%s,%s,%s,%.2f%n", d.getTimestamp(), d.getSensorId(), d.getType(), d.getValue());
            }
        }
        return out;
    }
}