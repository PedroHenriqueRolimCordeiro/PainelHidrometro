package excecoes;

/**
 * Exceção lançada quando uma operação não é permitida no estado atual da conta.
 *
 * Cenários típicos:
 * - Tentar vincular SHA em conta suspensa
 * - Tentar realizar consumo em conta inadimplente
 * - Tentar alterar dados de conta cancelada
 *
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class OperacaoNaoPermitidaException extends Exception {

    /**
     * Construtor com mensagem de erro
     */
    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }

    /**
     * Construtor com mensagem e causa
     */
    public OperacaoNaoPermitidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

