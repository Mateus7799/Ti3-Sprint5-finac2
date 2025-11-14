package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import java.util.List;

public record FinanceiroCustoAnualResponseDTO(
        Integer ano,
        BigDecimal custoTotalAnual,
        List<CustoMensalDetalhe> custoMensal
) {

    public record CustoMensalDetalhe(
            Integer mes,
            String mesNome, // Ex: "Janeiro", "Fevereiro"...
            BigDecimal total
    ) {}
}