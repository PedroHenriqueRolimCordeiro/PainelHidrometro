package subsistemas.comandos;

import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import subsistemas.contas.GerenciadorContas;
import java.time.LocalDateTime;

/**
 * Comando para alterar o estado de uma conta.
 *
 * Permite undo: restaura o estado anterior.
 *
 * @pattern Command (Comando Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class ComandoAlterarEstadoConta implements Comando {

    private String numeroConta;
    private TipoEstadoConta novoEstado;
    private GerenciadorContas gerenciadorContas;
    private LocalDateTime timestamp;

    // Estado salvo para undo
    private TipoEstadoConta estadoAnterior;

    /**
     * Construtor: recebe parâmetros para executar o comando.
     *
     * @param numeroConta Número da conta
     * @param novoEstado Novo estado
     * @param gerenciadorContas Gerenciador de contas
     */
    public ComandoAlterarEstadoConta(String numeroConta, TipoEstadoConta novoEstado,
                                      GerenciadorContas gerenciadorContas) {
        this.numeroConta = numeroConta;
        this.novoEstado = novoEstado;
        this.gerenciadorContas = gerenciadorContas;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public void executar() {
        // Salva estado anterior (para undo)
        ContaAgua conta = gerenciadorContas.obterConta(numeroConta);

        if (conta == null) {
            System.err.println("⚠️  Conta não encontrada: " + numeroConta);
            return;
        }

        estadoAnterior = conta.getTipoEstado();

        // Executa alteração via State
        try {
            conta.getEstado().alterarPara(conta, novoEstado);
            System.out.println("📊 Estado alterado: " + numeroConta + " → " + novoEstado);
        } catch (Exception e) {
            System.err.println("❌ Erro ao alterar estado: " + e.getMessage());
        }
    }

    @Override
    public void desfazer() {
        if (estadoAnterior == null) {
            System.err.println("⚠️  Nada para desfazer (estado não foi alterado)");
            return;
        }

        ContaAgua conta = gerenciadorContas.obterConta(numeroConta);

        if (conta == null) {
            System.err.println("⚠️  Conta não encontrada: " + numeroConta);
            return;
        }

        // Restaura estado anterior
        try {
            conta.getEstado().alterarPara(conta, estadoAnterior);
            System.out.println("↶  Estado restaurado: " + numeroConta + " → " + estadoAnterior);
        } catch (Exception e) {
            System.err.println("❌ Erro ao desfazer: " + e.getMessage());
        }
    }

    @Override
    public String getDescricao() {
        return "Alterar estado da conta " + numeroConta + " para " + novoEstado;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

