package main.java.com.smartroom.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementação específica para dispositivos de ventilação.
 * 
 * Implementa a interface Device com comportamentos específicos de ventiladores:
 * - Controle de velocidade (múltiplas velocidades)
 * - Diferentes tipos de ventilador (teto, mesa, torre)
 * - Oscilação automática
 * - Monitoramento de temperatura de operação
 * - Timer automático
 * 
 * Resolve problemas do código original:
 * - Eliminação de variáveis booleanas globais (fanStatus)
 * - Encapsulamento de lógica específica de ventilação
 * - Possibilidade de múltiplos ventiladores independentes
 */
public class Fan implements Device {

    // Tipos de ventilador suportados
    public enum FanType {
        CEILING(75, 3, "Teto"), // 75W, 3 velocidades
        DESK(45, 3, "Mesa"), // 45W, 3 velocidades
        TOWER(65, 5, "Torre"), // 65W, 5 velocidades
        INDUSTRIAL(150, 3, "Industrial"); // 150W, 3 velocidades

        private final int maxWatts;
        private final int maxSpeed;
        private final String description;

        FanType(int maxWatts, int maxSpeed, String description) {
            this.maxWatts = maxWatts;
            this.maxSpeed = maxSpeed;
            this.description = description;
        }

        public int getMaxWatts() {
            return maxWatts;
        }

        public int getMaxSpeed() {
            return maxSpeed;
        }

        public String getDescription() {
            return description;
        }
    }

    // Direções de oscilação
    public enum OscillationMode {
        OFF("Parado"),
        HORIZONTAL("Horizontal"),
        VERTICAL("Vertical"),
        BOTH("Horizontal + Vertical");

        private final String description;

        OscillationMode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String id;
    private final String name;
    private final FanType fanType;

    private boolean isOn;
    private int speed; // 1 até maxSpeed
    private OscillationMode oscillation;
    private boolean isAvailable;
    private String lastError;
    private LocalDateTime lastOperation;
    private LocalDateTime timerEndTime;
    @SuppressWarnings("unused")
    private int totalOperatingHours;
    private double operatingTemperature;

    /**
     * Construtor padrão para ventilador de teto
     */
    public Fan(String id, String name) {
        this(id, name, FanType.CEILING);
    }

    /**
     * Construtor com tipo específico de ventilador
     */
    public Fan(String id, String name, FanType fanType) {
        this.id = id;
        this.name = name;
        this.fanType = fanType;
        this.isOn = false;
        this.speed = 1; // Velocidade mínima por padrão
        this.oscillation = OscillationMode.OFF;
        this.isAvailable = true;
        this.lastError = null;
        this.lastOperation = LocalDateTime.now();
        this.timerEndTime = null;
        this.totalOperatingHours = 0;
        this.operatingTemperature = 25.0; // Temperatura ambiente inicial
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
        return "ventilador";
    }

    @Override
    public boolean turnOn() {
        return turnOn(this.speed);
    }

    /**
     * Liga o ventilador em uma velocidade específica
     */
    public boolean turnOn(int speed) {
        if (!isAvailable) {
            lastError = "Ventilador não disponível - possível problema no motor";
            return false;
        }

        if (speed < 1 || speed > fanType.getMaxSpeed()) {
            lastError = String.format("Velocidade inválida. Use 1 a %d", fanType.getMaxSpeed());
            return false;
        }

        if (!isOn) {
            isOn = true;
            lastOperation = LocalDateTime.now();
            lastError = null;

            // Simular aquecimento gradual do motor
            operatingTemperature = 25.0 + (speed * 5); // Temperatura aumenta com velocidade
        }

        this.speed = speed;

        // Verificar superaquecimento
        if (operatingTemperature > 80.0) {
            isOn = false;
            isAvailable = false;
            lastError = "Motor superaquecido - ventilador desligado por segurança";
            return false;
        }

        return true;
    }

    @Override
    public boolean turnOff() {
        if (isOn) {
            isOn = false;
            lastOperation = LocalDateTime.now();
            timerEndTime = null; // Cancela timer se ativo
            lastError = null;

            // Simular resfriamento gradual
            operatingTemperature = Math.max(25.0, operatingTemperature - 10.0);
        }
        return true;
    }

    @Override
    public boolean isOn() {
        // Verificar se timer expirou
        if (isOn && timerEndTime != null && LocalDateTime.now().isAfter(timerEndTime)) {
            turnOff();
            return false;
        }

        return isOn && isAvailable;
    }

    @Override
    public String getStatus() {
        if (!isAvailable) {
            return "INDISPONÍVEL (" + lastError + ")";
        }

        if (!isOn) {
            return "DESLIGADO";
        }

        String speedInfo = String.format("Velocidade %d/%d", speed, fanType.getMaxSpeed());
        String oscillationInfo = oscillation != OscillationMode.OFF ? " - Oscilação " + oscillation.getDescription()
                : "";
        String timerInfo = timerEndTime != null ? " - Timer ativo" : "";

        return "LIGADO (" + speedInfo + oscillationInfo + timerInfo + ")";
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public int getCurrentPowerConsumption() {
        if (!isOn || !isAvailable) {
            return 0;
        }

        // Consumo baseado na velocidade (velocidade máxima = consumo máximo)
        double speedRatio = (double) speed / fanType.getMaxSpeed();
        int basePower = (int) (fanType.getMaxWatts() * speedRatio);

        // Adicionar consumo extra se oscilação estiver ativa
        int oscillationPower = oscillation != OscillationMode.OFF ? 5 : 0;

        return basePower + oscillationPower;
    }

    @Override
    public int getMaxPowerConsumption() {
        return fanType.getMaxWatts() + 5; // +5W para oscilação
    }

    @Override
    public boolean performSelfDiagnostic() {
        // Simular diagnóstico do motor
        boolean motorOk = operatingTemperature < 70.0;
        boolean speedOk = speed <= fanType.getMaxSpeed();
        boolean oscillationOk = oscillation != null;

        boolean diagnostic = motorOk && speedOk && oscillationOk && isAvailable;

        if (!diagnostic) {
            if (!motorOk) {
                lastError = "Temperatura do motor muito alta";
            } else if (!speedOk) {
                lastError = "Velocidade fora dos limites";
            } else {
                lastError = "Falha no sistema de oscilação";
            }
        }

        return diagnostic;
    }

    @Override
    public String getLastError() {
        return lastError;
    }

    @Override
    public void reset() {
        if (!isAvailable) {
            // Simular manutenção/reparo
            isAvailable = true;
            totalOperatingHours = 0;
            operatingTemperature = 25.0;
            lastError = null;
            isOn = false;
            speed = 1;
            oscillation = OscillationMode.OFF;
            timerEndTime = null;
        }
    }

    /**
     * Define a velocidade do ventilador
     */
    public boolean setSpeed(int speed) {
        if (!isAvailable) {
            lastError = "Ventilador não disponível";
            return false;
        }

        if (speed < 1 || speed > fanType.getMaxSpeed()) {
            lastError = String.format("Velocidade deve estar entre 1 e %d", fanType.getMaxSpeed());
            return false;
        }

        this.speed = speed;
        lastOperation = LocalDateTime.now();

        // Se ventilador estava desligado, ligar
        if (!isOn) {
            return turnOn(speed);
        }

        // Ajustar temperatura baseada na nova velocidade
        operatingTemperature = 25.0 + (speed * 5);

        return true;
    }

    /**
     * Obtém a velocidade atual
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Define o modo de oscilação
     */
    public boolean setOscillation(OscillationMode mode) {
        if (!isAvailable) {
            lastError = "Ventilador não disponível";
            return false;
        }

        this.oscillation = mode;
        lastOperation = LocalDateTime.now();
        return true;
    }

    /**
     * Obtém o modo de oscilação atual
     */
    public OscillationMode getOscillation() {
        return oscillation;
    }

    /**
     * Obtém o tipo do ventilador
     */
    public FanType getFanType() {
        return fanType;
    }

    /**
     * Define timer para desligar automaticamente
     */
    public boolean setTimer(int minutes) {
        if (!isAvailable) {
            lastError = "Ventilador não disponível";
            return false;
        }

        if (minutes <= 0) {
            timerEndTime = null; // Cancela timer
        } else {
            timerEndTime = LocalDateTime.now().plusMinutes(minutes);
        }

        return true;
    }

    /**
     * Obtém tempo restante do timer em minutos
     */
    public long getTimerRemainingMinutes() {
        if (timerEndTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(timerEndTime)) {
            return 0;
        }

        return java.time.Duration.between(now, timerEndTime).toMinutes();
    }

    /**
     * Aumenta velocidade em 1 nível
     */
    public boolean increaseSpeed() {
        if (speed < fanType.getMaxSpeed()) {
            return setSpeed(speed + 1);
        }
        return false;
    }

    /**
     * Diminui velocidade em 1 nível
     */
    public boolean decreaseSpeed() {
        if (speed > 1) {
            return setSpeed(speed - 1);
        } else {
            return turnOff();
        }
    }

    /**
     * Obtém a temperatura de operação do motor
     */
    public double getOperatingTemperature() {
        return operatingTemperature;
    }

    /**
     * Verifica se motor está superaquecendo
     */
    public boolean isOverheating() {
        return operatingTemperature > 70.0;
    }

    /**
     * Ativa modo turbo (velocidade máxima + oscilação)
     */
    public boolean enableTurboMode() {
        if (!isAvailable) {
            return false;
        }

        boolean speedOk = setSpeed(fanType.getMaxSpeed());
        boolean oscillationOk = setOscillation(OscillationMode.HORIZONTAL);

        return speedOk && oscillationOk;
    }

    /**
     * Obtém informações detalhadas sobre o dispositivo
     */
    public String getDetailedInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return String.format(
                "Ventilador: %s [%s]\n" +
                        "Tipo: %s (%dW máx)\n" +
                        "Status: %s\n" +
                        "Velocidade: %d/%d\n" +
                        "Oscilação: %s\n" +
                        "Consumo Atual: %dW\n" +
                        "Temperatura Motor: %.1f°C\n" +
                        "Timer: %s\n" +
                        "Última Operação: %s\n" +
                        "Disponível: %s%s",
                name, id,
                fanType.getDescription(), fanType.getMaxWatts(),
                getStatus(),
                speed, fanType.getMaxSpeed(),
                oscillation.getDescription(),
                getCurrentPowerConsumption(),
                operatingTemperature,
                timerEndTime != null ? getTimerRemainingMinutes() + " min restantes" : "Inativo",
                lastOperation.format(formatter),
                isAvailable ? "Sim" : "Não",
                lastError != null ? " (Erro: " + lastError + ")" : "");
    }

    /**
     * Representação textual do ventilador
     */
    @Override
    public String toString() {
        String status = isOn ? String.format("LIGADO (V%d)", speed) : "DESLIGADO";
        String availability = isAvailable ? "" : " [INDISPONÍVEL]";
        String oscillationInfo = oscillation != OscillationMode.OFF && isOn ? " + Oscilação" : "";
        String timerInfo = timerEndTime != null && isOn ? String.format(" (Timer: %dmin)", getTimerRemainingMinutes())
                : "";

        return String.format("%s [%s]: %s%s%s%s (%s, %dW)",
                name, id, status, oscillationInfo, timerInfo, availability,
                fanType.getDescription(), fanType.getMaxWatts());
    }

    /**
     * Comparação baseada no ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Fan fan = (Fan) obj;
        return id.equals(fan.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}