package subsistemas.comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Gerenciador de comandos (Invoker do padrão Command).
 *
 * Responsabilidades:
 * - Executar comandos
 * - Manter histórico de comandos executados
 * - Suportar undo (desfazer)
 * - Suportar redo (refazer)
 *
 * PADRÃO COMMAND:
 * - Invoker armazena comandos em stacks
 * - Stack de executados (para undo)
 * - Stack de desfeitos (para redo)
 * - Limpa redo após nova execução
 *
 * @pattern Command (Invoker)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class GerenciadorComandos {

    /**
     * Histórico de comandos executados (para undo).
     * PADRÃO COMMAND:
     */
    private Stack<Comando> historicoExecutados;

    /**
     * Histórico de comandos desfeitos (para redo).
     * PADRÃO COMMAND:
     */
    private Stack<Comando> historicoDesfeitos;

    /**
     * Construtor: inicializa as stacks vazias.
     */
    public GerenciadorComandos() {
        this.historicoExecutados = new Stack<>();
        this.historicoDesfeitos = new Stack<>();
    }

    /**
     * Executa um comando.
     * PADRÃO COMMAND.
     *
     * @param comando Comando a executar
     */
    public void executar(Comando comando) {
        if (comando == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }

        // Executa comando
        comando.executar();

        // Adiciona ao histórico de executados
        historicoExecutados.push(comando);

        // Limpa histórico de desfeitos (não pode refazer após nova ação)
        historicoDesfeitos.clear();

        System.out.println("✅ Comando executado: " + comando.getDescricao());
    }

    /**
     * Desfaz o último comando executado (undo).
     * PADRÃO COMMAND.
     */
    public void desfazer() {
        if (historicoExecutados.isEmpty()) {
            System.out.println("⚠️  Nada para desfazer");
            return;
        }

        // Remove do histórico de executados
        Comando comando = historicoExecutados.pop();

        // Desfaz o comando
        comando.desfazer();

        // Adiciona ao histórico de desfeitos (para redo)
        historicoDesfeitos.push(comando);

        System.out.println("↶  Comando desfeito: " + comando.getDescricao());
    }

    /**
     * Refaz o último comando desfeito (redo).
     * PADRÃO COMMAND.
     */
    public void refazer() {
        if (historicoDesfeitos.isEmpty()) {
            System.out.println("⚠️  Nada para refazer");
            return;
        }

        // Remove do histórico de desfeitos
        Comando comando = historicoDesfeitos.pop();

        // Executa novamente
        comando.executar();

        // Adiciona de volta ao histórico de executados
        historicoExecutados.push(comando);

        System.out.println("↷  Comando refeito: " + comando.getDescricao());
    }

    /**
     * Retorna o histórico completo de comandos executados.
     *
     * @return Lista de comandos (do mais antigo ao mais recente)
     */
    public List<Comando> getHistorico() {
        return new ArrayList<>(historicoExecutados);
    }

    /**
     * Retorna o histórico limitado aos N comandos mais recentes.
     *
     * @param limite Número máximo de comandos a retornar
     * @return Lista de comandos recentes
     */
    public List<Comando> getHistoricoLimitado(int limite) {
        List<Comando> historico = getHistorico();
        int tamanho = historico.size();

        if (tamanho <= limite) {
            return historico;
        }

        // Retorna os últimos N
        return historico.subList(tamanho - limite, tamanho);
    }

    /**
     * Verifica se há comandos para desfazer.
     *
     * @return true se há comandos, false caso contrário
     */
    public boolean podeDesfazer() {
        return !historicoExecutados.isEmpty();
    }

    /**
     * Verifica se há comandos para refazer.
     *
     * @return true se há comandos, false caso contrário
     */
    public boolean podeRefazer() {
        return !historicoDesfeitos.isEmpty();
    }

    /**
     * Limpa todo o histórico.
     */
    public void limparHistorico() {
        historicoExecutados.clear();
        historicoDesfeitos.clear();
        System.out.println("🗑️  Histórico limpo");
    }
}

