package dto;

import modelo.enums.TipoEstadoConta;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * DTO para transferência de dados de conta de água entre camadas.
 * Evita exposição direta da entidade de domínio.
 *
 * @author Pedro Henrique
 */
public class ContaAguaDTO {
    private String numeroConta;
    private String cpfUsuario;
    private TipoEstadoConta tipoEstado;
    private Set<Integer> shaIds;
    private double limiteConsumo;
    private double consumoAcumulado;

    /**
     * Construtor completo
     */
    public ContaAguaDTO(String numeroConta, String cpfUsuario,
                        TipoEstadoConta tipoEstado, Set<Integer> shaIds,
                        double limiteConsumo, double consumoAcumulado) {
        this.numeroConta = numeroConta;
        this.cpfUsuario = cpfUsuario;
        this.tipoEstado = tipoEstado;
        this.shaIds = shaIds != null ? new HashSet<>(shaIds) : new HashSet<>();
        this.limiteConsumo = limiteConsumo;
        this.consumoAcumulado = consumoAcumulado;
    }

    /**
     * Construtor vazio
     */
    public ContaAguaDTO() {
        this.shaIds = new HashSet<>();
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
        this.shaIds = shaIds != null ? new HashSet<>(shaIds) : new HashSet<>();
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

    // Equals e HashCode baseados em numeroConta

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaAguaDTO that = (ContaAguaDTO) o;
        return Objects.equals(numeroConta, that.numeroConta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroConta);
    }

    // ToString para debug

    @Override
    public String toString() {
        return "ContaAguaDTO{" +
                "numeroConta='" + numeroConta + '\'' +
                ", cpfUsuario='" + cpfUsuario + '\'' +
                ", tipoEstado=" + tipoEstado +
                ", shaIds=" + shaIds +
                ", limiteConsumo=" + limiteConsumo +
                ", consumoAcumulado=" + consumoAcumulado +
                '}';
    }
}

