package br.com.vaztech.vaztech.dto;

import br.com.vaztech.vaztech.entity.Pessoa;
import br.com.vaztech.vaztech.entity.Servico;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ServicoResponseDTO(
        Integer id,
        ProdutoResponseDTO produto,
        Integer tipo,
        BigDecimal valor,
        Pessoa pessoa,
        LocalDate dataInicio,
        LocalDate dataFim,
        String observacoes,
        Integer status,
        Integer formaPagamento
) {
    public ServicoResponseDTO(Servico servico) {
        this(
                servico.getId(),
                new ProdutoResponseDTO(servico.getProduto()),
                servico.getTipo(),
                servico.getValor(),
                servico.getPessoa(),
                servico.getDataInicio(),
                servico.getDataFim(),
                servico.getObservacoes(),
                servico.getStatus() != null ? servico.getStatus().getId() : null,
                servico.getMetodoPagamento() != null ? servico.getMetodoPagamento().getId() : null
        );
    }
}
