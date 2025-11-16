package br.com.vaztech.vaztech.dto;

import br.com.vaztech.vaztech.entity.Pessoa;

import java.time.LocalDate;

public record PessoaAniversarioResponseDTO(
        Integer id,
        String nome,
        LocalDate dataNascimento,
        String contato
) {
    public PessoaAniversarioResponseDTO(Pessoa pessoa) {
        this(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getContato()
        );
    }
}