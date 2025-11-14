package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import java.util.List;

public record FinanceiroLucroAnualResponseDTO(
        Integer ano,
        BigDecimal lucroTotalAnual,
        List<LucroMensalDetalhe> lucroMensal
) {

    public record LucroMensalDetalhe(
            Integer mes,
            String mesNome, // Ex: "Janeiro", "Fevereiro"...
            BigDecimal total
    ) {}
}