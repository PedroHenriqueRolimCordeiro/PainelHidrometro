package subsistemas.monitoramento.state;

import subsistemas.monitoramento.MonitorConsumo;

/**
 * Interface State para gerenciamento de estados do monitoramento.
 *
 * Cada estado define comportamentos específicos, eliminando
 * if-else para verificar se pode executar leituras, pausar, etc.
 *
 * Estados possíveis (Seção 3.2.4):
 * - Iniciado: Coleta ativa de dados em intervalos configurados
 * - Pausado: Mantém configuração, mas não coleta dados
 * - Parado: Monitoramento inativo
 * - Erro: Falha persistente na leitura (após múltiplas tentativas)
 *
 * PADRÃO STATE (Marcos Brizeno):
 * - Interface define operações comuns
 * - Cada estado concreto implementa comportamento específico
 * - Contexto (MonitorConsumo) delega para o estado atual
 * - Estados podem mudar dinamicamente
 *
 * @pattern State (Interface)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public interface EstadoMonitoramento {

    /**
     * Inicia o monitoramento.
     *
     * @param contexto Contexto do monitoramento
     */
    void iniciar(MonitorConsumo contexto);

    /**
     * Pausa o monitoramento.
     *
     * @param contexto Contexto do monitoramento
     */
    void pausar(MonitorConsumo contexto);

    /**
     * Retoma o monitoramento pausado.
     *
     * @param contexto Contexto do monitoramento
     */
    void retomar(MonitorConsumo contexto);

    /**
     * Para o monitoramento.
     *
     * @param contexto Contexto do monitoramento
     */
    void parar(MonitorConsumo contexto);

    /**
     * Executa leitura de consumo.
     *
     * @param contexto Contexto do monitoramento
     */
    void executarLeitura(MonitorConsumo contexto);

    /**
     * Retorna o nome do estado.
     *
     * @return Nome do estado (INICIADO, PAUSADO, PARADO, ERRO)
     */
    String getNomeEstado();
}

