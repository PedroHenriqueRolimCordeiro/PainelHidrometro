package subsistemas.notificacoes;

import modelo.Alerta;
import modelo.ContaAgua;
import modelo.Usuario;
import excecoes.NotificacaoException;

/**
 * Estratégia de notificação no PAINEL INTERNO.
 * Registra o alerta para visualização pela equipe da concessionária.
 *
 * @pattern Strategy (Implementação Concreta)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class EstrategiaNotificacaoPainelInterno implements EstrategiaNotificacao {

    private boolean habilitada;

    public EstrategiaNotificacaoPainelInterno() {
        this.habilitada = true;
    }

    @Override
    public void notificar(Alerta alerta, ContaAgua conta, Usuario usuario)
            throws NotificacaoException {
        if (!habilitada) {
            return;
        }

        // Marca alerta como disponível para visualização interna
        // (já está persistido, apenas registra no log/console)
        System.out.println("📋 ALERTA REGISTRADO NO PAINEL INTERNO:");
        System.out.println("   Conta: " + alerta.getNumeroConta());
        System.out.println("   Usuário: " + usuario.getNome() + " (" + usuario.getCpf() + ")");
        System.out.println("   Consumo: " + String.format("%.2f m³", alerta.getConsumoAtual()));
        System.out.println("   Disponível para visualização pela concessionária");
    }

    @Override
    public String getTipo() {
        return "PAINEL_INTERNO";
    }

    @Override
    public boolean isHabilitada() {
        return habilitada;
    }

    @Override
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
}

