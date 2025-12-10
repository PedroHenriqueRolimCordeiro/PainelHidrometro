package fachada;

import dto.AlertaDTO;
import dto.ContaAguaDTO;
import dto.UsuarioDTO;
import excecoes.OperacaoNaoPermitidaException;
import modelo.enums.PerfilUsuario;
import modelo.enums.TipoEstadoConta;
import modelo.enums.TipoNotificacao;
import modelo.enums.TipoOperacao;
import subsistemas.alertas.SistemaAlertas;
import subsistemas.comandos.GerenciadorComandos;
import subsistemas.contas.GerenciadorContas;
import subsistemas.log.SistemaLog;
import subsistemas.monitoramento.LeitorOCRImpl;
import subsistemas.monitoramento.MonitorConsumo;
import subsistemas.notificacoes.GerenciadorNotificacoes;
import subsistemas.persistencia.GerenciadorDados;
import subsistemas.persistencia.PersistenciaArquivoImpl;
import subsistemas.usuarios.GerenciadorUsuarios;

import java.util.List;
import java.util.Set;

/**
 * Fachada do Painel de Monitoramento de Hidrômetros.
 * Implementa os padrões Facade e Singleton.
 *
 * @pattern Facade, Singleton
 * @author Pedro Henrique
 */
public class FachadaPainel {

    // SINGLETON - instância única
    private static FachadaPainel instancia;

    // FACADE - Subsistemas
    protected GerenciadorUsuarios usuarios;
    protected GerenciadorContas contas;
    protected MonitorConsumo monitor;
    protected SistemaAlertas alertas;
    protected GerenciadorNotificacoes notificacoes;
    protected GerenciadorDados dados;
    protected SistemaLog log;
    protected GerenciadorComandos comandos;

    // SINGLETON - Construtor PROTEGIDO
    protected FachadaPainel() {
        // 1. Log (primeiro)
        this.log = SistemaLog.getInstancia();
        log.info("═══════════════════════════════════════════════");
        log.info("Iniciando Painel de Monitoramento de Hidrômetros");
        log.info("═══════════════════════════════════════════════");

        // 2. Persistência
        this.dados = new GerenciadorDados(new PersistenciaArquivoImpl());
        log.info("✓ Subsistema de Persistência inicializado");

        // 3. Usuários
        this.usuarios = new GerenciadorUsuarios();
        log.info("✓ Gerenciador de Usuários inicializado");

        // 4. Contas
        this.contas = new GerenciadorContas();
        log.info("✓ Gerenciador de Contas inicializado");

        // 5. Monitoramento (Bridge)
        // é lido o valor do consumo via OCR
        this.monitor = new MonitorConsumo(new LeitorOCRImpl(), contas);
        log.info("✓ Monitor de Consumo inicializado (Bridge com LeitorOCRImpl)");

        // 6. Notificações (Strategy + Factory)
        subsistemas.notificacoes.EnviadorEmail enviadorEmail =
            new subsistemas.notificacoes.EnviadorEmail();
        this.notificacoes = new GerenciadorNotificacoes(
            new subsistemas.notificacoes.FabricaEstrategiaNotificacao(enviadorEmail),
            usuarios
        );
        log.info("✓ Gerenciador de Notificações inicializado");

        // 7. Alertas (Observer)
        this.alertas = new SistemaAlertas(monitor, contas, dados, notificacoes);
        this.monitor.attach(alertas);
        log.info("✓ Sistema de Alertas inicializado e registrado como Observer");

        // 8. Comandos (Command)
        this.comandos = new GerenciadorComandos();
        log.info("✓ Gerenciador de Comandos inicializado");

        log.info("═══════════════════════════════════════════════");
        log.info("Sistema PAINEL SHA iniciado com sucesso!");
        log.info("═══════════════════════════════════════════════");
    }

    // SINGLETON - Método getInstancia
    public static synchronized FachadaPainel getInstancia() {
        if (instancia == null) {
            instancia = new FachadaPainel();
        }
        return instancia;
    }

    // ========================================================================
    // RF01 – CRUD DE USUÁRIOS
    // ========================================================================

    public void cadastrarUsuario(String cpf, String nome, String email,
                                 String telefone, String endereco, PerfilUsuario perfil) {
        usuarios.cadastrar(cpf, nome, email, telefone, endereco, perfil);
        log.info("Usuário cadastrado: " + nome + " (" + cpf + ") - Perfil: " + perfil);
    }

    public UsuarioDTO buscarUsuario(String cpf) {
        return usuarios.buscar(cpf);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarios.listarTodos();
    }

    public void atualizarUsuario(String cpf, String nome, String email,
                                  String telefone, String endereco, PerfilUsuario perfil) {
        usuarios.atualizar(cpf, nome, email, telefone, endereco, perfil);
        log.info("Usuário atualizado: " + nome + " (" + cpf + ")");
    }

    public void removerUsuario(String cpf) {
        usuarios.remover(cpf);
        log.info("Usuário removido: " + cpf);
    }

    // ========================================================================
    // RF01 – CRUD DE CONTAS DE ÁGUA
    // ========================================================================

    public void criarConta(String numeroConta, String cpfUsuario) {
        contas.criar(numeroConta, cpfUsuario);
        log.info("Conta criada: " + numeroConta + " - Usuário: " + cpfUsuario);
    }

    public ContaAguaDTO buscarConta(String numeroConta) {
        return contas.buscar(numeroConta);
    }

    public List<ContaAguaDTO> listarContasPorUsuario(String cpfUsuario) {
        return contas.listarPorUsuario(cpfUsuario);
    }

    public void atualizarConta(String numeroConta, String cpfUsuario) {
        contas.atualizar(numeroConta, cpfUsuario);
        log.info("Conta atualizada: " + numeroConta);
    }

    public void removerConta(String numeroConta) {
        contas.remover(numeroConta);
        log.info("Conta removida: " + numeroConta);
    }

    // ========================================================================
    // RF01 – VÍNCULO DE SHA (Hidrômetros)
    // ========================================================================

    public void vincularSHAConta(String numeroConta, int idSHA) {
        contas.vincularSHA(numeroConta, idSHA);
        log.info("SHA " + idSHA + " vinculado à conta " + numeroConta);
    }

    public void desvincularSHAConta(String numeroConta, int idSHA) {
        contas.desvincularSHA(numeroConta, idSHA);
        log.info("SHA " + idSHA + " desvinculado da conta " + numeroConta);
    }

    public Set<Integer> listarSHAsDaConta(String numeroConta) {
        return contas.listarSHAs(numeroConta);
    }

    // ========================================================================
    // RF01 – GESTÃO DE ESTADOS DE CONTA (State Pattern)
    // ========================================================================

    public void alterarEstadoConta(String numeroConta, TipoEstadoConta novoEstado) throws OperacaoNaoPermitidaException {
        contas.alterarEstado(numeroConta, novoEstado);
        log.info("Estado da conta " + numeroConta + " alterado para: " + novoEstado);
    }

    public subsistemas.contas.state.EstadoConta consultarEstadoConta(String numeroConta) {
        return contas.consultarEstado(numeroConta);
    }

    public boolean contaPodeRealizarOperacao(String numeroConta, TipoOperacao operacao) {
        return contas.podeRealizarOperacao(numeroConta, operacao);
    }

    // ========================================================================
    // RF02 – MONITORAMENTO & CONSUMO
    // ========================================================================

    public double obterConsumoSHA(int idSHA) {
        try {
            double consumo = monitor.lerConsumoSHA(idSHA);
            log.info("Consumo do SHA " + idSHA + ": " + consumo + " m³");
            return consumo;
        } catch (Exception e) {
            log.error("Erro ao ler consumo do SHA " + idSHA, e);
            throw new RuntimeException("Erro ao ler consumo do SHA " + idSHA, e);
        }
    }

    public double obterConsumoConta(String numeroConta) {
        try {
            double consumo = monitor.lerConsumoConta(numeroConta);
            log.info("Consumo da conta " + numeroConta + ": " + consumo + " m³");
            return consumo;
        } catch (Exception e) {
            log.error("Erro ao ler consumo da conta " + numeroConta, e);
            throw new RuntimeException("Erro ao ler consumo da conta " + numeroConta, e);
        }
    }

    public void iniciarMonitoramentoConta(String numeroConta, int intervaloSegundos) {
        monitor.iniciarMonitoramentoConta(numeroConta, intervaloSegundos);
        log.info("Monitoramento iniciado para conta " + numeroConta +
                 " (intervalo: " + intervaloSegundos + "s)");
    }

    public void pausarMonitoramentoConta(String numeroConta) {
        monitor.pausarMonitoramento(numeroConta);
        log.info("Monitoramento pausado para conta " + numeroConta);
    }

    public void retomarMonitoramentoConta(String numeroConta) {
        monitor.retomarMonitoramento(numeroConta);
        log.info("Monitoramento retomado para conta " + numeroConta);
    }

    public void pararMonitoramentoConta(String numeroConta) {
        monitor.pararMonitoramentoConta(numeroConta);
        log.info("Monitoramento parado para conta " + numeroConta);
    }

    public subsistemas.monitoramento.state.EstadoMonitoramento consultarEstadoMonitoramento(String numeroConta) {
        return monitor.getEstadoMonitoramento();
    }

    public boolean isMonitoramentoAtivo(String numeroConta) {
        return monitor.isMonitoramentoAtivo(numeroConta);
    }

    // ========================================================================
    // RF03 – SISTEMA DE ALERTAS
    // ========================================================================

    public void configurarLimiteConsumo(String numeroConta, double volumeMaximo) {
        alertas.configurarLimiteConsumo(numeroConta, volumeMaximo);
        log.info("Limite de consumo configurado: " + numeroConta + " = " + volumeMaximo + " m³");
    }

    public void habilitarAlertaEmail(String numeroConta, boolean habilitar) {
        String acao = habilitar ? "habilitado" : "desabilitado";
        log.info("Alerta email " + acao + " para conta " + numeroConta);
    }

    public void habilitarAlertaConcessionaria(String numeroConta, boolean habilitar) {
        String acao = habilitar ? "habilitado" : "desabilitado";
        log.info("Alerta concessionária " + acao + " para conta " + numeroConta);
    }

    public List<AlertaDTO> listarAlertasConta(String numeroConta) {
        List<AlertaDTO> resultado = new java.util.ArrayList<>();
        for (modelo.Alerta a : alertas.listarAlertasConta(numeroConta)) {
            AlertaDTO dto = new AlertaDTO();
            dto.setId(a.getId());
            dto.setNumeroConta(a.getNumeroConta());
            dto.setConsumoAtual(a.getConsumoAtual());
            dto.setLimiteConfigurado(a.getLimiteConfigurado());
            dto.setDataHora(a.getDataHora());
            dto.setLido(a.isLido());
            resultado.add(dto);
        }
        return resultado;
    }

    public List<AlertaDTO> listarAlertasPendentes() {
        List<AlertaDTO> resultado = new java.util.ArrayList<>();
        for (modelo.Alerta a : alertas.listarAlertasPendentes()) {
            AlertaDTO dto = new AlertaDTO();
            dto.setId(a.getId());
            dto.setNumeroConta(a.getNumeroConta());
            dto.setConsumoAtual(a.getConsumoAtual());
            dto.setLimiteConfigurado(a.getLimiteConfigurado());
            dto.setDataHora(a.getDataHora());
            dto.setLido(a.isLido());
            resultado.add(dto);
        }
        return resultado;
    }

    public void marcarAlertaComoLido(int idAlerta) {
        alertas.marcarAlertaComoLido(idAlerta);
        log.info("Alerta " + idAlerta + " marcado como lido");
    }

    // ========================================================================
    // RF03 – CONFIGURAÇÃO DE ESTRATÉGIAS DE NOTIFICAÇÃO (Strategy + Factory)
    // ========================================================================

    public void configurarEstrategiasNotificacao(String numeroConta, List<TipoNotificacao> tipos) {
        notificacoes.configurarEstrategias(numeroConta, tipos);
        log.info("Estratégias de notificação configuradas para conta " + numeroConta + ": " + tipos);
    }

    public void habilitarEstrategia(String numeroConta, TipoNotificacao tipo, boolean habilitar) {
        notificacoes.habilitarEstrategia(numeroConta, tipo, habilitar);
        String acao = habilitar ? "habilitada" : "desabilitada";
        log.info("Estratégia " + tipo + " " + acao + " para conta " + numeroConta);
    }

    public List<TipoNotificacao> listarEstrategiasAtivas(String numeroConta) {
        return notificacoes.listarEstrategiasAtivas(numeroConta);
    }

    // ========================================================================
    // RF04 – SISTEMA DE LOG
    // ========================================================================

    public List<String> obterLogs(int limite) {
        return log.obterLogs(limite);
    }

    // ========================================================================
    // RF06 – OPERAÇÕES REVERSÍVEIS (Command Pattern)
    // ========================================================================

    public void desfazerUltimaOperacao() {
        comandos.desfazer();
        log.info("Operação desfeita");
    }

    public void refazerOperacao() {
        comandos.refazer();
        log.info("Operação refeita");
    }

    public List<String> obterHistoricoComandos(int limite) {
        List<String> resultado = new java.util.ArrayList<>();
        for (subsistemas.comandos.Comando cmd : comandos.getHistoricoLimitado(limite)) {
            resultado.add(cmd.getTimestamp() + " - " + cmd.getDescricao());
        }
        return resultado;
    }
}
