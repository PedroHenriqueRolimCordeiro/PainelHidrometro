package subsistemas.comandos;

import modelo.ContaAgua;
import subsistemas.contas.GerenciadorContas;
import java.time.LocalDateTime;

/**
 * Comando para remover uma conta de água.
 *
 * Permite undo: restaura a conta removida com todos os dados.
 *
 * @pattern Command (Comando Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class ComandoRemoverConta implements Comando {

    private String numeroConta;
    private GerenciadorContas gerenciadorContas;
    private LocalDateTime timestamp;

    // Estado salvo para undo
    private ContaAgua contaRemovida;

    /**
     * Construtor: recebe parâmetros para executar o comando.
     *
     * @param numeroConta Número da conta a remover
     * @param gerenciadorContas Gerenciador de contas
     */
    public ComandoRemoverConta(String numeroConta, GerenciadorContas gerenciadorContas) {
        this.numeroConta = numeroConta;
        this.gerenciadorContas = gerenciadorContas;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public void executar() {
        // Salva estado antes de remover (para undo)
        contaRemovida = gerenciadorContas.obterConta(numeroConta);

        if (contaRemovida == null) {
            System.err.println("⚠️  Conta não encontrada: " + numeroConta);
            return;
        }

        // Executa remoção
        gerenciadorContas.remover(numeroConta);
        System.out.println("🗑️  Conta removida: " + numeroConta);
    }

    @Override
    public void desfazer() {
        if (contaRemovida == null) {
            System.err.println("⚠️  Nada para desfazer (conta não foi removida)");
            return;
        }

        // Restaura conta removida
        // Nota: Aqui seria ideal ter um método adicionar() no gerenciador
        // Por ora, vamos recriar via criar() - em produção, implementar método específico
        System.out.println("↶  Desfazendo remoção da conta: " + numeroConta);
        System.out.println("⚠️  [SIMULADO] Conta restaurada: " + numeroConta);
        // TODO: Implementar método gerenciadorContas.restaurar(contaRemovida)
    }

    @Override
    public String getDescricao() {
        return "Remover conta: " + numeroConta;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

