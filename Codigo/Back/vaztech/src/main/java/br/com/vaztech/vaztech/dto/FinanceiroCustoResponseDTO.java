package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;

public record FinanceiroCustoResponseDTO(
        BigDecimal custoMesAtual,
        String anoMesAtual, // Formato "YYYY-MM"
        BigDecimal custoMesComparacao,
        String anoMesComparacao,
        BigDecimal margem
) {}