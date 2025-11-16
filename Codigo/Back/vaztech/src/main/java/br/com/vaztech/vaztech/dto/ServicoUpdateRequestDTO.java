package br.com.vaztech.vaztech.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ServicoUpdateRequestDTO(
        @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
        BigDecimal valor,

        String observacoes,

        Integer idStatus,

        Integer metodoPagamento
) {}
