package subsistemas.contas.state;

import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import excecoes.OperacaoNaoPermitidaException;

/**
 * Estado CANCELADA: Apenas consulta histórica permitida.
 *
 * Operações permitidas:
 * - ❌ Realizar consumo (bloqueado permanentemente)
 * - ❌ Vincular/Desvincular SHA (bloqueado permanentemente)
 * - ✅ Consultas históricas (não afeta)
 * - ❌ Alterar dados (bloqueado permanentemente)
 *
 * Transições permitidas:
 * - NENHUMA (estado final)
 *
 * @pattern State (Estado Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstadoContaCancelada implements EstadoConta {

    @Override
    public boolean podeRealizarConsumo() {
        return false; // ❌ Consumo bloqueado permanentemente
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

        if (novoEstado == TipoEstadoConta.CANCELADA) {
            // Já está cancelada
            return;
        }

        // Estado CANCELADA é final - não permite transições
        throw new OperacaoNaoPermitidaException(
            "Conta cancelada não pode ser alterada para outro estado. " +
            "Estado CANCELADA é final."
        );
    }

    @Override
    public String getNomeEstado() {
        return "CANCELADA";
    }
}

