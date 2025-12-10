package modelo.enums;

/**
 * Define os tipos de notificação disponíveis no sistema.
 * Usado pelo padrão Strategy para permitir múltiplas formas de notificação.
 *
 * @pattern Strategy
 * @author Pedro Henrique
 */
public enum TipoNotificacao {
    /**
     * Notificação por email
     */
    EMAIL,

    /**
     * Notificação por SMS
     */
    SMS,

    /**
     * Notificação push
     */
    PUSH,

    /**
     * Notificação interna para concessionária
     */
    CONCESSIONARIA,

    /**
     * Notificação para painel interno do sistema
     */
    PAINEL_INTERNO
}

