package subsistemas.contas.state;

import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import excecoes.OperacaoNaoPermitidaException;

/**
 * Estado SUSPENSA: Permite apenas consultas; consumo bloqueado.
 *
 * Operações permitidas:
 * - ❌ Realizar consumo (bloqueado)
 * - ❌ Vincular/Desvincular SHA (bloqueado)
 * - ✅ Consultas (não afeta)
 * - ❌ Alterar dados (bloqueado)
 *
 * Transições permitidas:
 * - SUSPENSA → ATIVA (reativação)
 * - SUSPENSA → CANCELADA
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoContaSuspensa implements EstadoConta {

    @Override
    public boolean podeRealizarConsumo() {
        return false; // ❌ Consumo bloqueado
    }

    @Override
    public boolean podeVincularSHA() {
        return false; // ❌ Não pode vincular SHA
    }

    @Override
    public boolean podeAlterarDados() {
        return false; // ❌ Não pode alterar dados
    }

    @Override
    public void alterarPara(ContaAgua conta, TipoEstadoConta novoEstado)
            throws OperacaoNaoPermitidaException {

        switch (novoEstado) {
            case SUSPENSA:
                // Já está suspensa
                break;

            case ATIVA:
                // Reativação permitida
                conta.setEstado(new EstadoContaAtiva());
                conta.setTipoEstado(TipoEstadoConta.ATIVA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: SUSPENSA → ATIVA (reativada)");
                break;

            case CANCELADA:
                conta.setEstado(new EstadoContaCancelada());
                conta.setTipoEstado(TipoEstadoConta.CANCELADA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: SUSPENSA → CANCELADA");
                break;

            case INADIMPLENTE:
                // Transição não comum, mas permitida
                conta.setEstado(new EstadoContaInadimplente());
                conta.setTipoEstado(TipoEstadoConta.INADIMPLENTE);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: SUSPENSA → INADIMPLENTE");
                break;

            default:
                throw new OperacaoNaoPermitidaException(
                    "Transição desconhecida: " + novoEstado
                );
        }
    }

    @Override
    public String getNomeEstado() {
        return "SUSPENSA";
    }
}

