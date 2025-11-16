package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PessoaHistoricoDTO(
        String label,
        ProdutoDTO produto,
        LocalDate data,
        LocalDate dataFim,
        FuncionarioDTO funcionario,
        BigDecimal valor
) {
    public record ProdutoDTO(
            Integer id,
            String numeroSerie,
            String aparelho,
            String modelo
    ) {}

    public record FuncionarioDTO(
            Integer id,
            String nome,
            String cpf
    ) {}
}
