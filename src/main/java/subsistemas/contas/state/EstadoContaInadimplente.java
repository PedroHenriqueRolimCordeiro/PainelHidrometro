package subsistemas.contas.state;

import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import excecoes.OperacaoNaoPermitidaException;

/**
 * Estado INADIMPLENTE: Permite pagamento para regularização; consumo bloqueado.
 *
 * Operações permitidas:
 * - ❌ Realizar consumo (bloqueado até pagamento)
 * - ❌ Vincular/Desvincular SHA (bloqueado)
 * - ✅ Consultas (não afeta)
 * - ❌ Alterar dados (bloqueado)
 * - ✅ Pagamento (via transição para ATIVA)
 *
 * Transições permitidas:
 * - INADIMPLENTE → ATIVA (após pagamento)
 * - INADIMPLENTE → CANCELADA (por inadimplência prolongada)
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoContaInadimplente implements EstadoConta {

    @Override
    public boolean podeRealizarConsumo() {
        return false; // ❌ Consumo bloqueado até regularização
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
            case INADIMPLENTE:
                // Já está inadimplente
                break;

            case ATIVA:
                // Regularização após pagamento
                conta.setEstado(new EstadoContaAtiva());
                conta.setTipoEstado(TipoEstadoConta.ATIVA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: INADIMPLENTE → ATIVA (regularizada)");
                break;

            case CANCELADA:
                // Cancelamento por inadimplência prolongada
                conta.setEstado(new EstadoContaCancelada());
                conta.setTipoEstado(TipoEstadoConta.CANCELADA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: INADIMPLENTE → CANCELADA");
                break;

            case SUSPENSA:
                // Transição não comum, mas permitida
                conta.setEstado(new EstadoContaSuspensa());
                conta.setTipoEstado(TipoEstadoConta.SUSPENSA);
                System.out.println("📊 Conta " + conta.getNumeroConta() +
                    " alterada: INADIMPLENTE → SUSPENSA");
                break;

            default:
                throw new OperacaoNaoPermitidaException(
                    "Transição desconhecida: " + novoEstado
                );
        }
    }

    @Override
    public String getNomeEstado() {
        return "INADIMPLENTE";
    }
}

