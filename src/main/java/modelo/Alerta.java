package modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um alerta gerado quando o consumo ultrapassa o limite configurado.
 * Alertas podem ser direcionados à concessionária, usuário ou sistemas externos.
 *
 * @author Pedro Henrique
 */
public class Alerta {
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
    public Alerta(int id, String numeroConta, String cpfUsuario,
                  double consumoAtual, double limiteConfigurado,
                  LocalDateTime dataHora) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.cpfUsuario = cpfUsuario;
        this.consumoAtual = consumoAtual;
        this.limiteConfigurado = limiteConfigurado;
        this.dataHora = dataHora;
        this.lido = false;
        this.emailEnviado = false;
        this.notificadoConcessionaria = false;
        this.mensagem = gerarMensagemPadrao();
    }

    /**
     * Construtor vazio para frameworks de persistência
     */
    public Alerta() {
        this.dataHora = LocalDateTime.now();
        this.lido = false;
        this.emailEnviado = false;
        this.notificadoConcessionaria = false;
    }

    /**
     * Gera mensagem padrão do alerta
     */
    private String gerarMensagemPadrao() {
        return String.format(
            "ALERTA: Consumo excedido! Conta: %s | Consumo: %.2f m³ | Limite: %.2f m³",
            numeroConta, consumoAtual, limiteConfigurado
        );
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
     * Retorna data/hora formatada como string.
     *
     * @return Data/hora formatada (dd/MM/yyyy HH:mm:ss)
     */
    public String getDataHoraFormatada() {
        if (dataHora == null) {
            return "";
        }
        java.time.format.DateTimeFormatter formatter =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatter);
    }

    /**
     * Marca o alerta como lido.
     */
    public void marcarComoLido() {
        this.lido = true;
    }

    // Equals e HashCode baseados em ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alerta alerta = (Alerta) o;
        return id == alerta.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ToString para debug

    @Override
    public String toString() {
        return "Alerta{" +
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

