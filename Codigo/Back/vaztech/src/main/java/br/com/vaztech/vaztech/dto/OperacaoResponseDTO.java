package br.com.vaztech.vaztech.dto;

import br.com.vaztech.vaztech.entity.Funcionario;
import br.com.vaztech.vaztech.entity.Operacao;
import br.com.vaztech.vaztech.entity.Pessoa;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperacaoResponseDTO (
        Integer id,
        ProdutoResponseDTO produto,
        BigDecimal valor,
        Pessoa pessoa,
        Funcionario funcionario,
        Integer tipo,
        String observacoes,
        LocalDateTime dataHoraTransacao,
        String metodoPagamento
) {
    public OperacaoResponseDTO(Operacao operacao) {
        this(
                operacao.getId(),
                new ProdutoResponseDTO(operacao.getProduto()),
                operacao.getValor(),
                operacao.getPessoa(),
                operacao.getFuncionario(),
                operacao.getTipo(),
                operacao.getObservacoes(),
                operacao.getDataHoraTransacao(),
                operacao.getMetodoPagamento() != null ? operacao.getMetodoPagamento().getNome() : null
        );
    }
}
