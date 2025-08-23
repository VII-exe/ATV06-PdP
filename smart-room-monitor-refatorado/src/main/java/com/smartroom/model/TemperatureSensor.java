package main.java.com.smartroom.model;

import java.util.Random;

/**
 * Sensor específico para leitura de temperatura.
 * 
 * Estende AbstractSensor implementando o Template Method pattern:
 * - Herda comportamentos comuns da classe abstrata
 * - Implementa lógica específica de geração de temperatura
 * - Implementa validação específica para valores de temperatura
 * 
 * Resolve problemas do código original:
 * - Elimina hardcoding de tipos de sensores
 * - Encapsula lógica específica de temperatura
 * - Permite extensibilidade através de herança
 */
public class TemperatureSensor extends AbstractSensor {
    
    // Constantes específicas para temperatura
    private static final double MIN_TEMPERATURE = -10.0;
    private static final double MAX_TEMPERATURE = 50.0;
    private static final double DEFAULT_TEMPERATURE = 22.0;
    
    private final Random random;
    private double baseTemperature;
    
    /**
     * Construtor para sensor de temperatura
     * 
     * @param id Identificador único do sensor
     * @param name Nome descritivo do sensor
     */
    public TemperatureSensor(String id, String name) {
        super(id, name, "temperatura", "°C");
        this.random = new Random();
        this.baseTemperature = DEFAULT_TEMPERATURE;
    }
    
    /**
     * Construtor com temperatura base personalizada
     * 
     * @param id Identificador único do sensor
     * @param name Nome descritivo do sensor
     * @param baseTemperature Temperatura base para simulação
     */
    public TemperatureSensor(String id, String name, double baseTemperature) {
        super(id, name, "temperatura", "°C");
        this.random = new Random();
        this.baseTemperature = Math.max(MIN_TEMPERATURE, 
                                Math.min(MAX_TEMPERATURE, baseTemperature));
    }
    
    /**
     * Implementação do Template Method para gerar valores de temperatura.
     * 
     * Simula variações realistas de temperatura:
     * - Variação de ±3°C em relação à temperatura base
     * - Mudanças graduais (não bruscas)
     * - Mantém valores dentro de limites realistas
     */
    @Override
    protected Object generateValue() {
        // Variação gradual de ±3°C
        double variation = (random.nextDouble() - 0.5) * 6.0; // -3.0 a +3.0
        
        // Aplicar variação à temperatura base
        double newTemperature = baseTemperature + variation;
        
        // Garantir limites mínimos e máximos
        newTemperature = Math.max(MIN_TEMPERATURE, newTemperature);
        newTemperature = Math.min(MAX_TEMPERATURE, newTemperature);
        
        // Arredondar para 1 casa decimal
        return Math.round(newTemperature * 10.0) / 10.0;
    }
    
    /**
     * Validação específica para valores de temperatura
     * 
     * @param value Valor a ser validado
     * @return true se a temperatura está em uma faixa aceitável
     */
    @Override
    public boolean isValidValue(Object value) {
        if (value == null || !(value instanceof Double)) {
            return false;
        }
        
        double temperature = (Double) value;
        return temperature >= MIN_TEMPERATURE && temperature <= MAX_TEMPERATURE;
    }
    
    /**
     * Obtém a temperatura atual como Double
     * 
     * @return Temperatura em graus Celsius ou null se inativo
     */
    public Double getCurrentTemperature() {
        Object value = readValue();
        return value instanceof Double ? (Double) value : null;
    }
    
    /**
     * Define nova temperatura base para simulação
     * 
     * @param baseTemperature Nova temperatura base
     */
    public void setBaseTemperature(double baseTemperature) {
        this.baseTemperature = Math.max(MIN_TEMPERATURE, 
                                Math.min(MAX_TEMPERATURE, baseTemperature));
    }
    
    /**
     * Obtém a temperatura base atual
     * 
     * @return Temperatura base
     */
    public double getBaseTemperature() {
        return baseTemperature;
    }
    
    /**
     * Verifica se a temperatura está em faixa crítica (muito alta ou baixa)
     * 
     * @return true se temperatura crítica
     */
    public boolean isCriticalTemperature() {
        if (lastValue == null || !(lastValue instanceof Double)) {
            return false;
        }
        
        double temp = (Double) lastValue;
        return temp <= 5.0 || temp >= 40.0;
    }
    
    /**
     * Verifica se a temperatura está em faixa confortável (18°C a 26°C)
     * 
     * @return true se temperatura confortável
     */
    public boolean isComfortableTemperature() {
        if (lastValue == null || !(lastValue instanceof Double)) {
            return false;
        }
        
        double temp = (Double) lastValue;
        return temp >= 18.0 && temp <= 26.0;
    }
    
    /**
     * Obtém constantes de temperatura mínima
     */
    public static double getMinTemperature() {
        return MIN_TEMPERATURE;
    }
    
    /**
     * Obtém constantes de temperatura máxima
     */
    public static double getMaxTemperature() {
        return MAX_TEMPERATURE;
    }
    
    /**
     * Representação específica para sensor de temperatura
     */
    @Override
    public String toString() {
        String status = "";
        if (lastValue instanceof Double) {
            @SuppressWarnings("unused")
            double temp = (Double) lastValue;
            if (isCriticalTemperature()) {
                status = " [CRÍTICA]";
            } else if (isComfortableTemperature()) {
                status = " [CONFORTÁVEL]";
            }
        }
        
        return super.toString() + status;
    }
}