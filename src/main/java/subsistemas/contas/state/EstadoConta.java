package subsistemas.contas.state;

import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import excecoes.OperacaoNaoPermitidaException;

/**
 * Interface State para gerenciamento de estados da conta de água.
 * 
 * Cada estado define quais operações são permitidas, eliminando
 * a necessidade de múltiplos if-else espalhados pelo código.
 * 
 * Estados possíveis (Seção 3.1.4):
 * - Ativa: Permite todas as operações normais
 * - Suspensa: Permite apenas consultas; consumo bloqueado
 * - Inadimplente: Permite pagamento para regularização; consumo bloqueado
 * - Cancelada: Apenas consulta histórica permitida
 * 
 * PADRÃO STATE:
 * - Interface define operações comuns
 * - Cada estado concreto implementa comportamento específico
 * - Contexto (ContaAgua) delega para o estado atual
 * - Estados podem mudar dinamicamente
 * 
 * @pattern State (Interface)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public interface EstadoConta {
    
    /**
     * Verifica se pode realizar consumo de água neste estado.
     * 
     * @return true se permitido, false caso contrário
     */
    boolean podeRealizarConsumo();
    
    /**
     * Verifica se pode vincular SHA neste estado.
     * 
     * @return true se permitido, false caso contrário
     */
    boolean podeVincularSHA();
    
    /**
     * Verifica se pode alterar dados da conta neste estado.
     * 
     * @return true se permitido, false caso contrário
     */
    boolean podeAlterarDados();
    
    /**
     * Altera o estado da conta.
     * Valida se a transição é permitida.
     * 
     * @param conta Conta de água
     * @param novoEstado Novo estado desejado
     * @throws OperacaoNaoPermitidaException Se transição não permitida
     */
    void alterarPara(ContaAgua conta, TipoEstadoConta novoEstado) 
            throws OperacaoNaoPermitidaException;
    
    /**
     * Retorna o nome do estado.
     * 
     * @return Nome do estado (ATIVA, SUSPENSA, INADIMPLENTE, CANCELADA)
     */
    String getNomeEstado();
}

