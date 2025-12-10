package subsistemas.monitoramento;

import excecoes.ErroDeLeituraImagemException;
import java.util.Random;

/**
 * Implementação simulada do LeitorImplementador.
 * Retorna valores aleatórios entre 0 e 100 m³ para desenvolvimento e testes.
 *
 * NÃO faz leitura real de imagem - apenas simula o comportamento.
 * Útil para:
 * - Desenvolvimento sem dependência de imagens reais
 * - Testes automatizados
 * - Demonstração do sistema
 *
 * PRODUÇÃO: Substituir por LeitorOCRImpl
 *
 * @pattern Bridge (Implementação Concreta)
 * @author Pedro Henrique
 * @date 2025-12-07
 */
public class LeitorSimuladoImpl implements LeitorImplementador {

    private Random random;

    /**
     * Construtor: inicializa gerador de números aleatórios
     */
    public LeitorSimuladoImpl() {
        this.random = new Random();
    }

    /**
     * Simula leitura de consumo retornando valor aleatório.
     *
     * @param caminhoImagem Caminho da imagem (não é usado na simulação)
     * @return Consumo simulado entre 0.0 e 100.0 m³
     * @throws ErroDeLeituraImagemException Se o caminho for inválido
     */
    @Override
    public double lerConsumo(String caminhoImagem) throws ErroDeLeituraImagemException {
        // Validação básica
        if (caminhoImagem == null || caminhoImagem.trim().isEmpty()) {
            throw new ErroDeLeituraImagemException(
                "Caminho da imagem não pode ser nulo ou vazio"
            );
        }

        // Simula leitura: retorna valor aleatório entre 0 e 100 m³
        // Usa 2 casas decimais para simular precisão de medição
        double consumo = random.nextDouble() * 100.0;
        return Math.round(consumo * 100.0) / 100.0;
    }
}

