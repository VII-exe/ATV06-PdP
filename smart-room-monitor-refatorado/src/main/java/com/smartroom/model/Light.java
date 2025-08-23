package main.java.com.smartroom.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementação específica para dispositivos de iluminação.
 * 
 * Implementa a interface Device com comportamentos específicos de lâmpadas:
 * - Controle de intensidade (dimmer)
 * - Diferentes tipos de lâmpada (LED, incandescente, fluorescente)
 * - Monitoramento de vida útil
 * - Consumo energético baseado no tipo
 * 
 * Resolve problemas do código original:
 * - Eliminação de variáveis booleanas globais (lightStatus)
 * - Encapsulamento de lógica específica de iluminação
 * - Possibilidade de múltiplas luzes independentes
 */
public class Light implements Device {

    // Tipos de lâmpada suportados
    public enum LightType {
        LED(9, 25000), // 9W, 25000 horas
        INCANDESCENT(60, 1000), // 60W, 1000 horas
        FLUORESCENT(15, 8000), // 15W, 8000 horas
        HALOGEN(42, 2000); // 42W, 2000 horas

        private final int watts;
        private final int lifespanHours;

        LightType(int watts, int lifespanHours) {
            this.watts = watts;
            this.lifespanHours = lifespanHours;
        }

        public int getWatts() {
            return watts;
        }

        public int getLifespanHours() {
            return lifespanHours;
        }
    }

    private final String id;
    private final String name;
    private final LightType lightType;

    private boolean isOn;
    private int intensity; // 0-100%
    private boolean isAvailable;
    private String lastError;
    private LocalDateTime lastOperation;
    private int totalOperatingHours;
    private int switchCount;

    /**
     * Construtor padrão para luz LED
     */
    public Light(String id, String name) {
        this(id, name, LightType.LED);
    }

    /**
     * Construtor com tipo específico de lâmpada
     */
    public Light(String id, String name, LightType lightType) {
        this.id = id;
        this.name = name;
        this.lightType = lightType;
        this.isOn = false;
        this.intensity = 100; // Intensidade máxima por padrão
        this.isAvailable = true;
        this.lastError = null;
        this.lastOperation = LocalDateTime.now();
        this.totalOperatingHours = 0;
        this.switchCount = 0;
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
        return "luz";
    }

    @Override
    public boolean turnOn() {
        if (!isAvailable) {
            lastError = "Dispositivo não disponível - possível falha na lâmpada";
            return false;
        }

        if (!isOn) {
            isOn = true;
            lastOperation = LocalDateTime.now();
            switchCount++;
            lastError = null;

            // Simular possível falha após muitos ciclos
            if (switchCount > lightType.getLifespanHours() / 10) {
                // 10% chance de falha após muitos usos
                if (Math.random() < 0.1) {
                    isAvailable = false;
                    isOn = false;
                    lastError = "Lâmpada queimou - fim da vida útil";
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean turnOff() {
        if (isOn) {
            isOn = false;
            lastOperation = LocalDateTime.now();
            switchCount++;
            lastError = null;
        }
        return true;
    }

    @Override
    public boolean isOn() {
        return isOn && isAvailable;
    }

    @Override
    public String getStatus() {
        if (!isAvailable) {
            return "INDISPONÍVEL (" + lastError + ")";
        }

        if (isOn) {
            return String.format("LIGADA (%d%% intensidade)", intensity);
        } else {
            return "DESLIGADA";
        }
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

        // Consumo baseado no tipo e intensidade
        return (int) (lightType.getWatts() * (intensity / 100.0));
    }

    @Override
    public int getMaxPowerConsumption() {
        return lightType.getWatts();
    }

    @Override
    public boolean performSelfDiagnostic() {
        // Simular diagnóstico
        boolean diagnostic = isAvailable && (switchCount < lightType.getLifespanHours() / 5);

        if (!diagnostic && isAvailable) {
            lastError = "Diagnóstico indica desgaste excessivo";
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
            // Simular troca de lâmpada
            isAvailable = true;
            totalOperatingHours = 0;
            switchCount = 0;
            lastError = null;
            isOn = false;
            intensity = 100;
        }
    }

    /**
     * Define a intensidade da luz (dimmer)
     * 
     * @param intensity Intensidade de 0 a 100
     * @return true se operação bem-sucedida
     */
    public boolean setIntensity(int intensity) {
        if (!isAvailable) {
            lastError = "Não é possível ajustar intensidade - dispositivo indisponível";
            return false;
        }

        this.intensity = Math.max(0, Math.min(100, intensity));
        lastOperation = LocalDateTime.now();

        // Se intensidade é 0, desligar a luz
        if (this.intensity == 0) {
            isOn = false;
        } else if (!isOn) {
            // Se intensidade > 0 e luz estava desligada, ligar
            turnOn();
        }

        return true;
    }

    /**
     * Obtém a intensidade atual da luz
     */
    public int getIntensity() {
        return intensity;
    }

    /**
     * Obtém o tipo de lâmpada
     */
    public LightType getLightType() {
        return lightType;
    }

    /**
     * Obtém o número total de vezes que a luz foi ligada/desligada
     */
    public int getSwitchCount() {
        return switchCount;
    }

    /**
     * Obtém as horas totais de operação estimadas
     */
    public int getTotalOperatingHours() {
        return totalOperatingHours;
    }

    /**
     * Calcula a vida útil restante em porcentagem
     */
    public double getRemainingLifePercentage() {
        int maxSwitches = lightType.getLifespanHours() / 2; // Estimativa de ciclos
        return Math.max(0, 100.0 * (maxSwitches - switchCount) / maxSwitches);
    }

    /**
     * Verifica se a lâmpada está próxima do fim da vida útil
     */
    public boolean isNearEndOfLife() {
        return getRemainingLifePercentage() < 20.0;
    }

    /**
     * Simula modo economia de energia (reduz intensidade)
     */
    public boolean enableEcoMode() {
        if (!isOn || !isAvailable) {
            return false;
        }

        // Reduz intensidade para 70%
        return setIntensity(70);
    }

    /**
     * Volta à intensidade máxima
     */
    public boolean disableEcoMode() {
        return setIntensity(100);
    }

    /**
     * Obtém informações detalhadas sobre o dispositivo
     */
    public String getDetailedInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return String.format(
                "Luz: %s [%s]\n" +
                        "Tipo: %s (%dW)\n" +
                        "Status: %s\n" +
                        "Intensidade: %d%%\n" +
                        "Consumo Atual: %dW\n" +
                        "Vida Útil Restante: %.1f%%\n" +
                        "Ciclos de Uso: %d\n" +
                        "Última Operação: %s\n" +
                        "Disponível: %s%s",
                name, id,
                lightType.name(), lightType.getWatts(),
                getStatus(),
                intensity,
                getCurrentPowerConsumption(),
                getRemainingLifePercentage(),
                switchCount,
                lastOperation.format(formatter),
                isAvailable ? "Sim" : "Não",
                lastError != null ? " (Erro: " + lastError + ")" : "");
    }

    /**
     * Representação textual da luz
     */
    @Override
    public String toString() {
        String status = isOn ? "LIGADA" : "DESLIGADA";
        String availability = isAvailable ? "" : " [INDISPONÍVEL]";
        String intensityInfo = isOn ? String.format(" (%d%%)", intensity) : "";

        return String.format("%s [%s]: %s%s%s (%s, %dW)",
                name, id, status, intensityInfo, availability,
                lightType.name(), lightType.getWatts());
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

        Light light = (Light) obj;
        return id.equals(light.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}