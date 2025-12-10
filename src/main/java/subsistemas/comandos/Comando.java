package subsistemas.comandos;

import java.time.LocalDateTime;

/**
 * Interface Command para operações reversíveis.
 *
 * Operações críticas são encapsuladas em comandos que permitem:
 * - Execução controlada
 * - Desfazer (undo)
 * - Refazer (redo)
 * - Histórico completo
 *
 * Operações que usam Command (Seção 3.6.2):
 * - Remover usuário/conta
 * - Vincular/Desvincular SHA
 * - Alterar estado da conta
 * - Suspender/Reativar conta
 *
 * PADRÃO COMMAND:
 * - Encapsula requisição como objeto
 * - Permite parametrizar clientes com diferentes requisições
 * - Suporta undo/redo
 * - Mantém histórico de operações
 *
 * @pattern Command (Interface)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public interface Comando {

    /**
     * Executa o comando.
     */
    void executar();

    /**
     * Desfaz o comando (undo).
     */
    void desfazer();

    /**
     * Retorna descrição do comando.
     *
     * @return Descrição legível da operação
     */
    String getDescricao();

    /**
     * Retorna timestamp da criação do comando.
     *
     * @return Data/hora da criação
     */
    LocalDateTime getTimestamp();
}

