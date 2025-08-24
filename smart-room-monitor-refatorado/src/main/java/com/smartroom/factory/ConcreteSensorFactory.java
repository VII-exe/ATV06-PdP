package main.java.com.smartroom.factory;

import main.java.com.smartroom.model.*;

/**
 * Implementação concreta do Factory Method Pattern para criação de sensores.
 * 
 * Esta classe resolve diretamente os anti-patterns do código original:
 * 
 * ANTES (Anti-patterns):
 * ```java
 * // Copy-Paste Programming + Magic Strings
 * sensorsData.add("TEMP001|Sensor Temperatura 1|temperatura|22.5|true");
 * sensorsData.add("TEMP002|Sensor Temperatura 2|temperatura|22.0|true");
 * sensorsData.add("PRES001|Sensor Presença 1|presenca|false|true");
 * ```
 * 
 * DEPOIS (Factory Pattern):
 * ```java
 * SensorFactory factory = new ConcreteSensorFactory();
 * Sensor temp1 = factory.createSensor(TEMPERATURE, "TEMP001", "Sensor Temperatura 1");
 * Sensor pres1 = factory.createSensor(PRESENCE, "PRES001", "Sensor Presença 1");
 * ```
 * 
 * Benefícios aplicados:
 * - Elimina duplicação de código
 * - Remove magic strings e numbers
 * - Centraliza lógica de criação
 * - Facilita extensão para novos tipos
 * - Melhora testabilidade
 */
public class ConcreteSensorFactory implements SensorFactory {
    
    // Valores padrão para diferentes tipos de sensores
    private static final double DEFAULT_TEMPERATURE = 22.0;
    private static final boolean DEFAULT_PRESENCE = false;
    private static final int DEFAULT_LUMINOSITY = 400;
    private static final double DEFAULT_HUMIDITY = 45.0;
    
    /**
     * Implementação do Factory Method principal.
     * Substitui a lógica hardcoded do sistema original.
     */
    @Override
    public Sensor createSensor(SensorType type, String id, String name) {
        validateParameters(type, id, name);
        
        switch (type) {
            case TEMPERATURE:
                return new TemperatureSensor(id, name, DEFAULT_TEMPERATURE);
                
            case PRESENCE:
                return new PresenceSensor(id, name);
                
            case LIGHT:
                return new LightSensor(id, name, DEFAULT_LUMINOSITY);
                
            case HUMIDITY:
                // Implementação futura - por enquanto usar temperatura como base
                return new TemperatureSensor(id, name, DEFAULT_HUMIDITY);
                
            default:
                throw new UnsupportedOperationException(
                    "Tipo de sensor não suportado: " + type);
        }
    }
    
    /**
     * Factory method com valor base personalizado.
     */
    @Override
    public Sensor createSensor(SensorType type, String id, String name, Object baseValue) {
        validateParameters(type, id, name);
        
        switch (type) {
            case TEMPERATURE:
                double tempValue = extractDoubleValue(baseValue, DEFAULT_TEMPERATURE);
                return new TemperatureSensor(id, name, tempValue);
                
            case PRESENCE:
                int sensitivity = extractIntValue(baseValue, 30);
                return new PresenceSensor(id, name, sensitivity);
                
            case LIGHT:
                int lumValue = extractIntValue(baseValue, DEFAULT_LUMINOSITY);
                return new LightSensor(id, name, lumValue);
                
            case HUMIDITY:
                double humValue = extractDoubleValue(baseValue, DEFAULT_HUMIDITY);
                return new TemperatureSensor(id, name, humValue); // Placeholder
                
            default:
                throw new UnsupportedOperationException(
                    "Tipo de sensor não suportado: " + type);
        }
    }
    
    /**
     * Factory method usando configuração completa.
     */
    @Override
    public Sensor createSensor(SensorConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuração não pode ser nula");
        }
        
        Sensor sensor = createSensor(config.getType(), config.getId(), 
                                   config.getName(), config.getBaseValue());
        
        // Aplicar configurações adicionais
        sensor.setActive(config.isActive());
        
        // Configurações específicas por tipo
        if (sensor instanceof PresenceSensor && config.getSensitivity() > 0) {
            ((PresenceSensor) sensor).setSensitivity(config.getSensitivity());
        }
        
        return sensor;
    }
    
    /**
     * Verifica se um tipo é suportado.
     */
    @Override
    public boolean supportsType(SensorType type) {
        if (type == null) return false;
        
        switch (type) {
            case TEMPERATURE:
            case PRESENCE:
            case LIGHT:
            case HUMIDITY:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Retorna todos os tipos suportados.
     */
    @Override
    public SensorType[] getSupportedTypes() {
        return new SensorType[] {
            SensorType.TEMPERATURE,
            SensorType.PRESENCE,
            SensorType.LIGHT,
            SensorType.HUMIDITY
        };
    }
    
    /**
     * Método de conveniência para criar sensores padrão do sistema.
     * Substitui o método setupDefaultSensors() do código original.
     */
    public Sensor[] createDefaultSensors() {
        return new Sensor[] {
            // Sensores de temperatura
            createSensor(SensorType.TEMPERATURE, "TEMP001", "Sensor Temperatura Sala", 22.5),
            createSensor(SensorType.TEMPERATURE, "TEMP002", "Sensor Temperatura Quarto", 21.8),
            
            // Sensores de presença
            createSensor(SensorType.PRESENCE, "PRES001", "Sensor Presença Porta", 30),
            createSensor(SensorType.PRESENCE, "PRES002", "Sensor Presença Janela", 25),
            
            // Sensor de luminosidade
            createSensor(SensorType.LIGHT, "LUX001", "Sensor Luminosidade Principal", 450)
        };
    }
    
    /**
     * Cria sensores de temperatura com diferentes configurações.
     */
    public Sensor[] createTemperatureSensors(String... locations) {
        Sensor[] sensors = new Sensor[locations.length];
        
        for (int i = 0; i < locations.length; i++) {
            String id = String.format("TEMP%03d", i + 1);
            String name = "Sensor Temperatura " + locations[i];
            sensors[i] = createSensor(SensorType.TEMPERATURE, id, name);
        }
        
        return sensors;
    }
    
    /**
     * Cria sensores usando o padrão de nomes do sistema original.
     * Mantém compatibilidade com formato existente.
     */
    public Sensor createFromLegacyFormat(String legacyData) {
        try {
            String[] parts = legacyData.split("\\|");
            
            if (parts.length < 5) {
                throw new IllegalArgumentException("Formato legacy inválido: " + legacyData);
            }
            
            String id = parts[0];
            String name = parts[1];
            String typeStr = parts[2];
            String valueStr = parts[3];
            boolean active = Boolean.parseBoolean(parts[4]);
            
            // Converter tipo string para enum
            SensorType type = parseTypeFromString(typeStr);
            
            // Converter valor
            Object baseValue = parseValueByType(valueStr, type);
            
            // Criar sensor
            SensorConfig config = new SensorConfig(id, name, type)
                .withBaseValue(baseValue)
                .withActive(active);
            
            return createSensor(config);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao criar sensor do formato legacy: " + e.getMessage(), e);
        }
    }
    
    // Métodos utilitários privados
    
    private void validateParameters(SensorType type, String id, String name) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo do sensor não pode ser nulo");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do sensor não pode ser nulo ou vazio");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do sensor não pode ser nulo ou vazio");
        }
        if (!supportsType(type)) {
            throw new UnsupportedOperationException("Tipo de sensor não suportado: " + type);
        }
    }
    
    private double extractDoubleValue(Object value, double defaultValue) {
        if (value == null) return defaultValue;
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private int extractIntValue(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private SensorType parseTypeFromString(String typeStr) {
        if (typeStr == null) {
            throw new IllegalArgumentException("Tipo não pode ser nulo");
        }
        
        switch (typeStr.toLowerCase().trim()) {
            case "temperatura":
                return SensorType.TEMPERATURE;
            case "presenca":
            case "presença":
                return SensorType.PRESENCE;
            case "luminosidade":
                return SensorType.LIGHT;
            case "umidade":
                return SensorType.HUMIDITY;
            default:
                throw new IllegalArgumentException("Tipo desconhecido: " + typeStr);
        }
    }
    
    private Object parseValueByType(String valueStr, SensorType type) {
        if (valueStr == null || valueStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            switch (type) {
                case TEMPERATURE:
                case HUMIDITY:
                    return Double.parseDouble(valueStr);
                case PRESENCE:
                    return Boolean.parseBoolean(valueStr);
                case LIGHT:
                    return Integer.parseInt(valueStr);
                default:
                    return valueStr;
            }
        } catch (NumberFormatException e) {
            // Fallback para valores padrão se parsing falhar
            switch (type) {
                case TEMPERATURE:
                    return DEFAULT_TEMPERATURE;
                case HUMIDITY:
                    return DEFAULT_HUMIDITY;
                case PRESENCE:
                    return DEFAULT_PRESENCE;
                case LIGHT:
                    return DEFAULT_LUMINOSITY;
                default:
                    return valueStr;
            }
        }
    }
    
    /**
     * Método para debugging e logging.
     */
    public String getFactoryInfo() {
        StringBuilder info = new StringBuilder();
        info.append("ConcreteSensorFactory {\n");
        info.append("  Tipos suportados: ");
        
        SensorType[] types = getSupportedTypes();
        for (int i = 0; i < types.length; i++) {
            info.append(types[i].getDisplayName());
            if (i < types.length - 1) info.append(", ");
        }
        
        info.append("\n");
        info.append("  Valores padrão:\n");
        info.append("    - Temperatura: ").append(DEFAULT_TEMPERATURE).append("°C\n");
        info.append("    - Presença: ").append(DEFAULT_PRESENCE).append("\n");
        info.append("    - Luminosidade: ").append(DEFAULT_LUMINOSITY).append(" lux\n");
        info.append("    - Umidade: ").append(DEFAULT_HUMIDITY).append("%\n");
        info.append("}");
        
        return info.toString();
    }
    
    @Override
    public String toString() {
        return "ConcreteSensorFactory{supportedTypes=" + getSupportedTypes().length + "}";
    }
}