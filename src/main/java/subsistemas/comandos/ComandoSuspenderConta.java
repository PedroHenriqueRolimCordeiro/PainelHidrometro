package subsistemas.comandos;

import modelo.enums.TipoEstadoConta;
import subsistemas.contas.GerenciadorContas;
import java.time.LocalDateTime;

/**
 * Comando para suspender uma conta.
 *
 * Atalho para alterar estado para SUSPENSA.
 * Permite undo: reativa a conta (volta para ATIVA).
 *
 * @pattern Command (Comando Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class ComandoSuspenderConta implements Comando {

    private ComandoAlterarEstadoConta comandoInterno;

    /**
     * Construtor: recebe parâmetros para executar o comando.
     *
     * @param numeroConta Número da conta a suspender
     * @param gerenciadorContas Gerenciador de contas
     */
    public ComandoSuspenderConta(String numeroConta, GerenciadorContas gerenciadorContas) {
        // Delega para ComandoAlterarEstadoConta
        this.comandoInterno = new ComandoAlterarEstadoConta(
            numeroConta,
            TipoEstadoConta.SUSPENSA,
            gerenciadorContas
        );
    }

    @Override
    public void executar() {
        comandoInterno.executar();
        System.out.println("⏸️  Conta suspensa");
    }

    @Override
    public void desfazer() {
        comandoInterno.desfazer();
        System.out.println("↶  Suspensão desfeita (conta reativada)");
    }

    @Override
    public String getDescricao() {
        return "Suspender " + comandoInterno.getDescricao();
    }

    @Override
    public LocalDateTime getTimestamp() {
        return comandoInterno.getTimestamp();
    }
}

