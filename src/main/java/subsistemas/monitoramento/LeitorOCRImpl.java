package subsistemas.monitoramento;

import excecoes.ErroDeLeituraImagemException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementação OCR real do LeitorImplementador usando Tess4J/Tesseract.
 *,
 * - Ler diretamente a imagem do hidrômetro
 * - Usar Tesseract com PSM 7 (uma linha) e whitelist numérica
 * - Extrair e normalizar o maior número encontrado como consumo em m³
 */
public class LeitorOCRImpl implements LeitorImplementador {

    private final ITesseract tesseract;

    public LeitorOCRImpl() {
        this.tesseract = new Tesseract();
        // Datapath padrão do Tesseract instalado via apt no Ubuntu 24.04
        // Ajuste se seu tessdata estiver em outro local.
        this.tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        this.tesseract.setLanguage("eng");
        this.tesseract.setPageSegMode(7); // uma única linha de texto (visor)
        this.tesseract.setOcrEngineMode(1); // LSTM only
        this.tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");
    }

    @Override
    public double lerConsumo(String caminhoImagem) throws ErroDeLeituraImagemException {
        if (caminhoImagem == null || caminhoImagem.trim().isEmpty()) {
            throw new ErroDeLeituraImagemException("Caminho da imagem não pode ser nulo ou vazio");
        }

        File arquivo = new File(caminhoImagem);
        if (!arquivo.exists() || !arquivo.isFile() || !arquivo.canRead()) {
            throw new ErroDeLeituraImagemException(
                    "Arquivo de imagem inválido ou sem permissão de leitura: " + arquivo.getAbsolutePath()
            );
        }

        try {
            String texto = tesseract.doOCR(arquivo);

            if (texto == null || texto.trim().isEmpty()) {
                throw new ErroDeLeituraImagemException(
                        "OCR não retornou nenhum texto para a imagem: " + arquivo.getAbsolutePath()
                );
            }

            // Apenas para debug pontual: descomente se quiser ver o bruto
            // System.out.println("===== OCR bruto da imagem " + arquivo.getName() + " =====");
            // System.out.println(texto);
            // System.out.println("========================================================");

            List<String> candidatos = new ArrayList<>();
            Matcher m = Pattern.compile("[0-9]+").matcher(texto);
            while (m.find()) {
                candidatos.add(m.group());
            }

            if (candidatos.isEmpty()) {
                throw new ErroDeLeituraImagemException(
                        "Não foi possível encontrar números na imagem: " + arquivo.getAbsolutePath() +
                                "\nTexto OCR: " + texto
                );
            }

            Double melhorValor = null;
            String melhorBruto = null;

            for (String bruto : candidatos) {
                try {
                    double valor = Double.parseDouble(bruto);
                    // Intervalo razoável para consumo em m³
                    if (valor < 0 || valor > 999999) {
                        continue;
                    }
                    if (melhorValor == null || valor > melhorValor) {
                        melhorValor = valor;
                        melhorBruto = bruto;
                    }
                } catch (NumberFormatException ignore) {
                    // ignora este candidato e tenta o próximo
                }
            }

            if (melhorValor == null) {
                throw new ErroDeLeituraImagemException(
                        "Não foi possível converter texto OCR em número válido. Candidatos: " + candidatos +
                                "\nTexto OCR: " + texto
                );
            }

            return melhorValor;

        } catch (TesseractException e) {
            throw new ErroDeLeituraImagemException(
                    "Erro de OCR ao processar a imagem: " + arquivo.getAbsolutePath(), e
            );
        }
    }
}
