package subsistemas.monitoramento.state;

import subsistemas.monitoramento.MonitorConsumo;

/**
 * Estado PARADO: Monitoramento inativo.
 *
 * Operações permitidas:
 * - ✅ iniciar() - Pode iniciar novamente
 * - ❌ pausar() - Não está ativo
 * - ❌ retomar() - Não está pausado
 * - ❌ parar() - Já está parado
 * - ❌ executarLeitura() - Bloqueada (inativo)
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoMonitoramentoParado implements EstadoMonitoramento {

    @Override
    public void iniciar(MonitorConsumo contexto) {
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoIniciado());
        System.out.println("▶️  Monitoramento iniciado");
    }

    @Override
    public void pausar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento não está ativo");
    }

    @Override
    public void retomar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento não está pausado. Use iniciar()");
    }

    @Override
    public void parar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento já está parado");
    }

    @Override
    public void executarLeitura(MonitorConsumo contexto) {
        // Estado PARADO: não executa leitura
        System.out.println("⏹️  Estado PARADO: Leitura bloqueada");
    }

    @Override
    public String getNomeEstado() {
        return "PARADO";
    }
}

