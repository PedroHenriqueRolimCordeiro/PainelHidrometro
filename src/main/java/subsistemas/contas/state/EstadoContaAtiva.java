package subsistemas.contas.state;

import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import excecoes.OperacaoNaoPermitidaException;

/**
 * Estado ATIVA: Permite todas as operações normais.
 *
 * Operações permitidas:
 * - ✅ Realizar consumo
 * - ✅ Vincular/Desvincular SHA
 * - ✅ Alterar dados
 *
 * Transições permitidas:
 * - ATIVA → SUSPENSA
 * - ATIVA → INADIMPLENTE
 * - ATIVA → CANCELADA
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoContaAtiva implements EstadoConta {

    @Override
    public boolean podeRealizarConsumo() {
        return true; // ✅ Permite consumo
    }

    @Override
    public boolean podeVincularSHA() {
        return true; // ✅ Permite vincular SHA
    }

    @Override
    public boolean podeAlterarDados() {
        return true; // ✅ Permite alterar dados
    }

    @Override
    public void alterarPara(ContaAgua conta, TipoEstadoConta novoEstado)
            throws OperacaoNaoPermitidaException {

        // Valida transições permitidas
        switch (novoEstado) {
            case ATIVA:
                // Já está ativa, nada a fazer
                break;

            case SUSPENSA:
                conta.setEstado(new EstadoContaSuspensa());
                conta.setTipoEstado(TipoEstadoConta.SUSPENSA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: ATIVA → SUSPENSA");
                break;

            case INADIMPLENTE:
                conta.setEstado(new EstadoContaInadimplente());
                conta.setTipoEstado(TipoEstadoConta.INADIMPLENTE);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: ATIVA → INADIMPLENTE");
                break;

            case CANCELADA:
                conta.setEstado(new EstadoContaCancelada());
                conta.setTipoEstado(TipoEstadoConta.CANCELADA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: ATIVA → CANCELADA");
                break;

            default:
                throw new OperacaoNaoPermitidaException(
                    "Transição desconhecida: " + novoEstado
                );
        }
    }

    @Override
    public String getNomeEstado() {
        return "ATIVA";
    }
}

