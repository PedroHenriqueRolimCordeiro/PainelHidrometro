package subsistemas.monitoramento;

import excecoes.ErroDeLeituraImagemException;
import subsistemas.contas.GerenciadorContas;
import subsistemas.alertas.AlertaObserver;
import dto.ContaAguaDTO;

import java.util.*;
import java.util.concurrent.*;

/**
 * Abstração no padrão Bridge para monitoramento de consumo de hidrômetros.
 *
 * Responsabilidades:
 * - Ler consumo individual de um SHA via imagem
 * - Agregar consumo de múltiplos SHAs de uma conta
 * - Construir caminho das imagens conforme convenção
 *
 * PADRÃO BRIDGE:
 * - Atributo implementador: PROTECTED (obrigatório)
 * - Construtor: EXIGE o implementador (obrigatório)
 * - Delega leitura de baixo nível para LeitorImplementador
 *
 * RESTRIÇÕES CRÍTICAS:
 * - R1: NÃO acessa classes do SHA diretamente
 * - R2: Leitura APENAS via arquivo de imagem
 *
 * Convenção de caminho:
 * - saida/leitura_do_hidrometro_<idSHA>.jpg
 *
 * @pattern Bridge (Abstração)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class MonitorConsumo {

    // ========================================================================
    // BRIDGE - Atributo PROTECTED
    // ========================================================================

    /**
     * Implementador para leitura de imagens.
     * PROTECTED conforme formalidade do padrão.
     */
    protected LeitorImplementador implementador;

    /**
     * Referência ao gerenciador de contas para buscar SHAs vinculados.
     */
    private GerenciadorContas gerenciadorContas;

    /**
     * Diretório onde as imagens dos hidrômetros são armazenadas.
     * Padrão: "Medicoes_202311250023/" (conforme SHA real)
     */
    private String diretorioImagens;

    /**
     * Matrícula SUAP usada pelo SHA para nomear o diretório de medições.
     */
    private String matriculaSUAP;

    // ========================================================================
    // OBSERVER - Lista de observers e estado
    // ========================================================================

    /**
     * Lista de observers que serão notificados.
     * PROTECTED conforme a formalidade do padrão.
     */
    protected ArrayList<AlertaObserver> observers;

    /**
     * Consumo atual (estado observado).
     * PROTECTED conforme formalidade do padrão.
     */
    protected double consumoAtual;

    /**
     * Conta atualmente sendo monitorada (para contexto do Observer).
     */
    protected String contaAtualMonitorada;

    /**
     * Executor para monitoramento periódico por intervalo.
     */
    private ScheduledExecutorService executorMonitoramento;

    /**
     * Mapa de tarefas agendadas por conta.
     */
    private Map<String, ScheduledFuture<?>> tarefasMonitoramento;

    // ========================================================================
    // STATE - Estado do monitoramento
    // ========================================================================

    /**
     * Estado atual do monitoramento.
     * PROTECTED conforme formalidade do padrão.
     */
    protected subsistemas.monitoramento.state.EstadoMonitoramento estadoMonitoramento;

    /**
     * Contador de falhas consecutivas de leitura.
     * Após 3 falhas, muda para estado ERRO.
     */
    private Map<String, Integer> contadorFalhasPorConta;

    // ========================================================================
    // BRIDGE - Construtor EXIGE implementador
    // ========================================================================

    /**
     * Construtor: EXIGE o implementador (padrão Bridge).
     *
     * @param implementador Implementação concreta para leitura de imagens
     * @param gerenciadorContas Gerenciador para buscar contas e SHAs
     */
    public MonitorConsumo(LeitorImplementador implementador,
                          GerenciadorContas gerenciadorContas) {
        this(implementador, gerenciadorContas, "202311250023");
    }

    /**
     * Construtor completo: permite configurar a matrícula SUAP.
     *
     * @param implementador Implementação concreta para leitura de imagens
     * @param gerenciadorContas Gerenciador para buscar contas e SHAs
     * @param matriculaSUAP Matrícula SUAP usada pelo SHA (ex: "202311250023")
     */
    public MonitorConsumo(LeitorImplementador implementador,
                          GerenciadorContas gerenciadorContas,
                          String matriculaSUAP) {
        if (implementador == null) {
            throw new IllegalArgumentException(
                "LeitorImplementador não pode ser nulo (padrão Bridge)"
            );
        }
        if (gerenciadorContas == null) {
            throw new IllegalArgumentException(
                "GerenciadorContas não pode ser nulo"
            );
        }
        if (matriculaSUAP == null || matriculaSUAP.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Matrícula SUAP não pode ser nula ou vazia"
            );
        }

        this.implementador = implementador;
        this.gerenciadorContas = gerenciadorContas;
        this.matriculaSUAP = matriculaSUAP;
        // Agora o diretório é a pasta de medições do simulador
        // Exemplo: /home/pedro/IdeaProjects/SimuladorHidrometro/Medicoes_202311250023/
        this.diretorioImagens = "/home/pedro/IdeaProjects/SimuladorHidrometro/Medicoes_" + matriculaSUAP + "/";

        // OBSERVER: Inicializa lista de observers
        this.observers = new ArrayList<>();
        this.consumoAtual = 0.0;
        this.contaAtualMonitorada = null;

        // Inicializa executor para monitoramento periódico
        this.executorMonitoramento = Executors.newScheduledThreadPool(2);
        this.tarefasMonitoramento = new ConcurrentHashMap<>();

        // STATE: Inicializa estado do monitoramento
        this.estadoMonitoramento = new subsistemas.monitoramento.state.EstadoMonitoramentoParado();
        this.contadorFalhasPorConta = new ConcurrentHashMap<>();
    }

    // ========================================================================
    // LEITURA DE CONSUMO (Alto Nível - Delega para Implementador)
    // ========================================================================

    /**
     * Lê o consumo atual de um hidrômetro (SHA) específico.
     *
     * Processo:
     * 1. Constrói caminho da imagem: Medicoes_<MATRICULA>/<NN>.jpeg
     * 2. Delega leitura para o implementador (LeitorSimuladoImpl ou LeitorOCRImpl)
     * 3. Retorna consumo lido
     *
     * RESTRIÇÃO R2: Leitura APENAS via imagem!
     *
     * Convenção do SHA real:
     * - Diretório: Medicoes_202311250023/
     * - Arquivos: 01.jpeg a 99.jpeg (rollover)
     * - idSHA mapeia para número do arquivo (1-99)
     *
     * @param idSHA ID do hidrômetro SHA (1-99)
     * @return Consumo atual em metros cúbicos (m³)
     * @throws ErroDeLeituraImagemException Se houver erro na leitura da imagem
     */
    public double lerConsumoSHA(int idSHA) throws ErroDeLeituraImagemException {
        // Constrói caminho da imagem conforme convenção (Seção 4.2.3)
        String caminhoImagem = construirCaminhoImagem(idSHA);

        // BRIDGE: Delega para o implementador
        return implementador.lerConsumo(caminhoImagem);
    }

    /**
     * Lê o consumo agregado de uma conta (soma de todos os SHAs vinculados).
     *
     * Processo:
     * 1. Busca a conta pelo número
     * 2. Obtém lista de SHAs vinculados à conta
     * 3. Para cada SHA, lê o consumo via lerConsumoSHA()
     * 4. Soma todos os consumos
     * 5. Retorna total agregado
     *
     * @param numeroConta Número da conta
     * @return Consumo total agregado em metros cúbicos (m³)
     * @throws IllegalArgumentException Se conta não existir
     * @throws ErroDeLeituraImagemException Se houver erro na leitura de alguma imagem
     */
    public double lerConsumoConta(String numeroConta) throws ErroDeLeituraImagemException {
        // Validação
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }

        // Busca a conta
        ContaAguaDTO conta = gerenciadorContas.buscar(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException(
                "Conta não encontrada: " + numeroConta
            );
        }

        // Obtém SHAs vinculados
        Set<Integer> shaIds = conta.getShaIds();
        if (shaIds == null || shaIds.isEmpty()) {
            return 0.0; // Conta sem SHAs vinculados
        }

        // Agrega consumo de todos os SHAs
        double consumoTotal = 0.0;
        for (Integer idSHA : shaIds) {
            try {
                double consumoSHA = lerConsumoSHA(idSHA);
                consumoTotal += consumoSHA;
            } catch (ErroDeLeituraImagemException e) {
                // TODO FASE 7: Registrar erro no SistemaLog
                // Por enquanto, propaga a exceção
                throw new ErroDeLeituraImagemException(
                    "Erro ao ler consumo do SHA " + idSHA + " da conta " + numeroConta,
                    e
                );
            }
        }

        return consumoTotal;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /**
     * Constrói o caminho completo da imagem de um hidrômetro.
     *
     * Convenção do Painel:
     * - O GerenciadorSincronizacao copia periodicamente a imagem mais recente
     *   de cada simulador para o diretório "saida/" com o nome:
     *     saida/leitura_do_hidrometro_<SHAID>.jpg
     *
     * Portanto, para ler o consumo de um SHA específico, basta usar
     * esse caminho padronizado, sem depender da estrutura interna
     * das pastas de medições dos simuladores (Medicoes_*).
     *
     * @param idSHA ID do hidrômetro SHA (ex.: 1001)
     * @return Caminho completo da imagem sincronizada
     */
    private String construirCaminhoImagem(int idSHA) {
        if (idSHA <= 0) {
            throw new IllegalArgumentException("idSHA deve ser positivo");
        }
        // Caminho padronizado gerado pelo GerenciadorSincronizacao
        return "saida/leitura_do_hidrometro_" + idSHA + ".jpg";
    }

    /**
     * Permite alterar o diretório das imagens (configuração).
     *
     * @param diretorioImagens Novo diretório (deve terminar com /)
     */
    public void setDiretorioImagens(String diretorioImagens) {
        if (diretorioImagens == null || diretorioImagens.trim().isEmpty()) {
            throw new IllegalArgumentException("Diretório não pode ser vazio");
        }

        // Garante que termina com /
        if (!diretorioImagens.endsWith("/")) {
            diretorioImagens += "/";
        }

        this.diretorioImagens = diretorioImagens;
    }

    /**
     * Retorna o diretório configurado para as imagens.
     */
    public String getDiretorioImagens() {
        return diretorioImagens;
    }

    // ========================================================================
    // OBSERVER - Gerenciamento de Observers
    // ========================================================================

    /**
     * Adiciona um observer à lista.
     * PADRÃO OBSERVER
     *
     * @param observer Observer a ser adicionado
     */
    public void attach(AlertaObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Remove um observer da lista.
     * PADRÃO OBSERVER
     *
     * @param indice Índice do observer a ser removido
     */
    public void detach(int indice) {
        if (indice >= 0 && indice < observers.size()) {
            observers.remove(indice);
        }
    }

    /**
     * Notifica todos os observers sobre mudança de estado.
     * PADRÃO OBSERVER
     *
     * Este método é chamado automaticamente após cada leitura de consumo.
     */
    private void notifyObservers() {
        for (AlertaObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Retorna o consumo atual (para os observers).
     * PADRÃO OBSERVER
     *
     * @return Consumo atual em m³
     */
    public double getConsumoAtual() {
        return consumoAtual;
    }

    /**
     * Retorna a conta atualmente sendo monitorada (para os observers).
     *
     * @return Número da conta ou null
     */
    public String getContaAtualMonitorada() {
        return contaAtualMonitorada;
    }

    // ========================================================================
    // MONITORAMENTO POR INTERVALO (Seção 4.2.2)
    // ========================================================================

    /**
     * Inicia monitoramento periódico de uma conta em intervalos configurados.
     *
     * Processo (Seção 4.2.2):
     * 1. Localiza todas as imagens dos SHAs associados à conta
     * 2. Para cada SHA, lê o consumo via lerConsumoSHA()
     * 3. Soma todos os consumos da conta
     * 4. Atualiza consumoAtual e contaAtualMonitorada
     * 5. Notifica observers (SistemaAlertas verifica limite)
     * 6. Repete a cada intervaloSegundos
     *
     * @param numeroConta Número da conta
     * @param intervaloSegundos Intervalo entre leituras em segundos
     */
    public void iniciarMonitoramentoConta(String numeroConta, int intervaloSegundos) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta é obrigatório");
        }

        if (intervaloSegundos <= 0) {
            throw new IllegalArgumentException("Intervalo deve ser maior que zero");
        }

        // Para monitoramento anterior se existir
        pararMonitoramentoConta(numeroConta);

        // PADRÃO STATE: Altera estado para INICIADO
        estadoMonitoramento = new subsistemas.monitoramento.state.EstadoMonitoramentoIniciado();

        // Reseta contador de falhas
        contadorFalhasPorConta.put(numeroConta, 0);

        // Cria tarefa periódica
        Runnable tarefaMonitoramento = () -> {
            try {
                // PADRÃO STATE: Verifica se pode executar leitura
                estadoMonitoramento.executarLeitura(this);

                // Se estado não é INICIADO, não executa leitura
                if (!"INICIADO".equals(estadoMonitoramento.getNomeEstado())) {
                    return;
                }

                // Lê consumo da conta
                double consumo = lerConsumoConta(numeroConta);

                // Registra sucesso (reseta contador de falhas)
                registrarSucessoLeitura(numeroConta);

                // Atualiza estado e notifica observers (PADRÃO OBSERVER)
                this.consumoAtual = consumo;
                this.contaAtualMonitorada = numeroConta;
                notifyObservers();

                System.out.println("📊 Monitoramento [" + numeroConta + "]: " +
                    String.format("%.2f m³", consumo));

            } catch (ErroDeLeituraImagemException e) {
                // Registra falha (após 3 falhas → estado ERRO)
                registrarFalhaLeitura(numeroConta, e);
            }
        };

        // Agenda execução periódica
        ScheduledFuture<?> futureTask = executorMonitoramento.scheduleAtFixedRate(
            tarefaMonitoramento,
            0,                      // Delay inicial (executa imediatamente)
            intervaloSegundos,      // Período
            TimeUnit.SECONDS
        );

        tarefasMonitoramento.put(numeroConta, futureTask);

        System.out.println("✅ Monitoramento iniciado: " + numeroConta +
            " (intervalo: " + intervaloSegundos + "s)");
    }

    /**
     * Para o monitoramento periódico de uma conta.
     *
     * @param numeroConta Número da conta
     */
    public void pararMonitoramentoConta(String numeroConta) {
        if (numeroConta == null) {
            return;
        }

        ScheduledFuture<?> tarefa = tarefasMonitoramento.remove(numeroConta);
        if (tarefa != null) {
            tarefa.cancel(false);

            // PADRÃO STATE: Altera estado para PARADO
            estadoMonitoramento.parar(this);

            System.out.println("⏹️  Monitoramento parado: " + numeroConta);
        }
    }

    /**
     * Verifica se o monitoramento de uma conta está ativo.
     *
     * @param numeroConta Número da conta
     * @return true se monitoramento está ativo, false caso contrário
     */
    public boolean isMonitoramentoAtivo(String numeroConta) {
        if (numeroConta == null) {
            return false;
        }

        ScheduledFuture<?> tarefa = tarefasMonitoramento.get(numeroConta);
        return tarefa != null && !tarefa.isDone() && !tarefa.isCancelled();
    }

    /**
     * Finaliza o executor de monitoramento (cleanup).
     * Deve ser chamado ao encerrar a aplicação.
     */
    public void shutdown() {
        if (executorMonitoramento != null && !executorMonitoramento.isShutdown()) {
            executorMonitoramento.shutdownNow();
        }
    }

    // ========================================================================
    // STATE - Gerenciamento de Estado do Monitoramento
    // ========================================================================

    /**
     * Retorna o estado atual do monitoramento.
     * PADRÃO STATE
     *
     * @return Estado atual
     */
    public subsistemas.monitoramento.state.EstadoMonitoramento getEstadoMonitoramento() {
        return estadoMonitoramento;
    }

    /**
     * Define o estado do monitoramento.
     * PADRÃO STATE
     * Usado internamente pelos estados concretos.
     *
     * @param estadoMonitoramento Novo estado
     */
    public void setEstadoMonitoramento(subsistemas.monitoramento.state.EstadoMonitoramento estadoMonitoramento) {
        this.estadoMonitoramento = estadoMonitoramento;
    }

    /**
     * Pausa o monitoramento (via State).
     * PADRÃO STATE: Delega para o estado atual.
     *
     * @param numeroConta Número da conta
     */
    public void pausarMonitoramento(String numeroConta) {
        if (numeroConta == null || !isMonitoramentoAtivo(numeroConta)) {
            System.out.println("⚠️  Conta não está sendo monitorada: " + numeroConta);
            return;
        }

        // PADRÃO STATE: Delega para o estado
        estadoMonitoramento.pausar(this);
    }

    /**
     * Retoma o monitoramento pausado (via State).
     * PADRÃO STATE: Delega para o estado atual.
     *
     * @param numeroConta Número da conta
     */
    public void retomarMonitoramento(String numeroConta) {
        if (numeroConta == null) {
            System.out.println("⚠️  Número da conta é obrigatório");
            return;
        }

        // PADRÃO STATE: Delega para o estado
        estadoMonitoramento.retomar(this);
    }

    // ========================================================================
    // TRATAMENTO DE FALHAS (3 falhas → Estado ERRO)
    // ========================================================================

    /**
     * Registra uma falha de leitura para uma conta.
     * Após 3 falhas consecutivas, muda para estado ERRO.
     *
     * @param numeroConta Número da conta
     * @param erro Exceção que causou a falha
     */
    public void registrarFalhaLeitura(String numeroConta, Exception erro) {
        int falhas = contadorFalhasPorConta.getOrDefault(numeroConta, 0) + 1;
        contadorFalhasPorConta.put(numeroConta, falhas);

        System.err.println("❌ Falha de leitura #" + falhas + " [" + numeroConta + "]: " +
            erro.getMessage());

        // Após 3 falhas consecutivas, muda para estado ERRO
        if (falhas >= 3) {
            estadoMonitoramento.parar(this);
            estadoMonitoramento = new subsistemas.monitoramento.state.EstadoMonitoramentoErro(
                "3 falhas consecutivas na leitura de " + numeroConta
            );
            System.err.println("🔴 Estado alterado para ERRO: " + numeroConta);
        }
    }

    /**
     * Registra sucesso na leitura (reseta contador de falhas).
     *
     * @param numeroConta Número da conta
     */
    public void registrarSucessoLeitura(String numeroConta) {
        contadorFalhasPorConta.put(numeroConta, 0);
    }

    /**
     * Reseta o contador de falhas (usado ao reiniciar após erro).
     */
    public void resetarContadorFalhas() {
        contadorFalhasPorConta.clear();
    }
}

