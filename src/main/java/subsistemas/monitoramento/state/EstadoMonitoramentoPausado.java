package subsistemas.monitoramento.state;

import subsistemas.monitoramento.MonitorConsumo;

/**
 * Estado PAUSADO: Mantém configuração, mas não coleta dados.
 *
 * Operações permitidas:
 * - ❌ iniciar() - Não aplicável (use retomar)
 * - ❌ pausar() - Já está pausado
 * - ✅ retomar() - Volta ao estado INICIADO
 * - ✅ parar() - Pode parar definitivamente
 * - ❌ executarLeitura() - Bloqueada (pausado)
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoMonitoramentoPausado implements EstadoMonitoramento {

    @Override
    public void iniciar(MonitorConsumo contexto) {
        System.out.println("⚠️  Use retomar() para voltar ao estado ativo");
    }

    @Override
    public void pausar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento já está pausado");
    }

    @Override
    public void retomar(MonitorConsumo contexto) {
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoIniciado());
        System.out.println("▶️  Monitoramento retomado");
    }

    @Override
    public void parar(MonitorConsumo contexto) {
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoParado());
        System.out.println("⏹️  Monitoramento parado");
    }

    @Override
    public void executarLeitura(MonitorConsumo contexto) {
        // Estado PAUSADO: não executa leitura
        System.out.println("⏸️  Estado PAUSADO: Leitura bloqueada");
    }

    @Override
    public String getNomeEstado() {
        return "PAUSADO";
    }
}

