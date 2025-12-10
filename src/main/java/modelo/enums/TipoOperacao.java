package modelo.enums;

/**
 * Define os tipos de operações que podem ser realizadas em uma conta.
 * Usado pelo padrão State para verificar permissões.
 *
 * @pattern State
 * @author Pedro Henrique
 */
public enum TipoOperacao {
    /**
     * Operação de realizar consumo de água
     */
    REALIZAR_CONSUMO,

    /**
     * Operação de consumo (alias para REALIZAR_CONSUMO)
     */
    CONSUMO,

    /**
     * Operação de vincular hidrômetro (SHA) à conta
     */
    VINCULAR_SHA,

    /**
     * Operação de alterar dados da conta
     */
    ALTERAR_DADOS,

    /**
     * Operação de consultar dados da conta
     */
    CONSULTAR_DADOS
}

