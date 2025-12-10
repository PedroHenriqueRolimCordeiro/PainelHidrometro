package subsistemas.monitoramento.state;

import subsistemas.monitoramento.MonitorConsumo;

/**
 * Estado ERRO: Falha persistente na leitura (após múltiplas tentativas).
 *
 * Operações permitidas:
 * - ✅ iniciar() - Pode tentar reiniciar
 * - ❌ pausar() - Não está ativo
 * - ❌ retomar() - Não está pausado
 * - ✅ parar() - Pode parar definitivamente
 * - ❌ executarLeitura() - Bloqueada (estado de erro)
 *
 * Transição para ERRO:
 * - Após 3 falhas consecutivas de leitura
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoMonitoramentoErro implements EstadoMonitoramento {

    private String motivoErro;

    /**
     * Construtor com motivo do erro.
     *
     * @param motivoErro Descrição do erro
     */
    public EstadoMonitoramentoErro(String motivoErro) {
        this.motivoErro = motivoErro;
    }

    /**
     * Construtor padrão.
     */
    public EstadoMonitoramentoErro() {
        this("Falhas persistentes na leitura");
    }

    @Override
    public void iniciar(MonitorConsumo contexto) {
        // Permite reiniciar após erro (tentativa de recuperação)
        contexto.resetarContadorFalhas();
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoIniciado());
        System.out.println("🔄 Monitoramento reiniciado após erro");
    }

    @Override
    public void pausar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento em ERRO não pode ser pausado");
    }

    @Override
    public void retomar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento em ERRO. Use iniciar() para tentar recuperar");
    }

    @Override
    public void parar(MonitorConsumo contexto) {
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoParado());
        System.out.println("⏹️  Monitoramento parado (estava em erro)");
    }

    @Override
    public void executarLeitura(MonitorConsumo contexto) {
        // Estado ERRO: não executa leitura
        System.out.println("❌ Estado ERRO: Leitura bloqueada (" + motivoErro + ")");
    }

    @Override
    public String getNomeEstado() {
        return "ERRO";
    }

    /**
     * Retorna o motivo do erro.
     */
    public String getMotivoErro() {
        return motivoErro;
    }
}

