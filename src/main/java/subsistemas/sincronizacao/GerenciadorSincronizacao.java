package subsistemas.sincronizacao;

import subsistemas.log.SistemaLog;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Gerenciador de sincronização de múltiplos simuladores de hidrômetro.
 * Executa em background e sincroniza automaticamente as imagens dos simuladores
 * para o diretório saida/ do painel.
 *
 * @pattern Singleton
 * @author Pedro Henrique
 */
public class GerenciadorSincronizacao {

    private static GerenciadorSincronizacao instancia;
    private final SistemaLog log;

    private ScheduledExecutorService executor;
    private final Map<String, String> simuladores; // Caminho -> SHA ID
    private final String diretorioSaida;
    private boolean executando = false;
    private int intervaloSegundos = 1;

    /**
     * Construtor privado (Singleton)
     */
    private GerenciadorSincronizacao() {
        this.log = SistemaLog.getInstancia();
        this.simuladores = new LinkedHashMap<>();
        this.diretorioSaida = "saida";

        // Criar diretório se não existir
        criarDiretorioSaida();

        // Carregar configuração padrão
        carregarConfiguracaoPadrao();
    }

    /**
     * Obtém instância única (Singleton)
     */
    public static synchronized GerenciadorSincronizacao getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorSincronizacao();
        }
        return instancia;
    }

    /**
     * Cria o diretório de saída se não existir
     */
    private void criarDiretorioSaida() {
        try {
            Path path = Paths.get(diretorioSaida);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Diretório de saída criado: " + diretorioSaida);
            }
        } catch (IOException e) {
            log.error("Erro ao criar diretório de saída: " + e.getMessage());
        }
    }

    /**
     * Carrega configuração padrão dos simuladores
     */
    private void carregarConfiguracaoPadrao() {
        // Tentar carregar de arquivo de configuração
        File configFile = new File("config_sincronizacao.txt");
        if (configFile.exists()) {
            carregarDeArquivo(configFile);
        } else {
            // Configuração padrão
            String caminhoSimulador = System.getProperty("user.home") +
                                     "/IdeaProjects/SimuladorHidrometro";
            adicionarSimulador(caminhoSimulador, "1001");
            log.info("Configuração padrão carregada: 1 simulador");
        }
    }

    /**
     * Carrega simuladores de arquivo de configuração
     */
    private void carregarDeArquivo(File arquivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int count = 0;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                String[] partes = linha.split(":");
                if (partes.length == 2) {
                    adicionarSimulador(partes[0].trim(), partes[1].trim());
                    count++;
                }
            }
            log.info("Configuração carregada de arquivo: " + count + " simulador(es)");
        } catch (IOException e) {
            log.error("Erro ao carregar configuração: " + e.getMessage());
        }
    }

    /**
     * Adiciona um simulador para sincronização
     */
    public void adicionarSimulador(String caminho, String shaId) {
        simuladores.put(caminho, shaId);
        log.info("Simulador adicionado: SHA " + shaId + " -> " + caminho);
    }

    /**
     * Remove um simulador
     */
    public void removerSimulador(String caminho) {
        String shaId = simuladores.remove(caminho);
        if (shaId != null) {
            log.info("Simulador removido: SHA " + shaId);
        }
    }

    /**
     * Lista todos os simuladores configurados
     */
    public Map<String, String> listarSimuladores() {
        return new LinkedHashMap<>(simuladores);
    }

    /**
     * Define o intervalo de sincronização
     */
    public void setIntervaloSegundos(int segundos) {
        this.intervaloSegundos = segundos;
        if (executando) {
            parar();
            iniciar();
        }
    }

    /**
     * Inicia a sincronização automática em background
     */
    public void iniciar() {
        if (executando) {
            log.warn("Sincronização já está em execução");
            return;
        }

        if (simuladores.isEmpty()) {
            log.warn("Nenhum simulador configurado para sincronização");
            return;
        }

        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SincronizadorSHA");
            t.setDaemon(true);
            return t;
        });

        executor.scheduleWithFixedDelay(
            this::sincronizarTodos,
            0,
            intervaloSegundos,
            TimeUnit.SECONDS
        );

        executando = true;
        log.info("Sincronização automática iniciada: " + simuladores.size() +
                " simulador(es), intervalo de " + intervaloSegundos + "s");
    }

    /**
     * Para a sincronização automática
     */
    public void parar() {
        if (!executando) {
            return;
        }

        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        executando = false;
        log.info("Sincronização automática parada");
    }

    /**
     * Verifica se está executando
     */
    public boolean isExecutando() {
        return executando;
    }

    /**
     * Sincroniza todos os simuladores (ciclo único)
     */
    private void sincronizarTodos() {
        int sucesso = 0;
        int falhas = 0;

        for (Map.Entry<String, String> entry : simuladores.entrySet()) {
            String caminho = entry.getKey();
            String shaId = entry.getValue();

            try {
                if (sincronizarSimulador(caminho, shaId)) {
                    sucesso++;
                } else {
                    falhas++;
                }
            } catch (Exception e) {
                falhas++;
                log.error("Erro ao sincronizar SHA " + shaId + ": " + e.getMessage());
            }
        }

        if (sucesso > 0 || falhas > 0) {
            log.info(String.format("Sincronização: %d sucesso(s), %d falha(s), %d total",
                                  sucesso, falhas, simuladores.size()));
        }
    }

    /**
     * Sincroniza um simulador específico
     */
    private boolean sincronizarSimulador(String caminhoSimulador, String shaId) {
        try {
            Path dirSimulador = Paths.get(caminhoSimulador);

            // Verificar se o diretório existe
            if (!Files.exists(dirSimulador) || !Files.isDirectory(dirSimulador)) {
                return false;
            }

            // Procurar pasta de medições mais recente
            Path pastaMaisRecente = encontrarPastaMedicoesRecente(dirSimulador);
            if (pastaMaisRecente == null) {
                return false;
            }

            // Procurar imagem mais recente na pasta
            Path imagemRecente = encontrarImagemRecente(pastaMaisRecente);
            if (imagemRecente == null) {
                return false;
            }

            // Copiar para diretório de saída com nome padronizado
            Path destino = Paths.get(diretorioSaida,
                                    "leitura_do_hidrometro_" + shaId + ".jpg");

            Files.copy(imagemRecente, destino, StandardCopyOption.REPLACE_EXISTING);

            long tamanho = Files.size(destino);
            log.info(String.format("SHA %s sincronizado: %s (%.1f KB)",
                                  shaId, imagemRecente.getFileName(), tamanho / 1024.0));

            return true;

        } catch (IOException e) {
            log.error("Erro ao sincronizar SHA " + shaId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Encontra a pasta de medições mais recente.
     *
     * Suporta dois cenários:
     * 1) dirSimulador é um diretório PAI que contém subpastas "Medicoes_*";
     * 2) dirSimulador já é a própria pasta de medições (ex.: "Medicoes_" ou "Medicoes_20231125...").
     */
    private Path encontrarPastaMedicoesRecente(Path dirSimulador) throws IOException {
        Path maisRecente = null;
        long timestampMaisRecente = 0L;

        // Primeiro, tentar encontrar subpastas "Medicoes_*" dentro do diretório informado
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirSimulador, "Medicoes*")) {
            for (Path pasta : stream) {
                if (Files.isDirectory(pasta)) {
                    long timestamp = Files.getLastModifiedTime(pasta).toMillis();
                    if (timestamp > timestampMaisRecente) {
                        timestampMaisRecente = timestamp;
                        maisRecente = pasta;
                    }
                }
            }
        }

        // Fallback: se não encontrou nenhuma subpasta, verificar se o PRÓPRIO dirSimulador
        // já é uma pasta de medições (caso 2: simulador do João, etc.).
        if (maisRecente == null && Files.isDirectory(dirSimulador)) {
            String nome = dirSimulador.getFileName().toString();
            // Aceita nomes que começam com "Medicoes" (sem acento), independente de sufixo
            // e também o caso simples "Medicoes_" usado em alguns simuladores.
            if (nome.startsWith("Medicoes")) {
                maisRecente = dirSimulador;
            }
        }

        return maisRecente;
    }

    /**
     * Encontra a imagem mais recente na pasta
     */
    private Path encontrarImagemRecente(Path pasta) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta,
                                                path -> {
                                                    String nome = path.getFileName().toString().toLowerCase();
                                                    return nome.endsWith(".jpg") ||
                                                           nome.endsWith(".jpeg");
                                                })) {
            Path maisRecente = null;
            long timestampMaisRecente = 0;

            for (Path imagem : stream) {
                long timestamp = Files.getLastModifiedTime(imagem).toMillis();
                if (timestamp > timestampMaisRecente) {
                    timestampMaisRecente = timestamp;
                    maisRecente = imagem;
                }
            }

            return maisRecente;
        }
    }

    /**
     * Salva configuração atual em arquivo
     */
    public void salvarConfiguracao() {
        File configFile = new File("config_sincronizacao.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
            writer.println("# Configuração de Sincronização de Simuladores");
            writer.println("# Formato: /caminho/para/simulador:SHAID");
            writer.println();

            for (Map.Entry<String, String> entry : simuladores.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }

            log.info("Configuração salva em: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Erro ao salvar configuração: " + e.getMessage());
        }
    }

    /**
     * Retorna status da sincronização
     */
    public String getStatus() {
        if (!executando) {
            return "Parado";
        }
        return String.format("Executando (%d simulador(es), intervalo de %ds)",
                           simuladores.size(), intervaloSegundos);
    }
}
