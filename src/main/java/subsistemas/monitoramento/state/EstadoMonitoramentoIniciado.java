package subsistemas.monitoramento.state;

import subsistemas.monitoramento.MonitorConsumo;

/**
 * Estado INICIADO: Coleta ativa de dados em intervalos configurados.
 *
 * Operações permitidas:
 * - ❌ iniciar() - Já está iniciado
 * - ✅ pausar() - Pode pausar
 * - ❌ retomar() - Não aplicável (já está ativo)
 * - ✅ parar() - Pode parar
 * - ✅ executarLeitura() - Executa leitura normalmente
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoMonitoramentoIniciado implements EstadoMonitoramento {

    @Override
    public void iniciar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento já está iniciado");
    }

    @Override
    public void pausar(MonitorConsumo contexto) {
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoPausado());
        System.out.println("⏸️  Monitoramento pausado");
    }

    @Override
    public void retomar(MonitorConsumo contexto) {
        System.out.println("⚠️  Monitoramento já está ativo");
    }

    @Override
    public void parar(MonitorConsumo contexto) {
        contexto.setEstadoMonitoramento(new EstadoMonitoramentoParado());
        System.out.println("⏹️  Monitoramento parado");
    }

    @Override
    public void executarLeitura(MonitorConsumo contexto) {
        // Estado INICIADO: executa leitura normalmente
        // A lógica real de leitura está no MonitorConsumo
        System.out.println("📊 Estado INICIADO: Executando leitura...");
    }

    @Override
    public String getNomeEstado() {
        return "INICIADO";
    }
}

