package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.*;
import br.com.vaztech.vaztech.repository.OperacaoRepository;
import br.com.vaztech.vaztech.repository.ServicoRepository;
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

    @Autowired
    private ServicoRepository servicoRepository;

    private static final Integer TIPO_VENDA = 0;
    private static final Integer TIPO_COMPRA = 1;
    private static final Locale LOCALE_BRASIL = new Locale("pt", "BR");

    private static final Integer SERVICO_EXTERNO = 1;
    private static final Integer SERVICO_INTERNO = 2;


    public FinanceiroFaturamentoResponseDTO getFaturamentoMensal(
            Integer anoAtual, Integer mesAtual,
            Integer anoComparacao, Integer mesComparacao) {

        BigDecimal faturamentoOperacoes = operacaoRepository.sumValorByTipoAndAnoAndMes(
                TIPO_VENDA, anoAtual, mesAtual);
        BigDecimal faturamentoServicos = servicoRepository.sumValorByTipoAndAnoAndMes(
                SERVICO_EXTERNO, anoAtual, mesAtual);
        BigDecimal faturamentoAtual = faturamentoOperacoes.add(faturamentoServicos);

        String anoMesAtualStr = String.format("%d-%02d", anoAtual, mesAtual);

        BigDecimal faturamentoComparacao = null;
        String anoMesComparacaoStr = null;
        BigDecimal margem = null;

        if (anoComparacao != null && mesComparacao != null) {
            BigDecimal faturamentoOpComp = operacaoRepository.sumValorByTipoAndAnoAndMes(
                    TIPO_VENDA, anoComparacao, mesComparacao);
            BigDecimal faturamentoServComp = servicoRepository.sumValorByTipoAndAnoAndMes(
                    SERVICO_EXTERNO, anoComparacao, mesComparacao);
            faturamentoComparacao = faturamentoOpComp.add(faturamentoServComp);

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

        List<FaturamentoPorMesDTO> faturamentoOperacoes =
                operacaoRepository.findFaturamentoByAnoGroupByMes(TIPO_VENDA, ano);

        Map<Integer, BigDecimal> mapaFaturamento = faturamentoOperacoes.stream()
                .collect(Collectors.toMap(FaturamentoPorMesDTO::getMes, FaturamentoPorMesDTO::getTotal));

        List<FaturamentoPorMesDTO> faturamentoServicos =
                servicoRepository.findValorByAnoGroupByMes(SERVICO_EXTERNO, ano);

        faturamentoServicos.forEach(servicoMes -> {
            mapaFaturamento.merge(
                    servicoMes.getMes(),
                    servicoMes.getTotal(),
                    BigDecimal::add
            );
        });

        List<FinanceiroFaturamentoAnualResponseDTO.FaturamentoMensalDetalhe> detalhamentoMensal =
                new ArrayList<>();

        BigDecimal faturamentoTotal = BigDecimal.ZERO;

        for (int mes = 1; mes <= 12; mes++) {
            BigDecimal totalMes = mapaFaturamento.getOrDefault(mes, BigDecimal.ZERO);
            String nomeMes = getNomeMesFormatado(mes);

            detalhamentoMensal.add(
                    new FinanceiroFaturamentoAnualResponseDTO.FaturamentoMensalDetalhe(
                            mes,
                            nomeMes,
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

    public FinanceiroLucroResponseDTO getLucroMensal(
            Integer anoAtual, Integer mesAtual,
            Integer anoComparacao, Integer mesComparacao) {


        BigDecimal faturamentoOperacoes = operacaoRepository.sumValorByTipoAndAnoAndMes(TIPO_VENDA, anoAtual, mesAtual);
        BigDecimal faturamentoServicos = servicoRepository.sumValorByTipoAndAnoAndMes(SERVICO_EXTERNO, anoAtual, mesAtual);
        BigDecimal faturamentoAtual = faturamentoOperacoes.add(faturamentoServicos);

        BigDecimal custoOperacoes = operacaoRepository.sumValorByTipoAndAnoAndMes(TIPO_COMPRA, anoAtual, mesAtual);
        BigDecimal custoServicos = servicoRepository.sumValorByTipoAndAnoAndMes(SERVICO_INTERNO, anoAtual, mesAtual);
        BigDecimal custoAtual = custoOperacoes.add(custoServicos);

        BigDecimal lucroAtual = faturamentoAtual.subtract(custoAtual);
        String anoMesAtualStr = String.format("%d-%02d", anoAtual, mesAtual);

        BigDecimal lucroComparacao = null;
        String anoMesComparacaoStr = null;
        BigDecimal margem = null;

        if (anoComparacao != null && mesComparacao != null) {

            BigDecimal faturamentoOpComp = operacaoRepository.sumValorByTipoAndAnoAndMes(TIPO_VENDA, anoComparacao, mesComparacao);
            BigDecimal faturamentoServComp = servicoRepository.sumValorByTipoAndAnoAndMes(SERVICO_EXTERNO, anoComparacao, mesComparacao);
            BigDecimal faturamentoComparacao = faturamentoOpComp.add(faturamentoServComp);

            BigDecimal custoOpComp = operacaoRepository.sumValorByTipoAndAnoAndMes(TIPO_COMPRA, anoComparacao, mesComparacao);
            BigDecimal custoServComp = servicoRepository.sumValorByTipoAndAnoAndMes(SERVICO_INTERNO, anoComparacao, mesComparacao);
            BigDecimal custoComparacao = custoOpComp.add(custoServComp);

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

    public FinanceiroCustoResponseDTO getCustoMensal(
            Integer anoAtual, Integer mesAtual,
            Integer anoComparacao, Integer mesComparacao) {

        BigDecimal custoOperacoes = operacaoRepository.sumValorByTipoAndAnoAndMes(
                TIPO_COMPRA, anoAtual, mesAtual);
        BigDecimal custoServicos = servicoRepository.sumValorByTipoAndAnoAndMes(
                SERVICO_INTERNO, anoAtual, mesAtual);
        BigDecimal custoAtual = custoOperacoes.add(custoServicos);

        String anoMesAtualStr = String.format("%d-%02d", anoAtual, mesAtual);

        BigDecimal custoComparacao = null;
        String anoMesComparacaoStr = null;
        BigDecimal margem = null;

        if (anoComparacao != null && mesComparacao != null) {
            BigDecimal custoOpComp = operacaoRepository.sumValorByTipoAndAnoAndMes(
                    TIPO_COMPRA, anoComparacao, mesComparacao);
            BigDecimal custoServComp = servicoRepository.sumValorByTipoAndAnoAndMes(
                    SERVICO_INTERNO, anoComparacao, mesComparacao);
            custoComparacao = custoOpComp.add(custoServComp);

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

    public FinanceiroCustoAnualResponseDTO getCustoAnual(Integer ano) {

        List<FaturamentoPorMesDTO> custoOperacoes =
                operacaoRepository.findFaturamentoByAnoGroupByMes(TIPO_COMPRA, ano);

        Map<Integer, BigDecimal> mapaCusto = custoOperacoes.stream()
                .collect(Collectors.toMap(FaturamentoPorMesDTO::getMes, FaturamentoPorMesDTO::getTotal));

        List<FaturamentoPorMesDTO> custoServicos =
                servicoRepository.findValorByAnoGroupByMes(SERVICO_INTERNO, ano);

        custoServicos.forEach(servicoMes -> {
            mapaCusto.merge(
                    servicoMes.getMes(),
                    servicoMes.getTotal(),
                    BigDecimal::add
            );
        });

        List<FinanceiroCustoAnualResponseDTO.CustoMensalDetalhe> detalhamentoMensal =
                new ArrayList<>();

        BigDecimal custoTotal = BigDecimal.ZERO;

        for (int mes = 1; mes <= 12; mes++) {
            BigDecimal totalMes = mapaCusto.getOrDefault(mes, BigDecimal.ZERO);
            String nomeMes = getNomeMesFormatado(mes);

            detalhamentoMensal.add(
                    new FinanceiroCustoAnualResponseDTO.CustoMensalDetalhe(
                            mes,
                            nomeMes,
                            totalMes
                    )
            );
            custoTotal = custoTotal.add(totalMes);
        }

        return new FinanceiroCustoAnualResponseDTO(
                ano,
                custoTotal.setScale(2, RoundingMode.HALF_UP),
                detalhamentoMensal
        );
    }

    public FinanceiroLucroAnualResponseDTO getLucroAnual(Integer ano) {

        List<FaturamentoPorMesDTO> faturamentoOperacoes =
                operacaoRepository.findFaturamentoByAnoGroupByMes(TIPO_VENDA, ano);
        Map<Integer, BigDecimal> mapaFaturamento = faturamentoOperacoes.stream()
                .collect(Collectors.toMap(FaturamentoPorMesDTO::getMes, FaturamentoPorMesDTO::getTotal));

        List<FaturamentoPorMesDTO> faturamentoServicos =
                servicoRepository.findValorByAnoGroupByMes(SERVICO_EXTERNO, ano);

        faturamentoServicos.forEach(servicoMes -> {
            mapaFaturamento.merge(servicoMes.getMes(), servicoMes.getTotal(), BigDecimal::add);
        });

        List<FaturamentoPorMesDTO> custoOperacoes =
                operacaoRepository.findFaturamentoByAnoGroupByMes(TIPO_COMPRA, ano);
        Map<Integer, BigDecimal> mapaCusto = custoOperacoes.stream()
                .collect(Collectors.toMap(FaturamentoPorMesDTO::getMes, FaturamentoPorMesDTO::getTotal));

        List<FaturamentoPorMesDTO> custoServicos =
                servicoRepository.findValorByAnoGroupByMes(SERVICO_INTERNO, ano);

        custoServicos.forEach(servicoMes -> {
            mapaCusto.merge(servicoMes.getMes(), servicoMes.getTotal(), BigDecimal::add);
        });

        List<FinanceiroLucroAnualResponseDTO.LucroMensalDetalhe> detalhamentoMensal =
                new ArrayList<>();
        BigDecimal lucroTotal = BigDecimal.ZERO;

        for (int mes = 1; mes <= 12; mes++) {
            BigDecimal faturamentoMes = mapaFaturamento.getOrDefault(mes, BigDecimal.ZERO);
            BigDecimal custoMes = mapaCusto.getOrDefault(mes, BigDecimal.ZERO);
            BigDecimal lucroMes = faturamentoMes.subtract(custoMes);

            String nomeMes = getNomeMesFormatado(mes);

            detalhamentoMensal.add(
                    new FinanceiroLucroAnualResponseDTO.LucroMensalDetalhe(
                            mes,
                            nomeMes,
                            lucroMes
                    )
            );
            lucroTotal = lucroTotal.add(lucroMes);
        }

        return new FinanceiroLucroAnualResponseDTO(
                ano,
                lucroTotal.setScale(2, RoundingMode.HALF_UP),
                detalhamentoMensal
        );
    }

    private BigDecimal calcularMargem(BigDecimal atual, BigDecimal comparacao) {
        if (comparacao == null || comparacao.compareTo(BigDecimal.ZERO) == 0) {
            if (atual.compareTo(BigDecimal.ZERO) > 0) {
                return new BigDecimal("100.00");
            }
            return BigDecimal.ZERO;
        }

        BigDecimal diferenca = atual.subtract(comparacao);

        return diferenca.divide(comparacao, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String getNomeMesFormatado(int mes) {
        String nomeMes = Month.of(mes).getDisplayName(TextStyle.FULL, LOCALE_BRASIL);
        // ("janeiro" -> "Janeiro")
        return nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);
    }
}