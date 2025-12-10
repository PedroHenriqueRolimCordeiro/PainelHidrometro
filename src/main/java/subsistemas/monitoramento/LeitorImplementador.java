package subsistemas.monitoramento;

import excecoes.ErroDeLeituraImagemException;

/**
 * Interface do Implementador no padrão Bridge.
 * Define a operação de baixo nível para leitura de consumo via imagem.
 *
 * Implementações concretas:
 * - LeitorSimuladoImpl: Retorna valores simulados (desenvolvimento)
 * - LeitorOCRImpl: Usa OCR para ler imagem real (produção)
 *
 * RESTRIÇÃO CRÍTICA (R2):
 * - Leitura de consumo APENAS via arquivo de imagem
 * - NÃO pode acessar classes do SHA diretamente
 *
 * Convenção de caminho das imagens:
 * - saida/leitura_do_hidrometro_<idSHA>.jpg
 *
 * @pattern Bridge (Implementador)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public interface LeitorImplementador {

    /**
     * Lê o consumo de água de uma imagem de hidrômetro.
     *
     * @param caminhoImagem Caminho do arquivo de imagem (ex: saida/leitura_do_hidrometro_101.jpg)
     * @return Consumo lido em metros cúbicos (m³)
     * @throws ErroDeLeituraImagemException Se houver erro na leitura da imagem
     */
    double lerConsumo(String caminhoImagem) throws ErroDeLeituraImagemException;
}

