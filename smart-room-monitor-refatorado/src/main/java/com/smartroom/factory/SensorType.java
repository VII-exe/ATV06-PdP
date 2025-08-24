package main.java.com.smartroom.factory;

/**
 * Enum que define os tipos de sensores suportados pelo sistema.
 * 
 * Resolve os anti-patterns do código original:
 * - Magic Strings: Substitui strings hardcoded ("temperatura", "presenca",
 * etc.)
 * - Type Safety: Garante que apenas tipos válidos sejam usados
 * - Centralização: Define todos os tipos em um local
 * 
 * ANTES (Anti-pattern - Magic Strings):
 * ```java
 * if (!tipo.equals("temperatura") && !tipo.equals("presenca") &&
 * !tipo.equals("luminosidade") && !tipo.equals("umidade")) {
 * return "ERRO: Tipo inválido";
 * }
 * ```
 * 
 * DEPOIS (Type-Safe Enum):
 * ```java
 * SensorType type = SensorType.TEMPERATURE;
 * if (factory.supportsType(type)) {
 * sensor = factory.createSensor(type, id, name);
 * }
 * ```
 * 
 * Aplica princípios:
 * - DRY: Não repete strings de tipos
 * - Type Safety: Compile-time checking
 * - Extensibilidade: Fácil adicionar novos tipos
 */
public enum SensorType {

    /**
     * Sensor de temperatura ambiente.
     * Mede valores em graus Celsius.
     */
    TEMPERATURE(
            "temperatura",
            "°C",
            "Temperatura",
            "Mede a temperatura do ambiente em graus Celsius"),

    /**
     * Sensor de detecção de presença.
     * Detecta movimento ou presença humana.
     */
    PRESENCE(
            "presenca",
            "",
            "Presença",
            "Detecta movimento ou presença de pessoas no ambiente"),

    /**
     * Sensor de luminosidade.
     * Mede intensidade de luz em lux.
     */
    LIGHT(
            "luminosidade",
            "lux",
            "Luminosidade",
            "Mede a intensidade de luz do ambiente em lux"),

    /**
     * Sensor de umidade relativa do ar.
     * Mede percentual de umidade.
     */
    HUMIDITY(
            "umidade",
            "%",
            "Umidade",
            "Mede a umidade relativa do ar em percentual"),

    /**
     * Sensor de pressão atmosférica.
     * Para futuras expansões do sistema.
     */
    PRESSURE(
            "pressao",
            "hPa",
            "Pressão",
            "Mede a pressão atmosférica em hectopascal"),

    /**
     * Sensor de qualidade do ar.
     * Mede partículas e gases no ambiente.
     */
    AIR_QUALITY(
            "qualidade_ar",
            "AQI",
            "Qualidade do Ar",
            "Mede a qualidade do ar usando índice AQI"),

    /**
     * Sensor de ruído/som.
     * Mede níveis de decibéis.
     */
    SOUND(
            "ruido",
            "dB",
            "Ruído",
            "Mede níveis de ruído em decibéis");

    // Campos do enum
    private final String legacyName; // Nome usado no sistema original
    private final String unit; // Unidade de medida
    private final String displayName; // Nome para exibição
    private final String description; // Descrição detalhada

    /**
     * Construtor do enum.
     */
    SensorType(String legacyName, String unit, String displayName, String description) {
        this.legacyName = legacyName;
        this.unit = unit;
        this.displayName = displayName;
        this.description = description;
    }

    // Getters

    /**
     * Obtém o nome usado no sistema original (para compatibilidade).
     */
    public String getLegacyName() {
        return legacyName;
    }

    /**
     * Obtém a unidade de medida do sensor.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Obtém o nome formatado para exibição.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtém a descrição detalhada do tipo de sensor.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica se o sensor produz valores numéricos.
     */
    public boolean isNumeric() {
        return this != PRESENCE;
    }

    /**
     * Verifica se o sensor produz valores booleanos.
     */
    public boolean isBoolean() {
        return this == PRESENCE;
    }

    /**
     * Obtém a faixa típica de valores para o tipo de sensor.
     */
    public String getTypicalRange() {
        switch (this) {
            case TEMPERATURE:
                return "10°C - 40°C";
            case PRESENCE:
                return "true/false";
            case LIGHT:
                return "0 - 10000 lux";
            case HUMIDITY:
                return "0% - 100%";
            case PRESSURE:
                return "950 - 1050 hPa";
            case AIR_QUALITY:
                return "0 - 500 AQI";
            case SOUND:
                return "30 - 120 dB";
            default:
                return "N/A";
        }
    }

    /**
     * Obtém cor CSS recomendada para exibição (para dashboards).
     */
    public String getDisplayColor() {
        switch (this) {
            case TEMPERATURE:
                return "#dc3545"; // Vermelho
            case PRESENCE:
                return "#28a745"; // Verde
            case LIGHT:
                return "#ffc107"; // Amarelo
            case HUMIDITY:
                return "#17a2b8"; // Azul claro
            case PRESSURE:
                return "#6f42c1"; // Roxo
            case AIR_QUALITY:
                return "#fd7e14"; // Laranja
            case SOUND:
                return "#20c997"; // Verde claro
            default:
                return "#6c757d"; // Cinza
        }
    }

    /**
     * Obtém ícone Bootstrap recomendado para o tipo.
     */
    public String getBootstrapIcon() {
        switch (this) {
            case TEMPERATURE:
                return "bx-thermometer";
            case PRESENCE:
                return "bx-user-check";
            case LIGHT:
                return "bx-bulb";
            case HUMIDITY:
                return "bx-water";
            case PRESSURE:
                return "bx-trending-up";
            case AIR_QUALITY:
                return "bx-leaf";
            case SOUND:
                return "bx-volume-full";
            default:
                return "bx-device";
        }
    }

    // Métodos estáticos utilitários

    /**
     * Converte string do sistema legado para enum.
     * Mantém compatibilidade com código original.
     * 
     * @param legacyName Nome usado no sistema original
     * @return SensorType correspondente
     * @throws IllegalArgumentException se tipo não encontrado
     */
    public static SensorType fromLegacyName(String legacyName) {
        if (legacyName == null) {
            throw new IllegalArgumentException("Nome legado não pode ser nulo");
        }

        String normalized = legacyName.toLowerCase().trim();

        for (SensorType type : values()) {
            if (type.legacyName.equals(normalized)) {
                return type;
            }
        }

        // Tentar variações comuns
        switch (normalized) {
            case "temp":
            case "temperature":
                return TEMPERATURE;
            case "presence":
            case "motion":
            case "movimento":
                return PRESENCE;
            case "luz":
            case "light":
                return LIGHT;
            case "humidity":
            case "humid":
                return HUMIDITY;
            case "pressure":
            case "press":
                return PRESSURE;
            case "air":
            case "quality":
                return AIR_QUALITY;
            case "noise":
            case "audio":
                return SOUND;
            default:
                throw new IllegalArgumentException("Tipo de sensor desconhecido: " + legacyName);
        }
    }

    /**
     * Obtém todos os tipos básicos (implementados no sistema atual).
     */
    public static SensorType[] getBasicTypes() {
        return new SensorType[] {
                TEMPERATURE, PRESENCE, LIGHT, HUMIDITY
        };
    }

    /**
     * Obtém todos os tipos avançados (para futuras implementações).
     */
    public static SensorType[] getAdvancedTypes() {
        return new SensorType[] {
                PRESSURE, AIR_QUALITY, SOUND
        };
    }

    /**
     * Verifica se um tipo é básico (já implementado).
     */
    public boolean isBasicType() {
        return this == TEMPERATURE || this == PRESENCE ||
                this == LIGHT || this == HUMIDITY;
    }

    /**
     * Verifica se um tipo é avançado (implementação futura).
     */
    public boolean isAdvancedType() {
        return !isBasicType();
    }

    /**
     * Obtém informações detalhadas do tipo como string formatada.
     */
    public String getDetailedInfo() {
        return String.format(
                "%s (%s)\n" +
                        "Unidade: %s\n" +
                        "Faixa típica: %s\n" +
                        "Descrição: %s\n" +
                        "Implementado: %s",
                displayName, legacyName,
                unit.isEmpty() ? "N/A" : unit,
                getTypicalRange(),
                description,
                isBasicType() ? "Sim" : "Não");
    }

    /**
     * Representação string usando display name.
     */
    @Override
    public String toString() {
        return displayName;
    }
}