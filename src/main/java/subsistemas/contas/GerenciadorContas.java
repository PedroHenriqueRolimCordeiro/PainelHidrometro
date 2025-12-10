package subsistemas.contas;

import dto.ContaAguaDTO;
import excecoes.OperacaoNaoPermitidaException;
import modelo.ContaAgua;
import modelo.enums.TipoEstadoConta;
import modelo.enums.TipoOperacao;
import subsistemas.contas.state.EstadoConta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerenciador responsável pelo CRUD de contas de água e associação de hidrômetros (SHAs).
 *
 * @author Pedro Henrique
 * @date 2025-12-08
 */
public class GerenciadorContas {

    private final Map<String, ContaAgua> contas = new HashMap<>();
    private final Map<Integer, String> shaParaConta = new HashMap<>();

    public GerenciadorContas() {
    }

    // CRUD - CREATE
    public void criar(String numeroConta, String cpfUsuario) {
        validarCamposObrigatorios(numeroConta, cpfUsuario);
        if (contas.containsKey(numeroConta)) {
            throw new IllegalArgumentException("Número de conta já cadastrado: " + numeroConta);
        }
        ContaAgua conta = new ContaAgua(numeroConta, cpfUsuario);
        contas.put(numeroConta, conta);
    }

    // CRUD - READ
    public ContaAguaDTO buscar(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) return null;
        ContaAgua conta = contas.get(numeroConta);
        return conta != null ? converterParaDTO(conta) : null;
    }

    public List<ContaAguaDTO> listarPorUsuario(String cpfUsuario) {
        if (cpfUsuario == null || cpfUsuario.trim().isEmpty()) return new ArrayList<>();
        return contas.values().stream()
                .filter(c -> cpfUsuario.equals(c.getCpfUsuario()))
                .sorted(Comparator.comparing(ContaAgua::getNumeroConta))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<ContaAguaDTO> listarTodas() {
        return contas.values().stream()
                .sorted(Comparator.comparing(ContaAgua::getNumeroConta))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public boolean existe(String numeroConta) {
        return numeroConta != null && contas.containsKey(numeroConta);
    }

    // CRUD - UPDATE
    public void atualizar(String numeroConta, String cpfUsuario) {
        validarCamposObrigatorios(numeroConta, cpfUsuario);
        if (!contas.containsKey(numeroConta)) {
            throw new IllegalArgumentException("Conta não encontrada: " + numeroConta);
        }
        ContaAgua conta = contas.get(numeroConta);
        conta.setCpfUsuario(cpfUsuario);
    }

    // CRUD - DELETE
    public ContaAgua remover(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) return null;
        ContaAgua conta = contas.get(numeroConta);
        if (conta != null) {
            Set<Integer> shasVinculados = new HashSet<>(conta.getShaIds());
            for (Integer idSHA : shasVinculados) {
                desvincularSHA(numeroConta, idSHA);
            }
        }
        return contas.remove(numeroConta);
    }

    // GESTÃO DE VÍNCULOS SHA
    public void vincularSHA(String numeroConta, int idSHA) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + numeroConta);
        }

        // Verifica se SHA já está vinculado a outra conta ATIVA
        String contaExistenteNum = shaParaConta.get(idSHA);
        if (contaExistenteNum != null && !contaExistenteNum.equals(numeroConta)) {
            ContaAgua contaExistente = contas.get(contaExistenteNum);
            if (contaExistente != null &&
                contaExistente.getTipoEstado() == TipoEstadoConta.ATIVA) {
                throw new IllegalStateException(
                    "SHA " + idSHA + " já está vinculado à conta ativa: " + contaExistenteNum
                );
            }
        }

        conta.adicionarSHA(idSHA);
        shaParaConta.put(idSHA, numeroConta);
    }

    public void desvincularSHA(String numeroConta, int idSHA) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + numeroConta);
        }
        conta.removerSHA(idSHA);
        shaParaConta.remove(idSHA);
    }

    public Set<Integer> listarSHAs(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + numeroConta);
        }
        return new HashSet<>(conta.getShaIds());
    }

    public boolean shaEstaVinculadoAContaAtiva(int idSHA) {
        if (!shaParaConta.containsKey(idSHA)) return false;
        String numeroConta = shaParaConta.get(idSHA);
        ContaAgua conta = contas.get(numeroConta);
        return conta != null && conta.getTipoEstado() == TipoEstadoConta.ATIVA;
    }

    public String obterContaDeSHA(int idSHA) {
        return shaParaConta.get(idSHA);
    }

    // GESTÃO DE ESTADOS (State Pattern)
    public void alterarEstado(String numeroConta, TipoEstadoConta novoEstado) throws OperacaoNaoPermitidaException {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + numeroConta);
        }
        conta.alterarEstado(novoEstado);
    }

    public EstadoConta consultarEstado(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + numeroConta);
        }
        return conta.getEstado();
    }

    public boolean podeRealizarOperacao(String numeroConta, TipoOperacao operacao) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            return false;
        }
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            return false;
        }
        switch (operacao) {
            case CONSUMO:
                return conta.getEstado().podeRealizarConsumo();
            case VINCULAR_SHA:
                return conta.getEstado().podeVincularSHA();
            case ALTERAR_DADOS:
                return conta.getEstado().podeAlterarDados();
            default:
                return false;
        }
    }

    // MÉTODOS AUXILIARES
    private void validarCamposObrigatorios(String numeroConta, String cpfUsuario) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }
        if (cpfUsuario == null || cpfUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do usuário é obrigatório");
        }
    }

    private ContaAguaDTO converterParaDTO(ContaAgua conta) {
        return new ContaAguaDTO(
            conta.getNumeroConta(),
            conta.getCpfUsuario(),
            conta.getTipoEstado(),
            conta.getShaIds(),
            conta.getLimiteConsumo(),
            conta.getConsumoAcumulado()
        );
    }

    public int quantidade() {
        return contas.size();
    }

    public ContaAgua obterConta(String numeroConta) {
        return contas.get(numeroConta);
    }
}

