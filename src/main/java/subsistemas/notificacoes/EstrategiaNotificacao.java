package subsistemas.notificacoes;

import modelo.Alerta;
import modelo.ContaAgua;
import modelo.Usuario;
import excecoes.NotificacaoException;

/**
 * Interface Strategy para diferentes formas de notificação.
 *
 * Implementações:
 * - EstrategiaNotificacaoEmail: Envia email
 * - EstrategiaNotificacaoSMS: Envia SMS
 * - EstrategiaNotificacaoPush: Envia notificação push
 * - EstrategiaNotificacaoPainelInterno: Registra no painel interno
 *
 * PADRÃO STRATEGY:
 * - Interface define contrato comum
 * - Cada estratégia implementa algoritmo específico
 * - Estratégias são intercambiáveis em tempo de execução
 *
 * @pattern Strategy (Interface)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public interface EstrategiaNotificacao {

    /**
     * Notifica sobre um alerta.
     *
     * @param alerta Alerta a ser notificado
     * @param conta Conta de água relacionada
     * @param usuario Usuário a ser notificado
     * @throws NotificacaoException Se houver erro no envio
     */
    void notificar(Alerta alerta, ContaAgua conta, Usuario usuario)
            throws NotificacaoException;

    /**
     * Retorna o tipo da estratégia.
     *
     * @return Tipo da notificação (EMAIL, SMS, PUSH, PAINEL_INTERNO)
     */
    String getTipo();

    /**
     * Verifica se a estratégia está habilitada.
     *
     * @return true se habilitada, false caso contrário
     */
    boolean isHabilitada();

    /**
     * Habilita ou desabilita a estratégia.
     *
     * @param habilitada true para habilitar, false para desabilitar
     */
    void setHabilitada(boolean habilitada);
}

