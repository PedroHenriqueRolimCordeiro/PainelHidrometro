package modelo;

import modelo.enums.TipoEstadoConta;
import subsistemas.contas.state.EstadoConta;
import subsistemas.contas.state.EstadoContaAtiva;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa uma conta de água vinculada a um usuário.
 * Identificada unicamente por numeroConta.
 * Uma conta pode ter múltiplos hidrômetros (SHAs) associados.
 *
 * Usa padrão State para gerenciar estados da conta.
 *
 * @pattern State (Contexto)
 * @author Pedro Henrique
 */
public class ContaAgua {
    private String numeroConta;
    private String cpfUsuario;
    private TipoEstadoConta tipoEstado;
    private Set<Integer> shaIds; // IDs dos hidrômetros (SHAs) vinculados
    private double limiteConsumo; // Volume máximo permitido (m³)
    private double consumoAcumulado; // Consumo atual acumulado (m³)

    // PADRÃO STATE: Campo estado (contexto mantém referência ao estado atual)
    private EstadoConta estado;

    /**
     * Construtor completo
     */
    public ContaAgua(String numeroConta, String cpfUsuario) {
        this.numeroConta = numeroConta;
        this.cpfUsuario = cpfUsuario;
        this.tipoEstado = TipoEstadoConta.ATIVA; // Estado inicial
        this.shaIds = new HashSet<>();
        this.limiteConsumo = 0.0;
        this.consumoAcumulado = 0.0;
        this.estado = new EstadoContaAtiva(); // PADRÃO STATE: Estado inicial
    }

    /**
     * Construtor vazio para frameworks de persistência
     */
    public ContaAgua() {
        this.shaIds = new HashSet<>();
        this.tipoEstado = TipoEstadoConta.ATIVA;
        this.estado = new EstadoContaAtiva(); // PADRÃO STATE: Estado inicial
    }

    // Getters e Setters

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public TipoEstadoConta getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(TipoEstadoConta tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public Set<Integer> getShaIds() {
        return shaIds;
    }

    public void setShaIds(Set<Integer> shaIds) {
        this.shaIds = shaIds;
    }

    public double getLimiteConsumo() {
        return limiteConsumo;
    }

    public void setLimiteConsumo(double limiteConsumo) {
        this.limiteConsumo = limiteConsumo;
    }

    public double getConsumoAcumulado() {
        return consumoAcumulado;
    }

    public void setConsumoAcumulado(double consumoAcumulado) {
        this.consumoAcumulado = consumoAcumulado;
    }

    // PADRÃO STATE: Getters e Setters para o estado

    /**
     * Retorna o estado atual da conta (padrão State).
     */
    public EstadoConta getEstado() {
        return estado;
    }

    /**
     * Define o estado da conta (padrão State).
     * Usado internamente pelos estados concretos.
     */
    public void setEstado(EstadoConta estado) {
        this.estado = estado;
    }

    /**
     * Altera o estado da conta para um novo estado (padrão State).
     * Delega a transição para o estado atual.
     */
    public void alterarEstado(TipoEstadoConta novoEstado) throws excecoes.OperacaoNaoPermitidaException {
        this.estado.alterarPara(this, novoEstado);
        this.tipoEstado = novoEstado; // Atualiza o enum também
    }

    // Métodos auxiliares para gerenciar SHAs

    /**
     * Vincula um SHA à conta
     */
    public void adicionarSHA(int idSHA) {
        this.shaIds.add(idSHA);
    }

    /**
     * Desvincula um SHA da conta
     */
    public void removerSHA(int idSHA) {
        this.shaIds.remove(idSHA);
    }

    /**
     * Verifica se um SHA está vinculado à conta
     */
    public boolean contemSHA(int idSHA) {
        return this.shaIds.contains(idSHA);
    }

    // Equals e HashCode baseados em numeroConta (chave única)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaAgua contaAgua = (ContaAgua) o;
        return Objects.equals(numeroConta, contaAgua.numeroConta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroConta);
    }

    // ToString para debug

    @Override
    public String toString() {
        return "ContaAgua{" +
                "numeroConta='" + numeroConta + '\'' +
                ", cpfUsuario='" + cpfUsuario + '\'' +
                ", tipoEstado=" + tipoEstado +
                ", shaIds=" + shaIds +
                ", limiteConsumo=" + limiteConsumo +
                ", consumoAcumulado=" + consumoAcumulado +
                '}';
    }
}

