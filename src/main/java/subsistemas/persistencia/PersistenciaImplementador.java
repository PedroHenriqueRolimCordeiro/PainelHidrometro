package subsistemas.persistencia;

import java.util.List;

/**
 * Interface do Implementador no padrão Bridge para persistência de dados.
 * Define operações de baixo nível para gravar e recuperar dados.
 *
 * Implementações concretas:
 * - PersistenciaArquivoImpl: Salva em arquivos JSON locais
 * - PersistenciaBancoImpl: Salva em banco de dados via JDBC
 *
 * PADRÃO BRIDGE:
 * - Implementador define operações de BAIXO NÍVEL
 * - Abstração (GerenciadorDados) define operações de ALTO NÍVEL
 *
 * @pattern Bridge (Implementador)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public interface PersistenciaImplementador {

    /**
     * Grava um registro no armazenamento.
     *
     * @param tipoEntidade Tipo da entidade (ex: "Usuario", "Conta", "Alerta")
     * @param chave Chave única do registro (ex: CPF, numeroConta, idAlerta)
     * @param dados Objeto a ser persistido (será serializado)
     * @throws Exception Se houver erro na gravação
     */
    void gravarRegistro(String tipoEntidade, String chave, Object dados) throws Exception;

    /**
     * Lê um registro do armazenamento.
     *
     * @param tipoEntidade Tipo da entidade
     * @param chave Chave única do registro
     * @param classe Classe do objeto a ser deserializado
     * @return Objeto recuperado ou null se não encontrado
     * @throws Exception Se houver erro na leitura
     */
    <T> T lerRegistro(String tipoEntidade, String chave, Class<T> classe) throws Exception;

    /**
     * Lista todos os registros de um tipo.
     *
     * @param tipoEntidade Tipo da entidade
     * @param classe Classe dos objetos a serem deserializados
     * @return Lista de objetos (vazia se nenhum encontrado)
     * @throws Exception Se houver erro na listagem
     */
    <T> List<T> listarRegistros(String tipoEntidade, Class<T> classe) throws Exception;

    /**
     * Remove um registro do armazenamento.
     *
     * @param tipoEntidade Tipo da entidade
     * @param chave Chave única do registro
     * @return true se removido, false se não encontrado
     * @throws Exception Se houver erro na remoção
     */
    boolean removerRegistro(String tipoEntidade, String chave) throws Exception;

    /**
     * Verifica se um registro existe.
     *
     * @param tipoEntidade Tipo da entidade
     * @param chave Chave única do registro
     * @return true se existe, false caso contrário
     * @throws Exception Se houver erro na verificação
     */
    boolean existeRegistro(String tipoEntidade, String chave) throws Exception;
}

