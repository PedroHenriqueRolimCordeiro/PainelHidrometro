import fachada.FachadaPainel;
import modelo.enums.PerfilUsuario;
import modelo.enums.TipoEstadoConta;
import modelo.enums.TipoNotificacao;
import dto.UsuarioDTO;
import dto.ContaAguaDTO;
import dto.AlertaDTO;
import subsistemas.sincronizacao.GerenciadorSincronizacao;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal para execução do Painel de Monitoramento de Hidrômetros.
 * 
 * Menu interativo completo com todas as funcionalidades do sistema.
 * 
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class Main {
    
    private static FachadaPainel fachada;
    private static Scanner scanner;
    private static GerenciadorSincronizacao sincronizador;

    public static void main(String[] args) {
        // Inicializar
        fachada = FachadaPainel.getInstancia();
        scanner = new Scanner(System.in);
        sincronizador = GerenciadorSincronizacao.getInstancia();

        // Adicionar shutdown hook para parar sincronização ao fechar
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (sincronizador.isExecutando()) {
                sincronizador.parar(); // apenas para em silêncio; logs ficam no SistemaLog
            }
        }));

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   PAINEL DE MONITORAMENTO DE HIDRÔMETROS (PAiNEL SHA)    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Iniciar sincronização automática em background (silenciosa no terminal)
        sincronizador.iniciar();

        // Menu principal
        boolean continuar = true;
        while (continuar) {
            continuar = exibirMenuPrincipal();
        }
        
        // Finalizar
        sincronizador.parar();
        scanner.close();
        System.out.println("\n✅ Sistema encerrado. Até logo!");
    }
    
    // ========================================================================
    // MENU PRINCIPAL
    // ========================================================================
    
    private static boolean exibirMenuPrincipal() {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("                      MENU PRINCIPAL");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println(" 1. Gestão de Usuários");
        System.out.println(" 2. Gestão de Contas de Água");
        System.out.println(" 3. Monitoramento de Consumo");
        System.out.println(" 4. Alertas e Notificações");
        System.out.println(" 5. Operações Reversíveis (Undo/Redo)");
        System.out.println(" 6. Consultar Logs do Sistema");
        System.out.println(" 7. Sincronização de Simuladores");
        System.out.println(" 0. Sair");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerInteiro();
        System.out.println();
        
        switch (opcao) {
            case 1: menuUsuarios(); break;
            case 2: menuContas(); break;
            case 3: menuMonitoramento(); break;
            case 4: menuAlertas(); break;
            case 5: menuComandos(); break;
            case 6: menuLogs(); break;
            case 7: menuSincronizacao(); break;
            case 0: return false;
            default: System.out.println("❌ Opção inválida!");
        }
        
        return true;
    }
    
    // ========================================================================
    // MENU 1: GESTÃO DE USUÁRIOS
    // ========================================================================
    
    private static void menuUsuarios() {
        System.out.println("━━━ GESTÃO DE USUÁRIOS ━━━\n");
        System.out.println("1. Cadastrar usuário");
        System.out.println("2. Listar todos os usuários");
        System.out.println("3. Buscar usuário por CPF");
        System.out.println("4. Atualizar usuário");
        System.out.println("5. Remover usuário");
        System.out.println("0. Voltar");
        System.out.print("\nOpção: ");
        
        int opcao = lerInteiro();
        System.out.println();
        
        switch (opcao) {
            case 1: cadastrarUsuario(); break;
            case 2: listarUsuarios(); break;
            case 3: buscarUsuario(); break;
            case 4: atualizarUsuario(); break;
            case 5: removerUsuario(); break;
        }
    }
    
    private static void cadastrarUsuario() {
        System.out.println("▸ Cadastrar Novo Usuário\n");
        
        System.out.print("CPF (apenas números): ");
        String cpf = scanner.nextLine();
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        
        System.out.print("Perfil (1=ADMIN, 2=OPERADOR): ");
        int perfil = lerInteiro();
        PerfilUsuario perfilUsuario = perfil == 1 ? PerfilUsuario.ADMIN : PerfilUsuario.OPERADOR;
        
        try {
            fachada.cadastrarUsuario(cpf, nome, email, telefone, endereco, perfilUsuario);
            System.out.println("\n✅ Usuário cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void listarUsuarios() {
        System.out.println("▸ Todos os Usuários\n");
        
        List<UsuarioDTO> usuarios = fachada.listarUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            for (UsuarioDTO u : usuarios) {
                System.out.println("────────────────────────────────");
                System.out.println("CPF: " + u.getCpf());
                System.out.println("Nome: " + u.getNome());
                System.out.println("Email: " + u.getEmail());
                System.out.println("Perfil: " + u.getPerfil());
            }
            System.out.println("────────────────────────────────");
            System.out.println("Total: " + usuarios.size() + " usuário(s)");
        }
    }
    
    private static void buscarUsuario() {
        System.out.print("CPF para buscar: ");
        String cpf = scanner.nextLine();
        
        UsuarioDTO usuario = fachada.buscarUsuario(cpf);
        
        if (usuario == null) {
            System.out.println("\n❌ Usuário não encontrado.");
        } else {
            System.out.println("\n✅ Usuário encontrado:");
            System.out.println("CPF: " + usuario.getCpf());
            System.out.println("Nome: " + usuario.getNome());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Telefone: " + usuario.getTelefone());
            System.out.println("Endereço: " + usuario.getEndereco());
            System.out.println("Perfil: " + usuario.getPerfil());
        }
    }
    
    private static void atualizarUsuario() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine();
        
        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Novo email: ");
        String email = scanner.nextLine();
        
        System.out.print("Novo telefone: ");
        String telefone = scanner.nextLine();
        
        System.out.print("Novo endereço: ");
        String endereco = scanner.nextLine();
        
        System.out.print("Novo perfil (1=ADMIN, 2=OPERADOR): ");
        int perfil = lerInteiro();
        PerfilUsuario perfilUsuario = perfil == 1 ? PerfilUsuario.ADMIN : PerfilUsuario.OPERADOR;

        try {
            fachada.atualizarUsuario(cpf, nome, email, telefone, endereco, perfilUsuario);
            System.out.println("\n✅ Usuário atualizado com sucesso!");
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void removerUsuario() {
        System.out.print("CPF do usuário a remover: ");
        String cpf = scanner.nextLine();
        
        System.out.print("Confirma remoção? (S/N): ");
        String confirma = scanner.nextLine();
        
        if (confirma.equalsIgnoreCase("S")) {
            try {
                fachada.removerUsuario(cpf);
                System.out.println("\n✅ Usuário removido com sucesso!");
            } catch (Exception e) {
                System.out.println("\n❌ Erro: " + e.getMessage());
            }
        } else {
            System.out.println("\n❌ Operação cancelada.");
        }
    }
    
    // ========================================================================
    // MENU 2: GESTÃO DE CONTAS
    // ========================================================================
    
    private static void menuContas() {
        System.out.println("━━━ GESTÃO DE CONTAS DE ÁGUA ━━━\n");
        System.out.println("1. Criar conta");
        System.out.println("2. Listar contas de um usuário");
        System.out.println("3. Buscar conta");
        System.out.println("4. Vincular SHA à conta");
        System.out.println("5. Desvincular SHA da conta");
        System.out.println("6. Listar SHAs de uma conta");
        System.out.println("7. Alterar estado da conta");
        System.out.println("8. Remover conta");
        System.out.println("0. Voltar");
        System.out.print("\nOpção: ");
        
        int opcao = lerInteiro();
        System.out.println();
        
        switch (opcao) {
            case 1: criarConta(); break;
            case 2: listarContasUsuario(); break;
            case 3: buscarConta(); break;
            case 4: vincularSHA(); break;
            case 5: desvincularSHA(); break;
            case 6: listarSHAs(); break;
            case 7: alterarEstadoConta(); break;
            case 8: removerConta(); break;
        }
    }
    
    private static void criarConta() {
        System.out.println("▸ Criar Nova Conta\n");
        
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("CPF do usuário: ");
        String cpfUsuario = scanner.nextLine();
        
        try {
            fachada.criarConta(numeroConta, cpfUsuario);
            System.out.println("\n✅ Conta criada com sucesso!");
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void listarContasUsuario() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine();
        
        List<ContaAguaDTO> contas = fachada.listarContasPorUsuario(cpf);
        
        if (contas.isEmpty()) {
            System.out.println("\nNenhuma conta encontrada para este usuário.");
        } else {
            System.out.println("\n▸ Contas do usuário " + cpf + ":\n");
            for (ContaAguaDTO c : contas) {
                System.out.println("────────────────────────────────");
                System.out.println("Número: " + c.getNumeroConta());
                System.out.println("Estado: " + c.getTipoEstado());
                System.out.println("SHAs: " + c.getShaIds());
                System.out.println("Limite: " + c.getLimiteConsumo() + " m³");
            }
            System.out.println("────────────────────────────────");
        }
    }
    
    private static void buscarConta() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        ContaAguaDTO conta = fachada.buscarConta(numeroConta);
        
        if (conta == null) {
            System.out.println("\n❌ Conta não encontrada.");
        } else {
            System.out.println("\n✅ Conta encontrada:");
            System.out.println("Número: " + conta.getNumeroConta());
            System.out.println("CPF Usuário: " + conta.getCpfUsuario());
            System.out.println("Estado: " + conta.getTipoEstado());
            System.out.println("SHAs: " + conta.getShaIds());
            System.out.println("Limite: " + conta.getLimiteConsumo() + " m³");
            System.out.println("Consumo: " + conta.getConsumoAcumulado() + " m³");
        }
    }
    
    private static void vincularSHA() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("ID do SHA (hidrômetro): ");
        int idSHA = lerInteiro();
        
        try {
            fachada.vincularSHAConta(numeroConta, idSHA);
            System.out.println("\n✅ SHA vinculado com sucesso! (Operação reversível)");
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void desvincularSHA() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("ID do SHA: ");
        int idSHA = lerInteiro();
        
        try {
            fachada.desvincularSHAConta(numeroConta, idSHA);
            System.out.println("\n✅ SHA desvinculado com sucesso!");
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void listarSHAs() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        var shas = fachada.listarSHAsDaConta(numeroConta);
        
        if (shas.isEmpty()) {
            System.out.println("\nNenhum SHA vinculado a esta conta.");
        } else {
            System.out.println("\n▸ SHAs vinculados à conta " + numeroConta + ":");
            shas.forEach(sha -> System.out.println("  • SHA " + sha));
        }
    }
    
    private static void alterarEstadoConta() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.println("\nEstados disponíveis:");
        System.out.println("1. ATIVA");
        System.out.println("2. SUSPENSA");
        System.out.println("3. INADIMPLENTE");
        System.out.println("4. CANCELADA");
        System.out.print("Escolha: ");
        
        int opcao = lerInteiro();
        TipoEstadoConta estado = null;
        
        switch (opcao) {
            case 1: estado = TipoEstadoConta.ATIVA; break;
            case 2: estado = TipoEstadoConta.SUSPENSA; break;
            case 3: estado = TipoEstadoConta.INADIMPLENTE; break;
            case 4: estado = TipoEstadoConta.CANCELADA; break;
        }
        
        if (estado != null) {
            try {
                fachada.alterarEstadoConta(numeroConta, estado);
                System.out.println("\n✅ Estado alterado com sucesso! (Operação reversível)");
            } catch (Exception e) {
                System.out.println("\n❌ Erro: " + e.getMessage());
            }
        }
    }
    
    private static void removerConta() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("Confirma remoção? (S/N): ");
        String confirma = scanner.nextLine();
        
        if (confirma.equalsIgnoreCase("S")) {
            try {
                fachada.removerConta(numeroConta);
                System.out.println("\n✅ Conta removida com sucesso! (Operação reversível)");
            } catch (Exception e) {
                System.out.println("\n❌ Erro: " + e.getMessage());
            }
        }
    }
    
    // ========================================================================
    // MENU 3: MONITORAMENTO
    // ========================================================================
    
    private static void menuMonitoramento() {
        System.out.println("━━━ MONITORAMENTO DE CONSUMO ━━━\n");
        System.out.println("1. Obter consumo de um SHA");
        System.out.println("2. Obter consumo de uma conta");
        System.out.println("3. Iniciar monitoramento periódico");
        System.out.println("4. Pausar monitoramento");
        System.out.println("5. Retomar monitoramento");
        System.out.println("6. Parar monitoramento");
        System.out.println("7. Verificar estado do monitoramento");
        System.out.println("0. Voltar");
        System.out.print("\nOpção: ");
        
        int opcao = lerInteiro();
        System.out.println();
        
        switch (opcao) {
            case 1: obterConsumoSHA(); break;
            case 2: obterConsumoConta(); break;
            case 3: iniciarMonitoramento(); break;
            case 4: pausarMonitoramento(); break;
            case 5: retomarMonitoramento(); break;
            case 6: pararMonitoramento(); break;
            case 7: verificarEstadoMonitoramento(); break;
        }
    }
    
    private static void obterConsumoSHA() {
        System.out.print("ID do SHA: ");
        int idSHA = lerInteiro();
        
        try {
            double consumo = fachada.obterConsumoSHA(idSHA);
            System.out.println("\n✅ Consumo do SHA " + idSHA + ": " + 
                String.format("%.2f m³", consumo));
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void obterConsumoConta() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        try {
            double consumo = fachada.obterConsumoConta(numeroConta);
            System.out.println("\n✅ Consumo total da conta " + numeroConta + ": " + 
                String.format("%.2f m³", consumo));
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void iniciarMonitoramento() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("Intervalo em segundos: ");
        int intervalo = lerInteiro();
        
        try {
            fachada.iniciarMonitoramentoConta(numeroConta, intervalo);
            System.out.println("\n✅ Monitoramento iniciado!");
            System.out.println("💡 O sistema verificará o consumo a cada " + intervalo + " segundos");
            System.out.println("💡 Se o limite for excedido, alertas serão disparados automaticamente");
        } catch (Exception e) {
            System.out.println("\n❌ Erro: " + e.getMessage());
        }
    }
    
    private static void pausarMonitoramento() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        fachada.pausarMonitoramentoConta(numeroConta);
        System.out.println("\n✅ Monitoramento pausado!");
    }
    
    private static void retomarMonitoramento() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        fachada.retomarMonitoramentoConta(numeroConta);
        System.out.println("\n✅ Monitoramento retomado!");
    }
    
    private static void pararMonitoramento() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        fachada.pararMonitoramentoConta(numeroConta);
        System.out.println("\n✅ Monitoramento parado!");
    }
    
    private static void verificarEstadoMonitoramento() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        var estado = fachada.consultarEstadoMonitoramento(numeroConta);
        boolean ativo = fachada.isMonitoramentoAtivo(numeroConta);
        
        System.out.println("\n▸ Estado do monitoramento:");
        System.out.println("Estado: " + estado.getNomeEstado());
        System.out.println("Ativo: " + (ativo ? "Sim" : "Não"));
    }
    
    // ========================================================================
    // MENU 4: ALERTAS
    // ========================================================================
    
    private static void menuAlertas() {
        System.out.println("━━━ ALERTAS E NOTIFICAÇÕES ━━━\n");
        System.out.println("1. Configurar limite de consumo");
        System.out.println("2. Configurar estratégias de notificação");
        System.out.println("3. Habilitar/Desabilitar alerta por email");
        System.out.println("4. Habilitar/Desabilitar alerta concessionária");
        System.out.println("5. Listar alertas de uma conta");
        System.out.println("6. Listar alertas pendentes");
        System.out.println("7. Marcar alerta como lido");
        System.out.println("0. Voltar");
        System.out.print("\nOpção: ");
        
        int opcao = lerInteiro();
        System.out.println();
        
        switch (opcao) {
            case 1: configurarLimite(); break;
            case 2: configurarEstrategias(); break;
            case 3: habilitarAlertaEmail(); break;
            case 4: habilitarAlertaConcessionaria(); break;
            case 5: listarAlertasConta(); break;
            case 6: listarAlertasPendentes(); break;
            case 7: marcarAlertaLido(); break;
        }
    }
    
    private static void configurarLimite() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("Limite de consumo (m³): ");
        double limite = lerDouble();
        
        fachada.configurarLimiteConsumo(numeroConta, limite);
        System.out.println("\n✅ Limite configurado com sucesso!");
    }
    
    private static void configurarEstrategias() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.println("\nEscolha as estratégias (separadas por vírgula):");
        System.out.println("1=EMAIL, 2=SMS, 3=PUSH, 4=CONCESSIONARIA");
        System.out.print("Ex: 1,2,4: ");
        
        String entrada = scanner.nextLine();
        String[] partes = entrada.split(",");
        
        var estrategias = Arrays.stream(partes)
            .map(String::trim)
            .map(Integer::parseInt)
            .map(i -> {
                switch (i) {
                    case 1: return TipoNotificacao.EMAIL;
                    case 2: return TipoNotificacao.SMS;
                    case 3: return TipoNotificacao.PUSH;
                    case 4: return TipoNotificacao.CONCESSIONARIA;
                    default: return null;
                }
            })
            .filter(e -> e != null)
            .toList();
        
        fachada.configurarEstrategiasNotificacao(numeroConta, estrategias);
        System.out.println("\n✅ Estratégias configuradas com sucesso!");
    }
    
    private static void habilitarAlertaEmail() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("Habilitar? (S/N): ");
        boolean habilitar = scanner.nextLine().equalsIgnoreCase("S");
        
        fachada.habilitarAlertaEmail(numeroConta, habilitar);
        System.out.println("\n✅ Configuração salva!");
    }
    
    private static void habilitarAlertaConcessionaria() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        System.out.print("Habilitar? (S/N): ");
        boolean habilitar = scanner.nextLine().equalsIgnoreCase("S");
        
        fachada.habilitarAlertaConcessionaria(numeroConta, habilitar);
        System.out.println("\n✅ Configuração salva!");
    }
    
    private static void listarAlertasConta() {
        System.out.print("Número da conta: ");
        String numeroConta = scanner.nextLine();
        
        List<AlertaDTO> alertas = fachada.listarAlertasConta(numeroConta);
        
        if (alertas.isEmpty()) {
            System.out.println("\nNenhum alerta para esta conta.");
        } else {
            System.out.println("\n▸ Alertas da conta " + numeroConta + ":\n");
            for (AlertaDTO a : alertas) {
                System.out.println("────────────────────────────────");
                System.out.println("ID: " + a.getId());
                System.out.println("Data/Hora: " + a.getDataHora());
                System.out.println("Consumo: " + a.getConsumoAtual() + " m³");
                System.out.println("Limite: " + a.getLimiteConfigurado() + " m³");
                System.out.println("Lido: " + (a.isLido() ? "Sim" : "Não"));
                System.out.println("Mensagem: " + a.getMensagem());
            }
            System.out.println("────────────────────────────────");
        }
    }
    
    private static void listarAlertasPendentes() {
        List<AlertaDTO> alertas = fachada.listarAlertasPendentes();
        
        if (alertas.isEmpty()) {
            System.out.println("\nNenhum alerta pendente.");
        } else {
            System.out.println("\n▸ Alertas Pendentes:\n");
            for (AlertaDTO a : alertas) {
                System.out.println("────────────────────────────────");
                System.out.println("ID: " + a.getId());
                System.out.println("Conta: " + a.getNumeroConta());
                System.out.println("Consumo: " + a.getConsumoAtual() + " m³ > " + 
                    a.getLimiteConfigurado() + " m³");
            }
            System.out.println("────────────────────────────────");
        }
    }
    
    private static void marcarAlertaLido() {
        System.out.print("ID do alerta: ");
        int idAlerta = lerInteiro();
        
        fachada.marcarAlertaComoLido(idAlerta);
        System.out.println("\n✅ Alerta marcado como lido!");
    }
    
    // ========================================================================
    // MENU 5: COMANDOS (UNDO/REDO)
    // ========================================================================
    
    private static void menuComandos() {
        System.out.println("━━━ OPERAÇÕES REVERSÍVEIS (UNDO/REDO) ━━━\n");
        System.out.println("1. Desfazer última operação (Undo)");
        System.out.println("2. Refazer operação (Redo)");
        System.out.println("3. Ver histórico de comandos");
        System.out.println("0. Voltar");
        System.out.print("\nOpção: ");
        
        int opcao = lerInteiro();
        System.out.println();
        
        switch (opcao) {
            case 1: desfazer(); break;
            case 2: refazer(); break;
            case 3: verHistorico(); break;
        }
    }
    
    private static void desfazer() {
        try {
            fachada.desfazerUltimaOperacao();
            System.out.println("✅ Operação desfeita com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private static void refazer() {
        try {
            fachada.refazerOperacao();
            System.out.println("✅ Operação refeita com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
    
    private static void verHistorico() {
        System.out.print("Quantos comandos mostrar? ");
        int limite = lerInteiro();
        
        List<String> historico = fachada.obterHistoricoComandos(limite);
        
        if (historico.isEmpty()) {
            System.out.println("\nNenhum comando executado ainda.");
        } else {
            System.out.println("\n▸ Histórico de Comandos:\n");
            for (int i = 0; i < historico.size(); i++) {
                System.out.println((i + 1) + ". " + historico.get(i));
            }
        }
    }
    
    // ========================================================================
    // MENU 6: LOGS
    // ========================================================================
    
    private static void menuLogs() {
        System.out.print("Quantos logs mostrar? ");
        int limite = lerInteiro();
        
        List<String> logs = fachada.obterLogs(limite);
        
        if (logs.isEmpty()) {
            System.out.println("\nNenhum log registrado.");
        } else {
            System.out.println("\n▸ Logs do Sistema:\n");
            for (String log : logs) {
                System.out.println(log);
            }
            System.out.println("\n💾 Logs completos salvos em: logs/painel.log");
        }
    }
    
    // ========================================================================
    // MENU 7: SINCRONIZAÇÃO DE SIMULADORES
    // ========================================================================

    private static void menuSincronizacao() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("            SINCRONIZAÇÃO DE SIMULADORES");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println(" 1. Ver Status");
        System.out.println(" 2. Listar Simuladores Configurados");
        System.out.println(" 3. Adicionar Simulador");
        System.out.println(" 4. Remover Simulador");
        System.out.println(" 5. Alterar Intervalo de Sincronização");
        System.out.println(" 6. Parar Sincronização");
        System.out.println(" 7. Reiniciar Sincronização");
        System.out.println(" 8. Salvar Configuração");
        System.out.println(" 0. Voltar");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.print("Escolha uma opção: ");

        int opcao = lerInteiro();
        System.out.println();

        switch (opcao) {
            case 1: verStatusSincronizacao(); break;
            case 2: listarSimuladoresConfigurados(); break;
            case 3: adicionarSimulador(); break;
            case 4: removerSimulador(); break;
            case 5: alterarIntervaloSincronizacao(); break;
            case 6: pararSincronizacao(); break;
            case 7: reiniciarSincronizacao(); break;
            case 8: salvarConfiguracaoSincronizacao(); break;
            case 0: break;
            default: System.out.println("❌ Opção inválida!");
        }
    }

    private static void verStatusSincronizacao() {
        System.out.println("▸ Status da Sincronização\n");
        System.out.println("Status: " + sincronizador.getStatus());
        System.out.println("Simuladores: " + sincronizador.listarSimuladores().size());
        System.out.println("Executando: " + (sincronizador.isExecutando() ? "✅ Sim" : "❌ Não"));
    }

    private static void listarSimuladoresConfigurados() {
        System.out.println("▸ Simuladores Configurados\n");

        var simuladores = sincronizador.listarSimuladores();

        if (simuladores.isEmpty()) {
            System.out.println("Nenhum simulador configurado.");
        } else {
            for (var entry : simuladores.entrySet()) {
                System.out.println("────────────────────────────────");
                System.out.println("SHA ID: " + entry.getValue());
                System.out.println("Caminho: " + entry.getKey());
            }
            System.out.println("────────────────────────────────");
            System.out.println("Total: " + simuladores.size() + " simulador(es)");
        }
    }

    private static void adicionarSimulador() {
        System.out.print("Caminho do simulador: ");
        String caminho = scanner.nextLine();

        System.out.print("ID do SHA: ");
        String shaId = scanner.nextLine();

        sincronizador.adicionarSimulador(caminho, shaId);
        System.out.println("\n✅ Simulador adicionado com sucesso!");
        System.out.println("💡 Use opção 8 para salvar a configuração permanentemente.");
    }

    private static void removerSimulador() {
        listarSimuladoresConfigurados();
        System.out.println();
        System.out.print("Caminho do simulador para remover: ");
        String caminho = scanner.nextLine();

        sincronizador.removerSimulador(caminho);
        System.out.println("\n✅ Simulador removido!");
    }

    private static void alterarIntervaloSincronizacao() {
        System.out.print("Novo intervalo (segundos): ");
        int intervalo = lerInteiro();

        if (intervalo < 1) {
            System.out.println("❌ Intervalo deve ser maior que 0");
            return;
        }

        sincronizador.setIntervaloSegundos(intervalo);
        System.out.println("\n✅ Intervalo alterado para " + intervalo + " segundos");

        if (sincronizador.isExecutando()) {
            System.out.println("🔄 Sincronização reiniciada com novo intervalo");
        }
    }

    private static void pararSincronizacao() {
        if (!sincronizador.isExecutando()) {
            System.out.println("⚠️  Sincronização já está parada");
            return;
        }

        sincronizador.parar();
        System.out.println("\n✅ Sincronização parada");
        System.out.println("💡 Use opção 7 para reiniciar quando necessário");
    }

    private static void reiniciarSincronizacao() {
        if (sincronizador.isExecutando()) {
            System.out.println("⚠️  Sincronização já está executando");
            return;
        }

        sincronizador.iniciar();
        System.out.println("\n✅ Sincronização reiniciada");
        System.out.println("Status: " + sincronizador.getStatus());
    }

    private static void salvarConfiguracaoSincronizacao() {
        sincronizador.salvarConfiguracao();
        System.out.println("\n✅ Configuração salva em: config_sincronizacao.txt");
        System.out.println("💡 Esta configuração será carregada automaticamente na próxima execução");
    }

    // ========================================================================
    // UTILITÁRIOS
    // ========================================================================
    
    private static int lerInteiro() {
        try {
            int valor = Integer.parseInt(scanner.nextLine());
            return valor;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static double lerDouble() {
        try {
            double valor = Double.parseDouble(scanner.nextLine());
            return valor;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

