package subsistemas.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sistema de Log para rastreabilidade, depuração e auditoria.
 * 
 * Funcionalidades (Seção 3.4):
 * - Depuração (debug)
 * - Auditoria
 * - Análise de falhas e alertas
 * 
 * Eventos típicos registrados (Seção 3.4.3):
 * - Login/logout (sucesso/falha)
 * - CRUD de usuários e contas
 * - Configuração de alertas
 * - Disparo de alertas
 * - Início e fim de monitoramento
 * - Falhas de leitura de imagem
 * - Problemas de persistência
 * - Execução, desfazer e refazer de comandos
 * 
 * PADRÃO SINGLETON: Uma única instância para todo o sistema.
 * 
 * @pattern Singleton
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class SistemaLog {
    
    // SINGLETON: Instância única
    private static SistemaLog instancia;
    
    private String caminhoArquivo;
    private DateTimeFormatter formatter;
    private List<String> logsMemoria; // Cache em memória
    private int maxLogsMemoria;
    
    /**
     * Construtor privado (Singleton).
     */
    private SistemaLog() {
        this.caminhoArquivo = "logs/painel.log";
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.logsMemoria = Collections.synchronizedList(new ArrayList<>());
        this.maxLogsMemoria = 1000; // Mantém últimos 1000 logs em memória
        
        // Garante que diretório existe
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao criar diretório de logs: " + e.getMessage());
        }
    }
    
    /**
     * Retorna a instância única (Singleton thread-safe).
     * 
     * @return Instância do SistemaLog
     */
    public static synchronized SistemaLog getInstancia() {
        if (instancia == null) {
            instancia = new SistemaLog();
        }
        return instancia;
    }
    
    // ========================================================================
    // MÉTODOS DE LOG (Seção 3.4.2)
    // ========================================================================
    
    /**
     * Registra mensagem informativa (INFO).
     * 
     * @param msg Mensagem a registrar
     */
    public void info(String msg) {
        registrar("INFO", msg, null);
    }
    
    /**
     * Registra aviso (WARN).
     * 
     * @param msg Mensagem de aviso
     */
    public void warn(String msg) {
        registrar("WARN", msg, null);
    }
    
    /**
     * Registra erro (ERROR).
     * 
     * @param msg Mensagem de erro
     * @param t Exceção associada (opcional)
     */
    public void error(String msg, Throwable t) {
        registrar("ERROR", msg, t);
    }
    
    /**
     * Registra erro sem exceção.
     * 
     * @param msg Mensagem de erro
     */
    public void error(String msg) {
        registrar("ERROR", msg, null);
    }
    
    // ========================================================================
    // REGISTRO INTERNO
    // ========================================================================
    
    /**
     * Registra log no arquivo e em memória.
     * 
     * @param nivel Nível do log (INFO, WARN, ERROR)
     * @param msg Mensagem
     * @param t Exceção (opcional)
     */
    private synchronized void registrar(String nivel, String msg, Throwable t) {
        String timestamp = LocalDateTime.now().format(formatter);
        String linha = String.format("[%s] [%s] %s", timestamp, nivel, msg);
        
        // Adiciona stacktrace se houver exceção
        if (t != null) {
            linha += " | Exceção: " + t.getClass().getSimpleName() + " - " + t.getMessage();
        }
        
        // Salva em memória (mantém últimos N)
        logsMemoria.add(linha);
        if (logsMemoria.size() > maxLogsMemoria) {
            logsMemoria.remove(0); // Remove mais antigo
        }
        
        // Salva em arquivo
        salvarEmArquivo(linha, t);
        // Removido: impressão direta no console para evitar poluir a saída do painel
        // System.out.println(linha);
    }
    
    /**
     * Salva log em arquivo.
     * 
     * @param linha Linha de log formatada
     * @param t Exceção (opcional)
     */
    private void salvarEmArquivo(String linha, Throwable t) {
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(caminhoArquivo, true)))) {
            
            writer.println(linha);
            
            // Se houver exceção, salva stacktrace completo
            if (t != null) {
                t.printStackTrace(writer);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao escrever log: " + e.getMessage());
        }
    }
    
    // ========================================================================
    // CONSULTA DE LOGS
    // ========================================================================
    
    /**
     * Obtém os últimos N logs da memória.
     * Conforme Seção 3.4.2: List<String> obterLogs(int limite)
     * 
     * @param limite Número máximo de logs a retornar
     * @return Lista de logs (mais recentes primeiro)
     */
    public List<String> obterLogs(int limite) {
        List<String> resultado = new ArrayList<>(logsMemoria);
        Collections.reverse(resultado); // Mais recentes primeiro
        
        if (resultado.size() <= limite) {
            return resultado;
        }
        
        return resultado.subList(0, limite);
    }
    
    /**
     * Obtém todos os logs da memória.
     * 
     * @return Lista de todos os logs em memória
     */
    public List<String> obterTodosLogs() {
        List<String> resultado = new ArrayList<>(logsMemoria);
        Collections.reverse(resultado);
        return resultado;
    }
    
    /**
     * Filtra logs por nível.
     * 
     * @param nivel Nível desejado (INFO, WARN, ERROR)
     * @param limite Número máximo a retornar
     * @return Lista filtrada de logs
     */
    public List<String> obterLogsPorNivel(String nivel, int limite) {
        return logsMemoria.stream()
                .filter(log -> log.contains("[" + nivel + "]"))
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    /**
     * Limpa os logs da memória (mantém arquivo).
     */
    public void limparMemoria() {
        logsMemoria.clear();
        info("Logs da memória limpos");
    }
    
    /**
     * Configura o caminho do arquivo de log.
     * 
     * @param caminho Novo caminho
     */
    public void configurarCaminho(String caminho) {
        this.caminhoArquivo = caminho;
        info("Caminho do log alterado para: " + caminho);
    }
}
