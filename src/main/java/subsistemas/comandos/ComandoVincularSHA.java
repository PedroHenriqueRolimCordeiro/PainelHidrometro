package subsistemas.comandos;

import subsistemas.contas.GerenciadorContas;
import java.time.LocalDateTime;

/**
 * Comando para vincular um SHA a uma conta.
 *
 * Permite undo: desvincula o SHA.
 *
 * @pattern Command (Comando Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class ComandoVincularSHA implements Comando {

    private String numeroConta;
    private int idSHA;
    private GerenciadorContas gerenciadorContas;
    private LocalDateTime timestamp;

    /**
     * Construtor: recebe parâmetros para executar o comando.
     *
     * @param numeroConta Número da conta
     * @param idSHA ID do SHA a vincular
     * @param gerenciadorContas Gerenciador de contas
     */
    public ComandoVincularSHA(String numeroConta, int idSHA, GerenciadorContas gerenciadorContas) {
        this.numeroConta = numeroConta;
        this.idSHA = idSHA;
        this.gerenciadorContas = gerenciadorContas;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public void executar() {
        gerenciadorContas.vincularSHA(numeroConta, idSHA);
        System.out.println("🔗 SHA " + idSHA + " vinculado à conta " + numeroConta);
    }

    @Override
    public void desfazer() {
        gerenciadorContas.desvincularSHA(numeroConta, idSHA);
        System.out.println("↶  SHA " + idSHA + " desvinculado da conta " + numeroConta);
    }

    @Override
    public String getDescricao() {
        return "Vincular SHA " + idSHA + " à conta " + numeroConta;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

