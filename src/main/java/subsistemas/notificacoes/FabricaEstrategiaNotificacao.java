package subsistemas.notificacoes;

import modelo.enums.TipoNotificacao;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory para criação de estratégias de notificação.
 *
 * Encapsula a lógica de criação das estratégias, permitindo adicionar
 * novos tipos sem alterar código existente (Open/Closed Principle).
 *
 * PADRÃO FACTORY METHOD:
 * - Método de criação retorna interface/classe abstrata
 * - Esconde lógica de instanciação
 * - Facilita adição de novos produtos
 *
 * @pattern Factory Method
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class FabricaEstrategiaNotificacao {

    private EnviadorEmail enviadorEmail;

    /**
     * Construtor: recebe dependências necessárias para criar estratégias.
     *
     * @param enviadorEmail Enviador de emails (para EstrategiaEmail)
     */
    public FabricaEstrategiaNotificacao(EnviadorEmail enviadorEmail) {
        this.enviadorEmail = enviadorEmail;
    }

    /**
     * Cria uma estratégia de notificação baseada no tipo.
     *
     * FACTORY METHOD: Retorna interface EstrategiaNotificacao.
     *
     * @param tipo Tipo de notificação desejado
     * @return Estratégia correspondente
     * @throws IllegalArgumentException Se tipo desconhecido
     */
    public EstrategiaNotificacao criarEstrategia(TipoNotificacao tipo) {
        switch (tipo) {
            case EMAIL:
                return new EstrategiaNotificacaoEmail(enviadorEmail);

            case SMS:
                return new EstrategiaNotificacaoSMS();

            case PUSH:
                return new EstrategiaNotificacaoPush();

            case CONCESSIONARIA:
                return new EstrategiaNotificacaoPainelInterno();

            default:
                throw new IllegalArgumentException(
                    "Tipo de notificação desconhecido: " + tipo
                );
        }
    }

    /**
     * Cria múltiplas estratégias de uma vez.
     *
     * @param tipos Lista de tipos de notificação
     * @return Lista de estratégias correspondentes
     */
    public List<EstrategiaNotificacao> criarEstrategias(List<TipoNotificacao> tipos) {
        return tipos.stream()
                .map(this::criarEstrategia)
                .collect(Collectors.toList());
    }
}

