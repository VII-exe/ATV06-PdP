package main.java.com.smartroom.factory;

import main.java.com.smartroom.model.*;

/**
 * Interface para Factory Method Pattern na criação de sensores.
 * 
 * Resolve os anti-patterns do código original:
 * - Copy-Paste Programming: Elimina duplicação na criação de sensores
 * - Magic Strings: Substitui strings hardcoded por enums tipados
 * - God Object: Remove responsabilidade de criação do SmartRoomSystem
 * 
 * Aplica os princípios SOLID:
 * - SRP: Responsabilidade única de criar sensores
 * - OCP: Aberto para extensão (novos tipos), fechado para modificação
 * - DIP: Dependência de abstração ao invés de classes concretas
 * 
 * Padrão GOF aplicado: Factory Method
 * - Define interface para criação de objetos
 * - Permite subclasses decidir qual classe instanciar
 * - Promove loose coupling entre cliente e produtos
 */
public interface SensorFactory {

    /**
     * Método factory principal para criar sensores.
     * 
     * @param type Tipo do sensor a ser criado
     * @param id   Identificador único do sensor
     * @param name Nome descritivo do sensor
     * @return Instância do sensor criado
     * @throws IllegalArgumentException      se parâmetros inválidos
     * @throws UnsupportedOperationException se tipo não suportado
     */
    Sensor createSensor(SensorType type, String id, String name);

    /**
     * Método factory com parâmetros específicos por tipo.
     * 
     * @param type      Tipo do sensor a ser criado
     * @param id        Identificador único do sensor
     * @param name      Nome descritivo do sensor
     * @param baseValue Valor base para simulação (opcional)
     * @return Instância do sensor criado
     */
    Sensor createSensor(SensorType type, String id, String name, Object baseValue);

    /**
     * Método factory para criar sensor com configuração personalizada.
     * 
     * @param config Configuração do sensor
     * @return Instância do sensor criado
     */
    Sensor createSensor(SensorConfig config);

    /**
     * Verifica se a factory suporta um tipo específico de sensor.
     * 
     * @param type Tipo do sensor a verificar
     * @return true se suportado, false caso contrário
     */
    boolean supportsType(SensorType type);

    /**
     * Obtém todos os tipos de sensores suportados por esta factory.
     * 
     * @return Array com tipos suportados
     */
    SensorType[] getSupportedTypes();

    /**
     * Cria múltiplos sensores de uma vez.
     * 
     * @param configs Array de configurações de sensores
     * @return Array de sensores criados
     */
    default Sensor[] createMultipleSensors(SensorConfig... configs) {
        Sensor[] sensors = new Sensor[configs.length];
        for (int i = 0; i < configs.length; i++) {
            sensors[i] = createSensor(configs[i]);
        }
        return sensors;
    }

    /**
     * Classe interna para configuração de sensores.
     * Aplica o Builder Pattern para construção flexível.
     */
    class SensorConfig {
        private final String id;
        private final String name;
        private final SensorType type;
        private Object baseValue;
        private boolean active = true;
        private String location;
        private int sensitivity = 50; // Para sensores de presença

        public SensorConfig(String id, String name, SensorType type) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID do sensor não pode ser nulo ou vazio");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do sensor não pode ser nulo ou vazio");
            }
            if (type == null) {
                throw new IllegalArgumentException("Tipo do sensor não pode ser nulo");
            }

            this.id = id.trim();
            this.name = name.trim();
            this.type = type;
        }

        public SensorConfig withBaseValue(Object baseValue) {
            this.baseValue = baseValue;
            return this;
        }

        public SensorConfig withActive(boolean active) {
            this.active = active;
            return this;
        }

        public SensorConfig withLocation(String location) {
            this.location = location;
            return this;
        }

        public SensorConfig withSensitivity(int sensitivity) {
            this.sensitivity = Math.max(0, Math.min(100, sensitivity));
            return this;
        }

        // Getters
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public SensorType getType() {
            return type;
        }

        public Object getBaseValue() {
            return baseValue;
        }

        public boolean isActive() {
            return active;
        }

        public String getLocation() {
            return location;
        }

        public int getSensitivity() {
            return sensitivity;
        }

        @Override
        public String toString() {
            return String.format("SensorConfig{id='%s', name='%s', type=%s, baseValue=%s, active=%s}",
                    id, name, type, baseValue, active);
        }
    }
}