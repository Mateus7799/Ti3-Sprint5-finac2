package br.com.vaztech.vaztech.dto;

import br.com.vaztech.vaztech.entity.Funcionario;
import br.com.vaztech.vaztech.entity.Pessoa;
import java.time.LocalDate;

public record AniversarianteResponseDTO(
        Integer id,
        String nome,
        LocalDate dataNascimento,
        String contato,
        Boolean souFuncionario
) {
    public AniversarianteResponseDTO(Pessoa pessoa) {
        this(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getContato(),
                false
        );
    }

    public AniversarianteResponseDTO(Funcionario funcionario) {
        this(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getDataNascimento(),
                null,
                true
        );
    }
}

