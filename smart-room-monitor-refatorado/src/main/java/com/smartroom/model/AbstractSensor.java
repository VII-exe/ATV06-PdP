package main.java.com.smartroom.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe abstrata que implementa comportamentos comuns a todos os sensores.
 * 
 * Aplica o Template Method pattern implicitamente:
 * - Define estrutura comum para sensores
 * - Permite especialização em subclasses
 * 
 * Resolve os problemas do código original:
 * - Eliminação de código duplicado
 * - Encapsulamento adequado
 * - Responsabilidades bem definidas
 */
public abstract class AbstractSensor implements Sensor {

    protected final String id;
    protected final String name;
    protected final String type;
    protected final String unit;
    protected boolean active;
    protected Object lastValue;
    protected LocalDateTime lastReading;

    /**
     * Construtor protegido para sensores
     */
    protected AbstractSensor(String id, String name, String type, String unit) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unit = unit;
        this.active = true;
        this.lastValue = null;
        this.lastReading = null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Object readValue() {
        if (!active) {
            return null;
        }

        Object value = generateValue();

        if (isValidValue(value)) {
            this.lastValue = value;
            this.lastReading = LocalDateTime.now();
            return value;
        }

        return lastValue; // Retorna último valor válido
    }

    /**
     * Método abstrato que deve ser implementado por cada tipo de sensor
     * para gerar seus valores específicos.
     * 
     * Template Method: Define o algoritmo, subclasses implementam detalhes
     */
    protected abstract Object generateValue();

    /**
     * Obtém o último valor lido
     */
    public Object getLastValue() {
        return lastValue;
    }

    /**
     * Obtém o timestamp da última leitura
     */
    public LocalDateTime getLastReading() {
        return lastReading;
    }

    /**
     * Verifica se o sensor tem uma leitura válida recente (últimos 30 segundos)
     */
    public boolean hasRecentReading() {
        if (lastReading == null) {
            return false;
        }
        return lastReading.isAfter(LocalDateTime.now().minusSeconds(30));
    }

    /**
     * Representação textual do sensor
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String timestamp = lastReading != null ? lastReading.format(formatter) : "Nunca";

        return String.format("%s [%s]: %s %s (Ativo: %s, Última leitura: %s)",
                name, id, lastValue, unit, active, timestamp);
    }

    /**
     * Comparação de sensores por ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        AbstractSensor sensor = (AbstractSensor) obj;
        return id.equals(sensor.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}