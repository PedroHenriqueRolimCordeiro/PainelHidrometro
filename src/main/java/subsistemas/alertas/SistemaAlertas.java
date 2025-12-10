package subsistemas.alertas;

import modelo.Alerta;
import modelo.ContaAgua;
import subsistemas.contas.GerenciadorContas;
import subsistemas.monitoramento.MonitorConsumo;
import subsistemas.persistencia.GerenciadorDados;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Sistema de Alertas - Observer concreto que monitora consumo e gera alertas.
 *
 * Responsabilidades:
 * - Receber notificações do MonitorConsumo quando há nova leitura
 * - Verificar se consumo excedeu limite configurado
 * - Criar e persistir alertas
 * - Gerenciar configurações de alertas por conta
 *
 * PADRÃO OBSERVER:
 * - Estende AlertaObserver
 * - Recebe Subject (MonitorConsumo) no construtor
 * - Implementa update() com lógica específica
 * - É notificado automaticamente quando há mudança de estado
 *
 * Fluxo:
 * 1. MonitorConsumo lê nova imagem
 * 2. MonitorConsumo chama notifyObservers()
 * 3. SistemaAlertas.update() é chamado
 * 4. Verifica limite e cria alerta se necessário
 *
 * @pattern Observer (Observer Concreto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class SistemaAlertas extends AlertaObserver {

    private GerenciadorContas gerenciadorContas;
    private GerenciadorDados gerenciadorDados;
    private subsistemas.notificacoes.GerenciadorNotificacoes gerenciadorNotificacoes;

    /**
     * Configurações de alerta por conta: numeroConta -> ConfiguracaoAlerta
     */
    private Map<String, ConfiguracaoAlerta> configuracoes;

    /**
     * Gerador de IDs únicos para alertas
     */
    private AtomicInteger geradorIdAlerta;

    /**
     * Construtor: recebe Subject (MonitorConsumo) conforme padrão Observer.
     *
     * @param subject MonitorConsumo que será observado
     * @param gerenciadorContas Gerenciador para buscar contas
     * @param gerenciadorDados Gerenciador para persistir alertas
     * @param gerenciadorNotificacoes Gerenciador para enviar notificações
     */
    public SistemaAlertas(MonitorConsumo subject,
                          GerenciadorContas gerenciadorContas,
                          GerenciadorDados gerenciadorDados,
                          subsistemas.notificacoes.GerenciadorNotificacoes gerenciadorNotificacoes) {
        super(subject); // OBRIGATÓRIO: passa subject para classe base
        this.gerenciadorContas = gerenciadorContas;
        this.gerenciadorDados = gerenciadorDados;
        this.gerenciadorNotificacoes = gerenciadorNotificacoes;
        this.configuracoes = new HashMap<>();
        this.geradorIdAlerta = new AtomicInteger(1);

        // Carrega próximo ID de alerta baseado nos alertas existentes
        inicializarGeradorId();
    }

    /**
     * Método update() chamado quando MonitorConsumo notifica mudança.
     *
     * PADRÃO OBSERVER: Este método é chamado automaticamente pelo Subject.
     */
    @Override
    public void update() {
        // Obtém conta e consumo atual do Subject
        String numeroConta = subject.getContaAtualMonitorada();
        double consumoAtual = subject.getConsumoAtual();

        if (numeroConta == null) {
            return; // Nenhuma conta sendo monitorada
        }

        // Verifica se há configuração de alerta para esta conta
        ConfiguracaoAlerta config = configuracoes.get(numeroConta);
        if (config == null || config.limiteConsumo <= 0) {
            return; // Sem limite configurado
        }

        // Verifica se excedeu o limite
        if (consumoAtual > config.limiteConsumo) {
            gerarAlerta(numeroConta, consumoAtual, config.limiteConsumo);
        }
    }

    // ========================================================================
    // CONFIGURAÇÃO DE ALERTAS
    // ========================================================================

    /**
     * Configura o limite de consumo para uma conta.
     *
     * @param numeroConta Número da conta
     * @param volumeMaximo Volume máximo em m³
     */
    public void configurarLimiteConsumo(String numeroConta, double volumeMaximo) {
        ConfiguracaoAlerta config = configuracoes.computeIfAbsent(
            numeroConta, k -> new ConfiguracaoAlerta()
        );
        config.limiteConsumo = volumeMaximo;

        // Atualiza também na conta
        ContaAgua conta = gerenciadorContas.obterConta(numeroConta);
        if (conta != null) {
            conta.setLimiteConsumo(volumeMaximo);
            gerenciadorDados.salvarConta(conta);
        }
    }

    /**
     * Habilita/desabilita alerta por email para uma conta.
     *
     * @param numeroConta Número da conta
     * @param habilitar true para habilitar, false para desabilitar
     */
    public void habilitarAlertaEmail(String numeroConta, boolean habilitar) {
        ConfiguracaoAlerta config = configuracoes.computeIfAbsent(
            numeroConta, k -> new ConfiguracaoAlerta()
        );
        config.alertaEmailHabilitado = habilitar;
    }

    /**
     * Habilita/desabilita alerta para concessionária para uma conta.
     *
     * @param numeroConta Número da conta
     * @param habilitar true para habilitar, false para desabilitar
     */
    public void habilitarAlertaConcessionaria(String numeroConta, boolean habilitar) {
        ConfiguracaoAlerta config = configuracoes.computeIfAbsent(
            numeroConta, k -> new ConfiguracaoAlerta()
        );
        config.alertaConcessionariaHabilitado = habilitar;
    }

    // ========================================================================
    // GERAÇÃO DE ALERTAS
    // ========================================================================

    /**
     * Gera um alerta quando limite é excedido.
     *
     * @param numeroConta Número da conta
     * @param consumoAtual Consumo atual
     * @param limiteConfigurado Limite configurado
     */
    private void gerarAlerta(String numeroConta, double consumoAtual, double limiteConfigurado) {
        // Busca conta para obter CPF do usuário
        ContaAgua conta = gerenciadorContas.obterConta(numeroConta);
        if (conta == null) {
            return;
        }

        // Cria alerta
        int idAlerta = geradorIdAlerta.getAndIncrement();
        Alerta alerta = new Alerta(
            idAlerta,
            numeroConta,
            conta.getCpfUsuario(),
            consumoAtual,
            limiteConfigurado,
            LocalDateTime.now()
        );

        // Obtém configuração
        ConfiguracaoAlerta config = configuracoes.get(numeroConta);

        // Marca flags conforme configuração
        if (config != null) {
            alerta.setEmailEnviado(config.alertaEmailHabilitado);
            alerta.setNotificadoConcessionaria(config.alertaConcessionariaHabilitado);
        }

        // Persiste alerta
        gerenciadorDados.salvarAlerta(alerta);

        // TODO FASE 7: Registrar no SistemaLog
        System.out.println("🚨 ALERTA GERADO: " + alerta.getMensagem());

        // FASE 7: Notifica usando Strategy + Factory
        gerenciadorNotificacoes.notificar(alerta, conta);
    }

    // ========================================================================
    // CONSULTA DE ALERTAS
    // ========================================================================

    /**
     * Lista todos os alertas de uma conta.
     *
     * @param numeroConta Número da conta
     * @return Lista de alertas da conta
     */
    public List<Alerta> listarAlertasConta(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return gerenciadorDados.listarAlertas().stream()
                .filter(a -> numeroConta.equals(a.getNumeroConta()))
                .sorted(Comparator.comparing(Alerta::getDataHora).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os alertas pendentes (não lidos).
     *
     * @return Lista de alertas pendentes
     */
    public List<Alerta> listarAlertasPendentes() {
        return gerenciadorDados.listarAlertas().stream()
                .filter(a -> !a.isLido())
                .sorted(Comparator.comparing(Alerta::getDataHora).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Marca um alerta como lido.
     *
     * @param idAlerta ID do alerta
     */
    public void marcarAlertaComoLido(int idAlerta) {
        Alerta alerta = gerenciadorDados.buscarAlerta(idAlerta);
        if (alerta != null) {
            alerta.setLido(true);
            gerenciadorDados.salvarAlerta(alerta);
        }
    }

    /**
     * Obtém a configuração de alerta de uma conta.
     *
     * @param numeroConta Número da conta
     * @return Configuração ou null se não existir
     */
    public ConfiguracaoAlerta obterConfiguracao(String numeroConta) {
        return configuracoes.get(numeroConta);
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Inicializa o gerador de IDs baseado nos alertas existentes.
     */
    private void inicializarGeradorId() {
        List<Alerta> alertas = gerenciadorDados.listarAlertas();
        if (!alertas.isEmpty()) {
            int maxId = alertas.stream()
                    .mapToInt(Alerta::getId)
                    .max()
                    .orElse(0);
            geradorIdAlerta.set(maxId + 1);
        }
    }

    // ========================================================================
    // CLASSE INTERNA: CONFIGURAÇÃO DE ALERTA
    // ========================================================================

    /**
     * Configuração de alerta para uma conta.
     */
    public static class ConfiguracaoAlerta {
        public double limiteConsumo = 0.0;
        public boolean alertaEmailHabilitado = false;
        public boolean alertaConcessionariaHabilitado = false;

        public ConfiguracaoAlerta() {
        }

        @Override
        public String toString() {
            return "ConfiguracaoAlerta{" +
                    "limiteConsumo=" + limiteConsumo +
                    ", alertaEmailHabilitado=" + alertaEmailHabilitado +
                    ", alertaConcessionariaHabilitado=" + alertaConcessionariaHabilitado +
                    '}';
        }
    }
}

