package subsistemas.persistencia;

import modelo.Usuario;
import modelo.ContaAgua;
import modelo.Alerta;

import java.util.List;

/**
 * Abstração no padrão Bridge para gerenciamento de persistência de dados.
 *
 * Responsabilidades:
 * - Métodos de ALTO NÍVEL para salvar/buscar/listar/remover entidades
 * - Conversão entre chaves e objetos
 * - Tratamento de exceções
 * - Delegação para PersistenciaImplementador (baixo nível)
 *
 * PADRÃO BRIDGE:
 * - Atributo implementador: PROTECTED (obrigatório)
 * - Construtor: EXIGE o implementador (obrigatório)
 * - Delega operações de baixo nível para PersistenciaImplementador
 *
 * Tipos de entidade:
 * - "Usuario" → CPF como chave
 * - "Conta" → numeroConta como chave
 * - "Alerta" → ID como chave
 *
 * @pattern Bridge (Abstração)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class GerenciadorDados {

    // ========================================================================
    // BRIDGE - Atributo PROTECTED
    // ========================================================================

    /**
     * Implementador para persistência.
     * PROTECTED conforme o padrão formalizado.
     */
    protected PersistenciaImplementador implementador;

    // ========================================================================
    // BRIDGE - Construtor EXIGE implementador
    // ========================================================================

    /**
     * Construtor: EXIGE o implementador (padrão Bridge).
     *
     * @param implementador Implementação concreta para persistência
     */
    public GerenciadorDados(PersistenciaImplementador implementador) {
        if (implementador == null) {
            throw new IllegalArgumentException(
                "PersistenciaImplementador não pode ser nulo (padrão Bridge)"
            );
        }
        this.implementador = implementador;
    }

    // ========================================================================
    // USUÁRIOS - Métodos de Alto Nível
    // ========================================================================

    /**
     * Salva um usuário no armazenamento.
     *
     * @param usuario Usuario a ser salvo
     * @return true se salvou com sucesso
     */
    public boolean salvarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getCpf() == null) {
            return false;
        }

        try {
            // BRIDGE: Delega para o implementador
            implementador.gravarRegistro("Usuario", usuario.getCpf(), usuario);
            return true;
        } catch (Exception e) {
            // TODO FASE 7: Registrar no SistemaLog
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca um usuário por CPF.
     *
     * @param cpf CPF do usuário
     * @return Usuario encontrado ou null
     */
    public Usuario buscarUsuario(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }

        try {
            // BRIDGE: Delega para o implementador
            return implementador.lerRegistro("Usuario", cpf, Usuario.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os usuários.
     *
     * @return Lista de usuários (vazia se nenhum)
     */
    public List<Usuario> listarUsuarios() {
        try {
            // BRIDGE: Delega para o implementador
            return implementador.listarRegistros("Usuario", Usuario.class);
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            return List.of(); // Lista vazia em caso de erro
        }
    }

    /**
     * Remove um usuário.
     *
     * @param cpf CPF do usuário
     * @return true se removeu com sucesso
     */
    public boolean removerUsuario(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }

        try {
            // BRIDGE: Delega para o implementador
            return implementador.removerRegistro("Usuario", cpf);
        } catch (Exception e) {
            System.err.println("Erro ao remover usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se usuário existe.
     *
     * @param cpf CPF do usuário
     * @return true se existe
     */
    public boolean existeUsuario(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }

        try {
            return implementador.existeRegistro("Usuario", cpf);
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // CONTAS - Métodos de Alto Nível
    // ========================================================================

    /**
     * Salva uma conta no armazenamento.
     *
     * @param conta Conta a ser salva
     * @return true se salvou com sucesso
     */
    public boolean salvarConta(ContaAgua conta) {
        if (conta == null || conta.getNumeroConta() == null) {
            return false;
        }

        try {
            // BRIDGE: Delega para o implementador
            implementador.gravarRegistro("Conta", conta.getNumeroConta(), conta);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar conta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca uma conta por número.
     *
     * @param numeroConta Número da conta
     * @return Conta encontrada ou null
     */
    public ContaAgua buscarConta(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            return null;
        }

        try {
            // BRIDGE: Delega para o implementador
            return implementador.lerRegistro("Conta", numeroConta, ContaAgua.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar conta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todas as contas.
     *
     * @return Lista de contas (vazia se nenhuma)
     */
    public List<ContaAgua> listarContas() {
        try {
            // BRIDGE: Delega para o implementador
            return implementador.listarRegistros("Conta", ContaAgua.class);
        } catch (Exception e) {
            System.err.println("Erro ao listar contas: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Remove uma conta.
     *
     * @param numeroConta Número da conta
     * @return true se removeu com sucesso
     */
    public boolean removerConta(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            return false;
        }

        try {
            // BRIDGE: Delega para o implementador
            return implementador.removerRegistro("Conta", numeroConta);
        } catch (Exception e) {
            System.err.println("Erro ao remover conta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se conta existe.
     *
     * @param numeroConta Número da conta
     * @return true se existe
     */
    public boolean existeConta(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            return false;
        }

        try {
            return implementador.existeRegistro("Conta", numeroConta);
        } catch (Exception e) {
            return false;
        }
    }

    // ========================================================================
    // ALERTAS - Métodos de Alto Nível
    // ========================================================================

    /**
     * Salva um alerta no armazenamento.
     *
     * @param alerta Alerta a ser salvo
     * @return true se salvou com sucesso
     */
    public boolean salvarAlerta(Alerta alerta) {
        if (alerta == null) {
            return false;
        }

        try {
            // BRIDGE: Delega para o implementador
            String chave = String.valueOf(alerta.getId());
            implementador.gravarRegistro("Alerta", chave, alerta);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao salvar alerta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca um alerta por ID.
     *
     * @param idAlerta ID do alerta
     * @return Alerta encontrado ou null
     */
    public Alerta buscarAlerta(int idAlerta) {
        try {
            // BRIDGE: Delega para o implementador
            String chave = String.valueOf(idAlerta);
            return implementador.lerRegistro("Alerta", chave, Alerta.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar alerta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os alertas.
     *
     * @return Lista de alertas (vazia se nenhum)
     */
    public List<Alerta> listarAlertas() {
        try {
            // BRIDGE: Delega para o implementador
            return implementador.listarRegistros("Alerta", Alerta.class);
        } catch (Exception e) {
            System.err.println("Erro ao listar alertas: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Remove um alerta.
     *
     * @param idAlerta ID do alerta
     * @return true se removeu com sucesso
     */
    public boolean removerAlerta(int idAlerta) {
        try {
            // BRIDGE: Delega para o implementador
            String chave = String.valueOf(idAlerta);
            return implementador.removerRegistro("Alerta", chave);
        } catch (Exception e) {
            System.err.println("Erro ao remover alerta: " + e.getMessage());
            return false;
        }
    }
}

