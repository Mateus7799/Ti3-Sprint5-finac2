package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FuncionarioHistoricoDTO(
        String label,
        PessoaDTO pessoa,
        ProdutoDTO produto,
        LocalDate data,
        LocalDate dataFim,
        BigDecimal valor
) {
    public record PessoaDTO(
            Integer id,
            String nome,
            String cpfCnpj
    ) {}

    public record ProdutoDTO(
            Integer id,
            String numeroSerie,
            String aparelho,
            String modelo
    ) {}
}
