package subsistemas.usuarios;

import dto.UsuarioDTO;
import modelo.Usuario;
import modelo.enums.PerfilUsuario;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerenciador responsável pelo CRUD de usuários do sistema.
 * Armazenamento em memória (Map CPF -> Usuario) para as fases iniciais.
 */
public class GerenciadorUsuarios {

    private final Map<String, Usuario> usuarios = new HashMap<>();

    public GerenciadorUsuarios() {
    }

    public void cadastrar(String cpf, String nome, String email,
                          String telefone, String endereco, PerfilUsuario perfil) {
        validarCamposObrigatorios(cpf, nome, email, perfil);
        if (usuarios.containsKey(cpf)) {
            throw new IllegalArgumentException("Usuário já cadastrado: " + cpf);
        }
        Usuario usuario = new Usuario(cpf, nome, email, telefone, endereco, perfil);
        usuarios.put(cpf, usuario);
    }

    public UsuarioDTO buscar(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) return null;
        Usuario u = usuarios.get(cpf);
        return u == null ? null : converterParaDTO(u);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getCpf))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public boolean existe(String cpf) {
        return cpf != null && usuarios.containsKey(cpf);
    }

    public void atualizar(String cpf, String nome, String email,
                          String telefone, String endereco, PerfilUsuario perfil) {
        validarCamposObrigatorios(cpf, nome, email, perfil);
        if (!usuarios.containsKey(cpf)) throw new IllegalArgumentException("Usuário não encontrado: " + cpf);
        Usuario usuario = usuarios.get(cpf);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setEndereco(endereco);
        usuario.setPerfil(perfil);
    }

    public Usuario remover(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) return null;
        return usuarios.remove(cpf);
    }

    private void validarCamposObrigatorios(String cpf, String nome, String email, PerfilUsuario perfil) {
        if (cpf == null || cpf.trim().isEmpty()) throw new IllegalArgumentException("CPF é obrigatório");
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome é obrigatório");
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email é obrigatório");
        if (perfil == null) throw new IllegalArgumentException("Perfil é obrigatório");
    }

    private UsuarioDTO converterParaDTO(Usuario u) {
        return new UsuarioDTO(u.getCpf(), u.getNome(), u.getEmail(), u.getTelefone(), u.getEndereco(), u.getPerfil());
    }

    public int quantidade() { return usuarios.size(); }

    public Usuario obterUsuario(String cpf) { return usuarios.get(cpf); }
}
