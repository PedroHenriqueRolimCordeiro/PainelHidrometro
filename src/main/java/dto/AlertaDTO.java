package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * DTO para transferência de dados de alerta entre camadas.
 * Evita exposição direta da entidade de domínio.
 *
 * @author Pedro Henrique
 */
public class AlertaDTO {
    private int id;
    private String numeroConta;
    private String cpfUsuario;
    private double consumoAtual;
    private double limiteConfigurado;
    private LocalDateTime dataHora;
    private boolean lido;
    private boolean emailEnviado;
    private boolean notificadoConcessionaria;
    private String mensagem;

    /**
     * Construtor completo
     */
    public AlertaDTO(int id, String numeroConta, String cpfUsuario,
                     double consumoAtual, double limiteConfigurado,
                     LocalDateTime dataHora, boolean lido,
                     boolean emailEnviado, boolean notificadoConcessionaria,
                     String mensagem) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.cpfUsuario = cpfUsuario;
        this.consumoAtual = consumoAtual;
        this.limiteConfigurado = limiteConfigurado;
        this.dataHora = dataHora;
        this.lido = lido;
        this.emailEnviado = emailEnviado;
        this.notificadoConcessionaria = notificadoConcessionaria;
        this.mensagem = mensagem;
    }

    /**
     * Construtor vazio
     */
    public AlertaDTO() {
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getConsumoAtual() {
        return consumoAtual;
    }

    public void setConsumoAtual(double consumoAtual) {
        this.consumoAtual = consumoAtual;
    }

    public double getLimiteConfigurado() {
        return limiteConfigurado;
    }

    public void setLimiteConfigurado(double limiteConfigurado) {
        this.limiteConfigurado = limiteConfigurado;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }

    public boolean isEmailEnviado() {
        return emailEnviado;
    }

    public void setEmailEnviado(boolean emailEnviado) {
        this.emailEnviado = emailEnviado;
    }

    public boolean isNotificadoConcessionaria() {
        return notificadoConcessionaria;
    }

    public void setNotificadoConcessionaria(boolean notificadoConcessionaria) {
        this.notificadoConcessionaria = notificadoConcessionaria;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    /**
     * Retorna data/hora formatada
     */
    public String getDataHoraFormatada() {
        if (dataHora == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatter);
    }

    // Equals e HashCode baseados em ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertaDTO alertaDTO = (AlertaDTO) o;
        return id == alertaDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ToString para debug

    @Override
    public String toString() {
        return "AlertaDTO{" +
                "id=" + id +
                ", numeroConta='" + numeroConta + '\'' +
                ", cpfUsuario='" + cpfUsuario + '\'' +
                ", consumoAtual=" + consumoAtual +
                ", limiteConfigurado=" + limiteConfigurado +
                ", dataHora=" + dataHora +
                ", lido=" + lido +
                ", emailEnviado=" + emailEnviado +
                ", notificadoConcessionaria=" + notificadoConcessionaria +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}

