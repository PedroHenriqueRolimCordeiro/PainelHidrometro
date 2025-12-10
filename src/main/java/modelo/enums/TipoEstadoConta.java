package modelo.enums;

/**
 * Define os possíveis estados de uma conta de água.
 * Usado pelo padrão State para gerenciar comportamento da conta.
 *
 * @pattern State
 * @author Pedro Henrique
 */
public enum TipoEstadoConta {
    /**
     * Conta ativa: permite todas as operações normais
     */
    ATIVA,

    /**
     * Conta suspensa: permite apenas consultas, consumo bloqueado
     */
    SUSPENSA,

    /**
     * Conta inadimplente: permite pagamento para regularização, consumo bloqueado
     */
    INADIMPLENTE,

    /**
     * Conta cancelada: apenas consulta histórica permitida
     */
    CANCELADA
}

