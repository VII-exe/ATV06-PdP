package main.java.com.smartroom.model;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Sensor específico para detecção de presença.
 * 
 * Estende AbstractSensor implementando o Template Method pattern:
 * - Herda comportamentos comuns da classe abstrata
 * - Implementa lógica específica de detecção de presença
 * - Implementa validação específica para valores booleanos
 * 
 * Resolve problemas do código original:
 * - Elimina hardcoding de lógica de presença
 * - Encapsula comportamento específico de detecção
 * - Permite configuração de sensibilidade
 */
public class PresenceSensor extends AbstractSensor {

    // Constantes específicas para detecção de presença
    private static final int DEFAULT_DETECTION_PROBABILITY = 30; // 30% chance
    private static final int HIGH_SENSITIVITY = 50; // 50% chance
    private static final int LOW_SENSITIVITY = 15; // 15% chance

    private final Random random;
    private int detectionProbability;
    private LocalDateTime lastDetectionTime;
    private boolean previousState;
    private int consecutiveDetections;
    private int consecutiveAbsences;

    /**
     * Construtor padrão para sensor de presença
     * 
     * @param id   Identificador único do sensor
     * @param name Nome descritivo do sensor
     */
    public PresenceSensor(String id, String name) {
        super(id, name, "presenca", "");
        this.random = new Random();
        this.detectionProbability = DEFAULT_DETECTION_PROBABILITY;
        this.lastDetectionTime = null;
        this.previousState = false;
        this.consecutiveDetections = 0;
        this.consecutiveAbsences = 0;
    }

    /**
     * Construtor com sensibilidade personalizada
     * 
     * @param id          Identificador único do sensor
     * @param name        Nome descritivo do sensor
     * @param sensitivity Sensibilidade do sensor (0-100)
     */
    public PresenceSensor(String id, String name, int sensitivity) {
        super(id, name, "presenca", "");
        this.random = new Random();
        this.detectionProbability = Math.max(0, Math.min(100, sensitivity));
        this.lastDetectionTime = null;
        this.previousState = false;
        this.consecutiveDetections = 0;
        this.consecutiveAbsences = 0;
    }

    /**
     * Implementação do Template Method para gerar valores de presença.
     * 
     * Simula detecção de presença com padrões realistas:
     * - Se havia presença, maior chance de continuar presente
     * - Se não havia presença, menor chance de detectar
     * - Evita mudanças muito bruscas de estado
     */
    @Override
    protected Object generateValue() {
        boolean currentPresence;

        // Lógica de persistência de estado (mais realística)
        if (previousState) {
            // Se havia presença, 70% chance de continuar presente
            currentPresence = random.nextInt(100) < 70;

            if (currentPresence) {
                consecutiveDetections++;
                consecutiveAbsences = 0;
            } else {
                consecutiveAbsences++;
                if (consecutiveAbsences == 1) {
                    // Primeiro ciclo sem detecção, ainda pode ser ruído
                    currentPresence = true;
                    consecutiveAbsences = 0;
                } else {
                    consecutiveDetections = 0;
                }
            }
        } else {
            // Se não havia presença, usar probabilidade configurada
            currentPresence = random.nextInt(100) < detectionProbability;

            if (currentPresence) {
                consecutiveDetections++;
                consecutiveAbsences = 0;
                lastDetectionTime = LocalDateTime.now();
            } else {
                consecutiveAbsences++;
                consecutiveDetections = 0;
            }
        }

        previousState = currentPresence;
        return currentPresence;
    }

    /**
     * Validação específica para valores de presença
     * 
     * @param value Valor a ser validado
     * @return true se o valor é um Boolean válido
     */
    @Override
    public boolean isValidValue(Object value) {
        return value instanceof Boolean;
    }

    /**
     * Obtém o estado atual de presença como Boolean
     * 
     * @return true se presença detectada, false caso contrário, null se inativo
     */
    public Boolean isPresenceDetected() {
        Object value = readValue();
        return value instanceof Boolean ? (Boolean) value : null;
    }

    /**
     * Define a sensibilidade do sensor
     * 
     * @param sensitivity Sensibilidade de 0 a 100
     */
    public void setSensitivity(int sensitivity) {
        this.detectionProbability = Math.max(0, Math.min(100, sensitivity));
    }

    /**
     * Obtém a sensibilidade atual do sensor
     * 
     * @return Sensibilidade de 0 a 100
     */
    public int getSensitivity() {
        return detectionProbability;
    }

    /**
     * Obtém o tempo da última detecção de presença
     * 
     * @return LocalDateTime da última detecção ou null se nunca detectou
     */
    public LocalDateTime getLastDetectionTime() {
        return lastDetectionTime;
    }

    /**
     * Verifica se há atividade recente (detecção nos últimos 5 minutos)
     * 
     * @return true se há atividade recente
     */
    public boolean hasRecentActivity() {
        if (lastDetectionTime == null) {
            return false;
        }
        return lastDetectionTime.isAfter(LocalDateTime.now().minusMinutes(5));
    }

    /**
     * Obtém o número de detecções consecutivas
     * 
     * @return Número de detecções consecutivas
     */
    public int getConsecutiveDetections() {
        return consecutiveDetections;
    }

    /**
     * Obtém o número de ausências consecutivas
     * 
     * @return Número de ausências consecutivas
     */
    public int getConsecutiveAbsences() {
        return consecutiveAbsences;
    }

    /**
     * Verifica se o ambiente está vazio por muito tempo (10+ ausências
     * consecutivas)
     * 
     * @return true se ambiente aparentemente vazio
     */
    public boolean isLongTermEmpty() {
        return consecutiveAbsences >= 10;
    }

    /**
     * Verifica se há alta atividade (5+ detecções consecutivas)
     * 
     * @return true se alta atividade detectada
     */
    public boolean isHighActivity() {
        return consecutiveDetections >= 5;
    }

    /**
     * Redefine o sensor para sensibilidade alta
     */
    public void setHighSensitivity() {
        this.detectionProbability = HIGH_SENSITIVITY;
    }

    /**
     * Redefine o sensor para sensibilidade baixa
     */
    public void setLowSensitivity() {
        this.detectionProbability = LOW_SENSITIVITY;
    }

    /**
     * Redefine o sensor para sensibilidade padrão
     */
    public void setDefaultSensitivity() {
        this.detectionProbability = DEFAULT_DETECTION_PROBABILITY;
    }

    /**
     * Força uma detecção de presença (útil para testes)
     */
    public void forceDetection() {
        this.lastValue = true;
        this.lastReading = LocalDateTime.now();
        this.lastDetectionTime = LocalDateTime.now();
        this.previousState = true;
        this.consecutiveDetections++;
        this.consecutiveAbsences = 0;
    }

    /**
     * Limpa o histórico de detecções (reset do sensor)
     */
    public void resetHistory() {
        this.lastDetectionTime = null;
        this.previousState = false;
        this.consecutiveDetections = 0;
        this.consecutiveAbsences = 0;
    }

    /**
     * Representação específica para sensor de presença
     */
    @Override
    public String toString() {
        String status = "";
        if (lastValue instanceof Boolean) {
            boolean presence = (Boolean) lastValue;
            if (presence) {
                if (isHighActivity()) {
                    status = " [ALTA ATIVIDADE]";
                } else {
                    status = " [PRESENÇA DETECTADA]";
                }
            } else {
                if (isLongTermEmpty()) {
                    status = " [AMBIENTE VAZIO]";
                } else {
                    status = " [SEM PRESENÇA]";
                }
            }
        }

        return super.toString() + status +
                String.format(" (Sensibilidade: %d%%, Consecutivas: +%d/-%d)",
                        detectionProbability, consecutiveDetections, consecutiveAbsences);
    }
}