package excecoes;

/**
 * Exceção lançada quando ocorre erro na leitura de imagem do hidrômetro (SHA).
 *
 * Cenários típicos:
 * - Arquivo de imagem não encontrado
 * - Arquivo corrompido ou ilegível
 * - Formato de imagem inválido
 * - Erro no processamento OCR
 *
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class ErroDeLeituraImagemException extends Exception {

    /**
     * Construtor com mensagem de erro
     */
    public ErroDeLeituraImagemException(String mensagem) {
        super(mensagem);
    }

    /**
     * Construtor com mensagem e causa
     */
    public ErroDeLeituraImagemException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

