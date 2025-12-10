package excecoes;

/**
 * Exceção lançada quando ocorre erro no envio de notificação.
 *
 * Cenários típicos:
 * - Falha no envio de email (servidor SMTP indisponível)
 * - Falha no envio de SMS (serviço de SMS indisponível)
 * - Falha no envio de Push (serviço de Push indisponível)
 * - Configuração inválida
 *
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class NotificacaoException extends Exception {

    /**
     * Construtor com mensagem de erro
     */
    public NotificacaoException(String mensagem) {
        super(mensagem);
    }

    /**
     * Construtor com mensagem e causa
     */
    public NotificacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

