package subsistemas.notificacoes;

import modelo.Alerta;
import modelo.ContaAgua;
import modelo.Usuario;
import modelo.enums.TipoNotificacao;
import excecoes.NotificacaoException;
import subsistemas.usuarios.GerenciadorUsuarios;

import java.util.*;

/**
 * Gerenciador responsável por coordenar estratégias de notificação.
 *
 * PADRÃO STRATEGY:
 * - Mantém lista de estratégias
 * - Delega notificação para as estratégias
 * - Permite trocar estratégias em tempo de execução
 *
 * Responsabilidades:
 * - Gerenciar múltiplas estratégias por conta
 * - Notificar usando todas as estratégias habilitadas
 * - Configurar quais estratégias usar
 *
 * @pattern Strategy (Contexto)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class GerenciadorNotificacoes {

    private FabricaEstrategiaNotificacao fabrica;
    private GerenciadorUsuarios gerenciadorUsuarios;

    /**
     * Estratégias por conta: numeroConta -> Lista de Estratégias
     */
    private Map<String, List<EstrategiaNotificacao>> estrategiasPorConta;

    /**
     * Construtor: recebe Factory e GerenciadorUsuarios.
     *
     * @param fabrica Factory para criar estratégias
     * @param gerenciadorUsuarios Gerenciador para buscar usuários
     */
    public GerenciadorNotificacoes(FabricaEstrategiaNotificacao fabrica,
                                     GerenciadorUsuarios gerenciadorUsuarios) {
        this.fabrica = fabrica;
        this.gerenciadorUsuarios = gerenciadorUsuarios;
        this.estrategiasPorConta = new HashMap<>();
    }

    /**
     * Configura as estratégias de notificação para uma conta.
     * Usa Factory para criar as estratégias.
     *
     * @param numeroConta Número da conta
     * @param tipos Lista de tipos de notificação a habilitar
     */
    public void configurarEstrategias(String numeroConta, List<TipoNotificacao> tipos) {
        // Usa Factory para criar estratégias
        List<EstrategiaNotificacao> estrategias = fabrica.criarEstrategias(tipos);
        estrategiasPorConta.put(numeroConta, estrategias);

        System.out.println("📢 Estratégias configuradas para conta " + numeroConta + ":");
        for (EstrategiaNotificacao est : estrategias) {
            System.out.println("   - " + est.getTipo());
        }
    }

    /**
     * Habilita ou desabilita uma estratégia específica.
     *
     * @param numeroConta Número da conta
     * @param tipo Tipo de notificação
     * @param habilitar true para habilitar, false para desabilitar
     */
    public void habilitarEstrategia(String numeroConta, TipoNotificacao tipo,
                                      boolean habilitar) {
        List<EstrategiaNotificacao> estrategias = estrategiasPorConta.get(numeroConta);

        if (estrategias == null) {
            return;
        }

        for (EstrategiaNotificacao est : estrategias) {
            if (est.getTipo().equals(tipo.name())) {
                est.setHabilitada(habilitar);
                System.out.println("📢 Estratégia " + tipo + " " +
                    (habilitar ? "habilitada" : "desabilitada") +
                    " para conta " + numeroConta);
                break;
            }
        }
    }

    /**
     * Lista as estratégias ativas para uma conta.
     *
     * @param numeroConta Número da conta
     * @return Lista de tipos de notificação ativos
     */
    public List<TipoNotificacao> listarEstrategiasAtivas(String numeroConta) {
        List<EstrategiaNotificacao> estrategias = estrategiasPorConta.get(numeroConta);

        if (estrategias == null) {
            return new ArrayList<>();
        }

        List<TipoNotificacao> ativas = new ArrayList<>();
        for (EstrategiaNotificacao est : estrategias) {
            if (est.isHabilitada()) {
                try {
                    ativas.add(TipoNotificacao.valueOf(est.getTipo()));
                } catch (IllegalArgumentException e) {
                    // Tipo não existe no enum, ignora
                }
            }
        }

        return ativas;
    }

    /**
     * Notifica sobre um alerta usando todas as estratégias habilitadas.
     * PADRÃO STRATEGY: Delega para as estratégias.
     *
     * @param alerta Alerta a ser notificado
     * @param conta Conta de água
     */
    public void notificar(Alerta alerta, ContaAgua conta) {
        String numeroConta = alerta.getNumeroConta();
        List<EstrategiaNotificacao> estrategias = estrategiasPorConta.get(numeroConta);

        if (estrategias == null || estrategias.isEmpty()) {
            // Sem estratégias configuradas
            return;
        }

        // Busca usuário
        Usuario usuario = gerenciadorUsuarios.obterUsuario(conta.getCpfUsuario());
        if (usuario == null) {
            System.err.println("❌ Usuário não encontrado: " + conta.getCpfUsuario());
            return;
        }

        // Notifica usando todas as estratégias habilitadas
        for (EstrategiaNotificacao estrategia : estrategias) {
            if (!estrategia.isHabilitada()) {
                continue; // Estratégia desabilitada
            }

            try {
                // STRATEGY: Delega para a estratégia
                estrategia.notificar(alerta, conta, usuario);
            } catch (NotificacaoException e) {
                // TODO FASE 7: Registrar no SistemaLog
                System.err.println("❌ Erro ao notificar via " + estrategia.getTipo() +
                    ": " + e.getMessage());
            }
        }
    }

    /**
     * Remove todas as estratégias de uma conta.
     *
     * @param numeroConta Número da conta
     */
    public void removerEstrategias(String numeroConta) {
        estrategiasPorConta.remove(numeroConta);
    }
}

