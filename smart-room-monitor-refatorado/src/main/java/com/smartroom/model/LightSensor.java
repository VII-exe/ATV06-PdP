package main.java.com.smartroom.model;

import java.time.LocalTime;
import java.util.Random;

/**
 * Sensor específico para medição de luminosidade.
 * 
 * Estende AbstractSensor implementando o Template Method pattern:
 * - Herda comportamentos comuns da classe abstrata
 * - Implementa lógica específica de medição de luz
 * - Simula variações baseadas no horário do dia
 * 
 * Resolve problemas do código original:
 * - Elimina hardcoding de valores de luminosidade
 * - Encapsula lógica específica de medição de luz
 * - Simula comportamento realístico baseado no tempo
 */
public class LightSensor extends AbstractSensor {
    
    // Constantes específicas para luminosidade
    private static final int MIN_LUX = 0;
    private static final int MAX_LUX = 100000;
    private static final int DEFAULT_INDOOR_LUX = 400;
    
    // Níveis de referência
    private static final int DARKNESS_THRESHOLD = 50;
    private static final int DIM_LIGHT_THRESHOLD = 200;
    private static final int ADEQUATE_LIGHT_MIN = 300;
    private static final int ADEQUATE_LIGHT_MAX = 500;
    private static final int BRIGHT_LIGHT_THRESHOLD = 1000;
    private static final int VERY_BRIGHT_THRESHOLD = 10000;
    
    private final Random random;
    private int baseLuminosity;
    private boolean artificialLightInfluence;
    private double naturalLightFactor;
    
    /**
     * Construtor padrão para sensor de luminosidade
     * 
     * @param id Identificador único do sensor
     * @param name Nome descritivo do sensor
     */
    public LightSensor(String id, String name) {
        super(id, name, "luminosidade", "lux");
        this.random = new Random();
        this.baseLuminosity = DEFAULT_INDOOR_LUX;
        this.artificialLightInfluence = false;
        this.naturalLightFactor = 1.0;
    }
    
    /**
     * Construtor com luminosidade base personalizada
     * 
     * @param id Identificador único do sensor
     * @param name Nome descritivo do sensor
     * @param baseLuminosity Luminosidade base do ambiente
     */
    public LightSensor(String id, String name, int baseLuminosity) {
        super(id, name, "luminosidade", "lux");
        this.random = new Random();
        this.baseLuminosity = Math.max(MIN_LUX, Math.min(MAX_LUX, baseLuminosity));
        this.artificialLightInfluence = false;
        this.naturalLightFactor = 1.0;
    }
    
    /**
     * Implementação do Template Method para gerar valores de luminosidade.
     * 
     * Simula variações realistas de luminosidade baseadas em:
     * - Horário do dia (luz natural)
     * - Influência de luz artificial
     * - Variações aleatórias pequenas
     * - Condições climáticas simuladas
     */
    @Override
    protected Object generateValue() {
        // Calcular luz natural baseada no horário
        int naturalLight = calculateNaturalLight();
        
        // Calcular luz artificial (se influenciada)
        int artificialLight = artificialLightInfluence ? 
                            (200 + random.nextInt(300)) : 0; // 200-500 lux
        
        // Aplicar fator de luz natural (simulando condições climáticas)
        naturalLight = (int) (naturalLight * naturalLightFactor);
        
        // Combinar luzes e adicionar variação aleatória
        int totalLux = baseLuminosity + naturalLight + artificialLight;
        
        // Adicionar pequena variação aleatória (±10%)
        int variation = (int) (totalLux * 0.1 * (random.nextDouble() - 0.5));
        totalLux += variation;
        
        // Garantir limites mínimos e máximos
        totalLux = Math.max(MIN_LUX, Math.min(MAX_LUX, totalLux));
        
        return totalLux;
    }
    
    /**
     * Calcula a luz natural baseada no horário do dia
     * 
     * @return Valor de luz natural em lux
     */
    private int calculateNaturalLight() {
        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();
        
        // Converter para minutos desde meia-noite
        int totalMinutes = hour * 60 + minute;
        
        // Simular curva de luz solar
        if (totalMinutes < 360) { // 0:00 - 6:00 (noite)
            return random.nextInt(50); // 0-50 lux
            
        } else if (totalMinutes < 480) { // 6:00 - 8:00 (amanhecer)
            int progress = totalMinutes - 360; // 0-120 minutos
            return (int) (50 + (progress / 120.0) * 300); // 50-350 lux
            
        } else if (totalMinutes < 1020) { // 8:00 - 17:00 (dia)
            return 400 + random.nextInt(600); // 400-1000 lux
            
        } else if (totalMinutes < 1140) { // 17:00 - 19:00 (entardecer)
            int progress = totalMinutes - 1020; // 0-120 minutos
            return (int) (1000 - (progress / 120.0) * 800); // 1000-200 lux
            
        } else { // 19:00 - 24:00 (noite)
            return random.nextInt(100); // 0-100 lux
        }
    }
    
    /**
     * Validação específica para valores de luminosidade
     * 
     * @param value Valor a ser validado
     * @return true se o valor está em uma faixa aceitável
     */
    @Override
    public boolean isValidValue(Object value) {
        if (value == null || !(value instanceof Integer)) {
            return false;
        }
        
        int lux = (Integer) value;
        return lux >= MIN_LUX && lux <= MAX_LUX;
    }
    
    /**
     * Obtém a luminosidade atual como Integer
     * 
     * @return Luminosidade em lux ou null se inativo
     */
    public Integer getCurrentLuminosity() {
        Object value = readValue();
        return value instanceof Integer ? (Integer) value : null;
    }
    
    /**
     * Define se há influência de luz artificial
     * 
     * @param hasArtificialLight true se há luz artificial
     */
    public void setArtificialLightInfluence(boolean hasArtificialLight) {
        this.artificialLightInfluence = hasArtificialLight;
    }
    
    /**
     * Verifica se há influência de luz artificial
     * 
     * @return true se há influência de luz artificial
     */
    public boolean hasArtificialLightInfluence() {
        return artificialLightInfluence;
    }
    
    /**
     * Define o fator de luz natural (0.0 = nublado, 1.0 = normal, 1.5 = muito ensolarado)
     * 
     * @param factor Fator de luz natural
     */
    public void setNaturalLightFactor(double factor) {
        this.naturalLightFactor = Math.max(0.0, Math.min(2.0, factor));
    }
    
    /**
     * Obtém o fator de luz natural atual
     * 
     * @return Fator de luz natural
     */
    public double getNaturalLightFactor() {
        return naturalLightFactor;
    }
    
    /**
     * Define nova luminosidade base
     * 
     * @param baseLuminosity Nova luminosidade base
     */
    public void setBaseLuminosity(int baseLuminosity) {
        this.baseLuminosity = Math.max(MIN_LUX, Math.min(MAX_LUX, baseLuminosity));
    }
    
    /**
     * Obtém a luminosidade base atual
     * 
     * @return Luminosidade base
     */
    public int getBaseLuminosity() {
        return baseLuminosity;
    }
    
    /**
     * Verifica se está muito escuro (< 50 lux)
     * 
     * @return true se muito escuro
     */
    public boolean isDark() {
        if (lastValue == null || !(lastValue instanceof Integer)) {
            return false;
        }
        return (Integer) lastValue < DARKNESS_THRESHOLD;
    }
    
    /**
     * Verifica se a luz está adequada para leitura (300-500 lux)
     * 
     * @return true se adequada para leitura
     */
    public boolean isAdequateForReading() {
        if (lastValue == null || !(lastValue instanceof Integer)) {
            return false;
        }
        int lux = (Integer) lastValue;
        return lux >= ADEQUATE_LIGHT_MIN && lux <= ADEQUATE_LIGHT_MAX;
    }
    
    /**
     * Verifica se está muito claro (> 10000 lux)
     * 
     * @return true se muito claro
     */
    public boolean isVeryBright() {
        if (lastValue == null || !(lastValue instanceof Integer)) {
            return false;
        }
        return (Integer) lastValue > VERY_BRIGHT_THRESHOLD;
    }
    
    /**
     * Obtém classificação textual da luminosidade
     * 
     * @return Classificação da luminosidade atual
     */
    public String getLightLevelDescription() {
        if (lastValue == null || !(lastValue instanceof Integer)) {
            return "Desconhecido";
        }
        
        int lux = (Integer) lastValue;
        
        if (lux < DARKNESS_THRESHOLD) {
            return "Muito Escuro";
        } else if (lux < DIM_LIGHT_THRESHOLD) {
            return "Escuro";
        } else if (lux < ADEQUATE_LIGHT_MIN) {
            return "Pouca Luz";
        } else if (lux <= ADEQUATE_LIGHT_MAX) {
            return "Adequado";
        } else if (lux < BRIGHT_LIGHT_THRESHOLD) {
            return "Claro";
        } else if (lux < VERY_BRIGHT_THRESHOLD) {
            return "Muito Claro";
        } else {
            return "Extremamente Claro";
        }
    }
    
    /**
     * Simula condições climáticas
     */
    public void setWeatherCondition(String condition) {
        switch (condition.toLowerCase()) {
            case "ensolarado":
                setNaturalLightFactor(1.3);
                break;
            case "parcialmente_nublado":
                setNaturalLightFactor(0.8);
                break;
            case "nublado":
                setNaturalLightFactor(0.5);
                break;
            case "chuva":
                setNaturalLightFactor(0.3);
                break;
            default:
                setNaturalLightFactor(1.0); // Normal
        }
    }
    
    /**
     * Representação específica para sensor de luminosidade
     */
    @Override
    public String toString() {
        String description = getLightLevelDescription();
        String artificialStatus = artificialLightInfluence ? " [+LUZ ARTIFICIAL]" : "";
        String weatherStatus = naturalLightFactor != 1.0 ? 
            String.format(" [Fator Natural: %.1f]", naturalLightFactor) : "";
        
        return super.toString() + " [" + description + "]" + artificialStatus + weatherStatus;
    }
}