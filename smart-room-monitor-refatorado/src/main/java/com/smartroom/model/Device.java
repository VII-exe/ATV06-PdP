package main.java.com.smartroom.model;

/**
 * Interface base para todos os dispositivos controláveis do sistema.
 * 
 * Aplica os princípios SOLID:
 * - SRP: Responsabilidade única de definir contratos de dispositivos
 * - OCP: Aberto para extensão (novos tipos de dispositivos)
 * - DIP: Abstrações ao invés de implementações concretas
 * 
 * Resolve problemas do código original:
 * - Eliminação de controle hardcoded de dispositivos
 * - Criação de abstrações para dispositivos
 * - Padronização de interface de controle
 */
public interface Device {
    
    /**
     * Obtém o identificador único do dispositivo
     * @return ID do dispositivo
     */
    String getId();
    
    /**
     * Obtém o nome descritivo do dispositivo
     * @return Nome do dispositivo
     */
    String getName();
    
    /**
     * Obtém o tipo do dispositivo (luz, ventilador, ar-condicionado)
     * @return Tipo do dispositivo
     */
    String getType();
    
    /**
     * Liga o dispositivo
     * @return true se operação foi bem-sucedida
     */
    boolean turnOn();
    
    /**
     * Desliga o dispositivo
     * @return true se operação foi bem-sucedida
     */
    boolean turnOff();
    
    /**
     * Verifica se o dispositivo está ligado
     * @return true se ligado, false se desligado
     */
    boolean isOn();
    
    /**
     * Obtém o status atual do dispositivo
     * @return Status textual do dispositivo
     */
    String getStatus();
    
    /**
     * Verifica se o dispositivo está disponível/operacional
     * @return true se disponível, false se com problema
     */
    boolean isAvailable();
    
    /**
     * Obtém informações sobre consumo de energia (em watts)
     * @return Consumo atual em watts, 0 se desligado
     */
    int getCurrentPowerConsumption();
    
    /**
     * Obtém o consumo máximo de energia do dispositivo
     * @return Consumo máximo em watts
     */
    int getMaxPowerConsumption();
    
    /**
     * Executa auto-diagnóstico do dispositivo
     * @return true se dispositivo está funcionando normalmente
     */
    boolean performSelfDiagnostic();
    
    /**
     * Obtém a última mensagem de erro do dispositivo
     * @return Mensagem de erro ou null se não há erros
     */
    String getLastError();
    
    /**
     * Reseta o dispositivo para estado inicial
     */
    void reset();
}