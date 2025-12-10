package modelo.enums;

/**
 * Define os perfis de acesso do sistema.
 * ADMIN: Acesso completo às funcionalidades administrativas
 * OPERADOR: Acesso restrito (apenas leitura e monitoramento)
 *
 * @author Pedro Henrique
 */
public enum PerfilUsuario {
    /**
     * Administrador com acesso completo ao sistema
     */
    ADMIN,

    /**
     * Operador com acesso restrito (consultas e monitoramento)
     */
    OPERADOR
}

