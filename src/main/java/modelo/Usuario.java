package modelo;

import modelo.enums.PerfilUsuario;
import java.util.Objects;

/**
 * Representa um usuário do sistema (cliente da concessionária).
 * Identificado unicamente por CPF.
 *
 * @author Pedro Henrique
 */
public class Usuario {
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private PerfilUsuario perfil;

    /**
     * Construtor completo
     */
    public Usuario(String cpf, String nome, String email, String telefone,
                   String endereco, PerfilUsuario perfil) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.perfil = perfil;
    }

    /**
     * Construtor vazio para frameworks de persistência
     */
    public Usuario() {
    }

    // Getters e Setters

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    // Equals e HashCode baseados em CPF (chave única)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(cpf, usuario.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    // ToString para debug

    @Override
    public String toString() {
        return "Usuario{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", endereco='" + endereco + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}

