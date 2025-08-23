package main.java.com.smartroom.model;

/**
 * Interface base para todos os sensores do sistema.
 * 
 * Aplica o princípio da Inversão de Dependência (DIP):
 * - Abstrações não devem depender de detalhes
 * - Detalhes devem depender de abstrações
 * 
 * Resolve o problema do código original onde não havia abstrações.
 */
public interface Sensor {
    
    /**
     * Obtém o identificador único do sensor
     * @return ID do sensor
     */
    String getId();
    
    /**
     * Obtém o nome descritivo do sensor
     * @return Nome do sensor
     */
    String getName();
    
    /**
     * Obtém o tipo do sensor (temperatura, presenca, luminosidade)
     * @return Tipo do sensor
     */
    String getType();
    
    /**
     * Lê o valor atual do sensor
     * @return Valor lido do sensor
     */
    Object readValue();
    
    /**
     * Verifica se o sensor está ativo
     * @return true se ativo, false caso contrário
     */
    boolean isActive();
    
    /**
     * Ativa ou desativa o sensor
     * @param active Estado desejado do sensor
     */
    void setActive(boolean active);
    
    /**
     * Valida se o valor lido está dentro dos parâmetros esperados
     * @param value Valor a ser validado
     * @return true se válido, false caso contrário
     */
    boolean isValidValue(Object value);
    
    /**
     * Obtém a unidade de medida do sensor (°C, lux, etc.)
     * @return Unidade de medida
     */
    String getUnit();
}