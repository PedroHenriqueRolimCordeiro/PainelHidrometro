package subsistemas.persistencia;

import modelo.Usuario;
import modelo.ContaAgua;
import modelo.Alerta;
import modelo.enums.PerfilUsuario;
import modelo.enums.TipoEstadoConta;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementação de persistência em arquivos JSON locais.
 * Usa JSON NATIVO do Java (sem bibliotecas externas).
 *
 * @pattern Bridge (Implementação Concreta)
 * @author Pedro Henrique
 * @date 2025-12-08
 */
public class PersistenciaArquivoImpl implements PersistenciaImplementador {

    private String diretorioBase;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public PersistenciaArquivoImpl() {
        this("dados/");
    }

    public PersistenciaArquivoImpl(String diretorioBase) {
        this.diretorioBase = diretorioBase;
        criarDiretorioSeNaoExistir(diretorioBase);
    }

    @Override
    public void gravarRegistro(String tipoEntidade, String chave, Object dados) throws Exception {
        validarParametros(tipoEntidade, chave, dados);
        
        String dirEntidade = obterDiretorioEntidade(tipoEntidade);
        criarDiretorioSeNaoExistir(dirEntidade);
        
        String json = converterParaJson(dados);
        String caminhoArquivo = obterCaminhoArquivo(tipoEntidade, chave);
        
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.write(json);
        }
    }

    @Override
    public <T> T lerRegistro(String tipoEntidade, String chave, Class<T> classe) throws Exception {
        if (tipoEntidade == null || chave == null || classe == null) {
            return null;
        }

        String caminhoArquivo = obterCaminhoArquivo(tipoEntidade, chave);
        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                sb.append(linha);
            }
        }

        return converterDeJson(sb.toString(), classe);
    }

    @Override
    public <T> List<T> listarRegistros(String tipoEntidade, Class<T> classe) throws Exception {
        List<T> resultados = new ArrayList<>();

        if (tipoEntidade == null || classe == null) {
            return resultados;
        }

        String dirEntidade = obterDiretorioEntidade(tipoEntidade);
        File diretorio = new File(dirEntidade);

        if (!diretorio.exists() || !diretorio.isDirectory()) {
            return resultados;
        }

        File[] arquivos = diretorio.listFiles((dir, nome) -> nome.endsWith(".json"));
        if (arquivos == null) {
            return resultados;
        }

        for (File arquivo : arquivos) {
            try {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        sb.append(linha);
                    }
                }
                T objeto = converterDeJson(sb.toString(), classe);
                if (objeto != null) {
                    resultados.add(objeto);
                }
            } catch (Exception e) {
                System.err.println("Erro ao ler arquivo: " + arquivo.getName() + " - " + e.getMessage());
            }
        }

        return resultados;
    }

    @Override
    public boolean removerRegistro(String tipoEntidade, String chave) throws Exception {
        if (tipoEntidade == null || chave == null) {
            return false;
        }

        String caminhoArquivo = obterCaminhoArquivo(tipoEntidade, chave);
        File arquivo = new File(caminhoArquivo);

        return arquivo.exists() && arquivo.delete();
    }

    @Override
    public boolean existeRegistro(String tipoEntidade, String chave) throws Exception {
        if (tipoEntidade == null || chave == null) {
            return false;
        }

        String caminhoArquivo = obterCaminhoArquivo(tipoEntidade, chave);
        return new File(caminhoArquivo).exists();
    }

    // ========================================================================
    // CONVERSÃO JSON MANUAL (SEM GSON)
    // ========================================================================

    private String converterParaJson(Object objeto) {
        if (objeto instanceof Usuario) {
            return usuarioParaJson((Usuario) objeto);
        } else if (objeto instanceof ContaAgua) {
            return contaParaJson((ContaAgua) objeto);
        } else if (objeto instanceof Alerta) {
            return alertaParaJson((Alerta) objeto);
        }
        throw new IllegalArgumentException("Tipo não suportado: " + objeto.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private <T> T converterDeJson(String json, Class<T> classe) {
        if (classe == Usuario.class) {
            return (T) jsonParaUsuario(json);
        } else if (classe == ContaAgua.class) {
            return (T) jsonParaConta(json);
        } else if (classe == Alerta.class) {
            return (T) jsonParaAlerta(json);
        }
        throw new IllegalArgumentException("Tipo não suportado: " + classe.getName());
    }

    // Usuario -> JSON
    private String usuarioParaJson(Usuario u) {
        return String.format(
            "{\n" +
            "  \"cpf\": \"%s\",\n" +
            "  \"nome\": \"%s\",\n" +
            "  \"email\": \"%s\",\n" +
            "  \"telefone\": \"%s\",\n" +
            "  \"endereco\": \"%s\",\n" +
            "  \"perfil\": \"%s\"\n" +
            "}",
            escapar(u.getCpf()),
            escapar(u.getNome()),
            escapar(u.getEmail()),
            escapar(u.getTelefone()),
            escapar(u.getEndereco()),
            u.getPerfil().name()
        );
    }

    // JSON -> Usuario
    private Usuario jsonParaUsuario(String json) {
        String cpf = extrairValor(json, "cpf");
        String nome = extrairValor(json, "nome");
        String email = extrairValor(json, "email");
        String telefone = extrairValor(json, "telefone");
        String endereco = extrairValor(json, "endereco");
        String perfilStr = extrairValor(json, "perfil");
        PerfilUsuario perfil = PerfilUsuario.valueOf(perfilStr);

        return new Usuario(cpf, nome, email, telefone, endereco, perfil);
    }

    // ContaAgua -> JSON
    private String contaParaJson(ContaAgua c) {
        StringBuilder shasJson = new StringBuilder("[");
        Set<Integer> shas = c.getShaIds();
        int count = 0;
        for (Integer sha : shas) {
            if (count > 0) shasJson.append(", ");
            shasJson.append(sha);
            count++;
        }
        shasJson.append("]");

        return String.format(
            "{\n" +
            "  \"numeroConta\": \"%s\",\n" +
            "  \"cpfUsuario\": \"%s\",\n" +
            "  \"tipoEstado\": \"%s\",\n" +
            "  \"shaIds\": %s,\n" +
            "  \"limiteConsumo\": %.2f,\n" +
            "  \"consumoAcumulado\": %.2f\n" +
            "}",
            escapar(c.getNumeroConta()),
            escapar(c.getCpfUsuario()),
            c.getTipoEstado().name(),
            shasJson.toString(),
            c.getLimiteConsumo(),
            c.getConsumoAcumulado()
        );
    }

    // JSON -> ContaAgua
    private ContaAgua jsonParaConta(String json) {
        String numeroConta = extrairValor(json, "numeroConta");
        String cpfUsuario = extrairValor(json, "cpfUsuario");
        String estadoStr = extrairValor(json, "tipoEstado");
        TipoEstadoConta estado = TipoEstadoConta.valueOf(estadoStr);

        ContaAgua conta = new ContaAgua(numeroConta, cpfUsuario);

        // Extrai SHAs (array de inteiros)
        String shasStr = extrairArray(json, "shaIds");
        if (shasStr != null && !shasStr.isEmpty()) {
            String[] shaArray = shasStr.split(",");
            for (String shaStr : shaArray) {
                try {
                    int sha = Integer.parseInt(shaStr.trim());
                    conta.adicionarSHA(sha);
                } catch (NumberFormatException ignored) {}
            }
        }

        try {
            double limite = Double.parseDouble(extrairValor(json, "limiteConsumo"));
            conta.setLimiteConsumo(limite);
        } catch (Exception ignored) {}

        try {
            double consumo = Double.parseDouble(extrairValor(json, "consumoAcumulado"));
            conta.setConsumoAcumulado(consumo);
        } catch (Exception ignored) {}

        return conta;
    }

    // Alerta -> JSON
    private String alertaParaJson(Alerta a) {
        return String.format(
            "{\n" +
            "  \"id\": %d,\n" +
            "  \"numeroConta\": \"%s\",\n" +
            "  \"consumoAtual\": %.2f,\n" +
            "  \"limiteConfigurado\": %.2f,\n" +
            "  \"dataHora\": \"%s\",\n" +
            "  \"lido\": %s\n" +
            "}",
            a.getId(),
            escapar(a.getNumeroConta()),
            a.getConsumoAtual(),
            a.getLimiteConfigurado(),
            a.getDataHora().format(FORMATTER),
            a.isLido()
        );
    }

    // JSON -> Alerta
    private Alerta jsonParaAlerta(String json) {
        int id = Integer.parseInt(extrairValor(json, "id"));
        String numeroConta = extrairValor(json, "numeroConta");
        double consumoAtual = Double.parseDouble(extrairValor(json, "consumoAtual"));
        double limite = Double.parseDouble(extrairValor(json, "limiteConfigurado"));
        String dataHoraStr = extrairValor(json, "dataHora");
        LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr, FORMATTER);
        boolean lido = Boolean.parseBoolean(extrairValor(json, "lido"));

        // Cria Alerta com construtor completo (cpfUsuario será null por enquanto)
        Alerta alerta = new Alerta(id, numeroConta, "", consumoAtual, limite, dataHora);
        if (lido) {
            alerta.marcarComoLido();
        }
        return alerta;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    private String extrairValor(String json, String chave) {
        String pattern = "\"" + chave + "\":\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }

        // Tenta sem aspas (para números/booleanos)
        pattern = "\"" + chave + "\":\\s*([^,\\}\\n]*)";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(json);
        if (m.find()) {
            return m.group(1).trim();
        }

        return "";
    }

    private String extrairArray(String json, String chave) {
        String pattern = "\"" + chave + "\":\\s*\\[([^\\]]*)\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private String escapar(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }

    private void validarParametros(String tipoEntidade, String chave, Object dados) {
        if (tipoEntidade == null || tipoEntidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de entidade não pode ser nulo ou vazio");
        }
        if (chave == null || chave.trim().isEmpty()) {
            throw new IllegalArgumentException("Chave não pode ser nula ou vazia");
        }
        if (dados == null) {
            throw new IllegalArgumentException("Dados não podem ser nulos");
        }
    }

    private String obterDiretorioEntidade(String tipoEntidade) {
        return diretorioBase + tipoEntidade + "/";
    }

    private String obterCaminhoArquivo(String tipoEntidade, String chave) {
        String chaveLimpa = chave.replace("/", "_").replace("\\", "_");
        return obterDiretorioEntidade(tipoEntidade) + chaveLimpa + ".json";
    }

    private void criarDiretorioSeNaoExistir(String caminho) {
        try {
            Path path = Paths.get(caminho);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório: " + caminho + " - " + e.getMessage());
        }
    }
}

