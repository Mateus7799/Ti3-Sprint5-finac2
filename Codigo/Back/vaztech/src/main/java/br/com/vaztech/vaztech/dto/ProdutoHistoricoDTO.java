package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoHistoricoDTO(
        String label,
        PessoaDTO pessoa,
        LocalDate data,
        LocalDate dataFim,
        FuncionarioDTO funcionario,
        BigDecimal valor
) {
    public record PessoaDTO(
            Integer id,
            String nome,
            String cpfCnpj
    ) {}

    public record FuncionarioDTO(
            Integer id,
            String nome,
            String cpf
    ) {}
}
