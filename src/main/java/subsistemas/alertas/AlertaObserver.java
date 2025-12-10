package subsistemas.alertas;

import subsistemas.monitoramento.MonitorConsumo;

/**
 * Classe abstrata Observer no padrão Observer.
 * Define o contrato para todos os observers que serão notificados pelo MonitorConsumo.
 *
 * PADRÃO OBSERVER:
 * - Observer mantém referência ao Subject
 * - Construtor recebe Subject
 * - Método abstrato update() implementado pelos observers concretos
 *
 * @pattern Observer (Observer)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public abstract class AlertaObserver {

    /**
     * Referência ao Subject (MonitorConsumo).
     * PROTECTED conforme padrão Observer do.
     */
    protected MonitorConsumo subject;

    /**
     * Construtor: recebe o Subject (obrigatório no padrão Observer).
     *
     * @param subject MonitorConsumo que será observado
     */
    public AlertaObserver(MonitorConsumo subject) {
        this.subject = subject;
    }

    /**
     * Método chamado quando o Subject notifica mudança de estado.
     * Cada observer concreto implementa sua lógica específica.
     */
    public abstract void update();
}

