package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.FinanceiroCustoResponseDTO;
import br.com.vaztech.vaztech.dto.FinanceiroFaturamentoAnualResponseDTO;
import br.com.vaztech.vaztech.dto.FinanceiroFaturamentoResponseDTO;
import br.com.vaztech.vaztech.dto.FinanceiroLucroResponseDTO;
import br.com.vaztech.vaztech.dto.FaturamentoPorMesDTO;
import br.com.vaztech.vaztech.repository.OperacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    @Autowired
    private OperacaoRepository operacaoRepository;

    private static final Integer TIPO_VENDA = 0;
    private static final Integer TIPO_COMPRA = 1;

    public FinanceiroFaturamentoResponseDTO getFaturamentoMensal(
            Integer anoAtual, Integer mesAtual,
            Integer anoComparacao, Integer mesComparacao) {

        BigDecimal faturamentoAtual = operacaoRepository.sumValorByTipoAndAnoAndMes(
                TIPO_VENDA, anoAtual, mesAtual);
        String anoMesAtualStr = String.format("%d-%02d", anoAtual, mesAtual);

        BigDecimal faturamentoComparacao = null;
        String anoMesComparacaoStr = null;
        BigDecimal margem = null;

        if (anoComparacao != null && mesComparacao != null) {
            faturamentoComparacao = operacaoRepository.sumValorByTipoAndAnoAndMes(
                    TIPO_VENDA, anoComparacao, mesComparacao);
            anoMesComparacaoStr = String.format("%d-%02d", anoComparacao, mesComparacao);

            margem = calcularMargem(faturamentoAtual, faturamentoComparacao);
        }

        return new FinanceiroFaturamentoResponseDTO(
                faturamentoAtual,
                anoMesAtualStr,
                faturamentoComparacao,
                anoMesComparacaoStr,
                margem
        );
    }

    public FinanceiroFaturamentoAnualResponseDTO getFaturamentoAnual(Integer ano) {

        List<FaturamentoPorMesDTO> faturamentoAgrupado =
                operacaoRepository.findFaturamentoByAnoGroupByMes(TIPO_VENDA, ano);

        Map<Integer, BigDecimal> mapaFaturamento = faturamentoAgrupado.stream()
                .collect(Collectors.toMap(FaturamentoPorMesDTO::getMes, FaturamentoPorMesDTO::getTotal));

        List<FinanceiroFaturamentoAnualResponseDTO.FaturamentoMensalDetalhe> detalhamentoMensal =
                new ArrayList<>();

        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        Locale localeBrasil = new Locale("pt", "BR");

        for (int mes = 1; mes <= 12; mes++) {
            BigDecimal totalMes = mapaFaturamento.getOrDefault(mes, BigDecimal.ZERO);
            String nomeMes = Month.of(mes).getDisplayName(TextStyle.FULL, localeBrasil);

            detalhamentoMensal.add(
                    new FinanceiroFaturamentoAnualResponseDTO.FaturamentoMensalDetalhe(
                            mes,
                            // ("janeiro" -> "Janeiro")
                            nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1),
                            totalMes
                    )
            );
            faturamentoTotal = faturamentoTotal.add(totalMes);
        }

        return new FinanceiroFaturamentoAnualResponseDTO(
                ano,
                faturamentoTotal.setScale(2, RoundingMode.HALF_UP),
                detalhamentoMensal
        );
    }


     //Calcula o lucro mensal (Faturamento - Custo) e permite comparação.
    public FinanceiroLucroResponseDTO getLucroMensal(
            Integer anoAtual, Integer mesAtual,
            Integer anoComparacao, Integer mesComparacao) {

        // Cálculo do Lucro Atual
        BigDecimal faturamentoAtual = operacaoRepository.sumValorByTipoAndAnoAndMes(
                TIPO_VENDA, anoAtual, mesAtual);
        BigDecimal custoAtual = operacaoRepository.sumValorByTipoAndAnoAndMes(
                TIPO_COMPRA, anoAtual, mesAtual);
        BigDecimal lucroAtual = faturamentoAtual.subtract(custoAtual);
        String anoMesAtualStr = String.format("%d-%02d", anoAtual, mesAtual);

        BigDecimal lucroComparacao = null;
        String anoMesComparacaoStr = null;
        BigDecimal margem = null;

        // Cálculo da Comparação
        if (anoComparacao != null && mesComparacao != null) {
            BigDecimal faturamentoComparacao = operacaoRepository.sumValorByTipoAndAnoAndMes(
                    TIPO_VENDA, anoComparacao, mesComparacao);
            BigDecimal custoComparacao = operacaoRepository.sumValorByTipoAndAnoAndMes(
                    TIPO_COMPRA, anoComparacao, mesComparacao);
            lucroComparacao = faturamentoComparacao.subtract(custoComparacao);
            anoMesComparacaoStr = String.format("%d-%02d", anoComparacao, mesComparacao);

            margem = calcularMargem(lucroAtual, lucroComparacao);
        }

        return new FinanceiroLucroResponseDTO(
                lucroAtual,
                anoMesAtualStr,
                lucroComparacao,
                anoMesComparacaoStr,
                margem
        );
    }

    //Calcula o custo mensal (Operações de Compra) e permite comparação.
    public FinanceiroCustoResponseDTO getCustoMensal(
            Integer anoAtual, Integer mesAtual,
            Integer anoComparacao, Integer mesComparacao) {

        BigDecimal custoAtual = operacaoRepository.sumValorByTipoAndAnoAndMes(
                TIPO_COMPRA, anoAtual, mesAtual);
        String anoMesAtualStr = String.format("%d-%02d", anoAtual, mesAtual);

        BigDecimal custoComparacao = null;
        String anoMesComparacaoStr = null;
        BigDecimal margem = null;

        if (anoComparacao != null && mesComparacao != null) {
            custoComparacao = operacaoRepository.sumValorByTipoAndAnoAndMes(
                    TIPO_COMPRA, anoComparacao, mesComparacao);
            anoMesComparacaoStr = String.format("%d-%02d", anoComparacao, mesComparacao);

            margem = calcularMargem(custoAtual, custoComparacao);
        }

        return new FinanceiroCustoResponseDTO(
                custoAtual,
                anoMesAtualStr,
                custoComparacao,
                anoMesComparacaoStr,
                margem
        );
    }

    // ((atual - comparacao) / comparacao) * 100
    private BigDecimal calcularMargem(BigDecimal atual, BigDecimal comparacao) {
        if (comparacao == null || comparacao.compareTo(BigDecimal.ZERO) == 0) {
            // Se comparacao é zero e atual é positivo, o crescimento é "infinito" (representado como 100%)
            if (atual.compareTo(BigDecimal.ZERO) > 0) {
                return new BigDecimal("100.00");
            }
            return BigDecimal.ZERO;
        }

        // ((atual - comparacao) / comparacao) * 100
        BigDecimal diferenca = atual.subtract(comparacao);

        return diferenca.divide(comparacao, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}