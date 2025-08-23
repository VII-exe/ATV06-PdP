package main.java.com.smartroom.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Classe que representa os dados coletados de um sensor.
 * 
 * Aplica os padrões:
 * - Data Transfer Object (DTO): Carrega dados entre camadas
 * - Value Object: Representa um valor imutável com validação
 * - Builder Pattern: Construção flexível de objetos
 * 
 * Resolve problemas do código original:
 * - Eliminação de estruturas primitivas (Map<String, Object>)
 * - Tipagem forte para dados dos sensores
 * - Validação centralizada de dados
 * - Imutabilidade para thread-safety
 */
public class SensorData {

    // Campos imutáveis
    private final String sensorId;
    private final String sensorName;
    private final String sensorType;
    private final Object value;
    private final String unit;
    private final LocalDateTime timestamp;
    private final boolean isValid;
    private final String errorMessage;
    private final double numericValue; // Para facilitar cálculos

    /**
     * Construtor principal
     */
    public SensorData(String sensorId, String sensorName, String sensorType,
            Object value, String unit, LocalDateTime timestamp,
            boolean isValid, String errorMessage) {
        this.sensorId = validateNotNull(sensorId, "Sensor ID");
        this.sensorName = validateNotNull(sensorName, "Sensor Name");
        this.sensorType = validateNotNull(sensorType, "Sensor Type");
        this.value = value;
        this.unit = unit != null ? unit : "";
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.numericValue = extractNumericValue(value);
    }

    /**
     * Construtor simplificado para dados válidos
     */
    public SensorData(String sensorId, String sensorName, String sensorType,
            Object value, String unit) {
        this(sensorId, sensorName, sensorType, value, unit,
                LocalDateTime.now(), true, null);
    }

    /**
     * Construtor para dados inválidos/com erro
     */
    public static SensorData createErrorData(String sensorId, String sensorName,
            String sensorType, String errorMessage) {
        return new SensorData(sensorId, sensorName, sensorType, null, "",
                LocalDateTime.now(), false, errorMessage);
    }

    // Getters
    public String getSensorId() {
        return sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public Object getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public double getNumericValue() {
        return numericValue;
    }

    /**
     * Obtém o valor formatado para exibição
     */
    public String getFormattedValue() {
        if (!isValid) {
            return "ERRO";
        }

        if (value == null) {
            return "N/A";
        }

        // Formatação específica por tipo
        switch (sensorType.toLowerCase()) {
            case "temperatura":
                return String.format("%.1f%s", numericValue, unit);
            case "luminosidade":
                return String.format("%.0f %s", numericValue, unit);
            case "presenca":
                return value instanceof Boolean ? ((Boolean) value ? "Detectada" : "Ausente") : value.toString();
            case "umidade":
                return String.format("%.1f%s", numericValue, unit);
            default:
                return value.toString() + (unit.isEmpty() ? "" : " " + unit);
        }
    }

    /**
     * Obtém o timestamp formatado
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    /**
     * Verifica se os dados são recentes (últimos 30 segundos)
     */
    public boolean isRecent() {
        return timestamp.isAfter(LocalDateTime.now().minusSeconds(30));
    }

    /**
     * Verifica se os dados são de hoje
     */
    public boolean isFromToday() {
        return timestamp.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * Obtém a idade dos dados em segundos
     */
    public long getAgeInSeconds() {
        return java.time.Duration.between(timestamp, LocalDateTime.now()).getSeconds();
    }

    /**
     * Verifica se é um tipo de sensor específico
     */
    public boolean isTemperatureSensor() {
        return "temperatura".equalsIgnoreCase(sensorType);
    }

    public boolean isPresenceSensor() {
        return "presenca".equalsIgnoreCase(sensorType);
    }

    public boolean isLightSensor() {
        return "luminosidade".equalsIgnoreCase(sensorType);
    }

    public boolean isHumiditySensor() {
        return "umidade".equalsIgnoreCase(sensorType);
    }

    /**
     * Converte para formato de string para persistência
     * Formato: ID|Nome|Tipo|Valor|Unidade|Timestamp|Válido|Erro
     */
    public String toPersistenceFormat() {
        return String.join("|",
                sensorId,
                sensorName,
                sensorType,
                value != null ? value.toString() : "",
                unit,
                timestamp.toString(),
                String.valueOf(isValid),
                errorMessage != null ? errorMessage : "");
    }

    /**
     * Cria SensorData a partir de formato de persistência
     */
    public static SensorData fromPersistenceFormat(String data) {
        try {
            String[] parts = data.split("\\|", -1); // -1 para incluir campos vazios

            if (parts.length < 7) {
                throw new IllegalArgumentException("Formato inválido: poucos campos");
            }

            String id = parts[0];
            String name = parts[1];
            String type = parts[2];
            Object value = parseValue(parts[3], type);
            String unit = parts[4];
            LocalDateTime timestamp = LocalDateTime.parse(parts[5]);
            boolean valid = Boolean.parseBoolean(parts[6]);
            String error = parts.length > 7 && !parts[7].isEmpty() ? parts[7] : null;

            return new SensorData(id, name, type, value, unit, timestamp, valid, error);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao parsear dados: " + e.getMessage(), e);
        }
    }

    /**
     * Converte para JSON simples
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"sensorId\":\"").append(escapeJson(sensorId)).append("\",");
        json.append("\"sensorName\":\"").append(escapeJson(sensorName)).append("\",");
        json.append("\"sensorType\":\"").append(escapeJson(sensorType)).append("\",");
        json.append("\"value\":").append(valueToJson(value)).append(",");
        json.append("\"unit\":\"").append(escapeJson(unit)).append("\",");
        json.append("\"timestamp\":\"").append(timestamp.toString()).append("\",");
        json.append("\"isValid\":").append(isValid).append(",");
        json.append("\"errorMessage\":").append(errorMessage != null ? "\"" + escapeJson(errorMessage) + "\"" : "null")
                .append(",");
        json.append("\"formattedValue\":\"").append(escapeJson(getFormattedValue())).append("\",");
        json.append("\"ageInSeconds\":").append(getAgeInSeconds());
        json.append("}");
        return json.toString();
    }

    /**
     * Cria uma cópia com novo valor (para atualizações)
     */
    public SensorData withNewValue(Object newValue) {
        return new SensorData(this.sensorId, this.sensorName, this.sensorType,
                newValue, this.unit, LocalDateTime.now(),
                true, null);
    }

    /**
     * Cria uma cópia marcada como erro
     */
    public SensorData withError(String errorMessage) {
        return new SensorData(this.sensorId, this.sensorName, this.sensorType,
                this.value, this.unit, this.timestamp,
                false, errorMessage);
    }

    // Métodos utilitários privados
    private String validateNotNull(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo ou vazio");
        }
        return value.trim();
    }

    private double extractNumericValue(Object value) {
        if (value == null)
            return 0.0;

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1.0 : 0.0;
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static Object parseValue(String valueStr, String type) {
        if (valueStr == null || valueStr.isEmpty()) {
            return null;
        }

        try {
            switch (type.toLowerCase()) {
                case "temperatura":
                case "luminosidade":
                case "umidade":
                    return Double.parseDouble(valueStr);
                case "presenca":
                    return Boolean.parseBoolean(valueStr);
                default:
                    return valueStr;
            }
        } catch (Exception e) {
            return valueStr; // Fallback para string
        }
    }

    private String escapeJson(String str) {
        if (str == null)
            return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String valueToJson(Object value) {
        if (value == null)
            return "null";
        if (value instanceof String)
            return "\"" + escapeJson(value.toString()) + "\"";
        if (value instanceof Boolean || value instanceof Number)
            return value.toString();
        return "\"" + escapeJson(value.toString()) + "\"";
    }

    // Métodos de comparação e hash
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SensorData that = (SensorData) o;
        return Objects.equals(sensorId, that.sensorId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, timestamp, value);
    }

    @Override
    public String toString() {
        if (!isValid) {
            return String.format("SensorData[%s:%s - ERRO: %s]",
                    sensorId, sensorType, errorMessage);
        }

        return String.format("SensorData[%s:%s = %s (%s)]",
                sensorId, sensorType, getFormattedValue(),
                getFormattedTimestamp());
    }

    /**
     * Representação detalhada para logs
     */
    public String toDetailedString() {
        return String.format(
                "SensorData{\n" +
                        "  ID: %s\n" +
                        "  Nome: %s\n" +
                        "  Tipo: %s\n" +
                        "  Valor: %s\n" +
                        "  Unidade: %s\n" +
                        "  Timestamp: %s\n" +
                        "  Válido: %s\n" +
                        "  Idade: %ds\n" +
                        "  Erro: %s\n" +
                        "}",
                sensorId, sensorName, sensorType, getFormattedValue(),
                unit, getFormattedTimestamp(), isValid, getAgeInSeconds(),
                errorMessage != null ? errorMessage : "Nenhum");
    }

    /**
     * Builder para construção flexível
     */
    public static class Builder {
        private String sensorId;
        private String sensorName;
        private String sensorType;
        private Object value;
        private String unit = "";
        private LocalDateTime timestamp = LocalDateTime.now();
        private boolean isValid = true;
        private String errorMessage;

        public Builder(String sensorId, String sensorName, String sensorType) {
            this.sensorId = sensorId;
            this.sensorName = sensorName;
            this.sensorType = sensorType;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder invalid(String errorMessage) {
            this.isValid = false;
            this.errorMessage = errorMessage;
            return this;
        }

        public SensorData build() {
            return new SensorData(sensorId, sensorName, sensorType, value,
                    unit, timestamp, isValid, errorMessage);
        }
    }
}